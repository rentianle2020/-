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

