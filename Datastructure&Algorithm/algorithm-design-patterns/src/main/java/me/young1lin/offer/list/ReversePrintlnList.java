package me.young1lin.offer.list;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 从尾到头打印链表
 * 不允许修改链表结构
 * 用栈来实现，LinkedList 就是默认的栈
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/28 下午11:40
 * @version 1.0
 */
public class ReversePrintlnList {

	private static final ListNode HEAD = new ListNode();

	private static final int LIST_LENGTH = 50;

	static {
		initListNode();
	}

	private static class ListNode {

		int value;

		ListNode next;

	}

	private static void initListNode() {
		ListNode prev = ReversePrintlnList.HEAD;
		for (int i = 0; i < LIST_LENGTH; i++) {
			ListNode tmp = new ListNode();
			prev.value = i;
			prev.next = tmp;
			prev = tmp;
		}
	}

	public static void main(String[] args) {
		ListNode next = HEAD;
		while (next.next != null) {
			System.out.println(next.value);
			next = next.next;
		}
		System.out.println("=======");
		// 反向打印
		Deque<Integer> stack = new LinkedList<>();
		ListNode next2 = HEAD;
		while (next2.next != null) {
			stack.push(next2.value);
			next2 = next2.next;
		}
		while (!stack.isEmpty()) {
			System.out.println(stack.poll());
		}
	}

}
