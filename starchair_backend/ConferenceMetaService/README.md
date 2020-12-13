# ConferenceMetaService

**port: 5002**

url_prefix="/api/conference"**

# 数据库字段参考

| 名字               | 类型       | 可选  | 举例                                                         | 说明 |
| ------------------ | ---------- | ----- | ------------------------------------------------------------ | ---- |
| shortenform        | String     | false | CHI                                                          |      |
| fullname           | String     | false | ACM Conference on Human Factors in Computing Systems         |      |
| starttime          | String     | false | Mon Dec  1 2020 00:00:00 GMT+0800                            |      |
| location           | String     | false | Shanghai                                                     |      |
| stopsubmittingtime | String     | false | Tue Dec  15 2020 00:00:00 GMT+0800                           |      |
| publishingtime     | String     | false | Mon Dec  30 2020 00:00:00 GMT+0800                           |      |
| chairman           | User       | false | {_id: user_id}                                               |      |
| status             | String     | false | init -> accept / reject -> submitting -> reviewing -> firstDiscussion -> firstPublish -> finalDiscussion -> finalPublish |      |
| topics             | [String]   | false | [HCI]                                                        |      |
| pcmembers          | [pcmember] | false | [uid1, uid2, uid3]                                           |      |

# API文档参考

### 状态码

绝大部分API返回的数据都包含一个code状态码，分别代表不同的状况
以下是状态码表

| 状态码 | 原因             |
| ------ | ---------------- |
| 0      | 正常             |
| 1      | 未知错误         |
| 2      | 不存在该id       |
| 3      | 没有该记录       |
| 4      | 重复的数据id     |
| 5      | 已存在该用户名   |
| 6      | 已存在该邮箱     |
| 7      | 用户名或密码错误 |
| 8      | 没有登录         |
| 9      | 注册信息不完整   |

### 申请创建会议

----

> 以下API，未登录时返回code:8, error: 没有登录  

```text
 [POST] /createConference 
```

使用该方法进行会议创建的申请

**请求**

```text
{
    ShortenForm = 'CHI'
    FullName = 'ACM Conference on Human Factors in Computing Systems'
    StartTime = 'Mon Dec  1 2020 00:00:00 GMT+0800'
    Location = 'Shanghai'
    StopSubmittingTime = 'Tue Dec 15 2020 00:00:00 GMT+0800'
    PublishingTime = 'Mon Dec 30 2020 00:00:00 GMT+0800'
    Topics = '[HCI]'
}
```

| 名字               | 类型     | 可选  | 举例                                                 | 说明 |
| ------------------ | -------- | ----- | ---------------------------------------------------- | ---- |
| shortenform        | String   | false | CHI                                                  |      |
| fullname           | String   | false | ACM Conference on Human Factors in Computing Systems |      |
| starttime          | String   | false | Mon Dec  1 2020 00:00:00 GMT+0800                    |      |
| location           | String   | false | Shanghai                                             |      |
| stopsubmittingtime | String   | false | Tue Dec  15 2020 00:00:00 GMT+0800                   |      |
| publishingtime     | String   | false | Mon Dec  30 2020 00:00:00 GMT+0800                   |      |
| topics             | [String] | false | [HCI]                                                |      |

**响应**
自动初始化 status 为 "init"

```text
{
    "code": 0, 
    "data": conference_id
}
```

### 获取会议相关信息

------

> 以下API，未登录时返回code:8, error: 没有登录

#### 通过会议Id获取会议信息

```text
[GET] /getConferenceById/<cid>
```

使用该方法根据id获取会议信息

**请求**

| 名字 | 类型   | 可选  | 举例 | 说明        |
| ---- | ------ | ----- | ---- | ----------- |
| cid  | String | false | 1    | url路径参数 |

**响应**

```text
{
    "code": 0,
    "data": {
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
			}//返回对应记录信息
}
```

> 找不到对应记录时返回code:3, error: 没有该记录



#### 通过会议Id组获取会议信息

*服务间调用*

```text
[POST] /findConferenceByIds
```

使用该方法根据id获取会议信息

**请求**

| 名字 | 类型 | 可选  | 举例         | 说明        |
| ---- | ---- | ----- | ------------ | ----------- |
| Ids  | List | false | [cid1, cid2] | url路径参数 |

**响应**

```text
{
    "code": 0,
    "data": {
  			"cid1": {info},
  			"cid2": {info}
			}//返回对应记录信息map
}
```

> 找不到对应记录时返回[]

#### 获得用户为chairman的所有会议

```text
[POST] /attendAsChairman
```

使用该方法根据会议发起人id获取相关信息

**请求**

| 名字 | 类型 | 可选  | 举例               | 说明             |
| ---- | ---- | ----- | ------------------ | ---------------- |
| data | Dict | false | {status: 'accept'} | 可以传别的filter |

**响应**

```text
{
    "code": 0,
   	"data": [
   			{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"chairman": {
   						 "_id":hui789cx,
   						 ……
  						},
  			 ……
			}//返回对应记录信息
		]
}
```

> 找不到返回[]



#### 获得用户为pcmembers的所有会议

```text
[POST] /attendAsPc
```

使用该方法根据会议发起人id获取相关信息

**请求**

| 名字 | 类型 | 可选  | 举例               | 说明             |
| ---- | ---- | ----- | ------------------ | ---------------- |
| data | Dict | false | {status: 'accept'} | 可以传别的filter |

**响应**

```text
{
    "code": 0,
   	"data": [
   			{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"chairman": {
   						 "_id":hui789cx,
   						 ……
  						},
  			 ……
			}//返回对应记录信息
		]
}
```

> 找不到返回[]



#### 获取用户参与所有会议的信息

```text
[GET] /attendConference
```

使用该方法获得自己参与的所有会议信息

**请求**

| 名字 | 类型 | 可选  | 举例               | 说明             |
| ---- | ---- | ----- | ------------------ | ---------------- |
| Data | Dict | False | {status: 'accept'} | 可以传别的filter |

**响应**

```text
{
    "code": 0,
   	"data": [
   			{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"chairman": {
   						 "_id":hui789cx,
   						 ……
  						},
  			 ……
			}//返回对应记录信息
		]
}
```

> 找不到返回[]



#### 通过会议状态获取未被审核的会议信息

```text
[GET] /getUncheckedConference
```

使用该方法获取所有未被审核的会议

**请求**

无需参数

**响应**

```text
{
    "code": 0,
  	"data": [
  			{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"status": reviewing,
  			 ……
			}//返回对应记录信息
		]
}
```

> 找不到对应记录时返回code:3, error: 没有该记录



#### 获得所有可见的会议

```text
[POST] /visible
```

使用该方法获得所有可见的会议，即状态不为init和reject的会议

**请求**

| 名字 | 类型 | 可选  | 举例               | 说明             |
| ---- | ---- | ----- | ------------------ | ---------------- |
| Data | Dict | False | {status: 'accept'} | 可以传别的filter |

响应**

```text
{
    "code": 0,
  	"data": [
  			{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"status": reviewing,
  			 ……
			}//返回对应记录信息
		]
}
```

> 找不到对应记录时返回[]



#### 通过会议状态获取可投稿的会议信息

```text
[GET] /getAcceptConference
```

使用该方法获取所有可投稿的会议

**请求**

无需参数

**响应**

```text
{
    "code": 0,
  	"data": [
  			{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"status": accept,
  			 ……
			}//返回对应记录信息
		]
}
```

> 找不到对应记录时返回code:3, error: 没有该记录

### 审核会议通过

------

> 未登录时返回code:8, error: 没有登录
>
> 根据login判断是否为admin

```text
[POST] /approveConference/<cid>
```

使用该方法通过特定会议ID的会议申请

**请求**

| 名字 | 类型   | 可选  | 举例 | 说明        |
| ---- | ------ | ----- | ---- | ----------- |
| cid  | String | false | 1    | url路径参数 |

**响应**

> 该会议status修改为accept

```text
{
    "code": 0,
    "data": null
}
```

### 审核会议未通过

------

> 未登录时返回code:8, error: 没有登录
>
> 根据uid判断是否为admin

```text
[POST] /rejectConference/<cid>
```

使用该方法拒绝特定会议ID的会议申请

**请求**

| 名字 | 类型   | 可选  | 举例 | 说明        |
| ---- | ------ | ----- | ---- | ----------- |
| cid  | String | false | 1    | url路径参数 |

**响应**

> 该会议status修改为reject

```text
{
    "code": 0,
    "data": null
}
```

### 为会议添加审稿人

------

> 未登录时返回code:8, error: 没有登录
>
> 根据uid与对应cid判断是否为chairman

```text
[POST] /addPcMember/<cid>
```

使用该方法为会议添加审稿人

**请求**

```text
{
    "pcmemberID": hui789cx,
    "confId": 1,
}
```

| 名字       | 类型   | 可选  | 举例     | 说明 |
| ---------- | ------ | ----- | -------- | ---- |
| pcmemberId | String | false | hui789cx |      |
| confId     | String | false | 1        |      |

**响应**

```text
{
    "code": 0,
}
```

### 修改会议状态

------

> 未登录时返回code:8, error: 没有登录
>
> 根据uid判断是否为admin

```text
[POST] /changeStatus/<cid>
```

使用该方法为会议修改状态

**请求**

```text
{
    "status": "accept"
}
```

| 名字   | 类型   | 可选  | 举例   | 说明 |
| ------ | ------ | ----- | ------ | ---- |
| status | String | false | accept |      |
| cid    | String | false | 1      |      |

**响应**

```text
{
    "code": 0,
    "data": null
}
```

