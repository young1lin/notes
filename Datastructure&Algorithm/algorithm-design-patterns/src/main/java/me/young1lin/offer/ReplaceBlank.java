package me.young1lin.offer;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/27 下午11:25
 * @version 1.0
 */
public class ReplaceBlank {

	private static final String STR = "We are Happy";

	public static void main(String[] args) {
		// forbidden
		System.out.println(STR.replaceAll("\\s", "%20"));
		ReplaceBlank replaceBlank = new ReplaceBlank();
		// right way
		System.out.println(replaceBlank.replace(STR, "%20"));
	}

	public String replace(String str, String replaceStr) {
		StringBuilder sb = new StringBuilder();
		if (str == null) {
			return "";
		}
		char[] strArr = str.toCharArray();
		for (char a : strArr) {
			if (a == ' ') {
				sb.append(replaceStr);
			}
			else {
				sb.append(a);
			}
		}
		return sb.toString();
	}

}
