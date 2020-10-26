package me.young1lin.netty.demo.channel.handler;

import io.netty.channel.*;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/26 9:13 下午
 */
public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(!future.isSuccess()){
                    future.cause().printStackTrace();
                    future.channel().close();
                }
            }
        });
    }
}
