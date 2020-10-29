# -*- coding: utf-8 -*-
"""
Created on 2020/10/29 11:25 上午

@Author: fduxuan

Desc:

"""
import json
from functools import wraps


# 修饰返回值格式
def json_success(func):
    @wraps(func)
    def success(**kwargs):
        return json.dumps({'code':0 , 'data': func(**kwargs)})
    return success


def parse_find_from_request(request):
    """
    parse query project and sort from request
    :param request:
    :return:
    """
    json = request.json or dict()
    query = json.get('filter', dict())
    project = json.get('project', None)
    sort = json.get('sort', None)
    limit = json.get('limit', 500)
    skip = json.get('skip', 0)
    if sort is None:
        sort = [("created_at", 1)]
    if isinstance(sort, dict):
        sort = [(key, value) for key, value in sort.items()]
    return query, project, sort, limit, skip