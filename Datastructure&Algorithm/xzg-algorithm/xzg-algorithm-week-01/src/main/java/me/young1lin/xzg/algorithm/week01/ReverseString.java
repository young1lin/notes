package me.young1lin.xzg.algorithm.week01;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 char[] 的形式给出。
 *
 * 不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。
 *
 * 你可以假设数组中的所有字符都是 ASCII 码表中的可打印字符。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：["h","e","l","l","o"]
 * 输出：["o","l","l","e","h"]
 * 示例 2：
 *
 * 输入：["H","a","n","n","a","h"]
 * 输出：["h","a","n","n","a","H"]
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/21 下午9:05
 * @version 1.0
 */
public class ReverseString {

	/**
	 * 不能额外分配空间，那么可以用双指针，左右互换
	 *
	 * @param s char 数组
	 */
	public static void reverseString(char[] s) {
		int left = 0, right = s.length - 1;
		while (left < right) {
			char tmp = s[left];
			s[left] = s[right];
			s[right] = tmp;
			left++;
			right--;
		}
	}

	public static void reverseString2(char[] s) {
		// 使用异或操作
		for (int i = 0, length = s.length - 1; i < length; i++, length--) {
			s[i] ^= s[length];
			s[length] ^= s[i];
			s[i] ^= s[length];
		}
	}

	public static void main(String[] args) {
		char[][] ss = new char[][] {
				{'h', 'e', 'l', 'l', 'o'},
				{'H', 'a', 'n', 'n', 'a', 'h'},
				{'f', 'o', 'o', 'b', 'a', 'r'},
		};
		Stream.of(ss).forEach(s -> {
			reverseString(s);
			System.out.println(Arrays.toString(s));
		});
		System.out.println("\n reverseString2");
		Stream.of(ss).forEach(s -> {
			reverseString2(s);
			System.out.println(Arrays.toString(s));
		});
	}

}
