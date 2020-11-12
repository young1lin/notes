书上的用例都是 Scala 版本的，看得懂，但是没必要。大部分讲的是小步快跑，逐步重构，更简单使用 Java 的 NIO 特性。







# Maven

## 打 war 包的 Maven 工程

```\main
\{project.basedir}
|---\src
  |---\main
    |---\java
    |---\resources
    |---\webapp
  |---\test
    |---\java
    |---\java
    |---\resources
|---\target
```

## POM 大纲

### POM 文件的大纲

```xml
<project>
	<groupId/>
    <artifactId/>
    <version/>
    <packaging/>
    <properties/>
    <dependencies/>
    <profiles/>
</project>
```

`<packagins>`常用的值是 pom、jar、ear、war 以及 maven-plugin

### POM 文件的用例

+ 默认的——用于构建一个构件。
+ 父 POM——提供一个由子项目继承的单个配置信息源——声明这个 POM 文件作为它们的 `<parent>` 元素的值。

+ 聚合器——用于构建一组声明为 `<modules>` 的项目。

注意：作为父 POM 或者聚合器的 POM 文件的 `<packaging>`元素的值将是 pom。一个 pom 文件可能同时提供两项功能。

### GAV 坐标

groupId、artifactId、packaging、version、classifier

例如 `io.netty:netty-all:4.1.9.Final`

将会产生

netty-all-4.1.9.Final.jar

### 依赖

```xml
<dependencies>
    <dependency>
    	<groupId/>
        <artifactId/>
        <version/>
        <scope/>
        <systemPath/>
    </dependency>
</dependencies>
```

**scope**元素有以下值

compile——编译和执行需要的（默认值）。

runtime——只有执行需要。

optional——不会引用了这个项目产生的构件的其他项目，视为传递依赖。

provided——不会被包含了在由这个 POM 产生的 WAR 文件的 WEB-INF/lib 目录中。如果 Spring Boot 要打 war 包的话 ， Tomcat 依赖常用。

test—— 编译+测试。

import——依赖管理。

### 依赖管理

`<dependencyManagement>`

用例

```xml
<properties>
	<netty.version>4.1.9</netty.version>
</properties>
<dependencyManagement>
	<dependencies>
    	<dependecy>
        	<groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>${netty.version}</version>
        </dependecy>
    </dependencies>
</dependencyManagement>
```

对于这这种场景，依赖的`<scope>`元素有一个特殊的 import 值：它将把外部 POM（没有被声明为 `<parent>`）的`<dependencyManagement>`元素的内容导入到当前 POM 的 `<dependencyManagement>`元素中。

### 构建的生命周期 lifecycle

+ validate——检查项目是否正确，所有必需的信息是否已经就绪。
+ process-sources——处理源代码，如过滤任何值。
+ compile——编译项目的源代码。
+ process-test-resources——复制并处理资源到测试目标目录中。
+ test-complile——将测试源代码编译到测试目标目录中。
+ test——使用合适的单元测试框架测试编译的源代码。
+ package——将编译的代码打爆为它的可分法格式，如 JAR。
+ integration-test——处理并将软件包部署到一个可以运行集成测试的环境中。
+ verify——运行任何的检查以验证软件包是否有效，并且符合质量标准。
+ install——将软件包安装到本地存储库中，在那里其他本地构建项目可以将它引用为依赖。
+ deploy——将最终的构件上传到远程存储库，以与其他开发人员和项目共享。

执行这些阶段中的一个阶段将会调用所有前面的阶段。例如 mvn package 将会执行 validate、compile、以及 test

### 插件

### 插件管理

可以省略 groupId 为 org.apache.maven.plugins 的 groupId 编写。仅这个可以。

插件版本也可以被继承

### 配置文件

`<profiles>`是一组自定义的 POM 元素，可以以不同环境 dev、uat、test、prod，换不同的内容来做。

mvn -P jdk16 clean install

### 存储库

repositoty

### 快照和发布

-SNAPSHOT 结尾的构件将被认为是还没有发布的，这种构件可以重复地使用相同的`<version>`值被上传到存储库，每次它都会被分配一个唯一的时间戳，当项目检索构件时，下载的是最新实例。

反之相反。

## Maven 命令行

`mvn [options] [<goal(s)>] [<phase(se)>]`

![721605187421_.pic.jpg](https://i.loli.net/2020/11/12/oIe28NvFX3SOQcK.jpg)

![731605187426_.pic.jpg](https://i.loli.net/2020/11/12/SEfeUivYc65TFgk.jpg)