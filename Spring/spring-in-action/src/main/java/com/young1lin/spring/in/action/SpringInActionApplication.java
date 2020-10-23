package com.young1lin.spring.in.action;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @date 2020/8/25
 * @author young1lin
 */
@SpringBootApplication
public class SpringInActionApplication {

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
	}
}
