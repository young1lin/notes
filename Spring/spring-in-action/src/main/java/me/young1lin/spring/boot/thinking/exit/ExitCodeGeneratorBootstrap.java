package me.young1lin.spring.boot.thinking.exit;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/5 上午8:20
 * @version 1.0
 */
@EnableAutoConfiguration
public class ExitCodeGeneratorBootstrap {

	@Bean
	public ExitCodeGenerator exitCodeGenerator() {
		System.out.println("ExitCodeGenerator Bean 创建.....");
		return () -> {
			System.out.println("执行退出码（88）生成");
			return 88;
		};
	}

	public static void main(String[] args) {
//		new SpringApplicationBuilder(ExitCodeGeneratorBootstrap.class)
//				.web(WebApplicationType.NONE)
//				.run(args)
//				.close();
		// 重构后
		SpringApplication.exit(new SpringApplicationBuilder(ExitCodeGeneratorBootstrap.class)
				.web(WebApplicationType.NONE)
				.run(args));
	}

}
