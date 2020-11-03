package me.young1lin.algorithm.search;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/2 4:54 下午
 */
public class BinarySearch extends AbstractSearch {

    @Override
    public int find(int[] arr, int value) {
        return search2(arr, value, 0, arr.length);
    }

    private int search1(int[] arr, int value) {
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            // 如果 low + high 过大的话可能导致数值溢出，可以改成 low+(high-low)/2，再优化可以为 low+((high-low)>>1)
            int mid = (low + high) / 2;
            if (arr[mid] == value) {
                return mid;
            } else if (arr[mid] < value) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    /**
     * 递归的方式查找
     *
     * @param arr   待查找的数组
     * @param value 待查找的值
     * @return 查找到的索引
     */
    private int search2(int[] arr, int value, int left, int right) {
        if (right < left) {
            return -1;
        }
        int mid = (left + right) >>> 1;
        if (arr[mid] == value) {
            return mid;
        } else if (arr[mid] < value) {
            return search2(arr, value, mid + 1, right);
        } else {
            return search2(arr, value, left, mid - 1);
        }
    }

    public static void main(String[] args) {
        new BinarySearch().print();
    }
}
