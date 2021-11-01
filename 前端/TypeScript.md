# TypeScript

Alter to JS(superset)

Allows strick types

Supports modern featrues (arrow functions, let, const)

Extra featrues (generics, interfaces, tuples etc)



## 开始

tsc xxx.ts -w



#### Basic Type







### Vue

reactive可以直接在data中as string...

ref因为是一个引用，需要使用<generic>



在types文件夹中声明对象和类型，然后使用泛型约束ref对象的类型



在设置中打开vetur template的Template Interpolation Service



**PropType**

```typescript
import { defineComponent,PropType } from 'vue'
import Job from '../types/Job'

export default defineComponent({
    props: {
        jobs: {
            required: true,
            type: Array as PropType<Job[]>,
        }
    },
    setup() {
        
    },
})
```



定义ref<>

定义function参数类型

定义propType

