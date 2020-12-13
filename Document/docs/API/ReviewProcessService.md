# ReviewProcessService

!!! note
    **port**: 5006 <br>

## 1. 数据库字段参考

| 名字        | 类型   | 可选  | 举例                                     | 说明 |
| ----------- | ------ | ----- | ---------------------------------------- | ---- |
| _Id         | String | false | process1                                 |      |
| confId      | String | false | conference1                              |      |
| draftId     | String | false | draft1                                   |      |
| pcMemberId  | String | false | pcmember1                                |      |
| rebuttal    | String | false | rebuttal                                 |      |
| status      | String | false | init->firstResult->rebuttal->finalResult |      |
| contributor | String | false | user_id1                                 |      |
| score       | String | false | 2                                        |      |

----



## 2. 面向服务API

!!! attention
    以下所有api均需登录后方可调用，否则返回`{'code': 8, "data": "没有登录"}`

### 2.1 为稿件评分

```
 [POST] /api/reviewProcess/score 
```

!!! hint
    使用该方法为某一稿件评分

#### **请求**

根据session自动获得`user_id`

| 名字       | 类型   | 可选  | 举例     | 说明 |
| ---------- | ------ | ----- | -------- | ---- |
| score      | String | false | 1/-1     |      |
| draftId    | String | false | draftId1 |      |
| confidence | String | false | High     |      |
| comment    | String | false | Good     |      |

#### **响应**

**自动响应**

* `status='firstResult'` 当前状态为`init`时触发
* `status='finalResult'` 当前状态为`rebuttal`时触发

```
{
  “code”:0,
  "data":null
}
```

#### 异常

| 状态码 | 原因     | 说明                             |
| ------ | -------- | -------------------------------- |
| 10     | 无权操作 | 当前用户不为该稿件分配的pcmember |

----



### 2.2 获取当前用户需评审的投稿

```
 [POST] /api/reviewProcess/getReviewProcessByPcMember
```

!!! hint
    使用该方法，pcmember获取自己需要审阅的稿件

#### **请求**

| 名字   | 类型   | 可选  | 举例           | 说明     |
| ------ | ------ | ----- | -------------- | -------- |
| cid    | String | false | Conference_id1 | 会议id   |
| status | String | true  | ‘firstResult’  | 筛选条件 |

#### **响应**

**服务间调用**

* 调用 **DraftService** 获得 `draft` 详细信息，组成结构体一体化返回
* 调用 **UserService** 获得 `contributor` 详细信息，组成结构体一体化返回

```
{
    "code": 0, 
    "data": [process_info1 ,process_info2] // 其中process_info中包含 draft_info, contributor_info 作为详细信息字段
}
```



-----



### 2.3 分配稿件

```
 [POST] /api/reviewProcess/allocDraft
```

!!! hint
    `chairman` 使用该方法为所有pcmember分配稿件， 对每个稿件创建3个reviewProcess, 分别属于三个pcMembers

#### **请求**

| 名字   | 类型   | 可选  | 举例           | 说明 |
| ------ | ------ | ----- | -------------- | ---- |
| confId | string | false | conference_id1 |      |

#### **响应**

**服务间通信**

* 调用 **ConferenceService** 获得会议详情，得到 `pcMember`
* 调用 **DraftService** 获得当前会议下的所有稿件信息

```
{
    "code": 0, 
    "data": null
}
```

#### 异常

| 状态码 | 原因     | 说明                       |
| ------ | -------- | -------------------------- |
| 3      | 没有记录 | 不存在该会议               |
| 10     | 无权操作 | 无稿件提交时不可开启       |
| 10     | 无权操作 | 当前`status!='submitting'` |

----



### 2.4 Rebuttal

```
[POST] /api/reviewProcess/rebuttal
```

!!! hint
    `contributor` 使用该方法为自己的论文提交**rebuttal**

#### **请求**

| 名字     | 类型   | 可选  | 举例                        | 说明 |
| -------- | ------ | ----- | --------------------------- | ---- |
| reviewId | string | false | Review_id1                  |      |
| rebuttal | String | False | 'I think my  draft is good' |      |

#### 响应

**自动响应**

`contributor`提交rebuttal后，会对该稿件所有的ReviewProcess更改状态：

`status='rebuttal'`

```
{
    "code": 0, 
    "data": null
}
```



----





## 3. 服务间通信API

!!! attention
    服务间API仅供服务间通信调用，通过设置白名单阻止外来源访问

### 3.1 获取会议下processReview

```
[POST] /api/reviewProcess/getReviewProcessByConfId/<cid>
```

!!! hint
    根据会议id获得一个会议下的所有reviewProcess

#### **请求**

| 名字 | 类型   | 可选 | 举例             | 说明       |
| ---- | ------ | ---- | ---------------- | ---------- |
| data | Object | True | {'status': xxxx} | post的整体 |

#### 响应

```
{
    "code": 0, 
    "data": [process_info1, process_info2]
}
```



## 4. 心跳检测

!!! attention
    心跳检测API针对注册中心 **consul** 调用，对服务的健康进行自动检测

```
[GET] /api/reviewProcess/check
```

