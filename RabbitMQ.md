# RabbitMQ

MQ：消息队列（Message Queue），也称为消息中间件

**生产者**不断向消息队列生产消息，消息排列在**消息队列**中，**消费者**从消息队列中获取消息

因为发送和接收都是异步的，可以轻松实现系统间的解耦



### 不同的MQ特点

1. ActiveMQ

   Apache老牌产品，丰富的API，多种集群架构模式；性能受限，可以满足需求小型企业小吞吐量

2. Kafka（效率高，数据一致性低）

   Apache顶级项目，追求高吞吐量，一开始的目的是用于日志的收集和传输。不支持事务，对消息丢失、重复、错误没有严格要求。适合大量数据收集业务

3. RocketMQ

   阿里巴巴开源，纯Java开发；高吞吐量，高可用性，思路源于Kafka，但是对事务做了优化；在阿里巴巴集团广泛用于交易、充值、流计算、消息推送、日志处理、binglog分发等场景。

4. RabbitMQ

   基于AMQP网络协议，Erlang语言开发，与Spring框架无缝整合；对数据一致性，稳定性和可靠性要求很高，对性能和吞吐量的要求在其次

   

### RabbitMQ引言

AMQP（advanced message queuing protocol）在2003年被提出，是一种链接协议。

不是从API进行限定，而是直接定义网络交换的数据格式



**安装**

1. 官网下载erlang和rabbitmq的rpm安装包，并解压rpm -ivh xxx

2. 命令行输入rabbitmq-plugins enable rabbitmq_management，启用web管理界面插件

3. 启动服务 systemctl start rabbitmq-server

4. 添加一个管理员用户

   rabbitmqctl add_user tyler 1028

   rabbitmqctl set_user_tags tyler administrator

   rabbitmqctl list_users

5. 远程登录，管理rabbitmq



### RabbitMQ管理界面

rabbitmqctl：在不使用web管理界面的情况下操作RabbitMQ

rabbitmq-plugins：插件管理



- `connections`：无论生产者还是消费者，都需要与 RabbitMQ 建立连接后才可以完成消息的生产和消费，在这里可以查看连接情况
- `channels`：**通道**，建立连接后，会形成通道，消息的投递获取依赖的通道
- `Exchanges`：**交换机**，用来实现消息的路由
- `Queues`：**队列**，就是消息队列，消息存放在队列中，等待消费，消费后会被移除队列



在服务器中，构建虚拟主机；用户通过账号密码链接虚拟主机，生产正通过通道向交换机中写入生产消息，交换机路由，找到指定的队列；当然生产者也可以直接将消息写入队列；最终，消费者从队列中获取消息；消息模型有很多种！



### 消息模型

channel.queueDeclare(String queue，Boolean duration，Boolean exclusive，Boolean autoDelete）

- 将通道和队列绑定
- 持久化的队列在RabbitMQ服务重启后，依然保留，队列中消息的持久化不在这个属性中涉及
- 队列是否被本次connection独占
- 消息被消费完，并与消费者断开连接后，自动删除

channel.baseicPublish(String exchange，String routingKey，BasicProperties props，byte[] body)

- 交换机
- 路由key
- 消息持久化等额外属性
- 消息

channel.basicConsume(String queue, Boolean autoAck, Consumer consumer)

- 消费的队列
- 自动确认机制：获取到数据后，自动向消息队列确认已经被消费，消息队列即刻删除该消息；如果取消自动确认，但是并未手动确认，消息会一直保存在队列中

channel.basicQos(Integer prepetchCount)

- 让通道每次放入1个消息，而不是一股脑的全部放入，防止消费者宕机

channel.exchangeDeclare(String exchange, String type)

- 交换机名称
- 交换机类型



**Hello World！**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210801225122193.png" alt="image-20210801225122193" style="zoom:67%;" />	

问题：生产的快，消费的慢，导致消息队列的消息堆积

解决方式：让多个消费者共同消费一个队列，队列中的消息被消费后即刻消失，不会被重复消费



**任务模型 Work queues**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210801225101897.png" alt="image-20210801225101897" style="zoom:67%;" />	

问题：

RabbitMQ顺序的将每个消息发送给下一个消费者，每个消费者都会获取相同数量的消息。这种平均分发消息的方式也称为“循环”。

如果消息者1处理的快，而消费者2处理的慢，平均分配就会导致以最慢处理者为标准，拉低所有消费者的对这个队列的处理速度！

循环是由RabbitMQ的自动确认机制导致的，

解决方式：

设置服务器每次向通道中传入1个消息；关闭通道对消息的自动确认，在业务处理完一个消息后手动确认

这样，每次服务器向通道中传入1个消息，消费者手动确认后，服务器会再传1个消息到通道；实现能者多劳

> Spring AMQP实现Work模型使用的是公平调度，如果想要变成能者多劳需要额外配置



**发布订阅模型 Publish/Subscribe**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210801225036807.png" alt="image-20210801225036807" style="zoom:67%;" />	

生产者不再直接连接队列，而是发送给交换机，由交换机决定发送给哪个队列



使用广播（Fanout）类型的交换机

交换机可以把消息发送给所有绑定过的队列，所有绑定过这些队列的消费者都能拿到信息；实现发布一条消息，被多个消费者消费！

由于每一个消费者可能都要有一个队列来绑定这个fanout交换机，所以每个消费者创建一个空的临时队列，断开连接后，这个队列会被自动删除



**路由模型 Routing**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210801224958702.png" alt="image-20210801224958702" style="zoom:67%;" />	

相比fanout那种被所有订阅的队列消费，我们希望不同的消息被不同的队列消费



使用直连（Direct）类型的交换机

生产者向路由器发布消息时指定`RoutingKey`

消费者的临时队列绑定路由器时，也指定`RoutingKey`

只有发布和接收时指定的`RoutingKey`相等时，消息参会被发送到队列中，然后被消费者消费



**Topic**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802115456776.png" alt="image-20210802115456776" style="zoom:67%;" />	

`Topic`类型的交换机与`Direct`一样，都支持`RoutingKey`。只不过`Topic`允许队列再绑定`RoutingKey`的时候使用通配符！可以由多个单词组成，以“`.`”分割

`*`可以替代1个单词

`#`可以代替零或多个单词



### RabbitMQ整合Springboot

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802145958827.png" alt="image-20210802145958827" style="zoom:67%;" />	

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802150019664.png" alt="image-20210802150019664" style="zoom:67%;" />	



### MQ的应用场景

**异步处理**

业务场景：用户注册信息写入数据库后，需要同时发注册邮件和注册短信

解决方案：

1. 串行执行：响应客户端时间太长，没必要等待

   <img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802160206593.png" alt="image-20210802160206593" style="zoom:67%;" />	

2. 并行执行：多线程同时发送

   <img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802160219163.png" alt="image-20210802160219163" style="zoom:67%;" />	

3. 消息队列广播

   <img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802160444602.png" alt="image-20210802160444602" style="zoom:67%;" />	

**应用解耦**

业务场景：用户下订单后，需要通知库存系统；换句话说，订单系统需要调用库存系统的接口；两个系统高度耦合，如果库存系统出现故障，则订单系统的可用性收到影响，无法第一时间返回

解决方案：

用户下订单 --> 订单系统完成持久化 --> 将消息写入队列并返回下单成功 --> 库存系统从消息队列中拉去消息，并执行对应的业务逻辑；两个系统互不影响

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802162441061.png" alt="image-20210802162441061" style="zoom:67%;" />	

**流量削峰**

业务场景：秒杀活动，流量过大导致秒杀系统被压垮

解决方案：

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802162855918.png" alt="image-20210802162855918" style="zoom:67%;" />	

服务器收到业务请求后将其写入消息队列，超过队列设置的阈值（最大长度），则直接抛弃请求并返回“秒杀失败”页面；秒杀业务按照自己的最大处理能力，从队列中获取消息并完成业务处理



### RabbitMQ的集群

**普通集群（副本集群）**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802181709978.png" alt="image-20210802181709978" style="zoom:80%;" />	

从节点复制主节点中交换机的数据和状态；从节点可以看到主节点的消息队列（在web管理页面显示），但无法同步消息队列以及其中的数据。

如果主节点正常运行，消费者是可以向从节点消费消息的，从节点回去找主节点要对应的消息

一旦主节点宕机，从节点无法对外提供主节点消息队列中的消息！



**镜像集群（高可用）**

在普通集群之上，做二次配置，rabbitmqctl set_policy...

高可用（减少系统不能提供服务的时间）+自动的故障转移（主节点宕机后，重新分配主节点）

![image-20210802191543470](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210802191543470.png)



### **其他**

如果不指定交换机，生产者会将消息发布给AMQP default交换机；而每一个队列，无论后天绑定了哪个交换机，先天会默认绑定AMQP default交换机（无法解绑，这个交换机也无法被删除）；而这个交换机的匹配方式，是通过生产者的routingKey匹配队列的queue name

这就解释了为什么在生产者不指定交换机时，消息会发送给名称为routingKey的队列。

在RabbitMQ的web管理端中，点进AMQP default交换机，会看到他的介绍。
The default exchange is implicitly bound to every queue, with a routing key equal to the queue name. It is not possible to explicitly bind to, or unbind from the default exchange. It also cannot be deleted.





