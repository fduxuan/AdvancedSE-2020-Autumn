# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:19 下午

@Author: fduxuan

Desc:

"""
from .helper import Model


class ReviewProcess(Model):
    coll_name = "reviewProcess"

    ConfId = 'confId'
    DraftId = 'draftId'
    PcMemberId = 'pcMemberId'
    Rebuttal = 'rebuttal'
    Status = 'status'
    Contributor = 'contributor'
    Score = 'score'
