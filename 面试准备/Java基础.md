### **Java的编译与运行**

先由Java Compiler编译为字节码（.class文件），再由JVM的Interpreter编译为机器码并运行（高频使用的代码，将通过JIT直接编译为Machine Code）



**编译与运行（图）**

https://stackoverflow.com/questions/1326071/is-java-a-compiled-or-an-interpreted-programming-language/36394113#36394113

**JIT的功能**

https://stackoverflow.com/questions/16439512/what-is-the-use-of-jvm-if-jit-is-performing-bytecode-conversion-to-machine-instr/16440092#16440092

**为什么不直接将所有程序编译为Machine Code？**

https://stackoverflow.com/a/5795505



### JDK & JRE 

https://stackoverflow.com/questions/1906445/what-is-the-difference-between-jdk-and-jre



### Oracle JDK & OpenJDK

https://stackoverflow.com/questions/22358071/differences-between-oracle-jdk-and-openjdk



### 接口和抽象类

**共同点**

- 不能被实例化
- 可以包含抽象方法
- 可以包含默认实现的方法（Java 8支持在接口中添加default关键字修饰的默认方法）

**区别**

- 接口强调行为约束，抽象类强调所属关系和代码复用
- 单继承，多实现
- 接口中只能存放`public static final`修饰的常量，不允许其他类型的变量



### 方法重写

方法名相同，形参列表相同

返回值和抛出的异常都可以为父类方法中的衍生类

访问权限可以更宽泛



### 拷贝

实现Cloneable接口，调用默认clone()方法进行对象`浅拷贝`。

需要重写clone()方法， 调用成员对象的clone()方法，才能完成`深拷贝`

![img](assets/shallow&deep-copy.8d5a2e45.png)



### 字符串

**Immutable不可修改**

- class为final修饰，不允许继承
- 内部是private final修饰的byte[]数组，不允许修改
- 所有修改相关的public方法全部返回新String。

**String constant pool字符串常量池**

- 字符串常量池存在于堆中，用于存储String对象
- 如果创建已经存在于常量池的String，直接赋值给引用，不再生成新的。

```java
// 在堆中创建字符串对象”Java“
// 将字符串对象”Java“的引用保存在字符串常量池中
String s1 = "Java";
// 直接返回字符串常量池中字符串对象”Java“对应的引用
String s2 = s1.intern();
// 会在堆中在单独创建一个字符串对象
String s3 = new String("Java");
// 直接返回字符串常量池中字符串对象”Java“对应的引用
String s4 = s3.intern();
// s1 和 s2 指向的是堆中的同一个对象
System.out.println(s1 == s2); // true
// s3 和 s4 指向的是堆中不同的对象
System.out.println(s3 == s4); // false
// s1 和 s4 指向的是堆中的同一个对象
System.out.println(s1 == s4); //true
```

使用new关键字创建的String对象存在于堆中（如果常量池中没有，就额外在常量池中再创建一个String对象）

```java
public static void main(String[] args) throws IOException {
    String s1 = new String("abc"); //堆中对象，常量池中指针
    String s2 = "abc";
    System.out.println(s2 == s2); //true
}
```

https://stackoverflow.com/a/20488205

**拼接**

```java
//字面量拼接，JVM常量折叠(Constant Folding)直接优化为"abcdef"
String str1 = "abc" + "def";
//String对象拼接，底层调用StringBuilder.append().append().toString()完成拼接
//toString()调用new String()，所以是在堆中，常量池之外创建新的对象
String str2 = "abc";
String str3 = "def";
String str4 = str2 + str3;
```



### 异常

Throwable

- Error：程序无法处理的异常，如JVM运行错误，JVM内存不够等...
- Exception
  - Checked Exception：需要被`catch`或`throws`，否则无法通过编译
  - Unchecked Exception：RuntimeException and its subclasses are unchecked exceptions.



**不要在 finally 语句块中使用 return!** 

当try语句中执行return

1. 将返回值保存为本地变量
2. 执行finally
3. 返回本地变量中的返回值
4. 如果finally中return，本地变量就会被改变，try中的return值就会被忽略

finally语句块中的代码，在线程死亡`System.exit(1)`或者关闭CPU的情况下，都不会被执行。



**使用try-with-resources**

当对象实现了`java.lang.AutoCloseable`或者 `java.io.Closeable`

面对必须要关闭的资源，我们总是应该优先使用 `try-with-resources` 而不是`try-finally`。随之产生的代码更简短，更清晰，产生的异常对我们也更有用。

```java
try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(new File("test.txt")));
     BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(new File("out.txt")))) {
    int b;
    while ((b = bin.read()) != -1) {
        bout.write(b);
    }
}
catch (IOException e) {
    e.printStackTrace();
}
```



### 泛型

泛型类（new时定义泛型），泛型接口（implement时定义/不定义泛型），泛型方法（调用时定义泛型）

静态方法因为先于类实例加载，所以无法使用类上的泛型



### 反射

框架的灵魂，支持运行时分析执行类中的方法，业务代码中很少使用。

框架大量使用动态代理，JDK动态代理依赖反射。



### 注解

编译时扫描：@Override

运行时反射处理：Spring中的@Component



### I/O

序列化：将对象转化为二进制字节流

反序列化：将二进制字节流转换成对象

对于不想进行序列化的变量，使用 `transient` 关键字修饰。

https://tech.meituan.com/2015/02/26/serialization-vs-deserialization.html



**字节流&字符流**

网络传输和文件读写都是字节传输，图片，音频等文件一般都是用字节流收发。

字符流将字节转化为字符，方便我们使用，但是这个转化过程还是耗时的。



### 值传递

Java 中将实参传递给方法（或函数）的方式是 **值传递** ：

- 如果参数是基本类型的话，很简单，传递的就是基本类型的字面量值的拷贝，会创建副本。
- 如果参数是引用类型，传递的就是实参所引用的对象在堆中地址值的拷贝，同样也会创建副本