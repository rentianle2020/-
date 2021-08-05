User：解析请求头/请求体中的KV，赋值到User中，比如name=tyler，age=12（请求行/请求体表单提交）

如果User中有List<Card> card，那么提交表单前要将cards标下标，比如card[0].bank="招商银行",card[1].bank=”人民银行“

@RequestParam String name：接收请求行/请求体中key为name的值

@RequestBody User：解析请求体中的json，赋值到User对象中

