package me.young1lin.xzg.algorithm.week01;

import java.util.stream.Stream;

/**
 * 给你一个字符串 s，由若干单词组成，单词之间用空格隔开。返回字符串中最后一个单词的长度。如果不存在最后一个单词，请返回 0 。
 *
 * 单词 是指仅由字母组成、不包含任何空格字符的最大子字符串。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：s = "Hello World"
 * 输出：5
 * 示例 2：
 *
 * 输入：s = " "
 * 输出：0
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/24 上午2:57
 * @version 1.0
 */
public class LengthOfLastWord {

	private static final char BLANK = ' ';


	public int lengthOfLastWord(String s) {
		int n = s.length();
		int end = n - 1;
		char[] arr = s.toCharArray();
		// 把后面的空字符串剔除
		while (end >= 0 && arr[end] == BLANK) {
			end--;
		}
		if (end < 0) {
			return 0;
		}
		int start = end;
		while (start >= 0 && s.charAt(start) != BLANK) {
			start--;
		}
		return end - start;
	}

	public static void main(String[] args) {
		String[] testStrArr = new String[] {
				"Hello World",
				"Foo Bar",
				"Bar Foo",
				"Stupid World ***"
		};
		LengthOfLastWord lengthOfLastWord = new LengthOfLastWord();
		Stream.of(testStrArr).forEach(str ->
				System.out.println(lengthOfLastWord.lengthOfLastWord(str)));
	}

}
