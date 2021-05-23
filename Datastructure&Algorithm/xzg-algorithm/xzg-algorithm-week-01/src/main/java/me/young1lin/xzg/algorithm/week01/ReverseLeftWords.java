package me.young1lin.xzg.algorithm.week01;

/**
 * 字符串的左旋转操作是把字符串前面的若干个字符转移到字符串的尾部。
 * 请定义一个函数实现字符串左旋转操作的功能。比如，输入字符串"abcdefg"和数字2，该函数将返回左旋转两位得到的结果"cdefgab"。
 *
 *  
 *
 * 示例 1：
 *
 * 输入: s = "abcdefg", k = 2
 * 输出: "cdefgab"
 * 示例 2：
 *
 * 输入: s = "lrloseumgh", k = 6
 * 输出: "umghlrlose"
 *  
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/24 上午3:15
 * @version 1.0
 */
public class ReverseLeftWords {

	public String reverseLeftWords(String s, int n) {
		// 这题挺简单的，就是把内容搬运到合适的地方
		if (s == null) {
			return "";
		}
		int len = s.length();
		char[] arr = s.toCharArray();
		char[] leftStr = new char[len - n];
		char[] rightStr = new char[n];
		int k = 0;
		for (int i = n; i < len; i++) {
			leftStr[k++] = arr[i];
		}
		int j = 0;
		for (int i = 0; i < n; i++) {
			rightStr[j++] = arr[i];
		}
		return String.valueOf(leftStr) +
				String.valueOf(rightStr);
	}

	public String reverseLeftWords2(String s, int n) {
		char[] arr = s.toCharArray();
		for (int i = 0; i < n; i++) {
			char tmp = arr[0];
			for (int j = 1; j < s.length() - 1; j++) {
				arr[j - 1] = arr[j];
			}
			arr[s.length() - 1] = tmp;
		}
		return new String(arr);
	}

	public static void main(String[] args) {
		ReverseLeftWords re = new ReverseLeftWords();
		System.out.println(re.reverseLeftWords("abcdefg", 2));
	}

}
