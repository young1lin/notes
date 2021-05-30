package me.young1lin.xzg.algorithm.week02;

import java.util.Stack;
import java.util.stream.Stream;

/**
 * 字符串删除掉连续的 3 个重复的字符串。比如 “abbbc”，返回 "ac"; "abbbaac", 返回 c。
 *
 * 注：这里 b 消除了，然后 a 就再一起了，所以返回 c
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/29 下午4:18
 * @version 1.0
 */
public class RemoveDuplicateChars {

	static class CharWithCount {

		char c;

		int count;

		public CharWithCount(char c, int count) {
			this.c = c;
			this.count = count;
		}

	}

	public String remove(String str) {
		// 用一个栈就行
		Stack<CharWithCount> stack = new Stack<>();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// 如果栈是空的，也就是第一次，或者已经消完了，则添加元素
			if (stack.isEmpty()) {
				stack.push(new CharWithCount(c, 1));
				continue;
			}
			CharWithCount topChar = stack.peek();
			// 如果连续的字符不相等，则放入
			if (topChar.c != c) {
				stack.push(new CharWithCount(c, 1));
				continue;
			}
			// 如果相等并且连续的字符等于 2，则需要进行消消乐
			// 栈顶元素和 c 一样，并且等于 2 了，那就是第三个了，需要消消乐
			if (topChar.count == 2) {
				stack.pop();
				continue;
			}
			topChar.count++;
		}
		StringBuilder sb = new StringBuilder();
		while (!stack.isEmpty()) {
			// 如果不等于 1，则需要 append 两次，同理如果是 4 个一消，则需要进行 3 次以下的 append
			if (stack.peek().count != 1) {
				sb.append(stack.peek().c);
			}
			sb.append(stack.pop().c);
		}
		return sb.reverse().toString();
	}

	public static void main(String[] args) {
		RemoveDuplicateChars removeDuplicateChars = new RemoveDuplicateChars();
		String[] strArr = new String[] {
				"abbbc",
				"abbbaac",
				"aaabbbcccz",
				"ddaadddax"
		};
		Stream.of(strArr).forEach(str ->
				System.out.println(removeDuplicateChars.remove(str)));
	}

}