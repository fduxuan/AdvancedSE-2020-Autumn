# DiscussService

!!! note
    **port**: 5005 <br>


## 1. 数据库字段参考

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| draftId | string | false | 'MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA' | |
| posts | list[post] | false | [] | 留言 |

### 1.1 post object

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| _id | string | false | "111" | |
| content | string | false | "liuyan" | |
| username | string | false | "ringz" | |
| created_time | string | false | "2020-10-1" | |
| comments | array[comment] | false | [] | |

### 1.2 comment object

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| _id | string | false | "222" | |
| content | string | false | "liuyan" | |
| username | string | false | "ringz" | |
| created_time | string | false | "2020-10-1" | |
| replyTo | string | true | "ringz" | |

---



## 2. 面向用户API

!!! attention
    以下所有api均需登录后方可调用，否则返回`{'code': 8, "data": "没有登录"}`

### 2.1 获取或创建讨论

```
 [GET] /api/discuss/getOrCreateByDraft/<draftId>
```

!!! hint
    使用该方法通过pid得到对应的讨论，如果尚未创建该讨论，则先创建

#### **请求**

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| processId | string | false | "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA" | |

#### **响应**

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
---




### 2.2 添加讨论留言

```
  [POST] /api/discuss/createPost
```

!!! hint
    使用该方法为讨论添加留言

#### 请求

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| did | string | false | "111" | discuss id |
| post | post object | false | | |

#### **响应**

```
{
  "code": 0,
  "data": post_id
}
```
#### 异常

| 状态码 | 原因       | 说明                     |
| ------ | ---------- | ------------------------ |
| 9      | 信息不完整 | 没有上传完整的post结构体 |
| 4      | 重复id     | 插入时post id 重复时触发 |

----




### 2.3 添加留言评论

```
  [POST] /createReply
```

!!! hint
    使用该方法为留言添加评论

#### **请求**

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| did | string | false | "222" | |
| post_id | string | false | "333" | |
| comment | comment object | false | | |

#### **响应**

```
{
  "code": 0,
  "data": comment_id
}
```
#### 异常

| 状态码 | 原因       | 说明                        |
| ------ | ---------- | --------------------------- |
| 9      | 信息不完整 | 没有上传完整的post结构体    |
| 4      | 重复id     | 插入时comment id 重复时触发 |



## 3. 心跳检测

!!! attention
    心跳检测API针对注册中心 **consul** 调用，对服务的健康进行自动检测

```
[GET] /api/discuss/check
```

