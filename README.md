# 课程实践3



### 架构基础

* **数据库为mongodb**
* **所有api均为get或者post形式，post请求头规定为json**
* **使用redis作为session缓存，共享session，作为登录判断**



#### user

忽略首字母大写

* **数据库设计**

  | Column   | Type          | desc                                                         |
  | -------- | ------------- | ------------------------------------------------------------ |
  | _id      | Object/string | mongo 主键                                                   |
  | Nickname | String        | 用户名                                                       |
  | Fullname | String        | 真实姓名                                                     |
  | Password | Passwork      | 随意，不加密也行                                             |
  | email    | string        |                                                              |
  | company  | string        |                                                              |
  | area     | String        | 用户所在地                                                   |
  | admin    | boolen        | 决定用户是否为该整个系统的管理员，默认始终为false，在此应用中不应该提供api更改 |

  

* **API**

  | api                 | type | desc     | param                                         | return                                                       |
  | ------------------- | ---- | -------- | --------------------------------------------- | ------------------------------------------------------------ |
  | /api/user/register  | Post | 注册     | 除了admin和id以外的所有字段                   | None                                                         |
  | /api/user/login     | Post | 登录     | {nickname, password }**or** {email, password} | None                                                         |
  | /api/user/find/:mid | Post | 查找用户 | mongo风格的查找，filter写法                   | 列表形式的可见用户信息，当前规定为除password，admin之外的所有字段：[{user1}, {user2}] |

  ###### **事实上这里有一个问题，chair 可以在搜索框中搜索系统用户的真实姓名。**  

  **但是chair是针对一个会议的，而并非全局，因此这里需要进一步的讨论**			

  

-----

#### Meeting

* **数据库设计**

  | Column   | Type          | desc                                                         |
  | -------- | ------------- | ------------------------------------------------------------ |
  | _id      | Object/string | mongo 主键                                                   |
  | Nickname | String        | 用户名                                                       |
  | Fullname | String        | 真实姓名                                                     |
  | Password | Passwork      | 随意，不加密也行                                             |
  | email    | string        |                                                              |
  | company  | string        |                                                              |
  | area     | String        | 用户所在地                                                   |
  | admin    | boolen        | 决定用户是否为该整个系统的管理员，默认始终为false，在此应用中不应该提供api更改 |

  

