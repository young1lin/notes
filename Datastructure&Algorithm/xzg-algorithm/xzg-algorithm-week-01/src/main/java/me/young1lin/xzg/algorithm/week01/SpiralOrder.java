package me.young1lin.xzg.algorithm.week01;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/31 下午11:07
 * @version 1.0
 */
public class SpiralOrder {

	class Solution {

		public List<Integer> spiralOrder(int[][] matrix) {
			int m = matrix.length;
			int n = matrix[0].length;
			List<Integer> result = new ArrayList<>();
			int left = 0;
			int right = n - 1;
			int top = 0;
			int bottom = m - 1;
			while (left <= right && top <= bottom) {
				for (int j = left; j <= right; j++) {
					result.add(matrix[top][j]);
				}
				for (int i = top + 1; i <= bottom; i++) {
					result.add(matrix[i][right]);
				}
				if (top != bottom) {
					for (int j = right - 1; j >= left; j--) {
						result.add(matrix[bottom][j]);
					}
				}
				if (left != right) {
					for (int i = bottom - 1; i > top; i--) {
						result.add(matrix[i][left]);
					}
				}
				left++;
				right--;
				top++;
				bottom--;
			}
			return result;
		}

	}

}
