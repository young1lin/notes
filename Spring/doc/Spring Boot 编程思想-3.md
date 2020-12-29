# 摘要

第十九天。

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



## 10. @EventListener 方法实现原理

