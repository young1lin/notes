package me.young1lin.offer.list;


import java.util.HashMap;
import java.util.Map;

/**
 * O(1) 的复杂度实现  get 和 put
 * 原文
 * https://leetcode-cn.com/problems/lru-cache/solution/zui-jin-mian-zi-jie-yi-mian-peng-dao-lia-1t15/
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/5 上午6:02
 * @version 1.0
 */
public class LeastRecentlyUsed {

	private final Map<Integer, Node> map;

	private final DoubleLinkedList cache;

	private final int capacity;


	public LeastRecentlyUsed(int capacity) {
		this.map = new HashMap<>();
		this.cache = new DoubleLinkedList();
		this.capacity = capacity;
	}

	void put(int key, int val) {
		Node node = new Node(key, val);
		if (map.containsKey(key)) {
			cache.delete(map.get(key));
		}
		else {
			if (map.size() == capacity) {
				int k = cache.deleteLast();
				map.remove(k);
			}
		}
		cache.addFirst(node);
		map.put(key, node);
	}

	int get(int key) {
		if (!map.containsKey(key)) {
			return -1;
		}
		int val = map.get(key).val;
		put(key, val);
		return val;
	}

}

class DoubleLinkedList {

	Node head;

	Node tail;


	DoubleLinkedList() {
		head = new Node(0, 0);
		tail = new Node(0, 0);
		head.next = tail;
		tail.prev = head;
	}

	void addFirst(Node node) {
		node.next = head.next;
		node.prev = head;
		head.next.prev = node;
		head.next = node;
	}

	int delete(Node n) {
		int key = n.key;
		n.next.prev = n.prev;
		n.prev.next = n.next;

		return key;
	}

	int deleteLast() {
		if (head.next == tail) {
			return -1;
		}
		return delete(tail.prev);
	}

}

class Node {

	int key;

	int val;

	Node prev;

	Node next;


	Node(int key, int val) {
		this.key = key;
		this.val = val;
	}

}