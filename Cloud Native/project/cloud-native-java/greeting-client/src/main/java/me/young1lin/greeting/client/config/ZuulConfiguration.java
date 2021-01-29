package me.young1lin.greeting.client.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/29 上午8:31
 * @version 1.0
 */
@Configuration
@EnableZuulProxy
public class ZuulConfiguration {

	@Bean
	CommandLineRunner commandLineRunner(RouteLocator routeLocator) {
		Log log = LogFactory.getLog(getClass());
		return args -> routeLocator.getRoutes().forEach(
				r -> log.info(String.format("%s (%s) %s", r.getId(), r.getLocation(),
						r.getFullPath()))
		);
	}
}
