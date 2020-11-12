注释

默认情况下，Spring 事务处理只对 RuntimeException 方法进行回滚，所以，如果将 RuntimeException 替换成普通的 Exception 不会产生回滚效果。

# 事务自定义标签

事务标签在这里被加载解析。其中 AnnotationDrivenBeanDefinitionParser 是解析注解的关键。

**TxNamespaceHandler**

```java
/**
 * {@code NamespaceHandler} allowing for the configuration of
 * declarative transaction management using either XML or using annotations.
 *
 * <p>This namespace handler is the central piece of functionality in the
 * Spring transaction management facilities and offers two approaches
 * to declaratively manage transactions.
 *
 * <p>One approach uses transaction semantics defined in XML using the
 * {@code <tx:advice>} elements, the other uses annotations
 * in combination with the {@code <tx:annotation-driven>} element.
 * Both approached are detailed to great extent in the Spring reference manual.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class TxNamespaceHandler extends NamespaceHandlerSupport {

   static final String TRANSACTION_MANAGER_ATTRIBUTE = "transaction-manager";

   static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";


   static String getTransactionManagerName(Element element) {
      return (element.hasAttribute(TRANSACTION_MANAGER_ATTRIBUTE) ?
            element.getAttribute(TRANSACTION_MANAGER_ATTRIBUTE) : DEFAULT_TRANSACTION_MANAGER_BEAN_NAME);
   }


   @Override
   public void init() {
      registerBeanDefinitionParser("advice", new TxAdviceBeanDefinitionParser());
      registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
      registerBeanDefinitionParser("jta-transaction-manager", new JtaTransactionManagerBeanDefinitionParser());
   }

}
```

**AnnotationDrivenBeanDefinitionParser#parse**

```java
/**
 * Parses the {@code <tx:annotation-driven/>} tag. Will
 * {@link AopNamespaceUtils#registerAutoProxyCreatorIfNecessary register an AutoProxyCreator}
 * with the container as necessary.
 */
@Override
@Nullable
public BeanDefinition parse(Element element, ParserContext parserContext) {
   registerTransactionalEventListenerFactory(parserContext);
   String mode = element.getAttribute("mode");
   if ("aspectj".equals(mode)) {
      // mode="aspectj"
      registerTransactionAspect(element, parserContext);
      if (ClassUtils.isPresent("javax.transaction.Transactional", getClass().getClassLoader())) {
         registerJtaTransactionAspect(element, parserContext);
      }
   }
   else {
      // mode="proxy"
      AopAutoProxyConfigurer.configureAutoProxyCreator(element, parserContext);
   }
   return null;
}
```

## 注册 InfrastructureAdvisorAutoProxyCreator

**AopAutoProxyConfigurer** 下面注册了三个 Bean，支撑了整个事务功能。Advisor 为首。

```java
/**
 * Inner class to just introduce an AOP framework dependency when actually in proxy mode.
 */
private static class AopAutoProxyConfigurer {

   public static void configureAutoProxyCreator(Element element, ParserContext parserContext) {
      // 锚点 1
      AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);

      String txAdvisorBeanName = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME;
      if (!parserContext.getRegistry().containsBeanDefinition(txAdvisorBeanName)) {
         Object eleSource = parserContext.extractSource(element);

         // Create the TransactionAttributeSource definition.
         RootBeanDefinition sourceDef = new RootBeanDefinition(
               "org.springframework.transaction.annotation.AnnotationTransactionAttributeSource");
         sourceDef.setSource(eleSource);
         sourceDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
         String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);

         // Create the TransactionInterceptor definition.
         RootBeanDefinition interceptorDef = new RootBeanDefinition(TransactionInterceptor.class);
         interceptorDef.setSource(eleSource);
         interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
         registerTransactionManager(element, interceptorDef);
         interceptorDef.getPropertyValues().add("transactionAttributeSource", new RuntimeBeanReference(sourceName));
         String interceptorName = parserContext.getReaderContext().registerWithGeneratedName(interceptorDef);

         // Create the TransactionAttributeSourceAdvisor definition.
         RootBeanDefinition advisorDef = new RootBeanDefinition(BeanFactoryTransactionAttributeSourceAdvisor.class);
         advisorDef.setSource(eleSource);
         advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
          // 锚点 2
         advisorDef.getPropertyValues().add("transactionAttributeSource", new RuntimeBeanReference(sourceName));
          //
         advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
         if (element.hasAttribute("order")) {
            advisorDef.getPropertyValues().add("order", element.getAttribute("order"));
         }
         parserContext.getRegistry().registerBeanDefinition(txAdvisorBeanName, advisorDef);

         CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
         compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
         compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
         compositeDef.addNestedComponent(new BeanComponentDefinition(advisorDef, txAdvisorBeanName));
         parserContext.registerComponent(compositeDef);
      }
   }
}
```

**AopNameSpaceUtils#registerAutoProxyCreatorIfNecessary**

```java
public static void registerAutoProxyCreatorIfNecessary(
      ParserContext parserContext, Element sourceElement) {
   // 锚点 3
   BeanDefinition beanDefinition = AopConfigUtils.registerAutoProxyCreatorIfNecessary(
         parserContext.getRegistry(), parserContext.extractSource(sourceElement));
   useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
   // 锚点 4
   registerComponentIfNecessary(beanDefinition, parserContext);
}
```
**AopConfigUtils#registerAutoProxyCreatorIfNecessary**

```java
// 锚点 3
public static BeanDefinition registerAutoProxyCreatorIfNecessary(
      BeanDefinitionRegistry registry, @Nullable Object source) {

   return registerOrEscalateApcAsRequired(InfrastructureAdvisorAutoProxyCreator.class, registry, source);
}
```
InfrastructureAdvisorAutoProxyCreator 类图，原来是实现了 SmartInstantiationAwareBeanPostProcessor 接口
![image.png](https://i.loli.net/2020/11/05/1T6SY3b9rqf4s5E.png)

**AopNameSpaceUtils#registerComponentIfNecessary**

```java
// 锚点 4
private static void registerComponentIfNecessary(@Nullable BeanDefinition beanDefinition, ParserContext parserContext) {
   if (beanDefinition != null) {
      parserContext.registerComponent(
            new BeanComponentDefinition(beanDefinition, AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME));
   }
}
```

所有 Bean 的实例化前的阶段时候，也就是调用 AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation，都会调用这个 InstantiationAwareBeanPostProcessor 中的方法。

**AbstractAutoProxyCreator#postProcessBeforeInstantiation**

```java
@Override
public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
   Object cacheKey = getCacheKey(beanClass, beanName);
   // 下面三个 if 本来是一方法中的
   if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
      if (this.advisedBeans.containsKey(cacheKey)) {
         return null;
      }
      if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
         this.advisedBeans.put(cacheKey, Boolean.FALSE);
         return null;
      }
   }

   // Create proxy here if we have a custom TargetSource.
   // Suppresses unnecessary default instantiation of the target bean:
   // The TargetSource will handle target instances in a custom fashion.
   TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
   if (targetSource != null) {
      if (StringUtils.hasLength(beanName)) {
         this.targetSourcedBeans.add(beanName);
      }
      Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
      Object proxy = createProxy(beanClass, beanName, specificInterceptors, targetSource);
      this.proxyTypes.put(cacheKey, proxy.getClass());
      return proxy;
   }

   return null;
}
```

+ 找出指定 Bean 对应的增强器
+ 根据找出的增强器创建代理

## 获取对应的 class/method 的增强器

**AbstractAdvisorAutoProxyCreator#getAdvicesAndAdvisorsForBean**

```java
protected Object[] getAdvicesAndAdvisorsForBean(
      Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
   // 锚点 5
   List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
   if (advisors.isEmpty()) {
      return DO_NOT_PROXY;
   }
   return advisors.toArray();
}
```

**AbstractAdvisorAutoProxyCreator#findEligibleAdvisors**

```java
/**
 * Find all eligible Advisors for auto-proxying this class.
 * @param beanClass the clazz to find advisors for
 * @param beanName the name of the currently proxied bean
 * @return the empty List, not {@code null},
 * if there are no pointcuts or interceptors
 * @see #findCandidateAdvisors
 * @see #sortAdvisors
 * @see #extendAdvisors
 */
protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
   // 获取候选的 advisor
   List<Advisor> candidateAdvisors = findCandidateAdvisors();
   List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
   extendAdvisors(eligibleAdvisors);
   if (!eligibleAdvisors.isEmpty()) {
      eligibleAdvisors = sortAdvisors(eligibleAdvisors);
   }
   return eligibleAdvisors;
}
```

**AbstractAdvisorAutoProxyCreator#findCandidateAdvisors**

```java
/**
 * Find all candidate Advisors to use in auto-proxying.
 * @return the List of candidate Advisors
 */
protected List<Advisor> findCandidateAdvisors() {
   Assert.state(this.advisorRetrievalHelper != null, "No BeanFactoryAdvisorRetrievalHelper available");
   return this.advisorRetrievalHelper.findAdvisorBeans();
}
```

**AbstractAdvisorAutoProxyCreator#findAdvisorsThatCanApply**

```java
/**
 * Search the given candidate Advisors to find all Advisors that
 * can apply to the specified bean.
 * @param candidateAdvisors the candidate Advisors
 * @param beanClass the target's bean class
 * @param beanName the target's bean name
 * @return the List of applicable Advisors
 * @see ProxyCreationContext#getCurrentProxiedBeanName()
 */
protected List<Advisor> findAdvisorsThatCanApply(
      List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {

   ProxyCreationContext.setCurrentProxiedBeanName(beanName);
   try {
      return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
   }
   finally {
      ProxyCreationContext.setCurrentProxiedBeanName(null);
   }
}
```

### 1. 寻找候选增强器

**AbstractAdvisorAutoProxyCreator#findCandidateAdvisors**

```java
/**
 * Find all candidate Advisors to use in auto-proxying.
 * @return the List of candidate Advisors
 */
protected List<Advisor> findCandidateAdvisors() {
   Assert.state(this.advisorRetrievalHelper != null, "No BeanFactoryAdvisorRetrievalHelper available");
   return this.advisorRetrievalHelper.findAdvisorBeans();
}
```

**BeanFactoryAdvisorRetrievaHelper#findAdvisorBeans**这是少有的非接口，直接是实现类的调用。

```java
/**
 * Find all eligible Advisor beans in the current bean factory,
 * ignoring FactoryBeans and excluding beans that are currently in creation.
 * @return the list of {@link org.springframework.aop.Advisor} beans
 * @see #isEligibleBean
 */
public List<Advisor> findAdvisorBeans() {
   // Determine list of advisor bean names, if not cached already.
   String[] advisorNames = this.cachedAdvisorBeanNames;
   if (advisorNames == null) {
      // Do not initialize FactoryBeans here: We need to leave all regular beans
      // uninitialized to let the auto-proxy creator apply to them!
      advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
            this.beanFactory, Advisor.class, true, false);
      this.cachedAdvisorBeanNames = advisorNames;
   }
   if (advisorNames.length == 0) {
      return new ArrayList<>();
   }

   List<Advisor> advisors = new ArrayList<>();
   for (String name : advisorNames) {
      if (isEligibleBean(name)) {
         if (this.beanFactory.isCurrentlyInCreation(name)) {
            if (logger.isTraceEnabled()) {
               logger.trace("Skipping currently created advisor '" + name + "'");
            }
         }
         else {
            try {
               advisors.add(this.beanFactory.getBean(name, Advisor.class));
            }
            catch (BeanCreationException ex) {
               Throwable rootCause = ex.getMostSpecificCause();
               if (rootCause instanceof BeanCurrentlyInCreationException) {
                  BeanCreationException bce = (BeanCreationException) rootCause;
                  String bceBeanName = bce.getBeanName();
                  if (bceBeanName != null && this.beanFactory.isCurrentlyInCreation(bceBeanName)) {
                     if (logger.isTraceEnabled()) {
                        logger.trace("Skipping advisor '" + name +
                              "' with dependency on currently created bean: " + ex.getMessage());
                     }
                     // Ignore: indicates a reference back to the bean we're trying to advise.
                     // We want to find advisors other than the currently created bean itself.
                     continue;
                  }
               }
               throw ex;
            }
         }
      }
   }
   return advisors;
}
```

### 2. 候选增强器中寻找到匹配项

**AopUtils#findAdvisorsThatCanApply**

```java
/**
 * Determine the sublist of the {@code candidateAdvisors} list
 * that is applicable to the given class.
 * @param candidateAdvisors the Advisors to evaluate
 * @param clazz the target class
 * @return sublist of Advisors that can apply to an object of the given class
 * (may be the incoming List as-is)
 */
public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
   if (candidateAdvisors.isEmpty()) {
      return candidateAdvisors;
   }
   List<Advisor> eligibleAdvisors = new ArrayList<>();
   // 处理引介增强
   for (Advisor candidate : candidateAdvisors) {
      // 锚点 1
      if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
         eligibleAdvisors.add(candidate);
      }
   }
   boolean hasIntroductions = !eligibleAdvisors.isEmpty();
   for (Advisor candidate : candidateAdvisors) {
      // 引介增强已经处理
      if (candidate instanceof IntroductionAdvisor) {
         // already processed
         continue;
      }
      // 对于普通 bean 的处理
      if (canApply(candidate, clazz, hasIntroductions)) {
         eligibleAdvisors.add(candidate);
      }
   }
   return eligibleAdvisors;
}
```

**AopUtils#canApply**

```java
/**
 * Can the given advisor apply at all on the given class?
 * This is an important test as it can be used to optimize
 * out a advisor for a class.
 * @param advisor the advisor to check
 * @param targetClass class we're testing
 * @return whether the pointcut can apply on any method
 */
// 锚点 1
public static boolean canApply(Advisor advisor, Class<?> targetClass) {
   return canApply(advisor, targetClass, false);
}

/**
 * Can the given advisor apply at all on the given class?
 * <p>This is an important test as it can be used to optimize out a advisor for a class.
 * This version also takes into account introductions (for IntroductionAwareMethodMatchers).
 * @param advisor the advisor to check
 * @param targetClass class we're testing
 * @param hasIntroductions whether or not the advisor chain for this bean includes
 * any introductions
 * @return whether the pointcut can apply on any method
 */
public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
   if (advisor instanceof IntroductionAdvisor) {
      return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
   }
   else if (advisor instanceof PointcutAdvisor) {
      PointcutAdvisor pca = (PointcutAdvisor) advisor;
      return canApply(pca.getPointcut(), targetClass, hasIntroductions);
   }
   else {
      // It doesn't have a pointcut so we assume it applies.
      return true;
   }
}
```

**AopUtils#canApply**

```java
/**
 * Can the given pointcut apply at all on the given class?
 * <p>This is an important test as it can be used to optimize
 * out a pointcut for a class.
 * @param pc the static or dynamic pointcut to check
 * @param targetClass the class to test
 * @param hasIntroductions whether or not the advisor chain
 * for this bean includes any introductions
 * @return whether the pointcut can apply on any method
 */
public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
   Assert.notNull(pc, "Pointcut must not be null");
   if (!pc.getClassFilter().matches(targetClass)) {
      return false;
   }
   // pc 表示的是 TransactionAttributeSourcePointcut#getMethodMatcher 返回的正是本身
   // @Override
   // public final MethodMatcher getMethodMatcher() {
   //		return this;
   //}

   MethodMatcher methodMatcher = pc.getMethodMatcher();
   if (methodMatcher == MethodMatcher.TRUE) {
      // No need to iterate the methods if we're matching any method anyway...
      return true;
   }

   IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
   if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
      introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
   }

   Set<Class<?>> classes = new LinkedHashSet<>();
   if (!Proxy.isProxyClass(targetClass)) {
      classes.add(ClassUtils.getUserClass(targetClass));
   }
   classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));

   for (Class<?> clazz : classes) {
      Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
      for (Method method : methods) {
         // 锚点 2 matches 
         if (introductionAwareMethodMatcher != null ?
               introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions) :
               methodMatcher.matches(method, targetClass)) {
            return true;
         }
      }
   }

   return false;
}
```

**BeanFactoryTransactionAttributeSourceAdvisor.pointcut** 这个在上面的时候，注册了进来。

```java
@Nullable
private TransactionAttributeSource transactionAttributeSource;

private final TransactionAttributeSourcePointcut pointcut = new TransactionAttributeSourcePointcut() {
   @Override
   @Nullable
   protected TransactionAttributeSource getTransactionAttributeSource() {
      return transactionAttributeSource;
   }
};
```

**TransactionAttributeSourcePointcut#matches**

```java
@Override
public boolean matches(Method method, Class<?> targetClass) {
   if (TransactionalProxy.class.isAssignableFrom(targetClass) ||
         PlatformTransactionManager.class.isAssignableFrom(targetClass) ||
         PersistenceExceptionTranslator.class.isAssignableFrom(targetClass)) {
      return false;
   }
   TransactionAttributeSource tas = getTransactionAttributeSource();
   // 锚点 3  此时的 tas 表示 AnnotationTransactionAttributeSource 类型
   return (tas == null || tas.getTransactionAttribute(method, targetClass) != null);
}
```

**AbstractFallbackTransactionAttributeSource#getTransactionAttribute**

```java
// 锚点 3
@Nullable
public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
   if (method.getDeclaringClass() == Object.class) {
      return null;
   }

   // First, see if we have a cached value.
   Object cacheKey = getCacheKey(method, targetClass);
   TransactionAttribute cached = this.attributeCache.get(cacheKey);
   if (cached != null) {
      // Value will either be canonical value indicating there is no transaction attribute,
      // or an actual transaction attribute.
      if (cached == NULL_TRANSACTION_ATTRIBUTE) {
         return null;
      }
      else {
         return cached;
      }
   }
   else {
      // We need to work it out.
      // 提取事务标签
      TransactionAttribute txAttr = computeTransactionAttribute(method, targetClass);
      // Put it in the cache.
      if (txAttr == null) {
         this.attributeCache.put(cacheKey, NULL_TRANSACTION_ATTRIBUTE);
      }
      else {
         String methodIdentification = ClassUtils.getQualifiedMethodName(method, targetClass);
         if (txAttr instanceof DefaultTransactionAttribute) {
            ((DefaultTransactionAttribute) txAttr).setDescriptor(methodIdentification);
         }
         if (logger.isTraceEnabled()) {
            logger.trace("Adding transactional method '" + methodIdentification + "' with attribute: " + txAttr);
         }
         this.attributeCache.put(cacheKey, txAttr);
      }
      return txAttr;
   }
}
```

### 3. 提取事务标签

**AbstractFallbackTransactionAttributeSource#computeTransactionAttribute**

```java
/**
 * Same signature as {@link #getTransactionAttribute}, but doesn't cache the result.
 * {@link #getTransactionAttribute} is effectively a caching decorator for this method.
 * <p>As of 4.1.8, this method can be overridden.
 * @since 4.1.8
 * @see #getTransactionAttribute
 */
@Nullable
protected TransactionAttribute computeTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
   // Don't allow no-public methods as required.
   if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
      return null;
   }

   // The method may be on an interface, but we need attributes from the target class.
   // If the target class is null, the method will be unchanged.
   Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
   // 锚点 4 查看方法中是否存在事务声明
   // First try is the method in the target class.
   TransactionAttribute txAttr = findTransactionAttribute(specificMethod);
   if (txAttr != null) {
      return txAttr;
   }
   // 锚点 5 查看方法所在类中是否存在事务声明
   // Second try is the transaction attribute on the target class.
   txAttr = findTransactionAttribute(specificMethod.getDeclaringClass());
   if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
      return txAttr;
   }
   // 如果存在接口，则到接口中去寻找
   if (specificMethod != method) {
      // Fallback is to look at the original method. 查找接口方法
      txAttr = findTransactionAttribute(method);
      if (txAttr != null) {
         return txAttr;
      }
      // Last fallback is the class of the original method. 到接口中的类去寻找
      txAttr = findTransactionAttribute(method.getDeclaringClass());
      if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
         return txAttr;
      }
   }

   return null;
}
```

**AbstractFallbackTransactionAttributeSource#findTransactionAttribute**

```java
// 锚点 4 
@Override
@Nullable
protected TransactionAttribute findTransactionAttribute(Class<?> clazz) {
   return determineTransactionAttribute(clazz);
}

@Override
@Nullable
protected TransactionAttribute findTransactionAttribute(Method method) {
   return determineTransactionAttribute(method);
}

/**
 * Determine the transaction attribute for the given method or class.
 * <p>This implementation delegates to configured
 * {@link TransactionAnnotationParser TransactionAnnotationParsers}
 * for parsing known annotations into Spring's metadata attribute class.
 * Returns {@code null} if it's not transactional.
 * <p>Can be overridden to support custom annotations that carry transaction metadata.
 * @param element the annotated method or class
 * @return the configured transaction attribute, or {@code null} if none was found
 */
@Nullable
protected TransactionAttribute determineTransactionAttribute(AnnotatedElement element) {
   // 这里的 annotationParsers 是当前类 AnnotationTransactionAttributeSource 初始化的时候初始化的，其中的
   // 的值被加入了 SpringTransactionAnnotationParser，也就是当进行属性获取的时候其实是使用 SpringTransactionAnnotationParser 类的 parseTransactionAnnotation 放的进行解析。
   for (TransactionAnnotationParser annotationParser : this.annotationParsers) {
      TransactionAttribute attr = annotationParser.parseTransactionAnnotation(element);
      if (attr != null) {
         return attr;
      }
   }
   return null;
}
```

**SpringTransactionAnnotationParser#parseTransactionAnnotation**

```java
public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
   AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
         element, Transactional.class, false, false);
   if (attributes != null) {
      // 锚点 5
      return parseTransactionAnnotation(attributes);
   }
   else {
      return null;
   }
}
```

**SpringTransactionAnnotationParser#parseTransactionAnnotation**

```java
protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
   RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();

   Propagation propagation = attributes.getEnum("propagation");
   rbta.setPropagationBehavior(propagation.value());
   Isolation isolation = attributes.getEnum("isolation");
   rbta.setIsolationLevel(isolation.value());
   rbta.setTimeout(attributes.getNumber("timeout").intValue());
   rbta.setReadOnly(attributes.getBoolean("readOnly"));
   rbta.setQualifier(attributes.getString("value"));

   List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
   for (Class<?> rbRule : attributes.getClassArray("rollbackFor")) {
      rollbackRules.add(new RollbackRuleAttribute(rbRule));
   }
   for (String rbRule : attributes.getStringArray("rollbackForClassName")) {
      rollbackRules.add(new RollbackRuleAttribute(rbRule));
   }
   for (Class<?> rbRule : attributes.getClassArray("noRollbackFor")) {
      rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
   }
   for (String rbRule : attributes.getStringArray("noRollbackForClassName")) {
      rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
   }
   rbta.setRollbackRules(rollbackRules);

   return rbta;
}
```

这里只是事务功能的初始化工作便结束了，当判断某个 bean 适用于事务增强时，也就是适用于增强器 BeanFactoryTransactionAttributeSourceAdvisor，在自定义标签解析时，注入的类成为了整个事务功能的基础。

# 事务增强器

**TransactionInterceptor#invoke**

```java
@Override
@Nullable
public Object invoke(MethodInvocation invocation) throws Throwable {
   // Work out the target class: may be {@code null}.
   // The TransactionAttributeSource should be passed the target class
   // as well as the method, which may be from an interface.
   Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

   // Adapt to TransactionAspectSupport's invokeWithinTransaction...
   return invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed);
}
```

**TransactionAspectSupport#invokeWithinTransaction**  TransactionInterceptor 继承自该类

```java
/**
 * General delegate for around-advice-based subclasses, delegating to several other template
 * methods on this class. Able to handle {@link CallbackPreferringPlatformTransactionManager}
 * as well as regular {@link PlatformTransactionManager} implementations.
 * @param method the Method being invoked
 * @param targetClass the target class that we're invoking the method on
 * @param invocation the callback to use for proceeding with the target invocation
 * @return the return value of the method, if any
 * @throws Throwable propagated from the target invocation
 */
@Nullable
protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
      final InvocationCallback invocation) throws Throwable {
   // 获取对应事务属性
   // If the transaction attribute is null, the method is non-transactional.
   TransactionAttributeSource tas = getTransactionAttributeSource();
   final TransactionAttribute txAttr = (tas != null ? tas.getTransactionAttribute(method, targetClass) : null);
   // 获取 beanFatory 中的 transactionManager
   final PlatformTransactionManager tm = determineTransactionManager(txAttr);
   // 构造方法唯一标识，类#方法
   final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);
   // 声明式事务处理
   if (txAttr == null || !(tm instanceof CallbackPreferringPlatformTransactionManager)) {
      // Standard transaction demarcation with getTransaction and commit/rollback calls.
       // 创建事务。上面的意思是根据 getTransaction 和 commit/rollback 调用进行标准事务划分
       // 锚点 1
      TransactionInfo txInfo = createTransactionIfNecessary(tm, txAttr, joinpointIdentification);

      Object retVal;
      try {
         // This is an around advice: Invoke the next interceptor in the chain.
         // This will normally result in a target object being invoked.
         // 执行被增强方法
         retVal = invocation.proceedWithInvocation();
      }
      catch (Throwable ex) {
         // target invocation exception
         // 异常回滚
         completeTransactionAfterThrowing(txInfo, ex);
         throw ex;
      }
      finally {
         // 清除信息
         cleanupTransactionInfo(txInfo);
      }
      // 提交事务
      commitTransactionAfterReturning(txInfo);
      return retVal;
   }

   else {
      // 编程式事务处理
      final ThrowableHolder throwableHolder = new ThrowableHolder();

      // It's a CallbackPreferringPlatformTransactionManager: pass a TransactionCallback in.
      try {
         Object result = ((CallbackPreferringPlatformTransactionManager) tm).execute(txAttr, status -> {
            TransactionInfo txInfo = prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
            try {
               return invocation.proceedWithInvocation();
            }
            catch (Throwable ex) {
               if (txAttr.rollbackOn(ex)) {
                  // A RuntimeException: will lead to a rollback.
                  if (ex instanceof RuntimeException) {
                     throw (RuntimeException) ex;
                  }
                  else {
                     throw new ThrowableHolderException(ex);
                  }
               }
               else {
                  // A normal return value: will lead to a commit.
                  throwableHolder.throwable = ex;
                  return null;
               }
            }
            finally {
               cleanupTransactionInfo(txInfo);
            }
         });

         // Check result state: It might indicate a Throwable to rethrow.
         if (throwableHolder.throwable != null) {
            throw throwableHolder.throwable;
         }
         return result;
      }
      catch (ThrowableHolderException ex) {
         throw ex.getCause();
      }
      catch (TransactionSystemException ex2) {
         if (throwableHolder.throwable != null) {
            logger.error("Application exception overridden by commit exception", throwableHolder.throwable);
            ex2.initApplicationException(throwableHolder.throwable);
         }
         throw ex2;
      }
      catch (Throwable ex2) {
         if (throwableHolder.throwable != null) {
            logger.error("Application exception overridden by commit exception", throwableHolder.throwable);
         }
         throw ex2;
      }
   }
}
```

从上面的函数中， Spring 支持两种事务处理的方式，声明式和编程式事务。

声明式事务步骤如下：

1. 获取事务的属性。
2. 加载配置中配置的 TransactionManager。
3. 不同的事务处理方式使用不同的逻辑。

4. 在目标方法执行前获取事务并手机事务信息。
5. 执行目标方法。
6. 一旦出现异常，尝试异常处理。（Spring 默认只支持 RuntimeException 回滚，所以必须在上面写上 Exception）
7. 提交事务前的事务信息清除。
8. 提交事务。

## 创建事务

**TransactionAspectSupport#createTransactionIfNecessary**

```java
/**
 * Create a transaction if necessary based on the given TransactionAttribute.
 * <p>Allows callers to perform custom TransactionAttribute lookups through
 * the TransactionAttributeSource.
 * @param txAttr the TransactionAttribute (may be {@code null})
 * @param joinpointIdentification the fully qualified method name
 * (used for monitoring and logging purposes)
 * @return a TransactionInfo object, whether or not a transaction was created.
 * The {@code hasTransaction()} method on TransactionInfo can be used to
 * tell if there was a transaction created.
 * @see #getTransactionAttributeSource()
 */
@SuppressWarnings("serial")
protected TransactionInfo createTransactionIfNecessary(@Nullable PlatformTransactionManager tm,
      @Nullable TransactionAttribute txAttr, final String joinpointIdentification) {

   // If no name specified, apply method identification as transaction name.
    // 如果没有名称指定则使用方法唯一标识，并使用 DelegatingTransactionAttribute 封装 txAttr
   if (txAttr != null && txAttr.getName() == null) {
      txAttr = new DelegatingTransactionAttribute(txAttr) {
         @Override
         public String getName() {
            return joinpointIdentification;
         }
      };
   }

   TransactionStatus status = null;
   if (txAttr != null) {
      if (tm != null) {
          // 获取事务
         status = tm.getTransaction(txAttr);
      }
      else {
         if (logger.isDebugEnabled()) {
            logger.debug("Skipping transactional joinpoint [" + joinpointIdentification +
                  "] because no transaction manager has been configured");
         }
      }
   }
    // 根据指定的属性与 status 准备一个 TransactionInfo
   return prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
}
```

1. 使用 DelegatingTransactionAttribute 封装传入的 TransactionAttribute 实例。
2. 获取事务。
3. 构建事务信息。

### 1. 获取事务

**AbstractPlatformTransactionManager#getTransaction**

```java
/**
 * This implementation handles propagation behavior. Delegates to
 * {@code doGetTransaction}, {@code isExistingTransaction}
 * and {@code doBegin}.
 * @see #doGetTransaction
 * @see #isExistingTransaction
 * @see #doBegin
 */
@Override
public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
      throws TransactionException {

   // Use defaults if no transaction definition given.
   TransactionDefinition def = (definition != null ? definition : TransactionDefinition.withDefaults());
   // 锚点 1
   Object transaction = doGetTransaction();
   boolean debugEnabled = logger.isDebugEnabled();
   // 判断当前线程是否存在事务，判断依据为当前线程记录的连接不为空且连接中 connectionHolder 中的 transactionActive 属性不为空
   if (isExistingTransaction(transaction)) {
      // Existing transaction found -> check propagation behavior to find out how to behave.
       // 当前线程已经存在事务
      return handleExistingTransaction(def, transaction, debugEnabled);
   }
	// 事务超时设置验证
   // Check definition settings for new transaction.
   if (def.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {
      throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());
   }
	// 如果当前线程不存在事务，但是 propagationBehavior 却被声明为 PROPAGATION_MANDATORY 抛出异常
   // No existing transaction found -> check propagation behavior to find out how to proceed.
   if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {
      throw new IllegalTransactionStateException(
            "No existing transaction found for transaction marked with propagation 'mandatory'");
   }
    // 下面几个都需要新建事务 PROPAGATION_REQUIRED、PROPAGATION_REQUIRES_NEW、PROPAGATION_NESTED
   else if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED ||
         def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW ||
         def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
       // 空挂起
      SuspendedResourcesHolder suspendedResources = suspend(null);
      if (debugEnabled) {
         logger.debug("Creating new transaction with name [" + def.getName() + "]: " + def);
      }
      try {
         // 这是原文中的开始事务的方法
         return startTransaction(def, transaction, debugEnabled, suspendedResources);
      }
      catch (RuntimeException | Error ex) {
         resume(null, suspendedResources);
         throw ex;
      }
   }
   else {
      // Create "empty" transaction: no actual transaction, but potentially synchronization.
      if (def.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT && logger.isWarnEnabled()) {
         logger.warn("Custom isolation level specified but no actual transaction initiated; " +
               "isolation level will effectively be ignored: " + def);
      }
      boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
      return prepareTransactionStatus(def, null, true, newSynchronization, debugEnabled, null);
   }
}
```

**DataSourceTransactionManager#doGetTransaction**

```java
// 锚点 1 这里是上面的类的子类，供于提供扩展，
@Override
protected Object doGetTransaction() {
   DataSourceTransactionObject txObject = new DataSourceTransactionObject();
   txObject.setSavepointAllowed(isNestedTransactionAllowed());
   ConnectionHolder conHolder =
         (ConnectionHolder) TransactionSynchronizationManager.getResource(obtainDataSource());
   txObject.setConnectionHolder(conHolder, false);
   return txObject;
}
```

**AbstractPlatformTransactionManager#startTransaction**

```java
/**
 * Start a new transaction.
 */
private TransactionStatus startTransaction(TransactionDefinition definition, Object transaction,
      boolean debugEnabled, @Nullable SuspendedResourcesHolder suspendedResources) {
   
   boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
   DefaultTransactionStatus status = newTransactionStatus(
         definition, transaction, true, newSynchronization, debugEnabled, suspendedResources);
   // 构造 transaction，包括设置 ConnectionHolder、隔离级别、timeout
   // 这里是给子类扩展的，spring 有默认的 jdbc 实现，为 DataSourceTransactionManager
   doBegin(transaction, definition);
   // 新同步事务的设置，针对于当前线程的设置
   prepareSynchronization(status, definition);
   return status;
}
```

事务的准备工作包括以下：

1. 获取事务。
2. 如果当前线程存在事务，则转向嵌套事务的处理。
3. 事务超时设置验证。
4. 事务 propagationBehavior 属性的设置验证。
5. 构建 DefaultTransactionStatus/
6. 完善 transaction，包括设置 ConnectionHolder、隔离级别、timeout，如果是新连接，则绑定到当前线程。

**DataSourceTransactionManager#dobegin**

```java
@Override
protected void doBegin(Object transaction, TransactionDefinition definition) {
   DataSourceTransactionObject txObject = (DataSourceTransactionObject) transaction;
   Connection con = null;

   try {
      if (!txObject.hasConnectionHolder() ||
            txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
          // DataSourceTransactionManager 内置了个 DataSource 引用，并且实现了InitializingBean 接口，在 afterProperties 方法里面检查 DataSource 是否为空。
          // 并且在这个基础上，返回当前通过构造函数注入的实际 DataSource 对象的引用。
         Connection newCon = obtainDataSource().getConnection();
         if (logger.isDebugEnabled()) {
            logger.debug("Acquired Connection [" + newCon + "] for JDBC transaction");
         }
         txObject.setConnectionHolder(new ConnectionHolder(newCon), true);
      }

      txObject.getConnectionHolder().setSynchronizedWithTransaction(true);
      con = txObject.getConnectionHolder().getConnection();
	  // 设置事务隔离级别
      // prepareConnectionForTransaction 下面第一个方法
      Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(con, definition);
      txObject.setPreviousIsolationLevel(previousIsolationLevel);
      txObject.setReadOnly(definition.isReadOnly());

      // Switch to manual commit if necessary. This is very expensive in some JDBC drivers,
      // so we don't want to do it unnecessarily (for example if we've explicitly
      // configured the connection pool to set it already).
      // 更改自动提交设置，由 Spring 控制提交
      if (con.getAutoCommit()) {
          // 这一步就是给 Spring 来控制的。
         txObject.setMustRestoreAutoCommit(true);
         if (logger.isDebugEnabled()) {
            logger.debug("Switching JDBC Connection [" + con + "] to manual commit");
         }
          // 然后将原来的 dataSource 返回的 Connection 设置为默认不自动提交
         con.setAutoCommit(false);
      }
		
      prepareTransactionalConnection(con, definition);
      // 设置判断当前线程是否存在事务的依据
      txObject.getConnectionHolder().setTransactionActive(true);

      int timeout = determineTimeout(definition);
      if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
         txObject.getConnectionHolder().setTimeoutInSeconds(timeout);
      }

      // Bind the connection holder to the thread.
      if (txObject.isNewConnectionHolder()) {
         // 将当前获取到的连接绑定到当前线程 TransactionSynchronizationManager.resources 这个 Manager 有很多的 NameThreadLocal
         // HBase 的客户端也做了类似的事情，也有个 HTableCacheManager
         TransactionSynchronizationManager.bindResource(obtainDataSource(), txObject.getConnectionHolder());
      }
   }

   catch (Throwable ex) {
      if (txObject.isNewConnectionHolder()) {
         DataSourceUtils.releaseConnection(con, obtainDataSource());
         txObject.setConnectionHolder(null, false);
      }
      throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction", ex);
   }
}
```

可以说事务是从这个函数开始的，在这个函数中已经开始尝试了对数据库连接的获取，当然，在获取数据库连接的同时，一些必要的设置也是需要同步设置的。

1. 尝试获取连接。
2. 设置隔离级别一级只读标识。
3. 更改默认的提交设置。
4. 设置标识位，标识当前连接已经被事务激活。
5. 设置过期时间。
6. 将 connectionHolder 绑定到当前线程。

**DataSourceUtils#prepareConnectionForTransaction**

```java
// prepareConnectionForTransaction
/**
 * Prepare the given Connection with the given transaction semantics.
 * @param con the Connection to prepare
 * @param definition the transaction definition to apply
 * @return the previous isolation level, if any
 * @throws SQLException if thrown by JDBC methods
 * @see #resetConnectionAfterTransaction
 * @see Connection#setTransactionIsolation
 * @see Connection#setReadOnly
 */
@Nullable
public static Integer prepareConnectionForTransaction(Connection con, @Nullable TransactionDefinition definition)
      throws SQLException {

   Assert.notNull(con, "No Connection specified");

   boolean debugEnabled = logger.isDebugEnabled();
   // Set read-only flag.
   if (definition != null && definition.isReadOnly()) {
      try {
         if (debugEnabled) {
            logger.debug("Setting JDBC Connection [" + con + "] read-only");
         }
         con.setReadOnly(true);
      }
      catch (SQLException | RuntimeException ex) {
         Throwable exToCheck = ex;
         while (exToCheck != null) {
            if (exToCheck.getClass().getSimpleName().contains("Timeout")) {
               // Assume it's a connection timeout that would otherwise get lost: e.g. from JDBC 4.0
               throw ex;
            }
            exToCheck = exToCheck.getCause();
         }
         // "read-only not supported" SQLException -> ignore, it's just a hint anyway
         logger.debug("Could not set JDBC Connection read-only", ex);
      }
   }

   // Apply specific isolation level, if any.
   Integer previousIsolationLevel = null;
   if (definition != null && definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
      if (debugEnabled) {
         logger.debug("Changing isolation level of JDBC Connection [" + con + "] to " +
               definition.getIsolationLevel());
      }
      int currentIsolation = con.getTransactionIsolation();
      if (currentIsolation != definition.getIsolationLevel()) {
         previousIsolationLevel = currentIsolation;
         con.setTransactionIsolation(definition.getIsolationLevel());
      }
   }

   return previousIsolationLevel;
}
```

**AbstractPlatformTransactionManager#prepareSynchronization** 这一步是上面执行 dobegin 后的一步骤。将事务信息记录在当前线程中。

```java
/**
 * Initialize transaction synchronization as appropriate.
 */
protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
   if (status.isNewSynchronization()) {
      TransactionSynchronizationManager.setActualTransactionActive(status.hasTransaction());
      TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(
            definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT ?
                  definition.getIsolationLevel() : null);
      TransactionSynchronizationManager.setCurrentTransactionReadOnly(definition.isReadOnly());
      TransactionSynchronizationManager.setCurrentTransactionName(definition.getName());
      TransactionSynchronizationManager.initSynchronization();
   }
}
```

### 2. 处理已经存在的事务

Spring 中支持多种事务的传播规则，这些都是在已经存在事务的基础上进一步的处理。

**AbstractPlatformTransactionManager#handleExistingTransaction**

```java
/**
 * Create a TransactionStatus for an existing transaction.
 */
private TransactionStatus handleExistingTransaction(
      TransactionDefinition definition, Object transaction, boolean debugEnabled)
      throws TransactionException {

   if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NEVER) {
      throw new IllegalTransactionStateException(
            "Existing transaction found for transaction marked with propagation 'never'");
   }

   if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {
      if (debugEnabled) {
         logger.debug("Suspending current transaction");
      }
      Object suspendedResources = suspend(transaction);
      boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
      return prepareTransactionStatus(
            definition, null, false, newSynchronization, debugEnabled, suspendedResources);
   }

   if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
      if (debugEnabled) {
         logger.debug("Suspending current transaction, creating new transaction with name [" +
               definition.getName() + "]");
      }
       // 新事务的建立
      SuspendedResourcesHolder suspendedResources = suspend(transaction);
      try {
         return startTransaction(definition, transaction, debugEnabled, suspendedResources);
      }
      catch (RuntimeException | Error beginEx) {
         resumeAfterBeginException(transaction, suspendedResources, beginEx);
         throw beginEx;
      }
   }
	// 嵌入式事务的处理
   if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
      if (!isNestedTransactionAllowed()) {
         throw new NestedTransactionNotSupportedException(
               "Transaction manager does not allow nested transactions by default - " +
               "specify 'nestedTransactionAllowed' property with value 'true'");
      }
      if (debugEnabled) {
         logger.debug("Creating nested transaction with name [" + definition.getName() + "]");
      }
      if (useSavepointForNestedTransaction()) {
         // Create savepoint within existing Spring-managed transaction,
         // through the SavepointManager API implemented by TransactionStatus.
         // Usually uses JDBC 3.0 savepoints. Never activates Spring synchronization.
          // 如果没有可以使用保存点的方式控制事务回滚，那么在嵌入式事务的建立初始建立保存点。
         DefaultTransactionStatus status =
               prepareTransactionStatus(definition, transaction, false, false, debugEnabled, null);
         status.createAndHoldSavepoint();
         return status;
      }
      else {
         // Nested transaction through nested begin and commit/rollback calls.
         // Usually only for JTA: Spring synchronization might get activated here
         // in case of a pre-existing JTA transaction.
          // 有些晴空是不能使用保存点操作，比如 JTA，那么建立新事务。
         return startTransaction(definition, transaction, debugEnabled, null);
      }
   }

   // Assumably PROPAGATION_SUPPORTS or PROPAGATION_REQUIRED.
   if (debugEnabled) {
      logger.debug("Participating in existing transaction");
   }
   if (isValidateExistingTransaction()) {
      if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
         Integer currentIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
         if (currentIsolationLevel == null || currentIsolationLevel != definition.getIsolationLevel()) {
            Constants isoConstants = DefaultTransactionDefinition.constants;
            throw new IllegalTransactionStateException("Participating transaction with definition [" +
                  definition + "] specifies isolation level which is incompatible with existing transaction: " +
                  (currentIsolationLevel != null ?
                        isoConstants.toCode(currentIsolationLevel, DefaultTransactionDefinition.PREFIX_ISOLATION) :
                        "(unknown)"));
         }
      }
      if (!definition.isReadOnly()) {
         if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            throw new IllegalTransactionStateException("Participating transaction with definition [" +
                  definition + "] is not marked as read-only but existing transaction is");
         }
      }
   }
   boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
   return prepareTransactionStatus(definition, transaction, false, newSynchronization, debugEnabled, null);
}
```

对于已经存在的事务的处理考虑两种情况。

+ **PROPAGATION_REQUIRES_NEW** 表示当前方法必须在它自己的事务里运行，一个新的事务将被启动，而如果有一个事务正在运行的话，则在这个方法运行期间被挂起。而 Spring 中对于此种传播方式的处理与新事务建立最大的不同点在与使用 suspend 方法将原事务挂起。将信息挂起的目的当然是为了在当前事务执行完毕后再将原事务还原。
+ **PROPAGATION_NESTED** 表示如果当前一正有一个事务在运行中，则该方法应该运行在一个嵌套的事务中，被嵌套的事务可以独立于封装事务进行提交或者回滚，如果封装事务不存在，行为就像上面的 PROPAGATION_REQUIRES_NEW。对于嵌入式事务的处理，Spring 主要考虑了两种方式的处理。
  + Spring 中允许嵌入式事务的时候，则首选设置保存点的方式作为异常处理的回滚。
  + 对于其他方式，比如 JTA 无法使用保存点的方式，那么处理方式于 PROPAGATION_REQUIRES_NEW 相同，而一旦出现异常，则由 Spring 的事务异常处理机制去完成后续操作。

**AbstractPlatformTransactionManager#suspend**

```java
/**
 * Suspend the given transaction. Suspends transaction synchronization first,
 * then delegates to the {@code doSuspend} template method.
 * @param transaction the current transaction object
 * (or {@code null} to just suspend active synchronizations, if any)
 * @return an object that holds suspended resources
 * (or {@code null} if neither transaction nor synchronization active)
 * @see #doSuspend
 * @see #resume
 */
@Nullable
protected final SuspendedResourcesHolder suspend(@Nullable Object transaction) throws TransactionException {
   if (TransactionSynchronizationManager.isSynchronizationActive()) {
      List<TransactionSynchronization> suspendedSynchronizations = doSuspendSynchronization();
      try {
         Object suspendedResources = null;
         if (transaction != null) {
            suspendedResources = doSuspend(transaction);
         }
         String name = TransactionSynchronizationManager.getCurrentTransactionName();
         TransactionSynchronizationManager.setCurrentTransactionName(null);
         boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
         TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);
         Integer isolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
         TransactionSynchronizationManager.setCurrentTransactionIsolationLevel(null);
         boolean wasActive = TransactionSynchronizationManager.isActualTransactionActive();
         TransactionSynchronizationManager.setActualTransactionActive(false);
         return new SuspendedResourcesHolder(
               suspendedResources, suspendedSynchronizations, name, readOnly, isolationLevel, wasActive);
      }
      catch (RuntimeException | Error ex) {
         // doSuspend failed - original transaction is still active...
         doResumeSynchronization(suspendedSynchronizations);
         throw ex;
      }
   }
   else if (transaction != null) {
      // Transaction active but no synchronization active.
      Object suspendedResources = doSuspend(transaction);
      return new SuspendedResourcesHolder(suspendedResources);
   }
   else {
      // Neither transaction nor synchronization active.
      return null;
   }
}
```

### 3. 准备事务信息

当已经建立事务连接并完成了事务信息的提取后，需要将所有的事务信息统一记录在 TransactionInfo 类型的实例中，这个实例包含了目标方法开始前的所有状态信息，一旦事务执行失败，Spring 会通过 TransactionInfo 类型的实例中的信息来进行回滚等后续工作。

**TransactionAspectSupport#prepareTransactionInfo**

```java
/**
 * Prepare a TransactionInfo for the given attribute and status object.
 * @param txAttr the TransactionAttribute (may be {@code null})
 * @param joinpointIdentification the fully qualified method name
 * (used for monitoring and logging purposes)
 * @param status the TransactionStatus for the current transaction
 * @return the prepared TransactionInfo object
 */
protected TransactionInfo prepareTransactionInfo(@Nullable PlatformTransactionManager tm,
      @Nullable TransactionAttribute txAttr, String joinpointIdentification,
      @Nullable TransactionStatus status) {

   TransactionInfo txInfo = new TransactionInfo(tm, txAttr, joinpointIdentification);
   if (txAttr != null) {
      // We need a transaction for this method...
      if (logger.isTraceEnabled()) {
         logger.trace("Getting transaction for [" + txInfo.getJoinpointIdentification() + "]");
      }
      // The transaction manager will flag an error if an incompatible tx already exists.
      txInfo.newTransactionStatus(status);
   }
   else {
      // The TransactionInfo.hasTransaction() method will return false. We created it only
      // to preserve the integrity of the ThreadLocal stack maintained in this class.
      if (logger.isTraceEnabled()) {
         logger.trace("No need to create transaction for [" + joinpointIdentification +
               "]: This method is not transactional.");
      }
   }

   // We always bind the TransactionInfo to the thread, even if we didn't create
   // a new transaction here. This guarantees that the TransactionInfo stack
   // will be managed correctly even if no transaction was created by this aspect.
   txInfo.bindToThread();
   return txInfo;
} 
```

1. **TransactionAspectSupport#createTransactionIfNecessary** 

2. createTransactionIfNecessary 里面调用 AbstractPlatformTransactionManager#getTransaction，就是上面的创建事务 -> 存在事务处理 -> 超时处理 -> 挂起 等操作的方法，
3. 然后执行 prepareTransactionInfo

## 回滚处理

在  **TransactionAspectSupport#invokeWithinTransaction** 里面执行完 createTransactionIfNecessary 后，如果抛出异常，则进行捕捉，并且执行 completeTransactionAfterThrowing 方法。

**TransactionAspectSupport#completeTransactionAfterThrowing**

```java
/**
 * Handle a throwable, completing the transaction.
 * We may commit or roll back, depending on the configuration.
 * @param txInfo information about the current transaction
 * @param ex throwable encountered
 */
protected void completeTransactionAfterThrowing(@Nullable TransactionInfo txInfo, Throwable ex) {
    // 当抛出异常时首先判断当前是否存在事务，如果没有，则不执行任何操作
   if (txInfo != null && txInfo.getTransactionStatus() != null) {
      if (logger.isTraceEnabled()) {
         logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() +
               "] after exception: " + ex);
      }
       // 这里判断是否回滚默认的依据是抛出的异常是否是 RuntimeException 或者是 Error 的类型
      if (txInfo.transactionAttribute != null && txInfo.transactionAttribute.rollbackOn(ex)) {
         try {
             // 根据 TransactionStatus 信息进行回滚处理
            txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
         }
         catch (TransactionSystemException ex2) {
            logger.error("Application exception overridden by rollback exception", ex);
            ex2.initApplicationException(ex);
            throw ex2;
         }
         catch (RuntimeException | Error ex2) {
            logger.error("Application exception overridden by rollback exception", ex);
            throw ex2;
         }
      }
      else {
         // We don't roll back on this exception.
         // Will still roll back if TransactionStatus.isRollbackOnly() is true.
          // 如果不满足回滚条件即使抛出异常也同样会提交
         try {
            txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
         }
         catch (TransactionSystemException ex2) {
            logger.error("Application exception overridden by commit exception", ex);
            ex2.initApplicationException(ex);
            throw ex2;
         }
         catch (RuntimeException | Error ex2) {
            logger.error("Application exception overridden by commit exception", ex);
            throw ex2;
         }
      }
   }
}
```

### 1. 默认的回滚条件

**DefaultTransactionAttribute#rollbackOn**

```java
public boolean rollbackOn(Throwable ex) {
   return (ex instanceof RuntimeException || ex instanceof Error);
}
```

可以通过注解的方式来改变

### 2. 回滚处理

一旦符合回滚条件， Spring 就会将程序引导至回滚处理函数中。

**AbstractPlatformTransactionManager#rollback** 

```java
/**
 * This implementation of rollback handles participating in existing
 * transactions. Delegates to {@code doRollback} and
 * {@code doSetRollbackOnly}.
 * @see #doRollback
 * @see #doSetRollbackOnly
 */
@Override
public final void rollback(TransactionStatus status) throws TransactionException {
   // 如果事务已经完成，则抛出异常
   if (status.isCompleted()) {
      throw new IllegalTransactionStateException(
            "Transaction is already completed - do not call commit or rollback more than once per transaction");
   }

   DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
   processRollback(defStatus, false);
}
```

**AbstractPlatformTransactionManager#processRollback**

```java
/**
 * Process an actual rollback.
 * The completed flag has already been checked.
 * @param status object representing the transaction
 * @throws TransactionException in case of rollback failure
 */
private void processRollback(DefaultTransactionStatus status, boolean unexpected) {
   try {
      boolean unexpectedRollback = unexpected;

      try {
          // 激活所有 TransactionSynchronization 中对应的方法
         triggerBeforeCompletion(status);
		 // 如果有保存点
         if (status.hasSavepoint()) {
            if (status.isDebug()) {
               logger.debug("Rolling back transaction to savepoint");
            }
             // 如果有保存点，也就是当前事务为单独的线程则会退到保存点
            status.rollbackToHeldSavepoint();
         }
         else if (status.isNewTransaction()) {
            if (status.isDebug()) {
               logger.debug("Initiating transaction rollback");
            }
             // 如果当前事务为独立的新事务，则直接回退
            doRollback(status);
         }
         else {
            // Participating in larger transaction
            if (status.hasTransaction()) {
               if (status.isLocalRollbackOnly() || isGlobalRollbackOnParticipationFailure()) {
                  if (status.isDebug()) {
                     logger.debug("Participating transaction failed - marking existing transaction as rollback-only");
                  }
                   // 如果当前事务不是独立的事务，那么只能标记状态，等到事务链执行完毕后统一回滚。
                  doSetRollbackOnly(status);
               }
               else {
                  if (status.isDebug()) {
                     logger.debug("Participating transaction failed - letting transaction originator decide on rollback");
                  }
               }
            }
            else {
               logger.debug("Should roll back transaction but cannot - no transaction available");
            }
            // Unexpected rollback only matters here if we're asked to fail early
            if (!isFailEarlyOnGlobalRollbackOnly()) {
               unexpectedRollback = false;
            }
         }
      }
      catch (RuntimeException | Error ex) {
         triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
         throw ex;
      }
		// 激活所有 TransactionSynchronization 中对应的方法
      triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);

      // Raise UnexpectedRollbackException if we had a global rollback-only marker
      if (unexpectedRollback) {
         throw new UnexpectedRollbackException(
               "Transaction rolled back because it has been marked as rollback-only");
      }
   }
   finally {
       // 晴空记录的资源并将挂起的资源恢复
      cleanupAfterCompletion(status);
   }
}
```

上述方法过程如下：

1. 



## 事务提交

