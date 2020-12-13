启动有先后顺序

```
# 启动所有基础镜像，数据库，注册中心，消息队列
kubectl apply -f starchair-basic-component.yml

# 启动所有后端服务
kubectl apply -f starchair-backend.yml

# 启动前端
kubectl apply -f starchair-frontend.yml

```


通过查看以下配置，启动后可访问地址
starchair-frontend:80 我们的前端
consul-service:8500 注册中心前端
rabbit-service:15672 消息队列前端

## 配置详情

| service name | port | docker image | username | password | description | external IP| 注册中心service id |
| ---  | --- | --- | --- | ---  | --- | --- | --- | 
| consul-service | 8500 | consul:1.4.4 |  |  | 注册中心 | true | consul |
| rabbit-service | 5672, 15672 | rabbitmq:3.8-management | guest | guest | 消息队列，5672为访问端口，15672为web界面端口 | true | |
| redis-service | 6379 | redis:latest | | | session缓存 | false | |
| mongo-user-service | 27017 | mongo:latest | | | user数据库 | false | |
| mongo-conference-service | 27017 | mongo:latest | | | conference数据库 | false | |
| mongo-draft-service | 27017 | mongo:latest | | | draft数据库 | false | |
| mongo-invitation-service | 27017 | mongo:latest | | | invitation数据库 | false | |
| mongo-discussion-service | 27017 | mongo:latest | | | discussion数据库 | false | |
| mongo-review-process-service | 27017 | mongo:latest | | | review process数据库 | false | |
| mongo-notification-service | 27017 | mongo:latest | | | notification数据库 | false | |
|   |  |  |  |   |  |  |  | 
| starchair-frontend | 8080:80 |registry.cn-hangzhou.aliyuncs.com/youngf/starchair-frontend:1.0 | | | 前端 | true | StarchairFrontend | 
| starchair-user-service | 5001 | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-user-service:1.0 | | | UserService | true | UserService | 
| starchair-conference-service | 5002 | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-conference-service:1.0 | | | ConferenceMetaService | true | ConferenceService |
| starchair-draft-service | 5003 | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-draft-service:1.0 | | | DraftMetaService | true | DraftService |
| starchair-invitation-service | 5004 | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-invitation-service:1.0| | | InvitationService | true | InvitationService |
| starchair-discussion-service | 5005 |registry.cn-hangzhou.aliyuncs.com/youngf/starchair-discussion-service:1.0 | | | DiscussionService | true | DiscussionService |
| starchair-review-process-service | 5006 | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-review-process-service:1.0| | | ReviewProcessService | true | ReviewProcessService |
| starchair-notification-service | 5007 | registry.cn-shanghai.aliyuncs.com/iris_jiang/starchair-notification-service:1.0 | | | NotificationService | true | NotificationService |
