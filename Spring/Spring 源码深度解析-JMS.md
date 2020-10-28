## Spring 整合 JMS

Spring 对一些模版方法进行了一些封装。

1. Spring 配置文件

```xml
<beans>
    <bean id="connectionFactory" class="org.apache,.activemq.ActiveMQConnectionFactory">
    	<property name="brokerURL">
            <value>tcp://localhost:61616</value>
        </property>
    </bean>
    
    <bean id="jmsTemplate" class="org.springframework.jms.core.JmsTemplate">
    	<propety name="connectionFactory">
        	<ref bean="connectionFactory"/>
        </propety>
    </bean>
    
    <bean id="detination" class="org.apache.activemq.command.ActiveMQQueue">
    	<constructor-arg index="0">
        	<value>HelloWorldQueue</value>
        </constructor-arg>
    </bean>
</beans>
```

2. 发送端

模版方法，利用 JmsTemplate 进行发送和接受消息。

## JmsTemplate

1. **通用代码抽取**

**JmsTemplate#send**

```java
@Override
public void send(final Destination destination, final MessageCreator messageCreator) throws JmsException {
   execute(session -> {
      doSend(session, destination, messageCreator);
      return null;
   }, false);
}
```

**JmsTemplate#execute**

```java
@Nullable
public <T> T execute(SessionCallback<T> action, boolean startConnection) throws JmsException {
   Assert.notNull(action, "Callback object must not be null");
   Connection conToClose = null;
   Session sessionToClose = null;
   try {
      Session sessionToUse = ConnectionFactoryUtils.doGetTransactionalSession(
            obtainConnectionFactory(), this.transactionalResourceFactory, startConnection);
      if (sessionToUse == null) {
         conToClose = createConnection();
         sessionToClose = createSession(conToClose);
         if (startConnection) {
            conToClose.start();
         }
         sessionToUse = sessionToClose;
      }
      if (logger.isDebugEnabled()) {
         logger.debug("Executing callback on JMS Session: " + sessionToUse);
      }
      return action.doInJms(sessionToUse);
   }
   catch (JMSException ex) {
      throw convertJmsAccessException(ex);
   }
   finally {
      JmsUtils.closeSession(sessionToClose);
      ConnectionFactoryUtils.releaseConnection(conToClose, getConnectionFactory(), startConnection);
   }
}
```

2. 发送消息的实现

基于回调的方式，一步步封装了细节。

```java
protected void doSend(Session session, Destination destination, MessageCreator messageCreator)
      throws JMSException {

   Assert.notNull(messageCreator, "MessageCreator must not be null");
   MessageProducer producer = createProducer(session, destination);
   try {
      Message message = messageCreator.createMessage(session);
      if (logger.isDebugEnabled()) {
         logger.debug("Sending created message: " + message);
      }
      doSend(producer, message);
      // Check commit - avoid commit call within a JTA transaction.
      if (session.getTransacted() && isSessionLocallyTransacted(session)) {
         // Transacted session created by this template -> commit.
         JmsUtils.commitIfNecessary(session);
      }
   }
   finally {
      JmsUtils.closeMessageProducer(producer);
   }
}
```

3. 接收消息

和上面差不多