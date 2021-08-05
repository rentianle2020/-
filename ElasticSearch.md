# ElasticSearch

### RESTful

ES大量使用了RESTful风格的API

REST：表现层状态转化（Representational State Transfer），一种设计原则、设计约束、设计思路...

RESTful：是一种软件架构风格；如果一个架构设计遵循REST设计原则，我们称之为RESTful架构



**资源的表现层状态转化**

资源：网络上的一个一切事务，每一个资源存在一个唯一的资源标识符 URI

表现层：我们把“资源”具体呈现出来的形式

状态转化：客户端通过操作服务器中资源，使资源发生某种状态转变



**原则**

1. 使用REST的URI替换传统URI

   传统：http://localhost:8080/user/findOne?id=21

   REST：http://localhost:8080/user/findOne/21/

2. 四种请求动词对应服务端四种操作（CRUD）

   不让URI耦合请求操作，比如/user/find就对应查询，直接/user+GET方式访问就是查询

   转而使用，GET（查询）、POST（更新|添加）、PUT（添加|更新）、DELETE（删除）



### 全文检索

扫描文章中的每一个词，对每一个词建立索引，指明出现次数和位置。用户查询时，根据索引查找。

全面、准确、快速使衡量全文检索系统的关键指标。

1. 只处理文本，不处理语义
2. 搜索时英文不区分大小写
3. 结果列表按照相关度排序



### ElasticSearch简介

Lucene是迄今为止性能最好的一款开源搜索引擎工具包，但是API复杂，需要身后的搜索理论。

ES是基于Apache Lucene构建的开源搜索引擎；ES采用java语言编写，提供简单易用的RESTful API，避免乐Lucene的复杂性。



**应用场景**

ES主要以轻量级JSON作为数据存储格式。

支持地理位置查询，地理位置和文本混合查询，以及统计、日志类数据的存储、分析、可视化。

我们使用JAVA，主要做的是站内检索，而不像百度这样做站外检索。



**安装&运行**

ES7需求JDK11，ES6则可以使用JDK8

运行时可能被直接kill，因为ES运行在JVM上，堆划分过多导致内存不够；需要将config包中的jdk.options的-Xmx1g改为-Xmx512m

其次，elasticseaerch必须运行在非root用户上，否则报错：java.lang.RuntimeException: can not run elasticsearch as root

前台启动成功后，开启新的窗口来操作ES

![image-20210803145436439](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210803145436439.png)

web端口：9200（可以curl localhost:9200测试访问一下）

tcp端口：9300

和其他服务器单节点启动不同，ES默认以集群方式启动



开启远程连接，在配置文件中解除这行注释；一旦开启，默认从开发模式，变到

![image-20210803151215381](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210803151215381.png)	



### ES中的基本概念



**接近实时（NRT Near Real Time）**

Elasticsearch是一个接近实时的搜索平台；从索引一个文档，到这个文档能够被搜索到，有一个1秒内轻微的延迟



**索引（Index）**

索引由一个名字来表示，必须全部是小写字母；~~类似关系型数据库中Database的概念~~

相似文档的集合



**类型（Type）**

~~在一个索引中，可以定义一种或多种类型。类似关系型数据库中Table的概念。~~

ES 6.X或更高版本中，创建的索引就只能包含单个映射类型了！

```apl
Why are mapping types being removed?
Initially, we spoke about an “index” being similar to a “database” in an SQL database, and a “type” being equivalent to a “table”.

This was a bad analogy that led to incorrect assumptions. In an SQL database, tables are independent of each other. The columns in one table have no bearing on columns with the same name in another table. This is not the case for fields in a mapping type.

In an Elasticsearch index, fields that have the same name in different mapping types are backed by the same Lucene field internally. In other words, using the example above, the user_name field in the user type is stored in exactly the same field as the user_name field in the tweet type, and both user_name fields must have the same mapping (definition) in both types.

This can lead to frustration when, for example, you want deleted to be a date field in one type and a boolean field in another type in the same index.

On top of that, storing different entities that have few or no fields in common in the same index leads to sparse data and interferes with Lucene’s ability to compress documents efficiently.

For these reasons, we have decided to remove the concept of mapping types from Elasticsearch.
```



**映射（Mapping）**

类似关系型数据库中table的schema（约束），用于定义一个索引（index）中的类型（type）的数据结构。



**文档（Document）**

一个文档是一个可被索引的基本数据单元，类似关系型数据库中的一条数据



![image-20210803162350983](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210803162350983.png)



# Kibana

针对Elasticsearch的开源分析可视化平台，使用Kibana和ES索引的数据进行交互

修改kibana.yml配置文件

server.host：“0.0.0.0”

elasticsearch.hosts：“http://localhost:9200”

Kibana启动起来之后，默认监听5601端口，远程访问即可



### **索引操作**

**创建索引**

```apl
#6.x版本会生成5个备份索引，而7.x版本只会生成1个；也可以手动设置
#只能使用小写字母和数字
PUT index
{
	"settings":{
		"number_of_replicas":1,
		"number_of_shards":1
	}
}
#返回这个，即索引创建成功！
#! Deprecation: the default number of shards will change from [5] to [1] in 7.0.0; if you wish to continue using the default of [5] shards, you must manage this on the create index request or with an index template

{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "search"
} 
```

**查看索引**

```apl
#v表示显示表头
GET _cat/indices?v

#索引在集群中存储，索引状态为green代表健壮，yellow代表不健壮，red代表不完整/不可用
health status index     uuid                   pri rep docs.count docs.deleted store.size pri.store.size
yellow open   search    UCYE0ewlRKG3bZNTfljQGA   5   1          0            0      1.1kb          1.1kb
green  open   .kibana_1 sykIOtn7TZObhLqOIsKAgA   1   0          2            0      8.6kb          8.6kb

```

**删除索引**

```apl
#kibana索引不要随便删，否则服务无法正常运行
#索引用不到更新，如果建立错误，删除重建即可
DELETE index
DELETE *
```



### **类型操作**

**创建索引+类型+映射**

```apl
#同时创建索引和类型的映射，此种方法创建类型要求索引不能存在
PUT index
{
	#其实不用mappings了，就一个映射，属于历史遗留问题
	"mappings":{
		#在index索引下建立emp类型
		"book":{
			#属性，固定写死
			"properties":{
				#数据类型的映射,这个id字段没必要了，默认有一个_id
				"id":{"type":"keyword"},
				"name":{"type":"keyword"},
				"price":{"type":"double"},
				"des":{"type":"text"},
				"pubdate":{"type":"date"}
			}
		}
	}
}
```

**查询类型**

```apl
#查询索引+类型的映射
GET index
#单独获取类型的映射
GET index/_mapping
```



### **文档操作（重点）**

**插入一条文档**

```apl
#id是自动生成的，但也可以手动输入
PUT index/book/1
{
	"name":"goodbook",
	"price":100,
	"des":"this is a good book",
	"pubdate":"1998-10-28"
}
#不指定id，自动生成，使用POST
POST index/book
{
	...
}
```

**根据id删除一条文档**

```apl
DELETE index/book/1
```

**更新文档**

```apl
#这种方式不保留原始数据（先删除后插入）
POST index/book/1
{
	"name":"badbook"
}

#在原始数据之上更新
POST index/book/1/_update
{
	#这个doc是固定写死的,更新原有数据，甚至可以在保留的基础上添加新字段
	"doc":{
		"name":"luckybook",
		"des":"this is a lucky book",
		"extra":"test"
	}
}
```

**文档批量操作**

```apl
#_bulk 添加(index) 删除(delete) 更新(update);每个单独执行，并不是原子操作，ES几乎无事务
PUT index/book/_bulk
#不标明_id则自动生成
{"index":{"_id":"3"}}
	{"name":"book1","price":500}
{"delete":{"_id":"2"}}
{"update":{"_id":"1"}}
	{"doc":{"name":"updatebook"}}
```



### 检索（Query）

可以通过URL参数或者通过DSL（Domain Specified Language）

DSL通过将JSON写入请求体的方式进行搜索，官方建议使用！更强大，更简洁！



语法：

- URL：GET /索引/类型/_search?参数
- DSL查询：GET /索引/类型/_search{}



**Query String（URL）**

```apl
#前边这些参数还好，到了_source就可能有无限个字段了，这时URL就有很大的局限性了！
GET index/book/_search?q=*&sort=price:desc&size=5&from=0&_source=name,price...
```



**Query DSL**

```apl
#查询所有
GET index/book/_search
{
	"query":{
		"match_all":{}
	}
}
```

