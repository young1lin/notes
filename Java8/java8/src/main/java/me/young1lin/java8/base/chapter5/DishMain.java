package me.young1lin.java8.base.chapter5;

import java.util.Optional;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/28 下午3:55
 * @version 1.0
 */
public class DishMain {

	public static void main(String[] args) {
		Dish.menu.stream()
				.filter(Dish::isVegetarian)
				.findAny()
				.ifPresent(System.out::println);

		Optional<Dish> op = Dish.menu.stream()
				.filter(Dish::isVegetarian)
				.findFirst();
		System.out.println(op);

		Dish d = Dish.menu.stream().reduce(new Dish("season fruit",
				true, 120, Dish.Type.OTHER), Dish::getDish);
		System.out.println(d);

		Optional<Dish> d1 = Dish.menu.stream().reduce(Dish::getDish);
		System.out.println(d1);

		// menu 大小
		Optional<Integer> count = Dish.menu.stream().map(a->1).reduce(Integer::sum);
		Long count1 = Dish.menu.stream().count();
		

	}

}
