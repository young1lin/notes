package me.young1lin.netty.demo.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/2 12:25 上午
 */
public class LineBasedHandlerInitializer  extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 该 LineBasedFrameDecoder 将提取的帧转法给下一个 ChannelInboundHandler
        // 数值为接受的最大长度
        pipeline.addLast(new LineBasedFrameDecoder(64*1024));
        pipeline.addLast(new FrameHandler()) ;
    }

    public static final class  FrameHandler extends SimpleChannelInboundHandler<ByteBuf>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        }

    }

}
