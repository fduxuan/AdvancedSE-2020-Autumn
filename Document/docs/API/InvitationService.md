# InvitationService

!!! note
    **port**: 5004 



## 1. 数据库字段参考

| 名字    | 类型   | 可选  | 举例                | 说明   |
| ------- | ------ | ----- | ------------------- | ------ |
| inviter | String | false | xuan                | 发件人 |
| invitee | String | false | fang                | 收件人 |
| confId  | String | false | 1                   |        |
| status  | String | false | init->accept/reject |        |
| _id     | String | false | invitation1         |        |



-----



## 2. 面向用户API

!!! attention
    以下所有api均需登录后方可调用，否则返回`{'code': 8, "data": "没有登录"}`

### 2.1 发送PCmember邀请

```
 [POST] /api/invitation/createInvitation
```

!!! hint
    使用该方法发送PCmember的会议邀请 `

#### **请求**

| 名字    | 类型   | 可选  | 举例 | 说明       |
| ------- | ------ | ----- | ---- | ---------- |
| invitee | String | false | fang | 被邀请者id |
| confId  | String | false | 1    | 会议id     |

#### **响应**

**自动初始化**

`status='init'`

`inviter='current_userid'`

**服务间调用**

* 调用 **NotificationService** 服务获得发送异步通信消息，告诉被邀请者

  `“You have received an invitation about becoming pcmember!”`

```
{
    "code": 0, 
    "data": null
}
```

#### 异常

| 状态码 | 原因           | 说明                  |
| ------ | -------------- | --------------------- |
| 9      | 发送信息不完整 | 缺少confId或者invitee |



----



### 2.2 查看自己收到的邀请

```
[POST] /api/invitation/received
```

!!! hint
    使用该方法获取本人收到的所有邀请

#### **请求**

无需userid参数，从session中获得

| 名字 | 类型   | 可选 | 举例                 | 说明       |
| ---- | ------ | ---- | -------------------- | ---------- |
| Data | Object | true | {'status': 'accept'} | filter参数 |

#### **响应**

**服务间调用**

* 调用 **ConferenceService** 获得 `conference` 的详细信息，组成结构体一体化返回
* 调用 **UserService** 获得` inviter `的详细信息，组成结构体一体化返回

```
{
    "code": 0,
    "data": [
  		{
    		"_id": 'iid'
            ……
            "user_info": { userinfo}, //获得的是inviter信息，在前端展现
            "conference_info": {conferenceinfo} // 获得conference信息    
  		}
	]
}
```



-----



### 2.3 查看自己发出的邀请

```
[POST] /api/invitation/sent
```

!!! hint
    使用该方法获取自己发出的所有邀请

#### **请求**

| 名字 | 类型   | 可选 | 举例               | 说明       |
| ---- | ------ | ---- | ------------------ | ---------- |
| data  | Object | True | {'status': 'init'} | filter参数 |

#### **响应**

**服务间调用**

- 调用 **ConferenceService** 获得 `conference` 的详细信息，组成结构体一体化返回
- 调用 **UserService** 获得` invitee `的详细信息，组成结构体一体化返回

```
{
    "code": 0,
    "data": [
  		{
    		"_id": 'iid'
            ……
            "user_info": { userinfo}, //获得的是invitee信息，在前端展现
            "conference_info": {conferenceinfo} // 获得conference信息    
  		}
	]
}
```



----



### 2.4 用户接受邀请

```
[POST] /approve/<iid>
```

!!! hint
    使用该方法接受某一个成为PCmember的邀请

#### **请求**

| 名字 | 类型   | 可选  | 举例        | 说明    |
| ---- | ------ | ----- | ----------- | ------- |
| Iid  | String | false | invitation1 | url参数 |

#### **响应**

**服务间调用**

- 调用 **ConferenceService** 获得更新`pcMember`

- 调用 **Notification** 向邀请人发送：

  `{current_username} has agreed to become pcmember`

**自动响应**

`status='accept'`

```
{
    "code": 0, 
    "data":null
}
```

#### 异常

| 状态码 | 原因     | 说明                  |
| ------ | -------- | --------------------- |
| 10     | 无权操作 | 当前用户不为该invitation的被邀请人 |

-----





### 2.5 用户拒绝邀请

```
 [POST] /reject/<iid>
```

!!! hint
    使用该方法拒绝某一个成为PCmember的邀请

#### 请求

| 名字 | 类型   | 可选  | 举例        | 说明    |
| ---- | ------ | ----- | ----------- | ------- |
| Iid  | String | false | invitation1 | url参数 |

#### **响应**

**服务间调用**

- 调用 **ConferenceService** 获得更新`pcMember`

- 调用 **Notification** 向邀请人发送：

  `{current_username} has refused to become pcmember`

**自动响应**

`status='reject'`

```
{
    "code": 0, 
    "data":null
}
```

#### 异常

| 状态码 | 原因     | 说明                               |
| ------ | -------- | ---------------------------------- |
| 10     | 无权操作 | 当前用户不为该invitation的被邀请人 |

------



## 3. 心跳检测

!!! attention
    心跳检测API针对注册中心 **consul** 调用，对服务的健康进行自动检测

```
[GET] /api/invitation/check
```

