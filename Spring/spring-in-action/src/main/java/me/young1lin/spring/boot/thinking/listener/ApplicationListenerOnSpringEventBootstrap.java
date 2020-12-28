package me.young1lin.spring.boot.thinking.listener;

import org.springframework.context.support.GenericApplicationContext;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/28 上午8:11
 * @version 1.0
 */
public class ApplicationListenerOnSpringEventBootstrap {

	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		System.out.println("Create Spring Application context" + context.getDisplayName());
		context.addApplicationListener(event -> System.out.println(event.getClass().getSimpleName()));
		System.out.println("Spring Application context prepare to refresh");
		context.refresh();
		System.out.println("refreshed....");
		System.out.println("Spring Application context prepare to stop");
		context.stop();
		System.out.println("Spring Application context already stopped");

		System.out.println("Spring Application context prepare start");
		context.start();
		System.out.println("Spring Application context already started");


		System.out.println("Spring Application context prepare to stop");
		context.stop();
		System.out.println("Spring Application context already stopped");

	}

}
