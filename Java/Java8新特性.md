# Java8新特性



**接口默认方法**

在接口上定义默认方法Default修饰，即可在接口中实现默认方法

JDK8之前，接口中是不允许存在方法实现的，只能有静态常量属性和方法



**函数式接口**

方法是属于某一个对象的，而函数是独立存在的

传参只能传递对象，而不是方法



匿名内部类：new了一个没有名字的实现类



只有一个抽象方法的接口，称为函数接口；虽然是一个类，但是可以当作函数使用

new出一个匿名内部类，就是为了它的这一个方法

通过() -> {}表示对这个类的唯一方法的实现

()中放参数，{}放方法体，看起来就是一个函数，而不是一个类了！

如果只有一行，大括号可以不写，如果这一行就是返回值，则return也可以不写！

如果只有一个参数，小括号也可以省略

本质上就是一个匿名内部类，但是将类的概念转换为函数了，优雅！



@FunctioanlInterface 注解，起到一个告知他人的作用，不加也是函数式接口

只能有1个未实现的方法，但是可以有多个实现了的default默认方法



函数式编程中，实现使用的变量，必须是final修饰的



**内置函数式接口**

Comparator、Runnable



Predicates断言型接口，传入类型，返回布尔值，用于判断

Functions函数式，传入一个值，返回一个值

Suppliers生产者，只有一个get()方法，不需要任何参数，但是能返回相应的结果

Consumer消费者，给一个参数，然后把参数“消费”了，不给返回

Comparators，比较式接口



**Stream**

中间操作返回Stream本身，最终操作返回最终想要的返回值

对List的各种操作



**并行流**

parallelStream，多线程并行计算



**Date API**

Clock类，clock.systemDefaultZone()

ZoneId类，时区

LocalDate.now(ZoneId.of("America/Panama"))，获得巴拿马的时间

DateTimeFormatter.ofPattern()构造一个日期格式化

localDate.format(dateTimeFormatter);



