# 总览

其实总共分为两大类，一种是**不改动** Azkaban 的，一种是**改动**的。

方案一

很简单，只需要在一个 flow 文件中添加不同的命令，不需要任何外部软件。缺点很明显，如果治理任务和执行 dataX 的任务过多，到达 100 个甚至以上，就会很难维护，复杂度很高，可扩展性低，但是胜在简单。

方案二

在数据库设置一个表，然后每次执行完 flow 中的一个 job，就会发一个回调请求，在表中记录一个阈值。如果到达该阈值，则主动触发执行下一个 flow 的流程。主动调用其API，因为用到了三个软件，所以一旦某一方坏了，就会导致 datax 的任务不能执行。

~~方案三~~

~~通过接收回调，存储至数据库中， mysql-udf-http 插件，由插件主动通知 DWU Server，然后执行对应的方法。其实不推荐这种方法~~

方案四

通过安装 Kafka 插件，异步通知 DWU 程序，flow 运行状态。代价就是运维困难，并且现在还没有完全弄好这个，花费时间未知。

方案五

修改源码，本地没有安装 gradle 并且网络不通畅，编译不了。最下面是 Azkaban 源码相关的内容解释。

|             指标             |          方案一          |              方案二              |  方案三  |         方案四         |  方案五  |
| :--------------------------: | :----------------------: | :------------------------------: | :------: | :--------------------: | :------: |
| 预计能完成的时间（不含测试） |         一天左右         |             一天左右             |  一天半  |          未知          |   未知   |
|            可用性            |            高            |                中                |    低    |           低           |    高    |
|            复杂度            |            高            |                中                |    高    |           高           |   代码   |
|           可扩展性           |            低            |                中                |    中    |           中           |    高    |
|         实现困难程度         |            低            |                低                |    低    |           中           |    高    |
|          待解决问题          | 无，仅仅需要时间配置即可 | 回调不支持 https，试过了，不支持 | 同方案二 | 按照官网的来，不能实现 | 编译不了 |
# 方案一

![](file:///D:\soft\drawio\方案一.png)

# 方案二

![](file:///D:\soft\drawio\方案二.png)



# 方案三

![](file:///D:\soft\drawio\方案三.png)



# 方案四

![](file:///D:\soft\drawio\方案四.png)

# 方案五 

![](file:///D:\soft\drawio\方案五.png)



# 其他已经试错的坑

1. 关于 Gradle 在本地加载的情况。配置 GRADLE_HOME、GRADLE_USER_HOME 其实都没用，这边还是会走本地，不会使用 IDEA 的代理去上网。
2. Azkaban 不支持 HTTPS 的回调

### 关于 HTTPS 回调不发送

在 dwu 程序下，新建一个 Spring Boot 程序，专门接收 Azkaban 的回调，可以在项目启动的时候，自动把这些文件上传到 Azkaban 服务器上，然后执行。

## 获取 session.id

```shell
curl -k -X POST --data "action=login&username=azkaban&password=azkaban" http://
10.35.227.205:6653
```

返回结果

```json
{
  "session.id" : "3fc505cc-e20c-4514-a040-316bb1dfd201",
  "status" : "success"
}
```

## 创建 project

```shell
curl -k -X POST --data "session.id=3fc505cc-e20c-4514-a040-316bb1dfd201&name=yyl_test_project1&description=The project create by yyl" http://10.35.227.205:6653/manager?action=create
```

返回结果

```json
{"path":"manager?project=yyl_test_project","action":"redirect","status":"success"}
```

## 上传文件

```shell
curl -k -i -X POST --form 'session.id=3fc505cc-e20c-4514-a040-316bb1dfd201' --form 'ajax=upload' --form 'file=@kafka_
test.zip;type=application/zip' --form 'project=yyl_test_project' http://10.35.227.205:6653/manager
```

返回结果

```json
{
  "projectId" : "129",
  "version" : "1"
}
```

## 获取 Project 信息

```shell
curl -k --get --data "session.id=3fc505cc-e20c-4514-a040-316bb1dfd201&ajax=fetchprojectflows&project=yyl_test_project" http://10.35.227.205:6653/manager
```

返回结果

```json
{
  "flows" : [ {
    "flowId" : "kafka_test"
  } ],
  "project" : "yyl_test_project",
  "projectId" : 130
}
```

## 执行 Flow

```shell
curl -k --get --data 'session.id=3fc505cc-e20c-4514-a040-316bb1dfd201' --data 'ajax=executeFlow' --data 'project=yyl_test_project' --data 'flow=kafka_test' http://10.35.227.205:6653/executor
```

# Azkaban 源码内容

下面是看了源码之后，大概 Azkaban 的内容。

![Azkaban.png](https://i.loli.net/2021/03/22/3lZSs7aNhpLjvwR.png)

创建表之类的信息在 azkaban-db /azkaban/src/main/sql 文件夹下，会执行 DatabaseSetup

## 关于轻量级注入框架 Guice 介绍

https://www.jianshu.com/p/7fba7b43146a

## 关于上传文件

会执行 ExecutionFlowDao#uploadExecutableFlow

其中 ExecutableFlow 会在对应的 Servlet 暴露出来.

## 关于对应的 flow 信息

存在 execution_flows 表中，在 create.execution_flows.sql 文件中有展示。

**ExecutionFlowDao#uploadExecutableFlow**

```java
final String INSERT_EXECUTABLE_FLOW = "INSERT INTO execution_flows "
    + "(project_id, flow_id, version, status, submit_time, submit_user, update_time, "
    + "use_executor, flow_priority, execution_source, dispatch_method) values (?,?,?,?,?,?,?,"
    + "?,?,?,?)";
```

## 对应的触发 flow 的调用

在 azkaban-web-server 模块下的

**azkaban.flowtrigger.quartz.FlowTriggerScheduler#schedule**

具体方法如下

```java
/**
 * Schedule flows containing flow triggers for this project.
 */
public void schedule(final Project project, final String submitUser)
    throws ProjectManagerException, IOException, SchedulerException {

  for (final Flow flow : project.getFlows()) {
    //todo chengren311: we should validate embedded flow shouldn't have flow trigger defined.
    if (flow.isEmbeddedFlow()) {
      // skip scheduling embedded flow since embedded flow are not allowed to have flow trigger
      continue;
    }
    // 这里会把 flow 文件的后缀名字加上去
    final String flowFileName = flow.getId() + ".flow";
    final int latestFlowVersion = this.projectLoader
        .getLatestFlowVersion(flow.getProjectId(), flow
            .getVersion(), flowFileName);
    if (latestFlowVersion > 0) {
      final File tempDir = Files.createTempDir();
      final File flowFile;
      try {
        flowFile = this.projectLoader
            .getUploadedFlowFile(project.getId(), project.getVersion(),
                flowFileName, latestFlowVersion, tempDir);

        final FlowTrigger flowTrigger = FlowLoaderUtils.getFlowTriggerFromYamlFile(flowFile);

        if (flowTrigger != null) {
          final Map<String, Object> contextMap = ImmutableMap
              .of(FlowTriggerQuartzJob.SUBMIT_USER, submitUser,
                  FlowTriggerQuartzJob.FLOW_TRIGGER, flowTrigger,
                  FlowTriggerQuartzJob.FLOW_ID, flow.getId(),
                  FlowTriggerQuartzJob.FLOW_VERSION, latestFlowVersion,
                  FlowTriggerQuartzJob.PROJECT_ID, project.getId());
          final boolean scheduleSuccess = this.scheduler
              .scheduleJobIfAbsent(flowTrigger.getSchedule().getCronExpression(),
                  new QuartzJobDescription
                  (FlowTriggerQuartzJob.class, FlowTriggerQuartzJob.JOB_NAME,
                      generateGroupName(flow), contextMap));
          if (scheduleSuccess) {
            logger.info("Successfully registered flow {}.{} to scheduler", project.getName(),
                flow.getId());
          } else {
            logger.info("Fail to register a duplicate flow {}.{} to scheduler", project.getName(),
                flow.getId());
          }
        }
      } catch (final SchedulerException | IOException ex) {
        logger.error("Error in registering flow {}.{}", project.getName(), flow.getId(), ex);
        throw ex;
      } finally {
        FlowLoaderUtils.cleanUpDir(tempDir);
      }
    }
  }
}
```