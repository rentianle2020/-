# OOD介绍

OOD考察综合素质，注重Viability实现功能，Ex. Design Elevator System

System Design需要处理大量数据，强调Scalability扩展性：Ex. Design Twitter



OOA(Analysis), OOD(Design), OOP(Programming)



### **面试注意事项**

- 不要自说自话，侃侃而谈；先和面试官交流，获得更多的题目信息；对于分不清题目要求的，先问清楚是OOD，System Design，还是Algorithm
- 不要反复修改代码，先呈现一个Viable可用的成果
- 不要虎头蛇尾，使用use case去测试OOD



### **评判优劣**

通过查看OOD是否满足SOLID原则，来评判优劣

- Single responsiblity principle：一个类有且仅有一个去改变它的理由（只干一种工作）
- Open close principle：对扩展开放（代码面向抽象，复用性高），对修改封闭
- Liskov substitution principle：任何一个子类可以替代它们的父类（不应该强迫一个类去继承有用不上方法的父类）
- Interface segregation principle：同上（不应该强迫一个类去实现一个用不上的接口）
- Dependency inversion principle：抽象不应该依赖于具体实现，具体实现应该依赖于抽象



### **5C解题法**

*Can u design an elevator system for a building?*

**Clarify**

What

- Elevator，找关键属性（电梯重量？我们需要设计超重检测；货梯/客梯的不同？我们需要设计不同的类）
- Buidling，每层有多少个电梯入口？

> 不影响类图设计的通用属性对题目帮助不大（比如楼有多高，多大，楼内有多少人）

How

提出一些解决方案，看面试官反应；是否能够按反向楼层？按下按钮之后哪个电梯先响应？（同向>静止>反向）

Who（Optional）

设计由人主导（手动干涉） VS. 设计由系统主导（自动处理）

**Core objects**

以一个Object为基础，线性思考。

ElevatorSystem

- Request
- -List<Elevator>
  - -List<ElevatorButton>

**Cases**

Use cases，列举每个Object可能对应的情况

ElevatorSystem：Handle request

Elevator：Take external request，Take internal request，Open gate，Close gate，Check weight

> 因为都是电梯本身的职责，所以不违反SRP

ElevatorButton：Press button

**Classes**

类图UML：Class Name，Attributes，Functions

可交付，如果时间允许/面试官要求，便于转换代码

Access modifier：public + , private - , protected # , default（package可见，避免）

**Correctness**

- Validate use cases
- Follow good practice（Access modifier，继承，Exception）
- SOLID
- Design pattern



### Strategy Pattern

为不同的算法建立不同的类，通过组合的方式与调用算法的类所结合，根据不同的情境，去setStrategy

![Route planning strategies](https://refactoring.guru/images/patterns/diagrams/strategy/solution.png)	

