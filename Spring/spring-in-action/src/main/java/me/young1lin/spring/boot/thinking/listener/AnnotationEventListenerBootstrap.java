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
