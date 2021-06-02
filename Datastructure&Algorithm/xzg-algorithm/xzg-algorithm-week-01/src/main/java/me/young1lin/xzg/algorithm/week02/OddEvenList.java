package me.young1lin.xzg.algorithm.week02;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/6/2 下午9:31
 * @version 1.0
 */
public class OddEvenList {

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
