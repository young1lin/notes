package me.young1lin.data.structure;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/31 上午12:49
 * @version 1.0
 */
public class LinkedList<E> implements Deque<E>, List<E> {

	private int size = 0;

	private ListNode<E> first;

	private ListNode<E> last;

	private int modCount = 0;

	private LinkedList() {
	}

	private static class ListNode<E> {

		E item;

		ListNode<E> prev;

		ListNode<E> next;

		ListNode(ListNode<E> prev, E element, ListNode<E> next) {
			this.item = element;
			this.prev = prev;
			this.next = next;
		}

	}

	private void linkFirst(E e) {
		ListNode<E> f = first;
		ListNode<E> newNode = new ListNode<E>(null, e, f);
		first = newNode;
		if (f == null) {
			last = newNode;
		}
		else {
			f.prev = newNode;
		}
		size++;
		modCount++;
	}

	private void linkLast(E e) {
		ListNode<E> l = last;
		ListNode<E> newNode = new ListNode<>(l, e, null);
		last = newNode;
		if (l == null) {
			first = newNode;
		}
		else {
			l.next = newNode;
		}
		size++;
		modCount++;
	}

	private void linkBefore(E e, ListNode<E> cur) {
		ListNode<E> prev = cur.prev;
		ListNode<E> newNode = new ListNode<>(prev, e, cur);
		cur.prev = newNode;
		if (prev == null) {
			first = newNode;
		}
		else {
			prev.next = newNode;
		}
		size++;
		modCount++;
	}

	private E unlinkFirst(ListNode<E> f) {
		E element = f.item;
		ListNode<E> next = f.next;
		f.item = null;
		f.next = null;
		first = next;
		if (next == null) {
			last = null;
		}
		else {
			next.prev = null;
		}
		size--;
		modCount++;
		return element;
	}

	private E unlinkLast(ListNode<E> l) {
		final E element = l.item;
		final ListNode<E> prev = l.prev;
		l.item = null;
		l.prev = null;
		last = prev;
		if (prev == null) {
			first = null;
		}
		else {
			prev.next = null;
		}
		size--;
		modCount++;
		return element;
	}

	E unlink(ListNode<E> x) {
		final E element = x.item;
		final ListNode<E> next = x.next;
		final ListNode<E> prev = x.prev;

		if (prev == null) {
			first = next;
		}
		else {
			prev.next = next;
			x.prev = null;
		}

		if (next == null) {
			last = prev;
		}
		else {
			next.prev = prev;
			x.next = null;
		}

		x.item = null;
		size--;
		modCount++;
		return element;
	}

	@Override
	public E removeFirst() {
		ListNode<E> f = first;
		if (f == null) {
			throw new NoSuchElementException();
		}
		return unlinkFirst(f);
	}

	@Override
	public E removeLast() {
		ListNode<E> l = last;
		if (l == null) {
			throw new NoSuchElementException();
		}
		return unlinkLast(l);
	}

	@Override
	public E getFirst() {
		ListNode<E> f = first;
		if (f == null) {
			throw new NoSuchElementException();
		}
		return f.item;
	}

	@Override
	public E getLast() {
		ListNode<E> l = last;
		if (l == null) {
			throw new NoSuchElementException();
		}
		return l.item;
	}

	@Override
	public void addFirst(E e) {
		linkFirst(e);
	}

	@Override
	public void addLast(E e) {
		linkLast(e);
	}

	@Override
	public boolean offerFirst(E e) {
		return false;
	}

	@Override
	public boolean offerLast(E e) {
		return false;
	}


	@Override
	public E pollFirst() {
		return null;
	}

	@Override
	public E pollLast() {
		return null;
	}


	@Override
	public E peekFirst() {

		return null;
	}

	@Override
	public E peekLast() {
		return null;
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return false;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return false;
	}

	@Override
	public boolean add(E e) {
		linkLast(e);
		return true;
	}

	@Override
	public boolean offer(E e) {
		return false;
	}

	@Override
	public E remove() {
		return null;
	}

	@Override
	public E poll() {
		return null;
	}

	@Override
	public E element() {
		return null;
	}

	@Override
	public E peek() {
		return null;
	}

	@Override
	public void push(E e) {

	}

	@Override
	public E pop() {
		ListNode<E> f = first;
		if (f == null) {
			throw new NoSuchElementException();
		}
		return unlinkFirst(f);
	}

	@Override
	public boolean remove(Object o) {
		ListNode<E> x = first;
		if (o == null) {
			while (x != null) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
				x = x.next;
			}
		}
		else {
			while (x != null) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
				x = x.next;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
		ListNode<E> x = first;
		while (x != null) {
			ListNode<E> next = x.next;
			x.item = null;
			x.next = null;
			x.prev = null;
			x = next;
		}
		first = last = null;
		size = 0;
		modCount++;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		checkPositionIndex(index);
		Object[] objects = c.toArray();
		int numNew = objects.length;
		if (numNew == 0) {
			return false;
		}

		ListNode<E> prev, current;
		if (index == size) {
			current = null;
			prev = last;
		}
		else {
			current = node(index);
			prev = current.prev;
		}
		for (Object o : objects) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			ListNode<E> newNode = new ListNode<>(prev, e, null);
			if (prev == null) {
				first = newNode;
			}
			else {
				prev.next = newNode;
			}
			prev = newNode;
		}

		if (current == null) {
			last = prev;
		}
		else {
			prev.next = current;
			current.prev = prev;
		}
		size += numNew;
		modCount++;
		return true;
	}

	private void checkPositionIndex(int index) {
		if (!isPositionIndex(index)) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}
	}

	private boolean isPositionIndex(int index) {
		return index >= 0 && index <= size;
	}

	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

	ListNode<E> node(int index) {
		ListNode<E> x;
		if (index < (size >> 1)) {
			x = first;
			for (int i = 0; i < index; i++) {
				x = x.next;
			}
		}
		else {
			x = last;
			for (int i = size - 1; i > index; i--) {
				x = x.prev;
			}
		}
		return x;
	}

	@Override
	public E get(int index) {
		checkElementIndex(index);
		return node(index).item;
	}

	private void checkElementIndex(int index) {
		if (!isElementIndex(index)) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}
	}

	private boolean isElementIndex(int index) {
		return index >= 0 && index < size;
	}

	@Override
	public E set(int index, E element) {
		return null;
	}

	@Override
	public void add(int index, E element) {

	}

	@Override
	public E remove(int index) {
		return null;
	}

	@Override
	public int indexOf(Object o) {
		int index = 0;
		if (o == null) {
			for (ListNode<E> x = first; x != null; x = x.next) {
				if (x.item == null) {
					return index;
				}
				index++;
			}
		}
		else {
			for (ListNode<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item)) {
					return index;
				}
				index++;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return null;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}

	@Override
	public Iterator<E> descendingIterator() {
		return null;
	}

}
