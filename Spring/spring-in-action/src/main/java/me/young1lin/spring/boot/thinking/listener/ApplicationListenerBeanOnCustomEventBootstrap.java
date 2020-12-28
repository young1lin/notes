package me.young1lin.spring.boot.thinking.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/28 上午8:19
 * @version 1.0
 */
public class ApplicationListenerBeanOnCustomEventBootstrap {

	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		context.registerBean(MyApplicationListener.class);
		context.refresh();
		context.publishEvent(new MyApplicationEvent("Hello World", "foo"));
		context.close();

		// 上下文已经关闭，所以下面的不执行
		context.publishEvent(new MyApplicationEvent("Hello World again!!", "bar"));
	}
}

class MyApplicationEvent extends ApplicationEvent {

	private final String a;

	/**
	 * Create a new {@code ApplicationEvent}.
	 * @param source the object on which the event initially occurred or with
	 * which the event is associated (never {@code null})
	 */
	public MyApplicationEvent(Object source, String a) {
		super(source);
		this.a = a;
	}

	public String getA() {
		return a;
	}

}

class MyApplicationListener implements ApplicationListener<MyApplicationEvent> {

	@Override
	public void onApplicationEvent(MyApplicationEvent event) {
		System.out.println(event.getClass().getSimpleName());
		System.out.println("a is " + event.getA());
	}

}