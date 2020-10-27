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

  | Column    | Type              | desc                                   |
  | --------- | ----------------- | -------------------------------------- |
  | _id       | Object/string     | mongo 主键                             |
  | Shortname | String            | 用户名                                 |
  | Fullname  | String            | 真实姓名                               |
  | Start     | Datetime          | 举办时间                               |
  | Location  | string            | 举办地点                               |
  | Deadline  | Datetime          | 截止时间                               |
  | Publish   | Datetime          | 评审发布时间                           |
  | chair     | String            | 存储user_id 作为标识符，不可更改       |
  | Pc        | [uid1, uid2,uid3] | 存储userid信息，暂时不考虑人数限制问题 |
  | status    | string            | 规定状态： applying，success，failed   |



* **API**

  | api                         | type | desc                              | param                                                        | return |
  | --------------------------- | ---- | --------------------------------- | ------------------------------------------------------------ | ------ |
  | /api/meeting/apply          | Post | 申请会议                          | 除了id和pc,status以外的所有字段，此时本人自动成为chair，当前该chair并不可见 | None   |
  | /api/meeting/find/ordinary  | Post | 普通查找，只返回status为success的 | mongo风格的查找，filter写法}                                 | List   |
  | /api/meeting/find/apply     | Post | filter写法，查找自己申请的会议    | mongo风格的查找                                              | list   |
  | /api/meeting/:mid/audit     | Post | 审核申请,admin为true的用户        | {'audit': ture/false}                                        | status |
  | /api/meeting/:mid/pc/invite | Post | 邀请pc                            | 列表形式的参数[uid1, uid2,uid3]                              | None   |
  | /api/meeting/:mid/pc/attend | Post | 邀请后加入                        | {'attend': true/false}                                       | None   |
  |                             |      |                                   |                                                              |        |

  ###### 



----







