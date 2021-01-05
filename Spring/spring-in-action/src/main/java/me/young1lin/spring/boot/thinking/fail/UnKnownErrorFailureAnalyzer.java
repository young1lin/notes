package me.young1lin.spring.boot.thinking.fail;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/5 上午7:46
 * @version 1.0
 */
public class UnKnownErrorFailureAnalyzer implements FailureAnalyzer,Ordered {

	private ConfigurableApplicationContext context;

	public UnKnownErrorFailureAnalyzer(){}

	@Override
	public FailureAnalysis analyze(Throwable failure) {
		if (failure instanceof UnknownError) {
			return new FailureAnalysis("未知错误", "重启试试？", failure);
		}
		return null;
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
