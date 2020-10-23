package me.young1lin.algorithm.sort;

import java.util.Arrays;
import java.util.Random;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 4:12 下午
 */
public interface Sort {

    /**
     * 对传入的数组进行排序
     *
     * @param arr 待排序的数组
     */
    void sort(int[] arr);

    /**
     * 获得对应的 arr
     *
     * @return 获得对应的 arr
     */
    int[] getArr();

    /**
     * 打印当前排序前的数组，以及排序后的数组
     */
    void print();

    /**
     * 打印数组信息
     *
     * @param arr 待输出数组
     */
    default void printArr(int[] arr) {
        System.out.println(this.getClass().getSimpleName() + ":" + Arrays.toString(arr));
    }

    /**
     * 提供默认比较并交换的方法
     *
     * @param arr 待排序的数组
     * @param i   左边
     * @param j   右边
     */
    default void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    class IntArrayGenerator {

        static final int DEFAULT_ARRAY_LENGTH = 1 << 4; // aka 16

        /**
         * @return 8 个随机数的 int 类型数组
         */
        public static int[] generator() {
            return generator(DEFAULT_ARRAY_LENGTH);
        }

        /**
         * 给定数组长度，生成指定数组
         *
         * @param arrLength 数组长度
         * @return 生成指定长度数组
         */
        public static int[] generator(int arrLength) {
            int[] arr = new int[arrLength];
            for (int i = 0; i < arrLength; i++) {
                // 扰动值
                arr[i] = new Random().nextInt((2 + i) * i ^ 97);
            }
            return arr;
        }
    }
}

