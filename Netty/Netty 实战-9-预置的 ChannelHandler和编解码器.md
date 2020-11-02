# 通过 SSL/TLS 保护 Netty 应用程序

+ 并非只有 HTTPS 可以有这个安全协议
+ Java 提供了 javax.net.ssl 包，它的 SSLContext 和 SSLEngine 类使得实现解密和加密相当简单直接。Netty 通过一个名为 SslHandler 的 ChannelHandler 实现利用了这个 API，其中 SslHandler 在内部使用 SSLEngine 来完成实际的工作。

**Netty 的 OpenSSL/SSLEngine 实现**

*Netty 还提供了使用 OpenSSL 工具包 SSLEngine 实现。这个 OpenSsl-Engine 类提供了比 JDK 提供的 SSLEngine 实现更好的性能。*

*默认用 Netty 的，如果不可用，退回到 JDK 的。* 

![通过 SslHandler 进行解密和加密的数据流 (1).png](https://i.loli.net/2020/10/30/TchiWrCPljao5fd.png)



**添加 SSL/TLS 支持**

```java
public class SslChannelInitializer extends ChannelInitializer<Channel> {

    private final SslContext context;

    private final boolean startTls;

    public SslChannelInitializer(SslContext context, boolean startTls) {
        this.context = context;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine engine = context.newEngine(ch.alloc());
        ch.pipeline().addFirst("ssl",new SslHandler(engine,startTls));
    }

}
```

在大多数情况下 SslHandler 将是 ChannelPipeline 中的第一个 ChannelHandler。

SslHandler 具有一些有用的方法。在握手阶段，两个节点相互握手后一旦完成之后提供通知，握手阶段完成之后，所有的数据都将会被加密。SSL/TLS 握手将会被自动执行。

**SslHandler 的方法** 书上的 long 是基本类型的，我为了更清楚的展示，写成了包装类型的。

| MethodName                                                   | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| setHandshakeTImeout(Long,TimeUnit)<br/>setHandshakeTimeoutMills(Long)<br/>getHandshakeTimeoutMillis() | 设置和获取超时时间，超时之后，握手 ChannelFuture 将会被通知失败 |
| setCloseNotifyTimeout(Long,TimeUnit)<br/>setCloseNotifyTimeoutMillis(long)<br/>getCloseNotifyTImeoutMills() | 超时之后，将会触发一个关闭通知并关闭连接。这也将会导致通知该 ChannelFuture 失败 |
| handshakeFuture                                              | 返回一个在握手完成后将会得到通知的 ChannnelFuture。如果握手先前已经执行过了，则返回一个包含了先前的握手结果的 ChannelFuture |
| close()<br/>close(ChannelPromise)<br/>close(ChannelHandlerContext,ChannelPromise) | 发送 close_notify 以请求关闭并销毁底层的 SslEngine           |

# 构建基于 Netty 的 HTTP/HTTPS 应用程序

## HTTP 解码器、编码器、编解码器

**HTTP 请求的组成部分**

![HTTP 请求的组成部分.png](https://i.loli.net/2020/10/30/ka2ew1TlLD9iHo6.png)

**HTTP 响应的组成部分**

![HTTP 响应的组成部分.png](https://i.loli.net/2020/10/30/hpXPOWZg2l4m1jQ.png)

一个 HTTP 请求/响应可能由多个数据部分组成，并且它总是以一个 LastHttpContent 部分作为结束。FullHttpRequest 和 FullHttpResponse 消息是特殊的子类型，分别代表了完整的请求和响应。所有类型的 HTTP 消息（FuulHttpRequest，LastHttpContent 以及上面展示那些）都实现了 HttpObject 接口。

**HTTP 解码器和编码器**

| MethodName          | Description |
| ------------------- | ----------- |
| HttpRequestEncoder  | 见名知意    |
| HttpResponseEncoder |             |
| HttpRequestDecoder  |             |
| HttpResponseDecoder |             |

再次说明，Encoder 是将消息编码，Decoder 是将消息解码。和序列化和反序列化是差不多的。

编码：Java 对象 -> bytes

解码：bytes -> Java 对象

```java
public class HttpPipelineInitializer extends ChannelInitializer<Channel> {

    private final boolean client;

    public HttpPipelineInitializer(boolean client){
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(client){
            // 客户端对请求消息编码，对响应消息解码
            pipeline.addLast("decoder",new HttpResponseDecoder());
            pipeline.addLast("encoder",new HttpRequestEncoder());
        }else {
            // 服务端，对请求消息解码，对响应消息编码
            pipeline.addLast("decoder",new HttpRequestDecoder());
            pipeline.addLast("encoder",new HttpResponseEncoder());
        }
    }
}
```

## 聚合 HTTP 消息

由于消息分段需要被缓冲，直到可以转发一个完整的消息给下一个 ChannelInboundHandler，所以这个操作还有轻微的开销。其所带来的好处便是不必关心消息碎片。

引入这种自动聚合机制，只不过是向 ChannelPipeline 中添加一个 ChannelHandler 罢了。

```java
public class HttpAggregatorInitializer extends ChannelInitializer<Channel> {

    private final boolean isClient;

    public HttpAggregatorInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(isClient){
            pipeline.addLast("codec",new HttpClientCodec());
        }else {
            pipeline.addLast("codec",new HttpServerCodec());
        }

        // 将最大的消息大小为 521 kb 的 HttpObjectAggregator 添加到 ChannelPipeline
        pipeline.addLast("aggregator",new HttpObjectAggregator(512*1024));
    }
}
```

真相了，这两个编解码器继承自 CombinedChannelDuplexHandler 之前看到过的这个类。

HttpObjectAggregator 继承自 MessageToMessageDecoder ->ChannelInboundHandlerAdapter。下面是关系类图

![image.png](https://i.loli.net/2020/10/30/2hnAmSlbZDPLOoq.png)



## HTTP 压缩

Netty 为压缩和解压缩提供了 ChanneHandler 实现，它们同时支持 gzip 和 deflate 编码。

**HTTP 请求头部信息**

是客户端在 HTTP Header 加上 Accept-Encoding: gzip,deflate

服务器没有义务压缩它发给客户端的信息。

**自动压缩 HTTP 消息**

```java
public class HttpCompressionInitializer extends ChannelInitializer<Channel> {

    private final boolean isClient;

    public HttpCompressionInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(isClient){
            pipeline.addLast("codec",new HttpClientCodec());
            pipeline.addLast("decompressor",new HttpContentDecompressor());
        }else {
            pipeline.addLast("codec",new HttpServerCodec());
            pipeline.addLast("compressor",new HttpContentCompressor());
        }
    }

}
```

JDK 6 版本及以下需要加 jzlib 依赖。

## 使用 HTTPS

**使用 HTTPS**

```java
public class HttpCodecInitializer extends ChannelInitializer<Channel> {

    private final boolean isClient;

    private final SslContext context;

    public HttpCodecInitializer(boolean isClient, SslContext context) {
        this.isClient = isClient;
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine sslEngine = context.newEngine(ch.alloc());
        pipeline.addLast("ssl",new SslHandler(sslEngine));

        if(isClient){
            pipeline.addLast("codec",new HttpClientCodec());
        }else {
            pipeline.addLast("codec",new HttpServerCodec());
        }
    }

}
```

这扩展性和 Spring 有的一比了。BeanPostProccesor 和这个 ChannelHandler 差不多，应该都是通过一个有序集合（例如 ArrayList）存储，然后遍历执行方法，应该不是递归操作。

## WebSocket

WebSocket 采用 ws 协议，其实是第一次发送 http 请求，在 http 请求头部中 为`Connection:Upgrade`,`Upgrade:websocket` 通知服务器将 http 请求升级为 ws/wss 协议。下面的也可以改成 socket = new WebSocket(url，protocols)。其中 url 必填，protocols 可选参数，参数为 string | string[] ，其中 string 为可使用的协议，包括 SMPP，SOAP 或者自定义的协议。

可以看我的[博客](https://www.cnblogs.com/young1lin/p/11167328.html)介绍 WebSocket，我把 《HTML5 WebSocket 权威指南》看完了才写的。挺简单的，书上还有整合 RabbitMQ 的示例，太水了。其实就大体上两步骤

1. 先发一个普通的 Http 请求，然后请求头部带`Connection:Upgrade`,`Upgrade:websocket`，服务器表示收到，有 B 数了返回服务器支持 WebSocket 协议。
2. 然后就建立的连接。

**js 的代码如下**

```javascript
var socket;
if (typeof(WebSocket) == "undefined") {
    console.log("您的浏览器不支持WebSocket");
} else {
    // 是的，这样就可以开启了页面的 WebSocket，也就是客户端
    //实例化 WebSocket对象，指定要连接的服务器地址与端口  建立连接
    let socketUrl = "ws://wsproject/im/123";
    socket = new ReconnectingWebSocket(socketUrl, null, {
        debug: false,
        reconnectInterval: 3000
    });
}
```



![WebSocket 协议.png](https://i.loli.net/2020/10/30/A1sWSGJYKQqzZ75.png)



WebSocket 以帧来进行传输，可以传输文本、二进制、Continuation、Close消息、ping、和pong

**WebSocketFrame 类型**

| ClassName                  | Description                        |
| -------------------------- | ---------------------------------- |
| BinaryWebSocketFrame       |                                    |
| TextWebSocketFrame         |                                    |
| ContinuationWebSocketFrame | 属于上一个二进制帧或者文本帧的数据 |
| CloseWebSocketFrame        |                                    |
| PingWebSocketFrame         | 请求一个 PongWebSocketFrame        |
| PongWebSocketFrame         | 对 PingWebSocketFrame 请求的响应   |

**在服务器端支持 WebSocket**

```java
public class WebSocketServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(
                new HttpServerCodec()
                , new HttpObjectAggregator(65536)
                , new WebSocketServerProtocolHandler("/hello")
                , new BinaryFrameHandler()
                , new TextFrameHandler()
                , new ContinuationFrameHandler()
        );
        
    }

    public static final class TextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        }

    }

    public static final class BinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {

        }

    }

    public static final class ContinuationFrameHandler extends SimpleChannelInboundHandler<ContinuationWebSocketFrame>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ContinuationWebSocketFrame msg) throws Exception {

        }

    }

}
```

# 空闲的连接和超时

**用于空闲连接以及超时的 ChannelHandler**

| ClassName           | Description                                                  |
| ------------------- | ------------------------------------------------------------ |
| IdleStateHandler    | 空闲时间太长，触发 IdleStateEvent 事件，可以在 ChannelInboundHandler 中重写 userEventTriggered 方法来处理该事件 |
| ReadTimeoutHandler  | 在指定时间间隔内没有收到任何的**入站**数据，则抛出 ReadTimeoutException 并关闭对应的 Channel。重写 exceptionCaught 方法检测该异常。 |
| WriteTimeoutHandler | 如果在指定的时间间隔内没有任何**出站**数据写入。则抛出 WriteTimeoutException 并关闭对应的 Channel。捕捉异常同上。 |

**发送心跳**

```java
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(
                // 读心跳时间，写心跳时间，所有心跳时间
                new IdleStateHandler(0,0,60, TimeUnit.SECONDS));
        pipeline.addLast(new HeartBeatHandler());
    }

    public static final class HeartBeatHandler extends ChannelInboundHandlerAdapter {
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("HEARTBEAT",CharsetUtil.UTF_8));

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if(evt instanceof IdleStateEvent){
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }else {
                super.userEventTriggered(ctx,evt);   
            }
        }
    }

}
```

# 解码给予分隔符的协议和机遇长度的协议

## 基于分隔符的协议

**用于处理基于分隔符的协议和基于长度的协议的解码器**

| Class Name                 | Description                                                  |
| -------------------------- | ------------------------------------------------------------ |
| DelimiterBasedFrameDecoder | 使用任何由用户提供的分隔符来提取帧的通用解码器               |
| LineBasedFrameDecoder      | 提取由行尾符（\n 或 \r\n）分隔的帧的解码器。这个解码器比上面的更快 |

**处理由行尾符分隔的帧**

![由行尾符分隔的帧.png](https://i.loli.net/2020/11/02/fInQSy2l8vErM7a.png)

处理由行尾符分隔的帧

```java
public class LineBasedHandlerInitializer  extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 该 LineBasedFrameDecoder 将提取的帧转法给下一个 ChannelInboundHandler
        // 数值为接受的最大长度
        pipeline.addLast(new LineBasedFrameDecoder(64*1024));
        pipeline.addLast(new FrameHandler()) ;
    }

    public static final class  FrameHandler extends SimpleChannelInboundHandler<ByteBuf>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        }
        
    }

}
```

下面是示例的协议规范

+ 传入数据流是一系列的帧，每个帧都由换行符（\n）分隔；
+ 每个帧都由一系列的元素组成，每个元素都由单个空格字符分隔；
+ 一个帧的内容代表一个命令，定义一个命令名称后跟着数目可变的参数。

用于这个协议的自定义解码器将定义以下类。

+ Cmd——将帧的内容存储在 ByteBuf 中，一个 ByteBuf 用于名称，另一个用于名称；
+ CmdDecoder——从被重写了的 decode 方法中获取一行字符串，并从他的内容构建一个 Cmd 的实例；
+ CmdHandler——从 CmdDecoder 获取解码的 Cmd 对象，并对它进行一些处理；
+ CmdHandlerInitializer——为了简便起见，我们将会把前面的这些定义为专门的  ChannelInitializer 的嵌套类，其将会把这些 ChannelInboundHandler 安装到 ChannelPipline 中。

**使用 ChannelInitializer 安装解码器**

```java
public class CmdHandlerInitializer extends ChannelInitializer<Channel> {

    static final byte SPACE = (byte) ' ';

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new CmdDecoder(64 * 1024));
        pipeline.addLast(new CmdHandler());
    }

    public static class Cmd {

        private final ByteBuf name;

        private final ByteBuf args;

        public Cmd(ByteBuf name, ByteBuf args) {
            this.name = name;
            this.args = args;
        }

        public ByteBuf name() {
            return name;
        }

        public ByteBuf args() {
            return args;
        }

    }


    public static final class CmdDecoder extends LineBasedFrameDecoder {

        public CmdDecoder(int maxLength) {
            super(maxLength);
        }

        @Override
        protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
            ByteBuf frame = (ByteBuf) super.decode(ctx, buffer);
            if (frame == null) {
                return null;
            }
            int index = frame.indexOf(frame.readerIndex(), frame.writerIndex(), SPACE);
            return new Cmd(frame.slice(frame.readerIndex(), index), frame.slice(index + 1, frame.writerIndex()));
        }

    }

    public static final class CmdHandler extends SimpleChannelInboundHandler<Cmd>{


        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
            // 处理对象
        }

    }
}
```

## 基于长度的协议

基于长度的协议通过将它的长度编码到帧的头部来定义帧，而不是使用特殊的分隔符来标记它的结束。

**用于基于长度的协议的解码器**

| ClassName                    | Description                                                  |
| ---------------------------- | ------------------------------------------------------------ |
| FixedLengthFrameDecoder      | 提取在调用构造函数时指定的定长帧                             |
| LengthFieldBasedFrameDecoder | 根据编码进帧头部中的长度值提取帧：该字段的偏移量以及长度在构造函数中指定 |

**解码长度为 8 字节的帧**

![解码长度为 8 字节的帧.png](https://i.loli.net/2020/11/02/kb6R5UavqQ1zjYD.png)

会遇到被编码到消息头部的帧大小不是固定之的协议。为了处理这种变长帧，可以使用 LengthFieldBasedFrameDecoder，它将从头部字段确定帧长，然后从数据流中国呢提取指定的字节数。

**将变长帧大小编码进头部的消息** 		我是觉得有点像 Class 文件的无符号数，前面标明了后面的内容长度。

![将变长帧大小编码进头部的消息.png](https://i.loli.net/2020/11/02/ejIEBX4YrWqHLSk.png)

LengthFieldBasedFrameDecoder 提供了几个构造函数来支持各种各样的头部配置情况。

**使用 LengthFieldBasedFrameDecoder 解码器基于长度的协议**

```java
public class LengthBasedInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 第一个参数是帧最大长度，第二参数是长度字段位偏移量，第三个参数是标示长度的字段的长度
        pipeline.addLast(new LengthFieldBasedFrameDecoder(64*1024,0,8));
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // Do something with the frame
            // 处理帧的数据
        }

    }

}
```

# 写大型数据

由于写操作是非阻塞的，所以即使没有写出所有的数据，写操作也会在完成时返回并通知 ChannelFuture。当这种情况发生时，如果仍然不停地写入，就有内存耗尽的风险，所以在写大型数据时，需要准备好处理到远程节点的连接时慢速连接的情况，这种情况会导致内存释放的延迟，

前面提到了 NOI 的零拷贝特性，这种特性消除了将文件的内容从文件系统移动到网络栈的复制过程。所有的这一切都发生在 Netty 的核心中，所以应用程序所有需要做的就是使用一个 FileRegion 接口的实现，其在 Netty 的 API 文档中的定义是：”通过支持零拷贝的文件传输的 Channel 来发送的文件区域。“

**使用 FileRegion 传输文件的内容**

```java
File file = new File(System.getProperty("user.dir") + File.separator + "pom.xml");
FileInputStream in = new FileInputStream(file);
FileRegion region = new DefaultFileRegion(
        in.getChannel(), 0, file.length());

ch.writeAndFlush(region).addListener(new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            cause.printStackTrace();
        }
    }
});
```

这个示例知识用于文件内容的直接传输，不包括应用程序对数据的任何处理。在需要将数据从文件系统复制到用户内存中时，可以使用 ChunkedWriteHandler，它是支持异步写大型数据，而又不会导致大量的内存消耗。

关键是 ChunkedInput<B> （接口），其中类型参数 B 是 readChunk 方法返回的类型 Netty 预置了该接口的 4 个实现，如表 11-7 中所列出的，每个都代表了一个将有 ChunkedWriteHandler 处理的不定长度的数据流。

下面说明了 ChunkedStream 的用法，它是实践中最常用的实现。所示的类使用了一个 File 以及一个 SslContexgt 进行实例化。当 initChannel 方法被调用时，它将使用所示 的ChannelHandler 链初始化该 Channel。

**ChunkedInput 的实现**

| Class Name       | Decription                                                   |
| ---------------- | ------------------------------------------------------------ |
| ChunkedFile      | 从文件中逐块获取数据，如果当前平台不支持零拷贝，或者需要住那还数据时使用 |
| ChunkedNioFIle   | 和上面类似，只是使用了 FileChannel                           |
| ChunkedStream    | 从 InputStream 中逐块传输内容                                |
| ChunkedNioStream | 从 ReadableByteChannel 中逐块传输内容                        |

当 Channel 的状态变为活动时，WriteStreamHandler 将会逐块地把来自文件中的数据作为 ChunkedStream 写入。数据在传输之前将会由 SslHandler 加密。

**使用 ChunkedStream 传输文件内容**

```java
public class ChunkedWriteHandlerInitializer extends ChannelInitializer<Channel> {

    private final File file;
    
    private final SslContext sslContext;

    public ChunkedWriteHandlerInitializer(File file, SslContext sslContext) {
        this.file = file;
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new SslHandler(sslContext.newEngine(ch.alloc())));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new WriteStreamHandler());
    }

    public final class WriteStreamHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            ctx.writeAndFlush(new ChunkedStream(new FileInputStream(file)));
        }

    }

}
```

**逐块输入**要是哟睾自己的 ChunkedInput 实现，请在 ChannelPipeline 中安装一个 ChunkedWriteHandler

# 序列化数据

JDK 提供了 ObjectInputStream 和 ObjectOutputStream 用于通过网络对 POJO 的基本类型和图进行序列化和反序列化。在《大型分布式网站架构与实战》中就讲到了，这个有安全问题，并且容易制造网络传输“炸弹”，导致整个应用的资源都耗在反序列化上。推荐 JSON 的和 Hessian。

## JDK 序列化

**JDK 序列化编解码器**

| Class Name                        | Description                                                  |
| --------------------------------- | ------------------------------------------------------------ |
| CompatibleObjectDecoder（已废弃） | 和使用 JDK 序列化的非基于 Netty 的远程节点进行互操作的解码器 |
| CompatibleObjectEncoder           | 和使用 JDK 序列化的非基于 Netty 的远程节点进行互操作的编码器 |
| ObjectDecoder                     | 构建于 JDK 序列化之上的使用自定义的序列化来解码的解码器；当没有其他的外部依赖时，它提供了速度上的改进。否则其他的序列化更加可取。 |
| ObjectEncoder                     | 同上                                                         |

## 使用 JBoss Marshalling 进行序列化

算是对原生 JDK 序列化一种优化。

**JBoss Marshalling 编解码器**

| Class Name                                                   | Description                                       |
| ------------------------------------------------------------ | ------------------------------------------------- |
| CompatibleMarshallingDecoder<br/>CompatibleMarshallingEncoder | 与只使用 JDK 序列化的远程节点兼容                 |
| MarshallingDecoder<br/>MarshallingEncoder                    | 适用于使用 JBoss Marshalling 的节点，必须一起使用 |

**使用 JBoss Marshalling **

```java
public class MarshallingInitializer extends ChannelInitializer<Channel> {

    private final MarshallerProvider marshallerProvider;

    private final UnmarshallerProvider unmarshallerProvider;

    public MarshallingInitializer(MarshallerProvider marshallerProvider
            , UnmarshallerProvider unmarshallerProvider) {
        this.marshallerProvider = marshallerProvider;
        this.unmarshallerProvider = unmarshallerProvider;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MarshallingDecoder(unmarshallerProvider));
        pipeline.addLast(new MarshallingEncoder(marshallerProvider));
        pipeline.addLast(new ObjectHandler());

    }

    public static final class ObjectHandler extends SimpleChannelInboundHandler<Serializable>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Serializable msg) throws Exception {
            // Do something
        }

    }

}
```

## 通过 Protocol Buffers 序列化

由 Google 公司开发，已经开源的数据交换格式。

以一种紧凑而高效的方式对结构化的数据进行编码以及解码，可以跨语言传输。

**Protobuf 编解码器**

| Class Name                           | Description                                                  |
| ------------------------------------ | ------------------------------------------------------------ |
| ProtobufDecoder                      | 使用 protobuf 对消息进行解码                                 |
| ProtobufEncoder                      | 编码                                                         |
| ProtobufVarint32FrameDecoder         | 根据消息中的 Google Protocol Buffers 的 “Base128 Varints”整型长度字段值动态地分割所接收到的 ByteBuf |
| ProtobufVarint32LengthFieldPropender | 向 ByteBuf 前追加一个 Google Protocol Buffers 的“Base 128 Varints” 整型的长度字段值 |

本质上编解码就是特殊的 ChannelHandler，通过 ChannelPipeline 添加，然后处理其中传输的消息。

