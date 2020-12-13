# NotificationService

!!! note
    **port**: 5007 <br>	==rabbitmq+websocket== 实现的异步消息通信  




## 1. 数据库字段参考

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| receiver | String | false | hui789cx | user id |
| message | String | false | 你的论文录用结果已发布  | |
| status | String | false | read | read/unread 表示已读/未读 |

---



## 2. websocket相关
```
ws = new WebSocket('ws://<HOST>:<PORT>/ws/'+uid);
ws.onopen = function (event) {
    console.log('WebSocket连接成功    状态码：' + ws.readyState)
}

ws.onerror = function (event) {
    console.log('WebSocket连接发生错误   状态码：' + ws.readyState)
}

ws.onclose = function (event) {
    console.log('WebSocket连接关闭')
}

ws.onmessage = function (event) {
    console.log("接收到WebSocket服务器消息: " + event.data);
};

// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function (event) {
    ws.close();
    ws = null;
}
```

---



## 2. 面向用户API

!!! attention
    以下所有api均需登录后方可调用，否则返回`{'code': 8, "data": "没有登录"}`

#### 2.1 通过Id获取消息信息

```
[GET] /api/notification/getNotificationById/<nid>
```

!!! hint
   使用该方法根据id获取消息信息详情

#### **请求**

| 名字 | 类型   | 可选  | 举例                                                         | 说明        |
| ---- | ------ | ----- | ------------------------------------------------------------ | ----------- |
| nid  | String | false | MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA | url路径参数 |

#### **响应**

```
{
    "code": 0,
    "data": {"_id": "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA", "receiver": "hui789cx", ...} // 返回对应的record
}
```

#### 异常

| 状态码 | 原因       |
| ------ | ---------- |
| 3      | 没有该记录 |

----



#### 2.2 通过用户Id获取用户收到的信息

```
[GET] /api/notification/getNotificationByUser/<uid>
```

!!! hint
   使用该方法根据用户Id获取用户收到的消息 

#### **请求**

| 名字 | 类型   | 可选  | 举例     | 说明        |
| ---- | ------ | ----- | -------- | ----------- |
| uid  | String | false | hui789cx | url路径参数 |

#### **响应**

```
{
    "code": 0,
    "data": [{"_id": "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA", "receiver": "hui789cx", ...}, {"_id": ""}] // 返回对应的record list
}
```



----




#### 2.3 修改消息状态

```
 [POST] /changeNotificationStatus
```

!!! hint
   使用该方法修改消息的状态为已读/未读  

#### **请求** 

| 名字   | 类型   | 可选  | 举例                                                         | 说明            |
| ------ | ------ | ----- | ------------------------------------------------------------ | --------------- |
| nid    | String | false | "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA", |                 |
| status | String | false | "read"                                                       | "read"/"unread" |

#### **响应**

```
{
    "code": 0,
    "data": null
}
```



---



## 3. 服务间通信API

### 3.1 发送消息  

-----


```
 [POST] /api/notification/send 
```

!!! note
  使用该方法向指定用户对应的消息队列发送一条消息 

**请求**

```
{
    "uid_list": ["hui789cx", "kg7oa8xv"],
    "message": "你的论文录用结果已发布"
}
```
| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| uid_list | List\<String\> | false | ["hui789cx", "kg7oa8xv"] | |
| message | String | false | "你的论文录用结果已发布" | |

**响应**
```
{
    "code": 0, 
    "data": ["inserted_id1", "inserted_id2"]
}
```





## 4. 心跳检测

!!! attention
    心跳检测API针对注册中心 **consul** 调用，对服务的健康进行自动检测

```
[GET] /api/notification/check
```

