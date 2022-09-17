# RabbitMQ

RabbitMQ is a message broker: it accepts and forwards messages. 

**生产者**不断向消息队列生产消息，消息排列在**消息队列**中，**消费者**从消息队列中获取消息

因为发送和接收都是异步的，可以轻松实现系统间的解耦



**特点**

- 异步通信
- 分布式
- 管理&监控



## Docker

docker pull 一个带management的版本，设置账号密码

docker run -d --hostname my-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:management

```bash
rabbitmqctl add_user mhlevel mhlevel  #添加用户，后面两个参数分别是用户名和密码
rabbitmqctl set_permissions -p / mhlevel ".*" ".*" ".*"  #添加权限
rabbitmqctl set_user_tags mhlevel administrator #修改用户角色
```



## 协议

RabbitMQ支持各种通信协议，包括STOMP、MQTT、AMQP



#### AMQP

AMQP  creates full functional interoperability between conforming clients and messaging middleware 
servers (also called "brokers")



**The Advanced Message Queuing Model (AMQ model)**

AMQ模型是一个逻辑框架，由3个关键组件组成。（和邮箱服务器逻辑类似）

三大关键组件：exchange，message queue，binding

exchange负责根据指定的binding规则，将消息路由到各个message queue



**The Advanced Message Queuing Protocol (AMQP)**

AMQP 0-9-1 (Advanced Message Queuing Protocol) is a messaging protocol that enables conforming client applications to communicate with conforming messaging middleware brokers.

AMQP是一个消息协议，让客户端应用可以按照规则与消息中间件进行通讯，改变AMQ模型的状态（设置交换机，消息队列，绑定...）



**AMQP 0-9-1 Model Explained**

交换机：默认交换机，创建队列时，默认绑定和队列名相同的routing key；其他的该啥样啥样

队列：属性Name、Durable、Exclusive、Auto-delete、Arguments

Bindings：交换机用来路由信息到队列的规则

消费者：两种方法消费信息，订阅队列&轮询队列，轮询效率极低应该避免使用

消息确认：消费者可以选择两种确认方法，自动确认&手动确认，第二种情况下，如果没收到手动确认，RabbitMQ会将消息推送给下一个消费者来处理（如果一个没有，就等一个新连接）

Rejecting Messages 退回消息：消费者可以在收到消息后，如果不能正确处理，可以退回消息并选择让RabbitMQ扔掉或重新分配给别人。（如果只有一个消费者订阅队列，就不要退回再requeue！会形成无限循环）

Negative Acknowledgements：RabbitMQ给多信息退回提供了解决方案

Prefetching Messages 预取消息：设定再下一个ack之前，可以向每个消费者发送多少条消息；RabbitMQ只支持channel级别的prefetch-count，不支持基于连接或者消息大小的。



连接：使用TCP实现可靠连接

Channel：channels that can be thought of as "lightweight connections that share a single TCP connection". 可以看作是一个Application与MessageBroker会话，不同的Thread应该使用不同的会话！

> A connection is a TCP connection between your application and the RabbitMQ broker. A channel is a virtual connection inside a connection. In other words, a channel multiplexes a TCP connection. Typically, each process only creates one TCP connection, and uses multiple channels in that connection for different threads. When you are publishing or consuming messages from a queue, it's all done over a channel.



## 消息队列

消息队列收到内存和磁盘大小的限制，它的本质就是一个大的消息缓存。

生产者可以向队列中发送消息，消费者可以从队列中接收消息。



#### **工作队列 Work Queues (aka: *Task Queues*)**

多个Worker从同一个消息队列中获取消息，队列采用公平的轮询分配，每个人获得同样多的消息。

适用于复杂的消息处理。



**公平分配**

RabbitMQ doesn't look at the number of unacknowledged messages for a consumer. It just blindly dispatches every n-th message to the n-th consumer.

这个设定让RabbitMQ只会循环调度`Round-robin dispatching`，而不会按劳分配

设置basicQos，告诉RabbitMQ，如果消费者没有Ack前一个消息，就不要发给他下一个消息！

```java
int prefetchCount = 1;
channel.basicQos(prefetchCount);
```



**可靠信息传递（publisher confirm & listener acknowledge）**

<img src="https://blog.rabbitmq.com/assets/images/2011/02/pubacks.svg" alt="img" style="zoom: 80%;" />	

如果消费者在进行消息处理时宕机，会导致消息丢失。为此，RabbitMQ设定了消息确认机制。

消费者消费完后，发送Ack回来，RabbitMQ才会将该消息从队列中移除。

若没有收到Ack，RabbitMQ认定消息丢失，转而将其重新发送给下一个消费者重新消费。

Manual message acknowledgments are turned on by default. In previous examples we explicitly turned them off via the autoAck=true flag. It's time to set this flag to false and send a proper acknowledgment from the worker, once we're done with a task.



**信息持久性 Message durability**

When RabbitMQ quits or crashes it will forget the queues and messages unless you tell it not to. Two things are required to make sure that messages aren't lost: we need to mark both the queue and messages as durable.

1. channel.queueDeclare()时，将durable设置为true
2. channel.basicPublish()时，给BasicProperties设置为MessageProperties.PERSISTENT_TEXT_PLAIN

 The persistence guarantees aren't strong, but it's more than enough for our simple task queue. If you need a stronger guarantee then you can use [publisher confirms](https://rabbitmq.com/confirms.html).



#### **发布订阅 publish/subscribe**

The core idea in the messaging model in RabbitMQ is that the producer never sends any messages directly to a queue. Actually, quite often the producer doesn't even know if a message will be delivered to any queue at all.



**临时队列 Temporary queues**

有了路由，我们不需要关心队列的名字（之前是生产者和队列点对点，所以需要知道）

queueDeclare() to create a non-durable, exclusive, autodelete queue with a generated name:

```java
String queueName = channel.queueDeclare().getQueue();
```

> exclusive：只被一个连接使用，这个连接断开时销毁队列



**扇出交换机 exchange**

Instead, the producer can only send messages to an *exchange*. An exchange is a very simple thing. On one side it receives messages from producers and the other side it pushes them to queues. The exchange must know exactly what to do with a message it receives.

There are a few exchange types available: direct, topic, headers and fanout.

```java
channel.exchangeDeclare("logs", "fanout");
channel.queueBind(queueName, "logs", "");
```



**直接交换机 Direct exchange**

交换机只将信息发送给与其routingkey完全相同的queue

Bindings can take an extra routingKey parameter. To avoid the confusion with a basic_publish parameter we're going to call it a binding key. This is how we could create a binding with a key:

```java
channel.queueBind(queueName, EXCHANGE_NAME, "black");
```



**主题交换机 Topic exchange**

在路由的基础上让交换机绑定更加灵活

可以设置多个routingkey，中间用`.`分割，可以用`*`占位符代表1个单词，用`#`代表0或多个单词	

比如生产者绑定routingkey：`tyler.handsome.guy`，消费者绑定routingkey：`tyler.#`或者`*.handsome.*`都可以接收到



## 死信队列

In certain situations, for example, when a message cannot be routed, messages may be *returned* to publishers, dropped, or, if the broker implements an extension, placed into a so-called "dead letter queue". Publishers choose how to handle situations like this by publishing messages using certain parameters.



通过policy配置

```bash
rabbitmqctl set_policy DLX ".*" '{"dead-letter-exchange":"my-dlx"}' --apply-to queues
```

通过args配置

```java
channel.exchangeDeclare("some.exchange.name", "direct");

Map<String, Object> args = new HashMap<String, Object>();
args.put("x-dead-letter-exchange", "some.exchange.name");
channel.queueDeclare("myqueue", false, false, false, args);
```



### RabbitMQ整合Springboot



**生产者**

publisher-confirm

```yml
publisher-returns: true
publisher-confirm-type: correlated
```

```java
@Component
public class PublisherListener {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    //生产者调用sendAndReceive()时，会等待回调这个方法；效率极低
    public void setRabbitTemplate(){
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println(correlationData);
            System.out.println(ack);
            System.out.println(cause);
        });
    }
}
```

正常发布消息，参数为exchange，routingKey，message

默认的SimpleMessageConverter只支持String, byte[] and Serializable payloads类型的message

```java
public void simplePublish(){
    rabbitTemplate.convertAndSend("testEx","log.info","simpleMessage");
    rabbitTemplate.convertAndSend("testEx","log.warn","warnMessage");
}
```



**消费者**

listener acknowledge

```yml
    listener:
#      simple:
#        acknowledge-mode: manual
#        prefetch: 1
#      simple & direct的不同，请见https://docs.spring.io/spring-amqp/reference/html/#choose-container
      direct:
        acknowledge-mode: manual
        prefetch: 1
```

```java
@RabbitListener(queues = "tyler")
public void consumerOne(String message,Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException{
    System.out.println("consumerOne accepts " + message);
    //返回nack，第一个参数是请求头里的一个参数，第二个参数是否要全部nack，第三个参数是否要requeue这些nack的消息
    channel.basicNack(tag,false,true);
    //channel.basicAck();
}
```

正常消费消息，默认autoAck，prefetch=250，@Queue生成临时队列，绑定交换机，设置routingKey

```java
@RabbitListener(bindings = @QueueBinding(
        value = @Queue,
        exchange = @Exchange(type = "topic",name = "testEx"),
        key = {"log.*"}
))
public void everyLog(String message){
    log.info(message);
}
```



### RabbitMQ集群



**节点标识**

在RabbitMQ中，节点使用节点名`node name`来标识。

一个集群中的每个节点的名字必须是独一无二的，节点之间通过节点名来完成通讯。

node name包括两个比分，前缀(通常为rabbit)和主机名(hostname)

> 启动Docker的时候设置hostname，然后在/var/hosts文件中，将其他节点的hostname对应IP地址写进去。到时候join_cluster rabbit@hostname，就可以加入集群了



**集群性质**

所有RabbitMQ的节点都是同等地位的`equal peers`，不像大多数分布式系统，存在主从节点之说。



两个节点想要通讯，必须有相同的密匙叫做`Erlang cookie`

在3.9版本以前，都可以通过设置环境变量`RABBITMQ_ERLANG_COOKIE`的方式设置`/var/lib/rabbitmq/.erlang.cookie`下的密匙

而3.9版本后，官方将环境变量废除，使用配置文件

> 官方建议集群的节点数量为奇数，最少为3个节点



**普通集群**

https://cloud.tencent.com/developer/article/1496835

By default, contents of a queue within a RabbitMQ cluster are located on a single node (the node on which the queue was declared). This is in contrast to exchanges and bindings, which can always be considered to be on all nodes.



**镜像集群**

rabbitmqctl set_policy --> 设置镜像policy

可以指定镜像模式：

- exactly：指定queue数量，1代表只有主节点1人存放queue
- all：所有节点都镜像主节点的队列（官方不推荐，Mirroring to a quorum (N/2 + 1) of cluster nodes is recommended instead）
- nodes：指定节点来镜像主节点的队列

配置后完毕后，queue就会形成master和mirrors的主从复制关系（rabbit1的节点被2和3mirror，raabit2的节点被1和3mirror，节点之间依然是公平的equal peer关系）

```bash
rabbitmq-plugins enable rabbitmq_federation

rabbitmqctl set_policy ha-nodes ".*" '{“ha-sync-mode":"automatic","ha-mode":"nodes","ha-params":["rabbit@rabbit1", "rabbit@rabbit2","rabbit@rabbit3"]}' --priority 1 --apply-to queues
```



在RabbitMQ管理网页上，可以看到queue采用的镜像policy

<img src="https://rabbitmq.com/img/mirroring/queue_mirroring_indicators_management_ui_row_only.png" alt="Mirrored queue indicators in management UI" style="zoom:67%;" />	



## Quorum queues

Quorum queues should be the **default choice** for a replicated queue type. Classic queue mirroring will be **removed in a future version** of RabbitMQ: classic queues will remain a supported non-replicated queue type.

The quorum queue is a modern queue type for RabbitMQ implementing a durable, replicated FIFO queue based on the [Raft consensus algorithm](https://raft.github.io/). It is available as of RabbitMQ 3.8.0.

