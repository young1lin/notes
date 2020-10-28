package me.young1lin.netty.demo.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 8:28 下午
 */
public class Test2 {
    public static void main(String[] args) {
        new BootstrapDemo().channelOption();
    }
}
