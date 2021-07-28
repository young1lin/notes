# 二分查找

二分查找，又叫折半查找，支持在有序数组中快速查找元素。

经典的二分解题

非递归

```java
public int binarySearh(int[] a, int n, int value) {
    int low = 0;
    int high = n - 1;
    while (low <= high) {
        int mid = (low + hight) / 2;
        if (a[mid] == value) {
            return mid;
        }
        else if (a[mid] < value) {
        	low = mid + 1;    
        }
        else {
            high = mid - 1;
        }
    }
    return -1;
}
```

递归

```java
public int binarySearch(int[] a, int n, int value) {
    return doSearch(a, 0, n - 1, value);
}
 
public int doSearch(int[] a, int low, int high, int value) {
    if (low > high) {
        return -1;
    }
    int mid = (low + high) / 2;
    if (a[mid] == value) {
        return mid;
    }
    else if (a[mid] < value) {
        return doSearch(a, mid + 1, high, value);
    }
    else {
        return doSearch(a, low, mid - 1, value);
    }
}
```

## 正确的二分查找解题思路

1. 查找区间永远是闭区间[low, high]
2. 循环条件永远是：low<= high
3. 对于 low == high 的情况，必要的时候特殊处理，在 while 内部补充退出条件。
4. 返回值永远是 mid，而不要是 low、high
5. low、high 的更新永远是 low = mid + 1 和 high - 1
6. 对于**非确定性**查找，使用前后探测法，来确定搜索区间
7. 先处理命中情况，再处理再左右半部分查找的情况。

非确定性查找

- 第一个、最后一个相等的
- 第一个大于等于的、最后一个小于等于的
- 循环数组寻找最小值
- 寻找峰值

## 1. 查找第一个等于 x、最后一个等于 x 的元素

第一个等于 X

```java
public int binarySearch(int[] a, int n, int target) {
    int low = 0;
    int high = n - 1;
    while (low <= high) {
        // 这样求中位数数组就不会越界，避免越界
        int mid = low + (high - low) / 2;
        if (a[mid] == target) {
            if ((mid == 0) || (a[mid - 1] != target)) {
                return mid;
            }
            else {
                high = mid -1;
            }
        }
        else if (a[mid] > target) {
            high = mid - 1;
        }
        else {
            low = mid + 1;
        }
    }
    return -1;
}
```

最后一个等于 x 的元素

```java
public int binarySearch(int[] a, int n, int target) {
    int low = 0;
    int high = n - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
    	if (a[mid] == target) {
            if ((mid == n - 1) || (a[mid + 1] != target)) {
                return mid;
            }
        }
        else if (a[mid] > target){
            high = mid - 1;
        }
        else {
            low = mid + 1;
        }
    }
    return -1;
}
```



## 2. 查找第一个大于等于 X，最后一个小于等于 X 的数

第一个大于等于 X 的

```java
public int binarySearch(int[] a, int n, int target) {
    int low = 0;
    int high = n - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (a[mid] >= target){
            if ((mid == 0) || (a[mid - 1] < target)) {
                return mid
            }
            else {
                high = mid - 1;
            }
        }
        else {
            low = mid + 1;
        }
    }
    return -1;
}
```

第一个小于等于的 X 的

```java
public int binarySearch(int[] a, int n, int target) {
    int low = 0;
    int high = n - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (a[mid] <= target) {
            if ((mid == n - 1) || a[mid + 1] > target) {
                return mid;
            }
            else {
                low = mid + 1;
            }
        }
        else {
            high = mid - 1;
        }
    }
    return -1;
}
```

## 循环有序数组（没有重复数据）

例如

1、2、3、4、5、6、7、8、9、10 是有序的

7、8、9、10、1、2、3、4、5、6 是循环有序的

```java
public int binarySearch(int[] a, int n, int target) {
    int low = 0;
    int high = n - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
		if (a[mid] == target) {
            return mid;
        }
        // 证明左边有序，例如 7、8、9、10、1、2、3
        // 要找 8，第一次 low == 0，a[low] == 7， a[mid] == 9
        else if (a[low] <= a[mid]) {
            // 满足这个条件，就要往左靠
            if (target >= a[low] && target < a[mid]) {
                high = mid - 1;
            }// 如果不满足，就要往右靠
            else {
            	low = mid + 1;    
            }
        }// 如果 a[low] > a[mid] 证明左边是无序的。
        else {
            // 如果 target 大于 a[mid] 且小于 a[high]，证明 mid - high 是有序的，且在其中，就往右靠
            if (target > a[mid] && target <= a[high]) {
                low = mid + 1;
            }
            else {
                // 不是则往左靠
                high = mid - 1;
            }
        }
    }
    return -1;
}
```

## 循环有序数组找最小值

```java
public int binarySearch(int[] a, int n) {
    int low = 0;
    int high = n - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        // 特殊处理一下
        if (low == high) {
            return mid;
        }
        if ((mid != 0 && a[mid] < a[mid - 1]) || (mid == 0 && a[mid] < a[high])) {
            return mid;
        }
        else if (a[mid] > a[high]) {
            low = mid + 1;
        }
        else {
            high  = mid - 1;
        }
    }
    // 永远到不了 -1
    return -1;
}
```

## [查找峰值](https://leetcode-cn.com/problems/peak-index-in-a-mountain-array/)

符合下列属性的数组 arr 称为 山脉数组 ：
arr.length >= 3
存在 i（0 < i < arr.length - 1）使得：
arr[0] < arr[1] < ... arr[i-1] < arr[i]
arr[i] > arr[i+1] > ... > arr[arr.length - 1]
给你由整数组成的山脉数组 arr ，返回任何满足 arr[0] < arr[1] < ... arr[i - 1] < arr[i] > arr[i + 1] > ... > arr[arr.length - 1] 的下标 i 。

 

示例 1：

```
输入：arr = [0,1,0]
输出：1
```

示例 2：

```
输入：arr = [0,2,1,0]
输出：1
```

示例 3：

```
输入：arr = [0,10,5,2]
输出：1
```

示例 4：

```
输入：arr = [3,4,5,1]
输出：2
```


示例 5：

```
输入：arr = [24,69,100,99,79,78,67,36,26,19]
输出：2
```


提示：

3 <= arr.length <= 104
0 <= arr[i] <= 106
题目数据保证 arr 是一个山脉数组

```java
class Solution {

    public int peakIndexInMountainArray(int[] arr) {
        int n = arr.length;
		int low = 0;
        int high = n - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            // 先把边界条件考虑完
            if (mid == 0) {
                low = mid + 1;
            }
            else if (mid == n - 1) {
                high = mid - 1;
            }
            else if ((arr[mid] > arr[mid + 1]) && (arr[mid - 1] < arr[mid])){
                return mid;
            }
            else if (arr[mid] > arr[mid - 1]) {
                low = mid + 1;
            }
            else {
            	high = mid - 1;
            }
        }
        return -1;
    }
    
}
```

## [X 的平方根](https://leetcode-cn.com/problems/sqrtx/)

实现 int sqrt(int x) 函数。

计算并返回 x 的平方根，其中 x 是非负整数。

由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。

示例 1:

```
输入: 4
输出: 2
```

示例 2:

```
输入: 8
输出: 2
说明: 8 的平方根是 2.82842..., 
     由于返回类型是整数，小数部分将被舍去。
```

```java
class Solution {
	// 超越 100% 用户
    public int mySqrt(int x) {
        // 1 - x
        if (x == 0) {
            return 0;
        }
        int low = 1;
        int high = x/2 + 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            long midSqrt = (long) mid * mid;
            if (midSqrt == x) {
                return mid;
            }
            else if (midSqrt < x) {
                long mid2 = (long)(mid + 1) * (mid + 1);
                if (mid2 <= x) {
                    low = mid + 1;
                }
                else {
                    return mid;
                }
            }
            else {
                high = mid - 1;
            }
        }
        return -1;
    }

}
```

[704. 二分查找](https://leetcode-cn.com/problems/binary-search/)（简单） 标准二分查找

[374. 猜数字大小](https://leetcode-cn.com/problems/guess-number-higher-or-lower/)（简单）

[744. 寻找比目标字母大的最小字母](https://leetcode-cn.com/problems/find-smallest-letter-greater-than-target/)（简单）

[35. 搜索插入位置](https://leetcode-cn.com/problems/search-insert-position/)（简单）

[34. 在排序数组中查找元素的第一个和最后一个位置](https://leetcode-cn.com/problems/find-first-and-last-position-of-element-in-sorted-array/) （中等）

[面试题 10.05. 稀疏数组搜索](https://leetcode-cn.com/problems/sparse-array-search-lcci/)（简单） 

[33. 搜索旋转排序数组](https://leetcode-cn.com/problems/search-in-rotated-sorted-array/)（中等）无重复数据 

[153. 寻找旋转排序数组中的最小值](https://leetcode-cn.com/problems/find-minimum-in-rotated-sorted-array/)（中等） 无重复数据

[852. 山脉数组的峰顶索引](https://leetcode-cn.com/problems/peak-index-in-a-mountain-array/)（简单）峰值二分

[162. 寻找峰值](https://leetcode-cn.com/problems/find-peak-element/)（中等）峰值二分

[367. 有效的完全平方数](https://leetcode-cn.com/problems/valid-perfect-square/)（简单）二分答案

[69. x 的平方根](https://leetcode-cn.com/problems/sqrtx/)（简单）二分答案

[74. 搜索二维矩阵](https://leetcode-cn.com/problems/search-a-2d-matrix/)（中等） 二维转一维，二分查找



以下为选做：

[658. 找到 K 个最接近的元素](https://leetcode-cn.com/problems/find-k-closest-elements/)（中等）

[875. 爱吃香蕉的珂珂](https://leetcode-cn.com/problems/koko-eating-bananas/)（中等）二分答案

[81. 搜索旋转排序数组 II](https://leetcode-cn.com/problems/search-in-rotated-sorted-array-ii/)（中等）有重复数据

[154. 寻找旋转排序数组中的最小值 II](https://leetcode-cn.com/problems/find-minimum-in-rotated-sorted-array-ii/) （困难） 有重复数据



## [704. 二分查找](https://leetcode-cn.com/problems/binary-search/)（简单） 标准二分查找

给定一个 n 个元素有序的（升序）整型数组 nums 和一个目标值 target  ，写一个函数搜索 nums 中的 target，如果目标值存在返回下标，否则返回 -1。

示例 1:

```
输入: nums = [-1,0,3,5,9,12], target = 9
输出: 4
解释: 9 出现在 nums 中并且下标为 4
```


示例 2:

```
输入: nums = [-1,0,3,5,9,12], target = 2
输出: -1
解释: 2 不存在 nums 中因此返回 -1
```

 



## [374. 猜数字大小](https://leetcode-cn.com/problems/guess-number-higher-or-lower/)（简单）



## [744. 寻找比目标字母大的最小字母](https://leetcode-cn.com/problems/find-smallest-letter-greater-than-target/)（简单）



## [35. 搜索插入位置](https://leetcode-cn.com/problems/search-insert-position/)（简单）



## [34. 在排序数组中查找元素的第一个和最后一个位置](https://leetcode-cn.com/problems/find-first-and-last-position-of-element-in-sorted-array/) （中等）



## [面试题 10.05. 稀疏数组搜索](https://leetcode-cn.com/problems/sparse-array-search-lcci/)（简单）



## [33. 搜索旋转排序数组](https://leetcode-cn.com/problems/search-in-rotated-sorted-array/)（中等）无重复数据



## [153. 寻找旋转排序数组中的最小值](https://leetcode-cn.com/problems/find-minimum-in-rotated-sorted-array/)（中等） 无重复数据



## [852. 山脉数组的峰顶索引](https://leetcode-cn.com/problems/peak-index-in-a-mountain-array/)（简单）峰值二分



## [162. 寻找峰值](https://leetcode-cn.com/problems/find-peak-element/)（中等）峰值二分



## [367. 有效的完全平方数](https://leetcode-cn.com/problems/valid-perfect-square/)（简单）二分答案



## [69. x 的平方根](https://leetcode-cn.com/problems/sqrtx/)（简单）二分答案



## [74. 搜索二维矩阵](https://leetcode-cn.com/problems/search-a-2d-matrix/)（中等） 二维转一维，二分查找



以下为选做：

## [658. 找到 K 个最接近的元素](https://leetcode-cn.com/problems/find-k-closest-elements/)（中等）



## [875. 爱吃香蕉的珂珂](https://leetcode-cn.com/problems/koko-eating-bananas/)（中等）二分答案



## [81. 搜索旋转排序数组 II](https://leetcode-cn.com/problems/search-in-rotated-sorted-array-ii/)（中等）有重复数据



## [154. 寻找旋转排序数组中的最小值 II](https://leetcode-cn.com/problems/find-minimum-in-rotated-sorted-array-ii/) （困难） 有重复数据



# 哈希表

1. 基础
   1. 由来
   2. 冲突解决
   3. 动态扩容
   4. HashMap/HashSet
2. 扩展
   1. 位图
   2. 布隆过滤器

装载因子 = 数据个数/ slot 的个数。

可以分批扩容，或者和 Redis（**Re**mote **Di**ctionary **S**erver）一样，搞两个 Hash 表，搞渐进式扩容。

BitMap（在大量数据中找是否出现在这个大量数据中），BloomFilter。

位运算妙啊。

# 题目

[两数之和](https://leetcode-cn.com/problems/two-sum/) （简单） 

[15. 三数之和](https://leetcode-cn.com/problems/3sum/)（中等）

[160. 相交链表](https://leetcode-cn.com/problems/intersection-of-two-linked-lists/)（简单） 

[141. 环形链表](https://leetcode-cn.com/problems/linked-list-cycle/)（简单）

[面试题 02.01. 移除重复节点](https://leetcode-cn.com/problems/remove-duplicate-node-lcci/)（中等） 

[面试题 16.02. 单词频率](https://leetcode-cn.com/problems/words-frequency-lcci/) （简单）

[面试题 01.02. 判定是否互为字符重排](https://leetcode-cn.com/problems/check-permutation-lcci/)（简单） 

[剑指 Offer 03. 数组中重复的数字](https://leetcode-cn.com/problems/shu-zu-zhong-zhong-fu-de-shu-zi-lcof/) （简单） 

[242. 有效的字母异位词](https://leetcode-cn.com/problems/valid-anagram/)（简单）

[49. 字母异位词分组](https://leetcode-cn.com/problems/group-anagrams/)（中等）

[136. 只出现一次的数字](https://leetcode-cn.com/problems/single-number/)（简单）

[349. 两个数组的交集](https://leetcode-cn.com/problems/intersection-of-two-arrays/) （简单）

[1122. 数组的相对排序](https://leetcode-cn.com/problems/relative-sort-array/)（中等）

[706. 设计哈希映射](https://leetcode-cn.com/problems/design-hashmap/)（简单）

[146. LRU 缓存机制](https://leetcode-cn.com/problems/lru-cache/) （中等）标准的LRU

[面试题 16.21. 交换和](https://leetcode-cn.com/problems/sum-swap-lcci/)（中等）

## 两数之和

三年前做过了

## 三数之和

O($n^2$) 时间复杂度

```java
class Solution {

    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            map.put(nums[i], i);
        }
        for (int i = 0; i < n; i++) {
            // 不等于 0，且前后两个不相等，因为是排过序的
            if (i != 0 && (nums[i] == nums[i - 1])) {
                continue;
            }
            for (int j = i + 1; j < n; j++) {
                if (j != i + 1 && (nums[j] == nums[j - 1])) {
                    continue;
                }
                int c = -1 * (nums[i] + nums[j]);
                if (!map.containsKey(c)) {
                    continue;
                }
                int k = map.get(c);
                // 保证有序了，保证了 C 不重复的情况
                if (k > j) {
                    List<Integer> resultItem = new ArrayList<>();
                    resultItem.add(nums[i]);
                    resultItem.add(nums[j]);
                    resultItem.add(nums[k]);
                    result.add(resultItem);
                 }
            }
        }
        return result;
    }

}
```

## 移除两个集合中的相同元素

给定 a1 和 a2 两个数组，将 a1 中出现在 a2 中的数字去掉。

e.g.

a1: 4, 6, 3, 2, 1

a2: 3, 5, 7, 2

用 Map 来做就好了



## 相交链表（两年前做过了）

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode p1 = headA;
        ListNode p2 = headB;
        int lenA = 0;
        int lenB = 0;
        while(p1 != null) {
            p1 = p1.next;
            lenA++;
        }
        while(p2 != null) {
            p2 = p2.next;
            lenB++;
        }
        int len = Math.abs(lenA - lenB);
        ListNode pA = headA;
        ListNode pB = headB;
        // 长的链表先走
        // 如果 A 链表比 B 链表长，则 A 先走这么多步
        if(lenA >= lenB) {
            while(len != 0) {
                pA = pA.next;
                len--;
            }
        }
        else {
            while(len != 0) {
                pB = pB.next;
                len--;
            }
        }
        while(pA != null && pB != null && pA != pB) {
            pA = pA.next;
            pB = pB.next;
        }
        if(pA == null || pB == null) {
            return null;
        }
        return pA;
    }

}
```



## 环形链表（两年前做过了）



## 移除重复节点

## 单词频率

## 是否互为字符重排

## 数组中重复的数字

## 有效的字母异位词

### 字母异位词分组

## 只出现一次的数字

## 两个数组的交集

## 数组的相对排序

## 设计哈希映射

## LRU 缓存机制（一年前做过了，比较简单）

运用你所掌握的数据结构，设计和实现一个  LRU (最近最少使用) 缓存机制 。
实现 LRUCache 类：

- LRUCache(int capacity) 以正整数作为容量 capacity 初始化 LRU 缓存
- int get(int key) 如果关键字 key 存在于缓存中，则返回关键字的值，否则返回 -1 。
- void put(int key, int value) 如果关键字已经存在，则变更其数据值；如果关键字不存在，则插入该组「关键字-值」。当缓存容量达到上限时，它应该在写入新数据之前删除最久未使用的数据值，从而为新的数据值留出空间。


进阶：你是否可以在 O(1) 时间复杂度内完成这两种操作？

示例：

```
输入
["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
[[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
输出
[null, null, null, 1, null, -1, null, -1, 3, 4]

解释
LRUCache lRUCache = new LRUCache(2);
lRUCache.put(1, 1); // 缓存是 {1=1}
lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
lRUCache.get(1);    // 返回 1
lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
lRUCache.get(2);    // 返回 -1 (未找到)
lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
lRUCache.get(1);    // 返回 -1 (未找到)
lRUCache.get(3);    // 返回 3
lRUCache.get(4);    // 返回 4
```



```java
class LRUCache {
    
    private class Node {
    	
        public int key;
        
        public int value;
        
        public Node prev;
        
        public Node next;
        
        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
        
    }
    
    private Map<Integer, Node> hashtable = new HashMap<>();
    
    private int size;
    
    private int capacity;
    
    private Node head;
    
    private Node tail;
    

    public LRUCache(int capacity) {
		this.size = 0;
        this.capacity = capacity;
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);
        this.head.prev = null;
        this.head.next = tail;
       	this.tail.prev = head;
        this.tail.next = null;
    }
    
    public int get(int key) {
		 if (size == 0) {
             return -1;
         }
        Node node = hashtable.get(key);
        if (node == null) {
            return -1;
        }
        removeNode(node);
        addNodeAtHead(node);
        return node.value;
    }
    
    private void removeNode(Node node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }
    
    private void addNodeAtHead(Node node) {
        node.next = head.next;
        head.next.prev = node;
        // 这里是两个步骤，一个是 head.next 和 node 的链接过程，一个是 head 和 node 的链接过程
        head.next = node;
        node.prev = head;
    }
        
    public void put(int key, int value) {
		Node node = hashtable.get(key);
        if (node != null)  {
            node.value = value;
        	removeNode(node);
            addNodeAtHead(node);
            return;
        }
        if (size == capacity) {
            hashtable.remove(tail.prev.key);
            removeNode(tail.prev);
            size--;
        }
        Node newNode = new Node(key, value);
        addNodeAtHead(newNode);
        hashtable.put(key, newNode);
        size++;
    }
    
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
```



## 交换和

