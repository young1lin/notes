package me.young1lin.xzg.algorithm.week01;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/25 上午1:17
 * @version 1.0
 */
public class OneEditAway {

	public boolean oneEditAway(String first, String second) {
		// 相同，则一样
		if (first.equals(second)) {
			return true;
		}
		int n = first.length();
		int m = second.length();
		// 如果两个长度相差大于1，那么就肯定不是一次编辑能过的
		if (Math.abs(n - m) > 1) {
			return false;
		}
		// 在长的字符串中的删除 = 在短的字符串中的新增
		// 长度一样，则是替换
		// 先对两个字符串进行遍历
		int i = 0;
		int j = 0;
		// 找出第一个不相等的元素
		while (i < n && j < m && first.charAt(i) == second.charAt(j)) {
			i++;
			j++;
		}
		// 下面是跳过当前不一样的字符
		// 表示是替换操作
		// 替换 abdf abcf，样例跳过索引为 2 的
		if (n == m) {
			i++;
			j++;
		}
		else if (n > m) {
			i++;
		}
		else {
			j++;
		}
		while (i < n && j < m) {
			// 我上面都已经跳过了，还是不一样，那后面肯定不用比了
			if (first.charAt(i) != second.charAt(j)) {
				return false;
			}
			i++;
			j++;
		}
		return true;
	}


	public static void main(String[] args) {
		String first = "intention";
		String second = "execution";
		OneEditAway oneEditAway = new OneEditAway();
		oneEditAway.oneEditAway(first, second);
	}

}
