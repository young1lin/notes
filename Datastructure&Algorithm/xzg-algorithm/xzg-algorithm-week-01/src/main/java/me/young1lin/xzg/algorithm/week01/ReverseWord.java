package me.young1lin.xzg.algorithm.week01;

import java.util.stream.Stream;

/**
 * 输入一个英文句子，翻转句子中单词的顺序，但单词内字符的顺序不变。为简单起见，标点符号和普通字母一样处理。例如输入字符串"I am a student. "，则输出"student. a am I"。
 *
 *  
 *
 * 示例 1：
 *
 * 输入: "the sky is blue"
 * 输出: "blue is sky the"
 * 示例 2：
 *
 * 输入: "  hello world!  "
 * 输出: "world! hello"
 * 解释: 输入字符串可以在前面或者后面包含多余的空格，但是反转后的字符不能包括。
 * 示例 3：
 *
 * 输入: "a good   example"
 * 输出: "example good a"
 * 解释: 如果两个单词间有多余的空格，将反转后单词间的空格减少到只含一个。
 *  
 *
 * 说明：
 *
 * 无空格字符构成一个单词。
 * 输入字符串可以在前面或者后面包含多余的空格，但是反转后的字符不能包括。
 * 如果两个单词间有多余的空格，将反转后单词间的空格减少到只含一个。
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/21 下午9:25
 * @version 1.0
 */
public class ReverseWord {

	private static final char BLANK = ' ';


	public String reverseWords(String s) {
		// 删除首尾空格
		s = s.trim();
		int j = s.length() - 1, i = j;
		StringBuilder res = new StringBuilder();
		while (i >= 0) {
			// 搜索首个空格
			while (i >= 0 && s.charAt(i) != BLANK) {
				i--;
			}
			// 添加单词
			res.append(s, i + 1, j + 1);
			// 跳过单词间空格
			while (i >= 0 && s.charAt(i) == BLANK) {
				i--;
			}
			// j 指向下个单词的尾字符
			j = i;
		}
		// 转化为字符串并返回
		return res.toString().trim();
	}

	public String reverseWords2(String s) {
		// 删除首尾空格，分割字符串
		String[] strs = s.trim().split(" ");
		StringBuilder res = new StringBuilder();
		// 倒序遍历单词列表
		for (int i = strs.length - 1; i >= 0; i--) {
			// 遇到空单词则跳过
			if (strs[i].equals("")) continue;
			// 将单词拼接至 StringBuilder
			res.append(strs[i]).append(" ");
		}
		// 转化为字符串，删除尾部空格，并返回
		return res.toString().trim();
	}


	public String reverseWords3(String s) {
		if (s == null || s.trim().length() == 0) {
			return "";
		}
		char[] str = s.toCharArray();
		// 先对 s 进行 trim 操作
		int n = trim(str);
		if (n == 0) {
			return "";
		}
		reverse(str, 0, n - 1);
		int p = 0;
		while (p < n) {
			int r = p;
			while (r < n && str[r] != BLANK) {
				r++;
			}
			reverse(str, p, r - 1);
			p = r + 1;
		}
		char[] newStr = new char[n];
		if (n >= 0) System.arraycopy(str, 0, newStr, 0, n);
		return new String(newStr);
	}

	/**
	 * 	原地删除前置空格和后置空格，以及内部多余的空格，返回新字符串长度
	 */
	private int trim(char[] strArr) {
		int i = 0;
		int n = strArr.length;
		// 记录删除多余空格后，数组的长度
		int k = 0;
		while (i < n && strArr[i] == BLANK) {
			i++;
		}
		while (i < n) {
			// 删除内部多于和尾部多于的空格
			if (strArr[i] == BLANK) {
				if (i + 1 < n && strArr[i + 1] != BLANK) {
					strArr[k++] = BLANK;
				}
			}
			else {
				strArr[k++] = strArr[i];
			}
			i++;
		}
		return k;
	}

	/**
	 * 返回 [p,r] 之间的字符串，这里是闭区间
	 *
	 *
	 * @param str str
	 * @param p left
	 * @param r right
	 */
	private void reverse(char[] str, int p, int r) {
		int mid = (p + r) / 2;
		for (int i = p; i <= mid; ++i) {
			char tmp = str[i];
			str[i] = str[r - (i - p)];
			str[r - (i - p)] = tmp;
		}
	}


	public static void main(String[] args) {
		String[] strings = new String[] {
				"the sky is blue",
				"  hello    world!  ",
				"a good   example"
		};
		ReverseWord reverseWord = new ReverseWord();
		for (String s : strings) {
			System.out.println(reverseWord.reverseWords(s));
			System.out.println(reverseWord.reverseWords2(s));
			System.out.println(reverseWord.reverseWords3(s));
		}
	}

}
