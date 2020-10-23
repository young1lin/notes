package me.young1lin.netty.demo.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * {@link Sharable} 标记该类的实例可以被多个 Channel 共享
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/15 11:45 下午
 */
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 将在一个连接建立时被调用。
     *
     * @param ctx 上下文
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("77777", CharsetUtil.UTF_8));
    }

    /**
     * 每当接收数据时，都会调用这个方法。由服务器发送的消息可能会被分块接收。
     *
     * @param channelHandlerContext 上下文
     * @param in                    读取的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) {
        System.out.printf("Client received: [%s] \n", in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 这里应该不是直接关闭，而是重试几次，如果是正常应用的话
        ctx.close();
    }
}
