# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:19 下午

@Author: fduxuan

Desc:

"""
from .helper import Model


class Draft(Model):
    coll_name = "draft"

    Title = 'title'
    Summary = 'summary'
    FilePath = 'filePath'
    Contributor = 'contributor'
    ConfId = 'confId'
    Status = 'status'
    Topics = 'topics'
    Authors = 'authors'
    FileId = 'file_id'