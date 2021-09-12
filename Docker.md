# Docker

<img src="https://docs.docker.com/engine/images/architecture.svg" alt="Docker Architecture Diagram" style="zoom:67%;" />	

## 概述

官方定义：We help developers and development teams build and ship apps

Docker就是**应用容器技术**



#### **为什么使用Docker？**

- 快速，便捷的部署服务
- namespace实现服务隔离，cgroup实现资源限制和分配



#### **Docker&VMWare**

![docker&vmware](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210811173905.png)	

虚拟机运行软件之前，必须自身携带操作系统；本身很小的应用变得非常大

虚拟机在资源调度上要经历更多的步骤：虚拟内存 --> 虚拟物理内存 --> 真正物理内存

Docker：轻量，直接调度宿主机资源



#### **Docker的安装**

https://docs.docker.com/engine/install/centos/

设置docker下载加速服务器（切换到阿里云仓库）

1. 登录阿里云账号

2. 搜索容器镜像服务

   <img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210809150044.png" alt="image-20210730160652289" style="zoom: 50%;" />	



#### Images

镜像是一个只读模板，用来创建容器。

一个镜像可以是基于其他镜像之上，with some additional customization

容器运行时，使用具有隔离性的文件系统，里面的所有内容是由镜像提供的。所以镜像包括容器的所有依赖、设置、脚本...



#### Containers

容器是一个运行的镜像实例。可以使用Docker API来创建、停止、移动、删除它。

简单来说，容器就是一个运行的进程，只不过和宿主机上的其他进程隔离开了。

这种隔离利用了内核命名空间`namespace`和控制组`cgroups`，这些特性在 Linux 中已经存在了很长时间。 Docker 一直致力于让这些功能易于使。



#### **Dockerfile**

| FROM                           | 当前镜像基于哪个镜像                 |
| ------------------------------ | ------------------------------------ |
| **WORKDIR**                    | 运行容器后，默认所在的目录           |
| **COPY**（ADD）                | 拷贝文件和目录到镜像中               |
| **RUN**                        | 镜像启动时，执行的指令               |
| **CMD**（ENTRYPOINT）          | 镜像启动后，执行的指令               |
| ------------------------------ |                                      |
| EXPOSE                         | 容器对外暴露的端口号                 |
| VOLUME                         | 容器数据卷，用于数据保存和持久化工作 |
| ENV                            | 配置环境变量 key value               |



Dockerfile文件没有任何后缀，如`.txt`

```dockerfile
 # syntax=docker/dockerfile:1
 FROM node:12-alpine
 RUN apk add --no-cache python g++ make
 WORKDIR /app
 COPY . .
 RUN yarn install --production
 CMD ["node", "src/index.js"]
```



`docker build`运行Dockerfile生成`Image`，将该Dockerfile的所在目录中的有文件打包成镜像

`-t`后面的是给这个image起名；最后的`.`代表运行的是当前目录中的Dockerfile

```
docker build -t getting-started .
```



## Docker CLI指令

https://hub.docker.com/



#### 容器操作

docker ps：查看当前运行的容器

docker stop 容器id：停止容器

docker rm 容器id：删除停止的容器，-f 可以删除正在运行的容器

docker exec -it 容器id bash：进入容器内部文件，使用bash命令



#### **推送到dockerhub**

1. docker login -u YOUR-USER-NAME
2. docker tag getting-started YOUR-USER-NAME/getting-started
3. docker push YOUR-USER-NAME/getting-started



#### 持久化DB

Volumes provide the ability to connect specific filesystem paths of the container back to the host machine. If a directory in the container is mounted, changes in that directory are also seen on the host machine. If we mount that same directory across container restarts, we’d see the same files.



**Named Volumes**

简单完成持久化（Docker帮我们选择宿主机的挂在地址）

1. docker volume create todo-db
2. docker run -dp 3000:3000 -v todo-db:/etc/todos getting-started
3. docker volume inspect todo-db



**Bind Mounts**

我们可以自己选择宿主机的挂载地址

/path/to/data:/usr/local/data



#### 多容器应用

![multi-app-architecture](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210811181123.png)	



**Container networking**

如果两个容器在同一个网段下，他们可以内网通信。否则只能远程路由。

1. docker network create todo-app

2. ```shell
   docker run -d `
        --network todo-app --network-alias mysql `
        -v todo-mysql-data:/var/lib/mysql `
        -e MYSQL_ROOT_PASSWORD=123456
        -e MYSQL_DATABASE=todos `
        mysql:5.7
   ```

3. 再run TodoApp，只要在一个网段下，可以直接localhost:3306访问，或者通过别名（在docker-compose中的服务名）连接

> In the “ANSWER SECTION”, you will see an `A` record for `mysql` that resolves to `172.23.0.2` (your IP address will most likely have a different value). While `mysql` isn’t normally a valid hostname, Docker was able to resolve it to the IP address of the container that had that network alias (remember the `--network-alias` flag we used earlier?).
>
> What this means is... our app only simply needs to connect to a host named `mysql` and it’ll talk to the database! It doesn’t get much simpler than that!



**docker-compose**

Docker Compose是一个用来帮助我们搭建多容器应用的工具。

使用 YML 文件来配置应用程序需要的所有服务。然后，使用一个命令，就可以从 YML 文件配置中创建并启动所有容器。

可以通过服务名来访问对应的应用IP地址



#### **常用指令**

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210811173931.png" alt="dockercli" style="zoom:67%;" />	

docker logs 容器名or容器id：查看容器日志，-f 查看实时日志，-t 加入时间戳

docker top 容器名or容器id：查看容器内运行的进程

docker stats  容器名or容器id：内存和CPU使用情况

exit：退出容器

docker cp 文容器唯一标识：容器内资源路径	操作系统资源路径 --> 容器复制文件到操作系统，反过来则是复制操作系统文件到容器

docker inspect 容器id：查看容器内部细节



## Portainer

Docker可视化工具

https://documentation.portainer.io/v2.0/deploy/ceinstalldocker/



## 实现原理

namespace：通过clone系统调用，传入各个Namespace对应的clone flag，创建了一个新的子进程，该进程拥有自己的Namespace。根据以上代码可知，该进程拥有自己的pid，mount，user，net，ipc和uts namespace。

cgroup：进程pid写入各个Cgroup子系统中，这样该进程就受到相应Cgroup子系统的控制

AuFS：检查所需文件(系统内核)是否存在，如果存在则再次利用，每次对image的更改也不过是在其他image的基础上增删改文件

Namespace做隔离；Cgroups做限制；unionFS做文件系统



https://stackoverflow.com/questions/16047306/how-is-docker-different-from-a-virtual-machine/16048358#16048358

Docker originally used [LinuX Containers](https://linuxcontainers.org/lxc/) (LXC), but later switched to [runC](https://github.com/opencontainers/runc) (formerly known as **libcontainer**), which runs in the same operating system as its host. This allows it to share a lot of the host operating system resources. Also, it uses a layered filesystem ([AuFS](http://aufs.sourceforge.net/)) and manages networking.

So, let's say you have a 1 GB container image; if you wanted to use a full VM, you would need to have 1 GB x number of VMs you want. With Docker and AuFS you can share the bulk of the 1 GB between all the containers and if you have 1000 containers you still might only have a little over 1 GB of space for the containers OS (assuming they are all running the same OS image).

A full virtualized system gets its own set of resources allocated to it, and does minimal sharing. You get more isolation, but it is much heavier (requires more resources). With Docker you get less isolation, but the containers are lightweight (require fewer resources). So you could easily run thousands of containers on a host, and it won't even blink.



Docker uses facilities like control groups for resource limiting and accounting, and a union-capable file system for efficient file system allocation.

This allows completely different containers to share their filesystem layers, even though some significant changes may have happened to the filesystem on the top-most layers in each container. This can save you a ton of disk space, when your containers share their base image layers. However, when you mount directories and files from the host system into your container by way of volumes, those volumes "bypass" the UnionFS, so changes are not stored in layers.



PID namespaces can be nested: each PID namespace has a parent, except for the initial ("root") PID namespace.

A process is visible to other processes in its PID namespace, and to the pocesses in each direct ancestor PID namespace going back to the root PID namespace. In this context, "visible" means that one process can be the target of operations by another process using system calls that specify a process ID.