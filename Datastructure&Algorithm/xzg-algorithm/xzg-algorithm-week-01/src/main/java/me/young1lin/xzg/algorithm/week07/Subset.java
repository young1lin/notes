package me.young1lin.xzg.algorithm.week07;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 78 题目
 *
 * @link https://leetcode-cn.com/problems/subsets/
 *
 * 输入：nums = [1,2,3]
 * 输出：[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/10/28 下午10:42
 * @version 1.0
 */
public class Subset {

	private final List<List<Integer>> result = new ArrayList<>();


	public List<List<Integer>> subsets(int[] nums) {
		backtrack(nums, 0, new ArrayList<>());
		return result;
	}

	/**
	 * 按理来说私有方法不应该写注释的
	 * @param nums 可选列表，nums[k] 选还是不选
	 * @param k 表示阶段
	 * @param path 表示路径
	 */
	private void backtrack(int[] nums, int k, List<Integer> path) {
		if (k == nums.length) {
			result.add(new ArrayList<>(path));
			return;
		}

		backtrack(nums, k + 1, path);

		path.add(nums[k]);
		backtrack(nums, k + 1, path);
		path.remove(path.size() - 1);
	}

	public static void main(String[] args) {
		int[] nums = {1, 2, 3};
		Subset subset = new Subset();
		List<List<Integer>> subsets = subset.subsets(nums);
		subsets.forEach(System.out::println);
	}

}
