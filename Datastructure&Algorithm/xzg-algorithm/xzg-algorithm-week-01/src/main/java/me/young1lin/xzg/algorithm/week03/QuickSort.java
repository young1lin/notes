package me.young1lin.xzg.algorithm.week03;

import java.util.Arrays;

/**
 * Quick sort implement
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/18 上午7:15
 * @version 1.0
 */
public class QuickSort implements Sort {

	@Override
	public void sort(int[] arr) {
		doSort(arr, 0, arr.length - 1);
	}

	private void doSort(int[] arr, int bottom, int top) {
		if (bottom >= top) {
			return;
		}
		int partition = partition(arr, bottom, top);
		doSort(arr, bottom, partition - 1);
		doSort(arr, partition + 1, top);
	}

	private int partition(int[] arr, int bottom, int top) {
		// [bottom, i] 表示小于 pivot 的值
		int i = bottom - 1;
		for (int j = bottom; j < top; j++) {
			if (arr[j] < arr[top]) {
				swap(arr, i + 1, j);
				i++;
			}
		}
		swap(arr, i + 1, top);
		return i + 1;
	}

	public static void main(String[] args) {
		int[] arr = {123, 112, 133, 443, 415, 64, 194, 411};
		Sort sort = new QuickSort();
		System.out.println(Arrays.toString(arr));
		sort.sort(arr);
		System.out.println(Arrays.toString(arr));
	}

}
