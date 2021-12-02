package me.young1lin.spring.framework.official.injection;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

/**
 * <?xml version="1.0" encoding="UTF-8"?>
 * <beans xmlns="http://www.springframework.org/schema/beans"
 *     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *     xmlns:context="http://www.springframework.org/schema/context"
 *     xsi:schemaLocation="http://www.springframework.org/schema/beans
 *         https://www.springframework.org/schema/beans/spring-beans.xsd
 *         http://www.springframework.org/schema/context
 *         https://www.springframework.org/schema/context/spring-context.xsd">
 *
 *     <context:annotation-config/>
 *
 * </beans>
 *
 * The <context:annotation-config/> element implicitly registers the following post-processors:
 *
 * ConfigurationClassPostProcessor
 *
 * AutowiredAnnotationBeanPostProcessor
 *
 * CommonAnnotationBeanPostProcessor
 *
 * PersistenceAnnotationBeanPostProcessor
 *
 * EventListenerMethodProcessor
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/11/7 上午12:02
 * @version 1.0
 */
public class ConstructorInjectionExampleApplication {

	private static final Logger logger =
			Logger.getLogger(ConstructorInjectionExampleApplication.class.getName());


	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		new XmlBeanDefinitionReader(context)
				.loadBeanDefinitions("injection/constructor-injection.xml",
						"injection/method-injection.xml");
		context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
		context.registerShutdownHook();

		context.refresh();

		context.getBean(ConstructorInjectionExample.class);
		MethodInjectionExample methodInjectionExample = context.getBean(MethodInjectionExample.class);
		ConstructorProperty property = methodInjectionExample.getProperty();
		logger.info(property.toString());
		context.close();
	}

}
