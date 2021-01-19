---
title: "ElasticSearch学习"
date: 2020-07-14 00:00
tags: ["ElasticSearch","ELK"]
categories: "notes"
cover: "https://i.loli.net/2020/07/14/1pUrsENfPRmIwlv.png"
---

# 准备环境

安装好`docker`，在`docker`里面安装和使用`ElasticSearch`

``` shell
docker pull elasticsearch:6.7.0 
#### 创建es的挂载目录以及配置文件：
## 这里可以自定义文件夹，没必要一定在根目录下创建
cd  / mkdir-p mnt/elasticsearch 

cd  mnt/elasticsearch 

mkdir config

mkdir master 

mkdir slave 

chmod 777 master 

chmod 777 slave

cd config 

touch master.yml

touch slave.yml
```

`master.yml`

``` yml
cluster.name: elasticsearch-cluster
node.name: master
network.bind_host: 0.0.0.0
network.publish_host: `your ip`
http.port: 9200
transport.tcp.port: 9300
http.cors.enabled: true
http.cors.allow-origin: "*"
node.master: true 
node.data: true  
discovery.zen.ping.unicast.hosts: ["这里填你自己的ipv4地址","这里填你自己的ipv4地址:9301"]
```

`slave.yml`

``` shell
cluster.name: elasticsearch-cluster
node.name: slave
network.bind_host: 0.0.0.0
network.publish_host: `your ip`
http.port: 9202
transport.tcp.port: 9302
http.cors.enabled: true
http.cors.allow-origin: "*"
node.master: false
node.data: true  
discovery.zen.ping.unicast.hosts: ["这里填你自己的ipv4地址:9300","这里填你自己的ipv4地址:9301"]

```

启动`master`（如果自定义了文件夹，非根目录创建，在下面加上绝对路径）

堆内存设置建议

1. 最小内存和最大内存设置为一样，这样就避免产生不必要的内存碎片，当然了，你设置成这么多，他其实有时候会显示不是使用了这么多内存
2. `Xmx`不要超过机器内存的50%
3. 不要超过`30GB` https://www.elastic.co/blog/a-heap-of-trouble

``` shell
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300 -v /mnt/elasticsearch/config/master.yml:/usr/share/elasticsearch/config/elasticsearch.yml -v /mnt/elasticsearch/master:/usr/share/elasticsearch/data --name es-master elasticsearch:6.7.0
```

启动`slave`（同上）

``` shell
 docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9201:9201 -p 9301:9301 -v /mnt/elasticsearch/config/slave.yml:/usr/share/elasticsearch/config/elasticsearch.yml -v /mnt/elasticsearch/slave:/usr/share/elasticsearch/data --name es-slave elasticsearch:6.7.0
```

## 安装`kibana`

刚开始装的时候看网上的教程来，一直连不上es，直接去官网找文档了，具体如下

```shell
docker pull kibana:7.1.0
docker run --link es-master:elasticsearch -p 5601:5601 --name kibana -d kibana:7.1.0
```

### 安装`Logstash`

``` shell
docker pull docker.elastic.co/logstash/logstash:7.1.0  # 根据自己不同的版本，选择合适的版本安装
docker-compose up #这里是我自己的安装所有7.1.0版本的东西
# 这里填自己对应的版本，我这是 7.1.0的
```

ELK全部安装完成

书籍中用下面的操作

![image.png](https://i.loli.net/2020/07/14/damvlVABiGSMNx1.png)

# 书籍部分

##  往`ElasticSearch`中插入数据/索引（这里用` Kibana` 的`Devtools`，不要用` curl`命令，有坑）

``` shell
PUT http://localhost:9200/megacorp/employee/1 
{

  "first_name" : "John",

  "last_name" : "Smith",

  "age" :    25,

  "about" :   "I love to go rock climbing",

  "interests": [ "sports", "music" ]

}
```

[书籍地址](http://blog.didispace.com/books/elasticsearch-definitive-guide-cn/010_Intro/30_Tutorial_Search.html)。检验是否成功

http://localhost:9200/megacorp/employee/_search

插入第二条信息

``` shell 
PUT http://localhost:9200/megacorp/employee/2
{
    "first_name" :  "Jane",
    "last_name" :   "Smith",
    "age" :         32,
    "about" :       "I like to collect rock albums",
    "interests":  [ "music" ]
}

```

第三条

``` shell 
PUT http://localhost:9200/megacorp/employee/3 
{
    "first_name" :  "Douglas",
    "last_name" :   "Fir",
    "age" :         35,
    "about":        "I like to build cabinets",
    "interests":  [ "forestry" ]
}
```

## 检索文档

响应的内容中包含一些文档的元信息，`John Smith`的原始JSON文档包含在`_source`字段中。

http://localhost:9200/megacorp/employee/1

``` json
{
    "_index": "megacorp",
    "_type": "employee",
    "_id": "1",
    "_version": 1,
    "_seq_no": 0,
    "_primary_term": 1,
    "found": true,
    "_source": {
        "first_name": "John",
        "last_name": "Smith",
        "age": 25,
        "about": "I love to go rock climbing",
        "interests": [
            "sports",
            "music"
        ]
    }
}
```

让我们搜索姓氏中包含**“Smith”**的员工。要做到这一点，我们将在命令行中使用轻量级的搜索方法。这种方法常被称作**查询字符串(query string)**搜索

http://localhost:9200/megacorp/employee/_search?q=last_name:Smith

``` json
{
    "took": 34,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 2,
        "max_score": 0.2876821,
        "hits": [{
                "_index": "megacorp",
                "_type": "employee",
                "_id": "2",
                "_score": 0.2876821,
                "_source": {
                    "first_name": "Jane",
                    "last_name": "Smith",
                    "age": 32,
                    "about": "I like to collect rock albums",
                    "interests": [
                        "music"
                    ]
                }
            },
            {
                "_index": "megacorp",
                "_type": "employee",
                "_id": "1",
                "_score": 0.2876821,
                "_source": {
                    "first_name": "John",
                    "last_name": "Smith",
                    "age": 25,
                    "about": "I love to go rock climbing",
                    "interests": [
                        "sports",
                        "music"
                    ]
                }
            }
        ]
    }
}
```

## 使用DSL语句查询

查询字符串搜索便于通过命令行完成**特定(ad hoc)**的搜索，但是它也有局限性（参阅简单搜索章节）。Elasticsearch提供丰富且灵活的查询语言叫做**DSL查询(Query DSL)**,它允许你构建更加复杂、强大的查询。

**DSL(Domain Specific Language特定领域语言)**以JSON请求体的形式出现。我们可以这样表示之前关于“Smith”的查询:

``` shell
GET http://localhost:9200/megacorp/employee/_search
{
    "query" : {
        "match" : {
            "last_name" : "Smith"
        }
    }
}
```

返回

``` json
{
    "took": 3,
    "timed_out": false,
    "_shards": { "total": 5, "successful": 5, "skipped": 0, "failed": 0 },
    "hits": {
        "total": 2,
        "max_score": 0.2876821,
        "hits": [{
            "_index": "megacorp",
            "_type": "employee",
            "_id": "2",
            "_score": 0.2876821,
            "_source": {
                "first_name": "Jane",
                "last_name": "Smith",
                "age": 32,
                "about": "I like to collect rock albums",
                "interests": ["music"]
            }
        }, {
            "_index": "megacorp",
            "_type": "employee",
            "_id": "1",
            "_score": 0.2876821,
            "_source": {

                "first_name": "John",

                "last_name": "Smith",

                "age": 25,

                "about": "I love to go rock climbing",

                "interests": ["sports", "music"]

            }
        }]
    }
}
```

## 更复杂的搜索(原文有坑)

我们让搜索稍微再变的复杂一些。我们依旧想要找到姓氏为“Smith”的员工，但是我们只想得到年龄大于30岁的员工。我们的语句将添加**过滤器(filter)**,它使得我们高效率的执行一个结构化搜索：

[书上的`filtered`已经废弃了](https://www.elastic.co/guide/en/elasticsearch/reference/5.0/query-dsl-filtered-query.html)，具体的看链接内容The `filtered` query is replaced by the [bool](https://www.elastic.co/guide/en/elasticsearch/reference/5.0/query-dsl-bool-query.html) query. Instead of the following

``` shell
curl -H 'Content-Type: application/json'  -XGET http://localhost:9200/megacorp/employee/_search -d '{
    "query": {
        "bool": {
            "must": {
                "match": {
                    "last_name": "smith"
                }
            },
            "filter": {
                "range": {
                    "age": { "gt": 30 }
                }
            }
        }
    }
}'
```

查询结果

``` json
{
    "took": 26,
    "timed_out": false,
    "_shards": { "total": 5, "successful": 5, "skipped": 0, "failed": 0 },
    "hits": {
        "total": 1,
        "max_score": 0.2876821,
        "hits": [{
            "_index": "megacorp",
            "_type": "employee",
            "_id": "2",
            "_score": 0.2876821,
            "_source": {
                "first_name": "Jane",
                "last_name": "Smith",
                "age": 32,
                "about": "I like to collect rock albums",
                "interests": ["music"]
            }
        }]
    }
}
```

`gt`为 `greater than`的缩写，[特殊转义符](https://tool.oschina.net/commons?type=2)

## 全文搜索

到目前为止搜索都很简单：搜索特定的名字，通过年龄筛选。让我们尝试一种更高级的搜索，全文搜索——一种传统数据库很难实现的功能。

我们将会搜索所有喜欢**“rock climbing”**的员工：

``` shell
curl -H 'Content-Type: application/json'  -XGET http://localhost:9200/megacorp/employee/_search -d '{
    "query" : {
        "match" : {
            "about" : "rock climbing"
        }
    }
}'
```

结果：

``` json
{
    "took": 2,
    "timed_out": false,
    "_shards": { "total": 5, "successful": 5, "skipped": 0, "failed": 0 },
    "hits": {
        "total": 2,
        "max_score": 0.5753642,
        "hits": [{
            "_index": "megacorp",
            "_type": "employee",
            "_id": "1",
            "_score": 0.5753642,   //这里是评分，评分越高越靠前，相关性。结果相关性评分就是文档与查询条件的匹配程度
            "_source": {

                "first_name": "John",

                "last_name": "Smith",

                "age": 25,

                "about": "I love to go rock climbing",

                "interests": ["sports", "music"]

            }
        }, {
            "_index": "megacorp",
            "_type": "employee",
            "_id": "2",
            "_score": 0.2876821,
            "_source": {
                "first_name": "Jane",
                "last_name": "Smith",
                "age": 32,
                "about": "I like to collect rock albums",
                "interests": ["music"]
            }
        }]
    }
}
```



并且返回相关性最大的结果集。**相关性(relevance)**的概念在Elasticsearch中非常重要，而这个概念在传统关系型数据库中是不可想象的，因为传统数据库对记录的查询只有匹配或者不匹配。



## 短语搜索

目前我们可以在字段中搜索单独的一个词，这挺好的，但是有时候你想要确切的匹配若干个单词或者**短语(phrases)**

``` shell
curl -H 'Content-Type: application/json'  -XGET http://localhost:9200/megacorp/employee/_search -d '{
    "query" : {
        "match_phrase" : {
            "about" : "rock climbing"
        }
    }
}'
```

结果

``` json
{
    "took": 2,
    "timed_out": false,
    "_shards": { "total": 5, "successful": 5, "skipped": 0, "failed": 0 },
    "hits": {
        "total": 1,
        "max_score": 0.5753642,
        "hits": [{
            "_index": "megacorp",
            "_type": "employee",
            "_id": "1",
            "_score": 0.5753642,
            "_source": {

                "first_name": "John",

                "last_name": "Smith",

                "age": 25,

                "about": "I love to go rock climbing",

                "interests": ["sports", "music"]

            }
        }]
    }
}
```

## 高亮我们的搜索

**高亮(highlight)**匹配到的关键字

``` shell
curl -H 'Content-Type: application/json'  -XGET http://localhost:9200/megacorp/employee/_search -d '{
    "query" : {
        "match_phrase" : {
            "about" : "rock climbing"
        }
    },
     "highlight": {
        "fields" : {
            "about" : {}
        }
    }
}'
```

结果

``` json
{
    "took": 4,
    "timed_out": false,
    "_shards": { "total": 5, "successful": 5, "skipped": 0, "failed": 0 },
    "hits": {
        "total": 1,
        "max_score": 0.5753642,
        "hits": [{
            "_index": "megacorp",
            "_type": "employee",
            "_id": "1",
            "_score": 0.5753642,
            "_source": {

                "first_name": "John",

                "last_name": "Smith",

                "age": 25,

                "about": "I love to go rock climbing",

                "interests": ["sports", "music"]

            },
            "highlight": { "about": ["I love to go <em>rock</em> <em>climbing</em>"] }
        }]
    }
}
```

## 分析

最后，我们还有一个需求需要完成：允许管理者在职员目录中进行一些分析。 Elasticsearch有一个功能叫做**聚合(aggregations)**，它允许你在数据上生成复杂的分析统计。它很像SQL中的`GROUP BY`但是功能更强大。

``` shell
curl -H 'Content-Type: application/json'  -XGET http://localhost:9200/megacorp/employee/_search -d '{
   "aggs": {
    "all_interests": {
        "terms": { "field": "interests" }
    }
	}
}'

```

[使用此功能前先执行下面语句]([https://russxia.com/2019/04/13/Elasticsearch%E4%B8%AD%E9%81%87%E8%A7%81%E7%9A%84%E9%97%AE%E9%A2%98%E6%B1%87%E6%80%BB/](https://russxia.com/2019/04/13/Elasticsearch中遇见的问题汇总/))（如果按照他上面说的，PUT请求也是失败的，返回的信息是`ES`只接受`GET`，`POST`请求，当然`PUT`是`POST`的变种而已，本质还是`POST`）

``` shell
curl -H 'Content-Type: application/json'  -X POST http://localhost:9200/megacorp/_mapping/employee -d '{
  "properties": {
      "interests": {
          "type": "text",
          "fielddata": true
      }
  }
}'
```

执行成功后返回

``` json
{"acknowledged":true}
```



最终分析成功返回

``` java
{
    "took": 4,
    "timed_out": false,
    "_shards": { "total": 5, "successful": 5, "skipped": 0, "failed": 0 },
    "hits": {
        "total": 4,
        "max_score": 1.0,
        "hits": [{
            "_index": "megacorp",
            "_type": "employee",
            "_id": "2",
            "_score": 1.0,
            "_source": {
                "first_name": "Jane",
                "last_name": "Smith",
                "age": 32,
                "about": "I like to collect rock albums",
                "interests": ["music"]
            }
        }, {
            "_index": "megacorp",
            "_type": "employee",
            "_id": "1",
            "_score": 1.0,
            "_source": {

                "first_name": "John",

                "last_name": "Smith",

                "age": 25,

                "about": "I love to go rock climbing",

                "interests": ["sports", "music"]

            }
        }, {
            "_index": "megacorp",
            "_type": "employee",
            "_id": "3",
            "_score": 1.0,
            "_source": {
                "first_name": "Douglas",
                "last_name": "Fir",
                "age": 35,
                "about": "I like to build cabinets",
                "interests": ["forestry"]
            }
        }, {
            "_index": "megacorp",
            "_type": "employee",
            "_id": "NrLnS3MBZ0JKzZs2l0EO",
            "_score": 1.0,
            "_source": {
                "properties": {
                    "interests": {
                        "type": "text",
                        "fielddata": true
                    }
                }
            }
        }]
    },
    "aggregations": { "all_interests": { "doc_count_error_upper_bound": 0, "sum_other_doc_count": 0, "buckets": [{ "key": "music", "doc_count": 2 }, { "key": "forestry", "doc_count": 1 }, { "key": "sports", "doc_count": 1 }] } }
}
```

### 原因分析

https://www.elastic.co/guide/en/elasticsearch/reference/current/fielddata.html

text类型的字段在查询时使用的是在内存中的称为fielddata的数据结构。这种数据结构是在第一次将字段用于聚合/排序/脚本时基于需求建立的。

它通过读取磁盘上每个segmet上所有的倒排索引来构建，反转term和document的关系(倒排)，并将结果存在Java堆上(内存中)。(因此会耗费很多的堆空间，特别是在加载很高基数的text字段时)。一旦fielddata被加载到堆中，它在segment中的生命周期还是存在的。

因此，加载fielddata是一个非常消耗资源的过程，甚至能导致用户体验到延迟.这就是为什么 fielddata 默认关闭。

开启text 字段的fielddata

{% note info%}

如果我们想知道所有姓"Smith"的人最大的共同点（兴趣爱好），我们只需要增加合适的语句既可：

{%endnote%}

``` shell
curl -H 'Content-Type: application/json'  -XGET http://localhost:9200/megacorp/employee/_search -d '{
    "query": {
      "match": {
        "last_name": "smith"
      }
    },
   "aggs": {
    "all_interests": {
        "terms": { "field": "interests" }
    }
	}
}'
```

返回

``` json
{
    "took": 3,
    "timed_out": false,
    "_shards": { "total": 5, "successful": 5, "skipped": 0, "failed": 0 },
    "hits": {
        "total": 2,
        "max_score": 0.2876821,
        "hits": [{
            "_index": "megacorp",
            "_type": "employee",
            "_id": "2",
            "_score": 0.2876821,
            "_source": {
                "first_name": "Jane",
                "last_name": "Smith",
                "age": 32,
                "about": "I like to collect rock albums",
                "interests": ["music"]
            }
        }, {
            "_index": "megacorp",
            "_type": "employee",
            "_id": "1",
            "_score": 0.2876821,
            "_source": {

                "first_name": "John",

                "last_name": "Smith",

                "age": 25,

                "about": "I love to go rock climbing",

                "interests": ["sports", "music"]

            }
        }]
    },
    "aggregations": {
        "all_interests": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [{
                    "key": "music",
                    "doc_count": 2
                },
                {
                    "key": "sports",
                    "doc_count": 1
                }
            ]
        }
    }
}
```

聚合也允许分级汇总。例如，让我们统计每种兴趣下职员的平均年龄：

``` shell
curl -H 'Content-Type: application/json'  -XGET http://localhost:9200/megacorp/employee/_search -d '{
    "query": {
        "match": {
            "last_name": "smith"
        }
    },
    "aggs": {
        "all_interests": {
           "terms" : { "field" : "interests" },
            "aggs" : {
                "avg_age" : {
                    "avg" : { "field" : "age" }
                }
            }
        }
    }
}'
```

返回

``` json
{
    "took": 3,
    "timed_out": false,
    "_shards": { "total": 5, "successful": 5, "skipped": 0, "failed": 0 },
    "hits": {
        "total": 2,
        "max_score": 0.2876821,
        "hits": [{
            "_index": "megacorp",
            "_type": "employee",
            "_id": "2",
            "_score": 0.2876821,
            "_source": {
                "first_name": "Jane",
                "last_name": "Smith",
                "age": 32,
                "about": "I like to collect rock albums",
                "interests": ["music"]
            }
        }, {
            "_index": "megacorp",
            "_type": "employee",
            "_id": "1",
            "_score": 0.2876821,
            "_source": {

                "first_name": "John",

                "last_name": "Smith",

                "age": 25,

                "about": "I love to go rock climbing",

                "interests": ["sports", "music"]

            }
        }]
    },
    "aggregations": {
        "all_interests": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [{
                    "key": "music",
                    "doc_count": 2,
                    "avg_age": {
                        "value": 28.5
                    }
                },
                {
                    "key": "sports",
                    "doc_count": 1,
                    "avg_age": {
                        "value": 25
                    }
                }
            ]
        }
    }
}
```

### 集群健康

在Elasticsearch集群中可以监控统计很多信息，但是只有一个是最重要的：**集群健康(cluster health)**。集群健康有三种状态：`green`、`yellow`或`red`。

```Javascript
GET http://localhost:9200/_cluster/health
```

在一个没有索引的空集群中运行如上查询，将返回这些信息：

```Javascript
{
   "cluster_name":          "elasticsearch",
   "status":                "green", <1>
   "timed_out":             false,
   "number_of_nodes":       1,
   "number_of_data_nodes":  1,
   "active_primary_shards": 0,
   "active_shards":         0,
   "relocating_shards":     0,
   "initializing_shards":   0,
   "unassigned_shards":     0
}
```

- <1> `status` 是我们最感兴趣的字段

`status`字段提供一个综合的指标来表示集群的的服务状况。三种颜色各自的含义：

| 颜色     | 意义                                       |
| -------- | ------------------------------------------ |
| `green`  | 所有主要分片和复制分片都可用               |
| `yellow` | 所有主要分片可用，但不是所有复制分片都可用 |
| `red`    | 不是所有的主要分片都可用                   |

在接下来的章节，我们将说明什么是**主要分片(primary shard)**和**复制分片(replica shard)**，并说明这些颜色（状态）在实际环境中的意义。



让我们在集群中唯一一个空节点上创建一个叫做`blogs`的索引。默认情况下，一个索引被分配5个主分片，但是为了演示的目的，我们只分配3个主分片和一个复制分片（每个主分片都有一个复制分片）

``` shell
curl -H 'Content-Type: application/json'  -X PUT http://localhost:9200/blogs -d '{
   "settings" : {
      "number_of_shards" : 3,
      "number_of_replicas" : 1
   }
}'
```

返回结果

``` json
{"acknowledged":true,"shards_acknowledged":true,"index":"blogs"}
```

再次检查health

``` json
{
    "cluster_name": "elasticsearch-cluster",
    "status": "yellow",
    "timed_out": false,
    "number_of_nodes": 1,
    "number_of_data_nodes": 1,
    "active_primary_shards": 10,
    "active_shards": 10,
    "relocating_shards": 0,
    "initializing_shards": 0,
    "unassigned_shards": 8,
    "delayed_unassigned_shards": 0,
    "number_of_pending_tasks": 0,
    "number_of_in_flight_fetch": 0,
    "task_max_waiting_in_queue_millis": 0,
    "active_shards_percent_as_number": 55.55555555555556
}
```



引用

>[docker安装elasticsearch](https://juejin.im/post/5ca0d12c518825550b35be6d)

> [Elasticsearch中遇见的问题汇总]([https://russxia.com/2019/04/13/Elasticsearch%E4%B8%AD%E9%81%87%E8%A7%81%E7%9A%84%E9%97%AE%E9%A2%98%E6%B1%87%E6%80%BB/](https://russxia.com/2019/04/13/Elasticsearch中遇见的问题汇总/))

> [ElasticSearch](http://blog.didispace.com/books/elasticsearch-definitive-guide-cn/)

