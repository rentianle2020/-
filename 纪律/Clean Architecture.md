# Clean Architecture

architecture架构 & design设计 虽然通常代指“高层级” & “低层级”，但他们其实是不分家的。

所有细小的设计，共同撑起了架构的设计！



### 编程范式（Paradgms）

范式与语言无关，他们规范了我们的代码结构。

3大范式在1958-1968间出现，no more paradigms have been added.



Structured Programming：流程控制语句。discipline on direct transfer of control

OOP：封装、继承、多态。discipline on indirect transfer of control（通过对象调用方法）

- 封装

  - 使用public/private等修饰将对外提供的接口与内部实现分离

  > C语言提供更严密的封装，使用linker将接口与实现类链接，调用者依赖接口，完全无法看到内部实现。
  >
  > 而OOP语言如Java而是使用private等访问控制关键字，调用者能看到内部实现，但是无法访问。

- 继承

  - 建立基类&衍生类之间的关系，避免重复代码，主要用于在基础上override

- 多态

  - 允许程序员面向基类 & interface编程；利用dependency inversion，轻松解耦high-level model & low-level model

FP：将可变与不可变分离，descipline implosed upon variable assignment.

- 源自于“范畴论”，函数就像数据的管道(pipe)。通过箭头，一头输入一个值，另一头出来一个新的值。

  pure function with no side effect



**总结：C语言本身其实可以实现封装，继承，多态；但是OOP使实现更容易，更强壮。**



## 设计原则

SOLID principles tell us how to arrange functions & data structrues into classes



Create "mid-level" software structure that: 

- Tolerate change
- Easy to understand
- Components can be used in many software systems

mid-level: 模块结构的设计，比code级别高，比architecture级别低



#### SRP: The Single Responsibility Principle

A module should be responsible to one, and only one actor.

每一个类应该只为了实现一种功能而引入，所以每一个方法也应该只因为一个原因而被调用

private function的封装不能同时被两个不同的actor调用，导致其中一个需要更改时，另一个被迫要出问题

每一个类应该只有一个职责，不能又当爹又当妈



#### OCP: The Open-Closed Principle

closed to modification, open for extension

对于单个class而言，可以在不改动它代码的情况下，通过更改依赖实现类来完成extension。



