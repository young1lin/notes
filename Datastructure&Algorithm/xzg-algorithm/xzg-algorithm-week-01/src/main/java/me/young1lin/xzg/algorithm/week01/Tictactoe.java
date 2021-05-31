package me.young1lin.xzg.algorithm.week01;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/31 下午9:22
 * @version 1.0
 */
public class Tictactoe {

	class Solution {

		private static final char BLANK = ' ';

		public String tictactoe(String[] board) {
			int n = board.length;
			char[][] boards = new char[n][n];
			for (int i = 0; i < n; i++) {
				boards[i] = board[i].toCharArray();
			}
			// 表示是否已经发现有人赢了
			boolean determined = false;
			// 检查行
			for (int i = 0; i < n; i++) {
				if (boards[i][0] == BLANK) {
					continue;
				}
				determined = true;
				for (int j = 1; j < n; j++) {
					if (boards[i][j] != boards[i][0]) {
						determined = false;
						break;
					}
				}
				if (determined) {
					return "" + boards[i][0];
				}
			}
			// 检查列
			for (int j = 0; j < n; j++) {
				if (boards[0][j] == BLANK) {
					continue;
				}
				determined = true;
				for (int i = 1; i < n; i++) {
					if (boards[i][j] != boards[0][j]) {
						determined = false;
						break;
					}
				}
				if (determined) {
					return "" + boards[0][j];
				}
			}
			// 检查对角线，左上 -> 右下
			if(boards[0][0] != BLANK){
				int i = 1;
				int j = 1;
				determined = true;
				while (i < n && j < n) {
					if (boards[i][j] != boards[0][0]) {
						determined = false;
						break;
					}
					i++;
					j++;
				}
				if (determined) {
					return boards[0][0] + "";
				}
			}
			// 检查对角
			if (boards[n - 1][0] != BLANK) {
				int i = n - 2;
				int j = 1;
				determined = true;
				while (i >= 0 && j < n) {
					if (boards[i][j] != boards[n - 1][0]) {
						determined = false;
						break;
					}
					i--;
					j++;
				}
				if (determined) {
					return "" + boards[n - 1][0];
				}
			}
			// 都没赢的话，判定游戏是否还能继续玩
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (boards[i][j] == BLANK) {
						return "Pending";
					}
				}
			}
			// 游戏结束，平局
			return "Draw";
		}

	}

}
