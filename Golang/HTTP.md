Go标准库net/http包，为HTTP客户端和服务端提供了出色的支持



### 客户端

```go
resp, err := http.Get("https://jsonplaceholder.typicode.com/todos/1")
defer resp.Body.Close()

scanner := bufio.NewScanner(resp.Body)
for scanner.Scan() {
	fmt.Println(scanner.Text())
}
```



### 服务端

```go

func helloHandler(w http.ResponseWriter, r *http.Request) {
    //向返回体中写入
	fmt.Fprintf(w, "hello my friend\n")
}

func main() {
    //请求路径为/hello，执行helloHandler函数
	http.HandleFunc("/hello", helloHandler)
    //监听8081端口
	http.ListenAndServe(":8081", nil)
}
```



### Context

carries deadlines, cancellation signals, and other request-scoped values across API boundaries and between processes.

这里以Request Context为例

服务器运行，收到请求断开信号，可以Context.Done()通道中读取到一个空struct

```go
func helloHandler(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	select {
	case <-time.After(10 * time.Second):
		fmt.Println("after 10 sec")
	case <-ctx.Done():
        fmt.Println("server:",ctx.Err())
	}
}

func main() {
	http.HandleFunc("/hello", helloHandler)
	http.ListenAndServe(":8081", nil)
}

//curl http://localhost:8081/hello
// ^C
//控制台打印 server: context canceled
```

