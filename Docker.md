# Docker



### 引言

官方定义：We help developers and development teams build and ship apps

Docker就是**应用容器技术**



开发环境需要安装mysql，部署到远程服务器，还要重新安装mysql...

开发环境，面向Docker容器，在容器中安装mysql等各种应用的环境，部署到服务器的时候，Docker将应用和各种应用环境打包一起发布，能保证环境版本高度一致



docker引擎是操作系统级别的隔离，和本地的服务无关



**为什么使用Docker？**

- 开发的时候，本地环境可以跑，生产环境跑不起来

  优势1：程序和软件环境直接打包在一起，无论在哪个机器上都保证了环境一致

- 服务器自己的程序挂了，发现是别的人的程序把内存吃完了，自己程序因为内存不够就挂了

  优势2：每一个应用服务一个容器，达成隔离

- 突然加大流量，要增加部署几十台服务器

  优势3：通过镜像，复制N多个环境一致容器



**Docker 与 虚拟机的对比**

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210730135425207.png" alt="image-20210730135425207" style="zoom: 67%;" />	

虚拟机运行软件之前，必须自身携带操作系统；本身很小的应用变得非常大

虚拟机在资源调度上要经历更多的步骤：虚拟内存 --> 虚拟物理内存 --> 真正物理内存

Docker：轻量，直接调度宿主机资源

​	

**Docker的安装**

https://docs.docker.com/engine/install/centos/



### **核心概念**

镜像image：一个镜像代表一个软件，只读

容器container：基于某个镜像运行run一次，就会生成一个程序实例，称之为一个容器，可读可写

仓库repository：储存docker镜像的位置

- 远程仓库：docker在世界范围内维护的唯一远程仓库
- 本地仓库：当前自己机器中下载镜像的存储位置

docker hub --> 远程仓库的web界面，搜索相关镜像，pull到本地

https://hub.docker.com/



设置docker下载加速服务器（切换到阿里云仓库）

1. 登录阿里云账号

2. 搜索容器镜像服务

   <img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210730160652289.png" alt="image-20210730160652289" style="zoom: 50%;" />	



### 镜像相关操作

docker images [镜像名称:版本号]：查看所有镜像（名称、版本、唯一ID、镜像创建时间、镜像大小）

<img src="C:\Users\乐乐大哥哥\AppData\Roaming\Typora\typora-user-images\image-20210730162910467.png" alt="image-20210730162910467" style="zoom:50%;" />	

docker search 镜像名称:版本号，以OFFICIAL为准，来pull镜像

docker pull 镜像名称:版本号

docker image rm [-f] 镜像名称:版本号：对于正在被容器使用的镜像，需要加f，force强制删除



### 容器相关操作

docker run

不良人视频p8