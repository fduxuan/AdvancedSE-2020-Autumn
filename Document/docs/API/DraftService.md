# DraftService

!!! note
    **port**: 5003



## 1. 数据库字段参考

| 名字        | 类型     | 可选  | 举例                   | 说明                                                |
| ----------- | -------- | ----- | ---------------------- | --------------------------------------------------- |
| title       | String   | false | AN EXAMPLE             |                                                     |
| summary     | String   | false | EXAMPLE SUMMARY        |                                                     |
| confId      | String   | false | 1                      |                                                     |
| contributor | String   | false | user_id                |                                                     |
| topics      | [String] | false | [HCI]                  |                                                     |
| authors     | [String] | false | [username1, username2] |                                                     |
| _id         | String   | false | draft1                 |                                                     |
| file_id     | String   | false | 12321313               | 论文附件id，调用单独的update采用数据库chunk进行处理 |

<br><br>

----



## 2. 面向用户API

!!! attention
    以下所有api均需登录后方可调用，否则返回`{'code': 8, "data": "没有登录"}`



### 2.1 添加新的投稿

```
 [POST] /api/draft/submitDraft 
```

!!! hint
    使用该方法进行论文的提交，**除file之外的数据**

#### **请求**

| 名字    | 类型        | 可选  | 举例            |
| ------- | ----------- | ----- | --------------- |
| title   | String      | false | AN EXAMPLE      |
| summary | String      | false | EXAMPLE SUMMARY |
| confId  | String      | false | 1               |
| topics  | [String]    | false | [HCI]           |
| authors | [user_name] | false | [fduxuan, sir]  |

#### **响应**

**自动响应**

`status='init'`  `contributor='current_userid'`

**服务间调用**

* 调用 **NotificationService** 服务获得发送异步通信消息，告诉会议发起人

  > `"{username} has submitted a draft to your conference {shortenForm}"`

```
{
    "code": 0, 
    "data":null
}
```



----



### 2.2 修改已提交的投稿

```
 [POST] /api/draft/modifyDraft 
```

!!! hint
    使用该方法进行已提交论文的修改，**除file之外的数据**

#### **请求**

| 名字    | 类型     | 可选  | 举例                   |
| ------- | -------- | ----- | ---------------------- |
| _id     | String   | false | draft1                 |
| title   | String   | True  | AN EXAMPLE modified    |
| summary | String   | True  | EXAMPLE SUMMARY        |
| topics  | [String] | True  | [HCI]                  |
| authors | [User]   | True  | [username1, username2] |

#### 响应

```
{
    "code": 0, 
    "data":null
}
```

#### 异常

| 状态码 | 原因     | 说明                     |
| ------ | -------- | ------------------------ |
| 10     | 没有权限 | 非该文稿的提交者调用触发 |



------



### 2.3 上传稿件

```
 [POST] /api/draft/upload
```

!!! hint
    使用该方法进行上传文件，写入mongodb GridFsBucket，进行分块存储，避免文件过大导致的问题，同时更有可移植性

#### **请求**

当前请求为 **Form** 格式

| 名字      | 类型   | 可选  | 举例           |
| --------- | ------ | ----- | -------------- |
| file      | FILE   | false | {..........}   |
| draftId   | String | False | Draft1         |
| file_name | String | False | "My_draft.pdf" |

#### 响应

```
{
		"code": 0,
		"data": "My_draft.pdf"
}
```



-----



### 2.4 下载/预览稿件

```
[GET] /api/draft/download/<fid> 
```

!!! hint
    使用该方法进行下载稿件，以文件流`application/octet-stream`形式传输，前端进行下载或者预览功能解析

#### **请求**

| 名字 | 类型   | 可选  | 举例  | 说明    |
| ---- | ------ | ----- | ----- | ------- |
| Fid  | String | false | File1 | url参数 |

#### 响应

```
{
		"code": 0,
		"data": {文件流}
}
```

-----



### 2.5 通过稿件Id获取信息

```
[GET] /api/draft/getDraftById/<did>
```

!!! hint
    使用该方法根据论文id获取详细信息

#### 请求

| 名字 | 类型   | 可选  | 举例   | 说明        |
| ---- | ------ | ----- | ------ | ----------- |
| did  | String | false | draft1 | url路径参数 |

#### **响应**

```
{
    "code": 0,
    "data":{
      "_id": draft1,
      "title": AN EXAMPLE,
      "summary": EXAMPLE SUMMARY modified,
      "file_id": "fileId",
      "confId": 1,
      "contributor": Xuanjie Fang,
      "topics": [HCI],
      "authors": ["xxx1", 'xxx2']
	}
}
```

#### 异常

| 状态码 | 原因       | 说明         |
| ------ | ---------- | ------------ |
| 3      | 没有该纪律 | 该投稿不存在 |

-----



### 2.6 获取自己提交论文

```
[GET] /api/draft/visible/<cid>
```

!!! hint
    使用该方法获取一个会议下当前用户投递的所有稿件

#### 请求

根据session自动获得当前用户id

| 名字 | 类型   | 可选  | 举例        | 说明        |
| ---- | ------ | ----- | ----------- | ----------- |
| cid  | String | false | Conference1 | url路径参数 |

#### 响应

```
{
    "code": 0,
    "data":[draft_info, draft_info]
}
```

----



## 3. 服务间通信API

!!! attention
    服务间API仅供服务间通信调用，通过设置白名单阻止外来源访问

### 3.1 通过Id组获取投稿信息

```
[POST] /api/draft/findByIds
```

!!! hint
    使用该方法获取id组覆盖的稿件信息

#### 请求

| 名字 | 类型 | 可选  | 举例            | 说明        |
| ---- | ---- | ----- | --------------- | ----------- |
| ids  | List | false | [id1, id2, id3] | url路径参数 |

#### 响应

```
{
    "code": 0,
    "data": {'id1': {draftInfo}, 'id2': {draftInfo}} // 返回对应的record map
}
```

----



### 3.2 获取一个会议下所有稿件

```
[GET] /api/draft/getDraftByConference/<cid>
```

!!! hint
    使用该方法根据会议id获取该会议上所有收到的论文相关信息

#### **请求**

| 名字 | 类型   | 可选  | 举例        | 说明        |
| ---- | ------ | ----- | ----------- | ----------- |
| cid  | String | false | Conference1 | url路径参数 |

#### **响应**

```
{
    "code": 0,
    "data": [draft_info1, draft_info2...]
}
```

----



### 3.3 获得用户投的所有稿件

```
[GET] /api/draft/getDraftByContributor/<contributor>
```

!!! hint
    使用该方法根据用户id获得该用户投递的所有稿件，不做会议范围限制

#### 请求

| 名字        | 类型   | 可选  | 举例    | 说明        |
| ----------- | ------ | ----- | ------- | ----------- |
| contributor | string | false | user_id | url路径参数 |

#### **响应**

```
{
    "code": 0,
    "data": [draft_info1, draft_info2...]
}
```

----



## 4. 心跳检测

!!! attention
    心跳检测API针对注册中心 **consul** 调用，对服务的健康进行自动检测

```
[GET] /api/draft/check
```

