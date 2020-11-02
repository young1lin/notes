package me.young1lin.netty.demo.wrap.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/2 8:51 下午
 */
public class LengthBasedInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 第一个参数是帧最大长度，第二参数是长度字段位偏移量，第三个参数是标示长度的字段的长度
        pipeline.addLast(new LengthFieldBasedFrameDecoder(64*1024,0,8));
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // Do something with the frame
            // 处理帧的数据
        }

    }

}
