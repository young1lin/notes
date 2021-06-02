package me.young1lin.xzg.algorithm.week02;

/**
 * 请判断一个链表是否为回文链表。
 *
 * 示例 1:
 *
 * 输入: 1->2
 * 输出: false
 * 示例 2:
 *
 * 输入: 1->2->2->1
 * 输出: true
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/2 下午9:30
 * @version 1.0
 */
public class PalindromeList {

	public class ListNode {

		int val;

		ListNode next;


		ListNode() {
		}

		ListNode(int val) {
			this.val = val;
		}

		ListNode(int val, ListNode next) {
			this.val = val;
			this.next = next;
		}

	}

	public boolean isPalindrome(ListNode head) {
		if (head == null || head.next == null) {
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
