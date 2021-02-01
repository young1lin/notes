package me.young1lin.offer.list;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/1 上午1:26
 * @version 1.0
 */
public class CircleList {

	static class Node {

		int val;

		Node next;

		Node(int val) {
			this.val = val;
		}

		@Override
		public boolean equals(Object o) {
			return super.equals(o);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}

	/**
	 * 自己想出的笨方法
	 * @param head Node head
	 * @return hasCircle
	 */
	boolean hasCircle(Node head) {
		Set<Node> set = new HashSet<>();
		Node h = head;
		while (h != null) {
			if (set.contains(h)) {
				return true;
			}
			set.add(h);
			h = h.next;
		}
		return false;
	}

	/**
	 * 双指针走法
	 * @param head Node Head
	 * @return hasCircle
	 */
	boolean advancedHasCircle(Node head) {
		if (head == null || head.next == null) {
			return false;
		}
		Node oneStep = head;
		Node twoStep = head.next.next;
		while (oneStep != twoStep) {
			if (twoStep == null || twoStep.next == null) {
				return false;
			}
			oneStep = oneStep.next;
			twoStep = twoStep.next.next;
		}
		return true;
	}

	/**
	 *     |-------|
	 * 3 > 2 > 0 > 4
	 *
	 * @return circle List
	 */
	Node initCircleList() {
		Node head = new Node(3);
		Node sec = new Node(2);
		head.next = sec;
		head.next.next = new Node(0);
		Node tail = new Node(4);
		head.next.next.next = tail;
		tail.next = sec;
		return head;
	}

	/**
	 *
	 * 3 > 2 > 0 > 4
	 *
	 * @return No-circle List
	 */
	Node initNoCircleList() {
		Node head = new Node(3);
		head.next = new Node(2);
		head.next.next = new Node(0);
		head.next.next.next = new Node(4);
		return head;
	}

	public static void main(String[] args) {
		CircleList circleList = new CircleList();
		Node node = circleList.initCircleList();
		System.out.println(circleList.hasCircle(node));
		Node node1 = circleList.initNoCircleList();
		System.out.println(circleList.hasCircle(node1));
		System.out.println(circleList.advancedHasCircle(node));
		System.out.println(circleList.advancedHasCircle(node1));
	}

}
