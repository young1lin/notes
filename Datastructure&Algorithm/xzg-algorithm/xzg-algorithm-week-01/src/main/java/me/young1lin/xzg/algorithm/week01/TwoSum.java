package me.young1lin.xzg.algorithm.week01;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 的那 两个 整数，并返回它们的数组下标。
 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
 *
 * 你可以按任意顺序返回答案。
 *
 *
 * 示例 1：
 * 输入：nums = [2,7,11,15], target = 9
 * 输出：[0,1]
 * 解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。
 *
 * 示例 2：
 * 输入：nums = [3,2,4], target = 6
 * 输出：[1,2]
 *
 * 示例 3：
 * 输入：nums = [3,3], target = 6
 * 输出：[0,1]
 *
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/21 下午8:35
 * @version 1.0
 */
public class TwoSum {

	/**
	 * 两数之和，两数的索引
	 *
	 * @param nums 数组
	 * @param target 两数之和
	 * @return 两数的索引
	 */
	public static int[] twoSum(int[] nums, int target) {
		if (nums == null || nums.length == 0) {
			return new int[0];
		}
		Map<Integer, Integer> integerMap = new HashMap<>(nums.length);
		for (int i = 0; i < nums.length; i++) {
			int x = nums[i];
			if (integerMap.containsKey(target - x)) {
				int index = integerMap.get(target - x);
				return new int[] {index, i};
			}
			integerMap.put(nums[i], i);
		}
		throw new IllegalArgumentException();
	}

	public static void main(String[] args) {
		int[][] arrays = new int[][] {
				{2, 7, 11, 15},
				{3, 2, 4},
				{3, 3}
		};
		int[] targets = new int[] {9, 6, 6};
		for (int i = 0; i < arrays.length; i++) {
			System.out.println(
					Arrays.toString(
							twoSum(arrays[i], targets[i])));
		}

	}

}
