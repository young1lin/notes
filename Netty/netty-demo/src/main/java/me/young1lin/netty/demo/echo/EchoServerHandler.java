package me.young1lin.netty.demo.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import javax.swing.plaf.synth.SynthTextAreaUI;

/**
 * {@link Sharable} 标记一个 ChannelHandler 可以被多个 Channel 安全地共享
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/15 12:02 上午
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.printf("server received msg: [%s]\n", in.toString(CharsetUtil.UTF_8));
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 如果不捕获异常
     * 每个 Channel 都拥有一个与之相关联的 ChannelPipeline，其持有一个 ChannelHandler 的实例链。在默认的情况下，ChannelHandler 会把
     * 它的方法的调用转发给链中的下一个 ChannelHandler。因此，如果 exceptionCaught() 方法没有被该链中的某处实现，那么所接收的异常将会被传递
     * 到 ChannelPipeline 的尾端并被记录。所以，应用程序最少应该有一个实现了 exceptionCaught() 方法的 ChannelHandler。
     *
     * @param ctx   ctx
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
