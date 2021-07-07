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

```java
class Solution {

    private static final int mod = 1000000007;

    private int[] mem;


    public int fib(int n) {
        mem = new int[n+1];
        return helper(n);
    }

    private int helper(int n) {
        if (n == 0 || n == 1){
            return n;
        }
        if (mem[n] != 0) {
            return mem[n];
        }
        mem[n] = (helper(n - 1) + helper(n - 2)) % mod;
        return mem[n];
    }

}
```

## 青蛙跳台阶问题

一只青蛙一次可以跳上1级台阶，也可以跳上2级台阶。求该青蛙跳上一个 n 级的台阶总共有多少种跳法。

答案需要取模 1e9+7（1000000007），如计算初始结果为：1000000008，请返回 1。

示例 1：

```
输入：n = 2
输出：2
```

示例 2：

```
输入：n = 7
输出：21
```

示例 3：

```
输入：n = 0
输出：1
```


提示：

`0 <= n <= 100`

```java
class Solution {

    private Map<Integer, Integer> mem = new HashMap<>();

    private int mod = 1000000007;


    public int numWays(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }        
        if (mem.containsKey(n)) {
            return mem.get(n);
        }
        int ret = (numWays(n - 1) + numWays(n - 2)) % mod;
        mem.put(n, ret);
        return ret;
    }

}
```

## 三步问题

三步问题。有个小孩正在上楼梯，楼梯有n阶台阶，小孩一次可以上1阶、2阶或3阶。实现一种方法，计算小孩有多少种上楼梯的方式。结果可能很大，你需要对结果模1000000007。

示例1:

```
输入：n = 3 
输出：4
说明: 有四种走法
```

示例2:

```
输入：n = 5
输出：13
```


提示:

`n范围在[1, 1000000]之间`

**击败 5% 的用户**。。。。。。O(n) 的时间复杂度，还有 O(n) 的空间复杂度。

```java
class Solution {

    private static final int mod = 1000000007;

    public int waysToStep(int n) {
        int[] mem = new int[n + 1];
        return helper(n, mem);
    }

    private int helper(int n, int[] mem) {
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        if (n == 3) {
            return 4;
        }
        if (mem[n] != 0) {
            return mem[n];
        }
        mem[n] = ((helper(n - 1, mem) + helper(n - 2, mem)) % mod + (helper(n - 3, mem))) % mod;
        return mem[n];
    }

}
```

 用 dp

```java

class Solution {
    
    public int waysToStep(int n) {
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        if (n == 3) {
            return 4;
        }
        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;
        dp[3] = 4;
        for (int i = 4; i <= n; i++) {
            dp[i] = ((dp[i - 1] + dp[i - 2]) % 1000000007 + dp[i - 3]) % 1000000007;
        }
        return dp[n];
    }

}
```

也可以用三个变量来做，因为我们只要最后的值

```java
class Solution {
    
    public int waysToStep(int n) {
        if (n == 1) return 1;
        if (n == 2) return 2;
        if (n == 3) return 4;
        int a = 1;
        int b = 2;
        int c = 4;
        int d = 0;
        for (int i = 4; i <= n; i++) {
            d = ((c + b) % 1000000007 + a) % 1000000007;
            a = b;
            b = c;
            c = d;
        }
        return d;
    }
    
}
```

## [从尾到头打印链表](https://leetcode-cn.com/problems/cong-wei-dao-tou-da-yin-lian-biao-lcof/)

输入一个链表的头节点，从尾到头反过来返回每个节点的值（用数组返回）。

示例 1：

```
输入：head = [1,3,2]
输出：[2,3,1]
```


限制：

`0 <= 链表长度 <= 10000`

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

## [剑指 Offer 24. 反转链表](https://leetcode-cn.com/problems/fan-zhuan-lian-biao-lcof/)

定义一个函数，输入一个链表的头节点，反转该链表并输出反转后链表的头节点。

 

示例:

```
输入: 1->2->3->4->5->NULL
输出: 5->4->3->2->1->NULL
```


限制：

`0 <= 节点个数 <= 5000`

```java
class Solution {
    
    public ListNode reverseList(ListNode head) {
        if (head == null) {
            return null;
        }
        // 特殊情况，只有一个节点的时候
        if (head.next == null) {
            return head;
        }
        ListNode newHead = reverseList(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }
    
}
```

O(n) 时间复杂度

## [剑指 Offer 16. 数值的整数次方](https://leetcode-cn.com/problems/shu-zhi-de-zheng-shu-ci-fang-lcof/)

实现 pow(x, n) ，即计算 x 的 n 次幂函数（即，xn）。不得使用库函数，同时不需要考虑大数问题。

 

示例 1：

```
输入：x = 2.00000, n = 10
输出：1024.00000
```


示例 2：

```
输入：x = 2.10000, n = 3
输出：9.26100
```


示例 3：

```
输入：x = 2.00000, n = -2
输出：0.25000
解释：2-2 = 1/22 = 1/4 = 0.25
```

解题



偶数的时候

$x^n$ = $x^{n/2}$ * $x^{n/2}$

奇数的时候

$x^n$ = $x^{n/2}$ *  $x^{n/2}$ * $x$

递归的公式可以拆成这样，

偶数

f (n) = f ($x^{n/2}$) *  f ($x^{n/2}$)

奇数

f (n) = f ($x^{n/2}$) *  f ($x^{n/2}$) * $x$

因为 n 可能为负数，举一个例子。

$x^{-5}$ = $1/x^5$

当 n 为负数的时候，公式如下

$x^n$ = $1/x^{-n}$  那么可以得出是 1 / rPow(x, -1 * n);

**但是**，这有个特殊样例，n 为 -2147483648 时，把它负负得正，得不到 2147483648，因为整数的最大值是 2147483647。所以，这个需要改进一下，下面有个例子。

$x^{-5}$ = $1/x^6$ * $x$，这样就抵消了一个 $\frac{1}{x}$

所以有下面的 1 /  (rPow(x, -1 * (n + 1)) * x);

```java
class Solution {

    public double myPow(double x, int n) {
        // 大于等于 0 时，都是正常的
        if (n >= 0) {
            return rPow(x, n);
        }
        else {
            // 只有这里比较难理解，但是只要看到上面的数学公式，就知道这里为什么这么做了，有特殊的样例数据，所以必须这么处理
            return 1 / (rPow(x, -1 * (n + 1)) * x );
        }
    }

    private double rPow(double x, int n) {
        // 任何数的 0 次方都是 1
        if (n == 0) {
            return 1;
        }
        // 没有头绪时，先看这，这里比较简单，是第一步推出来的公式，再反推上面的内容
        double halfPow = rPow(x, n / 2);
        // 奇数的时候
        if (n % 2 == 1) {
            return halfPow * halfPow * x;
        }// 偶数的时候
        else {
            return halfPow * halfPow;
        }
    }

}
```



## [面试题 08.05. 递归乘法](https://leetcode-cn.com/problems/recursive-mulitply-lcci/)

递归乘法。 写一个递归函数，不使用 * 运算符， 实现两个正整数的相乘。可以使用加号、减号、位移，但要吝啬一些。

示例1:

```
 输入：A = 1, B = 10
 输出：10
```


示例2:

```
 输入：A = 3, B = 4
 输出：12
```


提示:

保证乘法范围不会溢出

用加法即可，击败 100% 用户，朴实无华的解题。

```java
class Solution {

    public int multiply(int A, int B) {
        if(B == 0)
            return 0;
        return A + multiply(A, B - 1);
    }

}
```

还可以和上面一样。

```java
class Solution {
    
    public int multiply(int A, int B) {
        // A 个 B 相加
        if (A == 1) {
            return B;
        }
        int halfValue = multiply(A / 2, B);
        // 奇数
        if (A % 2 == 1) {
           	return halfValue + halfValue + B;
        }// 偶数
        else {
            return halfValue + halfValue;
        }
    }
    
}
```

优化，例如 5 * 8，可以是 5 个 8相加，或者 8 个 5 相加，用前者这样就可以更快。



# 排序

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

## 冒泡排序

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

## 插入排序

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

## 选择排序

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

## 快速排序

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

原地排序，不是稳定排序，O(nlogn)，平均情况下，空间复杂度是 O(nlogn)

# 例题1

输入一个整数数组，实现一个函数来调整该数组中数字的顺序，使得所有奇数位于数组的前半部分，所有的偶数位于数组的后半部分。

示例：

```
输入：nums = [1,2,3,4]
输出：[1,3,2,4]
注：[3,1,2,4] 也是正确答案之一。
```

```java
class Solution {
    
   /**
    * 双指针玩法
    */
    public int[] exchange(int[] nums) {
        int i = 0;
        int j = nums.length - 1;
        while (i < j) {
            // 奇数
            if (nums[i] % 2 == 1) {
                i++;
                continue;
            }
            // 偶数
            if (nums[j] % 2 == 0 ) {
                j--;
                continue;
            }
            int tmp = nums[i];
            num[i] = nums[j];
            nums[j] = tep;
            i++;
            j--;
        }
        return nums;
    }
    
}
```

## 数组中的第 K 个最大元素

在未排序的数组中找到第 K 个最大的元素。

```java
输入：[3,2,1,5,6,4] 和 k = 2
输出：5
```

```java
class Solution {
    
    public int findKthLargest (int[] nums, int k) {
        if (nums.length < k) return -1;
        return quickSort(nums, 0, nums.length - 1, k);
    }
    
    private int quirkSort(int[] nums, int bottom, int top, int k) {
        if (bottom > top) {
            return -1;
        }
        int pivot = partition(nums, p, r);
        if (pivot - bottom + 1 == k) {
            return nums[pivot];
        }
        else if (pivot - bottom + 1 < k) {
            return quickSort(nums, pivot + 1, top, k - (pivot - bottom + 1));
        }
        else {
            return quickSort(nums, bottom, pivot - 1, k);
        }
    }
    
    private int partition(int[] nums, int bottom, int top) {
        // [bottom, i] 表示小于 pivot 的值
		int i = bottom - 1;
		for (int j = bottom; j < top; j++) {
			if (arr[j] < nums[top]) {
				swap(nums, i + 1, j);
				i++;
			}
		}
        swap(nums, i + 1, top);
		return i + 1;
    }
    
    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
        
}
```

## [链表上的排序](https://leetcode-cn.com/problems/insertion-sort-list/)

对链表进行插入排序。

插入排序的动画演示如上。从第一个元素开始，该链表可以被认为已经部分排序（用黑色表示）。
每次迭代时，从输入数据中移除一个元素（用红色表示），并原地将其插入到已排好序的链表中。

 

插入排序算法：

插入排序是迭代的，每次只移动一个元素，直到所有元素可以形成一个有序的输出列表。
每次迭代中，插入排序只从输入数据中移除一个待排序的元素，找到它在序列中适当的位置，并将其插入。
重复直到所有输入数据插入完为止。

示例 1：

```
输入: 4->2->1->3
输出: 1->2->3->4
```

示例 2：
```
输入: -1->5->3->4->0
输出: -1->0->3->4->5
```

链表遍历三要素，找到开始遍历的地方，找到遍历的逻辑，结束条件。

```java
class Solution {
 	
    public ListNode insertionSortList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode dummyHead = new ListNode(Integer.MIN_VALUE, null);
        ListNode p = head;
        while (p != null) {
            ListNode tmp = p.next;
            // 寻找 p 节点插入的位置，插入到哪个节点后面
            ListNode q = dummyHead;
            while (q.next != null && q.next.val <= p.val) {
                q = q.next;
            }
            // 将 P 节点插入
            p.next = q.next;
            q.next = p;
            p = tmp;
        }
        return dummyHead.next;
    }
    
}
```

## 排序预处理

有一组无序数组，找出出现次数最多的数据。

```java
// 前面应该有个 sort 排序行数
public int maxCount(int[] data) {
    int n = data.length;
    int prev = -1;
    int count = 0;
    int max = -1;
    for (int i = 0; i < n; i++) {
        if (data[i] == prev) {
            count++;
            if (max < count) {
                max = count;
            }
        }
        else {
            count = 1;
            prev = data[i];
            if (max < count) {
                max = count;
            }
        }
        return max;
    }    
}
```

# 题目

1. [面试题 10.01. 合并排序的数组](https://leetcode-cn.com/problems/sorted-merge-lcci/)（简单）
2. [21. 合并两个有序链表](https://leetcode-cn.com/problems/merge-two-sorted-lists/)（简单）
3. [242. 有效的字母异位词](https://leetcode-cn.com/problems/valid-anagram/)（简单）
4. [1502. 判断能否形成等差数列](https://leetcode-cn.com/problems/can-make-arithmetic-progression-from-sequence/)（简单）
5. [252. 会议室](https://leetcode-cn.com/problems/meeting-rooms/)（简单）
6. [56. 合并区间](https://leetcode-cn.com/problems/merge-intervals/)（中等） 
7. [剑指 Offer 21. 调整数组顺序使奇数位于偶数前面](https://leetcode-cn.com/problems/diao-zheng-shu-zu-shun-xu-shi-qi-shu-wei-yu-ou-shu-qian-mian-lcof/) （简单）
8. [75. 颜色分类](https://leetcode-cn.com/problems/sort-colors/)（中等）
9. [147. 对链表进行插入排序](https://leetcode-cn.com/problems/insertion-sort-list/)（中等）
10. [148. 排序链表](https://leetcode-cn.com/problems/sort-list/)（中等） 链表上的归并排序
11. [215. 数组中的第K个最大元素](https://leetcode-cn.com/problems/kth-largest-element-in-an-array/)（中等） 
12. [面试题 17.14. 最小K个数](https://leetcode-cn.com/problems/smallest-k-lcci/)（中等）
13. [剑指 Offer 51. 数组中的逆序对](https://leetcode-cn.com/problems/shu-zu-zhong-de-ni-xu-dui-lcof/)（困难）

# 1. 合并排序的数组

给定两个排序后的数组 A 和 B，其中 A 的末端有足够的缓冲空间容纳 B。 编写一个方法，将 B 合并入 A 并排序。

初始化 A 和 B 的元素数量分别为 m 和 n。

示例:

```
输入:
A = [1,2,3,0,0,0], m = 3
B = [2,5,6],       n = 3

输出: [1,2,2,3,5,6]
```


说明:

A.length == n + m

```java
class Solution {

   public void merge(int[] A, int m, int[] B, int n) {
       // 得到各个区间的末尾下标
        int amount = m + n - 1, i = m - 1, j = n - 1;
        // 因为是已经排序的，所以每次都在各自的数组中，选择最小的，插入到 A 的数组中
        while (i >= 0 && j >= 0) {
            if (A[i] > B[j]) {
                A[amount--] = A[i--];
            } else {
                A[amount--] = B[j--];
            }
        }
        // j 还大于等于 0，表示 n > m，例如 A 有 2 个数，B 有 4 个数，需要对 B 的数值进入 A 末尾
        while (j >= 0) {
            A[amount--] = B[j--];
        }
    }

}
```

# 2. 合并两个有序链表

将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。 

 ![](https://assets.leetcode.com/uploads/2020/10/03/merge_ex1.jpg)

示例 1：

```
输入：l1 = [1,2,4], l2 = [1,3,4]
输出：[1,1,2,3,4,4]
```


示例 2：

```
输入：l1 = [], l2 = []
输出：[]
```


示例 3：

```
输入：l1 = [], l2 = [0]
输出：[0]
```


提示：

两个链表的节点数目范围是 [0, 50]
-100 <= Node.val <= 100
l1 和 l2 均按 非递减顺序 排列

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if(l1 == null && l2 == null){
            return null;
        }
        else if(l1 == null && l2 != null){
            return l2;
        }
        else if(l1 != null && l2 == null){
            return l1;
        }
        ListNode prehead = new ListNode(-1);
        ListNode prev = prehead;
        while (l1 != null && l2 != null) {
            if (l1.val > l2.val) {
                prev.next = l2;
                l2 = l2.next;
            }
            else {
                prev.next = l1;
                l1 = l1.next;
            }
            prev = prev.next;
        }
        // 合并未合并完成的链表
        prev.next = l1 == null ? l2 : l1;
        return prehead.next;
    }

}
```

# 3. [242. 有效的字母异位词](https://leetcode-cn.com/problems/valid-anagram/)（简单）

给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的字母异位词。

示例 1:

```
输入: s = "anagram", t = "nagaram"
输出: true
```

示例 2:

```
输入: s = "rat", t = "car"
输出: false
```

题解

```java
class Solution {

       public boolean isAnagram(String s, String t) {
            if (s.length() != t.length()) {
                return false;
            }
           char[] a = s.toCharArray();
           char[] b = t.toCharArray();
           Arrays.sort(a);
           Arrays.sort(b);
           for (int i = 0; i < a.length; i++) {
               if (a[i] != b[i]) {
                   return false;
               }
           }
           return true;
       }
}
```

# 3. 判断能否形成等差数列

给你一个数字数组 arr 。

如果一个数列中，任意相邻两项的差总等于同一个常数，那么这个数列就称为 等差数列 。

如果可以重新排列数组形成等差数列，请返回 true ；否则，返回 false 。



示例 1：

```
输入：arr = [3,5,1]
输出：true
解释：对数组重新排序得到 [1,3,5] 或者 [5,3,1] ，任意相邻两项的差分别为 2 或 -2 ，可以形成等差数列。
```


示例 2：

```
输入：arr = [1,2,4]
输出：false
解释：无法通过重新排序得到等差数列。
```


提示：

2 <= arr.length <= 1000
-10^6 <= arr[i] <= 10^6

击败 98% 的人！！

```java
class Solution {

    public boolean canMakeArithmeticProgression(int[] arr) {
        if (arr == null || arr.length == 0 || arr.length == 1) {
            return false;
        }
        Arrays.sort(arr);
        int con = arr[1] - arr[0];
        for (int i = 0; i < arr.length - 1; i++) {
            int tmp = arr[i + 1] - arr[i];
            if (tmp != con) {
                return false;
            }
        }
        return true;
    }

}
```

# 4. 会议室(会员题)

给定一个会议时间安排的数组 intervals，每个会议时间都会包括开始和结束时间 intervals[i] = [start<sub>i</sub>, end<sub>i</sub>]，请你判断一个人是否能够参加这里面的全部会议。

示例 1:

```
输入：intervals = [[0,30],[5,10],[15,20]]
输出：false
```

示例 2:

```
输入：intervals = [[7,10],[2,14]]
输出：true
```

可以先排个序，根据第一个时间排序，如果第一个时间就重复了，那么就是不可能的。

```java
class Solution {
    
    public boolean canEnterAllMeeting(int[][] intervals) {
        if (intervals.length == 0) {
            return true;
        }
        int n = intervals.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (intervals[j + 1][0] < intervals[i][0]) {
                    int[] tmp = intervals[j];
                    intervals[j] = intervals[i];
                    intervals[i] = tmp;
                }
            }
        }
        int x = 0;
        while (x < n - 1) {
            if (intervals[x][0] > intervals[x + 1][1]) {
                return false;
            }
            x++;
        }
        return true;
    }
    
}
```

小争哥的

```java
class Solution {
	
    public boolean canAttendMeetings(int[][] intervals) {
    	Arrays.sort(intervals, new Comparator<int[]>(){
            public int compare(int[] i1, int[] i2) {
                // 妙啊
                return i1[0] - i2[0];
            }
        });
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[i - 1][1]) {
                return false;
            }
        }
        return true;
    }
    
}
```



# 5. 合并区间

以数组 intervals 表示若干个区间的集合，其中单个区间为 intervals[i] = [starti, endi] 。请你合并所有重叠的区间，并返回一个不重叠的区间数组，该数组需恰好覆盖输入中的所有区间。

 

示例 1：

```
输入：intervals = [[1,3],[2,6],[8,10],[15,18]]
输出：[[1,6],[8,10],[15,18]]
解释：区间 [1,3] 和 [2,6] 重叠, 将它们合并为 [1,6].
```


示例 2：

```
输入：intervals = [[1,4],[4,5]]
输出：[[1,5]]
解释：区间 [1,4] 和 [4,5] 可被视为重叠区间。
```


提示：

1 <= intervals.length <= 104
intervals[i].length == 2
0 <= starti <= endi <= 104

```java
class Solution {

    public int[][] merge(int[][] intervals) {
        if (intervals.length == 1) {
            return intervals;
        }
        // 先排序
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] i1, int[] i2) {
                // 妙啊
                return i1[0] - i2[0];
            }
        });
        // 在对已排序的内容进行统计
        List<int[]> result = new ArrayList<>(intervals.length);
        // 用双指针走法
        int curLeft = intervals[0][0];
        int curRight = intervals[0][1];
        for (int i = 0; i < intervals.length; i++) {
            // 如果当前的小于右边值，
            if (intervals[i][0] <= curRight) {
                if (intervals[i][1] > curRight) {
                    // 那就合并
                    curRight = intervals[i][1];
                }
            }
            else {
                // save values;
                result.add(new int[]{curLeft, curRight});
                curLeft = intervals[i][0];
                curRight = intervals[i][1];
            }
        }
        result.add(new int[]{curLeft, curRight});
        // 再对已经排序的内容，遍历已排序的内容，
        // 拿 arr[i][1] 和 arr[i+1][0] 比较，如果前者比后者大于等于，那么就合并
        return result.toArray(new int[result.size()][]);
    }

}
```



# [剑指 Offer 21. 调整数组顺序使奇数位于偶数前面](https://leetcode-cn.com/problems/diao-zheng-shu-zu-shun-xu-shi-qi-shu-wei-yu-ou-shu-qian-mian-lcof/)



# 颜色分类



# 对链表进行插入排序



# 排序链表



# 数组中的第K个最大元素



# 最小K个数



# 数组中的逆序对
