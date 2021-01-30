Java 中的 PriorityQueue

# TopK 问题

```java 
import java.util.PriorityQueue;

/**
 * 面试要是出这种题目，就偷着乐吧。
 */
public class TopK{
    
    private final int k;
    
    private final PriorityQueue<Integer> queue;
    
    TopK(int k,int[] data){
        this.queue = new PriorityQueue<>();
        this.k = k;
        initQueue(data);
    }
    
    private void initQueue(int[] data){
     	for(int tmp : data){
            if(queue.size() < k){
                queue.add(tmp);
            }else{
                int minValue = queue.peek();
            	if(minValue < tmp){
                    queue.poll();
                    queue.add(tmp);
                }
            }
        }
    }
    
    int[] getTopK(){
        int[] result = new int[k];
        int index = 0;
        while(!queue.isEmpty()){
            result[index++] = queue.poll();
        }
        return result;
    }
    
	public static void main(String[] args) {
		int[] data = new int[] {51,5,1,55,61,61,166,122,123123,233,2133};
		int k = 5;
		TopK topK = new TopK(k, data);
		int[] arr = topK.getTopK();
		System.out.println(Arrays.toString(arr));
	}
    
}
```







# 中位数问题

维护一个大顶堆，和一个小顶堆。

```java
package com.liujun.datastruct.base.datastruct.heap.solution.midnum;

import java.util.PriorityQueue;

public class MidNumCount {

  /** 求中位数的信息 */
  public static final MidNumCount INSTANCE = new MidNumCount();

  /** 大顶堆，用来存储前半部分的数据，如果完整为100，那此存储的为0-50 */
  private PriorityQueue<Integer> firstBigHeap =
      new PriorityQueue<>(
          51,
          (o1, o2) -> {
            if (o1 < o2) {
              return 1;
            } else if (o1 > o2) {
              return -1;
            }
            return 0;
          });

  /** 小顶堆,用来存储后半部分的数据，如果完整为100,那此存储的为51-100 */
  private PriorityQueue<Integer> afterLittleHeap = new PriorityQueue<>(51);

  /** 元素的个数 */
  private int count;

  /**
   * 插入数据
   *
   * @param num 当前动态的数据集
   */
  public void putNum(int num) {

    count++;

    // 1,如果堆为空，则插入大顶堆中
    if (firstBigHeap.isEmpty() && afterLittleHeap.isEmpty()) {
      firstBigHeap.offer(num);
      return;
    }

    // 如果数据当前元素比大顶堆中的元素大，则插入小顶堆中
    // 如果元素的数据比大顶堆中的元素小，则插入大顶堆中
    if (firstBigHeap.peek() < num) {
      afterLittleHeap.offer(num);
    }else {
      firstBigHeap.offer(num);
    }

    int countValue = count / 2;

    // 如果大顶堆中的数据超过了中位数，则需要要移动,
    // 因为大顶堆中存储的数据为n/2+1个当n为奇数的情况下，所以每次取数，仅返回大顶堆中的数据即可
    if (firstBigHeap.size() > countValue) {
      move(firstBigHeap, afterLittleHeap, afterLittleHeap.size() - countValue);
      return;
    }
    // 如果小顶堆中的数据超过了中位数，也需要移动
    if (afterLittleHeap.size() > countValue) {
      move(afterLittleHeap, firstBigHeap, afterLittleHeap.size() - countValue);
      return;
    }
  }

  /**
   * 返回中位数的数据
   *
   * @return
   */
  public int getMidValue() {
    return firstBigHeap.peek();
  }

  /**
   * 从一个堆向另一个堆中移动元素
   *
   * @param src
   * @param out
   */
  private void move(PriorityQueue<Integer> src, PriorityQueue<Integer> out, int runNum) {
    for (int i = 0; i < runNum; i++) {
      out.offer(src.poll());
    }
  }
}
```



# 优先级队列

## 合并有序小文件

## 高性能定时器

