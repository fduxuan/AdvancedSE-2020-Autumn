import os
import base64
import json
import re
import logging
import importlib
import mimetypes
from datetime import datetime
import time
import traceback
import requests_unixsocket
import serverless_config

inited = False
socketPath = '/tmp/wb.sock'

def initializeMethod(console):
  global inited
  if serverless_config.FRAMEWORK['module']:
    result = serverless_config.run()  
    if result:
      inited = True
  else:
    console.error('[workbench-serverless]: 存量应用初始化失败，没有可执行的入口文件')  


# 每n秒执行一次
def detectServerStart():
  count = 0
  maxi = 100
  while True:
    if not os.path.exists(socketPath) and count <= maxi:
      time.sleep(0.1)
      count += 1
    else:
      return

def req(event, context):
  try:
    console = logging.getLogger()
    request = requests_unixsocket.Session()
    body = None
    headers = None
    qs = None
    method = 'GET'
    if 'queryParameters' in event:
      qs = event['queryParameters']

    if 'body' in event:
      body = event['body']  

    if 'headers' in event:
      headers = event['headers']
      if 'content-length' in event:
        del headers['content-length']
      
      if'accept-encoding' in event:
        del headers['accept-encoding']

    if 'path' in event:
      reqPath = event['path']
    else:
      reqPath = '/'
  
    if 'method' in event:
      method = event['method']
      if method == 'ANY':
        method = 'GET'
      elif not method:
        method = 'GET'
    else:
      method = 'GET'    
    
    response = request.request(method.lower(), 'http+unix://%2Ftmp%2Fwb.sock'+ reqPath, data = body, params = qs,headers = headers)
    strRes = response.content
    status = "200"
    del response.headers['content-length']

    newHead = {}
    for item in response.headers.items():
        newHead[item[0]] = item[1]
    unixResponse = {
      "isBase64Encoded": "true",
      "statusCode": status,
      "headers": newHead,
      "body": str(base64.b64encode(strRes),"utf-8")
    }
  except Exception as e:
    console.error(traceback.format_exc())
    strRes = 'Server Internal Error'
    status = "500"
    unixResponse = {
      "isBase64Encoded": "true",
      "statusCode": status,
      "headers": {},
      "body": strRes
    }
  
  return unixResponse

def handler(event, context):
  global inited
  console = logging.getLogger()
  try:
    if not inited:
      if serverless_config.FRAMEWORK['module']:
        initializeMethod(console)
      else:
        inited = True

      if not inited:
        raise Exception("存量应用初始化失败")  
  except Exception as e:
    console.error(repr(e))  
    raise Exception("存量应用初始化失败")  

  try:      
    htmlResponse = {
      "isBase64Encoded": "false",
      "statusCode": "404",
      "headers": {
        "Content-type": "text/html;charset=utf-8"
      },
      "body": "<h1>很抱歉，您要访问的页面不存在！</h1>"
    }
    if serverless_config.FRAMEWORK['module']:
      detectServerStart()
      return req(json.loads(event), context)
    else:
      request = json.loads(event)
      path = request['path']
      pathArray = re.findall('\/[^\/]*', path)
      pathLength = len(pathArray)
      lastPath = pathArray[pathLength-1]
      queryPath = re.split(r'\?', lastPath)
      lenQueryPath = len(queryPath)
      lastPath = queryPath[0]
      extArray = re.split(r'\.', lastPath) if(lastPath.find('.')>-1) else ''
      extLength = len(extArray)
      fileExt = extArray[extLength - 1] if(extArray) else ''
      codePath = './'
      codePath = re.sub(r'\/$', '', codePath)
      modulePath = codePath
      fnCall = ''

      if pathLength:
        modulePath = codePath + ''.join(pathArray)
        console.info('Request Path: %s', modulePath)
        if path=='/':
          modulePath = codePath + '/index.html'
          fileExt = 'html'
          if (not os.path.exists(modulePath)):
            modulePath = codePath + '/index.htm'
            if (not os.path.exists(modulePath)):
              modulePath = codePath + '/default.html'
              if (not os.path.exists(modulePath)):
                modulePath = codePath + '/default.htm'
            
      console.info('ModulePath: %s', modulePath)
      print('ModulePath: ' + modulePath)
      print('fileExt: ' + fileExt)

      if fileExt:
        SAFE = serverless_config.SAFE
        for v in pathArray:
          if(v in SAFE):
            print('SAFE CHECK:' + v);
            return htmlResponse

        try:
          fs = open(modulePath, 'rb')
          data = fs.read()
          fs.close()
          fileResponse = {
            "isBase64Encoded": "true",
            "statusCode": "200",
            "headers": {
              "Content-type": mimetypes.types_map['.' + fileExt]+"; charset=utf-8"
            },
            "body": str(base64.b64encode(data),"utf-8")
          }
          return fileResponse
        except:
          console.info('The requested file does not exist: %s', modulePath)
          print('The requested file does not exist: ' + modulePath)
          return htmlResponse
      else:
        try:
          modulePath = modulePath.replace('/', '.')
          modulePath = re.sub(r'^\.*', '', modulePath)
          console.info('The imported module: %s', modulePath)
          print('The imported module: ' + modulePath)
          module = importlib.import_module(modulePath)
          return module.handler(event, context)
        except Exception as e:
          console.error(e)
          return htmlResponse
  except Exception as e:
    console.error(e)
    return htmlResponse