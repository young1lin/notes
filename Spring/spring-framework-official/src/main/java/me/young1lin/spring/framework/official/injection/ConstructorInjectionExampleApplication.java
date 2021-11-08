package me.young1lin.spring.framework.official.injection;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/11/7 上午12:02
 * @version 1.0
 */
public class ConstructorInjectionExampleApplication {

	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		new XmlBeanDefinitionReader(context)
				.loadBeanDefinitions("injection/constructor-injection.xml");
		context.refresh();
		context.getBean(ConstructorInjectionExample.class);
		context.close();
	}

}
