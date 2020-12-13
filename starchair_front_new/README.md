# startchair_front_new

# 0.必备注意事项
**----------------------12.11更新--------------------------------**
* 新增分支 k8s 作为部署分支
  
  ```
   git checkout k8s
  ```

**----------------------11.26更新--------------------------------**

* 因为是多人开发，但是如果新建分支给master send merge request 校验有点烦

  也为了时刻保持ci版本为最新提交版本

  所以开放了master提交
  
* #### 为避免开发冲突，请**务必**使用以下git命令 
  
  
    ```
    git stash 
    
    git pull
    
    git stash pop
  
    git commit -a -m "your commit"
  
    git push origin master
  
    ```
  
**----------------------11.29更新--------------------------------**  

* 当前已做全局login检查处理，拉取新代码后请注意merge，
  以及**自行注册账号并登录**
  否则无法访问除homepage以外的所有页面！！！ 
  
* session已接入，首次登录后再次打开无需再次登录！！




# 1.使用

### Project setup 使用淘宝源
```
npm install --registry=https://registry.npm.taobao.org
```

### Compiles and hot-reloads for development 默认8080 向上增
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```


----
# 2. 简单说明
* 架构由vue2->vue3

* proxy已做后端转发，本地可直接请求后端

* 请求方式参考src/model/xx.js 方式，已做封装调用



* 自动部署上线于 http://106.14.244.24:3003/


**-------------------11-26 更新------------------------**  

* 组件库为 **ants-design**
  https://antdv.com/docs/vue/introduce-cn/
  
* 配色方案：基色如下
  https://colorhunt.co/palette/129857

# 3. 需求
* 后端api与数据库重构，在理解项目的情况下需要对前端进行api的重构

* 原项目前端过于繁琐冗杂，建议做搬迁重构而并非直接在原项目上进行更改，此为搬迁后的仓库
  
* 原项目地址：http://106.14.244.24:3000/fduxuan/starchair_front

* 原项目demo起在http://106.14.244.24:80 ,可直接访问并操作



# 4. 其他
* 非常感谢大家理解我的重构决定，这给各位增加了很多负担，非常感谢！
  （我会尽量把能做的都做了，尽可能少的工作分配给大家）

* 我相信这门课的目标是让我们亲身体会微服务的流程，所以不会对前端有太多要求，我会尽量把所有权限控制放在后端，避免原代码用v-if控制所有的情况再次出现

* 感谢大家的支持！
