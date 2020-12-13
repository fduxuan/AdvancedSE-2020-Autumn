# ConferenceService

!!! note
    **port**: 5002 <br>

## 1. 数据库字段参考

| 名字               | 类型         | 可选  | 举例                                                         |
| ------------------ | ------------ | ----- | ------------------------------------------------------------ |
| shortenForm        | String       | false | CHI                                                          |
| fullName           | String       | false | ACM Conference on Human Factors in Computing Systems         |
| startTime          | String       | false | 2020-12-13T13:44:44                                          |
| location           | String       | false | Shanghai                                                     |
| stopSubmittingTime | String       | false | 2020-12-13T13:44:44                                          |
| publishingTime     | String       | false | 2020-12-13T13:44:44                                          |
| chairman           | String:id    | false | user_id                                                      |
| status             | String       | false | init -> accept / reject -> submitting -> reviewing -> firstDiscussion -> firstPublish -> finalDiscussion -> finalPublish |
| topics             | [String]     | false | [HCI]                                                        |
| pcMembers          | [String: id] | false | [uid1, uid2, uid3]                                           |

------



## 2. 面向所有用户API

!!! attention
    以下所有api均需登录后方可调用，否则返回`{'code': 8, "data": "没有登录"}`

### 2.1 申请创建会议

```
 [POST] /api/conference/createConference 
```

!!! Hint
    使用该方法进行会议创建的申请

#### 请求

| 名字               | 类型     | 可选  | 举例                                                 |
| ------------------ | -------- | ----- | ---------------------------------------------------- |
| shortenform        | String   | false | CHI                                                  |
| fullname           | String   | false | ACM Conference on Human Factors in Computing Systems |
| starttime          | String   | false | 2020-12-13T13:44:44                                  |
| location           | String   | false | Shanghai                                             |
| stopsubmittingtime | String   | false | 2020-12-13T13:44:44                                  |
| publishingtime     | String   | false | 2020-12-13T13:44:44                                  |
| topics             | [String] | false | [HCI]                                                |

#### **响应**

**自动初始化**

 `status = "init"` 

 `chairman = "current_userid"`(从session中获取，无需传) 

`pcMember = ['current_userid']`(chairman自动成为pcmember)

```
{
    "code": 0, 
    "data": conference_id
}
```



-----



### 2.2 通过会议Id获取会议信息

```
[GET] /api/conference/getConferenceById/<cid>
```

!!! Hint
    使用该方法根据id获取会议信息

#### **请求**

| 名字 | 类型   | 可选  | 举例 | 说明        |
| ---- | ------ | ----- | ---- | ----------- |
| cid  | String | false | 1    | url路径参数 |

#### **响应**

**服务间调用**

调用 **UserService** 服务获得 `chairman` 和  `pcMember` 的详细信息，组成结构体一体化返回

```
{
    "code": 0,
    "data": 
    	{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
  			"chairman": {user_info},
  			"pcMember":[{user_info}, {user_info}]
 			 ……
		}//返回对应记录信息
}
```

#### 异常

| 状态码 | 原因             |
| ------ | ---------------- |
| 3      | 没有该记录 |

-----



### 2.3 当前用户获得自己作为chairman的所有会议

```
[POST] /api/conference/attendAsChairman
```

!!! Hint
    使用该方法根据当前用户的id获取会议信息

#### **请求**

无需传userid参数，自动从session中获取

| 名字 | 类型 | 可选  | 举例               | 说明             |
| ---- | ---- | ----- | ------------------ | ---------------- |
| data | object | true | {status: 'accept'} | 可以传别的filter |

#### **响应**

```
{
    "code": 0,
   	"data": [
   		{
  		    "_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"chairman": "chairman_id",
            "pcMember": [uid1, uid2]
  			 ……
		}//返回对应记录信息
	]
}
```

----



###2.4 当前用户获得自己作为pcMember的所有会议

```
[POST] /api/conference/attendAsPc
```
!!! hint
    使用该方法根据当前用户的id获取相关信息

####**请求**
无需传userid参数，自动从session中获取

| 名字 | 类型 | 可选  | 举例               | 说明             |
| ---- | ---- | ----- | ------------------ | ---------------- |
| data | object | true | {status: 'accept'} | 可以传别的filter |

####**响应**

```
{
    "code": 0,
   	"data": [
   		{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"chairman": "chairman_id"
  			 ……
		}//返回对应记录信息
	]
}
```

----
###2.5 当前用户获得自己作为contributor的所有会议

```
[POST] /api/conference/attendAsContributor
```
!!! hint
    使用该方法根据当前用户的id获取相关信息

####**请求**
无需传userid参数，自动从session中获取

| 名字 | 类型 | 可选  | 举例               | 说明             |
| ---- | ---- | ----- | ------------------ | ---------------- |
| data | object | true | {status: 'accept'} | 可以传别的filter |

####**响应**

**服务间调用**

* 调用 **DraftService** 服务获得当前用户投递的所有稿件，筛选出其中涉及所有的会议id进行查找

```
{
    "code": 0,
   	"data": [
   		{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"chairman": "chairman_id"
  			 ……
		}//返回对应记录信息
	]
}
```
----


### 2.6 获取当前用户所有参与会议的信息

```
[GET] /api/conference/attendConference
```
!!! hint
    使用该方法获得当前用户参与的所有会议信息，包括2.3, 2.4, 2.5 三种情况

####**请求**
无需传userid参数，自动从session中获取

| 名字 | 类型   | 可选 | 举例               | 说明             |
| ---- | ------ | ---- | ------------------ | ---------------- |
| Data | Object | True | {status: 'accept'} | 可以传别的filter |

#### **响应**
**服务间调用**

* 调用 **DraftService** 服务获得当前用户投递的所有稿件，筛选出其中涉及所有的会议id进行查找

```
{
    "code": 0,
   	"data": [
   		{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"chairman": "chairman_id"
  			 ……
		}//返回对应记录信息
	]
}
```
-----




### 2.7 当前所有用户可见的会议

```
[POST] /api/conference/visible
```

!!! hint
    使用该方法获得所有用户可见的会议，即状态不为`init`和`reject`的会议

#### **请求**

| 名字 | 类型   | 可选 | 举例               | 说明             |
| ---- | ------ | ---- | ------------------ | ---------------- |
| Data | Object | true | {status: 'accept'} | 可以传别的filter |

#### 响应

```
{
    "code": 0,
  	"data": [
  		{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"status": "...",
  			 ……
		}//返回对应记录信息
	]
}
```



----



### 2.8 可投稿的会议信息

```text
[GET] /api/conference/getAcceptConference
```

!!! hint
    使用该方法获取所有可投稿的会议， 即状态为`submitting` 的所有会议

#### **请求**

无需参数

#### **响应**

```
{
    "code": 0,
  	"data": [
  		{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName": ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"status": ’accept‘,
  			 ……
		}//返回对应记录信息
	]
}
```



-------



## 3. 面向chairman API

!!! attention
    以下所有api均需登录后方可调用，否则返回`{'code': 8, "data": "没有登录"}`

### 3.1 改变会议状态

```
[POST] /api/conference/changeStatus/<cid>
```

!!! hint
    `chairman`使用该方法为会议修改状态，<u>前提要求会议已被管理员通过</u>

#### **请求**

| 名字   | 类型   | 可选  | 举例   | 说明     |
| ------ | ------ | ----- | ------ | -------- |
| status | String | false | accept | post参数 |
| cid    | String | false | 1      | url参数  |

#### **响应**

**服务间调用**

* 当 `status to ['reviewing', 'firstDiscussion', 'finalDiscussion']`时

  > 调用 **NotificationService** 服务向所有`pcMember`  发送异步通信消息:  `Conference {ShortenForm} has started process {status}!`

* 当 `status to ['finalPublish', 'firstPublish']`时

  > 调用 **NotificationService** 服务向`contributor`  发送异步通信消息: `Conference {ShortenForm} has publish first/final result!`

```
{
    "code": 0,
    "data": null
}
```

#### 异常

| 状态码 | 原因       | 说明|
| ------ | ---------- |   |
| 10     | 无权操作 | 非chairman |
| 11       |   至少需要3个pcmember         | 开启submitting时需要有3个pcmember |
| 12 | 存在文稿未打分 | 开启firstPublish状态时 |
| 13 | 存在rebuttal文稿未打分 | 开启finalPublish状态时 |



----



## 4. 面向管理员API

!!! attention
    以下所有api均需登录后方可调用，否则返回`{'code': 8, "data": "没有登录"}`


### 4.1 审核通过会议

```
[POST] /api/conference/approveConference/<cid>
```

!!! hint
    `admin`使用该方法通过会议申请

#### **请求**

| 名字 | 类型   | 可选  | 举例 | 说明        |
| ---- | ------ | ----- | ---- | ----------- |
| cid  | String | false | 1    | url路径参数 |

#### **响应**

**服务间调用**

* 调用 **NotificationService** 服务获得发送异步通信消息，告诉会议发起人

  `Your Conference xxx has been accepted!`

**自动响应**

`status='accept'`

```text
{
    "code": 0,
    "data": null
}
```

#### 异常

| 状态码 | 原因     |
| ------ | -------- |
| 9      | 无权操作 |



---



### 4.2 审核拒绝会议

```
[POST] /api/conference/rejectConference/<cid>
```

!!! hint
    `admin`使用该方法拒绝会议申请

#### **请求**

| 名字 | 类型   | 可选  | 举例 | 说明        |
| ---- | ------ | ----- | ---- | ----------- |
| cid  | String | false | 1    | url路径参数 |

#### **响应**

**服务间调用**

- 调用 **NotificationService** 服务获得发送异步通信消息，告诉会议发起人

  `Your Conference xxx has been rejected!`

**自动响应**

`status='reject'`

```text
{
    "code": 0,
    "data": null
}
```



-----

### 4.3 获取未被审核的会议信息

```
[GET] /api/conference/getUncheckedConference
```

!!! hint
    使用该方法通过会议状态获取所有未被审核的会议， <u>管理员调用</u>

#### **请求**

无需参数

#### **响应**

```
{
    "code": 0,
  	"data": [
  		{
  			"_id": 1,
  			"shortenForm": CHI,
  			"fullName":ACM Conference on Human Factors in Computing Systems,
 			 ……
  			"status": 'init',
  			 ……
		}//返回对应记录信息
	]
}
```

#### 异常

| 状态码 | 原因     |
| ------ | -------- |
| 10     | 无权操作 |

------



## 5. 服务间通信API

!!! attention
    服务间API仅供服务间通信调用，通过设置白名单阻止外来源访问

### 5.1 添加审稿人

```text
[POST] /addPcMember/<cid>
```

!!! hint
    使用该方法添加审稿人, 在 **InvitationService** 中被邀请者同意时调用

#### **请求**

| 名字     | 类型   | 可选  | 举例         | 说明     |
| -------- | ------ | ----- | ------------ | -------- |
| pcMember | List   | false | [uid1, uid2] | post参数 |
| cid      | String | false | '1'          | url参数  |

#### **响应**

```
{
    "code": 0,
    "data": null
}
```

--------

### 5.2 通过Id组获取会议信息

!!! hint
    使用该方法根据id获取会议信息 

```
[POST] /api/user/findConferenceByIds
```

#### 请求

| 名字 | 类型 | 可选  | 举例           | 说明        |
| ---- | ---- | ----- | -------------- | ----------- |
| Ids  | List | false | ["id1", 'id2'] | url路径参数 |

#### 响应

```
{
    "code": 0,
    "data": {'id1': {conferenceInfo}, 'id2': {conferenceInfo}} // 返回对应的record map
}
```

----



## 6. 心跳检测

!!! attention
    心跳检测API针对注册中心 **consul** 调用，对服务的健康进行自动检测

```
[GET] /api/conference/check
```

