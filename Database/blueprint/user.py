# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:06 下午

@Author: fduxuan

Desc:

"""
from flask import Blueprint, request
import json

user_blueprint = Blueprint('user', __name__)


@user_blueprint.route("/create", methods=['POST'])
def create():
    data = request.get_json()
