# ReviewProcessService

**port: 5006**

**url_prefix="/api/reviewProcess"**

# 数据库字段参考

| 名字        | 类型   | 可选  | 举例         | 说明 |
| ----------- | ------ | ----- | ------------ | ---- |
| _Id         | String | false | process1     |      |
| confId      | String | false | conference1  |      |
| draftId     | String | false | draft1       |      |
| pcMemberId  | String | false | pcmember1    |      |
| rebuttal    | String | false | rebuttal     |      |
| status      | String | false | Reviewing    |      |
| Contributor | String | false | Xuanjie Fang |      |
| Score       | String | false | 2            |      |

# API文档参考

### 状态码

绝大部分API返回的数据都包含一个code状态码，分别代表不同的状况
以下是状态码表

| 状态码 | 原因         |
| ------ | ------------ |
| 0      | 正常         |
| 1      | 未知错误     |
| 2      | 不存在该id   |
| 3      | 没有该记录   |
| 4      | 重复的数据id |
| 5      | 没有登录     |

### 为稿件评分

----

> 以下API，未登录时返回code:5, error: 没有登录  

```text
 [POST] /score 
```

使用该方法为某一稿件评分

**请求**

```text
{
  "pcmemberId":pcmember1,
  "processId": reviewProcess,
  "score": 2,
  "confidence": 1,
  "comment": Good
}
```

| 名字       | 类型   | 可选  | 举例      | 说明 |
| ---------- | ------ | ----- | --------- | ---- |
| pcmemberId | String | false | pcmember1 |      |
| processId  | String | false | process1  |      |
| score      | String | false | 2         |      |
| confidence | String | false | 1         |      |
| comment    | String | false | Good      |      |

**响应**

```text
{
  “code”:0,
  "data":null
}
```

> 会议status修改为firstResult

### 作者为稿件添加评论

----

> 以下API，未登录时返回code:5, error: 没有登录  

```text
 [POST] /rebuttal
```

使用该方法使得作者为某一稿件添加评论

**请求**

```text
{
  "rebuttal":rebuttal,
  "username":xuan,
  "processId": process1,
}
```

| 名字      | 类型          | 可选  | 举例     | 说明 |
| --------- | ------------- | ----- | -------- | ---- |
| rebuttal  | String        | false | rebuttal |      |
| username  | String        | false | xuan     |      |
| processId | reviewProcess | false | process1 |      |

**响应**

```text
{
    "code": 0, 
    "data": null
}
```

### 为pcmember分配稿件

----

> 以下API，未登录时返回code:5, error: 没有登录  
>
> 以下API，判断用户是否为chairman，不是返回code：6，error：没有权限

```text
 [POST] /allocDraft
```

使用该方法允许chairman为pcmember分配稿件

**请求**

```text
{
  "strategy":withTopic,
  "confId":conference1
}
```

| 名字     | 类型       | 可选  | 举例        | 说明 |
| -------- | ---------- | ----- | ----------- | ---- |
| rebuttal | String     | false | rebuttal    |      |
| username | String     | false | xuan        |      |
| confId   | Conference | false | conference1 |      |

**响应**

```text
{
    "code": 0, 
    "data": null
}
```

> 根据confId获取不到对应会议，提示无此会议
>
> 无draft提示无稿件提交
>
> 根据status判断会议状态，非submitting状态时提示当前流程不可开启审稿

### 获取个人需要审阅的所有稿件信息

------

> 以下API，未登录时返回code:5, error: 没有登录

```text
[GET] /getReviewProcessByPcMember
```

使用该方法获取所有需要审阅的稿件

**请求**

| 名字       | 类型   | 可选  | 举例      | 说明 |
| ---------- | ------ | ----- | --------- | ---- |
| pcmemberId | String | false | pcmember1 |      |

**响应**

```text
{
	"code":0,
	"data":{
  "_id": process1,
  ……
  "pcMember": [
    {
      "score": 2,
      "comment": Good,
      "confidence": 1,
      "pcmemberId": {
        "_id": hui789cx,
        "username": xuan,
        "fullName": Xuanjie Fang,
        "password": 123456,
        "email": 123@abc.com,
        "company": fudan,
        "area": shanghai
      }
    }
  ],
  "rebuttal": rebuttal//返回对应记录
}
}
```

> 找不到对应记录时返回code:3, error: 没有该记录

### 根据Id获取稿件信息

------

> 以下API，未登录时返回code:5, error: 没有登录

```text
[GET] /getReviewProcessById
```

使用该方法根据Id获取稿件

**请求**

| 名字      | 类型          | 可选  | 举例     | 说明 |
| --------- | ------------- | ----- | -------- | ---- |
| processId | reviewProcess | false | process1 |      |

**响应**

```text
{
	"code":0,
	"data":{
  "_id": process1,
  ……
  "pcMember": [
    {
      "score": 2,
      "comment": Good,
      "confidence": 1,
      "pcmemberId": {
        "_id": hui789cx,
        "username": xuan,
        "fullName": Xuanjie Fang,
        "password": 123456,
        "email": 123@abc.com,
        "company": fudan,
        "area": shanghai
      }
    }
  ],
  "rebuttal": rebuttal//返回对应记录
}
}
```

> 找不到对应记录时返回code:3, error: 没有该记录

```text
{
    "code": 0,
    "data":null
}
```