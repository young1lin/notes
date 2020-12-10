package me.young1lin.multiplethreading.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/10 9:50 下午
 */
public class BoundedExecutor {

    private final Executor exe;

    private final Semaphore semaphore;


    public BoundedExecutor(Executor exe, Semaphore semaphore) {
        this.exe = exe;
        this.semaphore = semaphore;
    }

    public void submitTask(Runnable command) throws InterruptedException {
        semaphore.acquire();
        try {
            exe.execute(() -> {
                try {
                    command.run();
                } finally {
                    semaphore.release();
                }

            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
        }
    }

}
