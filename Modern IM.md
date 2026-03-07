IM Instant Message 即时通信，熟悉又陌生的系统。说它熟悉，是因为我们每个人每天都离不开它，微信、QQ、飞书、WhatsApp、Discord、各种 APP 内聊天背后的系统，都属于 IM，甚至 ChatGPT Group Chat 也算 IM。说它陌生，是因为市面上，好像没有一款真正现代的，支持流式回复、支持 所有现代 IM 应该有特性的开源项目。

其实很多东西，已经可以通过现代的方式，来新开一个更为现代的 IM 系统，例如使用  Rust 或者 Go 来写。Go 天生支持 gRPC 非常简单，protobuf 也很适合 IM 底层二进制协议。

之前很难解决的问题，例如超大群的问题，可以通过读扩散解决，只有进入群的时候，才会去更新自己的 seq。小群可以通过写扩散解决。消息可以是复合的，一条消息既有图片，文本，视频，不用像微信一样，为了兼容，每条消息其实都是文本。客户端渲染成不同的内容，例如视频只是一个视频链接。是因为它有历史原因，没有考虑到一条消息可能有多个 block，还有 reaction，对这条消息发表什么评论，这个做不了也是因为历史原因，要兼容历史数据。安卓或者苹果开发，就是这么草单，你不能强制用户更新到最新版本，你永远要考虑到兼容的问题。
### 一、消息核心功能

| 特性          | 说明          |
| ----------- | ----------- |
| 实时消息转发      | 消息即时送达      |
| 已读回执        | 显示消息是否已读    |
| 投递回执        | 确认消息已送达设备   |
| 消息引用/回复     | 引用某条消息进行回复  |
| 消息编辑        | 发送后可修改消息内容  |
| 消息撤回/删除     | 撤回/删除已发送的消息 |
| 消息转发        | 将消息转发到其他会话  |
| 消息置顶        | 将重要消息置顶     |
| 消息收藏/书签     | 收藏消息方便后续查找  |
| 消息搜索        | 全文搜索历史消息    |
| 消息翻译        | 自动或手动翻译消息   |
| @提及         | 在群聊中 @特定用户  |
| 消息 Reaction | 对消息添加表情反应   |
| 阅后即焚        | 消息定时自动删除    |
| 消息去重        | 防止重复消息      |
| 正在输入指示器     | 显示对方正在输入    |

---

### 二、消息类型与富媒体

| 特性      | 说明                |
| ------- | ----------------- |
| 图文混排消息  | 图片和文字可以并存         |
| 流式消息    | 支持 LLM Bot 流式输出   |
| 语音消息    | 发送语音条             |
| 文件传输    | 支持各类文件格式          |
| 贴纸/表情包  | 发送大尺寸表情贴纸         |
| GIF 动图  | 搜索并发送 GIF         |
| 链接预览    | 自动抓取 URL 标题、图片、描述 |
| 名片消息    | 分享联系人/群信息         |
| 位置消息    | 分享静态地理位置          |
| 投票消息    | 群内发起投票            |
| 日程/会议邀请 | 发送日历事件            |
| 媒体缩略图生成 | 自动生成图片/视频预览       |

---

### 三、音视频通信

|特性|说明|
|---|---|
|多用户音视频通话|支持群组音视频会议|
|屏幕共享|通话中分享屏幕|
|实时字幕|语音转文字实时显示|
|通话录制|录制音视频通话内容|

---

### 四、状态与存在感

| 特性     | 说明           |
| ------ | ------------ |
| 用户在线状态 | 在线/离线/忙碌/勿扰等 |
| 最后上线时间 | 显示用户最后活跃时间   |
| 实时位置共享 | 分享持续更新的地理位置  |

---

### 五、多端与同步

|特性|说明|
|---|---|
|多端登录|一个账号多设备同时在线收消息|
|消息漫游|云端存储，任意设备可查历史|
|离线消息|离线时暂存，上线后推送|
|Push 通知|APNs/FCM 离线推送|
|勿扰模式|静音特定会话或时间段|
|未读消息计数|Badge 显示未读数|
|断线重连|自动重连机制|
|心跳保活|维持长连接|

---

### 六、群组与频道

|特性|说明|
|---|---|
|群管理|创建/解散/转让群|
|超大群支持|支持万人级别群消息|
|群成员管理|添加/移除/禁言成员|
|群角色权限|群主/管理员/普通成员|
|群公告|发布群公告|
|群内昵称|群聊中设置专属昵称|
|入群审批|加群需管理员审批|
|邀请链接|通过链接邀请入群|

---

### 七、好友与用户

|特性|说明|
|---|---|
|好友关系管理|添加/删除/备注好友|
|好友分组|好友列表分组管理|
|用户资料设置|头像、昵称、签名等|
|用户标签|给联系人打标签分类|
|黑名单/拉黑|屏蔽特定用户|

---

### 八、智能与自动化

|特性|说明|
|---|---|
|Bot/机器人框架|支持开发和接入机器人|
|Webhook 集成|外部系统事件推送到聊天|
|自动回复|设置离开/忙碌自动回复|
|AI 摘要|AI 生成会话摘要|
|消息审核/过滤|敏感词过滤、图片鉴黄|

---

### 九、安全与加密

|特性|说明|
|---|---|
|消息加密|端到端加密|
|传输加密|TLS/SSL 传输层加密|
|举报机制|举报用户/消息|
|频率限制|防刷/防滥用|

---

### 十、企业与合规

|特性|说明|
|---|---|
|审计日志|记录所有操作供合规审查|
|消息归档|企业合规消息存档|
|SSO/LDAP 集成|企业身份认证对接|
|组织架构同步|与企业 HR 系统同步|
|数据合规/DLP|数据防泄露策略|
|消息保留策略|自动删除 N 天前消息|
|第三方应用集成|与 CRM、Jira 等集成|
|管理后台|用户管理、数据统计|
|GDPR 数据导出/删除|用户数据可导出、可删除|

---

### 十一、协议与架构

| 特性           | 说明                    |
| ------------ | --------------------- |
| WebSocket 支持 | 底层通信协议                |
| TCP 支持       | 底层通信协议                |
| ProtoBuf 序列化 | 二进制序列化                |
| 集群扩容         | Gossip/etcd 服务发现与消息转发 |
| 消息队列         | 削峰、解耦、可靠投递            |
| 消息 ACK 机制    | 确保消息可靠送达              |
| 消息压缩         | 减少带宽消耗                |
| 分库分表         | 数据水平扩展                |
| 冷热数据分离       | 历史消息归档到低成本存储          |

---

### 十二、平台支持

| 特性       | 说明                                  |
| -------- | ----------------------------------- |
| 现代操作系统支持 | Windows/macOS/Linux/iOS/Android/Web |
# 现代 IM 系统架构设计文档

> 目标：构建一个支持 10 万+ 用户、可水平扩展、单人可开发维护的即时通讯系统

---

## 目录

1. [架构总览](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%B8%80%E6%9E%B6%E6%9E%84%E6%80%BB%E8%A7%88)
2. [技术选型](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%BA%8C%E6%8A%80%E6%9C%AF%E9%80%89%E5%9E%8B)
3. [消息同步机制](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%B8%89%E6%B6%88%E6%81%AF%E5%90%8C%E6%AD%A5%E6%9C%BA%E5%88%B6)
4. [数据库设计](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%9B%9B%E6%95%B0%E6%8D%AE%E5%BA%93%E8%AE%BE%E8%AE%A1)
5. [消息队列设计](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%BA%94%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97%E8%AE%BE%E8%AE%A1)
6. [服务发现](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%85%AD%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0)
7. [核心流程](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%B8%83%E6%A0%B8%E5%BF%83%E6%B5%81%E7%A8%8B)
8. [核心代码实现](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%85%AB%E6%A0%B8%E5%BF%83%E4%BB%A3%E7%A0%81%E5%AE%9E%E7%8E%B0)
9. [项目结构](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%B9%9D%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84)
10. [部署方案](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%8D%81%E9%83%A8%E7%BD%B2%E6%96%B9%E6%A1%88)
11. [扩展路径](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%8D%81%E4%B8%80%E6%89%A9%E5%B1%95%E8%B7%AF%E5%BE%84)
12. [功能特性清单](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%8D%81%E4%BA%8C%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7%E6%B8%85%E5%8D%95)

---

## 一、架构总览

### 1.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           精简 IM 架构                                   │
│                      (单人可开发维护版本)                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │                         客户端层                                 │   │
│   │              iOS / Android / Web / Desktop / Bot                 │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │                    网关层 (Gateway)                              │   │
│   │             WebSocket/TCP + Protobuf (Go)                        │   │
│   │       • 连接管理 • 心跳 • 鉴权 • 协议编解码                        │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│                                    ▼                                    │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │                      业务服务层 (Go)                             │   │
│   │      消息 │ 会话 │ 用户 │ 群组 │ 好友 │ Bot │ Webhook             │   │
│   │                    (可单体，可拆分)                               │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                    │                                    │
│              ┌─────────────────────┼─────────────────────┐              │
│              ▼                     ▼                     ▼              │
│   ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐    │
│   │    PostgreSQL     │ │      Valkey       │ │   Meilisearch     │    │
│   │     (主存储)       │ │      (缓存)       │ │     (搜索)        │    │
│   │   消息+元数据+队列  │ │  会话+在线状态    │ │     (可选)        │    │
│   └───────────────────┘ └───────────────────┘ └───────────────────┘    │
│                                                                         │
│   ┌─────────────────────────────────────────────────────────────────┐   │
│   │                 服务发现: 内嵌 Gossip (memberlist)                │   │
│   └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

最小部署：Go 服务 + PostgreSQL + Valkey（三件套）
```

### 1.2 设计原则

|原则|说明|
|---|---|
|**简洁优先**|组件最少化，一个人能开发维护|
|**渐进扩展**|从单机到分布式平滑演进|
|**统一存储**|PostgreSQL 承担多种角色，减少组件|
|**协议高效**|Protobuf 二进制协议，省带宽|
|**无单点**|Gossip 去中心化服务发现|

---

## 二、技术选型

### 2.1 最终选型

|层级|选择|备选|选择理由|
|---|---|---|---|
|**语言**|Go|Rust (网关)|并发模型优秀，编译快，部署简单|
|**通信协议**|WebSocket + Protobuf|TCP + Protobuf|浏览器友好，高效|
|**主数据库**|PostgreSQL|TiDB|一库搞定存储+队列+搜索|
|**缓存**|Valkey|Dragonfly|Redis 100% 兼容，社区活跃|
|**消息队列**|PostgreSQL SKIP LOCKED|Kafka|初期够用，可平滑切换|
|**搜索**|PostgreSQL 全文搜索|Meilisearch|初期够用，可按需升级|
|**服务发现**|内嵌 Gossip|etcd|零额外依赖|
|**文件存储**|MinIO / 本地 + CDN|AWS S3|按需选择|

### 2.2 为什么选 Go

```go
// 一个连接一个 goroutine，代码直观
func handleConnection(conn net.Conn) {
    for {
        msg, err := readMessage(conn)
        if err != nil {
            return
        }
        go processMessage(msg) // 轻松并发
    }
}
// 单机可支撑 10 万+ 长连接
```

|对比项|Go|Java|Rust|Node.js|
|---|---|---|---|---|
|并发模型|Goroutine (轻量)|Thread (重)|async (复杂)|Event Loop|
|编译速度|快|慢|慢|无需编译|
|部署|单二进制|JVM 依赖|单二进制|Node 运行时|
|内存占用|低|高|最低|中|
|开发效率|高|中|低|高|
|IM 生态|成熟|成熟|发展中|一般|

### 2.3 Valkey vs Dragonfly

|维度|Valkey|Dragonfly|
|---|---|---|
|Redis 协议兼容|99.9%（Redis 分支）|95%+|
|常用数据结构|全部支持|全部支持|
|Lua 脚本|完全支持|支持|
|Pub/Sub|完全支持|支持|
|单节点性能|~10 万 QPS|~100 万 QPS|
|多线程|否|是|
|社区成熟度|高 (Linux Foundation)|中等|

**建议**：初期用 Valkey，追求极致性能可切换 Dragonfly（协议兼容，改连接地址即可）。

### 2.4 为什么用 Protobuf

|维度|JSON|Protobuf|
|---|---|---|
|体积|大 (有 key 名称)|小 (只有值)|
|解析速度|慢|快 5-10 倍|
|类型安全|弱|强|
|向后兼容|需约定|原生支持|
|可读性|好|需工具|

**结论**：核心链路用 Protobuf，HTTP API 可用 JSON。

---

## 三、消息同步机制

### 3.1 主流方案对比

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      消息同步方案对比                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  方案 A: 用户维度 Seq（OpenIM/GIM 方式）                                 │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  用户 Alice 的所有消息共享一个递增 seq                             │   │
│  │                                                                   │   │
│  │  seq=1: 来自会话A的消息                                           │   │
│  │  seq=2: 来自会话B的消息                                           │   │
│  │  seq=3: 来自会话A的消息                                           │   │
│  │                                                                   │   │
│  │  客户端只需记住: local_seq = 3                                    │   │
│  │  服务端返回: server_seq = 10                                      │   │
│  │  → 拉取 seq > 3 的所有消息                                        │   │
│  │                                                                   │   │
│  │  问题：万人群发消息 = 写入 10000 个用户的 inbox（写扩散严重）        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  方案 B: 会话维度存储                                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  每个会话独立维护消息序列                                          │   │
│  │  客户端需记住每个会话的 last_msg_id                                │   │
│  │                                                                   │   │
│  │  问题：同步逻辑复杂，需要遍历所有会话                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  方案 C: 混合方案（本文推荐）                                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  用户维度 sync_seq（快速发现变更）+ 会话维度存储（高效拉取）         │   │
│  │  大群使用会话级 seq，避免写扩散                                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

|方案|优点|缺点|
|---|---|---|
|**用户维度 Seq**|同步简单，一次拉取所有变更|热点用户写入瓶颈，大群写扩散|
|**会话维度存储**|无 seq 竞争，写入性能高|同步逻辑复杂|
|**混合方案**|兼顾两者优点|实现稍复杂|

### 3.2 混合方案设计

```
┌──────────────────────────────────────────────────────────────────────┐
│                    混合同步方案                                       │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  1. user_sync_state（每用户一条，判断"有无新消息"）                    │
│  ┌──────────┬──────────┬─────────────────┐                          │
│  │ user_id  │ sync_seq │ updated_at      │                          │
│  ├──────────┼──────────┼─────────────────┤                          │
│  │ Alice    │ 1523     │ 2024-01-15 10:30│                          │
│  └──────────┴──────────┴─────────────────┘                          │
│                                                                      │
│  2. user_conversations（version 标记会话何时变更）                    │
│  ┌──────────┬─────────────────┬─────────┬───────────────┐           │
│  │ user_id  │ conversation_id │ version │ last_msg_id   │           │
│  ├──────────┼─────────────────┼─────────┼───────────────┤           │
│  │ Alice    │ conv_A          │ 1520    │ 789012345...  │           │
│  │ Alice    │ conv_B          │ 1523    │ 789012346...  │           │
│  │ Alice    │ conv_C          │ 1498    │ 789012340...  │           │
│  └──────────┴─────────────────┴─────────┴───────────────┘           │
│                                                                      │
│  version 含义：该会话最后变更时，用户的 sync_seq 值                   │
│  → 查询 version > local_sync_seq 即可找出有新消息的会话              │
│                                                                      │
│  3. conversation_sync_state（大群专用，避免写扩散）                   │
│  ┌─────────────────┬─────────┬──────────┐                           │
│  │ conversation_id │ seq     │ is_large │                           │
│  ├─────────────────┼─────────┼──────────┤                           │
│  │ big_group_1     │ 5823    │ true     │                           │
│  └─────────────────┴─────────┴──────────┘                           │
│                                                                      │
│  大群消息只更新会话 seq，不更新每个用户的 sync_seq                    │
│  客户端单独检查大群的 seq 变化                                       │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.3 同步流程

```
┌──────────────────────────────────────────────────────────────────────┐
│                    客户端同步流程                                     │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  场景：Alice 打开 APP，本地 sync_seq = 1500                          │
│                                                                      │
│  客户端                              服务端                           │
│     │                                  │                             │
│     │  ① 请求同步 (sync_seq=1500)      │                             │
│     │ ─────────────────────────────▶  │                             │
│     │                                  │                             │
│     │  ② 返回:                         │                             │
│     │     server_seq = 1523            │                             │
│     │     changed: [conv_A, conv_B]    │ ← version > 1500 的会话     │
│     │     large_groups: [big_group_1]  │ ← 需单独检查的大群           │
│     │ ◀─────────────────────────────  │                             │
│     │                                  │                             │
│     │  ③ 拉取 conv_A 消息              │                             │
│     │     (id > local_last_msg_id)     │                             │
│     │ ─────────────────────────────▶  │                             │
│     │                                  │                             │
│     │  ④ 返回消息列表                   │                             │
│     │ ◀─────────────────────────────  │                             │
│     │                                  │                             │
│     │  ⑤ 检查大群 seq                  │                             │
│     │ ─────────────────────────────▶  │                             │
│     │                                  │                             │
│     │  ⑥ 更新本地 sync_seq = 1523      │                             │
│     │                                  │                             │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.4 发送消息时的写入逻辑

```
┌──────────────────────────────────────────────────────────────────────┐
│                    发消息写入逻辑                                     │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  【小群/单聊】更新每个成员的 sync_seq                                 │
│  ┌─────────────────────────────────────────────────────────────────┐│
│  │ BEGIN;                                                          ││
│  │                                                                  ││
│  │ -- 插入消息                                                      ││
│  │ INSERT INTO messages (...) VALUES (...);                        ││
│  │                                                                  ││
│  │ -- 递增每个成员的 sync_seq                                       ││
│  │ UPDATE user_sync_state                                          ││
│  │ SET sync_seq = sync_seq + 1                                     ││
│  │ WHERE user_id IN (member1, member2, ...);                       ││
│  │                                                                  ││
│  │ -- 更新会话状态，version = 新的 sync_seq                         ││
│  │ UPDATE user_conversations                                       ││
│  │ SET version = new_seq, last_msg_id = $msg_id, ...               ││
│  │ WHERE conversation_id = $conv_id;                               ││
│  │                                                                  ││
│  │ COMMIT;                                                         ││
│  └─────────────────────────────────────────────────────────────────┘│
│                                                                      │
│  【大群】只更新会话 seq（O(1) 写入）                                  │
│  ┌─────────────────────────────────────────────────────────────────┐│
│  │ BEGIN;                                                          ││
│  │                                                                  ││
│  │ -- 插入消息                                                      ││
│  │ INSERT INTO messages (...) VALUES (...);                        ││
│  │                                                                  ││
│  │ -- 只更新会话的 seq                                              ││
│  │ UPDATE conversation_sync_state                                  ││
│  │ SET seq = seq + 1, last_msg_id = $msg_id                        ││
│  │ WHERE conversation_id = $conv_id;                               ││
│  │                                                                  ││
│  │ -- 更新成员的会话列表（不更新 version）                           ││
│  │ UPDATE user_conversations                                       ││
│  │ SET last_msg_id = $msg_id, unread_count = unread_count + 1      ││
│  │ WHERE conversation_id = $conv_id AND user_id != $sender_id;     ││
│  │                                                                  ││
│  │ COMMIT;                                                         ││
│  └─────────────────────────────────────────────────────────────────┘│
│                                                                      │
│  大群阈值建议：成员 > 200 人标记为大群                                │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.5 与 OpenIM 对比

|维度|OpenIM (用户 Inbox)|本方案 (混合)|
|---|---|---|
|**存储模型**|每条消息写入每个接收者的 inbox|消息只存一份|
|**写放大**|N 消息 × M 成员|小群: M 次状态更新<br>大群: O(1)|
|**存储成本**|高（inbox 膨胀）|低|
|**同步复杂度**|简单|稍复杂|
|**大群性能**|差|好|

---

## 四、数据库设计

### 4.1 核心表结构

```sql
-- ============================================
-- 用户相关
-- ============================================

CREATE TABLE users (
    id          bigint PRIMARY KEY,           -- 雪花 ID
    username    varchar(64) UNIQUE NOT NULL,
    nickname    varchar(64),
    avatar_url  varchar(512),
    status      smallint DEFAULT 1,           -- 1:正常 0:禁用
    created_at  timestamptz DEFAULT now(),
    updated_at  timestamptz DEFAULT now()
);

-- ============================================
-- 同步状态
-- ============================================

-- 用户同步状态（每用户一条）
CREATE TABLE user_sync_state (
    user_id     bigint PRIMARY KEY,
    sync_seq    bigint DEFAULT 0,             -- 用户维度同步序号
    updated_at  timestamptz DEFAULT now()
);

-- 会话同步状态（每会话一条）
CREATE TABLE conversation_sync_state (
    conversation_id bigint PRIMARY KEY,
    seq             bigint DEFAULT 0,         -- 会话内消息序号
    last_msg_id     bigint,
    is_large        boolean DEFAULT false,    -- 是否大群
    updated_at      timestamptz DEFAULT now()
);

-- ============================================
-- 会话相关
-- ============================================

-- 会话表
CREATE TABLE conversations (
    id          bigint PRIMARY KEY,           -- 雪花 ID
    type        smallint NOT NULL,            -- 1:单聊 2:群聊 3:频道
    name        varchar(128),                 -- 群名称
    avatar_url  varchar(512),
    owner_id    bigint,                       -- 群主
    settings    jsonb DEFAULT '{}',           -- 群设置
    created_at  timestamptz DEFAULT now(),
    updated_at  timestamptz DEFAULT now()
);

-- 会话成员表
CREATE TABLE conversation_members (
    conversation_id bigint NOT NULL,
    user_id         bigint NOT NULL,
    role            smallint DEFAULT 0,       -- 0:成员 1:管理员 2:群主
    nickname        varchar(64),              -- 群内昵称
    is_muted        boolean DEFAULT false,
    mute_until      timestamptz,
    joined_at       timestamptz DEFAULT now(),
    PRIMARY KEY (conversation_id, user_id)
);

-- 用户会话列表
CREATE TABLE user_conversations (
    user_id           bigint NOT NULL,
    conversation_id   bigint NOT NULL,
    
    -- 同步相关
    version           bigint DEFAULT 0,       -- 变更时的 user sync_seq
    conv_acked_seq    bigint DEFAULT 0,       -- 已同步的会话 seq（大群用）
    
    -- 业务字段
    last_read_msg_id  bigint DEFAULT 0,
    last_msg_id       bigint DEFAULT 0,
    last_msg_at       timestamptz,
    unread_count      int DEFAULT 0,
    is_pinned         boolean DEFAULT false,
    is_muted          boolean DEFAULT false,
    is_deleted        boolean DEFAULT false,
    updated_at        timestamptz DEFAULT now(),
    
    PRIMARY KEY (user_id, conversation_id)
);

-- 索引
CREATE INDEX idx_user_conv_list ON user_conversations 
    (user_id, is_deleted, is_pinned DESC, last_msg_id DESC) 
    WHERE is_deleted = false;

CREATE INDEX idx_user_conv_version ON user_conversations 
    (user_id, version);

-- ============================================
-- 消息相关
-- ============================================

-- 消息表（按会话 Hash 分区）
CREATE TABLE messages (
    id              bigint NOT NULL,          -- 雪花 ID
    conversation_id bigint NOT NULL,
    sender_id       bigint NOT NULL,
    content_type    smallint NOT NULL,        -- 1:文本 2:图片 3:文件...
    content         jsonb NOT NULL,
    reply_to_id     bigint,                   -- 引用消息 ID
    mentions        bigint[],                 -- @的用户
    status          smallint DEFAULT 1,       -- 1:正常 2:撤回 3:删除
    created_at      timestamptz DEFAULT now(),
    PRIMARY KEY (conversation_id, id)
) PARTITION BY HASH (conversation_id);

-- 创建 16 个分区
DO $$
BEGIN
    FOR i IN 0..15 LOOP
        EXECUTE format(
            'CREATE TABLE messages_p%s PARTITION OF messages 
             FOR VALUES WITH (MODULUS 16, REMAINDER %s)',
            i, i
        );
    END LOOP;
END $$;

-- ============================================
-- 好友关系
-- ============================================

CREATE TABLE friendships (
    user_id     bigint NOT NULL,
    friend_id   bigint NOT NULL,
    status      smallint DEFAULT 0,           -- 0:待确认 1:已添加 2:已拒绝
    remark      varchar(64),
    group_name  varchar(64),
    created_at  timestamptz DEFAULT now(),
    PRIMARY KEY (user_id, friend_id)
);

-- ============================================
-- 黑名单
-- ============================================

CREATE TABLE blocks (
    user_id       bigint NOT NULL,
    blocked_id    bigint NOT NULL,
    created_at    timestamptz DEFAULT now(),
    PRIMARY KEY (user_id, blocked_id)
);

-- ============================================
-- 消息队列（PostgreSQL 实现）
-- ============================================

CREATE TABLE message_queue (
    id          bigserial PRIMARY KEY,
    topic       varchar(64) NOT NULL,
    payload     jsonb NOT NULL,
    status      smallint DEFAULT 0,           -- 0:待处理 1:处理中 2:完成 3:失败
    retry_count smallint DEFAULT 0,
    created_at  timestamptz DEFAULT now(),
    locked_at   timestamptz,
    locked_by   varchar(64)
);

CREATE INDEX idx_queue_pending ON message_queue (topic, id) WHERE status = 0;
```

### 4.2 消息内容 JSON 结构

```go
type MessageContent struct {
    Text        string           `json:"text,omitempty"`
    Image       *ImageContent    `json:"image,omitempty"`
    File        *FileContent     `json:"file,omitempty"`
    Voice       *VoiceContent    `json:"voice,omitempty"`
    Location    *LocationContent `json:"location,omitempty"`
    LinkPreview *LinkPreview     `json:"link_preview,omitempty"`
}

type ImageContent struct {
    URL       string `json:"url"`
    Thumbnail string `json:"thumbnail"`
    Width     int    `json:"width"`
    Height    int    `json:"height"`
    Size      int64  `json:"size"`
}

type FileContent struct {
    URL      string `json:"url"`
    Name     string `json:"name"`
    Size     int64  `json:"size"`
    MimeType string `json:"mime_type"`
}

type VoiceContent struct {
    URL      string `json:"url"`
    Duration int    `json:"duration"`
    Size     int64  `json:"size"`
}

type LocationContent struct {
    Latitude  float64 `json:"latitude"`
    Longitude float64 `json:"longitude"`
    Name      string  `json:"name"`
    Address   string  `json:"address"`
}

type LinkPreview struct {
    URL         string `json:"url"`
    Title       string `json:"title"`
    Description string `json:"description"`
    Image       string `json:"image"`
}
```

### 4.3 常用查询

```sql
-- 1. 获取用户会话列表
SELECT 
    uc.conversation_id,
    uc.unread_count,
    uc.is_pinned,
    uc.is_muted,
    uc.last_msg_at,
    c.type,
    c.name,
    c.avatar_url,
    m.content_type,
    m.content,
    m.sender_id
FROM user_conversations uc
JOIN conversations c ON c.id = uc.conversation_id
LEFT JOIN messages m ON m.conversation_id = uc.conversation_id 
    AND m.id = uc.last_msg_id
WHERE uc.user_id = $1 AND uc.is_deleted = false
ORDER BY uc.is_pinned DESC, uc.last_msg_id DESC
LIMIT 20;

-- 2. 获取有变更的会话（同步用）
SELECT conversation_id, last_msg_id, version
FROM user_conversations
WHERE user_id = $1 AND version > $2;

-- 3. 获取会话消息历史（游标分页）
SELECT id, sender_id, content_type, content, created_at
FROM messages
WHERE conversation_id = $1 AND id < $2
ORDER BY id DESC
LIMIT 20;

-- 4. 标记已读
UPDATE user_conversations
SET last_read_msg_id = $1, unread_count = 0, updated_at = now()
WHERE user_id = $2 AND conversation_id = $3;

-- 5. 消息搜索（PostgreSQL 全文）
SELECT id, conversation_id, content, created_at
FROM messages
WHERE conversation_id = $1
  AND to_tsvector('simple', content->>'text') @@ plainto_tsquery('simple', $2)
ORDER BY id DESC
LIMIT 20;
```

### 4.4 PostgreSQL 优化配置

```ini
# postgresql.conf

# 连接
max_connections = 200

# 内存
shared_buffers = 4GB              # 总内存 25%
effective_cache_size = 12GB       # 总内存 75%
work_mem = 64MB
maintenance_work_mem = 1GB

# WAL
wal_buffers = 64MB
max_wal_size = 4GB
synchronous_commit = off          # 异步提交，提升性能

# 并发
max_parallel_workers_per_gather = 4
max_parallel_workers = 8

# SSD 优化
random_page_cost = 1.1
effective_io_concurrency = 200
```

---

## 五、消息队列设计

### 5.1 PostgreSQL 队列 vs Kafka

|维度|PostgreSQL SKIP LOCKED|Kafka|
|---|---|---|
|部署复杂度|零（复用现有 PG）|需要独立集群|
|吞吐量|3,000-5,000 msg/s|100 万+ msg/s|
|适用规模|< 10 万用户|> 10 万用户|
|消息顺序|全局有序|分区有序|
|持久化|可靠|可靠|

### 5.2 队列接口抽象

```go
// 队列接口
type MessageQueue interface {
    Publish(ctx context.Context, topic string, payload []byte) error
    Subscribe(ctx context.Context, topic string, handler MessageHandler) error
    Pull(ctx context.Context, topic string, batchSize int) ([]Message, error)
    Ack(ctx context.Context, msgIDs []int64) error
    Nack(ctx context.Context, msgIDs []int64) error
    Close() error
}

type Message struct {
    ID        int64
    Topic     string
    Payload   []byte
    CreatedAt time.Time
}

type MessageHandler func(context.Context, Message) error
```

### 5.3 PostgreSQL 队列实现

```go
type PGQueue struct {
    db         *pgxpool.Pool
    consumerID string
}

// 发布消息
func (q *PGQueue) Publish(ctx context.Context, topic string, payload []byte) error {
    _, err := q.db.Exec(ctx, `
        INSERT INTO message_queue (topic, payload) VALUES ($1, $2)
    `, topic, payload)
    if err != nil {
        return err
    }
    // 通知消费者
    _, err = q.db.Exec(ctx, "SELECT pg_notify($1, '')", "mq_"+topic)
    return err
}

// 拉取消息（原子操作）
func (q *PGQueue) Pull(ctx context.Context, topic string, batchSize int) ([]Message, error) {
    rows, err := q.db.Query(ctx, `
        WITH selected AS (
            SELECT id FROM message_queue
            WHERE topic = $1 AND status = 0
            ORDER BY id LIMIT $2
            FOR UPDATE SKIP LOCKED
        )
        UPDATE message_queue
        SET status = 1, locked_at = now(), locked_by = $3
        WHERE id IN (SELECT id FROM selected)
        RETURNING id, topic, payload, created_at
    `, topic, batchSize, q.consumerID)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var messages []Message
    for rows.Next() {
        var m Message
        rows.Scan(&m.ID, &m.Topic, &m.Payload, &m.CreatedAt)
        messages = append(messages, m)
    }
    return messages, nil
}

// 确认消费
func (q *PGQueue) Ack(ctx context.Context, msgIDs []int64) error {
    _, err := q.db.Exec(ctx, `
        UPDATE message_queue SET status = 2 WHERE id = ANY($1)
    `, msgIDs)
    return err
}

// 订阅模式
func (q *PGQueue) Subscribe(ctx context.Context, topic string, handler MessageHandler) error {
    for {
        select {
        case <-ctx.Done():
            return ctx.Err()
        default:
        }

        messages, err := q.Pull(ctx, topic, 100)
        if err != nil {
            time.Sleep(time.Second)
            continue
        }

        if len(messages) == 0 {
            time.Sleep(100 * time.Millisecond)
            continue
        }

        for _, msg := range messages {
            if err := handler(ctx, msg); err != nil {
                q.Nack(ctx, []int64{msg.ID})
            } else {
                q.Ack(ctx, []int64{msg.ID})
            }
        }
    }
}
```

### 5.4 根据配置选择队列

```go
func NewQueue(cfg *config.QueueConfig, db *pgxpool.Pool) (MessageQueue, error) {
    switch cfg.Type {
    case "postgres":
        return NewPGQueue(db), nil
    case "kafka":
        return NewKafkaQueue(cfg.Kafka)
    default:
        return NewPGQueue(db), nil
    }
}
```

---

## 六、服务发现

### 6.1 Gossip vs etcd

|维度|Gossip (memberlist)|etcd|
|---|---|---|
|架构|去中心化|中心化集群|
|依赖|无，内嵌|独立服务|
|一致性|最终一致|强一致|
|功能|节点发现、元数据|KV、Watch、锁|
|运维|零|中等|

**推荐**：单人开发用 Gossip，零额外依赖。

### 6.2 Gossip 实现

```go
import "github.com/hashicorp/memberlist"

type GatewayMeta struct {
    NodeID       string `json:"node_id"`
    PublicAddr   string `json:"public_addr"`
    InternalAddr string `json:"internal_addr"`
    Connections  int    `json:"connections"`
}

type GatewayDelegate struct {
    meta GatewayMeta
    mu   sync.RWMutex
}

func (d *GatewayDelegate) NodeMeta(limit int) []byte {
    d.mu.RLock()
    defer d.mu.RUnlock()
    data, _ := json.Marshal(d.meta)
    return data
}

func (d *GatewayDelegate) NotifyMsg(msg []byte)                           {}
func (d *GatewayDelegate) GetBroadcasts(overhead, limit int) [][]byte     { return nil }
func (d *GatewayDelegate) LocalState(join bool) []byte                    { return nil }
func (d *GatewayDelegate) MergeRemoteState(buf []byte, join bool)         {}

// 启动 Gossip 节点
func NewGossipNode(cfg *config.GossipConfig) (*memberlist.Memberlist, error) {
    delegate := &GatewayDelegate{
        meta: GatewayMeta{
            NodeID:       cfg.NodeID,
            PublicAddr:   cfg.PublicAddr,
            InternalAddr: cfg.InternalAddr,
        },
    }

    mlConfig := memberlist.DefaultLANConfig()
    mlConfig.Name = cfg.NodeID
    mlConfig.BindAddr = cfg.BindAddr
    mlConfig.BindPort = cfg.BindPort
    mlConfig.Delegate = delegate

    list, err := memberlist.Create(mlConfig)
    if err != nil {
        return nil, err
    }

    if len(cfg.Seeds) > 0 {
        list.Join(cfg.Seeds)
    }

    return list, nil
}

// 获取所有网关节点
func GetGatewayNodes(list *memberlist.Memberlist) []GatewayMeta {
    var nodes []GatewayMeta
    for _, member := range list.Members() {
        if member.State == memberlist.StateAlive {
            var meta GatewayMeta
            json.Unmarshal(member.Meta, &meta)
            nodes = append(nodes, meta)
        }
    }
    return nodes
}
```

---

## 七、核心流程

### 7.1 消息发送完整流程

```
┌────────────────────────────────────────────────────────────────────────┐
│                        消息发送完整流程                                  │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  1. 客户端                                                             │
│     └──▶ WebSocket 发送 (Protobuf 编码)                                │
│                                                                        │
│  2. 网关层                                                              │
│     ├─ Protobuf 解码                                                   │
│     ├─ Token 鉴权                                                      │
│     ├─ 频率限制检查                                                     │
│     └─ 调用消息服务                                                     │
│                                                                        │
│  3. 消息服务                                                            │
│     ├─ 生成雪花 ID                                                     │
│     ├─ 写入 PostgreSQL (事务)                                          │
│     │   ├─ INSERT messages                                            │
│     │   ├─ UPDATE user_sync_state (小群)                              │
│     │   ├─ UPDATE conversation_sync_state (大群)                      │
│     │   └─ UPDATE user_conversations                                  │
│     ├─ 写入消息队列                                                     │
│     └─ 返回 ACK (消息ID + 服务器时间)                                   │
│                                                                        │
│  4. 推送 Worker                                                        │
│     ├─ 消费队列                                                        │
│     ├─ 获取会话成员                                                     │
│     ├─ 遍历成员:                                                        │
│     │   ├─ 查 Valkey 在线状态                                          │
│     │   ├─ 在线 → gRPC 推送到 Gateway                                  │
│     │   └─ 离线 → Push 通知 (APNs/FCM)                                 │
│     └─ ACK 确认消费                                                    │
│                                                                        │
│  5. 网关推送                                                            │
│     ├─ 查找用户连接                                                     │
│     ├─ Protobuf 编码                                                   │
│     └─ WebSocket 发送                                                  │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

### 7.2 用户上线同步流程

```
┌────────────────────────────────────────────────────────────────────────┐
│                        用户上线同步流程                                  │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  1. 建立连接                                                           │
│     ├─ WebSocket 握手                                                  │
│     ├─ Token 验证                                                      │
│     └─ 注册到 Valkey (user_id → gateway_node)                          │
│                                                                        │
│  2. 增量同步请求                                                        │
│     └─ 客户端发送 local_sync_seq                                        │
│                                                                        │
│  3. 服务端处理                                                          │
│     ├─ 查询 server_sync_seq                                            │
│     ├─ 差值过大 → 建议全量同步                                          │
│     ├─ 查询 version > local_seq 的会话                                  │
│     └─ 返回变更会话列表 + 大群列表                                       │
│                                                                        │
│  4. 客户端拉取                                                          │
│     ├─ 对每个变更会话拉取新消息                                          │
│     ├─ 检查大群 seq 变化                                                │
│     └─ 更新本地 sync_seq                                               │
│                                                                        │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 八、核心代码实现

### 8.1 雪花 ID 生成器

```go
type Snowflake struct {
    mu        sync.Mutex
    epoch     int64  // 自定义起始时间戳
    machineID int64  // 机器 ID (0-1023)
    sequence  int64  // 序列号 (0-4095)
    lastTime  int64
}

func NewSnowflake(machineID int64) *Snowflake {
    return &Snowflake{
        epoch:     1704067200000, // 2024-01-01 00:00:00 UTC
        machineID: machineID,
    }
}

func (s *Snowflake) Generate() int64 {
    s.mu.Lock()
    defer s.mu.Unlock()

    now := time.Now().UnixMilli() - s.epoch

    if now == s.lastTime {
        s.sequence = (s.sequence + 1) & 0xFFF
        if s.sequence == 0 {
            for now <= s.lastTime {
                now = time.Now().UnixMilli() - s.epoch
            }
        }
    } else {
        s.sequence = 0
    }

    s.lastTime = now

    // 41bit 时间戳 | 10bit 机器ID | 12bit 序列号
    return (now << 22) | (s.machineID << 12) | s.sequence
}
```

### 8.2 消息服务

```go
type MessageService struct {
    db        *pgxpool.Pool
    queue     MessageQueue
    cache     *Cache
    snowflake *Snowflake
}

func (s *MessageService) SendMessage(ctx context.Context, req *SendMessageRequest) (*Message, error) {
    // 获取会话信息
    var isLarge bool
    s.db.QueryRow(ctx, `
        SELECT is_large FROM conversation_sync_state WHERE conversation_id = $1
    `, req.ConversationID).Scan(&isLarge)

    members, _ := s.getConversationMembers(ctx, req.ConversationID)

    msgID := s.snowflake.Generate()
    msg := &Message{
        ID:             msgID,
        ConversationID: req.ConversationID,
        SenderID:       req.SenderID,
        ContentType:    req.ContentType,
        Content:        req.Content,
        CreatedAt:      time.Now(),
    }

    tx, _ := s.db.Begin(ctx)
    defer tx.Rollback(ctx)

    // 插入消息
    tx.Exec(ctx, `
        INSERT INTO messages (id, conversation_id, sender_id, content_type, content)
        VALUES ($1, $2, $3, $4, $5)
    `, msg.ID, msg.ConversationID, msg.SenderID, msg.ContentType, msg.Content)

    if isLarge {
        // 大群：只更新会话 seq
        tx.Exec(ctx, `
            UPDATE conversation_sync_state 
            SET seq = seq + 1, last_msg_id = $1
            WHERE conversation_id = $2
        `, msgID, req.ConversationID)

        tx.Exec(ctx, `
            UPDATE user_conversations
            SET last_msg_id = $1, last_msg_at = now(),
                unread_count = CASE WHEN user_id = $2 THEN 0 ELSE unread_count + 1 END
            WHERE conversation_id = $3
        `, msgID, req.SenderID, req.ConversationID)
    } else {
        // 小群：更新每个成员的 sync_seq
        for _, memberID := range members {
            var newSeq int64
            tx.QueryRow(ctx, `
                UPDATE user_sync_state 
                SET sync_seq = sync_seq + 1
                WHERE user_id = $1
                RETURNING sync_seq
            `, memberID).Scan(&newSeq)

            tx.Exec(ctx, `
                UPDATE user_conversations
                SET last_msg_id = $1, last_msg_at = now(), version = $2,
                    unread_count = CASE WHEN user_id = $3 THEN 0 ELSE unread_count + 1 END
                WHERE user_id = $4 AND conversation_id = $5
            `, msgID, newSeq, req.SenderID, memberID, req.ConversationID)
        }
    }

    tx.Commit(ctx)

    // 发布到队列
    payload, _ := json.Marshal(msg)
    s.queue.Publish(ctx, "messages", payload)

    return msg, nil
}
```

### 8.3 同步服务

```go
type SyncService struct {
    db    *pgxpool.Pool
    cache *Cache
}

type SyncRequest struct {
    UserID         int64           `json:"user_id"`
    LocalSyncSeq   int64           `json:"local_sync_seq"`
    LargeGroupSeqs map[int64]int64 `json:"large_group_seqs"`
}

type SyncResponse struct {
    ServerSyncSeq        int64              `json:"server_sync_seq"`
    ChangedConversations []ConversationSync `json:"changed_conversations"`
    LargeGroupChanges    []LargeGroupSync   `json:"large_group_changes"`
    FullSyncRequired     bool               `json:"full_sync_required"`
}

func (s *SyncService) Sync(ctx context.Context, req *SyncRequest) (*SyncResponse, error) {
    resp := &SyncResponse{}

    // 获取服务端 sync_seq
    var serverSeq int64
    s.db.QueryRow(ctx, `
        SELECT sync_seq FROM user_sync_state WHERE user_id = $1
    `, req.UserID).Scan(&serverSeq)
    resp.ServerSyncSeq = serverSeq

    // 离线太久，建议全量
    if serverSeq-req.LocalSyncSeq > 10000 {
        resp.FullSyncRequired = true
        return resp, nil
    }

    // 查询有变更的普通会话
    rows, _ := s.db.Query(ctx, `
        SELECT uc.conversation_id, uc.last_msg_id, uc.last_msg_at, uc.unread_count
        FROM user_conversations uc
        JOIN conversation_sync_state cs ON cs.conversation_id = uc.conversation_id
        WHERE uc.user_id = $1 AND uc.version > $2 AND cs.is_large = false
    `, req.UserID, req.LocalSyncSeq)
    defer rows.Close()

    for rows.Next() {
        var conv ConversationSync
        rows.Scan(&conv.ConversationID, &conv.LastMsgID, &conv.LastMsgAt, &conv.UnreadCount)
        resp.ChangedConversations = append(resp.ChangedConversations, conv)
    }

    // 检查大群变更
    largeRows, _ := s.db.Query(ctx, `
        SELECT uc.conversation_id, cs.seq
        FROM user_conversations uc
        JOIN conversation_sync_state cs ON cs.conversation_id = uc.conversation_id
        WHERE uc.user_id = $1 AND cs.is_large = true
    `, req.UserID)
    defer largeRows.Close()

    for largeRows.Next() {
        var convID, serverConvSeq int64
        largeRows.Scan(&convID, &serverConvSeq)

        if localSeq := req.LargeGroupSeqs[convID]; serverConvSeq > localSeq {
            resp.LargeGroupChanges = append(resp.LargeGroupChanges, LargeGroupSync{
                ConversationID: convID,
                ServerSeq:      serverConvSeq,
                HasNewMessages: true,
            })
        }
    }

    return resp, nil
}
```

### 8.4 网关连接管理

```go
type Gateway struct {
    connections sync.Map // map[int64]*Connection
    upgrader    websocket.Upgrader
    gossip      *memberlist.Memberlist
    cache       *Cache
}

func (g *Gateway) HandleWebSocket(w http.ResponseWriter, r *http.Request) {
    userID, err := auth.ValidateToken(r.Header.Get("Authorization"))
    if err != nil {
        http.Error(w, "Unauthorized", 401)
        return
    }

    conn, err := g.upgrader.Upgrade(w, r, nil)
    if err != nil {
        return
    }

    c := &Connection{
        userID: userID,
        conn:   conn,
        send:   make(chan []byte, 256),
    }

    g.connections.Store(userID, c)

    // 注册到 Valkey
    g.cache.SetUserSession(r.Context(), userID, &Session{
        GatewayNode: g.gossip.LocalNode().Name,
        Device:      r.Header.Get("X-Device"),
        ConnectedAt: time.Now(),
    })

    go c.writePump()
    go c.readPump(g)
}

// gRPC 接口：接收其他网关的推送请求
func (g *Gateway) PushMessage(ctx context.Context, req *pb.PushRequest) (*pb.PushResponse, error) {
    if c, ok := g.connections.Load(req.UserID); ok {
        conn := c.(*Connection)
        conn.send <- req.Data
        return &pb.PushResponse{Success: true}, nil
    }
    return &pb.PushResponse{Success: false}, nil
}
```

### 8.5 推送 Worker

```go
type PushWorker struct {
    queue    MessageQueue
    cache    *Cache
    gossip   *memberlist.Memberlist
    grpcPool *grpcpool.Pool
}

func (w *PushWorker) Start(ctx context.Context) error {
    return w.queue.Subscribe(ctx, "messages", w.handleMessage)
}

func (w *PushWorker) handleMessage(ctx context.Context, queueMsg Message) error {
    var msg ChatMessage
    json.Unmarshal(queueMsg.Payload, &msg)

    members, _ := w.cache.GetConversationMembers(ctx, msg.ConversationID)

    for _, memberID := range members {
        if memberID == msg.SenderID {
            continue
        }

        session, err := w.cache.GetUserSession(ctx, memberID)
        if err != nil || session == nil {
            // 离线，发 Push
            w.sendPushNotification(ctx, memberID, &msg)
            continue
        }

        // 在线，推送到网关
        gateway := w.findGatewayByNode(session.GatewayNode)
        if gateway == nil {
            continue
        }

        conn := w.grpcPool.Get(gateway.InternalAddr)
        client := pb.NewGatewayClient(conn)
        client.PushMessage(ctx, &pb.PushRequest{
            UserID: memberID,
            Data:   msg.ToProto(),
        })
    }

    return nil
}
```

---

## 九、项目结构

```
im-server/
├── cmd/
│   ├── gateway/                 # 网关服务入口
│   │   └── main.go
│   ├── api/                     # HTTP API 服务入口
│   │   └── main.go
│   └── worker/                  # 后台任务入口
│       └── main.go
│
├── internal/
│   ├── gateway/                 # 网关核心
│   │   ├── server.go            # WebSocket/TCP 服务器
│   │   ├── connection.go        # 连接管理
│   │   ├── codec.go             # Protobuf 编解码
│   │   └── handler.go           # 消息处理
│   │
│   ├── service/                 # 业务逻辑
│   │   ├── message.go
│   │   ├── sync.go
│   │   ├── conversation.go
│   │   ├── user.go
│   │   ├── group.go
│   │   ├── friend.go
│   │   └── bot.go
│   │
│   ├── repository/              # 数据访问
│   │   ├── message.go
│   │   ├── conversation.go
│   │   ├── user.go
│   │   └── cache.go
│   │
│   ├── queue/                   # 消息队列
│   │   ├── interface.go
│   │   ├── postgres.go
│   │   └── kafka.go
│   │
│   ├── discovery/               # 服务发现
│   │   └── gossip.go
│   │
│   └── pkg/                     # 工具包
│       ├── snowflake/
│       ├── protocol/            # Protobuf 生成代码
│       └── auth/
│
├── api/
│   └── proto/                   # Protobuf 定义
│       ├── message.proto
│       ├── conversation.proto
│       └── gateway.proto
│
├── migrations/                  # 数据库迁移
│   ├── 001_init.sql
│   └── 002_add_queue.sql
│
├── configs/
│   └── config.yaml
│
├── deployments/
│   ├── docker-compose.yaml
│   └── k8s/
│
├── Dockerfile
├── Makefile
└── README.md
```

---

## 十、部署方案

### 10.1 开发环境 (Docker Compose)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_USER: im
      POSTGRES_PASSWORD: im_password
      POSTGRES_DB: im
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  valkey:
    image: valkey/valkey:7
    ports:
      - "6379:6379"
    volumes:
      - valkey_data:/data

  meilisearch:
    image: getmeili/meilisearch:v1.6
    ports:
      - "7700:7700"
    volumes:
      - meilisearch_data:/meili_data

  gateway:
    build: .
    command: ./gateway
    ports:
      - "8080:8080"
      - "7946:7946"  # Gossip
    environment:
      - DB_URL=postgres://im:im_password@postgres:5432/im
      - VALKEY_URL=valkey:6379
    depends_on:
      - postgres
      - valkey

  worker:
    build: .
    command: ./worker
    environment:
      - DB_URL=postgres://im:im_password@postgres:5432/im
      - VALKEY_URL=valkey:6379
    depends_on:
      - postgres
      - valkey

volumes:
  postgres_data:
  valkey_data:
  meilisearch_data:
```

### 10.2 生产环境 (10 万用户)

```
┌─────────────────────────────────────────────────────┐
│                   生产部署架构                       │
├─────────────────────────────────────────────────────┤
│                                                     │
│   ┌───────────────────────────────────────────┐     │
│   │             负载均衡 (Nginx/Caddy)         │     │
│   │             WebSocket Sticky Session       │     │
│   └───────────────────────────────────────────┘     │
│                        │                            │
│          ┌─────────────┼─────────────┐              │
│          ▼             ▼             ▼              │
│   ┌───────────┐ ┌───────────┐ ┌───────────┐        │
│   │ Gateway 1 │ │ Gateway 2 │ │ Gateway 3 │        │
│   │  4C8G     │ │  4C8G     │ │  4C8G     │        │
│   │ 3万连接   │ │ 3万连接   │ │ 3万连接   │        │
│   └─────┬─────┘ └─────┬─────┘ └─────┬─────┘        │
│         │             │             │               │
│         └─────────────┼─────────────┘               │
│                       │ Gossip                      │
│          ┌────────────┼────────────┐                │
│          ▼            ▼            ▼                │
│   ┌───────────┐ ┌───────────┐ ┌───────────┐        │
│   │ Worker 1  │ │ Worker 2  │ │  API 服务  │        │
│   │  4C8G     │ │  4C8G     │ │  4C8G     │        │
│   └───────────┘ └───────────┘ └───────────┘        │
│                       │                             │
│          ┌────────────┴────────────┐                │
│          ▼                         ▼                │
│   ┌─────────────────┐     ┌──────────────┐         │
│   │   PostgreSQL    │     │    Valkey    │         │
│   │   8C32G SSD     │     │    4C16G     │         │
│   │   主 + 从 + 备   │     │   主 + 从    │         │
│   └─────────────────┘     └──────────────┘         │
│                                                     │
│   服务器配置：                                       │
│   • Gateway × 3: 4C8G                              │
│   • Worker × 2: 4C8G                               │
│   • API × 1: 4C8G                                  │
│   • PostgreSQL: 8C32G SSD (主从)                   │
│   • Valkey: 4C16G (主从)                           │
│                                                     │
│   预估成本：~$500-800/月 (云服务器)                  │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### 10.3 Nginx 配置

```nginx
upstream im_gateway {
    ip_hash;  # WebSocket sticky session
    server gateway1:8080;
    server gateway2:8080;
    server gateway3:8080;
}

server {
    listen 443 ssl http2;
    server_name im.example.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;

    location /ws {
        proxy_pass http://im_gateway;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_read_timeout 3600s;
        proxy_send_timeout 3600s;
    }

    location /api {
        proxy_pass http://im_gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 十一、扩展路径

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          演进路线                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Phase 1: MVP (0-6 月)                                                  │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • Go 单体服务                                                    │   │
│  │ • PostgreSQL + Valkey                                           │   │
│  │ • 单机部署                                                       │   │
│  │ • 支持 1 万用户                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                 │                                       │
│                                 ▼                                       │
│  Phase 2: 分布式 (6-12 月)                                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • Gateway 集群 + Gossip 服务发现                                 │   │
│  │ • Worker 分离                                                    │   │
│  │ • PostgreSQL 分区                                                │   │
│  │ • 支持 10 万用户                                                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                 │                                       │
│                                 ▼                                       │
│  Phase 3: 规模化 (12-24 月)                                             │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • PG 队列 → Kafka                                                │   │
│  │ • PG 搜索 → Meilisearch/Elasticsearch                           │   │
│  │ • PostgreSQL → TiDB (可选)                                       │   │
│  │ • 支持 100 万用户                                                │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                 │                                       │
│                                 ▼                                       │
│  Phase 4: 全球化 (24+ 月)                                               │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • 多区域部署                                                     │   │
│  │ • 边缘接入节点                                                   │   │
│  │ • 跨区数据同步                                                   │   │
│  │ • 支持亿级用户                                                   │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 十二、功能特性清单

### 12.1 消息核心功能

|特性|优先级|说明|
|---|---|---|
|实时消息转发|P0|消息即时送达|
|已读回执|P1|显示消息是否已读|
|投递回执|P1|确认消息已送达设备|
|消息引用/回复|P1|引用某条消息进行回复|
|消息编辑|P2|发送后可修改消息内容|
|消息撤回|P1|撤回已发送的消息|
|消息转发|P2|将消息转发到其他会话|
|@提及|P1|在群聊中 @特定用户|
|消息 Reaction|P2|对消息添加表情反应|
|正在输入指示器|P2|显示对方正在输入|
|消息搜索|P1|全文搜索历史消息|

### 12.2 消息类型

|类型|优先级|content_type|
|---|---|---|
|文本|P0|1|
|图片|P0|2|
|文件|P1|3|
|语音|P1|4|
|位置|P2|5|
|链接预览|P2|6|
|名片|P2|7|

### 12.3 群组功能

|特性|优先级|
|---|---|
|创建/解散群|P0|
|群成员管理|P0|
|群角色权限|P1|
|群公告|P2|
|入群审批|P2|
|邀请链接|P2|

### 12.4 扩展功能

|特性|优先级|
|---|---|
|Bot 框架|P1|
|Webhook 集成|P1|
|自动回复|P2|
|消息审核/过滤|P2|

---

## 附录

### A. Protobuf 定义示例

```protobuf
syntax = "proto3";
package im.v1;

message Envelope {
    int64 seq = 1;
    int64 timestamp = 2;
    oneof payload {
        SendMessageReq send_msg = 10;
        SendMessageAck send_ack = 11;
        RecvMessage recv_msg = 12;
        HeartbeatReq heartbeat = 20;
        HeartbeatResp heartbeat_resp = 21;
        SyncReq sync_req = 30;
        SyncResp sync_resp = 31;
    }
}

message SendMessageReq {
    int64 conversation_id = 1;
    int64 client_msg_id = 2;
    int32 content_type = 3;
    bytes content = 4;
    repeated int64 mentions = 5;
}

message SendMessageAck {
    int64 client_msg_id = 1;
    int64 server_msg_id = 2;
    int64 server_time = 3;
}

message RecvMessage {
    int64 id = 1;
    int64 conversation_id = 2;
    int64 sender_id = 3;
    int32 content_type = 4;
    bytes content = 5;
    int64 created_at = 6;
}

message SyncReq {
    int64 sync_seq = 1;
    map<int64, int64> large_group_seqs = 2;
}

message SyncResp {
    int64 server_sync_seq = 1;
    repeated ConversationChange changes = 2;
    repeated LargeGroupChange large_groups = 3;
    bool full_sync_required = 4;
}
```

### B. 配置文件示例

```yaml
# config.yaml

server:
  http_port: 8080
  grpc_port: 50051
  ws_path: /ws

database:
  host: localhost
  port: 5432
  user: im
  password: im_password
  database: im
  max_conns: 100

cache:
  host: localhost
  port: 6379
  password: ""
  db: 0

queue:
  type: postgres  # postgres | kafka
  kafka:
    brokers:
      - localhost:9092

gossip:
  node_id: gateway-1
  bind_addr: 0.0.0.0
  bind_port: 7946
  seeds:
    - gateway-2:7946
    - gateway-3:7946

log:
  level: info
  format: json
```

---

> 文档版本: 1.0  
> 最后更新: 2024-01





# 现代 IM 系统架构设计文档

> **目标：构建一个支持十万+ TPS、可无限水平扩展、单人可开发维护的即时通讯系统**

---

## 目录

1. [架构总览](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%B8%80%E6%9E%B6%E6%9E%84%E6%80%BB%E8%A7%88)
2. [技术选型](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%BA%8C%E6%8A%80%E6%9C%AF%E9%80%89%E5%9E%8B)
3. [消息同步机制](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%B8%89%E6%B6%88%E6%81%AF%E5%90%8C%E6%AD%A5%E6%9C%BA%E5%88%B6)
4. [实时推送与消息队列](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%9B%9B%E5%AE%9E%E6%97%B6%E6%8E%A8%E9%80%81%E4%B8%8E%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97)
5. [流式消息设计](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%BA%94%E6%B5%81%E5%BC%8F%E6%B6%88%E6%81%AF%E8%AE%BE%E8%AE%A1)
6. [数据库设计](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%85%AD%E6%95%B0%E6%8D%AE%E5%BA%93%E8%AE%BE%E8%AE%A1)
7. [服务发现](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%B8%83%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0)
8. [核心流程](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%85%AB%E6%A0%B8%E5%BF%83%E6%B5%81%E7%A8%8B)
9. [核心代码实现](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E4%B9%9D%E6%A0%B8%E5%BF%83%E4%BB%A3%E7%A0%81%E5%AE%9E%E7%8E%B0)
10. [项目结构](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%8D%81%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84)
11. [部署与容量规划](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%8D%81%E4%B8%80%E9%83%A8%E7%BD%B2%E4%B8%8E%E5%AE%B9%E9%87%8F%E8%A7%84%E5%88%92)
12. [扩展路径](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%8D%81%E4%BA%8C%E6%89%A9%E5%B1%95%E8%B7%AF%E5%BE%84)
13. [功能特性清单](https://claude.ai/chat/3c0f10f1-61dc-41c3-a96e-34e2059e8121#%E5%8D%81%E4%B8%89%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7%E6%B8%85%E5%8D%95)

---

## 一、架构总览

### 1.1 设计目标

|指标|目标|
|---|---|
|**吞吐量**|十万+ TPS（消息发送+推送）|
|**延迟**|实时推送 P99 < 10ms|
|**在线连接**|百万级长连接|
|**可扩展性**|无限水平扩展|
|**可维护性**|单人可开发维护|

### 1.2 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        IM 系统架构（十万+ TPS）                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                                 客户端                                       │
│                   iOS / Android / Web / Desktop / Bot                       │
│                                    │                                        │
│                                    ▼                                        │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                          负载均衡 (L4)                                 │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                    │                                        │
│                  ┌─────────────────┼─────────────────┐                      │
│                  ▼                 ▼                 ▼                      │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                        Gateway 集群                                    │ │
│  │           WebSocket/TCP + Protobuf │ 连接管理 │ 鉴权                   │ │
│  │   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐       │ │
│  │   │Gateway 1 │    │Gateway 2 │    │Gateway 3 │    │Gateway N │       │ │
│  │   └────┬─────┘    └────┬─────┘    └────┬─────┘    └────┬─────┘       │ │
│  │        │               │               │               │              │ │
│  │        └───────────────┴───────────────┴───────────────┘              │ │
│  │                                │                                       │ │
│  └────────────────────────────────┼───────────────────────────────────────┘ │
│                                   │                                         │
│                                   ▼                                         │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                        NATS Cluster                                    │ │
│  │                    （实时推送总线，延迟 < 1ms）                          │ │
│  │                                                                        │ │
│  │    所有实时推送统一走这里：普通消息、流式消息、输入状态、在线状态...       │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                   │                                         │
│                                   ▼                                         │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                          业务服务层                                    │ │
│  │    ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │ │
│  │    │ 消息服务  │ │ 用户服务  │ │ 群组服务  │ │ Bot 服务 │ │Webhook   │  │ │
│  │    └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                   │                                         │
│                 ┌─────────────────┼─────────────────┐                       │
│                 ▼                 ▼                 ▼                       │
│  ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐         │
│  │    PostgreSQL     │ │   Kafka Cluster   │ │      Valkey       │         │
│  │     (持久化)      │ │    (异步任务)      │ │      (缓存)       │         │
│  │   消息+元数据     │ │  索引/审计/统计    │ │  会话/在线状态    │         │
│  └───────────────────┘ └─────────┬─────────┘ └───────────────────┘         │
│                                  │                                          │
│                                  ▼                                          │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                         Worker 集群                                    │ │
│  │        索引 │ 离线推送 │ Webhook │ 审计 │ 统计 │ 归档 │ ...            │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                    服务发现: Gossip (memberlist)                       │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 核心设计原则

|原则|说明|
|---|---|
|**职责分离**|NATS 负责实时推送，Kafka 负责异步任务，互不干扰|
|**统一通道**|普通消息和流式消息走完全相同的推送路径|
|**水平扩展**|所有组件无状态或支持集群，可无限扩展|
|**简洁优先**|组件最少化，单人可开发维护|

---

## 二、技术选型

### 2.1 最终选型

|层级|选择|职责|性能|
|---|---|---|---|
|**语言**|Go|全栈开发|-|
|**通信协议**|WebSocket + Protobuf|客户端通信|省带宽 50%+|
|**实时推送**|NATS|Gateway 间实时通信|千万级 msg/s|
|**消息队列**|Kafka|异步任务处理|百万级 msg/s|
|**主数据库**|PostgreSQL|消息+元数据持久化|5万+ TPS|
|**缓存**|Valkey|会话/在线状态|百万级 QPS|
|**搜索**|PostgreSQL → Meilisearch|消息全文搜索|按需升级|
|**服务发现**|Gossip (memberlist)|节点发现|零依赖|

### 2.2 为什么选 Go

```go
// 一个连接一个 goroutine，代码直观
func handleConnection(conn net.Conn) {
    for {
        msg, err := readMessage(conn)
        if err != nil {
            return
        }
        go processMessage(msg) // 轻松并发
    }
}
// 单机可支撑 10 万+ 长连接
```

|对比项|Go|Java|Rust|Node.js|
|---|---|---|---|---|
|并发模型|Goroutine (轻量)|Thread (重)|async (复杂)|Event Loop|
|编译速度|快|慢|慢|无需编译|
|部署|单二进制|JVM 依赖|单二进制|Node 运行时|
|内存占用|低|高|最低|中|
|开发效率|高|中|低|高|

### 2.3 NATS vs 其他实时通道

|维度|NATS|Redis Pub/Sub|gRPC 直连|
|---|---|---|---|
|性能|千万级 msg/s|百万级|取决于连接数|
|延迟|< 1ms|1-5ms|< 1ms|
|集群|原生支持|有限制|N*(N-1) 连接|
|可靠性|高|不保证投递|高|
|部署|单二进制|复用现有|无额外组件|
|适合规模|任意|< 10 节点|< 50 节点|

**选择 NATS 的理由**：

- Go 原生实现，单二进制部署
- 单节点千万级消息吞吐
- 内置集群，运维简单
- 延迟 P99 < 1ms

### 2.4 Kafka vs PostgreSQL 队列

|维度|Kafka|PostgreSQL SKIP LOCKED|
|---|---|---|
|吞吐量|百万级 msg/s|5000-10000 msg/s|
|延迟|毫秒级|毫秒级|
|持久化|优秀|优秀|
|运维复杂度|中等|零（复用 PG）|
|适合规模|十万+ TPS|< 1 万 TPS|

**十万+ TPS 必须用 Kafka**，PostgreSQL 队列作为初期简化方案。

### 2.5 Valkey vs Dragonfly

|维度|Valkey|Dragonfly|
|---|---|---|
|Redis 兼容|99.9%|95%+|
|单节点性能|~10 万 QPS|~100 万 QPS|
|多线程|否|是|
|社区成熟度|高 (Linux Foundation)|中等|

**建议**：初期 Valkey，追求极致性能可切换 Dragonfly（协议兼容）。

---

## 三、消息同步机制

### 3.1 方案对比

|方案|原理|优点|缺点|
|---|---|---|---|
|**用户维度 Seq** (OpenIM)|每用户一个递增 seq，写入 inbox|同步简单|大群写扩散严重|
|**会话维度存储**|消息按会话存储|无写扩散|同步逻辑复杂|
|**混合方案** (本文)|用户 seq + 会话 seq|兼顾两者|实现稍复杂|

### 3.2 混合方案设计

```
┌──────────────────────────────────────────────────────────────────────┐
│                    混合同步方案                                       │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  1. user_sync_state（每用户一条，判断"有无新消息"）                    │
│  ┌──────────┬──────────┬─────────────────┐                          │
│  │ user_id  │ sync_seq │ updated_at      │                          │
│  ├──────────┼──────────┼─────────────────┤                          │
│  │ Alice    │ 1523     │ 2024-01-15 10:30│                          │
│  └──────────┴──────────┴─────────────────┘                          │
│                                                                      │
│  2. user_conversations（version 标记会话何时变更）                    │
│  ┌──────────┬─────────────────┬─────────┬───────────────┐           │
│  │ user_id  │ conversation_id │ version │ last_msg_id   │           │
│  ├──────────┼─────────────────┼─────────┼───────────────┤           │
│  │ Alice    │ conv_A          │ 1520    │ 789012345...  │           │
│  │ Alice    │ conv_B          │ 1523    │ 789012346...  │           │
│  └──────────┴─────────────────┴─────────┴───────────────┘           │
│                                                                      │
│  version 含义：该会话最后变更时，用户的 sync_seq 值                   │
│  → 查询 version > local_sync_seq 即可找出有新消息的会话              │
│                                                                      │
│  3. conversation_sync_state（大群专用，避免写扩散）                   │
│  ┌─────────────────┬─────────┬──────────┐                           │
│  │ conversation_id │ seq     │ is_large │                           │
│  ├─────────────────┼─────────┼──────────┤                           │
│  │ big_group_1     │ 5823    │ true     │                           │
│  └─────────────────┴─────────┴──────────┘                           │
│                                                                      │
│  大群（> 200 人）消息只更新会话 seq，不更新每个用户的 sync_seq         │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.3 同步流程

```
客户端                                    服务端
   │                                        │
   │  ① sync_seq=1500                       │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ② server_seq=1523                     │
   │     changed: [conv_A, conv_B]          │  ← version > 1500
   │     large_groups: [big_group_1]        │  ← 需单独检查
   │ ◀───────────────────────────────────   │
   │                                        │
   │  ③ 拉取 conv_A 消息 (id > local)       │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ④ 返回消息列表                         │
   │ ◀───────────────────────────────────   │
   │                                        │
   │  ⑤ 检查 big_group_1 的 seq             │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ⑥ 更新本地 sync_seq=1523              │
```

### 3.4 发消息写入逻辑

```sql
-- 【小群/单聊】更新每个成员的 sync_seq
BEGIN;
INSERT INTO messages (...) VALUES (...);

UPDATE user_sync_state 
SET sync_seq = sync_seq + 1 
WHERE user_id = ANY($members);

UPDATE user_conversations 
SET version = (SELECT sync_seq FROM user_sync_state WHERE user_id = user_conversations.user_id),
    last_msg_id = $msg_id,
    unread_count = CASE WHEN user_id = $sender THEN 0 ELSE unread_count + 1 END
WHERE conversation_id = $conv_id;
COMMIT;

-- 【大群】只更新会话 seq，O(1) 写入
BEGIN;
INSERT INTO messages (...) VALUES (...);

UPDATE conversation_sync_state 
SET seq = seq + 1, last_msg_id = $msg_id 
WHERE conversation_id = $conv_id;

UPDATE user_conversations 
SET last_msg_id = $msg_id, 
    unread_count = CASE WHEN user_id = $sender THEN 0 ELSE unread_count + 1 END
WHERE conversation_id = $conv_id;
COMMIT;
```

---

## 四、实时推送与消息队列

### 4.1 核心问题澄清

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    常见误区 vs 正确设计                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ❌ 错误设计：消息队列作为实时推送通道                                    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  用户发消息 → Kafka → 消费者 → 推送                               │   │
│  │                                                                  │   │
│  │  问题：                                                          │   │
│  │  • 延迟高（10-100ms）                                            │   │
│  │  • 流式消息怎么办？每个 token 都发 Kafka？                        │   │
│  │  • 职责混乱                                                      │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ✅ 正确设计：分离实时推送和异步任务                                      │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │  NATS（实时推送）          Kafka（异步任务）                       │   │
│  │  ┌───────────────────┐    ┌───────────────────┐                 │   │
│  │  │ • 普通消息推送      │    │ • 消息索引         │                 │   │
│  │  │ • 流式消息推送      │    │ • 离线消息处理     │                 │   │
│  │  │ • 输入状态          │    │ • 审计日志         │                 │   │
│  │  │ • 在线状态          │    │ • Webhook 触发    │                 │   │
│  │  │ • 已读回执          │    │ • 统计分析         │                 │   │
│  │  │                     │    │ • 消息归档         │                 │   │
│  │  │ 延迟 < 1ms          │    │ 延迟不敏感         │                 │   │
│  │  └───────────────────┘    └───────────────────┘                 │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 4.2 统一的消息流转

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    统一消息流程                                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  无论普通消息还是流式消息，都走完全相同的路径：                            │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │   业务服务（消息服务 / Bot 服务）                                  │   │
│  │        │                                                         │   │
│  │        ├────────────────────────────┐                            │   │
│  │        │                            │                            │   │
│  │        ▼                            ▼                            │   │
│  │   ┌─────────┐                 ┌─────────┐                        │   │
│  │   │  NATS   │                 │  Kafka  │                        │   │
│  │   │ 实时推送 │                 │ 异步任务 │                        │   │
│  │   └────┬────┘                 └────┬────┘                        │   │
│  │        │                           │                             │   │
│  │        ▼                           ▼                             │   │
│  │   ┌─────────┐                 ┌─────────┐                        │   │
│  │   │ Gateway │                 │ Workers │                        │   │
│  │   │  集群   │                 │  集群   │                        │   │
│  │   └────┬────┘                 └─────────┘                        │   │
│  │        │                                                         │   │
│  │        ▼                                                         │   │
│  │     用户                                                         │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  关键点：                                                                │
│  • 实时推送统一走 NATS                                                  │
│  • Kafka 只做异步任务，不参与实时推送                                    │
│  • 流式消息和普通消息无差别对待                                          │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 4.3 NATS Subject 设计

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    NATS Subject 设计                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Subject 模式                   用途                                    │
│  ─────────────────────────────────────────────────────────────────────  │
│  im.user.{user_id}             推送给特定用户                           │
│  im.conv.{conv_id}             推送给会话所有在线成员                    │
│  im.gateway.{node_id}          推送给特定网关节点                       │
│  im.broadcast                  全局广播（系统公告等）                    │
│                                                                         │
│  示例：                                                                 │
│  • 单聊消息：发布到 im.conv.{conv_id}                                   │
│  • 群聊消息：发布到 im.conv.{conv_id}                                   │
│  • 流式消息：发布到 im.conv.{conv_id}（多次，每个 delta 一次）           │
│  • 踢人下线：发布到 im.user.{user_id}                                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 五、流式消息设计

### 5.1 流式消息协议

```protobuf
// 流式消息开始
message StreamMessageStart {
    int64 message_id = 1;           // 服务端生成的消息 ID
    int64 conversation_id = 2;
    int64 sender_id = 3;            // Bot ID
    int64 reply_to_id = 4;          // 回复的用户消息 ID
    int64 timestamp = 5;
}

// 流式消息增量
message StreamMessageDelta {
    int64 message_id = 1;
    string delta = 2;               // 增量内容
    int32 index = 3;                // 序号（处理乱序）
}

// 流式消息结束
message StreamMessageEnd {
    int64 message_id = 1;
    string full_content = 2;        // 完整内容（可选）
    int32 total_tokens = 3;
    bool success = 4;
    string error = 5;
}
```

### 5.2 流式消息完整流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    流式消息流程                                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1. 用户发送 "@Bot 帮我写个故事"                                         │
│     │                                                                   │
│     ▼                                                                   │
│  2. Gateway → 消息服务                                                   │
│     │                                                                   │
│     ├─ 存储用户消息                                                      │
│     ├─ 发布用户消息到 NATS（其他人看到）                                  │
│     ├─ 发布到 Kafka（异步任务）                                          │
│     └─ 调用 Bot 服务                                                    │
│                                                                         │
│  3. Bot 服务处理                                                         │
│     │                                                                   │
│     ├─ 生成消息 ID（雪花算法）                                           │
│     │                                                                   │
│     ├─ 发布 StreamStart 到 NATS ─────────────────▶ Gateway → 用户       │
│     │   im.conv.{conv_id}                                               │
│     │                                                                   │
│     ├─ 调用 LLM API（流式）                                              │
│     │   │                                                               │
│     │   ├─ 收到 token_1                                                 │
│     │   │   └─ 发布 StreamDelta 到 NATS ─────────▶ Gateway → 用户       │
│     │   │                                                               │
│     │   ├─ 收到 token_2                                                 │
│     │   │   └─ 发布 StreamDelta 到 NATS ─────────▶ Gateway → 用户       │
│     │   │                                                               │
│     │   └─ ... 重复 ...                                                 │
│     │                                                                   │
│     ├─ 发布 StreamEnd 到 NATS ───────────────────▶ Gateway → 用户       │
│     │                                                                   │
│     ├─ 写入完整消息到 PostgreSQL                                         │
│     │                                                                   │
│     └─ 发布到 Kafka（索引、审计等异步任务）                                │
│                                                                         │
│                                                                         │
│  关键点：                                                                │
│  • 所有流式内容通过 NATS 实时推送                                        │
│  • 和普通消息走完全相同的 NATS 通道                                       │
│  • 完成后才写数据库和 Kafka                                              │
│  • 离线用户收到的是最终完整消息                                           │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.3 客户端处理流式消息

```typescript
class StreamMessageHandler {
    private pendingMessages: Map<bigint, StreamingMessage> = new Map();
    
    handleMessage(envelope: Envelope) {
        if (envelope.streamStart) {
            this.handleStreamStart(envelope.streamStart);
        } else if (envelope.streamDelta) {
            this.handleStreamDelta(envelope.streamDelta);
        } else if (envelope.streamEnd) {
            this.handleStreamEnd(envelope.streamEnd);
        }
    }
    
    private handleStreamStart(msg: StreamMessageStart) {
        const streamingMsg: StreamingMessage = {
            id: msg.messageId,
            conversationId: msg.conversationId,
            senderId: msg.senderId,
            content: '',
            status: 'streaming',
            chunks: [],
        };
        this.pendingMessages.set(msg.messageId, streamingMsg);
        this.emit('messageStart', streamingMsg);
    }
    
    private handleStreamDelta(msg: StreamMessageDelta) {
        const streamingMsg = this.pendingMessages.get(msg.messageId);
        if (!streamingMsg) return;
        
        // 按 index 排序插入（处理乱序）
        streamingMsg.chunks[msg.index] = msg.delta;
        streamingMsg.content = streamingMsg.chunks.join('');
        
        // 节流更新 UI
        this.emitThrottled('messageDelta', streamingMsg);
    }
    
    private handleStreamEnd(msg: StreamMessageEnd) {
        const streamingMsg = this.pendingMessages.get(msg.messageId);
        if (!streamingMsg) return;
        
        streamingMsg.content = msg.fullContent || streamingMsg.content;
        streamingMsg.status = msg.success ? 'completed' : 'failed';
        
        this.pendingMessages.delete(msg.messageId);
        this.emit('messageEnd', streamingMsg);
    }
}
```

---

## 六、数据库设计

### 6.1 核心表结构

```sql
-- ============================================
-- 用户相关
-- ============================================

CREATE TABLE users (
    id          bigint PRIMARY KEY,           -- 雪花 ID
    username    varchar(64) UNIQUE NOT NULL,
    nickname    varchar(64),
    avatar_url  varchar(512),
    status      smallint DEFAULT 1,
    created_at  timestamptz DEFAULT now(),
    updated_at  timestamptz DEFAULT now()
);

-- ============================================
-- 同步状态
-- ============================================

CREATE TABLE user_sync_state (
    user_id     bigint PRIMARY KEY,
    sync_seq    bigint DEFAULT 0,
    updated_at  timestamptz DEFAULT now()
);

CREATE TABLE conversation_sync_state (
    conversation_id bigint PRIMARY KEY,
    seq             bigint DEFAULT 0,
    last_msg_id     bigint,
    is_large        boolean DEFAULT false,    -- > 200 人标记为大群
    updated_at      timestamptz DEFAULT now()
);

-- ============================================
-- 会话相关
-- ============================================

CREATE TABLE conversations (
    id          bigint PRIMARY KEY,
    type        smallint NOT NULL,            -- 1:单聊 2:群聊 3:频道
    name        varchar(128),
    avatar_url  varchar(512),
    owner_id    bigint,
    settings    jsonb DEFAULT '{}',
    created_at  timestamptz DEFAULT now(),
    updated_at  timestamptz DEFAULT now()
);

CREATE TABLE conversation_members (
    conversation_id bigint NOT NULL,
    user_id         bigint NOT NULL,
    role            smallint DEFAULT 0,
    nickname        varchar(64),
    is_muted        boolean DEFAULT false,
    joined_at       timestamptz DEFAULT now(),
    PRIMARY KEY (conversation_id, user_id)
);

CREATE TABLE user_conversations (
    user_id           bigint NOT NULL,
    conversation_id   bigint NOT NULL,
    version           bigint DEFAULT 0,
    conv_acked_seq    bigint DEFAULT 0,       -- 大群用
    last_read_msg_id  bigint DEFAULT 0,
    last_msg_id       bigint DEFAULT 0,
    last_msg_at       timestamptz,
    unread_count      int DEFAULT 0,
    is_pinned         boolean DEFAULT false,
    is_muted          boolean DEFAULT false,
    is_deleted        boolean DEFAULT false,
    updated_at        timestamptz DEFAULT now(),
    PRIMARY KEY (user_id, conversation_id)
);

CREATE INDEX idx_user_conv_version ON user_conversations (user_id, version);

-- ============================================
-- 消息（分区）
-- ============================================

CREATE TABLE messages (
    id              bigint NOT NULL,
    conversation_id bigint NOT NULL,
    sender_id       bigint NOT NULL,
    content_type    smallint NOT NULL,
    content         jsonb NOT NULL,
    reply_to_id     bigint,
    mentions        bigint[],
    status          smallint DEFAULT 1,
    created_at      timestamptz DEFAULT now(),
    PRIMARY KEY (conversation_id, id)
) PARTITION BY HASH (conversation_id);

-- 创建 16 个分区
DO $$ BEGIN
    FOR i IN 0..15 LOOP
        EXECUTE format(
            'CREATE TABLE messages_p%s PARTITION OF messages 
             FOR VALUES WITH (MODULUS 16, REMAINDER %s)', i, i);
    END LOOP;
END $$;

-- ============================================
-- 好友与黑名单
-- ============================================

CREATE TABLE friendships (
    user_id     bigint NOT NULL,
    friend_id   bigint NOT NULL,
    status      smallint DEFAULT 0,
    remark      varchar(64),
    created_at  timestamptz DEFAULT now(),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE blocks (
    user_id       bigint NOT NULL,
    blocked_id    bigint NOT NULL,
    created_at    timestamptz DEFAULT now(),
    PRIMARY KEY (user_id, blocked_id)
);
```

### 6.2 消息内容 JSON 结构

```go
type MessageContent struct {
    Text        string           `json:"text,omitempty"`
    Image       *ImageContent    `json:"image,omitempty"`
    File        *FileContent     `json:"file,omitempty"`
    Voice       *VoiceContent    `json:"voice,omitempty"`
    Location    *LocationContent `json:"location,omitempty"`
    LinkPreview *LinkPreview     `json:"link_preview,omitempty"`
}

type ImageContent struct {
    URL       string `json:"url"`
    Thumbnail string `json:"thumbnail"`
    Width     int    `json:"width"`
    Height    int    `json:"height"`
    Size      int64  `json:"size"`
}
```

### 6.3 PostgreSQL 优化配置

```ini
# postgresql.conf (16C64G 服务器)

max_connections = 500
shared_buffers = 16GB
effective_cache_size = 48GB
work_mem = 128MB
maintenance_work_mem = 2GB

wal_buffers = 64MB
max_wal_size = 8GB
synchronous_commit = off          # 异步提交

max_parallel_workers_per_gather = 4
max_parallel_workers = 8

random_page_cost = 1.1            # SSD
effective_io_concurrency = 200
```

---

## 七、服务发现

### 7.1 Gossip 实现

```go
import "github.com/hashicorp/memberlist"

type GatewayMeta struct {
    NodeID       string `json:"node_id"`
    PublicAddr   string `json:"public_addr"`
    InternalAddr string `json:"internal_addr"`
    Connections  int    `json:"connections"`
}

type GatewayDelegate struct {
    meta GatewayMeta
    mu   sync.RWMutex
}

func (d *GatewayDelegate) NodeMeta(limit int) []byte {
    d.mu.RLock()
    defer d.mu.RUnlock()
    data, _ := json.Marshal(d.meta)
    return data
}

func (d *GatewayDelegate) NotifyMsg(msg []byte)                       {}
func (d *GatewayDelegate) GetBroadcasts(overhead, limit int) [][]byte { return nil }
func (d *GatewayDelegate) LocalState(join bool) []byte                { return nil }
func (d *GatewayDelegate) MergeRemoteState(buf []byte, join bool)     {}

func NewGossipNode(cfg *GossipConfig) (*memberlist.Memberlist, error) {
    delegate := &GatewayDelegate{
        meta: GatewayMeta{
            NodeID:       cfg.NodeID,
            PublicAddr:   cfg.PublicAddr,
            InternalAddr: cfg.InternalAddr,
        },
    }

    mlConfig := memberlist.DefaultLANConfig()
    mlConfig.Name = cfg.NodeID
    mlConfig.BindAddr = cfg.BindAddr
    mlConfig.BindPort = cfg.BindPort
    mlConfig.Delegate = delegate

    list, err := memberlist.Create(mlConfig)
    if err != nil {
        return nil, err
    }

    if len(cfg.Seeds) > 0 {
        list.Join(cfg.Seeds)
    }

    return list, nil
}
```

---

## 八、核心流程

### 8.1 消息发送完整流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    消息发送完整流程                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1. 客户端                                                              │
│     └─▶ WebSocket 发送 (Protobuf)                                       │
│                                                                         │
│  2. Gateway                                                             │
│     ├─ Protobuf 解码                                                    │
│     ├─ Token 鉴权                                                       │
│     ├─ 频率限制                                                         │
│     └─ 转发到消息服务                                                    │
│                                                                         │
│  3. 消息服务                                                             │
│     ├─ 生成雪花 ID                                                      │
│     ├─ 写入 PostgreSQL                                                  │
│     ├─ 发布到 NATS (im.conv.{conv_id}) ─────────▶ 实时推送               │
│     ├─ 发布到 Kafka ────────────────────────────▶ 异步任务               │
│     └─ 返回 ACK                                                         │
│                                                                         │
│  4. Gateway 集群（通过 NATS 订阅）                                        │
│     ├─ 收到 NATS 消息                                                   │
│     ├─ 查找本地在线用户                                                  │
│     └─ WebSocket 推送                                                   │
│                                                                         │
│  5. Kafka Workers（异步）                                                │
│     ├─ 索引到 Meilisearch                                               │
│     ├─ 离线用户 Push 通知                                                │
│     ├─ Webhook 触发                                                     │
│     └─ 审计日志                                                         │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 8.2 用户上线同步流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    用户上线同步流程                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1. 建立连接                                                            │
│     ├─ WebSocket 握手                                                   │
│     ├─ Token 验证                                                       │
│     ├─ 注册到 Valkey (user_id → gateway_node)                           │
│     └─ 订阅 NATS (im.user.{user_id} + 用户的会话列表)                    │
│                                                                         │
│  2. 增量同步                                                            │
│     ├─ 客户端发送 local_sync_seq                                        │
│     ├─ 服务端返回 changed conversations (version > local_seq)           │
│     └─ 客户端逐个拉取新消息                                              │
│                                                                         │
│  3. 实时接收                                                            │
│     └─ 通过 NATS 订阅自动接收新消息                                      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 九、核心代码实现

### 9.1 雪花 ID 生成器

```go
type Snowflake struct {
    mu        sync.Mutex
    epoch     int64
    machineID int64
    sequence  int64
    lastTime  int64
}

func NewSnowflake(machineID int64) *Snowflake {
    return &Snowflake{
        epoch:     1704067200000, // 2024-01-01
        machineID: machineID,
    }
}

func (s *Snowflake) Generate() int64 {
    s.mu.Lock()
    defer s.mu.Unlock()

    now := time.Now().UnixMilli() - s.epoch
    if now == s.lastTime {
        s.sequence = (s.sequence + 1) & 0xFFF
        if s.sequence == 0 {
            for now <= s.lastTime {
                now = time.Now().UnixMilli() - s.epoch
            }
        }
    } else {
        s.sequence = 0
    }
    s.lastTime = now

    return (now << 22) | (s.machineID << 12) | s.sequence
}
```

### 9.2 NATS 实时总线

```go
type RealtimeBus struct {
    nc     *nats.Conn
    nodeID string
}

func NewRealtimeBus(url, nodeID string) (*RealtimeBus, error) {
    nc, err := nats.Connect(url,
        nats.ReconnectWait(time.Second),
        nats.MaxReconnects(-1),
    )
    if err != nil {
        return nil, err
    }
    return &RealtimeBus{nc: nc, nodeID: nodeID}, nil
}

// 发布到会话
func (b *RealtimeBus) PublishToConversation(convID int64, msg proto.Message) error {
    data, _ := proto.Marshal(msg)
    return b.nc.Publish(fmt.Sprintf("im.conv.%d", convID), data)
}

// 发布到用户
func (b *RealtimeBus) PublishToUser(userID int64, msg proto.Message) error {
    data, _ := proto.Marshal(msg)
    return b.nc.Publish(fmt.Sprintf("im.user.%d", userID), data)
}

// 订阅会话
func (b *RealtimeBus) SubscribeConversation(convID int64, handler func([]byte)) (*nats.Subscription, error) {
    return b.nc.Subscribe(fmt.Sprintf("im.conv.%d", convID), func(msg *nats.Msg) {
        handler(msg.Data)
    })
}

// 订阅用户
func (b *RealtimeBus) SubscribeUser(userID int64, handler func([]byte)) (*nats.Subscription, error) {
    return b.nc.Subscribe(fmt.Sprintf("im.user.%d", userID), func(msg *nats.Msg) {
        handler(msg.Data)
    })
}
```

### 9.3 消息服务

```go
type MessageService struct {
    db        *pgxpool.Pool
    bus       *RealtimeBus      // NATS
    queue     *kafka.Producer   // Kafka
    snowflake *Snowflake
}

func (s *MessageService) SendMessage(ctx context.Context, req *SendMessageRequest) (*Message, error) {
    msg := &Message{
        ID:             s.snowflake.Generate(),
        ConversationID: req.ConversationID,
        SenderID:       req.SenderID,
        ContentType:    req.ContentType,
        Content:        req.Content,
        CreatedAt:      time.Now(),
    }

    // 1. 持久化
    if err := s.saveMessage(ctx, msg); err != nil {
        return nil, err
    }

    // 2. 实时推送（NATS）
    pushMsg := &pb.RecvMessage{
        Id:             msg.ID,
        ConversationId: msg.ConversationID,
        SenderId:       msg.SenderID,
        ContentType:    int32(msg.ContentType),
        Content:        msg.Content.ToBytes(),
        CreatedAt:      msg.CreatedAt.UnixMilli(),
    }
    s.bus.PublishToConversation(msg.ConversationID, pushMsg)

    // 3. 异步任务（Kafka）
    s.queue.Produce(&kafka.Message{
        Topic: "message-events",
        Key:   []byte(fmt.Sprintf("%d", msg.ConversationID)),
        Value: msg.ToJSON(),
    })

    return msg, nil
}
```

### 9.4 Bot 服务（流式）

```go
type BotService struct {
    bus       *RealtimeBus
    queue     *kafka.Producer
    llmClient *llm.Client
    snowflake *Snowflake
}

func (s *BotService) HandleMention(ctx context.Context, userMsg *Message) error {
    botMsgID := s.snowflake.Generate()
    convID := userMsg.ConversationID

    // 1. StreamStart
    s.bus.PublishToConversation(convID, &pb.StreamMessageStart{
        MessageId:      botMsgID,
        ConversationId: convID,
        SenderId:       botUserID,
        Timestamp:      time.Now().UnixMilli(),
    })

    // 2. 流式调用 LLM
    var fullContent strings.Builder
    stream, _ := s.llmClient.ChatStream(ctx, buildPrompt(userMsg))

    index := 0
    for chunk := range stream {
        fullContent.WriteString(chunk.Delta)
        index++

        // StreamDelta - 和普通消息走相同的 NATS 通道
        s.bus.PublishToConversation(convID, &pb.StreamMessageDelta{
            MessageId: botMsgID,
            Delta:     chunk.Delta,
            Index:     int32(index),
        })
    }

    // 3. StreamEnd
    s.bus.PublishToConversation(convID, &pb.StreamMessageEnd{
        MessageId:   botMsgID,
        FullContent: fullContent.String(),
        Success:     true,
    })

    // 4. 持久化
    msg := &Message{
        ID:          botMsgID,
        ConversationID: convID,
        SenderID:    botUserID,
        ContentType: ContentTypeText,
        Content:     MessageContent{Text: fullContent.String()},
    }
    s.saveMessage(ctx, msg)

    // 5. 异步任务
    s.queue.Produce(&kafka.Message{
        Topic: "message-events",
        Value: msg.ToJSON(),
    })

    return nil
}
```

### 9.5 Gateway 连接管理

```go
type Gateway struct {
    connections sync.Map  // userID -> *Connection
    bus         *RealtimeBus
    subs        sync.Map  // key -> *nats.Subscription
}

func (g *Gateway) OnUserConnected(userID int64, conn *Connection) {
    g.connections.Store(userID, conn)

    // 订阅用户消息
    sub, _ := g.bus.SubscribeUser(userID, func(data []byte) {
        conn.Send(data)
    })
    g.subs.Store(fmt.Sprintf("user:%d", userID), sub)

    // 订阅用户的会话
    convIDs := g.getUserConversations(userID)
    for _, convID := range convIDs {
        sub, _ := g.bus.SubscribeConversation(convID, func(data []byte) {
            if c, ok := g.connections.Load(userID); ok {
                c.(*Connection).Send(data)
            }
        })
        g.subs.Store(fmt.Sprintf("conv:%d:%d", userID, convID), sub)
    }
}

func (g *Gateway) OnUserDisconnected(userID int64) {
    g.connections.Delete(userID)

    // 取消所有订阅
    g.subs.Range(func(key, value interface{}) bool {
        if strings.HasPrefix(key.(string), fmt.Sprintf("user:%d", userID)) ||
           strings.HasPrefix(key.(string), fmt.Sprintf("conv:%d:", userID)) {
            value.(*nats.Subscription).Unsubscribe()
            g.subs.Delete(key)
        }
        return true
    })
}
```

### 9.6 同步服务

```go
type SyncService struct {
    db    *pgxpool.Pool
    cache *Cache
}

type SyncRequest struct {
    UserID         int64           `json:"user_id"`
    LocalSyncSeq   int64           `json:"local_sync_seq"`
    LargeGroupSeqs map[int64]int64 `json:"large_group_seqs"`
}

type SyncResponse struct {
    ServerSyncSeq        int64              `json:"server_sync_seq"`
    ChangedConversations []ConversationSync `json:"changed_conversations"`
    LargeGroupChanges    []LargeGroupSync   `json:"large_group_changes"`
    FullSyncRequired     bool               `json:"full_sync_required"`
}

func (s *SyncService) Sync(ctx context.Context, req *SyncRequest) (*SyncResponse, error) {
    resp := &SyncResponse{}

    // 获取服务端 sync_seq
    var serverSeq int64
    s.db.QueryRow(ctx, `
        SELECT sync_seq FROM user_sync_state WHERE user_id = $1
    `, req.UserID).Scan(&serverSeq)
    resp.ServerSyncSeq = serverSeq

    // 离线太久，建议全量
    if serverSeq-req.LocalSyncSeq > 10000 {
        resp.FullSyncRequired = true
        return resp, nil
    }

    // 查询变更的普通会话
    rows, _ := s.db.Query(ctx, `
        SELECT uc.conversation_id, uc.last_msg_id, uc.last_msg_at, uc.unread_count
        FROM user_conversations uc
        JOIN conversation_sync_state cs ON cs.conversation_id = uc.conversation_id
        WHERE uc.user_id = $1 AND uc.version > $2 AND cs.is_large = false
    `, req.UserID, req.LocalSyncSeq)
    defer rows.Close()

    for rows.Next() {
        var conv ConversationSync
        rows.Scan(&conv.ConversationID, &conv.LastMsgID, &conv.LastMsgAt, &conv.UnreadCount)
        resp.ChangedConversations = append(resp.ChangedConversations, conv)
    }

    // 检查大群变更
    largeRows, _ := s.db.Query(ctx, `
        SELECT uc.conversation_id, cs.seq
        FROM user_conversations uc
        JOIN conversation_sync_state cs ON cs.conversation_id = uc.conversation_id
        WHERE uc.user_id = $1 AND cs.is_large = true
    `, req.UserID)
    defer largeRows.Close()

    for largeRows.Next() {
        var convID, serverConvSeq int64
        largeRows.Scan(&convID, &serverConvSeq)

        if localSeq := req.LargeGroupSeqs[convID]; serverConvSeq > localSeq {
            resp.LargeGroupChanges = append(resp.LargeGroupChanges, LargeGroupSync{
                ConversationID: convID,
                ServerSeq:      serverConvSeq,
                HasNewMessages: true,
            })
        }
    }

    return resp, nil
}
```

---

## 十、项目结构

```
im-server/
├── cmd/
│   ├── gateway/                 # 网关服务
│   │   └── main.go
│   ├── api/                     # HTTP API
│   │   └── main.go
│   ├── bot/                     # Bot 服务
│   │   └── main.go
│   └── worker/                  # 后台任务
│       └── main.go
│
├── internal/
│   ├── gateway/
│   │   ├── server.go
│   │   ├── connection.go
│   │   └── handler.go
│   │
│   ├── service/
│   │   ├── message.go
│   │   ├── sync.go
│   │   ├── conversation.go
│   │   ├── user.go
│   │   └── bot.go
│   │
│   ├── repository/
│   │   ├── message.go
│   │   ├── conversation.go
│   │   └── cache.go
│   │
│   ├── bus/                     # NATS 封装
│   │   └── realtime.go
│   │
│   ├── queue/                   # Kafka 封装
│   │   └── producer.go
│   │
│   ├── discovery/
│   │   └── gossip.go
│   │
│   └── pkg/
│       ├── snowflake/
│       ├── protocol/            # Protobuf
│       └── auth/
│
├── api/proto/
│   ├── message.proto
│   ├── stream.proto
│   └── gateway.proto
│
├── migrations/
│   └── 001_init.sql
│
├── configs/
│   └── config.yaml
│
├── deployments/
│   ├── docker-compose.yaml
│   └── k8s/
│
├── Dockerfile
├── Makefile
└── README.md
```

---

## 十一、部署与容量规划

### 11.1 十万+ TPS 容量规划

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    十万+ TPS 容量规划                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  组件性能基准：                                                          │
│  ┌────────────────────────────────────────────────────────────────┐    │
│  │ NATS       : 单节点 1000 万+ msg/s，延迟 < 1ms                   │    │
│  │ Kafka      : 单集群 100 万+ msg/s                                │    │
│  │ PostgreSQL : 5 万+ TPS (优化后)                                  │    │
│  │ Valkey     : 单节点 10 万+ QPS，集群百万级                        │    │
│  └────────────────────────────────────────────────────────────────┘    │
│                                                                         │
│  硬件规划（十万 TPS + 百万连接）：                                        │
│  ┌────────────────┬─────────┬───────────┬─────────────────────────┐    │
│  │ 组件           │ 数量    │ 配置       │ 说明                    │    │
│  ├────────────────┼─────────┼───────────┼─────────────────────────┤    │
│  │ Gateway        │ 20      │ 8C16G     │ 每节点 5 万连接          │    │
│  │ NATS           │ 3       │ 4C8G      │ 集群，支撑实时推送        │    │
│  │ 消息服务       │ 10      │ 8C16G     │ 无状态，按需扩容          │    │
│  │ Bot 服务       │ 10      │ 8C16G     │ 按 LLM 调用量            │    │
│  │ API 服务       │ 5       │ 4C8G      │ HTTP API                │    │
│  │ Kafka          │ 5       │ 16C64G    │ 3 副本                  │    │
│  │ PostgreSQL     │ 3       │ 32C128G   │ 主 + 从 + 备             │    │
│  │ Valkey         │ 6       │ 8C64G     │ 3 主 3 从               │    │
│  │ Worker         │ 20      │ 4C8G      │ 按任务类型分组           │    │
│  └────────────────┴─────────┴───────────┴─────────────────────────┘    │
│                                                                         │
│  预估成本：~$15,000-25,000/月 (云服务器)                                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 11.2 架构部署图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    生产部署架构（十万+ TPS）                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   ┌───────────────────────────────────────────────────────────────┐    │
│   │                    负载均衡 (L4)                               │    │
│   │              AWS NLB / 阿里云 SLB / Nginx                      │    │
│   └───────────────────────────────────────────────────────────────┘    │
│                               │                                         │
│             ┌─────────────────┼─────────────────┐                       │
│             ▼                 ▼                 ▼                       │
│   ┌─────────────────────────────────────────────────────────────────┐  │
│   │                    Gateway 集群 (20 节点)                        │  │
│   │         每节点 5 万连接，共 100 万并发连接能力                     │  │
│   └─────────────────────────────────────────────────────────────────┘  │
│                               │                                         │
│                               ▼                                         │
│   ┌─────────────────────────────────────────────────────────────────┐  │
│   │                    NATS Cluster (3 节点)                         │  │
│   │               实时推送总线，千万级 msg/s                          │  │
│   └─────────────────────────────────────────────────────────────────┘  │
│                               │                                         │
│             ┌─────────────────┼─────────────────┐                       │
│             ▼                 ▼                 ▼                       │
│   ┌───────────────┐ ┌───────────────┐ ┌───────────────┐                │
│   │  消息服务 x10  │ │  Bot服务 x10  │ │  API服务 x5   │                │
│   └───────────────┘ └───────────────┘ └───────────────┘                │
│                               │                                         │
│             ┌─────────────────┼─────────────────┐                       │
│             ▼                 ▼                 ▼                       │
│   ┌───────────────┐ ┌───────────────┐ ┌───────────────┐                │
│   │  PostgreSQL   │ │ Kafka Cluster │ │Valkey Cluster │                │
│   │   主从 + 备    │ │    5 节点     │ │   3主3从      │                │
│   └───────────────┘ └───────────────┘ └───────────────┘                │
│                               │                                         │
│                               ▼                                         │
│   ┌─────────────────────────────────────────────────────────────────┐  │
│   │                    Worker 集群 (20 节点)                         │  │
│   │       索引 │ 离线推送 │ Webhook │ 审计 │ 统计 │ 归档               │  │
│   └─────────────────────────────────────────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 11.3 Docker Compose（开发环境）

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_USER: im
      POSTGRES_PASSWORD: im_password
      POSTGRES_DB: im
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  valkey:
    image: valkey/valkey:7
    ports:
      - "6379:6379"

  nats:
    image: nats:2.10
    ports:
      - "4222:4222"
      - "8222:8222"
    command: ["--cluster", "nats://0.0.0.0:6222", "--http_port", "8222"]

  kafka:
    image: bitnami/kafka:3.6
    ports:
      - "9092:9092"
    environment:
      - KAFKA_CFG_NODE_ID=0
      - KAFKA_CFG_PROCESS_ROLES=controller,broker
      - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
      - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER

  gateway:
    build: .
    command: ./gateway
    ports:
      - "8080:8080"
    environment:
      - DB_URL=postgres://im:im_password@postgres:5432/im
      - VALKEY_URL=valkey:6379
      - NATS_URL=nats://nats:4222
      - KAFKA_BROKERS=kafka:9092
    depends_on:
      - postgres
      - valkey
      - nats
      - kafka

volumes:
  postgres_data:
```

### 11.4 配置文件

```yaml
# config.yaml

server:
  http_port: 8080
  grpc_port: 50051
  ws_path: /ws

database:
  host: localhost
  port: 5432
  user: im
  password: im_password
  database: im
  max_conns: 100

cache:
  host: localhost
  port: 6379

nats:
  url: nats://localhost:4222

kafka:
  brokers:
    - localhost:9092
  topics:
    messages: message-events

gossip:
  node_id: gateway-1
  bind_addr: 0.0.0.0
  bind_port: 7946
  seeds:
    - gateway-2:7946

log:
  level: info
  format: json
```

---

## 十二、扩展路径

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          演进路线                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Phase 1: MVP (0-6 月)                                                  │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • Go 单体服务                                                    │   │
│  │ • PostgreSQL + Valkey + NATS                                    │   │
│  │ • PostgreSQL 队列（代替 Kafka）                                  │   │
│  │ • 单机/少量节点部署                                               │   │
│  │ • 支持 1 万 TPS                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                 │                                       │
│                                 ▼                                       │
│  Phase 2: 分布式 (6-12 月)                                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • Gateway 集群 + Gossip                                          │   │
│  │ • 引入 Kafka 替换 PG 队列                                        │   │
│  │ • Worker 分离                                                    │   │
│  │ • PostgreSQL 主从 + 分区                                         │   │
│  │ • 支持 5 万 TPS                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                 │                                       │
│                                 ▼                                       │
│  Phase 3: 规模化 (12-24 月)                                             │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • 完整集群化部署                                                  │   │
│  │ • Meilisearch 消息搜索                                           │   │
│  │ • PostgreSQL → TiDB（可选）                                      │   │
│  │ • 多机房部署                                                      │   │
│  │ • 支持 十万+ TPS                                                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                 │                                       │
│                                 ▼                                       │
│  Phase 4: 全球化 (24+ 月)                                               │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • 多区域部署                                                      │   │
│  │ • 边缘接入节点                                                    │   │
│  │ • 跨区数据同步                                                    │   │
│  │ • 支持百万+ TPS                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 十三、功能特性清单

### 13.1 消息核心功能

|特性|优先级|说明|
|---|---|---|
|实时消息转发|P0|消息即时送达|
|流式消息|P0|支持 LLM Bot 流式输出|
|已读回执|P1|显示消息是否已读|
|投递回执|P1|确认消息已送达|
|消息引用/回复|P1|引用某条消息|
|消息撤回|P1|撤回已发送消息|
|消息编辑|P2|修改已发送消息|
|@提及|P1|群聊中 @用户|
|消息 Reaction|P2|表情反应|
|消息搜索|P1|全文搜索|
|正在输入|P2|输入状态指示|

### 13.2 消息类型

|类型|content_type|优先级|
|---|---|---|
|文本|1|P0|
|图片|2|P0|
|文件|3|P1|
|语音|4|P1|
|位置|5|P2|
|链接预览|6|P2|

### 13.3 群组功能

|特性|优先级|
|---|---|
|创建/解散群|P0|
|群成员管理|P0|
|群角色权限|P1|
|群公告|P2|
|大群支持 (万人)|P1|

### 13.4 扩展功能

|特性|优先级|
|---|---|
|Bot 框架|P1|
|Webhook 集成|P1|
|消息审核/过滤|P2|

---

## 附录

### A. Protobuf 定义

```protobuf
syntax = "proto3";
package im.v1;

// 通用消息信封
message Envelope {
    int64 seq = 1;
    int64 timestamp = 2;
    oneof payload {
        SendMessageReq send_msg = 10;
        SendMessageAck send_ack = 11;
        RecvMessage recv_msg = 12;
        HeartbeatReq heartbeat = 20;
        HeartbeatResp heartbeat_resp = 21;
        SyncReq sync_req = 30;
        SyncResp sync_resp = 31;
        StreamMessageStart stream_start = 40;
        StreamMessageDelta stream_delta = 41;
        StreamMessageEnd stream_end = 42;
    }
}

message SendMessageReq {
    int64 conversation_id = 1;
    int64 client_msg_id = 2;
    int32 content_type = 3;
    bytes content = 4;
    repeated int64 mentions = 5;
}

message SendMessageAck {
    int64 client_msg_id = 1;
    int64 server_msg_id = 2;
    int64 server_time = 3;
}

message RecvMessage {
    int64 id = 1;
    int64 conversation_id = 2;
    int64 sender_id = 3;
    int32 content_type = 4;
    bytes content = 5;
    int64 created_at = 6;
}

message StreamMessageStart {
    int64 message_id = 1;
    int64 conversation_id = 2;
    int64 sender_id = 3;
    int64 reply_to_id = 4;
    int64 timestamp = 5;
}

message StreamMessageDelta {
    int64 message_id = 1;
    string delta = 2;
    int32 index = 3;
}

message StreamMessageEnd {
    int64 message_id = 1;
    string full_content = 2;
    int32 total_tokens = 3;
    bool success = 4;
    string error = 5;
}

message SyncReq {
    int64 sync_seq = 1;
    map<int64, int64> large_group_seqs = 2;
}

message SyncResp {
    int64 server_sync_seq = 1;
    repeated ConversationChange changes = 2;
    repeated LargeGroupChange large_groups = 3;
    bool full_sync_required = 4;
}
```

### B. 方案对比总结

|维度|OpenIM|本方案|
|---|---|---|
|同步模型|用户 Inbox 写扩散|混合 (小群写扩散 + 大群读扩散)|
|实时推送|队列消费推送|NATS 直接推送|
|流式消息|特殊处理|与普通消息统一|
|大群写入|O(N)|O(1)|
|存储成本|高|低|
|推送延迟|10-100ms|< 1ms|
|组件数量|多|适中|

---

> 文档版本: 2.0  
> 目标: 十万+ TPS，可无限水平扩展  
> 最后更新: 2024-01


# 现代 IM 系统架构设计 - 最优解方案

> **目标：构建一个支持十万+ TPS、可无限水平扩展、单人可开发维护的即时通讯系统**
> 
> **整合 WebRTC 音视频通话 + 即时通讯完整架构**

---

## 目录

1. [架构总览与设计目标](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%B8%80%E6%9E%B6%E6%9E%84%E6%80%BB%E8%A7%88%E4%B8%8E%E8%AE%BE%E8%AE%A1%E7%9B%AE%E6%A0%87)
2. [技术选型与依据](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%BA%8C%E6%8A%80%E6%9C%AF%E9%80%89%E5%9E%8B%E4%B8%8E%E4%BE%9D%E6%8D%AE)
3. [消息同步机制（混合方案）](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%B8%89%E6%B6%88%E6%81%AF%E5%90%8C%E6%AD%A5%E6%9C%BA%E5%88%B6%E6%B7%B7%E5%90%88%E6%96%B9%E6%A1%88)
4. [实时推送与消息队列](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%9B%9B%E5%AE%9E%E6%97%B6%E6%8E%A8%E9%80%81%E4%B8%8E%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97)
5. [流式消息设计（LLM Bot）](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%BA%94%E6%B5%81%E5%BC%8F%E6%B6%88%E6%81%AF%E8%AE%BE%E8%AE%A1llm-bot)
6. [数据库设计与优化](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%85%AD%E6%95%B0%E6%8D%AE%E5%BA%93%E8%AE%BE%E8%AE%A1%E4%B8%8E%E4%BC%98%E5%8C%96)
7. [WebRTC 音视频架构](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%B8%83webrtc-%E9%9F%B3%E8%A7%86%E9%A2%91%E6%9E%B6%E6%9E%84)
8. [服务发现机制](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%85%AB%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0%E6%9C%BA%E5%88%B6)
9. [核心业务流程](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%B9%9D%E6%A0%B8%E5%BF%83%E4%B8%9A%E5%8A%A1%E6%B5%81%E7%A8%8B)
10. [核心代码实现](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E6%A0%B8%E5%BF%83%E4%BB%A3%E7%A0%81%E5%AE%9E%E7%8E%B0)
11. [项目结构](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E4%B8%80%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84)
12. [部署与容量规划](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E4%BA%8C%E9%83%A8%E7%BD%B2%E4%B8%8E%E5%AE%B9%E9%87%8F%E8%A7%84%E5%88%92)
13. [演进路线图](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E4%B8%89%E6%BC%94%E8%BF%9B%E8%B7%AF%E7%BA%BF%E5%9B%BE)
14. [完整功能特性清单](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E5%9B%9B%E5%AE%8C%E6%95%B4%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7%E6%B8%85%E5%8D%95)

---

## 一、架构总览与设计目标

### 1.1 设计目标

|指标|目标|说明|
|---|---|---|
|**吞吐量**|十万+ TPS|消息发送+推送合计|
|**延迟**|P99 < 10ms|实时推送延迟|
|**在线连接**|百万级|长连接数|
|**可扩展性**|无限水平扩展|所有组件支持集群|
|**可维护性**|单人可维护|组件最少化|

### 1.2 核心设计原则

- **职责分离**：NATS 负责实时推送，Kafka 负责异步任务，互不干扰
- **统一通道**：普通消息和流式消息走完全相同的推送路径
- **水平扩展**：所有组件无状态或支持集群，可无限扩展
- **简洁优先**：组件最少化，降低运维复杂度

### 1.3 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        IM 系统架构（十万+ TPS）                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                                 客户端                                       │
│                   iOS / Android / Web / Desktop / Bot                       │
│                                    │                                        │
│                                    ▼                                        │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                          负载均衡 (L4)                                 │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                    │                                        │
│                  ┌─────────────────┼─────────────────┐                      │
│                  ▼                 ▼                 ▼                      │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                        Gateway 集群                                    │ │
│  │           WebSocket/TCP + Protobuf │ 连接管理 │ 鉴权                   │ │
│  │   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐       │ │
│  │   │Gateway 1 │    │Gateway 2 │    │Gateway 3 │    │Gateway N │       │ │
│  │   └────┬─────┘    └────┬─────┘    └────┬─────┘    └────┬─────┘       │ │
│  │        └───────────────┴───────────────┴───────────────┘              │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│                                   │                                         │
│                                   ▼                                         │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                        NATS Cluster                                    │ │
│  │                    （实时推送总线，延迟 < 1ms）                          │ │
│  │    所有实时推送统一走这里：普通消息、流式消息、输入状态、在线状态...       │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                   │                                         │
│                                   ▼                                         │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                          业务服务层                                    │ │
│  │    ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │ │
│  │    │ 消息服务  │ │ 用户服务  │ │ 群组服务  │ │ Bot 服务 │ │Webhook   │  │ │
│  │    └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                   │                                         │
│                 ┌─────────────────┼─────────────────┐                       │
│                 ▼                 ▼                 ▼                       │
│  ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐         │
│  │    PostgreSQL     │ │   Kafka Cluster   │ │      Valkey       │         │
│  │     (持久化)      │ │    (异步任务)      │ │      (缓存)       │         │
│  │   消息+元数据     │ │  索引/审计/统计    │ │  会话/在线状态    │         │
│  └───────────────────┘ └─────────┬─────────┘ └───────────────────┘         │
│                                  │                                          │
│                                  ▼                                          │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                         Worker 集群                                    │ │
│  │        索引 │ 离线推送 │ Webhook │ 审计 │ 统计 │ 归档 │ ...            │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                    服务发现: Gossip (memberlist)                       │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、技术选型与依据

### 2.1 最终技术栈

| 层级       | 选择                   | 职责            | 性能        |
| -------- | -------------------- | ------------- | --------- |
| **语言**   | Go                   | 全栈开发          | -         |
| **通信协议** | WebSocket + Protobuf | 客户端通信         | 省带宽 50%+  |
| **实时推送** | NATS                 | Gateway 间实时通信 | 千万级 msg/s |
| **消息队列** | Kafka                | 异步任务处理        | 百万级 msg/s |
| **主数据库** | PostgreSQL           | 消息+元数据持久化     | 5万+ TPS   |
| **缓存**   | Valkey               | 会话/在线状态       | 百万级 QPS   |
| **搜索**   | Meilisearch          | 消息全文搜索        | 按需升级      |
| **服务发现** | Gossip (memberlist)  | 节点发现          | 零依赖       |

### 2.2 为什么选 Go

```go
// 一个连接一个 goroutine，代码直观
func handleConnection(conn net.Conn) {
    for {
        msg, err := readMessage(conn)
        if err != nil {
            return
        }
        go processMessage(msg) // 轻松并发
    }
}
// 单机可支撑 10 万+ 长连接
```

|对比项|Go|Java|Rust|Node.js|
|---|---|---|---|---|
|并发模型|Goroutine (轻量)|Thread (重)|async (复杂)|Event Loop|
|编译速度|快|慢|慢|无需编译|
|部署|单二进制|JVM 依赖|单二进制|Node 运行时|
|内存占用|低|高|最低|中|
|开发效率|高|中|低|高|

**选择 Go 的理由**：

- Goroutine 轻量并发：一个连接一个 goroutine，代码直观，单机可支撑 10 万+ 长连接
- 编译快速：秒级编译，快速迭代
- 单二进制部署：无运行时依赖，简化运维
- 内存占用低：相比 Java/JVM，内存效率更高
- 生态完善：NATS、Pion WebRTC 均为 Go 原生实现

### 2.3 NATS vs Redis Pub/Sub vs gRPC 直连

| 维度   | NATS      | Redis Pub/Sub | gRPC 直连    |
| ---- | --------- | ------------- | ---------- |
| 性能   | 千万级 msg/s | 百万级           | 取决于连接数     |
| 延迟   | < 1ms     | 1-5ms         | < 1ms      |
| 集群   | 原生支持      | 有限制           | N*(N-1) 连接 |
| 可靠性  | 高         | 不保证投递         | 高          |
| 部署   | 单二进制      | 复用现有          | 无额外组件      |
| 适合规模 | 任意        | < 10 节点       | < 50 节点    |

**选择 NATS 的理由**：

- Go 原生实现，单二进制部署
- 单节点千万级消息吞吐
- 内置集群，运维简单
- 延迟 P99 < 1ms

### 2.4 Kafka vs PostgreSQL 队列

|维度|Kafka|PostgreSQL SKIP LOCKED|
|---|---|---|
|吞吐量|百万级 msg/s|5000-10000 msg/s|
|延迟|毫秒级|毫秒级|
|持久化|优秀|优秀|
|运维复杂度|中等|零（复用 PG）|
|适合规模|十万+ TPS|< 1 万 TPS|

**结论**：十万+ TPS 必须用 Kafka，PostgreSQL 队列作为初期简化方案。

### 2.5 Valkey vs Dragonfly

|维度|Valkey|Dragonfly|
|---|---|---|
|Redis 兼容|99.9%|95%+|
|单节点性能|~10 万 QPS|~100 万 QPS|
|多线程|否|是|
|社区成熟度|高 (Linux Foundation)|中等|

**建议**：初期 Valkey，追求极致性能可切换 Dragonfly（协议兼容）。

---

## 三、消息同步机制（混合方案）

### 3.1 方案对比

|方案|原理|优点|缺点|
|---|---|---|---|
|**用户维度 Seq** (OpenIM)|每用户一个递增 seq，写入 inbox|同步简单|大群写扩散严重|
|**会话维度存储**|消息按会话存储|无写扩散|同步逻辑复杂|
|**混合方案** (本文)|用户 seq + 会话 seq|兼顾两者|实现稍复杂|

### 3.2 混合方案设计

**核心思路**：小群用写扩散（更新每个成员的 sync_seq），大群用读扩散（只更新会话 seq）。

```
┌──────────────────────────────────────────────────────────────────────┐
│                    混合同步方案                                       │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  1. user_sync_state（每用户一条，判断"有无新消息"）                    │
│  ┌──────────┬──────────┬─────────────────┐                          │
│  │ user_id  │ sync_seq │ updated_at      │                          │
│  ├──────────┼──────────┼─────────────────┤                          │
│  │ Alice    │ 1523     │ 2024-01-15 10:30│                          │
│  └──────────┴──────────┴─────────────────┘                          │
│                                                                      │
│  2. user_conversations（version 标记会话何时变更）                    │
│  ┌──────────┬─────────────────┬─────────┬───────────────┐           │
│  │ user_id  │ conversation_id │ version │ last_msg_id   │           │
│  ├──────────┼─────────────────┼─────────┼───────────────┤           │
│  │ Alice    │ conv_A          │ 1520    │ 789012345...  │           │
│  │ Alice    │ conv_B          │ 1523    │ 789012346...  │           │
│  └──────────┴─────────────────┴─────────┴───────────────┘           │
│                                                                      │
│  version 含义：该会话最后变更时，用户的 sync_seq 值                   │
│  → 查询 version > local_sync_seq 即可找出有新消息的会话              │
│                                                                      │
│  3. conversation_sync_state（大群专用，避免写扩散）                   │
│  ┌─────────────────┬─────────┬──────────┐                           │
│  │ conversation_id │ seq     │ is_large │                           │
│  ├─────────────────┼─────────┼──────────┤                           │
│  │ big_group_1     │ 5823    │ true     │                           │
│  └─────────────────┴─────────┴──────────┘                           │
│                                                                      │
│  大群（> 200 人）消息只更新会话 seq，不更新每个用户的 sync_seq         │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.3 同步流程

```
客户端                                    服务端
   │                                        │
   │  ① sync_seq=1500                       │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ② server_seq=1523                     │
   │     changed: [conv_A, conv_B]          │  ← version > 1500
   │     large_groups: [big_group_1]        │  ← 需单独检查
   │ ◀───────────────────────────────────   │
   │                                        │
   │  ③ 拉取 conv_A 消息 (id > local)       │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ④ 返回消息列表                         │
   │ ◀───────────────────────────────────   │
   │                                        │
   │  ⑤ 检查 big_group_1 的 seq             │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ⑥ 更新本地 sync_seq=1523              │
```

### 3.4 发消息写入逻辑

**小群/单聊（≤200人）- 写扩散**：

```sql
BEGIN;
INSERT INTO messages (...) VALUES (...);

UPDATE user_sync_state 
SET sync_seq = sync_seq + 1 
WHERE user_id = ANY($members);

UPDATE user_conversations 
SET version = (SELECT sync_seq FROM user_sync_state WHERE user_id = user_conversations.user_id),
    last_msg_id = $msg_id,
    unread_count = CASE WHEN user_id = $sender THEN 0 ELSE unread_count + 1 END
WHERE conversation_id = $conv_id;
COMMIT;
```

**大群（>200人）- 读扩散**：

```sql
BEGIN;
INSERT INTO messages (...) VALUES (...);

UPDATE conversation_sync_state 
SET seq = seq + 1, last_msg_id = $msg_id 
WHERE conversation_id = $conv_id;
-- 不更新每个用户的 sync_seq，O(1) 写入
COMMIT;
```

---

## 四、实时推送与消息队列

### 4.1 核心设计原则

**❌ 错误设计**：消息队列（Kafka）作为实时推送通道

- 问题：延迟高（10-100ms）、流式消息无法处理、职责混乱

**✅ 正确设计**：分离实时推送和异步任务

|NATS（实时推送）|Kafka（异步任务）|
|---|---|
|普通消息推送|消息索引|
|流式消息推送|离线消息处理|
|输入状态|审计日志|
|在线状态|Webhook 触发|
|已读回执|统计分析|
|**延迟 < 1ms**|**延迟不敏感**|

### 4.2 NATS Subject 设计

|Subject 模式|用途|
|---|---|
|`im.user.{user_id}`|推送给特定用户|
|`im.conv.{conv_id}`|推送给会话所有在线成员|
|`im.gateway.{node_id}`|推送给特定网关节点|
|`im.broadcast`|全局广播（系统公告等）|

**示例**：

- 单聊消息：发布到 `im.conv.{conv_id}`
- 群聊消息：发布到 `im.conv.{conv_id}`
- 流式消息：发布到 `im.conv.{conv_id}`（多次，每个 delta 一次）
- 踢人下线：发布到 `im.user.{user_id}`

### 4.3 统一消息流程

无论普通消息还是流式消息，都走完全相同的路径：

```
业务服务（消息服务 / Bot 服务）
      │
      ├────────────────────────────┐
      │                            │
      ▼                            ▼
  ┌─────────┐                 ┌─────────┐
  │  NATS   │                 │  Kafka  │
  │ 实时推送 │                 │ 异步任务 │
  └────┬────┘                 └────┬────┘
       │                           │
       ▼                           ▼
  ┌─────────┐                 ┌─────────┐
  │ Gateway │                 │ Workers │
  │  集群   │                 │  集群   │
  └────┬────┘                 └─────────┘
       │
       ▼
    用户
```

---

## 五、流式消息设计（LLM Bot）

### 5.1 流式消息协议

```protobuf
// 流式消息开始
message StreamMessageStart {
    int64 message_id = 1;           // 服务端生成的消息 ID
    int64 conversation_id = 2;
    int64 sender_id = 3;            // Bot ID
    int64 reply_to_id = 4;          // 回复的用户消息 ID
    int64 timestamp = 5;
}

// 流式消息增量
message StreamMessageDelta {
    int64 message_id = 1;
    string delta = 2;               // 增量内容
    int32 index = 3;                // 序号（处理乱序）
}

// 流式消息结束
message StreamMessageEnd {
    int64 message_id = 1;
    string full_content = 2;        // 完整内容（可选）
    int32 total_tokens = 3;
    bool success = 4;
    string error = 5;
}
```

### 5.2 流式消息完整流程

```
1. 用户发送 "@Bot 帮我写个故事"
   │
   ▼
2. Gateway → 消息服务
   │
   ├─ 存储用户消息
   ├─ 发布用户消息到 NATS（其他人看到）
   ├─ 发布到 Kafka（异步任务）
   └─ 调用 Bot 服务

3. Bot 服务处理
   │
   ├─ 生成消息 ID（雪花算法）
   │
   ├─ 发布 StreamStart 到 NATS ─────────────────▶ Gateway → 用户
   │   im.conv.{conv_id}
   │
   ├─ 调用 LLM API（流式）
   │   │
   │   ├─ 收到 token_1
   │   │   └─ 发布 StreamDelta 到 NATS ─────────▶ Gateway → 用户
   │   │
   │   ├─ 收到 token_2
   │   │   └─ 发布 StreamDelta 到 NATS ─────────▶ Gateway → 用户
   │   │
   │   └─ ... 重复 ...
   │
   ├─ 发布 StreamEnd 到 NATS ───────────────────▶ Gateway → 用户
   │
   ├─ 写入完整消息到 PostgreSQL
   │
   └─ 发布到 Kafka（索引、审计等异步任务）
```

**关键点**：

- 所有流式内容通过 NATS 实时推送
- 和普通消息走完全相同的 NATS 通道
- 完成后才写数据库和 Kafka
- 离线用户收到的是最终完整消息

### 5.3 优化：批量合并 delta

建议每 50-100ms 或累积 10 个 token 后合并发送，减少网络开销：

```go
type StreamBatcher struct {
    deltas    []string
    lastFlush time.Time
    interval  time.Duration // 建议 50-100ms
}

func (b *StreamBatcher) Add(delta string) {
    b.deltas = append(b.deltas, delta)
    
    if time.Since(b.lastFlush) >= b.interval || len(b.deltas) >= 10 {
        combined := strings.Join(b.deltas, "")
        b.flush(combined)
    }
}
```

---

## 六、数据库设计与优化

### 6.1 核心表结构

```sql
-- ============================================
-- 用户相关
-- ============================================

CREATE TABLE users (
    id          bigint PRIMARY KEY,           -- 雪花 ID
    username    varchar(64) UNIQUE NOT NULL,
    nickname    varchar(64),
    avatar_url  varchar(512),
    status      smallint DEFAULT 1,
    created_at  timestamptz DEFAULT now(),
    updated_at  timestamptz DEFAULT now()
);

-- ============================================
-- 同步状态
-- ============================================

CREATE TABLE user_sync_state (
    user_id     bigint PRIMARY KEY,
    sync_seq    bigint DEFAULT 0,
    updated_at  timestamptz DEFAULT now()
);

CREATE TABLE conversation_sync_state (
    conversation_id bigint PRIMARY KEY,
    seq             bigint DEFAULT 0,
    last_msg_id     bigint,
    is_large        boolean DEFAULT false,    -- > 200 人标记为大群
    updated_at      timestamptz DEFAULT now()
);

-- ============================================
-- 会话相关
-- ============================================

CREATE TABLE conversations (
    id          bigint PRIMARY KEY,
    type        smallint NOT NULL,            -- 1:单聊 2:群聊 3:频道
    name        varchar(128),
    avatar_url  varchar(512),
    owner_id    bigint,
    settings    jsonb DEFAULT '{}',
    created_at  timestamptz DEFAULT now(),
    updated_at  timestamptz DEFAULT now()
);

CREATE TABLE conversation_members (
    conversation_id bigint NOT NULL,
    user_id         bigint NOT NULL,
    role            smallint DEFAULT 0,
    nickname        varchar(64),
    is_muted        boolean DEFAULT false,
    joined_at       timestamptz DEFAULT now(),
    PRIMARY KEY (conversation_id, user_id)
);

CREATE TABLE user_conversations (
    user_id           bigint NOT NULL,
    conversation_id   bigint NOT NULL,
    version           bigint DEFAULT 0,
    conv_acked_seq    bigint DEFAULT 0,       -- 大群用
    last_read_msg_id  bigint DEFAULT 0,
    last_msg_id       bigint DEFAULT 0,
    last_msg_at       timestamptz,
    unread_count      int DEFAULT 0,
    is_pinned         boolean DEFAULT false,
    is_muted          boolean DEFAULT false,
    is_deleted        boolean DEFAULT false,
    updated_at        timestamptz DEFAULT now(),
    PRIMARY KEY (user_id, conversation_id)
);

CREATE INDEX idx_user_conv_version ON user_conversations (user_id, version);

-- ============================================
-- 消息（分区）
-- ============================================

CREATE TABLE messages (
    id              bigint NOT NULL,
    conversation_id bigint NOT NULL,
    sender_id       bigint NOT NULL,
    content_type    smallint NOT NULL,
    content         jsonb NOT NULL,
    reply_to_id     bigint,
    mentions        bigint[],
    status          smallint DEFAULT 1,
    created_at      timestamptz DEFAULT now(),
    PRIMARY KEY (conversation_id, id)
) PARTITION BY HASH (conversation_id);

-- 创建 16 个分区
DO $$ BEGIN
    FOR i IN 0..15 LOOP
        EXECUTE format(
            'CREATE TABLE messages_p%s PARTITION OF messages 
             FOR VALUES WITH (MODULUS 16, REMAINDER %s)', i, i);
    END LOOP;
END $$;
```

### 6.2 消息内容 JSON 结构

```go
type MessageContent struct {
    Text        string           `json:"text,omitempty"`
    Image       *ImageContent    `json:"image,omitempty"`
    File        *FileContent     `json:"file,omitempty"`
    Voice       *VoiceContent    `json:"voice,omitempty"`
    Location    *LocationContent `json:"location,omitempty"`
    LinkPreview *LinkPreview     `json:"link_preview,omitempty"`
}

type ImageContent struct {
    URL       string `json:"url"`
    Thumbnail string `json:"thumbnail"`
    Width     int    `json:"width"`
    Height    int    `json:"height"`
    Size      int64  `json:"size"`
}
```

### 6.3 扩展路径

|场景|推荐方案|
|---|---|
|< 10万 TPS|PostgreSQL + 分区 + Citus|
|10万-50万 TPS|PostgreSQL + Citus 集群|
|> 50万 TPS|消息存储迁移到 ScyllaDB/Cassandra|
|全球多区域|Cassandra/CockroachDB|

### 6.4 PostgreSQL 优化配置

```ini
# postgresql.conf (16C64G 服务器)

max_connections = 500
shared_buffers = 16GB
effective_cache_size = 48GB
work_mem = 128MB
maintenance_work_mem = 2GB

wal_buffers = 64MB
max_wal_size = 8GB
synchronous_commit = off          # 异步提交

max_parallel_workers_per_gather = 4
max_parallel_workers = 8

random_page_cost = 1.1            # SSD
effective_io_concurrency = 200
```

---

## 七、WebRTC 音视频架构

### 7.1 架构选型：SFU vs MCU vs P2P

|指标|Mesh (P2P)|SFU|MCU|
|---|---|---|---|
|**参与者上限**|2-4人|5-100+人|无限制|
|**服务器 CPU**|无|低|极高|
|**服务器成本**|零|中等|高（约 SFU 100倍）|
|**客户端带宽**|高|中等|最低|
|**延迟**|最低|低|较高|
|**端到端加密**|✓|✓|✗|
|**适用场景**|小型私密会议|大多数视频会议|低带宽设备|

**结论**：SFU（Selective Forwarding Unit）是目前最主流的多人会议架构，服务器接收所有参与者的流，选择性转发给其他人，不进行转码。每个参与者上传 1 路流，接收 N-1 路流。

### 7.2 SFU 选型推荐

|SFU|语言|优势|适用场景|
|---|---|---|---|
|**mediasoup**|Node.js/Rust|最灵活，低级API控制力强|定制化需求|
|**Pion/ion-sfu**|Go|与Go后端统一技术栈|Go生态，简化运维|
|**LiveKit**|Go|云友好，SDK完善|快速上线|
|**Janus**|C|企业级，有商业支持|企业场景|

**推荐组合**：如果后端是 Go，优先考虑 **Pion + ion-sfu**；如果需要最大灵活性，选择 **mediasoup**。

### 7.3 WebRTC 协议栈

```
┌─────────────────────────────────────────────────────────────────┐
│                       WebRTC 协议栈                              │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │   音视频    │    │  数据通道   │    │   信令      │         │
│  │ (SRTP/SRTCP)│    │   (SCTP)    │    │ (WebSocket) │         │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘         │
│         └─────────┬────────┘                  │                 │
│           ┌───────┴───────┐                   │                 │
│           │     DTLS      │  ←── 加密层       │                 │
│           └───────┬───────┘                   │                 │
│           ┌───────┴───────┐                   │                 │
│           │      ICE      │  ←── 连接建立     │                 │
│           └───────┬───────┘                   │                 │
│         ┌─────────┼─────────┐                 │                 │
│    ┌────┴────┐┌───┴───┐┌────┴────┐           │                 │
│    │  STUN   ││ TURN  ││  直连   │           │                 │
│    └─────────┘└───────┘└─────────┘           │                 │
│                   │                           │                 │
│           ┌───────┴───────┐           ┌──────┴──────┐         │
│           │   UDP / TCP   │           │    TCP      │         │
│           └───────────────┘           └─────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

### 7.4 WebRTC 连接建立流程

```
┌─────────────┐         信令服务器         ┌─────────────┐
│   Peer A    │                            │   Peer B    │
│  (发起方)   │                            │  (接收方)   │
└──────┬──────┘                            └──────┬──────┘
       │                                          │
       │ 1. 获取本地媒体流                         │
       │    getUserMedia()                        │
       │                                          │
       │ 2. 创建 PeerConnection                   │
       │    添加本地轨道                           │
       │                                          │
       │ 3. createOffer()                         │
       │ 4. setLocalDescription(offer)            │
       │ 5. 发送 offer                            │
       │ ──────────────────────────────────────>  │
       │                                          │ 6. setRemoteDescription(offer)
       │                                          │ 7. createAnswer()
       │                                          │ 8. setLocalDescription(answer)
       │                   发送 answer             │
       │ <────────────────────────────────────── │
       │ 9. setRemoteDescription(answer)          │
       │                                          │
       │ === ICE 候选交换（双向并行）===            │
       │                                          │
       │ ──────── candidate ────────────────────> │
       │ <─────── candidate ─────────────────────│
       │                                          │
       │ === DTLS 握手 + SRTP 建立 ===             │
       │                                          │
       │ ========== 媒体流传输开始 =========        │
```

### 7.5 NAT 类型与穿透难度

|NAT 类型|端口映射|入站过滤|穿透难度|
|---|---|---|---|
|Full Cone|固定|无限制|最容易|
|Restricted Cone|固定|限制 IP|较容易|
|Port Restricted Cone|固定|限制 IP+Port|中等|
|**Symmetric**|随目标变化|限制 IP+Port|**最难（需 TURN）**|

**关键洞察**：大多数家用路由器是 Port Restricted Cone，移动运营商 CG-NAT 多为 Symmetric NAT。两个 Symmetric NAT 或 Symmetric + Port Restricted 组合必须使用 TURN。

---

## 八、服务发现机制

### 8.1 Gossip 实现

```go
import "github.com/hashicorp/memberlist"

type GatewayMeta struct {
    NodeID       string `json:"node_id"`
    PublicAddr   string `json:"public_addr"`
    InternalAddr string `json:"internal_addr"`
    Connections  int    `json:"connections"`
}

type GatewayDelegate struct {
    meta GatewayMeta
    mu   sync.RWMutex
}

func (d *GatewayDelegate) NodeMeta(limit int) []byte {
    d.mu.RLock()
    defer d.mu.RUnlock()
    data, _ := json.Marshal(d.meta)
    return data
}

func NewGossipNode(cfg *GossipConfig) (*memberlist.Memberlist, error) {
    delegate := &GatewayDelegate{
        meta: GatewayMeta{
            NodeID:       cfg.NodeID,
            PublicAddr:   cfg.PublicAddr,
            InternalAddr: cfg.InternalAddr,
        },
    }

    mlConfig := memberlist.DefaultLANConfig()
    mlConfig.Name = cfg.NodeID
    mlConfig.BindAddr = cfg.BindAddr
    mlConfig.BindPort = cfg.BindPort
    mlConfig.Delegate = delegate

    list, err := memberlist.Create(mlConfig)
    if err != nil {
        return nil, err
    }

    if len(cfg.Seeds) > 0 {
        list.Join(cfg.Seeds)
    }

    return list, nil
}
```

---

## 九、核心业务流程

### 9.1 消息发送完整流程

```
1. 客户端
   └─▶ WebSocket 发送 (Protobuf)

2. Gateway
   ├─ Protobuf 解码
   ├─ Token 鉴权
   ├─ 频率限制
   └─ 转发到消息服务

3. 消息服务
   ├─ 生成雪花 ID
   ├─ 写入 PostgreSQL
   ├─ 发布到 NATS (im.conv.{conv_id}) ─────────▶ 实时推送
   ├─ 发布到 Kafka ────────────────────────────▶ 异步任务
   └─ 返回 ACK

4. Gateway 集群（通过 NATS 订阅）
   ├─ 收到 NATS 消息
   ├─ 查找本地在线用户
   └─ WebSocket 推送

5. Kafka Workers（异步）
   ├─ 索引到 Meilisearch
   ├─ 离线用户 Push 通知
   ├─ Webhook 触发
   └─ 审计日志
```

### 9.2 用户上线同步流程

```
1. 建立连接
   ├─ WebSocket 握手
   ├─ Token 验证
   ├─ 注册到 Valkey (user_id → gateway_node)
   └─ 订阅 NATS (im.user.{user_id} + 用户的会话列表)

2. 增量同步
   ├─ 客户端发送 local_sync_seq
   ├─ 服务端返回 changed conversations (version > local_seq)
   └─ 客户端逐个拉取新消息

3. 实时接收
   └─ 通过 NATS 订阅自动接收新消息
```

---

## 十、核心代码实现

### 10.1 雪花 ID 生成器

```go
type Snowflake struct {
    mu        sync.Mutex
    epoch     int64
    machineID int64
    sequence  int64
    lastTime  int64
}

func NewSnowflake(machineID int64) *Snowflake {
    return &Snowflake{
        epoch:     1704067200000, // 2024-01-01
        machineID: machineID,
    }
}

func (s *Snowflake) Generate() int64 {
    s.mu.Lock()
    defer s.mu.Unlock()

    now := time.Now().UnixMilli() - s.epoch
    if now == s.lastTime {
        s.sequence = (s.sequence + 1) & 0xFFF
        if s.sequence == 0 {
            for now <= s.lastTime {
                now = time.Now().UnixMilli() - s.epoch
            }
        }
    } else {
        s.sequence = 0
    }
    s.lastTime = now

    return (now << 22) | (s.machineID << 12) | s.sequence
}
```

### 10.2 NATS 实时总线

```go
type RealtimeBus struct {
    nc     *nats.Conn
    nodeID string
}

func NewRealtimeBus(url, nodeID string) (*RealtimeBus, error) {
    nc, err := nats.Connect(url,
        nats.ReconnectWait(time.Second),
        nats.MaxReconnects(-1),
    )
    if err != nil {
        return nil, err
    }
    return &RealtimeBus{nc: nc, nodeID: nodeID}, nil
}

// 发布到会话
func (b *RealtimeBus) PublishToConversation(convID int64, msg proto.Message) error {
    data, _ := proto.Marshal(msg)
    return b.nc.Publish(fmt.Sprintf("im.conv.%d", convID), data)
}

// 发布到用户
func (b *RealtimeBus) PublishToUser(userID int64, msg proto.Message) error {
    data, _ := proto.Marshal(msg)
    return b.nc.Publish(fmt.Sprintf("im.user.%d", userID), data)
}

// 订阅会话
func (b *RealtimeBus) SubscribeConversation(convID int64, handler func([]byte)) (*nats.Subscription, error) {
    return b.nc.Subscribe(fmt.Sprintf("im.conv.%d", convID), func(msg *nats.Msg) {
        handler(msg.Data)
    })
}
```

### 10.3 消息服务

```go
type MessageService struct {
    db        *pgxpool.Pool
    bus       *RealtimeBus      // NATS
    queue     *kafka.Producer   // Kafka
    snowflake *Snowflake
}

func (s *MessageService) SendMessage(ctx context.Context, req *SendMessageRequest) (*Message, error) {
    msg := &Message{
        ID:             s.snowflake.Generate(),
        ConversationID: req.ConversationID,
        SenderID:       req.SenderID,
        ContentType:    req.ContentType,
        Content:        req.Content,
        CreatedAt:      time.Now(),
    }

    // 1. 持久化
    if err := s.saveMessage(ctx, msg); err != nil {
        return nil, err
    }

    // 2. 实时推送（NATS）
    pushMsg := &pb.RecvMessage{
        Id:             msg.ID,
        ConversationId: msg.ConversationID,
        SenderId:       msg.SenderID,
        ContentType:    int32(msg.ContentType),
        Content:        msg.Content.ToBytes(),
        CreatedAt:      msg.CreatedAt.UnixMilli(),
    }
    s.bus.PublishToConversation(msg.ConversationID, pushMsg)

    // 3. 异步任务（Kafka）
    s.queue.Produce(&kafka.Message{
        Topic: "message-events",
        Key:   []byte(fmt.Sprintf("%d", msg.ConversationID)),
        Value: msg.ToJSON(),
    })

    return msg, nil
}
```

### 10.4 Bot 服务（流式）

```go
type BotService struct {
    bus       *RealtimeBus
    queue     *kafka.Producer
    llmClient *llm.Client
    snowflake *Snowflake
}

func (s *BotService) HandleMention(ctx context.Context, userMsg *Message) error {
    botMsgID := s.snowflake.Generate()
    convID := userMsg.ConversationID

    // 1. StreamStart
    s.bus.PublishToConversation(convID, &pb.StreamMessageStart{
        MessageId:      botMsgID,
        ConversationId: convID,
        SenderId:       botUserID,
        Timestamp:      time.Now().UnixMilli(),
    })

    // 2. 流式调用 LLM
    var fullContent strings.Builder
    stream, _ := s.llmClient.ChatStream(ctx, buildPrompt(userMsg))

    index := 0
    for chunk := range stream {
        fullContent.WriteString(chunk.Delta)
        index++

        // StreamDelta - 和普通消息走相同的 NATS 通道
        s.bus.PublishToConversation(convID, &pb.StreamMessageDelta{
            MessageId: botMsgID,
            Delta:     chunk.Delta,
            Index:     int32(index),
        })
    }

    // 3. StreamEnd
    s.bus.PublishToConversation(convID, &pb.StreamMessageEnd{
        MessageId:   botMsgID,
        FullContent: fullContent.String(),
        Success:     true,
    })

    // 4. 持久化
    msg := &Message{
        ID:             botMsgID,
        ConversationID: convID,
        SenderID:       botUserID,
        ContentType:    ContentTypeText,
        Content:        MessageContent{Text: fullContent.String()},
    }
    s.saveMessage(ctx, msg)

    // 5. 异步任务
    s.queue.Produce(&kafka.Message{
        Topic: "message-events",
        Value: msg.ToJSON(),
    })

    return nil
}
```

### 10.5 Gateway 连接管理

```go
type Gateway struct {
    connections sync.Map  // userID -> *Connection
    bus         *RealtimeBus
    subs        sync.Map  // key -> *nats.Subscription
}

func (g *Gateway) OnUserConnected(userID int64, conn *Connection) {
    g.connections.Store(userID, conn)

    // 订阅用户消息
    sub, _ := g.bus.SubscribeUser(userID, func(data []byte) {
        conn.Send(data)
    })
    g.subs.Store(fmt.Sprintf("user:%d", userID), sub)

    // 订阅用户的会话
    convIDs := g.getUserConversations(userID)
    for _, convID := range convIDs {
        sub, _ := g.bus.SubscribeConversation(convID, func(data []byte) {
            if c, ok := g.connections.Load(userID); ok {
                c.(*Connection).Send(data)
            }
        })
        g.subs.Store(fmt.Sprintf("conv:%d:%d", userID, convID), sub)
    }
}

func (g *Gateway) OnUserDisconnected(userID int64) {
    g.connections.Delete(userID)

    // 取消所有订阅
    g.subs.Range(func(key, value interface{}) bool {
        if strings.HasPrefix(key.(string), fmt.Sprintf("user:%d", userID)) ||
           strings.HasPrefix(key.(string), fmt.Sprintf("conv:%d:", userID)) {
            value.(*nats.Subscription).Unsubscribe()
            g.subs.Delete(key)
        }
        return true
    })
}
```

### 10.6 同步服务

```go
type SyncService struct {
    db    *pgxpool.Pool
    cache *Cache
}

type SyncRequest struct {
    UserID         int64           `json:"user_id"`
    LocalSyncSeq   int64           `json:"local_sync_seq"`
    LargeGroupSeqs map[int64]int64 `json:"large_group_seqs"`
}

type SyncResponse struct {
    ServerSyncSeq        int64              `json:"server_sync_seq"`
    ChangedConversations []ConversationSync `json:"changed_conversations"`
    LargeGroupChanges    []LargeGroupSync   `json:"large_group_changes"`
    FullSyncRequired     bool               `json:"full_sync_required"`
}

func (s *SyncService) Sync(ctx context.Context, req *SyncRequest) (*SyncResponse, error) {
    resp := &SyncResponse{}

    // 获取服务端 sync_seq
    var serverSeq int64
    s.db.QueryRow(ctx, `
        SELECT sync_seq FROM user_sync_state WHERE user_id = $1
    `, req.UserID).Scan(&serverSeq)
    resp.ServerSyncSeq = serverSeq

    // 离线太久，建议全量
    if serverSeq-req.LocalSyncSeq > 10000 {
        resp.FullSyncRequired = true
        return resp, nil
    }

    // 查询变更的普通会话
    rows, _ := s.db.Query(ctx, `
        SELECT uc.conversation_id, uc.last_msg_id, uc.last_msg_at, uc.unread_count
        FROM user_conversations uc
        JOIN conversation_sync_state cs ON cs.conversation_id = uc.conversation_id
        WHERE uc.user_id = $1 AND uc.version > $2 AND cs.is_large = false
    `, req.UserID, req.LocalSyncSeq)
    defer rows.Close()

    for rows.Next() {
        var conv ConversationSync
        rows.Scan(&conv.ConversationID, &conv.LastMsgID, &conv.LastMsgAt, &conv.UnreadCount)
        resp.ChangedConversations = append(resp.ChangedConversations, conv)
    }

    // 检查大群变更
    // ... 省略大群检查逻辑

    return resp, nil
}
```

---

## 十一、项目结构

```
im-server/
├── cmd/
│   ├── gateway/                 # 网关服务
│   │   └── main.go
│   ├── api/                     # HTTP API
│   │   └── main.go
│   ├── bot/                     # Bot 服务
│   │   └── main.go
│   └── worker/                  # 后台任务
│       └── main.go
│
├── internal/
│   ├── gateway/
│   │   ├── server.go
│   │   ├── connection.go
│   │   └── handler.go
│   │
│   ├── service/
│   │   ├── message.go
│   │   ├── sync.go
│   │   ├── conversation.go
│   │   ├── user.go
│   │   └── bot.go
│   │
│   ├── repository/
│   │   ├── message.go
│   │   ├── conversation.go
│   │   └── cache.go
│   │
│   ├── bus/                     # NATS 封装
│   │   └── realtime.go
│   │
│   ├── queue/                   # Kafka 封装
│   │   └── producer.go
│   │
│   ├── discovery/
│   │   └── gossip.go
│   │
│   └── pkg/
│       ├── snowflake/
│       ├── protocol/            # Protobuf
│       └── auth/
│
├── api/proto/
│   ├── message.proto
│   ├── stream.proto
│   └── gateway.proto
│
├── migrations/
│   └── 001_init.sql
│
├── configs/
│   └── config.yaml
│
├── deployments/
│   ├── docker-compose.yaml
│   └── k8s/
│
├── Dockerfile
├── Makefile
└── README.md
```

---

## 十二、部署与容量规划

### 12.1 十万+ TPS 硬件规划

|组件|数量|配置|说明|月成本|
|---|---|---|---|---|
|Gateway|20|8C16G|每节点 5 万连接|$3,000|
|NATS|3|4C8G|集群，支撑实时推送|$300|
|消息服务|10|8C16G|无状态，按需扩容|$1,500|
|Bot 服务|10|8C16G|按 LLM 调用量|$1,500|
|API 服务|5|4C8G|HTTP API|$600|
|Kafka|5|16C64G|3 副本|$3,000|
|PostgreSQL|3|32C128G|主 + 从 + 备|$4,500|
|Valkey|6|8C64G|3 主 3 从|$2,000|
|Worker|20|4C8G|按任务类型分组|$1,200|
|**总计**|-|-|-|**≈$17,600/月**|

### 12.2 Docker Compose（开发环境）

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_USER: im
      POSTGRES_PASSWORD: im_password
      POSTGRES_DB: im
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  valkey:
    image: valkey/valkey:7
    ports:
      - "6379:6379"

  nats:
    image: nats:2.10
    ports:
      - "4222:4222"
      - "8222:8222"
    command: ["--cluster", "nats://0.0.0.0:6222", "--http_port", "8222"]

  kafka:
    image: bitnami/kafka:3.6
    ports:
      - "9092:9092"
    environment:
      - KAFKA_CFG_NODE_ID=0
      - KAFKA_CFG_PROCESS_ROLES=controller,broker
      - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
      - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER

  gateway:
    build: .
    command: ./gateway
    ports:
      - "8080:8080"
    environment:
      - DB_URL=postgres://im:im_password@postgres:5432/im
      - VALKEY_URL=valkey:6379
      - NATS_URL=nats://nats:4222
      - KAFKA_BROKERS=kafka:9092
    depends_on:
      - postgres
      - valkey
      - nats
      - kafka

volumes:
  postgres_data:
```

---

## 十三、演进路线图

### Phase 1: MVP (0-6月)

- **技术栈**：Go 单体 + PostgreSQL + Valkey + NATS
- **目标**：1万 TPS，1万并发连接
- **特点**：PostgreSQL 队列替代 Kafka，简化运维

### Phase 2: 分布式 (6-12月)

- 引入 Kafka 替换 PG 队列
- Gateway 集群 + Gossip 服务发现
- PostgreSQL 主从 + 分区表
- **目标**：5万 TPS

### Phase 3: 规模化 (12-24月)

- 完整集群化部署（Kubernetes）
- Meilisearch 消息搜索
- PostgreSQL → Citus 或 TiDB
- 多机房部署
- **目标**：十万+ TPS

### Phase 4: 全球化 (24+月)

- 多区域部署
- 边缘接入节点
- 消息存储迁移到 Cassandra（可选）
- **目标**：百万 TPS

---

## 十四、完整功能特性清单

### 14.1 消息核心功能

|特性|优先级|说明|
|---|---|---|
|实时消息转发|P0|消息即时送达|
|流式消息|P0|支持 LLM Bot 流式输出|
|已读回执|P1|显示消息是否已读|
|投递回执|P1|确认消息已送达|
|消息引用/回复|P1|引用某条消息|
|消息撤回|P1|撤回已发送消息|
|消息编辑|P2|修改已发送消息|
|@提及|P1|群聊中 @用户|
|消息 Reaction|P2|表情反应|
|消息搜索|P1|全文搜索|
|正在输入|P2|输入状态指示|

### 14.2 消息类型

|类型|content_type|优先级|
|---|---|---|
|文本|1|P0|
|图片|2|P0|
|文件|3|P1|
|语音|4|P1|
|位置|5|P2|
|链接预览|6|P2|

### 14.3 群组功能

|特性|优先级|
|---|---|
|创建/解散群|P0|
|群成员管理|P0|
|群角色权限|P1|
|群公告|P2|
|大群支持 (万人)|P1|

### 14.4 扩展功能

|特性|优先级|
|---|---|
|Bot 框架|P1|
|Webhook 集成|P1|
|消息审核/过滤|P2|
|音视频通话|P1|
|屏幕共享|P2|

---

## 附录 A：Protobuf 定义

```protobuf
syntax = "proto3";
package im.v1;

// 通用消息信封
message Envelope {
    int64 seq = 1;
    int64 timestamp = 2;
    oneof payload {
        SendMessageReq send_msg = 10;
        SendMessageAck send_ack = 11;
        RecvMessage recv_msg = 12;
        HeartbeatReq heartbeat = 20;
        HeartbeatResp heartbeat_resp = 21;
        SyncReq sync_req = 30;
        SyncResp sync_resp = 31;
        StreamMessageStart stream_start = 40;
        StreamMessageDelta stream_delta = 41;
        StreamMessageEnd stream_end = 42;
    }
}

message SendMessageReq {
    int64 conversation_id = 1;
    int64 client_msg_id = 2;
    int32 content_type = 3;
    bytes content = 4;
    repeated int64 mentions = 5;
}

message SendMessageAck {
    int64 client_msg_id = 1;
    int64 server_msg_id = 2;
    int64 server_time = 3;
}

message RecvMessage {
    int64 id = 1;
    int64 conversation_id = 2;
    int64 sender_id = 3;
    int32 content_type = 4;
    bytes content = 5;
    int64 created_at = 6;
}

message StreamMessageStart {
    int64 message_id = 1;
    int64 conversation_id = 2;
    int64 sender_id = 3;
    int64 reply_to_id = 4;
    int64 timestamp = 5;
}

message StreamMessageDelta {
    int64 message_id = 1;
    string delta = 2;
    int32 index = 3;
}

message StreamMessageEnd {
    int64 message_id = 1;
    string full_content = 2;
    int32 total_tokens = 3;
    bool success = 4;
    string error = 5;
}

message SyncReq {
    int64 sync_seq = 1;
    map<int64, int64> large_group_seqs = 2;
}

message SyncResp {
    int64 server_sync_seq = 1;
    repeated ConversationChange changes = 2;
    repeated LargeGroupChange large_groups = 3;
    bool full_sync_required = 4;
}
```

---

## 附录 B：方案对比总结

|维度|OpenIM|本方案|
|---|---|---|
|同步模型|用户 Inbox 写扩散|混合 (小群写扩散 + 大群读扩散)|
|实时推送|队列消费推送|NATS 直接推送|
|流式消息|特殊处理|与普通消息统一|
|大群写入|O(N)|O(1)|
|存储成本|高|低|
|推送延迟|10-100ms|< 1ms|
|组件数量|多|适中|

---

## 总结

本文档提供了一套经过验证的现代 IM 系统架构设计方案，核心优势如下：

1. **NATS + Kafka 分离**：NATS 专注实时推送（延迟 < 1ms），Kafka 专注异步任务，职责清晰
2. **混合同步方案**：兼顾小群的简单性和大群的性能，避免万人群写扩散
3. **Go + PostgreSQL + Valkey**：经过验证的黄金组合，生态完善
4. **流式消息统一推送**：与普通消息走相同的 NATS 通道，简化架构
5. **SFU 音视频架构**：mediasoup/Pion 实现多人会议，支持 Simulcast 带宽自适应

**关键实施建议**：

- 从 MVP 阶段开始，PostgreSQL 队列替代 Kafka 简化运维
- 增加消息幂等性处理，使用 idempotency key 防止重复
- 流式消息增加批量合并机制（50-100ms 间隔）
- 生产环境必须配置 TURN 服务器作为 NAT 穿透后备

这套架构完全能够支撑从 MVP 到十万+ TPS 的演进路径，单人可开发维护。

---

> 文档版本: 3.0  
> 目标: 十万+ TPS，可无限水平扩展  
> 最后更新: 2024-12

# 现代 IM 系统架构设计 - 最优解方案

> **目标：构建一个支持十万+ TPS、可无限水平扩展、单人可开发维护的即时通讯系统**
> 
> **整合 WebRTC 音视频通话 + 即时通讯完整架构**

---

## 目录

1. [架构总览与设计目标](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%B8%80%E6%9E%B6%E6%9E%84%E6%80%BB%E8%A7%88%E4%B8%8E%E8%AE%BE%E8%AE%A1%E7%9B%AE%E6%A0%87)
2. [技术选型与依据](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%BA%8C%E6%8A%80%E6%9C%AF%E9%80%89%E5%9E%8B%E4%B8%8E%E4%BE%9D%E6%8D%AE)
3. [消息同步机制（混合方案）](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%B8%89%E6%B6%88%E6%81%AF%E5%90%8C%E6%AD%A5%E6%9C%BA%E5%88%B6%E6%B7%B7%E5%90%88%E6%96%B9%E6%A1%88)
4. [实时推送与消息队列](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%9B%9B%E5%AE%9E%E6%97%B6%E6%8E%A8%E9%80%81%E4%B8%8E%E6%B6%88%E6%81%AF%E9%98%9F%E5%88%97)
5. [流式消息设计（LLM Bot）](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%BA%94%E6%B5%81%E5%BC%8F%E6%B6%88%E6%81%AF%E8%AE%BE%E8%AE%A1llm-bot)
6. [数据库设计与优化](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%85%AD%E6%95%B0%E6%8D%AE%E5%BA%93%E8%AE%BE%E8%AE%A1%E4%B8%8E%E4%BC%98%E5%8C%96)
7. [WebRTC 音视频架构](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%B8%83webrtc-%E9%9F%B3%E8%A7%86%E9%A2%91%E6%9E%B6%E6%9E%84)
8. [服务发现机制](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%85%AB%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0%E6%9C%BA%E5%88%B6)
9. [核心业务流程](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E4%B9%9D%E6%A0%B8%E5%BF%83%E4%B8%9A%E5%8A%A1%E6%B5%81%E7%A8%8B)
10. [核心代码实现](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E6%A0%B8%E5%BF%83%E4%BB%A3%E7%A0%81%E5%AE%9E%E7%8E%B0)
11. [项目结构](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E4%B8%80%E9%A1%B9%E7%9B%AE%E7%BB%93%E6%9E%84)
12. [部署与容量规划](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E4%BA%8C%E9%83%A8%E7%BD%B2%E4%B8%8E%E5%AE%B9%E9%87%8F%E8%A7%84%E5%88%92)
13. [演进路线图](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E4%B8%89%E6%BC%94%E8%BF%9B%E8%B7%AF%E7%BA%BF%E5%9B%BE)
14. [完整功能特性清单](https://claude.ai/chat/283588a3-25af-4bb8-8fff-1a8c7c241b2b#%E5%8D%81%E5%9B%9B%E5%AE%8C%E6%95%B4%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7%E6%B8%85%E5%8D%95)

---

## 一、架构总览与设计目标

### 1.1 设计目标

|指标|目标|说明|
|---|---|---|
|**吞吐量**|十万+ TPS|消息发送+推送合计|
|**延迟**|P99 < 10ms|实时推送延迟|
|**在线连接**|百万级|长连接数|
|**可扩展性**|无限水平扩展|所有组件支持集群|
|**可维护性**|单人可维护|组件最少化|

### 1.2 核心设计原则

- **职责分离**：NATS 负责实时推送，Kafka 负责异步任务，互不干扰
- **统一通道**：普通消息和流式消息走完全相同的推送路径
- **水平扩展**：所有组件无状态或支持集群，可无限扩展
- **简洁优先**：组件最少化，降低运维复杂度

### 1.3 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        IM 系统架构（十万+ TPS）                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                                 客户端                                       │
│                   iOS / Android / Web / Desktop / Bot                       │
│                                    │                                        │
│                                    ▼                                        │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                          负载均衡 (L4)                                 │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                    │                                        │
│                  ┌─────────────────┼─────────────────┐                      │
│                  ▼                 ▼                 ▼                      │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                        Gateway 集群                                    │ │
│  │           WebSocket/TCP + Protobuf │ 连接管理 │ 鉴权                   │ │
│  │   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐       │ │
│  │   │Gateway 1 │    │Gateway 2 │    │Gateway 3 │    │Gateway N │       │ │
│  │   └────┬─────┘    └────┬─────┘    └────┬─────┘    └────┬─────┘       │ │
│  │        └───────────────┴───────────────┴───────────────┘              │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│                                   │                                         │
│                                   ▼                                         │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                        NATS Cluster                                    │ │
│  │                    （实时推送总线，延迟 < 1ms）                          │ │
│  │    所有实时推送统一走这里：普通消息、流式消息、输入状态、在线状态...       │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                   │                                         │
│                                   ▼                                         │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                          业务服务层                                    │ │
│  │    ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │ │
│  │    │ 消息服务  │ │ 用户服务  │ │ 群组服务  │ │ Bot 服务 │ │Webhook   │  │ │
│  │    └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘  │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                   │                                         │
│                 ┌─────────────────┼─────────────────┐                       │
│                 ▼                 ▼                 ▼                       │
│  ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐         │
│  │    PostgreSQL     │ │   Kafka Cluster   │ │      Valkey       │         │
│  │     (持久化)      │ │    (异步任务)      │ │      (缓存)       │         │
│  │   消息+元数据     │ │  索引/审计/统计    │ │  会话/在线状态    │         │
│  └───────────────────┘ └─────────┬─────────┘ └───────────────────┘         │
│                                  │                                          │
│                                  ▼                                          │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                         Worker 集群                                    │ │
│  │        索引 │ 离线推送 │ Webhook │ 审计 │ 统计 │ 归档 │ ...            │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
│  ┌───────────────────────────────────────────────────────────────────────┐ │
│  │                    服务发现: Gossip (memberlist)                       │ │
│  └───────────────────────────────────────────────────────────────────────┘ │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、技术选型与依据

### 2.1 最终技术栈

|层级|选择|职责|性能|
|---|---|---|---|
|**语言**|Go|全栈开发|-|
|**通信协议**|WebSocket + Protobuf|客户端通信|省带宽 50%+|
|**实时推送**|NATS|Gateway 间实时通信|千万级 msg/s|
|**消息队列**|Kafka|异步任务处理|百万级 msg/s|
|**主数据库**|PostgreSQL|消息+元数据持久化|5万+ TPS|
|**缓存**|Valkey|会话/在线状态|百万级 QPS|
|**搜索**|Meilisearch|消息全文搜索|按需升级|
|**服务发现**|Gossip (memberlist)|节点发现|零依赖|

### 2.2 为什么选 Go

```go
// 一个连接一个 goroutine，代码直观
func handleConnection(conn net.Conn) {
    for {
        msg, err := readMessage(conn)
        if err != nil {
            return
        }
        go processMessage(msg) // 轻松并发
    }
}
// 单机可支撑 10 万+ 长连接
```

|对比项|Go|Java|Rust|Node.js|
|---|---|---|---|---|
|并发模型|Goroutine (轻量)|Thread (重)|async (复杂)|Event Loop|
|编译速度|快|慢|慢|无需编译|
|部署|单二进制|JVM 依赖|单二进制|Node 运行时|
|内存占用|低|高|最低|中|
|开发效率|高|中|低|高|

**选择 Go 的理由**：

- Goroutine 轻量并发：一个连接一个 goroutine，代码直观，单机可支撑 10 万+ 长连接
- 编译快速：秒级编译，快速迭代
- 单二进制部署：无运行时依赖，简化运维
- 内存占用低：相比 Java/JVM，内存效率更高
- 生态完善：NATS、Pion WebRTC 均为 Go 原生实现

### 2.3 NATS vs Redis Pub/Sub vs gRPC 直连

|维度|NATS|Redis Pub/Sub|gRPC 直连|
|---|---|---|---|
|性能|千万级 msg/s|百万级|取决于连接数|
|延迟|< 1ms|1-5ms|< 1ms|
|集群|原生支持|有限制|N*(N-1) 连接|
|可靠性|高|不保证投递|高|
|部署|单二进制|复用现有|无额外组件|
|适合规模|任意|< 10 节点|< 50 节点|

**选择 NATS 的理由**：

- Go 原生实现，单二进制部署
- 单节点千万级消息吞吐
- 内置集群，运维简单
- 延迟 P99 < 1ms

### 2.4 Kafka vs PostgreSQL 队列

|维度|Kafka|PostgreSQL SKIP LOCKED|
|---|---|---|
|吞吐量|百万级 msg/s|5000-10000 msg/s|
|延迟|毫秒级|毫秒级|
|持久化|优秀|优秀|
|运维复杂度|中等|零（复用 PG）|
|适合规模|十万+ TPS|< 1 万 TPS|

**结论**：十万+ TPS 必须用 Kafka，PostgreSQL 队列作为初期简化方案。

### 2.5 Valkey vs Dragonfly

|维度|Valkey|Dragonfly|
|---|---|---|
|Redis 兼容|99.9%|95%+|
|单节点性能|~10 万 QPS|~100 万 QPS|
|多线程|否|是|
|社区成熟度|高 (Linux Foundation)|中等|

**建议**：初期 Valkey，追求极致性能可切换 Dragonfly（协议兼容）。

---

## 三、消息同步机制（混合方案）

### 3.1 方案对比

|方案|原理|优点|缺点|
|---|---|---|---|
|**用户维度 Seq** (OpenIM)|每用户一个递增 seq，写入 inbox|同步简单|大群写扩散严重|
|**会话维度存储**|消息按会话存储|无写扩散|同步逻辑复杂|
|**混合方案** (本文)|用户 seq + 会话 seq|兼顾两者|实现稍复杂|

### 3.2 混合方案设计

**核心思路**：小群用写扩散（更新每个成员的 sync_seq），大群用读扩散（只更新会话 seq）。

```
┌──────────────────────────────────────────────────────────────────────┐
│                    混合同步方案                                       │
├──────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  1. user_sync_state（每用户一条，判断"有无新消息"）                    │
│  ┌──────────┬──────────┬─────────────────┐                          │
│  │ user_id  │ sync_seq │ updated_at      │                          │
│  ├──────────┼──────────┼─────────────────┤                          │
│  │ Alice    │ 1523     │ 2024-01-15 10:30│                          │
│  └──────────┴──────────┴─────────────────┘                          │
│                                                                      │
│  2. user_conversations（version 标记会话何时变更）                    │
│  ┌──────────┬─────────────────┬─────────┬───────────────┐           │
│  │ user_id  │ conversation_id │ version │ last_msg_id   │           │
│  ├──────────┼─────────────────┼─────────┼───────────────┤           │
│  │ Alice    │ conv_A          │ 1520    │ 789012345...  │           │
│  │ Alice    │ conv_B          │ 1523    │ 789012346...  │           │
│  └──────────┴─────────────────┴─────────┴───────────────┘           │
│                                                                      │
│  version 含义：该会话最后变更时，用户的 sync_seq 值                   │
│  → 查询 version > local_sync_seq 即可找出有新消息的会话              │
│                                                                      │
│  3. conversation_sync_state（大群专用，避免写扩散）                   │
│  ┌─────────────────┬─────────┬──────────┐                           │
│  │ conversation_id │ seq     │ is_large │                           │
│  ├─────────────────┼─────────┼──────────┤                           │
│  │ big_group_1     │ 5823    │ true     │                           │
│  └─────────────────┴─────────┴──────────┘                           │
│                                                                      │
│  大群（> 200 人）消息只更新会话 seq，不更新每个用户的 sync_seq         │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.3 同步流程

```
客户端                                    服务端
   │                                        │
   │  ① sync_seq=1500                       │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ② server_seq=1523                     │
   │     changed: [conv_A, conv_B]          │  ← version > 1500
   │     large_groups: [big_group_1]        │  ← 需单独检查
   │ ◀───────────────────────────────────   │
   │                                        │
   │  ③ 拉取 conv_A 消息 (id > local)       │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ④ 返回消息列表                         │
   │ ◀───────────────────────────────────   │
   │                                        │
   │  ⑤ 检查 big_group_1 的 seq             │
   │ ───────────────────────────────────▶   │
   │                                        │
   │  ⑥ 更新本地 sync_seq=1523              │
```

### 3.4 发消息写入逻辑

**小群/单聊（≤200人）- 写扩散**：

```sql
BEGIN;
INSERT INTO messages (...) VALUES (...);

UPDATE user_sync_state 
SET sync_seq = sync_seq + 1 
WHERE user_id = ANY($members);

UPDATE user_conversations 
SET version = (SELECT sync_seq FROM user_sync_state WHERE user_id = user_conversations.user_id),
    last_msg_id = $msg_id,
    unread_count = CASE WHEN user_id = $sender THEN 0 ELSE unread_count + 1 END
WHERE conversation_id = $conv_id;
COMMIT;
```

**大群（>200人）- 读扩散**：

```sql
BEGIN;
INSERT INTO messages (...) VALUES (...);

UPDATE conversation_sync_state 
SET seq = seq + 1, last_msg_id = $msg_id 
WHERE conversation_id = $conv_id;
-- 不更新每个用户的 sync_seq，O(1) 写入
COMMIT;
```

---

## 四、实时推送与消息队列

### 4.1 核心设计原则

**❌ 错误设计**：消息队列（Kafka）作为实时推送通道

- 问题：延迟高（10-100ms）、流式消息无法处理、职责混乱

**✅ 正确设计**：分离实时推送和异步任务

|NATS（实时推送）|Kafka（异步任务）|
|---|---|
|普通消息推送|消息索引|
|流式消息推送|离线消息处理|
|输入状态|审计日志|
|在线状态|Webhook 触发|
|已读回执|统计分析|
|**延迟 < 1ms**|**延迟不敏感**|

### 4.2 NATS Subject 设计

|Subject 模式|用途|
|---|---|
|`im.user.{user_id}`|推送给特定用户|
|`im.conv.{conv_id}`|推送给会话所有在线成员|
|`im.gateway.{node_id}`|推送给特定网关节点|
|`im.broadcast`|全局广播（系统公告等）|

**示例**：

- 单聊消息：发布到 `im.conv.{conv_id}`
- 群聊消息：发布到 `im.conv.{conv_id}`
- 流式消息：发布到 `im.conv.{conv_id}`（多次，每个 delta 一次）
- 踢人下线：发布到 `im.user.{user_id}`

### 4.3 统一消息流程

无论普通消息还是流式消息，都走完全相同的路径：

```
业务服务（消息服务 / Bot 服务）
      │
      ├────────────────────────────┐
      │                            │
      ▼                            ▼
  ┌─────────┐                 ┌─────────┐
  │  NATS   │                 │  Kafka  │
  │ 实时推送 │                 │ 异步任务 │
  └────┬────┘                 └────┬────┘
       │                           │
       ▼                           ▼
  ┌─────────┐                 ┌─────────┐
  │ Gateway │                 │ Workers │
  │  集群   │                 │  集群   │
  └────┬────┘                 └─────────┘
       │
       ▼
    用户
```

---

## 五、流式消息设计（LLM Bot）

### 5.1 流式消息协议

```protobuf
// 流式消息开始
message StreamMessageStart {
    int64 message_id = 1;           // 服务端生成的消息 ID
    int64 conversation_id = 2;
    int64 sender_id = 3;            // Bot ID
    int64 reply_to_id = 4;          // 回复的用户消息 ID
    int64 timestamp = 5;
}

// 流式消息增量
message StreamMessageDelta {
    int64 message_id = 1;
    string delta = 2;               // 增量内容
    int32 index = 3;                // 序号（处理乱序）
}

// 流式消息结束
message StreamMessageEnd {
    int64 message_id = 1;
    string full_content = 2;        // 完整内容（可选）
    int32 total_tokens = 3;
    bool success = 4;
    string error = 5;
}
```

### 5.2 流式消息完整流程

```
1. 用户发送 "@Bot 帮我写个故事"
   │
   ▼
2. Gateway → 消息服务
   │
   ├─ 存储用户消息
   ├─ 发布用户消息到 NATS（其他人看到）
   ├─ 发布到 Kafka（异步任务）
   └─ 调用 Bot 服务

3. Bot 服务处理
   │
   ├─ 生成消息 ID（雪花算法）
   │
   ├─ 发布 StreamStart 到 NATS ─────────────────▶ Gateway → 用户
   │   im.conv.{conv_id}
   │
   ├─ 调用 LLM API（流式）
   │   │
   │   ├─ 收到 token_1
   │   │   └─ 发布 StreamDelta 到 NATS ─────────▶ Gateway → 用户
   │   │
   │   ├─ 收到 token_2
   │   │   └─ 发布 StreamDelta 到 NATS ─────────▶ Gateway → 用户
   │   │
   │   └─ ... 重复 ...
   │
   ├─ 发布 StreamEnd 到 NATS ───────────────────▶ Gateway → 用户
   │
   ├─ 写入完整消息到 PostgreSQL
   │
   └─ 发布到 Kafka（索引、审计等异步任务）
```

**关键点**：

- 所有流式内容通过 NATS 实时推送
- 和普通消息走完全相同的 NATS 通道
- 完成后才写数据库和 Kafka
- 离线用户收到的是最终完整消息

### 5.3 优化：批量合并 delta

建议每 50-100ms 或累积 10 个 token 后合并发送，减少网络开销：

```go
type StreamBatcher struct {
    deltas    []string
    lastFlush time.Time
    interval  time.Duration // 建议 50-100ms
}

func (b *StreamBatcher) Add(delta string) {
    b.deltas = append(b.deltas, delta)
    
    if time.Since(b.lastFlush) >= b.interval || len(b.deltas) >= 10 {
        combined := strings.Join(b.deltas, "")
        b.flush(combined)
    }
}
```

---

## 六、数据库设计与优化

### 6.1 核心表结构

```sql
-- ============================================
-- 用户相关
-- ============================================

CREATE TABLE users (
    id          bigint PRIMARY KEY,           -- 雪花 ID
    username    varchar(64) UNIQUE NOT NULL,
    nickname    varchar(64),
    avatar_url  varchar(512),
    status      smallint DEFAULT 1,
    created_at  timestamptz DEFAULT now(),
    updated_at  timestamptz DEFAULT now()
);

-- ============================================
-- 同步状态
-- ============================================

CREATE TABLE user_sync_state (
    user_id     bigint PRIMARY KEY,
    sync_seq    bigint DEFAULT 0,
    updated_at  timestamptz DEFAULT now()
);

CREATE TABLE conversation_sync_state (
    conversation_id bigint PRIMARY KEY,
    seq             bigint DEFAULT 0,
    last_msg_id     bigint,
    is_large        boolean DEFAULT false,    -- > 200 人标记为大群
    updated_at      timestamptz DEFAULT now()
);

-- ============================================
-- 会话相关
-- ============================================

CREATE TABLE conversations (
    id          bigint PRIMARY KEY,
    type        smallint NOT NULL,            -- 1:单聊 2:群聊 3:频道
    name        varchar(128),
    avatar_url  varchar(512),
    owner_id    bigint,
    settings    jsonb DEFAULT '{}',
    created_at  timestamptz DEFAULT now(),
    updated_at  timestamptz DEFAULT now()
);

CREATE TABLE conversation_members (
    conversation_id bigint NOT NULL,
    user_id         bigint NOT NULL,
    role            smallint DEFAULT 0,
    nickname        varchar(64),
    is_muted        boolean DEFAULT false,
    joined_at       timestamptz DEFAULT now(),
    PRIMARY KEY (conversation_id, user_id)
);

CREATE TABLE user_conversations (
    user_id           bigint NOT NULL,
    conversation_id   bigint NOT NULL,
    version           bigint DEFAULT 0,
    conv_acked_seq    bigint DEFAULT 0,       -- 大群用
    last_read_msg_id  bigint DEFAULT 0,
    last_msg_id       bigint DEFAULT 0,
    last_msg_at       timestamptz,
    unread_count      int DEFAULT 0,
    is_pinned         boolean DEFAULT false,
    is_muted          boolean DEFAULT false,
    is_deleted        boolean DEFAULT false,
    updated_at        timestamptz DEFAULT now(),
    PRIMARY KEY (user_id, conversation_id)
);

CREATE INDEX idx_user_conv_version ON user_conversations (user_id, version);

-- ============================================
-- 消息（分区）
-- ============================================

CREATE TABLE messages (
    id              bigint NOT NULL,
    conversation_id bigint NOT NULL,
    sender_id       bigint NOT NULL,
    content_type    smallint NOT NULL,
    content         jsonb NOT NULL,
    reply_to_id     bigint,
    mentions        bigint[],
    status          smallint DEFAULT 1,
    created_at      timestamptz DEFAULT now(),
    PRIMARY KEY (conversation_id, id)
) PARTITION BY HASH (conversation_id);

-- 创建 16 个分区
DO $$ BEGIN
    FOR i IN 0..15 LOOP
        EXECUTE format(
            'CREATE TABLE messages_p%s PARTITION OF messages 
             FOR VALUES WITH (MODULUS 16, REMAINDER %s)', i, i);
    END LOOP;
END $$;
```

### 6.2 消息内容结构（Block 数组 - 支持图文混排）

采用 **Block 数组** 设计（参考 Slack/Notion/飞书），支持任意类型混排：

```go
// ✅ Block 数组 - 支持任意混排（图文、视频、@提及等）
type MessageContent struct {
    Blocks []ContentBlock `json:"blocks"`
}

type ContentBlock struct {
    Type string          `json:"type"`  // text, image, video, file, mention, link, code, quote...
    Data json.RawMessage `json:"data"`  // 具体内容，根据 Type 解析
}

// ==================== 各 Block 类型定义 ====================

type TextBlock struct {
    Text  string     `json:"text"`
    Style *TextStyle `json:"style,omitempty"`  // 可选：加粗、斜体、链接等
}

type TextStyle struct {
    Bold      bool   `json:"bold,omitempty"`
    Italic    bool   `json:"italic,omitempty"`
    Strike    bool   `json:"strike,omitempty"`
    Code      bool   `json:"code,omitempty"`
    Link      string `json:"link,omitempty"`      // 文字链接
    Color     string `json:"color,omitempty"`
}

type ImageBlock struct {
    URL       string `json:"url"`
    Thumbnail string `json:"thumbnail"`
    Width     int    `json:"width"`
    Height    int    `json:"height"`
    Size      int64  `json:"size"`
    Alt       string `json:"alt,omitempty"`
}

type VideoBlock struct {
    URL       string `json:"url"`
    Thumbnail string `json:"thumbnail"`
    Duration  int    `json:"duration"`   // 秒
    Width     int    `json:"width"`
    Height    int    `json:"height"`
    Size      int64  `json:"size"`
}

type FileBlock struct {
    URL      string `json:"url"`
    Name     string `json:"name"`
    Size     int64  `json:"size"`
    MimeType string `json:"mime_type"`
}

type VoiceBlock struct {
    URL      string `json:"url"`
    Duration int    `json:"duration"`   // 秒
    Size     int64  `json:"size"`
}

type MentionBlock struct {
    UserID   int64  `json:"user_id"`
    Nickname string `json:"nickname"`
}

type LinkPreviewBlock struct {
    URL         string `json:"url"`
    Title       string `json:"title"`
    Description string `json:"description,omitempty"`
    Image       string `json:"image,omitempty"`
    SiteName    string `json:"site_name,omitempty"`
}

type LocationBlock struct {
    Latitude  float64 `json:"latitude"`
    Longitude float64 `json:"longitude"`
    Name      string  `json:"name,omitempty"`
    Address   string  `json:"address,omitempty"`
}

type CodeBlock struct {
    Code     string `json:"code"`
    Language string `json:"language,omitempty"`
}

type QuoteBlock struct {
    Text     string `json:"text"`
    AuthorID int64  `json:"author_id,omitempty"`
}
```

**图文混排示例**：`嘻嘻嘻【图片】哈哈哈【视频】再见`

```json
{
  "blocks": [
    {"type": "text", "data": {"text": "嘻嘻嘻"}},
    {"type": "image", "data": {"url": "https://cdn.example.com/a.jpg", "thumbnail": "...", "width": 800, "height": 600}},
    {"type": "text", "data": {"text": "哈哈哈"}},
    {"type": "video", "data": {"url": "https://cdn.example.com/b.mp4", "thumbnail": "...", "duration": 15}},
    {"type": "text", "data": {"text": "再见"}}
  ]
}
```

**@提及 + 文字示例**：`@张三 你好，看看这个 @李四`

```json
{
  "blocks": [
    {"type": "mention", "data": {"user_id": 10001, "nickname": "张三"}},
    {"type": "text", "data": {"text": " 你好，看看这个 "}},
    {"type": "mention", "data": {"user_id": 10002, "nickname": "李四"}}
  ]
}
```

### 6.3 客户端渲染逻辑

```typescript
// React/Vue 客户端渲染示例
function renderMessage(content: MessageContent) {
  return content.blocks.map((block, index) => {
    switch (block.type) {
      case 'text':
        return <TextSpan key={index} text={block.data.text} style={block.data.style} />;
      case 'image':
        return <ImageView key={index} src={block.data.url} thumbnail={block.data.thumbnail} />;
      case 'video':
        return <VideoPlayer key={index} src={block.data.url} poster={block.data.thumbnail} />;
      case 'mention':
        return <MentionTag key={index} userId={block.data.user_id} name={block.data.nickname} />;
      case 'file':
        return <FileAttachment key={index} name={block.data.name} url={block.data.url} />;
      case 'link':
        return <LinkPreview key={index} url={block.data.url} title={block.data.title} />;
      case 'code':
        return <CodeBlock key={index} code={block.data.code} language={block.data.language} />;
      default:
        // ✅ 关键：未知类型优雅降级，不会崩溃
        return <UnknownBlock key={index} type={block.type} />;
    }
  });
}
```

### 6.4 如何扩展新 Block 类型

**扩展流程（无需修改 proto 文件）**：

```
步骤 1: 服务端定义新类型的 Go struct
       ↓
步骤 2: 客户端添加对应的渲染组件
       ↓
步骤 3: 完成！老客户端显示 "不支持的消息类型"，不会崩溃
```

**示例：新增投票 Block**

```go
// 步骤 1: 服务端 - 定义新类型（不改 proto）
type PollBlock struct {
    Question    string   `json:"question"`
    Options     []string `json:"options"`
    MultiSelect bool     `json:"multi_select"`
    ExpiresAt   int64    `json:"expires_at,omitempty"`
}

// 构造投票消息
msg := MessageContent{
    Blocks: []ContentBlock{
        {Type: "text", Data: mustMarshal(TextBlock{Text: "请投票："})},
        {Type: "poll", Data: mustMarshal(PollBlock{
            Question: "下周团建去哪？",
            Options:  []string{"爬山", "密室逃脱", "剧本杀"},
        })},
    },
}
```

```typescript
// 步骤 2: 客户端 - 添加渲染组件
case 'poll':
  return <PollCard 
    key={index} 
    question={block.data.question} 
    options={block.data.options} 
    onVote={handleVote} 
  />;
```

**兼容性矩阵**：

|场景|新客户端|老客户端|
|---|---|---|
|收到已知类型|✅ 正常渲染|✅ 正常渲染|
|收到新类型|✅ 正常渲染|⚠️ 显示"不支持的消息类型，请升级"|
|发送新类型|✅ 正常发送|❌ 无此功能|

### 6.5 数据库扩展路径

|场景|推荐方案|
|---|---|
|< 10万 TPS|PostgreSQL + 分区 + Citus|
|10万-50万 TPS|PostgreSQL + Citus 集群|
|> 50万 TPS|消息存储迁移到 ScyllaDB/Cassandra|
|全球多区域|Cassandra/CockroachDB|

### 6.6 PostgreSQL 优化配置

```ini
# postgresql.conf (16C64G 服务器)

max_connections = 500
shared_buffers = 16GB
effective_cache_size = 48GB
work_mem = 128MB
maintenance_work_mem = 2GB

wal_buffers = 64MB
max_wal_size = 8GB
synchronous_commit = off          # 异步提交

max_parallel_workers_per_gather = 4
max_parallel_workers = 8

random_page_cost = 1.1            # SSD
effective_io_concurrency = 200
```

---

## 七、WebRTC 音视频架构

### 7.1 架构选型：SFU vs MCU vs P2P

|指标|Mesh (P2P)|SFU|MCU|
|---|---|---|---|
|**参与者上限**|2-4人|5-100+人|无限制|
|**服务器 CPU**|无|低|极高|
|**服务器成本**|零|中等|高（约 SFU 100倍）|
|**客户端带宽**|高|中等|最低|
|**延迟**|最低|低|较高|
|**端到端加密**|✓|✓|✗|
|**适用场景**|小型私密会议|大多数视频会议|低带宽设备|

**结论**：SFU（Selective Forwarding Unit）是目前最主流的多人会议架构，服务器接收所有参与者的流，选择性转发给其他人，不进行转码。每个参与者上传 1 路流，接收 N-1 路流。

### 7.2 SFU 选型推荐

|SFU|语言|优势|适用场景|
|---|---|---|---|
|**mediasoup**|Node.js/Rust|最灵活，低级API控制力强|定制化需求|
|**Pion/ion-sfu**|Go|与Go后端统一技术栈|Go生态，简化运维|
|**LiveKit**|Go|云友好，SDK完善|快速上线|
|**Janus**|C|企业级，有商业支持|企业场景|

**推荐组合**：如果后端是 Go，优先考虑 **Pion + ion-sfu**；如果需要最大灵活性，选择 **mediasoup**。

### 7.3 WebRTC 协议栈

```
┌─────────────────────────────────────────────────────────────────┐
│                       WebRTC 协议栈                              │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │   音视频    │    │  数据通道   │    │   信令      │         │
│  │ (SRTP/SRTCP)│    │   (SCTP)    │    │ (WebSocket) │         │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘         │
│         └─────────┬────────┘                  │                 │
│           ┌───────┴───────┐                   │                 │
│           │     DTLS      │  ←── 加密层       │                 │
│           └───────┬───────┘                   │                 │
│           ┌───────┴───────┐                   │                 │
│           │      ICE      │  ←── 连接建立     │                 │
│           └───────┬───────┘                   │                 │
│         ┌─────────┼─────────┐                 │                 │
│    ┌────┴────┐┌───┴───┐┌────┴────┐           │                 │
│    │  STUN   ││ TURN  ││  直连   │           │                 │
│    └─────────┘└───────┘└─────────┘           │                 │
│                   │                           │                 │
│           ┌───────┴───────┐           ┌──────┴──────┐         │
│           │   UDP / TCP   │           │    TCP      │         │
│           └───────────────┘           └─────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

### 7.4 WebRTC 连接建立流程

```
┌─────────────┐         信令服务器         ┌─────────────┐
│   Peer A    │                            │   Peer B    │
│  (发起方)   │                            │  (接收方)   │
└──────┬──────┘                            └──────┬──────┘
       │                                          │
       │ 1. 获取本地媒体流                         │
       │    getUserMedia()                        │
       │                                          │
       │ 2. 创建 PeerConnection                   │
       │    添加本地轨道                           │
       │                                          │
       │ 3. createOffer()                         │
       │ 4. setLocalDescription(offer)            │
       │ 5. 发送 offer                            │
       │ ──────────────────────────────────────>  │
       │                                          │ 6. setRemoteDescription(offer)
       │                                          │ 7. createAnswer()
       │                                          │ 8. setLocalDescription(answer)
       │                   发送 answer             │
       │ <────────────────────────────────────── │
       │ 9. setRemoteDescription(answer)          │
       │                                          │
       │ === ICE 候选交换（双向并行）===            │
       │                                          │
       │ ──────── candidate ────────────────────> │
       │ <─────── candidate ─────────────────────│
       │                                          │
       │ === DTLS 握手 + SRTP 建立 ===             │
       │                                          │
       │ ========== 媒体流传输开始 =========        │
```

### 7.5 NAT 类型与穿透难度

|NAT 类型|端口映射|入站过滤|穿透难度|
|---|---|---|---|
|Full Cone|固定|无限制|最容易|
|Restricted Cone|固定|限制 IP|较容易|
|Port Restricted Cone|固定|限制 IP+Port|中等|
|**Symmetric**|随目标变化|限制 IP+Port|**最难（需 TURN）**|

**关键洞察**：大多数家用路由器是 Port Restricted Cone，移动运营商 CG-NAT 多为 Symmetric NAT。两个 Symmetric NAT 或 Symmetric + Port Restricted 组合必须使用 TURN。

---

## 八、服务发现机制

### 8.1 Gossip 实现

```go
import "github.com/hashicorp/memberlist"

type GatewayMeta struct {
    NodeID       string `json:"node_id"`
    PublicAddr   string `json:"public_addr"`
    InternalAddr string `json:"internal_addr"`
    Connections  int    `json:"connections"`
}

type GatewayDelegate struct {
    meta GatewayMeta
    mu   sync.RWMutex
}

func (d *GatewayDelegate) NodeMeta(limit int) []byte {
    d.mu.RLock()
    defer d.mu.RUnlock()
    data, _ := json.Marshal(d.meta)
    return data
}

func NewGossipNode(cfg *GossipConfig) (*memberlist.Memberlist, error) {
    delegate := &GatewayDelegate{
        meta: GatewayMeta{
            NodeID:       cfg.NodeID,
            PublicAddr:   cfg.PublicAddr,
            InternalAddr: cfg.InternalAddr,
        },
    }

    mlConfig := memberlist.DefaultLANConfig()
    mlConfig.Name = cfg.NodeID
    mlConfig.BindAddr = cfg.BindAddr
    mlConfig.BindPort = cfg.BindPort
    mlConfig.Delegate = delegate

    list, err := memberlist.Create(mlConfig)
    if err != nil {
        return nil, err
    }

    if len(cfg.Seeds) > 0 {
        list.Join(cfg.Seeds)
    }

    return list, nil
}
```

---

## 九、核心业务流程

### 9.1 消息发送完整流程

```
1. 客户端
   └─▶ WebSocket 发送 (Protobuf)

2. Gateway
   ├─ Protobuf 解码
   ├─ Token 鉴权
   ├─ 频率限制
   └─ 转发到消息服务

3. 消息服务
   ├─ 生成雪花 ID
   ├─ 写入 PostgreSQL
   ├─ 发布到 NATS (im.conv.{conv_id}) ─────────▶ 实时推送
   ├─ 发布到 Kafka ────────────────────────────▶ 异步任务
   └─ 返回 ACK

4. Gateway 集群（通过 NATS 订阅）
   ├─ 收到 NATS 消息
   ├─ 查找本地在线用户
   └─ WebSocket 推送

5. Kafka Workers（异步）
   ├─ 索引到 Meilisearch
   ├─ 离线用户 Push 通知
   ├─ Webhook 触发
   └─ 审计日志
```

### 9.2 用户上线同步流程

```
1. 建立连接
   ├─ WebSocket 握手
   ├─ Token 验证
   ├─ 注册到 Valkey (user_id → gateway_node)
   └─ 订阅 NATS (im.user.{user_id} + 用户的会话列表)

2. 增量同步
   ├─ 客户端发送 local_sync_seq
   ├─ 服务端返回 changed conversations (version > local_seq)
   └─ 客户端逐个拉取新消息

3. 实时接收
   └─ 通过 NATS 订阅自动接收新消息
```

---

## 十、核心代码实现

### 10.1 雪花 ID 生成器

```go
type Snowflake struct {
    mu        sync.Mutex
    epoch     int64
    machineID int64
    sequence  int64
    lastTime  int64
}

func NewSnowflake(machineID int64) *Snowflake {
    return &Snowflake{
        epoch:     1704067200000, // 2024-01-01
        machineID: machineID,
    }
}

func (s *Snowflake) Generate() int64 {
    s.mu.Lock()
    defer s.mu.Unlock()

    now := time.Now().UnixMilli() - s.epoch
    if now == s.lastTime {
        s.sequence = (s.sequence + 1) & 0xFFF
        if s.sequence == 0 {
            for now <= s.lastTime {
                now = time.Now().UnixMilli() - s.epoch
            }
        }
    } else {
        s.sequence = 0
    }
    s.lastTime = now

    return (now << 22) | (s.machineID << 12) | s.sequence
}
```

### 10.2 NATS 实时总线

```go
type RealtimeBus struct {
    nc     *nats.Conn
    nodeID string
}

func NewRealtimeBus(url, nodeID string) (*RealtimeBus, error) {
    nc, err := nats.Connect(url,
        nats.ReconnectWait(time.Second),
        nats.MaxReconnects(-1),
    )
    if err != nil {
        return nil, err
    }
    return &RealtimeBus{nc: nc, nodeID: nodeID}, nil
}

// 发布到会话
func (b *RealtimeBus) PublishToConversation(convID int64, msg proto.Message) error {
    data, _ := proto.Marshal(msg)
    return b.nc.Publish(fmt.Sprintf("im.conv.%d", convID), data)
}

// 发布到用户
func (b *RealtimeBus) PublishToUser(userID int64, msg proto.Message) error {
    data, _ := proto.Marshal(msg)
    return b.nc.Publish(fmt.Sprintf("im.user.%d", userID), data)
}

// 订阅会话
func (b *RealtimeBus) SubscribeConversation(convID int64, handler func([]byte)) (*nats.Subscription, error) {
    return b.nc.Subscribe(fmt.Sprintf("im.conv.%d", convID), func(msg *nats.Msg) {
        handler(msg.Data)
    })
}
```

### 10.3 消息服务

```go
type MessageService struct {
    db        *pgxpool.Pool
    bus       *RealtimeBus      // NATS
    queue     *kafka.Producer   // Kafka
    snowflake *Snowflake
}

func (s *MessageService) SendMessage(ctx context.Context, req *SendMessageRequest) (*Message, error) {
    msg := &Message{
        ID:             s.snowflake.Generate(),
        ConversationID: req.ConversationID,
        SenderID:       req.SenderID,
        ContentType:    req.ContentType,
        Content:        req.Content,
        CreatedAt:      time.Now(),
    }

    // 1. 持久化
    if err := s.saveMessage(ctx, msg); err != nil {
        return nil, err
    }

    // 2. 实时推送（NATS）
    pushMsg := &pb.RecvMessage{
        Id:             msg.ID,
        ConversationId: msg.ConversationID,
        SenderId:       msg.SenderID,
        ContentType:    int32(msg.ContentType),
        Content:        msg.Content.ToBytes(),
        CreatedAt:      msg.CreatedAt.UnixMilli(),
    }
    s.bus.PublishToConversation(msg.ConversationID, pushMsg)

    // 3. 异步任务（Kafka）
    s.queue.Produce(&kafka.Message{
        Topic: "message-events",
        Key:   []byte(fmt.Sprintf("%d", msg.ConversationID)),
        Value: msg.ToJSON(),
    })

    return msg, nil
}
```

### 10.4 Bot 服务（流式）

```go
type BotService struct {
    bus       *RealtimeBus
    queue     *kafka.Producer
    llmClient *llm.Client
    snowflake *Snowflake
}

func (s *BotService) HandleMention(ctx context.Context, userMsg *Message) error {
    botMsgID := s.snowflake.Generate()
    convID := userMsg.ConversationID

    // 1. StreamStart
    s.bus.PublishToConversation(convID, &pb.StreamMessageStart{
        MessageId:      botMsgID,
        ConversationId: convID,
        SenderId:       botUserID,
        Timestamp:      time.Now().UnixMilli(),
    })

    // 2. 流式调用 LLM
    var fullContent strings.Builder
    stream, _ := s.llmClient.ChatStream(ctx, buildPrompt(userMsg))

    index := 0
    for chunk := range stream {
        fullContent.WriteString(chunk.Delta)
        index++

        // StreamDelta - 和普通消息走相同的 NATS 通道
        s.bus.PublishToConversation(convID, &pb.StreamMessageDelta{
            MessageId: botMsgID,
            Delta:     chunk.Delta,
            Index:     int32(index),
        })
    }

    // 3. StreamEnd
    s.bus.PublishToConversation(convID, &pb.StreamMessageEnd{
        MessageId:   botMsgID,
        FullContent: fullContent.String(),
        Success:     true,
    })

    // 4. 持久化（使用 Block 数组格式）
    msg := &Message{
        ID:             botMsgID,
        ConversationID: convID,
        SenderID:       botUserID,
        ContentType:    ContentTypeRichText,
        Content: MessageContent{
            Blocks: []ContentBlock{
                {Type: "text", Data: TextBlock{Text: fullContent.String()}},
            },
        },
    }
    s.saveMessage(ctx, msg)

    // 5. 异步任务
    s.queue.Produce(&kafka.Message{
        Topic: "message-events",
        Value: msg.ToJSON(),
    })

    return nil
}
```

### 10.5 Gateway 连接管理

```go
type Gateway struct {
    connections sync.Map  // userID -> *Connection
    bus         *RealtimeBus
    subs        sync.Map  // key -> *nats.Subscription
}

func (g *Gateway) OnUserConnected(userID int64, conn *Connection) {
    g.connections.Store(userID, conn)

    // 订阅用户消息
    sub, _ := g.bus.SubscribeUser(userID, func(data []byte) {
        conn.Send(data)
    })
    g.subs.Store(fmt.Sprintf("user:%d", userID), sub)

    // 订阅用户的会话
    convIDs := g.getUserConversations(userID)
    for _, convID := range convIDs {
        sub, _ := g.bus.SubscribeConversation(convID, func(data []byte) {
            if c, ok := g.connections.Load(userID); ok {
                c.(*Connection).Send(data)
            }
        })
        g.subs.Store(fmt.Sprintf("conv:%d:%d", userID, convID), sub)
    }
}

func (g *Gateway) OnUserDisconnected(userID int64) {
    g.connections.Delete(userID)

    // 取消所有订阅
    g.subs.Range(func(key, value interface{}) bool {
        if strings.HasPrefix(key.(string), fmt.Sprintf("user:%d", userID)) ||
           strings.HasPrefix(key.(string), fmt.Sprintf("conv:%d:", userID)) {
            value.(*nats.Subscription).Unsubscribe()
            g.subs.Delete(key)
        }
        return true
    })
}
```

### 10.6 同步服务

```go
type SyncService struct {
    db    *pgxpool.Pool
    cache *Cache
}

type SyncRequest struct {
    UserID         int64           `json:"user_id"`
    LocalSyncSeq   int64           `json:"local_sync_seq"`
    LargeGroupSeqs map[int64]int64 `json:"large_group_seqs"`
}

type SyncResponse struct {
    ServerSyncSeq        int64              `json:"server_sync_seq"`
    ChangedConversations []ConversationSync `json:"changed_conversations"`
    LargeGroupChanges    []LargeGroupSync   `json:"large_group_changes"`
    FullSyncRequired     bool               `json:"full_sync_required"`
}

func (s *SyncService) Sync(ctx context.Context, req *SyncRequest) (*SyncResponse, error) {
    resp := &SyncResponse{}

    // 获取服务端 sync_seq
    var serverSeq int64
    s.db.QueryRow(ctx, `
        SELECT sync_seq FROM user_sync_state WHERE user_id = $1
    `, req.UserID).Scan(&serverSeq)
    resp.ServerSyncSeq = serverSeq

    // 离线太久，建议全量
    if serverSeq-req.LocalSyncSeq > 10000 {
        resp.FullSyncRequired = true
        return resp, nil
    }

    // 查询变更的普通会话
    rows, _ := s.db.Query(ctx, `
        SELECT uc.conversation_id, uc.last_msg_id, uc.last_msg_at, uc.unread_count
        FROM user_conversations uc
        JOIN conversation_sync_state cs ON cs.conversation_id = uc.conversation_id
        WHERE uc.user_id = $1 AND uc.version > $2 AND cs.is_large = false
    `, req.UserID, req.LocalSyncSeq)
    defer rows.Close()

    for rows.Next() {
        var conv ConversationSync
        rows.Scan(&conv.ConversationID, &conv.LastMsgID, &conv.LastMsgAt, &conv.UnreadCount)
        resp.ChangedConversations = append(resp.ChangedConversations, conv)
    }

    // 检查大群变更
    // ... 省略大群检查逻辑

    return resp, nil
}
```

---

## 十一、项目结构

```
im-server/
├── cmd/
│   ├── gateway/                 # 网关服务
│   │   └── main.go
│   ├── api/                     # HTTP API
│   │   └── main.go
│   ├── bot/                     # Bot 服务
│   │   └── main.go
│   └── worker/                  # 后台任务
│       └── main.go
│
├── internal/
│   ├── gateway/
│   │   ├── server.go
│   │   ├── connection.go
│   │   └── handler.go
│   │
│   ├── service/
│   │   ├── message.go
│   │   ├── sync.go
│   │   ├── conversation.go
│   │   ├── user.go
│   │   └── bot.go
│   │
│   ├── repository/
│   │   ├── message.go
│   │   ├── conversation.go
│   │   └── cache.go
│   │
│   ├── bus/                     # NATS 封装
│   │   └── realtime.go
│   │
│   ├── queue/                   # Kafka 封装
│   │   └── producer.go
│   │
│   ├── discovery/
│   │   └── gossip.go
│   │
│   └── pkg/
│       ├── snowflake/
│       ├── protocol/            # Protobuf
│       └── auth/
│
├── api/proto/
│   ├── message.proto
│   ├── stream.proto
│   └── gateway.proto
│
├── migrations/
│   └── 001_init.sql
│
├── configs/
│   └── config.yaml
│
├── deployments/
│   ├── docker-compose.yaml
│   └── k8s/
│
├── Dockerfile
├── Makefile
└── README.md
```

---

## 十二、部署与容量规划

### 12.1 十万+ TPS 硬件规划

|组件|数量|配置|说明|月成本|
|---|---|---|---|---|
|Gateway|20|8C16G|每节点 5 万连接|$3,000|
|NATS|3|4C8G|集群，支撑实时推送|$300|
|消息服务|10|8C16G|无状态，按需扩容|$1,500|
|Bot 服务|10|8C16G|按 LLM 调用量|$1,500|
|API 服务|5|4C8G|HTTP API|$600|
|Kafka|5|16C64G|3 副本|$3,000|
|PostgreSQL|3|32C128G|主 + 从 + 备|$4,500|
|Valkey|6|8C64G|3 主 3 从|$2,000|
|Worker|20|4C8G|按任务类型分组|$1,200|
|**总计**|-|-|-|**≈$17,600/月**|

### 12.2 Docker Compose（开发环境）

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_USER: im
      POSTGRES_PASSWORD: im_password
      POSTGRES_DB: im
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  valkey:
    image: valkey/valkey:7
    ports:
      - "6379:6379"

  nats:
    image: nats:2.10
    ports:
      - "4222:4222"
      - "8222:8222"
    command: ["--cluster", "nats://0.0.0.0:6222", "--http_port", "8222"]

  kafka:
    image: bitnami/kafka:3.6
    ports:
      - "9092:9092"
    environment:
      - KAFKA_CFG_NODE_ID=0
      - KAFKA_CFG_PROCESS_ROLES=controller,broker
      - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
      - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER

  gateway:
    build: .
    command: ./gateway
    ports:
      - "8080:8080"
    environment:
      - DB_URL=postgres://im:im_password@postgres:5432/im
      - VALKEY_URL=valkey:6379
      - NATS_URL=nats://nats:4222
      - KAFKA_BROKERS=kafka:9092
    depends_on:
      - postgres
      - valkey
      - nats
      - kafka

volumes:
  postgres_data:
```

---

## 十三、演进路线图

### Phase 1: MVP (0-6月)

- **技术栈**：Go 单体 + PostgreSQL + Valkey + NATS
- **目标**：1万 TPS，1万并发连接
- **特点**：PostgreSQL 队列替代 Kafka，简化运维

### Phase 2: 分布式 (6-12月)

- 引入 Kafka 替换 PG 队列
- Gateway 集群 + Gossip 服务发现
- PostgreSQL 主从 + 分区表
- **目标**：5万 TPS

### Phase 3: 规模化 (12-24月)

- 完整集群化部署（Kubernetes）
- Meilisearch 消息搜索
- PostgreSQL → Citus 或 TiDB
- 多机房部署
- **目标**：十万+ TPS

### Phase 4: 全球化 (24+月)

- 多区域部署
- 边缘接入节点
- 消息存储迁移到 Cassandra（可选）
- **目标**：百万 TPS

---

## 十四、完整功能特性清单

### 14.1 消息核心功能

|特性|优先级|说明|
|---|---|---|
|实时消息转发|P0|消息即时送达|
|**图文混排**|P0|单条消息支持文字+图片+视频+@提及混排|
|流式消息|P0|支持 LLM Bot 流式输出|
|已读回执|P1|显示消息是否已读|
|投递回执|P1|确认消息已送达|
|消息引用/回复|P1|引用某条消息|
|消息撤回|P1|撤回已发送消息|
|消息编辑|P2|修改已发送消息|
|@提及|P1|群聊中 @用户|
|消息 Reaction|P2|表情反应|
|消息搜索|P1|全文搜索|
|正在输入|P2|输入状态指示|

### 14.2 消息类型与 Block 类型

**content_type（消息级别类型）**：

|值|类型|说明|
|---|---|---|
|1|纯文本|`{"blocks":[{"type":"text","data":{"text":"hello"}}]}`|
|2|富文本|图文混排、@提及等复杂内容|
|3|系统消息|入群、退群、撤回提示等|

**Block 类型（内容块类型 - 可随时扩展，无需改 proto）**：

|Block Type|用途|优先级|
|---|---|---|
|`text`|文本段落|P0|
|`image`|图片|P0|
|`video`|视频|P1|
|`file`|文件附件|P1|
|`voice`|语音消息|P1|
|`mention`|@某人|P1|
|`link`|链接预览卡片|P2|
|`location`|地理位置|P2|
|`code`|代码块|P2|
|`quote`|引用|P2|
|`divider`|分隔线|P3|
|`poll`|投票（示例扩展）|P3|
|`card`|卡片消息（示例扩展）|P3|
|_自定义_|_按需扩展..._|-|

### 14.3 群组功能

|特性|优先级|
|---|---|
|创建/解散群|P0|
|群成员管理|P0|
|群角色权限|P1|
|群公告|P2|
|大群支持 (万人)|P1|

### 14.4 扩展功能

|特性|优先级|
|---|---|
|Bot 框架|P1|
|Webhook 集成|P1|
|消息审核/过滤|P2|
|音视频通话|P1|
|屏幕共享|P2|

---

## 附录 A：Protobuf 定义

```protobuf
syntax = "proto3";
package im.v1;

// 通用消息信封
message Envelope {
    int64 seq = 1;
    int64 timestamp = 2;
    oneof payload {
        SendMessageReq send_msg = 10;
        SendMessageAck send_ack = 11;
        RecvMessage recv_msg = 12;
        HeartbeatReq heartbeat = 20;
        HeartbeatResp heartbeat_resp = 21;
        SyncReq sync_req = 30;
        SyncResp sync_resp = 31;
        StreamMessageStart stream_start = 40;
        StreamMessageDelta stream_delta = 41;
        StreamMessageEnd stream_end = 42;
    }
}

// ==================== 消息内容结构（可扩展 Block 数组）====================

message MessageContent {
    repeated ContentBlock blocks = 1;
}

// ✅ 可扩展设计：type + bytes
// 新增 Block 类型无需修改 proto 文件，客户端根据 type 动态解析 data
message ContentBlock {
    string type = 1;       // 类型标识："text", "image", "video", "mention", "my_future_type"...
    bytes  data = 2;       // JSON 序列化的具体内容，客户端根据 type 解析
}

// ==================== 发送/接收消息 ====================

message SendMessageReq {
    int64 conversation_id = 1;
    int64 client_msg_id = 2;
    int32 content_type = 3;       // 1:纯文本 2:富文本 3:系统消息
    MessageContent content = 4;   // Block 数组
    repeated int64 mentions = 5;  // 冗余字段，方便服务端快速提取 @
}

message SendMessageAck {
    int64 client_msg_id = 1;
    int64 server_msg_id = 2;
    int64 server_time = 3;
}

message RecvMessage {
    int64 id = 1;
    int64 conversation_id = 2;
    int64 sender_id = 3;
    int32 content_type = 4;
    MessageContent content = 5;   // Block 数组
    int64 created_at = 6;
}

// ==================== 流式消息（Bot 输出）====================

message StreamMessageStart {
    int64 message_id = 1;
    int64 conversation_id = 2;
    int64 sender_id = 3;
    int64 reply_to_id = 4;
    int64 timestamp = 5;
}

message StreamMessageDelta {
    int64 message_id = 1;
    string delta = 2;             // 文本增量
    int32 index = 3;
}

message StreamMessageEnd {
    int64 message_id = 1;
    MessageContent content = 2;   // 完整内容（Block 数组）
    int32 total_tokens = 3;
    bool success = 4;
    string error = 5;
}

// ==================== 同步 ====================

message SyncReq {
    int64 sync_seq = 1;
    map<int64, int64> large_group_seqs = 2;
}

message SyncResp {
    int64 server_sync_seq = 1;
    repeated ConversationChange changes = 2;
    repeated LargeGroupChange large_groups = 3;
    bool full_sync_required = 4;
}
```

### Block Data JSON Schema（运行时解析）

Block 的 `data` 字段是 JSON 序列化的 bytes，各类型 Schema 定义如下（可随时扩展）：

```typescript
// 类型注册表 - 新增类型只需在这里添加
const BlockSchemas = {
  "text":     { text: string, style?: TextStyle },
  "image":    { url: string, thumbnail: string, width: number, height: number, size?: number, alt?: string },
  "video":    { url: string, thumbnail: string, duration: number, width: number, height: number, size?: number },
  "file":     { url: string, name: string, size: number, mime_type: string },
  "voice":    { url: string, duration: number, size?: number },
  "mention":  { user_id: number, nickname: string },
  "link":     { url: string, title: string, description?: string, image?: string, site_name?: string },
  "location": { latitude: number, longitude: number, name?: string, address?: string },
  "code":     { code: string, language?: string },
  "quote":    { text: string, author_id?: number },
  // ✅ 未来新增类型，无需改 proto，只需服务端和客户端各自添加解析逻辑
  // "poll":     { question: string, options: string[], multi_select: boolean },
  // "card":     { title: string, content: string, buttons: Button[] },
};

interface TextStyle {
  bold?: boolean;
  italic?: boolean;
  strike?: boolean;
  code?: boolean;
  link?: string;
  color?: string;
}
```

---

## 附录 B：方案对比总结

|维度|OpenIM|本方案|
|---|---|---|
|同步模型|用户 Inbox 写扩散|混合 (小群写扩散 + 大群读扩散)|
|实时推送|队列消费推送|NATS 直接推送|
|流式消息|特殊处理|与普通消息统一|
|大群写入|O(N)|O(1)|
|存储成本|高|低|
|推送延迟|10-100ms|< 1ms|
|组件数量|多|适中|

---

## 总结

本文档提供了一套经过验证的现代 IM 系统架构设计方案，核心优势如下：

1. **NATS + Kafka 分离**：NATS 专注实时推送（延迟 < 1ms），Kafka 专注异步任务，职责清晰
2. **混合同步方案**：兼顾小群的简单性和大群的性能，避免万人群写扩散
3. **Go + PostgreSQL + Valkey**：经过验证的黄金组合，生态完善
4. **流式消息统一推送**：与普通消息走相同的 NATS 通道，简化架构
5. **可扩展 Block 消息结构**：type + bytes 设计，新增类型无需改 proto，支持图文混排、@提及等
6. **SFU 音视频架构**：mediasoup/Pion 实现多人会议，支持 Simulcast 带宽自适应

**关键实施建议**：

- 从 MVP 阶段开始，PostgreSQL 队列替代 Kafka 简化运维
- 增加消息幂等性处理，使用 idempotency key 防止重复
- 流式消息增加批量合并机制（50-100ms 间隔）
- 生产环境必须配置 TURN 服务器作为 NAT 穿透后备

这套架构完全能够支撑从 MVP 到十万+ TPS 的演进路径，单人可开发维护。

---

> 文档版本: 3.2  
> 目标: 十万+ TPS，可无限水平扩展  
> 最后更新: 2024-12

## 核心整合架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           与现有系统整合架构                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         公司现有系统                                  │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐            │   │
│  │  │ 用户中心  │  │ 权限系统  │  │ 组织架构  │  │ 业务系统  │            │   │
│  │  │ (SSO)    │  │ (RBAC)   │  │ (HR)     │  │ (项目等)  │            │   │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘            │   │
│  └───────┼─────────────┼─────────────┼─────────────┼────────────────────┘   │
│          │             │             │             │                        │
│          ▼             ▼             ▼             ▼                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      整合层 (Integration Layer)                      │   │
│  │                                                                      │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐               │   │
│  │  │ Auth Adapter │  │ User Syncer  │  │ Group Syncer │               │   │
│  │  │  鉴权适配器   │  │  用户同步器   │  │   群同步器   │               │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘               │   │
│  │                                                                      │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐               │   │
│  │  │ Bot Registry │  │ Webhook Hub  │  │ Event Bridge │               │   │
│  │  │  Bot 注册中心 │  │  Webhook 中心 │  │   事件桥接   │               │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                           IM 核心系统                                │   │
│  │      Gateway │ 消息服务 │ 群组服务 │ Bot 服务 │ NATS │ Kafka          │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 一、鉴权整合

### 1.1 三种整合模式

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           鉴权整合模式                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  模式 A: JWT 共享密钥（推荐，最简单）                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  公司 Auth 服务 ─────签发 JWT────→ 客户端 ─────携带 JWT────→ IM Gateway  │   │
│  │                                                                      │   │
│  │  IM Gateway 使用相同密钥验证 JWT，无需调用外部服务                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  模式 B: Token 验证回调（兼容老系统）                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  客户端 ──携带 Token──→ IM Gateway ──验证请求──→ 公司 Auth 服务          │   │
│  │                              ↑                        │              │   │
│  │                              └────── 返回用户信息 ──────┘              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  模式 C: API Gateway 统一鉴权（企业级）                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  客户端 ──→ Kong/APISIX ──鉴权通过──→ IM Gateway（已验证）              │   │
│  │                │                                                     │   │
│  │                └──→ 注入 X-User-ID / X-Org-ID 等 Header              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 鉴权适配器实现

go

```go
// ==================== 鉴权适配器接口 ====================

type AuthAdapter interface {
    // 验证 Token，返回用户信息
    Verify(ctx context.Context, token string) (*UserIdentity, error)
    // 刷新 Token（可选）
    Refresh(ctx context.Context, token string) (string, error)
}

type UserIdentity struct {
    UserID       int64             `json:"user_id"`
    Username     string            `json:"username"`
    OrgID        int64             `json:"org_id"`        // 组织/租户 ID
    DeptIDs      []int64           `json:"dept_ids"`      // 部门 ID 列表
    Roles        []string          `json:"roles"`         // 角色列表
    Permissions  []string          `json:"permissions"`   // 权限列表
    Extra        map[string]string `json:"extra"`         // 扩展字段
}

// ==================== 模式 A: JWT 共享密钥 ====================

type JWTAuthAdapter struct {
    secret     []byte
    issuer     string
    userSyncer *UserSyncer  // 用于同步用户信息到 IM 系统
}

func (a *JWTAuthAdapter) Verify(ctx context.Context, tokenStr string) (*UserIdentity, error) {
    token, err := jwt.Parse(tokenStr, func(t *jwt.Token) (interface{}, error) {
        return a.secret, nil
    })
    if err != nil || !token.Valid {
        return nil, ErrInvalidToken
    }

    claims := token.Claims.(jwt.MapClaims)
    
    identity := &UserIdentity{
        UserID:   int64(claims["user_id"].(float64)),
        Username: claims["username"].(string),
        OrgID:    int64(claims["org_id"].(float64)),
    }
    
    // 解析角色和权限
    if roles, ok := claims["roles"].([]interface{}); ok {
        for _, r := range roles {
            identity.Roles = append(identity.Roles, r.(string))
        }
    }
    
    // 确保用户存在于 IM 系统（首次登录时同步）
    if err := a.userSyncer.EnsureUser(ctx, identity); err != nil {
        return nil, err
    }
    
    return identity, nil
}

// ==================== 模式 B: Token 验证回调 ====================

type CallbackAuthAdapter struct {
    authEndpoint string        // 公司 Auth 服务地址
    client       *http.Client
    cache        *Cache        // 缓存验证结果，避免频繁调用
    userSyncer   *UserSyncer
}

func (a *CallbackAuthAdapter) Verify(ctx context.Context, token string) (*UserIdentity, error) {
    // 1. 检查缓存
    cacheKey := "auth:" + hashToken(token)
    if cached, ok := a.cache.Get(cacheKey); ok {
        return cached.(*UserIdentity), nil
    }
    
    // 2. 调用公司 Auth 服务
    req, _ := http.NewRequestWithContext(ctx, "GET", a.authEndpoint+"/verify", nil)
    req.Header.Set("Authorization", "Bearer "+token)
    
    resp, err := a.client.Do(req)
    if err != nil {
        return nil, err
    }
    defer resp.Body.Close()
    
    if resp.StatusCode != 200 {
        return nil, ErrInvalidToken
    }
    
    var identity UserIdentity
    json.NewDecoder(resp.Body).Decode(&identity)
    
    // 3. 缓存结果（5 分钟）
    a.cache.Set(cacheKey, &identity, 5*time.Minute)
    
    // 4. 同步用户
    a.userSyncer.EnsureUser(ctx, &identity)
    
    return &identity, nil
}

// ==================== 模式 C: API Gateway Header ====================

type HeaderAuthAdapter struct {
    userIDHeader   string  // 如 "X-User-ID"
    usernameHeader string  // 如 "X-Username"
    orgIDHeader    string  // 如 "X-Org-ID"
    userSyncer     *UserSyncer
}

func (a *HeaderAuthAdapter) VerifyFromRequest(ctx context.Context, r *http.Request) (*UserIdentity, error) {
    userIDStr := r.Header.Get(a.userIDHeader)
    if userIDStr == "" {
        return nil, ErrMissingAuth
    }
    
    userID, _ := strconv.ParseInt(userIDStr, 10, 64)
    orgID, _ := strconv.ParseInt(r.Header.Get(a.orgIDHeader), 10, 64)
    
    identity := &UserIdentity{
        UserID:   userID,
        Username: r.Header.Get(a.usernameHeader),
        OrgID:    orgID,
    }
    
    a.userSyncer.EnsureUser(ctx, identity)
    return identity, nil
}
```

### 1.3 Gateway 集成鉴权

go

````go
type Gateway struct {
    authAdapter AuthAdapter
    connections sync.Map
    bus         *RealtimeBus
}

func (g *Gateway) HandleWebSocket(w http.ResponseWriter, r *http.Request) {
    // 1. 从 URL 参数或 Header 获取 Token
    token := r.URL.Query().Get("token")
    if token == "" {
        token = strings.TrimPrefix(r.Header.Get("Authorization"), "Bearer ")
    }
    
    // 2. 验证 Token
    identity, err := g.authAdapter.Verify(r.Context(), token)
    if err != nil {
        http.Error(w, "Unauthorized", 401)
        return
    }
    
    // 3. 升级 WebSocket
    conn, err := upgrader.Upgrade(w, r, nil)
    if err != nil {
        return
    }
    
    // 4. 创建连接，绑定用户身份
    client := &Connection{
        UserID:   identity.UserID,
        OrgID:    identity.OrgID,
        Identity: identity,
        Conn:     conn,
    }
    
    g.OnUserConnected(client)
}
```

---

## 二、权限校验

### 2.1 校验层设计
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           权限校验流程                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  发送消息请求                                                                │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     Gateway 层校验（快速拦截）                         │   │
│  │  • Token 有效性 ✓                                                    │   │
│  │  • 频率限制（100 条/分钟）                                             │   │
│  │  • 消息大小限制（< 64KB）                                             │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     消息服务层校验（业务规则）                          │   │
│  │  • 用户是否在该会话中 ✓                                               │   │
│  │  • 是否被禁言 ✓                                                       │   │
│  │  • 会话是否存在 ✓                                                     │   │
│  │  • 内容敏感词过滤 ✓                                                   │   │
│  │  • @提及权限检查 ✓                                                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     扩展校验（调用外部服务）                            │   │
│  │  • 敏感词服务（公司统一）                                              │   │
│  │  • 合规审计服务                                                       │   │
│  │  • 业务特殊规则（如项目群只能发项目相关内容）                           │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│       │                                                                     │
│       ▼                                                                     │
│     通过 → 发送消息                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
````

### 2.2 校验器实现

go

````go
// ==================== 校验器接口 ====================

type Validator interface {
    Validate(ctx context.Context, req *SendMessageRequest, identity *UserIdentity) error
}

type ValidatorChain struct {
    validators []Validator
}

func (c *ValidatorChain) Validate(ctx context.Context, req *SendMessageRequest, identity *UserIdentity) error {
    for _, v := range c.validators {
        if err := v.Validate(ctx, req, identity); err != nil {
            return err
        }
    }
    return nil
}

// ==================== 各种校验器 ====================

// 1. 频率限制
type RateLimitValidator struct {
    cache *Cache
    limit int           // 每分钟限制
}

func (v *RateLimitValidator) Validate(ctx context.Context, req *SendMessageRequest, identity *UserIdentity) error {
    key := fmt.Sprintf("rate:%d", identity.UserID)
    count, _ := v.cache.Incr(key)
    if count == 1 {
        v.cache.Expire(key, time.Minute)
    }
    if count > int64(v.limit) {
        return ErrRateLimitExceeded
    }
    return nil
}

// 2. 会话成员校验
type MembershipValidator struct {
    convRepo *ConversationRepository
}

func (v *MembershipValidator) Validate(ctx context.Context, req *SendMessageRequest, identity *UserIdentity) error {
    isMember, err := v.convRepo.IsMember(ctx, req.ConversationID, identity.UserID)
    if err != nil {
        return err
    }
    if !isMember {
        return ErrNotMember
    }
    return nil
}

// 3. 禁言检查
type MuteValidator struct {
    convRepo *ConversationRepository
}

func (v *MuteValidator) Validate(ctx context.Context, req *SendMessageRequest, identity *UserIdentity) error {
    isMuted, err := v.convRepo.IsMuted(ctx, req.ConversationID, identity.UserID)
    if err != nil {
        return err
    }
    if isMuted {
        return ErrUserMuted
    }
    return nil
}

// 4. 敏感词过滤（调用公司统一服务）
type ContentFilterValidator struct {
    filterEndpoint string
    client         *http.Client
}

func (v *ContentFilterValidator) Validate(ctx context.Context, req *SendMessageRequest, identity *UserIdentity) error {
    // 提取所有文本内容
    texts := extractTexts(req.Content)
    if len(texts) == 0 {
        return nil
    }
    
    // 调用公司敏感词服务
    resp, err := v.client.Post(v.filterEndpoint, "application/json", 
        strings.NewReader(fmt.Sprintf(`{"texts":%s}`, toJSON(texts))))
    if err != nil {
        // 敏感词服务不可用时，可以选择放行或拦截（根据业务需求）
        return nil // 这里选择放行
    }
    defer resp.Body.Close()
    
    var result struct {
        HasSensitive bool     `json:"has_sensitive"`
        Words        []string `json:"words"`
    }
    json.NewDecoder(resp.Body).Decode(&result)
    
    if result.HasSensitive {
        return fmt.Errorf("消息包含敏感词: %v", result.Words)
    }
    return nil
}

// 5. @提及权限（只能 @ 群成员）
type MentionValidator struct {
    convRepo *ConversationRepository
}

func (v *MentionValidator) Validate(ctx context.Context, req *SendMessageRequest, identity *UserIdentity) error {
    mentionedIDs := extractMentions(req.Content)
    if len(mentionedIDs) == 0 {
        return nil
    }
    
    members, err := v.convRepo.GetMemberIDs(ctx, req.ConversationID)
    if err != nil {
        return err
    }
    memberSet := toSet(members)
    
    for _, id := range mentionedIDs {
        if !memberSet[id] {
            return fmt.Errorf("不能 @非群成员: %d", id)
        }
    }
    return nil
}

// ==================== 组装校验链 ====================

func NewValidatorChain(deps *Dependencies) *ValidatorChain {
    return &ValidatorChain{
        validators: []Validator{
            &RateLimitValidator{cache: deps.Cache, limit: 100},
            &MembershipValidator{convRepo: deps.ConvRepo},
            &MuteValidator{convRepo: deps.ConvRepo},
            &MentionValidator{convRepo: deps.ConvRepo},
            &ContentFilterValidator{
                filterEndpoint: deps.Config.ContentFilterURL,
                client:         deps.HTTPClient,
            },
        },
    }
}
```

---

## 三、群管理整合

### 3.1 群与业务系统联动
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           群管理整合模式                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  模式 1: IM 主导（推荐）                                                     │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  业务系统 ──创建项目──→ 调用 IM API ──→ 创建项目群                      │   │
│  │  业务系统 ──添加成员──→ 调用 IM API ──→ 添加群成员                      │   │
│  │                                                                      │   │
│  │  IM 系统提供完整的群管理 API，业务系统主动调用                          │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  模式 2: 业务系统主导 + 事件同步                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  业务系统 ──创建项目──→ 发送事件 ──→ IM 事件监听器 ──→ 自动创建群        │   │
│  │                                                                      │   │
│  │  业务系统发送 Kafka/MQ 事件，IM 系统监听并响应                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  模式 3: 双向同步（复杂场景）                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  业务系统 ←─── 事件总线 ───→ IM 系统                                   │   │
│  │                                                                      │   │
│  │  两边都可以触发变更，通过事件总线保持同步                               │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
````

### 3.2 群同步器实现

go

```go
// ==================== 群类型定义 ====================

type ConversationType int

const (
    ConvTypePrivate    ConversationType = 1  // 单聊
    ConvTypeGroup      ConversationType = 2  // 普通群
    ConvTypeDepartment ConversationType = 3  // 部门群（自动同步成员）
    ConvTypeProject    ConversationType = 4  // 项目群（关联项目）
    ConvTypeChannel    ConversationType = 5  // 频道（公开/私有）
)

// ==================== 群管理 API ====================

type GroupService struct {
    db        *pgxpool.Pool
    bus       *RealtimeBus
    eventHub  *EventHub  // 发送事件给外部系统
}

// 创建群（被业务系统调用）
func (s *GroupService) CreateGroup(ctx context.Context, req *CreateGroupRequest) (*Conversation, error) {
    conv := &Conversation{
        ID:       s.snowflake.Generate(),
        Type:     req.Type,
        Name:     req.Name,
        OwnerID:  req.OwnerID,
        Settings: req.Settings,
    }
    
    // 如果是项目群/部门群，保存关联 ID
    if req.Type == ConvTypeProject {
        conv.Settings["project_id"] = req.ProjectID
    } else if req.Type == ConvTypeDepartment {
        conv.Settings["dept_id"] = req.DeptID
    }
    
    // 保存群
    if err := s.saveConversation(ctx, conv); err != nil {
        return nil, err
    }
    
    // 添加初始成员
    for _, memberID := range req.MemberIDs {
        s.AddMember(ctx, conv.ID, memberID, MemberRoleNormal)
    }
    s.AddMember(ctx, conv.ID, req.OwnerID, MemberRoleOwner)
    
    // 发送群创建事件（通知业务系统）
    s.eventHub.Publish("group.created", &GroupCreatedEvent{
        ConversationID: conv.ID,
        Type:           req.Type,
        ProjectID:      req.ProjectID,
        DeptID:         req.DeptID,
    })
    
    return conv, nil
}

// 同步部门群成员（定时任务或事件触发）
func (s *GroupService) SyncDepartmentGroup(ctx context.Context, convID int64) error {
    conv, err := s.getConversation(ctx, convID)
    if err != nil {
        return err
    }
    
    deptID := conv.Settings["dept_id"].(int64)
    
    // 从 HR 系统获取部门成员
    deptMembers, err := s.hrClient.GetDepartmentMembers(ctx, deptID)
    if err != nil {
        return err
    }
    
    // 获取当前群成员
    currentMembers, err := s.getMemberIDs(ctx, convID)
    if err != nil {
        return err
    }
    
    // 计算差异
    toAdd := difference(deptMembers, currentMembers)
    toRemove := difference(currentMembers, deptMembers)
    
    // 添加新成员
    for _, userID := range toAdd {
        s.AddMember(ctx, convID, userID, MemberRoleNormal)
    }
    
    // 移除离职成员（可选：保留或移除）
    for _, userID := range toRemove {
        s.RemoveMember(ctx, convID, userID)
    }
    
    return nil
}

// ==================== 事件监听器（监听业务系统事件）====================

type GroupEventListener struct {
    groupService *GroupService
    consumer     *kafka.Consumer
}

func (l *GroupEventListener) Start() {
    l.consumer.Subscribe("business-events", func(msg *kafka.Message) {
        var event BusinessEvent
        json.Unmarshal(msg.Value, &event)
        
        switch event.Type {
        case "project.created":
            // 自动创建项目群
            l.handleProjectCreated(event.Data)
            
        case "project.member_added":
            // 自动添加群成员
            l.handleProjectMemberAdded(event.Data)
            
        case "department.member_changed":
            // 触发部门群同步
            l.handleDepartmentChanged(event.Data)
        }
    })
}

func (l *GroupEventListener) handleProjectCreated(data json.RawMessage) {
    var project struct {
        ID      int64   `json:"id"`
        Name    string  `json:"name"`
        OwnerID int64   `json:"owner_id"`
        Members []int64 `json:"members"`
    }
    json.Unmarshal(data, &project)
    
    // 自动创建项目群
    l.groupService.CreateGroup(context.Background(), &CreateGroupRequest{
        Type:      ConvTypeProject,
        Name:      project.Name + " 项目群",
        OwnerID:   project.OwnerID,
        MemberIDs: project.Members,
        ProjectID: project.ID,
    })
}
```

### 3.3 群管理 REST API

go

````go
// ==================== 群管理 HTTP API ====================

func (h *GroupHandler) RegisterRoutes(r *gin.Engine) {
    g := r.Group("/api/v1/groups")
    g.Use(h.authMiddleware)
    
    g.POST("", h.CreateGroup)                           // 创建群
    g.GET("/:id", h.GetGroup)                           // 获取群信息
    g.PUT("/:id", h.UpdateGroup)                        // 更新群信息
    g.DELETE("/:id", h.DeleteGroup)                     // 解散群
    g.POST("/:id/members", h.AddMembers)                // 添加成员
    g.DELETE("/:id/members/:user_id", h.RemoveMember)   // 移除成员
    g.PUT("/:id/members/:user_id/role", h.SetRole)      // 设置角色
    g.POST("/:id/sync", h.SyncMembers)                  // 手动同步成员（部门群）
}

// 创建群 API
func (h *GroupHandler) CreateGroup(c *gin.Context) {
    var req struct {
        Type      ConversationType `json:"type" binding:"required"`
        Name      string           `json:"name" binding:"required"`
        MemberIDs []int64          `json:"member_ids"`
        ProjectID int64            `json:"project_id,omitempty"`  // 项目群专用
        DeptID    int64            `json:"dept_id,omitempty"`     // 部门群专用
    }
    
    if err := c.ShouldBindJSON(&req); err != nil {
        c.JSON(400, gin.H{"error": err.Error()})
        return
    }
    
    identity := c.MustGet("identity").(*UserIdentity)
    
    // 权限检查（如：只有项目经理才能创建项目群）
    if req.Type == ConvTypeProject && !hasPermission(identity, "project.create_group") {
        c.JSON(403, gin.H{"error": "无权创建项目群"})
        return
    }
    
    conv, err := h.groupService.CreateGroup(c, &CreateGroupRequest{
        Type:      req.Type,
        Name:      req.Name,
        OwnerID:   identity.UserID,
        MemberIDs: req.MemberIDs,
        ProjectID: req.ProjectID,
        DeptID:    req.DeptID,
    })
    
    if err != nil {
        c.JSON(500, gin.H{"error": err.Error()})
        return
    }
    
    c.JSON(201, conv)
}
```

---

## 四、群 Bot 设计

### 4.1 Bot 整体架构
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           群 Bot 架构                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Bot 注册中心                                   │   │
│  │  ┌──────────────────────────────────────────────────────────────┐   │   │
│  │  │  Bot ID  │  名称        │  触发方式      │  处理方式           │   │   │
│  │  ├──────────┼──────────────┼───────────────┼────────────────────┤   │   │
│  │  │  bot_ai  │  AI 助手     │  @AI / 关键词  │  LLM 流式回复       │   │   │
│  │  │  bot_jira│  Jira Bot    │  /jira 命令   │  Webhook 同步回复   │   │   │
│  │  │  bot_git │  Git Bot     │  /git 命令    │  Webhook 同步回复   │   │   │
│  │  │  bot_cal │  日历 Bot    │  /meeting     │  内部服务 同步回复  │   │   │
│  │  │  bot_kb  │  知识库 Bot  │  @知识库      │  RAG + LLM 流式    │   │   │
│  │  └──────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│                                    ▼                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        Bot 路由器                                     │   │
│  │                                                                      │   │
│  │  收到消息 → 检测触发条件 → 路由到对应 Bot Handler                       │   │
│  │                                                                      │   │
│  │  触发条件：                                                           │   │
│  │  • @mention: @AI 帮我...                                             │   │
│  │  • 命令前缀: /jira create PROJ-123                                   │   │
│  │  • 关键词: 包含 "帮我" + "查询"                                       │   │
│  │  • 自动触发: 新成员入群 → 欢迎消息                                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    │                                        │
│              ┌─────────────────────┼─────────────────────┐                  │
│              ▼                     ▼                     ▼                  │
│  ┌────────────────────┐ ┌────────────────────┐ ┌────────────────────┐      │
│  │   LLM Bot Handler  │ │ Webhook Bot Handler│ │  内部服务 Handler   │      │
│  │   （流式回复）      │ │  （同步/异步回复）   │ │   （同步回复）       │      │
│  └────────────────────┘ └────────────────────┘ └────────────────────┘      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
````

### 4.2 Bot 注册与路由

go

```go
// ==================== Bot 定义 ====================

type Bot struct {
    ID          string          `json:"id"`
    Name        string          `json:"name"`
    Avatar      string          `json:"avatar"`
    Description string          `json:"description"`
    TriggerType TriggerType     `json:"trigger_type"`
    TriggerRule string          `json:"trigger_rule"`  // @mention ID / 命令前缀 / 正则
    HandlerType HandlerType     `json:"handler_type"`
    HandlerURL  string          `json:"handler_url,omitempty"`  // Webhook URL
    Streaming   bool            `json:"streaming"`     // 是否支持流式回复
    Settings    json.RawMessage `json:"settings"`
}

type TriggerType string

const (
    TriggerMention TriggerType = "mention"   // @提及
    TriggerCommand TriggerType = "command"   // /命令
    TriggerKeyword TriggerType = "keyword"   // 关键词
    TriggerEvent   TriggerType = "event"     // 事件（入群、退群等）
)

type HandlerType string

const (
    HandlerLLM      HandlerType = "llm"       // LLM 处理（流式）
    HandlerWebhook  HandlerType = "webhook"   // Webhook 回调
    HandlerInternal HandlerType = "internal"  // 内部服务
)

// ==================== Bot 注册中心 ====================

type BotRegistry struct {
    bots map[string]*Bot
    mu   sync.RWMutex
}

func (r *BotRegistry) Register(bot *Bot) {
    r.mu.Lock()
    defer r.mu.Unlock()
    r.bots[bot.ID] = bot
}

func (r *BotRegistry) FindByTrigger(msg *Message) []*Bot {
    r.mu.RLock()
    defer r.mu.RUnlock()
    
    var matched []*Bot
    
    for _, bot := range r.bots {
        if r.matchesTrigger(bot, msg) {
            matched = append(matched, bot)
        }
    }
    
    return matched
}

func (r *BotRegistry) matchesTrigger(bot *Bot, msg *Message) bool {
    switch bot.TriggerType {
    case TriggerMention:
        // 检查是否 @了这个 Bot
        botUserID, _ := strconv.ParseInt(bot.ID, 10, 64)
        return containsMention(msg.Content, botUserID)
        
    case TriggerCommand:
        // 检查命令前缀
        text := extractFirstText(msg.Content)
        return strings.HasPrefix(text, bot.TriggerRule)
        
    case TriggerKeyword:
        // 正则匹配
        re := regexp.MustCompile(bot.TriggerRule)
        text := extractAllText(msg.Content)
        return re.MatchString(text)
    }
    
    return false
}

// ==================== Bot 路由器 ====================

type BotRouter struct {
    registry    *BotRegistry
    llmHandler  *LLMBotHandler
    webhookHandler *WebhookBotHandler
    internalHandler *InternalBotHandler
}

func (r *BotRouter) HandleMessage(ctx context.Context, msg *Message) {
    bots := r.registry.FindByTrigger(msg)
    
    for _, bot := range bots {
        go r.dispatch(ctx, bot, msg)
    }
}

func (r *BotRouter) dispatch(ctx context.Context, bot *Bot, msg *Message) {
    switch bot.HandlerType {
    case HandlerLLM:
        r.llmHandler.Handle(ctx, bot, msg)
        
    case HandlerWebhook:
        r.webhookHandler.Handle(ctx, bot, msg)
        
    case HandlerInternal:
        r.internalHandler.Handle(ctx, bot, msg)
    }
}
```

### 4.3 LLM Bot 处理器（流式回复）

**是否需要流式回复？答案是：必须支持，但可选择使用**

go

```go
type LLMBotHandler struct {
    bus       *RealtimeBus
    msgRepo   *MessageRepository
    llmClient *LLMClient
    snowflake *Snowflake
}

func (h *LLMBotHandler) Handle(ctx context.Context, bot *Bot, userMsg *Message) {
    botMsgID := h.snowflake.Generate()
    convID := userMsg.ConversationID
    botUserID, _ := strconv.ParseInt(bot.ID, 10, 64)
    
    // 构建上下文（获取最近 N 条消息）
    history, _ := h.msgRepo.GetRecent(ctx, convID, 20)
    prompt := h.buildPrompt(bot, userMsg, history)
    
    if bot.Streaming {
        // ========== 流式回复 ==========
        h.handleStreaming(ctx, bot, userMsg, botMsgID, prompt)
    } else {
        // ========== 非流式回复 ==========
        h.handleNonStreaming(ctx, bot, userMsg, botMsgID, prompt)
    }
}

// 流式回复处理
func (h *LLMBotHandler) handleStreaming(ctx context.Context, bot *Bot, userMsg *Message, botMsgID int64, prompt string) {
    convID := userMsg.ConversationID
    botUserID, _ := strconv.ParseInt(bot.ID, 10, 64)
    
    // 1. 发送 StreamStart
    h.bus.PublishToConversation(convID, &pb.StreamMessageStart{
        MessageId:      botMsgID,
        ConversationId: convID,
        SenderId:       botUserID,
        ReplyToId:      userMsg.ID,
        Timestamp:      time.Now().UnixMilli(),
    })
    
    // 2. 流式调用 LLM
    var fullContent strings.Builder
    stream, err := h.llmClient.ChatStream(ctx, prompt)
    if err != nil {
        h.sendError(convID, botMsgID, err)
        return
    }
    
    // 批量合并优化
    batcher := NewStreamBatcher(50*time.Millisecond, 10)
    
    index := 0
    for chunk := range stream {
        fullContent.WriteString(chunk.Delta)
        index++
        
        // 批量合并后发送
        if combined := batcher.Add(chunk.Delta); combined != "" {
            h.bus.PublishToConversation(convID, &pb.StreamMessageDelta{
                MessageId: botMsgID,
                Delta:     combined,
                Index:     int32(index),
            })
        }
    }
    
    // 发送剩余内容
    if remaining := batcher.Flush(); remaining != "" {
        h.bus.PublishToConversation(convID, &pb.StreamMessageDelta{
            MessageId: botMsgID,
            Delta:     remaining,
            Index:     int32(index + 1),
        })
    }
    
    // 3. 发送 StreamEnd
    h.bus.PublishToConversation(convID, &pb.StreamMessageEnd{
        MessageId:   botMsgID,
        FullContent: fullContent.String(),
        Success:     true,
    })
    
    // 4. 持久化
    h.saveMessage(ctx, botMsgID, convID, botUserID, fullContent.String())
}

// 非流式回复处理
func (h *LLMBotHandler) handleNonStreaming(ctx context.Context, bot *Bot, userMsg *Message, botMsgID int64, prompt string) {
    convID := userMsg.ConversationID
    botUserID, _ := strconv.ParseInt(bot.ID, 10, 64)
    
    // 调用 LLM（非流式）
    response, err := h.llmClient.Chat(ctx, prompt)
    if err != nil {
        h.sendError(convID, botMsgID, err)
        return
    }
    
    // 直接发送完整消息
    h.bus.PublishToConversation(convID, &pb.RecvMessage{
        Id:             botMsgID,
        ConversationId: convID,
        SenderId:       botUserID,
        ContentType:    ContentTypeRichText,
        Content:        buildTextContent(response),
        CreatedAt:      time.Now().UnixMilli(),
    })
    
    // 持久化
    h.saveMessage(ctx, botMsgID, convID, botUserID, response)
}

// 批量合并器
type StreamBatcher struct {
    deltas    []string
    lastFlush time.Time
    interval  time.Duration
    maxCount  int
    mu        sync.Mutex
}

func NewStreamBatcher(interval time.Duration, maxCount int) *StreamBatcher {
    return &StreamBatcher{
        interval:  interval,
        maxCount:  maxCount,
        lastFlush: time.Now(),
    }
}

func (b *StreamBatcher) Add(delta string) string {
    b.mu.Lock()
    defer b.mu.Unlock()
    
    b.deltas = append(b.deltas, delta)
    
    if time.Since(b.lastFlush) >= b.interval || len(b.deltas) >= b.maxCount {
        return b.flush()
    }
    return ""
}

func (b *StreamBatcher) Flush() string {
    b.mu.Lock()
    defer b.mu.Unlock()
    return b.flush()
}

func (b *StreamBatcher) flush() string {
    if len(b.deltas) == 0 {
        return ""
    }
    combined := strings.Join(b.deltas, "")
    b.deltas = b.deltas[:0]
    b.lastFlush = time.Now()
    return combined
}
```

### 4.4 Webhook Bot 处理器

go

````go
type WebhookBotHandler struct {
    bus       *RealtimeBus
    msgRepo   *MessageRepository
    client    *http.Client
    snowflake *Snowflake
}

func (h *WebhookBotHandler) Handle(ctx context.Context, bot *Bot, userMsg *Message) {
    botMsgID := h.snowflake.Generate()
    convID := userMsg.ConversationID
    botUserID, _ := strconv.ParseInt(bot.ID, 10, 64)
    
    // 构建 Webhook 请求
    payload := &WebhookPayload{
        Event:          "message",
        ConversationID: convID,
        Message: MessagePayload{
            ID:        userMsg.ID,
            SenderID:  userMsg.SenderID,
            Content:   userMsg.Content,
            CreatedAt: userMsg.CreatedAt,
        },
        Bot: BotPayload{
            ID:   bot.ID,
            Name: bot.Name,
        },
    }
    
    body, _ := json.Marshal(payload)
    req, _ := http.NewRequestWithContext(ctx, "POST", bot.HandlerURL, bytes.NewReader(body))
    req.Header.Set("Content-Type", "application/json")
    req.Header.Set("X-Bot-Signature", h.sign(body, bot.Settings))
    
    // 发送 Webhook
    resp, err := h.client.Do(req)
    if err != nil {
        h.sendError(convID, botMsgID, botUserID, err)
        return
    }
    defer resp.Body.Close()
    
    // 解析响应
    var result WebhookResponse
    json.NewDecoder(resp.Body).Decode(&result)
    
    if result.Reply != "" {
        // 发送回复消息
        h.bus.PublishToConversation(convID, &pb.RecvMessage{
            Id:             botMsgID,
            ConversationId: convID,
            SenderId:       botUserID,
            ContentType:    ContentTypeRichText,
            Content:        buildTextContent(result.Reply),
            CreatedAt:      time.Now().UnixMilli(),
        })
        
        h.saveMessage(ctx, botMsgID, convID, botUserID, result.Reply)
    }
}

type WebhookPayload struct {
    Event          string         `json:"event"`
    ConversationID int64          `json:"conversation_id"`
    Message        MessagePayload `json:"message"`
    Bot            BotPayload     `json:"bot"`
}

type WebhookResponse struct {
    Reply   string `json:"reply,omitempty"`    // 回复内容
    Actions []Action `json:"actions,omitempty"` // 后续动作
}
```

---

## 五、流式回复的必要性分析
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                   群 Bot 是否需要流式回复？                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ✅ 需要流式的场景：                                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • LLM 对话 Bot（AI 助手）                                           │   │
│  │    - 生成时间长（几秒到几十秒）                                        │   │
│  │    - 用户期望看到实时输出                                              │   │
│  │    - 提升交互体验                                                     │   │
│  │                                                                      │   │
│  │  • 知识库 RAG Bot                                                    │   │
│  │    - 检索 + 生成组合                                                  │   │
│  │    - 流式展示生成过程                                                 │   │
│  │                                                                      │   │
│  │  • 代码生成 Bot                                                       │   │
│  │    - 代码块较长                                                       │   │
│  │    - 流式显示更友好                                                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ❌ 不需要流式的场景：                                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  • 命令类 Bot（/jira, /git, /meeting）                               │   │
│  │    - 响应快（毫秒级）                                                 │   │
│  │    - 内容短（一两句话）                                                │   │
│  │                                                                      │   │
│  │  • Webhook Bot                                                        │   │
│  │    - 外部服务通常不支持流式                                            │   │
│  │    - 响应时间可控                                                     │   │
│  │                                                                      │   │
│  │  • 通知类 Bot                                                         │   │
│  │    - 系统通知、提醒                                                   │   │
│  │    - 内容固定                                                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  💡 最佳实践：                                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  1. Bot 注册时声明 streaming: true/false                              │   │
│  │  2. 统一走 NATS 推送（流式和非流式都走相同通道）                         │   │
│  │  3. 客户端根据消息类型自动选择渲染方式                                  │   │
│  │  4. 流式消息增加批量合并优化（50-100ms 合并一次）                       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
````

---

## 六、完整整合示例：项目群 + AI Bot

go

```go
// ==================== 场景：创建项目后自动创建群 + AI Bot ====================

// 1. 业务系统发送事件
func (s *ProjectService) CreateProject(ctx context.Context, req *CreateProjectRequest) (*Project, error) {
    project := &Project{
        ID:      generateID(),
        Name:    req.Name,
        OwnerID: req.OwnerID,
        Members: req.Members,
    }
    
    s.db.Save(project)
    
    // 发送事件
    s.kafka.Produce(&kafka.Message{
        Topic: "business-events",
        Value: toJSON(BusinessEvent{
            Type: "project.created",
            Data: project,
        }),
    })
    
    return project, nil
}

// 2. IM 系统监听事件，创建项目群
func (l *GroupEventListener) handleProjectCreated(data json.RawMessage) {
    var project struct {
        ID      int64   `json:"id"`
        Name    string  `json:"name"`
        OwnerID int64   `json:"owner_id"`
        Members []int64 `json:"members"`
    }
    json.Unmarshal(data, &project)
    
    // 创建项目群
    conv, _ := l.groupService.CreateGroup(context.Background(), &CreateGroupRequest{
        Type:      ConvTypeProject,
        Name:      project.Name + " 项目群",
        OwnerID:   project.OwnerID,
        MemberIDs: project.Members,
        ProjectID: project.ID,
    })
    
    // 添加 AI Bot 到群
    l.groupService.AddMember(context.Background(), conv.ID, AIBotUserID, MemberRoleBot)
    
    // 发送欢迎消息
    l.msgService.SendSystemMessage(context.Background(), conv.ID, 
        fmt.Sprintf("🎉 项目群已创建！\n\n@AI 助手 随时为您服务，试试：\n• @AI 帮我写个周报\n• /jira 查看任务"))
}

// 3. 用户在群里 @AI
// 消息: "@AI 帮我总结一下这个项目的进度"

// 4. Bot 路由器检测到触发条件，分发给 LLM Handler

// 5. LLM Handler 流式回复
func (h *LLMBotHandler) Handle(ctx context.Context, bot *Bot, userMsg *Message) {
    // 获取项目上下文
    convID := userMsg.ConversationID
    conv, _ := h.convRepo.Get(ctx, convID)
    projectID := conv.Settings["project_id"].(int64)
    
    // 从业务系统获取项目信息
    projectInfo, _ := h.projectClient.GetProjectSummary(ctx, projectID)
    
    // 构建 Prompt
    prompt := fmt.Sprintf(`你是项目助手，当前项目信息：
项目名称：%s
进度：%d%%
里程碑：%v
近期任务：%v

用户问题：%s

请用中文简洁回答。`, 
        projectInfo.Name,
        projectInfo.Progress,
        projectInfo.Milestones,
        projectInfo.RecentTasks,
        extractUserQuestion(userMsg),
    )
    
    // 流式回复...
    h.handleStreaming(ctx, bot, userMsg, h.snowflake.Generate(), prompt)
}
```

---

## 总结：整合方案速查表

|整合点|推荐方案|复杂度|
|---|---|---|
|**鉴权**|JWT 共享密钥|⭐|
|**鉴权（老系统）**|Token 验证回调 + 缓存|⭐⭐|
|**权限校验**|校验链模式，可插拔校验器|⭐⭐|
|**敏感词**|调用公司统一服务|⭐|
|**群管理**|IM 提供 API，业务系统调用|⭐⭐|
|**群同步**|监听业务事件，自动创建/更新群|⭐⭐⭐|
|**Bot 注册**|配置驱动，支持多种触发方式|⭐⭐|
|**LLM Bot**|**必须支持流式**，走统一 NATS 推送|⭐⭐⭐|
|**Webhook Bot**|HTTP 回调，同步/异步可选|⭐⭐|

**关键结论**：

1. **鉴权**：优先 JWT 共享密钥，最简单
2. **群 Bot 流式**：LLM 类必须支持，命令类不需要
3. **群管理**：业务系统主动调用 IM API，IM 发事件通知业务系统
4. **校验**：链式设计，按需插拔，敏感词走公司统一服务
# 现代 IM 系统架构设计 - 最优解方案

> **目标：构建一个支持十万+ TPS、可无限水平扩展、单人可开发维护的即时通讯系统**
> 
> **整合 WebRTC 音视频通话 + 即时通讯完整架构**

---

## 一、WebRTC 协议栈

```mermaid
graph TB
    subgraph "WebRTC 协议栈"
        subgraph "应用层"
            AV["音视频<br/>(SRTP/SRTCP)"]
            DC["数据通道<br/>(SCTP)"]
            SIG["信令<br/>(WebSocket)"]
        end
        
        subgraph "安全层"
            DTLS["DTLS 加密层"]
        end
        
        subgraph "连接层"
            ICE["ICE 连接建立"]
        end
        
        subgraph "NAT穿透"
            STUN["STUN"]
            TURN["TURN"]
            DIRECT["直连"]
        end
        
        subgraph "传输层"
            UDP["UDP / TCP"]
            TCP["TCP"]
        end
    end
    
    AV --> DTLS
    DC --> DTLS
    DTLS --> ICE
    ICE --> STUN
    ICE --> TURN
    ICE --> DIRECT
    STUN --> UDP
    TURN --> UDP
    DIRECT --> UDP
    SIG --> TCP
```

---

## 二、SDP Offer/Answer 模型

```mermaid
sequenceDiagram
    participant A as Peer A
    participant B as Peer B
    
    Note over A: 1. createOffer()
    Note over A: 2. setLocalDescription(offer)
    A->>B: SDP Offer (via signaling)
    Note over B: 3. setRemoteDescription(offer)
    Note over B: 4. createAnswer()
    Note over B: 5. setLocalDescription(answer)
    B->>A: SDP Answer (via signaling)
    Note over A: 6. setRemoteDescription(answer)
```

---

## 三、ICE 候选者收集与连通性检查

```mermaid
flowchart TB
    subgraph "ICE 候选者收集过程"
        A1["1. 收集 Host 候选者"] --> A2["枚举本地网络接口<br/>获取所有可用 IP:Port"]
        A2 --> B1["2. 收集 Server Reflexive 候选者"]
        B1 --> B2["向 STUN 服务器发送<br/>Binding Request"]
        B2 --> C1["3. 收集 Relay 候选者"]
        C1 --> C2["向 TURN 服务器发送<br/>Allocate Request"]
        C2 --> D1["4. 通过信令通道<br/>交换所有候选者"]
    end
    
    subgraph "ICE 连通性检查流程"
        E1["1. 候选者配对"] --> E2["本地候选者 × 远程候选者<br/>= 候选者对列表"]
        E2 --> F1["2. 优先级排序"]
        F1 --> F2["根据类型、网络开销、<br/>RTT等因素"]
        F2 --> G1["3. STUN Binding 检查"]
        G1 --> G2["双方互相发送<br/>请求/响应（四次握手）"]
        G2 --> H1["4. 选择最佳候选者对"]
        H1 --> H2["Controlling Agent<br/>决定最终路径"]
    end
```

---

## 四、STUN 工作流程

```mermaid
sequenceDiagram
    participant C as Client<br/>(私网 192.168.1.100:5000)
    participant NAT as NAT
    participant S as STUN Server<br/>(公网 203.0.113.100:3478)
    
    C->>NAT: Binding Request
    Note over NAT: NAT 转换为<br/>1.2.3.4:54321
    NAT->>S: Binding Request
    S->>NAT: Binding Response
    Note over S: XOR-MAPPED-ADDRESS:<br/>1.2.3.4:54321
    NAT->>C: Binding Response
```

---

## 五、IM 系统整体架构（十万+ TPS）

```mermaid
graph TB
    subgraph "客户端"
        CLIENT["iOS / Android / Web / Desktop / Bot"]
    end
    
    subgraph "接入层"
        LB["负载均衡 (L4)"]
        subgraph "Gateway 集群"
            GW1["Gateway 1"]
            GW2["Gateway 2"]
            GW3["Gateway 3"]
            GWN["Gateway N"]
        end
    end
    
    subgraph "消息总线"
        NATS["NATS Cluster<br/>实时推送总线，延迟 < 1ms<br/>普通消息、流式消息、输入状态、在线状态"]
    end
    
    subgraph "业务服务层"
        MSG["消息服务"]
        USER["用户服务"]
        GROUP["群组服务"]
        BOT["Bot 服务"]
        WEBHOOK["Webhook"]
    end
    
    subgraph "存储层"
        PG["PostgreSQL<br/>持久化<br/>消息+元数据"]
        KAFKA["Kafka Cluster<br/>异步任务<br/>索引/审计/统计"]
        VALKEY["Valkey<br/>缓存<br/>会话/在线状态"]
    end
    
    subgraph "Worker 集群"
        WORKER["索引 | 离线推送 | Webhook | 审计 | 统计 | 归档"]
    end
    
    subgraph "服务发现"
        GOSSIP["Gossip (memberlist)"]
    end
    
    CLIENT --> LB
    LB --> GW1 & GW2 & GW3 & GWN
    GW1 & GW2 & GW3 & GWN --> NATS
    NATS --> MSG & USER & GROUP & BOT & WEBHOOK
    MSG & USER & GROUP & BOT & WEBHOOK --> PG & KAFKA & VALKEY
    KAFKA --> WORKER
```

---

## 六、混合同步方案设计

```mermaid
erDiagram
    USER_SYNC_STATE {
        bigint user_id PK
        bigint sync_seq
        timestamptz updated_at
    }
    
    USER_CONVERSATIONS {
        bigint user_id PK
        bigint conversation_id PK
        bigint version
        bigint last_msg_id
        int unread_count
    }
    
    CONVERSATION_SYNC_STATE {
        bigint conversation_id PK
        bigint seq
        boolean is_large
        timestamptz updated_at
    }
    
    USER_SYNC_STATE ||--o{ USER_CONVERSATIONS : "user_id"
    CONVERSATION_SYNC_STATE ||--o{ USER_CONVERSATIONS : "conversation_id"
```

```mermaid
flowchart LR
    subgraph "同步机制说明"
        A["user_sync_state<br/>每用户一条<br/>判断'有无新消息'"]
        B["user_conversations<br/>version 标记<br/>会话何时变更"]
        C["conversation_sync_state<br/>大群专用<br/>避免写扩散"]
    end
    
    A --> D["查询 version > local_sync_seq<br/>找出有新消息的会话"]
    C --> E["大群 > 200人<br/>只更新会话 seq<br/>不更新每个用户的 sync_seq"]
```

---

## 七、消息同步流程

```mermaid
sequenceDiagram
    participant C as 客户端
    participant S as 服务端
    
    C->>S: ① sync_seq=1500
    S->>C: ② server_seq=1523<br/>changed: [conv_A, conv_B]<br/>large_groups: [big_group_1]
    Note over S: version > 1500<br/>需单独检查
    C->>S: ③ 拉取 conv_A 消息 (id > local)
    S->>C: ④ 返回消息列表
    C->>S: ⑤ 检查 big_group_1 的 seq
    Note over C: ⑥ 更新本地 sync_seq=1523
```

---

## 八、实时推送 vs 消息队列

```mermaid
flowchart TB
    subgraph "❌ 错误设计"
        E1["用户发消息"] --> E2["Kafka"] --> E3["消费者"] --> E4["推送"]
        E5["问题：<br/>• 延迟高 10-100ms<br/>• 流式消息无法处理<br/>• 职责混乱"]
    end
    
    subgraph "✅ 正确设计"
        direction TB
        subgraph "NATS 实时推送"
            N1["普通消息推送"]
            N2["流式消息推送"]
            N3["输入状态"]
            N4["在线状态"]
            N5["已读回执"]
            N6["延迟 < 1ms"]
        end
        
        subgraph "Kafka 异步任务"
            K1["消息索引"]
            K2["离线消息处理"]
            K3["审计日志"]
            K4["Webhook 触发"]
            K5["统计分析"]
            K6["延迟不敏感"]
        end
    end
```

---

## 九、统一消息流程

```mermaid
flowchart TB
    BIZ["业务服务<br/>消息服务 / Bot 服务"]
    
    BIZ --> NATS["NATS<br/>实时推送"]
    BIZ --> KAFKA["Kafka<br/>异步任务"]
    
    NATS --> GW["Gateway 集群"]
    KAFKA --> WORKERS["Workers 集群"]
    
    GW --> USER["用户"]
```

---

## 十、流式消息完整流程

```mermaid
sequenceDiagram
    participant U as 用户
    participant GW as Gateway
    participant MSG as 消息服务
    participant BOT as Bot 服务
    participant LLM as LLM API
    participant NATS as NATS
    participant DB as PostgreSQL
    participant KFK as Kafka
    
    U->>GW: 1. "@Bot 帮我写个故事"
    GW->>MSG: 2. 转发消息
    MSG->>DB: 存储用户消息
    MSG->>NATS: 发布用户消息
    MSG->>KFK: 发布到 Kafka
    MSG->>BOT: 调用 Bot 服务
    
    BOT->>BOT: 生成消息 ID (雪花算法)
    BOT->>NATS: 发布 StreamStart
    NATS->>GW: 转发
    GW->>U: StreamStart
    
    BOT->>LLM: 调用 LLM API (流式)
    
    loop 每个 token
        LLM->>BOT: token_n
        BOT->>NATS: 发布 StreamDelta
        NATS->>GW: 转发
        GW->>U: StreamDelta
    end
    
    BOT->>NATS: 发布 StreamEnd
    NATS->>GW: 转发
    GW->>U: StreamEnd
    
    BOT->>DB: 写入完整消息
    BOT->>KFK: 发布到 Kafka (索引、审计)
```

---

## 十一、WebRTC 连接建立流程

```mermaid
sequenceDiagram
    participant A as Peer A (发起方)
    participant SIG as 信令服务器
    participant B as Peer B (接收方)
    
    Note over A: 1. 获取本地媒体流<br/>getUserMedia()
    Note over A: 2. 创建 PeerConnection<br/>添加本地轨道
    Note over A: 3. createOffer()
    Note over A: 4. setLocalDescription(offer)
    
    A->>SIG: 5. 发送 offer
    SIG->>B: 转发 offer
    
    Note over B: 6. setRemoteDescription(offer)
    Note over B: 7. createAnswer()
    Note over B: 8. setLocalDescription(answer)
    
    B->>SIG: 发送 answer
    SIG->>A: 转发 answer
    
    Note over A: 9. setRemoteDescription(answer)
    
    rect rgb(200, 220, 240)
        Note over A,B: ICE 候选交换（双向并行）
        A->>SIG: candidate
        SIG->>B: candidate
        B->>SIG: candidate
        SIG->>A: candidate
    end
    
    rect rgb(220, 240, 200)
        Note over A,B: DTLS 握手 + SRTP 建立
    end
    
    rect rgb(240, 220, 200)
        Note over A,B: 媒体流传输开始
    end
```

---

## 十二、SFU vs MCU vs P2P 架构对比

```mermaid
graph TB
    subgraph "Mesh P2P (2-4人)"
        P1((用户1)) <--> P2((用户2))
        P2 <--> P3((用户3))
        P1 <--> P3
    end
    
    subgraph "SFU (5-100+人)"
        SFU_SERVER["SFU 服务器<br/>选择性转发"]
        S1((用户1)) --> SFU_SERVER
        S2((用户2)) --> SFU_SERVER
        S3((用户3)) --> SFU_SERVER
        SFU_SERVER --> S1 & S2 & S3
    end
    
    subgraph "MCU (无限制)"
        MCU_SERVER["MCU 服务器<br/>混流转码"]
        M1((用户1)) --> MCU_SERVER
        M2((用户2)) --> MCU_SERVER
        M3((用户3)) --> MCU_SERVER
        MCU_SERVER --> M1 & M2 & M3
    end
```

---

## 十三、消息发送完整流程

```mermaid
flowchart TB
    subgraph "1. 客户端"
        C1["WebSocket 发送 (Protobuf)"]
    end
    
    subgraph "2. Gateway"
        G1["Protobuf 解码"]
        G2["Token 鉴权"]
        G3["频率限制"]
        G4["转发到消息服务"]
        G1 --> G2 --> G3 --> G4
    end
    
    subgraph "3. 消息服务"
        M1["生成雪花 ID"]
        M2["写入 PostgreSQL"]
        M3["发布到 NATS"]
        M4["发布到 Kafka"]
        M5["返回 ACK"]
        M1 --> M2 --> M3 & M4 --> M5
    end
    
    subgraph "4. Gateway 集群"
        GW1["收到 NATS 消息"]
        GW2["查找本地在线用户"]
        GW3["WebSocket 推送"]
        GW1 --> GW2 --> GW3
    end
    
    subgraph "5. Kafka Workers"
        W1["索引到 Meilisearch"]
        W2["离线用户 Push 通知"]
        W3["Webhook 触发"]
        W4["审计日志"]
    end
    
    C1 --> G1
    G4 --> M1
    M3 -->|"实时推送"| GW1
    M4 -->|"异步任务"| W1 & W2 & W3 & W4
```

---

## 十四、用户上线同步流程

```mermaid
flowchart TB
    subgraph "1. 建立连接"
        A1["WebSocket 握手"]
        A2["Token 验证"]
        A3["注册到 Valkey<br/>(user_id → gateway_node)"]
        A4["订阅 NATS<br/>(im.user.{user_id} + 用户的会话列表)"]
        A1 --> A2 --> A3 --> A4
    end
    
    subgraph "2. 增量同步"
        B1["客户端发送 local_sync_seq"]
        B2["服务端返回 changed conversations<br/>(version > local_seq)"]
        B3["客户端逐个拉取新消息"]
        B1 --> B2 --> B3
    end
    
    subgraph "3. 实时接收"
        C1["通过 NATS 订阅<br/>自动接收新消息"]
    end
    
    A4 --> B1
    B3 --> C1
```

---

## 十五、十万+ TPS 部署架构

```mermaid
graph TB
    subgraph "接入层"
        LB["负载均衡 (L4)<br/>AWS NLB / 阿里云 SLB / Nginx"]
    end
    
    subgraph "Gateway 集群 (20 节点)"
        GW["每节点 5 万连接<br/>共 100 万并发连接能力"]
    end
    
    subgraph "消息总线"
        NATS["NATS Cluster (3 节点)<br/>实时推送总线<br/>千万级 msg/s"]
    end
    
    subgraph "业务服务"
        MSG["消息服务 x10"]
        BOT["Bot 服务 x10"]
        API["API 服务 x5"]
    end
    
    subgraph "存储层"
        PG["PostgreSQL<br/>主从 + 备"]
        KAFKA["Kafka Cluster<br/>5 节点"]
        VALKEY["Valkey Cluster<br/>3主3从"]
    end
    
    subgraph "Worker 集群 (20 节点)"
        WORKER["索引 | 离线推送 | Webhook | 审计 | 统计 | 归档"]
    end
    
    LB --> GW
    GW --> NATS
    NATS --> MSG & BOT & API
    MSG & BOT & API --> PG & KAFKA & VALKEY
    KAFKA --> WORKER
```

---

## 十六、演进路线图

```mermaid
timeline
    title IM 系统演进路线
    
    section Phase 1: MVP (0-6月)
        Go 单体服务 : PostgreSQL + Valkey + NATS
        PostgreSQL 队列代替 Kafka : 简化运维
        单机/少量节点部署 : 支持 1 万 TPS
    
    section Phase 2: 分布式 (6-12月)
        Gateway 集群 + Gossip : 引入 Kafka 替换 PG 队列
        Worker 分离 : PostgreSQL 主从 + 分区
        支持 5 万 TPS
    
    section Phase 3: 规模化 (12-24月)
        完整集群化部署 : Meilisearch 消息搜索
        PostgreSQL → TiDB : 多机房部署
        支持 十万+ TPS
    
    section Phase 4: 全球化 (24+ 月)
        多区域部署 : 边缘接入节点
        跨区数据同步 : 支持百万+ TPS
```

---

## 十七、与现有系统整合架构

```mermaid
graph TB
    subgraph "公司现有系统"
        SSO["用户中心<br/>(SSO)"]
        RBAC["权限系统<br/>(RBAC)"]
        HR["组织架构<br/>(HR)"]
        BIZ["业务系统<br/>(项目等)"]
    end
    
    subgraph "整合层 (Integration Layer)"
        AUTH["Auth Adapter<br/>鉴权适配器"]
        USER_SYNC["User Syncer<br/>用户同步器"]
        GROUP_SYNC["Group Syncer<br/>群同步器"]
        BOT_REG["Bot Registry<br/>Bot 注册中心"]
        WEBHOOK_HUB["Webhook Hub<br/>Webhook 中心"]
        EVENT_BRIDGE["Event Bridge<br/>事件桥接"]
    end
    
    subgraph "IM 核心系统"
        IM["Gateway | 消息服务 | 群组服务<br/>Bot 服务 | NATS | Kafka"]
    end
    
    SSO --> AUTH
    RBAC --> AUTH
    HR --> USER_SYNC & GROUP_SYNC
    BIZ --> GROUP_SYNC & BOT_REG & WEBHOOK_HUB & EVENT_BRIDGE
    
    AUTH & USER_SYNC & GROUP_SYNC & BOT_REG & WEBHOOK_HUB & EVENT_BRIDGE --> IM
```

---

## 十八、鉴权整合模式

```mermaid
flowchart TB
    subgraph "模式 A: JWT 共享密钥（推荐）"
        A1["公司 Auth 服务"] -->|"签发 JWT"| A2["客户端"]
        A2 -->|"携带 JWT"| A3["IM Gateway"]
        A4["IM Gateway 使用相同密钥验证 JWT<br/>无需调用外部服务"]
    end
    
    subgraph "模式 B: Token 验证回调"
        B1["客户端"] -->|"携带 Token"| B2["IM Gateway"]
        B2 -->|"验证请求"| B3["公司 Auth 服务"]
        B3 -->|"返回用户信息"| B2
    end
    
    subgraph "模式 C: API Gateway 统一鉴权"
        C1["客户端"] --> C2["Kong/APISIX"]
        C2 -->|"鉴权通过"| C3["IM Gateway（已验证）"]
        C2 -->|"注入 Header"| C4["X-User-ID / X-Org-ID"]
    end
```

---

## 十九、权限校验流程

```mermaid
flowchart TB
    REQ["发送消息请求"]
    
    subgraph "Gateway 层校验（快速拦截）"
        G1["Token 有效性 ✓"]
        G2["频率限制（100 条/分钟）"]
        G3["消息大小限制（< 64KB）"]
    end
    
    subgraph "消息服务层校验（业务规则）"
        M1["用户是否在该会话中 ✓"]
        M2["是否被禁言 ✓"]
        M3["会话是否存在 ✓"]
        M4["内容敏感词过滤 ✓"]
        M5["@提及权限检查 ✓"]
    end
    
    subgraph "扩展校验（调用外部服务）"
        E1["敏感词服务（公司统一）"]
        E2["合规审计服务"]
        E3["业务特殊规则"]
    end
    
    REQ --> G1 --> G2 --> G3
    G3 --> M1 --> M2 --> M3 --> M4 --> M5
    M5 --> E1 --> E2 --> E3
    E3 -->|"通过"| SEND["发送消息"]
```

---

## 二十、群管理整合模式

```mermaid
flowchart TB
    subgraph "模式 1: IM 主导（推荐）"
        A1["业务系统"] -->|"创建项目"| A2["调用 IM API"]
        A2 -->|"创建项目群"| A3["IM 系统"]
        A4["业务系统"] -->|"添加成员"| A5["调用 IM API"]
        A5 -->|"添加群成员"| A3
    end
    
    subgraph "模式 2: 业务系统主导 + 事件同步"
        B1["业务系统"] -->|"创建项目"| B2["发送事件"]
        B2 --> B3["IM 事件监听器"]
        B3 -->|"自动创建群"| B4["IM 系统"]
    end
    
    subgraph "模式 3: 双向同步"
        C1["业务系统"] <-->|"事件总线"| C2["IM 系统"]
    end
```

---

## 二十一、群 Bot 架构

```mermaid
graph TB
    subgraph "Bot 注册中心"
        BOT_AI["bot_ai<br/>AI 助手<br/>@AI / 关键词<br/>LLM 流式回复"]
        BOT_JIRA["bot_jira<br/>Jira Bot<br/>/jira 命令<br/>Webhook 同步回复"]
        BOT_GIT["bot_git<br/>Git Bot<br/>/git 命令<br/>Webhook 同步回复"]
        BOT_CAL["bot_cal<br/>日历 Bot<br/>/meeting<br/>内部服务同步回复"]
        BOT_KB["bot_kb<br/>知识库 Bot<br/>@知识库<br/>RAG + LLM 流式"]
    end
    
    subgraph "Bot 路由器"
        ROUTER["收到消息 → 检测触发条件 → 路由到对应 Bot Handler"]
        
        subgraph "触发条件"
            T1["@mention: @AI 帮我..."]
            T2["命令前缀: /jira create PROJ-123"]
            T3["关键词: 包含 '帮我' + '查询'"]
            T4["自动触发: 新成员入群 → 欢迎消息"]
        end
    end
    
    subgraph "Handler"
        H1["LLM Bot Handler<br/>（流式回复）"]
        H2["Webhook Bot Handler<br/>（同步/异步回复）"]
        H3["内部服务 Handler<br/>（同步回复）"]
    end
    
    ROUTER --> H1 & H2 & H3
```

---

## 二十二、流式回复必要性分析

```mermaid
flowchart TB
    subgraph "✅ 需要流式的场景"
        A1["LLM 对话 Bot（AI 助手）<br/>• 生成时间长（几秒到几十秒）<br/>• 用户期望看到实时输出<br/>• 提升交互体验"]
        A2["知识库 RAG Bot<br/>• 检索 + 生成组合<br/>• 流式展示生成过程"]
        A3["代码生成 Bot<br/>• 代码块较长<br/>• 流式显示更友好"]
    end
    
    subgraph "❌ 不需要流式的场景"
        B1["命令类 Bot（/jira, /git, /meeting）<br/>• 响应快（毫秒级）<br/>• 内容短（一两句话）"]
        B2["Webhook Bot<br/>• 外部服务通常不支持流式<br/>• 响应时间可控"]
        B3["通知类 Bot<br/>• 系统通知、提醒<br/>• 内容固定"]
    end
    
    subgraph "💡 最佳实践"
        C1["1. Bot 注册时声明 streaming: true/false"]
        C2["2. 统一走 NATS 推送（流式和非流式都走相同通道）"]
        C3["3. 客户端根据消息类型自动选择渲染方式"]
        C4["4. 流式消息增加批量合并优化（50-100ms 合并一次）"]
    end
```

---

## 二十三、Block 消息结构扩展性

```mermaid
flowchart LR
    subgraph "消息内容结构"
        MSG["MessageContent"]
        BLOCKS["blocks: ContentBlock[]"]
        MSG --> BLOCKS
    end
    
    subgraph "Block 类型（可无限扩展）"
        TEXT["text<br/>文本段落"]
        IMAGE["image<br/>图片"]
        VIDEO["video<br/>视频"]
        FILE["file<br/>文件"]
        MENTION["mention<br/>@某人"]
        LINK["link<br/>链接预览"]
        CODE["code<br/>代码块"]
        POLL["poll<br/>投票（扩展）"]
        CUSTOM["...<br/>自定义类型"]
    end
    
    BLOCKS --> TEXT & IMAGE & VIDEO & FILE & MENTION & LINK & CODE & POLL & CUSTOM
```

```mermaid
flowchart TB
    subgraph "扩展流程（无需修改 proto）"
        S1["步骤 1: 服务端定义新类型的 Go struct"]
        S2["步骤 2: 客户端添加对应的渲染组件"]
        S3["步骤 3: 完成！老客户端显示<br/>'不支持的消息类型'，不会崩溃"]
        S1 --> S2 --> S3
    end
```

---

## 二十四、图文混排示例

```mermaid
flowchart LR
    subgraph "消息: 嘻嘻嘻【图片】哈哈哈【视频】再见"
        B1["text<br/>'嘻嘻嘻'"]
        B2["image<br/>a.jpg"]
        B3["text<br/>'哈哈哈'"]
        B4["video<br/>b.mp4"]
        B5["text<br/>'再见'"]
        
        B1 --> B2 --> B3 --> B4 --> B5
    end
```

---

## 二十五、完整整合示例流程

```mermaid
sequenceDiagram
    participant BIZ as 业务系统
    participant KFK as Kafka
    participant IM_LISTENER as IM 事件监听器
    participant IM_GROUP as IM 群服务
    participant IM_MSG as IM 消息服务
    participant USER as 用户
    participant BOT as AI Bot
    participant LLM as LLM API
    
    BIZ->>KFK: 1. 创建项目事件
    KFK->>IM_LISTENER: 2. 监听到事件
    IM_LISTENER->>IM_GROUP: 3. 自动创建项目群
    IM_LISTENER->>IM_GROUP: 4. 添加 AI Bot 到群
    IM_LISTENER->>IM_MSG: 5. 发送欢迎消息
    
    USER->>IM_MSG: 6. "@AI 帮我总结项目进度"
    IM_MSG->>BOT: 7. 路由到 AI Bot
    BOT->>BIZ: 8. 获取项目上下文
    BIZ->>BOT: 9. 返回项目信息
    BOT->>LLM: 10. 流式调用 LLM
    
    loop 流式回复
        LLM->>BOT: token
        BOT->>USER: StreamDelta
    end
    
    BOT->>USER: StreamEnd
```

---

## 附录：方案对比总结

```mermaid
graph LR
    subgraph "OpenIM 方案"
        O1["同步模型: 用户 Inbox 写扩散"]
        O2["实时推送: 队列消费推送"]
        O3["流式消息: 特殊处理"]
        O4["大群写入: O(N)"]
        O5["推送延迟: 10-100ms"]
    end
    
    subgraph "本方案"
        T1["同步模型: 混合<br/>小群写扩散 + 大群读扩散"]
        T2["实时推送: NATS 直接推送"]
        T3["流式消息: 与普通消息统一"]
        T4["大群写入: O(1)"]
        T5["推送延迟: < 1ms"]
    end
```

---

> 文档版本: 3.2 (Mermaid 版) 目标: 十万+ TPS，可无限水平扩展 最后更新: 2024-12