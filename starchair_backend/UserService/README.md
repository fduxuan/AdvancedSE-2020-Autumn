# UserService
**port: 5001**

**url_prefix="/api/user"**




# 数据库字段参考

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| username | String | false | xuan | |
| fullname | String | false | Xuanjie Fang | |
| password | String | false | 123456 | |
| email | String | false | 123@abc.com | |
| company | String | false | fudan | |
| area | String | false | shanghai | |
| admin | Boolean | false | true | |

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







### 登录与注册  

-----



#### 登录

```
 [POST] /login 
```

使用该方法判断登录是否成功 

**请求**

```
{
    "username": "xuan",
    "password": "123456"
}
```
| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | ---| ---| --- |
| username | String | false | "xuan" | |
| password | String | false | "123456" | |

**响应**
```
{
    "code": 0, 
    "data": null
}
```
> 登录参数校验失败时返回code:7, error: 用户名或密码错误



#### 注销

```
[POST] /logout
```

使用该方法退出登录  

**请求**

无需参数（利用session）  

**响应**

```
{
    "code": 0,
    "data": null
}
```
> 未登录时返回code:8, error: 没有登录  



#### 注册

```
[POST] /register
```

使用该方法注册新账户（由于原starchair要求所有字段必填，此处同样设置）  

**请求**  

```
{
    "username": "xuan",
    "fullname": "Xuanjie Fang",
    "password": "123456",
    "email": "123@abc.com",
    "company": "fudan",
    "area": "shanghai",
    "admin": true
}
```

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| username | String | false | xuan | |
| fullname | String | false | Xuanjie Fang | |
| password | String | false | 123456 | |
| email | String | false | 123@abc.com | |
| company | String | false | fudan | |
| area | String | false | shanghai | |
| admin | Boolean | false | true | |

**响应**   
```
{
    "code": 0,
    "data": "inserted_id"
}
```
> 注册信息中有字段缺失时返回code: 9, error: 注册信息不完整
>
> username已存在时返回code: 5, error: 已存在该用户名  
>
> email已存在时返回code: 6, error: 已存在该邮箱  
>
> mongo新建记录id重复时返回code: 4, error: 重复的数据id  





### 获取用户相关信息 

----



> 以下API，未登录时返回code:8, error: 没有登录  



#### 通过Id获取用户信息

```
[GET] /getUserById/<uid>
```

使用该方法根据id获取用户信息 

**请求**

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| uid | String | false | hui789cx | url路径参数 |

**响应**

```
{
    "code": 0,
    "data": {"_id": "hui789cx", "username": "xuan", ...} // 返回对应的record
}
```
> 找不到对应记录时返回code:3, error: 没有该记录



#### 通过Id组获取用户信息

* *服务间通信调用*

```
[POST] /findUserByIds
```

使用该方法根据id获取用户信息 

**请求**

| 名字 | 类型 | 可选  | 举例           | 说明        |
| ---- | ---- | ----- | -------------- | ----------- |
| Ids  | List | false | ["id1", 'id2'] | url路径参数 |

**响应**

```
{
    "code": 0,
    "data": {'id1': {userinfo}, 'id2': {userinfo}} // 返回对应的record map
}
```

> 找不到返回[]



#### 通过username获取用户信息

```
[POST] /findUserByName
```

使用该方法根据用户名获取相关信息 

支持模糊查询

**请求**

| 名字 | 类型 | 可选 | 举例 | 说明 |
| ---  | --- | --- | --- | ---  |
| name | String | True | xuan | url路径参数 |

**响应**

```
{
    "code": 0,
    "data": {"_id": "hui789cx", "username": "xuan", ...} // 返回对应的record
}
```
> 找不到返回[]



#### 管理员判断

```
 [GET] /isAdmin
```

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

