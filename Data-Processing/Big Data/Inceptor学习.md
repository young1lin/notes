### 1. 按Inceptor的所有权分为外表和托管表（managed table）

- **托管表**

  **CREATE TABLE** 默认创建托管表。Inceptor对托管表有所有权——用 **DROP** 删除托管表时，Inceptor会将表中数据全部删除。

- **外表**

  外表用 **CREATE EXTERNAL TABLE** 创建，外表中的数据可以保存在HDFS的一个指定路径上（和 **LOCATION <hdfs_path>** 合用）。Inceptor对外表没有所有权。用 **DROP** 删除外部表时，Inceptor删除表在metastore中的元数据而不删除表中数据，也就是说 **DROP** 仅仅解除Inceptor对外表操作的权利。

#### 2.1.2.1.2. 按表的存储格式分类

按存储格式可以将表分为TEXT表、ORC表、CSV表和Holodesk表。

- **TEXT表**

  TEXT表即文本格式的表，是Inceptor默认的表格式。在数据量大的情况下，TEXT表的统计和查询性能都比较低；TEXT表也不支持事务处理，所以通常用于将文本文件中的原始数据导入Inceptor中。针对不同的使用场景，用户可以将其中的数据放入ORC表或Holodesk表中。

  Inceptor提供两种方式将文本文件中的数据导入TEXT表中：

  1. 建外部TEXT表，让该表指向HDFS上的一个目录，Inceptor会将目录下文件中的数据都导入该表。我们推荐使用这个方式导数据。

  2. 建TEXT表（外表内表皆可）后将本地或者HDFS上的一个文件或者一个目录下的数据 **LOAD** 进该表。这种方式在安全模式下需要多重认证设置，极易出错，我们 **不推荐** 使用这个方式导数据。

     关于TEXT表的更多内容和具体操作，请参考inceptor_sql手册/text表/text表。

- **CSV表**

  CSV表的数据来源为CSV格式（Comma-Separated Values）的文件。文件以纯文本形式存储表格数据（数字和文本），CSV文件由任意数目的记录组成，记录间以某种换行符分隔；每条记录由字段组成，字段间的分隔符是其它字符或字符串，最常见的是逗号或制表符。通常，所有记录都有完全相同的字段序列。和TEXT表相似，CSV表常用于向Inceptor中导入原始数据，然后针对不同场景，用户可以将其中的数据放入ORC表或Holodesk表中。

  关于CSV表的更多内容和具体操作，请参考inceptor_sql手册/csv表/csv表。

- **ORC表**

  ORC表即ORC格式的表。在Inceptor中，ORC表还分为ORC事务表和非事务表。

  1. ORC事务表支持事务处理和更多增删改语法（**INSERT VALUES/UPDATE/DELETE/MERGE**），所以如果您需要对表进行事务处理，应该选择使用ORC事务表。

  2. ORC非事务表则主要用来做统计分析。

     ORC表以及事务处理相关细节请参考inceptor_sql手册/orc表/orc表和inceptor_sql手册/事务处理语言（TCL）/事务处理语言（TCL）

- **Holodesk表**

  Holodesk表存储在内存或者SSD中（可以根据您的需要设置），同时，星环科技为其提供了一系列优化工具，使得在Holodesk表上进行大批量复杂查询能达到极高的性能。所以，<u>如果您的数据量特别大，查询非常复杂，您应该选择使用Holodesk表。</u>

  关于Holodesk表的更多内容和具体操作，请参考“Holodesk表”章节。

#### 2.1.2.1.3. 按表是否分区分类

按表是否分区可以将表为两类：分区表和非分区表。

- **分区表**

  如果在建表时使用了　**PARTITIONED BY**，表即为分区表。分区表下的数据按分区键的值（或值的范围）放在HDFS下的不同目录中，可以有效减少查询时扫描的数据量，提升查询效率。

  关于分区表的更多内容和具体操作，请参考“分区表”章节。

- **非分区表**

  非分区表即除分区表之外的表

