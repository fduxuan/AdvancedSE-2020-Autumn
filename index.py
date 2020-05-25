import logging
import json
import base64

def handler(event, context):
  request = json.loads(event)
  logger = logging.getLogger()
  content = "<h1>Hello world</h1><a href='list/200156'>打开 Gallery 列表</a>"
  api_rep = {
    "isBase64Encoded":"true",
    "statusCode":"200",
    "headers":{"Content-Type":"text/html; charset=utf-8"},
    "body":str(base64.b64encode(content.encode("utf-8")),"utf-8")
  }
  return api_rep