package me.young1lin.algorithm.sort;

/**
 * 快速排序的最坏运行情况是 O(n²)，比如说顺序数列的快排。但它的平摊期望时间是 O(nlogn)，且 O(nlogn)
 * 记号中隐含的常数因子很小，比复杂度稳定等于 O(nlogn) 的归并排序要小很多。所以，对绝大多数顺序性较弱的
 * 随机数列而言，快速排序总是优于归并排序。
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 4:47 下午
 */
public abstract class AbstractSort implements Sort {

	protected int[] arr;


	protected AbstractSort() {
		this(IntArrayGenerator.DEFAULT_ARRAY_LENGTH);
	}

	protected AbstractSort(int arrLength) {
		this.arr = Sort.IntArrayGenerator.generator(arrLength);
	}

	@Override
	public int[] getArr() {
		return arr;
	}

	@Override
	public void print() {
		this.printArr(getArr());
		this.sort(getArr());
		this.printArr(getArr());
	}

	/**
	 * just sort
	 * @param arr 待排序的数组
	 */
	@Override
	public void sort(int[] arr) {
		checkLength(arr);
		doSort(arr);
	}

	/**
	 * 实际排序
	 * @param arr be sorted int array
	 */
	protected abstract void doSort(int[] arr);

	protected void checkLength(int[] arr) {
		if (arr.length < 1) {
			System.err.println("什么都没有，排个🐔8");
			throw new IllegalArgumentException();
		}
	}

}
