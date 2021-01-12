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
		ArrayList<Demo> list = new ArrayList<>(1);
		for (int i = 0; i < DEMO_NUM; i++) {
			list.add(new Demo());
		}
		// 输出当前未被更改的 list
		list.forEach((e) -> System.out.println(e.toString()));
		// 筛选 order 大于 11 的 Demo
		System.out.println("================");
		List<Demo> list1 = list.stream()
				.filter((Demo e) -> e.getOrder() > 11)
				.collect(toList());
		list1.forEach((e) -> System.out.println(e.toString()));

		// 再次筛选 小于 11 大于 5 的
		System.out.println("================");
		List<Demo> list2 = list.stream()
				.filter((e) -> e.getOrder() < 11)
				.filter(e -> e.getOrder() > 5)
				.collect(toList());
		list2.forEach((e) -> System.out.println(e.toString()));

		// 根据 compareTo 排序，也就是 order 大小进行排序
		System.out.println("========");
		list.sort(Demo::compareTo);
		list.forEach((e) -> System.out.println(e.toString()));

		// 对原始 list 进行去重、大于 2 的筛选以及最多 5 个 order
		List<Integer> ll = list.stream()
				.map(Demo::getOrder)
				.filter(e -> e > 2)
				.distinct()
				.limit(5)
				.collect(toList());
		System.out.println("========");
		ll.forEach((e) -> System.out.println(e.toString()));

		// 对原始 list 进行去重、大于 2 的筛选以及最多 3 个 order
		List<Integer> ll1 = list.stream()
				.map(Demo::getOrder)
				.filter(e -> e > 2)
				.distinct()
				.limit(5)
				.skip(2)
				.collect(toList());
		System.out.println("========");
		ll1.forEach((e) -> System.out.println(e.toString()));
		// 语法 Demo::new
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
