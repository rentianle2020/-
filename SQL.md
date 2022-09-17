### **IS NULL**

**例题**

https://leetcode.cn/problems/find-customer-referee/



### **CASE WHEN... THEN... ELSE... END**

相同的逻辑可以使用IF(expr, then, else)的方式实现

**例题**

https://leetcode.cn/problems/calculate-special-bonus/

https://leetcode.cn/problems/swap-salary/



### **DELETE a FROM a JOIN b ON...**

**例题**

https://leetcode.cn/problems/delete-duplicate-emails/



### **字符串函数**

官方文档：https://www.mysqlzh.com/doc/116.html

常用

- CONCAT('a','b','c')
- LENGTH('abc')
- LEFT('foobar',3) = 'foo' ; RIGHT('foobar',3) = 'bar'
- UPPER('abc') = ‘ABC' ; LOWER('ABC') = 'abc'
- SUBSTRING('abcdef',2,4) = 'bcd

**例题**

https://leetcode.cn/problems/fix-names-in-a-table/



### **聚合函数**

operate on sets of values, often used with `GROUP BY` clause to group values into subsets.

官方文档：https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html

- COUNT(expr)：经常使用COUNT(DISTINCT name ORDERBY id)
- GROUP_CONCAT(expr) 

**例题**

https://leetcode.cn/problems/group-sold-products-by-the-date/

> GROUP BY 多个column的时候，需要通过逗号分割，不能使用AND



### 聚合函数+CASE

GROUP BY之后，使用聚合函数对筛选出的rows进行操作，其中使用CASE判断来决定其在聚合函数中的值

```SQL
SELECT stock_name, sum(
    CASE WHEN operation = 'BUY' THEN -price
    ELSE price
    END
) AS capital_gain_loss
FROM Stocks
GROUP BY stock_name
```



### **HAVING**

HAVING是用来筛选Group的，而不是在Group中筛选row的。

```sql
SELECT COUNT(CustomerID), Country
FROM Customers
GROUP BY Country
HAVING COUNT(CustomerID) > 5
ORDER BY COUNT(CustomerID) DESC;
```



### **LIKE模糊查询**

- #匹配任意一个
- %匹配任意多个

**例题**

https://leetcode.cn/problems/patients-with-a-condition/



### **UNION ALL**

UNION [DISTINCT]会对结果集去重；如果不需要去重，UNION ALL效率更高

**例题**

https://leetcode.cn/problems/rearrange-products-table/



### **LIMIT**

LIMIT 5,100 = 从下标5开始（包含），连续找100个



### **各种函数**

- IFNULL(expr, 'default')

  https://leetcode.cn/problems/second-highest-salary/solution/

- DATEDIFF(date1, date2)

  ```sql
  DATEDIFF('2007-12-31','2007-12-30');   # 1
  DATEDIFF('2010-12-30','2010-12-31');   # -1
  ```


- YEAR(time_stamp)

  ```sql
  WHERE YEAR(time_stamp) = '2022'
  ```

  https://leetcode.cn/problems/the-latest-login-in-2020/
  
- ROUND(expr)

  ```sql
  ROUND(SUM(p.price * u.units) / SUM(u.units),2) AS average_price
  ```

- DATESUB(DATE, INTERVAL value unit)

  ```sql
  SUBDATE("2017-06-15 09:34:21", INTERVAL 15 MINUTE);
  SUBDATE("2017-06-15 09:34:21", 15); --默认减DAY
  ```

  

### **JOIN**

![Visual explanation of JOIN types](assets/VQ5XP.png)

> do not use `LEFT JOIN` if you really mean `INNER JOIN`



### ON和WHERE

ON是在建立临时表的时候，无论是否满足条件，LEFT JOIN都会保留左表的Column

WHERE是在JOIN后，进行筛选，可能会筛除左表中的Column



### PARTITION BY（窗口函数）

平时我们只能GROUP BY + HAVING筛选分组，或者GROUP BY + 聚合函数得出每组的最值

如果想对每组的所有rows进行计算，如组内排序，使用窗口函数会非常方便

```sql
Window_function ( expression ) 
       Over ( partition by expr [order_clause] [frame_clause] ) 
```



**排序**

rank()：两个并列1，第三名排名为3

dense_rank()：两个并列1，第三并排名为2

row_number()：不存在两个并列，所有排名为连续的

```sql
SELECT *, dense_rank()
OVER(
	PARTITION BY student_id
	ORDER BY grade DESC, course_id ASC
) rk
FROM Enrollments
```



**获取前OFFSET个&后OFFSET个row的指定Column**

LAG(column, offset, default)

LEAD(column, offset, default)

offset默认1，default默认NULL

```sql
SELECT
	seat_id ,
	free ,
	LAG(free,1,0) over() pre_free,
	LEAD(free,1,0) over() next_free
FROM  cinema
```



**SUM() & COUNT()**

作为窗口函数使用，默认表示累计和&累计计数（PARTITION & ORDER之后，累计到当前row位置的值）

如果想要指定区间和，请使用ROWS再进行限制



**ROWS**

简单应用：ROWS 6 PRECEDING：选中当前行+之前的6行；可以配合SUM()函数使用

```sql
当前行 - current row
之前的行 - preceding
之后的行 - following
无界限 - unbounded
表示从前面的起点 - unbounded preceding
表示到后面的终点 - unbounded following

取当前行和前五行：ROWS between 5 preceding and current row --共6行
取当前行和后五行：ROWS between current row and 5 following --共6行
取前五行和后五行：ROWS between 5 preceding and 5 folowing --共11行
```



### COUNT(expr) 和 SUM(expr)

COUNT(expr)会返回不是null的rows个数，无论expr得到的值是true还是false；可以配合IF(COUNT(expr),TRUE,NULL)使用

SUM(expr)则只会返回expr返回true的Recrods个数



可以配合CASE连用，对于每个GROUP中的rows进行一次expr计算，然后得出最终的COUNT或SUM



### 多字段IN

可以多字段匹配IN，方便实用

```sql
//存在id相同，日期等于第二天的数据
(id,date) IN (SELECT id, date + INTERVAL 1 DAY FROM person GROUP BY id)
```



### AVG(expr)

判断符合条件语句的rows百分比

```sql
AVG(t.status = 'cancelled_by_driver' OR t.status = 'cancelled_by_client')
# 相当于
SUM(
    CASE 
    WHEN t.status = 'cancelled_by_driver' OR t.status = 'cancelled_by_client' 
    THEN 1 ELSE 0 END
) / COUNT(*)
```



### 递归

https://dev.mysql.com/doc/refman/8.0/en/with.html#common-table-expressions-recursive

```sql
with recursive t as (
    SELECT 1 AS n --初始化
    UNION ALL
    SELECT n + 1 FROM t WHERE n < 100 --递归函数，直到该函数不再新增rows，就停止递归。
)

SELECT * FROM t
//[[1], [2], [3], [4], [5] ... [100]]}
```

​	

# 技巧

### 寻找连续区间

通过字段 - rank的方式，连续的数字和rank会组成等差数列，导致diff相同，再GROUP BY即可

```sql
SELECT MIN(t.log_id) AS start_id, MAX(t.log_id) AS end_id
FROM (
    SELECT log_id,   
       log_id - rank() over(ORDER BY log_id) diff
    FROM Logs
) t 
GROUP BY t.diff
```



### 寻找共同伙伴

多个JOIN，找到a的朋友，找到b的朋友并确认他们是同一个朋友，然后再可以统计共同朋友的个数...

```sql
SELECT f1.user1_id, f1.user2_id, count(*) common_friend
FROM Friendship f1
INNER JOIN f f2 ON f1.user1_id = f2.user1_id
INNER JOIN f f3 ON f1.user2_id = f3.user1_id AND f2.user2_id = f3.user2_id
GROUP BY f1.user1_id, f1.user2_id
HAVING count(*) >= 3 
```



### GROUP BY日期

1. 可以通过`DATE(day)`获取`年-月-日`格式的日期
2. 窗口函数中使用`partition by date_format(day,"%Y-%m-%d")`手动格式化日期字段进行分组



### IN和EXSISTS区别

IN就是一个JOIN，EXSISTS是一个LOOP

如果内表大，但是索引好，用EXSISTS；如果内表小，用IN好。

如果Mysql的Optimizer够聪明，或者两表差不多大，或者内表没有索引等情况...IN和EXSISTS就没区别。

https://asktom.oracle.com/pls/apex/f?p=100:11:::::P11_QUESTION_ID:953229842074



### NOT IN和NOT EXSISTS

如果NOT IN(...)中有null值，则一定返回0行结果

```
SELECT * FROM T1 WHERE id NOT IN (1,null)
变成
SELECT * FROM T1 WHERE id <> 1 AND id <> null
而
null参与等值运算永远返回false
```

EXSISTS只在乎子查询中是否返回任何值，如果没有值返回，则NOT EXSISTS为true，将row添加到结果集

尽量避免使用NOT IN