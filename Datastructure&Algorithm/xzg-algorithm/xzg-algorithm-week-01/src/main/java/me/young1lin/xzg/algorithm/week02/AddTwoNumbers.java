package me.young1lin.xzg.algorithm.week02;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/30 下午11:03
 * @version 1.0
 */
public class AddTwoNumbers {

	static class ListNode {

		int val;

		ListNode next;

		ListNode() {

		}

		ListNode(int x) {
			val = x;
		}

		ListNode(int x, ListNode next) {
			val = x;
			this.next = next;
		}

	}

	public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
		ListNode pre = new ListNode(0);
		ListNode cur = pre;
		int carry = 0;
		while (l1 != null || l2 != null) {
			int x = l1 == null ? 0 : l1.val;
			int y = l2 == null ? 0 : l2.val;
			int sum = x + y + carry;
			// 将合计值➗10，有剩下的 ，则进行新增
			carry = sum / 10;
			sum = sum % 10;
			cur.next = new ListNode(sum);
			// 当前节点的下个节点，给当前节点
			cur = cur.next;
			if (l1 != null)
				l1 = l1.next;
			if (l2 != null)
				l2 = l2.next;
		}
		if (carry == 1) {
			cur.next = new ListNode(carry);
		}
		return pre.next;
	}

}
