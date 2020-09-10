FRAMEWORK = {
  'module': 'settings'
}

SAFE = [
  '/api',
  '/.workbench',
  '/serverless.py'
]

import multiprocessing
import gunicorn.app.base
import requests_unixsocket
import os
import logging
from django.core.wsgi import get_wsgi_application
# DJANGO_SETTINGS_MODULE设置为对应的模块名
os.environ.setdefault('DJANGO_SETTINGS_MODULE', FRAMEWORK['module'])

def number_of_workers():
  return (multiprocessing.cpu_count() * 2) + 1
 
class StandaloneApplication(gunicorn.app.base.BaseApplication):
  def __init__(self, app, options=None):
    self.options = options or {}
    self.application = app
    super().__init__()

  def load_config(self):
    config = {key: value for key, value in self.options.items()
      if key in self.cfg.settings and value is not None}
    for key, value in config.items():
      self.cfg.set(key.lower(), value)

  def load(self):
    return self.application

def worker():
  options = {
    'bind': 'unix:/tmp/wb.sock',
    'workers': number_of_workers(),
  }
  application = get_wsgi_application()  
  StandaloneApplication(application, options).run()    

def run():
  try:
    console = logging.getLogger()
    p = multiprocessing.Process(target = worker, args = ())
    p.start()
    p.join(1)
    return True
  except Exception as e: 
    console.error(repr(e))
    return False