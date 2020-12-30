# 摘要

第十九天。483 Spring 相关事件。

第二十天。 497 Spring 事件/监听机制总结及其原理，Spring Boot 事件简单介绍。

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

## 理解 Spring Boot 事件/监听机制

