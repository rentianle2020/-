# On Java 8

Requiring that everything be an object (especially all the way down to the lowest level) is a design mistake, but banning objects altogether seems equally draconian.



## OOP

OOP describes the problem in terms of the problem, rather than in terms of the computer where the solution will run.



So, although what we really do in object-oriented programming is create new data types, virtually all object-oriented programming languages use the “class” keyword. When you see the word “type” think “class” and vice versa.



**an excellent way to think about objects is as “service providers.”**

Interface定义Object提供的服务

Implementation提供服务的具体实现方法



理解为service provider带来的好处

- 从程序设计的角度：Improve Cohesion，elements of a module/class fit together well 让类中的元素紧密的结合在一起（each object does one thing well, but doesn’t try to do too much）
- 容易理解，方便复用



**访问控制**

The goal of the class creator is tobuild a class that exposes only what’s necessary to the client programmer and keeps everything else hidden.

 

原因

-  keep client programmers’ hands off portions they shouldn’t touch
- enable the library designer to change the internal workings of the class without worrying about how it will affect the client programmer. 



**继承**

基类 & 衍生类



区别

- 衍生类在基类的基础上扩展更多方法 is-like-a
- 衍生类覆盖/实现基类的方法（如果仅作覆盖，就是可以is-a）



**多态**

dependency inversion

没有多态：higher-level modal depends on lower-level module

有了多态：higher-level modal depends on interface, lower-level module also depends(follows) on interface



With the addition of an abstract layer, both high- and lower-level layers reduce the traditional dependencies from top to bottom. Nevertheless, the "inversion" concept does not mean that lower-level layers depend on higher-level layers directly. Both layers should depend on abstractions (interfaces) that expose the behavior needed by higher-level layers.

![DIPLayersPattern.png](https://upload.wikimedia.org/wikipedia/commons/8/8d/DIPLayersPattern.png)	



**Single Rooted Hierarchy**

所有类都是Object的衍生类

Java5之前，没有泛型generics，所有容器中的类都默认是Object



**对象的创建和生命周期**

Heap：随着函数的运行，动态开辟空间，由GC负责回收



**异常处理**

Java语言自带异常处理机制，并且强迫使用。这也是唯一报错的方法。

Exception handling existed before object-oriented languages. 所以并不是OOP的独有特性



## Objects Everywhere

Use reference to manipulate Object (使用遥控器来控制电视)



作用域：每个reference会在作用域执行完毕后被销毁，然而其指向的对象还会保留在heap中等待GC。



 When you say something is static, it means the field or method is not tied to any particular object instance. Even if you’ve never created an object of that class, you can call a static method or access a static field. With ordinary, non-static fields and methods, you must create an object and use that object to access the field or method, because non-static fields and methods must target a particular object.



java.lang is implicitly included in every Java code file, these classes are automatically available



Java Code Conventions: 类名UpperCamelCase，属性和方法lowerCamelCase



## Operators

函数传参，使用=给形参复制：基本数据类型直接复制值，对象类型则复制reference地址



自动包装，Integer i = 127，底层调用了Integer.valueOf(127); 先去找cache



The reason for short-circuiting, in fact, is that you can get a potential performance increase if all the parts of a logical expression do not need evaluation.



0b10 --> binary 3

0x10 --> hex 16

Integer.toBinaryString()将Integer转换为二进制字符串

可以给数字加下划线，增加可读性。1_000_000 = 1 million

e并不指代常数，而是代表10 to the power of。1e2 = 1 * 10^2 = 100.0 (浮点数)

`>>`有符号右移，`>>>`无符号右移



基本数据类型转换，如果没有精度损失的可能性，则自动转换。

casting to a smaller type，需要强转。



float转int会自动将小数点抹去，不四舍五入

char/byte/short/int混合运算，计算前都自动转成int，结果也是int

float和double，结果double

int和long，结果long



## Control Flow

Java uses all of C’s execution control statements, but Java doesn’t allow you to use a number as a boolean



For循环可以用`,`声明多个变量



for, while --> 循环控制（有条件）

return, break, continue --> unconditional branching 无条件分支



Although goto is a reserved word in Java, it is not used in the language—Java has no goto.

还有一种奇怪的语法，使用了和goto异曲同工，使用label标注一个循环，然后可以通过break和continue控制这个循环

```java
public static void main(String[] args) {
        outer:
        for (int i = 0; i < 10; i++) {
            inner:
            for (int j = 0; j < 10; j++) {
                if (j == 2) continue inner;
                if (j == 3) break outer;
                System.out.println(j);
            }
        }
    }
//output 0 1
```

It’s important to remember that the only reason to use labels in Java is when you have nested loops and you must break or continue through more than one nested level.



Switch case: Java 7之前只支持char or int，现在则支持char, byte, short, int, Character, Byte, Short, Integer, String, or an **enum**



## **Housekeeping**

initialization and cleanup



### 类initialization

new一个类，自动调用其constructor构造函数



**重载方法Overloading**

If methods have the same name, how can Java know which method you mean?

There’s a simple rule: Each overloaded method must take a **unique list of argument types**.(different order also make sense)

不同的基本数据类型参数也可以满足重载需求；传入small type，没有重载方法则自动promotion；传入char，如果没有重载方法，则按照int来算



**函数如何知道是哪个对象调用的它？**

```java
public class BananaPeel {
	public static void main(String[] args) {
		Banana a = new Banana(),
		b = new Banana();
		a.peel(1);
		b.peel(2);
	}
}
//虽然不允许这么写，但是这就是compiler的工作机制，将调用方法的对象传入参数
//Banana.peel(a, 1);
//Banana.peel(b, 2);
```

因为这个对象参数是compiler底层传入的，我们怎么在方法内访问它呢？使用this！

this仅仅是一个reference of 正在调用方法的 object，用于访问当前对象，或将当前对象作为参数传到其他静态方法中。



**构造函数互相调用**

constructor call：在构造函数中使用this(argument list)调用另一个match argument list的构造函数

constructor call 只能出现在构造函数中的第一行，且只能使用一次



### 变量Initialization



**非静态**

- 直接在类中定义变量值（new对象时，在构造函数前执行）
- 在构造函数中初始化变量值

非静态代码块，和直接被定义的变量值一样；new对象时，在构造函数前执行



**静态**

静态变量&静态代码块，在类被第一次加载时执行（访问类的静态方法（构造函数也算static）或静态变量时）



### 数组Initialization

静态hard code & 动态初始化

使用Object... args来接收变长参数（也就是数组）

只能出现一次，且出现在参数列表的最后

```java
public void test(int required, Integer... args){
  for (Integer arg : args) {
    System.out.println(arg);
  }
}
```



### Cleanup

Garbage collection is only about memory

the sole reason for the existence of the garbage collector is to recover memory your program is no longer using.

去看GC原理，看看它是怎么释放内存空间的！JIT直接将code编译为机器码，虽然增加compile时长 & 更大的执行文件，但是不用再次被JVM翻译，带来更快的执行速度。配合lazy evaluation，仅在需要时JIT compile代码。



### Enum Initialization

enum关键字影响编译行为，生产枚举类（enums are classes and have their own methods）

```java
public enum Drink {
    TEA,COFFEE,COKE,WATER
}

Drink[] drinks = Drink.values();
Drink coffee = Drink.COFFEE;
coffee.ordinal()
coffee.toString() //name()
```

A switch is intended to select from a limited set of possibilities so it’s an ideal match for an enum.



## Implementation Hiding 封装

Access control (or implementation hiding) is about “not getting it right the first time.”

将可改变的部分（内部实现）和不能改变的部分（接口：即函数名，参数列表，返回类型）分离

更改内部实现，并不会影响已经依赖接口的客户端程序员。

将类属性private，然后向外界提供方法访问，让属性访问更加灵活。如果直接通过 类.属性 的方式访问，我们的代码就完全没有修改的余地了，属性名都一点不能变！



封装encapsulation：As a library designer, you’ll keep everything as “private” as possible, and expose only the methods you want the client programmer to use. This is generally what you’ll do.



package关键字：bundled classes together into a cohesive library unit

import关键字：引入指定的package和class



编译型语言：编译为中间形式 --> linker to packaged together with other of its kind

Java：每个.java文件最多包括一个public class（多个default class AKA support class） --> .java中每个class文件编译为一个.class文件 --> 压缩为Java ARhive(JAR) File

The Java interpreter is responsible for finding, loading, and interpreting these files



Note that compiled code is often placed in a different directory than source code.（maven项目自动编译到target file中）

The path to the compiled code must still be found by the JVM through the CLASSPATH.



access control focuses on a relationship—and a kind of communication—between a library creator and the external clients of that library. 当然如果是一个单人项目，或者所有程序都在同一个包下，那就无所谓了，default(package) access works fine.



**封装的两大作用**

- 让调用者只能访问到指定的类和方法
- 允许对实现的改动，允许程序犯错



## Reuse

One of the most compelling reasons for object-oriented programming is code reuse.



**Composition**

将依赖的类写为当前类的属性，在合适的地方初始化

- 构造方法前，构造方法中，调用属性前（Lazy Initialization），代码块中

当你想使用某个类的功能（方法）时使用Composition，has-a



**Inheritance**

编译器强制在调用衍生类的构造函数前，完成对基类的构造

Java automatically inserts calls to the base-class **no-args-constructor** in the derived-class constructor.

如果要调用基类的有参构造函数，需要在衍生类构造函数中使用super(args...)



@Override给编译器看的，它来确保你的确Override方法了

当你想要写一个special version of基类时，使用Inheritance，is-a is-like-a



**Delegation**

介于Composition和Inheritance之间，依赖类，并且override所有它的方法



**Upcasting**

基类在上，衍生类在下；参数要求传入Instrument，而调用者传入Wind，就是向上转型

每次在使用Inheritance时，先问问自己是否会用到Upcasting？如果不需要，那可能多的使用Composition

<img src="/Users/cencen/Desktop/截图/截屏2021-10-09 下午3.25.41.png" alt="截屏2021-10-09 下午3.25.41" style="zoom:50%;" />	



**final**

static关键字：保证唯一性

final关键字：保证constant value（无论是primitive value还是reference的指向）

blank final必须在contructor中完成初始化



final方法可以保证方法的唯一性（不能Override），还可以提高效率（inline-call）；不用程序猿手动final method，而是由编译器和JVM完成这部分的优化



## Polymorphism

It provides another dimension of separation of interface from implementation, to decouple what from how.

数据封装让方法和方法实现解藕，多态让接口类与实现类解耦



inheritance enabled you to treat an object as its own type or its base type. a single piece of code works on all those different types equally(在不Override的情况下，继承关注于代码复用)

The polymorphic method call allows one type to express its distinction from another, similar type, as long as they’re both derived from the same base type. This distinction is expressed through differences in behavior of the methods you can call through the base class.



如果没有Inheritance和Polymorphism的向上转型，我们就只能写type-specific methods了，多一个实现类，就要再多写一个方法，完全违反各种程序设计原则。



**Method-Call Binding**

Connecting a method call to a method body is called binding.

想C这种面相流程的编程语言，只允许由Compiler或Linker完成early-binding before 

All method binding in Java uses late binding unless the method is static or final (private methods are implicitly final)

所以final method一大作用就是”turn off“ dynamic binding



**Extensibility**

Due to polimorphism, a program is extensible because you can add new functionality by inheriting new data types from the common base class



## Interface

abstract class could contains abstract methods

@Override用来让compiler检测是否为覆盖方法，也让代码更易读



interface can contains static final fields

Before Java 8, interface only allow public abstract methods, seen as an "protocol" between classes

With Java 8, interface allows both `default` and static methods

但interface的概念始终是更注重"concept of type"，更少的"implementation"



default关键字：当我们想在interface中扩展方法时，为了不影响所有的实现类，使用default修饰它。这种方法也称作`defender methods`



**多继承**

Java无法达到多继承for fields，但通过implement多个拥有default methods的借口，获得多继承的能力。

如果冲突了，可以使用override来解决conflicting，通常通过super选择一个

```java
public class Jim implements Jim1,Jim2{

    @Override
    public void jim() {
        Jim1.super.jim();
    }
}

interface Jim1{
    default void jim(){
        System.out.println("Jim1");
    }
}
interface Jim2{
    default void jim(){
        System.out.println("Jim2");
    }
}
```



##### abstract class & interface

区别：多继承 & 单继承，interface不能拥有non-static field、不能有构造、且只能是public

我们的原则就是，as abstract as possible --> interface first



**总结**

先使用普通class，当interface is necessary，重构！

虽然Interface是一个很好的抽象工具，但是经常被过度使用！



## Inner Classes

一种隐匿类的方式，将类声明在另一个类中

内部类可以引用外部类的成员变量/方法，然后外部类通过方法，创建一个内部类并返回。（类似继承，方法和变量都可以覆盖外部类的，Itr就是这样一个内部类）



内部类方法返回外部类

```java
public class Outer{
    private class Inner{
        public Outer outer(){
    			return Outer.this;
		}
    }
}
```

通过外部类实例获得内部类（如果是private修饰的内部类，在本类外也无法通过此方法访问到）

```java
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();
```

如果不需要Inner Class和Outer Class有任何关系，那么可以将内部类声明为静态



> 内部类也可以存在在方法或者if条件语句中，这样它的scope使用范围也仅限在本方法或者if语句中
>
> 使用outer.this获取外部类属性
>
> 使用outer.new Inner()创建内部类
>
> It’s not possible to create an object of the inner class unless you already have an object
>
> of the outer class. This is because the object of the inner class is quietly connected to
>
> the object of the outer class it was made from. 



**匿名内部类**

一个接口的实现类，但是没有类名，属于一次性使用。

new 抽象类/接口类，实现或者覆盖其中的一些方法。



**内部类存在的核心原因！**

> Each inner class can independently inherit from an implementation. Thus, the inner 
> class is not limited by whether the outer class is already inheriting from an 
> implementation. 从而使Java也可以完成多继承！

例子：LinkedList extends AbstractSequentialList，他的遍历器private class ListItr implements ListIterator，他的节点private static class Node（不需要访问外部类属性/方法，所以static）



通常将内部类设置为private，让它只能在外部类中使用。而且外部类和内部类实例的创建，是分离的。new Outer和 new Inner 无关



## Collections

**java.util类库**

Collection：单独元素的集合

- List：顺序表，通过不同的插入方式控制数组顺序
- Set：无需，无重复元素（Hashset查询快，TreeSet自动排序，LinkedHashSet维护插入顺序）
- Queue：按照队列规则输入输出元素（LinkedList链表双边队列，ArrayDeque数组双边队列，PriorityQueue优先队列）

Map：key-value pair的集合



**utility methods**

Arrays.class & Collections.class



**Iterator**

内部类，自带指针，用来向后遍历数组

List Iterator是子类，更厉害，可以前后移动

> There’s no need to use the legacy classes Vector, Hashtable, and Stack in new code. 

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210928202141.png" alt="image-20210928202127905" style="zoom:67%;" />	



Collection接口继承了Iterable接口，即实现类均需要实现iterator()方法，该方法返回的Iterator对象。

Iterator用来遍历容器，将容器的遍历与容器本身解耦。

实现了Iterable接口的类，均支持for-in增强for循环。



## Functional Programming

OO abstracts data, FP abstracts behavior

In a pure functional langurage, “immutable objects and no side effects”



- 接口定义一个单一方法
- 传入一个与其格式（signiture）相同的方法
  - () -> {}
  - MyClass :: method（甚至可以通过MyClass :: new传入一个构造函数）
- 调用单一方法，即调用一个静态方法，即调用的就是我们传入的方法



The goal of `java.util.function` is to create a complete-enough set of target interfaces that you don’t ordinarily need to define your own.

因为要满足基本数据类型，所以Java原生了好多的函数式接口



when working with functional interfaces, the name doesn’t matter—only the argument types and return type

将方法传入函数式接口后，调用接口方法即可，传入的方法名并不重要



# Stream

有了default关键字，java类库的设计者可以将stream()方法安全的添加到各个interface中，并不影响用户



三种操作：

- 创建stream
- 操作stream元素
- consuming stream，常常是将元素收集成新的集合



**创建**

Stream.of()

Collection对象，stream()

range(10,20)生成一个10-20的IntStream

Stream<T> stream = Stream.generate(Supplier<T>) 如Stream.generate(() -> new Person())



Stream stream = Stream.iterate(0, i -> return i++)：先将0赋值给i并放入Stream，每次将return的值重新赋值给i并放入stream

splitAsStream()



**操作**

map(e -> e.getKey())，输入Object，输出新的Object

mapToInt，输入Object，输出Integer，将stream转换为IntStream（Float和Double也有他们各自的方法）

map.entrySet().stream()，获得stream of entrySet，each with key and value



random.ints(streamSize int)

random.ints(begin,end bound).limit(10)

.boxed()：从IntStream转换到Stream<Integer>



map(), skip(), limit(), peek(), sorted(Comparator)

distinct(), filter()



**终结**

collect(Collectors.joining(" ")) //将Stream<String>，join成一个String

toArray()

forEach(Comsumer)

reduce()：combine all stream elements

allMatch(Predicate), anyMatch(Predicate), noneMatch(Predicate) 返回boolean



findFirst(), findAny() 返回Optianal



count(), max(Comparator), min(Comparator) 返回信息，如果是算数的，就不需要传入Comparator



**其他**

repeat(3,System.out.println("repeat 3 times"))

flatmap()和map()的区别，flatmap可以返回0个或多个value通过往新的stream中添加value

map如果返回Stream.of(i,i+1,i+1)就真的是返回一个Stream对象

而flatmap返回Stream.of(i,i+1)是会将这两个值添加到Stream，返回Stream.empty()就是返回0个值



## Optional

通常用来包装返回值，让函数的调用者不会得到null值，从而避免NullPointerException！

和Stream结合，创建流畅的代码！



**创建**

.empty(), of(value) -->不是null的情况下, ofNullable(value) --> 可能是null的情况下

**拆解**

ifPresent(Consumer)：如果存在的话，调用Consumer，否则什么都不做

orElse(), orElseGet(), orElseThrow()



## Exceptions

Exceptions that are checked and enforced at compile time are called *checked exceptions*.

checked exceptions就是为了确保调用函数的人必须处理Exception！



Throwable：顶级接口，可以被throw

Error：继承自Throwable，compile-time或者系统错误，通常不需要程序员自己catch处理

Exception：继承自Throwable，分为checked和unchecked

- 前者继承自Exception，要求调用者handle exception
- 后者继承自RuntimeException，属于程序bug，通过更改代码来避免，而不是出了错再处理。让代码更整洁。



Java缺点：finally代码块可能会吞掉Exception

- 在finally中抛出其他异常，覆盖try中抛出的异常
- 在finally中return，try中抛出的异常干脆就没了



One of the important guidelines in exception handling is “Don’t catch an exception unless you know what to do with it.” 

In fact, one of the important goals of exception handling is to move the error-handling code away from the point where the errors occur.

- 让逻辑代码与错误处理代码分离
- 减少error-handling code，让一个handler可以处理多个同类型的exception

Checked exceptions complicate this scenario a bit, because they force you to add catch clauses in places where you might not be ready to handle an error. 



try-with-resource

在try()中添加继承了AutoCloseable的对象，就可以不用finally了，try代码块执行完毕后会自动调用其close函数

https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html



## Validating Your Code

先跳过了，等我有一些Testing体验之后，再回来重温



## Files

 These new elements are packaged under java.nio.file,

where the n in nio formerly meant “new” but now means “non-blocking” (io is for

*input/output*). The java.nio.file library finally brings Java file manipulation into

the same arena as other programming languages. On top of that, Java 8 adds streams

to the mix, which makes file programming even nicer.



**Path对象，代表一个文件或者文件夹的路径**，可以传入多个String，避免不同OS的斜杠path separator不同

- 两个相对路径的相对路径p1.relativize(p2)
- actualPath(), normalize()删除冗余路径

**Files工具类**，操作文件和文件夹，分析Path对象

- ```java
  //Visitor设计模式，覆盖方法，并返回是否继续walk through
  Files.walkFileTree(p,new SimpleFileVisitor<>(){
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          System.out.println(file.getFileName());
          return FileVisitResult.CONTINUE;
      }
  });
  
  //不是Visitor模式，返回Stream<Path>
  Files.walk(Path p);
  
  //同样返回Stream<Path>，但是不包括传入的Path本身
  Files.list(Path p)
  ```

- craeteTempFile, createFile(p.resolve("xxx.txt"))

**FileSystem对象，用来查找OS相关文件系统信息**

**FileSystems工具类**

- FileSystems.getDefault().newWatchService()，监视file相关变动
- FileSystems.getDefault().getPathMatcher("glob:*.txt")，获得Matcher来match()路径找File



**读写文件**

List<String> lines = Files.readAllLines() ：一次性读小文件的所有

Stream<String> lines = Files.lines()：通过skip()，limit()等方法选择性的写入，用于大文件

Files.write(Path p , byte[] or Iterable<>)：向指定文件中写入



**study the Javadocs for java.nio.file, especially java.nio.file.Files.**



# Strings

String is Immutable，is read-only

如果将对象与String相加，底层会自动调用对象的toString，所以在对象的toString()方法中`“xxx” + this`会导致死循环



The ‘**+**’ and ‘**+=**‘ for **String** are the only operators that are overloaded in Java, and Java does not allow the programmer to overload any others.



jvm在9之前会调用StringBuilder的append()，不是非常有效率。

在9引入StringConcatFactory用来执行使用“+”拼接String的操作

https://www.youtube.com/watch?v=CzKkz6d6S0A



P724：各种String方法

Java的Formatter.format()等于C的printf()

String.format()内部就是new了一个Formatter



暂时跳过了Regex的部分，操作静态字符串和文件匹配，非常高效！但是可读性堪忧。



## Reflection

It’s important to realize that there’s nothing magic about reflection. When you’re using reflection to interact with an object of an unknown type, the JVM will simply look at the object and see that it belongs to a particular class (just like ordinary RTTI). Before anything can be done with it, the **Class** object must be loaded. 

动态代理：Proxy.newProxyInstance()，proxy对象在执行方法时，交给InvocationHandler类的invoke()来执行

InvocationHandler类包含一个target类，在invoke方法中通过反射来获取它的各种属性，执行它的方法



使用Reflection的get Declared...方法获得不是public的方法和属性，然后setAccess(true)，便可访问之前无法访问的内容了。



**Class类**

一个类对应一个Class文件，运行时被ClassLoader加载到JVM，包装成一个Class对象。当我们在new对象时，就是在调用Class对象的Constructor方法（虽然没有static修饰，构造方法也是一个静态方法）



准备一个可以使用的Class

1. Loading：ClassLoader通过磁盘路径，通过读取到的bytecodes创建Class类
2. Linking：检验bytecodes，给静态属性分配内存空间
3. Initialization：从父类开始，给静态属性赋值，执行静态代码块



获取Class对象的方法

- Class.forName("") 运行时检查，需要处理异常
- Class Literal --> Integer.class，编译时检查所以不用额外throw Exception，不触发Initialization



给Class<>加泛型，限制可以赋值的Class类型

```java
//看似合法，因为Integer是Number的子类，然而Integer Class不是Number Class的子类。Integer可以向上转型为Number，但是两个Class类无法互相转换。
Class<Number> genericNumberClass = int.class;
  
//可以这样
Class<? extends Number> genericIntClass = int.class;
```



instanceof运行时检查类型，判断该类是否为某个类，或者某个类的实现/继承类



## Generic

One of the most compelling initial motivations for generics is to create *container classes* 

也可以在interface上使用<T>，实现类中指定类型，相当于一种Factory Method设计模式。(比如Iterator，并且SpringDataJpa应该就是这样子的)

```java
public interface GenericInterface<T> {
    T get();
}

public class GenericImpl implements GenericInterface<Integer>{
    @Override
    public Integer get() {
        return 1;
    }
}
```



## Arrays

