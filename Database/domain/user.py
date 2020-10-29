# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:19 下午

@Author: fduxuan

Desc:

"""
from .helper import Model

class User(Model):
    coll_name = "user"

    Nickname = 'nickname'
    Fullname = 'fullname'
    Password = 'password'
    Email = 'email'
    Company = 'company'
    Area = 'area'
    Admin = 'admin'
