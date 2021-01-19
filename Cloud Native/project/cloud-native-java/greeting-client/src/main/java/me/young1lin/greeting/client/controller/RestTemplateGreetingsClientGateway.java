package me.young1lin.greeting.client.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/19 上午8:35
 * @version 1.0
 */
@Profile({"default", "insecure"})
@RestController
@RequestMapping(value = "/api")
public class RestTemplateGreetingsClientGateway {

	private final RestTemplate restTemplate;

	/**
	 *
	 * @param restTemplate for ribbon
	 */
	@Autowired
	public RestTemplateGreetingsClientGateway(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/resttemplate/{name}")
	public Map<String, String> restTemplate(@PathVariable("name") String name) {
		ParameterizedTypeReference<Map<String, String>> type = new ParameterizedTypeReference() {};
		ResponseEntity<Map<String, String>> responseEntity = this.restTemplate
				.exchange("http://greetings-service/greet/{name}", HttpMethod.GET, null, type, name);
		return responseEntity.getBody();
	}

}
