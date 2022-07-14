package main

import (
	"bufio"
	"fmt"
	"os"
)

type MyReader struct {
	reader *bufio.Reader
}

func (r *MyReader) ReadLine() (line []byte, isPrefix bool, err error) {
	fmt.Println("start reading...")
	b, p, e := r.reader.ReadLine()
	fmt.Println("finish reading...")
	return b, p, e
}

func test(reader bufio.Reader) {
	b, _, _ := reader.ReadLine()
	fmt.Println(string(b))
}

func main() {

	f, _ := os.Open("hello.txt")
	r := MyReader{bufio.NewReader(f)}
	test(*r.reader)
}
