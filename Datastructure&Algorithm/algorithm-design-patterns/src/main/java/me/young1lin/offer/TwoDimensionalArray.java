package me.young1lin.offer;


/**
 * 在一个二维数组中，每一行都按照从左到右递增的顺序排序，每
 * 一列都按照从上到下递增的顺序排序。请完成一个函数，输入这样的一个
 * 二维数组和一个整数，判断数组中是否含有该整数
 * 下面这样
 * -------------------
 * | 1   2   8   9   |
 * | 2   4   9   12  |
 * | 4   7   10  13  |
 * | 6   8   11  15  |
 * |-----------------|
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/27 下午11:04
 * @version 1.0
 */
public class TwoDimensionalArray {

	private static final int[][] TWO_DIMENSIONAL_ARRAY = new int[][] {
			{1, 2, 8, 9},
			{2, 4, 9, 12},
			{4, 7, 10, 13},
			{6, 8, 11, 15},
	};


	public static void main(String[] args) {
		int k = 7;
		System.out.println(findExists(k));
	}

	static boolean findExists(int k) {
		// 从右上角开始寻找
		int row = 0;
		int col = TWO_DIMENSIONAL_ARRAY[0].length - 1;
		while (row < TWO_DIMENSIONAL_ARRAY.length && col >= 0) {
			if (TWO_DIMENSIONAL_ARRAY[row][col] == k) {
				return true;
			}
			//当前位置下移，因为下面的元素更大
			else if (TWO_DIMENSIONAL_ARRAY[row][col] < k) {
				row++;
			}
			// 当前位置左移，因为左边的元素更小
			else {
				col--;
			}
		}
		return false;
	}

}
