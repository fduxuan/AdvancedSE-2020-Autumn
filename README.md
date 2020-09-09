## 系统文件

- .workbench
  - 不可删除，云开发平台应用部署配置文件
  - fcRouteDefault，「路由/函数入口」配置入口
  - cicd，构建打包部署应用的 Shell 指令集；核心：将要部署的内容全部打包到项目根目录的 code.zip 压缩包，云开发平台只认项目根目录的 code.zip 压缩包进行部署

- requirements.txt
  - 不可删除，依赖文件
  - 需要打开 CloudIDE 的「终端」输入以下命令进行安装
  ```
    sudo pip install -r requirements.txt --target ./ -i https://pypi.tuna.tsinghua.edu.cn/simple
  ```

- serverless_config.py
  - 不可删除
  - SAFE，不可访问扩展名的目录或文件配置入口。加入此列表的目录，代表该目录下所有的文件都不可通过扩展名进行访问；加入此列表的文件，也不能通过扩展名进行访问；

- serverless.py
  - 不可删除

## 安装依赖
- 打开依赖文件 requirements.txt，如果原 Python 项目有该文件，将原依赖项复制到该文件
```
gunicorn
requests_unixsocket
原应用依赖项添加到这里
```

- 打开 CloudIDE 终端，执行以下命令，安装依赖
```
sudo pip install -r requirements.txt --target ./ -i https://pypi.tuna.tsinghua.edu.cn/simple
```

## 创建静态应用
- 将本地开发好的静态应用直接拖拽到 CloudIDE 项目文件列表即可
- 或者直接在 CloudIDE 项目文件列表创建

## 创建函数计算风格API
- 建议在 CloudIDE 项目文件列表创建目录用于统一存放相关的 API
- API 格式
```
import json

def handler(event, context):
    request = json.loads(event) # 请求内容都会存储在 event 中，JSON编码后可遍历查看具体的内容
    # do sth
    msg = '你好，世界'
    # 返回值格式
    responseObject = {
        "isBase64Encoded": "false", # 与 body 内容是否进行 base64 编码保持一致
        "statusCode": "200", # 状态码，根据返回值自行决定适合的状态码
        "headers": {
        	"Content-type": "text/html; charset=utf-8" # 根据返回值设置正确的 Content-type
        },
        "body": msg # 返回值
    }
    return responseObject # 返回结果
```
- API 必须加入 serverless_config.py 的 SAFE 列表配置当中，避免泄漏

## 调试
- 在 CloudIDE 文件编辑器右上角，点击「执行」按钮，即可调试当前正在编辑的 Python 文件

## 部署
- 系统默认路由 /* 不可更改
- 打开云开发部署测试插件，选择环境，直接部署

## 存量 Python 应用迁移(Flask/Django)

## 上传应用
- 将存量 Python 应用拖拽上传到 CloudIDE 左侧文件列表，等待所有文件上传完成

## 安装依赖
- 打开依赖文件 requirements.txt，如果原 Python 项目有该文件，将原依赖项复制到该文件
```
gunicorn
requests_unixsocket
原应用依赖项添加到这里
```

- 打开 CloudIDE 终端，执行以下命令，安装依赖
```
sudo pip install -r requirements.txt --target ./ -i https://pypi.tuna.tsinghua.edu.cn/simple
```

## 配置应用入口信息
- 打开 serverless_config.py，配置以下信息
```
FRAMEWORK = {
  'module': '你的入口文件名称，比如：index，不要加 .py 后缀',
  'module.entry': '入口文件中应用对象名称，比如 app，以Flask为例 app = Flask(__name__)'
}
```

## 开发测试
- 接下来可以正常开发你的 Python 应用了，文件更新保存后，点击 CloudIDE 左侧 「WB」 插件，打开「测试」面板

- 在「测试」面板的「用户路径」输入要测试的 URL，点击「测试」即可看到输出日志

- 勾选「预览模式」点击「测试」，可以看到 UI 效果

## 部署
- 测试确定应用如预期工作，没有问题后，点击「WB」插件，打开「部署」面板，选择「日常环境」，点击「部署」，等待部署完成即可
