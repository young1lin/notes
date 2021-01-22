package me.young1lin.spring.boot.thinking.scope;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/22 11:40 下午
 */
@Configuration
public class ScopeConfig {

	@Bean
	public CustomScopeConfigurer customScopeConfigurer() {
		CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
		customScopeConfigurer.addScope(ThreadLocalScope.SCOPE_NAME, new ThreadLocalScope());
		return customScopeConfigurer;
	}

}
