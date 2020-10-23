package me.young1lin.algorithm.sort;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 4:12 下午
 */
public class BubbleSort extends AbstractSort {

    @Override
    public void sort(int[] arr) {
        checkLength(arr);
        // 提前退出冒泡循环的标识位
        boolean flag = false;
        for (int i = 0; i < arr.length; ++i) {
            for (int j = 0; j < arr.length - i - 1; ++j) {
                // 当左边比右边大
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    // 表示有数据交换
                    flag = true;
                }
            }
            // 没有数据交换直接退出
            if (!flag) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        new BubbleSort().print();
    }
}
