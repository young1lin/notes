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

**由于每个县城拥有各自的中断策略，因此除非你知道中断对该线程的含义，否则就不应该中断这个线程。**

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



### 阻塞库方法

Object#wait，Thread#sleep。（这两个的区别）


