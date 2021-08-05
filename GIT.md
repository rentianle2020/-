# GIT

git status：查看当前分支的情况，untracked（新建文件），staged（add之后），unmodified（commit之后），modified（更改之后）



git add . ：将所有更新的代码加入到暂存区

git commit -m "备注"：将所有暂存区代码，提交到本地库



git log/reflog：查看过去的提交

git reset 哈希值：回到之前的某个提交



git checkout 分支：切换到指定分支

git checkout -b 分支：在当前分支的基础上，新建分支，并切换到该分支

git branch：查看本地分支，如果后边加上分支名，为新建分支



git merge 分支：将指定分支合并到当前分支



如果在gitee上建立了新的分支，想获取到本地

1. 在远端建立新分支
2. git fetch：获悉远端新的分支和新的更改
3. git checkout 新分支：自动将远端分支同步到本地仓库



或者在本地建立了新的分支想同步到远端

1. git checkout -b 新分支
2. add --> commit
3. git push（不成功！）
4. git push --set-upstream origin 新分支：在远端建立新分支，并将新分支推送至远端



git pull = git fetch当前分支的远端更新，git merge到当前分支