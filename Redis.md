# Redis

Remote Dictionary Server（远程字典服务器）

完全开源免费，用C语言编写，遵守BSD协议



**主要特点**

- 数据结构存储 data structure store
- 原子操作 atomic operations
- 内存数据集 in-memory dataset



**其他功能**

- 事务

- 持久化

- Sentinel 自动故障转移，提高可用性

- Cluster 集群

- Pipline 管道



## 数据类型

Redis is not a *plain* key-value store, it is actually a *data structures server*, supporting different kinds of values. 

What this means is that, while in traditional key-value stores you associate string keys to string values, in Redis the value is not limited to a simple string, but can also hold more complex data structures. 



#### **Redis keys**

binary safe，可以使用序列作为key，从String、到JPEG file、甚至空字符串都可以作为合法key value

- key不要太长，从内存的角度，或者从key值对比的角度（哈希冲突，equals判断的时候要把key串里的每个char拿出来对比嘛！）

- key也不要太短，可读性会很差

- 尽量严格根据格式命名，比如user:1000，object-type:id

  > Dots or dashes are often used for multi-word fields

- key size最大512MB

**指令**

EXISTS、DEL、TTL、PTTL

PEXPIRE、EXPIRE：过期时间始终以毫秒级别计算，过期时间会以unix时间戳的形式被持久化到磁盘

PERSIST：移除过期时间，持久化key

FLUSHDB、FLUSHALL：清空当前库、清空所有16个库



#### **Strings**

最简单的数据类型，也是Memcached唯一的数据类型。

**指令**

SET、GET、INCR、INCRBY、DECR、DECRBY、MSET、MGET

GETSET：set new value，return old value



#### Lists

通过链表实现

优势：头部和尾部O(1)级别的快速增删

劣势：通过下标查询的复杂度是O(n)

**指令**

LPUSH、RPUSH：可以同时添加多个元素，如果key不存在则自动创建

RPOP、LPOP：元素出队并返回，如果key中没有元素了则自动删除

LRANGE：0代表第一个元素，-1代表最后一个元素，-2代表倒数第二个...

LTRIM：先PUSH再TRIM，剔除老数据，可以保证拿到最新的N个数据，

```ruby
LPUSH mylist <some element>
LTRIM mylist 0 999
```

BRPOP、BLPOP：Block POP

> 如果消费者没有拿到数据，过段时间会再次从客户端发送请求；这样不好，制造无效访问，而且不能第一时间获得生产的元素；
>
> 堵塞式就是让POP请求排列成LIST，当有了数据，第一时间按照顺序满足LIST中的请求，如果到时还没有数据，再返回NULL

LLEN、LMOVE、BLMOVE

**使用场景**

生产者消费者模型，消息发布



#### Hashes

就是普通的HashMap，KV键值对

**指令**

HSET、HGET、HMSET、HMGET、HINCRBY



#### Sets

无顺序的Stirng集合

**指令**

SADD，SMEMBERS、SISMEMBER

SPOP：默认随机弹出，如果加上count，会自动排序？

SINTER：两个Sets的交集；当然还有SUNION并集、SDIFF差集

SUNIONSTORE：先UNION，再STORE

SCARD：查看元素数量

SRANDMEMBER：随机获取而不POP

**适用场景**

给一个对象打上多个标签，当然也可以给标签附上对象；发牌

```
> sadd news:1000:tags 1 2 5 77
(integer) 4

> sadd tag:1:news 1000
(integer) 1
> sadd tag:2:news 1000
(integer) 1
> sadd tag:5:news 1000
(integer) 1
> sadd tag:77:news 1000
(integer) 1
```



#### Sorted Sets

根据score进行排序，如果score相同，则按照字典顺序排序。因为set中的元素不能重复，所以总能分出个先后

skipList + hashTable实现，添加&删除O(log(N))维护跳跃表，实现排序。获取O(1)，由HashTable实现

**指令**

ZADD：如果value已经存在，视为更新score

ZRANGE、ZREVRANGE

WITHSCORES后缀

ZRANGEBYSCORE：根据分数排序，-inf 60 表示60分(包含)以内的，60 100 表示60(包含)至100分(包含)

ZREMRANGEBYSCORE：删除区间元素



#### Bitmaps

比特表使用了String数据结构，只不过用这个value是一串最长2^32 - 1的0/1

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210812212806" alt="img" style="zoom:67%;" />	

**指令**

SETBIT、GETBIT、BITCOUNT

BITOP：Bit operations，拿一个destkey和其他的keys做AND OR XOR NOT等操作

BITPOS：找到第一个有明确0，或者明确1的value的offset

**使用场景**

存储布尔信息，类似是否登录，是否通过验证



#### HyperLogLogs

A HyperLogLog is a probabilistic data structure used in order to count unique things

通常情况下，必须保存历史记录才能判断本次ADD是否重复，但是HLL用了一种算法，用精度换内存。

损失少量的精度，节省保存元素的内存（只用一个最大12k bytes的内存空间），因为它只用来做count++

**指令**

PFADD、PFCOUNT（大约的无重复元素的数量）



## 事务 Transaction

MULTI、EXEC、DISCARD、WATCH是Redis实现事务的根本

事务允许一次执行一组指令，而且有两点保证

1. 所有指令串行执行，中间不会穿插别的Client发送的指令
2. 原子性，全部执行成功或者全部执行失败



EXEC前，出现语法错误返回ERROR，则整个事务在EXEC时自动DISCARD；客户端也可以根据返回的ERR，手动DISCARD

EXEC后，出现类型等错误，只有出错的语句失效，其他指令照常执行

>  even when a command fails, all the other commands in the queue are processed

```ruby
127.0.0.1:6379> multi
127.0.0.1:6379(TX)> set k2 v2
QUEUED
127.0.0.1:6379(TX)> lpop k2 5
QUEUED
127.0.0.1:6379(TX)> exec
1) OK
2) (error) WRONGTYPE Operation against a key holding the wrong kind of value
```



#### 为什么不支持roll backs？

对于学过RDBMS的人来说，事务过程中出现错误，就应该rollback。

- Redis命令在语法错误/数据类型错误是才会报错，所以报错等于编程错误
- 不需要rollback，以为着更简单的内部结构和更快的执行速度

rollback does not save you from prograaming errors, the kind of errors required for a Redis command to fail are unlikely to enter in production



#### WATCH

WATCH is used to provide a check-and-set (CAS) behavior to Redis transactions.

如果在WATCH和EXEC之间，被WATCH的key被另一个客户端修改，EXEC时会返回(nil)

WATCH也可以看成是一种乐观锁



So what is WATCH really about? It is a command that will make the EXEC conditional: we are asking Redis to perform the transaction only if none of the WATCHed keys were modified



## 持久化 Persistence

AOF works by incrementally updating an existing state, like MySQL or MongoDB does, while the RDB snapshotting creates everything from scratch again and again, that is conceptually more robust.

官方建议两个都开着，并计划着未来将两个策略合并成一个



#### RDB (Redis Database)

**优势**

- 一个紧凑的单文件代表Redis Database，可以自定义多长时间照一次，形成不同的version
- 最大化Redis性能，fork()一个子进程处理所有的IO操作

**缺点**

- 不能完全保证数据完整性（尽管可以设定save point，比如每5分钟，如果超过100次write，就save）
- 如果数据量很大，fork()也会变成一个费时操作



#### AOF(Append Only File)

**优势**

- 多种同步策略，每次query同步一次，还是每秒同步一次

  > The suggested (and default) policy is to `fsync` every second. It is both very fast and pretty safe.

- AOF会自己在后台进行优化，完全安全的进行；old依然使用，new准备好了就切换过去

**缺点**

- 通常比RDB files更大
- 如果同步策略是`always`，会比RDB效率更低



#### 持久化过程

**两种持久化策略，都是使用的copy-on-write方法**

RDB 快照：

1. fork()一个子进程
2. 开始将Databases写入临时文件
3. 替代旧文件

 AOF 同步：

1. fork()一个子进程，
2. 父进程将更改写入in-memory buffer
3. 子进程写入临时文件（同时父进程接着往buffer写新的数据）
4. 完成后，将buffer中的新内容放到临时文件的末尾，然后替换原文件

如果同步被打断`truncated`怎么办，放弃last non well formed command in the file就完事了

如果AOF文件中间夹杂乱码`corrupted`，可能需要我们手动修改文件了



## 管道 Pipeline

节约了多次访问的TTL网络访问时间，减少了Redis服务器IO读取请求的时间；从而大大提高了效率

注意：服务器需要在内存中堆积响应，然后一起返回；所以如果请求量过大，最好分成合理的份数。

for instance 10k commands, read the replies, and then send another 10k commands again, and so forth. 



**管道和事务的区别**

Pipelining is primarily a network optimization. It essentially means the client buffers up a bunch of commands and ships them to the server in one go. The commands are not guaranteed to be executed in a transaction. The benefit here is saving network round trip time for every command.

Redis is single threaded so an *individual* command is always atomic, but two given commands from different clients can execute in sequence, alternating between them for example.

Multi/exec, however, ensures no other clients are executing commands in between the commands in the multi/exec sequence.



## 分布式 Cluster (Docker)



Every Redis Cluster node requires two TCP connections open. The normal Redis TCP port used to serve clients, for example 6379, plus the port obtained by adding 10000 to the data port, so 16379 in the example.

This second *high* port is used for the Cluster bus, that is a node-to-node communication channel using a binary protocol. The Cluster bus is used by nodes for failure detection, configuration update, failover authorization and so forth. Clients should never try to communicate with the cluster bus port, but always with the normal Redis command port, however make sure you open both ports in your firewall, otherwise Redis cluster nodes will be not able to communicate.



**数据分配&故障转移**

> port 6379：正常的CS架构通信
>
> cluster bus port 16379 (port+10000)：不同节点之间的通信
>
> 目前Redis不支持IP address remap，所以不能使用Docker的端口转换技术
>
> 所以必须使用--net=host，直接绑定宿主机的不同端口



#### 数据分配 data sharding

every key is conceptually part of what we call a **hash slot**.

- Node A contains hash slots from 0 to 5500.
- Node B contains hash slots from 5501 to 11000.
- Node C contains hash slots from 11001 to 16383.

可以使用hash tags的方法强制让一些key属于同一个hash slot

为了避免Node挂掉后，它对应的hash slots数据丢失，使用主从模型`master-slave model`



一个好的Redis客户端，能够缓存hash slots和nodes addresses，从而直接访问对的节点地址

Resharding：选择一个集群-->选择重新分配槽的数量-->选择接收者-->选择给与者(可以让每个人都献出一点爱)



**主从复制和集群的区别**

data-sharing和nothing-sharing的区别。

集群：每个节点存储一些，通过共享数据组成一个系统。

复制：没有任何数据共享，每个节点都是独立且完整的系统。



#### **故障转移**

redis.conf通过**cluster-slave-validity-factor**设置，如果主机断开连接，设置多久之后开始接替位置，甚至就一直等着主机回来



由于异步的主从复制，Redis不能保证**强一致性**。如果写入B之后，没有完成主从复制就宕机，自动failover，那这些写入的数据就丢失了。如果非得先同步再reply client，性能太差。

- Your client writes to the master B.
- The master B replies OK to your client.
- The master B propagates the write to its slaves B1, B2 and B3.



但是可以手动failover，指定一个从机来接替主机的位置。这种情况下，redis会在保证数据完全转移成功的情况下才会进行failover

```
# Manual failover user request accepted.
# Received replication offset for paused master manual failover: 347540
# All master replication stream processed, manual failover can start.
# Start of election delayed for 0 milliseconds (rank #0, offset 347540).
# Starting a failover election for epoch 7545.
# Failover election won: I'm the new master.
```



#### 创建集群

启动多个redis服务，每个配置不同的IP+端口号

```
port 7000
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
```

创建集群，--cluster-replicas 1指的是我们希望每一个master能有一个slave

**这里注意要用公网IP建立集群，才能被其他IP地址访问集群！**

```
redis-cli --cluster create 47.106.137.238:7000 47.106.137.238:7001 \
47.106.137.238:7002 47.106.137.238:7003 47.106.137.238:7004 47.106.137.238:7005 \
--cluster-replicas 1
```

**添加节点**

添加master节点，然后给他reshard（刚添加完是没有任何slot的）

```
redis-cli --cluster add-node 127.0.0.1:7006 127.0.0.1:7000
```

添加slave节点，给他指定master，如果不指定的话就自动分配给一个备份最少的master

```
redis-cli --cluster add-node 127.0.0.1:7006 127.0.0.1:7000 --cluster-slave --cluster-master-id 3c3a0c74aae0b56170ccb03a76b60cfe7dc1912e
```

**删除节点**

The first argument is just a random node in the cluster, the second argument is the ID of the node you want to remove.

被删除的主节点必须为空，所以先将他的slots先都分配给别人，再删。

```
redis-cli --cluster del-node 127.0.0.1:7000 `<node-id>`
```



## **哨兵sentinel**

- 监控Monitoring：实时监控redis instance
- 提示Notification：监控到错误，传递错误信息
- Automatic failover：自动故障切换
- Configuration provider：Client连接Sentinel询问访问地址，Sentinel做服务发现



### Springboot-Redis

```yml
spring:
  redis:
    host: 47.106.137.238
    port: 6379
    password: 1028
```

```java
@RestController
public class Test{

    @Autowired
    private RedisTemplate<String,String>redisTemplate;
    
    @GetMapping("/test")
    public void set(){
        redisTemplate.opsForValue().set("k1","v1");
        String k1 = redisTemplate.opsForValue().get("k1");
        System.out.println(k1);
    }
}
```



# 后期可重点关注Sentinel，Replica，面试题