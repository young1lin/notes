package me.young1lin.xzg.algorithm.week01;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/31 下午11:26
 * @version 1.0
 */
public class SearchMatrix {

	class Solution {

		public boolean searchMatrix(int[][] matrix, int target) {
			// 我怎么觉得在《剑指 Offer》里面做过
			int h = matrix.length;
			int w = matrix[0].length;
			int i = 0;
			int j = w - 1;
			while (i <= h -1 && j >= 0) {
				if (matrix[i][j] == target) {
					return true;
				}
				if (matrix[i][j] > target) {
					j--;
					continue;
				}
				if (matrix[i][j] < target) {
					i++;
				}
			}
			return false;
		}

	}

}
