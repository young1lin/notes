package me.young1lin.spring.framework.official.injection;

import javax.annotation.PostConstruct;
import java.beans.ConstructorProperties;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.SmartLifecycle;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/11/6 下午11:34
 * @version 1.0
 */
public class ConstructorInjectionExample implements InitializingBean, SmartLifecycle,
		SmartInitializingSingleton {

	private static final Logger logger =
			Logger.getLogger(ConstructorInjectionExample.class.getName());

	/**
	 * Number of years to calculate the Ultimate Answer
	 */
	private final int years;

	/**
	 * The Answer to Life, the Universe, and Everything
	 */
	private final String ultimateAnswer;

	private boolean isRunning;


	@ConstructorProperties("property")
	public ConstructorInjectionExample(ConstructorProperty property) {
		this.years = property.getYears();
		this.ultimateAnswer = property.getUltimateAnswer();
	}

	@PostConstruct
	public void init() {
		logger.info("PostConstruct init");
	}

	@Override
	public void afterPropertiesSet() {
		logger.info("afterPropertiesSet init");
		logger.info(String.format("years: [%s], ultimateAnswer: [%s]", years,
				ultimateAnswer));
	}

	public void initMethod() {
		logger.info("initMethod init");
	}

	@Override
	public void afterSingletonsInstantiated() {
		logger.info("afterSingletonsInstantiated init");
	}

	@Override
	public void start() {
		logger.info("start...");
		isRunning = true;
	}

	@Override
	public void stop() {
		logger.info("stop...");
		isRunning = false;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

}
