package me.young1lin.xzg.algorithm.week07;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 八皇后问题
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/10/28 下午9:57
 * @version 1.0
 */
public class EightQueen {

	List<char[][]> result = new ArrayList<>();

	public List<char[][]> eightQeueue() {
		char[][] board = new char[8][8];
		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 8; ++j) {
				board[i][j] = '*';
			}
		}
		backtrack(0, board);
		return result;
	}

	/**
	 * row 阶段
	 * board：路径，记录已经做出的决策
	 * 可选列表：通过 board 推导出来，没有显式记录
	 */
	public void backtrack(int row, char[][] board) {
		// 结束条件，得到可行解
		if (row == 8) {
			char[][] snapshot = new char[8][8];
			for (int i = 0; i < 8; ++i) {
				System.arraycopy(board[i], 0, snapshot[i], 0, 8);
			}
			result.add(snapshot);
			return;
		}
		// 每一行都有 8 种放法
		for (int col = 0; col < 8; ++col) {
			// 可选列表
			if (isOk(board, row, col)) {
				// 做选择，第 row 行的棋子放到了 col 列
				board[row][col] = 'Q';
				// 考察下一行
				backtrack(row + 1, board);
				// 恢复选择
				board[row][col] = '*';
			}
		}
	}

	private boolean isOk(char[][] board, int row, int col) {
		int n = 8;
		// 检查列表是否有冲突
		for (int i = 0; i < row; i++) {
			if (board[i][col] == 'Q') {
				return false;
			}
		}
		// 检查右上角是否有冲突
		int i = row - 1;
		int j = col + 1;
		while (i >= 0 && j < n) {
			if (board[i][j] == 'Q') {
				return false;
			}
			i--;
			j++;
		}
		// 检查坐上对角线是否有冲突
		i = row - 1;
		j = col - 1;
		while (i >= 0 && j >= 0) {
			if (board[i][j] == 'Q') {
				return false;
			}
			i--;
			j--;
		}
		return true;
	}

	public static void main(String[] args) {
		EightQueen eightQueen = new EightQueen();
		List<char[][]> boards = eightQueen.eightQeueue();
		System.out.printf("Solutions' number is [%s] \r\n", boards.size());
		for (char[][] board : boards) {
			for (char[] row : board) {
				System.out.println(Arrays.toString(row));
			}
			System.out.println("=========================");
		}
	}

}
