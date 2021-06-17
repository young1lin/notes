package me.young1lin.xzg.algorithm.week03;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/18 上午6:12
 * @version 1.0
 */
public class SelectionSort implements Sort {

	@Override
	public void sort(int[] arr) {
		int n = arr.length;
		if (n <= 1) {
			return;
		}
		for (int i = 0; i < n - 1; i++) {
			// 未排序区间的第一个下标
			int minPos = i;
			// 从未排序区间选择最小值
			for (int j = i; j < n; j++) {
				// 保证一定是最小的
				if (arr[j] < arr[minPos]) {
					minPos = j;
				}
			}
			// swap
			int tmp = arr[i];
			arr[i] = arr[minPos];
			arr[minPos] = tmp;
		}
	}

}
