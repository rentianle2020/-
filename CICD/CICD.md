# Github CICD

1. Write Code
2. Push to GITHUB
3. Create a Workflow
4. CICD：Build & Test，Build Docker image， Push image to Docker Hub
5. Run image from Docker Hub



### Docker

VM：put a computer inside computer，我们进程互相影响，将进程隔离

Docker：不需要put a computer inside computer，但是依然做到隔离



Docker的实现基于Linux的技术

- Mount：Isolate files can see by a process
- Process ID：Isolate processes can see by a process
- Network：same for network interfaces
- Interprocess Communication：No Pipes, no shared memory
- UNIX Timesharing System：Isolated host and domain name
- User ID：Seperate user IDs for processes



**What is a container？**

https://www.youtube.com/watch?v=el7768BNUPw

Just normal processes on the host machine

container is not in the kernel, but namespace

Your whole machine is a container, each subsystem is  has a hierarchy(tree)

<img src="C:/Users/%E4%B9%90%E4%B9%90%E5%A4%A7%E5%93%A5%E5%93%A5/AppData/Roaming/Typora/typora-user-images/image-20220516150957779.png" alt="image-20220516150957779" style="zoom:50%;" />	

Ex中的数字代表PID

Each cgroup can visits certain pages and we can set limit on that



### **Workflow yaml file**

/.github/workflows/xxx.yml

- Events：listening for a event
- Jobs
- Runners：Container env that runs our code
- Steps
- Actions

在github项目的Actions中可以看到正在运行的workflow