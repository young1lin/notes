package me.young1lin.simple;

import java.util.stream.Stream;

/**
 *
 * 给你一个有效的 IPv4 地址 `address`，返回这个 IP 地址的无效化版本。
 * 所谓无效化版本，其实就是用 ""[.]" 代替了每个 "."。
 * <p>
 * e.g.1
 * 输入： address = "1.1.1.1"
 * 输出："1[.]1[.]1[.]1"
 * <p>
 * e.g.2
 * 输入：address = "255.100.50.0"
 * 输出："255[.]100[.]50[.]0"
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/17 下午10:31
 * @version 1.0
 */
public class IpAddressInvalidation {

	public String invalidateIpAddress(String address) {
		char[] origin = address.toCharArray();
		int n = origin.length;
		int newN = n + 2 * 3;
		char[] newString = new char[newN];
		int k = 0;
		for (char c : origin) {
			if (c != '.') {
				newString[k] = c;
				k++;
			}
			else {
				newString[k++] = '[';
				newString[k++] = '.';
				newString[k++] = ']';
			}
		}
		return new String(newString);
	}

	public String invalidateIpAddress2(String address) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < address.length(); ++i) {
			char c = address.charAt(i);
			if (c != '.') {
				sb.append(c);
			}
			else {
				sb.append("[.]");
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		IpAddressInvalidation ipAddressInvalidation = new IpAddressInvalidation();
		String[] ips = new String[] {"10.11.11.11", "1.1.1.1", "7.7.7.7", "8.8.8.8"};
		Stream.of(ips).forEach(e -> System.out.println(ipAddressInvalidation.invalidateIpAddress(e)));
		Stream.of(ips).forEach(e -> System.out.println(ipAddressInvalidation.invalidateIpAddress2(e)));
	}

}
