# ChannelHandler 和 ChannelPipeline

## ChannelHandler

### Channel 的生命周期

Channel 定义了一组和 ChannelInboundHandler API 密切相关的简单但功能强大的状态模型。

| 状态                | 描述                                                         |
| ------------------- | ------------------------------------------------------------ |
| ChannelUnregistered | Channel 已经被创建，但还未注册到 EventLoop                   |
| ChannelRegistered   | Channel 已经被注册到了 EventLoop                             |
| ChannelActive       | Cahnnel 处于活动状态（已经连接到它的远程节点），它现在可以接收和发送数据了 |
| ChannelInactive     | Channel 没有连接到远程节点                                   |

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

通过调用 CHannelPipeline 上的相关方法，ChannelHandler 可以添加、删除或者替换其他的 ChannelHandler，从而实时地修改 ChannelPipeline 的布局。（它也可以将它自己从 ChannelPipeline 中移除）。这是 ChannelHandler 最重要的能力之一。

**ChannelPipeline 上的相关方法，由 ChannelHandler 用来修改 ChannelPipeline 的布局**

| 名称                                            | 描述                                                    |
| ----------------------------------------------- | ------------------------------------------------------- |
| addFirst<br/>addBefore<br/>addAfter<br/>addLast | 将一个 ChannelHandler 添加到 ChannelPipeline 中         |
| remove                                          | 将一个 ChannelHandler 从 ChannelPipeline 中移除         |
| replace                                         | 将 ChannelPipeline 中的一个 ChannelHandler 替换成另一个 |

**修改一个 ChannelPipeline**

```java
ChannelPipeline pipeline = .....;
FirstHandler firstHandler = new FirstHandler(); 
pipeline.addFirst("first",firstHandler);
pipeline.addFirst("second",new SecondHandler());
pipeline.addFirst("third",new ThirdHandler());
// ....
pipeline.remove("third");
pipeline.remove(firstHandler);
pipeline.replace("second","forth",new ForthHandler());
```

**ChannelHandler 的执行和阻塞**

*通常 ChannelPipeline 中的每一个 ChannelHandler 都是通过它的 EventLoop （I/O 线程）来处理传递给它的事件的。所以至关重要的是不要阻塞这个线程，因为这会对整体的 I/O 处理产生负面的影响。但有时可能需要与哪些使用阻塞 API 的遗留代码进行交互。对与这种情况，ChannelPipeline 有一些接受一个 EventExecutorGroup 的 add() 方法。如果一个事件被传递一个自定义的 EventExecutorGroup，它将被包含在这个 EventExecutorGroup 中的某个 EventExecutor。所处理从而被从该 Channel 本身的 EventLoop 中移除。对于这种用例，Netty 提供了一个叫 DefaultEventExecutorGroup 的默认实现。*

ChannelPipeline 的用于访问 ChannelHandler 的操作

| 名称    | 描述                                                |
| ------- | --------------------------------------------------- |
| get     | 根据名称返回 CahnnelHandler                         |
| context |                                                     |
| names   | 返回 ChannelPipeline 中所有的 CHannelHandler 的名称 |

### 触发事件

ChannelPipeline 的 API 公开了用于调用入站和出站操作的附加方法。

**ChannelPipeline 的入站操作**

| Method Name                   | Description                                                  |
| ----------------------------- | ------------------------------------------------------------ |
| fireChanneRegistered          | 调用 ChannelPipeline 中下一个 ChannelInboundHandler 的<br/> channelRegistered(ChannelHandlerContext)方法 |
| fireChannelUnregistered       | 下面都是                                                     |
| fireChannelActive             |                                                              |
| fireChannelInactive           |                                                              |
| fireExceptionCaught           |                                                              |
| fireUserEventTriggered        |                                                              |
| fireChannelRead               |                                                              |
| fireChannelReadCompete        |                                                              |
| fireChannelWritabilityChanged |                                                              |

ChannelPipeline 的出站操作

| Method Name   | Description                                                  |
| ------------- | ------------------------------------------------------------ |
| bind          | 将 Channel 绑定到一个本地地址，这将调用 ChannnelPipeline 中的下一个 ChannelOutboundHandler 的 bind(ChannelHandlerContext,SocketADdress,ChannelPromis) 方法 |
| connect       | 将 Channel 连接到一个远程地址，这将调用 ChannelPipeline 中的 下一个 ChannelOutboundHandler 的 connect 方法 |
| disconnect    | 将 Channel 断开连接。同上                                    |
| close         | 将 Channel 关闭。同上                                        |
| deregister    | 将 Channel 从它先前分配的 EventExecutor（即 EventLoop）中注销。。同上 |
| flush         | 冲刷 Channel 所有挂起的写入。同上                            |
| write         | 将消息写入 Channel。同上。注意这并不会将消息写入底层的 Socket，而只会将它放入队列中，要将它写入 Socket，需要调用 flush 或者下面的方法。 |
| writeAndFlush | 之前写过这个方法的介绍，往上翻。                             |
| read          | 请求从 CHannel 中读取更多数据。同上                          |

总结：

+ ChannelPipeline 保存了与 Channel 相关联的 ChannelHandler
+ ChannelPipeline 可以根据需要，通过添加或者删除 ChannelHandler 来动态地修改
+ ChannelPipeline 有着丰富的 API 用以被调用，以响应入站和出站事件。

## ChannelHandlerContext

每当有 ChannelHandler 添加到 ChannelPipeline 中时，都会创建 ChannelHandlerContext。

ChannelHandlerContext 有很多的方法，其中一些方法也存在于 Channel 和 ChannelPipeline 本身上。但是有一点重要的不同。Channel 和 ChannelPipeline 本身的方法影响 All，ChannelHandlerContext 影响当前和下一个。

**我觉得应该是这样的** // TODO 把这部分看到后面时，补齐

```java
{
    ChannelPipeline cp = ...;
    Object msg = ...;
    ChannelHandlerContext ctx = new ChannelHandlerContext();
    channelHandler(ctx,cp,msg);
}
public void channelHandler(ChannelHandlerContex ctx,ChannelPipeline cp,Object msg){
    if(cp.next() == null){
        return;
    }
    cp.firexxxx(ctx,msg);
    channelHandler(ctx,cp,msg);
}

```

ChannelHandlerContext 的 API

| Method Name            | Description                                                  |
| ---------------------- | ------------------------------------------------------------ |
| alloc                  | 返回和这个实例相关联的 Channel 所配置的 ByteBufAllocator     |
| bind                   | 绑定到给定的 SocketAddress ，并返回 ChannleFuture            |
| channel                |                                                              |
| close                  | 关闭 Channel，并返回 ChannelFuture                           |
| connect                | 连接给定的 同行                                              |
| deregister             | 从之前分配的 EventExecutor 注销，并返回 ChannelFuture        |
| disconnect             | 从远程节点断开，并返回 ChannelFuture                         |
| executor               | 返回调度事件的 EventExecutor                                 |
| fireChannelActive      | 触发对下一个 ChannelInboundHandler 上的 channelActive 方法（已连接）的调用 |
| fireChannelInactive    |                                                              |
| fireChannelRead        |                                                              |
| handler                |                                                              |
| isRemoved              | 返回这个实例的唯一名称                                       |
| name                   | 返回这个实例所关联的 ChannelPipeline                         |
| pipeline               | 将数据从 Channel 读取到第一个入站缓冲区：如果读取成功则将出发一个 channelRead 事件，并（在最后一个消息读取完成后）通知 ChannelInboundCHandler 的 channelReadCompelete （ChannelHandlerContext）方法。 |
| read                   | 通过这个实例写入消息并经过 ChannelPipeline                   |
| write还有writeAndFlush |                                                              |

+ ChannelHandlerContext 和 ChannelHandler 之间的关联（绑定）是永远不会改变的，所以缓存对它的引用是安全的。
+ 更短的事件流

### 使用 ChannelHandlerContext

![Channel、ChannelPipeline、ChannelHandler 以及 Ctx 之间的关系.png](https://i.loli.net/2020/10/25/ljyxd3AvRfs4zuW.png)



为什么会想要从 ChannelPipeline 中的某个特定点开始传播事件呢？

+ 为了减少将事件传经它不感兴趣的 ChannelHandler 所带来的开销
+ 为了避免将事件传经那些可能会它感兴趣的 ChannelHandler

要想调用从某个特定的 ChannelHandler 开始的处理过程，必须获取到在 ChannelPipeline 该 ChannelHandler **之前的** ChannleHandler 所关联的 ChannelHandlerContext。这个 ChannelHandlerContext 将调用和它所关联的 ChannelHandler 之后的 ChannelHandler。

**通过 Channel 或者 CHannelPipeline 进行的事件传播。**

[![GlruK.png](https://b1.sbimg.org/file/chevereto-jia/2020/10/26/GlruK.png)](https://sbimg.cn/image/GlruK)

**通过 ChannelHandlerContext 触发的操作的事件流**，可以从特定的 ChannelHandlerContext 中插入事件。

![通过 ChannelHandlerContext 触发的操作的事件流.png](https://i.loli.net/2020/10/26/QLZeC2hHdO3SnY7.png)

### ChannelHandler 和 ChannelHandlerContext 的高级用法

可以通过 ChannelHandlerContext 上的 pipeline 方法来获得封闭的 ChannePipeline 的引用。这使得运行时得以操作 ChannelPipeline 的 ChannelHandler，我们可以利用这一点来实现一些复杂的设计。例如，通过将 ChannelHandler 添加到 ChannelPipeline 中来实现动态的协议切换。

另一种高级的用法是缓存到 ChannelHandlerContext 的引用以供稍后使用，这可能会发生在任何的 ChannelHandler 方法之外，甚至来自于不同的线程。

**缓存到 ChannelHandlerContext 的引用**

```java
public class WriteHandler extends ChannelHandlerAdapter{
    
    private ChannelHandlerContext ctx;
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        this.ctx = ctx;
    }
    public void send(String msg){
        ctx.writeAndFlush(msg);
    }
}
```

因为一个 ChannelHandler 可以从属于多个 ChannelPipeline，所以它也可以绑定到多个 ChannelHandlerContext 实例。对于这种用法（指在多个 ChannelPipeline 中共享同一个 ChannelHandler），对应的 ChannelHandler 必须使用 @Sharable 注解标注；否则试图将它添加到多个 ChannelPipeline 时将会触发异常。

ChannelHandler 不可拥有状态，即全局变量，应该是无状态的，这样做会有并发问题。



## 异常处理

### 处理入站异常

如果在处理入站时间的过程中有异常被抛出，那么它将从它在 ChannelInbountHandler 里被触发的那一点开始流经 ChannelPipeline。

重写 ChannelInbound#exceptionCaught(ChannelHandlerContext cox,Trowable cause);

异常将会继续按照入站方向流动，实现了前面所示逻辑的 ChannelInboundHandler 通常位于 ChannelPipeline 的最后。这确保了所有入站的异常总是会被处理。

如果你不实现任何处理入站异常的逻辑（或者没有消费该异常），那么 Netty 将会记录该异常没有被处理的事实（通过 Warning 级别的日志记录该异常到达了 ChannelPipeline 的尾端，但没有被处理，并尝试释放该异常）。

+ ChannelHandler#excetionCaught() 的默认实现是简单地将当前异常转发给 ChannelPipeline 中的下一个 ChannelInboundHandler
+ 如果异常达到了 ChannelPipeline 的尾端，它将会被记录为未被处理
+ 定义自定义的处理逻辑，需要重写 excetionCaught 方法。然后需要自己决定是否需要把该异常传播出去。

### 处理出站异常

用于处理出站操作中的正常完成以及异常的选项，都基于以下的通知机制。

+ 每个出站操作都将返回一个 ChannelFuture。注册到 ChannelFuture 的 ChannelFutureListener 将在操作完成时被通知该操作是已经成功了还是出错了
+ 几乎所有的 ChannelOutboundHandler 上的方法都会传入一个 ChannelPromise 的实例。作为 ChannelFuture 的子类，ChannelPromise 也可以被分配用于异步通知的监听器。但是，ChannelPromise 还具有提供立即通知的可写方法

ChannelPromise#setSuccess;

ChannelPromise#setFailure(Throwable cause)

添加 ChannelFutureListener 只需要调用 ChannelFuture 实例上的 addListener 方法，并且有两种不同的方式可以做到。**最常用的方式**是，调用出站操作（如 write()方法）所返回的 ChannelFuture 上的 addListener 方法。

**添加 ChannelFutureListener 到 ChannelFuture**

```java
ChannelFuture future = channel.write(someMessage);
future.addListener(new ChannelFutureListener(){
   @Override
    public void operationCompelete(ChannelFuture f){
        if(!f.isSuccess()){
         	f.cause().printStackTrace();
            f.channel.close();
        }
    }
});
```

第二种方式是将 ChannelFutureListener 添加到即将作为参数传递给 ChannelOutboundHandler 的方法的 ChannelPromise。

**添加 ChannelFutureListener 到 ChannelPromise**

```java
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
```

ChannelPromise 的可写方法

通过调用 ChannelPromise 上的 setSuccess 和 setFailure 方法，可以使一个操作的状态在 ChannelHandler 的方法返回给其调用者时便即可被感知到。

***选择第一种方式。对于细致的异常处理，在调用出站操作时添加 ChannelFutureListener 更合适。***

如果 ChannelOutboundHandler 本身抛出了异常，在这种情况下 Netty 本身会通知任何已经注册到对应 ChannelPromise 的监听器。