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

### 2. 候选增强器中寻找到匹配项

### 3. 提取事务标签

