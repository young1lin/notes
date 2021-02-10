package me.young1lin.algorithm.sort;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 9:04 下午
 */
public class SelectionSort extends AbstractSort {

	@Override
	public void doSort(int[] arr) {
		checkLength(arr);
		for (int i = 0; i < arr.length; i++) {
			int min = i;
			for (int j = i + 1; j < arr.length; j++) {
				// 遍历，找到最小值的索引
				if (arr[j] < arr[min]) {
					min = j;
				}
			}
			// 如果 i 和最小值索引不一样，就要换个边
			if (i != min) {
				swap(arr, i, min);
			}
		}
	}

	public static void main(String[] args) {
		new SelectionSort().print();
	}

}
