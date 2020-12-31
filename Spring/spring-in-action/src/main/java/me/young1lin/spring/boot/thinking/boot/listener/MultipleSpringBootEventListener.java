package me.young1lin.spring.boot.thinking.boot.listener;

import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/31 上午8:09
 * @version 1.0
 */
public class MultipleSpringBootEventListener implements SmartApplicationListener {

	@Override
	public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
		return ApplicationReadyEvent.class.equals(eventType) ||
				ApplicationFailedEvent.class.equals(eventType);
	}

	@Override
	public boolean supportsSourceType(Class<?> sourceType) {
		// SpringApplicationEvent 均以 SpringApplication 作为配置源
		return SpringApplication.class.equals(sourceType);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ApplicationReadyEvent) {
			// 当事件为 ApplicationReadyEvent 时，随机抛出异常
			if (new Random().nextBoolean()) {
				throw new RuntimeException("ApplicationReadyEvent 事件监听异常");
			}
		}
		System.out.println("MultipleSpringBootEventListener 监听到事件" + event.getClass().getSimpleName());
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@EventListener({ApplicationReadyEvent.class, ApplicationFailedEvent.class})
	public void onSpringBootEvent(ApplicationEvent event) {
		System.out.println("@EventListener 监听到事件：" + event.getClass().getSimpleName());
	}

}
