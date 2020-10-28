package me.young1lin.spring.core.resolve.auto.configuration;

import me.young1lin.spring.core.resolve.auto.configuration.service.HelloService;
import me.young1lin.spring.core.resolve.auto.configuration.service.impl.HelloServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 7:45 上午
 */
@Configuration
@ComponentScan({"me.young1lin.spring.core.resolve.auto"})
public class HelloServiceAutoConfiguration {
    @Bean
    public HelloService helloService(){
        return new HelloServiceImpl();
    }
}
