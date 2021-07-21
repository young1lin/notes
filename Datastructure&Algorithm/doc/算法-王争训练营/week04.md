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

# 哈希表



