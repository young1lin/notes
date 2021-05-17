package me.young1lin.simple;

import java.util.stream.Stream;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/17 下午10:03
 * @version 1.0
 */
public class IpCheck {

	private static final char BLANK = ' ';

	public boolean check(String ip) {
		if (ip == null || ip.trim().length() == 0) {
			return false;
		}
		// 将 IP 地址以 . 分隔
		String[] ipSegements = ip.split("\\.");
		// 验证是否满足子段个数为 4
		if (ipSegements.length != 4) {
			return false;
		}
		for (int i = 0; i < 4; ++i) {
			boolean valid = checkSegment(ipSegements[i]);
			if (!valid) return false;
		}
		return true;
	}

	private boolean checkSegment(String ipSegment) {
		int n = ipSegment.length();
		int i = 0;
		// 跳过前导空格，例如 ” 123“，把前面 ‘ ’ 的部分跳过，认为它是 ok 的
		// charAt(i) 相当于 ipSegment[i]
		while (i < n & ipSegment.charAt(i) == BLANK) {
			i++;
		}
		// 如果字符串全是空格，上面就已经判断过了
		// 处理数字（将字符串转换成数字），例如 “123 ”
		int digit = 0;
		while (i < n && ipSegment.charAt(i) != BLANK) {
			char c = ipSegment.charAt(i);
			// 如果是非字符数字
			if (c < '0' || c > '9') {
				return false;
			}
			// c = '1' -> 1
			digit = digit * 10 + (c - '0');
			// "1234" digit = 1,12,123,1234
			if (digit > 255) {
				return false;
			}
			i++;
		}
		// 处理后置空格， “123 ” or “12 3”
		while (i < n) {
			char c = ipSegment.charAt(i);
			// 后面有非空字符
			if (c != ' ') {
				return false;
			}
			i++;
		}
		// 当 i = n 的时候，表示没有后置空格
		return true;
	}

	public static void main(String[] args) {
		String[] ips = new String[] {"12.33.11.11", " 12. 222.22.1", "null", "12.3 3.11.11", "12.3.277.1"};
		IpCheck check = new IpCheck();
		Stream.of(ips).forEach(e -> System.out.println(check.check(e)));
	}

}
