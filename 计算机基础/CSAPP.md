Chapter 1 ：通过追踪Hello World程序的运行，来介绍计算机体系中的主要概念
**Chapter 2 ：计算机数字运算**
**Chapter 3 ：机器语言（教你阅读C编译器生成的机器语言）**
Chapter 4 ：基础逻辑元件，处理器结构
Chapter 5 ：介绍能够提高代码效率的技巧，如何通过C语言使编译器编译出更高效的机器语言
**Chapter 6 ：内存层次，不同的存储装置，with不同的容量、价格、处理次数**
Chapter 7 ： Linking
**Chapter 8 ：Exceptional Control Flow**
**Chapter 9 ：虚拟内存，如何运作，有什么特点。**
Chapter 10 ：操作系统IO，Linux I/O
Chapter 11 ：网络编程，最有意思的IO形式
**Chapter 12 ：Concurrent Programming，多线程编程**



### Chapter 1 计算机系统漫游

**一个使用C语言编写的Hello World文件的运行过程**

作为一个高级语言（可被人类阅读），在运行时需要被翻译成一系列低级的机器语言指令



![image-20210603140026068](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603140026068.png)

Linux> gcc -o hello hello.c	将源代码hello.c编译成可运行文件 hello




四个阶段：

1、Preprocessing phase，cpp根据’#’，将对应的头文件读入程序，hello.i
2、Compilation phase，编译器将.I文件编译为汇编语言，hello.s
3、Assembly phase，assembler将.s文件翻译为机器指令，hello.o
4、Linking phase，将需要的库，比如C library中的printf函数，和本程序合并，形成hello可运行文件



学习编译系统的原因？
1、优化程序性能（第3章）
2、了解链接时出现的错误 link-time errors（第7章）
3、避免安全漏洞（第3章）



现在我们已经将源文件编译为可运行文件了
想要在Unix操作系统运行这个hello文件，我们需要在一个叫shell的程序中打出文件名（第8章 shell程序的实现）

The shell is a command-line interpreter that prints a prompt（提示符）, waits for you to type a command line, and then performs the command. 



**计算机硬件组成**

Buses：总线，在不同组建之间传输bytes信息。每次传输的bytes大小是固定的，称为word，4 bytes（32bits）/ 8 bytes（64 bits）

I/O Devices：输入输出设备，与IO总线相连（第6章，第10章）

Main Memory：主存，物理上，主存中存储着一系列DRAM（dynamic random access memory）。逻辑上，主存是bytes数组，每个byte有对应的address（第6章）

Processor：CPU，简称processor，用来执行/翻译主存中的指令。（第4章，CPU是如何实现的）

- 其核心有一个word-size的寄存器，叫做program counter（PC）；它无时无刻的指向（存储地址）主存中的机器语言指令
  
- register file 寄存器文件，word-size寄存器，每个有不同名字；临时存放数字的空间
  
- ALU，算数/逻辑单元，用来处理运算 

  ​	

运行hello程序

我们输入./hello + enter

shell将hello程序从硬盘 --> IO bus --> 内存（DMA技术，不经过CPU，直接磁盘到内存）

CPU开始执行hello程序中的机器语言指令 --> 显示

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603140431761.png" alt="image-20210603140431761" style="zoom:50%;" />	<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603140443798.png" alt="image-20210603140443798" style="zoom:50%;" />



**Cache高速缓存的重要性（第6章）**

问题：

- 计算机经常需要移动数据（从disk到main memory），信息搬运减缓了程序的执行速度
- processor从register file读取快，从主存读取太慢。（processor-memory gap）

为了解决这个processor-memory gap，系统设计师发明了cache memories用来暂存信息

实际上，有一层层的cache，L1 cache、L2 cache、L3…

> 上一层存储设备，是下一层存储设备的高级缓存



**操作系统管理硬件**

hello程序运行时，我们没有直接访问硬件设备，而是依赖于操作系统

操作系统的目的：防止程序误操作硬件 & 给程序统一提供操作硬件的接口

通过多层抽象来实现这个目的：进程，虚拟内存，文件



Process进程：屏蔽掉了其他系统，仿佛只有程序本身在运行（第8章）

运行程序时，若需要操作系统一些操作，执行system call，将控制权交给kernel（比如文件读写操作）

Threads线程：运行在进程的上下文中，共享代码和数据

Virtual Memory虚拟内存：给进程提供假象，仿佛每个进程都在独自占用整个内存空间，每个进程看到的内存都是一样的；程序运行时，从disk加载到虚拟内存（第9章）
虚拟内存中的空间分配：Heap、Stack、Shared libraries、Kernel memory...

File文件：一切皆为文件；bytes序列 IO（第10章）



网络提供了计算机之间交流的通道。可以看作为另一种I/O设备

在ssh看客户端输入hello，发送到服务器，接收到后给到shell程序，加载，运行结果给到服务器，结果再发回ssh客户端（第11章，创建一个简单的web服务器）



Amdahl‘s Law

可加速部分加速，不可加速部分维持；通过加速大部分组件，加速计算机计算



加速方法：

多核处理multi-core共享同一个主存；从不同的缓存中读写，同时处理数据

并发Concurrency指的是同时运行多个活动的一个概念

同步parallelism指的是用利用并发，让程序运行的更快

3个不同层级的同步

- Thread-Level Concurrency 程序的并发
- Instruction-Level Parallelism 更低层的抽象，CPU可以同时执行多个指令
  2-4 instructions per clock cycle
- Single-Instruction Parallelism 最低层抽象，在硬件层面，允许CPU执行1条特殊指令后，多个操作被执行



**抽象对于计算机系统的重要性**

指令集—>对真实CPU的抽象，让我们可以通过机器语言控制CPU执行指令

操作系统：

文件files —> 对I/O设备的抽象

虚拟内存virtual memory —> 对主存和硬盘的抽象

进程processes —> 对于运行程序的抽象（CPU、主存、IO）

虚拟机virtual machine —> 对于整个计算机系统的抽象，包括操作系统、处理器、以及程序



### Chapter 2 

&&、||、！是用来做逻辑运算的，而&和｜是用俩做位运算的



**信息的存储**

通常情况下，程序将内存视为一个非常大的数组，数组的元素是由一个个的字节组成，每个字节都由一个唯一的数字来表示，我们称为地址（address），这些所有的地址的集合就称为虚拟地址空间（virtual address space）。

我们常使用16进制来表示4位2进制（的数据和地址），简洁且方便

word size字长，决定了虚拟地址的范围

64 bit —> 0至2^64 - 1 （向后兼容，可以运行32-bit program）

> Memory is a big array of bytes, each byte has an address

<img src="https://img-blog.csdnimg.cn/20210506160415617.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI5MDUxNDEz,size_16,color_FFFFFF,t_70" alt="在这里插入图片描述" style="zoom: 67%;" />	



最大内存地址实际上是2^47而不是2^64，但依然超级大，大约128T

逻辑上虽然有这么大，但是操作系统只允许你访问一部分，否则报错（分段错误）

在内存中，每个byte都有对应地址，

为了表示一个word的位置，我们通常采用第一个byte of the word

> 比如一个整数占4个字节，从地址 0000-0003，那我们就将它的address作为0000；
>
> byte在这4个字节中的存放顺序，就会涉及使用大端还是小端

现在基本上都是小端（将int的最高位byte，放到当前word adrress区间的最后面）

> 两种字节存放方式：
> 大端指的就是把字节序的尾端（0xcd）放在高内存地址，而小端指的就是把字节序的尾端（0xcd）放在低内存地址



**整数表示**

2种整数表示方式

- Unsigned无符号数（0000~1111，0 -15）
- Two’s-complement有符号数、补码（1000~0111，-8 - 7）

> 在C语言中，在执行一个运算时，如果一个运算数是有符号数，另外一个运算数是无符号数，那么C语言会隐式的将有符号数强制转换成无符号数来执行运算。



TMax作为有符号数的最大数：0111；

TMin作为有符号数的最小数：1000；



logic shift 和 arithmetic shift，我们现代计算机对于负数做位移时，默认使用arithmetic shift

左移两位就是丢弃最高的2位，并在右端补两个0

左移的情况比较简单，对于右移，分为逻辑右移和算术右移。

  逻辑右移和左移只是在方向上存在差异，逻辑右移一位就是丢弃最低的1位，并在左端补一个0。

至于算术右移，这里需要特别注意，当算术右移的操作对象的最高位等于0时，算术右移与逻辑右移是一样的，没有任何差别。

  但是当操作数的最高位为1时，算术右移之后，左端需要补1，而不是补0。



使用unsigned number可能会出现无限循环等问题，最好的解决方法就是不要用unsigned（当然也可以用一些奇怪的循环手法）
为什么要使用unsigned number？这就是Java做的lol，只不过加了>>>，代表逻辑右移

不表示数字，而是集合下标的时候，可以用无符号
或者用做高精度计算的时候，比如密码学



**浮点数**

101.11 --> 小数点右边的权值，为2^-1 , 2^-2 ... 2^k 

k取决于小数点后多少位



IEEE的关于浮点数的表示

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210507000127334.png)	

涉及三个变量：符号s、阶码E和尾数M。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210507000401425.png)	

浮点数的数值分为三类

- Normalized Value 规格化的值（阶码不全为0或全为1）
- Denormalized Value 非规格化的值（阶码全为0，前面不会有隐式添加1）
- Special Value 特殊值（阶码全为1，infinite）



例子

<img src="https://img-blog.csdnimg.cn/20210507010215971.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI5MDUxNDEz,size_16,color_FFFFFF,t_70" alt="在这里插入图片描述" style="zoom:67%;" />	



### **Chapter3 汇编语言（课程核心）**

我们并不会在CSAPP中学习写汇编

汇编语言作为中间的桥梁（高级语言到机器语言），我们通过了解它，来了解我们的程序。

编译器gcc，产生汇编语言，激活assembler和linker转化成机器语言



高级语言更可靠，效率更高。那为什么学汇编？

让我们可以手动调整底层，提高效率（现在都很牛了，不需要）

让我们可以直观的看到我们代码在机器中的运行

防止病毒侵入，增强安全

不用会写，能读懂就好



逆向工程reverse engineering，看着C语言，想象机器语言



本章使用的汇编语言（指令）：x86-64 代替 IA32（the traditional x86）

大多数机器的处理器都是使用x86-64，所以我们学习它的最常用的指令集



Instruction set：指令集

Machine Code，Assembly Code



PC：Address of next instruction

Register file: Heavily used program data

Condition codes: Store status information about most recent artihemetic or logic operation

Memory: array of byte

cache: 无法直接访问缓存



在micro architecture中还有更细微的东西，这里不深入



Turning C into Object Code

编译成汇编 --》  --》Linker将所有库merge到一起 --》在运行时，也有动态载入的库

![image-20210603145637648](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603145637648.png)	

Og --》 Optimize

S --》Stop



汇编语言

数据类型：各种不存在（没有什么array和list），都是各种bytes表示的整数和浮点数

操作：每一个指令能做的都很有限，基本就是一件事（所以手写汇编很...)；循环loop是使用更基础的指令打造出来的

*放在前边，表示指针，将数值放到这个指针位置



反汇编器：将运行文件转换回汇编语言文件（类似汇编）

在汇编和机器语言层面，所有变量名称都会消失。



寄存器：x86-64有一套特殊的寄存器（从IA32到x86，寄存器数量翻倍了，很有用）

16个保存整数和指针的寄存器，以%r开头，后面有的是英文，有的带有数字

%r得到64位，%e得到32位，两个都会看到，比如long和int

> 调用者保存：在调用函数B之前，将寄存器中的值保存push；为了保持函数调用前后，变量值不变；调用完之后pop

这些寄存器名字都有目的，但是这一切很多年前就消失了，所以寄存器名字和它的目的无关

historic lagacy stuff



movq Source，Dest指令：可以做很多事

将常数值，移动到内存、寄存器 ==》给常量赋值 temp = 0x4

从寄存器到另一个寄存器、内存 ==》 temp2 = temp

从内存到寄存器



%rdi作为第一个参数寄存器，%rsi作为第二个参数寄存器 



theinstruction set architecture, or ISA指令集让我们看起来它是一个个执行的

但硬件层面上far more elaborate, executing many instructions con-currently



Intel处理器的发展历程，1978第一台微处理器8086 到奔腾，到I7

.开头的行，都是指导汇编的伪指令，可以忽略



寄存器背景知识：

Intel处理器有16个通用寄存器，

An  x86-64  central  processing  unit  (CPU)  contains  a  set  of  16general-purposeregistersstoring 64-bit values.



Caller-saved / Callee-saved Register

调用者和被调用者



movb --》 Move byte

movw --》 Move word

movl --》Move double word 双字

movq --》Move quad word 四字

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603161256638.png" alt="image-20210603161256638" style="zoom:67%;" />	



**操作码和操作数	**

![image-20210603163300642](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603163300642.png)





Location of runtime stack %rsp

Condition Codes : CF,ZF,SF,OF

CF: Carry Flag (for unsigned)

ZF: Zero Flag

Sign Flag / Overflow Flag

做条件匀速那会被使用



cmp比较指令：将两个数相减，但是和sub不同的是，不会将结果赋值

test指令，只有一个值，看它是正、负还是零



不用指定返回地址，放到%rax中，自动找到



**Encoding**

使用Og来学习汇编，现实生活中使用-O1 / -O2

gcc包含着一系列程序，来完成整个编译过程

预处理器 解析头文件，将库包进来 --> 编译器compiler 汇编语言 --> 汇编程序assembler 二进制机器语言 --> 连接linker，将一些global values filled



**Machine-Level Code**

我们可以认为汇编和机器语言是等价的，只不过汇编是写出来可读的

它由两项重要的抽象组成

1. instruction set architecture，ISA	指令集，给我们感觉是一条一条执行的

   其实底层封装了硬件的操作，并行处理（不用操心）

2. 寻址，编程时使用的地址是虚拟内存virtual addresses，我们可以将其看成一串bytes。真实执行时当然会变成真实的物理内存（难） chapter9

   地址转换由操作系统完成



C程序员无法访问的底层（汇编能看到的）：

- PC 程序计数器，指向一个个地址
- integer register file 64位整数寄存器，可以存放整数和指针
- condition code registers 条件码寄存器，控制循环
- vector registers 向量寄存器，存多个整数或浮点数

汇编语言中，对于数据类型，数组等对象，不做区分！



我们使用反汇编软件disassembler，将二进制机器代码反汇编为汇编代码

反编译的和汇编编译生成的汇编代码，会有一些细微的差别



gcc产生的汇编代码可读性很差，其中“.”开头的都是给汇编器和连接器看的。

书中的例子都是添加了注释，并删除了没用的行



**数据格式Data Formats**

最早计算机时16bit，所以word指的是16bit

int就是double words，指针char*就是quad words

x86-64指令集包，在指令尾加一位，表示操作数的数据长度



**访问信息Accessing Information**

计算机中有16个general purpose registers通用寄存器

![image-20210603214040846](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603214040846.png)

从8位的al，16位的ax，延申到32位的eax，到现在最大64位的rax；

存储1byte或者2bytes的数据，其他的部分不动

但是对于存储4byte(32bit)的数据，要将前面的32位都归零（约定俗成）



rsp这个寄存器是最特殊的，用于指向运行时栈最结尾的位置

其他15个寄存器就比较灵活了



**operands操作数**

操作数可以分成3种：

- '$'开头的立即数，$0x1F

- register寄存器，4种不同大小的寄存器，需要指定

- memory内存，通常使用的寻址方式

  Base，[0]的位置；Index，[index]；Scale，根据元素类型的大小

  <img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603163321297.png" alt="image-20210603163321297" style="zoom:50%;" />	



**Data Movement Instructions 数据移动**

指令：mov，source operand，dest operand；

指令：movabsq，可以将64位立即数传送给寄存器

指令：movzbw，Zero-extended byte to word（没有movzlq，可以直接movl实现）

指令：movsbw，Sign-extended byte to word

指令：cltq，从eax到rax，与movsql %eax,%rax一样



mov的size必须和寄存器的size相同（无论是原寄存器还是目的寄存器）

movl $0x4050,%eax	

mov到destination，只会覆盖有值部分；但是movl，就会将目标寄存器前面32位都归零



指针其实就是一个address，直接将指针的值交给寄存器，就可以访问对应的值

局部变量都是存放在寄存器（快）

如果source和dest的长度不同，



**Pushing and Popping Stack Data 入栈弹栈**

指令：pushq；popq

程序栈Stack本身是虚拟内存中的一部分；栈对于函数调用是非常重要的，LIFO；%rsp寄存器指向栈顶

栈是在内存中的，栈顶的地址最小；每次push到栈顶，要将栈指针地址-8，然后将信息存到栈上；每次pop，读栈顶存放到寄存器，然后栈指针地址+8 --> 8(%rsp)

> 通过改变栈顶指针的地址，来“删除”栈顶的值，下一次push的值会覆盖这次“删除”的值

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603223903708.png" alt="image-20210603223903708" style="zoom:67%;" />



**Arithmetic and Logical Operations 算数和逻辑运算**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210603230241030.png" alt="image-20210603230241030" style="zoom:67%;" />	

load effective address指令leaq是movq的一个变种，将source内存地址载入到dest寄存器，而不是source的值。**有效加载地址**

leaq （基址，变址，比例因子），%rax

如果让 基址，变址，比例因子,都为值，就可以用来**实现算数运算**



**Unary and Binary Operations 一元/二元指令**

一元指令，同时作为source&destination，++和--就是单操作数

二元指令，右边减左边，再赋值给右边

如：x -=y ; subq %rax(y), %rdx(x)



**Shift Operations 平移操作**

指令：sal；shl；sar（算数右移）；shr（逻辑右移）

第一个参数是平移量，第二个是被平移数

右移可以是算数平移（补符号位），也可以是逻辑平移（补0）

汇编语言中，将C的乘法转换为lea和shift操作（2的幂次用shift，其他用lea）



**条件语句**

条件码寄存器Condition Code Register，由CPU维护，描述最近操作一些列指令的属性；

条件码寄存器的值是ALU在执行算数，或逻辑运算时写入的

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210608221443731.png" alt="image-20210608221443731" style="zoom:50%;" />	

除此之外，还有cmp和test指令，执行减法，判断两个数的>=<；只会设置条件码寄存器，不改变目的寄存器的值



cmovge %rdx, %rax 根据条件结果进行mov操作

这个汇编指令的执行会比跳转效率更高



**循环Loops**

do.while, while, for三种循环语句都是通过条件测试和跳转实现的

switch语句使用跳转表处理分支，程序的执行通过1次跳转就可以处理复杂的分支



**函数调用**

参数需要开辟8bit，而局部变量根据其大小开辟栈空间



**Data**

数组array：

为什么内存要用（基址，变址，scale）的方式表示，因为很常用；数组元素的寻址就是用这种方式

这样我们就理解为什么scale是1，2，4，8；

一个指针其实就代表一个地址，*int pointer，一个int类型指针+1，就相当于往后挪动4个bit



结构体struct：

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609105236633.png" alt="image-20210609105236633" style="zoom:50%;" />	



**Memory Layout**

当程序运行时，我们看到的是**虚拟内存**；而它底层的实现就是DRAM  随机存取存储器

Stack的大小为8MB；Heap存放malloc指定存放的数据；Data存放全局变量；Text存放函数

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609101536560.png" alt="image-20210609101536560" style="zoom:50%;" />	



### Chapter 6 内存层级结构

CPU执行命令，Memory存储数据；

之前我们假设Memory是一个线性数组，访问所有下标所花的时间是相同的，然而这并不是真实的。

现实生活中，内存系统是由一系列不同的容量、造价、访问速度的存储设备组成

作为一个程序员，需要了解内存结构。如果我们需要的data存储在寄存器上，瞬间访问；缓存区，4-75 cycles；内存，上百个cycles；磁盘，上百万个cycles



**Storage Technologies 存储技术**

Random-Access Memory（RAM）：分为静态SRAM和动态DRAM；静态要贵很多，使用在缓存技术上，CPU chip上；动态便宜很多，经常用在图形系统上frame buffer，主存

SRAM存储1bit在一个bistable memory cell；每个cell由6个晶体管组成

从名字上看，SRAM与DRAM的区别只在于一个是静态一个是动态。由于SRAM不需要刷新电路就能够保存数据，所以具有静止存取数据的作用。而DRAM则需要不停地刷新电路（电流可能会流失，导致数据消失）来保持数据。

SRAM存储一位需要花6个晶体管，而DRAM只需要花一个电容和一个晶体管。cache追求的是速度所以选择SRAM，而内存则追求容量所以选择能够在相同空间中存放更多内容并且造价相对低廉的DRAM。

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609144957720.png" alt="image-20210609144957720" style="zoom:50%;" />

他们都是volatile memories：断电，信息就会消失

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609145817910.png" alt="image-20210609145817910" style="zoom: 67%;" />	

Nonvolatile memroeis：ROM(（Read-only memory)等



**Memroy Modules**

DRAM chips作为一个组件，插在main system board（motherboard）的插槽上



磁盘：扇片；每个扇区的字节数，每个磁道上平均扇区数，每个盘面的平均磁道数，盘片上的盘面数，磁盘上的盘片数；

盘面旋转，arm的读写头去读和写；

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609125717181.png" alt="image-20210609125717181" style="zoom:50%;" />	

决定读写速度的因素：寻道时间，旋转延迟，传输时间

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609125820537.png" alt="image-20210609125820537" style="zoom:50%;" />	

DRAM和SRAM之间差距很大，而磁盘差距更大



Logical Disk Blocks

获得主线权限，通过io bus，复制信息道内存；传输完之后，告诉通过interrupt pin通知CPU

让CPU可以同时干别的事

SSD固态硬盘，DRAM和Disk之间的一种存储器，no moving part；里边是Flash memeory，Page can be written only after its block has been erased；Flash translation layer通过各种算法，提高闪存寿命



2003年，clock frequency到达极限，通过并行多核处理器加速

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609135438733.png" alt="image-20210609135438733" style="zoom:50%;" />	



**Locality 局部性**

有更好局部性的程序，更倾向于使用近期使用过的变量（Temporal locality）、或者物理上更靠近的data（Spatial locality），从而带来更高的程序性能

本章将介绍一些可以帮助提高程序局部性的技巧

> 这就解释了嵌套数组，为什么要fori嵌套forj对于 `a[i][j]`，而不是先forj再fori；虽然结果都能读出来，前者速度更快，后者物理上是跳跃的



Cache：你的背包！每次上学前从家里把需要的东西装上，不用每次需要啥都回家拿

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609143009787.png" alt="image-20210609143009787" style="zoom:50%;" />	

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609141221689.png" alt="image-20210609141221689" style="zoom:50%;" />	

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609141345852.png" alt="image-20210609141345852" style="zoom:50%;" />	

我们使用k+1存储。利用locality，CPU访问就访问k层的数据，构建一个更快的访问体系



放到cache中：hit就快，miss就慢；

Cold（compulsory） miss：cache is empty；warm up your cache！

Conflict miss：与映射算法相关

Capacity miss：active cache blocks（working set）比cache容量大，缓存不下



编译器管理reg，硬件管理多层cache，OS控制Buffer cache...



**Cache Memories**

Cache可以看作Memory的一个子集，Cache请求内存将一些block复制给它

一个很重要的缓存叫做Cache memory 高速缓存存储器，储存在CPU中；因为Locality，大多数请求都是由它完成的



Cache memory的组织方式：

缓存可以看作是一个set数组，每个set中有一个或多个lines，每个line包含一个valid bit（数据是否有意义，不是random bits），some tag bits（帮助搜寻块），and a block of data

S sets，E lines per set，B blocks per Set；Cache size = S * E * B

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210609150940094.png" alt="image-20210609150940094" style="zoom: 67%;" />	

将地址划分成多个部分，

Set Selection：将地址的一部分，作为Unsigned number，对应到set（可以看作下标）



把地址拆分成多个部分，映射实际地址，处理CPU请求

通过index bit找到set，然后通过tag bit找到line，然后在line中有多个byte，通过Offset bit确定偏移量，取出值

Direct-Mapped Cache：每一个地址，映射在指定的一个set的block中，每个set只有一个line；通过偏移量从block取出值；会出现miss conflict

Set-Associative Cache：每个set有多个line，匹配具体的line

Fuuly-Associative Cache：一个大的set多个line，包含所有地址映射和值



### Chapter 8 Exceptional Control Flow

控制流一般是说处理器中比较平稳的程序执行过程（PC不断指向不同的指令值）。在程序中改变控制流的方法有跳转和函数返回，但是还有很多情况我们没有考虑到。比如键盘输入、程序崩溃之类的。硬件 --> 操作系统对它们做出的处理叫做异常控制流ECF。

> An exception is an abrupt change in the control flow in response to some change in the processor's state

ECF机制是concurrency并发性实现的本质；比如两个进程、两个线程同时运行，通过ECF来控制流

操作系统层面的ECF，帮助我们理解高级语言的软件异常exceptions原理

nonlocal jumps，jumps that violate the usual call/return stack discipline；出现错误时允许出现这种nonlocal jumps



本章开始，我们start to focus on软件是如何和操作系统交互的



异常处理完后，会有三种可能性出现：

1. 继续执行当前的指令，最自然的
2. 执行下一条指令
3. 终止当前程序



**异常处理**

很容易混淆软件和硬件

每个异常都有一个整数标识符，有的是硬件的，有的是操作系统设计的

系统启动时，操作系统会初始化一个跳转表格`exception table`



程序运行 --> CPU detects exception --> determine exception number k --> find address of the handler for exception k in `exception table` --> handle exception

站在程序员的角度，都是函数调用；只不过普通函数调用是在当前程序的栈中，在user mode下执行；而系统调用是转到kernel mode，拥有系统权限



**异常种类**

低级的ECF，更高级的ECF

可以分成四类：interrupts，traps，faults，aborts

Interrupt：异步的（先将中断信号记录在中断寄存器，当前指令继续执行，执行结束再去检查中断寄存器是否enable interrupt）；通常是IO设备，通知CPU，打断一下；按了一下键盘，打断一下

Trap：有意产生的异常（应用程序需要）。最常见的是System call系统调用，调用内核的API，为了实现一些功能；同步的（打断当前程序，立即处理异常）

Fault：error，有可能可以recover

Abort：error，不能recover

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210610132259706.png" alt="image-20210610132259706" style="zoom:50%;" />	



**进程**

The classic defifinition of a process is *an instance of a program in execution*.

我们不关注操作系统对进程的具体实现，而是关注两个key abstraction

- independent logical control flow，给程序提供一种它拥有CPU独立使用权的假象（实际上很多程序在并行concurrency，每个进程轮流使用CPU）

  Each process executes a portion of its flflow and then is *preempted* (temporarily

  suspended) while other processes take their turns.

- private address space，虚拟内存，给程序提供它有拥有内存独立使用权的假象

  这个空间是private的，所以通常情况下进程之间不能访问彼此的空间



两个在同一个CPU下同时运行的进程，叫concurrency并发

此处的“同时”指的是，系统在两个进程间不断切换；图中A&B，A&C是在并发的

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210610185400469.png" alt="image-20210610185400469" style="zoom:50%;" />	



为了保证这些虚拟化，CPU会禁止程序使用一些指令，或者访问一些空间

在control register中有一个mode bit，可以在user mode和kernel mode中切换

在user mode中，程序不能执行IO操作，更改mode bit等操作；所以，想要切换成kernel mode，只能通过interrupt，fault，trapping system call



**Context Switch 环境切换**

环境就是指的当前进程的虚拟内存和寄存器值

为了实现multitasking，操作系统内核使用了一种更高层次的ECF（建立在低级ECF的基础上），叫做`context switch`

保存当前进程环境，然后运行其他进程（分配时间片time slice）



scheduler负责schedule线程的切换和开启，执行context switch

1. 保存当前进程的context
2. 恢复之前保存的某个进程的context
3. 将控制权交给新恢复的进程

如果system call导致了阻塞，也不会一直等着他；比如从磁盘读取的指令，内核可以执行一个context switch然后继续运行其他进程，不会一直等磁盘fetch data；磁盘拿到数据后，再interrupt kernel来读取数据

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210610182432050.png" alt="image-20210610182432050" style="zoom:50%;" />	



**操作进程的几个重要的System Call**

1. 获得Process ID（PID）

2. 创建/结束一个进程

   从程序员角度，进程可以看成3中状态：Running、Stopped、Terminated

   结束进程的方法：receive a signal，main方法return，手动调用exit()

   通过fork()可以建立子进程 return pid；子进程获得父进程中的所有内存和值，除了pid不同以外都一样（通过pid分辨子和父）

3. 收割子进程

   子进程结束后不会被系统移除，wait()，要等到它的父进程来收割它

   结束了，还没有被收割的成为zombie（half alive，half dead）

4. 睡眠进程sleep()

5. 在当前进程中运行新程序，execve()



**信号Signal**

Unix系统提供了一系列的signal，可以发送给进程；这个机制依赖了`process groups`的概念

每一个进程都属于一个特定的group，用int来表示；子与父在相同的group

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210610204303753.png" alt="image-20210610204303753" style="zoom:50%;" />	

键盘，快捷键；通过cammand line；发送signal

当kernel将进程p从kernel mode切换到user mode的时候，会检查它的`signal set`；如果有signal，内核会将signal传递给p；p接受信号后，会触发它的`action`

有一系列默认actions，比如process terminate；process stops



A *signal* is a small message that notififies a process that an event of some type

has occurred in the system. Figure 8.26 shows the 30 different types of signals that

are supported on Linux systems.



**shell**

shell is an application program that runs programs on behalf of the user

default Linux shell --> bash

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210610205116491.png" alt="image-20210610205116491" style="zoom:50%;" />	

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210610205323110.png" alt="image-20210610205323110" style="zoom:50%;" />	

foreground job：会让shell父线程wait，然后等执行完继续

bg：background job 后台进程；我们不会管，让他结束变成zombie

怎么办？使用signal通知；和异常很像，只不过signal是纯软件实现的



### Virtual Memroy

virtualize,当你虚拟化一个资源时；就是向用户呈现这个资源的某种抽象或者某种不同的视图

当你想虚拟化一个资源的时候，需要用对于它的访问做干涉



CPU执行一条指令 --> 发送虚拟地址给MMU --> 将虚拟内存转换为物理地址 --> 去内存中取值并返回



为什么要虚拟化地址？

物理内存128MB，运行程序A需要10MB，程序B需要110MB；分配10MB给A，再分配110MB给B

这样的内存分配导致的问题

- 程序直接访问物理内存，进程地址空间不隔离。一个恶意程序可以篡改另一个程序的内存数据
- 内存使用率低。如果有一个程序C需要10MB内存，还要将其从磁盘载入内存；此时系统必须将正在运行的程序中选一部分拷贝到磁盘，释放空间出来给C；大量数据装入装出，效率低下
- 程序运行的地址不确定。



地址空间：一个地址的集合

Linear address space线性地址空间：0,1,2,3,4...

Virtual address space：Set of N = 2^n；每个进程都拥有一样大的内存

Physical address space：Set of M = 2^m；内存中有多少DRAM

虚拟空间通常比物理空间大得多

1. 可以将虚拟内存看作物理内存的缓存，让我们更有效的使用内存
2. 每个进程的空间视图一样，让我们更好的管理进程的内存
3. 允许我们创建单独的、被保护的内存空间 Isolate address spaces



<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210610211341642.png" alt="image-20210610211341642" style="zoom: 67%;" />	

Fully Associative

简单来讲，系统把虚拟内存和物理内存都划分为等长的 page（页），并为每个进程维护一个 page table（页表）用来将虚拟页映射到物理页。**这样当我们访问一个虚拟地址时，系统就可以通过查表将其翻译为物理地址。**如果这个地址所在的页当前不在物理内存中，则系统会先将它从磁盘取出来替换掉内存里另一个暂时不用的页。整个过程是由操作系统和硬件协同完成的



