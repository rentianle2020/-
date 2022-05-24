### 数组

具有编号且长度固定的元素序列

在Go中，相较于数组，用的更多的是切片(slice)

```go
//动态声明，默认零值
var a [5]int
a[0] = 10

//静态声明
var a [5]int{1,2,3,4,5}

//数组类型是一维的，但是可以组合;len(数组)获得长度
var a [2][3]int
for i := 0; i < len(a); i++ {
    for j := 0; j < len(a[0]); j++ {
        a[i][j] = i + j
    }
}
fmt.Println(a) //[[0 1 2] [1 2 3]] 
```



### Slice切片

Go中重要的数据类型，比数组更强大的交互方式

https://go.dev/blog/slices-intro

```go
//创建；元素 + len + cap
a := make([]string, 3)
a[0] = "a"
a[1] = "b"
a[2] = "c"

//切片!
b := a[1:3]
c := a[:]

//copy()
d := make([]string, len(a))
copy(d, a)

//append()
a := []string{"John", "Paul"}
b := []string{"George", "Ringo", "Pete"}
a = append(a, b...) // equivalent to "append(a, b[0], b[1], b[2])"
```



### Map

哈希表，键值对存储

```go
//创建
m := make(map[string]int)
//赋值
m["a"] = 1
//取值
v1 := m["a"]
fmt.Println(v1)
//长度
v2 := len(m)
fmt.Println(v2)
//删除
delete(m, "a")

//可以选择接收第二个返回值，标识是否存在key
x, prs := m["a"]
switch prs {
case true:
	fmt.Println(x)
default:
	fmt.Println("key not exist")
}

//如果不需要值，使用空白标识符(black identifier)占位
_, prs = m["b"]
fmt.Println(prs)
```



### Range遍历

range遍历自动接收参数

省略参数方式：一个参数可以使用blank identifier省略，第二个参数可以直接省略

```go
//

a := []string{"a", "b", "c"}
for i, str := range a {
	fmt.Printf("index : %d, value : %s\n", i, str)
}

m := map[string]string{"a": "apple", "b": "banana"}
for k, v := range m { //这里也可以只获取key
	fmt.Printf("key : %s, value  : %s\n", k, v)
}

s := "golang"
for i, c := range s {
	fmt.Printf("index : %d, value : %c\n", i, c)
}
```



### 排序

sort包：sort.Ints(nums), sort.Strings(strs), sort.IntsAreSorted(nums)...

自定义排序：传入一个接收下标返回bool的func()即可

```go
arr := []int{1, 5, 4, 7, 10}
sort.Slice(arr, func(i, j int) bool { return arr[i] > arr[j] })

strs := []string{"a", "b", "c", "e", "d"}
sort.Slice(strs, func(i, j int) bool { return strs[i] < strs[j] })

fmt.Println(arr)  //[10 7 5 4 1]
fmt.Println(strs) //[a b c d e]
```

