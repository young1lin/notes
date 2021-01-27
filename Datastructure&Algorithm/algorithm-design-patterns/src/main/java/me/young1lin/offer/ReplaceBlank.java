package me.young1lin.offer;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/27 下午11:25
 * @version 1.0
 */
public class ReplaceBlank {

	private static String str = "We are Happy";

	public static void main(String[] args) {
		System.out.println(str.replaceAll("\\s", "%20"));
	}

}
