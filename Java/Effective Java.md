# Effective Java



## 第1章 对象导论

Grady Booch：对象具有状态、行为和标识

**将对象视为服务的提供者**，高内聚（每个对象都可以很好的完成一件事，并不视图做过多的事）



多态

```java
interface Person{
    void doSomething();
}

class Tyler implement Person{
    void doSomething(){
        //...
    }
}

class Test{
    public static void main(String[] args){
        Person person = new Tyler();
        person.doSomething();
    }
}
```

编译时：你是一个Person，你可以doSomething，那么去做吧，但是要注意正确性

运行时：去调用Tyler的doSomething



**单根继承结构**

所有的类都继承自Object基类

保证了所有对象都具备某些功能



**容器**

List、Map、Set、队列、堆栈、树...每种容器提供不同的接口方法

面向容器接口编程（ArrayList & LinkedList 都实现 List 接口），将容器实现类切换时产生的影响降低到最低



**泛型**

Java SE5以前，容器存储的对象均为Object

放入时向上转型为Object，丢失原本身份

取出时向下转型为确切对象，十分容易出错

反射机制的加入，让编译器可以自动识别容器中的类，加入时验证，取出时自动检测。



**对象的创建和生命期**

程序运行时，堆动态的分配内存给新创建的对象；垃圾回收器负责内存的释放



**异常处理**

Java内置了异常处理，而且强制程序员使用它

异常处理并不是面向对象的特征（尽管异常也会被表示成一个对象），它在OOP语言出现之前就存在了！



## 第2章 一切都是对象

方法名和参数列表合起来称为“方法签名”，唯一的标识出某个方法



static字段对每一个累来说只有一份存储空间，而非static字段则是对每一个对象有一个存储空间



java.lang是默认导入到每个Java文件中的



javadoc采用了Java编译器的某些技术，提取/**注释和于其相邻的类名/方法名，输出一个HTML文件



## 第3章 操作符

把等号右边的值复制给左边，必须有一个物理空间存储右边的值

而对象，则是将右边的**引用（地址？）**复制给左边



Random对象生成随机数，在没有任何参数的情况下，Java会将当前时间作为随机数生成器的种子



短路：一旦能准确无误的确定返回值，则直接返回，不再计算表达式的剩余部分



## 第4章 控制执行流程

foreach可以用于任何Iterable对象

goto是Java的一个保留字，但是Java没有goto（未在语言中使用它）



## 第5章 初始化与清理

重载：同名方法，且每一个方法都有独一无二的参数列表（参数类型，参数顺序）

如果编译器可以通过语境，就能以返回值区分方法；可惜函数不一定非得将返回值赋值给另一个对象，所以编译器通过返回值无法区分函数



this：在方法内部获得对象的引用，this和对其他对象的引用并无不同；如果要将自身传给外部方法，就传入this

```java
Class Bomb{
    public static void getDefused(Hero hero){
        hero.defuse(this);
    }
}

Class Hero{
    public void defuse(Bomb bomb){
		//defuse the bomb
	}
}
```

this()：在构造函数中，调用另一个构造函数，一个构造中只能调用一次！



static：可以在没有创建对象的情况下，直接调用static方法；属于全局的方法！具体它是否真的“面向对象”，交给理论家讨论吧~

finalize()：正常情况下用不到。在对象终结是被调用，可以在其中判断一下对象死时的各种属性是否符合要求之类的...



成员变量初始化：无论他们散布在类的哪个地方（当然最好都定义在前边），都会在任何方法（包括构造器）调用前得到初始化

静态变量初始化：在类对象第一次被创建 或 该类任意静态变量第一次被访问时，才会被初始化！静态代码块同理

对象创建时的执行顺序：类加载器xxx.class --> 堆上分配内存空间 --> 设置默认值 基本0/0.0/false 引用null --> 初始化静态变量 --> 初始化成员变量（int i = 100) --> 执行构造函数



枚举（Java5新特性，功能比C/C++更完备）

初始化枚举类时，编译器会自动添加一些有用的方法

因为switch实在有限的可能值集合中进行选择，枚举与它是绝配！



## 第6章 访问权限控制

**package** xxx.xxx.xxx/xxx.class：相同包路径下，类名不能重复，所以每个公司都拿自己域名作为包路径。

如果一个类中**import**相同类名的类，需要使用全限定类名

如：java.util.Vector v = new java.util.Vector()



**成员权限**

public：均可访问

默认：包内访问（关键字package相同）

private：你无法访问

protected：继承访问 || 包内访问



**类访问权限**

每个编译单元（文件）只能有一个public类，且必须完全与文件名相匹配

类也可以是默认（包内访问），也可以有静态内部类



**访问控制的原因**

- 封装客户端无需关心的内部实现

- 将实现和对外接口分离，类库程序员更改内部实现，调用者仅仅依赖接口



## 第7章 复用类

组合：has-a关系，将内部成员设置为private，可以灵活的对其进行修改

继承：is-a关系则覆盖方法，is-like-a关系则扩展方法

代理：引入一个a类，写一个x()方法，其中调用a.x()



**继承**

构造继承类之前，基类会被率先构造

默认调用基类的无参构造函数

如果想要调用一个带参数的基类构造器，需要显示的使用super(i)



**final关键字**

变量

- 编译时常量 static final

- 运行时初始化后不希望被改变的值（对于对象，这个值是引用）

方法

- 方法锁定，继承类无法覆盖s
- 出于设计考虑，确保继承中的方法行为保持不变
- private方法都隐式的指定为final

类

- 禁止继承，其中的方法也都隐式的指定为final的

> Vector中都是final，很难继承；最讽刺的是，java自己竟然还用Stack继承了Vector（Stack is a Vector???）；各种synchronized方法，让他的final方法效率极低。被ArrayList替代
>
> HashTable没有final，代码极简，需要使用者大量的扩展和自定义方法。被HashMap替代



 **static** field or **static** method is accessed --> 类加载：main方法是一个静态方法，该类被加载 --> 发现他有基类，加载 --> 发现还有基类，加载 --> 从最高的基类开始初始化静态变量



## 第8章 多态

 polymorphism (also called dynamic binding or late binding or run-time binding)



是否动态绑定不由程序员管理，因为除了被static或final修饰的方法，Java中所有其他方法都是动态绑定的！

因为static和final修饰的方法，全局中都只有一份，不用动态绑定



polymorphism is an important technique for the 
programmer to **“separate the things that change from the things that stay the same.”** 

为什么多态解耦，什么究竟是解耦？这句话就是精髓

不变的是接口对外提供的方法，变的是实现类。多态实现了接口类与实现类的解耦，允许我们面向接口编程，在给定（不改变的）一个参数是接口类的函数传入任意其实现类，依然work。



直接访问类的属性不会存在多态，因为实例化子类后，其中存在两个同名变量 —— 自己的和基类的

Father.field & Son.field 用来分别指向不同的变量值，不支持多态。

静态方法不存在多态，因为它和class绑定，而不是实例，无需通过判断实例来调用方法。



由于调用子类的构造会率先调用父类的，父类构造中调用方法，也会产生多态（动态绑定为子类方法），然而这时候子类构造还没有被调用，变量都是默认值（0/0.0/null），所以会出现奇妙的现象。



子类覆盖方法时，返回值允许是父类方法的子类。父类方法返回Father，子类覆盖的方法可以返回Son。同理，父类方法是protected，子类覆盖的方法可以是public。遵循了父类指定大规则，子类可以在此之上细化方法和权限，但是不能违反规则，否则多态就会出问题



属性采用向上找的理解，如果this.xxx能在子类中找到，就用，如果找不到，就向上找基类的属性。所以在子类没有同名属性的情况下，this.i和super.i指向的都是父类的属性



向上转型安全，因为基类不可能比子类有更多方法。向下转型需要先instance of验证，否则实例无法转的话ClassCastException



## 第9章 Interface

抽象类和接口类，提供了一种结构，让接口和实现解耦。

抽象类可以有，也可以没有抽象方法

接口类全是抽象方法，可以看作是给它的实现类制定的protocol

然而在Java8中，default关键字出现，接口类中也可以定义具体的方法实现了！



## 第10章 内部类





## 第11章 Holding Your Objects 容器



添加泛型后的ArrayList<Apple>：Compiler will prevent you from putting an Orange into apples, so it becomes a compile-time error rather than a runtime error. 







## Functional Programming













## Reflection

...