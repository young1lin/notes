package me.young1lin.algorithm.sort;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/22 4:47 下午
 */
public abstract class AbstractSort implements Sort {

    protected int[] arr;


    protected AbstractSort() {
        this(IntArrayGenerator.DEFAULT_ARRAY_LENGTH);
    }

    protected AbstractSort(int arrLength) {
        this.arr = Sort.IntArrayGenerator.generator(arrLength);
    }

    @Override
    public int[] getArr() {
        return arr;
    }

    @Override
    public void print() {
        this.printArr(getArr());
        this.sort(getArr());
        this.printArr(getArr());
    }

    @Override
    public abstract void sort(int[] arr);

    protected void checkLength(int[] arr) {
        if (arr.length < 1) {
            System.err.println("什么都没有，排个🐔8");
            throw new IllegalArgumentException();
        }
    }

}
