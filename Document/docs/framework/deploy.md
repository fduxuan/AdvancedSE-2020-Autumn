# 注册中心与部署



## 1. 注册中心

因为Eruka是支持Java生态的注册中心，因此在我们的项目中，我们使用了**Consul**作为注册中心进行服务注册与服务发现

==**服务通过向注册中心发送serviceid请求来获得其他服务的端口和地址**==



**问题与解决**

!!! Question
    采用consul 配置文件方式注册服务，灵活性差

* **解决：**<u>改用python consul-client, 每个service启动后调用函数，**动态注册服务</u>**



!!! Question
    服务shutdown后，仍然显示在consul界面上

* **解决：**<u>加入`health check`机制，设置超时销毁。当服务关闭后health check url 不再可达，**超时30s后自动注销。**</u>



----





## 2. K8S

以下是我们小组的注册所用服务列表

| service name                     | port        | docker image                                                 | username | password | description                                  | external IP | 注册中心service id   |
| -------------------------------- | ----------- | ------------------------------------------------------------ | -------- | -------- | -------------------------------------------- | ----------- | -------------------- |
| consul-service                   | 8500        | consul:1.4.4                                                 |          |          | 注册中心                                     | true        | consul               |
| rabbit-service                   | 5672, 15672 | rabbitmq:3.8-management                                      | guest    | guest    | 消息队列，5672为访问端口，15672为web界面端口 | true        |                      |
| redis-service                    | 6379        | redis:latest                                                 |          |          | session缓存                                  | false       |                      |
| mongo-user-service               | 27017       | mongo:latest                                                 |          |          | user数据库                                   | false       |                      |
| mongo-conference-service         | 27017       | mongo:latest                                                 |          |          | conference数据库                             | false       |                      |
| mongo-draft-service              | 27017       | mongo:latest                                                 |          |          | draft数据库                                  | false       |                      |
| mongo-invitation-service         | 27017       | mongo:latest                                                 |          |          | invitation数据库                             | false       |                      |
| mongo-discussion-service         | 27017       | mongo:latest                                                 |          |          | discussion数据库                             | false       |                      |
| mongo-review-process-service     | 27017       | mongo:latest                                                 |          |          | review process数据库                         | false       |                      |
| mongo-notification-service       | 27017       | mongo:latest                                                 |          |          | notification数据库                           | false       |                      |
|                                  |             |                                                              |          |          |                                              |             |                      |
| starchair-frontend               | 8080:80     | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-frontend:1.0 |          |          | 前端                                         | true        | StarchairFrontend    |
| starchair-user-service           | 5001        | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-user-service:1.0 |          |          | UserService                                  | true        | UserService          |
| starchair-conference-service     | 5002        | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-conference-service:1.0 |          |          | ConferenceMetaService                        | true        | ConferenceService    |
| starchair-draft-service          | 5003        | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-draft-service:1.0 |          |          | DraftMetaService                             | true        | DraftService         |
| starchair-invitation-service     | 5004        | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-invitation-service:1.0 |          |          | InvitationService                            | true        | InvitationService    |
| starchair-discussion-service     | 5005        | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-discussion-service:1.0 |          |          | DiscussionService                            | true        | DiscussionService    |
| starchair-review-process-service | 5006        | registry.cn-hangzhou.aliyuncs.com/youngf/starchair-review-process-service:1.0 |          |          | ReviewProcessService                         | true        | ReviewProcessService |
| starchair-notification-service   | 5007        | registry.cn-shanghai.aliyuncs.com/youngf/starchair-notification-service:1.0 |          |          | NotificationService                          | true        | NotificationService  |



### 2.1 镜像生成

由于从dockerhub上拉取镜像过于缓慢**~~（ 推也很慢）~~ **，因此采取阿里云的 ==容器镜像服务==， 将代码推送到阿里云 code，(code.aliyun.com) , 设置规则后 自动推送代码编译镜像，非常的方便，**“妈妈再也不用担心我手动推镜像”**



### 2.2 问题与解决

## 

!!! Question
    每次都要指定namespace 很麻烦

* **解决**：`kubectl config set-context --current --namespace=ase-ns-10`

!!! Question
    按照示例，无法创建pod

* **解决**：`kubectl describe`命令查看pod deployment replicaset情况 发现cpu资源超限。`kubectl describe ns <namespace> `发现整个命名空间限制了cpu 500m memory 1Gi，于是调整了yaml资源配置。

!!! Question
    前端请求路径问题

* **解决**： 从一开始重构这个项目，我们就已经想到了这个问题，因此对于每个服务，我们都有一个统一的前缀，比如**UserService**是`/api/user/xxx` ， **ConferenceService** 则是`/api/conference/xxx`

  > 这样不仅在开发的时候方便配置proxyTable， 而在最后的nginx转发中也非常方便
  >
  > ==同时在配置中解决了文件上传大小限制的问题==

**附上nginx.conf**

```
map $http_upgrade $connection_upgrade {
    default upgrade;
    ''      close;
  }

upstream websocket {
    #ip_hash;
    #转发到服务器上相应的ws端口
    server starchair-notification-service:5007;
}

server {
    listen 80;
    server_name localhost;

    location / {
        root /srv/dist;
        try_files $uri $uri/ @router;
        index  index.html;
    }

    location @router {
        rewrite ^.*$ /index.html last;
    }

    location /docs {
        alias /srv/docs/;
        index index.html;
    }

     location /api/user {
         proxy_pass http://starchair-user-service:5001;
         add_header 'Access-Control-Allow-Origin' '*';
         add_header 'Access-Control-Allow-Headers' '*';
         add_header 'Access-Control-Allow-Methods' '*';
     }

     location /api/conference {
         proxy_pass http://starchair-conference-service:5002;
         add_header 'Access-Control-Allow-Origin' '*';
         add_header 'Access-Control-Allow-Headers' '*';
         add_header 'Access-Control-Allow-Methods' '*';
     }
     location /api/draft {
         proxy_pass http://starchair-draft-service:5003;
         add_header 'Access-Control-Allow-Origin' '*';
         add_header 'Access-Control-Allow-Headers' '*';
         add_header 'Access-Control-Allow-Methods' '*';
 	    client_max_body_size 20m;
     }

     location /api/invitation {
         proxy_pass http://starchair-invitation-service:5004;
         add_header 'Access-Control-Allow-Origin' '*';
         add_header 'Access-Control-Allow-Headers' '*';
         add_header 'Access-Control-Allow-Methods' '*';
     }
     location /api/discuss {
         proxy_pass http://starchair-discussion-service:5005;
         add_header 'Access-Control-Allow-Origin' '*';
         add_header 'Access-Control-Allow-Headers' '*';
         add_header 'Access-Control-Allow-Methods' '*';
     }
     location /api/reviewProcess {
         proxy_pass http://starchair-review-process-service:5006;
         add_header 'Access-Control-Allow-Origin' '*';
         add_header 'Access-Control-Allow-Headers' '*';
         add_header 'Access-Control-Allow-Methods' '*';
     }
    location /api/notification {
        proxy_pass http://starchair-notification-service:5007;
        add_header 'Access-Control-Allow-Origin' '*';
        add_header 'Access-Control-Allow-Headers' '*';
        add_header 'Access-Control-Allow-Methods' '*';
    }

    location /ws{
        proxy_pass http://websocket;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        #升级http1.1到 websocket协议
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection  $connection_upgrade;
    }

}

```



