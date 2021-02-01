package me.young1lin.data.structure;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @date 2021/1/31 下午11:04
 * @version 1.0
 */
public interface Queue<E> {

	/**
	 * add a element to queue
	 * @param e element
	 */
	void enqueue(E e);

	/**
	 * poll first-in element
	 * @return first-in element
	 */
	E dequeue();

	/**
	 * return first-in element
	 * @return first-in element
	 */
	E peek();

	/**
	 * queue size isEmpty
	 * @return isEmpty
	 */
	boolean isEmpty();

	/**
	 * queue's size
	 * @return queue's size
	 */
	int size();

}
