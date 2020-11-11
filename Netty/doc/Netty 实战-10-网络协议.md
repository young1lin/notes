#  WebSocket

## 简介

看我的博客园的介绍。

**WebSocket 应用程序逻辑**
![WebSocket 应用程序逻辑 (1).png](https://i.loli.net/2020/11/02/hbxY2yZEQT3Fdnc.png)



## 添加 WebSocket 支持

从标准的 HTTP 或者 HTTPS 协议切换到 WebSocket 时，将会使用一种称为*升级握手的机制*（其实就是往请求头里塞两个信息）。

**服务器逻辑**

![服务器逻辑.png](https://i.loli.net/2020/11/02/5MLdJao63iejENp.png)

## 处理 HTTP 请求

***这个项目路径中间，不能有任何中文，否则就报错 ！！！！***

**HttpRequestHandler**

```java
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 如果请求了 WebSocket 协议升级，则增加引用计数（调用 retain 方法），并将它传递给下一个 ChannelInboundHandler
        if (wsUri.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            // 处理 100 continue请求以符合 HTTP 1.0 规范。
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            HttpResponse response = new DefaultHttpResponse(
                    request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");

            boolean keepAlive = HttpHeaders.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if(!keepAlive){
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx){
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
}
```

如果不需要压缩和加密，那么可以通过将 index.html 的内容存储到 DefaultFileRegion 中来达到最佳效率。这将会利用零拷贝特性进行内容的传输。

**处理文本帧**

```java
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 握手完成之后，去掉 HttpRequesthandler
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 广播到所有的 Channel
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        group.writeAndFlush(msg.retain());
    }

}
```

和之前一样，对于 retain 方法的调用是必需的，因为当 channelRead0 方法返回时，TextWebSocketFrame 的引用计数将会被减少。由于所有的操作都是异步的，因此，writeAndFlush 方法可能会在 channelRead0 方法返回之后完成，而且它绝对不能访问一个已经失效的引用。

## 初始化 ChannelPipeline

**初始化 ChannelPipeline**

```java
public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;

    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64 *1024));
        pipeline.addLast(new HttpRequestHandler("/ws"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }

}
```

**基于 WebSocket 聊天服务器的 ChannelHandler**

| ChannelHandler                 | Duty                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| HttpServerCodec                | 懂得都懂                                                     |
| ChunkedWriteHandler            | 写入一个文件的内容                                           |
| HttpObjectAggregator           | 将一个 HttpMessage 和跟随它的多个 HttpContent 聚合为单个 FullHttpRequest<br/>或者 FullHttpResponse （取决于它是被用来处理请求还是相应）。安装这个之后<br/>ChannelPipeline 中的下一个 ChannelHandler 将只会收到完整的 HTTP 请求或响应 |
| HttpRequestHandler             | 处理 FullHttpRequest （那些不发送到/ws URI 的请求）          |
| WebSocketServerProtocolHandler | 按照 WebSocket 规范的要求，处理 WebSocket 升级握手、PingWebSocketFrame<br/>、PongWebSocketFrame、和 CloseWebSocketFrame |
| TextWebSocketFrameHandler      | 处理 TextWebSocketFrame 和握手完成事件                       |



**WebSocket 协议升级之前的 ChannelPipeline**

![WebSocket 协议升级之前的 ChannelPipeline _1_.png](https://i.loli.net/2020/11/04/f4sajUBR1Fbxkl6.png)

**WebSocket 协议升级之后的 ChannelPipeline**

![WebSocket 协议升级之后的 ChannelPipeline.png](https://i.loli.net/2020/11/04/ncYP9guzC3JyFws.png)

Netty 目前支持 4 个版本的 WebSocket 协议，它们每个都具有自己的实现类。Netty 将会根据客户端（这里指浏览器）所支持的版本，自动地选择正确版本的 WebSocketFrameDecoder 和 WebSocketFrameEncoder 。这里假设选择 13 版本的。

## 引导

引导服务器，并安装 ChatSercerInitializer 的代码。

```java
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
    private final ChannelGroup channelGroup =  new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

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
```

**为 ChannelPipeline 添加加密**

```java
package me.young1lin.netty.demo.websocket.chat;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/4 11:14 下午
 */
public class SecureChatServerInitializer extends ChatServerInitializer {

    private final SslContext context;

    public SecureChatServerInitializer(ChannelGroup group, boolean isOriginal, SslContext context) {
        super(group, isOriginal);
        this.context = context;
    }


    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        SSLEngine sslEngine = context.newEngine(ch.alloc());
        sslEngine.setUseClientMode(false);
        ch.pipeline().addFirst(new SslHandler(sslEngine));
    }

}
```

```java
package me.young1lin.netty.demo.websocket.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/11/4 11:17 下午
 */
public class SecureChatServer extends ChatServer {

    private final SslContext context;

    public SecureChatServer(boolean isOriginal, SslContext context) {
        super(isOriginal);
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
        return new SecureChatServerInitializer(group,isOriginal,context);
    }

    public static void main(String[] args) throws CertificateException, SSLException {
        boolean isOriginal = true;
        int port = 9999;
        SelfSignedCertificate cert = new SelfSignedCertificate();
        SslContext context = SslContext.newServerContext(cert.certificate(),cert.privateKey());

        SecureChatServer endpoint = new SecureChatServer(isOriginal,context);
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }

}
```