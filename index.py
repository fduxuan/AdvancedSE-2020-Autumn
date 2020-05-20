import logging
import json
import base64

def handler(event, context):
  request = json.loads(event)
  logger = logging.getLogger()
  content = "<h1>hello world</h1>".encode("utf-8")
  api_rep = {
    "isBase64Encoded":"true",
    "statusCode":"200",
    "headers":{"Content-Type":"text/html; charset=utf-8"},
    "body":str(base64.b64encode(content),"utf-8")
  }
  return api_rep