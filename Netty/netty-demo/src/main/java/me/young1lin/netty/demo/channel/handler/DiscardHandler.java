package me.young1lin.netty.demo.channel.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/23 8:20 下午
 */
@ChannelHandler.Sharable
public class DiscardHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        // 丢弃已接收的消息
        ReferenceCountUtil.release(msg);
    }
}
