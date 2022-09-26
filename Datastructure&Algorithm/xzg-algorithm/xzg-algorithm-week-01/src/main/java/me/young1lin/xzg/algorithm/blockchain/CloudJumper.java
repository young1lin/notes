package me.young1lin.xzg.algorithm.blockchain;

/**
 * Jumping on the Clouds
 *
 * Emma is playing a new mobile game involving out on cloud , and they must jump to cloud or cloud .
 *
 * clouds numbered from to . A player initially starts . In each step, she can jump from any cloud to cloud
 *
 * There are two types of clouds, ordinary clouds and thunderclouds. The game ends if Emma jumps onto a thundercloud, but if she reaches the last cloud (i.e., ), she wins the game!
 *
 * Can you find the minimum number of jumps Emma must make to win the game? It is guaranteed that clouds and are ordinary-clouds and it is always possible to win the game.
 *
 * Input Format
 *
 * The first line contains an integer, (the total number of clouds).
 *
 * The second line contains space-separated binary integers describing clouds
 *
 * .
 *
 * If, the
 *
 * If
 *
 * , the
 *
 * cloud is an ordinary cloud.
 *
 * cloud is a thundercloud.
 *
 * Constraints
 *
 * Output Format
 *
 * Print the minimum number of jumps needed to win the game.
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2022/9/21 21:38
 * @version 1.0
 */
public class CloudJumper {

	public static void main(String[] args) {
		int[] clouds = new int[] {0, 0, 1, 0, 0, 1, 0};
		//int[] clouds = new int[] {0, 0, 0, 0, 1, 0};
		int n = clouds.length - 1;
		int minimumStep = getMinimumSteps(n, clouds);
		System.out.println(minimumStep);
	}

	private static int getMinimumSteps(int n, int[] clouds) {
		int minimumSteps = 0;
		for (int i = -1; i <= n; ) {
			if (i + 2 <= n && clouds[i + 2] == 0) {
				i += 2;
			}
			// if it's thundercloud, will be step one
			else {
				i += 1;
			}
			minimumSteps++;
			if (i == n) {
				break;
			}
		}
		return minimumSteps;
	}

}
