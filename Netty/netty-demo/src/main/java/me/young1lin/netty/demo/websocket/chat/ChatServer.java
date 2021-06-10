package me.young1lin.netty.demo.websocket.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * @author linyoung
 * @version 1.0
 * @date 2020/11/4 10:58 下午
 */
public class ChatServer {

	/**
	 * 创建 DefaultChannelGroup，其将保存所有已经连接的 WebSocket Channel
	 */
	private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

	private final EventLoopGroup group = new NioEventLoopGroup();

	private Channel channel;

	protected final boolean isOriginal;

	public ChatServer(boolean isOriginal) {
		this.isOriginal = isOriginal;
	}

	public ChannelFuture start(InetSocketAddress address) {
		//引导服务器
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(group)
				.channel(NioServerSocketChannel.class)
				.childHandler(createInitializer(channelGroup));
		ChannelFuture future = bootstrap.bind(address);
		future.syncUninterruptibly();
		channel = future.channel();
		return future;
	}

	/**
	 * 创建 ChatServerInitializer
	 */
	protected ChannelInitializer<Channel> createInitializer(
			ChannelGroup group) {
		return new ChatServerInitializer(group, isOriginal);
	}

	/**
	 * 处理服务器关闭，并释放所有的资源
	 */
	public void destroy() {
		if (channel != null) {
			channel.close();
		}
		channelGroup.close();
		group.shutdownGracefully();
	}

	public static void main(String[] args) throws Exception {
		boolean isOriginal = true;
		int port = 9999;
		final ChatServer endpoint = new ChatServer(isOriginal);
		ChannelFuture future = endpoint.start(
				new InetSocketAddress(port));
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				endpoint.destroy();
			}
		});
		future.channel().closeFuture().syncUninterruptibly();
	}

}
