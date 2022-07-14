# React



### **Why React?**

1. 自定义component，"composable"可组合的

```react
function MainContent(){
  return (<h1>I'm learning React</h1>)
}
```



2. 声明式What should be done

```react
//使用React渲染dom，JSX
//1.渲染什么2.渲染到哪里
ReactDOM.render(<h1>Hello, React !!!</h1>,document.getElementById("root"))
```

传统js复杂！命令式How should it be done

```js
const h1 = document.createElement("h1");
h1.textContent = "This is an imperative way to program"
h1.className = "header"
document.getElementById("root").append(h1)
```



### **JSX**

JSX returns plain Javascript Object, React add those elements to real DOM for us.

```react
const element = (
    //注意所有元素只能放在同一个parent元素下
    <div>
        <h1>This is JSX</h1>
        <h1>This is JSX</h1>
    </div>
)
console.log(element); //React可以将JSX其变为一个JS Object，然后渲染到屏幕上

ReactDOM.render(
  element,
  document.getElementById("root")
)
```



### **what is React component？**

A function that returns React elements. (UI)

```react
function Page() {
    const reasons = ['less code', 'cool es6', 'better taste']
    const [FirstReason, SecondReason, ThirdReason] = reasons;
    return (
        <div>
            <header>
                <nav>
                    <img src="react-logo.png" width="100px"></img>
                </nav>
            </header>

            <h1>Reasons I'm excited to learn React</h1>

            <ol>
                <li>{FirstReason}</li>
                <li>{SecondReason}</li>
                <li>{ThirdReason}</li>
            </ol>

            <small>@ 2022 Ren development. All right reserveds</small>
        </div>
    )
}

ReactDOM.render(<Page />, document.getElementById("root"))
```



# Props

Props本身是一个object，向component中传入props，并在component中使用。

Props make component reuseable，就像往一个function中传参一样。



### .map()

convert raw data into an array of JSX elements, then display on page

```react
import data from './data' //raw data数组

const dataElement = data.map((item) => {
    return <Card
        key={item.id}
        img={item.coverImg}
        rating={item.stats.rating}
        reviewCount={item.stats.reviewCount}
        location={item.location}
        title={item.title}
        price={item.price}
    />
})
```



# Event Listener

https://reactjs.org/docs/events.html#mouse-events

```react
function handleClick(){
    console.log("I got clicked!!!")
}

function logNumber(num){
    console.log(num)
}

//可以直接调用一个函数
<button onClick={handleClick}> Click me </button>

//也间接调用一个函数,并传递参数(这个参数通常来自于props)
<button onClick={() => logNumber(props.num)}> Click me </button>
```



# State

state vs props

- props are data received from parents component，不应该被重新赋值，Immutable
- state are values managed by compoennet itself，可以根据各种事件进行改变



如果每次更新都要getElementById()，就又命令式了。而React是使用State绑定Element，使用hook在每次更新信息时更新。



### useState()

```react
const  [value, setValue] = useState("Hello")
```

参数

- 初始值"Hello"

返回值

- value: 值
- setValue(val)：用来更新value的函数



当使用旧值更新新值时，best practice is to update state with callback function

```react
function add(){
	setCount(function(prevCount){
		return prevCount + 1
	})
}
```

如果不需要使用到旧值，直接

```react
function add(){
	setCount(10)
}
```



### Lazy State Initialization

```react
//对于比较耗时的initialization，我们不希望它每一次rerender都被执行
const [state, setState] = React.useState(
	function() {
		return localStorage.getItem("key")
	}
    //() => localStorage.getItem("key")
)
```



# Conditional Rendering

如果判断条件过于复杂，就在外部定义let，并使用判断语句if...else statement

**Ternary三元表达式**

表达”true为一种显示，false为另一种显示“时使用

```react
<h1 onClick={changeMind}>
    {isGoingOut ? "Yes" : "No"}
</h1>
```

**&&短路**

表达”显示或不显示“时使用

```react
{props.openSpots === 0 && <p>SOLD OUT</p>}
```



# Props + State

父组件可以传props给子组件，兄弟组件之间不能互相传值

props could be function which change state in the parent compoenent；在子组件的eventHandler中的调用，来改变父组件中的state



除非需要向多个子组件传递相同的state（如下所示），其他情况Keep the state as close to component as it can ! 

```react
const [user, setUser] = React.useState("Bob")
    
return (
    <main>
        <Header user={user} />
        <Body user={user} />
    </main>
)
```



如果你发现自己在子组件中使用prop的值创建state，那么应该有更好的方法！

This is called derived state，multiple sources of truth，容易出问题。

如果传递过于复杂，就需要用到状态容器如Redux



# Dynamic Style

```react
const styles = {
    backgroundColor:"black" //camelCase!
}

<div style={styles}></div>
```



# Forms

> 处理表单元素是React中最麻烦的一个部分

在form填写的过程中，不断更新信息，**最后submit时直接提交信息对象**，而不是像原生JS那样在submit之前才开始收集信息。**使用表单元素的name更新它的value属性**（checkbox是更新checked属性）。最终添加**onSubmit事件和自定义的apiCall function**来trigger submit。



input标签本身hold value state，如果我们又在外边维护了一个state，所以会有two source of truth。

将表单元素的value绑定state，将其变为**controlled components**，然后每次onChange，我们改变state，以影响input中的value；single source of truth！

```react
function handleChange(event){
    const {name, value, type, checked} = event.target
    setFormData(prevFormData => {
        return {
            ...prevFormData,
            //将name更新为value，如果是checkbox，就更新为checked的boolean值
            [name]: type === "checkbox" ? checked : value
        }
    })
}  
```

- [x] 


### textarea

```react
//原本在tag中间的输入就是value
<textarea>value here</textarea>

//change to self closing element，用React控制value
<textarea 
    value={formData.comments}
    placeholder="Comments"
    onChange={handleChange}
    name="comments"
/>
```



### **checkbox**

```react
<input 
    name="isFriendly"
    type="checkbox" 
    id="isFriendly" 
    checked={formData.isFriendly}
    onChange={handleChange}
/>
//使用htmlFor属性，将label绑定至checkbox
<label htmlFor="isFriendly">Are you friendly?</label>
```



### radio

name同一，将多选变为单选

```react
<input 
    type="radio"
    id="part-time"
    name="employment"
    value="part-time"
    checked={formData.employment === "part-time"}
    onChange={handleChange}
/>
<label htmlFor="part-time">Part-time</label>

<input 
    type="radio"
    id="full-time"
    name="employment"
    value="full-time"
    checked={formData.employment === "full-time"}
    onChange={handleChange}
/>
<label htmlFor="full-time">Full-time</label>
```



### select

相比起在html中给选中的option加selected属性，我们可以在select中添加选中option的value

```react
<select 
    id="favColor"
    value={formData.favColor}
    onChange={handleChange}
    name="favColor"
>
    <option value="">-- Choose --</option>
    <option value="red">Red</option>
    <option value="orange">Orange</option>
    <option value="yellow">Yellow</option>
</select>
```



### submit

在form中的<button>自动作为submit buttom

```react
function handleSubmit(event) {
    event.preventDefault()
    // submitToApi(formData)
    console.log(formData)
}

<form onSubmit={handleSubmit}>
	<button>Submit</button>
</form>
```

​	

# useEffect()

sync states with outside effects like localstorage, API call...

```react
React.useEffect(() => {},[count])
```

参数

- function run after first render and every re-render (assuming no dependencies array)
- dependencies数组，当这个数组中任意dependency的值有改变时，才会调用首个参数中的function。如果只想在first render中执行function，则放一个[]空的dependency array。



### side effect

**React's primary tasks**

- Work with DOM and render the page
- Manage state for us between render cycle
- Keep the UI updated when state changes occur



**(Out)side effect : React cant handle**

- localStorage
- API/database interactions
- Subscriptions(e.g. web sockets)
- Syncing 2 different internal states together



直接调用side effect

1. Get the data (fetch, axios)
2. Save the data to state

如果在Component中直接调用fetch并update state，会进入死循环 -> update，再fetch，再update...

如果没有dependency array，useEffect也是如此...



### clean up

在useEffect()中没有clean up就unmount component，会出现memory leak！

```react
//如果不clean up，这个EventListener会在component unomount后依然监听window size
React.useEffect(() => {
    function watchWidth(){
        console.log("resizing...")
        setWindowWidth(window.innerWidth)
    }
    window.addEventListener("resize", watchWidth)
    
    //clean up
    return function () {
        console.log("remove listener")
        window.removeEventListener("resize",watchWidth)
    }
}, [])
```



**not use async function in useEffect callback function directly**

因为async function自动return一个Promoise对象，如果useEffect的function有返回值，必须是一个clean up function；如果非要使用async function，需要把它单独声明，然后再调用一下





# Class components

Class只是基于prototype chains的“语法糖”

Class components

```react
export default class App extends React.Component {
    render() {
        return (
            <h1>{this.props.type} component</h1>
        )
    }
}
```



### State

```react
//state必须保存在一个名为state的对象中
state = {
        goOut: "Yes"
    }

//使用this.setState在function中改变state，必须使用arrow function
toggleGoOut = () => {
    this.setState(prevState => {
        return {
            goOut: prevState.goOut === "Yes" ? "No" : "Yes"
        }
    })
}
```



### componentDidMount()

 初始化state -> API call & setState



### componentDidUpdate()

处理更新state后的事情

```react
componentDidUpdate(prevProps, prevState) {
    if(prevState.count !== this.state.count) {
        this.getStarWarsCharacter(this.state.count)
    }
}
```



### componentWillUnmount()

Clean up side effects



### 其他

1. shouldComponentUpdate()
2. static getDerivedStateFromProps() -> 最好不要使用derived state
3. getSnapshotBeforeUpdate()



# Reusability

DRY -> Inheritance & Composition (recommand)

1. Components
2. Children
3. Heigher-order Components
4. Render props



### Children

```react
//使用非self-closing的component
<CTA>
    <h1>This is an important CTA</h1>
    <button>Click me now or you'll miss out!</button>
</CTA>

//然后在component中调用props.children获取
function CTA(props) {
    return (
        <div className="border">
            {props.children}
        </div>
    )
}
```

什么时候使用？

> `children` prop is something that you use when the structure of what needs to be rendered within the child component is not fixed and is controlled by the component which renders it.



### Higher Order Components

设计模式

function that accept component as its first arg and return a "super powered" component

```react
const upgradedConponent = withSuperPower(Component)
export default upgradedComponent
//然后在withSuperPower这个组件中定义一些通用的states和functions，然后传递给Coomponent使用
```



### Render  Props

> The term [“render prop”](https://cdb.reacttraining.com/use-a-render-prop-50de598f11ce) refers to a technique for sharing code between React components using a prop whose value is a function.

设计模式

- 组件1将自己定义为一个函数，并用组件2包裹，将自身传到组件2的props.children中

  ```react
  return <Component2>
  	(value) => (
      	<p>{value}</p>
      )
  </Component2>
  ```

- 组件2管理state，调用props.children(state/function)，以此来给组件1提供state/function

  ```react
  const value = React.useState(10086)
  return props.children(value)
  ```

  



# Performance



### React's Tree Rendering

一个组件更新，影响所有以它为root的subTree。



### Shallow Comparison

JS中用===只能比较基本数据类型，而不能比较对象（引用传递）

Shallow Comparison是比较对象中的每一个first-level基本数据类型

Deep Comparision是递归比较每一个基本数据类型

```react

const arr1 = [1, 2, 3, [4]]
const arr2 = [1, 2, 3, [4]]

const state = {
    favNumber: 42,
    name: "Bob",
    address: {
        street: "123 Main Street",
        city: "Nowhere, PA",
        zip: 12345
    }
}
```



### PureComponent

代替React.Component中的shouldComponentUpdate()

但是没必要，现在都使用Functional Component



### React.memo()

利用HOC，将组件缓存；每次更新时比较缓存，决定是否rerender

```react
export default React.memo(function Parent() {
    return (
        <div>
            <p>I'm a Parent Component</p>
            <Child />
            <Child />
        </div>
    )
})

//export default React.memo(Parent)
```



# Context

Can't pass data to sibling, you can only pass data downwards through props.

导致props drilling

如果只有一两层，别用；React甚至就不推荐使用Context解决props drilling问题

> https://reactjs.org/docs/context.html 在最高组件中建立最低组件，然后一路传下去

> Context provides a way to pass data through the component tree without having to pass props down manually at every level. 



### Context Provider

在所有Concumer的LCA component中使用Provider

```react
//themeContext.js
import React from "react"
const ThemeContext = React.createContext()
export default ThemeContext

//index.js
import themeContext from './themeContext'
ReactDOM.render(
    <ThemeContext.Provider value={"light"}>
        <App />
    </ThemeContext.Provider>, 
    document.getElementById("root")
)
```



### Context Consumer

```react
<ThemeContext.Consumer>
    {theme => (
        <Button theme={theme} />
    )}
</ThemeContext.Consumer>
```



# useRef()

获取DOM element的方法，不用原生JS的getElementById是为了避免相同id

```react
//初始化为null
const inputRef = useRef(null)

//渲染时绑定input DOM
<input ref={inputRef} type="text" name="todo" value={newTodoValue} onChange={handleChange}/>
```

绑定后的inputRef是一个对象，调用inputRef.current.xxx()即可调用函数操作DOM

```react
{current: <input type="text" name="todo" value>}
```



如果disabled是depends on一个set function，那要注意这个set是异步的。

```react
setIsTimeRunning(true)
//如果不手动取消disabled，那么focus会失败！
textBoxRef.current.disabled = false
textBoxRef.current.focus()
```



# useContext()

```react
const context = useContext(ThemeContext)
```



# 自定义Hook

就是普通的function，简单实现State/Function在多个组件中的复用

> 关于JS的Default parameters，避免参数出现undefined
>
> https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Functions/Default_parameters

```react
//自定义
function useCounter(defaultValue = 0) {
    const [count, setCount] = useState(defaultValue)
    
    function increment() {
        setCount(prevCount => prevCount + 1)
    }
    
    //返回数组是为了让使用者Destructuring时可以自定义变量名
    return [count, increment]
}

export default useCounter

//使用
const [number, add] = useCounter()
```



# React Router

https://v5.reactrouter.com/web/guides/quick-start

Single-page Application：1次请求，返回整个React APP，Conditional Rendering the page



### BrowserRouter

根组件中使用，BrowserRouter本质是一个ContextProvider，我们的DOM因此可以Consume它提供的State & Function



### Link

点击Link跳转路径



### Switch & Route

Switch类似switch statement，根据当前路径渲染Route

path的默认匹配方式是前缀匹配，添加exact关键字将匹配方式改为精确匹配



**useParam()**

Link中使用":xxx"的方式根据param跳转路径，path="/services/:serviceId"

Component中使用useParams()获得路径中的serviceId



**useRouteMatch()**

使用nested route的时候，子组件路径严格依赖父组件路径；因为如果父组件的/user不被渲染，那么子组件的/user/info肯定也不会得到渲染。当父组件路径被更改为/profile，子组件也要手动更改为/profile/info。

useRouteMath()得到path,url,exact信息，可以在nested route中拼接前缀，这样父组件更改路径也不需要更改子组件路径的前缀了



**useHistory()**

programmactically jump back and forth

- push(path) 编程式跳转路径
- go(-3) 向前/后跳转几个页面
- replace(path) 不向history中增加路径，而是直接代替最新路径（防止用户回到invalid page）



**useLocation()**

获取pathname和queryString



### Redirect

通常用于authentication，如果没有登录，就Redirect到登录页面

https://v5.reactrouter.com/web/example/auth-workflow

```react
<div>
    <Redirect to="/whatever" />
</div>
```





# 其他

### 传递匿名函数

```react
<Die holdDice={() => holdDice(die.index) />
 
//等价于 全传过去然后在子组件中调用
<Die index={die.index} holdDice={holdDice} />

function handleClick(){
	props.holdDice(props.index)
}
```



### 遍历Array

```react
const win = dice.every(die => die.isHeld === true && die.value === firstValue)
```



### useEffect() + setTimeout()

setTimeout在指定时间后执行一个函数，执行setState，然后可以让useEffect再次调用

```react
useEffect(() => {
    if(timeRemaining > 0) {
        setTimeout(() => {
            setTimeRemaining(time => time - 1)
        }, 1000)
    }
}, [timeRemaining])
```



### PropTypes

https://reactjs.org/docs/typechecking-with-proptypes.html#proptypes

```react
import PropTypes from "prop-types"

Button.propTypes = {
    theme: PropTypes.oneOf(["light", "dark"])
}

Button.defaultProps = {
    theme: ""
}
```

