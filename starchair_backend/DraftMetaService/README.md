# DraftMetaService

**port: 5003**

**url_prefix="/api/draft"**

# 数据库字段参考

| 名字        | 类型     | 可选  | 举例                                                         | 说明 |
| ----------- | -------- | ----- | ------------------------------------------------------------ | ---- |
| title       | String   | false | AN EXAMPLE                                                   |      |
| summary     | String   | false | EXAMPLE SUMMARY                                              |      |
| file        | file     | false | example.pdf                                                  |      |
| confId      | String   | false | 1                                                            |      |
| contributor | String   | false | Xuanjie Fang                                                 |      |
| topics      | [String] | false | [HCI]                                                        |      |
| authors     | [User]   | false | [<br />{<br />"__id":hui789cx,<br />"username":xuan,<br />"fullname":Xuanjie fang,<br />"password":123456,<br />"email":123@abc.com<br />"company":fudan,<br />"area":shanghai}<br />] |      |
| draftId     | String   | false | draft1                                                       |      |
| filepath    | String   | false | /……/example.pdf                                              |      |

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

### 提交论文

----

> 以下API，未登录时返回code:5, error: 没有登录  
>
> 以下API，未找到相应会议时返回code: x,error：不存在该会议

```text
 [POST] /submitDraft 
```

使用该方法进行论文的提交

**请求**

```text
{
	"title":AN EXANPLE,
	"summary":EXAMPLE SUMMARY,
	"filr":example.pdf,
	"confId":1,
	"contributor":Xuanjie Fang,
	"topics":[HCI],
	"autors":[
	{
      "_id": hui789cx,
      "username": xuan,
      "fullName": Xuanjie Fang,
      "password": 123456,
      "email": 123@abc.com,
      "company": fudan,
      "area": shanghai
    }
    ]
}
```

| 名字        | 类型     | 可选  | 举例                                                         | 说明 |
| ----------- | -------- | ----- | ------------------------------------------------------------ | ---- |
| title       | String   | false | AN EXAMPLE                                                   |      |
| summary     | String   | false | EXAMPLE SUMMARY                                              |      |
| file        | file     | false | example.pdf                                                  |      |
| confId      | String   | false | 1                                                            |      |
| contributor | String   | false | Xuanjie Fang                                                 |      |
| topics      | [String] | false | [HCI]                                                        |      |
| authors     | [User]   | false | [<br/>	{<br/>      "_id": hui789cx,<br/>      "username": xuan,<br/>      "fullName": Xuanjie Fang,<br/>      "password": 123456,<br/>      "email": 123@abc.com,<br/>      "company": fudan,<br/>      "area": shanghai<br/>    }<br/>] |      |

**响应**

```text
{
    "code": 0, 
    "data":null
}
```

### 修改已提交的论文

----

> 以下API，未登录时返回code:5, error: 没有登录  

```text
 [POST] /modifyDraft 
```

使用该方法进行已提交论文的修改

**请求**

```text
{
	"draftId":draft1,
	"title":AN EXANPLE modified,
	"summary":EXAMPLE SUMMARY,
	"filr":example.pdf,
	"confId":1,
	"contributor":Xuanjie Fang,
	"topics":[HCI],
	"autors":[
	{
      "_id": hui789cx,
      "username": xuan,
      "fullName": Xuanjie Fang,
      "password": 123456,
      "email": 123@abc.com,
      "company": fudan,
      "area": shanghai
    }
    ]
}
```

| 名字        | 类型     | 可选  | 举例                                                         | 说明 |
| ----------- | -------- | ----- | ------------------------------------------------------------ | ---- |
| draftId     | String   | false | draft1                                                       |      |
| title       | String   | false | AN EXAMPLE modified                                          |      |
| summary     | String   | false | EXAMPLE SUMMARY                                              |      |
| file        | file     | false | example.pdf                                                  |      |
| confId      | String   | false | 1                                                            |      |
| contributor | String   | false | Xuanjie Fang                                                 |      |
| topics      | [String] | false | [HCI]                                                        |      |
| authors     | [User]   | false | [<br/>	{<br/>      "_id": hui789cx,<br/>      "username": xuan,<br/>      "fullName": Xuanjie Fang,<br/>      "password": 123456,<br/>      "email": 123@abc.com,<br/>      "company": fudan,<br/>      "area": shanghai<br/>    }<br/>] |      |

**响应**

```text
{
    "code": 0, 
    "data":null
}
```

### 获取论文相关信息

------

> 以下API，未登录时返回code:5, error: 没有登录

#### 通过作者查找其论文信息

```text
[GET] /getDraftByAuthor
```

使用该方法根据作者获取他提交的论文信息

**请求**

| 名字 | 类型   | 可选  | 举例     | 说明 |
| ---- | ------ | ----- | -------- | ---- |
| uid  | String | false | hui789cx |      |

**响应**

```text
{
    "code": 0,
    "data":{
    [
  	{
    	"_id": draft1,
    	"title": AN EXAMPLE modified,
    	"summary": EXAMPLE SUMMARY,
    	"filePath": /……/example.pdf,
    	"confId": 1,
    	"contributor": Xuanjie Fang,
    	"topics": [
      		HCI
    	],
    	"authors": [
      	{
        	"_id": hui789cx,
        	"username": xuan,
        	"fullName": Xuanjie Fang,
        	"password": 123456,
        	"email": 123@abc.com,
        	"company": fudan,
        	"area": shanghai
      	}
    	]
  	}
	]
}
```

> 找不到对应记录时返回code:3, error: 没有该记录

#### 通过论文Id获取其信息

```text
[GET] /getDraftById/<did>
```

使用该方法根据论文id获取相关信息

**请求**

| 名字 | 类型   | 可选  | 举例   | 说明        |
| ---- | ------ | ----- | ------ | ----------- |
| did  | String | false | draft1 | url路径参数 |

**响应**

```text
{
    "code": 0,
    "data":{
    "_id": draft1,
    "title": AN EXAMPLE,
    "summary": EXAMPLE SUMMARY modified,
    "filePath": /……/example.pdf,
    "confId": 1,
    "contributor": Xuanjie Fang,
    "topics": [
      HCI
    ],
    "authors": [
      {
        "_id": hui789cx,
        "username": xuan,
        "fullName": Xuanjie Fang,
        "password": 123456,
        "email": 123@abc.com,
        "company": fudan,
        "area": shanghai
      }
    ]
}
}
```

> 找不到对应记录时返回code:3, error: 没有该记录

#### 通过会议Id获取其所有论文信息

```text
[GET] /getDraftByConference/<cid>
```

使用该方法根据会议id获取该会议上所有收到的论文相关信息

**请求**

| 名字 | 类型   | 可选  | 举例 | 说明        |
| ---- | ------ | ----- | ---- | ----------- |
| cid  | String | false | 1    | url路径参数 |

**响应**

```text
{
    "code": 0,
    "data":{
    [
  	{
    	"_id": draft1,
    	"title": AN EXAMPLE modified,
    	"summary": EXAMPLE SUMMARY,
    	"filePath": /……/example.pdf,
    	"confId": 1,
    	"contributor": Xuanjie Fang,
    	"topics": [
      		HCI
    	],
    	"authors": [
      	{
        	"_id": hui789cx,
        	"username": xuan,
        	"fullName": Xuanjie Fang,
        	"password": 123456,
        	"email": 123@abc.com,
        	"company": fudan,
        	"area": shanghai
      	}
    	]
  	}
	]
}
```

> 找不到对应记录时返回code:3, error: 没有该记录