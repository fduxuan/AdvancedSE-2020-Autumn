# -*- coding: utf-8 -*-

import redis


class Config(object):
    DEBUG = True
    TESTING = False
    MONGO_URL = 'mongodb://mongo-notification-service:27017'  # for k8s
    # MONGO_URL = 'mongodb://starchair_mongo:27017'
    # MONGO_URL = 'mongodb://localhost:27017'
    DEFAULT_DATABASE = 'star_chair'
    HOST = '0.0.0.0'
    PORT = 5007
    RABBIT_HOST = 'rabbit-service'  # for k8s
    # RABBIT_HOST = 'rabbitmq'
    # RABBIT_HOST = '106.14.244.24'
    RABBIT_PORT = 5672
    RABBIT_USERNAME = 'guest'
    RABBIT_PWD = 'guest'  # 默认密码
    # RABBIT_PWD = 'starchair'  # 服务器上现有rabbitmq 容器的密码
    CONSUL_HOST = 'consul-service'
    CONSUL_PORT = 8500
    SERVICE_HOST = 'starchair-notification-service'  # 用于health check 填k8s service name
    SERVICE_PORT = 5007
    SERVICE_NAME = 'NotificationService'
    SERVICE_ID = 'NotificationService'


    SECRET_KEY = 'fjhfehlhkw342&jve'
    SESSION_TYPE = 'redis'
    # SESSION_REDIS = redis.from_url('redis://redis')
    SESSION_REDIS = redis.from_url('redis://redis-service:6379')
