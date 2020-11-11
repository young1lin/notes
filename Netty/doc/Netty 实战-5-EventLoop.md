# 线程模型概述

我知道，ThreadPoolExecutor 也会用

# EventLoop 接口

运行任务来处理在连接的生命周期内发生的事件是任何网络框架的基本功能——在 Netty 中叫事件循环 EventLoop

Netty 的 EventLoop 是协同设计的一部分，它采用了两个基本的 API：并发和网络编程。

下面是类层次结构图

![image.png](https://i.loli.net/2020/10/26/ToMYKXFHf4SlZEj.png)

在这个模型中，一个 EventLoop 将由一个永远都会改变的 Thread 驱动，同时任务可以直接提交给 EventLoop 实现，以立即执行或者调度执行。根据配置和可用核心的不同，可能会创建多个 EventLoop 实例用以优化资源的使用，并且单个 EventLoop 可能会被派用于服务多个 Channel。**怎么这么像 Epoll，还有 Redis6 以前的线程模型？**

事件/任务的执行顺序 FIFO。

## Netty 4 中的 I/O 和事件处理

由 I/O 操作触发的事件将流经安装了一个或者多个 ChannelHandler 的 ChannelPipeline。传播这些事件的方法调用可以随后被 ChannelHandler 所拦截，并且可以按需地处理事件。

事件的性质通常决定了它将被如何处理；它可能将数据从网络栈中传递到你的应用程序中，或者进行逆向操作，或者执行一些截然不同的操作。但是事件的处理逻辑鼻血足够的通过和灵活，以处理所有可能的用例。在 Netty4  中，所有的 I/O 操作和事件都由已经被分配给了 EventLoop 的那个 Thread 来处理。

# 任务调度

## JDK 的任务调度 API

在 Java 5 之前，任务调度是建立在 java.util.Timer 类之上的，其使用了一个后台 Thread，并且具有标准线程相同的限制。后面有了 interface ScheduledExecutorService 。

**java.util.concurrent.Executors 相关的静态工厂方法**

| Method Name                                                  | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| newScheduledThreadPool(int corePoolSize)<br/>newScheduledThreaPool(<br/>int  corePoolSize,<br/>ThreadFactory threadFactory) | 创建一个 ScheduledThreadExecutorService，用于调度命令<br/>在指令延迟之后运行或者周期性地执行。它使用 corePoolSize 参数来计算线程数。 |
| newSingleThreadScheduledExecutor()<br/>newSingleThreadScheduledExecutor(<br/>ThreadFactory threadFactory) | 单个线程的                                                   |

**使用 ScheduledExecutorService 调度任务**

```java
ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
    @Override
    public Thread newThread(Runnable r) {
        return new Thread("777");
    }
});
executor.schedule(() -> {
    System.out.println("60s 过去了");
}, 60, TimeUnit.SECONDS);
// 其他逻辑处理
executor.shutdown();
```

但是在高负载下它将带来性能上的负担。

## 使用 EventLoop 调度任务

**使用 EventLoop 调度任务**

```java
Channel ch = ...;
ScheduledFuture<?> future = ch.eventLoop().schedule(()->{
    System.out.println("60s 过去了")；
},60,TimeUnit.SECONDS);
```

经过 60 秒之后，Runnable 实例将由分配给 Channel 的 EventLoop 执行。如果要调度任务以每隔 60 秒执行一次，请使用 scheduleAtFixedRate 方法。

**使用 EventLoop 调度周期性的任务**

```java
Channel ch = ...;
// 多了个参数罢了
ScheduledFuture<?> future = ch.eventLoop().scheduleAtFixedRate(()->{
    System.out.println("60s 过去了")；
},60,60,TimeUnit.SECONDS);

// 下面是取消该任务，防止它再次运行
future.cancel(fasle);
```

# 实现细节

## 线程管理

分配给 EventLoop 的“主线程”来确定它的卓越性能。

![EventLoop 的执行逻辑.png](https://i.loli.net/2020/10/26/qGOiSYwLXRz3AUd.png)

## EventLoop 线程的分配

服务于 Channel 的 I/O 事件的 EventLoop 包含在 EventLoopGroup 中。根据不同的额传输实现，EventLoop 的创建和分配方式也不同。

1. **异步传输**

异步传输实现只使用了少量的 EventLoop（以及和它们相关联的 Thread），而且在当前的线程模型中，它们可能会被多个 Channel 所共享。

![用于非阻塞传输的 EventLoop 分配方式.png](https://i.loli.net/2020/10/26/TMzRphOuDAVJanN.png)

EventLoopGroup 负责为每个新创建的 Channel 分配一个 EventLoop。在当前实现中，使用顺讯循环（round-robin）的方式进行分配以获取一个均衡的分布，并且相同的 EventLoop 可能会被分配多个 Channel。

一旦一个 Channel 被分配给一个 EventLoop，它将在它的整个生命周期中都使用这个 EventLoop（以及相关联的 Thread）。不用担心线程安全问题。

因为一个 EventLoop 通常会被用于支撑多个 Channel，所以对于所有相关联的 Channel 来说，ThreadLocal 都将是一样的。在一些无状态的上下文中，塔可以被用于在多个 Channel 之间共享一些重度的活着代价昂贵的对象，甚至是事件。

2. **阻塞传输**

一个 Channel 对应一个 EventLoop
