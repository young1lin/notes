package me.young1lin.xzg.algorithm.week02;

/**
 * 给你一个链表的头节点 head 和一个整数 val ，请你删除链表中所有满足 Node.val == val 的节点，并返回 新的头节点 。
 *
 * ![](https://assets.leetcode.com/uploads/2021/03/06/removelinked-list.jpg)
 *
 *
 * 示例 1：
 *
 *
 * 输入：head = [1,2,6,3,4,5,6], val = 6
 * 输出：[1,2,3,4,5]
 * 示例 2：
 *
 * 输入：head = [], val = 1
 * 输出：[]
 * 示例 3：
 *
 * 输入：head = [7,7,7,7], val = 7
 * 输出：[]
 *
 *
 * 提示：
 *
 * 列表中的节点在范围 [0, 104] 内
 * 1 <= Node.val <= 50
 * 0 <= k <= 50
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/26 下午11:21
 * @version 1.0
 */
public class RemoveElements {

	public static class ListNode {

		int val;

		ListNode next;


		ListNode() {
		}

		ListNode(int val) {
			this.val = val;
		}

		ListNode(int val, ListNode next) {
			this.val = val; this.next = next;
		}
	}

	public ListNode removeElements(ListNode head, int val) {
		ListNode p = head;
		if (p == null) {
			return null;
		}
		ListNode prev = null;
		while (p != null) {
			if (val == p.val) {
				if (prev == null) {
					head = p.next;
				}
				else {
					prev.next = p.next;
				}
			}
			prev = p;
			p = p.next;
		}
		if (head.val == val) {
			head = head.next;
		}
		return head;
	}

}
