package me.young1lin.spring.boot.thinking.conditional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/12/15 7:56 上午
 */
@Configuration
public class ConditionalMessageConfiguration {

    @ConditionalOnSystemProperty(name = "language", value = "Chinese")
    @Bean("message")
    public String chineseMessage() {
        return "你好，世界";
    }

    @ConditionalOnSystemProperty(name = "language", value = "English")
    @Bean("message")
    public String englishMessage() {
        return "Hello World";
    }

}
