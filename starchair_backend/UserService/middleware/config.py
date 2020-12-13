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
    MONGO_URL = 'mongodb://mongo-user-service:27017'
    # MONGO_URL = 'mongodb://localhost:27017'
    DEFAULT_DATABASE = 'star_chair'
    HOST = '0.0.0.0'
    PORT = 5001

    CONSUL_HOST = 'consul-service'
    CONSUL_PORT = 8500
    SERVICE_HOST = 'starchair-user-service'  # 用于health check 填k8s service name
    SERVICE_PORT = 5001
    SERVICE_NAME = 'UserService'
    SERVICE_ID = 'UserService'

    SECRET_KEY = 'fjhfehlhkw342&jve'
    SESSION_TYPE = 'redis'
    # SESSION_REDIS = redis.from_url('redis://redis')
    SESSION_REDIS = redis.from_url('redis://redis-service:6379')
