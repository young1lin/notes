package me.young1lin.java8.base;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/28 上午11:05
 * @version 1.0
 */
public class Demo2 implements Order<Integer> {

	private static final int DEMO_NUM = 8;

	private final Integer ordered;

	public Demo2() {
		ordered = new Random().nextInt(20);
	}

	public static void main(String[] args) {
		ArrayList<Demo> list = new ArrayList<>(1);
		for (int i = 0; i < DEMO_NUM; i++) {
			list.add(new Demo());
		}
		// 找出两个集合中相加能整除 3 的值。flatMap 流的扁平化
		List<Integer> num1 = Arrays.asList(1, 2, 3);
		List<Integer> num2 = Arrays.asList(3, 4);
		List<int[]> pairs =
				num1.stream()
						.flatMap(i -> num2.stream()
								.filter(j -> (i + j) % 3 == 0)
								.map(j -> new int[] {i, j}))
						.collect(toList());
		pairs.forEach(e -> System.out.println(Arrays.toString(e)));

	}

	@Override
	public Integer getOrder() {
		return null;
	}

	@Override
	public int compareTo(Integer o) {
		return this.ordered.compareTo(o);
	}

	@Override
	public String toString() {
		return "Demo2{order:" + getOrder() + "}";
	}
}
