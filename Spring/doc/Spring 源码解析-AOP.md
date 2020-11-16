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

## 注册 AnnotationAwareAspectAutoProxyCreator

**AnnotationAwareAspectAutoProxyCreator**

![image.png](https://i.loli.net/2020/11/16/Rexwrh9a6dTLSQY.png)

SmartInstantiationAwareBeanPostProcessor 老接口了，在 Spring 声明周期，BeanDefinition 实例化前会执行这个方法，如果返回不为空，则跳过后面的步骤，直接返回 Bean 实例。

