package me.young1lin.spring.framework.official.injection;

import org.springframework.beans.factory.annotation.Lookup;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/11/7 上午12:52
 * @version 1.0
 */
public class MethodInjectionExample {

	@Lookup("property")
	public ConstructorProperty getProperty(){
		ConstructorProperty property = new ConstructorProperty();
		property.setYears(88888);
		property.setUltimateAnswer("Bar");
		return property;
	}

}
