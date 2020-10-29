# -*- coding: utf-8 -*-
"""
Created on 2020/10/29 10:11 上午

@Author: fduxuan

Desc: 数据库连接

"""


class Config(object):
    DEBUG = True
    TESTING = False
    MONGO_URL = 'mongodb://localhost:27017'
    DEFAULT_DATABASE = 'testing'
    HOST = '0.0.0.0'
    PORT = 5000
