# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:19 下午

@Author: fduxuan

Desc:

"""
from .helper import Model


class Discuss(Model):
    coll_name = "discuss"

    DraftId = 'draftId'
    Posts = 'posts'