package me.young1lin.algorithm.search;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/2 4:54 下午
 */
public class BinarySearch extends AbstractSearch {

    public BinarySearch() {
        super();
    }

    public BinarySearch(int searchValue) {
        super(searchValue);
    }

    /**
     * @param arr         待搜索数组
     * @param searchValue 待搜索数字
     * @see BinarySearch#search6
     */
    public BinarySearch(int[] arr, int searchValue) {
        super(arr, searchValue);
    }

    @Override
    public int find(int[] arr, int value) {
        // return search1(arr,value);
        // return search2(arr,value,0,arr.length);
        // return search3(arr, value);
        // return search4(arr, value);
        // return search5(arr, value);
        return search6(arr, value);
    }

    //================================ part 1 start================================

    /**
     * 有序且数据不重复的数组 + 普通的循环
     *
     * @param arr   待搜寻的数组
     * @param value 要查找的值
     * @return 对应下标
     */
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
    //================================ part 1 end================================

    //================================ part 2 start================================

    /**
     * 有序且数据不重复的数组 + 递归的方式
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
    //================================ part 2 end================================


    //================================ part 3 start================================

    /**
     * @return 有序但有重复数据的数组
     * @see BinarySearch#search3
     * @see BinarySearch#search4
     * @see BinarySearch#search5
     */
    @Override
    public int[] getArr() {
        return new int[]{1, 3, 4, 5, 6, 8, 8, 8, 11, 18};
    }

    /**
     * 有序但重复的数组 + 普通循环 + 查找首个值下标
     * <p>
     * 这个方法是来查找有序的数组，并且有序数组中有重复数字的情况。
     * 例如[1,2,3,4,6,8,8,8,8,10,11]
     * 需要找到首个 8 的下标
     * </p>
     *
     * @param arr   to to searched array
     * @param value search value
     * @return 值的下标 index
     */
    private int search3(int[] arr, int value) {
        int n = arr.length;
        int low = 0;
        int high = n - 1;
        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            if (arr[mid] > value) {
                high = mid - 1;
            } else if (arr[mid] < value) {
                low = mid + 1;
            } else {
                if ((mid == 0) || (arr[mid - 1] != value)) {
                    return mid;
                } else {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }
    //================================ part 3 end================================

    //================================ part 4 start================================

    /**
     * 有序但重复的数组 + 普通循环 + 查找最后一个值下标
     * <p>
     * 这个方法是来查找有序的数组，并且有序数组中有重复数字的情况。
     * 例如[1,2,3,4,6,8,8,8,8,10,11]
     * 需要找到最后一个 8 的下标
     * </p>
     *
     * @param arr   待搜索数组
     * @param value 待搜索值
     * @return 值的下标
     */
    public int search4(int[] arr, int value) {
        int n = arr.length;
        int low = 0;
        int high = n - 1;
        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            if (arr[mid] > value) {
                high = mid - 1;
            } else if (arr[mid] < value) {
                low = mid + 1;
            } else {
                // TODO 应该不是这个判断，需要重新写
                if ((mid == n - 1) || (arr[mid + 1] != value)) {
                    return mid;
                } else {
                    low = mid + 1;
                }
            }
        }
        return -1;
    }
    //================================ part 4 end ================================

    //================================ part 5 start ================================

    /**
     * 有序但重复的数组 + 普通循环 + 查找第一个大于等于给定值的元素
     * <p>
     * 这个方法是来查找有序的数组，并且有序数组中有重复数字的情况。
     * 例如[1,2,3,4,6,8,8,8,8,10,11]
     * 需要找到最后一个 8 的下标
     * </p>
     *
     * @param arr   待搜索数组
     * @param value 第一个大于等于给定值的元素
     * @return 值的下标
     */
    public int search5(int[] arr, int value) {
        int n = arr.length;
        int low = 0;
        int high = n - 1;
        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            if (arr[mid] >= value) {
                if ((mid == 0) || (arr[mid - 1] < value)) {
                    return mid;
                } else {
                    high = mid - 1;
                }
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }

    //================================ part 5 end ================================

    //================================ part 6 start ================================


    /**
     * 有序但重复的数组 + 普通循环 + 查找第一个小于等于给定值的元素
     * <p>
     * 这个方法是来查找有序的数组，并且有序数组中有重复数字的情况。
     * 例如[1,2,3,4,6,8,8,8,8,10,11]
     * 需要找到最后一个 8 的下标
     * </p>
     *
     * @param arr   待搜索数组
     * @param value 第一个大于等于给定值的元素
     * @return 值的下标
     */
    public int search6(int[] arr, int value) {
        int n = arr.length;
        int low = 0;
        int high = n - 1;
        while (low <= high) {
            int mid = low + ((high - low) >> 1);
            if (arr[mid] > value) {
                high = mid - 1;
            } else {
                if ((mid == n - 1) || (arr[mid + 1] > value)) {
                    return mid;
                } else {
                    low = mid + 1;
                }
            }
        }
        return -1;
    }
    //================================ part 6 end ================================


    public static void main(String[] args) {
        // search 1 - 2
        // new BinarySearch().print();
        // search 3-5
        // new BinarySearch(8).print();
        // search 6
        int[] arr = new int[]{3, 5, 6, 8, 9, 10};
        new BinarySearch(arr, 8).print();
    }
}
