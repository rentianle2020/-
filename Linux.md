# Linux

JavaEE项目部署到Linux

运维工程师：服务器规划，调试优化，日常监控，故障处理，数据的备份和恢复，日志管理；一般都是管理Linux集群



**应用领域**

- 桌面领域（ubantu）
- **服务器（免费，稳定，高效）**
- 嵌入式（内核可裁剪）



**Linux和Unix的关系**

1. 贝尔实验室Kenneth Thompson和Dennis Ritchie开发了UNIX，各大公司做了垄断性的延申
2. Richard Stallman发起了伟大的GNU计划，即互联网开源
3. Linus Torvalds开发的Linux就是GNU计划下的产物，即GNU/Linux



**磁盘分区**

boot（启动文件和内核）、swap（交换区，暂时储存内存内容的地方）、根分区（所有文件的根）



**网络连接模式**

- 桥接模式：虚拟系统可以和外部系统通信，但容易造成IP冲突
- NAT模式：网络地址转换模式，与外部通信，不造成IP冲突
- 主机模式：独立的系统



### 1、Linux目录结构

windows：C、D、E磁盘 --> 目录 --> 文件/子目录

linux：根目录 --> 子目录，规定好的名称root、home、bin、etc等，每个子目录也规定好了存放的文件内容



Linux的文件系统采用层级式的树状结构，最上层是根目录“/”

在Linux世界里，一切皆文件



**具体目录结构（不用背）**

- **/bin**：常用命令
- /sbin：s = Super User。系统管理员使用的系统管理程序，如系统管理、目录查询等关键命令文件
- **/home**：普通用户的主目录。每个用户的用户数据，文件，设置
- **/root**：系统管理员的主目录
- /lib：核心模块的共享库。几乎所有应用程序都需要用到这些共享库。
- /lost+found：系统非法关机后，存放一些文件（一般情况下是空的）
- **/etc**：系统的配置文件目录。密码、网卡信息、环境变量。
- **/usr**：用户安装的应用程序，类似program files
- **/boot**：内核和系统启动文件

- /proc，/srv，/sys 不能动，系统文件夹
- /tmp：存放临时文件
- /dev：将所有硬件，用文件的形式存储
- **/media**：挂载多媒体设备。设备映射成文件挂载到这个目录下
- **/mnt**：作为挂载点使用（如挂载Windows下的某个分区）
- /opt：安装软件所存放的目录
- **/usr/local**：安装软件的默认安装目录
- **/var**：经常被修改的目录放在这个目录。如日志文件



### 2、远程登陆

Xshell：最好的远程登陆到Llinux操作的软件

Xftp：远程文件传输



### 3、vi和vim

文本编辑器，Vim可以看作是Vi的增强版，具有代码补完等功能



一般模式：命令行中输入vim xxx进入一般模式

编辑模式：一般模式下按 i 或者 a 进入

命令行模式：编辑模式下按 esc + 冒号，正常模式下直接冒号；wq保存退出，q退出，q！强制退出不保存



**常用快捷键（一般模式下）**

- 拷贝yy，拷贝当前向下5行 5yy，粘贴p
- 删除dd，删除当前向下5行 5dd
- /xxx快速搜索关键词，输入n就是定位下一个



### 4、开机、重启和用户登录注销

shutdown -h now 立即关机 = halt

shutdown -r now 现在重启计算机 = reboot

sync：把内存的数据同步到磁盘；虽然以上指令执行前会执行sync，但是小心驶得万年船

poweroff：关机且掉电



**用户登录、切换、注销**

su - 用户名：切换用户（高权限切换到低权限用户，不需要输入密码）

logout：注销用户



**用户管理（管理员权限）**

useradd 用户名：添加用户，用户的家目录默认在 /home/tom

passwd 用户名：指定/修改密码

userdel 用户名：删除用户

id 用户名：用户查询

who am i：查看当前用户



**用户组**

将相同权限的用户放到一个组，对多个用户进行统一管理

groupadd 组名：创建组

groupdel 组名：删除组

如果没有给用户指定组，Linux会建立一个与用户同名的组，然后将用户放进去



useradd  -g 组名 用户名 ：给组添加用户

usermod -g 组名 用户名：改变用户所在组



**相关文件**

/etc/passwd：用户的配置文件

/etc/group：组配置文件

/etc/shadow：加密口令配置文件



### 5、实用指令



**运行级别**

0. 关机
1. 单用户
2. 多用户状态，没有网络服务
3. **多用户状态，有网络服务**
4. 系统未使用，保留的级别
5. **图形界面**
6. 系统重启



**帮助指令**

man 指令



**文件目录指令**

pwd：当前目录的绝对路径

ls：当前目录 -a所有信息（包括隐藏） -l以列的形式 -h文件大小人性化显示 -a显示隐藏文件

cd：定位路径 ~回到家目录 ..回到上一级目录



mkdir：创建目录 -p创建多级目录

rmdir：删除空目录

tree 目录：树状展示目录结构



touch：创建空文件

rm：移除文件或目录 -r递归删除（进到目录，挨个删除文件，再退出来删除目录） -f强制删除不提示

cp 文件 （源地址）目的地址：\cp强制覆盖，-r递归复制



mv 旧名字 新名字：重命名

mv 文件 目标地址：移动文件/目录



cat：查看文件内容 -n显示行号；管道命令|more

more：基于VI编辑器的文本过滤器，内置若干交互指令

less：和more指令类似，但是更加强大



echo：输入内容到控制台（类似System.out)

head：查看文件前10行 -n 5看前5行

tail：和head相反，-f实时追踪文件更新（ctrl + c退出）



`>`：覆盖写入（ls -l文件信息写入；cat文件内容写入；echo内容写入）

`>>`：追加写入



ln -s 目标 软连接：ln -s /root /home/myroot，我们进入/home/myroot就相当于进入了/root；rm 删除软连接



history：查看历史操作；history 10 查看最近10条；history !5 查看历史编号为5的指令



**时间日期类**

date：当前时间；date + "%Y-%m-%d"指定格式

cal：日历，cal 2021显示整年日历



**搜索查找类**

find 搜索范围 选项 文件名：find /home -name hello.txt，在/home目录下查找文件名为hello.txt的文件

grep：过滤查找，cat hello.txt | grep ”Hello“，将搜索结果通过管道，交给grep过滤Hello所在的行；-n显示行号 -i忽略大小写



**压缩/解压缩**

gzip/gunzip 原地压缩/解压缩

zip 压缩名 被压缩文件名：压缩，-r递归压缩

unzip 解压到 被解压的文件名：默认解压缩到当前目录，-d指定存放目录；

unzip -d /root /home/yoyo.zip，将yoyo.zip解压到/root目录下



**打包**

打包：tar -zcvf xxx.tar.gz 打包的内容

拆包：

- 拆包到当前目录：tar -zxvf xxx.tar.gz
- 拆包到指定目录：tar -zxvf xxx.tar.gz -C 指定目录

![image-20210711170246213](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210711170246213.png)	



### 6、组管理和权限管理

Linux每个用户必须属于一个组。每个文件有所有者、所有组、其他组的概念。

- 所有者：文件属于某个用户（默认为文件创建者）
- 所有组：文件属于某个组（默认为创建者所在的组）
- 其他组：除了所有者和所有组之外的用户

这三个分类，对于该文件，都有不同的权限



**基础命令**

- ls -ahl：查看文件/目录所在组

- chown 新所有者 文件名：改变文件所有者（chown -R递归变更）

- chgrp 新所有组 文件名：改变文件所有组（chgrp -R递归变更）

- chmod u=rwx,g=rx,o=x 文件名/目录：设置权限，也可以通过+和-来变更权限（chmod a-x），a代表所有用户

  u --> user ; g --> group ; o --> other ; read write excute 

 

**权限的基本介绍**

ls -l显示权限

0-9位说明

- 第0位代表文件类型

  l是连接（快捷方式）、-是普通文件、d是目录、c是字符设备文件（鼠标、键盘）、b是块设备（硬盘）

- 1-3位代表所有者拥有的权限

- 4-6位代表所有组拥有的权限

- 7-9位代表其他组拥有的权限



**rwx权限详解**

- 作用到文件

  r代表可读、w代表可以修改、x代表可以执行

- 作用到目录

  r代表可以ls查看目录

  w代表可以在目录内创建+删除

  x代表可以cd进来 



![image-20210711174943107](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210711174943107.png)	

文件类型+权限    硬链接数    所属人    所属组    文件大小（字节）    更改日期    文件名



### 7、任务调度

定时调度：系统在某个时间执行特定的命令或shell脚本



**crond任务调度**

在固定时间或固定间隔执行程序

crontab：-e设置，-l查询，-r删除当前用户所有定时任务



5个占位符 + 命令/shell脚本：分钟，小时，第几天/月，第几月，星期几

*/1 * * * * ； */n代表每隔多久执行一次

1,3指的是两个分别的，1-3指的是连续的



**at定时任务**

在固定时间执行程序



at守护进程atd会在后台运行，每60秒检查定时作业队列，如果时间匹配则运行

ps -ef | grep atd：检测atd是否再运行



atq：查看系统中没有执行的任务

atrm 编号：删除指定编号的任务

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210712115523778.png" alt="image-20210712115523778" style="zoom: 67%;" />	



两天后下午5五点，将home目录下ls一下

1. at 5pm + 2 days
2. /bin/ls /home 
3. ctrl + d



### 8、磁盘分区和挂载

挂载 mount，将磁盘分区联系到指定文件目录上

![image-20210712134856156](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210712134856156.png)

lsblk：查看磁盘分区



当磁盘空间不够用了，需要添加

添加硬盘 --> 设置分区 --> 格式化磁盘（分配UUID）--> 挂载到指定目录

用命令行建立挂载关系，重启系统后会失效；永久挂载，修改/etc/fstab



**磁盘常用指令**

df -h：查询磁盘使用情况



### 9、Linux网络配置

![image-20210712151619331](C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210712151619331.png)

工作环境下，不能随机分配IP地址；一定固定IP，否则无法作为服务器



Hosts是一个文本文件，用来记录IP和主机名的映射关系（优先）

DNS是互联网上记录IP和域名映射关系的分布式数据库

域名劫持：黑客更改你的Hosts文件，将www.icbc.com映射为他自己做的钓鱼网站，这样在访问工商银行网站的时候，优先查看Hosts文件，就直接访问钓鱼网站了，而不是访问DNS服务器来解析出正确的ip地址。



### 10、进程管理(重点)

每个进程都有一个PID

每个进程可能以两种方式存在，前台（占有屏幕）与后台（mysql，tomcat等服务）



ps：查看进程，-aux 查看占用率，-ef 查看父进程和完整命令，|grep 进行过滤

%CPU：占用CPU百分比

%MEM：占用物理内存百分比



top：动态监控进程，k交互结束指定PID进程

pstree：树状显示进程，-p显示进程PID，-u显示进程所属用户



kill [选项] 进程号：终止进程；选项-9表示强迫进程立即停止；终止sshd服务可以终止对应的远程访问

killall 进程名称：终止所有进程以及其子进程



### **11、服务管理**

服务（service）本质就是进程/后台程序，通常会监听某个端口



**systemctl设置服务状态**

systemctl命令可以管理/usr/lib/systemd/system下的所有系统服务

systemctl [start|stop|restart|reload|status] 服务名：service管理指令



systemctl enable 服务名：设置服务开机启动

systemctl disable 服务名：关闭服务开机启动

systemctl is-enabled 服务名：查看服务是否为自启动



Linux开机流程：开机 --> BIOS --> /boot --> systemd进程1 --> 运行级别 --> 运行对应级别的服务

CentOS7之后，运行级别简化为3和5，所以不指定运行级别了



**防火墙**

防火墙管理端口的开放（如：开放22端口，8080端口），也可以对前来的请求进行过滤（如：某个IP地址被限制访问指定端口）



阿里云服务器默认关闭防火墙，使用安全组服务来开放/限制端口（如果没有安全组，使用防火墙来开放/限制端口也是同理）



其实，iptables与firewalld都不是真正的防火墙，它们都只是用来定义防火墙策略的防火墙管理工具而已，或者说，它们只是一种服务。iptables服务会把配置好的防火墙策略交由内核层面的netfilter网络过滤器来处理，而firewalld服务则是把配置好的防火墙策略交由内核层面的nftables包过滤框架来处理。换句话说，当前在Linux系统中其实存在多个防火墙管理工具，旨在方便运维人员管理Linux系统中的防火墙策略，我们只需要配置妥当其中的一个就足够了。



**监控网络状态**

netstat：查看网络状态，-an按一定顺序排列，-p显示哪个进程在调用

ping：测试连接



### 12、RPM与YUM

RedHat Package Manager（RPM）：互联网下载包的打包及安装工具



rpm -qa：查询已安装的rpm列表

rpm -qf 文件：查询文件属于哪个包

rpm -e 包名称：删除软件包

rpm -ivh RPM包全路径名称：install，verbose提示，hash进度条



Yum是一个Shell软件管理器，基于RPM包，能够从指定服务器下载RPM包并且安装，自动处理依赖关系（安装A，用到C，会自动下载C）

yum list | grep xx软件：查看yum服务器上，是否有指定的软件包

yum install 软件名：下载RPM包



### 13、搭建JavaEE环境

安装包/压缩包，放到/opt，安装完成后的程序放到/user/local

为了能在任何目录下，都能找到javac和java命令，需要配置环境变量，在/etc/profile下配置环境变量，然后source /etc/profile更新



安装好JavaEE环境后，在windows环境开发完毕，package打包，通过Xftp将jar包发送到Linux服务器，然后java -jar xxx运行



### 14、Shell编程

Shell是一个命令解释器，它在操作系统的最外层，负责直接与用户对话，把用户的输入解释给操作系统，并处理各种各样的操作系统的输出结果，输出屏幕返回给用户。



shell有多种，但是国内基本是bash

开头：#!/bin/bash

- 给用户可执行权限，直接运行xxx.sh
- sh xxx.sh，不需要权限，可以直接执行



**Shell变量**

系统变量：$HOME、$PATH...	

set：显示当前shell中的所有变量



变量的定义

变量名=值：定义变量；等号左右不能加空格，变量名规范为全大写，字母数字下划线，不能以数字开头；将指令的返回值赋给变量，变量名=$(命令)，将变量的值赋给变量，变量名=$变量名



unset 变量：撤销变量

readonly 变量：声明静态变量（类似java的final），不能unset



**设置环境变量**

export 变量名=变量值（将变量输出为环境变量/全局变量）

source 配置文件：让修改后的配置立即生效

定义好全局变量，该变量就可以在所有脚本文件中使用了！



**获取参数**

sh my.sh 参数1 参数2 参数3...

$0 - $9 获取第1-9个参数

$*获取所有参数（当作一个整体）

$@获取所有参数（分别对待，可遍历）

$#获取参数个数



**进程相关变量**

$$获取当前进程PID

$!：获取后台运行的最后一个进程的PID

$?：最后一个进程的运行结果



**运算符**

$((运算式))或者$[运算式]



**条件判断**

字符串比较=

整数比较-lt -eq -ge...

也可以按照文件权限/文件类型进行判断



**流程控制**

```shell
#if...else if
if [ $1 -ge 60 ]
then
	echo "及格了"
elif[ $1 -lt 60 ]
then
	echo "不及格"
fi
```



```shell
#case语法，超级奇怪...
case $1 in
"1")
echo "周一"
;;
"2")
echo "周二"
;;
esac
```



```shell
#for循环，累计求和
SUM=0
for(( i=1; i<$1; i++ ))
do
	SUM=$[$SUM+$i]
done
```



```shell
#while循环
i=0
while [ $i -le $1 ]
do
	代码
done
```



**read读取控制台输入**

read -t 输入限时 -p 输入提示 输入值赋给的变量



**函数**

系统函数：

- basename 完整路径 后缀名：获取文件名，去丢奥i后缀名
- dirname 完整路径：获得目录



自定义函数：

```shell
function getSum(){
	SUM=$[$n1+$n2]
	echo "和=$SUM"
}

read -p "请输入一个数n1=" n1
read -p "请输入一个数n2=" n2

getSum $n1 $n2
```



<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210715143614164.png" alt="image-20210715143614164" style="zoom: 67%;" />	

crond 来定时调用写好的shell脚本，每天凌晨2：30

脚本来完成对文件的备份

