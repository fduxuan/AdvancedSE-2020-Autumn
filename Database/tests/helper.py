# -*- coding: utf-8 -*-
"""
Created on 2020/10/29 12:43 下午

@Author: fduxuan

Desc: unittest setup and some help func

"""
from middleware.config import Config
import unittest
from server import app
import pymongo
from json import dumps, loads


TEST_DB = 'test_db'
HOST = '0.0.0.0'
PORT = '5001'
config = Config()
config.TESTING = True
config.DEFAULT_DATABASE = TEST_DB


class TestCase(unittest.TestCase):
    def setUp(self) -> None:
        self.config = config
        self.app = app
        self.app.config.from_object(self.config)
        self.app_client = self.app.test_client()
        mongo_client = pymongo.MongoClient(self.config.MONGO_URL)
        self.db = mongo_client.get_database(name=app.config.get('DEFAULT_DATABASE'))
        self.app.db = self.db

    def tearDown(self) -> None:
        pass

    def post(self, url, headers=None, data=None, status=200):
        if headers is None:
            headers = dict()

        data = dumps(data)
        if 'content-type' not in headers:
            headers['content-type'] = 'application/json'
        resp = self.app_client.post(url, data=data, headers=headers)
        self.assertEqual(status, resp.status_code)
        return loads(str(resp.data, encoding='utf-8'))

    def get(self, url, headers=None, data=None, status=200):
        if headers is None:
            headers = dict()
            if 'content-type' not in headers:
                headers['content-type'] = 'application/json'
            resp = self.app_client.get(url, headers=headers, data=data)
            if resp.status_code != status:
                raise ValueError(resp.status_code)
            return loads(str(resp.data, encoding='utf-8'))
