# 概述

1. 什么是分治？什么是递归？
2. 递归函数与函数调用栈
3. 堆栈溢出、重复计算
4. 如何用递归来解决问题
5. 递归时间、空间复杂度分析

分治：分而治之。

递归：自己调用自己。

栈帧：

1. 参数
2. 局部变量
3. 返回地址

重复计算可以用以下方法解决

1. 备忘录
2. 考虑是否可以用 DP 解决。

当有些全局变量需要定义的时候，我们可以在递归函数外面嵌套一个非递归的壳。

## 怎么发现这个问题可以用递归来做？

- 规模更小的问题，跟规模大点的问题，解决思路相同、仅规模不同。
- 利用子问题的解可以组合得到原问题的解。
- 存在最小子问题，可以直接返回结果（存在递归终止条件）

正确姿势

假设子问题 B、C 已经解决，在此基础上思考如何解决原问题 A。基于此，找出递推公式+终止条件，然后翻译成代码。

不要试图想清楚整个递归执行过程，实际上是进入了一种思维误区。

递归的时间复杂度是指数级别的

斐波那契数列的递归是 O(n)。

# 题目

1. [剑指 Offer 10- I. 斐波那契数列](https://leetcode-cn.com/problems/fei-bo-na-qi-shu-lie-lcof/) （简单）
2. [剑指 Offer 10- II. 青蛙跳台阶问题](https://leetcode-cn.com/problems/qing-wa-tiao-tai-jie-wen-ti-lcof/)（简单）
3. [面试题 08.01. 三步问题](https://leetcode-cn.com/problems/three-steps-problem-lcci/) （简单）
4. [剑指 Offer 06. 从尾到头打印链表](https://leetcode-cn.com/problems/cong-wei-dao-tou-da-yin-lian-biao-lcof/) （简单）
5. [剑指 Offer 24. 反转链表](https://leetcode-cn.com/problems/fan-zhuan-lian-biao-lcof/) （简单）
6. [剑指 Offer 16. 数值的整数次方](https://leetcode-cn.com/problems/shu-zhi-de-zheng-shu-ci-fang-lcof/) （中等）
7. [面试题 08.05. 递归乘法](https://leetcode-cn.com/problems/recursive-mulitply-lcci/) （中等）



# 递归

## 爬楼梯

```java
class Solution {

    public int climbStairs(int n) {
        int[] mem = new int[n+1];
        return helper(n, mem);
    }

    private int helper(int n, int[] mem) {
        if(n == 1) {
            return 1;
        }
        if(n == 2) {
            return 2;
        }
        if(mem[n] != 0) {
            return mem[n];
        }
        mem[n] = helper(n - 1, mem) + helper(n - 2, mem);
        return mem[n];
    }

}
```

## 细胞分裂

1 个细胞的生命周期是 3 小时，1 小时分裂一次。求 N 小时后，容器内有多少细胞（分裂完、死亡完之后的）？

先举例，倒推出递归公式，再识别终止结果。

```java
public int f(int n) {
    if (n == 0) {
        return 1;
    }
    if (n == 1) {
        return 2;
    }
    if (n == 2) {
        return 3;
    }
    if (n == 3) {
        return 8;
    }
	return 2 * f(n - 1) - f(n - 4);
}
```

## 逆序打印链表

用栈，倒腾栈也行。

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
class Solution {

    public int[] reversePrint(ListNode head) {
        List<Integer> list  = new ArrayList<>();
        reverseTravel(head, list);
        int size = list.size();
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private void reverseTravel(ListNode head, List<Integer> list) {
        if (head == null) {
            return;
        }
        reverseTravel(head.next, list);
        list.add(head.val);
    }

}
```

## 斐波那契



# 	排序

1. 排序算法的评价指标
2. 冒泡排序：原理、代码实现、性能分析
3. 插入排序：原理、代码实现、性能分析
4. 选择排序：原理、代码实现、性能分析
5. 归并排序：原理、代码实现、性能分析
6. 快速排序：原理、代码实现、性能分析
7. 桶排序：原理、性能分析——实用，海量数据处理。HBase 就是用的桶排序，来用 RowKey 来实现分桶。
8. 计数排序：不讲——没啥用
9. 基数排序：原理、性能分析——排序算法的稳定性

## 如何评价一个排序算法

- 时间复杂度
- 空间复杂度
- 原地
- 稳定性

### 原地

原地/非原地

- 能不能在存储原始数据的数组上，通过 “倒腾” 实现排序？
- 是不是需要申请新的存储空间，来存储待排序数据？

原地排序算法的空间复杂度不一定是 O(1)，空间复杂度为 O(1) 的排序算法肯定是原地排序算法。

快排也是原地的，但是不是 O(1) 的。

### 稳定性

如果待排序的数据中存在值相等的元素，

- 经过稳定排序算法排序后，相等元素之间的原有的先后顺序不变，
- 经过不稳定排序算法排序之后，相等元素之间的原有先后顺序可能会被改变。

 # 冒泡排序

```java
package me.young1lin.xzg.algorithm.week03;

import java.util.Arrays;

/**
 * 冒泡排序
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/17 下午9:09
 * @version 1.0
 */
public class BubbleSort implements Sort {

   @Override
   public void sort(int[] a) {
      int n = a.length;
      if (n <= 1) {
         return;
      }
      for (int i = 0; i < n; i++) {
         // 提前退出冒泡循环位的标志
         boolean flag = false;
         for (int j = 0; j < n - i - 1; j++) {
            if (a[j] > a[j + 1]) {
               int tmp = a[j];
               a[j] = a[j + 1];
               a[j + 1] = tmp;
               flag = true;
            }
         }
         // 没有数据交换，提前退出
         if (!flag) {
            break;
         }
      }
   }

   public static void main(String[] args) {
      int[] arr = {123, 112, 133, 443, 415, 64, 194, 411};
      Sort sort = new BubbleSort();
      System.out.println(Arrays.toString(arr));
      sort.sort(arr);
      System.out.println(Arrays.toString(arr));
   }

}
```

有序度

逆序度

6, 5, 4, 3, 2, 1 有序度是 0，逆序度是 n*(n-1)/2，也就是 15 

1, 2, 3, 4, 5, 6 逆序度是 0，有序度是 n*(n-1)/2，也就是 15 

逆序度 = 满有序度（n*(n-1)/2） - 有序度。

# 插入排序

将数组中的数据分为两个区间：已排序区间和未排序区间。

初始已排序区间只有一个元素，就是数组中的第一个元素。

插入算法的核心思想就是取未排序区间中的元素，在已排序区间中找合适的插入位置将其插入，保证已排序区间的数据一直有序。

重复这个过程，直到未区间中元素为空，算法结束。

时间复杂度是 O(n<sup>2</sup>)，空间复杂度是 O(1)。

```java
package me.young1lin.xzg.algorithm.week03;

import java.util.Arrays;

/**
 * InsertSort
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/17 下午9:44
 * @version 1.0
 */
public class InsertSort implements Sort {

   @Override
   public void sort(int[] arr) {
      int n = arr.length;
      if (n <= 1) {
         return;
      }
      for (int i = 1; i < n; i++) {
         int value = arr[i];
         int j = i - 1;
         for (; j >= 0; --j) {
            // search insert position
            if (arr[j] > value) {
               // move data
               arr[j + 1] = arr[j];
            }
            else {
               break;
            }
         }
         arr[j + 1] = value;
      }
   }

   public static void main(String[] args) {
      int[] arr = {123, 112, 133, 443, 415, 64, 194, 411};
      Sort sort = new InsertSort();
      System.out.println(Arrays.toString(arr));
      sort.sort(arr);
      System.out.println(Arrays.toString(arr));
   }

}
```

# 选择排序

类似插入排序，也将整个数组划分为已排序区间和未排序区间。选择排序算饭每次从未排序区间中，找到最小的元素，将其放到已排序区间的末尾。

不是稳定排序，O(n<sup>2</sup>) 时间

```java
package me.young1lin.xzg.algorithm.week03;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/18 上午6:12
 * @version 1.0
 */
public class SelectionSort implements Sort {

   @Override
   public void sort(int[] arr) {
      int n = arr.length;
      if (n <= 1) {
         return;
      }
      for (int i = 0; i < n - 1; i++) {
         // 未排序区间的第一个下标
         int minPos = i;
         // 从未排序区间选择最小值
         for (int j = i; j < n; j++) {
            // 保证一定是最小的
            if (arr[j] < arr[minPos]) {
               minPos = j;
            }
         }
         // swap
         int tmp = arr[i];
         arr[i] = arr[minPos];
         arr[minPos] = tmp;
      }
   }

}
```

## 归并排序

如果要排序一个数组，先把数组从中间分成前后两部分，然后，对前后两部分分别排序，再将排好序的两部分合并在一起。

递推公式：mergeSort(p, r) = merg(mergeSort(p, q), mergetSort(q+1, r))

q = (p+r)/2 

终止条件：p >= r 不用再继续分解

是稳定排序，复杂度为 O(nlogn)，空间复杂度是 O(n + logn) 也就是 O(n)

```java
package me.young1lin.xzg.algorithm.week03;

import java.util.Arrays;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/18 上午6:22
 * @version 1.0
 */
public class MergeSort implements Sort {

   @Override
   public void sort(int[] arr) {
      doSort(arr, 0, arr.length - 1);
   }

   private void doSort(int[] arr, int bottom, int upper) {
      if (bottom >= upper) {
         return;
      }
      int mid = (upper + bottom) / 2;
      doSort(arr, bottom, mid);
      doSort(arr, mid + 1, upper);
      merge(arr, bottom, mid, upper);
   }

   private void merge(int[] arr, int bottom, int mid, int upper) {
      int i = bottom;
      int j = mid + 1;
      int k = 0;
      int[] tmp = new int[upper - bottom + 1];
      while (i <= mid && j <= upper) {
         if (arr[i] <= arr[j]) {
            tmp[k++] = arr[i++];
         }
         else {
            tmp[k++] = arr[j++];
         }
      }
      while (i <= mid) {
         tmp[k++] = arr[i++];
      }
      while (j <= upper) {
         tmp[k++] = arr[j++];
      }
      // 将临时数组的内容，拷贝回去
      for (i = 0; i <= upper - bottom; i++) {
         arr[bottom + i] = tmp[i];
      }
   }

   public static void main(String[] args) {
      int[] arr = {123, 112, 133, 443, 415, 64, 194, 411};
      Sort sort = new MergeSort();
      System.out.println(Arrays.toString(arr));
      sort.sort(arr);
      System.out.println(Arrays.toString(arr));
   }

}
```

1. 递推公式来分析

2. 用递归树来进行时间复杂度分析。

# 快速排序

pivot

```java
 package me.young1lin.xzg.algorithm.week03;

import java.util.Arrays;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/18 上午7:15
 * @version 1.0
 */
public class QuickSort implements Sort {

	@Override
	public void sort(int[] arr) {
		doSort(arr, 0, arr.length - 1);
	}

	private void doSort(int[] arr, int bottom, int top) {
		if (bottom >= top) {
			return;
		}
		int partition = partition(arr, bottom, top);
		doSort(arr, bottom, partition - 1);
		doSort(arr, partition + 1, top);
	}

	private int partition(int[] arr, int bottom, int top) {
		// [bottom, i] 表示小于 pivot 的值
		int i = bottom - 1;
		for (int j = bottom; j < top; j++) {
			if (arr[j] < arr[top]) {
				swap(arr, i + 1, j);
				i++;
			}
		}
		swap(arr, i + 1, top);
		return i + 1;
	}

	public static void main(String[] args) {
		int[] arr = {123, 112, 133, 443, 415, 64, 194, 411};
		Sort sort = new QuickSort();
		System.out.println(Arrays.toString(arr));
		sort.sort(arr);
		System.out.println(Arrays.toString(arr));
	}

}
```

