package me.young1lin.xzg.algorithm.week01;

/**
 * 检验回文数字
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/24 上午2:47
 * @version 1.0
 */
public class PalindromeNum {

	public boolean isPalindrome(int x) {
		if (x < 0) {
			return false;
		}
		if (x < 10) {
			return true;
		}
		String s = x + "";
		StringBuilder sb = new StringBuilder(s);
		return sb.reverse().toString().equals(s);
	}

	public boolean isPalindrome2(int x) {
		if (x < 0) {
			return false;
		}
		if (x < 10) {
			return true;
		}
		int backup = x;
		int y = 0;
		while (x != 0) {
			y = y * 10 + x % 10;
			x /= 10;
		}
		return backup == y;
	}

	class Solution {
		public boolean isPalindrome(int x) {
			// Special cases:
			// As discussed above, when x < 0, x is not a palindrome.
			// Also if the last digit of the number is 0, in order to be a palindrome,
			// the first digit of the number also needs to be 0.
			// Only 0 satisfy this property.
			if (x < 0 || (x % 10 == 0 && x != 0)) {
				return false;
			}

			int revertedNumber = 0;
			while (x > revertedNumber) {
				revertedNumber = revertedNumber * 10 + x % 10;
				x /= 10;
			}

			// When the length is an odd number, we can get rid of the middle digit by revertedNumber/10
			// For example when the input is 12321, at the end of the while loop we get x = 12, revertedNumber = 123,
			// since the middle digit doesn't matter in palidrome(it will always equal to itself), we can simply get rid of it.
			return x == revertedNumber || x == revertedNumber / 10;
		}
	}

	class Solution1 {
		public boolean isPalindrome(int x) {
			if (x < 0) {
				return false;
			}
			if (x >= 0 && x < 10) {
				return true;
			}
			String str = x + "";
			int i = 0, j = str.length() - 1;
			while (i < j) {
				char left = str.charAt(i);
				char right = str.charAt(j);
				if (left != right) {
					return false;
				}
				else {
					i++;
					j--;
				}
			}
			return true;
		}
	}

	class Solution2 {
		public boolean isPalindrome(int x) {
			if (x < 0)
				return false;
			if (x < 10)
				return true;

			int input = x;
			int reverse = 0;
			while (x != 0) {

				if (x < 10)
					reverse = (reverse + (x % 10));
				else
					reverse = (reverse + (x % 10)) * 10;

				x /= 10;
			}

			return input == reverse;
		}
	}

}
