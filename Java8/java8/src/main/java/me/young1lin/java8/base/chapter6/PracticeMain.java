package me.young1lin.java8.base.chapter6;

import static java.util.stream.Collectors.*;
import static me.young1lin.java8.base.chapter5.Dish.menu;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import me.young1lin.java8.base.chapter5.Dish;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/29 下午3:01
 * @version 1.0
 */
public class PracticeMain {

	public static void main(String[] args) {
		// 总值 仅为演示用
		int totalCalories = menu.stream()
				.collect(summingInt(Dish::getCalories));
		System.out.println(totalCalories);

		System.out.println("===============");
		// 平均值
		double avg = menu.stream()
				.collect(averagingDouble(Dish::getCalories));
		System.out.println(avg);

		System.out.println("===============");
		// 获得 IntSummaryStatistics，即可获得最大值，最小值，平均值，count，sum
		IntSummaryStatistics intSummaryStatistics = menu.stream()
				.collect(summarizingInt(Dish::getCalories));
		System.out.println(intSummaryStatistics.toString());

		System.out.println("===============");
		// 获得短菜单
		String shortMenu = menu.stream().map(Dish::getName).collect(joining(","));
		System.out.println(shortMenu);

		System.out.println("===============");
		// 分组
		Map<Dish.Type, List<Dish>> dishesByType = menu.stream()
				.collect(groupingBy(Dish::getType));
		dishesByType.forEach((k, v) ->
				System.out.println(k + ":[" + v.stream().map(Dish::getName).collect(joining(",")) + "]"));
		;
	}

}
