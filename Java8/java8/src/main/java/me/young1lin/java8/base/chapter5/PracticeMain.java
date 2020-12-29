package me.young1lin.java8.base.chapter5;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/28 下午3:55
 * @version 1.0
 */
public class PracticeMain {

	public static void main(String[] args) {
		Dish.menu.stream()
				.filter(Dish::isVegetarian)
				.findAny()
				.ifPresent(System.out::println);

		System.out.println("==================");
		// 过滤，并找到第一个 Dish，返回成 Optional<Dish>
		Optional<Dish> op = Dish.menu.stream()
				.filter(Dish::isVegetarian)
				.findFirst();
		System.out.println(op);

		System.out.println("==================");
		// 给定一个初始值，规约所有 Dish 成一个 Optional<Dish>
		Dish d = Dish.menu.stream().reduce(new Dish("season fruit",
				true, 120, Dish.Type.OTHER), Dish::getDish);
		System.out.println(d);

		System.out.println("==================");
		// reduce 归约所有 Dish 成一个 Optional<Dish>
		Optional<Dish> d1 = Dish.menu.stream().reduce(Dish::getDish);
		System.out.println(d1);

		System.out.println("==================");
		// menu 大小
		Optional<Integer> count = Dish.menu.stream().map(a -> 1).reduce(Integer::sum);
		// 仅为演示需要
		Long count1 = Dish.menu.stream().count();
		Long count2 = Long.valueOf(count.get());
		System.out.println(count1.equals(count2));

		System.out.println("==================");
		// 免除自动拆装箱统计卡路里
		int calories = Dish.menu.stream()
				.mapToInt(Dish::getCalories)
				.sum();
		System.out.println(calories);

		System.out.println("==================");
		// 转化为流
		IntStream intStream = Dish.menu.stream().mapToInt(Dish::getCalories);
		Stream<Integer> stream = intStream.boxed();
		stream.forEach(System.out::println);

		System.out.println("==================");
		// 找到最大卡路里的值 OptionalInt
		OptionalInt max = Dish.menu.stream().mapToInt(Dish::getCalories).max();
		System.out.println(max.isPresent() ? max.getAsInt() : "0");

		System.out.println("==================");
		// 数值范围，带 Closed 表示 [1,100] 不带 Closed 表示 [1,100)。
		IntStream evenNumbers = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0);
		System.out.println(evenNumbers.count());

		System.out.println("==================");
		// 勾股数 Pythagorean Triples
		Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, 100)
				.boxed()
				.flatMap(a ->
						IntStream.rangeClosed(1, 100)
								.filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
								.mapToObj(b -> new int[] {a, b, (int) Math.sqrt(a * a + b * b)})
				);
		pythagoreanTriples.forEach(e -> System.out.println(Arrays.toString(e)));

		System.out.println("==================");
		// 从文件中读取数据，并且求出唯一的单词有多少。不知道为什么没用
		long uniqueWords = 0;
		try (Stream<String> lines = Files.lines(Paths.get("chapter5/data.txt"), Charset.defaultCharset())) {
			uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" "))).distinct().count();
		}
		catch (IOException e) {
			//e.printStackTrace();
		}
		System.out.println("uniqueWords num is :" + uniqueWords);

		System.out.println("==================");
		// 无限流♾️，第一个参数是初始值，后面是依次产生新值上的 Lambda
		Stream.iterate(0, n -> n + 2)
				.limit(10)
				.forEach(System.out::println);

		System.out.println("==================");
		// 斐波那契数列
		Stream.iterate(new int[] {0, 1}, t -> new int[] {t[1], t[0] + t[1]})
				.limit(20)
				.map(t -> t[0])
				.forEach(System.out::println);

		System.out.println("==================");
		// 流的生成
		Stream.generate(Math::random)
				.limit(10)
				.forEach(System.out::println);

		System.out.println("==================");
		IntSupplier fib = new IntSupplier(){
			private int previous = 0;
			private int current = 1;
			@Override
			public int getAsInt() {
				int oldPrevious = previous;
				int nextValue = previous + current;
				this.previous = current;
				this.current = nextValue;
				return oldPrevious;
			}
		};
		IntStream.generate(fib).limit(20).forEach(System.out::println);


	}

}
