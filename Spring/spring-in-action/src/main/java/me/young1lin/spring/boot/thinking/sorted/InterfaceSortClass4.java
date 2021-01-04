package me.young1lin.spring.boot.thinking.sorted;

import org.springframework.core.Ordered;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 上午8:45
 * @version 1.0
 */
public class InterfaceSortClass4 extends PrintRunnerAdapter implements Ordered {

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 4;
	}

}
