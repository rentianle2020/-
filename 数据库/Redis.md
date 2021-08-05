# Redis



### NoSql

Not Only SQL，泛指非关系型数据库

以键值对来存储，以CAP来对应关系型数据库的ACID

强悍的读写性能，在大数据量下同样表现优秀。这得益于它的无关系性，数据库的结构简单

易扩展，大数据量高性能，多样灵活的数据模型



**历史概括**

90年代，单数据库轻松应付

瓶颈：数据总量，索引（B+ Tree）总量，访问量（读写混合）



加上Cache，缓存替数据库挡了一层；垂直拆分（根据不同的业务进行拆分）

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210721182221059.png" alt="image-20210721182221059" style="zoom:67%;" />	



主从复制，读写分离

写的都放在主库，读放在从库

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210721182536319.png" alt="image-20210721182536319" style="zoom:50%;" />	



分库分表，水平拆分（同一张表中的数据拆分到不同的数据库中进行存储）

大文本文件存储在MySQL，导致数据表非常的大

随着数据和日志成倍增长，传统的关系型数据库难以支撑



**3V+3高**

海量Volume（海量数据），多样Variety（文字，视频，背景音乐），实时Velocity（数据准实时更新）

对应3V，出现3高解决方案：高并发，高可扩（纵向：一台主机，单核不行双核，双核不行四核；横向：一台机器不行，多台，做集群，负载均衡，调度算法），高性能



**多数据源，多数据类型**

1. 商品基本信息：存放在MySQL，关系型数据库
2. 商品描述、详情、评价（多文字类）：存放在MongoDB
3. 商品的图片：分布式的文件系统，OSS
4. 商品的关键字：搜索引擎，ISearch
5. 波段性的热点高频信息（情人节的玫瑰和巧克力）：内存数据库，Redis，Tair
6. 商品的交易，计算价格，积分累计：外部系统，第三方接口

统一数据平台服务层UDSL（统一API接口）



**NoSQL如何设计**

BSON：Binary JSON，传统情况需要建表，主外键连接；而JSON字符串很灵活，不需要关联

NoSQL数据模型：聚合模型 --> KV键值，Bson，列族，图形



**NoSQL数据库的四大分类**

KV键值：Redis，通常使用hashtable实现

文档型数据库：MongoDB（分布式文件存储的数据库，C++编写，介于关系数据库和非关系数据库之间）

列存储数据库：HBase

图关系数据库：Neo4J，朋友圈的社交网络，广告推荐系统



**CAP+BASE**

Consistency强一致性：业务数据精确度

Availability可用性：服务器稳定程度

Partition tolerance分布式容错性：允许分布式



CAP的3选2：一个分布式系统不可能同时很好的满足CAP，最多只能同时满足两个

CA：强一致性和可用性，扩展不好（RDBMS）

CP：强一致性，分区容忍，性能不高（Redis）

AP：大多数网站的妥协



避免数据库大表关联查询，将常用数据拼接，放到redis优先访问



BASE

基本可用Basically Available

软状态Soft state（硬状态保证多个服务节点的数据都是一致的，而软状态允许数据延迟）

最终一致Eventually consistent



基于CAP，BASE的策略如下

放松C，换取A；然后采用BASE解决C，达到最终一致



**分布式+集群**

分布式：：不同的多台服务器上部署不同的服务器模块（工程）

集群：不同的多台服务器上面部署相同的服务模块



### Redis入门概述

Remote Dictionary Server（远程字典服务器）

完全开源免费，用C语言编写，遵守BSD协议

高性能的（key/value）分布式内存数据库，基于内存运行，并支持持久化



redis-benchmark：测速

redis-server 配置文件：开启服务器

redis-cli：连接服务器



**基本命令**

dbsize：数据库量

keys *：列出所有key

flushdb：清空档期那库

flushall：清空所有16个库



redis默认关闭安全，让UNIX负责程序的访问权限



### Redis五大数据类型

Redis不是平铺的KV存储，而是数据类型的存储；这个的意思就是，不仅仅是String，String的KV，而是支持不同类型的存储

- String：Redis的二进制安全String可以包含任何数据，比如jpg图片或者序列化的对象，最多支持512M
- Hash（哈希）：类似Java中的HashMap
- List（列表）
- Set（集合） 
- Zset（sorted set 有序集合）：每个元素关联一个double类型愤俗，成员唯一，但是分数（score）可以重复



**常用命令**

http://redisdoc.com/



键key：

- set 增/改；del 删；get 查
- keys *：查看所有key
- type key：查看key类型
- exists key：返回1表示有，返回0表示没有
- move key db：将key移动到db号库
- exipre key 秒钟：给key设置过期时间
- ttl key：查看多少秒过期，-1表示永生，-2表示过期



字符串String：

- set/get/del/append/strlen

- incr/decr/incrby/decrby：数组加减，redis将其封装为原子操作，不用担心并发问题

  > 线程1 get到10，线程2 get到10，线程1 ++，线程2 ++，结果为11，应该为12；但是使用redis就不会出现这种问题了，因为整个incr为原子操作，get和++视为一个最小执行单位，绑定执行

- getrange key beginIndex endIndex：截取字符串；setrange k1 0 xxx：在下标为0的地方覆盖xxx

- setex key 秒数 value：指定存活秒数；setnx（set if not exist）：返回1表示生效，返回0表示失败

- mset/mget/msetnx：批量操作



列表List：单key多value，字符串链表，左右都可以插入

- lpush/rpush/lrange
- lpop/rpop
- lindex：按照索引获得元素
- llen
- lrem key 删N个value：lrem list01 2 3，删除list01中的2个3
- ltrim：截取，重新赋值给key、
- rpoplpush source dest：从源右pop，从目标左push
- lset key index value：指定下标赋值
- linsert key before/after value valueAdd：在指定的value前后插入值



集合Set：单key多value

- sadd/smembers/sismember
- scard：元素个数
- srem key value：删除集合中元素
- srandmember key 随机出多少个数
- spop key：随机出集合
- smove key1 key2 key1中的某个值：将key1中的某个值移动到key2集合
- 差集 sdiff；交集 sinter；并集 sunion



哈希Hash：KV模式不变，但是V是一个键值对

- **hset/hget/hmset/hmget/hgetall/hdel**
- hlen
- hexists key 在key中的某个值的key
- **hkeys/hvals**
- hincrby/hincrbyfloat
- hsetnx



有序集合Zset（sorted set）：在set基础上，加了一个score值；set是 k1 v1 v2 v3，zset是k1 score 1 v1 score2 v2

- zadd/zrange
- zrangebyscore key 开始score 结束score：withscores，（ 不包含，limit 开始下标 长度
- zrem key 某score下对应的value：删除元素
- zcard：统计元素数量（score+value是一个元素）
- zcount/zrank/zscore
- zrevrank key values：逆序获得下标
- zrevrange：逆序列出
- zrevrangebysocre key 结束score 开始score



Bitmap位表（testbitmap --> 0001001001）

setbit key value offset 0/1：给一个指定key的值得第offset位 赋值为0/1

getbit key offset：返回一个指定key的二进制信息

bitcount key start offset：返回一个指定key中位的值为1的个数



hyperloglog

专门用来做count统计的，用于计算基数（不重复元素）个数

pfadd key value...：添加元素，成功返回1，失败返回0

pfcount key：计算元素个数

pfmerge key key1 key2：将key1和key2的value合并到key中



Geospatial

针对地理位置，设置经度和纬度

geoadd key 经度 纬度 value...：添加元素

geopos key value：获取经度纬度

geodist key value1 value2：得到两个value的直线距离

georadius key 经度 纬度 半径（km）：返回方圆n公里内的所有value



### redis配置文件



**GENERAL**

tcp-backlog：保持默认511，只有高并发环境下才需要更高的值

timout：空闲多少秒后，关闭连接；0代表不关闭

tcp-keepalive：多少秒检测1次连接，建议设置为60

loglevel：日志级别；日志级别越高，信息越小，只关心更重要的信息；开发时可以debug，上线后notice或者warning

logfile：日志输出文件

syslog：系统日志，默认关闭

databases 16：默认启动16个数据库，可以通过 select 库号，改变数据库



**SNAPSHOT**

60分钟1次改动，2分钟10次，1分钟10000次改动；就会刷新内存到磁盘

出错停止，压缩，验证，都默认打开就好

RDB文件位置为当前conf文件的目录

文件名默认：dump.rdb



**SECURITY**

默认全部为注释

config set requirepass 密码

auth 输入密码，才能输入指令



**LIMITS 限制**

Maxclients：同一时间最大客户端连接数

Maxmemroy-policy：缓存过期策略，LRU，RANDOM，TTL（移除最近要过期的），Noeviction（不移除，返回错误信息）



**APPEND ONLY MODE**

AOF默认关闭

默认文件名appendonly.aof

Appendfsync：Always同步持久化，性能差但是完整；默认Everysec，每秒记录，如果一秒内宕机，有数据丢失

设置重写时的基准值（大于多少MB），设置重写的百分比（比上次重写大多少时重写）



### 持久化



**RDB**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210722135337986.png" alt="image-20210722135337986" style="zoom:67%;" />	

dump.rdb文件

在指定的时间间隔，将内存中的数据集Snapshot快照写入内存；恢复时将快照文件读到内存

Redis单独创建（fork）一个子进程来进行持久化，数据先写入临时文件，持久化结束后再替换上次持久化的文件；写入过程由子进程完成，主进程不进行其他IO操作



save指令：即刻保存，其他全部阻塞

bgsave：即可保存，异步进行



优势：

在恢复大的数据集时，更快

劣势：

Fork的时候，内存中数据被克隆，2倍变大

最后一次持久化的数据可能丢失



**AOF**

以日志的形式来记录每一个写操作

可以和RDB同时存在，redis优先加载AOF文件来恢复原始数据

如果AOF文档出现乱码出现文件损坏，使用redis-check-aof --fix appendonly.aof自动修复



数据完整，日志易读，支持重写（瘦身）

恢复慢（一条一条），而且文件体积大



**Rewrite**

AOF采用追加的方式，导致文件越来越大

重写机制：当AOF文件大小超过设定的阈值，压缩AOF文件内容，bgrewriteaof

当AOF大小是上次rewrite大小的一倍，且大于64MB，就会触发



![image-20210722163830360](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210722163830360.png)



### 事务

Redis事务是一个单独的隔离操作：事务中所有命令按顺序执行，事务执行过程中，不会被其他命令打断，

Redis事务主要作用就是串联多个命令，防止别的命令插队



**常用命令**

MULTI：事务开启

EXEC：执行事务块内的命令

DISCARD：取消事务，放弃事务块中的所有命令

WATCH key：监控一个key，如果事务执行前发现key被改动

UNWATCH：解除对所有key的监控



**WATCH监控**

事务开始前WATCH一个key

监控的key，如果在事务中被改变了，EXEC时不会返回OK执行成功，而是返回(nil)

执行EXEC后，之前的监控锁都会被取消掉



WATCH指令类似乐观锁，check-and-set机制，乐观锁适用于多读的应用类型，提高吞吐量



MULTI开启 --> 组队 --> EXEC执行

> 没有隔离级别概念，提交前都不会被实际的执行；所以就不存在“事务内的查询要看到事务内的更新，在事务外的查询不能看到”这种头疼的问题
>
> 不保证原子性，如果事务中有一条命令执行失败，其余命令仍然被执行，没有回滚



集体连坐：组队时命令出错；这时还可以继续组队，依然可以强行EXEC执行，但是命令会全部失败

冤头债主：运行时命令出错；冤有头债有主，报错的失败，其他的正常执行



### 发布订阅

subscribe 频道：订阅频道

publish 频道 消息：在指定频道发布消息

psubscribe [通配符]频道前/后缀[通配符]：批量订阅



### 主从复制

Master主机数据更新后根据配置和策略，自动同步到Slave备机。Master以写为主，Slave以读为主

作用：读写分离、容灾恢复



**命令**

info replication：查看主仆信息

slaveof 主库IP 主库端口：当前服务作为某个主服务的从服务；从头复制到尾

slaveof no one：反客为主



**一主二仆**

1个Master，2个Slave

主机shutdown后，重新连接，从机整个过程中原地待命，主机重新启动服务后，依然保持主从关系

从机shutdown后，重新开启后，需要重新指定主机，否则默认没有主从关系

所有从机都找一个主机要信息，过度中心化，主机压力大



**薪火相传**

1个Master，2个Slave，只不过2 --> 1，3 --> 2

2这台机器类似包工头，虽然有一个slave，但是它本身也是1的slave，所以role还是slave



**反客为主**

主机挂掉后，从机中的一个slaveof no one成为新主机，其他从机重新绑定新主机

这时候主机重新回来，它还是master，但是没有任何slave了



**复制原理**

slave连接到master后发送sync命令，master接到命令后台存盘，将整个数据文件发送到slave

首次连接为全量复制（master传所有，slave复制所有），后边新的修改命令为增量复制（master将新增命令发给slave，slave新增）

只要重连master，就会自动执行一次完全复制



**哨兵sentinel**

反客为主的自动挡，能够后台监控主机是否故障；如果故障，则根据投标自动将一个从库转换为主库；master重新启动后，会变成slave

1. 建一个sentinel.conf
2. 输入sentinel monitor 被监控的主机名 IP+端口 票数多余多少成为新主机（sentienl monitor host 127.0.0.1 6379 1）
3. redis-sentinel /myredis/sentinel.conf启动哨兵



### Jedis

在redis.conf中，将bind 127.0.0.1注释掉，将protected-mode从yes变为no

便可以远程访问redis了！



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



### Redis集群

集群实现了对Redis的水平扩容，启动N个redis节点，每个节点存储总数据的1/N

主从节点，主节点掉了之后，从节点上位

以hash的方法分配key，计算哈希值，redis集群有16384个插槽（hash表大小），第一个节点收录从0-4999插槽，第二个收录从5000-10000插槽...

存储一个key k1，计算k1的index为500，放到第一个节点...



无中心集群：不用通过某个中心服务器来分配任务，任何一个主从服务器都可以作为集群的入口

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210729212351618.png" alt="image-20210729212351618" style="zoom:67%;" />	

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210729213649637.png" alt="image-20210729213649637" style="zoom: 67%;" />	



优点：实现扩容、分摊压力、无中心配置相对简单

缺点：多键操作不支持（需要设置一个组key）、早期redis不支持集群，老项目难以迁移



### Redis应用问题解决



**缓存穿透**

现象：

1. 应用服务器压力突然变大（大量请求）
2. redis命中率降低
3. 一直查询数据库

原因：

1. redis查询不到数据
2. 出现很多非正常url访问（key值是随便写的，为了让服务器瘫痪）



解决方案：

1. 对空值做缓存（数据库查不到为null，将null保存到redis，过期时间较短）
2. 设置可访问的名单（白名单），使用bitmaps定义可访问的名单
3. 布隆过滤器，底层就是bitmaps
4. 实时监控，发现redis命中率急剧降低，排查访问对象



**缓存击穿**

现象：

1. 数据库访问压力瞬间增大
2. redis里面没有出现大量key过期
3. redis正常运行

原因：

1. redis某个key过期了，但是大量访问这个key

解决方案：

1. 预设热门数据，加大热门数据key的时长
2. 实时调整热门数据key的过期时长
3. 使用锁（排队访问数据库）



**缓存雪崩**

现象：

1. 数据库压力变大 --> 应用大量等待 --> 服务器崩溃

原因：

1. 极少时间段，大量热门key集中过期

解决方案：

1. 多级缓存，nginx缓存 + redis缓存 + 其他缓存
2. 使用锁或者队列
3. 设置提前量，更新key的时效
4. 让缓存失效时间分散开



### 分布式锁

共享锁，在分布式环境下，同时锁住多个服务器的某个key

使用setnx，setIfAbsent()来set一个锁，然后设定过期时间。

如果锁过期了，服务器才反应回来要继续执行，最后释放，这时候释放的可能不是自己的锁

所以setIfAbsent（”lock“，UUID），释放的时候也是做一个判断，如果这时候已经不是自己持有锁了，就别把别人的锁释放掉了！

通过LUA脚本实现释放锁的原子操作（可能判断UUID相等，然后想删之前，锁过期，结果把其他人的UUID锁删除了）