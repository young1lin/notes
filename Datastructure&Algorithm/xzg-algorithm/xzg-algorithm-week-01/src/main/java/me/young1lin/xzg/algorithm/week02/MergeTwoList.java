package me.young1lin.xzg.algorithm.week02;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/30 下午11:01
 * @version 1.0
 */
public class MergeTwoList {

	static class ListNode {

		int val;

		ListNode next;

		ListNode(){

		}

		ListNode(int x) {
			val = x;
		}

	}

	public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
		if (l1 == null) {
			return l2;
		}
		if (l2 == null) {
			return l1;
		}
		// 两个结点互相比较，如果一个比另一个小，则小的变成 tail
		ListNode p1 = l1;
		ListNode p2 = l2;
		// 引入虚拟结点，因为需要时刻插入尾部。
		ListNode dummyNode = new ListNode();
		// 声明尾结点
		ListNode tail = dummyNode;
		while (p1 != null && p2 != null) {
			if (p1.val <= p2.val) {
				tail.next = p1;
				tail = p1;
				p1 = p1.next;
			}
			else {
				tail.next = p2;
				tail = p2;
				p2 = p2.next;
			}
		}
		// 如果还有一个链表没走完，则进行赋值
		if (p1 != null) {
			tail.next = p1;
		}
		if (p2 != null) {
			tail.next = p2;
		}
		return dummyNode.next;
	}

}
