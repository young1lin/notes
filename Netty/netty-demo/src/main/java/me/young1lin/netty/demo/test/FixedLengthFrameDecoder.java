package me.young1lin.netty.demo.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 9:23 下午
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if(frameLength < 0){
            throw new IllegalArgumentException("frameLength muse be a positive integer"+frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= frameLength){
            ByteBuf buf = in.readBytes(frameLength);
            out.add(buf);
        }
    }

}
