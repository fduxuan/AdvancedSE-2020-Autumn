# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:06 下午

@Author: fduxuan

Desc:

"""
from flask import Blueprint, request, current_app, session
from domain import User
from middleware import json_success
from middleware.helper import user_has_login
from middleware.errors import DUPLICATED_USER_NAME, DUPLICATED_USER_EMAIL, LOGIN_ERROR, INCOMPLETE_PROFILE

user_blueprint: Blueprint = Blueprint('user', __name__)


@user_blueprint.route('/login', methods=['POST'])
@json_success
def login():
    """
    data = {username: xxx, password: xxx}
    """
    user = User(current_app.db)
    data = request.get_json()
    username = data.get(user.Username, "")
    password = data.get(user.Password, "")
    # 查找是否存在这个用户
    current_user = user.find_one(query={user.Username: username, user.Password: password},
                                 project={user.Password: 0})
    if not current_user:
        raise LOGIN_ERROR
    else:
        session['username'] = username
        session['userid'] = current_user["_id"]
        print(session)
        return current_user


@user_blueprint.route('/logout', methods=['POST'])
@json_success
@user_has_login
def logout():
    if session.get("username", None):
        session.pop('username')
    if session.get("userid", None):
        session.pop('userid')
    return None


@user_blueprint.route("/register", methods=['POST'])
@json_success
def register():
    user = User(current_app.db)
    data = request.get_json()
    data[user.Admin] = False
    if data.get(user.Username, "") == "" or data.get(user.Fullname, "") == "" or data.get(user.Password, "") == "" or data.get(user.Email, "") == "" or data.get(user.Company, "") == "" or data.get(user.Area, "") == "" :
        raise INCOMPLETE_PROFILE
    # 判断username是否已存在
    if user.find_from_args(user.Username, data):
        raise DUPLICATED_USER_NAME
    # 判断email是否已存在
    if user.find_from_args(user.Email, data):
        raise DUPLICATED_USER_EMAIL
    return user.create(data)


@user_blueprint.route('/info', methods=['GET'])
@json_success
@user_has_login
def info():
    # 获得个人info，用于前端session全局判断
    userid = session.get('userid', None)
    user = User(current_app.db)
    return user.find_one({'_id': userid})


@user_blueprint.route('/getUserById/<uid>', methods=['GET'])
@json_success
@user_has_login
def get_user_by_id(uid):
    user = User(current_app.db)
    return user.find_one({"_id": uid})


@user_blueprint.route('/findUserByIds', methods=['POST'])
@json_success
def find_user_by_ids():
    # 传入参数为 {ids: [uid1, uid2, uid3...]}
    # 服务间通信调用
    ids = request.get_json().get('ids', [])
    user = User(current_app.db)
    users = user.find({'_id': {"$in": ids}}, project={'admin': 0, 'password': 0, 'confirm_password': 0})
    infos = {x["_id"]: x for x in users}
    return infos


@user_blueprint.route('/findUserByName', methods=['POST'])
@json_success
@user_has_login
def get_user_by_name():
    # data : {fullname: xxx}
    # 模糊查询
    data = request.get_json()
    user = User(current_app.db)
    name = data.get(user.Fullname, "")
    if name == "":
        query = {user.Admin: False, "_id": {"$ne": session.get('userid')}}
    else:
        query = {user.Fullname: {"$regex": name}, user.Admin: False, "_id": {"$ne": session.get('userid')}}
    return user.find(query, project={"password": 0, "admin": 0, 'confirm_password': 0})


@user_blueprint.route('/isAdmin', methods=['GET'])
@json_success
@user_has_login
def is_admin():
    user = User(current_app.db)
    my_name = session['username']
    my_account = user.find_one(
        {user.Username: my_name}
    )
    if my_account.get(user.Admin, False) is True:
        return True
    return False





