package me.young1lin.spring.boot.thinking.value;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/4/27 下午7:43
 * @version 1.0
 */
@Component
public class ValueDetermined {

	@Value("${user.name}")
	private String name;


	public ValueDetermined() {
	}

	@PostConstruct
	public void init() {
		System.out.println(name);
	}

}
