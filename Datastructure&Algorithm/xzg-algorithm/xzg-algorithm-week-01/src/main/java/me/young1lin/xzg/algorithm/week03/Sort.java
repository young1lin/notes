package me.young1lin.xzg.algorithm.week03;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @date 2021/6/17 下午9:16
 * @version 1.0
 */
public interface Sort {

	/**
	 * sort
	 *
	 * @param arr the arr be sorted
	 */
	void sort(int[] arr);

	/**
	 * swap arr[i] & arr[j] 's value
	 *
	 * @param arr to be swapped array
	 * @param i i index
	 * @param j j index
	 */
	default void swap(int[] arr, int i, int j) {
		int tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

}
