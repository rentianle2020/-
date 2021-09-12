# ElasticSearch

开源的full-text搜索分析引擎

实现快速&接近实时的存储、搜索、分析大量数据



**使用场景**

- 商城的商品搜索
- 日志or大量数据的分析



**基本概念**

Near Realtime（NRT）：接近实时，代表从index a document到它searchable还是有一点延迟

Cluster：集群，名字唯一；多个节点集合在一起=服务器整个数据，默认名字为“elasticsearch”

Node：节点，名字为随机UUID；单个节点创建时，默认创建elasticsearch集群

Index：具有相似特性文档的集合，小写字母命名

Type：6.0.0被废弃，本来是Index内的一个细分；为了向下兼容，现在不用指定type，默认为_doc

Document：信息被存储在Index中的基本单元，JSON格式对象，每个Index中的Document数量可以有无限个

Shards：单个Index可能会非常的大，Sharding将Index中的数据水平拆分，分布到多个节点上并行操作，提高性能/吞吐量；ES管理Shard的分布和Documents的聚合，对用户完全透明

Replicas：网络环境随时可能崩溃，Replication用来实现高可用的服务，而复制分片永远不会和主分片放在同一个节点上；复制分片也可以提供完整的搜索功能，从而提高服务的搜索量/吞吐量



To summarize, each index can be split into multiple shards. An index can also be replicated zero (meaning no replicas) or more times. Once replicated, each index will have primary shards (the original shards that were replicated from) and replica shards (the copies of the primary shards). The number of shards and replicas can be defined per index at the time the index is created. After the index is created, you may change the number of replicas dynamically anytime but you cannot change the number of shards after-the-fact.

By default, each index in Elasticsearch is allocated 5 primary shards and 1 replica which means that if you have at least two nodes in your cluster, your index will have 5 primary shards and another 5 replica shards (1 complete replica) for a total of 10 shards per index.



## Docker

https://www.elastic.co/guide/en/elasticsearch/reference/7.5/docker.html

```sh
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms64m -Xmx128m" -d elasticsearch:7.5.2
```



## 底层原理

ES2.0中文文档：https://www.elastic.co/guide/cn/elasticsearch/guide/current/routing-value.html

In very simple terms, an inverted index is a mapping of each unique ‘word’ (token) to the list of documents (locations) containing that word, which makes it possible to locate documents with given keywords very quickly.Index information is stored in one or multiple partitions also called shards. Elasticsearch is able to distribute and allocate shards dynamically to the nodes in a cluster, as well as replicate them.

Index operations use primary shards and search queries use both shard types. Having multiple nodes and replicas increases query performance.



**集群本质**

集群其实就是将一个索引Index，分成多个分片shards和副本replicas，存在不同的节点nodes

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210817215530.png" alt="1_3FuRYIkmr1rzwjiDL7Iykg" style="zoom: 25%;" />	

**新建文档、索引、删除文档**

新文档存储时，收到请求的协调节点根据文档ID得出对应的主分片值，并存储过去（整体文档和倒排索引）

> shard = hash(routing) % number_of_primary_shards

存储成功后，将请求并行性的转发到副本，如果全部执行成功，则由协调节点向客户端报告成功

**搜索Query**

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210818121722.png" alt="1462195906-339a9f1df14728ef" style="zoom: 50%;" />	

> 当我们在 Lucene 中索引一个文档时，每个字段的值都被添加到相关字段的倒排索引中。（一个字段对应一个倒排索引）
>
> 搜索时，分词后通过该字段的Trie 树（前缀树），定位倒排索引下标，得到一系列文档ID

搜索时，收到请求的协调节点创建一个优先队列，将搜索条件广播到每一个分片（主或副分片皆可，比如主0和副1）

每个分片通过倒排索引，查询当前分片中匹配的文档ID和排序值，并放入本地的优先队列

协调节点收集所有优先队列的值，产生一个全局排序后的结果列表，合并到最终优先队列中

**搜索Fetch**

根据最终优先队列中的文档ID和顺序，向文档所在的分片发起多个GET请求，收集并返回客户端



## 集群（待深入）

**集群发现**

使用配置文件中的discovery.seed_hosts变量，使用具体IP或者域名都可以；每个节点需要发现别的节点

```yaml
discovery.seed_hosts:
   - 192.168.1.10:9300
   - 192.168.1.11 
   - seeds.mydomain.com 
```

通过Java操作集群：配置类中，创建连接时把所有的host和port都加进去即可，其他的操作不变

**仲裁决定**

在集群启动或者主节点宕机后，开始竞选新的主节点。

**集群状态**

```
GET /_cat/health?v
GET /_cat/nodes?v
```



## 映射 Mapping 

Mapping is the process of defining how a document, and the fields it contains, are stored and indexed.



**Meta-Fields 元字段**

一个文档除了有数据之外，它还包含了元数据(Metadata)

_index	_type	_id

_source：The original JSON representing the body of the document



**FieldType 字段类型**

a simple type like text, keyword, date, long, double, boolean or ip.

text：该类型字段被索引前，通过analyzer被分解成单个单词。用来全文搜索，不适合做排序和聚合

> 聚合：总结全套的数据而不是寻找单个文档，比如所有人的平均年龄

keyword：用于索引结构化内容（例如电子邮件地址、主机名、状态代码、邮政编码或标签）的字段；主要用来做过滤，排序，聚合；只能用完整值来查询，不会通过分词器

date：在不指定具体format的情况下，可以存储时间戳或者有格式的日期`"2015-01-01"` or `"2015/01/01 12:10:30"`

long、integer、double、float、boolean、ip



**Dynamic Mapping 动态配置**

当我们需要添加一个Document，不需要先创建Index和Field并指定FieldType

可以直接PUT，然后Index，Field，FieldType都会自动生成

```console
PUT data/counters/1 
{ "count": 5 }
```



## 分数 Score

The score is a numeric value that is a relative measure of how well the document matches the search query that we specified. The higher the score, the more relevant the document is, the lower the score, the less relevant the document is.

But queries do not always need to produce scores, in particular when they are only used for "filtering" the document set.



影响分数（相关度）的因素

- 检索词在字段中出现的频率，频率越高＝相关度越高
- 检索词在索引中出现的频率，频率越高＝相关度越低（物以稀为贵）
- 字段长度，字段越长＝相关度越低



## 检索

**Query context 查询上下文**

*How well does this document match this query clause?*

默认通过score关联程度排序



**Filter context 过滤上下文**

*Does this document match this query clause?*

使用filter参数，让普通的Query context变成Filter context

结果仅仅回答是否符合过滤，不会计算score

过滤器创建bitset，使用1来标识包含的文档，常用的filter的bitset会被缓存起来，提高性能



**精确查询 Term-level queries**

不会对搜索词进行此法分析，而是直接整个带着去倒排索引中匹配

EX.如果我们存储包含"brown dog"值的text，倒排索引中存在"brown"和"dog"，当我们直接term搜索”brown dog“的时候，什么也搜不到。这是因为term搜索不会将我们的搜索分词，而是直接去倒排索引中完全匹配。

所以这种查找，通常和filter结合，用单个条件来筛选文档



term查找：根据指定字段的精确值，不需要评分

fuzzy模糊查询：搜索包含与搜索词相似词的文档

ids、wildcard、prefix、range、exists



**全文查询 Full text queries**

如果Index存在text的fieldType，文档在存入和搜索的时候，都会经过Text analyzer

Tokenization将全文分别成词汇放到倒排索引、Normalization进行大小写和复数的转换



match，match_all

multi_match：一个查询条件，应用在索引的多个字段field中



**组合查找**

多个查询条件，应用在同一个字段field中

bool --> (must，should，must_not，filter)

- must：必须出现，且积分
- should：出现的加分
- must_not：必须不能出现
- filter：和must不同的是，过滤出来的所有结果不计分



## IK分词器

使用ES插件自动安装IK分词器（注意ES6.8.0要对应6.8.0的分词器）

./elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.5.2/elasticsearch-analysis-ik-7.5.2.zip



两种分词器

- ik_smart：最粗粒度拆分，“中华人民共和国国歌“拆分为“中华人民共和国”和“国歌”
- ik_max_word：最细粒度拆分，中华人民共和国国歌拆分为“中华人民共和国、中华人民、中华、华人、人民共和国、人民、人、民、共和国、共和、和、国国、国歌”

```apl
GET /_analyze
{
	"text":"中华人民共和国国歌",
	"anaylzer":"ik_smart"
}
```



**本地扩展词典**

IK自带分词：config/analysis-ik/main.dic

IK扩展：IKAnalyzer.cfg.xml，新建一个xxx.dic扩展词典，然后将其输入到扩展词典key的value中；扩展词只能对后续添加的文档生效，无法对原始文档生效；如果想要影响原始文档，需要清空data文件夹内容，重新输入原始文档



**远程扩展词典**

将搜索词汇添加到redis，然后每次+1；设置定时任务，将每天搜索最多的词远程添加到ES

IK扩展：IKAnalyzer.cfg.xml，将一个url输入到远程扩展字典的<>中，该地址返回一个txt文件就行



## **集群可视化**

集群可视化web界面：https://github.com/mobz/elasticsearch-head

在设置中配置这两个属性，即可使用head远程访问

http.cors.enabled: true

http.cors.allow-origin: "*"



# Kibana

https://www.elastic.co/guide/en/kibana/current/docker.html

针对Elasticsearch的开源分析可视化平台，使用Kibana和ES索引的数据进行交互

Kibana启动起来之后，默认监听5601端口，远程访问即可

```sh
docker run --name kibana -p 5601:5601 -e "ELASTICSEARCH_HOSTS=http://xxx:9200" -d kibana:7.5.2
```



## **Query DSL**

使用Springboot操作ES的基本结构也是按照DSL进行链式编程的！

```apl
#查询所有
GET index/book/_search
{
	"query":{
		"match_all":{}
	}
}
#查询所有并排序+分页
GET index/book/_search
{
  "query":{
    "match_all":{}
  },
  "size":"1",
  "from":"0",
  "sort":[
    {
      "price":{
        "order":"desc"
      }
    }]
}
#返回指定字段 _source
GET index/book/_search
{
  "query":{
    "match_all":{}
  },
  "_source":["name","price"]
}
#基于关键词进行查询 term
GET /index/book/_search
{
	"query":{
		"term":{
			"des":{
				"value":"good"
			}
		}
	}
}
#范围查询
GET /index/book/_search
{
  "query": {
    "range": {
      "price": {
        "gte": 10,
        "lte": 100
      }
    }
  }
}
#前缀查询
GET index/book/_search
{
  "query": {
    "prefix": {
      "name": {
        "value": "这"
      }
    }
  }
}
#通配符查询，#占1个，*占多个
GET index/book/_search
{
  "query": {
    "wildcard": {
      "name": {
        "value": "这*" 
      }
    }
  }
}
#多个id查询 ids查询
GET index/book/_search
{
  "query": {
    "ids": {
      "values": ["1","2","3","4"]
    }
  }
}
#fuzzy模糊查询 boak --> book，默认允许0-2个错误；
GET index/book/_search
{
  "query": {
    "fuzzy": {
      "des": "boak"
    }
  }
}
#布尔查询 bool --> must should must_not
GET /index/book/_search
{
  "query": {
    "bool": {
      "must_not": [
        {
          "range": {
            "price": {
              "gte": "100",
              "lte":"1000"
            }
          }
        }
      ],
      "should": [
        {
          "wildcard": {
            "name": {
              "value": "book*"
            }
          }
        }
      ]
    }
  }
}
#高亮查询
GET /index/book/_search
{
  "query": {
    "term": {
      "des": {
        "value": "this"
      }
    }
  }
  , "highlight": {
    "fields": {
      "des": {}
    }
  }
}
#单独返回一个高亮结果，也可以自定义pre_tags和post_tags，将<em>替换掉，改成其他html标签
"highlight" : {
          "des" : [
            "<em>this</em> is a good book"
          ]
        }
#多字段查询 multi_match,如果查的field是分词的映射，就先query分词，再查询；存在一个就返回
GET /index/book/_search
{
  "query": {
    "multi_match": {
      "query": "book",
      "fields": ["name","des"]
    }
  }
}
#多字段分词查询 query_string,和多字段查询类似，就多一个分词器
GET /index/book/_search
{
  "query": {
    "query_string": {
      "query": "book书",
      "fields": ["name","des"],
      "analyzer": ""
    }
  }
}
```

## **Filter Query**

```apl
GET test/article/_search
{
  "query": {
    "bool": {
      "must": {
        "term":{
          "content":"book"
        }
      },
      "filter": {
        "term": {
          "content": "数码宝贝"
        }
      }
    }
  }
}
```

