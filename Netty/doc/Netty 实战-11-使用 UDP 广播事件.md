# UDP 基础知识

无连接协议即用户数据报协议（UDP），通常在性能至关重要并且能够容忍一定的数据包丢失的情况下。**用户数据报协议**（英语：**U**ser **D**atagram **P**rotocol，缩写：**UDP**；又称**用户数据包协议**）是一个简单的面向[数据报](https://zh.wikipedia.org/wiki/数据报)的[通信协议](https://zh.wikipedia.org/wiki/通信协议)，位于[OSI模型](https://zh.wikipedia.org/wiki/OSI模型)的[传输层](https://zh.wikipedia.org/wiki/传输层)。该协议由[David P. Reed](https://zh.wikipedia.org/w/index.php?title=David_P._Reed&action=edit&redlink=1)在1980年设计且在[RFC 768](https://tools.ietf.org/html/rfc768)中被规范。典型网络上的众多使用UDP协议的关键应用在一定程度上是相似的。

在[TCP/IP](https://zh.wikipedia.org/wiki/TCP/IP)模型中，UDP为[网络层](https://zh.wikipedia.org/wiki/网络层)以上和[应用层](https://zh.wikipedia.org/wiki/应用层)以下提供了一个简单的接口。UDP只提供[数据](https://zh.wikipedia.org/wiki/数据)的不可靠传递，它一旦把应用程序发给网络层的数据发送出去，就不保留数据备份（所以UDP有时候也被认为是不可靠的数据报协议）。UDP在IP数据报的头部仅仅加入了复用和数据校验字段。

UDP适用于不需要或在[程序](https://zh.wikipedia.org/wiki/计算机程序)中执行[错误检查和纠正](https://zh.wikipedia.org/wiki/错误检测与纠正)的[应用](https://zh.wikipedia.org/wiki/应用程序)，它避免了[协议栈](https://zh.wikipedia.org/wiki/协议栈)中此类处理的[开销](https://zh.wikipedia.org/w/index.php?title=开销&action=edit&redlink=1)。对时间有较高要求的应用程序通常使用UDP，因为丢弃数据包比等待或重传导致延迟更可取。

## 可靠性

由于UDP缺乏[可靠性](https://zh.wikipedia.org/wiki/可靠性_(计算机网络))且属于[无连接](https://zh.wikipedia.org/wiki/無連接式通訊)协议，所以应用程序通常必须容许一些[丢失](https://zh.wikipedia.org/wiki/丢包)、错误或重复的[数据包](https://zh.wikipedia.org/wiki/数据包)。某些应用程序（如[TFTP](https://zh.wikipedia.org/wiki/TFTP)）可能会根据需要在应用程序层中添加基本的可靠性机制。[[1\]](https://zh.wikipedia.org/wiki/用户数据报协议#cite_note-forouzan-1)

一些应用程序不太需要可靠性机制，甚至可能因为引入可靠性机制而降低性能，所以它们使用UDP这种缺乏可靠性的协议。流媒体，实时多人游戏和IP语音（[VoIP](https://zh.wikipedia.org/wiki/VoIP)）是经常使用UDP的应用程序。 在这些特定应用中，丢包通常不是重大问题。如果应用程序需要高度可靠性，则可以使用诸如[TCP](https://zh.wikipedia.org/wiki/传输控制协议)之类的协议。

例如，在VoIP中[延迟](https://zh.wikipedia.org/wiki/來回通訊延遲)和[抖动](https://zh.wikipedia.org/wiki/抖动)是主要问题。如果使用TCP，那么任何数据包丢失或错误都将导致抖动，因为TCP在请求及重传丢失数据时不向应用程序提供后续数据。如果使用UDP，那么应用程序则需要提供必要的握手，例如实时确认已收到的消息。

由于UDP缺乏[拥塞控制](https://zh.wikipedia.org/wiki/拥塞控制)，所以需要基于网络的机制来减少因失控和高速UDP流量负荷而导致的拥塞崩溃效应。换句话说，因为UDP发送端无法检测拥塞，所以像使用包队列和丢弃技术的路由器之类的网络基础设备会被用于降低UDP过大流量。[数据拥塞控制协议](https://zh.wikipedia.org/wiki/数据拥塞控制协议)（DCCP）设计成通过在诸如流媒体类型的高速率UDP流中增加主机拥塞控制，来减小这个潜在的问题。

## 应用

许多关键的互联网应用程序使用UDP[[2\]](https://zh.wikipedia.org/wiki/用户数据报协议#cite_note-kuroseross-2)，包括：

- [域名系统](https://zh.wikipedia.org/wiki/域名系统)（DNS），其中查询阶段必须快速，并且只包含单个请求，后跟单个回复数据包；
- [动态主机配置协议](https://zh.wikipedia.org/wiki/动态主机配置协议)（DHCP），用于动态分配[IP地址](https://zh.wikipedia.org/wiki/IP地址)；
- [简单网络管理协议](https://zh.wikipedia.org/wiki/简单网络管理协议)（SNMP）；
- [路由信息协议](https://zh.wikipedia.org/wiki/路由信息协议)（RIP）；
- [网络时间协议](https://zh.wikipedia.org/wiki/網路時間協定)（NTP）。

[流媒体](https://zh.wikipedia.org/wiki/串流媒體)、[在线游戏](https://zh.wikipedia.org/wiki/線上遊戲)流量通常使用UDP传输。 实时视频流和音频流应用程序旨在处理偶尔丢失、错误的数据包，因此只会发生质量轻微下降，而避免了重传[数据包](https://zh.wikipedia.org/wiki/数据包)带来的高[延迟](https://zh.wikipedia.org/wiki/延遲)。 由于TCP和UDP都在同一网络上运行，因此一些企业发现来自这些实时应用程序的UDP流量影响了使用TCP的应用程序的性能，例如[销售](https://zh.wikipedia.org/wiki/销售)、[会计](https://zh.wikipedia.org/wiki/会计)和[数据库系统](https://zh.wikipedia.org/wiki/数据库系统)。 当TCP检测到数据包丢失时，它将限制其数据速率使用率。由于实时和业务应用程序对企业都很重要，因此一些人认为开发[服务质量](https://zh.wikipedia.org/wiki/服务质量)解决方案至关重要。[[3\]](https://zh.wikipedia.org/wiki/用户数据报协议#cite_note-3)

[详情](https://zh.wikipedia.org/wiki/%E7%94%A8%E6%88%B7%E6%95%B0%E6%8D%AE%E6%8A%A5%E5%8D%8F%E8%AE%AE)

TCP 是面向连接的传输，管理了两个端点之间的连接的建立，在连接的生命周期内的有序和可靠的消息传输，以及最后，连接的有序终止。相比之下，在类似于 UDP 这样的无连接协议中，并没有持久化连接的概念，并且每个消息（一个 UDP 数据报）都是一个单独的传输单元。

UDP 没有 TCP 的纠错机制，其中每个节点都将确认它们所接收到的包，而没有被确认的包会被发送方重新传输。



# UDP 广播

UDP 提供了向多个接收者发送消息的额外传输模式

+ 单播——发送消息给一个由唯一地址所标识的单一网络目的地。

+ 多播——传输到一个预定义的主机组。
+ 广播——传输到网络（或者子网）上的所有主机。

# UDP 示例应用程序

*发布/订阅模式 类似于 syslog 这样的应用程序通常会别归类为 发布/订阅 模式：一个生产者或者服务发布事件，而多个客户端进行订阅以接收它们。*

**广播系统概览**

![广播系统概览.png](https://i.loli.net/2020/11/05/POJhFpEDL7bXkr6.png)

# 消息 POJO： LogEvent

在消息处理的应用程序中，数据通常由 POJO 表示，除了实际上的消息内容，其还可以包含配置或处理信息。

在这个应用程序中，我们将会把消息作为事件处理，并且由于该数据来自于日志文件，所以称为 LogEvent。

**LogEvent**

```java
public class LogEvent {

    public static final byte SEPARATOR = (byte) ':';

    private final InetSocketAddress source;

    private final String logfile;

    private final String msg;

    private final long received;

    public LogEvent(String logfile, String msg) {
        this(null, -1L, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received, String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public long getReceivedTimestamp() {
        return received;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "source=" + source +
                ", logfile='" + logfile + '\'' +
                ", msg='" + msg + '\'' +
                ", received=" + received +
                '}';
    }
}
```

# 编写广播者

在广播者中使用的 Netty 的 UDP 相关类。

| Class Name                                                   | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| Interface AddressedEnvelope<br/><M,A extends SocketAddress><br/> extends RefrenceCounted | 定义一个消息，其包装了另一个消息并带有发送者和接收者地址。其中 M 是消息类型，A 是地址类型 |
| class DefaultAddressedEnvelope<br/><M,A extends SocketAddress><br/>implements AddressedEnvelope<M,A> | 提供了上面接口的默认实现                                     |
| class DatagramPacket<br/>extends DefaultAddressedEnvelope<ByteBuf,InetSocketAddress><br/>implements ByteBufHolder | 扩展了上面的类，用 ByteBuf 作为消息数据容器                  |
| Interface DatagramChannel <br/>extends Channel               | 扩展了 Netty 的 Channel 抽象以及支持 UDO 的多播组管理        |
| class NioDatagramChannel <br/>extends AbstractNioMessageChannel <br/>implements DatagramChannel | 定义了一个能够发送和接收 AddressedEnvelope 消息的 Channel 类型 |

**LogEventBroadcaster：ChannelPipeline 和 LogEvent 事件流**

![LogEventBroadcaster：ChannelPipeline 和 LogEvent 事件流.png](https://i.loli.net/2020/11/05/LNXr9qMk7hPEWIw.png)

**LogEventEncoder**

```java
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private final InetSocketAddress remoteAddress;

    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent logEvent, List<Object> out) throws Exception {
        byte[] file = logEvent.getLogfile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
        ByteBuf buf = ctx.alloc().buffer(file.length + msg.length + 1);
        buf.writeBytes(file);
        buf.writeByte(LogEvent.SEPARATOR);
        buf.writeBytes(msg);
        out.add(new DatagramPacket(buf,remoteAddress));
    }

}
```

**LogEventBroadcaster**

```java
public class LogEventBroadcaster {

    private final EventLoopGroup group;

    private final Bootstrap bootstrap;

    private final File file;


    public LogEventBroadcaster(InetSocketAddress address, File file) {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws Exception {
        Channel ch = bootstrap.bind(0).sync().channel();
        long pointer = 0;
        for (; ; ) {
            long len = file.length();
            if (len < pointer) {
                // file was reset
                pointer = len;
            } else if (len > pointer) {
                // Content was added
                RandomAccessFile raf = new RandomAccessFile(this.file, "r");
                raf.seek(pointer);
                String line;
                while ((line = raf.readLine()) != null) {
                    ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
                pointer = raf.getFilePointer();
                raf.close();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }


    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", 8888), new File(""));
        try {
            broadcaster.run();
        } finally {
            broadcaster.stop();
        }
    }
}
```

# 编写监听器

LogEventMonitor 应该有下面几个功能

1. 接收由 LogEventBroadcaster 广播的 UDP DatagramPacket；
2. 将它们解码为 LogEvent；
3. 将 LogEvent 消息写出到 System.out。

**LogEventMonitor 结构**

![](https://i.loli.net/2020/11/05/G4pbHntzsmlIZxC.png)

**LogEventDecoder**

```java
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf data = datagramPacket.content();
        int idx = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String filename = data.slice(0, idx)
                .toString(CharsetUtil.UTF_8);

        String logMsg = data.slice(idx + 1, data.readableBytes())
                .toString(CharsetUtil.UTF_8);
        LogEvent event = new LogEvent(datagramPacket.sender(), System.currentTimeMillis(), filename, logMsg);
        out.add(event);
    }

}
```

**LogEventHandler**

```java
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent event) throws Exception {
        System.out.println(event.toString());
    }

}
```

**LogEventMonitor**

```java
public class LogEventMonitor {

    private final EventLoopGroup group;

    private final Bootstrap bootstrap;

    public LogEventMonitor(InetSocketAddress address) {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LogEventDecoder());
                        pipeline.addLast(new LogEventHandler());
                    }
                })
                .localAddress(address);
    }

    public Channel bind() {
        return bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 7777;
        LogEventMonitor monitor = new LogEventMonitor(new InetSocketAddress(port));
        try {
            Channel channel = monitor.bind();
            System.out.println("running .....");
            channel.closeFuture().sync();
        } finally {
            monitor.stop();
        }
    }
    
}
```

*按照书上来，没用*

