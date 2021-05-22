package me.young1lin.xzg.algorithm.week01;

import java.util.stream.Stream;

/**
 * 给你一个有效的 IPv4 地址 address，返回这个 IP 地址的无效化版本。
 *
 * 所谓无效化 IP 地址，其实就是用 "[.]" 代替了每个 "."。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：address = "1.1.1.1"
 * 输出："1[.]1[.]1[.]1"
 * 示例 2：
 *
 * 输入：address = "255.100.50.0"
 * 输出："255[.]100[.]50[.]0"
 *
 * 提示：
 *
 * 给出的 address 是一个有效的 IPv4 地址
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/21 下午8:51
 * @version 1.0
 */
public class DefangIpAddress {

	/**
	 * 因为 toCharArray 后，Java 的字符串本来就是一个不可变的 char[]，只需比较对应的 . 符号，
	 * 创建一个新的数组，new String，即可返回无效化的地址。
	 *
	 * @param address ip 地址
	 * @return 无效化的 ip 地址
	 */
	public static String defangIpAddress(String address) {
		char[] charArr = address.toCharArray();
		char[] newCharArr = new char[charArr.length + 2 * 3];
		int k = 0;
		for (char c : charArr) {
			if (c == '.') {
				newCharArr[k++] = '[';
				newCharArr[k++] = '.';
				newCharArr[k++] = ']';
			}
			else {
				newCharArr[k++] = c;
			}
		}
		return new String(newCharArr);
	}

	public static String defangIpAddress2(String address){
		StringBuilder sb = new StringBuilder();
		char[] arr = address.toCharArray();
		for (char c : arr) {
			if(c == '.'){
				sb.append("[.]");
			}else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String[] ips = new String[] {
				"10.10.10.10",
				"10.27.223.29",
				"227.227.227.227",
				"77.77.77.77"
		};
		System.out.println("defang 1");
		Stream.of(ips).forEach(ip ->
				System.out.println(defangIpAddress(ip)));
		System.out.println("\ndefant 2");
		Stream.of(ips).forEach(ip ->
				System.out.println(defangIpAddress2(ip)));
	}

}
