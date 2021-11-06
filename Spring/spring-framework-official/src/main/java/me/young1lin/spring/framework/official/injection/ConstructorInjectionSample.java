package me.young1lin.spring.framework.official.injection;

import javax.annotation.PostConstruct;
import java.beans.ConstructorProperties;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/11/6 下午11:34
 * @version 1.0
 */
@Component
public class ConstructorInjectionSample implements InitializingBean,
		SmartInitializingSingleton {

	private static final Logger logger =
			Logger.getLogger(ConstructorInjectionSample.class.getName());

	/**
	 * Number of years to calculate the Ultimate Answer
	 */
	private final int years;

	/**
	 * The Answer to Life, the Universe, and Everything
	 */
	private final String ultimateAnswer;


	@ConstructorProperties({"year", "ultimateAnswer"})
	public ConstructorInjectionSample(int years, String ultimateAnswer) {
		this.years = years;
		this.ultimateAnswer = ultimateAnswer;
	}

	@PostConstruct
	public void init() {
		logger.info("PostConstruct init");
		logger.info(String.format("years: [%s], ultimateAnswer: [%s]", years,
				ultimateAnswer));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("afterPropertiesSet init");
	}

	@Override
	public void afterSingletonsInstantiated() {
		logger.info("afterSingletonsInstantiated init");
	}

}
