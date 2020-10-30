# 摘要

网络只将数据看作是原始的字节序列。然而，我们的应用程序则会把这些字节组织成有意义的信息。在数据和网络字节流之间做相互转换是最常见的编程任务之一。例如，可能需要处理标准的格式或者协议（FTP 或 Telnet）、实现一种由第三方定义的专有二进制协议，或者扩展一种由自己的组织创建的遗留的消息格式。

将应用程序的数据转换为网络格式，以及将网络格式转换为应用程序的数据的组件分别叫做编码器和解码器，同时具有这两种功能的单一组件叫做编解码器。Netty 提供了一系列用来创建所有这些编码器、解码器以及编解码器的工具，从专门为知名协议（如 Http 以及 Base64）预构建的类，到你可以按需要定制的通用的消息转化编码器，应有尽有。

# 编解码器框架

## 编解码器

每个网络应用程序都必须定义如何解析在两个节点之间来回传输的原始节点，以及如何将其和目标应用程序的数据格式做相互转换。这种转换逻辑由编解码器处理，编解码器由编码器和解码器组成，它们每种都可以将字节流从一种格式转换为另一种格式。

编码器将数据转换为合适于传输的格式，解码器将网络字节流转回应用程序的消息格式。因此，编码器操作出站数据，而解码器处理入站数据。

## 解码器

+ 将字节解码为消息——ByteToMessageDecoder 和 ReplayingDecoder
+ 将一种消息类型解码为另一种——MessageToMessageDecoder

Netty 的解码器实现了 ChannelInboundHandler。

我就感觉 ChannelPipeline 像责任链模式，ChannelHandler 就想 SpringMVC 里面的 interceptor

## 抽象类 BNyteToMessagekDecoder

将字节解码为消息（或则会另一个字节序列）。

**ByteToMessageDecoder**

| Method Name                                                  | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| decode(<br/>ChannelHandlerContext ctx,<br/>ByteBufin,<br/>List<Object out>) | 必须实现的的唯一抽象方法。decode 方法被调用时将会传入过一个包含了传入数据的 ByteBuf，以及一个用来添加解码消息的 List。对这个方法的调用将会重复进行，直到确定没有新的元素被添加到该 List，或者该 ByteBuf 中没有更多可读取的字节时为止。然后，如果该 List 不为空，那么它的内容将会被传递给 ChannelPipeline 中的下一个 ChannnelInboundHandler |
| decodeLast(<br/>ChannelhandlerContext ctx,<br/>ByteBuf in<br/>,List<Object> out) | Netty 提供的这个默认实现知识简单地调用 decode 方法。当 Channel 的状态变为非活动时，这个方法将会被调用一次，可以重写此方法以提供特殊的处理。 |

**ToIntegerDecoder**

```java
public class ToIntegerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) {
            // 这里会自动装箱成 Integer，因为 List 中不允许添加基础类型，编译的时候会默认添加 Integer.valueOf(in.readInt) 方法
            out.add(in.readInt());
        }
    }

}
```

每次都要判断是否大于 4 字节，后面有 RelayingDecoder 以少量的开销消除了这个事。

**编解码器的引用计数**

一旦被编码器或者解码，它就会被 ReferenceCountUtil#release(message) 调用自动释放，如果需要保留引用以便稍后使用，那么可以调用 ReferenceCountUtil#retain(message) 方法，这会增加该引用计数，从而防止该消息被释放。

## 抽象类 ReplayingDecoder

Replaying 扩展了 ByteToMessageDecoder 类，使得不用 readableBytes 方法。它通过使用一个自定义的 ByteBuf 实现，ReplayingDecoderByteBuf，包装传入的 ByteBuf 实现了这一点，其将在内部执行该调用（调用 readableBytes 方法）。

**ToIntegerDecoder2 类扩展了 ReplayingDecoder**

```java
/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/29 9:10 下午
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 九折？
        out.add(in.readInt());
    }

}
```

如果读取不了，就会向基类抛出一个 Error（实际是 Signal），然后在基类中捕获并处理。当有更多的数据可供读取时，该 decode 方法将会被再次调用。

+ 并不是所有的 BuyteBuf 操作都被支持，如果调用了一个不被支持的方法，将会抛出一个 UnsupportedOperationException
+ ReplayingDecoder 稍慢于 ByteToMessageDecoder。（这不是废话吗，做了读取之前判空的，还有捕捉异常的操作）

*如果使用 ByteToMessageDecoder 不会引入太多的复杂性，那么请使用它；否则，请使用 ReplayingDecoder。*

**更多额解码器**

+ *LineBasedFrameDecoder —— 这个类在 Netty 内部也有使用，它使用了行尾控制字符（\n 或者 \r\n）来解析消息数据。换行符？ Hive？Redis？*
+ *HttpObjectDecoder——一个 HTTP 数据的解码器。*

## 抽象类 MessageToMessageDecoder

`public abstract class MessageToMessageDecoder<I> extends ChannelInboundHandlerAdapter`

类型参数 I 指定了decode() 方法的输入参数 msg 的类型，它是必须实现的唯一方法。

**MessageToMessageDecoder API**

| Method Name                                                  | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| decode(ChannelHandlerContext ctx<br/>,I msg<br/>,List<Object> out) | 对于每个需要被解码器为另一种格式的入站消息来说，该方法将会被调用。解码消息随后会被传递给 ChannelPipeline 中的下一个 ChannelInboundHandler |

**IntegerToStringDecoder 类**

```java
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        // 9 折？这么简单？
        out.add(String.valueOf(msg));
    }

}
```

HttpObjectAggregator

要实现两个构造方法。

## TooLongFrameException 类

由于 Netty 是一个异步框架，所以需要在字节可以解码之前在内存中缓存它们，因此，不能让解码器缓冲大量的数据以至于耗尽可用的内存。为了解除这个常见的顾虑，Netty 提供了 TooLongFrameException 类，其将由解码器在帧超出指定的大小限制时抛出。

为了避免这个情况，可以设置一个最大的字节数的阈值，如果超出该阈值，则会导致抛出一个 TooLongFrameException（随后会被 ChannelHandler#exceptionCaught 方法捕获）。然后，如何处理该异常则完全取决于该解码器的用户。某些协议（如 HTTP）可能允许你返回一个特殊的响应。而在其他的情况下，唯一的选择可能就是关闭对应连接。

```java
public class SafeByteToMessageDecoder extends ByteToMessageDecoder {

    private static final int MAX_FRAME_SIZE = 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readable = in.readableBytes();
        // 9折？？
        if(readable > MAX_FRAME_SIZE){
            in.skipBytes(readable);
            throw new TooLongFrameException("Frame too big!");
        }
        // do something
    }
}
```

# 编码器

+ 将消息编码为字节
+ 将消息编码为消息

## 抽象类 MessageToByteEncoder

MessageToByteEncoder API

| MethodName                                                   | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| encode(ChannelHandlerContext ctx,<br/>I msg,<br/>ByteBuf out) | encode()方法是实现的唯一抽象方法。它被调用时将会传入要被该类编码为 BuyteBuf 随后将会被转发给 ChannelPipeline 中的下一个 ChannelOutboundHandler |

这个类只有一个方法，而且解码器有两个。原因是解码器通常需要在 Channel 关闭之后产生最后一个消息（因此也就有了 decodeLast 方法）。这显然不是用于编码器的场景——在连接被关闭之后仍然产生一个消息是毫无意义的。

下面展示了 ShortToByteEncoder，接受一个 Short 类型的实例作为消息，将它编码为 Short 的原始类型值，并将它写入 ByteBuf 中，其将随后被转发给 ChannelPipeline 中的下一个 ChannelOutboundHandler。每个传出的 Short 值都将会占用 ByteBuf 中的 2 字节。

## 抽象类 MessageToMessageEncoder

**MessageToMessageEncoder API**

| MethodName                                                   | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| encode(ChannelhadnlerContext ctx<br/>,I msg<br/>,List<Object> out) | 这是唯一需要实现的方法。每个通过 write 方法写入的消息都将会被传递给 encode 方法，以编码为一个或者多个出站消息。随后，这些出战消息将会被转发给 Channelpipeline 中的下一个 ChannelOutboundHandler |

书上这些编码解码的图太简单了，我就画一个就懂了

![IntegerToStringEncoder.png](https://i.loli.net/2020/10/29/v4oBltwxPRHIbpQ.png)

```java
public class IntegerToStringEncoder extends MessageToMessageEncoder<Integer> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        // 越看越像 ChannelHandler，结果一看，缝合怪继承了 ChannelOutboundHandlerAdapter
        out.add(String.valueOf(msg));
    }

}
```

# 抽象的编解码器类

编解码器同时实现了 ChannelInboundHandler 和 ChannelOutboundHandler

## ByteToMessageCodec 抽象类

没什么好说的，解码 -> 编码

**ByteToMessageCodec API**

| MethodName                                                   | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| decode(ChannelHandlerContext ctx,<br/>ByteBuf in,<br/>List<Object>) | 只要有字节可以被消费，这个方法就将会被调用。它将入站 ByteBuf 转换为指定的消息格式，并将其转发给 ChannelPipeline 中的下一个 ChannelInboundHandler |
| decodeLast(ChannelHandlerContext ctx,<br/>ByteBuf in,<br/>List<Object> out) | 这个方法的默认实现委托给了 decode 方法。它只会在 Channel 的状态变为非活动时调用一次。它可以被重写以实现特殊的处理。 |
| Encode(ChannelHandlerContext ctx,I msg,ByteBuf out)          | 对于每个将被编码并写入出站 ByteBuf 的（类型为 I 的）消息来说，这个方法都将会被调用。 |

## 抽象类 MessageToMessageCodec
`public abstract class MessageToMessageCodec<INBOUND_IN, OUTBOUND_IN> extends ChannelDuplexHandler {}`

这个 ChannelDuplexHandler 继承自 ChannelInboundHandlerAdapter 实现了 ChannelOutboundHandler

感觉这个应该叫适配器，继承一个类，实现一个接口，来实现整合。标准的适配器模式代码。

**MessageToMessageCodec 的方法**

| MethodName                                                   | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| Protected abstract decode(ChannelHandlerContext ctx,<br/>INBOUND_IN msg,<br/>List<Obejct> out) | 这个方法被调用时会被传入 INBOUND_IN 类型的消息，它将把它们解码为 OUTBOUND_IN 类型的消息，这些消息将被转发给 ChannelPipeline 中的下一个 ChannelInboundHandler |
| Protected abstract encode(ChannelHandlerContext ctx,<br/>OUTBOUND_IN msg,<br/>List<Object> out) | 和上面类似，缝合起来                                         |

 这编码和解码器，就是特殊的 ChannelHandler 啊。

下面为典型吃饱了没事干，下面 decode 的代码，书上都是 else 极其不符合格式化的代码的，估计就是 cv 大法，作者没有使用 ctrl+option+L 最后进行格式化代码，只是复制粘贴。

```java
public class WebSocketConvertHandler extends MessageToMessageCodec<WebSocketFrame, WebSocketConvertHandler.MyWebSocketFrame> {

    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketConvertHandler.MyWebSocketFrame msg, List<Object> out) throws Exception {
        // payload 可以看作是载体，🔧罢了。Spring cloud Kafka 里也是用的 payload 名称。
        ByteBuf payload = msg.getData().duplicate().retain();
        switch (msg.getType()) {
            case BINARY:
                out.add(new BinaryWebSocketFrame(payload));
                break;
            case PING:
                out.add(new PingWebSocketFrame(payload));
                break;
            case PONG:
                out.add(new PongWebSocketFrame(payload));
                break;
            case TEXT:
                out.add(new TextWebSocketFrame(payload));
                break;
            case CLOSE:
                out.add(new CloseWebSocketFrame(true, 0, payload));
                break;
            case CONTINUATION:
                out.add(new ContinuationWebSocketFrame(payload));
                break;
            default:
                throw new IllegalStateException("Unsupported websocket msg" + msg);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf payload = msg.content().duplicate().retain();
        // 吃饱了没事干
        if (msg instanceof BinaryWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.BINARY, payload));
        } else if (msg instanceof CloseWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.CLOSE, payload));
        } else if (msg instanceof PingWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.PING, payload));
        } else if (msg instanceof PongWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.PONG, payload));
        } else if (msg instanceof ContinuationWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.CONTINUATION, payload));
        } else if (msg instanceof TextWebSocketFrame) {
            out.add(new MyWebSocketFrame(MyWebSocketFrame.FrameType.TEXT, payload));
        } else {
            throw new IllegalStateException("Unsupported WebSocket msg" + msg);
        }
    }

    public static final class MyWebSocketFrame {
        public enum FrameType {
            BINARY,
            CLOSE,
            PING,
            PONG,
            TEXT,
            CONTINUATION
        }

        private final FrameType type;

        private final ByteBuf data;

        public MyWebSocketFrame(FrameType type, ByteBuf data) {
            this.data = data;
            this.type = type;
        }

        public FrameType getType() {
            return type;
        }

        public ByteBuf getData() {
            return data;
        }
    }
}
```

## CombinedChannelDuplexHandler 类

```java
public class CombinedChannelDuplexHandler<I extends ChannelInboundHandler, O extends ChannelOutboundHandler>
        extends ChannelDuplexHandler {}
```

这个类充当了 ChannelInboundHandler 和 ChannelOutboundHandler（该类型饿的参数 I 和 O）的容器。

**ByteToCharDecoder 类**

```java
public class ByteToCharDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 2){
            out.add(in.readChar());
        }
    }
}
```

char 是两字节，所以这里读的 char 是 2 byte 的，然后包装成 Character 写入到 List。

**CombinedByteCharCodec** 通过父类的构造器，注入

```java
public class CombinedByteCharCodec extends CombinedChannelDuplexHandler<ByteToCharDecoder,CharToByteEncoder> {
    public CombinedByteCharCodec(){
        // 9 折?
        super(new ByteToCharDecoder(),new CharToByteEncoder());
    }
}
```

CombinedChannelDuplexHandler

```java
/**
 * Creates a new instance that combines the specified two handlers into one.
 */
public CombinedChannelDuplexHandler(I inboundHandler, O outboundHandler) {
    // 这里就确保不共享
    // 父类有个 cache 的 Map，来缓存当前的类和是否 sharable
    ensureNotSharable();
    //
    init(inboundHandler, outboundHandler);
}
```

```java
protected final void init(I inboundHandler, O outboundHandler) {
    // 验证的一些操作，判空和类型的一些操作，还行，跟我平时写的代码差不多，把长而太多逻辑代码，拆分成一个小的方法
    validate(inboundHandler, outboundHandler);
    this.inboundHandler = inboundHandler;
    this.outboundHandler = outboundHandler;
}
```

```java
private void validate(I inboundHandler, O outboundHandler) {
    if (this.inboundHandler != null) {
        throw new IllegalStateException(
                "init() can not be invoked if " + CombinedChannelDuplexHandler.class.getSimpleName() +
                        " was constructed with non-default constructor.");
    }

    ObjectUtil.checkNotNull(inboundHandler, "inboundHandler");
    ObjectUtil.checkNotNull(outboundHandler, "outboundHandler");

    if (inboundHandler instanceof ChannelOutboundHandler) {
        throw new IllegalArgumentException(
                "inboundHandler must not implement " +
                ChannelOutboundHandler.class.getSimpleName() + " to get combined.");
    }
    if (outboundHandler instanceof ChannelInboundHandler) {
        throw new IllegalArgumentException(
                "outboundHandler must not implement " +
                ChannelInboundHandler.class.getSimpleName() + " to get combined.");
    }
}
```

