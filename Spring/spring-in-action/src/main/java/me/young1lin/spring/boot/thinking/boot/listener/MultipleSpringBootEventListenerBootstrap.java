package me.young1lin.spring.boot.thinking.boot.listener;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/31 上午8:15
 * @version 1.0
 */
public class MultipleSpringBootEventListenerBootstrap {

	public static void main(String[] args) {
		new SpringApplicationBuilder(MultipleSpringBootEventListener.class)
				// 非 Web 应用
				.web(WebApplicationType.NONE)
				.run(args)
				.close();
	}

}
