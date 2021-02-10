package me.young1lin.offer.list;

/**
 *《算法》 105 页
 * 约瑟夫问题
 * 在这个古老的问题中，N 个身陷绝境的人一致同意通过一下方式减少生存人数。他们围坐成一圈（位置记为 0 到 N-1）
 * 并从第一个人开始报数，报到 M 的人会被杀死，直到最后一个人留下来。传说中 Josephus 找到了不会被杀死的位置。
 * 编写一个 Queue 的用力 Josephus，从命令行接受 N 和 M 并打印出人们被杀死的顺序（这也将显示 Josephus 在
 * 圈中的位置）
 *
 * Josephus 7 2
 * 1 3 5 0 4 2 6
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/2/5 上午5:23
 * @version 1.0
 */
public class JosephusProblem {

	/**
	 * https://blog.csdn.net/u011500062/article/details/72855826
	 * f(N,M)=(f(N−1,M)+M)%N
	 * @param n 人数
	 * @param m 每报到M时杀掉那个人
	 * @return 不会被杀的位置
	 */
	int find(int n, int m) {
		int p = 0;
		for (int i = 2; i <= n; i++) {
			p = (p + m) % i;
		}
		return p + 1;
	}

	public static void main(String[] args) {
		JosephusProblem p = new JosephusProblem();
		System.out.println(p.find(11,3));
	}

}
