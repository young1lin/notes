package me.young1lin.spring.boot.thinking.listener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/29 上午7:58
 * @version 1.0
 */
public class AnnotatedEventListenerOnMultiEventBootstrap {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(MyMultiEventListener.class);
		context.refresh();
		context.close();
	}

	public static class MyMultiEventListener {

		@EventListener({ContextRefreshedEvent.class, ContextClosedEvent.class})
		public void onEvent() {
			System.out.println("onEvent=========start");
		}

		@EventListener({ContextRefreshedEvent.class, ContextClosedEvent.class})
		public void onApplicationContextEvent(ApplicationContextEvent event) {
			System.out.println("onApplicationContextEvent : "
					+ event.getClass().getSimpleName());
		}

		// 最多支持一个 Event 参数，并且这个 event 参数应是 ApplicationEvent,或者不加这个参数
//		@EventListener({ContextRefreshedEvent.class, ContextClosedEvent.class})
//		public void onEvents(ContextRefreshedEvent refreshedEvent, ContextClosedEvent closedEvent) {
//			System.out.println("onEvents : " + refreshedEvent.getClass().getSimpleName()
//					+ "," + closedEvent.getClass().getSimpleName());
//		}

	}

}
