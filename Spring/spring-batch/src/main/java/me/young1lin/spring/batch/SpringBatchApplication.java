package me.young1lin.spring.batch;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import me.young1lin.spring.batch.entity.Person;
import me.young1lin.spring.batch.mapper.PersonMapper;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 */
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				new SpringApplicationBuilder(SpringBatchApplication.class)
						.web(WebApplicationType.SERVLET)
						.run(args);
		PersonMapper personMapper = context.getBean(PersonMapper.class);
		List<Person> list = personMapper.listAll();
		list.forEach(System.out::println);
	}

}
