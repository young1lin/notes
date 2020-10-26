package me.young1lin.netty.demo.component;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/17 2:38 上午
 */
public class ChannelDemo {
    public static void main(String[] args) throws IOException {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread("777");
            }
        });
        executor.schedule(() -> {
            System.out.println("60s 过去了");
        }, 60, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
