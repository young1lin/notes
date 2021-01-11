package me.young1lin.spring.in.action;

import java.time.Instant;
import java.util.function.Supplier;

import me.young1lin.spring.core.resolve.auto.configuration.service.HelloService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author young1lin
 * @date 2020/8/25
 */
@SpringBootApplication
public class SpringInActionApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringInActionApplication.class);
	}

	public static void main(String[] args) {

//		ConfigurableApplicationContext cac = SpringApplication.run(SpringInActionApplication.class, args);
//		BeanFactory beanFactory = cac.getParent();
//		System.out.println("--------------------");
//		ConfigurableListableBeanFactory beanFactory1 = cac.getBeanFactory();
//		System.out.println(beanFactory1.getClass().getName());
//		if(beanFactory!=null){
//			System.out.println(beanFactory.getClass().getName());
//		}
//		DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory)beanFactory;
		// listableBeanFactory.set
		ConfigurableApplicationContext cac = new SpringApplication(SpringInActionApplication.class).run(args);
		// cac.
		// 通过依赖查找，来找到 Bean
		HelloService helloService = cac.getBean("helloService", HelloService.class);
		// 这里输出的话，就已经自动装配成功了
		System.out.println(helloService.hello());

	}
}
