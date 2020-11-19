package me.young1lin.algorithm.filter;


import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 布隆过滤器由一个长度为N的01数组array组成。首先将数组array每个元素初始设为0。
 * 对集合A中的每个元素w，做K次哈希，第i次哈希值对N取模得到一个index(i)，
 * 即index(i)=HASH_i(w)%N，将array数组中的array[index(i)]置为1。
 * 最终array变成一个某些元素为1的01数组。
 * </p>
 * BloomFilter Easy find some Object can be exists or not exists
 *
 * @author <a href="mailto:young1lin0108@gmail.com">young1lin</a>
 * @version 1.0
 * @since 2020/11/19 2:00 下午
 */
public class BloomFilter {

    /**
     * Hash times
     * ｜A｜ 代表集合 A 元素的个数
     * k = 3,|A| = 20
     * K*|A|/ln2 = 87
     */
    private final int k;

    /**
     * 表示每个 Key 占用的二进制 bit 数，若有 x 个 Key ，则 N=x*bitsPerKey
     */
    private final int bitsPerKey;

    private int bitLen;

    private byte[] result;

    public BloomFilter(int k, int bitsPerKey) {
        this.k = k;
        this.bitsPerKey = bitsPerKey;
    }

    /**
     * @param keys values
     * BloomFilter byte array generate
     */
    public void generate(byte[][] keys) {
        assert keys != null;
        bitLen = keys.length * bitsPerKey;
        // align the bitLen
        bitLen = ((bitLen + 7) / 8);

        bitLen = Math.max(bitLen, 64);
        // each byte have 8 bit
        result = new byte[bitLen >> 3];
        for (byte[] value : keys) {
            assert value != null;
            int h = Bytes.hash(value);
            for (int j = 0; j < k; j++) {
                int idx = (h % bitLen + bitLen) % bitLen;
                result[idx / 8] |= (1 << (idx % 8));
                int delta = (h >> 17) | (h << 15);
                h += delta;
            }
        }
    }

    /**
     * @param key key
     * @return true or false,true 表示可能含有这个值，false 表示一定不可能有这个值。
     */
    public boolean contains(byte[] key) {
        assert result != null;

        int h = Bytes.hash(key);
        for (int i = 0; i < k; i++) {
            int idx = (h % bitLen + bitLen) % bitLen;
            if ((result[idx / 8] & (1 << (idx % 8))) == 0) {
                return false;
            }
            int delta = (h >> 17) | (h << 15);
            h += delta;
        }
        return true;
    }

    private static final class Bytes {

        /**
         * copy hadoop HBase 的 Bytes
         *
         * @param bytes to be hash bytes
         * @return hash value
         */
        static int hash(byte[] bytes) {
            int length = bytes.length;
            int offset = 0;
            int hash = 1;
            for (int i = offset; i < offset + length; ++i) {
                hash = 31 * hash + bytes[i];
            }
            return hash;
        }
    }

    public static void main(String[] args) {
        byte[][] values = new byte[20][];
        int low = 48;
        int high = low + 20;
        // ASCII 表见 https://tool.oschina.net/commons?type=4
        for (int i = low; i < high; i++) {
            String tmpString = "" + i + (char) i;
            values[i - low] = tmpString.getBytes(StandardCharsets.UTF_8);
        }
        BloomFilter bf = new BloomFilter(3, 48);
        printlnArray(values);
        bf.generate(values);
        System.out.println("\n--------------------------------------------------------");
        System.out.println(bf.contains("480".getBytes(StandardCharsets.UTF_8)));
        System.out.println(bf.contains("491".getBytes(StandardCharsets.UTF_8)));
        System.out.println(bf.contains("60<".getBytes(StandardCharsets.UTF_8)));
        System.out.println(bf.contains("33321;".getBytes(StandardCharsets.UTF_8)));
    }

    private static void printlnArray(byte[][] values) {
        for (byte[] value : values) {
            String tmpString = new String(value, StandardCharsets.UTF_8);
            System.out.print(tmpString + "\t");
        }
    }
}
