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

		@EventListener(ContextRefreshedEvent.class)
		@Async
		public void onContextRefreshedEvent(ContextRefreshedEvent event) {
			System.out.println("current Thread is :"
					+ Thread.currentThread().getName() + event.getClass().getSimpleName());
		}
	}

}
