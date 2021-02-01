package me.young1lin.offer.list;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/1 上午12:35
 * @version 1.0
 */
public class ReverseList {

	private Node head;

	private Node cur;


	static class Node {

		int val;

		Node next;

		Node(int val) {
			this.val = val;
		}

	}

	/**
	 * 双指针法
	 * cur 指针先走， prev 后走
	 * 1 > 2 > 3 > 4 > 5 > null
	 * 5 > 4 > 3 > 2 > 1 > null
	 *
	 * @param head list head
	 * @return reversed node
	 */
	public Node reverse(Node head) {
		Node prev = null;
		Node cur = head;
		while (cur != null) {
			Node next = cur.next;
			cur.next = prev;
			prev = cur;
			cur = next;
		}
		return prev;
	}


	void initHead() {
		for (int i = 1; i < 6; i++) {
			add(i);
		}
	}

	void add(int val) {
		if (head == null) {
			head = new Node(val);
			cur = head;
		}
		else {
			cur.next = new Node(val);
			cur = cur.next;
		}
	}

	public static void main(String[] args) {
		ReverseList list = new ReverseList();
		list.initHead();
		Node head = list.head;
		printlnNode(head);
		System.out.println("========");
		Node reverseNode = list.reverse(head);
		printlnNode(reverseNode);
	}

	private static void printlnNode(Node head) {
		while (head != null) {
			System.out.println(head.val);
			head = head.next;
		}
	}

}
