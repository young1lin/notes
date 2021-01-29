package me.young1lin.greeting.client.feign;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 这里的 name 替换了原来的 serviceId
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/19 上午8:44
 * @version 1.0
 */
@FeignClient(name = "greetings-service")
public interface GreetingsClient {

	/**
	 * 远程过程调用，调用 greetings-service 这个服务下的 /greet/name
	 * @param name the name for greeting
	 * @return Hello + {@code name}
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/greet/{name}")
	Map<String, String> greet(@PathVariable("name") String name);

}
