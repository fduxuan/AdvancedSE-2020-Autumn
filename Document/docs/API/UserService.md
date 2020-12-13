# UserService

!!! note
    **port**: 5001 <br>



## 1. 数据库字段参考

| 名字 | 类型 | 可选 | 举例 |
| ---  | --- | --- | --- |
| username | String | false | xuan |
| fullname | String | false | Xuanjie Fang |
| password | String | false | 123456 |
| email | String | false | 123@abc.com |
| company | String | false | fudan |
| area | String | false | shanghai |
| admin | Boolean | false | true |

<br>

--------



## 2. 面向用户API



### 2.1 登录

```
 [POST] /api/user/login 
```

!!! Hint
    使用该方法进行登录

#### 请求

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| username | String | false | "xuan" | |
| password | String | false | "123456" | |

#### 响应
```
{
    "code": 0, 
    "data": {username:xxx, fullname:xxx...}(当前用户信息)
}
```
#### 异常

| 状态码 | 原因             |
| ------ | ---------------- |
| 7      | 用户名或密码错误 |



----



### 2.2 注销

```
[POST] /api/user/logout
```

!!! Hint
    使用该方法退出登录

#### 请求

无需参数（利用session）  

#### 响应

```
{
    "code": 0,
    "data": null
}
```
#### 异常

| 状态码 | 原因             |
| ------ | ---------------- |
| 8      | 用户名或密码错误 |

-----



### 2.3 注册

```
[POST] /api/user/register
```

!!! Hint
    使用该方法注册新账户（由于原starchair要求所有字段必填，此处同样设置）  

#### 请求 

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| username | String | false | xuan | |
| fullname | String | false | Xuanjie Fang | |
| password | String | false | 123456 | |
| email | String | false | 123@abc.com | |
| company | String | false | fudan | |
| area | String | false | shanghai | |

#### 响应   
```
{
    "code": 0,
    "data": "user_id"(返回注册后生成user的id)
}

```
#### 异常

| 状态码 | 原因           |
| ------ | -------------- |
| 9      | 注册信息不完整 |
| 5      | 已存在该用户名 |
| 6      | 已存在该邮箱   |

-----



### 2.4 当前用户信息

```
[GET] /api/user/info
```

!!! Hint
    使用该方法获得自己的信息

#### 请求 

无需参数，根据session自动获得

#### 响应   

```
{
    "code": 0,
    "data": {...} 用户的个人信息
}

```

#### 异常

| 状态码 | 原因     |
| ------ | -------- |
| 8      | 没有登录 |

-----



### 2.5 通过Id获取用户详细信息

```
[GET] /api/user/getUserById/<uid>
```

!!! Hint
    使用该方法根据id获取用户详细信息

#### 请求

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| uid | String | false | hui789cx | url路径参数 |

#### 响应

```
{
    "code": 0,
    "data": {"_id": "hui789cx", "username": "xuan", ...} // 返回对应的record
}
```
#### 异常

| 状态码 | 原因       |
| ------ | ---------- |
| 3      | 没有该记录 |
| 8      | 没有登录   |

---

### 2.6 通过username查询用户信息

```
[POST] /api/user/findUserByName
```

!!! Hint
    使用该方法根据fullname进行模糊查询

#### 请求

| 名字     | 类型   | 可选 | 举例 | 说明        |
| -------- | ------ | ---- | ---- | ----------- |
| fullname | String | True | xuan | url路径参数 |

#### 响应

```
{
    "code": 0,
    "data": {"_id": "hui789cx", "username": "xuan", ...} // 返回对应的record
}
```

#### 异常

| 状态码 | 原因     |
| ------ | -------- |
| 8      | 没有登录 |

----

### 2.7 管理员判断

```
 [GET] /api/user/isAdmin
```

!!! Hint
    使用该方法判断用户是否为管理员  

**请求** 

无需参数（利用session） 

**响应**

```
{
    "code": 0,
    "data": false // 返回true/false
}
```

#### 异常

| 状态码 | 原因     |
| ------ | -------- |
| 8      | 没有登录 |

------



## 3. 服务间通信API

!!! attention
    服务间API仅供服务间通信调用，通过设置白名单阻止外来源访问



### 3.1 通过Id组获取用户信息

```
[POST] /api/user/findUserByIds
```

!!! hint
    使用该方法根据id获取用户信息 

#### 请求

| 名字 | 类型 | 可选  | 举例           | 说明        |
| ---- | ---- | ----- | -------------- | ----------- |
| Ids  | List | false | ["id1", 'id2'] | url路径参数 |

#### 响应

```
{
    "code": 0,
    "data": {'id1': {userinfo}, 'id2': {userinfo}} // 返回对应的record map
}
```

----



## 4. 心跳检测

!!! attention
    心跳检测API针对注册中心 **consul** 调用，对服务的健康进行自动检测

```
[GET] /api/user/check
```

