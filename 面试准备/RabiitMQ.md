### RabbitMQ

使用Erlang语言编写，实现AMQP协议的消息队列中间件



### 特点

可靠，灵活路由，集群扩展，高可用，多协议，跨语言，管理界面



### AMQP是什么

高级消息队列协议(Advanced Message Queuing Protocol)

由Type，Channel（连接会话session）为必须项，各种具体Class&Method&Arguments组成

三大组件：Exchange，Queue，Binding规则



Type：Method，右边需要给出具体Method（如Declare，Bind，Publish，Conusme），然后Arguments中包括

- Publish：Exchange名称和Routing-Key
- Consume：Queue名称
- Declare：Queue/Exchange名称，是否Durable等Flag
- Bind：Bind的Queue和Exchange，还有Exchange到Queue的Routing-Key
  - 还有其他的Method，包括Connection，Qos，各种Ok（Consume-Ok，Declare-Ok），Ack，Nack

Publish和Consume还会在同一个请求中包含额外的两个AMQP协议包，Type分别是Content Header和Content Body

其他Type还包括HeartBeat



### 死信队列

导致死信的原因

- 消息被拒绝，Nack，且Requeue=false
- 消息TTL过期
- 队列满了

Consumer端抛异常，发送Nack包，配置default-requeue-rejected: false，这样就不会一直requeue



### 消息的可靠传递

MQ收到服务器Publish的消息（并持久化后），发送Ack给服务器

服务器Consume消息后，手动发送Ack给MQ



### 重复Publish

使用内部插件https://github.com/noxdafox/rabbitmq-message-deduplication

发送消息时在header中给定一个全球为一个ID，插件自动去重



### 重复Consume

防止重复消费，最好的方式是让消息的处理是幂等性的（对于重复的操作，可以保持一样的结果），这样的话也不怕重复Publish了

如果无法保持幂等性，可以在Message中添加全球ID，如果Consume的Message中redelivered flag is on，就去判断之前是否有处理过这个ID。



### 高可用

开启镜像模式，每个节点都是平等的，互相同步数据。