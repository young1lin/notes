package me.young1lin.algorithm.dynamic;


/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/28 下午3:38
 * @version 1.0
 */
public class Coin {

	/**
	 *
	 * @param coins 币种
	 * @param amount 目标金额
	 * @return 多少枚硬币
	 */
	public static int coinChange(int[] coins, int amount) {
		if (coins.length == 0) {
			return -1;
		}
		int[] memo = new int[amount + 1];
		memo[0] = 0;
		for (int i = 1; i <= amount; i++) {
			int min = Integer.MAX_VALUE;
			for (int coin : coins) {
				if (i - coin >= 0 && memo[i - coin] < min) {
					min = memo[i - coin] + 1;
				}
			}
			memo[i] = min;
		}
		return memo[amount] == Integer.MAX_VALUE ? -1 : memo[amount];
	}

	/**
	 * 这个提交到 LeetCode 超时，我想不到的
	 * @param coins 币种
	 * @param amount 目标金额
	 * @return 多少枚硬币
	 */
	public static int coinChangeTwo(int[] coins, int amount) {
		int[] cache = new int[amount + 1];
		return helper(cache, amount, coins);
	}

	private static int helper(int[] cache, int amount, int[] coins) {
		if (amount == 0) {
			return 0;
		}
		if (amount < 0) {
			return -1;
		}
		if (cache[amount] != 0) {
			return cache[amount];
		}
		int res = Integer.MAX_VALUE;
		for (int coin : coins) {
			int subProblem = helper(cache, amount - coin, coins);
			if (subProblem == -1) {
				continue;
			}
			res = Math.min(res, 1 + subProblem);
		}
		if (res != Integer.MAX_VALUE) {
			cache[amount] = res;
		}
		else {
			return -1;
		}
		return helper(cache, amount, coins);
	}


	public static void main(String[] args) {
		int[] coins = new int[] {65, 7};
		int amount = 177;
		System.out.println(coinChange(coins, amount));
		System.out.println(coinChangeTwo(coins, amount));

	}

}
