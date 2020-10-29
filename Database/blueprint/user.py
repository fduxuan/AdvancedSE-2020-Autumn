# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:06 下午

@Author: fduxuan

Desc:

"""
from flask import Blueprint, request, current_app
from domain import User
from middleware import json_success
from middleware.helper import parse_find_from_request

user_blueprint: Blueprint = Blueprint('user', __name__)


@user_blueprint.route("/property", methods=['GET'])
@json_success
def show_property():
    user = User(current_app.db)
    return f"database:{user.database} collection:{user.collection}"


@user_blueprint.route('/create', methods=['POST'])
@json_success
def create():
    user = User(current_app.db)
    data = request.get_json()
    return user.create(data)


@user_blueprint.route('/show/<uid>', methods=['GET'])
@json_success
def show(uid):
    user = User(current_app.db)
    return user.find_one({"_id": uid})


@user_blueprint.route('/find', methods=['POST'])
@json_success
def find():
    user = User(current_app.db)
    query, project, sort, limit, skip = parse_find_from_request(request)
    return user.find(query=query, project=project, sort=sort, limit=limit, skip=skip)


@user_blueprint.route("/delete/<uid>", methods=['POST'])
@json_success
def delete_one(uid):
    user = User(current_app.db)
    return user.delete_one({"_id": uid})


@user_blueprint.route("/update/<uid>", methods=['POST'])
@json_success
def update(uid):
    user = User(current_app.db)
    data = request.get_json()
    return user.update_one({'_id': uid}, data)


