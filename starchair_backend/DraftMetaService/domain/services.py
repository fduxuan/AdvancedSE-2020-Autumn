# -*- coding: utf-8 -*-
"""
Created on 2020/12/2 1:11 下午

@Author: fduxuan

Desc:  for other services, 服务间通信应该写在这里

"""
import requests
import json


class Service:
    address = None
    port = None

    def get(self, api, data=None):
        res = requests.get(f'{self.address}:{self.port}{api}', params=data, headers={'Content-Type': "application/json"})
        if res.json()['code'] != 0:
            raise Exception
        else:
            return res.json()['data']

    def post(self, api, data=None):
        if data:
            data = json.dumps(data)
        res = requests.post(f'{self.address}:{self.port}{api}', data=data, headers={'Content-Type': "application/json"})
        if res.json()['code'] != 0:
            raise Exception
        else:
            return res.json()['data']


class ConferenceService(Service):
    address = 'http://106.14.244.24'
    port = '5002'

    def find_conference_by_ids(self, ids):
        return self.post('/api/conference/findConferenceByIds', {'ids': ids})


class NotificationService(Service):
    address = 'http://106.14.244.24'
    port = '5007'

    def send(self, receiver_list, message):
        return self.post(f'/api/notification/send', {'uid_list': receiver_list, 'message': message})


