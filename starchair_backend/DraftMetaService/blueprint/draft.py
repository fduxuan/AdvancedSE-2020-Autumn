# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:06 下午

@Author: fduxuan

Desc:

"""
from flask import Blueprint, request, current_app, session, Response
from domain import Draft
from middleware.helper import user_has_login, json_success
from domain.helper import get_uuid
from gridfs import *
from middleware.errors import UNKNOWN_ERROR
from io import BytesIO
from domain.services import NotificationService, ConferenceService


draft_blueprint: Blueprint = Blueprint('draft', __name__)


@draft_blueprint.route('/submitDraft', methods=['POST'])
@json_success
@user_has_login
def submit_draft():
    # 调用conference service判断会议是否存在
    # data = {draft}
    user_id = session.get('userid')
    username = session.get('username')
    draft = Draft(current_app.db)
    data = request.get_json()
    data[draft.Contributor] = user_id
    data[draft.Status] = 'init'

    confId = data[draft.ConfId]
    conference_info = (ConferenceService().find_conference_by_ids([confId]))[confId]
    did = draft.create(data)

    NotificationService().send(receiver_list=[conference_info['chairman']],
                               message=f"{username} has submitted a draft to your conference {conference_info['shortenForm']}")
    return did


@draft_blueprint.route('/modifyDraft', methods=['POST'])
@json_success
@user_has_login
def modify_draft():
    draft = Draft(current_app.db)
    data = request.get_json()
    draft.update_one({"_id": data.get('_id')}, data)
    return None


@draft_blueprint.route('/getDraftByAuthor', methods=['GET'])
@json_success
@user_has_login
def get_draft_by_author():
    # 查看自己某个提交的论文
    # author 是个list
    user_id = "调用userservice获得id"
    draft = Draft(current_app.db)
    return draft.find({draft.Authors: {'$in': [user_id]}})


@draft_blueprint.route('/visible/<cid>', methods=['GET'])
@json_success
@user_has_login
def get_draft_by_contributor(cid):
    # 获得已提交的论文
    # author 是个list
    user_id = session.get('userid')
    draft = Draft(current_app.db)
    return draft.find({draft.Contributor: user_id, draft.ConfId: cid})


@draft_blueprint.route('/getDraftById/<did>', methods=['GET'])
@json_success
@user_has_login
def get_draft_by_id(did):
    # 根据id查看论文元数据
    draft = Draft(current_app.db)
    return draft.find_one({'_id': did}, to_raise=True)


@draft_blueprint.route('/upload', methods=['POST'])
@json_success
@user_has_login
def upload():

    # 上传论文 为form格式
    # data = {'draftId: xxx, file:{xxxx}, file_name: ""}
    # 简单粗暴，draftid 和 file id 相同
    data = request.form
    draft_file = request.files.get('file')
    file_name = data.get('file_name')
    user_id = session.get('userid')
    draft = Draft(current_app.db)
    draftId = data.get('draftId')

    current_draft = draft.find_one({"_id": draftId}, to_raise=True)
    if draft_file is None:
        raise UNKNOWN_ERROR('未上传文件！')

    with BytesIO() as bio:
        draft_file.save(bio)
        fs = GridFSBucket(current_app.db, bucket_name=draft.collection)
        old = current_app.db.draft.files.find_one({"_id": current_draft["_id"]})
        if old is not None:
            fs.delete(old['_id'])

        grid_in = fs.open_upload_stream_with_id(current_draft["_id"], file_name,
                                                metadata={'draftId': draftId, 'user_id': user_id})
        try:
            grid_in.write(bio.getvalue())
        finally:
            grid_in.close()  # uploaded on close
    draft.update_one({"_id": draftId}, {'file_id': draftId})
    return file_name


@draft_blueprint.route('/download/<fid>', methods=['GET'])
@user_has_login
def download(fid):
    file_info = current_app.db.draft.files.find_one({'_id': fid})
    if file_info is None:
        raise UNKNOWN_ERROR('不存在该文件！')

    def send_file(db):
        """
        发送附件

        :param response:
        :return:
        """
        fs_bucket = GridFSBucket(db, bucket_name=Draft().coll_name)
        grid_out = fs_bucket.open_download_stream(file_info['_id'])
        try:
            while True:
                chunk = grid_out.readchunk()
                if chunk is None or len(chunk) == 0:
                    break
                yield chunk
        finally:
            grid_out.close()

    response = Response(send_file(current_app.db), content_type='application/octet-stream')
    response.headers["Content-disposition"] = 'attachment; filename=%s' % file_info['filename']
    return response


@draft_blueprint.route('/findByIds', methods=['POST'])
@json_success
def find_by_ids():
    # 传入参数为 {ids: [did1, did2, did3...]}
    # 服务间通信调用
    ids = request.get_json().get('ids', [])
    draft = Draft(current_app.db)
    all_drafts = draft.find({'_id': {"$in": ids}})
    infos = {x["_id"]: x for x in all_drafts}
    return infos


@draft_blueprint.route('/getDraftByConference/<cid>', methods=['GET'])
@json_success
def get_draft_by_conference(cid):
    # 一个会议下的所有稿件, 服务间调用
    draft = Draft(current_app.db)
    return draft.find({draft.ConfId: cid})


@draft_blueprint.route('/getDraftByContributor/<contributor>', methods=['GET'])
@json_success
def get_draft_by_all_contributor(contributor):
    # 一个人投的所有稿件, 服务间调用
    draft = Draft(current_app.db)
    return draft.find({draft.Contributor: contributor})