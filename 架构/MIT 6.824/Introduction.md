### 为什么需要分布式系统？

- 效率：parallelism
- 容错：fault tolerance
- 物理原因：physical position
- 安全：isolated system



### Labs

1. MapReduce
2. Raft for fault tolerance
3. K/V server
4. Sharded K/V service



### 分布式架构Infrastructure

我们希望将这些分布式操作，抽象成简单接口，让使用者意识不到背后复杂的parallelism

- Storage
- Communications
- Computation

Impl.

- RPC, threads, concurrency control



# Topics



### Performance

 Scalability：2x computer gets 2x throughput(吞吐量)



### **Fault Tolerance**

large scale make rare problem into constant problem, failures occur. （机器可能出问题，网络可能出问题）



Availability：continue provide undamaged service even if failures occur

Recoverbility：weaker acquire than availablity, measure the ability that system repair themselves from failure

常见方案：Non-volatile storage & Replication stored in different area



### Consistency

Performance和Fault Tol.都让我们有更多copy of data（cache，replica...）

假设我们有两个分片，但是在一个replica上的put后，再向另一个replica put之前服务挂了了，导致信息不对等



Define Put(k,v) & Get(k) -> v

**Strong** gurantee read latest value, very expensive communications between replicas

**Weak** allow old value to be read





# MapReduce

Google为了解决大量计算（如sort整个互联网的URL）而发明的框架，让使用者不需要是分布式专家，也可以执行分布式计算。

http://nil.csail.mit.edu/6.824/2020/papers/mapreduce.pdf

MapReduce是一种分布式计算模型，使用者需要提供两个核心函数：Map and Reduce

MapReduce library is designed to help process very large amounts of data using hundreds or thousands of machines.



**例子：统计文件中每个单词的数量**

one job -> many map tasks -> many reduce tasks

worker1: file1 -> apple,banana -> Map -> {apple:1, banana:1}

worker2: file2 -> banana,peach -> Map -> {banana:1, peach:1}

worker3: Reduce -> banana:2

worker4: Reduce -> apple:1

worker5: Reduce -> peach:1

```java
//每个map task：是将单个文件中的单词统计出来
Map<FileName,File>
    for word w in File
        emit(w,"1") //write output to local disk
//每个reduce task：获得每个单词对应的List of “1”；统计个数，返回len(List)
Word,List<Value>
        emit(len(List)) //write output to global disk
```



**Master**

用来控制流程和安排worker工作，保存任务状态和worker状态

在每个map worker完成的工作后，获悉intermediate KV的大小和地址，交代给reduce worker

**Map**

取得data的split，执行map函数，生成intermediate KV键值对，保存到local machine

**Reduce**

从各个local machine读取intermediate KV键值对，合并key相同的数据，输出到global file



![Execution Overview](https://ladychili.top/CS5052-1-MapReduce/overview.png)



### FaultTolerance

**Worker Failure**

Master会定期ping worker，如果挂掉，则任务状态恢复idle，重新schedule

map worker挂了，需要从头执行，因为intermediate KV保存在它的local disk；而reduce worker挂了，就让别的worker继续它的工作就行，因为output已经输出到global file了



**Master Failure**

因为只有一个master，它挂掉的概率很低，所以如果master挂了，程序直接终止，可以断retry MapReduce operation



### Google

google 2004年的时候收到network bottleneck

Map操作是在GFS local进行的，存储intermediate KV也是在本地，而reduce worker需要从各个GFS收集data，所以这里是最耗时的。

reduce output to GFS server, GFS need to at least write one copy to another GFS, 同样是非常耗时的。

2020年，现代网络不再是问题，就可以安心的利用网络进行Map的读取工作了。
