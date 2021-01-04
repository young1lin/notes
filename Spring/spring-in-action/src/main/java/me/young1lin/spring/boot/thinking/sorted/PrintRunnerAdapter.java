package me.young1lin.spring.boot.thinking.sorted;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/4 上午8:37
 * @version 1.0
 */
public class PrintRunnerAdapter implements PrintRunner {

	@Override
	public void print() {
		System.out.println(this.getClass().getSimpleName() + "========print()");
	}

}
