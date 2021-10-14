### 2.2. HBase管理页面

HBaseweb管理页面主要用于HBase服务的各种数据和信息的查看，下面我们将介绍管理页面的一些简单操作。

#### 2.2.1. Hmaster管理页面

打开HBase active master 管理页面的方法有两种：

1. 根据集群的active master的ip地址打开: http://master_node_ip:60010 。如下图：

   ![hbase home](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/hbase-home.png)

   图 2. hyperbase主页面

2. 通过TDH管理页面中HBase服务的Hmaster的 **Service Link** 打开，详细流程如下：

   1. Transwarp Data HubWEB管理页面也要根据集群的active master的ip地址打开，地址一般是 http://master_node_ip:8180 。

   2. 打开对应的HBase服务的 **Roles** 页面。如下图：

      ![hbase roles](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/hbase-roles.png)

      图 3. hyperbase roles

      |      | 页面左上角服务名后的圆点颜色表示集群中的Hyperbase服务的状态，比如当前是绿色的 green（HEALTHY） ，健康状态。另两种状态是 yellow（WARNING） 和 red（DOWN） 。 |
      | ---- | ------------------------------------------------------------ |
      |      |                                                              |

      通过每个Hmaster对应的 **Service Link** 可以打开Hmaster管理页面。如下图：

      ![hbase link](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/hbase-link.png)

      图 4. master管理页面

      若打开的Master并非当前集群的active master，可点击页面左上角 **Master** 下的 shiva01 的链接，即为 active master的管理页面 。

active master的管理页面 主要包含Region Servers， Backup Masters， Tables， Tasks等相关信息。下面将主要介绍一些常见操作。

#### 2.2.2. Region Servers

hyperbase主页面的第一块区域包含HBase集群的所有Region Server的信息:Base Stats， Memory， Requests， Storefiles， Compactions。

其中，每个ServerName的链接如 RegionServer shiva01,60020,1480995863625 ，对应相应Region Server的管理页面，可查看更详细的信息。

同样可以通过hyperbase角色页面每个Region Server对应的 **Service Link** 都可以直接链接到对应Server的Web管理页面。

#### 2.2.3. Backup Masters

hyperbase主页面的第一块区域包含Backup Masters的信息：ServerName, Port, Start Time等。打开ServerName下的链接可以链接到对应节点的管理页面，如shiva02管理页面。

#### 2.2.4. Tables

包含HBase中所有的表：User Tables，Catalog Tables和Snapshots。这里将主要介绍User Tables和Catalog Tables中的hbase:meta表。

- User Tables

  包含HBase中所有的用户表，并且可以查看表名，Region数目和表的元数据。

  ![hbase tables01](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/hbase-tables01.png)

  并且可以点击![hbase tables05](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/hbase-tables05.png)中的 Details 链接查看所有用户表的不同Region所在的Region Server，以及起止的row key。如下图：

  ![hbase tables8](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/hbase-tables8.png)

- hbase:meta表

  Catalog Tables中包含三个特殊的表：hbase:meta表，hbase:namespace表和hbase:snapshot表。如下图：

  ![hbase tables02](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/hbase-tables02.png)

  打开hbase:meta的链接如下图：

  ![hbase meta](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/hbase-meta.png)

  可以查看到hbase:meta表所在的Region Server是 shiva01:60030 ，以及一些其他相关信息。

  ### 2.3. 和HBase交互

  和HBase交互有以下三种方式：

  - SQL（**推荐方式**）：我们的SQL引擎Inceptor Engine提供了丰富的SQL语法，并对SQL的执行进行了充分的优化，使用SQL和HBase交互在正确性和性能方面都有很好的保证。HBase表相关SQL语句请参考HBase表 SQL使用说明章节，hyperdrive表的相关SQL语句请参考Hyperdrive表 SQL使用说明章节。
  - Shell：HBase提供交互式Shell以及一系列Shell指令用于数据操作，细节请参考HBase Shell命令。
  - Java API：HBase支持Apache HBase原生的API，同时还提供多种自有的API，细节请参考HBase API使用说明。

### 3.1. 建表

语法：create

```ruby
create '<table>', '<column_family>' [, '<column_family>', ...]
```

示例

```ruby
create 'test', 'f1', 'f2'
```

建一张表名为 test ， 含两个列族 f1 和 f2 的表。

|      | HBase中 表 、 列族 和 列限定符名 都要尽量短，以减少读写时的I/O负载。三个数据对象的具体介绍请参考数据模型章节。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | 在建较大表时，需要预分 region 。建议 1 个 region 分 3G 数据。 |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### 3.2. 插入数据

语法：put

```ruby
put '<table>', '<row_key>', '<column_family:column_qualifier>', '<cell_value>' [, <timestamp>]
```

|      | timestamp 是可选项。推荐使用默认时间戳，即数据写入时间。 |
| ---- | -------------------------------------------------------- |
|      |                                                          |

示例

```ruby
put 'test', 'r1', 'f1:c1', 'value1'
put 'test', 'r1', 'f1:c2', 'value2'
put 'test', 'r1', 'f2:c3', 'value3'
put 'test', 'r2', 'f1:c1', 'value4'
put 'test', 'r2', 'f1:c2', 'value5'
put 'test', 'r2', 'f2:c3', 'value6'
```

|      | 更新单元格的值依旧使用 put 命令。 |
| ---- | --------------------------------- |
|      |                                   |

### 3.3. 查询数据

语法：scan

```ruby
scan '<table>'
```

示例

```ruby
scan 'test'

ROW      COLUMN+CELL
 r1      column=f1:c1, timestamp=1480923384536, value=value1
 r1      column=f1:c2, timestamp=1480923384583, value=value2
 r1      column=f2:c3, timestamp=1480923384615, value=value3
 r2      column=f1:c1, timestamp=1480923384660, value=value4
 r2      column=f1:c2, timestamp=1480923384700, value=value5
 r2      column=f2:c3, timestamp=1480923385957, value=value6
```

- ROW: 即该表的row key。
- COLUMN+CELL: 包含列：column，时间戳：timestamp，及值：value。

语法：get

```ruby
get '<table>', '<row_key>' [, {<PROPERTY> => <value>, ...}]
```

示例

```ruby
get 'test', 'r1', {COLUMN => 'f1:c1'}

ROW      COLUMN+CELL
 r1      column=f1:c1, timestamp=1480923384536, value=value1
```

查询指定单元格的值。

### 3.4. 更新数据

示例

```ruby
put 'test', 'r1', 'f1:c1', 'update value'
```

查询单元格更新后的值

```ruby
get 'test', 'r1', {COLUMN => 'f1:c1'}

ROW      COLUMN+CELL
 r1      column=f1:c1, timestamp=1480923386068 value=update value 
```

|      | 更新后 f1:c1 的 timestamp 也更新了。 |
| ---- | ------------------------------------ |
|      |                                      |

### 3.5. 删除数据

#### 3.5.1. 删除单元格

语法：delete

```ruby
delete '<table>', '<row_key>', '<column_family:column_qualifier>'
```

为表删除一个单元格中的数据用 delete 指令，删除时，需要指定表名、row key，列族和列限定符。

示例

```ruby
delete 'test', 'r1', 'f1:c1'
```

再次扫描全表

```ruby
ROW      COLUMN+CELL
 r1      column=f1:c2, timestamp=1480923384583, value=value2
 r1      column=f2:c3, timestamp=1480923384615, value=value3
 r2      column=f1:c1, timestamp=1480923384660, value=value4
 r2      column=f1:c2, timestamp=1480923384700, value=value5
 r2      column=f2:c3, timestamp=1480923385957, value=value6
```

指定单元格数据已删除。

#### 3.5.2. 删除整行数据

将一行中的数据全部删除用deleteall指令，执行时，需要指定表名和row key。

语法：deleteall

```
deleteall '<table_name>', '<row_key>'
```

示例

```ruby
deleteall 'test', 'r1'
```

再次扫描全表

```ruby
ROW      COLUMN+CELL
 r2      column=f1:c1, timestamp=1480923384660, value=value4
 r2      column=f1:c2, timestamp=1480923384700, value=value5
 r2      column=f2:c3, timestamp=1480923385957, value=value6
```

指定行 **r1** 已被删除.

### 3.6. 删除表

在删除一张表之前，要先用 disable 将这张表下线，下线后使用 drop 删除表。

语法：disable

```ruby
disable '<table>'
```

示例

```ruby
disable 'test'
```

语法：drop

```ruby
drop '<table>'
```

示例

```ruby
drop 'test'
```

以上就是通过hbase shell的简单入门，更详细的Shell指令请参考本手册中的HBase Shell命令。

### 4.1. 建表： CREATE

参考《Inceptor使用手册》，连接到Inceptor Engine后，通过SQL语句建一张名为 **bank_info** 的Hyperdrive映射表，用来存储银行用户账号，用户名，密码，电话号码，余额等信息。具体建表语句如下：

```sql
CREATE TABLE bank_info(
    acc_num STRING,
    name STRING,
    password STRING,
    email STRING,
    cellphone STRING,
    balance DOUBLE
)STORED AS HYPERDRIVE; 
```

|      | 建hyperdrive表的简化写法，具体将在建Hyperdrive内表中介绍。 |
| ---- | ---------------------------------------------------------- |
|      |                                                            |

### 4.2. 插入数据： INSERT

建表成功后就可以向 bank_info 表中插入数据了，一条记录插入如下：

```sql
insert into table bank_info values("0001", "Zhang San", "5678", "zs@mail.com", "12345678912", "10000.0");
```

如上，插入用户"Zhang San"的一条完整记录。

### 4.3. 查询数据： SELECT

输入成功插入后即可通过DML语言来查询表中的数据：

```sql
select * from bank_info;
```

查询结果为：

```sql
+----------+------------+-----------+--------------+--------------+----------+
| acc_num  |    name    | password  |    email     |  cellphone   | balance  |
+----------+------------+-----------+--------------+--------------+----------+
| 0001     | Zhang San  | 5678      | zs@mail.com  | 12345678912  | 10000.0  |
+----------+------------+-----------+--------------+--------------+----------+
```

正是我们刚刚插入的用户"Zhang San"的一条完整记录。

### 4.4. 删除表： DROP

最后，当表无用之时，我们可以删除它，语句如下：

```sql
drop table bank_info;
```

简单示例到此结束，下面将详细介绍hyperdrive表和hyperbase表的DDL、DML和DQL。

## 1. Hyperbase表 VS Hyperdrive表

|          |                                                         |                                                  |
| :------- | :------------------------------------------------------ | :----------------------------------------------- |
| 背景     | 基于开源Hive HBaseStorageHandler研发                    | 星环科技自主研发，更贴合Inceptor的设计，性能更优 |
| 优势     | 简单                                                    | 见下文                                           |
| 劣势     | 缺少schema信息，一些SQL功能支持不好，无法做一些性能优化 | 附带schema信息，直接操作底层Hyperbase表较复杂    |
| 适用场景 | Inceptor对接Hyperbase外表                               | Inceptor建内表                                   |

### 5.1. Hyperbase表

基于开源Hive HBaseStorageHandler研发，本身设计比较简单，Hyperbase底层不存储表的schema信息，数据的序列化反序列化依赖于上层Hive。这造成了很多功能和性能上的问题，例如：null值与空字符串的区分，SQL执行计划的优化等。因此在与Inceptor配合使用的情况下不推荐使用Hyperbase表。只有一种场景比较适合：在Hyperbase中已存在一张表，需要通过Inceptor的外表功能进行对接。这样保证了Inceptor对原生Hyperbase表的访问功能。

### 5.2. Hyperdrive表

为了解决Hyperbase表的一些设计缺陷，星环科技自主研发了Hyperdrive表，去除了开源Hive HBaseStorageHandler的设计，并增加了新的功能，使其能更高效地通过Inceptor访问存储在Hyperbase中的数据。

- 在底层Hyperbase中加入表的schema信息，数据存储压缩率更高，序列化/反序列化更高效。数据类型支持BOOLEAN、TINYINT、SMALLINT、INTEGER、BIGINT、DATE、TIMESTAMP、DECIMAL、FLOAT、DOUBLE、STRING、VARCHAR、STRUCT、BINARY等多种类型。
- 对接Inceptor通用的存储访问层Stargate，可以支持完整的Filter转换下推、Global Lookup Join等特性，显著提升SQL性能。
- {elasticsearch}语法对接，不需要再使用以前的那套contains语句了，直接使用现有的条件即可。=、<、>、in、like、between and、not in、!=等对应的语句即可。
- 可以通过指定使用索引的方式使用对应的索引（i.e. hint）
- 支持 NULL值占位符，可正确表达NULL值
- 对接HUE

### 5.3. 其他通用功能

Hyperbase表和Hyperdrive表均具备以下星环科技自研功能：

- 多种索引实现

  支持全局、局部、高维索引和高级过滤器，可用于高并发低延时的OLAP查询。并可以自动利用索引加速数据检索，无需显式的指定索引。

- 超高并发CRUD

  支持超高并发的查询业务，满足上百万用户的高并发查询需求，从百亿历史数据中找到精确结果，并在毫秒级内返回查询结果。同时支持超高并发的插入/修改/删除操作，实现高速的数据入库。

- 全文检索

  结合基于Lucene的分布式全文索引，实现实时的全文搜索。

- 非结构化数据的支持

  支持文档型数据（如JSON/BSON/XML）的存储、索引和搜索，支持BLOB对象数据（图片、音视频、二进制文档等）的存储、检索和自动回收，并提供图形数据库服务。

- 完备的工具库

  自带如SQL bulkload等数据迁移工具和DSTool等一键修复集群的工具，极大降低运维成本。

### 6.1. HBase中的数据对象

HBase支持所有Apache HBase中的所有数据对象，同时还提供HBase独有的对象——Hyperdrive表和索引。这些数据对象如下：

- **命名空间（Namespace）**：对表的逻辑分组，类似于关系型数据库中的Database概念。Namespace可以帮助用户在多租户场景下做到更好的资源和数据隔离。
- **表（Table）**：一张表由多个行组成。HBase中的表分为两种：
  - **HBase表**：HBase表没有数据类型，表中单元格的值都存储为byte[], 在和二维表之间序列化和反序列化时会引起一些数据类型识别上的问题。hbase表相关SQL语句请参考HBase表SQL使用说明。
  - **Hyperdrive表**：Hyperdrive表是星环信息科技（上海）有限公司为SQL对接HBase专门设计的表。具体介绍请参考星环科技的Hyperdrive表SQL使用说明。
- **行（Row）**：每一个行由一个独特的row key和多个列组成。表中各行数据按row key排序，所以row key的设计对HBase的性能影响非常重要。HBase Schema设计中会进行详细介绍。
- **列族（Column Family）**：表中数据以列族分组。物理上，同一列族的数据存储在一起，每个列族都可以有各自的存储属性。例如数据是否缓存在内存中，数据是否压缩等等。列族必须在建表时申明。一个列族下可以有多个列，这些列的列名前缀相同，都为列族名。
- **列限定名（Column Qualifier）**：统一列族下的列由列限定名指定。例如一个名为 info 的列族下可以有 info:name 列、info:age 列和 info:gender 列。这里 name，age 和 gender 即为列限定名。
- **列（Column）**：一个列由一个列族和一个列限定符唯一指定，列名以列族名和列限定名组成，中间以“:”隔开，
- **单元格（Cell）**：一组Row Key、列族和列限定符指定唯一的单元格。单元格中存放一个值和一个timestamp。
- **时间戳（Timestamp）**：单元格的值可以有不同版本，各个版本由时间戳区分。默认时间戳为单元格值被写入时的时间。
- **索引（Index）**：为了弥补Apache HBase本身只能利用原表row key的条件查询的局限，HBase中增加了下面几种索引。通过将查询字段作为索引的row key，可以提速对任何指定字段的查询：
  - **全局索引（Global Index）**：全局索引中的数据做为另一张相对独立的HBase表存在，它的row key即为原表的索引字段，整个过程对用户完全透明的完成。
  - **全文索引（Fulltext Index）**：全文索引利用Elasticsearch作为索引数据的存储，用于加速对指定字段的 **模糊查询**。

### 6.2. HBase中的表

本节通过一个存放用户信息的表bank_info来介绍HBase表中数据的结构。

#### 6.2.1. 概念上的表

下面是一张概念上的bank_info表：

| *Account Number* (row key)  | *Personal* (column family)      | *Contact* (column family)    | *Balance* (column family)        | Timestamp                      |          |      |
| --------------------------- | ------------------------------- | ---------------------------- | -------------------------------- | ------------------------------ | -------- | ---- |
| **name** (column qualifier) | **password** (column qualifier) | **email** (column qualifier) | **cellphone** (column qualifier) | **balance** (column qualifier) |          |      |
| 0001                        |                                 | 5678                         |                                  |                                |          | t16  |
|                             |                                 |                              |                                  | 10000.00                       | t05      |      |
|                             |                                 |                              | 12345678912                      |                                | t04      |      |
|                             |                                 | zs@mail.com                  |                                  |                                | t03      |      |
|                             | 1234                            |                              |                                  |                                | t02      |      |
| Zhang San                   |                                 |                              |                                  |                                | t01      |      |
| 0002                        |                                 |                              |                                  |                                | 56000.00 | t18  |
|                             |                                 | ls@sample.com                |                                  |                                | t17      |      |
|                             |                                 |                              |                                  | 1000.00                        | t10      |      |
|                             |                                 |                              | 13513572468                      |                                | t09      |      |
|                             |                                 | ls@school.edu                |                                  |                                | t08      |      |
|                             | 2468                            |                              |                                  |                                | t07      |      |
| Li Si                       |                                 |                              |                                  |                                | t06      |      |
| 0003                        |                                 |                              |                                  |                                | 500.00   | t15  |
|                             |                                 |                              | 13612345678                      |                                | t14      |      |
|                             |                                 | ww@hmail.com                 |                                  |                                | t13      |      |
|                             | 1357                            |                              |                                  |                                | t12      |      |
| Wang Wu                     |                                 |                              |                                  |                                | t11      |      |

表bank_info的row key为账户号码Account Number；各行以账户号码排序。该表有三个列族：

- Personal：含有两列，列限定名分别为name和password。
- Contact：含有两列，列限定名分别为email和cellphone。
- Balance：一列，列限定名为balance。

从timestamp上看：

- Row key为0001的行有6个版本。
- Row key为0002的行有7个版本。
- Row Key为0003的行有5各版本。

表中无值的单元格在系统中是不存在的，表的“稀疏性”就体现在这里。上面的表以一个二维表形式展现，但是在HBase中，这并不准确。HBase表更接近多维map，由多层 **键值对** 组成：

多维map形式的bank_info表

```
{Table: {RowKey: {ColumnFamily: {ColumnQualifier: {Timestamp: Value}}}}}
```

所以，bank_info表还可以表示如下：

```
bank_info:{
    0001:{
        Personal: {
            name: {t01: Zhang San}
            password: {t16: 5678
                       t02: 1234}
                  }
        Contact: {
            email：{t03: zs@mail.com}
            cellphone: {t04: 12345678912}
                 }
        Balance: {
            balance: {t05: 10000.00}
                 }
          }
    0002: {
        Personal: {
            name: {t06: Li Si}
            password: {t07: 2468}
                  }
        Contact: {
            email: {t08: ls@sample.com
                    t17: ls@school.edu
                   }
            cellphone: {t09: 13513572468}
                 }
        Balance: {
            balance: {t18: 56000.00
                      t10: 1000.00}
                 }
          }
    0003: {
        Personal: {
            name: {t11: Wang Wu}
            password：{t12：1357}
                  }
        Contact: {
            email: {t13: ww@hmail.com}
            cellphone: {t14: 13612345678}
                 }
        Balance: {
            balance: {t15: 500.00}
                 }
          }
         }
```

#### 6.2.2. 物理上的表

在实际的存储上，概念上的银行用户表bank_info中空出的单元格是不存在的，物理上的bank_info如下，bank_info中的数据的存储按Column Family组织

| **Row Key** | **Timestamp** | **Column**              |
| ----------- | ------------- | ----------------------- |
| 0001        | t16           | Personal:password=5678  |
| 0001        | t02           | Personal:password=1234  |
| 0001        | t01           | Personal:name=Zhang San |
| 0002        | t07           | Personal:password=2468  |
| 0002        | t06           | Personal:name=Li Si     |
| 0003        | t12           | Personal:password=1357  |
| 0003        | t11           | Personal:name=Wang Wu   |

| **Row Key** | **Timestamp** | **Column**                    |
| ----------- | ------------- | ----------------------------- |
| 0001        | t04           | Contact:cellphone=12345678912 |
| 0001        | t03           | Contact:email=zs@mail.com     |
| 0002        | t17           | Contact:email=ls@sample.com   |
| 0002        | t09           | Contact:cellphone=13513572468 |
| 0002        | t08           | Contact:email=ls@school.edu   |
| 0003        | t14           | Contact:cellphone=13612345678 |
| 0003        | t13           | Contact:email=ww@hmail.com    |

| **Row Key** | **Timestamp** | **Column**               |
| ----------- | ------------- | ------------------------ |
| 0001        | t05           | Balance:balance=10000.00 |
| 0002        | t18           | Balance:balance=56000.00 |
| 0002        | t10           | Balance:balance=1000.00  |
| 0003        | t15           | Balance:balance=500.00   |

#### 6.2.3. HBase表映射成二维表

通过Inceptor Engine使用SQL对HBase表进行操作时，Inceptor Engine中的表将是HBase表的 **二维映射**，映射表的列将为原表的列（Column Family:Column Qualifier）。映射表的单元格中将只有原表单元格中 **timestamp最新** 的值。将bank_info映射为二维表的逻辑如下图所示：

![mapping hbase table 2](https://www.warpcloud.cn/docs/images/TDH-PLATFORM/6.2.1/030HyperbaseManual/mapping_hbase_table_2.jpg)

图 5. HBase表映射为二维表





### 6.3. HBase中的索引

HBase中增加了下面几种索引：

- **全局索引（Global Index）**：全局索引中的数据做为另一张相对独立的HBase表存在，它的row key即为原表的索引字段，整个过程对用户完全透明的完成。
- **全文索引（Fulltext Index）**：全文索引利用Elasticsearch作为索引数据的存储，用于加速对指定字段的 **模糊查询**。

HBase表中的数据是按照Row Key排列的，这使得对row key的查询非常高效。HBase中索引的设计是为了弥补Apache HBase只能利用原表row key的条件查询的局限。索引的Row Key为查询字段和原表Row Key的拼接，这样，通过合理地设计索引，HBase可以加速对任意字段的查询。

**索引的创建和删除**

创建索引时，索引数据将由原表中的哪个或哪些字段生成。那么索引就可以加速对这个字段或这些字段的查询。一张表可以有多个全局索引，但是只能有一个全文索引。

索引的创建和删除可以通过HBase Shell命令，SQL以及API。

**索引数据的生成**

新建的索引中没有数据，索引中的数据在两种情况下才会生成：

- 原表中有新的数据插入时，HBase会为新插入的数据自动生成索引。
- 对索引执行 **重建（rebuild）** 操作，HBase会为表中已有的所有数据生成索引。索引的重建需要通过HBase Shell命令或者API执行。

**索引的使用**

在使用SQL查询时，您可以在SQL中指定使用哪个索引来加速查询，相关的SQL语法请参考《Hyperdrive使用手册》。



## 1. HBase Schema设计

------

### 7.1. 模式(Schema) 创建

可以使用HBase shell或Java API的HBaseAdmin来创建和编辑HBase Schema。

当修改列族（或其他表元属性）时，建议先将这张表下线（disable）。如：

```java
Configuration config = HBaseConfiguration.create();
HBaseAdmin admin = new HBaseAdmin(conf);
String table = "myTable";

admin.disableTable(table);        // 将表下线

HColumnDescriptor cf1 = ...;
admin.addColumn(table, cf1);      // 增加新的列族
HColumnDescriptor cf2 = ...;
admin.modifyColumn(table, cf2);   // 修改列族

admin.enableTable(table);
```

#### 7.1.1. 模式更新

当表或列族改变时(如压缩格式，编码方式, block大小), 这些改变将会在下次major compaction及StoreFiles重写时起作用。

### 7.1. 模式(Schema) 创建

可以使用HBase shell或Java API的HBaseAdmin来创建和编辑HBase Schema。

当修改列族（或其他表元属性）时，建议先将这张表下线（disable）。如：

```java
Configuration config = HBaseConfiguration.create();
HBaseAdmin admin = new HBaseAdmin(conf);
String table = "myTable";

admin.disableTable(table);        // 将表下线

HColumnDescriptor cf1 = ...;
admin.addColumn(table, cf1);      // 增加新的列族
HColumnDescriptor cf2 = ...;
admin.modifyColumn(table, cf2);   // 修改列族

admin.enableTable(table);
```

#### 7.1.1. 模式更新

当表或列族改变时(如压缩格式，编码方式, block大小), 这些改变将会在下次major compaction及StoreFiles重写时起作用。

### 7.2. 表模式经验法则

不同的数据集可能有着不同的访问模式和服务级期望，下面这些经验法则只是一些概述：

- region规模大小在10到50GB之间；

- 单元的大小不要超过10MB，如果使用 Object Store ，可放宽到50MB；不然，可以考虑将单元数据存在HDFS中，或者在HBase中存一个指向这些数据的指针；

  |      | 更多Object Store使用方法详见Object Store使用方法 |
  | ---- | ------------------------------------------------ |
  |      |                                                  |

- 一个典型的模式每个表中含有1～3个列族。HBase表不应该设计成类似RDBM的样式；

- 对于只有1～2个列族的表，50到100个region是一个比较合适的数量。需要提醒的是，每个region都是列族的一个连续段；

- 列族的名字越短越好，因为对每个值（忽略前缀编码， prefix encoding ），列族名都会存一次。它们不应当像典型RDNMS一样自记录（ self-documenting ） 和描述。

- 如果在基于时间的机器上存储数据或日志信息，行键（Row Key）是由设备ID或服务器ID加上时间得到的，那最后能得到这样的模式：除了某个特定的时间段，旧的数据region没有额外的写。在这种情况下，得到的是少量的活跃region和大量的没有新写入的旧region。这时由于资源消耗仅来自于活跃的region，大量的region能被容纳接受；

### 7.3. 列族的数量

现在HBase并不能很好的处理两个或者三个以上的列族，所以尽量让列族数量少一些。

目前， flush 和 compaction 操作是针对一个region。所以当一个列族操作大量数据的时候会引发一个flush。那些邻近的列族也有进行flush操作，尽管它们没有操作多少数据。

compaction操作现在是根据一个列族下的全部文件的数量触发的，而不是根据文件大小触发的。

当很多的列族在flush和compaction时,会造成很多没用的I/O负载(要想解决这个问题，需要将flush和compaction操作只针对一个列族) 。

尽量在模式中只针对一个列族操作。将使用率相近的列归为一列族，这样每次访问时就只用访问一个列族，提高效率。

#### 7.3.1. 列族的基数（Cardinality）

如果一个表存在多个列族，要注意列族之间基数(如行数)相差不要太大。 例如列族A有100万行，列族B有10亿行，按照行键切分后，列族A可能被分散到很多很多region(及RegionServer)，这导致扫描列族A十分低效。



### 7.3. 列族的数量

现在HBase并不能很好的处理两个或者三个以上的列族，所以尽量让列族数量少一些。

目前， flush 和 compaction 操作是针对一个region。所以当一个列族操作大量数据的时候会引发一个flush。那些邻近的列族也有进行flush操作，尽管它们没有操作多少数据。

compaction操作现在是根据一个列族下的全部文件的数量触发的，而不是根据文件大小触发的。

当很多的列族在flush和compaction时,会造成很多没用的I/O负载(要想解决这个问题，需要将flush和compaction操作只针对一个列族) 。

尽量在模式中只针对一个列族操作。将使用率相近的列归为一列族，这样每次访问时就只用访问一个列族，提高效率。

#### 7.3.1. 列族的基数（Cardinality）

如果一个表存在多个列族，要注意列族之间基数(如行数)相差不要太大。 例如列族A有100万行，列族B有10亿行，按照行键切分后，列族A可能被分散到很多很多region(及RegionServer)，这导致扫描列族A十分低效。

### 7.4. 行键(RowKey)设计

HBase的RowKey设计会在很大程度上影响HBase的性能，下面将介绍RowKey设计的几种常见问题及相应的解决方法。

#### 7.4.1. Hotspotting

HBase的行由行键按字典顺序排序，这样的设计优化了扫描，允许存储相关的行或者那些将被一起读的邻近的行。然而，设计不好的行键是导致 hotspotting 的常见原因。当大量的客户端流量（ traffic ）被定向在集群上的一个或几个节点时，就会发生 hotspotting。这些流量可能代表着读、写或其他操作。流量超过了承载该region的单个机器所能负荷的量，这就会导致性能下降并有可能造成region的不可用。在同一 RegionServer 上的其他region也可能会受到其不良影响，因为主机无法提供服务所请求的负载。设计使集群能被充分均匀地使用的数据访问模式是至关重要的。

为了防止在写操作时出现 hotspotting ，设计行键时应该使得数据尽量同时往多个region上写，而避免只向一个region写，除非那些行真的有必要写在一个region里。

下面介绍了集中常用的避免 hotspotting 的技巧，它们各有优劣：

##### 7.4.1.1. Salting

Salting 从某种程度上看与加密无关，它指的是将随机数放在行键的起始处。进一步说，salting给每一行键随机指定了一个前缀来让它与其他行键有着不同的排序。所有可能前缀的数量对应于要分散数据的region的数量。如果有几个“hot”的行键模式，而这些模式在其他更均匀分布的行里反复出现，salting就能到帮助。下面的例子说明了salting能在多个RegionServer间分散负载，同时也说明了它在读操作时候的负面影响。

假设行键的列表如下，表按照每个字母对应一个region来分割。前缀‘a’是一个region，‘b’就是另一个region。在这张表中，所有以‘f’开头的行都属于同一个region。这个例子关注的行和键如下：

```
foo0001

foo0002

foo0003

foo0004
```

现在，假设想将它们分散到不同的region上，就需要用到四种不同的 salts ：a，b，c，d。在这种情况下，每种字母前缀都对应着不同的一个region。用上这些salts后，便有了下面这样的行键。由于现在想把它们分到四个独立的区域，理论上吞吐量会是之前写到同一region的情况的吞吐量的四倍。

```
a-foo0003

b-foo0001

c-foo0004

d-foo0002
```

如果想新增一行，新增的一行会被随机指定四个可能的salt值中的一个，并放在某条已存在的行的旁边。

```
a-foo0003

b-foo0001

c-foo0003

c-foo0004

d-foo0002
```

由于前缀的指派是随机的，因而如果想要按照字典顺序找到这些行，则需要做更多的工作。从这个角度上看，salting增加了写操作的吞吐量，却也增大了读操作的开销。

##### 7.4.1.2. Hashing

可用一个单向的 hash 散列来取代随机指派前缀。这样能使一个给定的行在“salted”时有相同的前缀，从某种程度上说，这在分散了RegionServer间的负载的同时，也允许在读操作时能够预测。确定性hash（ deterministic hash ）能让客户端重建完整的行键，以及像正常的一样用Get操作重新获得想要的行。

考虑和上述salting一样的情景，现在可以用单向hash来得到行键foo0003，并可预测得‘a’这个前缀。然后为了重新获得这一行，需要先知道它的键。可以进一步优化这一方法，如使得将特定的键对总是在相同的region。

##### 7.4.1.3. 反转键（Reversing the Key）

第三种预防hotspotting的方法是反转一段固定长度或者可数的键，来让最常改变的部分（最低显著位， the least significant digit ）在第一位，这样有效地打乱了行键，但是却牺牲了行排序的属性。

#### 7.4.2. 单调递增行键/时序数据

在Tom White的 Hadoop:The Definitive Guide 一书中，有个章节描述了一个值得注意的问题：在一个集群中，一个导入数据的进程锁住不动，所有的client都在等待一个region(因而也就是一个单个节点)，过了一会后，变成了下一个region…如果使用了单调递增或者时序的key便会造成这样的问题。详情可以参见IKai画的漫画 monotonically increasing values are bad。使用了顺序的key会将本没有顺序的数据变得有顺序，把负载压在一台机器上。所以要尽量避免时间戳或者序列(e.g. 1, 2, 3)这样的行键。

如果需要导入时间顺序的文件(如log)到HBase中，可以学习OpenTSDB的做法。它有一个页面来描述它的HBase模式。OpenTSDB的Key的格式是[metric_type][event_timestamp]，乍一看，这似乎违背了不能将timestamp做key的建议，但是它并没有将timestamp作为key的一个关键位置，有成百上千的metric_type就足够将压力分散到各个region了。因此，尽管有着连续的数据输入流，Put操作依旧能被分散在表中的各个region中。

#### 7.4.3. 简化行和列

在HBase中，值是作为一个单元(Cell)保存在系统的中的，要定位一个单元，需要行，列名和时间戳。通常情况下，如果行和列的名字要是太大(甚至比value的大小还要大)的话，可能会遇到一些有趣的情况。在HBase的存储文件（ storefiles ）中，有一个索引用来方便值的随机访问，但是访问一个单元的坐标要是太大的话，会占用很大的内存，这个索引会被用尽。要想解决这个问题，可以设置一个更大的块大小，也可以使用更小的行和列名 。压缩也能得到更大指数。

大部分时候，细微的低效不会影响很大。但不幸的是，在这里却不能忽略。无论是列族、属性和行键都会在数据中重复上亿次。

##### 7.4.3.1. 列族

尽量使列族名小，最好一个字符。(如 "d" 表示 data/default).

##### 7.4.3.2. 属性

详细属性名 (如, "myVeryImportantAttribute") 易读，最好还是用短属性名 (e.g., "via") 保存到HBase.

##### 7.4.3.3. 行键长度

让行键短到可读即可，这样对获取数据有帮助(e.g., Get vs. Scan)。短键对访问数据无用，并不比长键对get/scan更好。设计行键需要权衡。

##### 7.4.3.4. 字节模式

long类型有8字节。8字节内可以保存无符号数字到18,446,744,073,709,551,615。 如果用字符串保存——假设一个字节一个字符——需要将近3倍的字节数。

下面是示例代码，可以自己运行一下。

```java
// long
//
long l = 1234567890L;
byte[] lb = Bytes.toBytes(l);
System.out.println("long bytes length: " + lb.length);   // returns 8

String s = "" + l;
byte[] sb = Bytes.toBytes(s);
System.out.println("long as string length: " + sb.length);    // returns 10
```

不幸的是，用二进制表示会使数据在代码之外难以阅读。下例便是当需要增加一个值时会看到的shell：

```java
hbase(main):001:0> incr 't', 'r', 'f:q', 1
COUNTER VALUE = 1

hbase(main):002:0> get 't', 'r'
COLUMN                                        CELL
 f:q                                          timestamp=1369163040570, value=\x00\x00\x00\x00\x00\x00\x00\x01
1 row(s) in 0.0310 seconds
```

这个shell尽力在打印一个字符串，但在这种情况下，它决定只将进制打印出来。当在region名内行键会发生相同的情况。如果知道储存的是什么，那自是没问题，但当任意数据都可能被放到相同单元的时候，这将会变得难以阅读。这是最需要权衡之处。

#### 7.4.4. 倒序时间戳

一个数据库处理的通常问题是找到最近版本的值。采用倒序时间戳作为键的一部分可以对此特定情况有很大帮助。该技术包含追加( Long.MAX_VALUE - timestamp ) 到key的后面，如 [key][reverse_timestamp] 。

表内[key]的最近的值可以用[key]进行Scan，找到并获取第一个记录。由于HBase行键是排序的，该键排在任何比它老的行键的前面，所以是第一个。

该技术可以用于代替版本数，其目的是保存所有版本到“永远”(或一段很长时间) 。同时，采用同样的Scan技术，可以很快获取其他版本。

#### 7.4.5. 行键和列族

行键在列族范围内。所以同样的行键可以在同一个表的每个列族中存在而不会冲突。

#### 7.4.6. 行键不可改

行键不能改变。唯一可以“改变”的方式是删除然后再插入。这是一个常问问题，所以要注意开始就要让行键正确(且/或在插入很多数据之前)。

#### 7.4.7. 行键和region split的关系

如果已经 pre-split （预裂）了表，接下来关键要了解行键是如何在region边界分布的。为了说明为什么这很重要，可考虑用可显示的16位字符作为键的关键位置（e.g., "0000000000000000" to "ffffffffffffffff"）这个例子。通过 Bytes.split来分割键的范围（这是当用 Admin.createTable(byte[] startKey, byte[] endKey, numRegions) 创建region时的一种拆分手段），这样会分得10个region。

48 48 48 48 48 48 48 48 48 48 48 48 48 48 48 48 // 0

54 -10 -10 -10 -10 -10 -10 -10 -10 -10 -10 -10 -10 -10 -10 -10 // 6

61 -67 -67 -67 -67 -67 -67 -67 -67 -67 -67 -67 -67 -67 -67 -68 // =

68 -124 -124 -124 -124 -124 -124 -124 -124 -124 -124 -124 -124 -124 -124 -126 // D

75 75 75 75 75 75 75 75 75 75 75 75 75 75 75 72 // K

82 18 18 18 18 18 18 18 18 18 18 18 18 18 18 14 // R

88 -40 -40 -40 -40 -40 -40 -40 -40 -40 -40 -40 -40 -40 -40 -44 // X

95 -97 -97 -97 -97 -97 -97 -97 -97 -97 -97 -97 -97 -97 -97 -102 // _

102 102 102 102 102 102 102 102 102 102 102 102 102 102 102 102 // f

但问题在于，数据将会堆放在前两个region以及最后一个region，这样就会导致某几个region由于数据分布不均匀而特别忙。为了理解其中缘由，需要考虑ASCII Table的结构。根据ASCII表，“0”是第48号，“f”是102号；但58到96号是个巨大的间隙，考虑到在这里仅[0-9]和[a-f]这些值是有意义的，因而这个区间里的值不会出现在键空间（ keyspace ），进而中间区域的region将永远不会用到。为了pre-split这个例子中的键空间，需要自定义拆分。

教程1.预裂表（ pre-splitting tables ） 是个很好的实践，但pre-split时要注意使得所有的region都能在键空间中找到对应。尽管例子中解决的问题是关于16位键的键空间，但其他任何空间也是同样的道理。

教程2.16位键（通常用到可显示的数据中）尽管通常不可取，但只要所有的region都能在键空间找到对应，它依旧能和预裂表配合使用。

下例说明了如何为16位键预创建合适的拆分:

```java
public static boolean createTable(Admin admin, HTableDescriptor table, byte[][] splits)
throws IOException {
  try {
    admin.createTable( table, splits );
    return true;
  } catch (TableExistsException e) {
    logger.info("table " + table.getNameAsString() + " already exists");
    // the table already exists...
    return false;
  }
}
public static byte[][] getHexSplits(String startKey, String endKey, int numRegions) {
  byte[][] splits = new byte[numRegions-1][];
  BigInteger lowestKey = new BigInteger(startKey, 16);
  BigInteger highestKey = new BigInteger(endKey, 16);
  BigInteger range = highestKey.subtract(lowestKey);
  BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));
  lowestKey = lowestKey.add(regionIncrement);
  for(int i=0; i < numRegions-1;i++) {
    BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
    byte[] b = String.format("%016x", key).getBytes();
    splits[i] = b;
  }
  return splits;
}
```



### 7.5. 版本数量

#### 7.5.1. 最大版本数

行的版本的数量是HColumnDescriptor设置的，每个列族可以单独设置，默认是3。这个设置是很重要的，因为HBase是不会去覆盖一个值的，它只会在后面在追加写，用时间戳来区分、过早的版本会在执行major compaction时删除，这些在HBase数据模型有描述。这个版本的值可以根据具体的应用增加减少。

不推荐将版本最大值设到一个很高的水平 (如, 成百或更多)，除非老数据对你很重要。因为这会导致存储文件变得极大。

#### 7.5.2. 最小版本数

和行的最大版本数一样，最小版本数也是通过HColumnDescriptor 在每个列族中设置的。最小版本数缺省值是0，表示该特性禁用。 最小版本数参数和存活时间一起使用，允许配置如“保存最后T秒有价值数据，最多N个版本，但最少约M个版本”(M是最小版本数，M<N)。 该参数仅在存活时间对列族启用，且必须小于行版本数。