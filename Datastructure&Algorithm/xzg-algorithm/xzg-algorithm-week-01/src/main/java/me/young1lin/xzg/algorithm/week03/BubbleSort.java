package me.young1lin.xzg.algorithm.week03;

import java.util.Arrays;

/**
 * BubbleSort
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/17 下午9:09
 * @version 1.0
 */
public class BubbleSort implements Sort {

	@Override
	public void sort(int[] a) {
		int n = a.length;
		if (n <= 1) {
			return;
		}
		for (int i = 0; i < n; i++) {
			// 提前退出冒泡循环位的标志
			boolean flag = false;
			for (int j = 0; j < n - i - 1; j++) {
				if (a[j] > a[j + 1]) {
					int tmp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = tmp;
					flag = true;
				}
			}
			// 没有数据交换，提前退出
			if (!flag) {
				break;
			}
		}
	}

	public static void main(String[] args) {
		int[] arr = {123, 112, 133, 443, 415, 64, 194, 411};
		Sort sort = new BubbleSort();
		System.out.println(Arrays.toString(arr));
		sort.sort(arr);
		System.out.println(Arrays.toString(arr));
	}

}
