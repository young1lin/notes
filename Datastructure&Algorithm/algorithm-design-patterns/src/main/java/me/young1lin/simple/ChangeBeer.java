package me.young1lin.simple;

/**
 * 现有 x 瓶啤酒，每 3 个空瓶子换一瓶啤酒，每 7 个瓶盖子也可以换一瓶啤酒，最后可以喝多少瓶啤酒。
 *
 * 给定一个确定 x 值，那这就是一道小学数学题。如果 x 值很小，很容易求出，x 很大，需要逻辑足够清晰才能给出正确答案。
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/5/17 下午11:10
 * @version 1.0
 */
public class ChangeBeer {

	public int drink(int x) {
		// 有多少瓶啤酒
		int count = x;
		// 空瓶子
		int k1 = x;
		// 瓶盖
		int k2 = x;
		while (k1 >= 3 || k2 >= 7) {
			while (k1 >= 3) {
				int change = k1 / 3;
				count += change;
				k1 %= 3;
				k1 += change;
				k2 += change;
			}
			while (k2 >= 7) {
				int change = k2 / 7;
				count += change;
				k2 %= 7;
				k1 += change;
				k2 += change;
			}
		}
		return count;
	}

	public static void main(String[] args) {
		ChangeBeer changeBeer = new ChangeBeer();
		int[] beerNum = new int[] {1, 5, 6, 79, 0, 6, 7};
		for (int num : beerNum) {
			System.out.println(changeBeer.drink(num));
		}
	}

}
