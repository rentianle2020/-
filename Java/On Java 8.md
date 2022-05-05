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

.empty()

of(value) -->不是null的情况下

ofNullable(value) --> 可能是null的情况下



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

 These new elements are packaged under java.nio.file, where the n in nio formerly meant “new” but now means “non-blocking” (io is for *input/output*). 

The java.nio.file library finally brings Java file manipulation into the same arena as other programming languages. On top of that, Java 8 adds streams to the mix, which makes file programming even nicer.



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

一个类对应一个Class对象，当我们首次调用一个类的静态方法时被载入JVM（虽然没有static修饰，构造方法也是一个静态方法）

Each Class object is loaded by Classloader only when it’s needed

Once the Class object for that type is in memory, it is used to create all objects of that type



准备一个可以使用的Class

1. Loading：ClassLoader通过磁盘路径，通过读取到的bytecodes创建Class类
2. Linking：检验bytecodes，给静态属性分配内存空间
3. Initialization：从父类开始，给静态属性赋值，执行静态代码块



获取Class对象的方法

- Class.forName("") ，运行时检查，需要处理异常
- Integer.class，编译时检查所以不用额外throw Exception，不触发Initialization
- xxx实例.getClass()



Class.newInstance()方法，如今已经被Constructor.newInstance()代替



给Class<>加泛型，用于编译时检查

```java
//看似合法，因为Integer是Number的子类，然而Integer Class不是Number Class的子类。Integer可以向上转型为Number，但是两个Class类无法互相转换。
Class<Number> genericNumberClass = int.class;
  
//可以这样
Class<? extends Number> genericIntClass = int.class;
```



可以用cast(Object o)方法强转参数对象为本类，useful for situations where you *can’t* use an ordinary cast

实例 instanceof Class：运行时检查类型，判断该类是否为某个类，或者某个类的实现/继承类

isAssignableFrom(Class c)，调用方法的类是否为参数类的本类或父类



## Generic

用于给Tuple（DTO）一个/多个Type parameter，然后编译器干活！



**泛型类**

One of the most compelling initial motivations for generics is to create *container classes* 

在类上指定泛型，相当于一种Factory Method设计模式。

with generic class，必须在初始化（new或者继承）时指定泛型类型。

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



**泛型方法**

To define a generic method, place the generic parameter list before the return value

这样就可以在方法参数/返回值上使用泛型了，看起来只是一个普通方法，但其实该方法已经被无限重载了！

Arrays.asList(args...)就是用泛型完成方法重载的。

@SafeVarargs会让编译器去检查方法内是否有对泛型参数不安全的操作，比如强转。

```java
public class Util {
    public static <K, V> boolean compare(Pair<K, V> p1, Pair<K, V> p2) {
        return p1.getKey().equals(p2.getKey()) &&
               p1.getValue().equals(p2.getValue());
    }
}

public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(K key) { this.key = key; }
    public void setValue(V value) { this.value = value; }
    public K getKey()   { return key; }
    public V getValue() { return value; }
}

boolean same = Util.<Integer, String>compare(p1, p2);
```



这不算Java的一个特性，而是一种**妥协**。经过编译擦除，类上的泛型消失，属性和方法上的泛型在没有指定extend/super限制的情况下转换为Object

type annotations such as List<T> are erased to List, and ordinary type variables are erased to Object unless a bound is specified

**Erasure enables this migration towards generics by allowing non-generic code to coexist with generic code**



**泛型擦除**

Class.getTypeParameters()只能得到泛型的占位符如T，K，V

虽然List<Integer>和List<String>在编译前只能插入对应的类型元素，看起来不是一个类，但是在运行时被擦除，所以同属于List类。

C++中，是可以直接在方法中调用泛型实例类的方法；但在Java中会报错，因为泛型擦除后，方法中的Object类找不到泛型实例的方法，除非指定的**type parameter是<T extends Integer>**,这种情况下就可以假设所有泛型实例都有实现Integer方法，就可以在方法体中把泛型参数当作Integer对象来调用方法了。因为擦除后不是转换为Object，而是Integer。



因为泛型擦除，占位符T不能执行 xxx instanceof T，只能传到Class<T> kind属性中，使用kind.isInstance(xxx)来判断类型，还可以用此方法来new泛型对象



Remember *PECS*: **"Producer Extends, Consumer Super"**.

如果要生产一个生产Number的List，使用List<? Extends Number>，就算声明为<Integer>，也可以读出为Number

如果需要一个可以消耗(存入)Integer的List，使用List<? super Integer>，就算声明为<Number>，也可以存入Integer

既不读，也不写，可以使用<?>

如果需要又读又写，就不要使用通配符了，直接在泛型中指定具体类型！



**泛型存在的原因**

作者认为，泛型并不像很多人说的，为了避免我们把猫对象放进狗集合中，这种错误不常见。



声明泛型类和方法时，更加的generic泛泛，让一段代码可以起到多类型兼容的重载作用。

使用带有泛型的类时，用清晰可见的泛型类声明，和避免转换的方式，更优雅的写代码。

https://docs.oracle.com/javase/tutorial/java/generics/why.html



## Arrays

Java provides reasonable support for fixed-sized, low-level arrays. This kind of array emphasizes performance over flexibility

大部分情况我们都应该使用Collections，它具有面向对象，动态扩容等好处，自动装箱/拆箱 & 泛型也让其操作变得异常简单。

除非性能受到了严重影响，可以考虑使用原始数组。



Arrays.setAll(xxx[], index -> index)

Arrays.fill()



Arrays.parallelSetAll(), Arrays.parallelSort() 并行操作，什么时候使用？

http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html



工具方法 Arrays Utilities，p1027



## Enumerations

枚举类是一个有限元素的类，使用enum关键字定义，编译器负责创建这个类，这个类自动继承java.lang.Enum类。

自定义枚举类可以被implement，无法被inherit，可以照常添加方法



**获取枚举类中的所有元素**

test.values()：不是从Enum继承而来，而是 a static method that is added by the compiler

Test[] enumConstants = Test.class.getEnumConstants();



**EnumSet**

内部是一个64bits的long，通过bit vector储存ordinal的方式保存一个Enum类中的元素

主要用来作为bit flags使用



**EnumMap**

本身是一个array，下标是元素在Enum类中的下标

 use EnumMaps for enum-based lookups



**constant-specific method**

用静态内部类的形式定义每个枚举

```java
public enum Test {

    GOOD_INFO("nice"),BAD_INFO("no!"),
    NOT_EXIST_INFO{
        @Override
        String getInfo() {
            throw new IllegalArgumentException("INFO NOT EXIST");
        }
    };

    String info;
    Test(){}
    Test(String info) {
        this.info = info;
    }
    String getInfo(){
        return info;
    }
}
```



Java 1.0中有一个非常不好的混淆的选择，就是定义了一个Enumeration接口，负责将元素从容器中枚举出来。现在被矫枉过来，大家也都会选择使用Iterator遍历容器元素了，但是为了兼容，这个接口也不能被随便删除...



# Annotations

为了追随“metadata置入source-code”的潮流，Java在Java 5中引入的注解

声明注解类 + 创建注解处理器，使用反射来处理注解

注解类无法使用extends关键字来继承



**声明**

一个注解类上需要/可以指定的元注解：

- Target注解目标（类，方法，属性）
- Retention注解可用阶段（编译前，编译后，运行时）

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {}
```



**注解类成员**

允许使用的成员类型：基础数据类型，String，Class，enums，注解类，以上所有的Arrays

基本类型之外的成员不允许为null，没有default value的成员必须在使用是赋值！作者解释说，这么做是为了让注解解释器更好写，不用判断null。

String成员经常需要我们给一个空值 default "" 作为默认值



**javac**

使用编译时处理器来处理注解（继承了AbstractProcessor的自定义Processor）

javac -processor annotations.simplest.SimpleProcessor SimpleTest.java



# Concurrent Programming



### **并发&同步的完整定义**

**Concurrency**

Accomplishing more than one task at the same time. One task doesn’t need to

complete before you start working on other tasks. Concurrency solves problems

where *blocking* occurs—when a task can’t progress further until something

outside its control changes. The most common example is I/O, where a task

must wait for some input (in which case it is said to be *blocked*). A problem like

this is said to be *I/O bound*.

**Parallelism**

Accomplishing more than one task *in multiple places* at the same time. This

solves so-called *compute-bound* problems, where a program can run faster

if you split it into multiple parts and run those different parts on different

processors.



### 什么时候使用多线程

1. 线程可能会block，导致无法继续处理其他任务
2. *event-driven programming*，处理events时不能让用户界面死机

作者的观点：能不要用就不用，除非程序速度受限。有时你以为threaded-safe，很可能时broken的。但你依然需要去学习并理解，因为我们日常使用的web-server都是多线程的。

 Java will never be a language designed for concurrency, but simply a language that allows it.



### **parallel()**

stream中使用

1. 将data split为多个部分同步处理
2. 将结果merge

**什么时候使用？**

如果可以用primitive遍历，会利用Memory Locality。如果用对象指针，就慢了。

默认还是用普通stream()，除非有性能需要。



数组是最好的同步执行容器，它既可以利用locality，又可以轻易的split



### 多线程

Runnable对象：交给ExecutorService来execute()

Callable对象：线程结束返回Future对象，交给ExecutorService来invoke()



Future：通过get()阻塞获取返回值，感觉像是js的await



- SingleThreadExe'cutor：单线程运行tasks，线程安全
- CachedThreadPool：线程池同步运行tasks



race condition：多个task试着改变同一个变量值，最好的方法就是share nothing



**结束线程**

interrupt一个运行线程是错误的模式，catch InterruptedException仅仅为了兼容之前的设计。



 The best approach to task termination is to set a flag that the task periodically checks.

```java
//自己设置一个running，给一个quit()方法结束掉自己
private AtomicBoolean running = new AtomicBoolean(true);

public void quit() {
    running.set(false); 
}

@Override public void run() {
    while(running.get()) // [1]
        Thread.sleep(1000);
    System.out.print(id + " "); // [2]
}

public static void main(String[] args) {
    ExecutorService exec = Executors.newCachedThreadPool();
    List<Test> tasks =
            IntStream.range(1, 150)
                    .mapToObj(Test::new)
                    .peek(exec::execute)
                    .collect(Collectors.toList());
    tasks.forEach(Test::quit); //结束
    exec.shutdown();
}
```



**CompletableFuture**

Future缺陷

- get()会阻塞主线程，如果多个Runnable同时运行，我们get()哪一个来确保任务完成？而第一个get()的也不能确保是最先完成的（有可能其他任务都完成了，主线程还在阻塞等待第一个任务）
- 不支持链式任务执行，必须get()拿到结果，再把结果包装成下一个任务，再get()

我们想要的

```java
int main(){
    for(i in n){ //同时运行多个线程
    	Run the task;
    	Once done, run its dependent task;
    	Once done, run next depentdent task; //每个线程在完成上一步后，链式调用下一步
    	...
	}

	//Don't bother main Thread
}
```



CompletableFuture是java8引入的更强力的Future，它实现了Future接口，提供了更全面的功能

1. 传入对象或初始方法，开启新的线程
2. 再该线程上，通过thenApply等方法执行方法
3. 可以在不干扰main和其他线程的情况下，链式调用thenApply等方法

thenApply() applies a Function that takes an input and produces an output



**BlockingQueue**

blocks(waits) if you call take() and the queue is empty



**死锁**

写一个哲学家问题

死锁的4个必须满足的条件

- 对不可共享资源的争夺（筷子）
- 手里拿着资源，同时请求别人手里的资源（拿一只筷子，需求另一只）
- 不能强行掠夺资源（哲学家都很礼貌）
- 出现死循环（每个哲学家都拿着一只筷子，等着另一只）



> Parallel streams and CompletableFutures are the most well-developed techniques
>
> in the Java concurrency toolbox. You should always choose one of these first. The
>
> parallel stream approach is most appropriate when a problem is *embarrassingly*
>
> *parallel*, that is, when it is trivially easy to break your data into identical, easy-to
>
> process pieces (when doing this yourself you must roll up your sleeves and delve
>
> into the Spliterator documentation). CompletableFutures work best when the
>
> pieces of *work* are distinct. CompletableFuture seems more task-oriented than data
>
> oriented



### Low-Level Concurrency

Thread程序员自己控制 -> Executor交给线程池控制 -> CompletableFuture自带线程池和更多的机制



**创建Thread**

JVM为其分配空间（线程私有的program counter、stack1、stack2 for native code, thread-local, JVM通过内部状态管理Thread）

将Thread注册到OS，以获得CPU时间片。



**最佳线程个数**

Runtime.getRuntime().availableProcessors() 看一下最多运行线程数量

我的电脑，8核，共16个hyperthreads逻辑处理器（基于CPU快速context switching的硬件技术，在不增加处理能力的情况下，能将1个处理器当2个用）



**捕获异常**

继承UncaughtExceptionHandler接口，设置默认捕获类Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());

CompletableFuture可以更简单优雅的捕获异常！



# synchronized

多个Threads共享同一个对象状态，对于对象状态的更改会导致互相影响，也就是“非线程安全”。



**每个对象自带一个锁（也叫monitor）**，使用synchronized给方法上锁（这也是为什么我们需要将变量作为private，通过方法访问），其他Thread则需要等待锁释放。



同一个Thread可以对一个对象多次上锁，也叫”重入“。JVM会记录上锁次数，从0开始，上锁++，释放--，直到归0则视为彻底释放锁。



**每个Class也自带一个锁（对于Class对象）**，使用synchronized给静态方法上锁



什么使用使用synchronized？当你需要更改一个变量，该变量需要被另一个线程读取时，或反之亦然。



### volatile

Java中最难的关键字，可以在现代Java中避免使用；如果见到，保持怀疑。



**使用场景**

1. Word Tearing

   在32位操作系统上(64位也可能)，当数据类型为64bits（long & double），会分解为2个32bits分别写入，所以可能导致读写不一致。刚写了一般，上下文切换，另一个Thread读到错误的输入。



1628
