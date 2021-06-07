# 逻辑题练习

1. [面试题 01.08. 零矩阵](https://leetcode-cn.com/problems/zero-matrix-lcci/) （简单）已解决
2. [面试题 16.11. 跳水板](https://leetcode-cn.com/problems/diving-board-lcci/)（简单）已解决
3. [面试题 01.05. 一次编辑](https://leetcode-cn.com/problems/one-away-lcci/)（中等） 已解决
4. [面试题 16.15. 珠玑妙算](https://leetcode-cn.com/problems/master-mind-lcci/) （简单）已解决
5. [面试题 16.04. 井字游戏](https://leetcode-cn.com/problems/tic-tac-toe-lcci/)（中等）已解决
6. [55. 跳跃游戏](https://leetcode-cn.com/problems/jump-game/) （中等）已解决
7. [48. 旋转图像](https://leetcode-cn.com/problems/rotate-image/) （中等）经典 已解决
8. [54. 螺旋矩阵](https://leetcode-cn.com/problems/spiral-matrix/)（中等）经典 已解决
9. [240. 搜索二维矩阵 II](https://leetcode-cn.com/problems/search-a-2d-matrix-ii/) （中等）经典 已解决

# 链表

[234. 回文链表](https://leetcode-cn.com/problems/palindrome-linked-list/) （中等）未解决

[328. 奇偶链表](https://leetcode-cn.com/problems/odd-even-linked-list/)（中等）未解决

[25. K 个一组翻转链表](https://leetcode-cn.com/problems/reverse-nodes-in-k-group/)（困难）未解决

[剑指 Offer 22. 链表中倒数第k个节点](https://leetcode-cn.com/problems/lian-biao-zhong-dao-shu-di-kge-jie-dian-lcof/) （简单）未解决

[19. 删除链表的倒数第 N 个结点](https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list/) （中等）未解决

[160. 相交链表](https://leetcode-cn.com/problems/intersection-of-two-linked-lists/)（简单） 未解决

# 栈

[面试题 03.05. 栈排序](https://leetcode-cn.com/problems/sort-of-stacks-lcci/)（中等）未解决

[155. 最小栈](https://leetcode-cn.com/problems/min-stack/)（简单） 未解决

[面试题 03.01. 三合一](https://leetcode-cn.com/problems/three-in-one-lcci/)（简单）  未解决

[20. 有效的括号](https://leetcode-cn.com/problems/valid-parentheses/)（简单）这个以前做过，简单，消消乐

[面试题 16.26. 计算器](https://leetcode-cn.com/problems/calculator-lcci/)（中等）未解决

[772. 基本计算器 III](https://leetcode-cn.com/problems/basic-calculator-iii/)（困难 包含括号 力扣会员）已解决，需要重点再看看

[1047. 删除字符串中的所有相邻重复项](https://leetcode-cn.com/problems/remove-all-adjacent-duplicates-in-string/)（简单）未解决

[剑指 Offer 31. 栈的压入、弹出序列](https://leetcode-cn.com/problems/zhan-de-ya-ru-dan-chu-xu-lie-lcof/)（中等）未解决

历史已堆积 23 道题目



[面试题 03.05. 栈排序](https://leetcode-cn.com/problems/sort-of-stacks-lcci/)（中等）未解决

[155. 最小栈](https://leetcode-cn.com/problems/min-stack/)（简单） 未解决

[面试题 03.01. 三合一](https://leetcode-cn.com/problems/three-in-one-lcci/)（简单）  未解决



回文链表

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

    public boolean isPalindrome(ListNode head) {
        if(head == null || head.next == null){
            return true;
        }
        ListNode midNode = findMidNode(head);
        ListNode rightHalfHead = reverseList(midNode.next);
        ListNode p = head;
        ListNode q = rightHalfHead;
        while (q != null) {
            if (p.val != q.val) {
                return false;
            }
            p = p.next;
            q = q.next;
        }
        return true;
    }

    private ListNode findMidNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    private ListNode reverseList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode newHead = null;
        ListNode p = head;
        while (p != null) {
            ListNode tmp = p.next;
            p.next = newHead;
            newHead = p;
            p = tmp;
        }
        return newHead;
    }

}
```

奇偶链表

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

    public ListNode oddEvenList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode dummyOddHead = new ListNode(-1);
        ListNode oddTail = dummyOddHead;
        ListNode dummyEvenHead = new ListNode(-1);
        ListNode evenTail = dummyEvenHead;
        ListNode p = head;
        int count = 1;
        while (p != null) {
            ListNode tmp = p.next;
            p.next = null;
            // 奇数
            if(count % 2 == 1){
                oddTail.next = p;
                oddTail = p;
            }// 偶数
            else {
                evenTail.next = p;
                evenTail = p;
            }
            p = tmp;
            count++;
        }
        oddTail.next = dummyEvenHead.next;
        return dummyOddHead.next;
    }

}
```

K 个 一组翻转链表

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

    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummyHead = new ListNode();
        ListNode tail  = dummyHead;
        ListNode p = head;
        while (p != null) {
            int count = 0;
            ListNode q = p;
            while (q != null) {
                count++;
                if (count == k) {
                    break;
                }
                q = q.next;
            }
            if (q == null) {
                tail.next = p;
                return dummyHead.next;
            }
            else {
                ListNode tmp = q.next;
                ListNode[] nodes = reverse(p, q);
                tail.next = nodes[0];
                tail = nodes[1];
                p = tmp;
            }
        }
        return dummyHead.next;
    }
    
    private ListNode[] reverse(ListNode head, ListNode tail) {
        ListNode newHead = null;
        ListNode p = head;
        while (p != tail) {
            ListNode tmp = p.next;
            p.next = newHead;
            newHead = p;
            p = tmp;
        }
        tail.next = newHead;
        newHead = tail;
        return new ListNode[]{tail, head};
    }

}
```



链表中倒数第k个节点（太简单了）

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

    public ListNode getKthFromEnd(ListNode head, int k) {
        ListNode fast = head;
        ListNode slow = fast;
        while(k != 0) {
            fast = fast.next;
            k--;
        }
        while(fast != null) {
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }

}
```

删除链表中倒数第 N 个节点

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

    public ListNode removeNthFromEnd(ListNode head, int n) {
        // 遍历1，fast 先到第 n 个节点处。
        ListNode fast = head;
        int count = 0;
        while(fast != null) {
            count++;
            if(count == n) {
                break;
            }
            fast = fast.next;
        }
        // 不够 n 个
        if(fast == null) {
            return head;
        }
        ListNode slow = head;
        ListNode prev = null;
        while(fast.next != null) {
            fast = fast.next;
            prev = slow;
            slow = slow.next;
        }
        if(prev == null) {
            head = head.next;
        }
        else {
            prev.next = slow.next;
        }
        return head;
    }

}

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

    /**
     * 快慢指针法
     * @param ListNode 链表头节点
     * @param n 倒数第  n  个节点
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        // 引入虚拟节点，作为慢指针起始位置
        ListNode dummy = new ListNode(0, head);
        ListNode first = head;
        ListNode second = dummy;
        for(int i = 0; i < n; i++){
            first = first.next;
        }
        while(first != null){
            first = first.next;
            second = second.next;
        }
        second.next = second.next.next;
        ListNode ans = dummy.next;
        return ans;
    }

}
```

相交链表

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

环形链表（以前做过了，快慢指针，Set）

```java
/**
 * Definition for singly-linked list.
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {


    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        ListNode oneStep = head.next;
        ListNode twoStep = head.next.next;
        while(oneStep != twoStep){
            if(twoStep == null || twoStep.next == null || oneStep == null){
                return false;
            }
            oneStep = oneStep.next;
            twoStep = twoStep.next.next;
        }
		return true;
    }

}
```

用队列实现栈

```java
public interface Stack {
    
    public void push(int x);
    
    public int pop();
    
    public boolean empty();
    
}

public class QueueImplStack implements Stack {
    
    Queue<Integer> queue;
    
    public QueueImplStack() {
        queue = new LinkedList<>();
    }
    
    public void push(int x) {
        queue.offer(x);
    }
    
    public int pop() {
    	int n = queue.size();
        for (int i = 0; i < n-1; i++) {
            queue.offer(queue.poll());
        }
        return queue.poll();
    }
    
    public int top() {
        int n = queue.size();
        for (int i = 0; i < n - 1; i++) {
            queue.offer(queue.poll());
        }
        int ret = queue.poll();
        queue.offer(ret);
    	return ret;
    }
    
    public boolean empty() {
        return queue.size() == 0;
    }
    
}
```

栈排序

```java
class SortedStack {

    private final Stack<Integer> stack = new Stack<>();

    private final Stack<Integer> tmpStack = new Stack<>();


    public SortedStack() {

    }
    
    public void push(int val) {
        stack.push(val);
    }
    
    public void pop() {
        if(stack.isEmpty()) {
            return;
        }
        int minVal = Integer.MAX_VALUE;
        while(!stack.isEmpty()) {
            int val = stack.pop();
            minVal = Math.min(val, minVal);
            tmpStack.push(val);
        }
        boolean removed = false;
        while(!tmpStack.isEmpty()) {
            int val = tmpStack.pop();
            if(val != minVal || (val == minVal && removed == true)) {
                stack.push(val);
            }
            else {
                removed = true;
            }
        }
    }
    
    public int peek() {
        if(stack.isEmpty()) {
            return -1;
        }
        int minVal = Integer.MAX_VALUE;
        while(!stack.isEmpty()) {
            int val = stack.pop();
            minVal = Math.min(val, minVal);
            tmpStack.push(val);
        }
        while(!tmpStack.isEmpty()) {
            stack.push(tmpStack.pop());
        }
        return minVal;
    }
    
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
}

/**
 * Your SortedStack object will be instantiated and called as such:
 * SortedStack obj = new SortedStack();
 * obj.push(val);
 * obj.pop();
 * int param_3 = obj.peek();
 * boolean param_4 = obj.isEmpty();
 */
class SortedStack {

    private final Stack<Integer> stack = new Stack<>();

    private final Stack<Integer> tmpStack = new Stack<>();


    public SortedStack() {

    }
    
    public void push(int val) {
        while(!stack.isEmpty() && stack.peek() < val) {
            tmpStack.push(stack.pop());
        }
        stack.push(val);
        while(!tmpStack.isEmpty()) {
            stack.push(tmpStack.pop());
        }
    }
    
    public void pop() {
        if(!stack.isEmpty()) {
            stack.pop();
        }
    }
    
    public int peek() {
        if(stack.isEmpty()) {
            return -1;
        }
        return stack.peek();
    }
    
    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

/**
 * Your SortedStack object will be instantiated and called as such:
 * SortedStack obj = new SortedStack();
 * obj.push(val);
 * obj.pop();
 * int param_3 = obj.peek();
 * boolean param_4 = obj.isEmpty();
 */
```

最小栈

```java
class MinStack {

    private final Stack<Integer> dataStack = new Stack<>();

    private final Stack<Integer> minValStack = new Stack<>();

    /** initialize your data structure here. */
    public MinStack() {

    }
    
    public void push(int val) {
        if(dataStack.isEmpty()) {
            minValStack.push(val);
        }
        else {
            int curMinVal = minValStack.peek();
            if(val > curMinVal) {
                minValStack.push(curMinVal);
            }
            else {
                minValStack.push(val);
            }
        }
        dataStack.push(val);
    }

    
    public void pop() {
        dataStack.pop();
        minValStack.pop();
    }
    
    public int top() {
        return dataStack.peek();
    }
    
    public int getMin() {
        return minValStack.peek();
    }
}

/**
 * Your MinStack object will be instantiated and called as such:
 * MinStack obj = new MinStack();
 * obj.push(val);
 * obj.pop();
 * int param_3 = obj.top();
 * int param_4 = obj.getMin();
 */
// 还有一种思路，是编写一个类，类里面有两个变量，一个是当前最小值，一个是当前值，压栈，弹栈
```

三合一

```java
class TripleInOne {

    // 0，1，2 为一组，3，4，5 为一组，分别存对应的栈的值，并用 int top[] 存对应的栈的栈顶下标

    private final int[] arr;

    private final int[] top;

    private final int n;

    public TripleInOne(int stackSize) {
        n = 3 * stackSize;
        arr = new int[n];
        top = new int[3];
        top[0] = -3;
        top[1] = -2;
        top[2] = -1;
    }
    
    public void push(int stackNum, int value) {
        // 栈顶都大于最大 size 了，直接返回
        if(top[stackNum] + 3 >= n) {
            return;
        }
        top[stackNum] += 3;
        arr[top[stackNum]] = value;
    }
    
    public int pop(int stackNum) {
        if(top[stackNum] < 0) {
            return -1;
        }
        int ret = arr[top[stackNum]];
        top[stackNum] -= 3;
        return ret;
    }
    
    public int peek(int stackNum) {
        if(top[stackNum] < 0) {
            return -1;
        }
        return arr[top[stackNum]];
    }
    
    public boolean isEmpty(int stackNum) {
        return top[stackNum] < 0;
    }
}

/**
 * Your TripleInOne object will be instantiated and called as such:
 * TripleInOne obj = new TripleInOne(stackSize);
 * obj.push(stackNum,value);
 * int param_2 = obj.pop(stackNum);
 * int param_3 = obj.peek(stackNum);
 * boolean param_4 = obj.isEmpty(stackNum);
 */
```

有效的括号

```java
class Solution {

    public boolean isValid(String s) {
        char[] arr = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        for (char c : arr) {
            if(c == '(' || c == '[' || c == '{') {
                stack.push(c);
            }
            else {
                if(stack.empty()) {
                    return false;
                }
                char pop = stack.pop();
                if(c == ')' && pop != '(') {
                    return false;
                }
                if(c == ']' && pop != '[') {
                    return false;
                }
                if(c == '}' && pop != '{') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

}
```

栈的压入、弹出序列

```java
class Solution {

    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Stack<Integer> stack = new Stack<>();
        int j = 0;
        for (int i = 0; i < popped.length; i++) {
            int number = popped[i];
            if(!stack.empty() && stack.peek() == number) {
                stack.pop();
            }
            else {
                while(j < pushed.length && pushed[i] != number) {
                    stack.push(pushed[j]);
                    j++;
                }
                if(j == pushed.length) return false;
                j++;
            }
        }
        return true;
    }

}
```

