![img](https://cdn.jsdelivr.net/gh/rentianle2020/Image/20210830184534.png)



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

von，vdata，vprops...各种快捷键



# Vue

Only a `Single Page Application` sent (initally) to the browser

Vue handle “page” changes in the browser by swapping what components are shown on the page

更快更流畅的用户体验



### Vue Basic

v-bind

v-model(表单绑定，select，checkbox)，绑定列表元素并提供value，就支持多选了

v-if v-else



**事件绑定**

@click，@mouseover，@submit表单，@keyup.enter --> 这个enter叫做modifier



**class**

:class="{ disabledButton: !inStock}" --> 冒号左边是css Class，右边是data中的数据（条件），根据条件动态改变样式



**component 复用**

使用prop添加属性和方法，使用emit释放event给调用它的component（还可以附带信息）

event modifier --> @click.shift 必须shift+点击 @click.right 必须右键点击 @click.self 必须点击其本身，不能是其中包括的内容

使用slot添加html元素，使用template v-slot:links命名slot，配合slot标签name属性，可以有default content

teleport to=".modal"将组件添加到全局的html文件中



**component Lifecycle**

mounted 最初绑定

updated 内容更新

unmounted 取消绑定



### Vue CLI

index.html 最初的空页面

App.vue 根组件

main.js 使用App.vue渲染页面

package.json 依赖



vue create xxx / npm run serve



### Vue Router



**文件夹**

component：不分页面的可复用组件

views：页面相关组件，如Jobs.vue & JobDetails.vue可以放到views/jobs/目录下



**基础**

<router-view/> 根据url，动态渲染component

<router-link to="/">Home</router-link> client-routing，vue介入来完成request

可以通过a.router-link-exact-active管理选中router-link的css



使用<router-link :to="{ name: 'About'}">，以后更改路径，只要不更改名字，就依然适用



**Route Parameters**

/jobs/**123**	/jobs/**567**

直接访问param中的值（这里是id）：this.$route.params.id

动态链接： <router-link :to="{ name: 'JobDetails',params: {id: job.id}}">



将route的props:true，就可以直接在component中，通过props接收param上传来的参数了

比如Post.vue路径是locaohost:8080/post/:id --> localhost:8080/post/1，可以通过props:['id']接收参数



**复用路径&404**

```css
//redirect
  {
    path: '/alljobs',
    redirect: '/jobs'
  },
  //catchAll
  {
    path: '/:catchAll(.*)',
    name: 'NotFound',
    component: NotFound
  }
```



**回退/前进&redirect**

this.$router.go(-1) 回退一步

this.$router.push({ name: 'Home' }) 重定向到Home



### Fetch Data



### Composition API

Options API的方法和数据都散布在各个地方，易读性差

Composition API可以更好的复用function



setup()在一切生命周期函数之前加载，在其中适用js创建变量和方法，在最后return



使用ref，在返回之后绑定dom，可以在方法中操作ref来操作dom

原本的data是reactive的，但是setup()返回后，就不是reactive了



**ref()**

使用ref()包装一下数据，然后就可以操作ref对象了（ref真正的使用情况，99%）

ref包装的可以是数据，可以是对象ref({name:'tyler', age:23})

数据访问：对象.value.属性

使用场景：访问基本数据类型，直接.value



**reactive()**

使用reactive()包装，不用再.value了

数据访问：对象.属性，return ...toRefs(对象)，可以在外边直接访问属性

使用场景：对象！必须有属性名



**computed()**



**watch() & watchEffect**

当他watch的数据改变时，被调用

调用它的返回值函数，就可以停止watch



**props**

在setup(props)中接收，然后通过computed修改



**生命周期**

在setup中，给之前的lifecycle函数加个on，mount变为onMounted(() => {})



**fetch data**

async() await

```javascript
const load = async() => {
            let data = await fetch("http://localhost:8081/post")
            posts.value = await data.json()
        }
load()
```



**reuseable function**

可以从js中import function

```js
import getPost from '../composables/getPost'

export default {
    props: ['id'],
    setup(props){
        const {post, error,load} = getPost(props.id)
        load()
        return {post,error}
    }
}

//getPost.js
import {ref} from 'vue'

const getPost = (id) => {
    const post = ref(null)
    const error = ref(null)
    const load = async() => {
        try{
            let data = await fetch("http://localhost:8081/posts/" + id)
            if(!data.ok){
                throw Error('no data avilable')
            }
            post.value = await data.json()
        }catch(err){
            error.value = err.message
            console.log(error.value)
        }
    }
    return { post, error,load}
}
export default getPost
```

