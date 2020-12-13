# -*- coding: utf-8 -*-
"""
Created on 2020/10/29 11:25 上午

@Author: fduxuan

Desc:

"""
import json
from functools import wraps
from flask import session
import flask
from .errors import NOT_LOGIN


def json_success(func):
    """
    decorate return format
    :param func:
    :return:
    """

    @wraps(func)
    def success(**kwargs):
        return json.dumps({'code': 0, 'data': func(**kwargs)})

    return success


def parse_find_from_request(request):
    """
    parse query project and sort from request
    :param request:
    :return:
    """
    data = request.get_json()
    query = data.get('filter', dict())
    project = data.get('project', None)
    sort = data.get('sort', None)
    limit = data.get('limit', None)
    skip = data.get('skip', None)
    if limit is None:
        limit = 500
    if skip is None:
        skip = 0
    if sort is None:
        sort = [("created_at", 1)]
    if isinstance(sort, dict):
        sort = [(key, value) for key, value in sort.items()]
    return query, project, sort, limit, skip


def user_has_login(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        # Do something with session here
        username = session.get('username', None)
        if not username:
            raise NOT_LOGIN
        return f(*args, **kwargs)

    return decorated_function


def map_key_with_detail(key: str, origin_data: list, detail: dict, target_key: str):
    """
    ex: origin_data = [{invitee: 111}, {invitee: 222}]
    key: invitee
    detail: {111: {xxx..}, 222:{...}}
    target_key = 'user_info'
    """
    for i in range(len(origin_data)):
        current = origin_data[i]
        current[target_key] = detail[current[key]]
        origin_data[i] = current
    return origin_data
