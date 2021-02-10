package me.young1lin.offer.list;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

/**
 * 找到循环链表中的循环的节点
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/5 上午4:35
 * @version 1.0
 */
public class DetectCycleList {

	private static class ListNode {

		int val;

		ListNode next;


		ListNode(int val) {
			this.val = val;
			next = null;
		}

		@Override
		public String toString() {
			return String.valueOf(val);
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}

	/**
	 *              |<<<<<<<<<<<<<<|
	 * @return 3 -> 2 -> 0 -> 4 -> |
	 *
	 */
	ListNode init() {
		ListNode head = new ListNode(3);
		ListNode cycle = new ListNode(2);
		head.next = cycle;
		cycle.next = new ListNode(0);
		cycle.next.next = new ListNode(4);
		cycle.next.next.next = cycle;
		return head;
	}

	ListNode detectCycle(ListNode head) {
		if (head == null) {
			return null;
		}
		ListNode slow = head, fast = head;
		while (fast != null) {
			slow = slow.next;
			if (fast.next != null) {
				fast = fast.next.next;
			}
			else {
				return null;
			}
			if (fast == slow) {
				ListNode ptr = head;
				while (ptr != slow) {
					ptr = ptr.next;
					slow = slow.next;
				}
				return ptr;
			}
		}
		return head;
	}

	ListNode detectCycle2(ListNode head) {
		ListNode pos = head;
		HashSet<ListNode> set = new HashSet<>();
		while (pos != null) {
			if (set.contains(pos)) {
				return pos;
			}
			set.add(pos);
			pos = pos.next;
		}
		return null;
	}

	public static void main(String[] args) {
		DetectCycleList de = new DetectCycleList();
		ListNode head = de.init();
		System.out.println(de.detectCycle(head));
		System.out.println(de.detectCycle2(head));
	}

}
