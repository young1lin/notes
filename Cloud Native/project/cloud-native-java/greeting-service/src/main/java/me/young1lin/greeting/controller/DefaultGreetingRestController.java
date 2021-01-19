package me.young1lin.greeting.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/19 上午8:25
 * @version 1.0
 */
@Profile({"default", "insecure"})
@RestController
@RequestMapping(method = RequestMethod.GET, value = "/greet/{name}")
public class DefaultGreetingRestController {

	@RequestMapping
	public Map<String, String> hi(@PathVariable(name = "name") String name) {
		return Collections.singletonMap("greeting", "Hello" + name + "!");
	}

}
