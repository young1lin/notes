package me.young1lin.xzg.algorithm.week01;

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
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == ' ') {
				sb.append("%20");
			}
			else {
				sb.append(arr[i]);
			}
		}
		return sb.toString();
	}

}
