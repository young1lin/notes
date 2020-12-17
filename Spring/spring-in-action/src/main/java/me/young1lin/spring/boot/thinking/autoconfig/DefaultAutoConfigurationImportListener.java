package me.young1lin.spring.boot.thinking.autoconfig;

import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfigurationImportEvent;
import org.springframework.boot.autoconfigure.AutoConfigurationImportListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/17 7:50 上午
 * @version 1.0
 */
public class DefaultAutoConfigurationImportListener implements AutoConfigurationImportListener {

	@Override
	public void onAutoConfigurationImportEvent(AutoConfigurationImportEvent event) {
		// 获取当前 ClassLoader
		ClassLoader classLoader = event.getClass().getClassLoader();
		// 候选的自动装配 Class 名单
		List<String> candidates =
				SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,
						classLoader);
		// 实际的自动装配 Class 名单
		List<String> configurations = event.getCandidateConfigurations();
		// 排除的自动装配的 Class 名单
		Set<String> exclusions = event.getExclusions();
		System.out.printf("自动装配 Class 名单 - 候选数量：%d，排除数量：%s\n",
				candidates.size(), exclusions.size());
		// 输出实际和排除的自动装配的 Class 名单
		event.getCandidateConfigurations().forEach(System.out::println);
		System.out.println("=====排除的自动装配的名单");
		event.getExclusions().forEach(System.out::println);
	}

}
