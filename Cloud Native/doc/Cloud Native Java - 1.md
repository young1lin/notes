# 摘要

第一天 14 页（不含序，目录）简单介绍一下发展史，构建权责边界清晰且可持续交付的软件的约束。

第二天 60 页，介绍云原生应用的十二要素，简单介绍 Spring Boot 项目，以及 Cloud Foundry。

第三天 90 页，介绍 Pivotal Cloud Foundry 平台一些基本属性，以及 Enviroment 以及配置，还有部分测试内容。

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

终于找到 CommandLineRunner 实际应用场景了，启动时，部署项目到  CloudFoundry 平台。

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