package me.young1lin.algorithm.book;

import java.util.LinkedList;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/5 上午3:30
 * @version 1.0
 */
public class Queue<E> {

	private final LinkedList<E> queue;

	public Queue() {
		this.queue = new LinkedList<>();
	}

	void enqueue(E item) {
		queue.offerFirst(item);
	}

	E dequeue() {
		return queue.pollLast();
	}

	boolean isEmpty() {
		return queue.isEmpty();
	}

	int size() {
		return queue.size();
	}

	public static void main(String[] args) {
		Queue<Integer> q = new Queue<>();
		for (int i = 0; i < 20; i++) {
			q.enqueue(i);
		}
		for (int i = 0; i < 19; i++) {
			System.out.println(q.dequeue());
		}
		System.out.println("size:\t" + q.size());
		System.out.println("isEmpty:\t" + q.isEmpty());
	}

}
