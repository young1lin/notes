package me.young1lin.spring.boot.thinking.fail;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalysisReporter;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/5 上午7:49
 * @version 1.0
 */
public class ConsoleFailureAnalysisReporter implements FailureAnalysisReporter {

	@Override
	public void report(FailureAnalysis analysis) {
		System.out.printf("故障描述：%s \n执行动作：%s \n 异常堆栈：%s\n",
				analysis.getDescription(),
				analysis.getAction(),
				analysis.getCause());
	}

}
