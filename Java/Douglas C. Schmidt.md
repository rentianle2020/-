# Douglas C. Schmidt

split data source into "chunks", process chunks in a poll of threads, combine into a single result

intermediate operation are “lazy”

Compiler：runtime, stream turned into a pipline（LinkedList with aggregate operation）

每一个pipline stage is a bitmap of flags

- sized
- distinct
- sorted
- ordered



创建：spliterator设定flag

<img src="C:/Users/%E4%B9%90%E4%B9%90%E5%A4%A7%E5%93%A5%E5%93%A5/Desktop/%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/Java/assets/image-20211101045854704.png" alt="image-20211101045854704" style="zoom:50%;" />	

Stream generate() & iterate() 没有sized



中间操作：改变stream flags

- map()：清除sorted & distinct，保留sized
- filter()：清除sized，保留sorted & distinct
- sorted()：保留其他同时，添加sorted

the flags at each stage are updated

为什么让java compiler这么做？和查询优化同理！

两类：

- stateless：filter,map,flatMap（pipline with only stateless operation, run in one pass）

- stateful: sorted,limit,distinct（有stateful，就要分成多部分执行了）

  <img src="C:/Users/%E4%B9%90%E4%B9%90%E5%A4%A7%E5%93%A5%E5%93%A5/Desktop/%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/Java/assets/image-20211101053617262.png" alt="image-20211101053617262" style="zoom: 50%;" />	



终结操作：runs the stream，就像执行优化后的查询操作

两类：

- Non-short-circuiting：reduce,collect,forEach
- short-circuiting：anyMath,findFirst

