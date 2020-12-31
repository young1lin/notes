package me.young1lin.spring.boot.thinking.boot.listener;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/31 上午8:28
 * @version 1.0
 */
public class SpringEventListenerBootstrap {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Object.class)
				.listeners(event -> {
					System.out.println(event.getClass().getSimpleName());
				})
				.web(WebApplicationType.NONE)
				.run(args)
				.close();
	}

}
