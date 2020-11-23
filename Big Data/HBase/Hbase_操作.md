1、Hbase的应用场景

```
1、数据量规模非常庞大
一般而言，单表数据量如果只有百万级或者更少，不是非常建议使用HBase而应该考虑关系型数据库是否能够满足需求；我们一般放在orc表或者orc事务表里。单表数据量超过千万或者十亿百亿的时候，并且伴有较高并发，可以考虑使用HBase。这主要是充分利用分布式存储系统的优势，如果数据量比较小，单个节点就能有效存储的话则其他节点的资源就会存在浪费。

2、要求是实时的点查询
HBase是一个Key-Value数据库，默认对Rowkey即行键做了索引优化，所以即使数据量非常庞大，根据行键的查询效率依然会很高，这使得HBase非常适合根据行键做单条记录的查询。根据Rowkey的设计，允许做一部分做范围查询。

3、数据分析需求并不多
数据分析是HBase的弱项，所以如果主要的业务需求就是为了做数据分析，比如做报表，那么不建议直接使用HBase。

4、能够容忍Hbase的短板
如果业务场景是需要事务支持、复杂的关联查询等，不建议使用HBase。
```

2、Hbase的特性

```
强一致性读写：HBase并不是最终一致性，而是强一致性的系统，这使得HBase非常适合做高速的聚合操作。

自动sharding：HBase的表在水平方向上以region为单位分布式存储在各个节点上，当region达到一定大小时，就会自动split重新分布数据。

自动故障转移：这是HBase高可用的体现，当某一个节点故障下线时，节点上的region也会下线并会自动转移到状态良好的节点上线。

面向列的存储：HBase是面向列的存储系统，相同特征（列族相同）的数据会被尽量放到一起，这有利于提高数据读取的效率。

无缝结合Hadoop：HBase被定义为Hadoop database，就是基于HDFS做的数据存储，同时原生的支持MapReduce计算引擎。

非常友好的API操作：HBase提供了简单易用的Java API，并且提供了Thrift与REST的API供非Java环境使用。

Block Cache与Bloom Filter：查询优化方面HBase支持Block Cache与Bloom Filter，使得HBase能够对海量数据做高效查询。
```

3、hbase支持的数据类型

```sql
hbase表支持的数据类型包括:BOOLEAN, TINYINT, SMALLINT, INT, BIGINT, DATE, TIMESTAMP, FLOAT, DOUBLE, STRING, VARCHAR, DECIMAL 和 STRUCT。所有数据类型映射在实际HBase表中的类型都是byte[]
```



4、hbase建表语句

```sql
创建内表
CREATE TABLE <tableName> (
    <key> <data_type>,
    <column> <data_type>,
    <column> <data_type>,
    ...
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'  
[WITH SERDEPROPERTIES('hbase.columns.mapping'=':key,<f1:c1>,<f1:c2>,...')] 
[TBLPROPERTIES ("hbase.table.name" = "<hbase_table>")];

例：
CREATE TABLE crm_zjnx.test (
    key string,
    id int,
    name string
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'  
WITH SERDEPROPERTIES('hbase.columns.mapping'=':key,f:a,f:b')
TBLPROPERTIES ('hbase.table.name' = 'test_20200801',
              'hbase.table.splitkey' = '001,005',
               'COMPRESSION' = 'SNAPPY'
              );
注意 建内表时hbase表在hbase shell中不能存在，这是会自动在hbbase shell中创建此表。


创建外表
CREATE EXTERNAL TABLE <tableName> (
    <key> <data_type>,
    <column> <data_type>,
    <column> <data_type>,
    ...
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES('hbase.columns.mapping'=':key,<f:q1>,<f:q2>,...') 
TBLPROPERTIES ("hbase.table.name" = "<hbase_table>");

例：
CREATE TABLE crm_zjnx.test (
    key string,
    id int,
    name string
)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'  
WITH SERDEPROPERTIES('hbase.columns.mapping'=':key,f:id,f:name')
TBLPROPERTIES ('hbase.table.name' = 'test_20200801');

注意 hbase 映射列名为hbase真实存在的列名，表名为hbase真实存在的。

```

5、查看hbase表的*元数据信息*

```sql
describe formatted hbase_inner_table;
```

![1597286188605](C:\Users\123\AppData\Roaming\Typora\typora-user-images\1597286188605.png)

6、给内标新增列

```sql
ALTER TABLE <tableName> ADD COLUMNS (<column> <datat_type>, <column> <data_type>, ...);

ALTER TABLE hbase_inner_table ADD COLUMNS (bl boolean);

一般我们是删表重建，因为每个表最后一个字段都是last_etl_acg_dt,表示数据入库时间。
last_etl_acg_dt放在表中最后一个字段，是我们默认位置。
```

7、delete、truncate、drop

```sql
delete from table_name where 1=1;
truncate table table_name;
drop table table_name;
```

8、hbase SQL 创建索引

```sql
创建索引有两种方法，一种直接使用sql在inceptor中创建，另一种是在，hbase shell中创建。我们一般使用第二种创建方法。
索引分为：全局索引、全文索引。

创建索引：CREATE INDEX
创建全局索引: CREATE GLOBAL INDEX
创建全文索引: CREATE FULLTEXT INDEX

删除索引：DROP INDEX
删除全局索引: DROP GLOBAL INDEX
删除全文索引: DROP FULLTEXT INDEX

但是，目前HBase不支持使用SQL生成索引，您可以从HBase Shell中执行 rebuild 指令来生成索引。

注意：看到表中的索引名字已经创建成功，并不代表索引创建成功了。
成功的标志：同一查询条件，在没有索引的表中查询结果和有索引的表中查询结果一样且查询速度为毫秒级别。

索引字段一般为查询条件。


```

全局索引

```
HBase表建全局索引的语法
CREATE GLOBAL INDEX <index_name> ON <tableName> (
  <column1> <SEGMENT LENGTH length1>|<<(length1)>  
  [,<column2> <SEGMENT LENGTH length2>|<(length2)>,...]  
);
column1 ：指根据哪个列建全局索引，可以有多个列，但不可包含首列（因该列映射为RowKey）。
SEGMENT LENGTH length1 ：每个列在索引词条中所占字段的长度，可简写为 (length1) 。注意，简写必须有 () 。
例：
--根据列ch创建一个名为ch_global的全局索引，并指定该索引字段的长度为10.
CREATE GLOBAL INDEX ch_global ON hbase_inner_table(ch(10));

--应该如何设置 SEGMENT LENGTH？
一个字段的 SEGMENT LENGTH 的设定最好大于等于字段的实际长度，这样设置可以最优化查询性能。 SEGMENT LENGTH 小于字段的实际长度可能会影响查询性能，但是不会影响查询的正确性。
例如，如果我们用身份证号id建索引，那么id的 SEGMENT LENGTH 可以设置为18。

索引创建后可通过 describe formatted hbase_inner_table; 查看表的索引信息，确认是否创建成功。如下图：
```

![1597287769005](C:\Users\123\AppData\Roaming\Typora\typora-user-images\1597287769005.png)

全文索引

```sql
为HBase表建全文索引的语法
CREATE FULLTEXT INDEX ON <tableName> ( 
  <column1> [DOCVALUES <TRUE|FALSE>] 
  [,<column2> [DOCVALUES <TRUE|FALSE>], ...] 
)SHARD NUM <n>; 
一张表只能有 一个全文索引 ，所以建全文索引无须指定索引名。索引名默认为：elasticsearch_Hbase的名字
任意 column1 的数据类型不可以是：decimal，date，timestamp，会报错。且同样不可包含首列。
DOCVALUES 是一个优化查询的开关，默认是打开（TRUE）的。
SHARD NUM 指定全文索引的分片数。
全文索引的分片数是不可变的，只能在创建时指定，这里需要用户预估计索引数据的量，一个SHARD上的数据不要超过25GB。超过25GB可能会有比较严重的性能问题。DOCVALUES默认是TRUE，请使用默认值。

例
--为内表hbase_inner_table根据列en、cn来创建全文索引。
CREATE FULLTEXT INDEX ON hbase_inner_table(cn,eh) SHARD NUM 1;
同样，全文索引创建后 describe formatted hbase_inner_table; 查看。如下图：

```

![1597288404357](C:\Users\123\AppData\Roaming\Typora\typora-user-images\1597288404357.png)

####  删除索引

```sql
--删除全局索引的语法,因一张表可以有多个全局索引，所以需指定索引名：index_name。
DROP INDEX [IF EXISTS] <index_name> ON <tableName>;

--删除全文索引的语法,因每张表只有一个全文索引，所以只需指定表名。
DROP FULLTEXT INDEX [IF EXISTS] ON <tableName>;

```

生成索引数据

```
生成索引数据有两种办法：
1、选创建表、再创建索引、再插入数据。
2、在hbase shell种使用 rebuild 指令来生成索引数据。
```

9、hbase shell 创建索引

```shell
创建、生成和删除全局索引：add_index, rebuild_global_index/rebuild_global_index_with_range, delete_global_index

生成全文索引：rebuild_fulltext_index/rebuild_fulltext_index_with_range
```

##### 创建全局索引

```shell
#语法
add_index '<table>', '<global_index_name>', '<index_definition>'
#该语句为指定表 <table> 添加全局索引，生成一张索引表，索引表的表名将是 table_global_index_name（注意区分索引名和索引表名）。<index_definition> 为索引的定义，用于指示HBase如何建索引。
#索引的定义形式如下：
COMBINE_INDEX|INDEXED= <cf>:<cq>:<n>[|<cf>:<cq>:<n>|...]|rowKey:rowKey:<m>,[UPDATE=true]
#n、m的长度为实际最大长度。n、m值过小，会导致数据查询结果为空。因为全局索引为hbase表的二级索引，相当于又创建了一个hbase表，这个hbase表的rowkey则是有，原来的索引字段和原来hbase的rowkey拼接而来的。
例如：
原表数据
	rowkey：00000
	字段一：11111
	字段二：22222
全局索引
	n=5,m=5
	rowkey：11111&22222&00000
	n=3,m=3
	rowkey：111&222&000112200
以111&222&000112200去查11111&22222&00000 结果为空
	
#解释
1、<cf>:<cq>:<n> 指定生成索引所用的列以及索引长度：<cf> 是该列所在的列族，<cq> 是该列的列限定符，<n> 是索引字段在索引词条中的长度。例如 f1:q1:n1 为表中每一行记录用 f1:q1 列的数据生成的一段长度为n1字段放在索引词条中。如果指定的 f1:q1 列的值长度不足n1，HBase将用0表示空格将长度补足。如果长度超过n1，超过的部分将在索引词条的末尾添上。
2、如果需要使用多列生成索引，可以在定义中添加多组 <cf>:<cq>:<n>，组之间用 | 隔开。
3、每个生成的索引词条中都会包含一段原表Row Key生成的长度为m的字段，由 rowKey:rowKey:<m> 指定。
4、[UPDATE=true] 为可选项，设为true代表索引词条会随着对应原表数据的更新自动更新，设为false则不会。
索引词条（row_key） = 索引字段 + 原表rowkey

#创建全局索引创建一张空的索引表，没有索引词条，索引词条在两种情况下生成：
1、当原表中有新的数据插入时，HBase会 自动 为新增数据生成索引词条，写入索引表中。
2、用户执行 rebuild_global_index/rebuild_global_index_with_range 让HBase为原表中数据生成索引词条，写入索引表中。

#HBase中可以直接为全表生成全局索引： rebuild_global_index ；也可以按表的Row Key分段生成索引： rebuild_global_index_with_range 。但是，在数据量较大时， rebuild_global_index_with_range 生成索引的性能更优。

#为全表生成全局索引
rebuild_global_index '<table>','<index_name>'

#分段生成全局索引(根据rowkey生成)
在HBase表非常大时，可以分段生成全局索引，指令为 rebuild_global_index_with_range，为指定的Row Key区间中的记录生成索引。
语法
rebuild_global_index_with_range '<table>','<index_name>', '<start_key>', '<end_key>','<encoder>'
start_key 为区间的下限，end_key 为区间的上限，该指令为[start_key, end_key) 左闭右开 区间生成索引；Encoder为编码方式，表示'<start_key>'和 '<end_key>'的输入类型，目前支持’string'、'hex’和’binary’三种，默认使用’string’来编码。start_key 为区间的下限，end_key 为区间的上限，该指令为[start_key, end_key) 左闭右开 区间生成索引；Encoder为编码方式，表示'<start_key>'和 '<end_key>'的输入类型，目前支持’string'、'hex’和’binary’三种，默认使用’string’来编码。

#分段生成全局索引(根据时间戳生成)
rebuild_global_index_with_timerange '<table>','<index_name>', '<start_timestamp000>', '<end_timestamp000>'

#删除全局索引
delete_global_index '<table>', '<index_name>'

```

#### 创建全文索引

```shell
#创建全文索引
alterUseJson 'ssss.json'

#为全表生成全文索引，因为一张表只能有一个全文索引，生成时只需指定表名即可。
rebuild_fulltext_index '<table>'

#分段生成全文索引，（根据rowkey生成）
rebuild_fulltext_index_with_range '<table>', '<start_key>', '<end_key>'

start_key 为区间的下限，end_key 为区间的上限，该指令为[start_key, end_key) 左闭右开 区间生成索引；Encoder为编码方式，表示'<start_key>'和 '<end_key>'的输入类型，目前支持’string'、'hex' 和’binary’三种，默认使用’string’来编码。

#分段生成全局索引(根据时间戳生成)
rebuild_fulltext_index_with_timerange '<table>', '<start_timestamp000>', '<end_timestamp000>'

#删除全文索引
delete_fulltext_index 'table'




```



### 10、Base SQL DML

```sql
--插入数据：INSERT
--更新数据：UPDATE
--删除数据：DELETE

--hbase SQL支持向HBase表中单条插入数据或者批量插入查询结果。
--单条插入的语法
INSERT INTO TABLE <tableName> [(<column1>, <column2>, ...)]
VALUES (<value1>, <value2>, ...);

--批量插入语法
BATCHINSERT INTO <tableName> [(<column1>, <column2>, ...)] BATCHVALUES(VALUES(<value1>, <value2>, ...),VALUES(<value1>, <value2>, ...),...);

--更新表中数据的语法
UPDATE <tableName> SET <column> = <value> WHERE <filter_conditions>;

--删除记录语法：
DELETE FROM <tableName> WHERE <filter_conditions>;
```

### 11、HBase SQL DQL

#### SELECT

```sql
--HBase中的索引可以让带 WHERE 过滤的查询更加高效。HBase SQL中提供了一系列独有的利用索引的语法，可以让用户在使用SQL和HBase交互时能够利用全局索引和全文索引进行查询。
--除此之外，HBase SQL中的查询语法和Inceptor SQL相同，请参考《Transwarp Data Hub Inceptor 使用手册》。
--Inceptor Engine的优化器有一套自动选择索引的机制，当 SELECT 查询语句未明确使用哪个全局索引时，优化器会为HBase表自动选择认为合适的全局索引。

--注意：优化器不能为HBase表选择全文索引，HBase全文索引的利用必须通过 CONTAINS 函数。hyperdrive表的全文索引，不需要使用 CONTAINS 函数，直接使用现有的条件即可。=、<、>、in、like、between and、not in、!=等对应的语句即可。


```

#### 指定索引查询模式

```sql
--指定全局索引和全文索引进行查询，只有在 local mode 下才会生效，该模式的参数设置如下：
set ngmr.exec.mode=local;
set ngmr.exec.mode=cluster;

```

*指定全局索引查询的语法*

```sql
SELECT /*+USE_INDEX(<table_alias> USING <index_name>)*/ ...      --1
  FROM <tableName> <table_alias>                                 --2
  WHERE <filter_conditions>;                                     --3

--1、/*+USE_INDEX(<table_alias> USING <index_name>)*/ 为指定索引的提示，在提示内必须使用表的化名 table_alias。
--2、必须为表起化名。
--3、filter_conditions ：过滤条件中至少含一个使用的索引中的列。

我们使用的查询语句一般为：select l1，l2，l3..... from tabne_name where l1 = 1;
不指定使用哪个索引，优化器会为HBase表自动选择认为合适的全局索引。
```

#####  指明不使用全局索引查询

```sql
--由前文介绍可知，由于优化器的存在，普通的、未指定全局索引的查询语句 SELECT …​ FROM …​ 会由优化器自动选择一个合适的全局索引。因此，若用户想对含全局索引的表做普通的查询时，需明确指定不使用任何索引，具体语法如下：

SELECT /*+USE_INDEX(<table_alias> USING NOT_USE_INDEX)*/ ... FROM <tableName> <table_alias> WHERE <filter_conditions>;
```

##### 指定全文索引查询

```sql
--hbase表的全文索引通过 CONTAINS 函数使用，目前支持精确匹配，前缀查询，模糊查询和范围查询四种语义，支持检索条件的逻辑组合。

--全文索引查询的语法
SELECT ... FROM <tableName>
  WHERE CONTAINS(<column>, "<fulltext_query>")                                   --1
  [AND|OR CONTAINS(<column>, "<fulltext_query>") AND|OR CONTAINS(<column>, "    <fulltext_query>") ...];                                                         --2
  
1、CONTAINS 函数需要两个参数：列名 <column> 和检索表达式 "<fulltext_query>"（注意，检索条件要放在 双引号 中），且两者是一一匹配的。
例； fulltext_query 表达式；contains(id ,"term '01010'")   --精确匹配 id = 01010的数据
2、CONTAINS 中包含全文检索条件，一次查询中可以使用多个 CONTAINS ，之间用 AND 或者 OR 连接。

CONTAINS 函数中的检索表达式 "<fulltext_query>" 需要有如下形式：
CONTAINS检索表达式的格式
"<operator> '<search_contents>' [and|or <operator> '<search_contents>' and|or <operator> '<search_contents>'...]"
其中，<operator> 为全文检索运算符， '<search_contents>' 为检索内容（注意单引号）。一个全文检索条件可以由多个 <operator> <search_contents> 组成，之间用 and 或 or 连接。

全文索引创建时支持的数据类型包括：BOOLEAN, TINYINT, SMALLINT, INT, BIGINT,FLOAT, DOUBLE, STRING, VARCHAR和STRUCT。但模糊、前缀、正则查询只可用于 STRING 类型的列。

不要对非 STRING 类型进行模糊、前缀、正则等查询。如果是对数字类型的进行范围查询，则需保证该列的数据类型为 #b(即int类型) 才可以。

							使用 CONTAINS 时的已知问题
1、只支持Inceptor Engine在 local mode，其他模式下运行直接报错。
2、如果全文检索条件涉及的列不在全文索引中，会直接报错。
3、只支持正序排序。
4、对OR操作符，不支持与非全文检索条件的混用，如不能写 CONTAINS(col, <search_contents>) OR col = <search_contents> ；对AND条件无此限制。
5、使用 range 操作符比 <=, \<, >= 和 > 号，如推荐使用 (bi, "range '[1,3]'") 。不推荐使用 (bi,"\< '3' and >='1'")。
6、使用 in 操作符比使用多个or表达式好,如推荐使用 (en, "in 'sunday,monday'"),不推荐使用 (en, "term 'sunday'" or "term 'monday'")。 

全文检索操作符 <OPERATOR> 包括；

```

```sql
例；
--精确匹配(term),查询en列的值 精确等于tuesday 的记录。
select * from hbase_inner_table where contains(en, "term 'tuesday'");
+-------+-----+-------+--------+------------------------+----------+--------+
| key1  | bi  |  dc   |   ch   |           ts           |    en    |   bl   |
+-------+-----+-------+--------+------------------------+----------+--------+
| 003   | 3   | 3.01  | hbase  | 2017-01-10 15:05:20.0  | tuesday  | false  |
+-------+-----+-------+--------+------------------------+----------+--------+

--前缀匹配(prefix),查询en列值的前缀为 tues 的记录。
select * from hbase_inner_table where contains(en, "prefix 'tues'");
+-------+-----+-------+-----------+------------------------+--------------+--------+
| key1  | bi  |  dc   |    ch     |           ts           |      en      |   bl   |
+-------+-----+-------+-----------+------------------------+--------------+--------+
| 003   | 3   | 3.01  | hbase     | 2017-01-10 15:05:20.0  | tuesday      | false  |
| 008   | 8   | 8.01  | fulltext  | 2017-01-15 17:23:40.0  | tuestuesday  | false  |
+-------+-----+-------+-----------+------------------------+--------------+--------+

--模糊查询(wildcard),查询 \* 前的字符 tues 出现任意次，且以 day 结尾记录。
select * from hbase_inner_table where contains(en, "wildcard 'tues*day'");
+-------+-----+-------+-----------+------------------------+--------------+--------+
| key1  | bi  |  dc   |    ch     |           ts           |      en      |   bl   |
+-------+-----+-------+-----------+------------------------+--------------+--------+
| 003   | 3   | 3.01  | hbase     | 2017-01-10 15:05:20.0  | tuesday      | false  |
| 008   | 8   | 8.01  | fulltext  | 2017-01-15 17:23:40.0  | tuestuesday  | false  |
+-------+-----+-------+-----------+------------------------+--------------+--------+

--范围查询，查询bi列值大于 6 的记录。
select * from hbase_inner_table where contains(bi, "> '6'");
+-------+-----+-------+-----------+------------------------+--------------+--------+
| key1  | bi  |  dc   |    ch     |           ts           |      en      |   bl   |
+-------+-----+-------+-----------+------------------------+--------------+--------+
| 007   | 7   | 7.01  | inceptor  | 2017-01-14 10:55:20.0  | saturday     | false  |
| 008   | 8   | 8.01  | fulltext  | 2017-01-15 17:23:40.0  | tuestuesday  | false  |
+-------+-----+-------+-----------+------------------------+--------------+--------+

--in表达式(in)//枚举，查询en列的值在 'sunday,monday' 中的记录。
select * from hbase_inner_table where contains(en, "in 'sunday,monday'");
+-------+-----+-------+-------------+------------------------+---------+-------+
| key1  | bi  |  dc   |     ch      |           ts           |   en    |  bl   |
+-------+-----+-------+-------------+------------------------+---------+-------+
| 001   | 1   | 1.01  | Hyperbase   | 2017-01-08 20:31:46.0  | sunday  | true  |
| 002   | 2   | 2.01  | transwarp   | 2017-01-09 10:25:45.0  | monday  | true  |
+-------+-----+-------+-------------+------------------------+---------+-------+

--正则表达式(regexp)，查询en列满足正则表达式为 's.*y' 的记录，.* 表示 s 和 y 间可出现任意个字符。
select * from hbase_inner_table where contains(en, "regexp 's.*y'");
+-------+-----+-------+------------+------------------------+-----------+--------+
| key1  | bi  |  dc   |     ch     |           ts           |    en     |   bl   |
+-------+-----+-------+------------+------------------------+-----------+--------+
| 001   | 1   | 1.01  | Hyperbase  | 2017-01-08 20:31:46.0  | sunday    | true   |
| 007   | 7   | 7.01  | inceptor   | 2017-01-14 10:55:20.0  | saturday  | false  |
+-------+-----+-------+------------+------------------------+-----------+--------+

--全文检索（match），匹配en列中值为 tuesday 的记录。
select * from hbase_inner_table where contains(en, "match 'tuesday'");
+-------+-----+-------+--------+------------------------+----------+--------+
| key1  | bi  |  dc   |   ch   |           ts           |    en    |   bl   |
+-------+-----+-------+--------+------------------------+----------+--------+
| 003   | 3   | 3.01  | hbase  | 2017-01-10 15:05:20.0  | tuesday  | false  |
+-------+-----+-------+--------+------------------------+----------+--------+


--多个操作符查询，查询满足 \* 前字符 tues 出现任意次，且以 day 结尾并且bi列值为 3 的记录。
select * from hbase_inner_table where contains(en, "wildcard 'tues*day'") and contains(bi, "term '3'");
+-------+-----+-------+-----------+------------------------+--------------+--------+
| key1  | bi  |  dc   |    ch     |           ts           |      en      |   bl   |
+-------+-----+-------+-----------+------------------------+--------------+--------+
| 003   | 3   | 3.01  | hbase     | 2017-01-10 15:05:20.0  | tuesday      | false  |
+-------+-----+-------+-----------+------------------------+--------------+--------+

--范围表达式(range)，查询bi列的值满足 [1,3) 的记录。数据范围是较特殊的search_content，支持开闭区间的组合：
select * from hbase_inner_table where contains(bi, "range '[1,3)'");
+-------+-----+-------+-------------+------------------------+---------+-------+
| key1  | bi  |  dc   |     ch      |           ts           |   en    |  bl   |
+-------+-----+-------+-------------+------------------------+---------+-------+
| 001   | 1   | 1.01  | Hyperbase   | 2017-01-08 20:31:46.0  | sunday  | true  |
| 002   | 2   | 2.01  | transwarp   | 2017-01-09 10:25:45.0  | monday  | true  |
+-------+-----+-------+-------------+------------------------+---------+-------+


```

12、Hbase shell

表管理命令

```shell
#列出HBase中所有的表，可以选择列出和正则表达式 <regex> 匹配的表。
list
list ['<regex>']

#创建表
create '<table>', {NAME => '<column_family>' [, ...]} [, {...}, ...]
建表时至少要指定一个列族，列族名通过 NAME => '<column_family>' 指定，在建表同时还可以设置列族的其他元数据。如果建表时要创建多个列族，不同列族的元信息要用 {} 隔开。

#建表同时划分region
create 't1', 'f1', 'q1', {SPLITS => ['3', '5', '7']}
这里 ['3', '5', '7'] 为表 t1 的split key。

#查看 <table> 的元数据。
describe '<table>'

#查看 <table> 是否存在。
exists '<table>'

#列出HBase中所有的filter。
show_filters

#将指定表上线。
enable '<table>'

#将指定表下线。
disable '<table>'

#将所有表上线。
enable_all 't.*'

#查看 <table> 是否上线。
is_enabled '<table>'

#将所有表下线。
disable_all 't.*'

#查看 <table> 是否下线。
is_disabled '<table>'

#delete_table 删除指定表，不需要先将表下线就可以删除表。我们推荐使用 delete_table 而不是 disable 和 drop 来删除表。
delete_table '<my_table>'

#将所有表名和指定正则表达式 <regex> 匹配的表删除。
drop_all '<regex>'

#alter 可以用于添加和修改表或表中列族的元数据，可以同时修改多个列簇。使用 <PROPERTY> => <value> 的方式设置表或列族的属性。如果只修改一组元数据，可以不加 {}；如果同时修改多组元数据（例如修改多个列族元数据），每组属性需要放在不同的 {} 中。
alter '<table>', {<PROPERTY_1> => <value_1>, <PROPERTY_2> => <value_2>, ...}, {...}, ...
alter 't1', {NAME => 'f1', IN_MEMORY => true}, {NAME => 'f2', VERSION => 5}

#用 alter 删除列族
alter 't1', NAME => 'f1', METHOD => 'delete'
或 alter 't1', 'delete' => 'f1'

#alter_async 和 alter 语法相同，但是语义和 alter 略有不同。alter_async 指令可以立即返回，而 alter 需要等到所有的region都更新完成后才会返回。而要查看更新期间所有regions的更新进度, 可以使用 alter_status 命令.

```

数据操作命令

```shell
#返回指定表中的行数。默认情况下该命令每次只获取一行, 因此对于一张大表来说该命令会执行得很慢。可以通过设置 CACHE 参数来增加每次获取的行数, 从而加速该命令的执行. 还可以指定查询到多少行显示一次 count 结果, 默认值是1000行, 可以通过 INTERVAL 参数进行修改.
count '<table>' [, CACHE => <n>, INTERVAL => <m>]

#将值 <cell_value> 填入指定的表（<table>）、行（<row_key>）和列（<column_family:column_qualifier>）对应的单元格中。时间戳 <timestamp> 为可选项。
put '<table>', '<row_key>', '<column_family:column_qualifier>', '<cell_value>', [<timestamp>]

#获取指定的表和行中的数据。可以通过 <PROPERTY> ⇒ <value> 指定某些属性来过滤获取的结果。例如 COLUMN ⇒ 'column_family:column_qualifier' 指定只获取某一列中的数据。可指定的属性有：COLUMN, TIMESTAMP, TIMERANGE, VERSIONS 和 FILTER。
get '<table>', '<row_key>' [{<PROPERTY> => <value>, ...}]

#扫描指定的表，批量获取表中数据。可以通过 <PROPERTY> ⇒ <value> 指定某些属性来过滤获取的结果。可指定的属性有：TIMERANGE, FILTER, LIMIT, STARTROW, STOPROW, TIMESTAMP, MAXLENGTH, COLUMNS 和 CACHE。

#删除指定的表（<table>）、行（<row_key>）和列（<column_family:column_qualifier>）对应的单元格。可以加上 <timestamp> 选项指定删除某个时间戳对应的数据。
delete '<table>', '<row_key>', '<column_family:column_qualifier>' [, <timestamp>]

#删除指定表、指定行中全部的数据。可以加上 '<column_family:column_qualifier>' 和 <timestamp> 选项指定删除某个列或时间戳对应的数据。
deleteall '<table>', '<row_key>' [, '<column_family:column_qualifier>', <timestamp>]

# truncate 和 truncate_preserve
truncate 类似 delete, 但该命令会立即删除表中所有的数据以及region的划分. 它的内部实现是将指定的表下线, 删除, 并重建. 如果只想立即删除表中所有的数据而不想丢掉原来的region划分, 需要使用 truncate_preserve。
truncate '<table>'
trucnate_preserve '<table>'

```

13、HBase JSON配置使用说明

```shell
#表的扩展数据
索引中的数据不属于表数据，但是从表数据而来，和表密不可分，所以我们将表数据和它所有索引中的数据合称为 表的扩展数据。
表的扩展数据 = 表数据＋全局索引数据＋局部索引数据＋全文索引数据＋LOB索引数据

#表的扩展元数据
一张HBase表的各个索引也有自己的元数据，和索引数据一样，索引的元数据和表的关系也十分紧密，所以我们将表的元数据和它所有索引的元数据合称为 表的扩展元数据
表的扩展元数据 = 表的元数据＋全局索引元数据＋局部索引元数据＋全文索引元数据＋LOB索引元数据

#为操作表的扩展数据和扩展元数据服务，HBase提供了 扩展的命令行指令：describeInJson、alterUseJson 和 truncate_all。

#语法：describeInJson
describeInJson '<table>', ['true|false'] ,['/<target_dir>/<target_file>'], ['true|false']

说明：打印 <table> 的扩展元数据（以JSON串形式输出）。第二个参数为可选参数，选择true会将表的扩展元数据以格式化的JSON串打印，默认值为false。第三个参数为可选参数，指定　/<target_dir>/<target_file> 可以将表的扩展元数据打印到本地 <target_dir> 目录下的 <target_file> 文件中，不指定则默认将结果打印到console。如果指定打印到文件，请注意操作的用户需要有本地 <target_dir> 的写权限，所以保险起见可以选择使用/tmp目录。第四个参数为可选参数，指定是否将Split Keys以十六进制打印。

#语法：alterUseJson
alterUseJson '<table>','<json_string>|/<dir>/<json_file>', '[true|false]'

说明：如果 <table> 存在，根据提供的JSON串配置它的扩展元数据；如果 <table> 不存在，则先建表，并根据提供的JSON串配置它的扩展元数据。有两种提供JSON串的方式：直接提供一个JSON串 <json_string>，或者提供JSON串所在文件的路径 /<dir>/<json_file>。第三个参数为可选参数，指定是否以将JSON串中指定的Split Keys当做十六进制的字符读取。

注意：describeInJson 和 alterUseJson 两个指令的最后一个参数指定是否将Split Keys以十六进制输出/输入。在Split Keys以十六进制字符表示的情况下，执行 describeInJson 和 alterUseJson 时必须总是将这个参数设为true，以保证在每次扩展元数据输出/输入时，HBase可以正常地识别Split Keys。如果不进行设置，会发生Split Keys乱码，导致JSON串中的Split Key和表实际的Split Key不同。

#语法：truncate_all
truncate_all '<table>'
说明：清空 <table> 以及所有 <table> 的各类索引中的数据，也就是清除表的扩展数据，保留表的扩展元数据

#表的扩展元数据全部都包含在传给HBase的JSON串中，alterUseJson 会根据JSON描述来创建和修改表的扩展元数据，将表配置成输入的JSON串描述相同的结构
```

命令对比

| describe（打印元数据）                 | describeInJson（打印扩展元数据）             |
| -------------------------------------- | -------------------------------------------- |
| truncate（清空表数据，保留基础元数据） | truncate_all（清空扩展数据，保留扩展元数据） |
| alter（配置元数据）                    | alterUseJson（配置扩展元数据）               |

- alter 指令不能建表，只能修改表的元数据；而 alterUseJson 可以建表。
- 扩展指令可以对表的扩展元数据进行统一操作和管理，而普通指令只能修改表的基础元数据，容易导致表（元）数据和表的扩展（元）数据不统一的情况（譬如，某表被执行 truncate后，其索引数据并不会被清空）。所以我们建议尽量使用扩展指令。

扩张元数据json的基本格式

```json
{
    "tableName" : "<table_name>"
    "base" : {...},
    "fulltextindex" : {...},
    "globalindex" : {...},
    "lob" : {...}
}
                     
当执行 alterUseJson 来根据输入的JSON串来配置表时，HBase会直接将表的扩展元数据修改为和输入的JSON串一致。也就是说：

如果输入的JSON串相对于表当前的扩展元数据减少了一部分信息，HBase会删除这部分信息。注意，扩展元数据中的所有可选配置项都有默认值，所以“将某配置项删除”等同于“将某配置项的值恢复为默认值”。

如果输入的JSON串相对于表当前的扩展元数据增加了一部分信息，HBase会添加这部分信息；

如果输入的JSON串相对于表当前的扩展元数据有一部分信息发生了变化，HBase会更新这部分信息。
                     

```

JSON串的编写规则

```
拼写规则（配置）
	1、除了fulltextindex模块和部分索引模块的配置项，大多数配置项都要放在双引号中，
	2、除了fulltextindex模块以外，所有模块中的配置项名称都需要 大写，除非另外指出，配置项的值都需要放在双引号中。
	3、因为fulltextindex的信息需要和Elasticsearch共享，fulltextindex模块的拼写和其他模块不同，有专门的规定
                     
关于JSON串中的顺序：
	JSON串中的模块顺序没有规定。
	JSON串中配置项的先后顺序没有规定。
	"SPLIT_KEYS" 组中元素出现顺序没有规定。
	有的配置项组内元素的顺序有规定，包括：

```

#### base模块

```json
#base模块是表的扩展元数据中必须的模块，任何一个 alterUseJson 传递给HBase的JSON串都必须有该模块。
#普通HBase表的base模块格式

"base" : {
    "SPLIT_KEYS": ["<split_key1>", "<split_key2>", ...], 
    "families" : [{<cf_meta>}, {<cf_meta>}, ...}], 
    "THEMIS_ENABLE" : false 
}

1、"SPLIT_KEYS" 配置项指定该表的Split Key，该配置项为 选填项 。Split Key只能在建表时指定，建表后无法修改，所以建表后传给HBase的JSON串中这一配置项都无效。
2、"families" 配置项配置表中列族的元数据，该配置项为 必填项。它由一组列族元数据组成：每个元素 {<cf_meta>} 各包含一个列族的元数据，组中至少要有一个元素。列族的元数据配置 {<cf_meta>} 有固定的格式，如下{<cf_meta>} 的配置。
3、"THEMIS_ENABLE" 为 选填项 ，bool型，不加引号。

{<cf_meta>} 的配置
{
    "FAMILY":"<column_family>",  // 必选项，指定列族名。
    "DATA_BLOCK_ENCODING":"<encoding_scheme>", // 可选项，默认为NONE，无编码方式
    "BLOOMFILTER":"<bloomfilter>", //可选项，默认为NONE
    "REPLICATION_SCOPE":"<int>", //可选项，默认为0
    "VERSIONS":"<num_versions>", // 可选项，默认为1
    "COMPRESSION":"<compression_scheme>", // 可选项，默认为NONE
    "MIN_VERSIONS":"<num_minversions>", // 可选项，默认为0
    "TTL":"<ttl>", //可选项，默认为Integer.Max
    "KEEP_DELETED_CELLS":"<boolean>", //　可选项，请指定为FALSE
    "BLOCKSIZE":"<blocksize>", // 可选项，默认65535
    "IN_MEMORY":"<boolean>", //可选项，默认为false
    "BLOCKCACHE":"<boolean>"  //可选项，默认为true
}

#我们一般使用的配置为：
{
    "FAMILY":"f",  // 必选项，指定列族名。
    "DATA_BLOCK_ENCODING":"PREFIX", // 可选项，默认为NONE，无编码方式
    "BLOOMFILTER":"ROW", //可选项，默认为NONE
    "REPLICATION_SCOPE":"0", //可选项，默认为0
    "VERSIONS":"1", // 可选项，默认为1
    "COMPRESSION":"SNAPPY", // 可选项，默认为NONE
    "MIN_VERSIONS":"0", // 可选项，默认为0
    "TTL":"时间戳", //可选项，默认为Integer.Max
    "KEEP_DELETED_CELLS":"FALSE", //　可选项，请指定为FALSE
    "BLOCKSIZE":"65535", // 可选项，默认65535
    "IN_MEMORY":"false", //可选项，默认为false
    "BLOCKCACHE":"ture"  //可选项，默认为true
}
```

<cf_meta>} 的可选配置

| 配置名称            | 值                                                         | 默认值                        | 一般为                                                       |
| ------------------- | ---------------------------------------------------------- | ----------------------------- | ------------------------------------------------------------ |
| DATA_BLOCK_ENCODING | NONE, PREFIX, DIFF, FAST_DIFF, PREFIX_TREE, INDEXED_PREFIX | NONE                          | PREFIX 或 PREFIX_TREE                                        |
| BLOOMFILTER         | NONE, ROW, ROWCOL                                          | NONE                          | ROW                                                          |
| REPLICATION_SCOPE   | 0（表示local scope），1（表示global scope）                | 0                             | 0                                                            |
| VERSIONS            | 1或更多                                                    | 1                             | 1                                                            |
| COMPRESSION         | LZO, GZ, NONE, SNAPPY, LZ4                                 | NONE                          | SNAPPY                                                       |
| MIN_VERSIONS        | 0或更多                                                    | 0                             | 0                                                            |
| TTL                 | 正整数（表示秒数）                                         | INT最大值2147483647，表示永久 | 按业务确定表的 TTL，如对于LOB这样的特大列族，设置合理的 TTL 是一个删除过期旧数据的好方法。 |
| KEEP_DELETED_CELLS  | true或false                                                | false                         | false                                                        |
| BLOCKSIZE           | 正整数（表示一个Hfile中的byte数量）                        | 65536                         | 65536                                                        |
| IN_MEMORY           | true或false                                                | false                         | false                                                        |
| BLOCKCACHE          | true或false                                                | false                         | false                                                        |
|                     |                                                            |                               |                                                              |

```json
下面是一张有两个列族（f1, f2）的HBase表的base模块：

"base" : {
    "families" : [ {
      "FAMILY" : "f1",  // 列族f1
      "DATA_BLOCK_ENCODING" : "NONE",
      "BLOOMFILTER" : "ROW",
      "REPLICATION_SCOPE" : "0",
      "VERSIONS" : "1",
      "COMPRESSION" : "NONE",
      "MIN_VERSIONS" : "0",
      "TTL" : "2147483647",
      "KEEP_DELETED_CELLS" : "FALSE",
      "BLOCKSIZE" : "65536",
      "IN_MEMORY" : "false",
      "BLOCKCACHE" : "true"
    }, {
      "FAMILY" : "f2",  // 列族f2
      "DATA_BLOCK_ENCODING" : "NONE",
      "BLOOMFILTER" : "ROW",
      "REPLICATION_SCOPE" : "0",
      "VERSIONS" : "1",
      "COMPRESSION" : "NONE",
      "MIN_VERSIONS" : "0",
      "TTL" : "2147483647",
      "KEEP_DELETED_CELLS" : "FALSE",
      "BLOCKSIZE" : "65536",
      "IN_MEMORY" : "false",
      "BLOCKCACHE" : "true"
    } ],
    "THEMIS_ENABLE" : false
  }
```

#### fulltextindex模块

```json
#fulltextindex模块是表的扩展元数据中的可选模块，您只需在为表建全文索引时在JSON串中填写该模块。
"fulltextindex" : {
    "tableName" : "<affiliated_table>", 
    "allowUpdate" : "<string>", //非必填，string型，加引号；兼容之前不加引号写法
    "ttl" : "<string>", //非必填，string型，加引号；兼容之前不加引号写法
    "source" : "<string>" //非必填，string型，加引号；兼容之前不加引号写法
    "all" : "<string>", //非必填，string型，加引号；兼容之前不加引号写法
    "storeAsSource" : "<string>", //非必填，string型，加引号；兼容之前不加引号写法
    "storeFamily" : "<string>", //非必填，string型，加引号
    "writeConsistencyLevel" : "<string>", //非必填，string型，加引号
    "fields" : [{<field_meta>}, {<field_meta>}, ... ] 
}
1、"tableName"	必填项，指定全文索引所属表的名称。注意和base模块的同名配置项的意义区分。
2、"fields" 配置项用于配置建全文索引的各个字段，为必选项。它由一组字段配置信息组成：每个元素 {<field_meta>} 各包含一个字段的配置，组中至少要有一个元素。字段的配置 <field_meta> 有固定的格式，在{<field_meta>} 的配置中会详细介绍。
3、和其他模块不同，fulltextindex模块中的配置项都 必须小写，并且，配置项的值的拼写，包括大小写和是否放在双引号之间，必须和上面提供的完全一致

#我们的配置一般为：
"fulltextindex" : {
    "tableName" : "<affiliated_table>", 
    "allowUpdate" : "true", //非必填，string型，加引号；兼容之前不加引号写法
    "ttl" : "时间戳", //非必填，string型，加引号；兼容之前不加引号写法
    "source" : "false" //非必填，string型，加引号；兼容之前不加引号写法
    "all" : "false", //非必填，string型，加引号；兼容之前不加引号写法
    "storeAsSource" : "<string>", //非必填，string型，加引号；兼容之前不加引号写法
    "storeFamily" : "false", //非必填，string型，加引号
  //  "writeConsistencyLevel" : "<string>", //非必填，string型，加引号
    "fields" : [{<field_meta>}, {<field_meta>}, ... ] 
}
```

fulltextindex模块中的配置选项

| 配置名称    | 值                   | 默认值 | 一般为                         |
| ----------- | -------------------- | ------ | ------------------------------ |
| allowUpdate | true或false          | true   | true                           |
| ttl         | 正整数（表示毫秒数） | 0      | 如果没有超期限制，使用默认值。 |
| source      | true或false          | true   | true                           |
| all         | true或false          | false  | false                          |

*fulltextindex的{<field_meta>} 的配置*

```json
{<field_meta>} 的配置
{
    "family" : "<column_family_name>", // 必填项，字段所在列族的名称
    "qualifier" : "<column_qualifier>", // 必填项，字段的column qualifier （列的限定符）
    "encode_as_string" : "<string>", //非必填,string型，加引号；兼容之前不加引号写法 
    "attributes" : {
        "index" : "<index_option>", // 可选项，索引方式。
        "store" : "<boolean>", // 可选项，指定是否存储。
        "doc_values" : "<boolean>", //可选项，指定是否使用doc_vales优化。
        "type" : "<data_type>"
    }
}

1、"encode_as_string" 可选项，这个配置项 非常重要，它指定了是否将该字段作为STRING类型转换为byte[]——如果它为true，则将该字段作为STRING类型转换；如果它为false，则根据该字段的 实际类型 转换。该配置项的默认值为 false，即按字段的实际类型转换。但是如果您从Inceptor Engine映射表向HBase导入数据，HBase会默认将数据当做STRING转换（除非[b 关键字显示指定]）。所以，您需要格外注意该字段的数据是如何转换的，如果使用默认方式从Inceptor Engine中导入数据（不根据数据实际类型转换），那么您需要将 encode_as_string 值设为true。
2、"type" 必填项，指定该字段的数据类型。目前支持的数据类型有：string, float, double, byte, short, integer, long和boolean。
#我们的配置一般为
{
    "family" : "f", // 必填项，字段所在列族的名称
    "qualifier" : "a", // 必填项，字段的column qualifier （列的限定符）
    "encode_as_string" : "false", //非必填,string型，加引号；兼容之前不加引号写法 
    "attributes" : {
        "index" : "not_analyzed", // 可选项，索引方式。
        "store" : "true", // 可选项，指定是否存储。
        "doc_values" : "true", //可选项，指定是否使用doc_vales优化。
        "type" : "string"  //实际情况而定
    }
}
```

| 配置名称   | 值                         | 默认值       | 一般为       |
| ---------- | -------------------------- | ------------ | ------------ |
| store      | true或false                | true         | true         |
| index      | analyzed, not_analyzed, no | not_analyzed | not_analyzed |
| doc_values | true或false                | true         | true         |

```json
#用两个字段创建全文索引的表的fulltextindex模块
#下面是一张名为 hyper:testTable 的表的fulltextindex模块。该全文索引使用了两个字段创建。

  "fulltextindex" : {
    "tableName" : "testTable",
    "allowUpdate" : "true",
    "ttl" : "0",
    "source" : "true",
    "all" : "false",
    "storeAsSource" : "false", 
    "storeFamily" : "", 
    "writeConsistencyLevel" : "default", 
    "fields" : [ {
      "family" : "f",
      "qualifier" : "q1",
      "encode_as_string" : "false",
      "attributes" : {
        "index" : "not_analyzed",
        "store" : "true",
        "type" : "integer"
      }
    }, {
      "family" : "f",
      "qualifier" : "q2",
      "encode_as_string" : "false",
      "attributes" : {
        "index" : "not_analyzed",
        "store" : "true",
        "type" : "long"
      }
    } ]
}

  "storeAsSource" : "false", 
  "storeFamily" : "", 
  "writeConsistencyLevel" : "default", 
HBase会自动生成这三项，您应当永远使用系统默认值，无需您自己配置。

```

#### globalindex模块

```json
#globalindex模块是表的扩展元数据中的可选模块，您只需在为表建全局索引时在JSON串中填写该模块，它的格式如下：
#globalindex模块格式
"globalindex": {
    "indexs": [{<global_index_meta>}, {<global_index_meta>}, ...]
}

globalindex模块下只有一个配置项 "indexs"，为必填项。它是由表的所有全局索引元数据构成的组，组中的每个元素 {<global_index_meta>} 对应表的一个全局索引的元数据，组中至少要有一个元素。全局索引的元数据 <global_index_meta> 有固定的格式，在{<global_index_meta>} 的配置中会详细介绍。
```

{<global_index_meta>} 的配置

```json
{
    "INDEX_NAME":"<global_index_name>", //必填项，指定索引名称
    "SPLIT_KEYS": [ "<split_key1>", "<split_key2>" ], //可选项，指定Split Key。默认无Split key，即只有一个region。只能在建索引时指定，建好后不能修改。
    "UPDATE":"<boolean>", //可选项，默认为true
    "DCOP":"<boolean>", //可选项，默认为true
    "INDEX_CLASS":"COMBINE_INDEX", //必填项，值填为COMBINE_INDEX
    "indexColumnInfos": [{<index_column_info>}, {<index_column_info>}, ..., {<rowkey_info>}]  
}
"indexColumnInfos" 必填项。它是一个组，组中的元素 {<index_column_info>} 是各个构成该全局索引的列的信息，包括该列的列族，qualifier和在索引词条中所占的长度。组中的最后一个元素 {<rowkey_info>} 是Row Key信息。组中至少要有两个元素：一个属于构成索引的列，另一个属于Row Key。列信息 {<index_column_info>} 如{<index_column_info>} 的配置中所示，Row Key信息 {<rowkey_info>} 如{<rowkey_info>} 的配置中所示。注意，列信息的先后顺序决定了各列在索引词条中出现的顺序，所以这里组中元素的先后顺序是有意义的，需要您根据自己对索引的设计排列。
```

{<global_index_meta>} 的配置可选项

| 配置名称 | 值                           | 默认值 | 一般为 |
| -------- | ---------------------------- | ------ | ------ |
| UPDATE   | true或false （指定是否更新） | true   | true   |
| DCOP     | true或false                  | true   | true   |

*{<index_column_info>} 的配置*

```json
{
    "FAMILY" : "<column_family>",  // 必填项，该列所属的列族名
    "QUALIFY" : "<column_qualifier>", //必填项，该列的column qualifier
    "SEGMENT_LENGTH" : "<string>" // 必填项，string型，加引号；兼容之前的数字型，不加引号写法
}
```

{<index_column_info>} 的配置

```json
{<rowkey_info>} 的配置
{
    "FAMILY" : "rowKey",  // 必填项，rowKey为固定用法
    "QUALIFY" : "rowKey", //必填项，rowKey为固定用法
    "SEGMENT_LENGTH" : "<string>" //必填项，string型，加引号；兼容之前的数字型，表示该列在全局索引词条中所占长度
}
```

SEGMENT_LENGTH 的选择

```
总得来说，列的 SEGMENT_LENGTH 和列值的平均长度相近即可。但是列值的长度如何计算呢？HBase表中的数据以byte[]形式存储，所有类型的数据在存入HBase时都会被转换成byte[]，列值的长度和 转换成byte[] 的方式直接相关。将数据转换成byte[]有两种方式：

方法一
将数据的值当做STRING类型转换成byte[]

方法二
根据数据的实际类型转换成对应byte[]

如果用方法一转换，那么 SEGMENT_LENGTH 应当设置成列值作为STRING类型的平均长度；如果用方法二转换，那么根据数据类型的不同， SEGMENT_LENGTH 也不同，具体如下表：

```

| 类型     | 长度                                                     |
| -------- | -------------------------------------------------------- |
| BOOLEAN  | 1                                                        |
| TINYINT  | 1                                                        |
| SMALLINT | 2                                                        |
| INTEGER  | 4                                                        |
| BIGINT   | 8                                                        |
| FLOAT    | 4                                                        |
| DOUBLE   | 8                                                        |
| STRING   | 字符串长度 (select max(length(索引列)) from table_name;) |
| VARCHAR  | 字符串长度 (select max(length(索引列)) from table_name;) |

```sql
所以，您不仅需要对建索引列的值有大致的了解，还需要知道建索引列的数据存入HBase时是按照哪种方法转换的。

如果您选择通过Inceptor Engine的映射表向HBase插入数据，可以在建表时指定数据是否按类型转换：

指定数据是否按类型转换
CREATE TABLE ... STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES ("hbase.columns.mapping"=":key,f:q1,f:q2#b");
在 "hbase.columns.mapping"=":key,f:q1,f:q2#b" 中，列名后添加 #b 为指定按照数据实际类型转换为byte[]（方法二），如果没有添加 #b 则默认将数据当做STRING转换为byte[]（方法一）。
```

```json
#下面的globalindex模块属于一张有两个全局索引的表。两个全局索引分别名为 name_balance_global_index　和 name_global_index。其中，name_balance_global_index 由两列建成，name_global_inex 由一列建成。

"globalindex" : {
    "indexs" : [ { //第一个全局索引
      "INDEX_NAME" : "name_balance_global_index",
      "UPDATE" : "true",
      "DCOP" : "true",
      "INDEX_CLASS" : "COMBINE_INDEX",
      "indexColumnInfos" : [ {　//建name_balance_global_index的列之一
        "FAMILY" : "f",
        "QUALIFY" : "q1",
        "SEGMENT_LENGTH" : "8"
      }, { 　// 建name_balance_global_index的列之二
        "FAMILY" : "f",
        "QUALIFY" : "q3",
        "SEGMENT_LENGTH" : "9"
      }, {　
        "FAMILY" : "rowKey",
        "QUALIFY" : "rowKey",
        "SEGMENT_LENGTH" : "16"
      } ],
    }, {　//第二个全局索引
      "INDEX_NAME" : "name_global_index",
      "UPDATE" : "true",
      "DCOP" : "true",
      "INDEX_CLASS" : "COMBINE_INDEX",
      "indexColumnInfos" : [ {　//建name_global_index的列
        "FAMILY" : "f",
        "QUALIFY" : "q1",
        "SEGMENT_LENGTH" : "8"
      }, {
        "FAMILY" : "rowKey",
        "QUALIFY" : "rowKey",
        "SEGMENT_LENGTH" : "16"
      } ],
    } ]
  }
```

# 设置本地模式



set ngmr.exec.mode=local；

