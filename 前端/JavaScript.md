# JavaScript



**关键词**

var：声明基本数据类型变量/对象/数组

typeof：查看变量了类型

function：声明函数/方法

import, export

let，const



将对象转化为JSON：JSON.stringify(person)

将对象转化为数组：Object.values(person)



JS文档

https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference



## Function





## **Array**

**属性&方法**

names.length



**Traversal**

```javascript
for(var n of names){
    console.log(n)
}

names.forEach(function(n,index){
    console.log(n + " " + index)
})
```



## Loop

和Java语法一样：for, while, do while, break, continue



## 条件语句

和Java语法一样：if, else if, else, switch



## 判断语句

和Java语法一样：==, !=, <, >, &&, || 



==的时候，会发生类型转换（type coercion）：0 == false, "1" == 1

最安全的比较方法是使用===



## 单引号&双引号&分号

保持一致性即可，没有强制要求加分号，或者使用单or双引号



### Map | Filter | Reduce

最常用的操作数组的方法

```js
var array = [0,1,2,3,4,5]

var map = array.map(function(n){
    return n * 2;
}) //2,4,6,8,10

var filter = array.filter(function(n){
    return n < 3
}) //1,2

var reduce = array.reduce(function(accumulator,current){
    return accumulator + current
    
}) //15
```



## Callbacks

function parse into another function, called at certain time

```javascript
function callbackExample(name, callback){
    console.log(callback(name))
}

callbackExample("Tyler",function(name){
    return "Hello " + name
})
```



## Exports / Import

import所有：import * as X from "./x"

import指定：import {x,y,z} from "./x"

export多个value：export {x,y,z}

export一个value/对象：export default x



## Let

var全局变量

let局部变量，循环中使用



## Const

reasign is not avliable，类似Java的final关键字

对象和数组中的元素都可以改变，但是指针不能变了



## 字符串拼接模板

使用 ${变量} 代替 + 拼接

```javascript
const name = "tyler"
var output = `my name is ${name}`;
```



## Spread Operator

使用`...`将数组和String中的元素提取出来

```javascript
const arrayOne = [1,2,3]
const arrayTwo = [4,5,6]
const concat = [...arrayOne, ...arrayTwo];
console.log(concat) //1,2,3,4,5,6

const name = 'tyler'
const nameToArray = [...name]
console.log(nameToArray) //'t','y','l','e','r'

const numbers = [1,5,9]
const addition = addNumber(...numbers)
console.log(addition) //15

const name = {
    firstName: 'Tyler',
    lastName: 'Ren'
}
const address = {
    country: 'China',
    city: 'Beijing',
}
const person = {...address,...name}
console.log(JSON.stringify(person)) 
//{"country":"China","city":"Beijing","firstName":"Tyler","lastName":"Ren"}
```



## Arrow Function

将function(){}，改为() => {}

如果方法体只有一行，return关键字可以省略

类似Java的lambda



## this

const指向自己，不能使用Arrow Function，要使用functin(){ console.log(this.name) }



## 高级对象

```javascript
const person = (name,age) => {
    return {
        name,
        age,
        print(){console.log("print")}
    }
}
const p = person('tyler',15)
console.log(p)  //{name: "tyler", age: 15}
p.print() //print
```



## Descruturing

不再需要用下标一个个获取数组元素了！

```javascript
const numbers = [1,2,3,4,5]
const [first,second,...rest] = numbers
console.log(first) //1
console.log(second) //2
console.log(rest) //[3,4,5]
```

可以直接从对象中提取多个属性值了！

```javascript
const person = {
    name: 'tyler',
    family: {
        mother: 'mom',
        father: 'papa'
    }
}
const {name,family : {mother}} = person
console.log(name) //tyler
console.log(mother) //mom
```



## Function Default Param

```JavaScript
const calculatePay = (yearSalary = 10000, bonus = 500) => yearSalary + bonus
console.log(calculatePay()) //10500
```



## OOP

class，extends，static method

和Java一样，只不过不用指定返回值类型和参数类型



## Promises

状态：Pending、Fulfilled、Rejected

如果Fulfilled，就可以继续then

如果Rejected，就可以catch错误



promise.all：所有promise必须别fulfil，否则catch

fetch()返回的结果就是一个Promises

https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch



## Generator

function* () { yield 1}

使用generator.next()获得下一个yield，有boolean done和value两个属性

类似Iterator

coroutine + generator + promise没有太懂...



## Async

The `async` and `await` keywords enable asynchronous, promise-based behavior to be written in a cleaner style, avoiding the need to explicitly configure promise chains.

The `await` operator is used to wait for a [`Promise`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise). 

It can only be used inside an [`async function`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/async_function) within regular JavaScript code

并使用try catch来handle error
