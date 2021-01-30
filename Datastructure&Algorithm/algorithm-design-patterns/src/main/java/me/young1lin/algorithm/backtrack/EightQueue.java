package me.young1lin.algorithm.backtrack;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/29 下午4:44
 * @version 1.0
 */
public class EightQueue {

	/** 全局或成员变量,下标表示行,值表示queen存储在哪一列 */
	int[] result = new int[8];

	public void cal8queens(int row) { // 调用方式：cal8queens(0);
		// 8个棋子都放置好了，打印结果
		if (row == 8) {
			printQueens(result);
			// 8行棋子都放好了，已经没法再往下递归了，所以就return
			return;
		}
		// 每一行都有8中放法
		for (int column = 0; column < 8; ++column) {
			// 有些放法不满足要求
			if (isOk(row, column)) {
				// 第row行的棋子放到了column列
				result[row] = column;
				// 考察下一行
				cal8queens(row + 1);
			}
		}
	}

	private boolean isOk(int row, int column) {
		//判断row行column列放置是否合适
		int leftup = column - 1, rightup = column + 1;
		// 逐行往上考察每一行
		for (int i = row - 1; i >= 0; --i) {
			// 第i行的column列有棋子吗？
			if (result[i] == column) return false;
			// 考察左上对角线：第i行leftup列有棋子吗？
			if (leftup >= 0) {
				if (result[i] == leftup) return false;
			}
			// 考察右上对角线：第i行rightup列有棋子吗？
			if (rightup < 8) {
				if (result[i] == rightup) return false;
			}
			--leftup; ++rightup;
		}
		return true;
	}

	/**
	 * 	打印出一个二维矩阵
	 */
	private void printQueens(int[] result) {
		for (int row = 0; row < 8; ++row) {
			for (int column = 0; column < 8; ++column) {
				if (result[row] == column) System.out.print("Q ");
				else System.out.print("* ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void main(String[] args) {
		new EightQueue().cal8queens(0);
	}

}
