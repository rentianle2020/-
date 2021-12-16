# CS61B



**Java**

8个基本数据类型+1个对象类型

参数传递永远都是pass by value，数组&对象传递reference值，基本数据类型传递value值

Restrict Access：暴露用户需要的接口，让我们可以更改private methods实现而不影响用户使用



**链表**

快速让链表访问last节点，变为使用两个sentinel节点让其变为双向链表，保持一个sentinel然后让其转换为循环链表，first.prev = last, last.next = first

使用generic，延迟链表元素类型的声明时间到创建对象时



**数组**

随机访问

Arrays，同样类型的元素，固定的大小

二维数组就是声明一数组的指针，默认都是null，每个指针可以指向一个一维数组





