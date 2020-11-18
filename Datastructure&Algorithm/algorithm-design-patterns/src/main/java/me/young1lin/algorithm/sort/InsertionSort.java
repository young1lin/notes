package me.young1lin.algorithm.sort;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 6:01 下午
 */
public class InsertionSort extends AbstractSort {

    @Override
    public void sort(int[] arr) {
        checkLength(arr);
        for (int i = 0; i < arr.length; i++) {
            int value = arr[i];
            int j = i - 1;
            // 查找插入的位置
            for (; j >= 0; --j) {
                if (arr[j] > value) {
                    // 数据移动
                    arr[j + 1] = arr[j];
                } else {
                    break;
                }
            }
            arr[j + 1] = value;
        }
    }

    public static void main(String[] args) {
        new InsertionSort().print();
    }
}
