# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:06 下午

@Author: fduxuan

Desc:

"""
from flask import Blueprint, request, current_app
from domain import Discuss
from domain.helper import get_uuid
from middleware.helper import user_has_login, json_success
from middleware.errors import INCOMPLETE_PROFILE, DUPLICATED_MONGO_KEY


discuss_blueprint: Blueprint = Blueprint('discuss', __name__)

@discuss_blueprint.route('/getOrCreateByDraft/<draftId>', methods=['GET'])
@json_success
@user_has_login
def get_or_create_discussion_by_draft(draftId):
    discuss = Discuss(current_app.db)
    discussion = discuss.find_one({'draftId': draftId})
    if discussion is None:
        discuss.create({'draftId': draftId, 'posts': []})
        discussion = discuss.find_one({'draftId': draftId})
    return discussion


@discuss_blueprint.route('/createPost', methods=['POST'])
@json_success
@user_has_login
def create_post():
    data = request.get_json()
    did = data.get('did', "")
    post = data.get('post', {})
    # 字段检查
    if did == "" or "content" not in post or "username" not in post or "created_time" not in post or "comments" not in post:
        raise INCOMPLETE_PROFILE
    
    discuss = Discuss(current_app.db)
    discussion = discuss.find_one({'_id': did}, to_raise=True)

    # 判断post id是否重复
    posts = discussion.get('posts', [])
    posts_id_list = map(lambda x: x['_id'], posts)
    post['_id'] = get_uuid()
    if post['_id'] in posts_id_list:
        raise DUPLICATED_MONGO_KEY

    # 插入
    discuss.insert_array_field_at_start({"_id": did}, discuss.Posts, [post])
    return post['_id']


@discuss_blueprint.route('/createReply', methods=['POST'])
@json_success
@user_has_login
def create_reply():
    data = request.get_json()
    did = data.get('did', "")
    post_id = data.get('post_id', "")
    comment = data.get('comment', {})
    # 字段检查
    if did == "" or post_id == "" or "content" not in comment or "username" not in comment or "created_time" not in comment:
        raise INCOMPLETE_PROFILE
    
    discuss = Discuss(current_app.db)
    discussion = discuss.find_one({'_id': did, 'posts._id': post_id}, to_raise=True)

    # 判断comment id是否重复
    posts = discussion.get('posts', [])
    post = list(filter(lambda x: x['_id'] == post_id, posts))[0]
    comments_id_list = map(lambda x: x['_id'], post.get('comments', []))
    comment['_id'] = get_uuid()
    if comment['_id'] in comments_id_list:
        raise DUPLICATED_MONGO_KEY

    # 插入
    discuss.insert_inner_array_field_at_start({'_id': did, 'posts._id': post_id}, discuss.Posts, 'comments', [comment])
    return comment['_id']
