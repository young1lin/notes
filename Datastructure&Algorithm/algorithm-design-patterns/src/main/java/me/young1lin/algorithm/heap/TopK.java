package me.young1lin.algorithm.heap;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * 这个还是挺简单的，会用 PriorityQueue 就行了。
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2021/1/25 下午4:42
 * @version 1.0
 */
public class TopK {

	private final int k;

	private final PriorityQueue<Integer> queue;

	TopK(int k, int[] data) {
		this.queue = new PriorityQueue<>((o1, o2) -> {
			// 其实这里不用写，默认就是这样的，默认维护一个小顶堆。如果需要改成大顶堆，那么就把 1 和 -1 互换就行了。
			if (o1 > o2) {
				return 1;
			}
			else if (o1 < o2) {
				return -1;
			}
			return 0;
		});
		//this.queue = new PriorityQueue<>();
		this.k = k;
		initQueue(data);
	}

	private void initQueue(int[] data) {
		for (int tmp : data) {
			if (queue.size() < k) {
				queue.add(tmp);
			}
			else {
				if (queue.peek() != null) {
					int minValue = queue.peek();
					if (minValue < tmp) {
						queue.poll();
						queue.add(tmp);
					}
				}
			}
		}
	}

	int[] getTopK() {
		int[] result = new int[k];
		int index = 0;
		while (!queue.isEmpty()) {
			result[index++] = queue.poll();
		}
		return result;
	}

	public static void main(String[] args) {
		int[] data = new int[] {51, 5, 1, 55, 61, 61, 166, 122, 123123, 233, 2133};
		int k = 5;
		TopK topK = new TopK(k, data);
		int[] arr = topK.getTopK();
		System.out.println(Arrays.toString(arr));
	}

}