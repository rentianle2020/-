# Visual Studio

快捷键

- `!`：快速生成HTML模板
- `link`：引入css文件
- `.xxx`：快速生成class=xxx的div
- `a.xxx`：快速生成class=xxx的a
- `shift + alt + ⬇`：快速复制到下一行
- `ctrl + b`：开合左边文件栏



# HTML

查看各种标签和它们的属性：https://www.w3schools.com/TAGS/



# CSS



### 颜色和背景

```css
background-image: url()
background-repeat: no-repeat;
background-size: contain;

background: rgba($color: red, $alpha: 0.4);
background: linear-gradient(to bottom,red,green);
background: radial-gradient(red,green);

color: green;
```



### 单位

px：像素点，在高像素的显示器上，400px就会很小；在低像素的显示器上，400px就相对会大

%：和容器相对的大小

em：原本大小的几倍



### 字体和文本操作

使用google fonts引入字体，以serif作为默认字体

```css
font-size: 1.25em;
font-weight: 400;
font-family: 'ZCOOL KuaiLe', serif;
font-style:italic;

text-align:justify;
text-transform: lowercase;
text-decoration: underline;
```



### Layout



##### Box Model

content --> padding --> boarding --> margin

padding&margin简写时注意TRouBLe顺序，没有声明的一边会取对边值

box-shadow是先制定x轴再指定y轴

```css
padding: 5px 15px
margin: 10px auto
box-shadow: 10px 2px #6f9090
```



##### element 

A block-level element 

- always starts on a new line
- always takes up the full width available (stretches out to the left and right as far as it can)
- has a top and a bottom margin, whereas an inline element does not



An inline element

- does not start on a new line
- only takes up as much width as necessary.



inline-block element

- don't start on a new line
- allows to set width and height



### Flexbox

操作container里边的元素

```css
display: flex;

flex-direction: row / column-reverse;
flex-wrap: wrap / unwrap;
justify-content: center / space-around; /*控制左右*/
align-items: center / stretch / ; /*控制上下*/
```

container中的flex item，用来完成响应式功能

```css
flex-basis: 100px; /* min-width */
flex-grow: 1;  /* 窗口变大时 它grow的rate */
flex-shrink: 1;

flex: 1 1 100px

align-self: center /* 覆盖container的align-items */
```



### Grid

关注于每一个格子的height & width

```css
display: grid;

grid-template-columns: auto auto auto; /*三个元素一行，平均分 */
grid-template-rows: 50px 250px;

grid-row-gap: 100px;
grid-column-gap: 150px;
gap: 100px 150px;

grid-area: 2 / 2 / span 2 / span 3 /* 起始横竖坐标 和 结束横竖坐标 */
```



### Animation & Transition



##### Transition

```css
button {
    -webkit-transition: transition: all 200ms ease 1s; /* propterty 动画时长 动画方式 delay */
}

button:hover{
    background: rgb(200, 180, 280);
    padding: 20px 30px;
}
```

-webkit-：chrome和safari

-moz-：firefox

-o-：opera



##### transformation function

```css
transform: translate(50px,30px); /* x坐标移动 & y坐标移动 */
transform: scale(2.5);
transform: rotate(-50deg);

/* 不建议使用 */
transform: skewX(45deg); 
transform: matrix(1, 0.45, 0.45, 1, 100, 50) /* scale x skew x skew y scale y translate x & y */
```



##### Animation

```css
@keyframes red-to-black {
    0% {background: red; transform: translate(0px,0px)}
    50% {background: yellow; transform: translate(10px,10px)}
    100% {background: black; transform: translate(20px,20px)}
}

h3{
    /* animation-name: red-to-black;
    animation-duration: 4s;
    animation-timing-function: linear;
    animation-delay: 0s;
    animation-iteration-count: 2;
    animation-direction: alternate; */

    animation: red-to-black 4s linear 0s 2 alternate-reverse;
}
```



# Tailwind CSS

### 大小单位

1 rem = document base font 16px = 4 in Tailwind

ex: 20px = 5 in Tailwind



### 背景

从左到右渐变

```css
<div className='bg-gradient-to-r from-purple-900 to-purple-600'> </div>
```



### 图片

让card变成圆角，多余的图片hidden

```css
<div calssName='rounded-lg overflow-hidden'>
	<img src='' />
</div>
```



### 文字

文字大小，粗细，颜色，位置，字体

```css
<p className='text-4xl font-bold text-gray-900 text-center font-sans'> </p>
```



字间距（单位em），行间距

```css
<p className='tracking-tight leading-relaxed'> </p>
```



斜字体，下划线，大写

```css
<p className='italic underline uppercase'> </p>
```



line-clamp插件，给文字确定行数，多余的用省略号代替“...”

(如果插件安装后compile报错，就删掉node_modules重新npm install一下)

```css
<p className='line-clamp-3'>xxxxxxxx</p>
```



### Display

**block, flex, hidden**

inline, inline-block



### Flexbox

控制元素在main axis & cross axis的定位

如果item-center不管用，说明整个div的height不够

```css
<div className='flex justify-center items-center'></div>
```



确定card大小后，再剧中

```css
<div className="flex max-w-3xl mx-auto"> </div>
```



flexbox + margin可以让push element left/right/top/bottom/center



flexbasis：flex col的时候用来设置高度，flex row的时候用来设置宽度；响应式很好用！



### 边框border

大小（border = 1px），颜色，风格，半径

```css
<div className="border-t-2 border-red-900 border-solid rounded-tr-lg"> </div>
```



### 定位

left是对于父组件的size，transform是对于本组件的size

如果父组件1000px，子组件100px，他的起始位置就是450，结束位置就是550，达到剧中效果！

```css
<div className="absolute w-4/5 text-center left-1/2 -translate-x-1/2"></div>
```



### Input

```css
<input id="full_name" placeholder="Enter your full name" class="mt-2 shadow border rounded-lg px-3 py-2 text-gray-700 placeholder-indigo-300 focus:bg-blue-100 ">
```



### DarkMode

https://tailwindcss.com/docs/dark-mode#toggling-dark-mode-manually

If you want to support toggling dark mode manually instead of relying on the operating system preference, use the `class` strategy instead of the `media` strategy:

```javascript
//tailwind.config.js
module.exports = {
  darkMode: 'class',
    //...
}
```



```react
//depends on父标签的className="dark" 
<nav className="dark">
    <div className="bg-white dark:bg-black">
</nav>
```



### Grid

根据index给不同grid-element不同的span

添加auto-fit：https://gist.github.com/iamazik/5aa934513388a6e48f44e63648a261d8

```css
padding: 10px;
display: grid;
grid-gap: 2px;
/* 控制row高度 */
grid-auto-rows: 100px;
/* 控制col的min和max，同时不让每行有确定的格子数，达到responsive design */
grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
/* 让后边的小图可以前移来填补空缺 */
grid-auto-flow: dense;
```



### 其他

opacity-0，shadow-lg，cursor-pointer，select-none，sr-only给视觉障碍者的screen reader only



### SVG

https://heroicons.com/
