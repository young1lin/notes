# 示例

不做演示，在 Evaluation 那个项目也有 demo 代码。

# 动态 AOP 自定义标签

## 找到 AopNamespaceHandler

```java
/**
 * {@code NamespaceHandler} for the {@code aop} namespace.
 *
 * <p>Provides a {@link org.springframework.beans.factory.xml.BeanDefinitionParser} for the
 * {@code <aop:config>} tag. A {@code config} tag can include nested
 * {@code pointcut}, {@code advisor} and {@code aspect} tags.
 *
 * <p>The {@code pointcut} tag allows for creation of named
 * {@link AspectJExpressionPointcut} beans using a simple syntax:
 * <pre class="code">
 * &lt;aop:pointcut id=&quot;getNameCalls&quot; expression=&quot;execution(* *..ITestBean.getName(..))&quot;/&gt;
 * </pre>
 *
 * <p>Using the {@code advisor} tag you can configure an {@link org.springframework.aop.Advisor}
 * and have it applied to all relevant beans in you {@link org.springframework.beans.factory.BeanFactory}
 * automatically. The {@code advisor} tag supports both in-line and referenced
 * {@link org.springframework.aop.Pointcut Pointcuts}:
 *
 * <pre class="code">
 * &lt;aop:advisor id=&quot;getAgeAdvisor&quot;
 *     pointcut=&quot;execution(* *..ITestBean.getAge(..))&quot;
 *     advice-ref=&quot;getAgeCounter&quot;/&gt;
 *
 * &lt;aop:advisor id=&quot;getNameAdvisor&quot;
 *     pointcut-ref=&quot;getNameCalls&quot;
 *     advice-ref=&quot;getNameCounter&quot;/&gt;</pre>
 *
 * @author Rob Harrop
 * @author Adrian Colyer
 * @author Juergen Hoeller
 * @since 2.0
 */
public class AopNamespaceHandler extends NamespaceHandlerSupport {

   /**
    * Register the {@link BeanDefinitionParser BeanDefinitionParsers} for the
    * '{@code config}', '{@code spring-configured}', '{@code aspectj-autoproxy}'
    * and '{@code scoped-proxy}' tags.
    */
   @Override
   public void init() {
      // In 2.0 XSD as well as in 2.5+ XSDs
      registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
       // 这里就是解析 AOP 的转换器
      registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
      registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());

      // Only in 2.0 XSD: moved to context namespace in 2.5+
      registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
   }

}
```

**AspectJAutoProxyBeanDefinitionParser#parse**

```java
@Override
@Nullable
public BeanDefinition parse(Element element, ParserContext parserContext) {
    // 锚点 1
   AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
    // 对于注解中子类的处理
   extendBeanDefinition(element, parserContext);
   return null;
}
```

**AopNamespaceUtils#registerAspectJAnnotationAutoProxyCreatorIfNecessary**

```java
// 锚点 1
public static void registerAspectJAnnotationAutoProxyCreatorIfNecessary(
      ParserContext parserContext, Element sourceElement) {
	// 锚点 2
    // 注册或升级 AutoProxyCreator 定义 beanName 为 org.springframework.aop.config.internalAutoProxyCreator 的 BeanDefinition
   BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(
         parserContext.getRegistry(), parserContext.extractSource(sourceElement));
    // 对于 proxy-target-class 以及 expose-proxy 属性的处理
   useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
    // 注册组件并通知，便于监听器做进一步处理
    // 其中 beanDefinition 中的 className 为 AnnotationAwareAspectJAutoProxyCreator
   registerComponentIfNecessary(beanDefinition, parserContext);
}
```

上面的 **AopNamespaceUtils#registerComponentIfNecessary**主要完成了三件事

### 1. 注册或升级 AnnotationAwareAspectJAtuoProxyCreator

对于 AOP 的实现，基本上都是靠 AnnotationAwareAspectJAutoProxyCreator 去完成，它可以根据 @Point 注解定义的切点来自动代理相匹配的 bean。但是为了配置简便，Spring 使用了自定义配置来帮助我们自动注册 AnnotationAwareAspectJAutoProxyCreator，其注册过程如下。

**AopConfigUtils#registerAspectJAnnotationAutoProxyCreatorIfNecessary**

```java
// 锚点 2
@Nullable
public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(
      BeanDefinitionRegistry registry, @Nullable Object source) {
	// 锚点 3
   return registerOrEscalateApcAsRequired(AnnotationAwareAspectJAutoProxyCreator.class, registry, source);
}
```

**AopConfigUtils#registerOrEscalateApcAsRequired**

```java
// 锚点 3
@Nullable
private static BeanDefinition registerOrEscalateApcAsRequired(
      Class<?> cls, BeanDefinitionRegistry registry, @Nullable Object source) {

   Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
	// 如果已经存在了自动代理创建器且存在的自动代理创建器与现在的不同，那么需要根据优先级来判断到底需要使用哪个
   if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
      BeanDefinition apcDefinition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
      if (!cls.getName().equals(apcDefinition.getBeanClassName())) {
         int currentPriority = findPriorityForClass(apcDefinition.getBeanClassName());
         int requiredPriority = findPriorityForClass(cls);
          // 根据优先级
         if (currentPriority < requiredPriority) {
             // 替换 beanClassName
            apcDefinition.setBeanClassName(cls.getName());
         }
      }
       // 如果已经存在自动大力创建器并且与将要创建的一样，那么无需再次创建。
      return null;
   }

   RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
   beanDefinition.setSource(source);
   beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
   beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
   registry.registerBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME, beanDefinition);
   return beanDefinition;
}
```

### 2. 处理 proxy-target-class 以及 expose-proxy 属性

**AopNamespaceUtils#useClassProxyingIfNecessary**

```java
private static void useClassProxyingIfNecessary(BeanDefinitionRegistry registry, @Nullable Element sourceElement) {
   if (sourceElement != null) {
       // 对于 proxy-target-class 属性的处理
      boolean proxyTargetClass = Boolean.parseBoolean(sourceElement.getAttribute(PROXY_TARGET_CLASS_ATTRIBUTE));
      if (proxyTargetClass) {
         AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
      }
       // 对于 expose-proxy 属性的处理
      boolean exposeProxy = Boolean.parseBoolean(sourceElement.getAttribute(EXPOSE_PROXY_ATTRIBUTE));
      if (exposeProxy) {
         AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
      }
   }
}
```

**AopConfigUtils#forceAutoProxyCreatorToUseClassProxying**

```java
// 强制使用的过程其实也是一个属性设置的过程
public static void forceAutoProxyCreatorToUseClassProxying(BeanDefinitionRegistry registry) {
   if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
      BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
      definition.getPropertyValues().add("proxyTargetClass", Boolean.TRUE);
   }
}
```

**AopConfigUtils#forceAutoProxyCreatorToExposeProxy**

```java
public static void forceAutoProxyCreatorToExposeProxy(BeanDefinitionRegistry registry) {
   if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
      BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
      definition.getPropertyValues().add("exposeProxy", Boolean.TRUE);
   }
}
```

+ proxy-target-class：Spring AOP 部分使用 JDK 动态代理或者 CGLIB 来为目标对象创建代理（书上是建议尽量使用 JDK 的动态代理，我觉得都差不多）。如果被代理的目标对象实现了至少一个接口，则会使用 JDK 动态代理。所有该目标类型实现的接口都将被代理。若该目标对象没有实现任何接口，则创建一个 CGLIB 代理。如果希望强制使用 CGLIB 代理，例如希望代理目标对象的所有方法，而不只是实现自接口的方法。会有两个问题

  + 无法通知（Advise）Final 方法，因为 Final 方法不能被覆盖。
  + 需要引入 CGLIB 二进制发行包在 classpath 下面。

  强制使用 `CGLIB <aop:config proxy-target-class="true">...</aop:config>`

+ JDK 动态代理：其代理对象必须是某个接口的实现，它是通过在运行期间创建一个接口的实现类来完成目标对象的代理。

+ CGLIB 代理：运行期间生成的代理对象是针对目标类扩展的子类。CGLIB 是高效的代码生成包，底层是靠 ASM 操作字节码实现的，性能比 JDK 强（现在其实差不多了）。

+ expose-proxy：有时候目标对象内部的自我调用将无法实施切面中的增强。

```java
public interface Service{

    void a();
    
    void b();

}
@Service
public class DefaultService implements Service{

    @Tranactional(propagation = Propagation.REQUIRED)
    @Override
    public void a(){
        this.b();
    }
    
    @Tranactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void b(){
        
    }

}
```

需要把 this.b() 改成 ((Service)AopContext.currentProxy()).b();或者自己引入自己，例如 

```java
public class DefaultService implements Service{
    /**
     * 这里为了演示方便，没有用构造器注入，正确的应该是构造器注入更为合理
     */
    @Autowried
    private Service service;
    
    @Tranactional(propagation = Propagation.REQUIRED)
    @Override
    public void a(){
        service.b();
    }
    
    @Tranactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void b(){
        
    }
    
}
```

# 创建 AOP 代理

**AbstractAutoProxyCreator#postProcessAfterInitialization**

```java
/**
 * Create a proxy with the configured interceptors if the bean is
 * identified as one to proxy by the subclass.
 * @see #getAdvicesAndAdvisorsForBean
 */
@Override
public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
   if (bean != null) {
       // 根据给定的 bean 的 class 和 name 构建出个 key，格式为 beanClassName_beanName
      Object cacheKey = getCacheKey(bean.getClass(), beanName);
      if (this.earlyProxyReferences.remove(cacheKey) != bean) {
          // 如果它适合被代理，则需要封装指定 bean
         return wrapIfNecessary(bean, beanName, cacheKey);
      }
   }
   return bean;
}
```

**AbstractAutoProxyCreator#wrapIfNecessary**

```java
/**
 * Wrap the given bean if necessary, i.e. if it is eligible for being proxied.
 * @param bean the raw bean instance
 * @param beanName the name of the bean
 * @param cacheKey the cache key for metadata access
 * @return a proxy wrapping the bean, or the raw bean instance as-is
 */
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
   // 如果已经处理过
    if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
      return bean;
   }
    // 无需增强
   if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
      return bean;
   }
    // 给定的 bean 类是否代表一个基础设施类，基础设施类不应被代理，或者配置了指定 bean 不需要自代理
   if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
      this.advisedBeans.put(cacheKey, Boolean.FALSE);
      return bean;
   }

   // Create proxy if we have advice.
   Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
   if (specificInterceptors != DO_NOT_PROXY) {
      this.advisedBeans.put(cacheKey, Boolean.TRUE);
      Object proxy = createProxy(
            bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
      this.proxyTypes.put(cacheKey, proxy.getClass());
      return proxy;
   }

   this.advisedBeans.put(cacheKey, Boolean.FALSE);
   return bean;
}
```

创建代理主要包含了两个步骤。

1. 获取增强方法或者增强器；
2. 根据获取的增强进行代理。

**AbstractAutoProxyCreator 的 postProcessAfterInitialization**

![AbstractAutoProxyCreator 的 postProcessAfterInitialization.png](https://i.loli.net/2020/11/17/8eQ3LJVoNgbGCc2.png)

**AbstractAdvisorAutoProxyCreator#getAdvicesAndAdvisorsForBean**

```java
@Override
@Nullable
protected Object[] getAdvicesAndAdvisorsForBean(
      Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {

   List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
   if (advisors.isEmpty()) {
      return DO_NOT_PROXY;
   }
   return advisors.toArray();
}

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
    // 寻找候选 Advisors
   List<Advisor> candidateAdvisors = findCandidateAdvisors();
   List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
   extendAdvisors(eligibleAdvisors);
   if (!eligibleAdvisors.isEmpty()) {
      eligibleAdvisors = sortAdvisors(eligibleAdvisors);
   }
    // 如果没找到，便返回 DO_NOT_PROXY，这里的 DO_NOT_PROXY = null 
   return eligibleAdvisors;
}
```

## 获取增强器

注解完成的 AOP 是通过 AnnotationAwareAspectAutoProxyCreator 类完成的，在 findCandidateAdvisors 中可以找到。

**AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors**

```java
@Override
protected List<Advisor> findCandidateAdvisors() {
   // Add all the Spring advisors found according to superclass rules.
   List<Advisor> advisors = super.findCandidateAdvisors();
   // Build Advisors for all AspectJ aspects in the bean factory.
   if (this.aspectJAdvisorsBuilder != null) {
       // advisors add all aspectJAdvisors
      advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
   }
   return advisors;
}
```

**AnnotationAwareAspectAutoProxyCreator**

![image.png](https://i.loli.net/2020/11/16/Rexwrh9a6dTLSQY.png)

SmartInstantiationAwareBeanPostProcessor 接口，在 Spring 声明周期，BeanDefinition 实例化前会执行这个方法，如果返回不为空，则跳过后面的步骤，直接返回 Bean 实例。

下面的步骤是 

1. 获取所有 beanName，这一步骤中所有在 beanFactory 中注册的 bean 都会被提取出来。
2. 遍历所有的 beanName，并找出声明 AspectJ 注解的类，进行进一步的处理。
3. 对标记为 AspectJ 注解的类进行增强器的提取。
4. 将提取结果加入缓存。

**BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors**

```java
/**
 * aspectJAdvisorsBuilder.buildAspectJAdvisors()
 * 
 * Look for AspectJ-annotated aspect beans in the current bean factory,
 * and return to a list of Spring AOP Advisors representing them.
 * <p>Creates a Spring Advisor for each AspectJ advice method.
 * @return the list of {@link org.springframework.aop.Advisor} beans
 * @see #isEligibleBean
 */
public List<Advisor> buildAspectJAdvisors() {
   List<String> aspectNames = this.aspectBeanNames;

   if (aspectNames == null) {
      synchronized (this) {
         aspectNames = this.aspectBeanNames;
         if (aspectNames == null) {
            List<Advisor> advisors = new ArrayList<>();
            aspectNames = new ArrayList<>();
             // 获取所有的 beanName
            String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                  this.beanFactory, Object.class, true, false);
             // 不合法的 bean 则略过，由子类定义规则，默认返回 true
            for (String beanName : beanNames) {
               if (!isEligibleBean(beanName)) {
                  continue;
               }
                // 进一步处理
                // 我们必须小心，不要急于实例化bean，因为在这种情况下，它们将由Spring容器缓存，但不会被织入。
               // We must be careful not to instantiate beans eagerly as in this case they
               // would be cached by the Spring container but would not have been weaved.
               Class<?> beanType = this.beanFactory.getType(beanName);
               if (beanType == null) {
                  continue;
               }
                //如果存在 AspectJ 注解
               if (this.advisorFactory.isAspect(beanType)) {
                  aspectNames.add(beanName);
                  AspectMetadata amd = new AspectMetadata(beanType, beanName);
                  if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
                     MetadataAwareAspectInstanceFactory factory =
                           new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
                      // 锚点 1
                      // 解析标记 AspectJ 注解中的增强方法
                     List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
                     if (this.beanFactory.isSingleton(beanName)) {
                        this.advisorsCache.put(beanName, classAdvisors);
                     }
                     else {
                        this.aspectFactoryCache.put(beanName, factory);
                     }
                     advisors.addAll(classAdvisors);
                  }
                  else {
                     // Per target or per this.
                     if (this.beanFactory.isSingleton(beanName)) {
                        throw new IllegalArgumentException("Bean with name '" + beanName +
                              "' is a singleton, but aspect instantiation model is not singleton");
                     }
                     MetadataAwareAspectInstanceFactory factory =
                           new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
                     this.aspectFactoryCache.put(beanName, factory);
                     advisors.addAll(this.advisorFactory.getAdvisors(factory));
                  }
               }
            }
            this.aspectBeanNames = aspectNames;
            return advisors;
         }
      }
   }

   if (aspectNames.isEmpty()) {
      return Collections.emptyList();
   }
    // 记录在缓存中
   List<Advisor> advisors = new ArrayList<>();
   for (String aspectName : aspectNames) {
      List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
      if (cachedAdvisors != null) {
         advisors.addAll(cachedAdvisors);
      }
      else {
         MetadataAwareAspectInstanceFactory factory = this.aspectFactoryCache.get(aspectName);
         advisors.addAll(this.advisorFactory.getAdvisors(factory));
      }
   }
   return advisors;
}
```

**ReflectiveAspectJAdvisorFactory#getAdvisors**

```java
// 锚点 1
@Override
public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
    // 获取标记为 AspectJ 的类
   Class<?> aspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
    // 获取标记为 AspectJ 的 name
   String aspectName = aspectInstanceFactory.getAspectMetadata().getAspectName();
   validate(aspectClass);

   // We need to wrap the MetadataAwareAspectInstanceFactory with a decorator
   // so that it will only instantiate once.
   MetadataAwareAspectInstanceFactory lazySingletonAspectInstanceFactory =
         new LazySingletonAspectInstanceFactoryDecorator(aspectInstanceFactory);

   List<Advisor> advisors = new ArrayList<>();
    // 锚点
   for (Method method : getAdvisorMethods(aspectClass)) {
      // Prior to Spring Framework 5.2.7, advisors.size() was supplied as the declarationOrderInAspect
      // to getAdvisor(...) to represent the "current position" in the declared methods list.
      // However, since Java 7 the "current position" is not valid since the JDK no longer
      // returns declared methods in the order in which they are declared in the source code.
      // Thus, we now hard code the declarationOrderInAspect to 0 for all advice methods
      // discovered via reflection in order to support reliable advice ordering across JVM launches.
      // Specifically, a value of 0 aligns with the default value used in
      // AspectJPrecedenceComparator.getAspectDeclarationOrder(Advisor).
      Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, 0, aspectName);
      if (advisor != null) {
         advisors.add(advisor);
      }
   }

   // If it's a per target aspect, emit the dummy instantiating aspect.
   if (!advisors.isEmpty() && lazySingletonAspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
      Advisor instantiationAdvisor = new SyntheticInstantiationAdvisor(lazySingletonAspectInstanceFactory);
      advisors.add(0, instantiationAdvisor);
   }

   // Find introduction fields.
   for (Field field : aspectClass.getDeclaredFields()) {
      Advisor advisor = getDeclareParentsAdvisor(field);
      if (advisor != null) {
         advisors.add(advisor);
      }
   }

   return advisors;
}
```

**ReflectiveAspectJAdvisorFactory#getAdvisorMethods**

```java
// 锚点
private List<Method> getAdvisorMethods(Class<?> aspectClass) {
   final List<Method> methods = new ArrayList<>();
   ReflectionUtils.doWithMethods(aspectClass, method -> {
      // Exclude pointcuts 声明为 Pointcut  的方法不处理 
      if (AnnotationUtils.getAnnotation(method, Pointcut.class) == null) {
         methods.add(method);
      }
   }, ReflectionUtils.USER_DECLARED_METHODS);
   if (methods.size() > 1) {
      methods.sort(METHOD_COMPARATOR);
   }
   return methods;
}
```

### 1. 普通增强器的获取

普通增强器的获取逻辑通过 getAdvisor 方法实现，实现步骤包括对切点的注解的获取以及根据注解信息生成增强。

**ReflectiveAspectJAdvisorFactory#getAdvisor**

```java
@Override
@Nullable
public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
      int declarationOrderInAspect, String aspectName) {

   validate(aspectInstanceFactory.getAspectMetadata().getAspectClass());
	// 切点信息的获取
   AspectJExpressionPointcut expressionPointcut = getPointcut(
         candidateAdviceMethod, aspectInstanceFactory.getAspectMetadata().getAspectClass());
   if (expressionPointcut == null) {
      return null;
   }
  // 根据切点信息生成增强器
   return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod,
         this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
}
```

1. 切点信息的获取。所谓获取切点信息就是指定注解的表达信息的获取，如@Before("test()")

**ReflectiveAspectJAdvisorFactory#getPointcut**

```java
@Nullable
private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
    // 锚点
    // 获取方法上的注解
   AspectJAnnotation<?> aspectJAnnotation =
         AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
   if (aspectJAnnotation == null) {
      return null;
   }
	// 使用 AspectJExpressionPointcut 实例封装获取的信息
   AspectJExpressionPointcut ajexp =
         new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class<?>[0]);
    // 提取得到的注解中的表达式，如：
    // @Point("execution(* *.*test*(..))") 中的 value
   ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
   if (this.beanFactory != null) {
      ajexp.setBeanFactory(this.beanFactory);
   }
   return ajexp;
}
```

**AbstractAspectJAdvisorFactory#findAspectJAnnotationOnMethod**

```java
private static final Class<?>[] ASPECTJ_ANNOTATION_CLASSES = new Class<?>[] {
			Pointcut.class, Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class};
/**
 * Find and return the first AspectJ annotation on the given method
 * (there <i>should</i> only be one anyway...).
 */
@SuppressWarnings("unchecked")
@Nullable
protected static AspectJAnnotation<?> findAspectJAnnotationOnMethod(Method method) {
   for (Class<?> clazz : ASPECTJ_ANNOTATION_CLASSES) {
       // 书上和这里不一样，这里把代码优化了，把局部变量提升至全局变量，并且静态且私有化，这也是提高加载速度的一种手段
      AspectJAnnotation<?> foundAnnotation = findAnnotation(method, (Class<Annotation>) clazz);
      if (foundAnnotation != null) {
         return foundAnnotation;
      }
   }
   return null;
}

@Nullable
private static <A extends Annotation> AspectJAnnotation<A> findAnnotation(Method method, Class<A> toLookFor) {
   A result = AnnotationUtils.findAnnotation(method, toLookFor);
   if (result != null) {
      return new AspectJAnnotation<>(result);
   }
   else {
      return null;
   }
}
```

2. 根据切点信息生成增强。所有的增强都由 Advisor 的实现类 InstantiationModelAwarePointcutAdvisorImpl 统一封装的。

**InstantiationModelAwarePointcutAdvisorImpl 构造方法**

```java
public InstantiationModelAwarePointcutAdvisorImpl(AspectJExpressionPointcut declaredPointcut,
      Method aspectJAdviceMethod, AspectJAdvisorFactory aspectJAdvisorFactory,
      MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {
	// 赋值切点
   this.declaredPointcut = declaredPointcut;
   this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
   this.methodName = aspectJAdviceMethod.getName();
   this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
   this.aspectJAdviceMethod = aspectJAdviceMethod;
   this.aspectJAdvisorFactory = aspectJAdvisorFactory;
   this.aspectInstanceFactory = aspectInstanceFactory;
   this.declarationOrder = declarationOrder;
   this.aspectName = aspectName;

   if (aspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
      // Static part of the pointcut is a lazy type.
      Pointcut preInstantiationPointcut = Pointcuts.union(
            aspectInstanceFactory.getAspectMetadata().getPerClausePointcut(), this.declaredPointcut);

      // Make it dynamic: must mutate from pre-instantiation to post-instantiation state.
      // If it's not a dynamic pointcut, it may be optimized out
      // by the Spring AOP infrastructure after the first evaluation.
      this.pointcut = new PerTargetInstantiationModelPointcut(
            this.declaredPointcut, preInstantiationPointcut, aspectInstanceFactory);
      this.lazy = true;
   }
   else {
      // A singleton aspect.
      this.pointcut = this.declaredPointcut;
      this.lazy = false;
       // 锚点
      this.instantiatedAdvice = instantiateAdvice(this.declaredPointcut);
   }
}
```

上面只是简单地将信息封装在类的实例中，所有的信息单纯地赋值，在实例初始化的过程中还完成了对于增强器的初始化。因为不同的增强所体现的逻辑是不同的，例如 @Before 和 @After 就是不同的增强位置。所以需要不同的增强器来完成不同的逻辑，根据注解中的信息初始化对应的增强器就是在 instantiateAdvice 中实现的。

**InstantiationModelAwarePointcutAdvisorImpl#instantiateAdvice**

```java
private Advice instantiateAdvice(AspectJExpressionPointcut pointcut) {
    // 锚点
   Advice advice = this.aspectJAdvisorFactory.getAdvice(this.aspectJAdviceMethod, pointcut,
         this.aspectInstanceFactory, this.declarationOrder, this.aspectName);
   return (advice != null ? advice : EMPTY_ADVICE);
}
```

**ReflectiveAspectJAdvisorFactory#getAdvice**

```java
// 锚点
@Override
@Nullable
public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
      MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {

   Class<?> candidateAspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
   validate(candidateAspectClass);

   AspectJAnnotation<?> aspectJAnnotation =
         AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
   if (aspectJAnnotation == null) {
      return null;
   }

   // If we get here, we know we have an AspectJ method.
   // Check that it's an AspectJ-annotated class
   if (!isAspect(candidateAspectClass)) {
      throw new AopConfigException("Advice must be declared inside an aspect type: " +
            "Offending method '" + candidateAdviceMethod + "' in class [" +
            candidateAspectClass.getName() + "]");
   }

   if (logger.isDebugEnabled()) {
      logger.debug("Found AspectJ method: " + candidateAdviceMethod);
   }

   AbstractAspectJAdvice springAdvice;
	// 根据不同的注解类型封装不同的增强器
   switch (aspectJAnnotation.getAnnotationType()) {
      case AtPointcut:
         if (logger.isDebugEnabled()) {
            logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
         }
         return null;
      case AtAround:
         springAdvice = new AspectJAroundAdvice(
               candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
         break;
      case AtBefore:
         springAdvice = new AspectJMethodBeforeAdvice(
               candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
         break;
      case AtAfter:
         springAdvice = new AspectJAfterAdvice(
               candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
         break;
      case AtAfterReturning:
         springAdvice = new AspectJAfterReturningAdvice(
               candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
         AfterReturning afterReturningAnnotation = (AfterReturning) aspectJAnnotation.getAnnotation();
         if (StringUtils.hasText(afterReturningAnnotation.returning())) {
            springAdvice.setReturningName(afterReturningAnnotation.returning());
         }
         break;
      case AtAfterThrowing:
         springAdvice = new AspectJAfterThrowingAdvice(
               candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
         AfterThrowing afterThrowingAnnotation = (AfterThrowing) aspectJAnnotation.getAnnotation();
         if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
            springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
         }
         break;
      default:
         throw new UnsupportedOperationException(
               "Unsupported advice type on method: " + candidateAdviceMethod);
   }

   // Now to configure the advice...
   springAdvice.setAspectName(aspectName);
   springAdvice.setDeclarationOrder(declarationOrder);
   String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
   if (argNames != null) {
      springAdvice.setArgumentNamesFromStringArray(argNames);
   }
   springAdvice.calculateArgumentBindings();

   return springAdvice;
}
```

常用的增强器实现。

+ MethodBeforeAdviceInterceptor

```java
/**
 * Interceptor to wrap a {@link MethodBeforeAdvice}.
 * <p>Used internally by the AOP framework; application developers should not
 * need to use this class directly.
 *
 * @author Rod Johnson
 * @see AfterReturningAdviceInterceptor
 * @see ThrowsAdviceInterceptor
 */
@SuppressWarnings("serial")
public class MethodBeforeAdviceInterceptor implements MethodInterceptor, BeforeAdvice, Serializable {

   private final MethodBeforeAdvice advice;


   /**
    * Create a new MethodBeforeAdviceInterceptor for the given advice.
    * @param advice the MethodBeforeAdvice to wrap
    */
   public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
      Assert.notNull(advice, "Advice must not be null");
      this.advice = advice;
   }


   @Override
   public Object invoke(MethodInvocation mi) throws Throwable {
      this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
      return mi.proceed();
   }

}
```

**AspectJMethodBeforeAdvice**

```java
/**
 * Spring AOP advice that wraps an AspectJ before method.
 *
 * @author Rod Johnson
 * @author Adrian Colyer
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice implements MethodBeforeAdvice, Serializable {

   public AspectJMethodBeforeAdvice(
         Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

      super(aspectJBeforeAdviceMethod, pointcut, aif);
   }

	// 上面会调用这个方法，再调用下面的方法
   @Override
   public void before(Method method, Object[] args, @Nullable Object target) throws Throwable {
      invokeAdviceMethod(getJoinPointMatch(), null, null);
   }

   @Override
   public boolean isBeforeAdvice() {
      return true;
   }

   @Override
   public boolean isAfterAdvice() {
      return false;
   }

}
```

**AbstractAspectJAdvice#invokeAdviceMethod**

```java
/**
 * Invoke the advice method.
 * @param jpMatch the JoinPointMatch that matched this execution join point
 * @param returnValue the return value from the method execution (may be null)
 * @param ex the exception thrown by the method execution (may be null)
 * @return the invocation result
 * @throws Throwable in case of invocation failure
 */
protected Object invokeAdviceMethod(
      @Nullable JoinPointMatch jpMatch, @Nullable Object returnValue, @Nullable Throwable ex)
      throws Throwable {

   return invokeAdviceMethodWithGivenArgs(argBinding(getJoinPoint(), jpMatch, returnValue, ex));
}
```

**AbstractAspectJAdvice#invokeAdviceMethodWithGivenArgs**

```java
protected Object invokeAdviceMethodWithGivenArgs(Object[] args) throws Throwable {
   Object[] actualArgs = args;
   if (this.aspectJAdviceMethod.getParameterCount() == 0) {
      actualArgs = null;
   }
   try {
      ReflectionUtils.makeAccessible(this.aspectJAdviceMethod);
      // TODO AopUtils.invokeJoinpointUsingReflection
       // 这个其实就是方法了，Method#invoke
      return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), actualArgs);
   }
   catch (IllegalArgumentException ex) {
      throw new AopInvocationException("Mismatch on arguments to advice method [" +
            this.aspectJAdviceMethod + "]; pointcut expression [" +
            this.pointcut.getPointcutExpression() + "]", ex);
   }
   catch (InvocationTargetException ex) {
      throw ex.getTargetException();
   }
}
```

+ AspectJAfterAdvice

后置增强和前置增强有些不一样。前置增强大致的结构是在拦截器链中防止 MethodBeforeAdviceInterceptor，而在 MethodBeforeAdviceInterceptor 中又防止了 AspectJMethodBeforeAdvice，并在调用 invoke 的时候首先串联调用。

```java
/**
 * Spring AOP advice wrapping an AspectJ after advice method.
 *
 * @author Rod Johnson
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJAfterAdvice extends AbstractAspectJAdvice
      implements MethodInterceptor, AfterAdvice, Serializable {

   public AspectJAfterAdvice(
         Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

      super(aspectJBeforeAdviceMethod, pointcut, aif);
   }


   @Override
   public Object invoke(MethodInvocation mi) throws Throwable {
      try {
         return mi.proceed();
      }
      finally {
         invokeAdviceMethod(getJoinPointMatch(), null, null);
      }
   }

   @Override
   public boolean isBeforeAdvice() {
      return false;
   }

   @Override
   public boolean isAfterAdvice() {
      return true;
   }

}
```

### 2. 增强同步实例化增强器

如果寻找的增强器不为空且又配置了增强延迟初始化，那么就需要在首位加入同步实例化增强器。同步实例化增强器 SyntheticInstantiationAdvisor 如下。

**ReflectiveAspectJAdvisorFactory.SyntheticInstantiationAdvisor**

```java
/**
 * Synthetic advisor that instantiates the aspect.
 * Triggered by per-clause pointcut on non-singleton aspect.
 * The advice has no effect.
 */
@SuppressWarnings("serial")
protected static class SyntheticInstantiationAdvisor extends DefaultPointcutAdvisor {

   public SyntheticInstantiationAdvisor(final MetadataAwareAspectInstanceFactory aif) {
      super(aif.getAspectMetadata().getPerClausePointcut(), (MethodBeforeAdvice)
            (method, args, target) -> aif.getAspectInstance());
   }
}
```

### 3. 获取 DeclareParents 注解

**ReflectiveAspectJAdvisorFactory#getDeclareParentsAdvisor**

```java
/**
 * Build a {@link org.springframework.aop.aspectj.DeclareParentsAdvisor}
 * for the given introduction field.
 * <p>Resulting Advisors will need to be evaluated for targets.
 * @param introductionField the field to introspect
 * @return the Advisor instance, or {@code null} if not an Advisor
 */
@Nullable
private Advisor getDeclareParentsAdvisor(Field introductionField) {
   DeclareParents declareParents = introductionField.getAnnotation(DeclareParents.class);
   if (declareParents == null) {
      // Not an introduction field
      return null;
   }

   if (DeclareParents.class == declareParents.defaultImpl()) {
      throw new IllegalStateException("'defaultImpl' attribute must be set on DeclareParents");
   }

   return new DeclareParentsAdvisor(
         introductionField.getType(), declareParents.value(), declareParents.defaultImpl());
}
```

## 寻找匹配的增强器

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
       // 过滤已经得到的 advisors
      return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
   }
   finally {
      ProxyCreationContext.setCurrentProxiedBeanName(null);
   }
}
```

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
    // 首先处理引介增强
   for (Advisor candidate : candidateAdvisors) {
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
       // 对于普通的 bean 的处理
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

# 创建代理

**AbstractAutoProxyCreator#createProxy**

```java
/**
 * Create an AOP proxy for the given bean.
 * @param beanClass the class of the bean
 * @param beanName the name of the bean
 * @param specificInterceptors the set of interceptors that is
 * specific to this bean (may be empty, but not null)
 * @param targetSource the TargetSource for the proxy,
 * already pre-configured to access the bean
 * @return the AOP proxy for the bean
 * @see #buildAdvisors
 */
protected Object createProxy(Class<?> beanClass, @Nullable String beanName,
      @Nullable Object[] specificInterceptors, TargetSource targetSource) {

   if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
      AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory) this.beanFactory, beanName, beanClass);
   }

   ProxyFactory proxyFactory = new ProxyFactory();
    // 获取当前类中相关属性
   proxyFactory.copyFrom(this);
	// 决定对于给定的 bean 是否应该使用 targetClass 而不是它的接口代理
    // 检查 proxyTragetClass 设置以及 preserveTargetClass 属性
   if (!proxyFactory.isProxyTargetClass()) {
      if (shouldProxyTargetClass(beanClass, beanName)) {
         proxyFactory.setProxyTargetClass(true);
      }
      else {
          // 评估代理接口，并且评估通过就给添加代理接口
         evaluateProxyInterfaces(beanClass, proxyFactory);
      }
   }

   Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
    // 加入增强器
   proxyFactory.addAdvisors(advisors);
    // 设置要代理的类
   proxyFactory.setTargetSource(targetSource);
	// 定制代理
   customizeProxyFactory(proxyFactory);
	// 是否冻结，东来控制代理工厂被配置之后，是否还允许修改通知
    // 缺省值为 false
   proxyFactory.setFrozen(this.freezeProxy);
   if (advisorsPreFiltered()) {
      proxyFactory.setPreFiltered(true);
   }

   return proxyFactory.getProxy(getProxyClassLoader());
}
```

对于代理类的创建及处理，Spring 委托给了 ProxyFactory 去处理，而在此函数中主要是对 ProxyFactory 的初始化操作，进而对真正的创建代理做准备。初始化操作包括如下：

1. 获取当前类中的属性。
2. 添加代理接口。
3. 封装 Advisor 并加入到 ProxyFactory 中。
4. 设置要代理的类。
5. 还为子类提供了定制的函数 customizeProxyFactory，子类可以在此函数中进行对 ProxyFactory 的进一步封装。
6. 进行获取代理操作。

其中，封装 Advisor 并加入到 ProxyFactory 中以及创建代理是两个相对繁琐的过程，可以通过 ProxyFactory 提供的 addAdvisors 方法直接将增强器置入代理创建工厂中，

**AbstractAutoProxyCreator#buildAdvisors**

```java
/**
 * Determine the advisors for the given bean, including the specific interceptors
 * as well as the common interceptor, all adapted to the Advisor interface.
 * @param beanName the name of the bean
 * @param specificInterceptors the set of interceptors that is
 * specific to this bean (may be empty, but not null)
 * @return the list of Advisors for the given bean
 */
protected Advisor[] buildAdvisors(@Nullable String beanName, @Nullable Object[] specificInterceptors) {
   // Handle prototypes correctly...
   Advisor[] commonInterceptors = resolveInterceptorNames();

   List<Object> allInterceptors = new ArrayList<>();
   if (specificInterceptors != null) {
       // 加入拦截器
      allInterceptors.addAll(Arrays.asList(specificInterceptors));
      if (commonInterceptors.length > 0) {
         if (this.applyCommonInterceptorsFirst) {
            allInterceptors.addAll(0, Arrays.asList(commonInterceptors));
         }
         else {
            allInterceptors.addAll(Arrays.asList(commonInterceptors));
         }
      }
   }
   if (logger.isTraceEnabled()) {
      int nrOfCommonInterceptors = commonInterceptors.length;
      int nrOfSpecificInterceptors = (specificInterceptors != null ? specificInterceptors.length : 0);
      logger.trace("Creating implicit proxy for bean '" + beanName + "' with " + nrOfCommonInterceptors +
            " common interceptors and " + nrOfSpecificInterceptors + " specific interceptors");
   }

   Advisor[] advisors = new Advisor[allInterceptors.size()];
   for (int i = 0; i < allInterceptors.size(); i++) {
       // 下面的方法
      advisors[i] = this.advisorAdapterRegistry.wrap(allInterceptors.get(i));
   }
   return advisors;
}
```

**DefaultAdvisorAdapterRegistry#wrap**

```java
@Override
public Advisor wrap(Object adviceObject) throws UnknownAdviceTypeException {
   if (adviceObject instanceof Advisor) {
      return (Advisor) adviceObjec t;
   }
   if (!(adviceObject instanceof Advice)) {
      throw new UnknownAdviceTypeException(adviceObject);
   }
   Advice advice = (Advice) adviceObject;
   if (advice instanceof MethodInterceptor) {
      // So well-known it doesn't even need an adapter.
      return new DefaultPointcutAdvisor(advice);
   }
   for (AdvisorAdapter adapter : this.adapters) {
      // Check that it is supported.
      if (adapter.supportsAdvice(advice)) {
         return new DefaultPointcutAdvisor(advice);
      }
   }
   throw new UnknownAdviceTypeException(advice);
}
```

### 1. 创建代理

**ProxyFactory#getProxy**

```java
/**
 * Create a new proxy according to the settings in this factory.
 * <p>Can be called repeatedly. Effect will vary if we've added
 * or removed interfaces. Can add and remove interceptors.
 * <p>Uses the given class loader (if necessary for proxy creation).
 * @param classLoader the class loader to create the proxy with
 * (or {@code null} for the low-level proxy facility's default)
 * @return the proxy object
 */
public Object getProxy(@Nullable ClassLoader classLoader) {
   return createAopProxy().getProxy(classLoader);
}
```

**ProxyCreatorSupport#createAopProxy**

```java
/**
 * Subclasses should call this to get a new AOP proxy. They should <b>not</b>
 * create an AOP proxy with {@code this} as an argument.
 */
protected final synchronized AopProxy createAopProxy() {
   if (!this.active) {
      activate();
   }
    // getProxyFactory 就是获取当前的 proxyFactory，没有什么特殊的地方  		return this.aopProxyFactory;

   return getAopProxyFactory().createAopProxy(this);
}
```

**DefaultAopProxyFactory#createAopProxy**

```java
@Override
public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
   if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
      Class<?> targetClass = config.getTargetClass();
      if (targetClass == null) {
         throw new AopConfigException("TargetSource cannot determine target class: " +
               "Either an interface or a target is required for proxy creation.");
      }
      if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
         return new JdkDynamicAopProxy(config);
      }
      return new ObjenesisCglibAopProxy(config);
   }
   else {
      return new JdkDynamicAopProxy(config);
   }
}
```

从 if 中的判断条件可以看到 3 个方面影响着 Spring 的判断。

+ optimize：用来控制通过 CGLIB 创建的代理是否使用激进的优化策略，除非完全了解 AOP 代理如何处理优化，否则不推荐用户使用这个设置。目前这个属性仅用于 CGLIB 代理，对于 JDK 动态代理（缺省代理）无效。
+ proxyTargetClass：这个属性为 true 时，目标类本身被代理而不是目标类的接口。如果这个属性值被设为 true，CGLIB 代理将被创建，设置方式：`<aop:aspectj-autoproxy proxy-target-class="true">`
+ hasNoUserSuppliedProxyInterfaces：是否存在代理接口。

总结：

+ 如果目标对象实现了接口，默认情况下会采用 JDK 的动态代理实现 AOP。
+ 如果目标对象实现了接口，可以强制使用 CGLIB 实现 AOP。
+ 如果目标对象没有实现了接口，必须采用 CGLIB 库，Spring 会自动在 JDK 动态代理和 CGLIB 之间转换。

如何强制使用 CGLIB 实现 AOP？

+ 添加 CGLIB 库，SPRING_HOME/cglib/*.jar。
+ 在 Spring 配置文件中加入`<aop:aspectj-autoproxy proxy-target-class="true">`

JDK 动态代理和 CGLIB 字节码生成的区别。

+ JDK 动态代理只能对实现了接口的类生成大力，而不能针对类。
+ CGLIB 时针对类实现代理，主要是对指定的类生成一个子类，覆盖其中的方法，因为是集成，所以 final 方法不会被代理。

# 创建 AOP 静态代理

CGLIB 执行拦截器链，关于 CGLIB 及动态代理内容中文解释可以详细参考美团的一篇文章。当然也可以翻官网。



**JdkDynamicAopProxy#getProxy**

```java
@Override
public Object getProxy() {
   return getProxy(ClassUtils.getDefaultClassLoader());
}

@Override
public Object getProxy(@Nullable ClassLoader classLoader) {
   if (logger.isTraceEnabled()) {
      logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
   }
   Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised, true);
   findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
   return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
}
```

最下面就是调用 Proxy 来创建 JDK 动态代理，把当前类传入进去，当前类也就是 JdkDynamicAopProxy 实现了 InvocationHandler 接口，实现了它的接口方法 invoke，如下：

```java
/**
 * Implementation of {@code InvocationHandler.invoke}.
 * <p>Callers will see exactly the exception thrown by the target,
 * unless a hook method throws an exception.
 */
@Override
@Nullable
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
   Object oldProxy = null;
   boolean setProxyContext = false;

   TargetSource targetSource = this.advised.targetSource;
   Object target = null;

   try {
      if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
         // The target does not implement the equals(Object) method itself.
         return equals(args[0]);
      }
      else if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
         // The target does not implement the hashCode() method itself.
         return hashCode();
      }
      else if (method.getDeclaringClass() == DecoratingProxy.class) {
         // There is only getDecoratedClass() declared -> dispatch to proxy config.
         return AopProxyUtils.ultimateTargetClass(this.advised);
      }
       // Class 类的 isAssignableFrom 如果调用这个方法的 class 或接口与参数 cls 表示的类或接口相同，或者是参数
       // cls 表示的是的类或接口的父类，则返回 true
       // Object.class.isAssignableFrom(ArrayList.class);  true
       // ArrayList.class.isAssignableFrom(Object.class);  false
      else if (!this.advised.opaque && method.getDeclaringClass().isInterface() &&
            method.getDeclaringClass().isAssignableFrom(Advised.class)) {
         // Service invocations on ProxyConfig with the proxy config...
         return AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
      }

      Object retVal;

      if (this.advised.exposeProxy) {
         // Make invocation available if necessary.
         oldProxy = AopContext.setCurrentProxy(proxy);
         setProxyContext = true;
      }

      // Get as late as possible to minimize the time we "own" the target,
      // in case it comes from a pool.
      target = targetSource.getTarget();
      Class<?> targetClass = (target != null ? target.getClass() : null);

      // Get the interception chain for this method.  !!!
      List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

      // Check whether we have any advice. If we don't, we can fallback on direct
      // reflective invocation of the target, and avoid creating a MethodInvocation.
       // 如果没有发现任何拦截器那么直接调用切点方法
      if (chain.isEmpty()) {
         // We can skip creating a MethodInvocation: just invoke the target directly
         // Note that the final invoker must be an InvokerInterceptor so we know it does
         // nothing but a reflective operation on the target, and no hot swapping or fancy proxying.
         Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
         retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
      }
      else {
          // 不为空，则创建 invocation
         // We need to create a method invocation...
         MethodInvocation invocation =
               new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
         // Proceed to the joinpoint through the interceptor chain.
          // 执行拦截器链
          // 锚点
         retVal = invocation.proceed();
      }

      // Massage return value if necessary.
      Class<?> returnType = method.getReturnType();
      if (retVal != null && retVal == target &&
            returnType != Object.class && returnType.isInstance(proxy) &&
            !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
         // Special case: it returned "this" and the return type of the method
         // is type-compatible. Note that we can't help if the target sets
         // a reference to itself in another returned object.
         retVal = proxy;
      }
      else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
         throw new AopInvocationException(
               "Null return value from advice does not match primitive return type for: " + method);
      }
      return retVal;
   }
   finally {
      if (target != null && !targetSource.isStatic()) {
         // Must have come from TargetSource.
         targetSource.releaseTarget(target);
      }
      if (setProxyContext) {
         // Restore old proxy.
         AopContext.setCurrentProxy(oldProxy);
      }
   }
}
```

**ReflectiveMethodInvocation#proceed**

```java
// 锚点
@Override
@Nullable
public Object proceed() throws Throwable {
   // We start with an index of -1 and increment early.
   if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
      return invokeJoinpoint();
   }
	// 获取下一个要执行的拦截器
   Object interceptorOrInterceptionAdvice =
         this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
    // 动态匹配
   if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
      // Evaluate dynamic method matcher here: static part will already have
      // been evaluated and found to match.
      InterceptorAndDynamicMethodMatcher dm =
            (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
      Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
      if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
         return dm.interceptor.invoke(this);
      }
      else {
         // Dynamic matching failed.
         // Skip this interceptor and invoke the next in the chain.
          // 不匹配则不执行拦截器
         return proceed();
      }
   }
   else {
      // It's an interceptor, so we just invoke it: The pointcut will have
      // been evaluated statically before this object was constructed.
       // 普通拦截器，直接调用拦截器，比如：
       // ExposeInvocationInterceptor
       // DelegatePerTargetObjectIntroductionInterceptor
       // MethodBeforeAdviceInterceptor
       // AspectJAroundAdvice
       // AspectJAfterAdivice
      return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
   }
}
```

1. ReflectiveMethodInvocation 中的主要职责是维护了链接调用的计数器，记录着当前调用链接的位置，以便链可以有序地进行下去，各个工作委托给了各个增强器，使各个增强器在内部进行逻辑实现。
2. CGLIB 使用示例：

```java
public class CglibDemo {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CglibDemo.class);
        enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
            System.err.println("Before invoke " + method);
            Object result = methodProxy.invokeSuper(o, objects);
            System.err.println("After invoke" + method);
            return result;
        });

        CglibDemo cglibDemo = (CglibDemo) enhancer.create();
        cglibDemo.test();
        //System.out.println(cglibDemo.toString());
    }

    public void test() {
        System.out.println("I'm test method");
    }
    
}
```

**CglibAopProxy#getProxy**

```java
@Override
public Object getProxy() {
   return getProxy(null);
}

@Override
public Object getProxy(@Nullable ClassLoader classLoader) {
   if (logger.isTraceEnabled()) {
      logger.trace("Creating CGLIB proxy: " + this.advised.getTargetSource());
   }

   try {
      Class<?> rootClass = this.advised.getTargetClass();
      Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");

      Class<?> proxySuperClass = rootClass;
      if (rootClass.getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
         proxySuperClass = rootClass.getSuperclass();
         Class<?>[] additionalInterfaces = rootClass.getInterfaces();
         for (Class<?> additionalInterface : additionalInterfaces) {
            this.advised.addInterface(additionalInterface);
         }
      }

      // Validate the class, writing log messages as necessary.
      validateClassIfNecessary(proxySuperClass, classLoader);

      // Configure CGLIB Enhancer...
      Enhancer enhancer = createEnhancer();
      if (classLoader != null) {
         enhancer.setClassLoader(classLoader);
         if (classLoader instanceof SmartClassLoader &&
               ((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
            enhancer.setUseCache(false);
         }
      }
      enhancer.setSuperclass(proxySuperClass);
      enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
      enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
      enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(classLoader));
		// 设置拦截器
      Callback[] callbacks = getCallbacks(rootClass);
      Class<?>[] types = new Class<?>[callbacks.length];
      for (int x = 0; x < types.length; x++) {
         types[x] = callbacks[x].getClass();
      }
      // fixedInterceptorMap only populated at this point, after getCallbacks call above
      enhancer.setCallbackFilter(new ProxyCallbackFilter(
            this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
      enhancer.setCallbackTypes(types);

      // Generate the proxy class and create a proxy instance.
      return createProxyClassAndInstance(enhancer, callbacks);
   }
   catch (CodeGenerationException | IllegalArgumentException ex) {
      throw new AopConfigException("Could not generate CGLIB subclass of " + this.advised.getTargetClass() +
            ": Common causes of this problem include using a final class or a non-visible class",
            ex);
   }
   catch (Throwable ex) {
      // TargetSource.getTarget() failed
      throw new AopConfigException("Unexpected AOP exception", ex);
   }
}
```

**CglibAopProxy#getCallbacks**

```java
private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
   // Parameters used for optimization choices...
   boolean exposeProxy = this.advised.isExposeProxy();
   boolean isFrozen = this.advised.isFrozen();
   boolean isStatic = this.advised.getTargetSource().isStatic();
    // 锚点
	// 将拦截器封装在 DynamicAdvisedInterceptor
   // Choose an "aop" interceptor (used for AOP calls).
   Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);

   // Choose a "straight to target" interceptor. (used for calls that are
   // unadvised but can return this). May be required to expose the proxy.
   Callback targetInterceptor;
   if (exposeProxy) {
      targetInterceptor = (isStatic ?
            new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) :
            new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource()));
   }
   else {
      targetInterceptor = (isStatic ?
            new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) :
            new DynamicUnadvisedInterceptor(this.advised.getTargetSource()));
   }

   // Choose a "direct to target" dispatcher (used for
   // unadvised calls to static targets that cannot return this).
   Callback targetDispatcher = (isStatic ?
         new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp());
	// 将拦截器链加入 Callback 中
   Callback[] mainCallbacks = new Callback[] {
         aopInterceptor,  // for normal advice
         targetInterceptor,  // invoke target without considering advice, if optimized
         new SerializableNoOp(),  // no override for methods mapped to this
         targetDispatcher, this.advisedDispatcher,
         new EqualsInterceptor(this.advised),
         new HashCodeInterceptor(this.advised)
   };

   Callback[] callbacks;

   // If the target is a static one and the advice chain is frozen,
   // then we can make some optimizations by sending the AOP calls
   // direct to the target using the fixed chain for that method.
   if (isStatic && isFrozen) {
      Method[] methods = rootClass.getMethods();
      Callback[] fixedCallbacks = new Callback[methods.length];
      this.fixedInterceptorMap = new HashMap<>(methods.length);

      // TODO: small memory optimization here (can skip creation for methods with no advice)
      for (int x = 0; x < methods.length; x++) {
         Method method = methods[x];
         List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, rootClass);
         fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(
               chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
         this.fixedInterceptorMap.put(method, x);
      }

      // Now copy both the callbacks from mainCallbacks
      // and fixedCallbacks into the callbacks array.
      callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
      System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
      System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
      this.fixedInterceptorOffset = mainCallbacks.length;
   }
   else {
      callbacks = mainCallbacks;
   }
   return callbacks;
}
```

**DynamicAdvisedInterceptor**

```java
/**
 * General purpose AOP callback. Used when the target is dynamic or when the
 * proxy is not frozen.
 */
private static class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable {

   private final AdvisedSupport advised;

   public DynamicAdvisedInterceptor(AdvisedSupport advised) {
      this.advised = advised;
   }

   @Override
   @Nullable
   public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
      Object oldProxy = null;
      boolean setProxyContext = false;
      Object target = null;
      TargetSource targetSource = this.advised.getTargetSource();
      try {
         if (this.advised.exposeProxy) {
            // Make invocation available if necessary.
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
         }
         // Get as late as possible to minimize the time we "own" the target, in case it comes from a pool...
         target = targetSource.getTarget();
         Class<?> targetClass = (target != null ? target.getClass() : null);
         List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
         Object retVal;
         // Check whether we only have one InvokerInterceptor: that is,
         // no real advice, but just reflective invocation of the target.
         if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
            // We can skip creating a MethodInvocation: just invoke the target directly.
            // Note that the final invoker must be an InvokerInterceptor, so we know
            // it does nothing but a reflective operation on the target, and no hot
            // swapping or fancy proxying.
             // 如果拦截器链为空则直接激活原方法
            Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
            retVal = methodProxy.invoke(target, argsToUse);
         }
         else {
             // 进入拦截器链
            // We need to create a method invocation...
            retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
         }
         retVal = processReturnType(proxy, target, method, retVal);
         return retVal;
      }
      finally {
         if (target != null && !targetSource.isStatic()) {
            targetSource.releaseTarget(target);
         }
         if (setProxyContext) {
            // Restore old proxy.
            AopContext.setCurrentProxy(oldProxy);
         }
      }
   }
    // 省略 hashcode 和 equals 方法
}
```

# 静态 AOP 使用示例

加载时织入（Load-Time Weaving，LTW）

-javaagent：

破解的时候经常要加这个

LoadTimeWeaverAwareProcessor 是 BeanPostProcessor 接口的实现类。

