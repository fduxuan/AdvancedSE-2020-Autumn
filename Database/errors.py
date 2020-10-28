# -*- coding: utf-8 -*-
"""
Created on 2020/10/29 12:12 上午

@Author: fduxuan

Desc:

"""
from itertools import count


class DatebaseError(Exception):

    uni_name = "DatebaseError"  # MediateError

    def __init__(self, code, reason=None):
        self.code = code
        self.reason = reason

    def json(self, **kwargs):
        result = {
            "code": self.code,
            "error": self.reason
        }
        result.update(kwargs)
        return result

    def __call__(self, *, code=None, reason=None):
        return type(self)(code or self.code, reason or self.reason)

    def __str__(self):
        return f'Code: {self.code}, reason: {self.reason}'


err_count = count()

SUCCESS = DatebaseError(next(err_count))
UNKNOWN_ERROR = DatebaseError(next(err_count))
NONEXISTENT_MONGO_ID = DatebaseError(next(err_count), "不存在该id")
NO_RECORD = DatebaseError(next(err_count), "没有该记录")
DUPLICATED_MONGO_KEY = DatebaseError(next(err_count), "重复的数据id")


def find_error(code) -> DatebaseError:
    for name, obj in globals().items():
        if isinstance(obj, DatebaseError):
            if obj.code == code:
                return obj
    raise DatebaseError(code=code)


def update_name():
    for name, obj in globals().copy().items():
        if isinstance(obj, DatebaseError):
            obj.name = name


update_name()