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


| 名称                   | 描述                                                         |
| ---------------------- | ------------------------------------------------------------ |
| getBoolean(int)        | 给定索引值处的 Boolean 值。下面类似                          |
| getByte(int)           |                                                              |
| getUnsignedByte(int)   | 将给定索引处的无符号字节值作为 short 返回                    |
| getMedium(int)         | 24 位的中等 int 值                                           |
| getUnsignedMedium(int) |                                                              |
| getInt(int)            |                                                              |
| getUnsignedInt(int)    | 将给定索引处的无符号 int 值作为 long 返回                    |
| getLong(int)           |                                                              |
| getShort(int)          |                                                              |
| getUnsignedShort(int)  | int getUnsignedShort(int var1)                               |
| getBytes(int,....)     | ByteBuf getBytes(int var1, ByteBuf var2); 等等。将该缓冲区<br/>中从给定索引开始的数据传送到指定的目的地 |

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

| 名称                                                         | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| readMedium()                                                 | 返回当前 readerIndex 处的 24 位的中等 int 值，并将 readerIndex +3 |
| readUnsignedMedium()                                         | 返回当前 readerIndex 处的 24 位无符号的中等 int 值，并将 readerIndex +3 |
| readInt()                                                    | 返回当前 readerIndex 的 Int 值，并将 readerIndex +4          |
| readLong()                                                   | +8                                                           |
| readShort()                                                  | +2                                                           |
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