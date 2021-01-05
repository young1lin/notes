package me.young1lin.spring.boot.thinking.fail;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/5 上午7:51
 * @version 1.0
 */
public class UnKnowErrorSpringBootstrap {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Object.class)
				.initializers(context -> {
					throw new UnknownError("故意抛出异常");
				})
				.web(WebApplicationType.NONE)
				.run(args)
				.close();
	}

}

