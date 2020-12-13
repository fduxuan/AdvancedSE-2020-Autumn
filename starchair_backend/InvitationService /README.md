# InviationService

**port: 5004**

**url_prefix="/api/invitation"**


# 数据库字段参考

| 名字         | 类型   | 可选  | 举例                | 说明 |
| ------------ | ------ | ----- | ------------------- | ---- |
| Inviter      | String | false | xuan                |      |
| Invitee      | String | false | fang                |      |
| ConfId       | String | false | 1                   |      |
| Status       | String | false | sent->accept/reject |      |
| invitationId | String | false | invitation1         |      |

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

### 发送PCmember邀请

-----

> 以下API，未登录时返回code:5, error: 没有登录  

```
 [POST] /createInvitation
```

使用该方法发送PCmember的会议邀请 

**请求**

```
{
    "inviter": xuan,
    "invitee": fang,
    "confId":1,
    "status":""
}
```

| 名字    | 类型   | 可选  | 举例 | 说明 |
| ------- | ------ | ----- | ---- | ---- |
| inviter | String | false | xuan |      |
| invitee | String | false | fang |      |
| confId  | String | false | 1    |      |
| status  | String | false | sent |      |

**响应**

```
{
    "code": 0, 
    "data": null
}
```

### 查看自己收到的邀请

----

> 以下API，未登录时返回code:5, error: 没有登录  

```
[GET] /getInvitationByInvitee
```

使用该方法获取本人收到的所有邀请

**请求**

| 名字 | 类型   | 可选  | 举例     | 说明 |
| ---- | ------ | ----- | -------- | ---- |
| uid  | String | false | hui789cx |      |

**响应**

```
{
    "code": 0,
    "data": [
  	{
    "_id": invitation1,
    ……
    "invitee": {
      "_id": hui789cx,
      "username": xuan,
      "fullName": Xuanjie Fang,
      "password": 123456,
      "email": 123@abc.com,
      "company": fudan,
      "area": shanghai
    },
    ……
  }// 返回对应的record
]
}
```

> 找不到对应记录时返回code:3, error: 没有该记录

### 查看自己发出的邀请

----

> 以下API，未登录时返回code:5, error: 没有登录  

```
[GET] /getInvitationByInviter
```

使用该方法获取本人发出的所有邀请

**请求**

| 名字 | 类型   | 可选  | 举例     | 说明 |
| ---- | ------ | ----- | -------- | ---- |
| uid  | String | false | hui789cx |      |

**响应**

```
{
    "code": 0,
    "data": [
  	{
    "_id": invitation1,
    "inviter": {
      "_id": hui789cx,
      "username": xuan,
      "fullName": Xuanjie Fang,
      "password": 123456,
      "email": 123@abc.com,
      "company": fudan,
      "area": shanghai
    },
    ……
  }// 返回对应的record
]
}
```

> 找不到对应记录时返回code:3, error: 没有该记录

### 用户接受邀请

----

> 以下API，未登录时返回code:5, error: 没有登录  

```text
 [POST] /approveInvitation
```

使用该方法接受某一个成为PCmember的邀请

**请求**

```text
{
	"inviationId":invitation1
}
```

| 名字         | 类型   | 可选  | 举例        | 说明 |
| ------------ | ------ | ----- | ----------- | ---- |
| invitationId | String | false | invitation1 |      |

**响应**

```text
{
    "code": 0, 
    "data":null
}
```

> status修改为approve

### 用户拒绝邀请

----

> 以下API，未登录时返回code:5, error: 没有登录  

```text
 [POST] /rejectInvitation
```

使用该方法拒绝某一个成为PCmember的邀请

**请求**

```text
{
	"inviationId":invitation1
}
```

| 名字         | 类型   | 可选  | 举例        | 说明 |
| ------------ | ------ | ----- | ----------- | ---- |
| invitationId | String | false | invitation1 |      |

**响应**

```text
{
    "code": 0, 
    "data":null
}
```

> status修改为reject