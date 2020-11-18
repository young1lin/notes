package me.young1lin.algorithm.search;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/3 2:04 下午
 */
public interface Search {

    int[] DEFAULT_UNSORTED_ARR = new int[]{71, 52, 23, 44, 77, 55, 90};

    int[] DEFAULT_SORTED_ARR = new int[]{12, 13, 14, 15, 16, 23, 55, 66, 77, 88, 99};

    int DEFAULT_SEARCH_VALUE = 23;

    /**
     * 查询值具体在哪
     *
     * @param arr   待搜索数组
     * @param value 待搜索值
     * @return 具体索引位置
     */
    int find(int[] arr, int value);

    /**
     * 获得当前搜索的数组
     *
     * @return 当前搜索的数组
     */
    int[] getArr();

    /**
     * 打印搜索信息
     */
    void print();

}
