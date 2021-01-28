package me.young1lin.algorithm.backtrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/28 下午4:24
 * @version 1.0
 */
public class BackTrackDemo {

	List<List<Integer>> res = new LinkedList<>();

	/**
	 * 主函数，输入一组不重复的数字，返回它们的全排列
	 */
	List<List<Integer>> permute(int[] nums) {
		// 记录「路径」
		LinkedList<Integer> track = new LinkedList<>();
		backtrack(nums, track);
		return res;
	}

	/**
	 * 	路径：记录在 track 中
	 * 	选择列表：nums 中不存在于 track 的那些元素
	 * 	结束条件：nums 中的元素全都在 track 中出现
	 */
	void backtrack(int[] nums, LinkedList<Integer> track) {
		// 触发结束条件
		if (track.size() == nums.length) {
			res.add(new LinkedList<>(track));
			return;
		}

		for (int num : nums) {
			// 排除不合法的选择
			if (track.contains(num))
				continue;
			// 做选择
			track.add(num);
			// 进入下一层决策树
			backtrack(nums, track);
			// 取消选择
			track.removeLast();
		}
	}

	public List<List<Integer>> permute1(int[] nums) {

		List<List<Integer>> res = new ArrayList<>();
		int[] visited = new int[nums.length];
		backtrack(res, nums, new ArrayList<Integer>(), visited);
		return res;

	}

	private void backtrack(List<List<Integer>> res, int[] nums, ArrayList<Integer> tmp, int[] visited) {
		if (tmp.size() == nums.length) {
			res.add(new ArrayList<>(tmp));
			return;
		}
		for (int i = 0; i < nums.length; i++) {
			if (visited[i] == 1) continue;
			visited[i] = 1;
			tmp.add(nums[i]);
			backtrack(res, nums, tmp, visited);
			visited[i] = 0;
			tmp.remove(tmp.size() - 1);
		}
	}

	public static void main(String[] args) {
		BackTrackDemo btd = new BackTrackDemo();
		int[] arr = new int[] {113, 11, 55};
		btd.permute(arr).forEach(System.out::println);
	}

}
