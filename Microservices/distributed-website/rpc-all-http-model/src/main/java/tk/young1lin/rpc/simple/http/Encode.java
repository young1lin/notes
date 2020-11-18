package tk.young1lin.rpc.simple.http;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/2812:23 下午
 */
public enum Encode {
    /**
     * GBK编码
     */
    GBK((byte) 0),
    /**
     * UTF8编码
     */
    UTF8((byte) 1);

    /**
     * 对应值
     */
    private byte value;

    Encode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
