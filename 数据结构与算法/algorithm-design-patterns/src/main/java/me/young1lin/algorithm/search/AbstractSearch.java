package me.young1lin.algorithm.search;

import java.util.Arrays;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/3 2:07 下午
 */
public abstract class AbstractSearch implements Search {

    private final int[] arr;

    private final int searchValue;

    public AbstractSearch() {
        this(DEFAULT_SORTED_ARR, DEFAULT_SEARCH_VALUE);
    }

    public AbstractSearch(int[] arr, int searchValue) {
        this.arr = arr;
        this.searchValue = searchValue;
    }

    @Override
    public int[] getArr() {
        return arr;
    }

    @Override
    public abstract int find(int[] arr, int value);

    @Override
    public void print() {
        System.out.println(this.getClass().getSimpleName() + ":\t" + Arrays.toString(getArr()));
        System.out.println(this.getClass().getSimpleName() + ":\t" + find(arr, searchValue));
    }

}
