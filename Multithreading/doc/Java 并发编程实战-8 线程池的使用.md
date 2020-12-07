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

无界队列将不会执行拒绝策略，当然如果你的线程数大于了 Integer.MAX_VALUE 还是会执行的，前提是你到达这么多。一般情况就 OOM 了。所以《阿里巴巴 Java 开发规范》中强制不使用 Executors 创建线城池。

