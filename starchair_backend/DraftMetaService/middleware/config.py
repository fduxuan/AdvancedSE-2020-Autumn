# -*- coding: utf-8 -*-
"""
Created on 2020/10/29 10:11 上午

@Author: fduxuan

Desc: 数据库连接

"""
import redis


class Config(object):
    DEBUG = True
    TESTING = False
    MONGO_URL = 'mongodb://starchair_mongo:27017'
    # MONGO_URL = 'mongodb://localhost:27017'
    DEFAULT_DATABASE = 'star_chair'
    HOST = '0.0.0.0'
    PORT = 5003
    SECRET_KEY = 'fjhfehlhkw342&jve'
    SESSION_TYPE = 'redis'
    # SESSION_REDIS = redis.from_url('redis://redis')
    SESSION_REDIS = redis.from_url('redis://redis:6379')
