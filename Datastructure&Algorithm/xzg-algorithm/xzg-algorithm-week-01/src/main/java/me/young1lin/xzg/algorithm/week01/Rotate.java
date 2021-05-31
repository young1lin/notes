package me.young1lin.xzg.algorithm.week01;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 给定一个 n × n 的二维矩阵 matrix 表示一个图像。请你将图像顺时针旋转 90 度。
 *
 * 你必须在 原地 旋转图像，这意味着你需要直接修改输入的二维矩阵。请不要 使用另一个矩阵来旋转图像。
 *
 *  
 *
 * 示例 1：
 *
 *
 * 输入：matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * 输出：[[7,4,1],[8,5,2],[9,6,3]]
 * 示例 2：
 *
 *
 * 输入：matrix = [[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]
 * 输出：[[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]
 * 示例 3：
 *
 * 输入：matrix = [[1]]
 * 输出：[[1]]
 * 示例 4：
 *
 * 输入：matrix = [[1,2],[3,4]]
 * 输出：[[3,1],[4,2]]
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/31 下午10:47
 * @version 1.0
 */
public class Rotate {

	public void rotate(int[][] matrix) {
		// 90 degrees 翻转，可以拆成两步
		// 一、上下翻转
		// 二、对角翻转
		int n = matrix.length;
		// 先上下翻转
		for (int i = 0; i < n / 2; i++) {
			for (int j = 0; j < n; j++) {
				swap(matrix, i, j, n - i - 1, j);
			}
		}
		// 对角翻转
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				swap(matrix, i, j, j, i);
			}
		}
	}

	private void swap(int[][] matrix, int i, int j, int p, int q) {
		int tmp = matrix[i][j];
		matrix[i][j] = matrix[p][q];
		matrix[p][q] = tmp;
	}

	public static void main(String[] args) {
		int[][][] testArr = new int[][][] {
				{
						{1, 2, 3},
						{4, 5, 6},
						{7, 8, 9}
				},
				{
						{5, 1, 9, 11},
						{2, 4, 8, 10},
						{13, 3, 6, 7},
						{15, 14, 12, 16}
				},
				{
						{1, 2},
						{3, 4},
				},
		};
		Rotate rotate = new Rotate();
		Stream.of(testArr).forEach(arrS -> {
					rotate.rotate(arrS);
					for (int[] arr : arrS) {
						System.out.println(Arrays.toString(arr));
					}
					System.out.println("-----------");
				}
		);
	}

}
