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



## 注册 AnnotationAwareAspectAutoProxyCreator

**AnnotationAwareAspectAutoProxyCreator**

![image.png](https://i.loli.net/2020/11/16/Rexwrh9a6dTLSQY.png)

SmartInstantiationAwareBeanPostProcessor 老接口了，在 Spring 声明周期，BeanDefinition 实例化前会执行这个方法，如果返回不为空，则跳过后面的步骤，直接返回 Bean 实例。

