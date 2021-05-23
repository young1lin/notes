package me.young1lin.xzg.algorithm.week01;

/**
 * 给定一个字符串，验证它是否是回文串，只考虑字母和数字字符，可以忽略字母的大小写。
 *
 * 说明：本题中，我们将空字符串定义为有效的回文串。
 *
 * 示例 1:
 *
 * 输入: "A man, a plan, a canal: Panama"
 * 输出: true
 * 示例 2:
 *
 * 输入: "race a car"
 * 输出: false
 *
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/24 上午2:26
 * @version 1.0
 */
public class PalindromeStr {

	public boolean isPalindrome(String s) {
		// 如果是空字符串，那肯定是回文字符串
		if (s == null || s.trim().length() == 0) {
			return true;
		}
		int i = 0;
		int j = s.length() - 1;
		// 双指针 i, j，两端向中间靠拢，中间遇到特殊字符就跳过，如果 s[i] != s[j] 则不是回文串
		while (i < j) {
			if (!isAlpha(s.charAt(i))) {
				i++;
				continue;
			}
			if (!isAlpha(s.charAt(j))) {
				j--;
				continue;
			}
			if (toLower(s.charAt(i)) != toLower(s.charAt(j))) {
				return false;
			}
			else {
				// 如果相等，那么双指针都向中间靠拢
				i++;
				j--;
			}
		}
		return true;
	}

	private boolean isAlpha(char charAt) {
		if (charAt >= 'a' && charAt <= 'z') {
			return true;
		}
		if (charAt >= 'A' && charAt <= 'Z') {
			return true;
		}
		if (charAt >= '0' && charAt <= '9') {
			return true;
		}
		return false;
	}

	private char toLower(char charAt) {
		if (charAt >= 'a' && charAt <= 'z') {
			return charAt;
		}
		if (charAt >= '0' && charAt <= '9') {
			return charAt;
		}
		// 因为 a == 97, A == 65, 这是 ASCII 基础内容
		return ((char) ((int) charAt + 32));
	}


	public static void main(String[] args) {
		PalindromeStr palindromeStr = new PalindromeStr();

	}

}
