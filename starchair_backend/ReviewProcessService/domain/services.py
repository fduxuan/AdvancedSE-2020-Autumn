# -*- coding: utf-8 -*-
"""
Created on 2020/12/2 6:22 下午

@Author: fduxuan

Desc:

"""
import json
import requests


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


class DraftService(Service):
    address = 'http://106.14.244.24'
    port = '5003'

    def get_draft_by_conference(self, cid):
        return self.get(f'/api/draft/getDraftByConference/{cid}')

    def find_draft_by_ids(self, ids):
        return self.post(f'/api/draft/findByIds', {"ids": ids})


class UserService(Service):
    address = 'http://106.14.244.24'
    port = '5001'

    def find_user_by_ids(self, ids):
        return self.post('/api/user/findUserByIds', {'ids': ids})


class ConferenceService(Service):
    address = 'http://106.14.244.24'
    port = '5002'

    def find_conference_by_ids(self, ids):
        return self.post('/api/conference/findConferenceByIds', {'ids': ids})



if __name__ == "__main__":
    pass
