# JavaScript + React



### 变量

声明：

- const不变
- let可变

数据类型：

- 字符串
- 整数，浮点数
- 布尔值
- 对象



### 对象

```javascript
const lastName = 'ren';
const fullName = `tianle ${lastName}` //模板字符串，将变量添加到string

const user = {
	firstName: 'Tyler',
	age: 23,
	isSmart: true,
    [lastName]: 'tianle' //使用变量作为属性名
}
```



### 数组

```react
//声明
const favoriteFruits = ['apple', 'orange', 27, true];
//添加
favoriteFruits.push('banana');
//遍历并以div形式添加到总div
const page = <div>
    {
        favoriteFruits.map((fruit, index) => {
            return <div>{index} - {fruit}</div>
        })
    }
</div>
```



### **解构（Destructuring）**

结构数组/对象中的变量，结构之后可以方便访问，或者传给子组件

好处

- 增加可读性
- 如果该变量需要被多次提取出来访问，提前置为local variable可以增加性能

```react
//数组
const names = ['tyler', 'lucky'];

const [firstName, secondName] = names;

const page = (
    <div>{firstName}<hr/>{secondName}</div>
)

//对象
const user = {
    name: 'Tyler',
    age: 23,
    sex: 'male',
    salary: 100
}

//拆解名需要对应对象的属性名
const {name, age} = user;

//嵌套对象 + rename属性
const user = {
    name: 'Tyler',
    age: 23,
    beseFriend: {
        name: 'lucky',
        age: 100
    }
}

const {beseFriend: {name: friendName, age: friendAge},...rest} = user;

const page = (
    <div>{friendName} is {friendAge} years old</div>
)
```



### Spread语法

1. 配合destructuring

   父组件通过spread语法传输对象，子组件使用spread语法接收prop

```react
//父组件
function Father(){
	const user = {
		name: 'Tyler',
    	age: 23,
    	sex: 'male',
    	salary: 100
	}
	
	return (
    	<div>
        	<User {...user}/>
        </div>
    )
}

//子组件
function User({name,age,...rest}){
    return (
    	<div>
        	<h1>User: {name}</h1>
            <p>{JSON.stringify(rest)}</p>
        </div>
    )
}
```

2. 将引用传递变为值传递

   和Java一样，JS也是Call by sharing：对象是引用传递，基本数据类型是值传递

```react
function Father(){
	const user = {
		name: 'Tyler',
    	age: 23,
    	sex: 'male',
    	salary: 100
	}
    
    //由于js对象引用传递，user2对name的更改同时影响了user的name
    const user2 = user
    user2.name = 'lucky'
    
    //将user中的所有属性，通过值传递赋值给user2, 可以再增加/覆盖属性
    const user2 = {
        ...user,
        name = 'lucky'
        mainLanguage: 'Java'
    }
}
```



### Local Storage

https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage



### .map()

遍历，操作，返回new Array。比for循环整洁且易读。

```javascript
const energyCostEuros = [140, 153, 164, 153, 128, 146]
const exchangeRate = 1.13

const energyCostDollars = energyCostEuros.map(function(euroCost){
    console.log(euroCost * exchangeRate)
})
```



### .join()

拼接Array元素，返回new string。

```javascript
const cssClassesArray = ['btn', 'btn-primary', 'btn-active', 'btn-sidebar']

const cssClassesString = cssClassesArray.join('🐶')
```



### return function inside function

```javascript
function getLottoNumbers(){
    const winningNums = []
    for (let i = 0; i < 6; i++){
        winningNums.push(Math.round(Math.random()*100))
    }
    return winningNums
}

function getWinningNumbersHtml(){
    return getLottoNumbers().map(function(num){
        return `<div class="number">${num}</div>`
    }).join('')
}

document.getElementById('winning-numbers').innerHTML = getWinningNumbersHtml()
```



### Array Constructor

初始化数组，每个元素默认undefined

```javascript
//init length
const goldCoins = new Array(1000)
```



### .fill()

初始化数组值

```javascript
const poisonMushrooms = new Array(1000).fill('🍄')
```



### Chaining

```javascript
const poisonMushrooms = new Array(10).fill('🍄').map(function(mushroom){
    return `<div class="box">${mushroom}</div>`
}).join('')
```



### Constructor Function + Method

定义构造函数，然后使用new的方式构造对象

```javascript
const animalForRelease1 = {
    name: 'Tilly',
    species: 'tiger',
    weightKg: 56,
    age: 2,
    dateOfRelease: '03-02-2022'
}

function Animal(data){
    this.name = data.name
    this.species = data.species
    this.weightKg = data.weightKg
    this.age = data.age
    this.dateOfRelease = data.dateOfRelease
    this.summariseAnimal = function(){
		console.log(`${this.name} is a ${this.age} year old 
		${this.species} which weighs ${this.weightKg}kg and was 
		released into the wild on ${this.dateOfRelease}`)
	}
}

const tillyTheTiger = new Animal(animalForRelease1)
tillyTheTiger.summariseAnimal()
```



### Obeject.assign

```javascript
const studentDetails = {
    firstName: 'janaka',
    lastName: 'siriwardena',
    age: 28,
    country: 'sri lanka',
    email: 'j.siri@totalinternet.com',
    discordUsername: 'JS1',
    } 
    
const studentDetailsCopy = {}

Object.assign(studentDetailsCopy, studentDetails) 
```

简化构造函数

```javascript
function Student(data) {
    // this.firstName = data.firstName
    // this.lastName = data.lastName
    // this.age = data.age
    // this.country = data.country
    // this.email = data.email
    // this.discordUsername = data.discordUsername
    Object.assign(this, data)
    this.summariseStudent = function () {
        console.log(`${this.firstName} ${this.lastName} is ${this.age} years old 
        and comes from ${this.country}. Their email is ${this.email} and you can find them on discord as ${this.discordUsername}`)
    }
}
```



### import & export

多个file互相依赖

```javascript
//export单个元素
export default function xxx(){}
export default xxxData
import xxxData from './data.js'

//多个元素
export function xxx(){}
export {aaa,bbb,ccc}
import {aaa,bbb,ccc} from './data.js'
```



### .reduce()

Give me just one thing! 将多个元素变为1个！

```javascript
const rainJanuaryByWeek = [ 10, 20, 0, 122 ]

//10作为inital value -> total; 20、0、122分别被放入currentElement
const totalRainfallJanuary = rainJanuaryByWeek.reduce(function(total, currentElement){
    
    return total + currentElement
})
```



### Arrow Function

new addtion to JS with ES6，简介的函数声明语法

普通函数访问surrunding context，箭头函数访问lexical scope。便于在对象的函数中访问this，但不能将对象的函数声明为箭头函数，否则就访问window了。



### setTimeout()

```javascript
const question = 'What is the capital of Peru?'
const answer = 'Lima!'

console.log(question)
//function & delay
setTimeout(()=> console.log(answer), 3000)
```



### Classes

ES6，将constructor function变成class

blueprint for creating object with same properties and access same methods

```javascript
class AdvertisingChannel {
    constructor(data){
        Object.assign(this, data)
        this.conversionRate = (this.conversions / this.clicks * 100).toFixed(1)
    }
    getAdvertisingChannelHtml(){
        const {site, adViews, clicks, conversions, conversionRate} = this
        return `
            <div class="site-name"> ${site} </div>
            <div>Views: ${adViews} </div>
            <div>Clicks: ${clicks} </div>
            <div>Conversions: ${conversions} </div>
            <div>Conv. Rate: <span class="highlight"> ${conversionRate} %</span></div> 
        `
    }
}
```



# API

不同程序之间交互的方式。

比如：

- 后端服务器向外界暴露的接口
- 程序自带的用来操作Array的公开方法



### JSON

key value pair，所有值都是字符串，其格式和一个JS object相似。



### fetch & then

```javascript
fetch('http://example.com/movies.json')
    .then(response => response.json())
    .then(data => console.log(data));
```

then会等待fetch结果，整个函数是异步执行的



### HTTP Requests

an agreed-upon way of transerring text over the internet

1. Path (URL)
   - Address where resouces "live"
   - BaseURL (won't change) + Endpoint (depends on resources)= Full URL
2. Method (GET, POST, PUT, DELETE...)
   - Getting data; Adding new data; Updating existing data; Removing data
3. Body (opthonal)
4. Headers (Meta info about the request)

```javascript
const options = {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            "Content-Type": "application/json"
        }
    }
    
    fetch("https://apis.scrimba.com/jsonplaceholder/posts", options)
        .then(res => res.json())
        .then(data => console.log(data))
```



### REST

A design pattern to provide a standard way for client and server to communicate

send and return JSON data

GET /bike ; GET /bike/:id

POST /bike

PUT /bike/:id

/bike/:id/reviews



### Query Strings (Params)

/bike?type=road&brand=trek



# "Asynchronous" JS

code that can run "out of order" without blocking other code from running int the meantime

JS不是真异步，而是利用了callback机制；真异步需要多线程支持，而JS是Single-threaded，only one command can run at a time



### Callback function

function is the first class object in JS

function可以像变量一样赋值，而不用加()

```javascript
function callback() {
    console.log("I finally ran!")
}
setTimeout(callback, 2000)
```



### 自己实现.filter() with callback

遍历，返回true的元素保留，返回newArray

```javascript
function filterArray(array, callback) {
    const resultingArray = []
    //filtering logic
    for (let item of array) {
        const shouldBeIncluded = callback(item)
        if (shouldBeIncluded) {
            resultingArray.push(item)
        }
    }
    return resultingArray
}
```



### Promises

As special object in JS has function then

An operation that normally takes time and eventually finish running

- Pending: has yet to be completed -> fetch(resource)
- Fulfilled: was completed as promised -> .then(callback())
- Rejected: was not completed as promised -> throw Error(" ") .catch(handleErr())



**链式编程**

filter() + map()

getElementId() + addEventListener()

上一个函数的返回值，继续调用下一个函数。Promise将上一个函数的返回值，作为下一个函数的callback()参数



### async / await

introduced in ES8

await：加在返回Promise的方法前，返回后再赋值



# Array

**主要函数**

- map()
- filter()
- reduce()
- some() / every()
- find() / findIndex()
- forEach()

- slice()
- concat()
- includes()
- array spread operator

```javascript
const newArray = [...oldArray]
const newArray = [...oldArray.slice(0,index)]
const newArray = [...oldArray1, ...oldArray2]
```



**遍历对象键值对**

JS对象又键值对组成

const keyArray = Object.keys(obj).map(...)

const valueArray = Object.values(obj).map(...)

const entryArray = Object.entries(obj).map(...)



**Set去重**

```javascript
//创建 
const s = new Set([1, 1, 2, 2, 3]);
console.log(s.size) //3

//返回去重后的数组 
const uniqueArray = [...new Set(oldArray)];
```

