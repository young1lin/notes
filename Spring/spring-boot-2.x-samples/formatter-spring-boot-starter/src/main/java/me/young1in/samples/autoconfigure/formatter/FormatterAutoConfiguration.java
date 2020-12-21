package me.young1in.samples.autoconfigure.formatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/21 8:05 上午
 * @version 1.0
 */
@Configuration
public class FormatterAutoConfiguration {

	/**
	 * 构建 {@link DefaultFormatter} Bean
	 * @return {@link DefaultFormatter}
	 */
	@Bean
	public Formatter defaultFormatter() {
		return new DefaultFormatter();
	}

}
