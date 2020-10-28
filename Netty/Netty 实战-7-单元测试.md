# EmbeddedChannel

ChannelPipline + ChannelHandler 将长任务分解为小的可充重用的组件，每个组件都处理一个明确定义的任务或者步骤。

将入站数据或者出站数据写入到 EmbeddedChannel 中，然后检查是否有任何东西送达到了 ChannelPipeline 的尾端。以这种方式，便可以确定消息是否已经被编码或者被解码过了，以及是否触发了任何的 ChannelHandler 动作。

**特殊的 EmbeddedChannel 方法**

| 名称                         | 指责                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| writeInbound(Object....msgs) | 将入站消息写入到 EmbeddedChannel 中，如果可以通过 readInbound 方法从 EmbeddedChannel 中读取数据，则返回 true |
| readInbound()                | 从 EmbeddedChannel 中读取一个入站消息。任何返回的东西都穿越了整个 ChannelPipline。如果没有任何可供读取的，则返回 null。 |
| writeOutbound(Object...msgs) | 将出站消息写入到 EmbeddedChannel 中。如果现在可以通过 readOutbound 方法从 EmbeddedChannel 中读取到什么东西，则返回 true |
| readOutbound()               | 从 EmbeddedChannel 中读取一个出站消息。任何返回的东西都穿越了整个 ChannelPipeline。如果没有任何可供读取的，则返回 null |
| finish()                     | 将 EmbeddedChannel 标记为完成，并且如果有可被读取的入站数据或者出站数据，则返回 true。这个方法还将会调用 EmbeddedChannel 上的 close 方法。 |

入站数据又 ChannelInboundHandler 处理，代表从远程节点读取的数据。出站数据由 ChannelOutboundHandler 处理，代表将要写到远程节点的数据。根据你要测试的 ChannelHandler，是用 *Inbound 或者 *Outbound 方法对，或者兼而有之。

在每种情况下，消息都将会传递过 ChannelPipeline，并且被相关的 ChannelInboundHandler 或者 ChannelOutboundHandler 处理。如果消息没有被消费，那么可以使用 readInboud 或者 readOutbound 方法来处理过了这些消息之后，酌情把它们从 Channel 中读出来。

**EmbeddedChannel 的数据流**

![EmbeddedChannel 的数据流.png](https://i.loli.net/2020/10/28/EbxlHItWz6w2iSk.png)

# 使用 EmbeddedChannel 测试 ChannelHandler

## JUnit 断言类

会用的

## 测试入站消息

展示了一个简单的 ByteToMessageDecode 实现。

**通过 FixedLengthFrameDecoder 解码**

![通过 FixedLengthFrameDecoder 解码.png](https://i.loli.net/2020/10/28/fBEmPMsL65yVCzO.png)

**FixedLengthFrameDecoder**

```java
package me.young1lin.netty.demo.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 9:23 下午
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if(frameLength < 0){
            throw new IllegalArgumentException("frameLength muse be a positive integer"+frameLength);
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= frameLength){
            ByteBuf buf = in.readBytes(frameLength);
            out.add(buf);
        }
    }

}
```


**FixedLengthFrameDecoderTest**

```java
package me.young1lin.netty.demo.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 9:27 下午
 */
public class FixedLengthFrameDecoderTest {

    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());

        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(3),read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3),read);
        read.release();
		read = channel.readInbound();
        assertEquals(buf.readSlice(3),read);
        read.release();
        
        assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    public void testFramesDecoded2(){
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(7)));

        assertTrue(channel.finish());
        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(3),read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3),read);
        read.release();
        
        // 同样这里会报错，期待是 null，实际是还有值的
        assertNull(channel.readInbound());
        buf.release();
    }
    
}
```

上面的 testFramesDecoded 方法验证了：一个包含 9 个可读字节的 ByteBuf 被解码为 3 个 ByteBuf，每个都包含了 3 字节。需要注意的是，仅通过一次 对 writeInbound 方法的调用，ByteBuf 是如何被填充了 9 个可读字节的。在此之后，通过执行 finish 方法，将 EmbeddedChannel 标记为已完成的状态，最后通过调用 readeInbound 方法，从 EmbeddedChannel 中正好读取了 3 个帧换个一个 null。

## 测试出站消息

简单了解 AbsIntegerEncoder，它是 Netty 的 MessageToMessageEncoder 的一个特殊化的实现，用于将负值整数转换为绝对值。

工作方式

+ 持有 AbsIntegerEncoder 的 EmbeddedChannel 将会以 4 字节的负整数形式写出站数据。
+ 编码器将从传入的 ByteBuf 中读取每个负整数，并将会调用 Math.abs()方法来获取其绝对值。
+ 编码器将会把每个负整数的绝对值写到 ChannelPipeline 中。

AbsIntegerEncoder

```java
package me.young1lin.netty.demo.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 9:59 下午
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        while (msg.readableBytes() >= 4){
            // int 类型是 4 byte == 32 bit 的
            int value = Math.abs(msg.readInt());
            out.add(value);
        }
    }

}
```

AbsIntegerEncoderTest

```java
package me.young1lin.netty.demo.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 10:02 下午
 */
public class AbsIntegerEncoderTest {

    @Test
    public void testEncoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 10; i++) {
            buf.writeInt(i * -1);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());

        assertTrue(channel.writeOutbound(buf));
        assertTrue(channel.finish());

        // read bytes
        for (int i = 0; i < 10; i++) {
            assertEquals(i,(int)channel.readOutbound());
        }

        assertNull(channel.readOutbound());
    }
}
```

执行步骤

1. 将 4 字节的负整数写到一个新的 ByteBuf 中。
2. 创建一个 EmbeddedChannel，并为它分配一个 AbsIntegerEncoder。
3. 调用 EmbeddedChannel 上的 writeOutbound 方法来写入该 ByteBuf。
4. 标记该 Channel 为已完成状态
5. 从 EmbeddedChannel 的出站短读取素偶有的整数，并做比较

# 测试异常处理

应用程序通常需要执行比转换数据更加复杂的任务。下面是演示如果一帧太长了，就抛出异常。

![通过 FrameChunkDecoder 解码 (1).png](https://i.loli.net/2020/10/28/Rg1QmdUoPDzJfwA.png)



FrameChunkDecoder

```java
package me.young1lin.netty.demo.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 10:21 下午
 */
public class FrameChunkDecoder extends ByteToMessageDecoder {
    private final int maxFrameSize;

    public FrameChunkDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if (readableBytes > maxFrameSize) {
            in.clear();
            throw new TooLongFrameException();
        }
        ByteBuf buf = in.readBytes(readableBytes);
        out.add(buf);
    }

}
```

FrameChunkDecoderTest

```java
package me.young1lin.netty.demo.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/28 10:24 下午
 */
public class FrameChunkDecoderTest {

    @Test
    public void testFrameDecoded(){
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

        assertTrue(channel.writeInbound(input.readBytes(2)));

        try {
            channel.writeInbound(input.readBytes(4));
            //Assert.fail();
        }catch (TooLongFrameException e){

        }

        assertTrue(channel.writeInbound(input.readBytes(3)));
        assertTrue(channel.finish());

        // Read frames
        ByteBuf read = channel.readInbound();
        assertEquals(buffer.readSlice(2),read);
        read.release();

        read = channel.readInbound();
        assertEquals(buffer.skipBytes(4).readSlice(3),read);
        read.release();
        buffer.release();

    }

}
```