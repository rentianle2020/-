# Spring Data JPA

https://www.youtube.com/watch?v=otinfgwkMbY

Java Persistence API：Java提供的ORM接口，由具体的ORM框架做实

EntityManagerFactory工厂模式拿到一个EntityManager

通过这个对象来进行find，remove等操作

JPA统一了ORM标准，让企业可以在ORM框架中更简单的切换



How spring picks up the implementation for interface at run time

https://stackoverflow.com/questions/54495660/how-spring-picks-up-the-implementation-for-interface-at-run-time



**Understand JPA and Not Limited By Framework**



## Entities and the context

Entity class map to the table

each field map a column



**EntityManager**

obtained from EntityManagerFactory

manage the entities

context --> a collect of instance

创建一个Entity，调用entityManager.persist(entity)将其交给manager，然后就可以操作它了



@Entity 表示实体

@Table 映射table

@Column 映射comlumn（可以设置not null等属性）

@Id 映射主键

@Basic(optional = false) 是否可为null



#### 主键生成

@Id主键生成策略，不同的数据库map不同的功能，这里只关注MySQL

- IDENTITY：Auto Increment

- TABLE：使用key_generator表，负责生成主键值

  ```java
  @GeneratedValue(strategy = GenerationType.TABLE,generator = "key_generator")
  ```

- SQUENCE：MySQL没有这个选项，如果使用Oracle的话，是一个优选



UUID：UUID values are unique across tables, databases, and even servers that allow you to merge rows from different databases or **distribute databases across servers**

```java
@GenericGenerator(name = "uuid",strategy = "org.hibernate.id.UUIDHexGenerator")
@GeneratedValue(generator = "uuid")
private String id
```



#### @Enumerated and @Temporal types

对于枚举类型的字段，比如币种（RMB，US Dollar...）



默认EnumType.Ordinal，向数据库中存储存枚举下标，导致枚举类中的对象不能轻易改变顺序，扩展性差

最好选择EnumType.STRING，直接存储枚举类对象的toString()值

```java
@Enumerated(EnumType.STRING)
```



**日期映射**

| Java class    | mysql comlumn type  |
| ------------- | ------------------- |
| LocalDate     | date                |
| LocalDateTime | timestamp, datetime |
| ZoneDateTime  | 同上，zone不起作用  |

如果是old java version使用Date类，就要使用@Temporal来指定日期的存储格式



**@Embedded**

@Embeddable：class which field mapped to column, ready to be embedded in another entity

@Embedded：embedded a class as field, and map its field to cloumn

Each fields of the embedded object is mapped to the database table for the entity.

让Embedded Object可以在多个Entity中复用



@AttributeOverride：在Embedded基础上，自定义Embedded的field到comlumn的映射

为什么不直接在Embeddble的类中直接更改field name？因为Embeddle可能是在多个Entity中被引用，而表中的字段名不统一。

```java
@Embedded
@AttributeOverride(name = "c", column = @Column(name = "country"))
private Address address;
```



**Composed Primary Keys**

方法1：写一个xxxPK类implement Serializable（JPA规定），然后使用@IdClass在Entity中引入该类，并在Entity中也要使用多个@Id来标明。@Id fieldName，要和xxxPK fieldName对应。

方法2：使用@Embeddable implement Serializable + @EmbeddedId，Entity中@EmbeddedId的主键fieldName不重要。同样可以使用@AttribueOverride来改变Embeddable的映射



**@Access**

AccessType：

- Field：反射读写
- Property：get set方法读写

使用@Access来改变AccessType，通常都使用Field方式

https://thorben-janssen.com/access-strategies-in-jpa-and-hibernate/



## Relationship

OneDirectional：只有一方知道另一方（仅拥有外键的实体类中，包含另一方）

BiDirectional：双方都知道另一方（两个实体类中都包含对方）



**@OneToOne**

owner：维持关系的一方，拥有外键的一方

@JoinColumn：默认的外键名是xxx_id，可以通过@JoinColumn自定义

需要先persist non-owner，再persist owner，否则外键没人可以指向！



属性

- cascade：目标类需要被执行的操作，如PERSIST

- fetch：获取数据是的方式（LAZY就只获取本表，不获取关联表中的内容）

- optional：关联关系是否是可选的，是否可以为空

- mappedBy：field that owns the relationship

  如：Department被Employee中名为department的field影射了（外键department_id），就需要在Department类的private Employee employee上，标注是Employee.department这个field，影射了Department本身

- targetEntity：指定field类



**@OneToMany & @ManyToOne**

一对多的情况下使用

- 在“一”表中的集合类field上@OneToMany

  默认通过创建第三张表来维护关系

- **在“多”表中使用@ManyToOne，通过外键形式来维护关系**



**@ManyToMany**

@JoinTable + @JoinColumns：默认表名是xxx_xxx，默认comlumn名是xxx_id & yyy_id

在创造BiDirectional关系时，任意一方都可以作为owner，另一方使用mappedBy即可。



**Embedded扩展注解**

@Embedded + @AttributeOverride + @AssociationOverride：override a mapping for an entity attribute & relationship.



@Embedded（optional） + @ElementCollection + @CollectionTable：Specifies a collection of instances of a basic type or embeddable class



**Map**

@MapKey...

OneToMany中，在One表中通过key=id，value=Many的方式，可以在find时获取相关的所有Many

```java
@OneToMany(mappedBy = "department")
@MapKey(name = "id")
private Map<Integer, Employee> employees;
```



ManyToMany中，通过key value方式存储第三张表，可以在find时获取map

```java
@ManyToMany
@JoinTable(name = "prof_stud",
           joinColumns = @JoinColumn(name = "prof"),
           inverseJoinColumns = @JoinColumn(name = "stud"))
@MapKeyColumn(name = "course")
private Map<String, Student> students;
```

> 小心Lombok toString()循环导致的StackOverflow



**@MappedSuperclass and Inheritance strategies**

3 Inheritance strategies（继承自非抽象类）

- Single Table：所有Entity都在一张表上，使用dtype字段保存Entity类型

- Joined：衍生类类Entity设立表，存储额外信息，使用foreign key作为主键指向基类Entity表的主键

- Table Per Class：基类和继承类Entity的表就是两张表，不能使用auto increment，两张表的id不能重复。

  现实项目中不会使用到！

  

抽象类不是一个Entity，需要使用@MappedSuperclass

继承自抽象类，没有strategy可以选择，每一个继承类单独建表，独立的Entity



> 如果需要被JPA管理，使用@Entity、@Embedded、@MappedSuperclass其中一种来标注类



## EntityManager

persist(Object)：如果Entity和其他有relationship，需要同时persist关联Entity，或者cascade PERSIST

flush()：立即将context中的Entity同步到database



find(Class, primaryKey)：查询

merge(Object)：更新，返回由context管理的Entity

remove(Object)：删除



getReference(Class, primaryKey)：获得Entity，但是它的state都是被lazy fetched

refresh(Object)：Refresh the state of the instance from the database

contains(Object)：Entity是否存在于context



detach(Object)：将具体Entity移出context

clear()：清空context

> **The persistence context is the first-level cache where all the entities are fetched from the database or saved to the database**



## Entity Lifecycle

LOAD：@PostLoad

UPDATE：@PreUpdate @PostUpdate

REMOVE：@PreRemove @PostRemove

PERSIST：@PrePersist @PostPersist 



# Spring Data JPA

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jpa?serverTimezone=UTC
    username: root
    password: 1028
  jpa:
    show-sql: on
    hibernate:
      ddl-auto: update
```



**自定义查询语句**

```java
//JPQL

@Query("select s.name from Student s where s.id = ?1") //1代指第一个参数
String findNameById(Integer id);

@Query("select s.name from Student s where s.id = :id") 
String findNameByIdParam(Integer id);
```

https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods



**自定义update语句**

```java
@Modifying
@Transactional
@Query(value = "update student set name = ?1 where id = ?2",
            nativeQuery = true)
int updateStudentNameById(String studentName,Integer id);
```



**PagingAndSortingRepository**

```java
Pageable pageable = PageRequest.of(0,4,Sort.by("id").descending());

Page<Student> students = repository.findAll(pageable);

students.getContent();
students.getTotalElements();
students.getTotalPages();
```





https://www.educba.com/mybatis-vs-hibernate/