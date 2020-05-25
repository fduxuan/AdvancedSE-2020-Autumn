import logging
import json
import base64

def handler(event, context):
  request = json.loads(event)
  logger = logging.getLogger()
  content = "<h1>Gallery 列表</h1>"
  content += "<div>Gallery 列表的 ID 是："+request['pathParameters']['listId']+"</div>"
  content += "<div>它从一个 Restful 的 API中解析得到 request['pathParameters']['listId']</div>"
  content += "<a href='/'>回到首页</a>"
  api_rep = {
    "isBase64Encoded":"true",
    "statusCode":"200",
    "headers":{"Content-Type":"text/html; charset=utf-8"},
    "body":str(base64.b64encode(content.encode("utf-8")),"utf-8")
  }
  return api_rep