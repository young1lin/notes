# NIO 简单介绍

NIO 最开始是新的输入/输出（New Input/Output）的英文缩写，但是，该 Java API 已经出现足够长的时间了，不再是“新的”了，因此，如今大多数的用户认为 NIO 代表非阻塞 I/O（Non-blocking I/O），而阻塞的 I/O（blocking I/O）是旧的输入/输出（old input/output，OIO）。你也可能遇到它被称为普通 I/O（plain I/O）的时候。

# Netty 特性


| 分类     | Netty的特性                                                  |
| :------- | :----------------------------------------------------------- |
| 设计     | 统一的 API，支持多种传输类型，阻塞的和非阻塞的<br/>简单而强大的线程模型<br/>真正的无连接数据报套接字支持<br/>链接逻辑组件以支持复用 |
| 易于使用 | 翔实的 Javadoc 和大量的示例集<br/>不需要超过 JDK 1.6+ 的依赖。（一些可选的特性可能需要 Java 1.7+ 和/或额外的依赖） |
| 性能     | 拥有比 Java 的核心的 API 更高的吞吐量以及更低的延迟<br/>得益于池化和服用，拥有更低的资源消耗<br/>最少的内存复制 |
| 健壮性   | 不会因为慢速、快速或者超载的连接而导致OOM<br/>消除在高速网络中 NIO 应用程序常见的不公平读/写比率 |
| 安全性   | 完整的 SSL/TLS 以及 SmartTLS 支持<br/>可用于受限环境下，如 Applet 和 OSGI |
| 社区驱动 | 发布快速而且频繁                                             |

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
