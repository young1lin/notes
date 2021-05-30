# 数组

不讲了，太基础，这是逻辑上的数组，各个语言实现不同。

JavaScript 的“数组”能存不同对象，各个语言实现是不同的。

```javascript
var b = {};
var arr = ["22",b,2,"1"]
```

# 链表

## 遍历

遍历所有结点

```java
public void travese(Node head){
    Node p = head;
    while(p != null){
        p = p.next;
    }
}
```

## 查找

查找结点

```java
public Node find(int value){
    Node p = head;
    while(p != null){
        if(p.data == value){
            return p;
        }
        p = p.next;
    }
}
```

## 插入

### 头部插入

```java
public void insertAtHead(int value){
    Node newNode = new Node(value, null);
    newNode.next = head;
    head = newNode;
}
```

### 尾部插入

没有尾结点的

```java
public void insertAtTail(int value){
    Node newNode = new Node(value, null);
    if(head == null){
        head = newNode;
    }
    else{
        Node p = head;
    	while(p.next  != null){
        	p = p.next;
   	 	}
    	p.next = newNode;
    }   
}
```

有尾结点的

```java
private Node head = null;

private Node tail = null;


public void insertAtTail2(){
    Node newNode = new Node(value, null);
    if(head == null){
        head = newNode;
        tail = newNode;
    }
    else{
        tail.next = newNode;
        tail = newNode;
    }
}
```

 引入虚拟头结点，也就是 DummyNode

```java
private Node head = new Node();

private Node tail = head;

public insertAtTail3(int value){
    Node newNode = new Node(value, null);
    tail.next = newNode;
    tail = newNode;
}
```

### 给定结点插入

```java
public void inseterAfter(Node p, int value){
    if(p == null){
        return;
    }
    Node newNode = new Node(value, null);
    newNode.next = p.next;
    p.next = newNode;
}
```

## 删除

1. 删除给定结点之后的结点
2. 删除给定结点

### 删除给定结点之后的结点

```java
public void deleteNextNode(Node p){
    if(p == null || p.next == null){
        return;
    }
    p.next = p.next.next;
}
```

### 删除给定结点

```java
public void deleteThisNode2(Node head, Node p){
    if(head == null || p == null){
        return;
    }
    Node prev = null;
    Node q = head;
    while(q != null){
        if(q == p){
            break;
        }
        prev = q;
        q = q.next;
    }
	if(q == null){
        return;
    }
    // 前驱结点是空，则删除头结点
    if(prev == null){
        head = head.next;
    }
    else{
        prev.next = prev.next.next;
    }
    return head;
}
```

 # 链表解题

## 技巧

链表遍历三要素

1. 遍历的结束条件：`p == null or p.next == null..`
2. 指针的初始值: `p = head or...`
3. 遍历的核心逻辑: ... 视题目而定

特殊情况处理：是否需要对头结点、尾结点、空链表等做特殊处理？

引入虚拟结点：是否可以通过添加虚拟结点简化编程？

#### 手动实现 LinkedList

实现头插、尾插、根据索引插入、根据索引删除、toString 打印

```java
package me.young1lin.xzg.algorithm.week02;

/**
 * 实现头插、尾插、根据索引插入、根据索引删除、toString 打印
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/26 下午10:23
 * @version 1.0
 */
public class LinkedList<E> {

	private Node<E> head = null;

	private Node<E> tail = null;

	private int size = 0;

	private static class Node<E> {

		E value;

		Node<E> prev;

		Node<E> next;


		Node(E value, Node<E> prev, Node<E> next) {
			this.prev = prev;
			this.next = next;
			this.value = value;
		}

	}

	public LinkedList() {

	}

	public void insertHead(E e) {
		Node<E> p = head;
		Node<E> newNode = new Node<>(e, null, p);
		head = newNode;
		if (p == null) {
			tail = head;
		}
		else {
			p.prev = newNode;
		}
		size++;
	}

	public void insertTail(E e) {
		Node<E> p = tail;
		Node<E> newNode = new Node<>(e, p, null);
		tail = newNode;
		if (p == null) {
			head = newNode;
		}
		else {
			p.next = newNode;
		}
		size++;
	}

	public void insertByIndex(int index, E e) {
		if (index < 0) {
			throw new IllegalArgumentException();
		}
		if (index != 0 && index > size - 1) {
			throw new IndexOutOfBoundsException(String.format("index %s is not allowed ", index));
		}
		Node<E> p = head;
		Node<E> prev = null;
		while (p != null) {
			if (index == 0) {
				break;
			}
			prev = p;
			p = p.next;
			--index;
		}
		Node<E> newNode = new Node<>(e, prev, p);
		if (p == null) {
			head = newNode;
			tail = head;
		}
		else if (prev == null) {
			head.next = head;
			newNode.next = p;
			tail = head;
		}
		else {
			prev.next = newNode;
			p.prev = newNode;
		}
		size++;
	}

//	public E deleteByIndex(int index) {
//		if (index >= size - 1) {
//			throw new IndexOutOfBoundsException(String.format("index %s is not allowed ", index));
//		}
//
//		return E;
//	}


	@Override
	public String toString() {
		Node<E> p = head;
		StringBuilder sb = new StringBuilder();
		while (p != null) {
			sb.append(p.value.toString()).append("\n");
			p = p.next;
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		LinkedList<String> list = new LinkedList<>();
		list.insertHead("111");
		list.insertHead("222");
		list.insertTail("3333");
		list.insertHead("7777");
		list.insertTail("88888");
		list.insertByIndex(1, "111");
		list.insertByIndex(1, "2222");
		System.out.println(list.toString());
	}

}
```

# 练习题

[203. 移除链表元素](https://leetcode-cn.com/problems/remove-linked-list-elements/) （简单）

[876. 链表的中间结点](https://leetcode-cn.com/problems/middle-of-the-linked-list/)（简单）

[83. 删除排序链表中的重复元素](https://leetcode-cn.com/problems/remove-duplicates-from-sorted-list/)（简单）

[剑指 Offer 25. 合并两个排序的链表](https://leetcode-cn.com/problems/he-bing-liang-ge-pai-xu-de-lian-biao-lcof/) （中等）

[2. 两数相加](https://leetcode-cn.com/problems/add-two-numbers/) （中等）

[206. 反转链表](https://leetcode-cn.com/problems/reverse-linked-list/) （中等）

[234. 回文链表](https://leetcode-cn.com/problems/palindrome-linked-list/) （中等）

[328. 奇偶链表](https://leetcode-cn.com/problems/odd-even-linked-list/)（中等）

[25. K 个一组翻转链表](https://leetcode-cn.com/problems/reverse-nodes-in-k-group/)（困难）

[剑指 Offer 22. 链表中倒数第k个节点](https://leetcode-cn.com/problems/lian-biao-zhong-dao-shu-di-kge-jie-dian-lcof/) （简单）

[19. 删除链表的倒数第 N 个结点](https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list/) （中等）

[160. 相交链表](https://leetcode-cn.com/problems/intersection-of-two-linked-lists/)（简单） 

[141. 环形链表](https://leetcode-cn.com/problems/linked-list-cycle/)（简单）

### 移除链表元素

给你一个链表的头结点 head 和一个整数 val ，请你删除链表中所有满足 Node.val == val 的结点，并返回 新的头结点 。

![](https://assets.leetcode.com/uploads/2021/03/06/removelinked-list.jpg)


示例 1：


输入：head = [1,2,6,3,4,5,6], val = 6
输出：[1,2,3,4,5]
示例 2：

输入：head = [], val = 1
输出：[]
示例 3：

输入：head = [7,7,7,7], val = 7
输出：[]


提示：

列表中的结点在范围 [0, 104] 内
1 <= Node.val <= 50
0 <= k <= 50

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

    public ListNode removeElements(ListNode head, int val) {
        ListNode p = head;
		if (p == null) {
			return null;
		}
		while (p.next != null) {
			if (val == p.next.val) {
                p.next = p.next.next;
			}else{
                p = p.next;
            }
		}
        if(head.val == val){
            head = head.next;
        }
		return head;
    }
    
}
```

### 求链表中间结点

给定一个头结点为 head 的非空单链表，返回链表的中间结点。

如果有两个中间结点，则返回第二个中间结点。

 

示例 1：

输入：[1,2,3,4,5]
输出：此列表中的结点 3 (序列化形式：[3,4,5])
返回的结点值为 3 。 (测评系统对该结点序列化表述是 [3,4,5])。
注意，我们返回了一个 ListNode 类型的对象 ans，这样：
ans.val = 3, ans.next.val = 4, ans.next.next.val = 5, 以及 ans.next.next.next = NULL.
示例 2：

输入：[1,2,3,4,5,6]
输出：此列表中的结点 4 (序列化形式：[4,5,6])
由于该列表有两个中间结点，值分别为 3 和 4，我们返回第二个结点。

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

    public ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while(fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

}
```

#### [剑指 Offer 25. 合并两个排序的链表](https://leetcode-cn.com/problems/he-bing-liang-ge-pai-xu-de-lian-biao-lcof/)

难度简单120收藏分享切换为英文接收动态反馈

输入两个递增排序的链表，合并这两个链表并使新链表中的节点仍然是递增排序的。

**示例1：**

```
输入：1->2->4, 1->3->4
输出：1->1->2->3->4->4
```

**限制：**

```
0 <= 链表长度 <= 1000
```



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

    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if(l1 == null){
            return l2;
        }
        if(l2 == null){
            return l1;
        }
        // 两个结点互相比较，如果一个比另一个小，则小的变成 tail
        ListNode p1 = l1;
        ListNode p2 = l2;
        // 引入虚拟结点，因为需要时刻插入尾部。
        ListNode dummyNode = new ListNode();
    	// 声明尾结点
        ListNode tail = dummyNode;
        while(p1 != null && p2 != null){
            if(p1.val <= p2.val){
                tail.next = p1;
                tail = p1;
                p1 = p1.next;
            }else{
                tail.next = p2;
                tail = p2;
                p2 = p2.next;
            }
        }
        // 如果还有一个链表没走完，则进行赋值
        if(p1 != null){
            tail.next = p1;
        }
        if(p2 != null){
            tail.next = p2;
        }
        return dummyNode.next;
    }	

}
```

# 栈

细分题型：

- 直接以栈为背景的题目：比如用栈实现队列、最小栈、栈排序
- 连连消题目：字符串连连消、求表达式。
- 单调栈题目：栈延伸出来的一种新的数据结构。（不多，不怎么考，比较难）

## 题目

[剑指 Offer 09. 用两个栈实现队列](https://leetcode-cn.com/problems/yong-liang-ge-zhan-shi-xian-dui-lie-lcof/)（简单）

[225. 用队列实现栈](https://leetcode-cn.com/problems/implement-stack-using-queues/)（简单）

[面试题 03.05. 栈排序](https://leetcode-cn.com/problems/sort-of-stacks-lcci/)（中等）

[155. 最小栈](https://leetcode-cn.com/problems/min-stack/)（简单）

[面试题 03.01. 三合一](https://leetcode-cn.com/problems/three-in-one-lcci/)（简单） 

[20. 有效的括号](https://leetcode-cn.com/problems/valid-parentheses/)（简单）

[面试题 16.26. 计算器](https://leetcode-cn.com/problems/calculator-lcci/)（中等）

[772. 基本计算器 III](https://leetcode-cn.com/problems/basic-calculator-iii/)（困难 包含括号 力扣会员）

[1047. 删除字符串中的所有相邻重复项](https://leetcode-cn.com/problems/remove-all-adjacent-duplicates-in-string/)（简单）

[剑指 Offer 31. 栈的压入、弹出序列](https://leetcode-cn.com/problems/zhan-de-ya-ru-dan-chu-xu-lie-lcof/)（中等）



**以下选做，在有时间有精力之后再刷**

[739. 每日温度](https://leetcode-cn.com/problems/daily-temperatures/)（中等） 单调栈 

[42. 接雨水](https://leetcode-cn.com/problems/trapping-rain-water/)（困难）单调栈

[84. 柱状图中最大的矩形](https://leetcode-cn.com/problems/largest-rectangle-in-histogram/)（困难）单调栈

[面试题 03.06. 动物收容所](https://leetcode-cn.com/problems/animal-shelter-lcci/)（中等） 队列

[剑指 Offer 59 - II. 队列的最大值](https://leetcode-cn.com/problems/dui-lie-de-zui-da-zhi-lcof/)（中等） 单调队列

[剑指 Offer 59 - I. 滑动窗口的最大值](https://leetcode-cn.com/problems/hua-dong-chuang-kou-de-zui-da-zhi-lcof/) （困难）单调队列

## 用栈实现队列

这其实剑指Offer 也有。

我记得有个博客园博客写得很详细。各种优化

https://www.cnblogs.com/wanghui9072229/archive/2011/11/22/2259391.html

是这个。

```java
package me.young1lin.xzg.algorithm.week02;

import java.util.Stack;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/28 上午12:23
 * @version 1.0
 */
public class StackToQueue {

	private Stack<Integer> stack = new Stack<>();

	private Stack<Integer> tmpStack = new Stack<>();


	public StackToQueue() {

	}

	public void enqueue(Integer data) {
		stack.push(data);
	}

	public Integer dequeue() {
		if (stack.isEmpty()) {
			return null;
		}
		while (!stack.isEmpty()) {
			tmpStack.push(stack.pop());
		}
		Integer result = tmpStack.pop();
		while (!tmpStack.isEmpty()) {
			stack.push(tmpStack.pop());
		}
		return result;
	}

}
```

## 删除连续重复字符

字符串删除掉连续的 3 个重复的字符串。比如 “abbbc”，返回 "ac"; "abbbaac", 返回 c。

注：这里 b 消除了，然后 a 就再一起了，所以返回 c

```java
public class RemoveDuplicateChars {

	static class CharWithCount {

		char c;

		int count;

		public CharWithCount(char c, int count) {
			this.c = c;
			this.count = count;
		}

	}

	public String remove(String str) {
		// 用一个栈就行
		Stack<CharWithCount> stack = new Stack<>();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// 如果栈是空的，也就是第一次，或者已经消完了，则添加元素
			if (stack.isEmpty()) {
				stack.push(new CharWithCount(c, 1));
				continue;
			}
			CharWithCount topChar = stack.peek();
			// 如果连续的字符不相等，则放入
			if (topChar.c != c) {
				stack.push(new CharWithCount(c, 1));
				continue;
			}
			// 如果相等并且连续的字符等于 2，则需要进行消消乐
			// 栈顶元素和 c 一样，并且等于 2 了，那就是第三个了，需要消消乐
			if (topChar.count == 2) {
				stack.pop();
				continue;
			}
			topChar.count++;
		}
		StringBuilder sb = new StringBuilder();
		while (!stack.isEmpty()) {
			// 如果不等于 1，则需要 append 两次，同理如果是 4 个一消，则需要进行 3 次以下的 append
			if (stack.peek().count != 1) {
				sb.append(stack.peek().c);
			}
			sb.append(stack.pop().c);
		}
		return sb.reverse().toString();
	}

}
```

## 计算器/表达式求值（LeetCode 会员题）

面试题 16.26 计算器。面试官也是 Google 的

给定一个包含正整数、加➕、减(-)、乘(*)、除(/)的算数表达式（括号除外），计算其结果。

表达式仅包含非负整数，+, -, *, / 四种运算符**和空格**，整数除法仅保留整数部分。

示例 1:

```
输入：“3+2*2”
输出：7
```
示例 2:

```
输入：“3/2”
输出：1
```

示例 3:

```
输入：“3+5/2”
输出：5
```

两个栈，一个存能放处理的符号，一个放不能处理的。

因为乘除优先于加减。* & / 优先级大于 + -。

栈1 存数字，栈 2 存符号，栈 2 有三种情况，

```java
public class Calculator {

	private static final char BLANK = ' ';


	public int calculate(String s) {
		Stack<Integer> numbers = new Stack<>();
		Stack<Character> ops = new Stack<>();
		int i = 0;
		int n = s.length();
		while (i < n) {
			char c = s.charAt(i);
			// 跳过空格
			if (c == BLANK) {
				i++;

			}// 如果是数字的话
			else if (isDigit(c)) {
				int number = 0;
				while (i < n && isDigit(s.codePointAt(i))) {
					number = number * 10 + (s.charAt(i) - '0');
					i++;
				}
				numbers.push(number);
			}// 运算符
			else {
				if (ops.isEmpty() || !prior(c, ops.peek())) {
					ops.push(c);
				}
				else {
					while (!ops.isEmpty() && prior(c, ops.peek())) {
						fetchAndCal(numbers, ops);
					}
					ops.push(c);
				}
				i++;
			}
		}
		while (!ops.isEmpty()) {
			fetchAndCal(numbers, ops);
		}
		return numbers.pop();
	}

	private boolean prior(char c, Character peek) {
		// 返回优先级，c 是否比 peek 的优先级大，乘除比加减优先级大
		return (c != '*' && c != '/') || (peek != '+' && peek != '-');
	}

	private void fetchAndCal(Stack<Integer> numbers, Stack<Character> ops) {
		int number2 = numbers.pop();
		int number1 = numbers.pop();
		char op = ops.pop();
		int ret = cal(op, number1, number2);
		numbers.push(ret);
	}

	private int cal(char op, int number1, int number2) {
		switch (op) {
			case '+':
				return number1 + number2;
			case '-':
				return number1 - number2;
			case '*':
				return number1 * number2;
			case '/':
				return number1 / number2;
			default:
				return -1;
		}
	}

	public static void main(String[] args) {
		String[] strArr = new String[] {
				"3+2*2",
				"3/2",
				"3+5/2"
		};
		Calculator calculator = new Calculator();
		Stream.of(strArr).forEach(str -> System.out.println(calculator.calculate(str)));
	}

}
```

## 单调栈-每日温度（中等）





```java
// 单调栈处理
class Solution {

    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        Stack<Integer> stack = new Stack<>();
        // 默认会初始化全部为 0
        int[] result = new int[n];
        for(int i = 0; i < n; i++) {
            // 如果栈不为空，且栈顶元素小于当前值，则可以弹出，并且设置栈里的元素的值是 i - idx
            while(!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]){
                int idx = stack.pop();
                result[idx] = i - idx;
            }
            // push 的是下标
            stack.push(i);
        }
        return result;
    }

}
// 笨方法
class Solution1 {

    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        for(int i = 0; i < n; i++) {
            for(int j = i + 1; j < n; j++){
                if(temperatures[i] < temperatures[j]){
                    result[i] = j - i;
                    break;
                }
            }
        }
        return result;
    }

}
```



# 队列
