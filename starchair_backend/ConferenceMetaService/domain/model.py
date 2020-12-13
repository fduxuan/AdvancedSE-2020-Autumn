# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:19 下午

@Author: fduxuan

Desc:

"""
from .helper import Model


class Conference(Model):
    coll_name = "conference"

    ShortenForm = 'shortenForm'
    FullName = 'fullName'
    StartTime = 'startTime'
    Location = 'location'
    StopSubmittingTime = 'stopSubmittingTime'
    PublishingTime = 'publishingTime'
    Chairman = 'chairman'
    Status = 'status'
    Topics = 'topics'
    PcMembers = 'pcMembers'
