package me.young1lin.simple;

/**
 * 剑指 offer 61
 *
 * 给 5 个扑克牌，判断是否是顺子
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/17 下午11:30
 * @version 1.0
 */
public class StraightPoke {

	public boolean isStraight(int[] nums) {
		boolean[] dup = new boolean[14];
		int min = 100;
		int max = -1;
		for (int i = 0; i < 5; i++) {
			if (nums[i] != 0) {
				if (dup[nums[i]]) {
					return false;
				}
				else {
					dup[nums[i]] = true;
				}
				if (nums[i] < min) {
					min = nums[i];
				}
				if (nums[i] > max) {
					max = nums[i];
				}
			}
		}
		return (max - min) < 5;
	}

	public static void main(String[] args) {
		StraightPoke poke = new StraightPoke();
		int[] numArr = new int[]{1,3,4,2,5};
		System.out.println(poke.isStraight(numArr));
		int[] numArr1 = new int[]{0,3,4,2,5};
		System.out.println(poke.isStraight(numArr1));
		int[] numArr2 = new int[]{0,0,7,2,5};
		System.out.println(poke.isStraight(numArr2));
	}

}
