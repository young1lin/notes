package me.young1lin.xzg.algorithm.week01;

import java.util.stream.Stream;

/**
 * 请实现一个函数，把字符串 s 中的每个空格替换成"%20"。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：s = "We are happy."
 * 输出："We%20are%20happy."
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/24 上午3:11
 * @version 1.0
 */
public class ReplaceSpace {

	public String replaceSpace(String s) {
		return s.replaceAll(" ", "%20");
	}

	public String replaceSpace2(String s) {
		if (s == null) {
			return "";
		}
		char[] arr = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (char c : arr) {
			if (c == ' ') {
				sb.append("%20");
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		ReplaceSpace replaceSpace = new ReplaceSpace();
		String[] arr = new String[] {
				"We are happy.",
				"We are not happy",
				"Rome wasn't built in a day",
				"I'm not a. communist necessarily, but i've been in the red all my life.",
				"Image life as a game in which you are juggling some five balls in the air. You name them - work, family, health, friends and spirit, and you're keeping all these ball in the air."
		};
		Stream.of(arr).forEach(str -> System.out.println(replaceSpace.replaceSpace2(str)));
		// It also works, but no one accesses it as an interview solution
		Stream.of(arr).forEach(str -> System.out.println(replaceSpace.replaceSpace2(str)));
	}

}
