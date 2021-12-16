# BFS

基本问题：

1. 如果复杂程度类似， 面试中尽量优先使用BFS

2. BFS主要几种场景： 层级遍历，拓扑排序，图上搜索（包括二叉树，矩阵）

3. Queue的使用技巧，BFS的终止条件？

4. 什么时候使用分层？什么时候不需要？实现的时候的区别在哪里？

   > 当一层代表一步 + 我们需要求最短/最长步数时，需要分层，每一层开始求一个size然后再for循环去poll

5. 拓扑排序的概念？如何判断是否存在拓扑排序？是否存在唯一的拓扑排序？找到所有拓扑排序？

   > 每一个节点都有一些indegree，当一个节点的indegree为0时，他就可以加入Queue并开始拓扑他的关联节点。 每层Queue中都只有一个节点的话，代表它就是唯一拓扑排序。

6. 什么时候需要使用set记录访问过的节点？（为什么二叉树上的BFS往往不需要set？）什么时候需要map记录到达过的节点距离？

   > 当可能出现重复访问的时候，就使用一个set保存访问过的节点，防止duplicate。

7. 如何在矩阵中遍历下一步的所有节点？如果每次可能走不止一步怎么办（Maze II）？

   > 使用一个dirs二维数组，判断是否能向上下左右走，可以的话就将下一步入队。
   >
   > 不止走一步的话，就需要升级为Dijkstra问题了。

8. 为什么BFS解决的基本都是简单图（边长为1）问题？如果边长不为1，该怎么办？

   > 为了让每一层的价值都是相等的，所以最好用来解决无权图问题。如果不为1，则最好升级成Dijkstra，使用PQ，优先取出最短/最长的路径。

9. BFS的时空复杂度估算？

   > O(n+e)
   >
   > n代表节点数量，e代表边数量

10. 如何使用双向BFS进行优化？

    > 创建三个队列，从头开始的队列，从尾开始的队列，和一个next队列保存下一层的节点。
    >
    > 永远从size较小的队列开始寻找下一层元素，直到头尾相遇，返回count/step。



### 二叉树

> 边界条件：序列化时root为null返回null，反序列化时string为null返回null
>
> 序列化：有值，左右进q；无值，"n"占位，左右不进q
>
> 反序列化：q中的所有元素都有值，index指向它的左节点，index++后指向右节点，左右节点不为"n"则再次进q；index再++，指向下一个poll出来的有值元素的左节点

- [x] **297.Serialize and Deserialize Binary Tree**

- [x] *102.二叉树的层序遍历*
- [x] *103.二叉树的锯齿形层序遍历*

- [x] *107.二叉树的层序遍历 II*

- [x] *513.找树左下角的值*
- [x] LintCode-242.将二叉树按照层级转化为链表



### 拓扑排序

> 建立图，保存from Edge 和 to List<Edge>之间的关系
>
> 建立入度表，保存所有Edge的入度数量
>
> 从入度表中取出入度为0的Edge，加入到Queue中遍历与它关联的Edge，让新的Edge入度变为0，从而找到一种拓扑路径

- [x] **Lint-127. Topological Sorting**

- [x] *207.课程表*
- [x] *210.课程表 II*
- [x] *269.Alien Dictionary*
- [x] 444.Sequence Reconstruction



### 矩阵

> 建立dirs二维数组代表往上下左右走
>
> 从矩阵的起点开始放入queue，试着向上下左右走，然后再将下一步放入queue
>
> 为了避免重复visit，使用二位数组保存访问过的地点

- [x] **200.岛屿数量**
- [x] *490.迷宫*
- [x] *505.迷宫 II*

- [x] *542.01 矩阵*

- [x] *733.图像渲染*

- [x] *994.腐烂的橘子*

- [ ] 305.Number of Islands II

- [x] 773.Sliding Puzzle

- [ ] Lint-573.Build Post Office II

- [ ] Lint-598.Zombie in Matrix

- [ ] Lint-611.Knight Shortest Path

- [ ] Lint-794.Sliding Puzzle II



### 图

- [x] **133.克隆图**
- [x] *127.Word Ladder*
- [x] *261.Graph Valid Tree*
- [x] *841.Keys and Rooms*
- [x] 323.Number of Connected Components in an Undirected Graph
- [ ] 1306.Jump Game III
- [ ] Lint-531.Six Degree

- [ ] Lint-618.Search Graph Nodes

- [ ] Lint-624.Remove Substrings



