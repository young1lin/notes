package me.young1lin.xzg.algorithm.week01;

import java.util.stream.Stream;

/**
 * 给定一个非负整数数组 nums ，你最初位于数组的 第一个下标 。
 *
 * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
 *
 * 判断你是否能够到达最后一个下标。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：nums = [2,3,1,1,4]
 * 输出：true
 * 解释：可以先跳 1 步，从下标 0 到达下标 1, 然后再从下标 1 跳 3 步到达最后一个下标。
 * 示例 2：
 *
 * 输入：nums = [3,2,1,0,4]
 * 输出：false
 * 解释：无论怎样，总会到达下标为 3 的位置。但该下标的最大跳跃长度是 0 ， 所以永远不可能到达最后一个下标。
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/31 下午10:19
 * @version 1.0
 */
public class CanJump {

	public boolean canJump(int[] nums) {
		int reachMax = 0;
		for (int i = 0; i < nums.length; i++) {
			if (i > reachMax) {
				return false;
			}
			if (i + nums[i] > reachMax) {
				reachMax = i + nums[i];
			}
			if (reachMax >= nums.length - 1) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		int[][] testArr = new int[][] {
				{2, 3, 1, 1, 4},
				{3, 2, 1, 0, 4},
				{1, 1, 1, 11, 1, 11, 1}
		};
		CanJump canJump = new CanJump();
		Stream.of(testArr).forEach(arr ->
				System.out.println(canJump.canJump(arr)));
	}

}
