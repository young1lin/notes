# 简介

Redis 是一个主要由 Salvatore Sanfilippo（Antirez）开发的开源内粗数据结构存储器。因为其丰富的数据类型结构的值，可以被用作缓存、数据库、分布式锁和消息队列等<sup>[1](https://weread.qq.com/web/reader/75732070719551157574079)</sup>。

# 数据类型

Redis 包含了 5 种的基本数据类型

1. String

2. Hash

3. List

4. Set

5. SortedSet

和四个特殊的数据类型

1. HyperLogLog
2. BitMap
3. GEO（地理坐标）
4. Steam（流）

下面讲的是 5 种的基本数据类型，后面的四个之后再说。

注意，这个 5 种的基本数据类型不代表它的具体实现。它的具体实现是根据值的大小，而不同的。下面有个图<sup>[2](https://static001.geekbang.org/resource/image/82/01/8219f7yy651e566d47cc9f661b399f01.jpg)</sup>，

![Redis 数据类型和底层数据结构对应图.png](https://i.loli.net/2020/12/13/n8bsoCXH3jMcRBv.png)

可以看出来，除了它的表面上的对应的实现，还有压缩列表这个数据结构(ZipList)，**在它的值到达一定阈值时**，会自动转换为其表面的实现。

其实可以这么记，String 就是 Simple Dynamic String，*Set 是哈希表和整数数组，List 、SortedList 和 Hash 在一定阈值后变成其对应实现。List -> 双向链表，Hash -> 哈希表，SortedSet -> 跳表.*

下面是 Redis 6.0.5 的 redis.conf 的配置及其注释**（可以跳过不看）。**

```properties
############################### ADVANCED CONFIG ###############################
# Hash 对应的 ZipList设置 -------------------------------
# Hashes are encoded using a memory efficient data structure when they have a
# small number of entries, and the biggest entry does not exceed a given
# threshold. These thresholds can be configured using the following directives.
hash-max-ziplist-entries 512
hash-max-ziplist-value 64

# list 对应的 ZipList 设置 -------------------------------
# Lists are also encoded in a special way to save a lot of space.
# The number of entries allowed per internal list node can be specified
# as a fixed maximum size or a maximum number of elements.
# For a fixed maximum size, use -5 through -1, meaning:
# -5: max size: 64 Kb  <-- not recommended for normal workloads
# -4: max size: 32 Kb  <-- not recommended
# -3: max size: 16 Kb  <-- probably not recommended
# -2: max size: 8 Kb   <-- good
# -1: max size: 4 Kb   <-- good
# Positive numbers mean store up to _exactly_ that number of elements
# per list node.
# The highest performing option is usually -2 (8 Kb size) or -1 (4 Kb size),
# but if your use case is unique, adjust the settings as necessary.
list-max-ziplist-size -2

# set 最大值 -------------------------------
# Sets have a special encoding in just one case: when a set is composed
# of just strings that happen to be integers in radix 10 in the range
# of 64 bit signed integers.
# The following configuration setting sets the limit in the size of the
# set in order to use this special memory saving encoding.
set-max-intset-entries 512

# zset 命令也就是 SortedList 最大的 ZipList 值 -------------------------------
# Similarly to hashes and lists, sorted sets are also specially encoded in
# order to save a lot of space. This encoding is only used when the length and
# elements of a sorted set are below the following limits:
zset-max-ziplist-entries 128
zset-max-ziplist-value 64

```

**下面我讲下对应的数据类型及其数据结构实现**

# 总览

为了实现从键到值的快速访问，Redis 使用了一个哈希表来保存所有键值对。

一个哈希表，其实就是一个数组，数组的每个元素称为一个哈希桶。所以，我们常说，一个哈希表是由多个哈希桶组成的，每个哈希桶中保存了键值对数据。

哈希桶中的元素保存的并不是值本身，而是指向具体值的指针。指针你懂我意思吧，就是个内存地址，并不是实际内容。这个其实就是 HashMap，讲这个，就顺便复习下 HashMap 的东西。*tips：这里的 *key 其实是 c++ 里面的声明变量的方式。和 Java 里面的 String s 这种类似*

![全局哈希表.png](https://i.loli.net/2020/12/13/IqZMVBe6nlJrPuC.png)

## HashTable 问题

这里其实 Java 里面的本来应该常用哈希表（HashTable）的，这里的哈希表和 Java  HashMap 实现是差不多的。后者是双向链表（过一定阈值）转红黑树。

### 1. 哈希冲突

哈希冲突就是 12%6 = 0，24%6 = 0，或者两个不同的键算出了同样的哈希值。单单靠计算哈希值然后取模的方式，不同的键值肯定会存在一样的取模后的值。所以哈希表里面就会用往下加 。但是这个往下加也有个限度，它会一个个加，加到 10w 个，那就要往下找 10w 次，对本来是 O(1) 查找速度的数据结构肯定接受不了。下面 rehash 就有用了。

### 2. rehash

为了解决那么多的节点可能出现在哈希表的一个下标下，那么就需要 rehash 来重新调整哈希表的大小，即数组大小。

**为了使 rehash 操作更高效，Redis 默认使用了两个全局哈希表：哈希表 1 和哈希表 2。一开始，当你刚插入数据时，默认使用哈希表 1，此时的哈希表 2 并没有被分配空间。随着数据逐步增多，Redis 开始执行 rehash，这个过程分为三步<sup>[3]</sup>：**

1. 给哈希表 2 分配更大的空间，例如是当前哈希表 1 大小的两倍；
2. 把哈希表 1 中的数据重新映射并拷贝到哈希表 2 中；
3. 释放哈希表 1 的空间。

到此，我们就可以从哈希表 1 切换到哈希表 2，用增大的哈希表 2 保存更多数据，而原来的哈希表 1 留作下一次 rehash 扩容备用。

这个过程看似简单，但是第二步涉及大量的数据拷贝，如果一次性把哈希表 1 中的数据都迁移完，会造成 Redis 线程阻塞，无法服务其他请求。此时，Redis 就无法快速访问数据了。为了避免这个问题，Redis 采用了**渐进式 rehash**。

**简单来说就是在第二步拷贝数据时，Redis 仍然正常处理客户端请求，每处理一个请求时，从哈希表 1 中的第一个索引位置开始，顺带着将这个索引位置上的所有 entries 拷贝到哈希表 2 中；等处理下一个请求时，再顺带拷贝哈希表 1 中的下一个索引位置的 entries。**如下图所示：

![渐进式 rehash](https://static001.geekbang.org/resource/image/73/0c/73fb212d0b0928d96a0d7d6ayy76da0c.jpg)

## 压缩列表 ZipList 介绍

压缩列表是Redis为了节约内存而开发的，是由一系列特殊编码的连续内存块组成的顺序型（sequential）数据结构。一个压缩列表可以包含任意多个节点（entry），每个节点可以保存一个字节数组或者一个整数值。

压缩列表实际上类似于一个数组，数组中的每一个元素都对应保存一个数据。和数组不同的是，压缩列表在表头有三个字段 zlbytes、zltail 和 zllen，分别表示列表长度、列表尾的偏移量和列表中的 entry 个数；压缩列表在表尾还有一个 zlend，表示列表结束。

**在压缩列表中，如果我们要查找定位第一个元素和最后一个元素，可以通过表头三个字段的长度直接定位，复杂度是 O(1)。而查找其他元素时，就没有这么高效了，只能逐个查找，此时的复杂度就是 O(N) 了。**

![压缩列表 ZipList.png](https://i.loli.net/2020/12/13/4vCA6IgxzS29kRP.png)

**下面不是重点可以跳过**

|  属性   |   类型   |  长度  |                             用途                             |
| :-----: | :------: | :----: | :----------------------------------------------------------: |
| zlbytes | unit32_t | 4字节  | 记录整个压缩列表占用的内存字节数；在对压缩列表进行内存重分配，或者计算 zlend 的位置时使用。 |
| zltail  | unit32_t | 4字节  | 记录压缩列表表尾节点距离压缩列表的起始地址有多少字节：通过这个偏移量，程序无需遍历压缩列表就可以确定表尾节点的地址。 |
|  zllen  | unit16_t | 2字节  | 记录了压缩列表包含的节点数量：当这个属性的值小于 UINT16_MAX（65535）时，这个属性的值就是压缩力表包含节点的数量；等于时，节点的真实数量需要遍历整个压缩列表才能计算得出。（**所以压缩列表节点数不宜过大**） |
| entryX  | 列表节点 | 不确定 |    压缩列表包含的各个节点，节点的长度由节点保存的内容决定    |
|  zlend  | unit8_t  | 1字节  |     特殊值 0xFF （十进制 255），用于标记和压缩列表的末端     |

下面是一个压缩列表的样例<sup>[4]</sup>

![包含 5 个节点的压缩列表 _1_.png](https://i.loli.net/2020/12/13/n1foXe3tvPsONSW.png)

+ 列表zlbytes属性的值为0xd2（十进制210），表示压缩列表的总长为210字节。
+ 列表zltail属性的值为0xb3（十进制179），这表示如果我们有一个指向压缩列表起始地址的指针p，那么只要用指针p加上偏移量179，就可以计算出表尾节点entry5的地址。
+ 列表zllen属性的值为0x5（十进制5），表示压缩列表包含五个节点。

**压缩列表节点数不宜过大！！！**

## 跳表 SkipList

跳表在链表的基础上，增加了多级索引，通过索引位置的几个跳转，实现数据的快速定位。

跳跃表（SkipList）是一种有序数据结构，它通过在每个节点中维持多个指向其他节点的指针，从而达到快速访问节点的目的。

跳跃表支持平均 O（logN）、最坏 O（N）复杂度的节点查找，还可以通过顺序性操作来批量处理节点。

在大部分情况下，跳跃表的效率可以和平衡树相媲美，并且因为跳跃表的实现比平衡树要来得更为简单，所以**有不少程序都使用跳跃表来代替平衡树。**

和链表、字典等数据结构被广泛地应用在Redis内部不同，**Redis只在两个地方用到了跳跃表**，一个是实现**有序集合键**，另一个是在**集群节点中用作内部数据结构**，除此之外，跳跃表在Redis里面没有其他用途。

下面是演示跳表找一个节点。以此类推，三级索引，N 级索引。**当数据量很大时，跳表的查找复杂度就是 O(logN)。**

![跳表.png](https://i.loli.net/2020/12/13/AKBT6WLskvbYr2o.png)

[更为详细的跳表介绍](https://time.geekbang.org/column/article/42896)（可以不看）

*还有，为什么选择跳表而不是平衡树，看这个[解释](https://www.jianshu.com/p/8ac45fd01548)（**可以跳过不看**）*

## 数据结构的时间复杂度

|   名称   | 时间复杂度 |
| :------: | :--------: |
|  哈希表  |    O(1)    |
|   跳表   |  O(logN)   |
| 双向链表 |    O(N)    |
| 压缩列表 |    O(N)    |
| 整数列表 |    O(N)    |

## 不同操作的复杂度

不同操作的复杂度集合类型的操作类型很多，有读写单个集合元素的，例如 HGET、HSET，也有操作多个元素的，例如 SADD，还有对整个集合进行遍历操作的，例如 SMEMBERS。这么多操作，它们的复杂度也各不相同。而复杂度的高低又是我们选择集合类型的重要依据。

1. **单元素操作，是指每一种集合类型对单个数据实现的增删改查操作。**例如，Hash 类型的 HGET、HSET 和 HDEL，Set 类型的 SADD、SREM、SRANDMEMBER 等。这些操作的复杂度由集合采用的数据结构决定，例如，HGET、HSET 和 HDEL 是对哈希表做操作，所以它们的复杂度都是 O(1)；Set 类型用哈希表作为底层数据结构时，它的 SADD、SREM、SRANDMEMBER 复杂度也是 O(1)。*集合类型支持同时对多个元素进行增删改查，例如 Hash 类型的 HMGET 和 HMSET，Set 类型的 SADD 也支持同时增加多个元素。此时，这些操作的复杂度，就是由单个元素操作复杂度和元素个数决定的。例如，HMSET 增加 M 个元素时，复杂度就从 O(1) 变成 O(M) 了。*
2. **范围操作，是指集合类型中的遍历操作，可以返回集合中的所有数据**，比如 Hash 类型的 HGETALL 和 Set 类型的 SMEMBERS，或者返回一个范围内的部分数据，比如 List 类型的 LRANGE 和 ZSet 类型的 ZRANGE。**这类操作的复杂度一般是 O(N)，比较耗时，我们应该尽量避免。**Redis 从 2.8 版本开始提供了 SCAN 系列操作（包括 HSCAN，SSCAN 和 ZSCAN），这类操作实现了渐进式遍历，每次只返回有限数量的数据。
3. **统计操作，**是指**集合类型对集合中所有元素个数的记录**，例如 LLEN 和 SCARD。这类操作复杂度只有 O(1)，这是因为当集合类型采用压缩列表、双向链表、整数数组这些数据结构时，这些结构中专门记录了元素的个数统计，因此可以高效地完成相关操作。
4. **例外情况，**是指某些数据结构的特殊记录，例如**压缩列表和双向链表都会记录表头和表尾的偏移量**。这样一来，对于 List 类型的 LPOP、RPOP、LPUSH、RPUSH 这四个操作来说，它们是在列表的头尾增删元素，这就可以通过偏移量直接定位，所以它们的复杂度也只有 O(1)，可以实现快速操作。



# 参考

> 《Redis 使用手册》

> [ 极客时间《Redis 核心技术实战》](https://time.geekbang.org/column/article/268253)