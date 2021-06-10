package me.young1lin.netty.demo.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioDatagramChannel;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/27 9:03 下午
 */
public class BootstrapDemo {

	public static void main(String[] args) {
		NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.channel(NioSocketChannel.class)
				.handler(new SimpleChannelInboundHandler<ByteBuf>() {
					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
						System.out.println("Received data!");
					}
				});
		ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8080));
		future.addListener((ChannelFutureListener) future1 -> {
			if (future1.isSuccess()) {
				System.out.println("Connection attempt established");
			}
			else {
				System.err.println("Connection attempt failed");
				future1.cause().printStackTrace();
			}
		});
	}

	/**
	 * 兼容性测试
	 */
	public void compatibilityTest() {
		NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.channel(OioSocketChannel.class)
				.handler(new SimpleChannelInboundHandler<ByteBuf>() {
					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
						System.out.println("Received data");
					}
				});
		ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8080));
		future.syncUninterruptibly();
	}


	public void serverTest() {
		NioEventLoopGroup group = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(group)
				.channel(NioServerSocketChannel.class)
				// Simple 的会在调用结束后 release ByteBuf
				.childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
						System.out.println("Received data");
					}
				});
		ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					System.out.println("Server bound");
				}
				else {
					System.err.println("Bound attempt failed");
					future.cause().printStackTrace();
				}
			}
		});
	}

	public void serverSharedChannelTest() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
				.channel(NioServerSocketChannel.class)
				.childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
					ChannelFuture channelFuture;

					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						Bootstrap bootstrap = new Bootstrap();
						bootstrap.channel(NioSocketChannel.class)
								.handler(new SimpleChannelInboundHandler<ByteBuf>() {
									@Override
									protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
										System.out.println("Receive data");
									}
								});
						// 使用于分配给已被接受的子 Channel 相同的 EventLoop
						bootstrap.group(ctx.channel().eventLoop());
						channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 8080));
					}

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
						if (channelFuture.isDone()) {
							// 当连接完成时，执行一些数据操作（如代理）
							// do something with the data
						}
					}
				});
		ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				if (channelFuture.isSuccess()) {
					System.out.println("Server bound");
				}
				else {
					System.err.println("Bind attempt failed");
					channelFuture.cause().printStackTrace();
				}
			}
		});
	}

	/**
	 * 添加多个 ChannelHandler
	 *
	 * @throws InterruptedException
	 */
	public void channelInitializerTest() throws InterruptedException {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializerImpl());
		ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
		future.sync();

	}

	final class ChannelInitializerImpl extends ChannelInitializer<Channel> {

		@Override
		protected void initChannel(Channel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new HttpClientCodec());
			pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
		}
	}

	public void channelOption() {
		AttributeKey<Integer> id = AttributeKey.newInstance("ID");
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup())
				.channel(NioSocketChannel.class)
				.handler(new SimpleChannelInboundHandler<ByteBuf>() {
					@Override
					public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
						Integer idValue = ctx.channel().attr(id).get();
						// do something with the idValue
						System.out.printf("idValue is : %s", idValue);
					}

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
						System.out.println("Received data");
					}
				});
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		bootstrap.attr(id, 123456);
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(8080));
		future.syncUninterruptibly();
	}

	public void bootstrapAndDataGramChannel() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup())
				.channel(OioDatagramChannel.class)
				.handler(new SimpleChannelInboundHandler<DatagramPacket>() {

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
						System.out.println("Receive data");
					}

				});
		ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(0));
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					System.out.println("Channel bound");
				}
				else {
					System.err.println("Bind attempt failed");
					future.cause().printStackTrace();
				}
			}
		});
	}

	public void shutdown() {
		NioEventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group)
				.channel(NioSocketChannel.class)
				.handler(new SimpleChannelInboundHandler<ByteBuf>() {
					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
						System.out.println("Receive data");
					}
				});
		Future<?> future = group.shutdownGracefully();
		// block until the group has shutdown
		future.syncUninterruptibly();
	}
}
