package me.young1lin.xzg.algorithm.week01;

/**
 * 编写一种算法，若M × N矩阵中某个元素为0，则将其所在的行与列清零。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：
 * [
 *   [1,1,1],
 *   [1,0,1],
 *   [1,1,1]
 * ]
 * 输出：
 * [
 *   [1,0,1],
 *   [0,0,0],
 *   [1,0,1]
 * ]
 * 示例 2：
 *
 * 输入：
 * [
 *   [0,1,2,0],
 *   [3,4,5,2],
 *   [1,3,1,5]
 * ]
 * 输出：
 * [
 *   [0,0,0,0],
 *   [0,4,5,0],
 *   [0,3,1,0]
 * ]
 *
 * 纯编程题，需要多举例
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/24 下午11:53
 * @version 1.0
 */
public class SetZeros {

	public void setZeroes(int[][] matrix) {
		int n = matrix.length;
		if(n == 0){
			return;
		}
		int m = matrix[0].length;
		// 标记哪一行，哪一列需要清零
		boolean[] zeroRows = new boolean[n];
		boolean[] zeroColumns = new boolean[m];
		for(int i = 0;i < n;++i){
			for(int j = 0; j < m; j++){
				if(matrix[i][j] == 0){
					zeroRows[i] = true;
					zeroColumns[j] = true;
				}
			}
		}
		// 这一步就是变为 0 的操作
		for(int i = 0;i < n;i++){
			for(int j = 0;j < m; j++){
				if(zeroRows[i] || zeroColumns[j]){
					matrix[i][j] = 0;
				}
			}
		}
	}

}
