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

GROUP BY之后，使用聚合函数对筛选出的Records进行操作，其中使用CASE判断来决定其在聚合函数中的值

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

HAVING是用来筛选Group的，而不是在Group中筛选Record的。

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

UNION会对结果集去重；如果不需要去重，UNION ALL效率更高

**例题**

https://leetcode.cn/problems/rearrange-products-table/



### **LIMIT**

LIMIT 1,1 = LIMIT 1, OFFSET 1（这时LIMIT从下标0开始，LIMIT 1代表从第二个Record开始，连续找OFFSET个）



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



### **JOIN**

![Visual explanation of JOIN types](assets/VQ5XP.png)

> do not use `LEFT JOIN` if you really mean `INNER JOIN`



### ON和WHERE

ON是在建立临时表的时候，无论是否满足条件，LEFT JOIN都会保留左表的Column

WHERE是在JOIN后，进行筛选，可能会筛除左表中的Column



### PARTITION BY（窗口函数）

平时我们只能GROUP BY + HAVING筛选分组，或者GROUP BY + 聚合函数得出每组的最值

如果想对每组的所有Records进行计算，如组内排序，使用窗口函数会非常方便

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



**获取前OFFSET个&后OFFSET个Record的指定Column**

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



### COUNT(expr) 和 SUM(expr)

COUNT(expr)会返回不是null的Records个数，无论expr得到的值是true还是false；所以可以配合IF(COUNT(expr),TRUE,NULL)使用

SUM(expr)则只会返回expr返回true的Recrods个数



可以配合CASE连用，对于每个GROUP中的Records进行一次expr计算，然后得出最终的COUNT或SUM