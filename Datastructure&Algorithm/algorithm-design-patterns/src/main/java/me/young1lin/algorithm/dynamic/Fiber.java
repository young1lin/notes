package me.young1lin.algorithm.dynamic;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/28 下午3:36
 * @version 1.0
 */
public class Fiber {

	/**
	 * 换成自定向上求解
	 */
	public static int fib(int n) {
		if (n == 1 || n == 2) {
			return 1;
		}
		int[] cache = new int[n + 1];
		cache[1] = 1;
		cache[2] = 1;
		for (int i = 3; i <= n; i++) {
			cache[i] = cache[i - 1] + cache[i - 2];
		}
		return cache[n];
	}

	/**
	 * 带字典去求解
	 */
	public static int fib2(int n) {
		if (n < 1) {
			return 0;
		}
		int[] cache = new int[n + 1];
		return helper(cache, n);
	}

	static int helper(int[] cache, int n) {
		if (n == 1 || n == 2) {
			return 1;
		}
		int cacheInt = cache[n];
		if (cacheInt != 0) {
			return cacheInt;
		}
		cache[n] = helper(cache, n - 1) + helper(cache, n - 2);
		return cache[n];
	}

	public static void main(String[] args) {
		System.out.println(fib(11));
	}
}
