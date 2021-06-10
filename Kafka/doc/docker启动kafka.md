``` shell
docker pull wurstmeister/zookeeper
```

```shell
docker run -d --name zookeeper -p 2181:2181 -t wurstmeister/zookeeper
```


``` shell
docker pull wurstmeister/kafka 
```
``` shell
HOST_IP="$(ifconfig | grep inet | grep -v inet6 | grep -v 127 | cut -d ' ' -f2)"
echo $HOST_IP
```


启动kafka

```shell
docker run  -d --name kafka-emp -p 9092:9092 -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=$HOST_IP:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://$HOST_IP:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -t wurstmeister/kafka
```

4测试kafka 进入kafka容器的命令行

```shell
docker exec -it kafka /bin/bash
```

进入kafka所在目录

```shell
cd opt/kafka_2.11-2.0.0/
```

启动消息发送方

```shell
 ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic mykafka
```

启动消息接收方

```shell
 ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic mykafka --from-beginning
```



使用docker命令可快速在同一台机器搭建多个kafka，只需要改变brokerId和端口。

```shell
docker run -d --name kafka-home-1 -p 9093:9093 -e KAFKA_BROKER_ID=1 -e KAFKA_ZOOKEEPER_CONNECT=$HOST_IP:2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://$HOST_IP:9093 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9093 -t wurstmeister/kafka
```

6查看topic的状态 在kafka容器中的opt/kafka_2.12-1.1.0/ （因为版本更新，自己对应版本，按tab键自动补全） 目录下输入

```shell
bin/kafka-topics.sh --describe --zookeeper 192.168.124.17:2181 --topic mykafka
```

显示每个分区的Leader机器为broker0，在broker0和1上具有备份，Isr代表存活的备份机器中存活的。 当停掉kafka1后，

```shell
docker stop kafka1
```

查看所有topic

```shell
bin/kafka-topics.sh --zookeeper 192.168.124.17:2181 --list
```

## 增加topic分区数

```shell
bin/kafka-topics.sh --zookeeper 192.168.124.13:2181  --alter --topic mykafka --partitions 10
```



第 1 步：创建用户配置 SASL/SCRAM 的第一步，是创建能否连接 Kafka 集群的用户。在本次测试中，我会创建 3 个用户，分别是 admin 用户、writer 用户和 reader 用户。admin 用户用于实现 Broker 间通信，writer 用户用于生产消息，reader 用户用于消费消息。

```shell
bin/kafka-configs.sh --zookeeper 192.168.124.13:2181 --alter --add-config 'SCRAM-SHA-256=[password=admin],SCRAM-SHA-512=[password=admin]' --entity-type users --entity-name admin
```

```shell

bin/kafka-configs.sh --zookeeper 192.168.124.13:2181 --alter --add-config 'SCRAM-SHA-256=[password=writer],SCRAM-SHA-512=[password=writer]' --entity-type users --entity-name writer
```

```shell

bin/kafka-configs.sh --zookeeper 192.168.124.13:2181 --alter --add-config 'SCRAM-SHA-256=[password=reader],SCRAM-SHA-512=[password=reader]' --entity-type users --entity-name reader
```

查看刚才创建的用户

```shell
bin/kafka-configs.sh --zookeeper 192.168.124.13:2181 --describe --entity-type users  --entity-name writer
```

返回结果

```shell
Configs for user-principal 'writer' are SCRAM-SHA-512=salt=NTk3c3pyYTZ2eHZ5MXhibGlqdXBiYjEzaA==,stored_key=yOUHPEnw6wyxLYEQ4254D6FAoRogZCdz0+mDrQaVlvc2M3GPABSFHKBPyVcOEZginr2etwwvCb+L3ZEM2OIgug==,server_key=maD4k4RFgNp4EJvbsjT8fM3/5+CPDzFnkcaX/VnQbA+QvtyNDiIPNkUACpQRYJLpoEiYZROJ4y4AgBcuNZleBg==,iterations=4096,SCRAM-SHA-256=salt=eGxrZTZ5Zml4cjczOXdwbnJ2NmQzM2Yxbw==,stored_key=CbD/oyO5eiZvnnX2sKjRUMcTAJexr594T/XO7RhxezE=,server_key=maQQpPSfvo7zRh7QfOk3WWx5Fvnquf/Ja9jU6/+N0fg=,iterations=4096

```

第 2 步：创建 JAAS 文件配置了用户之后，我们需要为每个 Broker 创建一个对应的 JAAS 文件。因为本例中的两个 Broker 实例是在一台机器上，所以我只创建了一份 JAAS 文件。但是你要切记，在实际场景中，你需要为每台单独的物理 Broker 机器都创建一份 JAAS 文件。

```jaas
KafkaServer {
org.apache.kafka.common.security.scram.ScramLoginModule required
username="admin"
password="admin";
};
```



关于这个文件内容，你需要注意以下两点：不要忘记最后一行和倒数第二行结尾处的分号；JAAS 文件中不需要任何空格键。这里，我们使用 admin 用户实现 Broker 之间的通信。接下来，我们来配置 Broker 的 server.properties 文件，下面这些内容，是需要单独配置的。



这里，我们使用 admin 用户实现 Broker 之间的通信。接下来，我们来配置 Broker 的 server.properties 文件，下面这些内容，是需要单独配置的：

```properties
sasl.enabled.mechanisms=SCRAM-SHA-256

sasl.mechanism.inter.broker.protocol=SCRAM-SHA-256

security.inter.broker.protocol=SASL_PLAINTEXT

listeners=SASL_PLAINTEXT://192.168.124.13:9092
```



## 引用

> https://juejin.im/entry/5cbfe36b6fb9a032036187aa