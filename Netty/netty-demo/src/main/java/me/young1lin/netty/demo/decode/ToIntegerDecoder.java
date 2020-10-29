package me.young1lin.netty.demo.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/29 8:51 下午
 */
public class ToIntegerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) {
            // 这里会自动装箱成 Integer，因为 List 中不允许添加基础类型，编译的时候会默认添加 Integer.valueOf(in.readInt) 方法
            out.add(in.readInt());
        }
    }

}
