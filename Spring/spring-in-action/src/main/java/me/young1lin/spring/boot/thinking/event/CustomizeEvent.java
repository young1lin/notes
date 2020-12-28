package me.young1lin.spring.boot.thinking.event;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/28 上午8:04
 * @version 1.0
 */
public class CustomizeEvent extends ApplicationEvent implements ApplicationContextAware {

	private final String address;

	private final String test;

	private ApplicationContext applicationContext;

	/**
	 * Create a new {@code ApplicationEvent}.
	 * @param source the object on which the event initially occurred or with
	 * which the event is associated (never {@code null})
	 * @param address address
	 * @param test test
	 */
	public CustomizeEvent(Object source, String address, String test) {
		super(source);
		this.address = address;
		this.test = test;
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	public void test(){
		applicationContext.publishEvent(new CustomizeEvent("","",""));
	}

}
