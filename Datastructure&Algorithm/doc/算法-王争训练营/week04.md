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

