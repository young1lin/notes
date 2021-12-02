package me.young1lin.spring.framework.official.el;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/11/7 上午12:29
 * @version 1.0
 */
public class ExpressionLanguageExampleApplication {

	public static void main(String[] args) {
		GenericApplicationContext context = new GenericApplicationContext();
		new XmlBeanDefinitionReader(context)
				.loadBeanDefinitions("el/expression-language.xml");
		context.refresh();
		context.getBean(ExpressionLanguageExample.class);
		context.close();
	}

}
