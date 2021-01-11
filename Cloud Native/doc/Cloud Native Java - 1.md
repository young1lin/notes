# 摘要

第一天 14 页（不含序，目录）简单介绍一下发展史，构建权责边界清晰且可持续交付的软件的约束。

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

### 十二要素

