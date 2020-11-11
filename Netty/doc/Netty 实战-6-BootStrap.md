# BootStrap （这是个类）

**引导类的层次结构**

![引导类的层次结构final.png](https://i.loli.net/2020/10/26/9fwLeMHB17nGV4X.png)



服务器致力于使用一个父 Channel 来接受来自客户端的连接，并创建子 Channel 以用于它们之间的通信；而客户端将最可能只需要一个单独的、没有父 Channel 的 Channel 类来用于所有的网络交互。（适用于无连接的传输协议，例如 UDP，它们并不是每个连接都需要一个单独的 Channel）。

两种应用程序类型之间**通用的引导步骤**由 AbtractBootstrap 来处理，特定的给子类来做。

**为什么引导类是 Cloneable 的**

*可能会需要创建多个具有类似配置或者完全相同配置的 Channel。为了支持这种模式而又不需要为每个 Channel 都创建并配置一个新的引导类实例，AbstractBootstrap 被标记为了 Cloneable。这种方式创建引导类实例的 EventLoopGroup 的一个浅拷贝，所以被浅拷贝的 EventLoopGroup 将在所有克隆的 Channel 实例之间共享。这是可以接受的，因为通常这些克隆的 Channel 的生命周期都很短暂，一个典型的场景——创建一个 Channel 以进行一次 HTTP 请求。*

**AbstractBootstrap 的声明**

```java
public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel> implements Cloneable {
    
}
```

根据范型，支持链式调用（流式语法）。

# 引导客户端和无连接协议

BootStrap 类被用于客户端活着使用了无连接协议的应用程序中。

**BootStrap 类的 API**

| Method Name                                                  | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| group(EventLoopGroup)                                        | 用于处理 Channel 所有事件的 EventLoopGroup                   |
| channel(Class<? extends C>)<br/>channelFactory(ChannelFactory<? extends C>) | Channel 方法指定了 Channel 的实现类，如果该实现类没提供默认的构造函数，可以通过 ChannelFactory 方法来制定一个工厂类，它将会被 bind() 方法调用。 |
| localAddress(SocketAddress)                                  | 指定 Channel 应该绑定到本地的地址。如果没有指定，将由操作系统创建一个随机的地址。<br/>活着，也可以通过 bind() 或者 connect 方法指定 localAddress |
| option(ChannelOption option,T value)                         | 设置 ChannelOption，其将被应用到每个新创建的 Channel 的 ChannelConfig。这些选项将会通过 bind 或者 connect 方法设置到 Channel，不管哪个先被调用。这个方法在 Channel 已经被创建后在调用将不会有任何的效果。支持的 ChannelOption 取决于 Channel 的类型 |
| handler(ChannelHandler)                                      | 设置将被添加到 ChannelPipeline 以接收事件通知的 ChannelHandler |
| clone()                                                      | 浅拷贝                                                       |
| remoteAddress(SocketAddress)                                 | 设置远程地址，或者 connect 也可以指定                        |
| connet                                                       | 连接到远程节点并返回一个 ChannelFuture，其将会在连接操作完成后接收通知 |
| bind                                                         | 绑定 Channel 并返回一个 ChannelFuture，其将会在绑定操作完成后收到通知，在那之后必须调用 Channel#connect 方法来建立连接。 |

## 引导客户端

![引导过程.png](https://i.loli.net/2020/10/27/UnIqVd1GDg4poAE.png)

**引导一个客户端**

```java
package me.young1lin.netty.demo.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

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
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost",8080));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("Connection attempt established");
                }else {
                    System.err.println("Connection attempt failed");
                    future.cause().printStackTrace();
                }
            }
        });
    }
}
```

展示了流式语法。

## Channel 和 EventLoopGroup 兼容性

oio 开头的类不能和 nio 开头的类混用。

**演示不兼容的 Channel 和 EventLoopGroup**

```java
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
ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost",8080));
future.syncUninterruptibly();
```

上面代码会报错，IllegalStateException。

**关于 IllegalStateException**

*在引导的过程中，在调用 bind 或者 connect 方法之前，必须调用以下方法来设置所需要的组件*

+ group
+ channel 或者 channelFactory
+ handler

如果不这么做，就会抛出 IllegalStateException。对 handler 的方法调用尤其重要，因为它需要配置好 ChannelPipeline。

# 引导服务器

## ServerBootstrap 类

| Method Name    | Description                                                  |
| -------------- | ------------------------------------------------------------ |
| group          | 这个 EventLoopGroup 将用于 ServerChannel 和被接受的子 Channel 的 I/O 处理 |
| channel        | 设置将要被实例化的 ServerChannel 类                          |
| channelFactory | 如果不能通过默认的构造函数创建 Channel，那么可以提供一个 ChannelFactory |
| localAddress   | 指定 ServerChannel 应该绑定到本地地址。如果没有指定，则将由操作系统使用一个随机地址，或者用 bind |
| option         | 指定要应用到新创建的 ServerChannel 的 ChannelConfig 的 ChannelOption。这些选项将会通过 bind 方法设置到 Channel。在 bind 方法被调用之后，设置或者改变 ChannelOption 都不会有任何的效果。所支持的 ChannelOption 取决于所使用的 Channel 类型。 |
| childOption    | 指定当子 Channel 被接受时，应用到自 Channel 的 ChannelConfig 的 ChannelOption。所支持的 ChannelOption 取决于所使用的 Channel 的类型。 |
| attr           | 指定 ServerChannel 上的属性，属性将会通过 bind 方法设置给 Channel。同样在 bind 方法调用之后，不会生效。 |
| handler        | 很多次了，不再赘述                                           |
| childHandler   | 设置将被添加到已被接受的子 Channel 的 ChannelPipeline 中的 ChannelHandler。handler 方法和 childHandler之间的区别是：前者所添加的 ChannelHandler 由接受子 Channel 的 ServerChannel 处理，而 childHandler 方法所添加的 ChannelHandler 将由已被接受的子 Channel 处理，其代表一个绑定到远程节点的套接字。 |
| clone          | 浅拷贝                                                       |
| bind           | 绑定 ServerChannel 并且返回一个 ChannelFuture，其将会在绑定操作完成后收到通知（带着成功或者失败的结果） |

## 引导服务器

**ServerBootstrap 和 ServerChannel**

![ServerBootstrap 和 ServerChannel.png](https://i.loli.net/2020/10/27/JVRyreX2aDm8kPq.png)

**引导服务器代码**

```java
public void serverTest(){
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
            if(future.isSuccess()){
                System.out.println("Server bound");
            }else {
                System.err.println("Bound attempt failed");
                future.cause().printStackTrace();
            }
        }
    });
}
```

# 从 Channel 引导客户端

**在两个 Channel 之间共享 EventLoop**

![在两个 Channel 之间共享 EventLoop.png](https://i.loli.net/2020/10/27/oHAKf3a258dMGBD.png)

**引导服务器**

```java
public void serverSharedChannelTest(){
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(new NioEventLoopGroup(),new NioEventLoopGroup())
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
                    channelFuture = bootstrap.connect(new InetSocketAddress("localhost",8080));
                }

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    if(channelFuture.isDone()){
                        // 当连接完成时，执行一些数据操作（如代理）
                        // do something with the data
                    }
                }
            });
    ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
    future.addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if(channelFuture.isSuccess()){
                System.out.println("Server bound");
            }else {
                System.err.println("Bind attempt failed");
                channelFuture.cause().printStackTrace();
            }
        }
    });
}
```

# 在引导过程中添加多个 ChannelHandler

ChannelInboundHandlerAdapter 类来将添加多个 ChannelHandler 添加到 ChannelPipeline。只需要简单地向 Bootstrap 或者 ServerBootstrap 的实例提供 ChannelInitializer 实现即可。在该方法返回之后，ChannelInitializer 的实例将会从 ChannelPipeline 中移除它自己。

**引导和使用 ChannelInitializer** 

```java
public void channelInitializerTest() throws InterruptedException {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(new NioEventLoopGroup(),new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializerImpl());
    ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
    future.sync();

}
final class ChannelInitializerImpl extends ChannelInitializer<Channel>{

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
    }
}
```

# 使用 Netty 的 ChannelOption 和属性

ChannelOption make channel created easier。

Netty 应用程序通常于组织的专有软件集成在一起，而像 Channel 这样的组件可能设置会在正常的 Netty 生命周期之外被使用。在某些常用的属性和数据不可用时，Netty 提供了 AttributeMap 抽象（一个由 Channel 和引导类提供的集合）以及 AttributeKey<T>(一个用于插入和获取属性的范型类)。使用这些工具，便可以安全地将任何类型的数据项于客户端和服务器 Channel（包含 ServerChannel 的子 Channel）相关联。

**使用属性值**

```java
AttributeKey<Integer> id = AttributeKey.newInstance("ID");
Bootstrap bootstrap = new Bootstrap();
bootstrap.group(new NioEventLoopGroup())
        .channel(NioSocketChannel.class)
        .handler(new SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                Integer idValue = ctx.channel().attr(id).get();
                // do something with the idValue
                System.out.printf("idValue is : %s",idValue);
            }

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                System.out.println("Received data");
            }
        });
bootstrap.option(ChannelOption.SO_KEEPALIVE,true)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000);
bootstrap.attr(id,123456);
ChannelFuture future = bootstrap.connect(new InetSocketAddress(8080));
future.syncUninterruptibly();
```

# 引导 DatagramChannel

前面的引导代码示例都是基于 TCP 协议的 SocketChannel，但是 Bootstrap 类也可以被用于无连接的协议。为此，Netty 提供了各种 DatagramChannel 的实现。唯一区别就是。不再调用 connect 方法。

**使用 Bootstrap 和 DatagramChannel**

```java
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
        if(future.isSuccess()){
            System.out.println("Channel bound");
        }else {
            System.err.println("Bind attempt failed");
            future.cause().printStackTrace();
        }
    }
});
```

# 关闭

引导使你的应用程序启动并且运行期俩，但迟早都需要优雅将它关闭，优雅是指干净地释放所有活动的线程。这就是 EventLoop#shutdownGracefully 方法的作用。这个调用将会返回一个 Future，这个 Future 将在关闭完成时收到通知。shutdownGracefully 也是一个异步的操作。

**优雅关闭**

```java
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
```