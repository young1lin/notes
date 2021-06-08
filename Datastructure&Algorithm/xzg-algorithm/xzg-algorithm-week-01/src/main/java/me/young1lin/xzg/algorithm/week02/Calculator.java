package me.young1lin.xzg.algorithm.week02;

import static java.lang.Character.isDigit;

import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * 面试题 16.26 计算器。面试官也是 Google 的
 *
 * 给定一个包含正整数、加➕、减(-)、乘(*)、除(/)的算数表达式（括号除外），计算其结果。
 *
 * 表达式仅包含非负整数，+, -, *, / 四种运算符**和空格**，整数除法仅保留整数部分。
 *
 * 示例 1:
 *
 * 输入：  “3+2*2”
 * 输出：7
 *
 * 示例 2:
 *
 * 输入： “3/2”
 * 输出：1
 *
 * 示例 3:
 *
 * 输入： “3+5/2”
 * 输出：5
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/29 下午4:51
 * @version 1.0
 */
public class Calculator {

	private static final char BLANK = ' ';


	public int calculate(String s) {
		Stack<Integer> numbers = new Stack<>();
		Stack<Character> ops = new Stack<>();
		int i = 0;
		int n = s.length();
		while (i < n) {
			char c = s.charAt(i);
			// 跳过空格
			if (c == BLANK) {
				i++;
			}// 如果是数字的话
			else if (isDigit(c)) {
				int number = 0;
				while (i < n && isDigit(s.codePointAt(i))) {
					number = number * 10 + (s.charAt(i) - '0');
					i++;
				}
				numbers.push(number);
			}// 运算符
			else {
				if (ops.isEmpty() || !prior(c, ops.peek())) {
					ops.push(c);
				}
				else {
					while (!ops.isEmpty() && prior(c, ops.peek())) {
						fetchAndCal(numbers, ops);
					}
					ops.push(c);
				}
				i++;
			}
		}
		while (!ops.isEmpty()) {
			fetchAndCal(numbers, ops);
		}
		return numbers.pop();
	}

	private boolean prior(char c, Character peek) {
		// 返回优先级，c 是否比 peek 的优先级大，乘除比加减优先级大
		return (c != '*' && c != '/') || (peek != '+' && peek != '-');
	}

	private void fetchAndCal(Stack<Integer> numbers, Stack<Character> ops) {
		int number2 = numbers.pop();
		int number1 = numbers.pop();
		char op = ops.pop();
		int ret = cal(op, number1, number2);
		numbers.push(ret);
	}

	private int cal(char op, int number1, int number2) {
		switch (op) {
			case '+':
				return number1 + number2;
			case '-':
				return number1 - number2;
			case '*':
				return number1 * number2;
			case '/':
				return number1 / number2;
			default:
				return -1;
		}
	}

	public static void main(String[] args) {
		String[] strArr = new String[] {
				"3+2*2",
				"3/2",
				"3+5/2"
		};
		Calculator calculator = new Calculator();
		Stream.of(strArr).forEach(str -> System.out.println(calculator.calculate(str)));
	}

}
