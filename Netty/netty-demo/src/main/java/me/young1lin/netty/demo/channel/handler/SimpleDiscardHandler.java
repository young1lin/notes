package me.young1lin.netty.demo.channel.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/23 8:22 下午
 */
public class SimpleDiscardHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // No need to do anything special
        // 查看父类就知道，这个只是重写了 ChannelInboundHandlerAdapter 的 channelRead 方法，然后里面帮你写好了 release 的方法
        // 缝合怪罢了
    }
}
