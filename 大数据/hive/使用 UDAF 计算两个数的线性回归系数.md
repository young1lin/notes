---
title: "使用 [通用] UDAF 计算两个数的线性回归系数"
date: 2020-08-29 00:00
tags: ["hadoop","hive"]
categories: "notes"
sticky: 1
---

```xml
<!-- https://mvnrepository.com/artifact/org.apache.hive/hive-exec -->
<dependency>
    <groupId>org.apache.hive</groupId>
    <artifactId>hive-exec</artifactId>
    <version>2.3.1</version>
</dependency>
```

reduce 的结果，其实是 group by 后的结果

例如

| s | x | y |
| :-: | :-: | :-: |
| A | 1 | 2 |
| A | 6 | 4 |
| B | 7 | 2 |
| B | 8 | 9 |
| C | 10 | 2 |
`select s,regression(x,y) group by s;`

输出结果 group by s 为 reduce 结果，输出 3 行信息





# 参考

> [User-defined aggregation functions (Advanced)](https://subscription.packtpub.com/book/big_data_and_business_intelligence/9781782169475/1/ch01lvl1sec22/user-defined-aggregation-functions-advanced)

> [hive patch](https://issues.apache.org/jira/secure/attachment/12858977/HIVE-15978.3.patch)

>[Hive UDAF开发详解](https://blog.csdn.net/kent7306/article/details/50110067)

