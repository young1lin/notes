package me.young1lin.java8.base.chapter7;

import me.young1lin.java8.base.chapter5.Dish;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/5 上午10:09
 * @version 1.0
 */
public class Parallel {

	public static void main(String[] args) {
		Dish.menu.stream().parallel().count();
		// 书上不止讲并行 stream，还讲了 Lambda 之于几个设计模式的用法。我都会就没写了
	}

}
