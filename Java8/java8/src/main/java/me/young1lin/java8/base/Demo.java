package me.young1lin.java8.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/25 上午11:14
 * @version 1.0
 */
public class Demo implements Order<Demo> {

	private static final int DEMO_NUM = 8;

	private final int ordered;

	public Demo() {
		ordered = new Random().nextInt(20);
	}

	public static void main(String[] args) {
		ArrayList<Demo> objects = new ArrayList<>(1);
		for (int i = 0; i < DEMO_NUM; i++) {
			objects.add(new Demo());
		}

		objects.forEach((e) -> System.out.println(e.toString()));

		System.out.println("================");
		List<Demo> list = objects.stream()
				.filter((Demo e) -> e.getOrder() > 11)
				.collect(toList());
		list.forEach((e) -> System.out.println(e.toString()));

		System.out.println("================");
		List<Demo> list2 = objects.stream()
				.filter((e) -> e.getOrder() < 11)
				.filter(e -> e.getOrder() > 5)
				.collect(toList());
		list2.forEach((e) -> System.out.println(e.toString()));

		System.out.println("========");
		objects.sort(Demo::compareTo);
		objects.forEach((e) -> System.out.println(e.toString()));

		List<Integer> ll = objects.stream()
				.map(Demo::getOrder)
				.filter(e -> e > 2)
				.distinct()
				.limit(5)
				.collect(toList());
		System.out.println("========");
		ll.forEach((e) -> System.out.println(e.toString()));

		Supplier<Demo> supplier = Demo::new;
		System.out.println("-------" + supplier.get());

	}

	@Override
	public Integer getOrder() {
		return ordered;
	}

	@Override
	public int compareTo(Demo o) {
		return this.getOrder().compareTo(o.getOrder());
	}

	@Override
	public String toString() {
		return "Demo{order:" + getOrder() + "}";
	}
}
