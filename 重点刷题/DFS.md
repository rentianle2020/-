# DFS

基本问题：

1. DFS中递归的基本要素

2. 终止条件的选择；回溯；剪枝

3. 什么时候需要排序？

   > 顺序和下标不重要，但是不能出现重复结果时，先进排序！

4. 如何去除重复元素？一个元素允许使用多次的情况？

   > 保证大哥在前小弟在后的顺序，如果nums[i] == nums[i - 1] && !used[i - 1]，i直接被跳过，保证1a 1b不会出现1b 1a的情况。

5. 在图上进行DFS如何避免回到重复节点

6. 识别一个隐式图，并使用DFS

7. 在某些情况下，利用记忆化搜索进行优化



### 排列组合

> subsets：每个元素可以选可以不选
>
> permutations：每个元素都得选，顺序不同
>
> combination：选取不同元素的组合，满足特定要求，顺序不重要(123和321算一个答案，不能重复)
>
> 时间复杂度，复制到res需要n，排序需要nlogn
>
> 排列的所有状态和为n!（第一个位置有n种选择，第二个位置有n-1种...）
>
> 子序列&组合的状态和通常为2^n(每个元素有两个状态，选或者不选)，或者是C(5,3)代表5个里边选3个有多少种状态。
>
> 空间复杂度看递归深度

- [x] **39.Combination Sum**
- [x] **40.Combination Sum II**
- [x] **46.Permutations** 
- [x] **47.Permutations II**
- [x] **77.Combinations** 
- [x] **78.Subsets** 
- [x] **90.Subsets II**
- [x] *17.Letter Combinations of a Phone Number*
- [x] *22.Generate Parentheses*
- [x] *51.N-Queens*

- [x] *254.Factor Combinations*
- [x] *301.Remove Invalid Parentheses*
- [x] *491.Increasing Subsequences*
- [ ] 37.Sudoku Solver

- [ ] 52.N-Queens II

- [ ] 93.Restore IP Addresses

- [ ] 131.Palindrome Partitioning

- [ ] Lint-10.String Permutation II

- [ ] Lint-570.Find the Missing Number II

- [ ] Lint-680.Split String





### 二叉树

- [x] *113.Path Sum II*
- [x] *257.Binary Tree Paths*
- [x] *Lint-246.Binary Tree Path Sum II*
- [x] *Lint-376.Binary Tree Path Sum*
- [ ] *Lint-472.Binary Tree Path Sum III*



### 图

- [x] *140.Word Break II*
- [x] *494.Target Sum*
- [ ] *1192.Critical Connections in a Network*
- [ ] 126.Word Ladder II
- [ ] 290.Word Pattern
- [ ] 291.Word Pattern II

