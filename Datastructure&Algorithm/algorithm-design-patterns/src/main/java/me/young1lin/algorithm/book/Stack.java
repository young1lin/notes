package me.young1lin.algorithm.book;

import java.util.LinkedList;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/5 上午3:39
 * @version 1.0
 */
public class Stack<E> {

	private final LinkedList<E> stack;


	public Stack() {
		stack = new LinkedList<>();
	}

	void push(E item) {
		stack.push(item);
	}

	E pop() {
		return stack.pop();
	}

	boolean isEmpty() {
		return stack.isEmpty();
	}

	int size() {
		return stack.size();
	}

	public static void main(String[] args) {
		Stack<Integer> s = new Stack<>();
		for (int i = 0; i < 20; i++) {
			s.push(i);
		}
		for (int i = 0; i < 19; i++) {
			System.out.println(s.pop());
		}
		System.out.println("Size:\t" + s.size());
		System.out.println("isEmpty:\t" + s.isEmpty());
	}

}
