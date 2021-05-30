package me.young1lin.xzg.algorithm.week02;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Stream;

/**
 *
 * 请根据每日 气温 列表，重新生成一个列表。对应位置的输出为：要想观测到更高的气温，至少需要等待的天数。如果气温在这之后都不会升高，请在该位置用 0 来代替。
 *
 * 例如，给定一个列表 temperatures = [73, 74, 75, 71, 69, 72, 76, 73]，你的输出应该是 [1, 1, 4, 2, 1, 1,0, 0]。
 * 提示：气温 列表长度的范围是 [1, 30000]。每个气温的值的均为华氏度，都是在 [30, 100] 范围内的整数。
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/30 下午9:52
 * @version 1.0
 */
public class Temperatures {

	/**
	 * 	单调栈处理
	 */
	public int[] dailyTemperatures(int[] temperatures) {
		int n = temperatures.length;
		Stack<Integer> stack = new Stack<>();
		// 默认会初始化全部为 0
		int[] result = new int[n];
		for (int i = 0; i < n; i++) {
			// 如果栈不为空，且栈顶元素小于当前值，则可以弹出，并且设置栈里的元素的值是 i - idx
			while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) {
				int idx = stack.pop();
				result[idx] = i - idx;
			}
			// push 的是下标
			stack.push(i);
		}
		return result;
	}

	/**
	 * 	笨方法
	 */
	public int[] dailyTemperatures1(int[] temperatures) {
		int n = temperatures.length;
		int[] result = new int[n];
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				if (temperatures[i] < temperatures[j]) {
					result[i] = j - i;
					break;
				}
			}
		}
		return result;
	}

	public static void main(String[] args) {
		int[][] temperaturesArr = new int[][] {
				{73, 74, 75, 71, 69, 72, 76, 73},
				{71, 72, 30, 102, 35, 132, 222, 90},
				{89, 88, 76, 58, 81, 111, 122, 280},
				{380, 320, 33, 45, 79}
		};
		Temperatures temperatures = new Temperatures();
		Stream.of(temperaturesArr).forEach(arr ->
				System.out.println(Arrays.toString(temperatures.dailyTemperatures1(arr))));
		System.out.println("--------------");
		Stream.of(temperaturesArr).forEach(arr ->
				System.out.println(Arrays.toString(temperatures.dailyTemperatures(arr))));
	}

}
