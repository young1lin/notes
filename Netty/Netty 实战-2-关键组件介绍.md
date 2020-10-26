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


| 方法名        | 描述                                                         |
| ------------- | ------------------------------------------------------------ |
| eventLoop     | 返回分配给 Channel 的 EventLoop                              |
| pipeline      | 返回分配给 Channel 的 ChannelPipeline                        |
| isActive      | 如果 Channel 是活动的，则返回 true。活动的意义可能依赖于底层的传输。例如一个Socket传输<br/>一旦连接到了远程节点便是活动的，而一个 Datagram 传输一旦被打开，便是活动的 |
| localAddress  | 返回本地 SocketAddress                                       |
| remoteAddress | 返回远程SocketAddress                                        |
| write         | 将数据写到远程节点。这个数据将被传递给 ChannelPipeline，并且排队直到它被冲刷 |
| flush         | 将之前已写的数据冲刷到底层传输，入一个 Socket                |
| writeAndFlush | 等同于调用 write+flush                                       |

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