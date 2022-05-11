

# 管理类（高频，难度中）

- Parking lot、Restaurant、Library、Hotel...

这些系统都有管理员，而我们需要做的就是用软件替代管理员。

从系统管理员的角度出发，不要去考虑被管理者的视角。

Reserve预定，Serve接受服务，Checkout结账



**ParkingSystem**

- Clarify

  What：Parking lot（单层/多层），Vehicle（占位大小），Parking Spot（普通/充电桩/残疾人/）

  How：如何停车（进来一个车，给他一个位置让他去停，显示空位数），Fee（根据时间收费）

> 不要将静态的类和动态的类进行组合（如：Spot有一个Car属性 | ParkingLot有List<Car>属性），unnecessary dependency！
>
> 可以将静态的类进行组合，比如ParkingLot有List<Spot>

![image-20220507215939437](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20220507215939.png)



### Singleton

保证单一的类实例，且每次访问都访问到这个实例。

方式：private构造函数 +静态内部类的静态属性 +  public getInstance()；保证够懒，保证单例！