package me.young1lin.algorithm.sort;

/**
 * 暂时还是搞不明白
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/11/2 3:37 下午
 */
public class QuickSort extends AbstractSort {

    @Override
    public void sort(int[] arr) {
        doSort(arr, 0, arr.length - 1);
    }

    private void doSort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        int p = partition(arr, left, right);
        doSort(arr, left, p - 1);
        doSort(arr, p, right);
    }

    /**
     * 获取随机区间值
     * @param arr The array to be sorted
     * @param left The first index of an array
     * @param right The last index of an array
     * @return the partition index of the array
     */
    private int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left;
        for (int j = left; j < right - 1; i++) {
            if (arr[j] < pivot) {
                swap(arr, i, j);
                i = i + 1;
            }
        }
        swap(arr,i,right);
        return i;
    }

    public static void main(String[] args) {
        new QuickSort().print();
    }
}
