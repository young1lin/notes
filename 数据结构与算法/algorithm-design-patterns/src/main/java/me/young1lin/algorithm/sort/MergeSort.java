package me.young1lin.algorithm.sort;

import java.util.Arrays;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/26 7:26 下午
 */
public class MergeSort extends AbstractSort {

    @Override
    public void sort(int[] arr) {
        doSort(arr, 0, arr.length - 1);
    }

    private void doSort(int[] arr, int left, int right) {
        // 递归终止条件
        if (left >= right) {
            return;
        }

        // 找中文数，实现分治思想
        int mid = left + (right - left) / 2;
        // 分治执行递归
        doSort(arr, left, mid);
        doSort(arr, mid + 1, right);
        // 递归逻辑
        merge(arr, left, mid, right);
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int length = right - left + 1;
        int[] temp = new int[length];
        int i = left;
        int j = mid + 1;
        int k = 0;

        while (i <= mid && j <= right) {
            if (arr[i] <= (arr[j])) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = arr[i++];
        }

        while (j <= right) {
            temp[k++] = arr[j++];
        }
        System.arraycopy(temp, 0, arr, left, length);
    }


    public static void main(String[] args) {
        new MergeSort().print();
    }
}
