package me.young1lin.netty.demo.udp.monitor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.young1lin.netty.demo.udp.LogEvent;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/5 9:56 下午
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent event) throws Exception {
        System.out.println(event.toString());
    }

}
