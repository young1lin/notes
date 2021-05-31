package me.young1lin.xzg.algorithm.week01;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 你正在使用一堆木板建造跳水板。有两种类型的木板，其中长度较短的木板长度为shorter，长度较长的木板长度为longer。你必须正好使用k块木板。编写一个方法，生成跳水板所有可能的长度。
 *
 * 返回的长度需要从小到大排列。
 *
 * 示例 1
 *
 * 输入：
 * shorter = 1
 * longer = 2
 * k = 3
 * 输出： [3,4,5,6]
 * 解释：
 * 可以使用 3 次 shorter，得到结果 3；使用 2 次 shorter 和 1 次 longer，得到结果 4 。以此类推，得到最终结果。
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/25 上午12:42
 * @version 1.0
 */
public class DivingBoard {

	public int[] divingBoard(int shorter, int longer, int k) {
		if (k == 0) {
			return new int[0];
		}
		// 如果长短一样，那么就只有一种情况
		if (shorter == longer) {
			return new int[] {k * longer};
		}
		int[] result = new int[k + 1];
		for (int i = 0; i <= k; i++) {
			result[i] = i * longer + (k - i) * shorter;
		}
		return result;
	}

	public static void main(String[] args) {
		int[][] testArr = new int[][] {
				{10, 10, 77},
				{1, 2, 3},
				{44, 31, 9}
		};
		DivingBoard divingBoard = new DivingBoard();
		Stream.of(testArr).forEach(arr ->
				System.out.println(
						Arrays.toString(
								divingBoard.divingBoard(arr[0], arr[1], arr[2]))));
	}

}
