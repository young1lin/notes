package me.young1lin.netty.demo.encode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/29 10:15 下午
 */
public class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        // 越看越像 ChannelHandler，结果一看，缝合怪继承了 ChannelOutboundHandlerAdapter
        out.add(String.valueOf(msg));
    }

}
