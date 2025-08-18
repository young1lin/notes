使用 volatile 类型的域来保存取消状态。

因为更改后线程间可见。如下：

```java
package me.young1lin.multiplethreading.cancelled;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/11/26 11:25 下午
 */
public class PrimeGenerator implements Runnable {

    private final List<BigInteger> primes = new ArrayList<BigInteger>();
    private volatile boolean cancelled;

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            p = p.nextProbablePrime();
            synchronized (this) {
                primes.add(p);
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public synchronized List<BigInteger> get() {
        return new ArrayList<BigInteger>(primes);
    }

    public static void main(String[] args) throws InterruptedException {
        PrimeGenerator generator = new PrimeGenerator();
        new Thread(generator).start();
        try {
            Thread.sleep(10000);
        } finally {
            generator.cancel();
        }
        System.out.println(generator.get());
    }

}
```

## 中断

1. 中断线程

Thread#interrupt

2. 返回目标线程中断状态

Thread#isInterrupted

3. 清除当前线程的中断状态

Thread#interrupted （静态方法）

调用 interrupt 并不意味着立即停止目标县城正在进行的工作，而只是传递了请求中断的消息。——给 OS 传个消息而已。

**通常，中断是实现取消的最合理的方式。**

## 中断策略

抛出 InterruptedException。

**由于每个线程拥有各自的中断策略，因此除非你知道中断对该线程的含义，否则就不应该中断这个线程。**

## 响应中断

当调用可中断的阻塞函数时，例如 Thread#sleep 或 BlockingQueue#put 等，有两种实用策略可用于处理 InterruptedException 

1. 传递异常（可能在执行某个特定于任务的清除操作之后），从而使你的方法行也成为可中断的阻塞方法。
2. 恢复中断状态，从而使调用栈中的上层代码能够对其进行处理。

只有实现了线程中断策略的代码才可以屏蔽中断请求。在常规的任务和库代码中都不应该屏蔽中断请求。

```java
/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/2 9:29 下午
 */
public class TimeRun {

    private static final ThreadPoolExecutor TASK_EXEC =
            new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "myThreadFactory-");
                }
            });


    public static void timedRun(Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> task = TASK_EXEC.submit(r);
        try {
            task.get(timeout, unit);
        } catch (TimeoutException e) {
            // 接下来任务将被取消
        } catch (ExecutionException e) {
            throw new InterruptedException();
        } finally {
            task.cancel(true);
        }
    }
}
```

## 处理不可中断的阻塞

```java
package me.young1lin.multiplethreading.cancelled;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/2 9:42 下午
 */
public class ReaderThread extends Thread {

    private final Socket socket;
    private final InputStream in;

    public ReaderThread(Socket socket, InputStream in) {
        this.socket = socket;
        this.in = in;
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignored) {

        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1024];
            while (true) {
                int count = in.read(buf);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    processBuffer(buf, count);
                }
            }
        } catch (IOException e) {
            
        }
    }

    private void processBuffer(byte[] buf, int count) {
    }

}
```

## 采用 newTaskFor 来封装非标准的取消

```java
RunnableFuture<?> rFuture = thredPoolExecutor.newTaskFor(callable);
```

runnableFuture 实现了 Future 和 Runnable 接口，可以调用 Future#cancel

# 停止基于线程的服务

shutdown

shutdownNow

我懂

## 毒丸对象

毒丸-> Queue ->消费者停止消费，在关闭完成前完成毒丸对象前的所有工作。

只有在生产者和消费者的数量都已知的情况下，才可以使用“毒丸”对象。只有在无界队列中，“毒丸”对象才能可靠地工作。

# 未捕获异常

RuntimeException 抛出后，能把当前线程退出。

Thread#UncaughtExceptionHandler 接口

```java
@FunctionalInterface
public interface UncaughtExceptionHandler {
    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     * @param t the thread
     * @param e the exception
     */
    void uncaughtException(Thread t, Throwable e);
}
```

捕捉线程异常。

在运行时间较长的应用程序中，通常会为线程的未捕获异常制定同一个异常处理器，并且该处理器至少会将异常信息记录到日志中。

execute 能捕捉异常。看源码就知道为什么了。

submit 不能，异常是其中的一部分。

# JVM 关闭

Shutdown Hook

Spring 中也有，注册 Shutdown Hook，优雅关闭 Gracefully。

## 守护线程

线程可分为两种：普通线程和守护线程。在 JVM 启动时创建的所有线程中，除了主线程以外，其他线程都是守护线程（例如垃圾回收器以及其他执行辅助工作的线程）。当创建一个新线程时，新线程将继承它的线程的守护状态，因此在默认的情况下，主线程创建的所有的线程都是普通线程。

当一个线程退出时，JVM 会检查其他正在进行的线程，如果这些线程都是守护线程，那么 JVM 会正常退出操作。当 JVM停止时，所有仍然存在的守护线程都将被抛弃——既不会执行 finally 代码块，也不会执行回卷栈，而 JVM 只是直接退出。

**守护线程通常不能用来替代应用程序管理程序中各个服务的生命周期**。

## 终结器

finaylize  方法。

避免使用。

### 阻塞库方法

Object#wait，Thread#sleep。（这两个的区别）


