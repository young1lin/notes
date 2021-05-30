package me.young1lin.xzg.algorithm.week02;

import java.util.Stack;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/28 上午12:23
 * @version 1.0
 */
public class StackToQueue {

	private Stack<Integer> stack = new Stack<>();

	private Stack<Integer> tmpStack = new Stack<>();


	public StackToQueue() {

	}

	public void enqueue(Integer data) {
		stack.push(data);
	}

	public Integer dequeue() {
		if (stack.isEmpty()) {
			return null;
		}
		while (!stack.isEmpty()) {
			tmpStack.push(stack.pop());
		}
		Integer result = tmpStack.pop();
		while (!tmpStack.isEmpty()) {
			stack.push(tmpStack.pop());
		}
		return result;
	}

}

class StackQueue2 {

	private final Stack<Integer> stack = new Stack<>();

	private final Stack<Integer> tmpStack = new Stack<>();


	public StackQueue2() {

	}

	/**
	 * 入队的时候麻烦点，出队的时候就简单了。
	 *
	 * @param data 入队数据
	 */
	public void enqueue(Integer data) {
		while (!stack.isEmpty()) {
			tmpStack.push(stack.pop());
		}
		stack.push(data);
		while (!tmpStack.isEmpty()) {
			stack.push(tmpStack.pop());
		}
	}

	public Integer dequeue() {
		if (stack.isEmpty()) {
			return null;
		}
		return stack.pop();
	}

}