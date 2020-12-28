package me.young1lin.java8.base.chapter5;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/28 上午10:37
 * @version 1.0
 */
public class Dish {

	public static final List<Dish> menu =
			asList(new Dish("pork", false, 800, Dish.Type.MEAT),
					new Dish("beef", false, 700, Dish.Type.MEAT),
					new Dish("chicken", false, 400, Dish.Type.MEAT),
					new Dish("french fries", true, 530, Dish.Type.OTHER),
					new Dish("rice", true, 350, Dish.Type.OTHER),
					new Dish("season fruit", true, 120, Dish.Type.OTHER),
					new Dish("pizza", true, 550, Dish.Type.OTHER),
					new Dish("prawns", false, 400, Dish.Type.FISH),
					new Dish("salmon", false, 450, Dish.Type.FISH));

	public static final Map<String, List<String>> dishTags = new HashMap<>();

	static {
		dishTags.put("pork", asList("greasy", "salty"));
		dishTags.put("beef", asList("salty", "roasted"));
		dishTags.put("chicken", asList("fried", "crisp"));
		dishTags.put("french fries", asList("greasy", "fried"));
		dishTags.put("rice", asList("light", "natural"));
		dishTags.put("season fruit", asList("fresh", "natural"));
		dishTags.put("pizza", asList("tasty", "salty"));
		dishTags.put("prawns", asList("tasty", "roasted"));
		dishTags.put("salmon", asList("delicious", "fresh"));
	}

	private final String name;
	private final boolean vegetarian;
	private final int calories;
	private final Type type;

	public Dish(String name, boolean vegetarian, int calories, Type type) {
		this.name = name;
		this.vegetarian = vegetarian;
		this.calories = calories;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public boolean isVegetarian() {
		return vegetarian;
	}

	public int getCalories() {
		return calories;
	}

	public Type getType() {
		return type;
	}

	public enum Type {MEAT, FISH, OTHER}

	public Dish getDish(Dish dish) {
		return new Dish(dish.getName(), dish.isVegetarian(),
				this.getCalories() + dish.getCalories(), dish.getType());
	}

	@Override
	public String toString() {
		return "Dish{" +
				"name='" + name + '\'' +
				", vegetarian=" + vegetarian +
				", calories=" + calories +
				", type=" + type +
				'}';
	}

}
