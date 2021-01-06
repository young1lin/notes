# 线程饥饿死锁

在线程池中，如果任务依赖于其他任务，那么可能产生死锁。

我怎么记得是2N+1 呢，一半读，一半写。

N+1个线程数大小通常最优的利用率。

Nt = Nc * Uc * (1+W/C)

Nt 是 N 个线程数。N thread num。

以此类推。

# 配置 ThreadPoolExecutor

```java
/**
 * Creates a new {@code ThreadPoolExecutor} with the given initial
 * parameters.
 *
 * @param corePoolSize the number of threads to keep in the pool, even
 *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
 * @param maximumPoolSize the maximum number of threads to allow in the
 *        pool
 * @param keepAliveTime when the number of threads is greater than
 *        the core, this is the maximum time that excess idle threads
 *        will wait for new tasks before terminating.
 * @param unit the time unit for the {@code keepAliveTime} argument
 * @param workQueue the queue to use for holding tasks before they are
 *        executed.  This queue will hold only the {@code Runnable}
 *        tasks submitted by the {@code execute} method.
 * @param threadFactory the factory to use when the executor
 *        creates a new thread
 * @param handler the handler to use when execution is blocked
 *        because the thread bounds and queue capacities are reached
 * @throws IllegalArgumentException if one of the following holds:<br>
 *         {@code corePoolSize < 0}<br>
 *         {@code keepAliveTime < 0}<br>
 *         {@code maximumPoolSize <= 0}<br>
 *         {@code maximumPoolSize < corePoolSize}
 * @throws NullPointerException if {@code workQueue}
 *         or {@code threadFactory} or {@code handler} is null
 */
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.acc = System.getSecurityManager() == null ?
            null :
            AccessController.getContext();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```

+ corePoolSize —— 线程池的目标大小，即在没有任务执行时线程池的大小，并且只有在工作队列满了的情况下才会创建超过这个数量的线程。在 ThreadPoolExecutor 初期，线程并不会立即启动，而是等到有任务提交时才会启动，除非调用 prestartAllThreads。
+ maximumPoolSize —— 线程池最大值。 the maximum number of threads to allow in the pool。
+ keepAliveTime —— 当线程数超过核心线程数时，这是多余的空闲线程将在终止之前等待新任务的最长时间。  when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.

+ unit  —— 上面的时间单位the time unit for the {@code keepAliveTime} argument
+ workQueue —— 任务队列，这个是提交的任务队列，如果提交的任务大于最大线程数了，会优先放到这个任务队列中。 the queue to use for holding tasks before they are executed.  This queue will hold only the {@code Runnable} tasks submitted by the {@code execute} method.
+ threadFactory —— 利用 ThreadFactory#newThread 来创建 Thread，这里可以对 Thread 进行命名。the factory to use when the executor creates a new thread.
+ handler —— 执行不了了，wokerqueue 都塞不下了，就执行拒绝策略。可以自定义，用来重试任务，默认是直接丢掉。JDK 提供了 4 种。the handler to use when execution is blocked because the thread bounds and queue capacities are reached

ThreadPoolExecutor 允许提供一个 Blocking Queue 来保存等待执行的任务。

1. 无界队列。LinkedBlockingQueue 默认的构造器就是无界的。`public LinkedBlockingQueue() { this(Integer.MAX_VALUE);}`
   2. 有界队列。ArrayBlockingQueue、有界的 LinkedBlockingQueue、PriorityBlockingQueue。
3. 同步移交。Synchronous Handoff。

无界队列将不会执行拒绝策略，当然如果你的线程数大于了 Integer.MAX_VALUE 还是会执行的，前提是你到达这么多。一般情况就 OOM 了。所以《阿里巴巴 Java 开发规范》中不使用 Executors 创建线城池。

## 饱和策略

1. AbortPolicy：中止策略是默认的饱和策略，该策略将抛出未检查的 RejectedExecutionException 调用者可以捕获这个异常，然后根据需求编写自己的处理代码。

2. DiscardPolicy：当新提交的任务无法保存到队列中等待执行时，抛弃策略会悄悄抛弃该任务。

3. CallerRunsPolicy：调用者运行（Caller-Runs）策略实现了一种调节机制，该策略既不会抛弃任务，也不会抛弃异常，而是将某些任务回退到调用者，从而降低新任务的流量。它不会在线程池的某个线程中执行新提交的任务，而是在一个调用了 execute 的线程中执行该任务。

4. DiscardOldestPolicy：抛弃最旧的（Discard-Oldest）策略则会抛弃下一个将被执行的任务，然后尝试重新提交新的任务。如果工作队列是一个优先队列，那么”抛弃最旧的“策略将导致抛弃优先级最高的任务，因此最好不要将“抛弃最旧的”饱和策略和优先级队列放在一起使用。

## SynchronousQueue

对于非常大的或者无界的线程池，可以通过使用 SynchronousQueue 来避免任务排队，以及直接将任务从生产者移交给工作者线程。SynchronousQueque 不是一个真正的队列，而是一种在线程之间进行移交的机制。要将一个元素放入 SynchronousQueue 中，必须有另一个线程正在等待接受这个元素。如果没有线程正在等待，并且线程池的当前大小小于最大值，那么 ThreadPoolExecutor 将创建一个新的线程，否则根据饱和策略，这个任务将被拒绝。

## ThreadFactory

许多情况下都需要使用定制的线程工厂方法。

例如，为线程池中的线程制定一个 UncaughtExceptionHandler，或者实例化一个定制的 Thread 类用于执行调试信息的记录。线程优先级（其实执行的时候优先级只是个参考），或者守护状态（也不建议），**最常用的其实就是取个名字**。

Pivatol 团队就封装了个 NamedThreadFactory，简单的封装，简单的应用。

```java
package io.micrometer.core.instrument.util;

public class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger sequence = new AtomicInteger(1);
    private final String prefix;

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        int seq = this.sequence.getAndIncrement();
        thread.setName(this.prefix + (seq > 1 ? "-" + seq : ""));
        if (!thread.isDaemon()) {
            thread.setDaemon(true);
        }

        return thread;
    }
}
```

SpringBoot相关的监控解决方案从SpringBoot 2.0开始全面更改为Micrometer，不过要知道Micrometer与Spring属于同门，都是Pivotal旗下的产品。

## 扩展 ThreadPoolexecutor

+ beforeExecute

+ afterExecutor
+ terminated

添加日志、计时、监视或统计信息收集的功能。无论任务是从 run 中正常返回，还是抛出一个异常而返回，afterExecute 都会被调用。如果是一个 Error 就不会。如果 beforeExecute 抛出一个 RuntimeException，那么任务将不被执行，并且 afterExecute 也不会被调用。

在线程池完成关闭操作时调用 terminated，也就是在所有任务都已经完成并且所有工作者线程也已经关闭后。terminated 可以用来释放Executor 在其生命周期里分配的各种资源，此外还可以执行发送通知、记录日志或者收集 finalize 统计信息等操作。

