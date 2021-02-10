package me.young1lin.offer.list;

/**
 * 两两交换链表的 val
 * LeetCode zh
 * https://leetcode-cn.com/problems/swap-nodes-in-pairs/
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/5 上午3:46
 * @version 1.0
 */
public class SwapList {

	private static class ListNode {

		int val;

		ListNode next;


		ListNode(int val) {
			this.val = val;
		}

		ListNode(int val, ListNode next) {
			this.val = val;
			this.next = next;
		}

	}

	ListNode init() {
		ListNode head = new ListNode(1);
		head.next = new ListNode(2);
		head.next.next = new ListNode(3);
		head.next.next.next = new ListNode(4);
		head.next.next.next.next = new ListNode(5);
		head.next.next.next.next.next = new ListNode(6);
		head.next.next.next.next.next.next = new ListNode(7);
		return head;
	}

	ListNode swapPairs(ListNode head) {
		// 引入哑节点，然后对哑节点后的两个 Node 进行两两交换
		// 时间复杂度是 O(n)
		// 没有引入额外的开销，空间复杂度是 O(1)
		ListNode dummyNode = new ListNode(-1, head);
		ListNode tmp = dummyNode;
		while (tmp.next != null && tmp.next.next != null) {
			ListNode node1 = tmp.next;
			ListNode node2 = tmp.next.next;
			tmp.next = node2;
			node1.next = node2.next;
			node2.next = node1;
			tmp = node1;
		}
		return dummyNode.next;
	}

	ListNode swapPairsRecursion(ListNode head) {
		// 递归思想
		// 时间复杂度是 O(n)
		// 空间复杂度是 O(n)
		if (head == null || head.next == null) {
			return head;
		}
		ListNode newNode = head.next;
		head.next = swapPairsRecursion(newNode.next);
		newNode.next = head;
		return newNode;
	}


	public static void main(String[] args) {
		SwapList swapList = new SwapList();
		ListNode head = swapList.init();
		printList(head);
		ListNode swapPairsNode = swapList.swapPairs(head);
		printList(swapPairsNode);
		ListNode recursion = swapList.swapPairsRecursion(swapPairsNode);
		printList(recursion);
	}

	static void printList(ListNode head) {
		while (head != null) {
			System.out.printf("%s\t", head.val);
			head = head.next;
		}
		System.out.println();
	}

}
