# GIT



### 本地仓库相关

`git status`：查看当前分支的情况，untracked（新建文件），staged（add之后），unmodified（commit之后），modified（更改之后）



`git init`：初始化git，生成文件夹中的.git文件

`git add . `：将所有更新的代码加入到暂存区

`git commit -m "备注"`：将所有暂存区代码，提交到本地库



`git log/reflog`：查看过去的提交

`git reset 哈希值`：回到之前的某个提交，不加哈希值就直接reset最近一次add



`git checkout 分支`：切换到指定分支

`git checkout -b 分支`：新建分支，并切换到该分支

`git branch`：查看本地分支，如果后边加上分支名，为新建分支

`git merge 分支`：将指定分支合并到当前分支

https://www.atlassian.com/git/tutorials/using-branches

https://community.atlassian.com/t5/Bitbucket-questions/What-does-the-behind-ahead-column-mean-in-the-branch-view/qaq-p/1191976



### 远程仓库相关

`git remote add origin https://github.com/rentianle2020/test.git`：关联远端仓库，**名为origin**

`git push -u origin master` 相当于 git branch --set-upstream origin master + git push origin master 也相当于 git push --set-upstream origin master

第一次push，将本地仓库master分支，绑定了origin远程仓库的master分支；此后只需简单git push即可



如果在gitee上建立了新的分支，想获取到本地

1. 在远端建立新分支
2. git fetch：获悉远端新的分支和新的更改
3. git checkout 新分支：自动将远端分支同步到本地仓库

或者在本地建立了新的分支想同步到远端

1. git checkout -b 新分支
2. add --> commit
3. git push（不成功！）
4. git push --set-upstream origin 新分支：在远端建立新分支，并将新分支推送至远端



`git pull` = git fetch当前分支的远端更新 + git merge到当前分支



**如果使用了VPN，导致SSL报错？？？**

git config --global http.sslVerify false



**使用SSH访问**

why？

https://jdblischak.github.io/2014-09-18-chicago/novice/git/05-sshkeys.html

how？

https://www.zhihu.com/question/21402411