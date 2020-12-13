# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:06 下午

@Author: fduxuan

Desc:

"""
from flask import Blueprint, request, current_app, session
from domain import Conference
from middleware.helper import user_has_login, json_success
from domain.services import UserService, NotificationService, DraftMetaService, ReviewProcessService
from middleware.errors import *

conference_blueprint: Blueprint = Blueprint('conference', __name__)


@conference_blueprint.route('/createConference', methods=['POST'])
@json_success
@user_has_login
def create_conference():
    user_id = session.get('userid', None)
    data = request.get_json()

    conference = Conference(current_app.db)
    data[conference.Status] = 'init'
    data[conference.Chairman] = user_id
    data[conference.PcMembers] = [user_id]
    return conference.create(data)


@conference_blueprint.route('/getConferenceById/<cid>', methods=['GET'])
@json_success
@user_has_login
def get_conference_by_id(cid):
    conference = Conference(current_app.db)
    conf_info = conference.find_one({'_id': cid}, to_raise=True)
    pcs = list(set(conf_info[conference.PcMembers]))
    pc_info = UserService().find_user_by_ids(pcs)
    conf_info[conference.PcMembers] = list(pc_info.values())

    chairman = conf_info[conference.Chairman]
    chairman_info = list(UserService().find_user_by_ids([chairman]).values())[0]
    conf_info[conference.Chairman] = chairman_info
    return conf_info


@conference_blueprint.route('/findConferenceByIds', methods=['POST'])
@json_success
def find_conference_by_ids():
    ids = request.get_json().get('ids', [])
    conf = Conference(current_app.db)
    conferences = conf.find({'_id': {"$in": ids}})
    infos = {x["_id"]: x for x in conferences}
    return infos


@conference_blueprint.route('/attendAsChairman', methods=['POST'])
@json_success
@user_has_login
def get_conference_by_chairman():
    # 获得自己作为chairman的所有会议
    # data=>query
    data = request.get_json()
    if not data:
        data = {}
    conference = Conference(current_app.db)
    user_id = session.get('userid', None)
    query = {"$and":
                 [{conference.Chairman: user_id}, data]
             }
    return conference.find(query)


@conference_blueprint.route('/attendAsPc', methods=['POST'])
@json_success
@user_has_login
def get_conference_by_pc():
    # 获得自己作为pcmembers的所有会议
    # data=>query
    data = request.get_json()
    if not data:
        data = {}
    conference = Conference(current_app.db)
    user_id = session.get('userid', None)
    query = {"$and":
                 [{conference.PcMembers: {"$in": [user_id]}}, data]
             }
    return conference.find(query)


@conference_blueprint.route('/attendConference', methods=['POST'])
@json_success
@user_has_login
def get_attend_conference():
    # 获得自己参与的所有会议，即参与过投稿，是chairman，是pcmember三种情况
    # 当前未加上参与过投稿
    data = request.get_json()
    if not data:
        data = {}
    user_id = session.get('userid', None)
    drafts = DraftMetaService().get_draft_by_contributor(user_id)
    conference_ids = [x['confId'] for x in drafts]

    conference = Conference(current_app.db)
    query = {"$or": [{conference.Chairman: user_id},
                     {conference.PcMembers: {"$in": [user_id]}},
                     {"_id": {"$in": conference_ids}}]}
    return conference.find({"$and": [query, data]})


@conference_blueprint.route('/attendAsContributor', methods=['POST'])
@json_success
@user_has_login
def get_conference_by_contributor():
    # 获得自己投稿过的会议，是chairman，是pcmember三种情况
    # 当前未加上参与过投稿
    data = request.get_json()
    if not data:
        data = {}
    user_id = session.get('userid', None)
    drafts = DraftMetaService().get_draft_by_contributor(user_id)
    conference_ids = [x['confId'] for x in drafts]
    conference = Conference(current_app.db)
    query = {"_id": {"$in": conference_ids}}
    return conference.find({"$and": [query, data]})


@conference_blueprint.route('/getUncheckedConference', methods=['GET'])
@json_success
@user_has_login
def get_unchecked_conference():
    # reviewing 状态,（管理员）获取所有没有被审批的会议
    user_id = session.get('userid')
    user_info = UserService().find_user_by_ids([user_id])
    if not user_info[user_id]['admin']:
        raise NO_PRIVILEGE
    conference = Conference(current_app.db)
    return conference.find({conference.Status: 'init'})


@conference_blueprint.route('/visible', methods=['POST'])
@json_success
@user_has_login
def visible():
    # 所有人可见的会议，即状态不为init，reject的会议
    # data => query
    data = request.get_json()
    if not data:
        data = {}
    conference = Conference(current_app.db)
    query = {conference.Status: {"$nin": ['init', 'reject']}}
    query = {"$and": [query, data]}
    return conference.find(query)


@conference_blueprint.route('/getAcceptConference', methods=['GET'])
@json_success
@user_has_login
def get_accept_conference():
    # status = pass （用户）获取所有可以投稿的会议
    conference = Conference(current_app.db)
    return conference.find({conference.Status: 'submitting'})


@conference_blueprint.route('/approveConference/<cid>', methods=['POST'])
@json_success
@user_has_login
def approve_conference(cid):
    # status -> pass
    conference = Conference(current_app.db)
    conf = conference.find_one({"_id": cid}, to_raise=True)
    conference.update_one({"_id": cid}, {'status': 'accept'})

    NotificationService().send(receiver_list=[conf[conference.Chairman]],
                               message=f"Your Conference {conf[conference.ShortenForm]} has been accepted!")


@conference_blueprint.route('/rejectConference/<cid>', methods=['POST'])
@json_success
@user_has_login
def reject_conference(cid):
    # status -> refuse
    conference = Conference(current_app.db)
    conf = conference.find_one({"_id": cid}, to_raise=True)
    conference.update_one({"_id": cid}, {'status': 'reject'})
    NotificationService().send(receiver_list=[conf[conference.Chairman]],
                               message=f"Your Conference {conf[conference.ShortenForm]} has been rejected!")
    return None


@conference_blueprint.route('/addPcMember/<cid>', methods=['POST'])
@json_success
def add_pc_member(cid):
    # data = {'pcMembers': []}
    data = request.get_json()
    conference = Conference(current_app.db)
    conf = conference.find_one({'_id': cid}, to_raise=True)
    pc_members = conf.get(conference.PcMembers, [])
    pc_members += data.get(conference.PcMembers, [])
    pc_members = list(set(pc_members))
    conference.update_one({'_id': cid}, {conference.PcMembers: pc_members})
    return None


@conference_blueprint.route('/changeStatus/<cid>', methods=['POST'])
@json_success
@user_has_login
def change_status(cid):
    # data = {'status': 'something'}
    data = request.get_json()

    conference = Conference(current_app.db)
    conf = conference.find_one({"_id": cid}, to_raise=True)
    user_id = session.get('userid')
    if user_id != conf[conference.Chairman]:
        raise NO_PRIVILEGE

    if data['status'] == 'submitting':
        if len(conf[conference.PcMembers]) < 3:
            raise PCMEMBERS_LIMIT

    if data['status'] == 'firstPublish':
        process = ReviewProcessService().get_review_process_by_conference(cid, {'status': 'init'})
        if len(process):
            raise DRAFT_SCORED
    if data['status'] == 'finalPublish':
        process = ReviewProcessService().get_review_process_by_conference(cid, {'status': 'rebuttal'})
        if len(process):
            raise REBUTTAL_DRAFT_SCORED

    conference.update_one(conf, data)
    if data['status'] in ['reviewing', 'firstDiscussion', 'finalDiscussion']:
        NotificationService().send(receiver_list=conf[conference.PcMembers],
                                   message=f"Conference {conf[conference.ShortenForm]} has started process {data['status']}")

    if data['status'] in ['finalPublish', 'firstPublish']:
        # 给所有投稿的contributor发消息
        result = 'first result' if data['status'] else 'final result'
        drafts = DraftMetaService().get_draft_by_conference(cid)
        contributors = list(set([draft['contributor'] for draft in drafts]))
        NotificationService().send(receiver_list=contributors,
                                   message=f"Conference {conf[conference.ShortenForm]} has published {result}!")
        pass
    return None
