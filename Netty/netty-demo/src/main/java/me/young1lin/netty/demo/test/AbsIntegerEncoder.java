package me.young1lin.netty.demo.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 9:59 下午
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        while (msg.readableBytes() >= 4){
            // int 类型是 4 byte == 32 bit 的
            int value = Math.abs(msg.readInt());
            out.add(value);
        }
    }

}
