package me.young1lin.algorithm.sort;

import me.young1lin.algorithm.sort.annotation.Order;

/**
 * 通透了
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/26 7:26 下午
 */
@Order(1)
public class MergeSort extends AbstractSort {

    @Override
    public void doSort(int[] arr) {
        doSort(arr, 0, arr.length - 1);
    }

    /**
     * 第一步是找出递归公式，第二步找出终止条件，第三步推导出代码
     * 1. 这里的递归公式 doSort(p,r) = merge(doSort(p,q),doSort(q+1,r))
     * <p>
     * 2. 终止条件 p>=r
     * <p>
     * 3. 就是下面的代码
     *
     * @param arr   待排序数组
     * @param left  待排序数组最左边
     * @param right 待排序数组最右边
     */
    private void doSort(int[] arr, int left, int right) {
        // 递归终止条件
        if (left >= right) {
            return;
        }

        // 找中位数，实现分治思想
        int mid = left + (right - left) / 2;
        // 分治执行递归
        doSort(arr, left, mid);
        doSort(arr, mid + 1, right);
        // 递归逻辑
        merge(arr, left, mid, right);
    }

    /**
     * @param arr   待合并的数组
     * @param left  最左边索引
     * @param mid   中位数
     * @param right 最右边索引
     */
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
