MySQL是怎样运行的

学习方法：

1. 先阅读一遍所有的，尽力理解
2. 使用实践+笔记的方法，吸收学习



### 2、重新认识MySQL

连接管理、解析与优化、存储引擎



**连接管理**

客户端进程可以采TCP/IP来与服务器进程建立连接，每当有一个客户端进程连接到服务器进程时，服务器进程都会创建一个线程来专门处理与这个客户端的交互。

客户端发送请求（SQL语句）到达服务器，服务器处理请求并返回数据

当该客户端退出时会与服务器断开连接，服务器并不会立即把与该客户端交互的线程销毁掉，而是把它缓存起来，在另一个新的客户端再进行连接时，把这个缓存的线程分配给该新客户端。



**解析与优化**

MySQL服务器收到请求后，需要解析SQL语句并对其进行优化。



**存储引擎**

优化完语句，MySQL将数据的存储和提取操作，都封装到`存储引擎`模块，怎么从表中读取数据，怎么把数据写入具体的物理存储器上，这都是`存储引擎`负责的事情。

为了管理方便，人们把`连接管理`、`语法解析`、`查询优化`这些并不涉及真实数据存储的功能划分为`MySQL server`的功能，把真实存取数据的功能划分为`存储引擎`的功能。



**常用存储引擎**

不同`存储引擎`管理的表具体的存储结构可能不同，采用的存取算法也可能不同。

我们最常用的就是`InnoDB`和`MyISAM`，有时会提一下`Memory`。其中`InnoDB`是`MySQL`默认的存储引擎。

| `InnoDB` | 具备外键支持功能的事务存储引擎 |
| -------- | ------------------------------ |
| `MEMORY` | 置于内存的表                   |
| `MyISAM` | 主要的非事务处理存储引擎       |



### 3、启动选项和系统变量

通过mysqld的方式启动服务器，会读取配置文件中的[mysqld]、[server]

可以通过命令行/更改配置文件，更改启动项

> 另外有一点需要特别注意，如果同一个启动选项既出现在命令行中，又出现在配置文件中，那么以命令行中的启动选项为准！



查看当前系统变量

```sql
SHOW VARIABLES [LIKE 匹配的模式];
```



### 4、字符集和比较规则

字符集：字符和二进制的映射规则

```
'a' -> 00000001 (十六进制：0x01)
'b' -> 00000010 (十六进制：0x02)
'A' -> 00000011 (十六进制：0x03)
'B' -> 00000100 (十六进制：0x04)
```

比较规则：比方说字符`'a'`的编码为`0x01`，字符`'b'`的编码为`0x02`，所以`'a'`小于`'b'`，这种简单的比较规则也可以被称为二进制比较规则，英文名为`binary collation`。



**MySQL中支持的字符集**

在`MySQL`中`utf8`是`utf8mb3`的别名，所以之后在`MySQL`中提到`utf8`就意味着使用1~3个字节来表示一个字符，如果大家有使用4字节编码一个字符的情况，比如存储一些emoji表情啥的，那请使用`utf8mb4`。

- `utf8mb3`：阉割过的`utf8`字符集，只使用1～3个字节表示字符。
- `utf8mb4`：正宗的`utf8`字符集，使用1～4个字节表示字符。



**字符集和比较规则的应用**

`MySQL`有4个级别的字符集和比较规则，分别是：

- 服务器级别
- 数据库级别
- 表级别
- 列级别



如果创建或修改列时没有显式的指定字符集和比较规则，则该列默认用表的字符集和比较规则；向上以此类推



知道各个列的字符集和比较规则是什么，就可以确定实际数据占用的存储空间大小了

```mysql
mysql> INSERT INTO t(col) VALUES('我我');
Query OK, 1 row affected (0.00 sec)

mysql> SELECT * FROM t;
+--------+
| s      |
+--------+
| 我我   |
+--------+
1 row in set (0.00 sec)
```

首先列`col`使用的字符集是`gbk`，一个字符`'我'`在`gbk`中的编码为`0xCED2`，占用两个字节，两个字符的实际数据就占用4个字节。如果把该列的字符集修改为`utf8`的话，这两个字符就实际占用6个字节！



**客户端和服务器通信中的字符集**

| 系统变量                   | 描述                                                         |
| -------------------------- | ------------------------------------------------------------ |
| `character_set_client`     | 服务器解码请求时使用的字符集                                 |
| `character_set_connection` | 服务器处理请求时会把请求字符串从`character_set_client`转为`character_set_connection` |
| `character_set_results`    | 服务器向客户端返回数据时使用的字符集                         |



如果我们存储是按照utf-8存储的“我我”

windows操作系统默认使用gbk

```sql
SELECT * FROM t WHERE s = '我';
```

服务器收到，character_set_client = gbk；通过gbk解码搜索条件（0xCED2），得到‘我‘

character_set_connection = utf-8；通过utf-8对’我‘进行编码（0xE68891），然后到数据库中进行查询

查询匹配到一条记录

character_set_results = gbk；通过gbk将结果集编码，发送给客户端



### 5、InnoDB记录结构

`InnoDB`是一个将表中的数据存储到磁盘上的存储引擎，所以即使关机后重启我们的数据还是存在的。

页是`MySQL`中磁盘和内存交互的基本单位，也是`MySQL`是管理存储空间的基本单位。



`InnoDB`目前定义了4种行格式：了解Compact行格式后，其他三种都大同小异

![image_1c9g4t114n0j1gkro2r1h8h1d1t16.png-42.4kB](https://user-gold-cdn.xitu.io/2019/3/12/169710e8fafc21aa?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



### 6、InnoDB数据页结构

![image_1cosvi1in9st476cdqfki1n39m.png-133.8kB](https://user-gold-cdn.xitu.io/2019/5/8/16a95c0fe86555ed?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

在页的7个组成部分中，我们存储的记录会按照我们指定的`行格式`存储到`User Records`部分。



**User Records**

![image_1cul8slbp1om0p31b3u1be11gco9.png-119.6kB](https://user-gold-cdn.xitu.io/2019/5/8/16a95c108ee1da43?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)不论我们怎么对页中的记录做增删改操作，InnoDB始终会维护一条记录的单链表，链表中的各个节点是按照主键值由小到大的顺序连接起来的。



**Page Directory（页目录）**

页目录中包含乐一个槽数组，每个槽对应一组数据；增删改查时，可以使用二分法快速定位到一个分组，然后通过`next_record`属性遍历组中的记录，找到对应的。

![image_1d6g64af2sgj1816ktl1q22dehp.png-189.1kB](https://user-gold-cdn.xitu.io/2019/5/8/16a95c10e3449897?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



**Page Header（页面头部）**

存储数据页的状态信息，比如本页中已经存储了多少条记录，第一条记录的地址是什么，页目录中存储了多少个槽等等



**File Header（文件头部）**

针对各种类型的页都通用，也就是说不同类型的页都会以`File Header`作为第一个组成部分，它描述了一些针对各种页都通用的一些信息，比方说这个页的编号是多少，它的上一个页、下一个页是谁



**File Trailer**

更改数据时，校验和会被首先同步到磁盘；当完全写完时，校验和也会被写到页的尾部，如果完全同步成功，则页的首部和尾部的校验和应该是一致的。



> **总结**
>
> 前边我们详细唠叨了`InnoDB`数据页的7个组成部分，知道了各个数据页可以组成一个`双向链表`，而每个数据页中的记录会按照主键值从小到大的顺序组成一个`单向链表`，每个数据页都会为存储在它里边儿的记录生成一个`页目录`，在通过主键查找某条记录的时候可以在`页目录`中使用二分法快速定位到对应的槽，然后再遍历该槽对应分组中的记录即可快速找到指定的记录



### 7、B+树索引



**聚簇索引**

1. 使用记录主键值的大小进行记录和页的排序
2. `B+`树的叶子节点存储的是完整的用户记录

具有这两种特性的`B+`树称为`聚簇索引`，InnoDB存储引擎会自动帮我们创建

索引即数据，数据即索引。

![image_1cacafpso19vpkik1j5rtrd17cm3a.png-158.1kB](https://user-gold-cdn.xitu.io/2019/4/9/16a01bd2a6c7a65f?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



**二级索引**

保存索引列的值和主键值；锁定指定数据行后，通过主键值进行回表查询（回到聚簇索引）

<img src="https://user-gold-cdn.xitu.io/2019/4/9/16a01bd2c92fbca0?imageView2/0/w/1280/h/960/format/webp/ignore-error/1" alt="image_1cpb919suginpp7lbgsk0147f20.png-58.6kB" style="zoom:50%;" />	



> 总结：
>
> - 每个索引都对应一棵`B+`树，`B+`树分为好多层，最下边一层是叶子节点，其余的是内节点。所有`用户记录`都存储在`B+`树的叶子节点，所有`目录项记录`都存储在内节点。
> - `InnoDB`存储引擎会自动为主键（如果没有它会自动帮我们添加）建立`聚簇索引`，聚簇索引的叶子节点包含完整的用户记录。
> - 我们可以为自己感兴趣的列建立`二级索引`，`二级索引`的叶子节点包含的用户记录由`索引列 + 主键`组成，所以如果想通过`二级索引`来查找完整的用户记录的话，需要通过`回表`操作，也就是在通过`二级索引`找到主键值之后再到`聚簇索引`中查找完整的用户记录。
> - `B+`树中每层节点都是按照索引列值从小到大的顺序排序而组成了双向链表，而且每个页内的记录（不论是用户记录还是目录项记录）都是按照索引列的值从小到大的顺序而形成了一个单链表。如果是`联合索引`的话，则页面和记录先按照`联合索引`前边的列排序，如果该列值相同，再按照`联合索引`后边的列排序。
> - 通过索引查找记录是从`B+`树的根节点开始，一层一层向下搜索。由于每个页面都按照索引列的值建立了`Page Directory`（页目录），所以在这些页面中的查找非常快。



### 8、B+树索引的使用



**索引的代价**

- 空间：每个索引都对应一棵B+树，每个节点为1页，每个页16KB
- 时间：增删改操作时对B+树的维护



```sql
CREATE TABLE person_info(
    id INT NOT NULL auto_increment,
    name VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL,
    phone_number CHAR(11) NOT NULL,
    country varchar(100) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_name_birthday_phone_number (name, birthday, phone_number)
);
```



**B+树索引适用的条件**

1. 全值匹配

   搜索条件中的列和索引列一致。

   ```sql
   SELECT * FROM person_info WHERE name = 'Ashburn' AND birthday = '1990-09-27' AND phone_number = '15123983239';
   ```

   这种情况下，where子句中的搜索顺序不影响索引的适用（查询优化器的作用）

2. 匹配左边的列

   搜索语句中也可以不用包含全部联合索引中的列，只包含左边的就行

   因为`B+`树的数据页和记录先是按照最左列（name）的值排序的

   ```sql
   SELECT * FROM person_info WHERE name = 'Ashburn' AND birthday = '1990-09-27';
   ```

3. 匹配列前缀

   对于字符串类型的索引列来说，我们只匹配它的前缀也是可以快速定位记录

   ```sql
   SELECT * FROM person_info WHERE name LIKE 'As%';
   ```

   但是需要注意的是，如果只给出后缀或者中间的某个字符串，比如这样：

   ```sql
   SELECT * FROM person_info WHERE name LIKE '%As%';
   ```

   `MySQL`就无法快速定位记录位置了

4. 匹配范围值

   ```sql
   SELECT * FROM person_info WHERE name > 'Asa' AND name < 'Barlow';
   ```

   在使用联合进行范围查找的时候需要注意，如果对多个列同时进行范围查找的话，只有对索引最左边的那个列进行范围查找的时候才能用到`B+`树索引

5. 精确匹配某一列并范围匹配另外一列

   ```sql
   SELECT * FROM person_info WHERE name = 'Ashburn' AND birthday > '1980-01-01' AND birthday < '2000-12-31' AND phone_number > '15100000000';
   ```

   此处name和birthday都可以使用B+树索引来确定，而phone_number只能通过遍历上一步得到的结果而进一步过滤乐

6. 用于排序

   一般情况下，我们只能把记录都加载到内存中，再用一些排序算法，比如快速排序、归并排序

   而如果我们order by后面的字段为索引值，则直接可以从B+树上取值，取出的就是顺序的，再回表即可

   对于`联合索引`有个问题需要注意，`ORDER BY`的子句后边的列的顺序也必须按照索引列的顺序给出，如果给出`ORDER BY phone_number, birthday, name`的顺序，那也是用不了`B+`树索引

7. 用于分组group by



**回表的代价**

```sql
SELECT * FROM person_info WHERE name > 'Asa' AND name < 'Barlow';
```

由于索引`idx_name_birthday_phone_number`对应的`B+`树中的记录首先会按照`name`列的值进行排序，所以值在`Asa`～`Barlow`之间的记录在磁盘中的存储是相连的，集中分布在一个或几个数据页中，我们可以很快的把这些连着的记录从磁盘中读出来，这种读取方式我们也可以称为`顺序I/O`。

根据第1步中获取到的记录的`id`字段的值可能并不相连，而在聚簇索引中记录是根据`id`（也就是主键）的顺序排列的，所以根据这些并不连续的`id`值到聚簇索引中访问完整的用户记录可能分布在不同的数据页中，这样读取完整的用户记录可能要访问更多的数据页，这种读取方式我们也可以称为`随机I/O`。

一般情况下，顺序I/O比随机I/O的性能高很多！



那什么时候采用全表扫描的方式，什么时候使用采用`二级索引 + 回表`的方式去执行查询呢？这个就是传说中的查询优化器做的工作，查询优化器会事先对表中的记录计算一些统计数据，然后再利用这些统计数据根据查询的条件来计算一下需要回表的记录数，需要回表的记录数越多，就越倾向于使用全表扫描，反之倾向于使用`二级索引 + 回表`的方式。



**覆盖索引**

```sql
SELECT name, birthday, phone_number FROM person_info WHERE name > 'Asa' AND name < 'Barlow'
```

直接通过`idx_name_birthday_phone_number`索引得到结果，不必回表

我们把这种只需要用到索引的查询方式称为`索引覆盖`。



**使用索引时的注意事项**

1. 只为用于搜索、排序或分组的列创建索引
2. 为列的基数大的列创建索引（列的基数 = 某一列不重复数据的个数）
3. 索引列的类型尽量小（索引占用空间小，CPU操作快）
4. 可以只对字符串值的前缀建立索引
5. 只有索引列在比较表达式中单独出现才可以适用索引
6. 为了尽可能少的让`聚簇索引`发生页面分裂和记录移位的情况，建议让主键拥有`AUTO_INCREMENT`属性。
7. 定位并删除表中的重复和冗余索引
8. 尽量使用`覆盖索引`进行查询，避免`回表`带来的性能损耗。（和1相反，需要权衡）



### 9、MySQL的数据目录

`数据目录`是用来存储`MySQL`在运行过程中产生的数据，一定要和`安装目录`区别开！

InnoDB将表和数据存储到磁盘，而操作系统通过`文件系统`管理磁盘。所以InnoDB引擎的读写就是在文件系统上的读写。



**表在文件系统中的表示**

1. 表结构的定义：表名.frm（保存视图时，只需存储一个 视图名.frm 即可）
2. 表中的数据：保存在表空间（一个抽象概念，用来管理页）



系统表空间：在一个MySQL服务器中，系统表空间只有一份。从MySQL5.5.7到MySQL5.6.6之间的各个版本中，我们表中的数据都会被默认存储到这个 ***系统表空间***。

独立表空间：表名.ibd，用来储存表中的索引和数据

还有各种其他类型的表空间，后边用到再提



**文件系统对数据库的影响**

因为`MySQL`的数据都是存在文件系统中的，就不得不受到文件系统的一些制约

1. 数据库名称和表名称不得超过文件系统所允许的最大长度
2. 特殊字符的问题
3. 文件大小受限于文件系统支持的最大文件大小



### 10、InnoDB的表空间

`表空间`是一个抽象的概念，对于每个独立表空间来说，对应着文件系统中一个名为`表名.ibd`的实际文件。



页 * 64 = 区（物理上连接的64个页）；区 * 256 = 组 

一个B+树索引生成两个段，一个叶子节点段，一个非叶子节点段；段是区的一个集合



区存在的必要性：范围查询，不同的页差的太远，就是随机IO；而连续的页就是，顺序IO



区的分类

| 状态名      | 含义                 |
| ----------- | -------------------- |
| `FREE`      | 空闲的区             |
| `FREE_FRAG` | 有剩余空间的碎片区   |
| `FULL_FRAG` | 没有剩余空间的碎片区 |
| `FSEG`      | 附属于某个段的区     |



![image_1crjo0hl4q8u1dkdofe187b10fa9.png-105.2kB](https://user-gold-cdn.xitu.io/2019/5/1/16a739f33df9307a?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



碎片区只属于表空间，其中的页可以属于段A，也可以属于段B

如果某个段已经占用了32个碎片区空间，就会以完整的区来存储



每一个区都对应着一个`XDES Entry`结构

![image_1crre79uq9971bsdj9s1i0j11en8a.png-96.2kB](https://user-gold-cdn.xitu.io/2019/5/1/16a739f343654829?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



XDES Entry结构，用来管理区，形成链表

每个表空间，都会维护3个XDES Entry链表；分别是free，free_frag，full_frag；这三个是碎片区的链表

每个索引分为两个段，每个段根据Segment ID维护属于该段的3个XDES Entry链表；分别是free，not_full，full；这三个是隶属于段的；当段中数据已经占满了32个零散的页后，就直接申请完整的区来插入数据了



链表基节点：每个链表都对应一个`List Base Node`的结构，这个结构里记录了链表的头、尾节点的位置以及该链表中包含的节点数。



每个段都定义了一个`INODE Entry`结构来记录一下段中的属性

<img src="https://user-gold-cdn.xitu.io/2019/5/1/16a739f4087c4a56?imageView2/0/w/1280/h/960/format/webp/ignore-error/1" alt="image_1crrju0cnji91a2fhv91ijb15hgb1.png-111.4kB" style="zoom: 67%;" />	



# 落地

第一个组的第一个页：256个区的所有XDES Entry的地址

<img src="https://user-gold-cdn.xitu.io/2019/5/1/16a739f4733af475?imageView2/0/w/1280/h/960/format/webp/ignore-error/1" alt="image_1crmfvigk938c8h1hahglr15329.png-146.8kB" style="zoom:67%;" />



File Space Header中，储存表空间的一些整体属性

包括free, free_frag, full_frag等XDES Entry链表的基节点地址

![image_1crrp2qp310rc10fd33ch716hcp.png-148.1kB](https://user-gold-cdn.xitu.io/2019/5/1/16a739f47508ede5?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



第一个组的第三个页

保存85个INODE Entry，其中维护了3个由该段的XDES Entry组成的链表；如果超出85个，就需要多个INODE类型页，List Node for INODE Page List就是用来指向上一个/下一个，INODE类型页的

![img](https://user-gold-cdn.xitu.io/2019/12/11/16ef3a8df380813e?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

<img src="https://user-gold-cdn.xitu.io/2019/5/1/16a739f4087c4a56?imageView2/0/w/1280/h/960/format/webp/ignore-error/1" alt="image_1crrju0cnji91a2fhv91ijb15hgb1.png-111.4kB" style="zoom:50%;" />	





怎么知道哪个索引对应哪个段？还要分叶子段和非叶子段

在每个B+树索引的根页，的page header中，都会定义两个属性；分别指向对应的叶子段和非叶子段的INODE Entry地址；哪个表空间的，哪个页，的哪个偏移量



### 11、单表访问方法



**访问方法**

1. const：通过主键或者唯一二级索引列 等值查询 
2. ref
   - 普通二级索引等值查询，然后回表
   - 联合索引，最左边的连续索引列是与常数的等值比较 
3. ref_or_null：ref 基础上查询null 值 
4. range：任意索引 in()或者范围查找 
5. index：遍历二级索引全部记录
6. all：全表扫描



**明确range访问方法的范围区间**

如果两个range使用同一个索引：合并

如果有的range没有索引/多个range使用不同的索引：使用最优索引的列，然后将没有索引的替换成True，然后将结果集再过滤



**索引合并 index merge**

Intersection：对多个索引求结果集，对主键求`交集`，再回表

> 比求一个索引结果集，回表，再过滤更优！因为索引是顺序IO，而回表是随机IO！当然最终还是看优化器的选择，有的时候就算能用Intersection/Union，优化器也不会选择使用

注意：只有等值查询的时候可以使用（联合索引也不能是部分匹配），只有这样，无论是const还是ref，主键是顺序排列的

当然主键列可以是范围匹配

那么求交集的过程就是这样：逐个取出这两个结果集中最小的主键值，如果两个值相等，则加入最后的交集结果中，否则丢弃当前较小的主键值，再取该丢弃的主键值所在结果集的后一个主键值来比较，直到某个结果集中的主键值用完了

当然，我们可以使用联合索引代替Intersection



Union：对多个索引求结果集，对主键求`并集`，再回表

同上，必须保证各个二级索引列在进行等值匹配的条件下才可能被用到

Union还有一种特殊的Sort-Union使用方式；取出range结果集，然后对主键排序，再求并集



### 12、连接的原理

- 步骤1：选取驱动表，使用与驱动表相关的过滤条件，选取代价最低的单表访问方法来执行对驱动表的单表查询。
- 步骤2：对上一步骤中查询驱动表得到的结果集中每一条记录，都分别到被驱动表中查找匹配的记录。

驱动表查询1次，被驱动表查询n次



这个`ON`子句是专门为外连接驱动表中的记录在被驱动表找不到匹配记录时应不应该把该记录加入结果集这个场景下提出的，所以如果把`ON`子句放到内连接中，`MySQL`会把它和`WHERE`子句一样对待，也就是说：内连接中的WHERE子句和ON子句是等价的。



join buffer：将查询驱动表得到的结果集，放入join buffer；和被驱动表匹配时，让每一条被驱动表的记录和多个join buffer中的结果匹配，而不是每次循环只匹配一个；从而减少被驱动表IO次数，增加查询效率

> 只有查询列表中的列和过滤条件中的列才会被放到`join buffer`中，所以再次提醒我们，最好不要把`*`作为查询列表，只需要把我们关心的列放到查询列表就好了，这样还可以在`join buffer`中放置更多的记录



### 13、MySQL基于成本的优化

I/O成本：从磁盘到内存这个加载的过程损耗的时间称之为`I/O`成本

CPU成本：读取、检测、排序等操作消耗的时间称为`CPU`成本



in语句中有多个`单点区间`时，通过`index dive`的方式（就是先获取索引对应的`B+`树的`区间最左记录`和`区间最右记录`，然后再计算这两条记录之间有多少记录）来统计成本；而当这个量过多的时候，采用估算的方法（Rows值的总数量/Cardinality不重复值的数量 = 每一个值大概有多少个）

内连接时，优化器通过计算不同表作为驱动表所耗费的总成本，来挑选成本更低的方案。





### 14、InnoDB统计数据是如何收集的

`MySQL`也会为表中的每一个索引维护一份统计数据，查看某个表中索引的统计数据可以使用`SHOW INDEX FROM 表名`的语法



对表和索引的统计数据，保存在了这两个表上：

- innodb_index_stats 索引的统计数据
- innodb_table_stats 表的统计数据



数据是如何收集的？以表为单位收集数据，从各个表空间中获取值，存入统计数据的表中



### 15、MySQL基于规则的优化

这个过程也可以被称作`查询重写`（就是人家觉得你写的语句不好，自己再重写一遍）





**标量子查询、行子查询优化**

不相关就先执行子查询，再到外层一个个匹配；相关就先执行外层查询，然后到子查询中一个个匹配



**IN子查询优化**

如果子查询中是唯一二级索引/簇级索引（不重复），可以直接将in转换为join；除了这种情况，优先考虑semi-join，即先转换为join后再采取策略，将匹配1次以上的值消除

- 如果`IN`子查询符合转换为`semi-join`的条件，查询优化器会优先把该子查询转换为`semi-join`，然后再考虑下边5种执行半连接的策略中哪个成本最低：

  - Table pullout
  - DuplicateWeedout
  - LooseScan
  - Materialization
  - FirstMatch

  选择成本最低的那种执行策略来执行子查询。

- 如果`IN`子查询不符合转换为`semi-join`的条件，那么查询优化器会从下边两种策略中找出一种成本更低的方式执行子查询：

  - 先将子查询物化之后再执行查询

    `物化 Materialize`：基于内存的物化表有哈希索引（一般结果集不是很大的情况，使用Memory存储引擎），基于磁盘的有B+树索引

  - 执行`IN to EXISTS`转换。



### 16 17、Explain详解

设计`MySQL`的大叔贴心的为我们提供了`EXPLAIN`语句来帮助我们**查看某个查询语句的具体执行计划**，本章的内容就是为了帮助大家看懂`EXPLAIN`语句的各个输出项都是干嘛使的，从而可以有针对性的提升我们查询语句的性能。

将我们之前学过的所有概念串起来！实用性和工具性很高，暂时不做理解的笔记了



### 18、optimizer trace的神奇功效

优化过程大致分为了三个阶段：

- `prepare`阶段
- `optimize`阶段
- `execute`阶段

我们所说的基于成本的优化主要集中在`optimize`阶段，对于单表查询来说，我们主要关注`optimize`阶段的`"rows_estimation"`这个过程，这个过程深入分析了对单表查询的各种执行方案的成本；对于多表连接查询来说，我们更多需要关注`"considered_execution_plans"`这个过程，这个过程里会写明各种不同的连接方式所对应的成本。



如果对使用`EXPLAIN`语句展示出的对某个查询的执行计划很不理解，可以尝试使用`optimizer trace`功能来详细了解每一种执行方案对应的成本，这个功能能让我们更深入的了解`MySQL`查询优化器。



### 19、InnoDB的Buffer Pool

为了缓存磁盘中的页，在`MySQL`服务器启动的时候就向操作系统申请了一片连续的内存，这片内存就叫做`Buffer Pool`（中文名是`缓冲池`）。

![image_1d15mh3d4oadq0e1qpme22u8i61.png-47.4kB](https://user-gold-cdn.xitu.io/2019/3/2/1693e86e2b9d6dd1?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

在Buffer Pool中对数据进行增删改查

后台有专门的线程每隔一段时间负责把脏页刷新到磁盘，这样可以不影响用户线程处理正常的请求。



### 20、事务简介



**ACID 4大特性**

原子性（Atomicity）：要么全做，要么全不做

隔离性（Isolation）：对于访问相同数据的不同线程，加以管理，让其在“一定程度上”互不影响

一致性（Consistency）：事务操作后，数据符合现实生活中的约束

持久性（Durability）：数据库操作所修改的数据都应该在磁盘上保留下来，不论之后发生了什么事故都不会被丢掉



**事务的不同阶段**

active --> partially committed 内存中 --> committed 磁盘中

active --> failed 运行/同步磁盘时出现错误 --> aborted 回滚



**事务的提交**

autocommit一开，每一条sql语句自动提交

关掉autocommit之后，有些语句也会有隐式的提交上一个事务，比如create table、alter user、begin



### 21 22、redo日志

存在的原因：保证`持久性`，在事务提交前，将修改的数据刷新到磁盘上

InnoDB是以16KB的页为单位，来存储数据的；如果只修改1个byte也要刷新16KB的数据到磁盘上，那就太浪费了



redo日志会把事务在执行过程中对数据库所做的所有修改都记录下来，在之后系统崩溃重启后可以把事务所做的任何修改都恢复出来。



服务器启动时就向操作系统申请了一大片称之为`redo log buffer`的连续内存空间，翻译成中文就是`redo日志缓冲区`，我们也可以简称为`log buffer`。这片内存空间被划分成若干个连续的`redo log block`，就像这样：

![image_1d4i4orkr17vl1m5l3hl1l341pad1j.png-76.5kB](https://user-gold-cdn.xitu.io/2019/3/4/169489303ceeb982?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)



**redo日志刷新时机**

- `log buffer`空间不足时

- 事务提交时

  我们前边说过之所以使用`redo`日志主要是因为它占用的空间少，还是顺序写，在事务提交时可以不把修改过的`Buffer Pool`页面刷新到磁盘，但是为了保证持久性，必须要把修改这些页面对应的`redo`日志刷新到磁盘。

- 后台线程不停的刷刷刷

  后台有一个线程，大约每秒都会刷新一次`log buffer`中的`redo`日志到磁盘。

- 正常关闭服务器时



### 23 24、undo日志

`事务`需要保证`原子性`，也就是事务中的操作要么全部完成，要么什么也不做

为了实现事务的`原子性`，`InnoDB`存储引擎在实际进行增、删、改一条记录时，都需要先把对应的`undo日志`记下来。

如果服务器异常，或者手动rollback；存储引擎会根据undo日志来撤销增删改操作



### 25、事务的隔离级别与MVCC

Mysql作为一个CS架构的软件，可以同时连接多个客户端，每一个连接也称为一个会话（Session）；每一个会话可以开启事务并向服务器发送请求语句，也就是说服务器可以同时处理多个事务。



**事务并发执行遇到的问题**

脏写（Dirty Write）：一个事务修改了另一个未提交事务修改过的数据

![](https://user-gold-cdn.xitu.io/2019/4/18/16a2f43405cb6e70?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

脏读（Dirty Read）：一个事务读到了另一个未提交事务修改过的数据

![image_1d8nn50kndkd8641epplvelhk9.png-91.8kB](https://user-gold-cdn.xitu.io/2019/4/18/16a2f79b4eacc05d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

不可重复读（Non-Repeatable Read）：一个事务只能读到另一个已经提交的事务修改过的数据，并且其他事务每对该数据进行一次修改并提交后，该事务都能查询得到最新值

![image_1d8nk4k1e1mt51nsj1hg41cd7v5950.png-139.4kB](https://user-gold-cdn.xitu.io/2019/4/18/16a2f5b32bc1f76b?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

幻读（Phantom）：

一个事务先根据某些条件查询出一些记录，之后另一个事务又向表中插入了符合这些条件的记录，原先的事务再次按照该条件查询时，能把另一个事务插入的记录也读出来

![image_1d8nl564faluogc1eqn1am812v79.png-96.1kB](https://user-gold-cdn.xitu.io/2019/4/18/16a2f5b32d7b9ada?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

`幻读`强调的是一个事务按照某个相同条件多次读取记录时，后读取时读到了之前没有读到的记录。



**SQL标准中的隔离级别**

严重性，从重到轻：脏写 > 脏读 > 不可重复读 > 幻读

| 隔离级别           | 脏读         | 不可重复读   | 幻读         |
| ------------------ | ------------ | ------------ | ------------ |
| `READ UNCOMMITTED` | Possible     | Possible     | Possible     |
| `READ COMMITTED`   | Not Possible | Possible     | Possible     |
| `REPEATABLE READ`  | Not Possible | Not Possible | Possible     |
| `SERIALIZABLE`     | Not Possible | Not Possible | Not Possible |

因为脏写这个问题太严重了，不论是哪种隔离级别，都不允许脏写的情况发生。



**MySQL中支持的四种隔离级别**

不同的数据库厂商对`SQL标准`中规定的四种隔离级别支持不一样



**MVCC原理**

数据行中的隐藏列：trx_id（事务id，递增），roll_pointer(本质就是一个指针，指向记录对应的undo日志)

对该记录每次更新后，都会将旧值放到一条`undo日志`中，就算是该记录的一个旧版本，随着更新次数的增多，所有的版本都会被`roll_pointer`属性连接成一个链表，我们把这个链表称之为`版本链`，版本链的头节点就是当前记录最新的值。

![image_1d8po6kgkejilj2g4t3t81evm20.png-81.7kB](https://user-gold-cdn.xitu.io/2019/4/19/16a33e277a98dbec?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

READ UNCOMMITTED：直接读版本链表头，获取最新数据即可（不在乎是否commited）



**ReadView机制**

- `m_ids`：表示在生成`ReadView`时当前系统中活跃的读写事务的`事务id`列表。
- `min_trx_id`：表示在生成`ReadView`时当前系统中活跃的读写事务中最小的`事务id`，也就是`m_ids`中的最小值。
- `max_trx_id`：表示生成`ReadView`时系统中应该分配给下一个事务的`id`值。

查询语句：如果trx_id小于min_trx_id，说明是已经提交的事务，可读；如果等于当前trx_id，可读；如果max_trx_id>trx_id>min_trx_id，则查看该trx_id是否在m_ids列表中，如果不在活跃事务列表中，可读；否则就跟着版本链往下找，直到找到最新的已经提交的事务所更新的数据行为止。



`READ COMMITTED`和`REPEATABLE READ`隔离级别的的一个非常大的区别就是它们生成ReadView的时机不同。

READ COMMITTED：每次查询时生成一个ReadView，所以可能导致“不可重复读”

REPEATABLE READ：在第一次查询语句时生成ReadView，之后每次查询时，都复用同一个ReadView；所以不会发生“不可重复读”（对于快照读，也就是普通select语句，解决了幻读；但对于当前读，也就是update、delete的时候，还是会产生幻读，就是读取最新数据，其他事务insert-commit的最新值也会被读到然后修改）



### 26、锁

三种并发情况：

- 读-读（无所谓）
- 写-写（会有脏写问题，必须加锁）
- 写-读/读-写（一个事务读，一个事务写；隔离级别，MVCC）

为了解决脏写、脏读、不可重复读、幻读

方案1：读采用MVCC，写采用锁；读写分离

方案2：读写都采用锁；业务中不允许读取旧版本，每次都读取最新版本；读到最新的，锁住，写，然后再读最新的；读最新存款，锁住存款余额，存钱，再读最新存款；这种情况下，读写就相关了



**一致性读（Consistent Read）**

事务利用`MVCC`进行的读取，读不加锁，其他事务可以自由的对表中的记录做改动



**锁定读（Locking Read）**

目的：由于既要允许`读-读`情况不受影响，又要使`写-写`、`读-写`或`写-读`情况中的操作相互阻塞



锁分类：

- `共享锁 Shared Locks`（S锁）：在事务要读取一条记录时，需要先获取该记录的`S锁`。
- `独占锁/排他锁 Exclusive Locks`(X锁)：在事务要改动一条记录时，需要先获取该记录的`X锁`。



除了S锁互相兼容（事务A获取S锁，事务B也可以进来回去S锁），其他组合情况都是互不兼容的



读取加S锁

```sql
SELECT ... LOCK IN SHARE MODE;
```

读取加X锁

```sql
SELECT ... FOR UPDATE;
```



**表级锁**

以上为行级锁，为数据行上锁；表级锁则是为整个表上锁

表级S锁，只兼容行级S锁和表级S锁；表级X锁，什么也不兼容



如何保证获取表级S锁前，没有其他事务持有行级X锁；或者获取表级X锁前，没有其他事务获取行级S/X锁？

- 意向共享锁 Intention Shared Lock（IS锁）：当事务准备在某条记录上加`S锁`时，需要先在表级别加一个`IS锁`。
- 意向独占锁 Intention Exclusive Lock（IX锁）：当事务准备在某条记录上加`X锁`时，需要先在表级别加一个`IX锁`。



**MySQL中的行锁和表锁**

对于`MyISAM`、`MEMORY`、`MERGE`这些存储引擎来说，它们只支持表级锁，而且这些引擎并不支持事务，所以使用这些存储引擎的锁一般都是针对当前会话来说的。

因为使用MyISAM、MEMORY、MERGE这些存储引擎的表在同一时刻只允许一个会话对表进行写操作，所以这些存储引擎实际上最好用在只读，或者大部分都是读操作，或者单用户的情景下。



以上为理论知识，以下重点讨论InnoDB存储引擎中的锁

**InnoDB表级锁**

- 表级别的`S锁`、`X锁`

  调用`LOCK TABLE`这样的手动锁表语句；通过在`server层`使用一种称之为`元数据锁`（英文名：`Metadata Locks`，简称`MDL`）东东来实现的；没什么用，只是会降低并发能力而已

- 表级别的`IS锁`、`IX锁`

  和上边概念一样，不赘述

- 表级别的`AUTO-INC锁`

  在执行插入语句时就在表级别加一个`AUTO-INC`锁，然后为每条待插入记录的`AUTO_INCREMENT`修饰的列分配递增的值，在该语句执行结束后，再把`AUTO-INC`锁释放掉。



**InnoDB行级锁（重点）**

InnoDB中有各种类型的行级锁，不同类型起到的作用也不一样



- `Record Locks`：就是普通的行级锁，分为X锁和S锁
- `Gap Locks`：为了防止插入幻影记录而提出的；不让往上一条记录，到这条记录之间的间隙中插入
- `Next-Key Locks`：Record Locks和Gap Locks的合体，它既能保护该条记录，又能阻止别的事务将新记录插入被保护记录前边的`间隙`。
- `Insert Intention Locks`：如果往有GAP锁的数据行前的间隙中插入，获取一个插入意向锁，然后等着



> InnoDB中的锁都是悲观锁；MVCC不是锁，解决读-写问题；乐观锁需要程序员自己搞，用来解决写-写问题
>
> MVCC不能完全解决幻读问题，算是解决了快照读（普通select不上锁）的幻读问题，并不解决当前读的幻读问题（update、delete、insert）

![image-20210628143105121](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210628143105121.png)



为什么用B+树，而不用B树：

数据库索引采用B+树的主要原因是：B树在提高了IO性能的同时并没有解决元素遍历效率低下的问题，正是为了解决这个问题，B+树应用而生。B+树只需要去遍历叶子节点就可以实现整棵树的遍历。而且在数据库中基于范围的查询是非常频繁的，而B树不支持这样的操作或者说效率太低。



Why are B+ trees used instead of binary search trees in databases?

This is primarily because unlike **binary search trees**, **B+ trees** have very high fanout (number of pointers to child nodes in a node, typically on the order of 100 or more), which reduces the number of I/O operations required to find an element in the **tree**.



It's all about branching factor. Because of the way B+-Trees store records (called "satellite information") at the leaf level of the tree, they maximize the branching factor of the internal nodes. High branching factor allows for a tree of lower height. Lower tree height allows for less disk I/O. Less disk I/O theoretically means better performance.

