package me.young1lin.xzg.algorithm.week01;

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

	public String reverseWords(String s) {
		StringBuilder sb = new StringBuilder();
		// 去除头尾空格
		s = s.trim();
		char[] charArray = s.toCharArray();
		int left = s.length() - 1;
		int right = left;
		while (left >= 0) {
			while (left >= 0 && left < right) {
				if (!Character.isLetterOrDigit(charArray[left])) {
					left--;
				}
			}
			while(right >= 0 && right > left){
				if (!Character.isLetterOrDigit(charArray[right])) {
					right--;
				}
			}

		}
	}


	public static void main(String[] args) {
		String[] strings = new String[] {
				"the sky is blue",
				"  hello world!  ",
				"a good   example"
		};

	}

}
