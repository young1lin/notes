package me.young1lin.xzg.algorithm.week03;

import java.util.Arrays;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/18 上午6:22
 * @version 1.0
 */
public class MergeSort implements Sort {

	@Override
	public void sort(int[] arr) {
		doSort(arr, 0, arr.length - 1);
	}

	private void doSort(int[] arr, int bottom, int upper) {
		if (bottom >= upper) {
			return;
		}
		int mid = (upper + bottom) / 2;
		doSort(arr, bottom, mid);
		doSort(arr, mid + 1, upper);
		merge(arr, bottom, mid, upper);
	}

	private void merge(int[] arr, int bottom, int mid, int upper) {
		int i = bottom;
		int j = mid + 1;
		int k = 0;
		int[] tmp = new int[upper - bottom + 1];
		while (i <= mid && j <= upper) {
			if (arr[i] <= arr[j]) {
				tmp[k++] = arr[i++];
			}
			else {
				tmp[k++] = arr[j++];
			}
		}
		while (i <= mid) {
			tmp[k++] = arr[i++];
		}
		while (j <= upper) {
			tmp[k++] = arr[j++];
		}
		// 将临时数组的内容，拷贝回去
		for (i = 0; i <= upper - bottom; i++) {
			arr[bottom + i] = tmp[i];
		}
	}

	public static void main(String[] args) {
		int[] arr = {123, 112, 133, 443, 415, 64, 194, 411};
		Sort sort = new MergeSort();
		System.out.println(Arrays.toString(arr));
		sort.sort(arr);
		System.out.println(Arrays.toString(arr));
	}

}
