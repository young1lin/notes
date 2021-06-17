package me.young1lin.xzg.algorithm.week03;

import java.util.Arrays;

/**
 * InsertSort
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/17 下午9:44
 * @version 1.0
 */
public class InsertSort implements Sort {

	@Override
	public void sort(int[] arr) {
		int n = arr.length;
		if (n <= 1) {
			return;
		}
		for (int i = 1; i < n; i++) {
			int value = arr[i];
			int j = i - 1;
			for (; j >= 0; --j) {
				// search insert position
				if (arr[j] > value) {
					// move data
					arr[j + 1] = arr[j];
				}
				else {
					break;
				}
			}
			arr[j + 1] = value;
		}
	}

	public static void main(String[] args) {
		int[] arr = {123, 112, 133, 443, 415, 64, 194, 411};
		Sort sort = new InsertSort();
		System.out.println(Arrays.toString(arr));
		sort.sort(arr);
		System.out.println(Arrays.toString(arr));
	}

}
