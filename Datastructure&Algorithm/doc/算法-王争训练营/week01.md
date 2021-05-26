# 纯编程题

## 解体技巧

1. 先忽略掉不容易处理的特殊情况，只考虑正常情况，简化编程。
2. 写代码前先写注释，通过注释让代码模块化，让思路更清晰。
3. 写完代码多举几个特例，来验证代码是否正确。

atoi() 类型题目

# IP 地址解析

给定一个字符串表示的 IP 地址，比如 “123.92.2.34”，判断其是否合法。合法 IP 地址规则如下：

1. 除了空格、数字和 . 之外，不得包含其他字符。
2. IP 地址由四个数字构成，由 . 分隔，每个 . 隔开的数字大小再 0～255 之间。
3. 数字前后可以有空格，但中间不能有空格。比如 “123 . 92 . 2 . 34” 合法，“12 3.9 2.2.34” 非法。

## 解体过程

1. 举例读懂题意，梳理题目要求。
2. 列出测试用例（测试驱动开发）
3. 总结归纳出处理思路（把逻辑中重复部分抽象出来）
4. 第一轮编写代码（写注释，让代码模块化，逻辑更清晰）
5. 使用测试用例验证代码，并完善代码。

```java
// 123.9.2.0 合法
// 223 . 33 . 13 . 33 合法
// 232. 22 1.223.1 非法
// 2ba.23.34.11 非法
// 222.319.2.3 非法
// 232.232.11 非法
// 233. .  33.2 非法
// "" or null 非法
public class IpCheck{
    
    public boolean check(String ip){
    	if(ip == null || ip.trim().length == 0){
            return false;
        }
        // 将 IP 地址以 . 分隔
        String[] ipSegements = ip.split("\\.");
        // 验证是否满足子段个数为 4
        if(ipSegements.length != 4){
            return false;
        }
        for(int i = 0; i < 4; ++i){
            boolean vaild = checkSegement(ipSegements[i]);
            if(!valid) return false;
        }
        return true;
    }
    
    private boolean checkSegement(String ipSegement){
        int n = ipSegment.length();
        int i = 0;
        // 跳过前导空格，例如 ” 123“，把前面 ‘ ’ 的部分跳过，认为它是 ok 的
        // charAt(i) 相当于 ipSegement[i]
        while(i < n & ipSegment.charAt(i) == ' '){
            i++;
        }
        // 如果字符串全是空格
        if(i == n){
            return false;
        }
        // 处理数字（将字符串转换成数字），例如 “123 ”
        int digit = 0;
        while(i < n && ipSegment.charAt(i) != ' '){
            char c = ipSegement.charAt(i);
            // 如果是非字符数字
            if(c < '0' || c > '9'){
                return false;
            }
            // c = '1' -> 1
            digit = digit*10 + (c - '0');
            // "1234" digit = 1,12,123,1234
            if(digit > 255){
                return false;
            }
            i++;
        }
        // 处理后置空格， “123 ” or “12 3”
        while(i < n){
            char c = ipSegement.charAt(i);
            // 后面有非空字符
            if(c != ' '){
                return false;
            }
            i++；
        }
        // 当 i = n 的时候，表示没有后置空格
        return true;
    }
    
}
```

# IP 地址无效化

> 给你一个有效的 IPv4 地址 `address`，返回这个 IP 地址的无效化版本。
>
> 所谓无效化版本，其实就是用 ""[.]" 代替了每个 "."。

示例 1：

```java
输入： address = "1.1.1.1"
输出："1[.]1[.]1[.]1"
```

示例 2:

```java
输入：address = "255.100.50.0"
输出："255[.]100[.]50[.]0"
```

提示：

- 给出的 `address` 是一个有效的 IPv4 地址

Solution1

```java
return address.replace(".", "[.]");
```

不能用。。。

我用 Double.valueOf(); 都不行。

```java
class Solution {
    
	public String neutralizeIpAddress(String address) {
		char[] origin = address.toCharArray();
		int n = origin.length;
		int newN = n + 2 * 3;
		char[] newString = new char[newN];
		int k = 0;
		for (int i = 0; i < n; i++) {
			if (origin[i] != '.') {
				newString[k] = origin[i];
				k++;
			}
			else {
				newString[k++] = '[';
				newString[k++] = '.';
				newString[k++] = ']';
			}
		}
		return new String(newString);
	}
    
}
```

方法二

```java
public class Solution {
    
    public String neutralizeIpAddress(String address){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < address.length(); ++i){
            char c = address.charAt(i);
            if(c != '.'){
                sb.append(c);
            }else{
                sb.append("[.]");
            }
        }
        return sb.toString();
    }
    
}
```

# 找规律题

和纯编程题相反，难在找规律，编程实现简单。

找规律题也叫逻辑题，主要考察智力，有点类似智力题，但比智力题要简单、要友好。只要找到了规律，编程实现一般比较简单。比如 90 度翻转二维矩阵，顺时针打印二维矩阵。

如何准备？

没有固定套路和细分题型。整体不会太难，规律都不会难想。

考察的是智力，需要长期锻炼，很难突击。 

不建议深入，顺带刷一刷就行。

## 解体技巧

举例总结规律，猜想举例验证。

通过多个例子，总结出规律。

## 例：

现有 x 瓶啤酒，每 3 个空瓶子换一瓶啤酒，每 7 个瓶盖子也可以换一瓶啤酒，最后可以喝多少瓶啤酒。

给定一个确定 x 值，那这就是一道小学数学题。如果 x 值很小，很容易求出，x 很大，需要逻辑足够清晰才能给出正确答案。

喝了 count = x;

空瓶 K1 = x ->k1-3 >= 3 ? Count++, k1++, k2++

瓶盖 K2 = x -> 类似上面

```java
 public int drink(int x){
     // 有多少瓶啤酒
     int count = x;
     // 空瓶子
     int k1 = x;
     // 瓶盖
     int k2 = x;
     while(k1 >= 3 || k2 >= 7){
         while(k1 >= 3){
             int change = k1/3;
             count += change;
             k1 %= 3;
             k1 += change;
             k2 += change;
         }
         while(k2 >= 7){
             int change = k2/7;
             count += change;
             k2 %= 7;
             k1 += change;
             k2 += change;
         }
     }
     return count;
 }
```

## 例2: 剑指 Offer 61 扑克牌的顺子

从扑克牌中随机抽 5 张牌，判断是不是一个顺子，即这 5 张牌是不是连续的。2～10 为数字本身， A 为 1，J为 11，Q 为 12，K为13，二大小王为 0，可以堪称任意数字。A 不能视为 14。

示例1：

```java
输入：[1,2,3,4,5]
输出：true
```

首先，有重复的数字，就有可能不是顺子。

如果最大值减最小值大于 4，就不是顺子，小于等于 4 就是顺子。

```java
class Solution {
    
    public boolean isStraight(int[] nums){
        boolean[] dup = new boolean[14];
        int min = 100;
        int max = -1;
        for(int i = 0; i < 5; i++){
            if(nums[i] != 0){
                if(dup[nums[i]]){
                    return false;
                }else{
                    dup[nums[i]] = true;
                }
                if(nums[i] < min){
                    min = nums[i];
                }
                if(nums[i] > max){
                    max = nums[i];
                }
            }
        }
        return (max - min) < 5;
    }
    
}
```

# 纯编程练习

[两数之和](https://leetcode-cn.com/problems/two-sum/)（简单） 两层for循环，或者用Map解决，或排序+双指针

[1108. IP 地址无效化](https://leetcode-cn.com/problems/defanging-an-ip-address/)（简单） 简单字符串替换

[344. 反转字符串](https://leetcode-cn.com/problems/reverse-string/)（简单）真的就是反转字符串

[剑指 Offer 58 - I. 翻转单词顺序](https://leetcode-cn.com/problems/fan-zhuan-dan-ci-shun-xu-lcof/)（简单） 反转两次

[125. 验证回文串](https://leetcode-cn.com/problems/valid-palindrome/) （简单） 比普通的验证回文串稍微复杂一点点

[9. 回文数](https://leetcode-cn.com/problems/palindrome-number/)（简单）需要先将数字转化成字符串数组

[58. 最后一个单词的长度](https://leetcode-cn.com/problems/length-of-last-word/)（简单） 从后往前扫描更简单

[剑指 Offer 05. 替换空格](https://leetcode-cn.com/problems/ti-huan-kong-ge-lcof/)（简单） 字符串中元素替换，减少数组元素的搬移

[剑指 Offer 58 - II. 左旋转字符串](https://leetcode-cn.com/problems/zuo-xuan-zhuan-zi-fu-chuan-lcof/)（简单） 纯数组搬移数据

[26. 删除排序数组中的重复项](https://leetcode-cn.com/problems/remove-duplicates-from-sorted-array/)（简单）顺序扫描 下标操作

[剑指 Offer 67. 把字符串转换成整数](https://leetcode-cn.com/problems/ba-zi-fu-chuan-zhuan-huan-cheng-zheng-shu-lcof/)（中等）经典atoi()，注意范围越界处理

# 逻辑题练习

1. [面试题 01.08. 零矩阵](https://leetcode-cn.com/problems/zero-matrix-lcci/) （简单）
2. [面试题 16.11. 跳水板](https://leetcode-cn.com/problems/diving-board-lcci/)（简单）
3. [面试题 01.05. 一次编辑](https://leetcode-cn.com/problems/one-away-lcci/)（中等） 
4. [面试题 16.15. 珠玑妙算](https://leetcode-cn.com/problems/master-mind-lcci/) （简单）
5. [面试题 16.04. 井字游戏](https://leetcode-cn.com/problems/tic-tac-toe-lcci/)（中等）
6. [55. 跳跃游戏](https://leetcode-cn.com/problems/jump-game/) （中等）
7. [48. 旋转图像](https://leetcode-cn.com/problems/rotate-image/) （中等）经典
8. [54. 螺旋矩阵](https://leetcode-cn.com/problems/spiral-matrix/)（中等）经典
9. [240. 搜索二维矩阵 II](https://leetcode-cn.com/problems/search-a-2d-matrix-ii/) （中等）经典



1. 两数之和

```java
class Solution {
    
	public int[] twoSum(int[] nums, int target) {
		if (nums == null || nums.length == 0) {
			return new int[0];
		}
		int[] result = new int[2];
		Map<Integer, Integer> integerMap = new HashMap<>(nums.length);
		for (int i = 0; i < nums.length; i++) {
			integerMap.put(nums[i], i);
		}
		for (int i = 0; i < nums.length; i++) {
			int key = target - nums[i];
			Integer index = integerMap.get(key);
			if(index != null){
				if(i != index) return new int[]{i, index};
			}
		}
		return result;
	}
    
}
// 优化版本
class Solution2 {
    
	public int[] twoSum(int[] nums, int target) {
		if (nums == null || nums.length == 0) {
			return new int[0];
		}
		Map<Integer, Integer> integerMap = new HashMap<>(nums.length);
		for (int i = 0; i < nums.length; i++) {
            int x = nums[i];
            if(map.containsKey(target - x)){
                int index = map.get(target - x);
                return new int[]{i, index};
            }
            map.put(nums[i], i);
        }
		throw new IllegalArgumentException();
	}
    
}
```

2. IP 地址无效化

```java
class Solution {
    
    public String defangIPaddr(String address) { 
        return address.replaceAll("\\.", "[.]");
    }
    
}
class Solution2 {
    
    public String defangIPaddr(String address) {
        char[] strArr = address.toCharArray();
        // 多了 [], IP 地址是 3 个 .
        char[] result = new char[strArr.length + 2*3];
        int k = 0;
        for(int i = 0;i < strArr.length; i++){
            if(strArr[i] == '.'){
                result[k++] = '[';
                result[k++] = '.';
                result[k++] = ']';
            }else{
                result[k++] = strArr[i];
            }
        }
        return new String(result);
    }
    
}
class Solution3 {
    
    public String defangIPaddr(String address) {
    	StringBuilder sb = new StringBuilder();
        char[] strArr = address.toCharArray();
        for(int i = 0;i < strArr.length; i++){
            if(strArr[i] == '.'){
                sb.append("[.]");
            }else{
                sb.append(strArr[i]);
            }
        }
        return sb.toString();
    }
    
}
```

3. 反转字符串

没什么好说的，就是双指针，遍历，交换

```java
class Solution {
    
    public void reverseString(char[] s) {
        int n = s.length;
        for (int left = 0, right = n - 1; left < right; ++left, --right) {
            char tmp = s[left];
            s[left] = s[right];
            s[right] = tmp;
        }
    }
    
}
```

4. 翻转单词顺序

```java
class Solution {
    
    public String reverseWords(String s) {
        s = s.trim();
        int right = s.length() -1 ;
        int left = right;
        StringBuilder res = new StringBuilder();
        while(left >= 0){
            while(left >= 0 && s.charAt(left) != ' ') left--;
            res.append(s.substring(left + 1, right + 1) + " "); // 添加单词
            while(left >= 0 && s.charAt(left) == ' ') left--; // 跳过单词间空格
            right = left; // j 指向下个单词的尾字符
        }
        return res.toString().trim();
    }
    
}

class Solution {
    
    public String reverseWords(String s) {
        String[] strs = s.trim().split(" "); // 删除首尾空格，分割字符串
        StringBuilder res = new StringBuilder();
        for(int i = strs.length - 1; i >= 0; i--) { // 倒序遍历单词列表
            if(strs[i].equals("")) continue; // 遇到空单词则跳过
            res.append(strs[i] + " "); // 将单词拼接至 StringBuilder
        }
        return res.toString().trim(); // 转化为字符串，删除尾部空格，并返回
    }
    
}
```

5. 验证回文串

双指针法，从两端向中间靠拢

```java
public class Solution{
    
    public boolean isPalindrome(String s) {
		// 如果是空字符串，那肯定是回文字符串
		if (s == null || s.trim().length() == 0) {
			return true;
		}
        // 双指针法，一个指针从左走，另一个从右走
        int left = 0;
        int right = s.length() - 1 ;
        char[] arr = s.toCharArray();
        while(left < right){
            // 左边不为字母或数字，直接跳过
            if(!isAlpha(arr[left])){
                left++;
                continue;
            }
            // 左边不为字母或数字，直接跳过
            if(!isAlpha(arr[right])){
                right--;
                continue;
            }
            if(toLower(arr[left]) != toLower(arr[right])){
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    
    private boolean isAlpha(char c){
        if(c >= 'a' && c <= 'z'){
            return true;
        }
        if(c >= 'A' && c <= 'Z'){
            return true;
        }
        if(c >= '0' && c <= '9'){
            return true;
        }
        return false;
    }
    
    private char toLower(char c){
        if(c >= 'a' && c <= 'z'){
            return c;
        }
        if(c >= '0' && c <= '9'){
            return c;
        }
        return ((char)((int)c + 32));
    }

}
```

6. 是否为回文数

可以和上面一样，将其转换为字符串再用双指针，或者用 StringBuilde#reverse 方法，或者 %10 /10 的方法

```java
class Solution {
    
    public boolean isPalindrome(int x) {
        // 负数肯定不是
    	if(x < 0){
            return false;
        }
        // 小于 10 的都是回文数字
        if(x < 10){
           return true; 
        }
        // 备份一下，假设 x 是 4554
        int backup = x;
        int reverse = 0;
        while(x != 0){
            if(x < 10){
                // 第一次就是 4
                reverse += x%10;
            }else{
                reverse = (reverse + x%10)*10;
            }
            // 第一次就是 455，reverse 就是 40
            x /= 10;
        }
     	return backup == reverse;
    }
    
}
```

7. 求最后一个单词的长度

```java
class Solution {
    
    public int lengthOfLastWord(String s) {
        // 首先，这肯定是从后往前遍历好。
        // 其次，后面的可能为空格，所以要判断
        int n = s.length();
        int end = n - 1;
        int count = 0;
        char[] arr = s.toCharArray();
		// 把后面的空字符串剔除
		while (end >= 0 && arr[end] == ' ') {
			end--;
		}
        if (end < 0) {
			return 0;
		}
        int start = end;
		while (start >= 0 && s.charAt(start) != ' ') {
			start--;
		}
        return end - start;
    }
    
}
```

8. 替换空白字符串

```java
class Solution {
    public String replaceSpace(String s) {
		if (s == null) {
			return "";
		}
		char[] arr = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == ' ') {
				sb.append("%20");
			}
			else {
				sb.append(arr[i]);
			}
		}
		return sb.toString();
    }
}
```

9. 左旋字符串

```java

class Solution{
    public String reverseLeftWords(String s, int n) {
		// 这题挺简单的，就是把内容搬运到合适的地方
		if (s == null) {
			return "";
		}
		int len = s.length();
		char[] arr = s.toCharArray();
		char[] leftStr = new char[len - n];
		char[] rightStr = new char[n];
		int k = 0;
		for (int i = n; i < len; i++) {
			leftStr[k++] = arr[i];
		}
		int j = 0;
		for (int i = 0; i < n; i++) {
			rightStr[j++] = arr[i];
		}
		return String.valueOf(leftStr) +
				String.valueOf(rightStr);
    }
}
```

10. 删除一个有序数组的重复项

```java
class Solution {
    public int removeDuplicates(int[] nums) {
        // 重点是有序的数组
		// 双指针走法，一个指针先走，另一个指针比对，如果一样，后走的指针不动。
		// [0,k] 中的元素是不重复的
        int n = nums.length;
           if (n == 0) {
            return 0;
        }
        // 从 1 开始，是因为 0 肯定和自己不重复
        int fast = 1, slow = 1;
        while (fast < n) {
            // 如果快指针的当前元素和快指针当前元素 -1 下标的元素不同，则把不同元素移动，慢指针也移动，否则不做任何操作
            if (nums[fast] != nums[fast - 1]) {
                nums[slow] = nums[fast];
                ++slow;
            }
            ++fast;
        }
        return slow;
    }
}
```

11. 把字符串转成整数

```java
public class Solution {

	public int strToInt(String str) {
		char[] arr = str.toCharArray();
		int n = arr.length;
		// 处理空
		if (n == 0) {
			return 0;
		}
		// 处理前置空格
		int k = 0;
		while (k < n && arr[k] == ' ') {
			k++;
		}
		// 全部为空格符号，就返回 0
		if (k == n) {
			return 0;
		}
		// 处理符号
		int sign = 1;
		char c = arr[k];
		// 负数
		if (c == '-') {
			sign = -1;
			k++;

		}// 正数
		else if (c == '+') {
			k++;
		}
		// 界定范围 -2147483638 - 2147483647
		int intAbsHigh = 214748364;
		int result = 0;
		while (k < n && isNum(arr[k])) {
			// - '0' 就可以转换成数字
			int d = arr[k] - '0';
			// 如果大于这个数了，那么最后一位数字，不管为什么，都是超过范围了
			if (result > intAbsHigh) {
				if (sign == 1) {
					return Integer.MAX_VALUE;
				}
				else {
					return Integer.MIN_VALUE;
				}
			}
			// 如果等于这个数，最后一位如果正数大于 7，那也超过了，负数大于 8 ，也是超过的
			if (result == intAbsHigh) {
				if (sign == 1 && d > 7) {
					return Integer.MAX_VALUE;
				}
				if (sign == -1 && d > 8) {
					return Integer.MIN_VALUE;
				}
			}
			// 正常逻辑
			result = result * 10 + d;
			k++;
		}
		return sign * result;
	}

	private boolean isNum(char c) {
		return c >= '0' && c <= '9';
	}

}
```

12. 零矩阵

```java
class Solution {

    public void setZeroes(int[][] matrix) {
        int n = matrix.length;
        if(n == 0){
            return;
        }
        int m = matrix[0].length;
        // 标记哪一行，哪一列需要清零
        boolean[] zeroRows = new boolean[n];
        boolean[] zeroColumns = new boolean[m];
        for(int i = 0;i < n;++i){
            for(int j = 0; j < m; j++){
                if(matrix[i][j] == 0){
                    zeroRows[i] = true;
                    zeroColumns[j] = true;
                }
            }
        }
        // 这一步就是变为 0 的操作
        for(int i = 0;i < n;i++){
            for(int j = 0;j < m; j++){
                if(zeroRows[i] || zeroColumns[j]){
                    matrix[i][j] = 0;
                }
            }
        }
    }

}
```

13. 跳水板

```java
class Solution {

    public int[] divingBoard(int shorter, int longer, int k) {
        if(k == 0){
            return new int[0];
        }
        // 如果长短一样，那么就只有一种情况
        if(shorter == longer){
            return new int[]{k * longer};
        }
        int[] result = new int[k + 1];
        for(int i = 0; i <= k; i++){
            result[i] = i * longer + (k - i) * shorter;
        }
        return result;
    }

}
```

14. 一次编辑

```java
class Solution{
    
	public boolean oneEditAway(String first, String second) {
		// 相同，则一样
		if (first.equals(second)) {
			return true;
		}
		int n = first.length();
		int m = second.length();
		// 如果两个长度相差大于1，那么就肯定不是一次编辑能过的
		if (Math.abs(n - m) > 1) {
			return false;
		}
		// 在长的字符串中的删除 = 在短的字符串中的新增
		// 长度一样，则是替换
		// 先对两个字符串进行遍历
		int i = 0;
		int j = 0;
		// 找出第一个不相等的元素
		while (i < n && j < m && first.charAt(i) == second.charAt(j)) {
			i++;
			j++;
		}
		// 下面是跳过当前不一样的字符
		// 表示是替换操作
		// 替换 abdf abcf，样例跳过索引为 2 的
		if (n == m) {
			i++;
			j++;
		}
		else if (n > m) {
			i++;
		}
		else {
			j++;
		}
		while (i < n && j < m) {
			// 我上面都已经跳过了，还是不一样，那后面肯定不用比了
			if (first.charAt(i) != second.charAt(j)) {
				return false;
			}
			i++;
			j++;
		}
		return true;
	}
}
```



