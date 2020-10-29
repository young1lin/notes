package me.young1lin.netty.demo.decode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/29 9:25 下午
 */
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        // 9 折？这么简单？
        out.add(String.valueOf(msg));
    }

}
