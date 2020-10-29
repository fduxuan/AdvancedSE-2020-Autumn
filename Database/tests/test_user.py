# -*- coding: utf-8 -*-
"""
Created on 2020/10/29 1:07 下午

@Author: fduxuan

Desc:

"""
from .helper import TestCase
from domain.user import User
import unittest
from flask import request


class TestUser(TestCase):
    def setUp(self) -> None:
        super().setUp()
        self.user = User(self.db)
        self.user.coll.drop()

    def test_read(self):
        # 不存在
        uid = 'fake_uid'
        res = self.get(f'/api/db/user/show/{uid}')
        self.assertNotEqual(res['code'], 0)
        # 插入后可查到
        self.user.create({'_id': 'fake_id', 'nickname': 'fake_name'})
        res = self.get(f'/api/db/user/show/fake_id')
        self.assertEqual(res['code'], 0)

    def test_create(self):
        data = {
            'nickname': 'fake_name'
        }
        res = self.post(f'/api/db/user/create', data=data)
        self.assertEqual(res['code'], 0)
        uid = res['data']   # 返回id

        res = self.get(f'/api/db/user/show/{uid}')
        self.assertEqual(res['code'], 0)
        self.assertEqual(res['data']['nickname'], 'fake_name')

    def test_find(self):
        # 找不到
        self.user.coll.drop()
        data = {
            'filter': {'nickname': '小红'},
            'project': {'_id': False},
            'sort': [('fullname',1)],
            'limit': None,
            'skip': None
        }
        res = self.post('/api/db/user/find', data=data)
        self.assertEqual(len(res['data']), 0)

        # 插入
        self.user.create({'nickname': '小红'})
        self.user.create({'nickname': '小明'})

        res = self.post('/api/db/user/find', data=data)
        self.assertEqual(len(res['data']), 1)
        self.assertNotIn('_id', res['data'][0])

    def test_update(self):
        self.user.coll.drop()
        self.user.create({'nickname': '小红', '_id':'fake_id'})

        data = {'nickname': '小明'}
        # normal
        res = self.post('/api/db/user/update/fake_id', data=data)
        self.assertEqual(res['code'], 0)
        res = self.get('/api/db/user/show/fake_id')
        self.assertEqual(res['data']['nickname'], '小明')
        # 自动去除id
        data = {'_id': 'ddd', "nickname": '小黄'}
        res = self.post('/api/db/user/update/fake_id', data=data)
        self.assertEqual(res['code'], 0)
        res = self.get('/api/db/user/show/fake_id')
        self.assertEqual(res['data']['nickname'], '小黄')
        self.assertEqual(res['data']['_id'], 'fake_id')

    def test_delete(self):
        self.user.coll.drop()
        data = {'nickname': '小明', "_id": 'fake_id'}
        self.user.create(data)
        # normal
        res = self.post('/api/db/user/delete/fake_id')
        self.assertEqual(res['code'], 0)

        res = self.get('/api/db/user/show/fake_id')
        self.assertNotEqual(res['code'], 0)





if __name__ == "__main__":
    unittest.main()