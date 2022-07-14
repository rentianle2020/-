Remote Dictionary Server（远程字典服务器）

完全开源免费，用C语言编写，遵守BSD协议



# 第一章 数据结构&对象

### sds 动态字符串

- int len 长度
- int free 空闲空间
- char buf[] 字符数组，以\0结尾



**与C字符串的区别**

- O(1)获取长度
- 字符创建/扩容时，空间与分配（需要15字节，额外分配15free）
- 字符串trim时，惰性空间释放
- 二进制安全（可以任意保存空字符）
- 因为以\0结尾，可以使用部分C语言<string.h>中的函数



### linkedlist 双端链表

- listNode *head
- listNode *tail
- long len：节点数量
- 节点复制函数，节点释放拿书，节点值对比函数

listNode：prev，next，value



### ziplist 压缩列表

当含有少量列表项，且每项都是小整数|短字符串时，为了节约内存，使用ziplist(压缩列表)作为底层实现。

优势：节约内存；存储在连续的地址中，更好的利用缓存。

![img](https://res.weread.qq.com/wrepub/epub_622000_68)

- int bytes：列表总长度
- int tail：列表尾部偏移量（头部地址 + tail = 尾部地址）
- int len：节点个数
- *entry
- int lend：末尾标记符

entry

- previous_entry_length：前一个节点的长度（根据前一个节点的长度，占1个或5个字节）
- encoding：00001011 -> "hello world"，高两位00表示字符数组，01011表示长度位11
- content



**连锁更新**

因为previous_entry_length不固定，添加或者删除元素，可能导致连锁更新，出现几率很小。

![img](https://res.weread.qq.com/wrepub/epub_622000_83)



### dict 字典

- dictType *type：包含了key比较函数等信息...
- void *privdata：私有数据
- dictht ht[2]：两个哈希表，ht[1]只会在对ht[0] rehash时使用
- int rehashidx：标识**渐进式rehash**进度，不在rehash时为-1

dictht哈希表，哈希冲突时采用拉链法

- dictEntry **table：dictEntry的数组指针
- long size：哈希表大小
- long sizemask：size - 1，用于计算索引值
- long used：键值对数量

dictEntiry：key, val, next



**哈希表扩展**

扩展大小：ht[1]的大小为第一个大于等于ht[0] * 2的质数

负载因子：ht[0].used / ht[0].size

扩展条件：服务器正在执行BGSAVE || BGWRITEAOF ? 负载因子 >= 5 ： 负载因子 >= 1

负载因子小于0.1时收缩



**渐进式rehash**

rehash不是一次性执行，而是使用rehashidx表示当前rehashing的下标，渐进式进行。

期间，删改查会在ht[0]和ht[1]两个哈希表上进行。



### intset 整数集合

当集合只包含整数元素时&元素不多时，Redis使用intset(整数集合)作为Set的底层实现；

- int encoding：编码方式 (int16, int32, int64)
- int length：元素数量
- int contents[]：元素数组



**升级**

当新添加的元素比所有元素类型都要长时，扩容元素数组，并将所有元素类型升级到新长度，放置到合适的地址上。

引发升级的元素，要么小于所有元素（放置在开头），要么大于所有元素（添加到结尾）

- 动态升级，提升灵活性
- 节约内存



### skipList 跳跃表

- skiplistNode *header, *tail：头尾节点指针
- long length：节点数量
- int level：最大节点层数

skiplistNode：

- struct skiplistLevel { skiplistNode *forward, int span 跨度} level[]：Node在不同层中的指针&跨度
- skiplistNode *backward：用于从后向前遍历时使用
- double score：分值
- *obj：成员对象

**每个节点的层高都是1~32之间的随机数**

![](https://res.weread.qq.com/wrepub/epub_622000_53)







### redisObject 对象

- type：类型
- encoding：编码方式，内部实现方式
- *ptr：底层数据结构实现的指针
- int refcount：通过引用计数，实现内存回收
- int lru：最近访问时间

对于Redis，键总是一个字符串对象，而值可以是STRING，LIST，HASH，SET，ZSET对象

> TYPE key：获取对应value的对象类型
>
> OBJECT ENCODING key：获得对应value的底层实现
>
> OBJECT REFCOUNT key：获得对应value的引用计数（通过修改redis.h/REDIS_SHARED_INTEGERS，创建共享整数，不支持共享其他数据结构，验证复杂度过高）
>
> OBJECT IDLETIME key：上一次访问key距离现在的时长，如果服务器打开maxmemory选项，并且使用lru算法，用于回收内存



**每种类型对应的编码&底层实现（3.0版本）**

![img](https://res.weread.qq.com/wrepub/epub_622000_89)



**字符串**

> int & sds两种底层实现

embstr：对于短字符串的优化，一次内存分配，包含redisObject和sds；只读，在执行APPEND指令后转化为raw

![img](https://res.weread.qq.com/wrepub/epub_622000_93)	

SET、GET：RANGE区间，BIT位

STRLEN、APPEND、INCR...



**列表**

> 3.2之前 ziplist & linkedlist两种底层实现
>
> 之后引入quicklist编码类型，使用Quicklist is a linked list of ziplists作为实现

LPUSH、RPUSH、RPOP、LPOP、LLEN

LRANGE：0代表第一个，-1代表最后一个元素

LTRIM：将链表删除TRIM到指定下标范围

BRPOP、BLPOP：阻塞式POP，设置阻塞等待时长timeout

> 如果消费者没有拿到数据，过段时间会再次从客户端发送请求；
>
> 堵塞式就是让POP请求排列成LIST，当有了数据，第一时间按照顺序满足LIST中的请求，如果到时还没有数据，再返回NULL

LMOVE、BLMOVE：从一个LIST POP到另一个LIST



**哈希**

> ziplist & dict两种底层实现

HSET、HGET、HMSET、HMGET、HINCRBY



**集合**

> dict & intset 两种底层实现

SADD，SMEMBERS、SISMEMBER

SPOP：默认随机弹出，如果加上count，会自动排序？

SINTER：两个Sets的交集；当然还有SUNION并集、SDIFF差集

SUNIONSTORE：先UNION，再STORE

SCARD：查看元素数量

SRANDMEMBER：随机获取而不POP



**有序集合**

> ziplist & (dict + skiplist) 两种底层实现

根据score进行排序，如果score相同，则按成员对象顺序排序。多节点可以包含相同的分数，但是成员对象必须是唯一的。

**指令**

ZADD：如果value已经存在，视为更新score

ZRANGE、ZREVRANGE

WITHSCORES后缀

ZRANGEBYSCORE：根据分数排序，-inf 60 表示60分(包含)以内的，60 100 表示60(包含)至100分(包含)

ZREMRANGEBYSCORE：删除区间元素



# 第二章 数据库

数据库默认16个数据库

> SELECT n：客户端选择第n个数据库为当前DB

<img src="https://res.weread.qq.com/wrepub/epub_622000_123" alt="img" style="zoom:67%;" />	

数据库主要由dict和expires两个字典构成，其中dict字典负责保存键值对，而expires字典则负责保存键的过期时间。

<img src="https://res.weread.qq.com/wrepub/epub_622000_132" alt="img" style="zoom:67%;" />	



**读写key时的维护**

- 根据key是否存在，更新命中hit和不命中miss的次数
- 更新LRU时间
- 检查过期
- 如果有被WATCH，修改后标记为dirty脏数据；对脏键计数器+1，到一定值后会触法持久化
- 数据库通知功能



### 键过期

过期信息以到期毫秒时间戳的形式存储在expires字典中

> EXPIRE key time：设置key存活是时长
>
> EXPIREAT key unit时间戳：设置key到期时间
>
> TTL key：计算key剩余存活时间
>
> 前边加P转为毫秒



**过期删除策略**

- 定时删除：对CPU不友好
- 惰性删除：对内存不友好
- 定期删除：折中

Redis使用惰性+定期两种策略



**RDB**

执行SAVE | BGSAVE生成新的RDB文件时，检查过期，过期的键不会被持久化

> 从服务器会持久化所有键，等主服务器同步时（收到DEL message）再删除过期键
>
> <img src="https://res.weread.qq.com/wrepub/epub_622000_139" alt="img" style="zoom:67%;" />



**AOF**

当过期键被惰性删除或者定期删除之后，程序会向AOF文件追加（append）一条DEL命令，来显式地记录该键已被删除。

执行AOF重写时，和RDB类似，检查过期，过期键不会被持久化。



**数据库通知**

SUB/PUB

当Redis命令对数据库进行修改之后，服务器会**根据配置**向客户端发送数据库通知



### RDB持久化

保存数据库中的键值对数据

Redis服务器启动时，若检测到RDB文件，则自动加载（AOF优先）

> SAVE：阻塞Redis服务器，由服务器进程执行保存工作
>
> BGSAVE：由子进程执行保存工作

<img src="https://res.weread.qq.com/wrepub/epub_622000_144" alt="img" style="zoom: 67%;" />	



通过save设置多个配置，服务器每100毫秒遍历一次，只要有一个要求满足，则自动执行BGSAVE命令

距离上一次BGSAVE的数据的修改次数，由dirty脏数据计数器统计

```go
save 900 1 		//900秒内至少对1个数据进行修改
save 300 10 	//300秒内至少对10个数据进行修改
save 60 10000 	//60秒内至少对10000个数据进行修改
```

<img src="https://res.weread.qq.com/wrepub/epub_622000_149" alt="img" style="zoom:50%;" />	



**RDB文件结构**

https://weread.qq.com/web/reader/d35323e0597db0d35bd957bkd2d32c50249d2ddea18fb39

> od -c dump.rdb：通过ASCII编码打印RDB文件
>
> cat读不全因为dump.rdb最后一行没有\n（ends in a newline），一般编辑器如vim都会自动帮我们在最后一行末尾添加\n
>
> 行为重现
>
> ```bash
> echo -n 'hello world' >> file.txt
> ```



### AOF持久化

Append Only File，所有对数据库的修改，以Redis命令的格式保存。

<img src="https://res.weread.qq.com/wrepub/epub_622000_178" alt="img" style="zoom:67%;" />	

append -> 写入内存缓冲区（write） -> 文件同步到磁盘（OS提供了fsync和fdatasync两个同步函数）



**appendfsync决定了同步的时间**

- always：写完立即同步（效率最低）
- everysec：每秒同步
- no：同步时间由OS控制



**AOF文件载入&数据还原**

<img src="https://res.weread.qq.com/wrepub/epub_622000_180" alt="img" style="zoom:50%;" />	



**AOF文件结构**

> config set appendonly yes
>
> config set appendfsync everysec
>
> cat appendonly.aof



**AOF重写**

为了解决AOF文件体积膨胀的问题

重写并不是读取并分析AOF文件再压缩，而是直接读取数据库的键值，使得AOF中没有一条命令是多余的。

```
SADD num 1
SADD num 2 3 4
SREM num 1
SADD num 1 5
转换为 SADD num 1 2 3 4 5
```



为了防止在执行AOF中命令时，客户端缓冲区溢出，若元素个数超出redis.h/REDIS_AOF_REWRITE_ITEMS_PER_CMD，则拆分为多条命令



**后台重写**

> BGREWRITEAOF

为了不阻止服务器（父进程），AOF重写程序放到子进程执行。

开启重写程序后，AOF缓冲区继续照常写AOF；直到子进程重写完毕，发送信号，让父进程同步所有AOF重写缓冲区中的内容，并原子的覆盖现有AOF文件。

<img src="assets/epub_622000_183" alt="img" style="zoom:67%;" />	



### 事件

**文件事件**

socket绑定READADBLE、WRITABLE等事件

1. 多路复用，创建Epoll

2. 客户端请求连接，初始化socket并添加到epoll，初始化socket.sock.wait_queue中给epoll添加事件的回调函数

3. 客户端发送命令请求，epoll_wait()返回READABLE事件，请求处理器执行命令

4. 将WRITABLE事件关联socket，epoll_wait()返回WRITABLE事件，执行套接字写入操作

   <img src="assets/epub_622000_185" alt="img" style="zoom:50%;" />	



**时间事件**

由id，时间戳，事件处理函数这3个属性组成。

分为定时事件(一次性)和周期性事件

服务器会轮流执行文件事件&时间时间，事件处理过程中不会进行抢占。

服务器在一般情况下只执行serverCron函数一个时间事件，并且这个事件是周期性事件

时间事件的实际处理时间通常会比设定的到达时间晚一些



# Bitmap

比特表使用了String数据结构，只不过用这个value是一串最长2^32 - 1的0/1

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210812212806" alt="img" style="zoom:67%;" />	

**指令**

SETBIT、GETBIT、BITCOUNT

BITOP：Bit operations，拿一个destkey和其他的keys做AND OR XOR NOT等操作

BITPOS：找到第一个有明确0，或者明确1的value的offset

**使用场景**

存储布尔信息，类似是否登录，是否通过验证



# HyperLogLog

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