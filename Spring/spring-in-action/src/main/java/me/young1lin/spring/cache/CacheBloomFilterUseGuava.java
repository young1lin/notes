package me.young1lin.spring.cache;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @since 2020/12/19 3:58 下午
 * @version 1.0
 */
public final class CacheBloomFilterUseGuava {

	private static final int size = 500;

	private static final BloomFilter<Integer> BLOOM_FILTER =
			BloomFilter.create(Funnels.integerFunnel(), size);

	public static void main(String[] args) {
		putAllDataCache();
		System.out.println(BLOOM_FILTER.mightContain(12));
		System.out.println(BLOOM_FILTER.mightContain(777));
	}

	private static void putAllDataCache() {
		for (int i = 0; i < size; i++) {
			BLOOM_FILTER.put(i);
		}
	}

}
