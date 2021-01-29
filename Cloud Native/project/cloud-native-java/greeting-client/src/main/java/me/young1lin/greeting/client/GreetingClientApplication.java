package me.young1lin.greeting.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/19 上午8:31
 * @version 1.0
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class GreetingClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreetingClientApplication.class, args);
	}

}
