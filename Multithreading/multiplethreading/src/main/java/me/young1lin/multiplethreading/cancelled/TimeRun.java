package me.young1lin.multiplethreading.cancelled;

import java.util.concurrent.*;

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
