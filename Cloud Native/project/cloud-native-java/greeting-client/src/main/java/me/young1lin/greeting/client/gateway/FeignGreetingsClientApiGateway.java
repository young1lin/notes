package me.young1lin.greeting.client.gateway;

import java.util.Map;

import me.young1lin.greeting.client.feign.GreetingsClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/29 上午8:26
 * @version 1.0
 */
@Profile({"feign"})
@RestController
@RequestMapping("/api")
public class FeignGreetingsClientApiGateway {

	private final GreetingsClient greetingsClient;

	/**
	 * 不用加 @Autowired 都行，Spring 会自动注入
	 * @param greetingsClient the client for greeting
	 */
	@Autowired
	FeignGreetingsClientApiGateway(GreetingsClient greetingsClient) {
		this.greetingsClient = greetingsClient;
	}

	@GetMapping("/feign/{name}")
	public Map<String, String> feign(@PathVariable String name) {
		return this.greetingsClient.greet(name);
	}

}
