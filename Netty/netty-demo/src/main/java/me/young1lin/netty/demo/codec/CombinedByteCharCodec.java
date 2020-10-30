package me.young1lin.netty.demo.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/30 8:23 下午
 */
public class CombinedByteCharCodec extends CombinedChannelDuplexHandler<ByteToCharDecoder,CharToByteEncoder> {
    public CombinedByteCharCodec(){
        // 9 折?
        super(new ByteToCharDecoder(),new CharToByteEncoder());
    }
}
