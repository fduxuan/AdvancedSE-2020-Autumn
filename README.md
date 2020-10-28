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
  | admin    | boolean       | 决定用户是否为该整个系统的管理员，默认始终为false，在此应用中不应该提供api更改 |

  

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

  | Column    | Type                                      | desc                                                         |
  | --------- | ----------------------------------------- | ------------------------------------------------------------ |
  | _id       | Object/string                             | mongo 主键                                                   |
  | Shortname | String                                    | 用户名                                                       |
  | Fullname  | String                                    | 真实姓名                                                     |
  | Start     | Datetime                                  | 举办时间                                                     |
  | Location  | string                                    | 举办地点                                                     |
  | Deadline  | Datetime                                  | 截止时间                                                     |
  | Publish   | Datetime                                  | 评审发布时间                                                 |
  | chair     | String                                    | 存储user_id 作为标识符，不可更改                             |
  | Pc        | [{uid1: topicidlist}, {uid2:topicidlist}] | 存储userid信息，暂时不考虑人数限制问题                       |
  | status    | string                                    | 规定状态，会议的生命周期： applying--> success/failed —>firstSubmit—>review—>firstDiscussion—>firstPublish—>rebuttalDiscussion—>finalPublish |



* **API**

  | api                             | type | desc                              | param                                                        | return |
  | ------------------------------- | ---- | --------------------------------- | ------------------------------------------------------------ | ------ |
  | /api/meeting/apply              | Post | 申请会议                          | 除了id和pc,status以外的所有字段，此时本人自动成为chair，当前该chair并不可见 | None   |
  | /api/meeting/find/ordinary      | Post | 普通查找，只返回status为success的 | mongo风格的查找，filter写法                                  | List   |
  | /api/meeting/find/apply         | Post | filter写法，查找自己申请的会议    | mongo风格的查找                                              | list   |
  | /api/meeting/find/unchecked     | Post | 查找当前待审核的会议，admin为true的用户，filter写法    | mongo风格的查找                                              | list   |
  | /api/meeting/:mid/audit         | Post | 审核申请,admin为true的用户        | {'audit': ture/false}                                        | status |
  | /api/meeting/:mid/pc/invite     | Post | 邀请pc                            | 列表形式的参数[uid1, uid2,uid3]                              | None   |
  | /api/meeting/:mid/pc/attend     | Post | 邀请后加入                        | {'attend': true/false}                                       | None   |
  | /api/meeting/:mid/update/status | Post | 更新status                        | status: string                                               | Status |
  | /api/meeting/show/:mid          | Get  | 获取会议内容                      | :mid                                                         | Json   |

  ###### 



----

#### Invitation

- **数据库设计**

  | Column     | Type          | desc              |
  | ---------- | ------------- | ----------------- |
  | _id        | Object/string | mongo 主键        |
  | Meeting_id | String        | 会议id            |
  | invitor    | String        | 真实姓名          |
  | invitee    | String        | 被邀请者的id      |
  | Status     | Boolean       | None-> true/false |



- **API**

  | api                         | type | desc                        | param | return |
  | --------------------------- | ---- | --------------------------- | ----- | ------ |
  | /api/invitation/receive/all | Get  | 获取自己接收的所有invitaion |       | None   |
  | /api/invitation/send/all    | Get  | 获取自己发送的所有invitaion |       | List   |



---

#### Paple

- **数据库设计**

  | Column      | Type                                               | desc       |
  | ----------- | -------------------------------------------------- | ---------- |
  | _id         | Object/string                                      | mongo 主键 |
  | Meeting_id  | String                                             | 会议id     |
  | Author      | [{userinfo1}, {user_info2}]                        | 直接       |
  | contributor | String                                             | 投稿人id   |
  | Status      | Boolean                                            | 标题       |
  | Start       | Datetime                                           | 投稿时间   |
  | Abstract    | String                                             | 摘要       |
  | attachment  | File_id ， 存在切片里，                            | 附件       |
  | topic       | [tid1, tid2]                                       | topic的id  |
  | Pc          | [pc1, pc2..]                                       | Pc_id      |
  | status      | 审稿->一轮讨论-> accept/failed if failed->rebuttal |            |



* **API**

  | API                                    | TYPE | Desc                                                         | Param                   | Return |
  | -------------------------------------- | ---- | ------------------------------------------------------------ | ----------------------- | ------ |
  | /api/article/show/:aid                 | Get  | 查看详情                                                     |                         | json   |
  | /api/article/:aid/allocate/pc          | Post | 分配PC                                                       | :api,自动分配，写在里面 | pc     |
  | /api/article/create                    | Post | 创建一个article                                              | 上传要的数据            |        |
  | /api/article/:aid/attachment/upload    | Post | 事实上需要分开，mongo的文件上传自动切片返回一个id，存在该条数据里 |                         |        |
  | /api/article/:aid/rebuttal             | Post | 二判                                                         |                         |        |
  | /api/article/:aid/attachment/show/:fid | Get  | 获得pdf                                                      |                         |        |
  | /api/article/:aid/update/status        | Post | 事实上应该自动更新，在chair更新会议状态的时候搞，**再议**    | status: String          |        |

  

----

#### record

- **数据库设计**

  | Column     | Type          | desc             |
  | ---------- | ------------- | ---------------- |
  | _id        | Object/string | mongo 主键       |
  | Meeting_id | String        | 会议id           |
  | Article_id | String        | paper的id        |
  | pc_id      | String        | 打分者id         |
  | score      | float         | 分数             |
  | comment    | string        | 评价             |
  | confidence | string        | 我也不知道是什么 |



* **API**

  | API                | TYPE | DESC                                                        | PARAM | RETURN |
  | ------------------ | ---- | ----------------------------------------------------------- | ----- | ------ |
  | /api/record/create | Post | 创建，在分配的时候调用，确保map关系唯一，联合作为主键的意思 | json  |        |
  | /api/record/update | Post | 要修改的时候修改                                            | json  |        |
  |                    |      |                                                             |       |        |

  

#### record

- **数据库设计**

  | Column     | Type          | desc             |
  | ---------- | ------------- | ---------------- |
  | _id        | Object/string | mongo 主键       |
  | Meeting_id | String        | 会议id           |
  | Article_id | String        | paper的id        |
  | pc_id      | String        | 打分者id         |
  | score      | float         | 分数             |
  | comment    | string        | 评价             |
  | confidence | string        | 我也不知道是什么 |



- **API**

  | API                | TYPE | DESC                                                        | PARAM | RETURN |
  | ------------------ | ---- | ----------------------------------------------------------- | ----- | ------ |
  | /api/record/create | Post | 创建，在分配的时候调用，确保map关系唯一，联合作为主键的意思 | json  |        |
  | /api/record/update | Post | 要修改的时候修改                                            | json  |        |
  | /api/record/find   | Post |                                                             |       |        |

  

----

#### Discussion

- **数据库设计**

  | Column     | Type                    | desc                              |
  | ---------- | ----------------------- | --------------------------------- |
  | _id        | Object/string           | mongo 主键                        |
  | Meeting_id | String                  | 会议id                            |
  | Article_id | String                  | paper的id                         |
  | pc_id      | String                  | 打分者id                          |
  | Detail     | string                  | 内容                              |
  | parent     | [grandparent.., parent] | 上级comment的path形式，按层级排序 |
  | children   | [did1, did2, did3]      | 下级评论                          |
  | Timestamp  |                         | 时间戳                            |



* **API**

  | API                    | TYPE | DESC     | PARAM      | RETURN |
  | ---------------------- | ---- | -------- | ---------- | ------ |
  | /api/discussion/create | Post | 创建评论 | 上面的字段 |        |
  | /api/discussion/find   | Post |          |            |        |
  |                        |      |          |            |        |

  

事实上find是一个大概的api，可以根据不同的场景拆分为find/<meeting_id> 啊代表在某个meeting下这种控制