# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:19 下午

@Author: fduxuan

Desc:

"""
from .helper import Model


class Invitation(Model):
    coll_name = "invitation"

    Inviter = "inviter"  # 邀请者
    Invitee = 'invitee'  # 被邀请者
    ConfId = 'confId'
    Status = 'status'
