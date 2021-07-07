package me.young1lin.xzg.algorithm.week03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/7/8 上午12:09
 * @version 1.0
 */
public class Merge {

	static class Solution {

		public int[][] merge(int[][] intervals) {
			if (intervals.length == 1) {
				return intervals;
			}
			// 先排序
			Arrays.sort(intervals, Comparator.comparingInt(i -> i[0]));
			// 在对已排序的内容进行统计
			List<int[]> result = new ArrayList<>(intervals.length);
			// 用双指针走法
			int curLeft = intervals[0][0];
			int curRight = intervals[0][1];
			for (int[] interval : intervals) {
				// 如果当前的小于右边值，
				if (interval[0] <= curRight) {
					if (interval[1] > curRight) {
						// 那就合并
						curRight = interval[1];
					}
				}
				else {
					// save values;
					result.add(new int[] {curLeft, curRight});
					curLeft = interval[0];
					curRight = interval[1];
				}
			}
			result.add(new int[] {curLeft, curRight});
			// 再对已经排序的内容，遍历已排序的内容，
			// 拿 arr[i][1] 和 arr[i+1][0] 比较，如果前者比后者大于等于，那么就合并
			return result.toArray(new int[result.size()][]);
		}

	}

	public static void main(String[] args) {
		Solution solution = new Solution();
		int[][] intervals = new int[][] {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
		int[][] result = solution.merge(intervals);
	}

}
