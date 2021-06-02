package me.young1lin.xzg.algorithm.week02;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/3 上午12:00
 * @version 1.0
 */
public class ReverseKGroupList {

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

	public ListNode reverseKGroup(ListNode head, int k) {
		ListNode dummyHead = new ListNode();
		ListNode tail = dummyHead;
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
		return new ListNode[] {tail, head};
	}

}
