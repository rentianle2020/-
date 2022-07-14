### ArrayList

底层数组，随机访问

动态扩容，默认size=10，每次扩容乘1.5

可以调用ensureCapacity(n)手动扩容到一个能容下n个元素的size



### HashMap



**红黑树转换**

HashMap默认采用拉链法处理哈希碰撞

当链表过长，为了平衡遍历开销，链表自动转换红黑树

转换红黑树时机：single bucket size >= 8(`TREEIFY_THRESHOLD`)，total bucket >= 64 (`MIN_TREEIFY_CAPACITY`)；如果不满足total bucket >= 64，就会先扩容，而不会继续treeify

```java
final void treeifyBin(Node<K,V>[] tab, int hash) {
        int n, index; Node<K,V> e;
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY) //64
            resize();
            ...
}
```

转换回链表时机：扩容时，调用split()拆分红黑树，single bucket size <= 6(`UNTREEIFY_THRESHOLD`)

```java
final void split(HashMap<K,V> map, Node<K,V>[] tab, int index, int bit) {
    TreeNode<K,V> b = this;
    // Relink into lo and hi lists, preserving order
    TreeNode<K,V> loHead = null, loTail = null;
    TreeNode<K,V> hiHead = null, hiTail = null;
    int lc = 0, hc = 0;
        ...

    if (loHead != null) {
        if (lc <= UNTREEIFY_THRESHOLD)
            tab[index] = loHead.untreeify(map);
        else {
            tab[index] = loHead;
            if (hiHead != null) // (else is already treeified)
                loHead.treeify(tab);
        }
    }
		...
}
```



**扩容机制**

自动将array length初始化在2的n次方，每次扩容乘2，用于计算index时当作mask

> length = 16, hashcode()返回10 = 1010，index = (16 - 1) & 10 = 1111 & 1010 = 1010
>
> 如果length = 17，index = 10000 & 1010 = 0，完全没有利用上hashcode()来得出迥异的index

一般意义上，应该使用prime number作为length，以减少哈希碰撞；

HashMap作为动态扩容的哈希表，为了减少findNextPrimeNumber()的开销，并没有这么做；为了避免差劲的Object.hashcode()实现，它会进一步通过hashcode & hashcode>>>16对hashcode进行加工

https://stackoverflow.com/a/15437377



size元素个数

load factor扩容因子

threshold扩容阈值：capacity * load factor



**非线程安全**

需要线程安全的哈希表，就使用ConcurrentHashMap

ConcurrentHashMap的并发控制使用 synchronized 和 CAS 来操作。（往不为空的bucket中添加新的节点时，synchronized头节点来完成插入）

```java
//自选初始化table
private final Node<K,V>[] initTable() {
    Node<K,V>[] tab; int sc;
    while ((tab = table) == null || tab.length == 0) {
        //如果 sizeCtl < 0 ,说明另外的线程执行CAS 成功，正在进行初始化。
        if ((sc = sizeCtl) < 0)
            // 让出 CPU 使用权
            Thread.yield(); // lost initialization race; just spin
        else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
            ...
        }
    }
    return tab;
}
```



Synchronized 锁自从引入锁升级策略后，性能不再是问题。

TODO://锁升级



### 集合遍历

Java集合有两种Iterators

- Fail Fast（ArrayList）

  为了保证遍历过程中，集合没有被别的线程更改，内部维护modCount变量

  当check next value时，发现currentModCount != initialModCount，则抛出异常

- Fail Safe（CopyOnWriteArrayList）

  Iterator内部保存一份copy，对集合的修改不会影响Iterator遍历的对象

  ```java
  static final class COWIterator<E> implements ListIterator<E> {
          /** Snapshot of the array */
          private final Object[] snapshot;
  }
  ```



### 集合转换



**集合转数组**

参数new Integer[]是为了说明返回类型，所以大小声明为0来节省空间

```java
List<Integer> list = new ArrayList<>();
list.add(1);
Integer[] integers = list.toArray(new Integer[0]);
```



**数组转集合**

使用Arrays.asList()转换后，得到的是Arrays类中的静态静态类ArrayList

继承于AbstractList，但并没有重写add(), remove()方法，调用会报错

```java
Integer[] ints = new Integer[]{1,2,3};
List<Integer> list = Arrays.asList(ints);
list.add(4);

/* Exception in thread "main" java.lang.UnsupportedOperationException
	at java.base/java.util.AbstractList.add(AbstractList.java:153)
	at java.base/java.util.AbstractList.add(AbstractList.java:111)
	at fun.tianlefirstweb.Test.main(Test.java:12)
```

更好的两种转换方法

```java
//直接new ArrayList
List<Integer> list = new ArrayList<>(Arrays.asList(ints));
//使用Stream
List<Integer> list = Arrays.stream(ints).collect(Collectors.toList());
```

