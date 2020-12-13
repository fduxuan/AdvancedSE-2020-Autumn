# NotificationService
python rabbitmq+websocket实现的异步消息通信  

**port: 5007**

**url_prefix="/api/notification"**




# 数据库字段参考

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| receiver | String | false | hui789cx | user id |
| message | String | false | 你的论文录用结果已发布  | |
| status | String | false | read | read/unread 表示已读/未读 |


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



### websocket相关
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



### 发送消息  

-----


```
 [POST] /send 
```

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





### 获取消息相关信息 

----

> 以下API，未登录时返回code:8, error: 没有登录  



#### 通过Id获取消息信息

```
[GET] /getNotificationById/<nid>
```

使用该方法根据id获取消息信息 

**请求**

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| nid | String | false | MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA | url路径参数 |

**响应**

```
{
    "code": 0,
    "data": {"_id": "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA", "receiver": "hui789cx", ...} // 返回对应的record
}
```
> 找不到对应记录时返回code:3, error: 没有该记录



#### 通过用户Id获取用户收到的信息

```
[GET] /getNotificationByUser/<uid>
```

使用该方法根据用户Id获取用户收到的消息 

**请求**

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| uid | String | false | hui789cx | url路径参数 |

**响应**
```
{
    "code": 0,
    "data": [{"_id": "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA", "receiver": "hui789cx", ...}, {"_id": ""}] // 返回对应的record list
}
```




#### 修改消息状态
> 以下API，未登录时返回code:8, error: 没有登录 
```
 [POST] /changeNotificationStatus
```

使用该方法修改消息的状态为已读/未读  

**请求** 

```
{
    "nid": "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA",
    "status": "read"
}
```
| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| nid | String | false | "MjAyMC0xMS0zMCAxOTo1MDo0MS4wNDg0OTRQkpIWN4QjYmVCf3TNHpLfy3wBJ_vrWovN2hZKpj0ZTA", | |
| status | String | false | "read" | "read"/"unread" |

**响应**

```
{
    "code": 0,
    "data": null
}
```
