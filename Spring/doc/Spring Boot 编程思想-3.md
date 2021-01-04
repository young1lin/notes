# 摘要

第十九天。483 Spring 相关事件。

第二十天。 497 Spring 事件/监听机制总结及其原理，Spring Boot 事件简单介绍。

第二十一天。520 Spring 以及 Spring Boot 事件在不同版本的实现，以及 Spring 应用上下文的讲解。

第二十二天。

# 第十九天

Spring 默认采用 SimpleApplicationEventMulticaster，该 Eventulticaster 默认采用同步广播事件的方式。

方法是 SimpleApplicationEventMulticaster#multicastEvent 方法广播，根据 ApplicationEvent 具体类型查找匹配的 ApplicationListener 列表，然后逐一同步或异步地调用 ApplicationListener#onApplicationEvent(Application Event) 方法，实现 ApplicationListener 事件监听。

## 4. 注解驱动 Spring 事件监听——@EventListener

@EventListener 必须标记在 Spring 托管 Bean 的 public 方法。

```java
package me.young1lin.spring.boot.thinking.listener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/29 上午7:47
 * @version 1.0
 */
public class AnnotationEventListenerBootstrap {

   public static void main(String[] args) {
      // 创建注解驱动 Spring 应用上下文
      AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
      context.register(MyListener.class);
      context.refresh();
      context.close();
   }

   public static abstract class AbstractEventListener {

      @EventListener(ContextRefreshedEvent.class)
      public void onContextRefreshedEvent(ContextRefreshedEvent event) {
         System.out.println("AbstractEventListener :" + event.getClass().getSimpleName());
      }

   }

   public static class MyListener extends AbstractEventListener {

      @EventListener(ContextClosedEvent.class)
      public boolean onContextClosedEvent(ContextClosedEvent event) {
         System.out.println("MyListener : " + event.getClass().getSimpleName());
         return true;
      }

   }

}
```

输出

```tex
.....省略
AbstractEventListener :ContextRefreshedEvent
.....省略
MyListener : ContextClosedEvent
```

不管是子类父类有无返回值，只要加了@EventListener 都是可以监听的。

## 5. @EventListener 方法监听多 ApplicationEvent

```java
package me.young1lin.spring.boot.thinking.listener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/29 上午7:58
 * @version 1.0
 */
public class AnnotatedEventListenerOnMultiEventBootstrap {

   public static void main(String[] args) {
      AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
      context.register(MyMultiEventListener.class);
      context.refresh();
      context.close();
   }

   public static class MyMultiEventListener {

      @EventListener({ContextRefreshedEvent.class, ContextClosedEvent.class})
      public void onEvent() {
         System.out.println("onEvent=========start");
      }

      @EventListener({ContextRefreshedEvent.class, ContextClosedEvent.class})
      public void onApplicationContextEvent(ApplicationContextEvent event) {
         System.out.println("onApplicationContextEvent : "
               + event.getClass().getSimpleName());
      }

      // 最多支持一个 Event 参数，并且这个 event 参数应是 ApplicationEvent,或者不加这个参数
//    @EventListener({ContextRefreshedEvent.class, ContextClosedEvent.class})
//    public void onEvents(ContextRefreshedEvent refreshedEvent, ContextClosedEvent closedEvent) {
//       System.out.println("onEvents : " + refreshedEvent.getClass().getSimpleName()
//             + "," + closedEvent.getClass().getSimpleName());
//    }

   }

}
```

## 6. @EventListener 异步方法

```java
package me.young1lin.spring.boot.thinking.listener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/29 上午8:08
 * @version 1.0
 */
@Configuration
public class AnnotatedAsyncEventListenerBootstrap {

   public static void main(String[] args) {
      AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
      context.register(AnnotatedAsyncEventListenerBootstrap.class);
      context.register(MyAsyncEventListener.class);
      context.refresh();
      context.close();
   }

   @Bean("TaskExecutor")
   public Executor taskExecutor() {
      return new ThreadPoolTaskExecutor();
   }

   @EnableAsync
   public static class MyAsyncEventListener {
		
      // 为了保证异步和同步时一致，需要返回为 void
      @EventListener(ContextRefreshedEvent.class)
      @Async
      public CompletableFuture<Boolean> onContextRefreshedEvent(ContextRefreshedEvent event) {
         System.out.println("current Thread is :"
               + Thread.currentThread().getName() + event.getClass().getSimpleName());
         return CompletableFuture.completedFuture(true);
      }
   }

}
```

## 7. @EventListener 方法执行顺序

@Order 标记，顺序

## 8. @EventListener 方法监听泛型 ApplicationEvent

ResolvableTypeProvider

`PayloadApplicationEvent\<T\> extends ApplicationEvent implements ResolvableTypeProvider`

无论注解驱动还是接口编程，都能监控泛型 ApplicationEvent。

## 9. @EventListener 方法对比 ApplicationListener 接口



# 第二十天

## 10. @EventListener 方法实现原理

必须在 Spring Bean 中，必须是 public 方法。

三个接口。

ApplicationEvent

ApplicationListener

ApplicationEventMulticaster

**AnnotationConfigUtils**

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
      BeanDefinitionRegistry registry, @Nullable Object source) {
	// ........ 省略一大段代码
   if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
       // 4.2 是要注册个 EventListenerMethodProcessor，在 5.0.6 该成了下面的实现
      RootBeanDefinition def = new RootBeanDefinition(DefaultEventListenerFactory.class);
      def.setSource(source);
      beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_FACTORY_BEAN_NAME));
   }

   return beanDefs;
}
```

## 11. SmartInitializingSingleton 生命周期回调

不是 SmartInitializingBeanPostProcessor

所有单体 Bean 预实例化阶段之后触发。

**DefaultListableBeanFactory#preInstantiateSingletons**

```java
@Override
public void preInstantiateSingletons() throws BeansException {
   if (logger.isTraceEnabled()) {
      logger.trace("Pre-instantiating singletons in " + this);
   }

   // Iterate over a copy to allow for init methods which in turn register new bean definitions.
   // While this may not be part of the regular factory bootstrap, it does otherwise work fine.
   List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

   // Trigger initialization of all non-lazy singleton beans...
   for (String beanName : beanNames) {
      RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
      if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
         if (isFactoryBean(beanName)) {
            Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
            if (bean instanceof FactoryBean) {
               FactoryBean<?> factory = (FactoryBean<?>) bean;
               boolean isEagerInit;
               if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                  isEagerInit = AccessController.doPrivileged(
                        (PrivilegedAction<Boolean>) ((SmartFactoryBean<?>) factory)::isEagerInit,
                        getAccessControlContext());
               }
               else {
                  isEagerInit = (factory instanceof SmartFactoryBean &&
                        ((SmartFactoryBean<?>) factory).isEagerInit());
               }
               if (isEagerInit) {
                  getBean(beanName);
               }
            }
         }
         else {
            getBean(beanName);
         }
      }
   }

   // Trigger post-initialization callback for all applicable beans...
   for (String beanName : beanNames) {
      Object singletonInstance = getSingleton(beanName);
       // 重点在这里============================
      if (singletonInstance instanceof SmartInitializingSingleton) {
         SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
         if (System.getSecurityManager() != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
               smartSingleton.afterSingletonsInstantiated();
               return null;
            }, getAccessControlContext());
         }
         else {
            smartSingleton.afterSingletonsInstantiated();
         }
      }
   }
}
```

**AbstractBeanFactory#refresh -> finishBeanFactoryInitialization(beanFactory).**

## 12. EventListenerMethodProcessor 的实现原理

**EventListenerMethodProcessor**

```java
public class EventListenerMethodProcessor
      implements SmartInitializingSingleton, ApplicationContextAware, BeanFactoryPostProcessor {

   protected final Log logger = LogFactory.getLog(getClass());

   @Nullable
   private ConfigurableApplicationContext applicationContext;

   @Nullable
   private ConfigurableListableBeanFactory beanFactory;

   @Nullable
   private List<EventListenerFactory> eventListenerFactories;

   private final EventExpressionEvaluator evaluator = new EventExpressionEvaluator();

   private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));


   @Override
   public void setApplicationContext(ApplicationContext applicationContext) {
      Assert.isTrue(applicationContext instanceof ConfigurableApplicationContext,
            "ApplicationContext does not implement ConfigurableApplicationContext");
      this.applicationContext = (ConfigurableApplicationContext) applicationContext;
   }

   @Override
   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
      this.beanFactory = beanFactory;

      Map<String, EventListenerFactory> beans = beanFactory.getBeansOfType(EventListenerFactory.class, false, false);
      List<EventListenerFactory> factories = new ArrayList<>(beans.values());
      AnnotationAwareOrderComparator.sort(factories);
      this.eventListenerFactories = factories;
   }

	// 这个是重点=============
   @Override
   public void afterSingletonsInstantiated() {
      ConfigurableListableBeanFactory beanFactory = this.beanFactory;
      Assert.state(this.beanFactory != null, "No ConfigurableListableBeanFactory set");
      String[] beanNames = beanFactory.getBeanNamesForType(Object.class);
      for (String beanName : beanNames) {
         if (!ScopedProxyUtils.isScopedTarget(beanName)) {
            Class<?> type = null;
            try {
               type = AutoProxyUtils.determineTargetClass(beanFactory, beanName);
            }
            catch (Throwable ex) {
               // An unresolvable bean type, probably from a lazy bean - let's ignore it.
               if (logger.isDebugEnabled()) {
                  logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
               }
            }
            if (type != null) {
               if (ScopedObject.class.isAssignableFrom(type)) {
                  try {
                     Class<?> targetClass = AutoProxyUtils.determineTargetClass(
                           beanFactory, ScopedProxyUtils.getTargetBeanName(beanName));
                     if (targetClass != null) {
                        type = targetClass;
                     }
                  }
                  catch (Throwable ex) {
                     // An invalid scoped proxy arrangement - let's ignore it.
                     if (logger.isDebugEnabled()) {
                        logger.debug("Could not resolve target bean for scoped proxy '" + beanName + "'", ex);
                     }
                  }
               }
               try {
                  processBean(beanName, type);
               }
               catch (Throwable ex) {
                  throw new BeanInitializationException("Failed to process @EventListener " +
                        "annotation on bean with name '" + beanName + "'", ex);
               }
            }
         }
      }
   }

   private void processBean(final String beanName, final Class<?> targetType) {
      if (!this.nonAnnotatedClasses.contains(targetType) &&
            AnnotationUtils.isCandidateClass(targetType, EventListener.class) &&
            !isSpringContainerClass(targetType)) {

         Map<Method, EventListener> annotatedMethods = null;
         try {
            annotatedMethods = MethodIntrospector.selectMethods(targetType,
                  (MethodIntrospector.MetadataLookup<EventListener>) method ->
                        AnnotatedElementUtils.findMergedAnnotation(method, EventListener.class));
         }
         catch (Throwable ex) {
            // An unresolvable type in a method signature, probably from a lazy bean - let's ignore it.
            if (logger.isDebugEnabled()) {
               logger.debug("Could not resolve methods for bean with name '" + beanName + "'", ex);
            }
         }

         if (CollectionUtils.isEmpty(annotatedMethods)) {
            this.nonAnnotatedClasses.add(targetType);
            if (logger.isTraceEnabled()) {
               logger.trace("No @EventListener annotations found on bean class: " + targetType.getName());
            }
         }
         else {
            // Non-empty set of methods
            ConfigurableApplicationContext context = this.applicationContext;
            Assert.state(context != null, "No ApplicationContext set");
            List<EventListenerFactory> factories = this.eventListenerFactories;
            Assert.state(factories != null, "EventListenerFactory List not initialized");
            for (Method method : annotatedMethods.keySet()) {
               for (EventListenerFactory factory : factories) {
                  if (factory.supportsMethod(method)) {
                     Method methodToUse = AopUtils.selectInvocableMethod(method, context.getType(beanName));
                     ApplicationListener<?> applicationListener =
                           factory.createApplicationListener(beanName, targetType, methodToUse);
                     if (applicationListener instanceof ApplicationListenerMethodAdapter) {
                        ((ApplicationListenerMethodAdapter) applicationListener).init(context, this.evaluator);
                     }
                     context.addApplicationListener(applicationListener);
                     break;
                  }
               }
            }
            if (logger.isDebugEnabled()) {
               logger.debug(annotatedMethods.size() + " @EventListener methods processed on bean '" +
                     beanName + "': " + annotatedMethods);
            }
         }
      }
   }

	// 省略。。。。
}
```

## 13. 总结 Spring 事件/监听机制

### 1. 总结 Spring 事件

ApplicationEvent 继承于 Java 规约 java.util.EventObject

### 2. 总结 Spring 事件监听手段

1. 面向接口编程 ApplicationListener
2. @EventListener

### 3. 总结 Spring 事件广播器

1. ApplicationEventPublisher#publishEvent
2. ApplicationEventMulticaster#multicastEvent

默认情况下只提供第一种实现

# 第二十一天

## 理解 Spring Boot 事件/监听机制

Listener 类的 hashCode 和 equals 方法不应该重写。

Spring Boot 部分事件也能被 ApplicationListener 监听。之所以是部分，因为在某些 Spring Boot 事件初始化完成后，Spring 上下文才开始初始化。

EventPublishingRunListener 与 Spring Boot 事件的关系，以及与 AbstractApplicationContext 调用的前后关系如下表所示。

|      监听方法       | refresh 方法执行顺序 |          Spring Boot 事件           | Spring Boot 起始版本 |
| :-----------------: | :------------------: | :---------------------------------: | :------------------: |
|       started       |        调用前        |       ApplicationStartedEvent       |         1.0          |
| environmentPrepared |        调用前        | ApplicationEnvironmentPreparedEvent |         1.0          |
|    contextLoaded    |        调用前        |      ApplicationPreparedEvent       |         1.0          |
|       running       |        调用后        |        ApplicationReadyEvent        |         1.3          |
|       failed        |        调用后        |       ApplicationFailedEvent        |         1.0          |

1. SmartApplicationListener 监听多 Spring Boot 事件

Spring Boot 2.0 监听不到关闭事件（按照书上操作来）

2. 当前的 Spring Boot 事件/监听机制

各自为政，互不干扰。

书上的和我实际跑出来的，不一样。

```java
public class SpringEventListenerBootstrap {

   public static void main(String[] args) {
      new SpringApplicationBuilder(Object.class)
            .listeners(event -> {
               System.out.println(event.getClass().getSimpleName());
            })
            .web(WebApplicationType.NONE)
            .run(args)
            .close();
   }

}
```

3. Spring Boot 内建事件监听器

spring-boot 下的 META/INF/spring.factories 文件里的内建的事件

```properties
# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.ClearCachesApplicationListener,\
org.springframework.boot.builder.ParentContextCloserApplicationListener,\
org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
org.springframework.boot.context.FileEncodingApplicationListener,\
org.springframework.boot.context.config.AnsiOutputApplicationListener,\
org.springframework.boot.context.config.ConfigFileApplicationListener,\
org.springframework.boot.context.config.DelegatingApplicationListener,\
org.springframework.boot.context.logging.ClasspathLoggingApplicationListener,\
org.springframework.boot.context.logging.LoggingApplicationListener,\
org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
```

## 装配 ApplicationArguments

## 准备 ConfigurableEnvironment

理解 Spring Boot Environment 生命周期展开讲

## 创建 Spring 应用上下文

一般是 AnnotationConfigServletWebServerApplicationContext

1. 根据 WebApplicationType 创建 Spring 应用上下文。

## Spring 应用上下文运行前准备

**SpringApplication#prepareContext**

```java
private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
      SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
   // 1. Environment 抽象的具体实现说明；
   context.setEnvironment(environment);
   // 2.  Spring 应用上下文后置处理；
   postProcessApplicationContext(context);
   // 3. 运用 Spring 应用上下文初始化器；
   applyInitializers(context);
   // 4. 执行 SpringApplicationRunListener#contextPrepared 方法回调。 
   listeners.contextPrepared(context);
   // 打印 active 信息而已
   if (this.logStartupInfo) {
      logStartupInfo(context.getParent() == null);
      logStartupProfileInfo(context);
   }
   // Add boot specific singleton beans，这里如果没什么特殊情况，就是 GenericApplicationContext 的默认的构造方法
   // 构建的 DefaultListableBeanFactory
   ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
   // #2. Spring 应用上下文装载阶段
   beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
   if (printedBanner != null) {
      // 注册 banner 对象。
      beanFactory.registerSingleton("springBootBanner", printedBanner);
   }
   if (beanFactory instanceof DefaultListableBeanFactory) {
      ((DefaultListableBeanFactory) beanFactory)
            .setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
   }
   if (this.lazyInitialization) {
      context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
   }
   // Load the sources
   Set<Object> sources = getAllSources();
   Assert.notEmpty(sources, "Sources must not be empty");
   load(context, sources.toArray(new Object[0]));
   listeners.contextLoaded(context);
}
```

1. Environment 抽象的具体实现说明；
2. Spring 应用上下文后置处理；
3. 运用 Spring 应用上下文初始化器；
4. 执行 SpringApplicationRunListener#contextPrepared 方法回调。

# 第二十二天

Spring 应用上下文的后置处理器 ConfigurationClassPostProcessor 这个影响注解驱动 Bean 名的生成。AnnotationConfigServletWebApplicationContext 和 GenericApplicationContext 关系如下。

![image.png](https://i.loli.net/2021/01/03/gTetZ1xkJ5NHIru.png)

**运用 Spring 应用上下文初始化器（ApplicationContextInitializer）**

排序，去重。需要覆盖 hashcode 以及 equals 方法，如果没有，则会出现多次调用。

**执行 SpringApplicationRunListener#contextPrepared 方法回调。**

这个会在 Spring 应用上下文创建并准备完毕时，该方法会被回调，不过该方法在 AppplicationContext 加载“源”之前执行。源指的是配置源。

## Spring 应用上下文装载阶段

1. 注册 Spring Boot Bean；

2. 合并 Spring 应用上下文配置源；

3. 加载 Spring 应用上下文配置源；

4. 执行 SpringApplicationRunListener#contextLoaded 方法回调。

   **SpringApplication#prepareContext** 		 				重点

```java   // #2. Spring 应用上下文装载阶段
private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
      SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
   // 省略前面的代码。
   // 1. 注册 Spring Boot Bean；
   beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
   if (printedBanner != null) {
      // 注册 banner 对象。
      beanFactory.registerSingleton("springBootBanner", printedBanner);
   }
   if (beanFactory instanceof DefaultListableBeanFactory) {
      ((DefaultListableBeanFactory) beanFactory)
            .setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
   }
   if (this.lazyInitialization) {
      context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
   }
   // Load the sources
    // 2. 合并 Spring 应用上下文配置源；
   Set<Object> sources = getAllSources();
   Assert.notEmpty(sources, "Sources must not be empty");
   // 3. 加载 Spring 应用上下文配置源；
   // 这里的关键是 BeanDefinitionLoader 可以加载注解，XML，Groovy
   load(context, sources.toArray(new Object[0]));
   // 4. 执行 SpringApplicationRunListener#contextLoaded 方法回调。
   // 已知的 SpringApplicationRunListener 唯一的实现类 EventPublishingRunListener 
   // 在此阶段讲 SpringApplication 关联的 ApplicationListener 追加到 Spring 应用上下文，随后发布  
   // ApplicationPreparedEvent 事件。
   listeners.contextLoaded(context);
}
```

在此步骤，可以进行外部化配置扩展。

## Spring 应用上下文启动阶段

```java
refreshContext(context);
```

其实就是套路 AbstractApplicationContext#refresh 而已。然后多了个 registerShutdownHook 注册关闭的勾子线程，实现优雅的 Spring Bean 销毁生命周期回调。这个应该是属于 JVM 的 shutdown hook 机制，具体执行并不由 Spring 应用上下文控制。

## Spring 应用上下文启动后阶段

```java
afterRefresh(context, applicationArguments);

/**
 * Called after the context has been refreshed.
 * @param context the application context
 * @param args the application arguments
 */
protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {
}
```

交给你来自己实现

# 第二十三天

## afterRefresh 方法语义的变化

1.3 版本有调用 callRunners 方法，到1.5，2.0 又移除了。

```java
public ConfigurableApplicationContext run(String... args) {
    // 。。。。。。。。省略一段代码
    ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
    // 准备 Environment
    ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
    configureIgnoreBeanInfo(environment);
    // 打印 banner 信息
    Banner printedBanner = printBanner(environment);
    // 根据导入的包，来反射创建 Spring 应用上下文
    context = createApplicationContext();
    // 从 spring.factories 文件中获得 SpringBootExceptionReporter
    exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
          new Class[] { ConfigurableApplicationContext.class }, context);
    // 准备上下文
    prepareContext(context, environment, listeners, applicationArguments, printedBanner);
    // 刷新上下文
    refreshContext(context);
    // 刷新之后
    afterRefresh(context, applicationArguments);
    stopWatch.stop();
    if (this.logStartupInfo) {
       new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
    }
    // SpringApplicationRunListener#started
    listeners.started(context);
    // callRunners 移到了这里，不再出现在 afterRefresh 方法里面，延迟了。
    callRunners(context, applicationArguments);
}
```

## Spring Boot 事件 ApplicationStartedEvent 语义的变化

ApplicationStartingEvent 替换了 ApplicationStartedEvent （1.5 版本起）。

当 Starting 事件被广播后，执行 callRunnenrs 方法。

## 执行 CommandLineRunner 和 ApplicationRunner

可以通过标注 @Order 注解的方式 来控制它们的执行顺序。

## SpringApplication 结束阶段

总的分为正常结束和异常结束。

## 正常结束

**SpringApplicationRunListener#running**

作为 SpringApplicationRunListener 唯一的实现 **EventPublishingRunListener** ，仅仅是简单的广播 ApplicationReadyEvent。

**EventPublishingRunListener#running**

```java
@Override
public void running(ConfigurableApplicationContext context) {
   context.publishEvent(new ApplicationReadyEvent(this.application, this.args, context));
   AvailabilityChangeEvent.publish(context, ReadinessState.ACCEPTING_TRAFFIC);
}
```

因为 SpringApplication 有 close 方法，所以在 finished 不仅调用了异常事件结束，还调用了所有的 Spring 应用的广播 **SpringApplicationEvent#finish** 事件。两者是 if else 关系。

## 异常结束

2.0 开始，调用 SpringApplicationEvent#failed 方法。

### 故障分析器——FailureAnalyzers

1.4 开始，是 FailureAnalyzer 的组合类。这有个坑，它只返回第一个错误，可能返回的错误并不是“精准”的。

**FailureAnalyzers#reportException**

```java
@Override
public boolean reportException(Throwable failure) {
   FailureAnalysis analysis = analyze(failure, this.analyzers);
   return report(analysis, this.classLoader);
}

private FailureAnalysis analyze(Throwable failure, List<FailureAnalyzer> analyzers) {
   for (FailureAnalyzer analyzer : analyzers) {
      try {
         FailureAnalysis analysis = analyzer.analyze(failure);
         if (analysis != null) {
            return analysis;
         }
      }
      catch (Throwable ex) {
         logger.debug(LogMessage.format("FailureAnalyzer %s failed", analyzer), ex);
      }
   }
   return null;
}
```

## 错误分析报告器 —— FailureAnalysisReporter

