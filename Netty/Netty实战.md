# NIO 简单介绍

NIO 最开始是新的输入/输出（New Input/Output）的英文缩写，但是，该 Java API 已经出现足够长的时间了，不再是“新的”了，因此，如今大多数的用户认为 NIO 代表非阻塞 I/O（Non-blocking I/O），而阻塞的 I/O（blocking I/O）是旧的输入/输出（old input/output，OIO）。你也可能遇到它被称为普通 I/O（plain I/O）的时候。

# Netty 特性


|  分类  | Netty的特性                                                  |
|  :---  | :---  |
| 设计 | 统一的 API，支持多种传输类型，阻塞的和非阻塞的<br/>简单而强大的线程模型<br/>真正的无连接数据报套接字支持<br/>链接逻辑组件以支持复用 |
| 易于使用 | 翔实的 Javadoc 和大量的示例集<br/>不需要超过 JDK 1.6+ 的依赖。（一些可选的特性可能需要 Java 1.7+ 和/或额外的依赖） |
| 性能 | 拥有比 Java 的核心的 API 更高的吞吐量以及更低的延迟<br/>得益于池化和服用，拥有更低的资源消耗<br/>最少的内存复制 |
| 健壮性 | 不会因为慢速、快速或者超载的连接而导致OOM<br/>消除在高速网络中 NIO 应用程序常见的不公平读/写比率 |
| 安全性 | 完整的 SSL/TLS 以及 SmartTLS 支持<br/>可用于受限环境下，如 Applet 和 OSGI |
| 社区驱动 | 发布快速而且频繁 |

# Netty 的核心组件

+ Channel
+ 回调
+ Future
+ 事件和 ChannelHandler

## Channel

Channel 是 Java NIO 的一个基本构造。

它代表一个到实体（如一个硬件设备、一个文件、一个网络套接字或者一个能够执行一个或者多个不同的 I/O 操作的程序组件）的开放连接，如读操作和写操作。

目前，可以把 Channel 看作是传入（入站）或者传出（出站）数据的载体，因此，它可以被打开或者被关闭，连接或者断开连接。

## 回调

一个回调其实就是一个方法，一个指向已经被提供给另外一个方法的方法的引用。这使得后者可以在适当的时候调用前者，回调在广泛的编程场景中都有应用，而且也是在操作完成后通知相关方最常见的方式之一。

```java

public interface ICallback {
  void methodToCallback();
}

public class BClass {
  public void process(ICallback callback) {
    //...
    callback.methodToCallback();
    //...
  }
}

public class AClass {
  public static void main(String[] args) {
    BClass b = new BClass();
    b.process(() -> { //回调对象
      @Override
      public void methodToCallback() {
        System.out.println("Call back me.");
      }
    });
  }
}
```

```java
public class ConnectHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        // 当一个新的连接已经被建立时，channelActive(ChannelHandlerContext)将会被调用
        System.out.printf("Client %s connected \n",ctx.channel().remoteAddress());
    }
}
```

Netty 在内部使用了回调来处理事件，当一个回调被触发时，相关的事件可以被一个 interfaceChannelHandler 的实现处理。

## Future

Future 提供了另一种在操作完成时通知应用程序的方式。这个对象可以看作是一个异步操作的结果的占位符，它将在未来的某个时刻完成，并提供对其结果的访问。

JDK 预置了 interface java.util.concurrent.Future，但是其所提供的实现，只允许手动检查对应的操作是否已经完成，或者一直阻塞直到它完成。Netty 提供了它自己的实现——ChannelFuture，用于在执行异步操作的时候使用。

ChannelFuture（接口） 提供了几种额外的方法，这些方法使得我们能够注册一个或者多个

ChannelFutureListener（接口） 实例。监听器的回调方法 operationComplete()，将会在对应的操作完成时被调用（如果在 ChannelFutureListener 添加在已完成的 ChannelFuture 的时候，前者会被直接地通知）。然后监听器可以判断该操作是成功地完成了还是出错了。如果是后者，我门可以检索产生的 Throwable。简而言之，由 ChannelFutureListener 提供的通知机制消除了手动检查对应的操作是否完成的必要。

每个 Netty 的出战 I/O 操作都将返回一个 ChannelFuture，也就是说，它们都不会阻塞。Netty 完全是异步和事件驱动的。

## 事件和 ChannelHandler

Netty 使用不同的事件来通知我们状态的改变或者是操作的状态。这使得我们能够基于已经发生的事件来触发适当的动作。这些动作可能是

+ 记录日志
+ 数据转换
+ 流控制
+ 应用程序逻辑

Netty 是一个网络编程框架，所以事件是按照它们与入站或出战数据流的相关性进行分类的。可能由入站数据或者相关的状态更改而触发的事件包括：

+ 连接已被激活或者连接失活
+ 数据读取
+ 用户事件
+ 错误事件

出战事件是由未来将会触发的某个动作的操作结果，这些动作包括

+ 打开或者关闭到远程节点的连接
+ 将数据写到或者冲刷到套接字


![Netty实战-流经 ChannelHandler 链的入站事件和出站事件 (1).png](https://i.loli.net/2020/10/14/fUtNMYO6P21FRiW.png)

Netty 的 ChannelHandler 为处理器提供了基本的抽象。

Netty 提供了大量预定义的可以开箱即用的 ChannelHandler 实现，包括用于各种协议的 ChannelHandler。在内部，ChannelHandler 自己也使用了事件 Future，使得它们也成了你的应用程序将使用的相同的抽象的消费者。



## 选择器、事件和 EventLoop

Netty 通过触发事件将 Selector 从应用程序中抽象出来，消除了所有本来将需要手动编写的派发代码。



# 编写 EchoServer

所有的 Netty 服务器都需要以下两部分

+ 至少一个 ChannelHandler —— 该组件实现了服务器对从客户端接收的数据的处理，即它的业务逻辑
+ 引导——这是配置服务器的启动代码。至少，它会将服务器绑定到它要监听连接请求的端口上。

## ChannelHandler 和业务逻辑

EchoServer 会响应传入的消息，所以它需要实现 ChannelInboundHandler 接口，用来定义入站事件的方法。因为只需要用到少量方法，所以继承 ChannelInboundHandlerAdapter 类就够了，它提供了 ChannelInboundHandler 的默认实现。

```java
package me.young1lin.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

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
        System.out.printf("server received msg :%s\n", in.toString(CharsetUtil.UTF_8));
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
```

## EchoServer

**传输**

在网络协议的标准多层视图中，传输层提供了端到端的活着主机到主机的通信服务。

出了一些由 Java NIO 实现提供的服务器端性能增强之外， NIO 传输大多数指的就是 TCP 之上。

**应用层，传输层，网络互连层，网络访问(链接)层 Network Access(link) laye**


![TCP/IP_Protocol.png](https://i.loli.net/2020/10/15/QWDC8PHZRobkSr5.png)


```java
package me.young1lin.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/15 12:13 上午
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.err.println("Error");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    private void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 创建 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        // 创建 ServerBootstrap
        ServerBootstrap b = new ServerBootstrap();
        b.group(group)
            // 指定所使用的 NIO 传输 Channel
            .channel(NioServerSocketChannel.class)
            // 使用指定的端口设置套接字地址
            .localAddress(new InetSocketAddress(port))
            // 添加一个 EchoServerHandler 到子 Channel 的 ChannelPipeline
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    // EchoServerHandler 被标注胃 @Shareable，所以总是可以使用同样的实例
                    socketChannel.pipeline().addLast(serverHandler);
                }
            });
        try {
            // 异步地绑定服务器，调用 sync() 方法阻塞等待直到绑定完成
            ChannelFuture f = b.bind().sync();
            // 获取 Channel 的 CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭 EventLoopGroup，释放所有的资源
            group.shutdownGracefully().sync();
        }

    }
}
```

### ChannelInitializer

当一个新的连接被接受时，一个新的子 Channel 将会被创建，而 ChannelInitializer 将会吧一个你的 EchoServerHandler 的实例添加到该 Channel 的 ChannelPipeline 中。这个 ChannelHandler 将会收到有关入站消息的通知。

## Echo 客户端

1. 连接到服务器
2. 发送一个或者多个消息
3. 对于每个消息，等待并接收从服务器发回的相同的消息
4. 关闭客户端连接

### 通过 ChannelHandler 实现客户端逻辑

扩展 SimpleChannelInboundHandler 类

重写 

+ channelActive() —— 在到服务器的连接已经建立之后将被调用
+ channelRead0() —— 当从服务器接收到一条消息时被调用
+ exceptionCaught —— 在处理过程中引发一场时被调用

EchoClientHandler

```java
package me.young1lin.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * {@link Sharable} 标记该类的实例可以被多个 Channel 共享
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/15 11:45 下午
 */
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 将在一个连接建立时被调用。
     *
     * @param ctx 上下文
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    /**
     * 每当接收数据时，都会调用这个方法。由服务器发送的消息可能会被分块接收。
     *
     * @param channelHandlerContext 上下文
     * @param in                    读取的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) {
        System.out.printf("Client received %s \n", in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 这里应该不是直接关闭，而是重试几次，如果是正常应用的话
        ctx.close();
    }
}
```

为什么客户端和服务端两个 Handler 不同

在客户端，当 ChannelRead0() 方法完成时，已经有传入消息，并且已经处理完它了，当该方法返回时，SimpleChannelInboundHandler 负责释放指向保存该消息的 ByteBuf 的内存饮用。

在 EchoServerHandler 中，你仍然需要将传入消息回送给发送者，而 write() 操作时异步的，直到 channelRead() 方法返回后可能仍然没有完成。为此 EchoServerHandler 扩展了 ChannleInboundHandlerAdapter，其在这个时间点上不会释放消息。

消息在 EchoServerHandler 的 channelReadComplete() 方法中，当 writeAndFlush() 方法被调用时释放。

### EchoClient

```java
package me.young1lin.netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Echo 程序客户端
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/15 11:24 下午
 */
public class EchoClient {

    private final String host;

    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new EchoClientHandler());
                    }
                });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println("参数必须为两个，一个为 host，一个为 port");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}
```

# Netty 的组件和设计

+ Channel —— Socket

+ EventLoop —— 控制流、多线程处理、并发
+ ChannelFuture —— 异步通知 

## Channel （渠道）接口

基本的 I/O 操作（bind()、connect()、read() 和 write() ）依赖于底层网络传输所提供的原语。在基于 Java 网络编程中，其基本的构造是 class Socket。Netty 的 Channel 接口所提供的 API，大大降低了直接使用 Socket 类的复杂性。Channel 也拥有许多预定义的、专门实现的广泛类层次结构的根，例如

+ EmbeddedChannel
+ LocalServerChannel
+ NioDatagramChannel
+ NioSctpChannel
+ NioSocketChannel

## EventLoop （事件循环） 接口

EventLoop 定义了 Netty 的核心抽象，用于处理连接的生命周期中所发生的事件。

+ 一个 EventLoopGroup 包含一个或者多个 EventLoop
+ 一个 EventLoop 在它的生命周期内只和一个 Thread 绑定
+ 所有由 EventLoop 处理的 I/O 事件都将在它专有的 Thread 上被处理
+ 一个 Channel 在它的生命周期内只注册一个 EventLoop
+ 一个 EventLoop 可能会被分配给一个或多个 Channel


![Netty实战-Channel、EventLoop 和 EventLoopGroup.png](https://i.loli.net/2020/10/20/AcXKtJIwRBaTq2e.png)

## ChannelFuture 接口

其 addListener() 方法注册了一个 ChannelFutureListener，以便在某个操作完成时（无论是否成功）得到通知。

类似 FutureTask

# ChannelHandler 和 ChannnelPipeline

## ChannelHandler 接口

和上面的 EchoServer 和 EchoClient 用法一样，充当了所有处理入站和出站数据的应用程序逻辑的容器。

它的子类接口有 ChannelInboundHandler 和 ChannelOutboundHandler。

每个都有对应的适配器类，为了简化开发，提供了默认实现。

+ ChannelHandlerAdapter

+ ChannelInboundHandlerAdapter

+ ChannelOutboundHandlerAdapter

+ ChannelDuplexHandler

  

## ChannelPipeline（管道） 接口

ChannelPipeline 为 ChannelHandler 链提供了容器，并定义了用于在该链上传播入站和出站事件流的 API。当 Channel 被创建时，它会被自动地分配到它专属的 ChannelPipeline。

ChannelHandler 就是管道里面的执行逻辑，由管道进行保证顺序。

在 Netty 中，有两种发送消息的方式。可以直接写到 Channel 中，也可以写到和 ChannelHandler 相关联 的 ChannelHandlerContext 对象中。前者将会导致消息从 ChannelPipeline 的尾端开始流动，后者是 ChannelPipeline 中的下一个 ChannelHandler 开始流动。

## 编码器和解码器

Netty 发送或者接收一个消息的时候，就将会发生一次数据转换。入站消息会被解码，从字节转换为另一种格式，通常是 Java 对象。出战则相反。

对入站消息来说，channelRead 已经被重写。出战消息类似。

## 抽象类 SimpleChannelInboudHandler

最常见的是用一个 ChannelHandler 来接收和处理消息。继承他，是个很好的方法

最重要的方法是 channelRead0

```java
    /**
     * 每当接收数据时，都会调用这个方法。由服务器发送的消息可能会被分块接收。
     *
     * @param channelHandlerContext 上下文
     * @param in                    读取的数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) {
        System.out.printf("Client received: [%s] \n", in.toString(CharsetUtil.UTF_8));
    }
```

# 传输

## 使用 NIO 创建 server 端代码

```java
package me.young1lin.netty.demo.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 使用 NIO 创建非阻塞的客户端，很复杂
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/10/20 12:57 上午
 */
public class PlainNioServer {

    public void server(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket socket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        socket.bind(address);
        // 打开 Selector 来处理 Channel
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        final ByteBuffer msg = ByteBuffer.wrap("Hi\r\n".getBytes());
        for (; ; ) {
            try {
                // 等待需要处理的新事件；阻塞将一直持续到下一个传入事件
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            // 获取所有接收事件的 SelectionKey
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    // 检查事件是否是一个新的已经就绪可以被接受的连接
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        // 接受客户端，并将他注册到选择器
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("Accepted connection from" + client);
                        if (key.isWritable()) {
                            SocketChannel clientWrite = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            while (buffer.hasRemaining()) {
                                if (client.write(buffer) == 0) {
                                    break;
                                }
                            }
                            clientWrite.close();
                        }
                        // 这是我自己加的，书上有误
                        client.close();
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                        // 忽略
                    }
                    ex.printStackTrace();
                }
            }
        }
    }
}

```

## 使用 Netty 创建 OIO 和 NIO 客户端

### OIO

```java
package me.young1lin.netty.demo.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/20 1:13 上午
 */
public class NettyOioServer implements Server{

    @Override
    public void server(int port) throws IOException {
        final ByteBuf buf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
        EventLoopGroup group = new OioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(buf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            try {
                // 释放所有资源
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### NIO

```java
package me.young1lin.netty.demo.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/20 1:26 上午
 */
public class NettyNioServer  implements Server{

    @Override
    public void server(int port) throws IOException {
        final ByteBuf buf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
        // 这里也是
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    // 这个地方和 OIO 不一样
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(buf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            try {
                // 释放所有资源
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## 传输 API

传输 API 的核心是 interface Channel，它被用于所有的 I/O 操作。

ChannelHandler 的典型用途包括

+ 将数据从一种格式转换为另一种格式
+ 提供异常的通知
+ 提供 Channel 变为活动的或者非活动的通知
+ 提供当 Channel 注册到 EventLoop 或者从 EventLoop 注销时的通知
+ 提供有关用户自定义事件的通知

Channel 的方法


| 方法名 | 描述 |
|  ----  | ----  |
| eventLoop | 返回分配给 Channel 的 EventLoop |
| pipeline | 返回分配给 Channel 的 ChannelPipeline |
| isActive | 如果 Channel 是活动的，则返回 true。活动的意义可能依赖于底层的传输。例如一个Socket传输<br/>一旦连接到了远程节点便是活动的，而一个 Datagram 传输一旦被打开，便是活动的 |
| localAddress | 返回本地 SocketAddress |
| remoteAddress | 返回远程SocketAddress |
| write | 将数据写到远程节点。这个数据将被传递给 ChannelPipeline，并且排队直到它被冲刷 |
| flush | 将之前已写的数据冲刷到底层传输，入一个 Socket |
| writeAndFlush | 等同于调用 write+flush |

# 内置的传输

Netty 内置了一些开箱即用的传输。因为并不是它们所有的传输都支持每一种协议，所以必须选择一个和应用程序所使用的协议相容的传输。

| 方法名   | 包                            | 描述                                                         |
| -------- | ----------------------------- | ------------------------------------------------------------ |
| NIO      | io.netty.channel.socket.nio   | 使用 java.nio.channels 包作为基础——基于选择器的方式          |
| Epoll    | io.netty.channel.socket.epoll | 由 JNI 驱动的 epoll() 和非阻塞 IO。这个传输支持只有在 Linux 上可用的多种特性，如<br/>SO_REQUESPORT，比 NIO 传输更快，而且完全是非阻塞的。（我记得在 window 下降级到 select） |
| OIO      | io.netty.channel.soket.oio    | 使用 java.net 包作为基础——使用阻塞流。（新版本的 Netty 全部将其包下的内容给标记为已废除了） |
| Local    | io.netty.channel.local        | 可以在 VM 内部通过管道进行通信的本地传输                     |
| Embedded | io.netty.channel.embedded     | Embedded 传输，允许使用 ChannelHandler 而又不需要一个真正的基于网络的传输。<br/>这在测试 ChannelHandler 实现时非常有用。 |

## NIO

选择器的基本概念时充当一个注册表，在那里可以请求在 Channel 的状态发生变化时得到通知。可能发生的变化有：

+ 新的 Channel 已被接受并且就绪
+ Channel 连接已经完成
+ Channel 有已经就绪的可供读取的数据
+ Channel 可用于写数据

选择器运行在一个检查状态变化并对其作出相应响应的线程上，在应用程序对状态的改变做出响应之后，选择器将会被充值，并将重复这个过程。对应的 SelectionKey 定义的位模式常量如下，这些位模式剋组合起来定义一组应用程序正在请求通知的状态变化集

| 名称       | 描述                                                         |
| ---------- | ------------------------------------------------------------ |
| OP_ACCEPT  | 请求在接受新连接并创建 Channel 时获得通知                    |
| OP_CONNECT | 请求在建立一个连接时获得通知                                 |
| OP_READ    | 请求当数据已经就绪，可以从 Channel 中读取时获得通知          |
| OP_WRITE   | 请求当可以向 Channel 中写更多的数据时获得通知。这处理了套接字缓冲区被完全填满时的情况，<br/>这种情况通常发生在数据的发送速度比远程节点可处理的速度更快的时候。 |


![image.png](https://i.loli.net/2020/10/20/EI19tOx7h56A48B.png)

### 零拷贝（面试重点）

零拷贝（zero-copy）是一种目前只有在使用 NIO 和 Epoll 传输才可使用的特性。它使你可以快速高效地将数据从文件系统移动到网络接口，而不需要将其从内核空间复制到用户空间，其在像 FTP 或者 HTTP 这样的协议中可以显著地提升性能。但是，并不是所有的操作系统都支持这一特性。特别地，它对于实现了数据加密或者压缩的文件系统是不可用的——只能传输文件的原始内容。反过来说，传输已被加密的文件则不是问题。

## Epoll——用于 Linux 的本地非阻塞传输

Epoll——一个高度可扩展的 I/O 事件通知特性。这个 API 自 Linux 内核版本 2.5.44（2002）被引入。提供了比旧的 POSIX select 和 poll 系统调用更好的性能，同时现在也是 Linux 上非阻塞网络编程的事实标准。Linux JDK NIO API 使用了这些 epoll 调用。

如果要在之前的代码清淡中使用 epoll 代替 NIO，只需要将 NioEventLoopGroup 替换成 EpollEventLoopGroup ，并且将 NioServerSocketChannel.class 替换为 EpollServerSocketChannel.class 即可。

## OIO

基于 java.net 包的阻塞实现之上，现在已经不推荐使用

## 用于 JVM 内部通信的 Local 传输

在这个传输中，和服务器 Channel 相关联的 SocketAddress 并没有绑定无力网络地址，相反，只要服务器还在运行，它就会被存储在注册表里，并在 Channel  关闭时注销。

## Embedded 传输

Netty 提供了一种额外的传输，使得可以将一组 ChannelHandler 作为帮助器类嵌入到其他的 ChannelHandler 内部。通过这种方式，可以扩展一个 ChannelHandler 功能，

而不需要修改其内部的代码。

EmbeddedChannel 是具体实现。

# 传输用例

支持的传输和网络协议

| 传输  | TCP  | UDP  | SCTP | UDT  |
| ----- | ---- | ---- | ---- | ---- |
| NIO   | ✓    | ✓    | ✓    | ✓    |
| Epoll | ✓    | ✓    | ✗    | ✗    |
| OIO   | ✓    | ✓    | ✓    | ✓    |

在 Linux 上启用 SCTP

SCTP 需要内核的支持，并且需要安装用户库。

例如 Ubuntu

`# sudo apt-get install libsctp1`

# ByteBuf

ByteBuf —— Netty 的数据容器

JDK 的 ByteBuffer 替代品是 Netty 的 ByteBuf，一个强大的实现，既解决了 JDK API 的局限性，又为网络应用程序的开发者提供了更好的 API。

## ByteBuf 的API

Netty 的数据处理 API 通过两个组件暴露—— abastract class ByteBuf 和 interface ByteBufHolder。

下面是一些 ByteBuf API 的优点。

+ 它可以被用户自定义的缓冲区类型扩展
+ 通过内置的复合缓冲区类型实现了透明的零拷贝
+ 容量可以按需增长（类似于 JDK 的 StringBuilder）StringBuilder 的 append 方法最终还是调用 System#arraycopy
+ 在读和写这两种模式之间不需要调用 ByteBuffer 的 flip() 方法
+ 读和写使用了不同的索引
+ 支持方法的链式调用
+ 支持引用计数
+ 支持池化

## ByteBuf 类 ——Netty 的数据容器

书上应该说的是 AbstractByteBuf，ByteBuf 是一个没有任何实体方法的抽象类。

```java
public abstract class AbstractByteBuf extends ByteBuf {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractByteBuf.class);
    private static final String LEGACY_PROP_CHECK_ACCESSIBLE = "io.netty.buffer.bytebuf.checkAccessible";
    private static final String PROP_CHECK_ACCESSIBLE = "io.netty.buffer.checkAccessible";
    static final boolean checkAccessible;
    private static final String PROP_CHECK_BOUNDS = "io.netty.buffer.checkBounds";
    private static final boolean checkBounds;
    static final ResourceLeakDetector<ByteBuf> leakDetector;
    // 读索引
    int readerIndex;
    // 写索引
    int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
    // 最大容量
    private int maxCapacity;

    protected AbstractByteBuf(int maxCapacity) {
        ObjectUtil.checkPositiveOrZero(maxCapacity, "maxCapacity");
        this.maxCapacity = maxCapacity;
    }
}
```

![Netty实战-一个读索引和写索引都设置为 0 的16 字节 ByteBuf.png](https://i.loli.net/2020/10/21/Ih9z513xJNZ7TKL.png)

ByteBuf 维护了两个不同的索引（我觉得应该叫指针比较合适），一个用于写入，一个用于读取（上面代码）。当你从 ByteBuf 读取时，它的 readerIndex 将会被递增已经被读取的字节数。当写入 ByteBuf 时，它的 writerIndex 也会被递增。

当 readerIndex > writerIndex 会触发一个 IndexOutOfBoundsException。下面代码。**以 read 和 write 开头的 ByteBuf 方法，将会推进其对应的索引，而以 set 或 get 开头的方法则不会**。指定 ByteBuf 最大容量，默认是 Integer.MAX_VALUE。

```java
private static void checkIndexBounds(int readerIndex, int writerIndex, int capacity) {
    if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > capacity) {
        throw new IndexOutOfBoundsException(String.format("readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))", readerIndex, writerIndex, capacity));
    }
}

public ByteBuf readerIndex(int readerIndex) {
    if (checkBounds) {
        checkIndexBounds(readerIndex, this.writerIndex, this.capacity());
    }

    this.readerIndex = readerIndex;
    return this;
}
```

## ByteBuf 的使用模式

1. **堆缓冲区**

最常用的 ByteBuf 模式是将数据存储在 JVM 的堆空间中。这种模式被称为支撑数据（backing array），它能在没有池化的情况下提供快速的分配和释放。非常适合于有遗留的数据需要处理的情况。

```java
ByteBuf heapBuf = ...;
if(heapBuf.hasArray()){
    byte[] array = heapBuf.array();
    // 计算第一个字节的偏移量
	int offset = heapBuf.arrayOffest() + heapBuf.readerIndex();
	int length = heapBuf.readableBytes();
    handleArray(array,offset,length);
}
```

2. **直接缓冲区**

NIO 在 JDK1.4 中引入的 ByteBuff 类允许 JVM 实现通过本地调用来分配内存。主要是为了避免每次调用本地 I/O 操作之前或者之后将缓冲区的内容复制到一个中间缓冲区（或者从中间缓冲区把内容复制到缓冲区）。

直接缓冲区的内容将驻留在常规的会被垃圾回收的堆之外。

主要缺点就是，相对于基于堆的缓冲区，它们的分配和释放都比较昂贵。如果在处理遗留代码，可能会有另一个缺点，如下面的代码所示，因为数据不是在堆上，所以不得不进行一次复制。

```java
ByteBuf directBuf = ...;
if(heapBuf.hasArray()){
	int length = directBuf.readableBytes();
    byte[] array = new byte[length];
    directBuf.getBytes(directBuf.readerIndex,array);
    handleArray(array,0,length);
}
```

3. **复合缓冲区**

在这里可以根据需要添加或者删除 ByteBuf 实例，这是一个 JDK 的ByteBuffer 实现完全缺失的特性。

通过一个子类 CompositeByteBuf 实现了这个模式，它提供了一个将多个缓冲区表示为单个合并缓冲区的虚拟表示。

*上面这个实例的 ByteBuf 可能同时包含直接内存分配和非直接内存分配。如果其中只有一个实例，那么对 CompositeByteBuf 上的 hasArray 方法的调用将返回该组件上的 hasArray 方法的值，否则它将返回 false*

Http head 信息和 body 信息就可以放在这个实例中。

```java
// CompositeByteBuf#hasArray
public boolean hasArray() {
    switch(this.componentCount) {
    case 0:
        return true;
    case 1:
        return this.components[0].buf.hasArray();
    // 我懂了，这里如果有两个组件，就会返回 false，因为是复合类型的       
    default:
        return false;
    }
}
```

**使用 ByteBuffer 的复合缓冲区模式**

分配和复制操作，以及伴随着对数组管理的需要，使得这个版本的实现效率低下而笨拙。

```java
ByteBuffer[] message = new ByteBuffer[]{header,body};
ByteBuffer message2 = ByteBuffer.allocate(headr.remaining+body.remaining);
message2.put(header);
message2.put(body);
message2.flip();
```

**使用 CompositeByteBuf 的复合缓冲区模式**

CompositeByteBuf 可能不支持访问其支撑数组，因此 CompositeByteBuf 中的数据类似于（访问）直接缓冲区的模式。

```java
CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
ByteBuf headerBuf = ...;
ByteBuf bodyBuf = ....;
messageBuf.addComponenets(headBuf,bodyBuf);
....;
// 移除 head 信息
// 删除位于索引位置为 0 的（第一个组件） ByteBuf
messageBuf.removeComponent(0);
for(ByteBuf buf : messageBuf){
    System.out.println(buf.toString());
}
```

**访问 CompositeByteBuf 中的数据**

Netty 使用了 CompositeByteBuf 来优化套接字的 I/O 操作，尽可能地消除了由 JDK 的缓冲区实现所导致的性能以及内存使用率的惩罚。

```java
CompositeByteBuf compBuf = Unpooled.compositeBuffer();
int length = compBuf.readableBytes();
byte[] array = new byte[length];
compBuf.getBytes(compBuf.readerIndex(),array);
handleArray(array,0,array.length);
```

## 字节级操作

### 随机访问索引

其实就是支持根据索引来访问

```java
ByteBuf buffer = ...;
for(int i = 0,capacity = buffer.capacity();i < capacity; i++){
    byte b = buffer.getByte(i);
    System.out.println((char)b);
}
```

### 顺序访问索引

JDK 的 ByteBuffer 只有一个索引。

![Netty实战-ByteBuf 的内部分段.png](https://i.loli.net/2020/10/21/5gICGNLkopF4lQ3.png)

### 可丢弃字节

可丢弃字节分段包含了已经被读过的字节。通过调用 discardReadBytes() 方法，可以丢弃它们并回收空间。这个分段的初始大小为0，存储在 readerIndex 中，会随着 read 操作的执行而增加。

调用 discardReadBytes() 极有可能导致内存复制。我觉得是类似调用 System#arrayCopy 那样，保持原来的不变，直接 copy 一份。

**建议只在由真正需要的时候才这样做，例如当内存非常宝贵的时候。**

### 可读字节

ByteBuf 的可读字节分段存储了实际数据。

```java
ByteBuf buffer = ...;
while(buffer.isReadable()){
    System.out.println(buffer.readByte());
}
```

### 可写字节

调用 writeBytes(ByteBuf dest);

调用 write 开头的方法可以写数据，并且移动 writeIndex。

如果可写的字节小于写入的字节，就会导致 IndexOutOfBoundsException。

```java
ByteBuf buffer = ...;
// 这里判断剩余可写字节是否大于等于 4。
while(buffer.writeableBytes() >= 4){
    buffer.writeInt(random.nextInt());
}
```

### 索引管理

JDK 的 InputStream 定义了 mark(int readlimit) 和  reset() 方法，这些方法分别被用来将流中的当钱位置标记为指定的值，以及将流充值到该位置。

markReaderIndex()、markWriterIndex()、resetWriterIndex、restReaderIndex 和上面的类似。只是没有 readlimit 参数来制定标记什么时候失效。

也可以通过 readerIndex(int)、writerIndex(int) 传入值来将索引移动到指定位置，如果移动到一个无效的位置都会抛出 IndexOutOfBoundsException

通过调用 clear() 方法将两个索引重置为 0。这里并不会清楚内存中的内容。

clear 比 discardReadBytes 轻量得多，它将知识重置索引而不会复制任何的内容。

它这不算重置，应该叫两个索引都移动到可写字节区域的开始，并且 capcity 变成之前可写字节的大小，两个索引值都变成 0。

### 查找操作

用 indexOf，传入 ByteBufProcessor（4.1后已经被标记为废弃） 查找的方法，不适合。

### 派生缓冲区

派生缓冲区为 ByteBuf 提供了以专门的方式来呈现其内容的视图。这类视图是通过以下方法被创建的

+ duplicate()
+ slice()
+ slice(int,int)
+ Unpooled.unmodifiableBuffer(...);
+ Order(ByteOrder)
+ readSlice(int)

每个这些方法都将返回一个新的 ByteBuf 实例，具有自己的两个索引和标记索引。其内部存储和 JDK 的 ByteBuffer 一样也是共享的。如果修改了它的内容，也同时修改了其对应的源实例。

**ByteBuf 复制** *如果需要一个现有的缓冲区的真实副本，使用 copy 或者 copy(int,int)方法。*

这不就是，一个是浅拷贝，一个深拷贝。

**对 ByteBuf 进行切片**

```java
Charset utf8 =  StandardCharsets.UTF_8;
ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!",utf8);
ByteBuf sliced = buf.slice(0,15);
System.out.println(sliced.toString(utf8));
buf.setByte(0,(byte)'J');
assert buf.getByte(0) == sliced.getByte(0);
System.out.println(buf.toString(utf8));
// 下面是输出的内容，因为是共享的，所以改了 buf，sliced 也会受到影响
// Netty in Action
// Jetty in Action rocks!
```

**复制一个 ByteBuf**

```java
Charset utf8 =  StandardCharsets.UTF_8;
ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!",utf8);
ByteBuf copy = buf.copy();
System.out.println(copy.toString(utf8));
buf.setByte(0,(byte)'J');
System.out.println(buf.toString(utf8));
assert buf.getByte(0) != copy.getByte(0);
System.out.println(copy.toString(utf8));
// 下面是输出内容
// Netty in Action rocks!
// Jetty in Action rocks!
// Netty in Action rocks!
```

只要有可能，尽可能使用 slice 来避免复制内存的开销。当然，如果要修改的，且要相互独立的部分，就不要用 slice。

### 读写操作

有两种类别的读/写操作：

+ get() 和 set() 操作，从给定的索引开始，并且保持索引不变
+ read() 和 write() 操作，从给定的索引开始，并且会根据已经访问过的字节数对索引进行调整。

<span style="margin-left:45%">**get() 操作**</span>


|  名称   | 描述  |
|  ----  | ----  |
| getBoolean(int) | 给定索引值处的 Boolean 值。下面类似 |
| getByte(int) | |
| getUnsignedByte(int) | 将给定索引处的无符号字节值作为 short 返回 |
| getMedium(int) | 24 位的中等 int 值 |
| getUnsignedMedium(int) | |
| getInt(int) | |
| getUnsignedInt(int) | 将给定索引处的无符号 int 值作为 long 返回 |
| getLong(int) | |
| getShort(int) |  |
| getUnsignedShort(int) | int getUnsignedShort(int var1) |
| getBytes(int,....) | ByteBuf getBytes(int var1, ByteBuf var2); 等等。将该缓冲区<br/>中从给定索引开始的数据传送到指定的目的地 |

<span style="margin-left:45%">**set() 操作**</span>

```java
public abstract ByteBuf setBoolean(int var1, boolean var2);

public abstract ByteBuf setByte(int var1, int var2);

public abstract ByteBuf setShort(int var1, int var2);

public abstract ByteBuf setShortLE(int var1, int var2);

public abstract ByteBuf setMedium(int var1, int var2);

public abstract ByteBuf setMediumLE(int var1, int var2);

public abstract ByteBuf setInt(int var1, int var2);

public abstract ByteBuf setIntLE(int var1, int var2);

public abstract ByteBuf setLong(int var1, long var2);

public abstract ByteBuf setLongLE(int var1, long var2);

public abstract ByteBuf setChar(int var1, int var2);

public abstract ByteBuf setFloat(int var1, float var2);
```

都能看懂吧。

**get() 和 set() 的用法**

```java
Charset utf8 = StandardCharsets.UTF_8;
ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
System.out.println((char)buf.getByte(0));
int readerIndex = buf.readerIndex();
int writerIndex = buf.writerIndex();
buf.setByte(0,(byte)'B');
System.out.println((char)buf.getByte(0));
assert readerIndex == buf.readerIndex();
assert writerIndex == buf.writerIndex();
// 输出
// N
// B
```

说白了，就是获取和替换，不改变已读的特质，类似 SQL 中的 update 操作。

**read() 操作**

和上面的 set get 操作一样，有多个重载方法。

下面是我看着比较特殊的方法，正常返回值。无符号数和正常的是类似的。

|  名称   | 描述  |
|  ----  | ----  |
| readMedium() | 返回当前 readerIndex 处的 24 位的中等 int 值，并将 readerIndex +3 |
| readUnsignedMedium() | 返回当前 readerIndex 处的 24 位无符号的中等 int 值，并将 readerIndex +3 |
| readInt() | 返回当前 readerIndex 的 Int 值，并将 readerIndex +4 |
| readLong() | +8 |
| readShort() | +2 |
| readBytes(ByteBuf \| byte[] <br/>destination,int dstIndex[,int length]) | 这是个重载的方法，意思是。将当前 ByteBuf 中从当前 readerIndex 处开始（如果<br/>设置了，length 长度的字节）数据传送到一个目标 ByteBuf 或者 byte[]，从目标的<br/>dstIndex 开始位置。本地的 readerIndex 将被增加已经传输的字节数。 |

**write 和 read 操作相反**

因为 Long 类型 8 byte = 64 bit，short 类型 2 byte =16 bit，int 类型 4 byte = 32 bit。这时候学校背的什么类型多少 bit 这个就起作用了。

| 数据类型 | 名称         | 长度       | 备注                                    |
| -------- | ------------ | ---------- | --------------------------------------- |
| byte     | 字节型       | 8bit       | 表示数据范围：-128~127                  |
| short    | 短整型       | 16bit      |                                         |
| char     | 字符型       | 16bit      |                                         |
| int      | 整型         | 32bit      |                                         |
| long     | 长整型       | 8 byte     |                                         |
| float    | 单精度浮点型 | 4 byte     | 精度：7-8位                             |
| double   | 双精度浮点型 | 8 byte     |                                         |
| boolean  | 布尔型       | true/false | 实际用 byte 存储，0 为 false，1 为 true |

**ByteBuf read() 和  write() 操作**

```java
Charset utf8 = StandardCharsets.UTF_8;
ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
System.out.println((char)buf.readByte());
int readerIndex = buf.readerIndex();
int writerIndex = buf.writerIndex();
buf.writeByte((byte)'?');
assert readerIndex == buf.readerIndex();
assert writerIndex == buf.writerIndex();
// 程序正常运行，输出为 N
// N
```

还有 isReadable() 之类的操作。

## ByteBufHolder 接口

为 Netty 的高级特性提供了支持，如缓冲区池化，其中可以从池中借用 ByteBuf，并且在需要时自动释放。

```java
/**
 * Return the data which is held by this {@link ByteBufHolder}.
 * 返回这个 ByteBufHoler 持有的 ByteBuf
 */
ByteBuf content();

/**
 * Creates a deep copy of this {@link ByteBufHolder}.
 * 创建一个深拷贝的 ByteBufHolder，包括一个其所包含的 ByteBuf 的非共享副本
 */
ByteBufHolder copy();

/**
 * Duplicates this {@link ByteBufHolder}. Be aware that this will not automatically call {@link #retain()}.
 * 创建一个浅拷贝的 ByteBufHolder，包括一个其所包含的 ByteBuf 的非共享副本
 */
ByteBufHolder duplicate();
```

## ByteBuf 分配

### 按需分配：ByteBufAllocator 接口

为了降低分配和释放内存的开销，Netty 通过 ByteBufAllocator 实现了（ByteBuf 的）池化。它可以用来分配我们所描述过的人一类型的 ByteBuf 实例。

```java
/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.buffer;

/**
 * Implementations are responsible to allocate buffers. Implementations of this interface are expected to be
 * thread-safe.
 */
public interface ByteBufAllocator {

    ByteBufAllocator DEFAULT = ByteBufUtil.DEFAULT_ALLOCATOR;

    /**
     * Allocate a {@link ByteBuf}. If it is a direct or heap buffer
     * depends on the actual implementation.
     * 返回一个基于堆或者直接内存存储的 ByteBuf
     */
    ByteBuf buffer();

    /**
     * Allocate a {@link ByteBuf} with the given initial capacity.
     * If it is a direct or heap buffer depends on the actual implementation.
     * 返回一个基于堆或者直接内存存储的 ByteBuf
     */
    ByteBuf buffer(int initialCapacity);

    /**
     * Allocate a {@link ByteBuf} with the given initial capacity and the given
     * maximal capacity. If it is a direct or heap buffer depends on the actual
     * implementation.
     * 返回一个基于堆或者直接内存存储的 ByteBuf
     */
    ByteBuf buffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a {@link ByteBuf}, preferably a direct buffer which is suitable for I/O.
     * 返回一个用于套接字的 I/O 操作的 ByteBuf
     */
    ByteBuf ioBuffer();

    /**
     * Allocate a {@link ByteBuf}, preferably a direct buffer which is suitable for I/O.
     */
    ByteBuf ioBuffer(int initialCapacity);

    /**
     * Allocate a {@link ByteBuf}, preferably a direct buffer which is suitable for I/O.
     */
    ByteBuf ioBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a heap {@link ByteBuf}.
     * 返回一个基于堆内存存储的 ByteBuf
     */
    ByteBuf heapBuffer();

    /**
     * Allocate a heap {@link ByteBuf} with the given initial capacity.
     */
    ByteBuf heapBuffer(int initialCapacity);

    /**
     * Allocate a heap {@link ByteBuf} with the given initial capacity and the given
     * maximal capacity.
     */
    ByteBuf heapBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a direct {@link ByteBuf}.
     */
    ByteBuf directBuffer();

    /**
     * Allocate a direct {@link ByteBuf} with the given initial capacity.
     */
    ByteBuf directBuffer(int initialCapacity);

    /**
     * Allocate a direct {@link ByteBuf} with the given initial capacity and the given
     * maximal capacity.
     */
    ByteBuf directBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a {@link CompositeByteBuf}.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    CompositeByteBuf compositeBuffer();

    /**
     * Allocate a {@link CompositeByteBuf} with the given maximum number of components that can be stored in it.
     * If it is a direct or heap buffer depends on the actual implementation.
     * 返回一个可以通过添加最大到指定数目的基于堆的活着直接内存存储的缓冲区来扩展的 CompositeByteBuf
     */
    CompositeByteBuf compositeBuffer(int maxNumComponents);

    /**
     * Allocate a heap {@link CompositeByteBuf}.
     */
    CompositeByteBuf compositeHeapBuffer();

    /**
     * Allocate a heap {@link CompositeByteBuf} with the given maximum number of components that can be stored in it.
     */
    CompositeByteBuf compositeHeapBuffer(int maxNumComponents);

    /**
     * Allocate a direct {@link CompositeByteBuf}.
     */
    CompositeByteBuf compositeDirectBuffer();

    /**
     * Allocate a direct {@link CompositeByteBuf} with the given maximum number of components that can be stored in it.
     */
    CompositeByteBuf compositeDirectBuffer(int maxNumComponents);

    /**
     * Returns {@code true} if direct {@link ByteBuf}'s are pooled
     */
    boolean isDirectBufferPooled();

    /**
     * Calculate the new capacity of a {@link ByteBuf} that is used when a {@link ByteBuf} needs to expand by the
     * {@code minNewCapacity} with {@code maxCapacity} as upper-bound.
     */
    int calculateNewCapacity(int minNewCapacity, int maxCapacity);
 }
```

可以通过 Channel （每个都可以有一个不同的 ByteBufAllocator 实例）或者绑定到 ChannelHandler 的 ChannelHandlerContext 获取 ByteBufAllocator 的引用

```java
Channel channel = ....;
ByteBufAllocator allocator = channel.alloc();
// ....
ChannelHandlerContext ctx = ...;
ByteBufAllocator allocator2 = ctx.alloc();
```

Netty 提供了两种 ByteBufAllocator 的实现。

1. PooledByteBufAllocator  池化 ByteBuf 以提高性能并最大限度地减少内存碎片。使用 **jemalloc** 来分配内存。
2. UnpooledByteBufAllocator  **不池化**ByteBuf 实例，并且每次它被调用时都会返回一个新的实例。

Netty 默认使用前者。

### Unpooled 缓冲区

提供静态工厂方法来创建未池化的 ByteBuf 实例。

+ buffer()	返回一个未池化的基于堆内存存储的 ByteBuf
+ directBuffer()  返回一个未池化的基于直接内存存储的 ByteBuf
+ wrappedBuffer()   返回一个包装了给定数据的 ByteBuf
+ copiedBuffer()  返回一个复制了给定数据的 ByteBuf

### ByteBufUtil

hexdump() 以十六进制的表示形式打印 ByteBuf 内容。

equals(ByteBuf,ByteBuf) 方法。

## 引用计数

**引用计数时一种通过在某个对象所持有的资源不再被其他对象引用时释放该对象所持有的资源来优化内存使用和性能的技术。**《深入理解 Java 虚拟机》中也有介绍，不过 HotSpot 不用引用计数，用的是对象可达性。对象不可达即需要被回收，什么时候对象不可达？

*通过一系列的名为GC Roots （GC 根节点）的对象作为起始点，从这些节点开始向下搜索，搜索所走过的路径，当一个对象到GC Roots没有任何引用链相连*

有资格做 **GC Roots** 对象有

   *虚拟机栈（栈帧中的本地变量表）中引用的对象。*

   *方法区中的类静态属性引用的对象*

   *方法区中的常量引用的对象*

   *本地方法栈JNI中的引用的对象。*

在 Netty 第 4 版中为 ByteBuf 和 ByteBufHoler 引入了引用计数，都实现了 RefrenceCounted 接口。

```java
Channel channel = ....;
ByteBufAllocator allocator = channel.alloc();
// ...
// 从 ByteBufHoler 分配一个 ByteBuf
ByteBuf buffer = allocator.directBuffer();
assert buffer.refCnt() == 1;

// 减少到改对象的活动引用，当减少到 0 时，该对象被释放，并返回 true
boolean released = buffer.release();
```

如果访问一个已经被释放的引用计数对象，会抛出 IlleagalReferenceCountException

# ChannelHandler 和 ChannelPipeline

## ChannelHandler

### Channel 的生命周期

Channel 定义了一组和 ChannelInboundHandler API 密切相关的简单但功能强大的状态模型。

| 状态 | 描述 |
|  ----  | ----  |
| ChannelUnregistered | Channel 已经被创建，但还未注册到 EventLoop |
| ChannelRegistered | Channel 已经被注册到了 EventLoop |
| ChannelActive | Cahnnel 处于活动状态（已经连接到它的远程节点），它现在可以接收和发送数据了 |
| ChannelInactive | Channel 没有连接到远程节点 |

Cahnnel 的正常声明周期如下所示。当这些状态发生改变时，将会生成对应的事件。这些事件会被转发给 ChannelPipeline 中的 ChannelHandler，其可以随后对它们做出响应。


![Netty实战——Channel 状态模型.png](https://i.loli.net/2020/10/22/YeAptQkCyKX5fG9.png)

### ChannelHandler 的声明周期

| 类型            | 描述                                                  |
| --------------- | ----------------------------------------------------- |
| handlerAdded    | 当把 ChannelHandler 添加到 ChannelPipeline 中时被调用 |
| handlerRemoved  | 当从 ChannelPipeline 移除 ChannelHandler 时被调用     |
| exceptionCaught | 当处理过程中在 ChannelPipeline 中有错误产生时被调用   |

Netty 定义了两个重要的 ChannelHandler 子接口

+ ChannelInboundHandler——处理入站数据以及各种状态变化
+ ChannelOutboundHandler——处理出站数据并且允许拦截所有的操作

### ChannelInbountHandler

| 类型                      | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| channelRegistered         | 当 Channel 已经注册到它的 EventLoop 并且能够处理 I/O 时被调用 |
| channelUnregistered       | 当 Channel 从它的 EventLoop 注销并且无法处理任何 I/O 时被调用 |
| channelActive             | 当 Channel 处于活动状态时被调用，Channel 已经连接/绑定并且已经就绪 |
| channelInactive           | 当 Channel 离开活动状态并且不再连接它的远程节点时被调用      |
| channelReadComplete       | 当 Channel 上的一个读操作完成时被调用                        |
| channelRead               | 当从 Channel 读取数据时被调用                                |
| ChannelWritabilityChanged | **当 Channel 的可写状态发生改变时被调用。用户可以确保写操作不会完成得太快（以避免发生<br/>OOM）或者可以在 Channel 变为再次可写时恢复写入。可以通过调用 Channel 的 isWritable() 方法<br/>来检测 Channel 的可写性。与可写性相关的阈值可以通过 <br/>Channel.config().setWriteHagnWaterMark() 和<br/>Channel.config().setWriteLowWaterMark()方法来设置** // TODO 这里暂时还不懂 |
| userEventTriggered        | 当 ChannelInboundHandler#fireUserEventTriggered 方法被调用时被调用，因为一个 POJO 被传进了 ChannelPipeline |

Netty 提供了一个实用方法 RefrenceCountUtil.release() 如下面所示

**释放消息资源**

```java
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
```

**还可以用更简单的方式**

```java
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
```

下面是 SimpleChannelInboundHandler#channelRead 实现了 ChannelInboundHandlerAdapter 的方法而已。

```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    boolean release = true;
    try {
        if (acceptInboundMessage(msg)) {
            @SuppressWarnings("unchecked")
            I imsg = (I) msg;
            channelRead0(ctx, imsg);
        } else {
            release = false;
            ctx.fireChannelRead(msg);
        }
    } finally {
        if (autoRelease && release) {
            ReferenceCountUtil.release(msg);
        }
    }
}
```

### ChannelOutboundHandler

出站操作和数据将由 ChannelOutboundHandler 处理。它的方法将被 Channel、ChannelPipeline 以及 ChannelHandlerContext 调用。

ChannleOutboundHandler 的一个强大的功能是可以按需推迟操作或者事件，这使得可以通过一些复杂的方法来处理请求。例如，如果写到远程节点的写入被暂停了，可以推迟 flush 并在稍后继续。

| 类型                                                        | 描述                                                  |
| ----------------------------------------------------------- | ----------------------------------------------------- |
| bind(ChannelHandlerContext,SocketAddress,ChannelPromise)    | 当请求将 Channel 绑定到本地地址时被调用               |
| connect(ChannelHandlerContext,SocketAddress,ChannelPromise) | 当请求将 Channel 连接到远程节点时被调用               |
| disconnect(ChannelHandlerContext,ChannelPromise)            | 当请求将 Channel 从远程节点断开时被调用               |
| close(ChannelHandlerContext,ChannelPromise)                 | 当请求将 Channel 从它的 EventLoop 注销时被调用        |
| read(ChannelHandlerContext)                                 | 当请求从 Channel 读取更多的数据时被调用               |
| flush(ChannelHandlerContext)                                | 当请求通过 Channel将入队数据 flush 到远程节点时被调用 |
| write(ChannelHandlerContext,Object,ChannelPromise)          | 当请求通过 Channel 将数据写到远程节点时被调用         |

**ChannelPromis 与 ChannelFuture** ChannelOutboundHandler 中的大部分方法都需要一个 ChannelPromise 参数，以便在操作完成时得到通知。ChannelPromise 时 ChannelFuture 的一个子类，其定义了一些可写的方法，如 setSuccess() 和 setFailure()，从而使 ChannelFuture 不可变。

### ChannelHandler 适配器


![Netty实战-ChannelInboundHandlerAdapter.png](https://i.loli.net/2020/10/23/4LoYulEAnhstXqf.png)

ChannelHandlerAdapter 提供了实用方法 siSharable()。如果其对应的实现被标注为 Sharable，那么这个方法将会返回 true，表示它可以被添加到多个 ChannelPipeline 中的下一个 ChannelHandler 中。

### 资源管理

每当通过调用 `ChannelInboundHandler#channelRead` 或者 `ChannelOutboundHandler#write` 方法来处理数据时，都需要确保没有任何的资源泄漏。Netty 使用引用计数来处理池化的 ByteBuf ，所以在完全使用完某个 ByteBuf 后，调整其引用计数是很重要的。

为了帮助诊断潜在的（资源泄漏）问题，Netty 提供了 class ResourceLeakDetector，它将对你的 app 的缓冲区做大约 1% 的采样来检测内存泄漏。

Netty 目前定义了 4 种泄漏级别。

| 级别     | 描述                                                         |
| -------- | ------------------------------------------------------------ |
| DISABLED | 禁用泄漏检测。在详尽的测试之后才应该设置成这个值             |
| SIMPLE   | 使用 1% 的默认采样率，报告所发现的任何的泄漏。默认级别，适合绝大部分的情况 |
| ADVANCED | 使用默认的采样率，报告所发现的任何的泄漏以及对应的消息被访问的位置 |
| PARANOID | 类似于 ADVANCED，但是每次对消息的访问都会进行采样。对性能影响极大，调试用 |

启动参数带 `java -Dio.netty.leakDetecionLevel=ADVANCED`

或者 `System.setProperty("io.netty.leakDetectionLevel","ADVACED")` 

```java
package me.young1lin.netty.demo.channel.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/23 10:01 下午
 */
@ChannelHandler.Sharable
public class DiscardOutBoundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ReferenceCountUtil.release(msg);
        promise.setSuccess();
    }
}
```

不仅要释放资源，还要通知 ChannelPromise 。否者可能会出现 ChannelFutureListener 收不到某个消息已经被处理了的通知情况。

如果一个消息被消费或者被丢弃了，并且没有传递给 ChannelPipeline 中的下一个 ChannelOutboundHandler，那么用户就有责任调用 ReferenceCountUtil#release 。如果消息到达了实际的传输层，那么当他它被写入时或者 Channel 关闭时，都将被自动释放。

## ChannelPipeline

每一个新创建的 Channel 都会被分配一个新的 ChannelPipeline。这项关联是永久的，不可替换或者增加。

根据事件的起源，事件将会被 ChannelInboundHandler 和 ChannelOutboundHandler 处理。随后，通过调用 ChannelHandlerContext 实现。它将被转发给同一超类型的下一个 ChannelHandler。

**ChannelHandlerContext**

<div style="background-color:grey">
ChannelHandlerContext 使得 ChannelHandler 能够和它的 ChannelPipeline 以及其他的 ChannelHandler 交互。ChannelHandler 可以通知其所属的 ChannelPipeline 中的下一个 ChannelHandler，甚至可以动态修改它所属的 ChannelPipeline（这里指的是其中的 ChannelHandler 的编排）。<br/>
ChannelHandlerContext 具有丰富的用于处理事件和执行 I/O 操作的 API
</div>





又是这个图，这里灰色的部分，就是 ChannelPipeline。

![Netty实战-流经 ChannelHandler 链的入站事件和出站事件 (1).png](https://i.loli.net/2020/10/14/fUtNMYO6P21FRiW.png)

**ChannelPipeline 相对论**

<div style="background-color:grey">
	Netty 总是将 ChannelPipeline 的入站口作为头部，出站口作为尾端。<br/>
    当你完成了通过调用 Channelpipeline#add* （这里的 * 指所有的 add 开头的方法）方法将入站处理器（ChannelInboundHandler）<br/>
    出站处理器（ChannelOutBoundHandler）混合添加到 ChannelPipeline 之后，每一个 ChannelHandler 从头头部到尾端的顺序<br/>位	 置正如定义他们的一样。
</div>

在 ChannelPipeline 传播事件时，它会测试ChannelPipeline 中的下一个 ChannelHandler 的类型是否和事件的运动方向相匹配。如果不匹配，ChannelPipeline 将跳过该 ChannelHandler 并前进到下一个，直到它找到和该事件所期望的方向相匹配的为止。

### 修改 ChannelPipline

