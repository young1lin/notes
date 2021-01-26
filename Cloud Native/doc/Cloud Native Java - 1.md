# 摘要

第一天 14 页（不含序，目录）简单介绍一下发展史，构建权责边界清晰且可持续交付的软件的约束。

第二天 60 页，介绍云原生应用的十二要素，简单介绍 Spring Boot 项目，以及 Cloud Foundry。

第三天 90 页，介绍 Pivotal Cloud Foundry 平台一些基本属性，以及 Enviroment 以及配置，还有部分测试内容。

第四天 121 页，介绍测试相关内容，没有实战，这个得下次来搞，还有迁移遗留程序到 Cloud Foundry 平台上，通过更改环境变量来达到无缝切换，一些中间件。

第五天 140 页，RMI、Google protocol buffer等，REST API 介绍

第六天 160 页，服务发现路由，RestTemplate 等介绍。

第七天 180 页，Spring Cloud Netflix 实战部分。 

第八天 230 页，暂时跳过实战部分，先看些理论，因为我两年前学过。再看看 OAuth 部分和 Spring Security 部分。

第九天 

第十天 308 页，管道符才是我真的想起来的东西，其他的都是看过，写过的。

第十一天 334 页，Binder、MessageChannel、@MessagingGateway、@Gateway、ItemReader、ItemProcessor、ItemWriter、Spring Batch 的批处理概念等。

第十二天 371 页，Spring Cloud Task 简单介绍，工作流介绍（这个我大致扫过），分布式事务，以及故障隔离和 Hystrix，saga 模式（Microservice Patterns 里面有更详细介绍），CQRS。

# 第一天

目录（只挑看起来像是重点的内容）

- 云原生应用程序

  - 亚马逊
  - Netflix
  - 十二要素原则
- Spring Boot 和 Cloud Foundry

  - 简单介绍 Spring Boot
  - 简单介绍 Cloud Foundry 
- 符合十二要素的程序风格的配置

  - 配置合并
  - Spring 框架对配置的支持
  - 启动配置
  - Spring Cloud Config Server 进行中心化、日志型的配置 // 和 Apollo 类似？ 我看应该是
- 测试

  - 在 Spring Boot 中进行测试
  - 集成测试
  - 端到端测试
- 迁移遗留的应用程序

  - 契约
  - 迁移应用程序环境
  - 将应用程序迁移到云上的微重构
- REST API

  - 伦纳德·理查森的成熟模型
  - 内容协商
  - 错误处理
  - 超媒体
  - API 版本
  - 编写 REST API 文档
  - 客户端
- 路由

  - DiscoveryClient 接口
  - Cloud Foundry Route 服务
- 边缘服务
  
  - Netflix Feign
  - Zuul
  - OAuth
  - Spring Cloud Security
- 数据管理

  - 数据建模
  - Spring Data
  - Spring Data MongoDB
  - Spring Data Noe4j
  - Spring Data Redis
- 消息系统

  - Spring Integration 的事件驱动架构
- 消息代理、桥接、竞争消费者模式和事件溯源
  - Spring Cloud Stream
- 批处理和任务
  - 批处理工作
  - Spring Batch
  - 调度
  - 任务管理
  - 通过 Workflow 进行的以工作流为中心的整合
  - 使用消息传递的分布式
- 数据集成
  - 分布式事务
  - 故障隔离和优雅降级
  - CORS（命令查询责任分离）
  - Spring Cloud Data Flow
    - Stream 
    - 任务
    - REST API
    - 实现 Data Flow 客户端
- 可观测的系统
  - 十二要素运维
  - 新方式
  - Spring Boot Actuator
  - 应用程序日志
  - 分布式跟踪
    - Spring Cloud Sleuth
    - OpenZipkin
  - Dashboard
    - Hystrix
    - Spring Boot Admin
    - Ordian Microservices 仪表板
    - Pivotal Cloud Foundry 的 AppsManager
- 服务代理
  - 平台视图
  - 使用 Spring Cloud Cloud Foundry Service Broker 实现服务代理
    - Amazon S3
    - 服务目录
    - 管理服务实例
    - 服务绑定
    - 保护服务代理
  - 部署
  - 运行测试
- 持续交付
  - Pipeline
  - 测试
  - Concourse
  - 持续交付微服务

## 第一章

构建权责边界清晰的可持续交付的软件。

约束条件：

- 软件组件需要构建为可独立部署的服务。
- 服务中的所有业务逻辑都需要使用其运行的数据进行封装。
- 从服务外部无法直接访问数据库。
- 服务需要发布一个允许其他服务访问自身业务逻辑的 Web 界面。

下面的图层描绘了不同抽象层次上的云服务类型。

![云计算栈.png](https://i.loli.net/2021/01/11/bqsZcogu4mdvNwB.png)

# 第二天

### 十二要素

十二要素程序的核心思想

+ 使用**声明**的方式来搭建自动化环境，最大限度地减少新加入项目的开发人员的时间和成本。
+ 与底层操作系统之间建立清晰的约定，在执行环境之间提供**最大的可移植性**。
+ 适合**部署**在现代的**云平台上**，无须提供服务器和系统管理工具。
+ **最大程度减少**开发环境与生产环境之间的**区别**，通过**持续部署**获得最大的灵活性。
+ 可以在不对工具、架构或开发实践带来重大变动的前提下，进行**水平扩展**。

十二要素程序的实践

| 代码库            | 一份版本控制下的基准代码库，多份部署         |
| ----------------- | :------------------------------------------- |
| 依赖              | 显示声明和隔离依赖关系                       |
| 配置              | 在环境中存储配置                             |
| 后端服务          | 把后端服务当作附加资源                       |
| 构建、发布、运行  | 严格分离构建和运行阶段                       |
| 进程              | 将应用程序作为一个或多个无状态进程执行       |
| 端口绑定          | 通过端口绑定暴露服务                         |
| 并发              | 通过进程模型进行扩展                         |
| 易处理            | 通过快速启动和正常关机来最大限度地提高健壮性 |
| 开发/生产环境一致 | 尽可能保持开发、预发布和生产环境的配置一致   |
| 日志              | 将日志视为事件流                             |
| 管理进程          | 将管理任务作为一次性进程运行                 |

构建、发布、运行

**构建阶段**

构建阶段将应用程序的源代码编译或打包到一个程序包中。创建的包被称为一次构建物。

**发布阶段**

发布阶段需要将某次构建与其配置相结合。随后，创建出的发布文件应该可以在某个执行环境中运行。无论是使用版本号还是时间戳，每个版本应该有一个唯一的标识符。每个发布文件都应该被添加到一个目录中，可以通过发布管理工具回滚到之前的发布版本。

**运行阶段**

运行阶段（通常称为运行时）是指在可执行环境中运行一个指定的应用版本。

# 第三天

终于找到 CommandLineRunner 实际应用场景了，启动时，部署项目到  Cloud Foundry 平台。

**PropertyPlaceHolder** Spring Framework 2.5

**@Value** Spring Framework 3.0

**Environment**  Spring Framework 3.1

@Value 还可以用在 setter 方法上面 

@Profile 可以用 SPRING_PROFILE_ACTIVE 来指定当前 profile、JVM 属性（-Dspring.profiles.active=...）、Servlet 应用程序初始化参数，或者以编程的方式设置当前活动的 profile 来指定。

从上到下，依次覆盖，越上面优先级越高

- 命令行参数
- 从 java:comp/env 获取到的 JNDI 属性
- System.getProperties() 的属性
- 操作系统的环境变量
- 文件系统上的外部属性文件：（config /）？application.(yml.properties)
- 归档（config /）? application.(yml.properties) 文件中的属性文件
- 配置类上的 @PropertySourec 注解
- SpringApplication#getDefaultProperties 提供的默认属性

YAML 规范，大量层级化的配置项。

Spring Cloud Config Server

Spring Cloud Config Client

**spring.application.name** 默认是去 bootstrap.yml 文件中找。

@RefreshScope 标注了这个 Bean 是可刷新的。

## 测试

单元测试

集成测试（需要 Spring ApplicationContext 内容）

# 第四天

测试，端到端测试，Mock 测试，消费者驱动的契约测试等，之后自己动手写一下消费者驱动的契约测试。

## 迁移遗留的应用程序

Cloud Foundry 为在它之上运行之上的程序，提供了总所周知的运维优势（日志聚合、路由、自我修复、动态扩展和收缩、安全等方面）。

**雪花式部署**

// TODO 翻译 Snowflake 官网的雪花是式部署[第一篇文章](https://community.snowflake.com/s/article/Snowflake-CI-CD-using-Flyway-and-Azure-DevOps-Pipeline-Part-1)                

[第二篇文章](https://www.snowflake.com/blog/embracing-agile-software-delivery-and-devops-with-snowflake/)

[第三篇](https://medium.com/slalom-data-analytics/snowflake-deployment-options-key-factors-ab40d58bf986)

[第四篇](https://docs.snowflake.com/en/user-guide/intro-key-concepts.html)

**冒烟测试**

>在[程序设计](https://zh.wikipedia.org/wiki/程序设计)和[软件测试](https://zh.wikipedia.org/wiki/软件测试)领域 ， **冒烟测试** （也包括**信心测试** 、**[健全性测试](https://zh.wikipedia.org/wiki/健全性测试)**、 [[1\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-ISTQB_glossary-1) **构建验证测试** （ **BVT** ） [[2\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-KanerBachPettichord2002-2) [[3\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-3)、**构建验收测试** ）是指初步地进行测试，并以此展示一些简单但足以影响发布软件版本的这一高级别的错误。 冒烟测试是[测试用例](https://zh.wikipedia.org/wiki/测试用例)的子集，测试主要为了覆盖了组件或系统的最重要功能，并用于辅助评价一个软件的主要功能是否正常运行。 [[4\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-DustinRashkaPaul1999-4) 当使用冒烟测试判断一个程序是否需要更深层次的、颗粒度更为细小的测试时，该测试也被称为**入门测试（intake test）** 。 或者，在测试部门对新版本程序进行测试之前，冒烟测试用于自动化测试新版本是否可以正常运行，是否值得测试。 [[5\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-MenakerGuttigoli2014-5) 在[DevOps](https://zh.wikipedia.org/wiki/DevOps)范例中，使用BVT步骤是[持续集成](https://zh.wikipedia.org/wiki/持續整合)成熟阶段的标志之一。 [[6\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-6)
>
>例如，冒烟测试可能会解决一些基本问题，例如“程序是否运行？”，“用户界面是否打开？”或“单击事件是否有效？”等。 冒烟测试的目的在于确认程序是否严重到，需要立即测试非必须的测试。 如《 *Lessons Learned in Software Testing》所写* [[7\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-7) ，“冒烟测试仅仅是在短时间广泛地覆盖产品功能。如果关键功能无法正常工作或关键bug尚未修复，那么你们的团队就不需要浪费更多时间去安装部署以及测试。，则烟雾测试将在有限的时间内广泛涵盖产品功能。不会浪费更多的时间来安装或测试”。 [[2\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-KanerBachPettichord2002-2)
>
>冒烟测试通常会快速地进行，好处就是反馈也是很快，相比之下，更为全面地[套件测试](https://zh.wikipedia.org/wiki/测试套件) 通常会花费更长的时间。
>
>每日构建和冒烟测试是工业界公认的[最佳实践](https://zh.wikipedia.org/wiki/最佳实践)之一 。 [[8\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-8)[[需要引文](https://zh.wikipedia.org/wiki/Wikipedia:可供查证)] 测试人员在构建并更深一步的测试之前，有必要进行冒烟测试。 [微软](https://zh.wikipedia.org/wiki/微软)声称，“在[代码进行审查](https://zh.wikipedia.org/wiki/代码审查)之后，*冒烟测试*是识别并修复软件的性价比最高的方法”。 [[9\]](https://zh.wikipedia.org/zh-cn/冒烟测试_(软件)#cite_note-9)
>
>冒烟测试，可以是手动测试或[自动工具](https://zh.wikipedia.org/wiki/自动化测试)进行冒烟测试。 对于自动化工具测试，构建工程的程序通常会顺带进行该测试。[[来源请求\]](https://zh.wikipedia.org/wiki/Wikipedia:列明来源)
>
>冒烟测试可以是功能测试或者[单元测试](https://zh.wikipedia.org/wiki/单元测试) 。 功能测试通常会使用各种输入设备。从而执行完整的程序。 单元测试则是针对单一功能、子例程、对象的方法。功能测试可以是 脚本化的输入，也可以是自动化的鼠标事件。单元测试可以是实现代码内部独立功能，也可以是通过调用的方式进行测试。

# 第五天

## 用 Spring 实现服务平等

简单介绍了 RMI 以及使用，HTTP 实现 RPC，还有 gRPC。

Spring Session + Redis/Hazelcast 实现 Session 共享。

使用 MongoDB 作为 FileSystem。

还有发送 email。

## REST API

Representational State Transfer

MVC 创建 REST API，线程池响应。

简单介绍 Google Protocol Buffers，以及在 Spring MVC 中的使用。

```protobuf
package demo;

option java_package = "demo";

option java_outer_classname = "CustomerProtos";

message Customer{
	optional int64 id = 1;
	required string firstName = 2;
	required string lastName = 3;
}

message Customers{
	repeated Customer customer = 1;
}
```



```shell
#!/bin/bash
#autor:young1lin
SRC_DIR='$pwd'
DST_DIR='$pwd'/../src/main/

echo source:           ${SRC_DIR}
echo detination root:  ${DST_DIR}

function ensure_implementations(){
  gem list | grep ruby-protocol-buffers || sudo gem install ruby-protocol-buffers
  go get -u github.com/golang/protobuf/{proto,protoc-gen-go}
}

function gen(){
	D=$1
	echo $D
	OUT=$DST_DIR/$D
	mkdir -p $OUT
	protoc -I=$SRC_DIR --${D}_out=$OUT $SRC_DIR/customer.proto
}

ensure_implementations

gen java
gen python
```

# 第六天

再提“鲁棒性”

> 比如说，计算机软件在输入错误、磁盘故障、网络过载或有意攻击情况下，能否不死机、不崩溃，就是该软件的鲁棒性。 所谓“鲁棒性”，也是指控制系统在一定（结构，大小）的参数摄动下，维持其它某些性能的特性。 根据对性能的不同定义，可分为稳定鲁棒性和性能鲁棒性。

Spring RESTDocs 相比于侵入式的 Swagger 更适合云原生应用。

RestTemplate 介绍。

路由（DiscoveryClient）Spring Cloud 提供的统一的抽象，用于提供服务路由。

```java
 * @author Olga Maciaszek-Sharma
 */
public interface DiscoveryClient extends Ordered {

   int DEFAULT_ORDER = 0;

   /**
    * A human-readable description of the implementation, used in HealthIndicator.
    *
    * @return The description.
    */
   String description();

   /**
    * Gets all ServiceInstances associated with a particular serviceId.
    *
    * @param serviceId The serviceId to query.
    * @return A List of ServiceInstance.
    */
   List<ServiceInstance> getInstances(String serviceId);

   /**
    * @return All known service IDs.
    */
   List<String> getServices();

   /**
    * Default implementation for getting order of discovery clients.
    *
    * @return order
    */
   @Override
   default int getOrder() {
      return DEFAULT_ORDER;
   }
}
```

又来了，开始介绍 Eureka、Robbin 以及它们的注解。@LoadBalanced

# 第七天

## 边缘服务

代码

# 第八天

打算跳过样例代码，暂存一下。

## OAuth

可以看看阮一峰的云冲印授权的那个。

OAuth （简称“开放授权”）是互联网上基于令牌的授权标准。令牌减少了用户名和密码暴露的时间窗口。令牌可以将客户端与密码分离，确保错误的客户端永远无法锁定你的账户，令牌可以表示某个用户使用的客户端，或者表示一个没有某个用户的上下文。

- 客户端： 一个发起保护请求的应用程序。
- 资源所有者：通常指允许客户端访问他们的信息的最终用户。
- 资源服务器：托管受保护资源的服务器，能够接收和响应包含访问令牌的受保护请求。
- 授权服务器：服务器在成功认证资源所有者并获得授权后，会向客户端颁发访问令牌。

密码模式授权：用户会直接向授权服务器发送用户名和密码来换取访问令牌。

## Spring Security

简单介绍了下，然后配合 Spring Data JPA 来简单演示了下如何交互授权。

# 第九天

没有看书，不在身边。



# 第十天

OAuth2 和 SSO 怎么实现，加几个模块，然后写些 JS，用 Angular 框架。

NoSQL

Polyglot Persistence（异构持久性）用于描述使用多种数据库模型的体系结构。

其实 DDD 的值对象之类的，更适合用 NoSQL 来解决，例如 MongoDB。

域类

库、仓储（Repository，我更喜欢叫它仓储）

又讲了些业务相关的，这部分确实一般，只是个 Demo 而且还是过时的 DDD package 命名。

## Spring Cache

Redis 用作 Spring Cache

## Spring Integration

事件驱动架构。

｜ 管道符。

# 第十一天

假如有一个产品目录。当向 ProductService 中添加新条目时，它可能会发布描述增量的事件。 SearchEngine 服务kennel会消费消息，然后更新本地绑定的 Elasticsearch 服务实例上的索引。InventoryService 可能会更新包含本地绑定的  RDBMS 服务实例中的数据。RecommendationService 可能决定更新本地绑定的 Neo4j 服务实例中的数据。这些系统不再需要询问 ProductiveService。

**事件溯源**

如果在有序日志中记录每个域事件，则可以通过在任何以前的时间点重新创建系统状态来执行临时查询。如果有任何服务失败，则其本地状态可以从日志中完全被重新创建。这种做法被称为事件溯源。

## Spring Cloud Stream

MessageChannel 

Binding、Binder

@MessagingGateway、@Gateway

消费者

SubscribeChannel

配置中配置消费者组

## 批处理

批流一体——Flink。说实话，我还没怎么用过，就是用 Docker 启动过而已。

### Spring Batch

![Spring Batch 作业的域.png](https://i.loli.net/2021/01/25/5FY17Mnmo8UtjuW.png)

@StepScope

ItemReader

ItemProcessor

ItemWriter

Spring Batch 的 Java 配置 DSL（*domain-specific language*）提供了方便的构建器 API 来配置常见的 ItemReader 和 ItemWriter 实现。

## 配置批和流一体应用

整合 Spring Batch 和 Spring Cloud Stream

# 第十二天

## 通过消息传递远程分区 Spring 批处理作业

TaskExecutor 可以使在 Spring 批处理作业中并行读取变得更容易。 Spring Batch 还支持通过两种机制来并行化写入：远程分区（Remote partitioning）和远程分块（remote chunking）。

## Spring Cloud Task

Task 可以运行并具有预期的最终状态。任务是任何短暂进程或工作负载的理想的抽象，它可能需要运行非常长的时间。Task 描述一次（响应事件）或按计划运行的工作负载。例如：

- 有人要求系统生成并发送重置密码电子邮件。
- 每当新文件到达目录时就运行一个 Spring 批处理作业。
- 定期进行垃圾文件收集和审核不一致的数据或消息队列日志的应用程序。
- 动态文档（或报告）生成。
- 媒体转码。

## 通过 Workflow 进行的以工作流位中心的整合

工作流时通过自治代理系统（和人类）明确建模进度的实践。工作流（Workflow）系统定义了一个状态机，并构建了一个状态机向目标进程的建模。工作流系统被设计成既是技术工件又是更高级别业务流程描述。

Activiti

## 数据集成

### 分布式事务

XADataSoure 

JDBC 的分布式事务的抽象。

### 故障隔离和优雅的降级

Spring Retry 库（我自己也写了，用 AOP 实现，接口自动重试，自动重试一次）。

Netflix Hystrix。

简单讲了 Spring Retry 库，以及 Netflix Hystrix

### saga  模式

saga 模式最初设计用于在统一节点上处理长期事务。传统的长期事务时分布式系统中的瓶颈，因为资源必须在事务处理期间保持不变。这些资源对系统时不可用的。这限制了系统的吞吐量。

saga 是可以写成子事务序列的长期事务。它是子事务的集合，在分布式系统中我们称其为请求（request）。事务需要是可交错的，它们不能相互依赖，必须可以重新排序。每个事务还必须定义补偿性事务，以消除事务的影响，使系统回到语义一致的状态。这些补偿性事务是由开发人员定义的。在设计系统时需要有一些预先考虑，以确保系统始终处于语义一致的状态。saga 模式权衡了系统的可用性和一致性；在整个 saga 完成之前，每个子事务的作用对系统的其他部分是可见的。

saga 事务应该被设计成最多一次（at-most-once）语义，补偿性 saga 事务应该被设计为至少一次（at-least-once）语义。它们应该是幂等的，如果多次执行，则不会留下明显的副作用，

**这个 Kafka 的事务，还有精准一次（exactly-once）；**

- 最多一次（at most once）：消息可能会丢失，但绝不会被重复发送。
- 至少一次（at least once）：消息不会丢失，但有可能被重复发送。
- 精确一次（exactly once）：消息不会丢失，也不会被重复发送。

## CQRS（命令查询责任分离）

将读取与写入分开。命令消息驱动服务中聚合的更新。如果你想问系统问题，只需要发送查询。通过视图（或查询模型）支持查询。查询返回结果，但没有任何可观察到的副作用。