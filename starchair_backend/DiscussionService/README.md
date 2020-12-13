# DiscussService
**port: 5005**

**url_prefix="/api/discuss"**


# 数据库字段参考

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| draftId | string | false | 'MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA' | |
| posts | array[post] | false | [] | 留言 |

**post object**
| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| _id | string | false | "111" | |
| content | string | false | "liuyan" | |
| username | string | false | "ringz" | |
| created_time | string | false | "2020-10-1" | |
| comments | array[comment] | false | [] | |

**comment object**
| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| _id | string | false | "222" | |
| content | string | false | "liuyan" | |
| username | string | false | "ringz" | |
| created_time | string | false | "2020-10-1" | |
| replyTo | string | true | "ringz" | |

# API文档参考
### 状态码
绝大部分API返回的数据都包含一个code状态码，分别代表不同的状况  
以下是状态码表
| 状态码 | 原因 |
| --- | --- |
| 0 | 正常 |
| 1 | 未知错误 |
| 2 | 不存在该id |
| 3 | 没有该记录 |
| 4 | 重复的数据id |
| 5 | 已存在该用户名 |
| 6 | 已存在该邮箱 |
| 7 | 用户名或密码错误 |
| 8 | 没有登录 |
| 9 | 注册信息不完整 |


### 获取或创建讨论

```
  [GET] /getOrCreateByDraft/<draftId>
```

使用该方法通过pid得到对应的讨论，如果尚未创建该讨论，则先创建

**请求**

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| processId | string | false | "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA" | |

**响应**
```
{
    "code": 0,
    "data": {
      "_id": "222",
      "draftId": "333",
      "posts": [post object]
    }
}
```
> 未登陆返回code: 8, error: 没有登陆


### 添加讨论留言

```
  [POST] /createPost
```

使用该方法为讨论添加留言

**请求**
```
{
  "did": "11111",
  "post": {
    "content": "111",
    "username": "ringz",
    "created_time": "2020-10-20",
    "comments": []
  }
}
```
| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| did | string | false | "111" | discuss id |
| post | post object | false | | |

**响应**
```
{
  "code": 0,
  "data": post_id
}
```
> 未登陆返回code: 8, error: 没有登陆; 未找到did返回code: 2, error: 不存在该id


### 添加留言评论

```
  [POST] /createReply
```

使用该方法为留言添加评论

**请求**
```
{
  "did": "1111",
  "post_id": "2222",
  "comment": {
    "content": "2222",
    "username": "3333",
    "created_time": "2020-10-20"
  }
}
```
| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| did | string | false | "222" | |
| post_id | string | false | "333" | | 
| comment | comment object | false | | |


**响应**
```
{
  "code": 0,
  "data": comment_id
}
```
> 未登陆返回code: 8, error: 没有登陆; 未找到did或者post_id返回code: 2, error: 不存在该id