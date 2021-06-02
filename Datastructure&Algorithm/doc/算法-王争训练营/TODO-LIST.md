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

5天是能做完的，但是周六要加班，草，垃圾公司，大小周。

但是这周又有 20道。。。还要上课



6 月 1 日待做的

[234. 回文链表](https://leetcode-cn.com/problems/palindrome-linked-list/) （中等）未解决

[328. 奇偶链表](https://leetcode-cn.com/problems/odd-even-linked-list/)（中等）未解决

[25. K 个一组翻转链表](https://leetcode-cn.com/problems/reverse-nodes-in-k-group/)（困难）未解决

[剑指 Offer 22. 链表中倒数第k个节点](https://leetcode-cn.com/problems/lian-biao-zhong-dao-shu-di-kge-jie-dian-lcof/) （简单）未解决

[19. 删除链表的倒数第 N 个结点](https://leetcode-cn.com/problems/remove-nth-node-from-end-of-list/) （中等）未解决

[160. 相交链表](https://leetcode-cn.com/problems/intersection-of-two-linked-lists/)（简单） 未解决

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



链表中倒数第k个节点

链表中倒数第k个节点

相交链表

栈排序

最小栈

三合一

