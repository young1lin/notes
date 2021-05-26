package me.young1lin.xzg.algorithm.week02;

/**
 * 实现头插、尾插、根据索引插入、根据索引删除、toString 打印
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/26 下午10:23
 * @version 1.0
 */
public class LinkedList<E> {

	private Node<E> head = null;

	private Node<E> tail = null;

	private int size = 0;

	private static class Node<E> {

		E value;

		Node<E> prev;

		Node<E> next;


		Node(E value, Node<E> prev, Node<E> next) {
			this.prev = prev;
			this.next = next;
			this.value = value;
		}

	}

	public LinkedList() {

	}

	public void insertHead(E e) {
		Node<E> p = head;
		Node<E> newNode = new Node<>(e, null, p);
		head = newNode;
		if (p == null) {
			tail = head;
		}
		else {
			p.prev = newNode;
		}
		size++;
	}

	public void insertTail(E e) {
		Node<E> p = tail;
		Node<E> newNode = new Node<>(e, p, null);
		tail = newNode;
		if (p == null) {
			head = newNode;
		}
		else {
			p.next = newNode;
		}
		size++;
	}

	public void insertByIndex(int index, E e) {
		if (index < 0) {
			throw new IllegalArgumentException();
		}
		if (index != 0 && index > size - 1) {
			throw new IndexOutOfBoundsException(String.format("index %s is not allowed ", index));
		}
		Node<E> p = head;
		Node<E> prev = null;
		while (p != null) {
			if (index == 0) {
				break;
			}
			prev = p;
			p = p.next;
			index--;
		}
		Node<E> newNode = new Node<>(e, prev, p);
		if (p == null) {
			head = newNode;
			tail = head;
		}
		else if (prev == null) {
			head.next = head;
			newNode.next = p;
			tail = head;
		}
		else {
			prev.next = newNode;
			p.prev = newNode;
		}
		size++;
	}

//	public E deleteByIndex(int index) {
//		if (index >= size - 1) {
//			throw new IndexOutOfBoundsException(String.format("index %s is not allowed ", index));
//		}
//
//		return E;
//	}


	@Override
	public String toString() {
		Node<E> p = head;
		StringBuilder sb = new StringBuilder();
		while (p != null) {
			sb.append(p.value.toString()).append("\n");
			p = p.next;
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		LinkedList<String> list = new LinkedList<>();
		list.insertHead("111");
		list.insertHead("222");
		list.insertTail("3333");
		list.insertHead("7777");
		list.insertTail("88888");
		list.insertByIndex(1, "111");
		list.insertByIndex(1, "2222");
		System.out.println(list.toString());
	}

}
