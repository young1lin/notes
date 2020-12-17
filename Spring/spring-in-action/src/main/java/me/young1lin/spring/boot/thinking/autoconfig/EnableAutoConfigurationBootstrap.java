package me.young1lin.spring.boot.thinking.autoconfig;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/17 7:59 上午
 * @version 1.0
 */
@EnableAutoConfiguration(exclude = SpringApplicationAdminJmxAutoConfiguration.class)
public class EnableAutoConfigurationBootstrap {

	public static void main(String[] args) {
		new SpringApplicationBuilder(EnableAutoConfigurationBootstrap.class)
				//非 Web 应用
				.web(WebApplicationType.NONE)
				.run(args)
				.close();
	}

}
