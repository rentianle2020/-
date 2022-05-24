# 实物类OOD（中高频，中低难度）

- Vending machine，CD Player，Coffee maker，ATM machine



- 考虑对于实物的输入/输出（Coffee bean, Coffee maker, Coffee)

- Design pattern的运用（State pattern，Decorate pattern，Factory pattern）



### **Vending Machine**

**Clarify**

- What：Payment（不同付款方式&面值，Strategy pattern）， item（sold out？refill？）
- How：input to select item(String: A1 A2...)

**Core Object**

Input：Coin

System：VendingMachine

Output：Sprite，Coke，MountainDew

**Use Cases**

- 选择物品：
  1. 输入一个字符串String，如“A1”
  2. 返回静态商品信息（价格）ItemInfo，通过ItemInfo获取List<Item>判断是否还有剩余，然后返回价格
  3. 或者直接返回List<Item>，查看是否为空，然后返回价格
- 插入硬币
- 处理交易
  1. 获得当前选择的物品
  2. 对比物品价格和插入的硬币
  3. 如果钱不够，抛异常 / 如果够了就直接返回item
  4. 找零
- Cancel transaction

![image-20220518212836382](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20220518212843.png)



**State Design Pattern**

使用场景：当一个类会根据它当前状态的不同，执行不同的行为；且state非常多，经常切换时，建议使用！

一个State Interface和多个实现

Person把他的方法全部delegate到state.方法()

Person有初始化的State，State中包含current Person，可以根据方法改变当前Person的State

```java
interface State {
    void say(String sentence);
    void sleep();
    void wakeup();
}

class AwakeState implements State{

    private Person person;

    public AwakeState(Person person) {
        this.person = person;
    }

    @Override
    public void say(String sentence) {
        System.out.println("我说:" + sentence);
    }

    @Override
    public void sleep() {
        System.out.println("切换到sleep");
        person.changeState(new SleepState(person));
    }

    @Override
    public void wakeup() {
        System.out.println("已经是awake的了!");
    }
}

class SleepState implements State{

    private Person person;

    public SleepState(Person person) {
        this.person = person;
    }

    @Override
    public void say(String sentence) {
        System.out.println("我现在sleep, can't say!");
    }

    @Override
    public void sleep() {
        System.out.println("已经在睡了!");
    }

    @Override
    public void wakeup() {
        System.out.println("切换到awake");
        person.changeState(new AwakeState(person));
    }
}

class Person {

    private State state;

    public Person() {
        this.state = new AwakeState(this);
    }

    public void changeState(State state){
        this.state = state;
    }

    public void say(String sentence){
        state.say(sentence);
    }

    public void sleep(){
        state.sleep();
    }

    public void wakeup(){
        state.wakeup();
    }
}
```



### Coffee Maker

Input：Coffee pack

Output：Coffee

Use Cases：

- Brew：提供一个CoffeePack，返回一个Coffee



**Decorator Design Pattern**

使用场景：给对象增加额外的属性，但并不改变对象本身，而是使用wrapper包裹它，然后调用它的函数/改变它的属性

```java
interface Coffee {

    double getCost();
    String getDescription();
}

class Americano implements Coffee{
    @Override
    public double getCost() {
        return 1.5;
    }

    @Override
    public String getDescription() {
        return "Americano";
    }
}

abstract class CoffeeDecorator implements Coffee{

    protected Coffee coffee;

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

class Sugar extends CoffeeDecorator{

    public Sugar(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 0.5;
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ",Sugar";
    }
}
```



### Design Kindle

Input：

Output：

Use Cases

- Upload book
- Download book
- Read book（with different fomat)
- Remove book



**Factory Pattern**

将生成对象的if...else...判断，从当前类放到Factory类中，当前类只需调用Factory的方法，即可获得对应的类

