# Vue

1. 组件化：提高代码复用

2. 声明式：无需操作DOM，一行一行命令，而都被封装好了

3. 虚拟化DOM：数据 --> Virtual-DOM --> Real-DOM

   很像把DOM封装成对象，添加一个数据相当于添加一个对象，而不是傻瓜式的替换所有数据



**Vue参考了MVVM模型**

将数据Model和视图模型View，通过ViewModel(Vue实例对象)进行了连接

<img src="https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210829230855.jpg" alt="vue-the-progressive-framework" style="zoom:67%;" />	

data中所有的属性都给了vm，vm身上的所有属性 以及 Vue对象上所有属性，都可以在模板中使用



**响应式编程原理**

一个Dependency class，方法的容器

使用watcher(func)函数，统一将匿名函数注入容器



遍历data对象每一个属性，new Dep()，get()方法中，将watcher函数直接注入容器，返回data

set方法中，调用dep.notify()来Re-run stored funtions



这样一来，我们第一次get，将匿名函数注入容器（函数会在watcher中被调用一次），返回data属性值

每次set，都会重新调用一遍容器中的所有匿名方法，自动更新对象相关的方法

```js
let data = { price: 5, quantity: 2 }
    let target = null
    
    // This is exactly the same Dep class
    class Dep {
      constructor () {
        this.subscribers = [] 
      }
      depend() {  
        if (target && !this.subscribers.includes(target)) {
          // Only if there is a target & it's not already subscribed
          this.subscribers.push(target)
        } 
      }
      notify() {
        this.subscribers.forEach(sub => sub())
      }
    }
    
    // Go through each of our data properties
    Object.keys(data).forEach(key => {
      let internalValue = data[key]
      
      // Each property gets a dependency instance
      const dep = new Dep()
      
      Object.defineProperty(data, key, {
        get() {
          dep.depend() // <-- Remember the target we're running
          return internalValue
        },
        set(newVal) {
          internalValue = newVal
          dep.notify() // <-- Re-run stored functions
        }
      })
    })
    
    // My watcher no longer calls dep.depend,
    // since that gets called from inside our get method.
    function watcher(myFunc) {
      target = myFunc
      target()
      target = null
    }
    
    watcher(() => {
      data.total = data.price * data.quantity
    })
```

> watcher设置target为myFunc后，调用target方法初始化值
>
> 这时data.price会调用price的get，data.quantity会调用quantity的get
>
> get方法中调用了dep.depend()，将target函数分别植入到两个属性的容器中
>
> 这样，每当price或quantity的set函数调用，每一个与其关联的存储在dep中的函数，都会被dep.notify()调用。在本例中，就是会将data.total被重新赋值

![img](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210830184534.png)



# 官方教学

https://www.vuemastery.com/courses/intro-to-vue-js/vue-instance



**绑定数据**

{{}} Expression，js表达式

HTML：{{reference}}

JS：new Vue({el:"",data: {xxx:xxx} })

- el绑定div，data定义数据，{{}}根据key引用数据value

响应式，js中的Vue对象改变，会改变页面中渲染的数据



**属性绑定**

v-bind: --> :



**条件渲染**

v-if=""

v-else-if=""

v-else



v-show="" --> 对于需要频繁显示和消失来说，更高效，它只是将style="display: none"



**列表渲染**

HTML列表：v-for="detail in details" {{detail}}

JS：details:[a,b,c]

渲染列表时建议使用:key给每一个节点绑定id值



**事件绑定**

HTML按钮：v-on:click="addToCart" --> @click

JS：methods：{addToCart: function(){ this.cart += 1}}



@mouseover，@submit表单，@keyup.enter --> 这个enter叫做modifier



**Class&Style绑定**

:style="{ backgroundColor: variant.variantColor } --> 冒号左边的是css Style，右边是data中的数据

:class="{ disabledButton: !inStock}" --> 冒号左边是css Class，右边是data中的数据（条件），根据条件动态改变样式



**计算属性**

computed{ title(){ return this.brand + " " + this.product} }

依赖的属性改变时，组合属性也会变化



**组件**

Components within component，让项目组件化

```javascript
Vue.component('product',{
    props:{
        premium:{
            required:true,
            type:Boolean,
            default:false
        }
    },
    template:
    `
		<div>xxxx</div>
	`,
    data(){
        return {
            xxx:xxx,
            xxx:xxx
        }
    },
    methods:{
        add(){},
        delete(){}
    },
    computed:{
        name(){},
        inventory(){}
    }
})
```

在html中使用<product>即可引用组件

使用组件标签属性，将更高层组件的data引用到当前组件

使用props，自定义type、required、default

在高层组件中，就可以使用底层组件的标签，给标签传入对应的属性，不用关心具体组件的实现



**事件交流**

本来onclick调用本模块的方法，但是我们向调用更高级模块的方法

使用this.$emit("signal")向外发出事件信号，在标签属性上接收@监听事件，并调用外界方法



**双向数据绑定**

v-model

用户输入到表单中的内容，会更改组件对象的data，当提交的时候，我们就可以根据data来提交内容了！



**全局数据传输**

让grandchild组件传数据给grandparent组件

var eventBus = new Vue();

emit的时候让eventBus.$emit，然后将grandparent组件mount(){eventBus}，即可



# Vue CLI

Command Line Interface

Filesystem for rapid Vue.js development



使用Node.js安装@vue/cli

create创建项目（也可以用vue ui），serve启动项目，build打包项目

启动：main.js渲染App.vue并挂载到index.html中id=app的div中

打包：webpack将我们的文件压缩，生成可以被部署的dist文件夹，script src="被压缩后的js文件"



**工程结构**

node_modules各种搭建Vue工程的依赖

public不想被webpack打包的资源

src项目目录

- assets，图片等资源
- components，vue组件
- views，不同的pages
- App.vue，根组件
- main.js，渲染App并将其挂载到DOM



**vetur**

ctrl+p快速搜索文件

Syntax&Semantic Highlighting：语法高亮

Snippet片段：快速生成文件模板

Emmet：快速生成标签



**Vue VSCode Snippets**

von，vdata，vprops...各种快捷键



# Router

服务端路由 Server-Side Routing：基本的请求和返回

客户端路由 Client-Side Routing：route from page to page，这就是Vue Router做的事情

在哪里引入？package.json, main.js引用router

在哪里使用？router包 --> Vue.use(VueRouter)

```javascript
const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/about",
    name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/About.vue"),
  },
];
```

put compotents that get loaded by Vue Router in the Views directory

将.vue组件加载import到Router中，然后定义路由。就可以在其他组件中router-link引入路由了

在View文件中，使用router-link to=“/"标签添加组件link，将显示结果router-view标签的位置



客户端路由的过程中，没有发生任何网络请求，而是根据路径渲染指定的component到页面

使用name routes的话，如果后期我们改变url，也不会影响模块的渲染



如果想改变路径

方法1：在router.js中设置redirect

方法2：在router.js中设置alias（如果在意SEO网页优化，two page same comtent不太好）



npm run serve 部署程序在8080端口



**Dynamic Routes 动态路由**

路由中使用冒号，props true可以让模块调用时通过props传参，将路径动态的改为参数值

```javascript
{
    path: "/user/:username",
    name: "user",
    component: User,
    props: true,
  },
```

在User.vue中通过props拿到更高层的username数据

```javascript
<template>
  <h1>This is {{ username }}'s page</h1>
</template>

<script>
export default {
  props: ["username"],
};
</script>
```

在顶级模块，App.vue中引用User.vue，并提供username参数

```javascript
 <router-link :to="{ name: 'user', params: { username: 'Tyler' } }"
```



**HTML5 History Mode 历史模式**

`vue-router` 默认 hash 模式 —— 使用 URL 的 hash 来模拟一个完整的 URL，于是当 URL 改变时，页面不会重新加载。

如果不想要很丑的 hash，我们可以用路由的 **history 模式**

```javascript
const router = new VueRouter({
  routes,
  mode: "history",
});
```



**Single File Components**



vue刨析：template身体，script大脑，style衣服

export default{}，将本组件输出为一个JS单元，可以从其他组件import __ from __

Style标签中的scope属性负责将style的作用域设定为当前component，如果没有scope则为全局style



Nested Component组件套娃



**Global Components**

公共组件应该被设为全局组件，就不用一个个import

在main.js中引入即可注册为全局组件

```javascript
import BaseIcon from "@/components/BaseIcon.vue";

Vue.component("BaseIcon", BaseIcon);
```

如果很多组件，不需要一个个手动import，可以使用表达式的方式自动注册



**Slots**

通过<slot>将个性化的模板放到全局component标签中

例子：一个按钮，中间写submit，save，Purchase for $12.99...

如果有多个slot，需要使用Named Slots --> <slot name="heading"> --> <h1 slot="heading">



**API Calls with Axios**

![image-20210902180703614](C:/Users/%E4%B9%90%E4%B9%90%E5%A4%A7%E5%93%A5%E5%93%A5/Desktop/%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/assets/image-20210902180703614.png)

异步请求，不会等待返回，而是直接向下执行



声明周期created(){

axios.get('')

}



**Service封装**

我们不希望Axios实例遍布在各个组件，而是使用Service将请求地址封装



# Vuex

- Single Source of Truth for your state
- Common Libaray of Aciotns,Mutations,Getters

```javascript
new Vuex.Sotre({
    actions:{}, //包含mutation/state/getters和Axios的整体业务逻辑
    mutations:{}, //改变state的方法,addCategory(state,cat){state.categories.push(cat)}
     state:{}, //公共信息/状态,categories:['a','b','c']
    getters:{} //从state中提取信息，state.categories.length
})
```



**State + Getters**

State和Router一样，在main.js中引入后，所有component均可使用/store/index.js中的state

在export default中定义state/getters后，可以通过mapState的方法，简洁的获取state数据

```javascript
<script>
import { mapState, mapGetters } from "vuex";

export default {
  computed: {
    ...mapState(["user", "categories"]),
    ...mapGetters(["catLength"]),
     userName(){
         return this.$store.state.user.n
     }
  },
};
</script>
```



**Mutations & Actions**

Muatations是同步的，需要按顺序hi行：$store.commit('MUTATIONS'，参数)

Actions是异步的，整合Mutations和State为一个整体业务逻辑：$store.dispatch('action', 参数)

![](C:/Users/%E4%B9%90%E4%B9%90%E5%A4%A7%E5%93%A5%E5%93%A5/Desktop/%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/assets/image-20210903150539127.png)



**Modules**

将store/index分成多个文件，分别对应不同的业务逻辑

通过export和import整合到store中，外界通过store.xxx来调用module

 

module调用其他module的state，需要通过传入rootState变量，也就是代表store/index文件

调用其他module的actions，直接dispatch即可，和其他component一样



Actions, Mutations, and Getters are always registered under the global namespace（aka. the root which is $store)

当然，也可以通过设置namespace = true将module中的各种隔离开，外界调用时就需要moduleName/actionToCall来调用了

