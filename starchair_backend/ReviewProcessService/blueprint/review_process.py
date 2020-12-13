# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:06 下午

@Author: fduxuan

Desc:

"""
from flask import Blueprint, request, current_app, session
from domain import ReviewProcess
from middleware.helper import user_has_login, json_success, map_key_with_detail
from domain.services import DraftService, ConferenceService, UserService
from middleware.errors import UNKNOWN_ERROR


review_process_blueprint: Blueprint = Blueprint('review_process', __name__)


@review_process_blueprint.route('/getReviewProcessByPcMember', methods=['POST'])
@json_success
@user_has_login
def get_review_process_by_pc_member():
    # data = {status: xxx, cid:xxx}
    # Pcmember获取自己需要审阅的稿件
    # review_process.PcMemberId 是list
    status = request.get_json().get('status', None)
    confId = request.get_json().get('confId', None)
    user_id = session.get('userid')
    review_process = ReviewProcess(current_app.db)
    query = {review_process.ConfId: confId, review_process.PcMemberId: user_id, review_process.Status: status} if status else {review_process.ConfId: confId, review_process.PcMemberId: user_id}
    all_process = review_process.find(query)

    draft_ids = list(set([x[review_process.DraftId] for x in all_process]))
    draft_info = DraftService().find_draft_by_ids(draft_ids)
    all_process = map_key_with_detail(key=review_process.DraftId, origin_data=all_process, detail=draft_info,
                                      target_key='draft_info')

    contributor_ids = list(set([x[review_process.Contributor] for x in all_process]))
    contributor_info = UserService().find_user_by_ids(contributor_ids)
    all_process = map_key_with_detail(key=review_process.Contributor, origin_data=all_process, detail=contributor_info,
                                      target_key='contributor_info')
    return all_process


@review_process_blueprint.route('/getReviewProcessByDraftId/<did>', methods=['GET'])
@json_success
@user_has_login
def get_review_process_by_id(did):
    review_process = ReviewProcess(current_app.db)
    return review_process.find_one({review_process.DraftId: did})


@review_process_blueprint.route('/getReviewProcessByConfId/<cid>', methods=['POST'])
@json_success
def get_review_process_by_conf_id(cid):
    # 服务间通信调用
    # data => query
    data = request.get_json()
    data = {} if data is None else dict(data)
    review_process = ReviewProcess(current_app.db)
    data[review_process.ConfId] = cid
    return review_process.find(data)


@review_process_blueprint.route('/score', methods=['POST'])
@json_success
@user_has_login
def score():

    # data = {score: xx, comment: xx, 'confidence':xxx, 'draftId' xx}
    userid = session.get('userid')
    data = request.get_json()
    # 给稿件评分，字段未定
    review_process = ReviewProcess(current_app.db)
    draftId = data.get('draftId')
    data.pop('draftId')
    process = review_process.find_one({review_process.DraftId: draftId, review_process.PcMemberId: userid},
                                      to_raise=True)
    # if data['status'] == 'firstResult':
    #     raise UNKNOWN_ERROR(reason='请勿重复打分！')
    if process[review_process.Status] == 'init':
        data['status'] = 'firstResult'
    if process[review_process.Status] == 'rebuttal':
        data['status'] = 'finalResult'
    review_process.update_one({review_process.DraftId: draftId, review_process.PcMemberId: userid}, data)
    return None


@review_process_blueprint.route('/rebuttal', methods=['POST'])
@json_success
@user_has_login
def rebuttal():
    data = request.get_json()
    rid = data.get('reviewId')
    rebuttal = data.get('rebuttal')
    review_process = ReviewProcess(current_app.db)
    process = review_process.find_one({"_id": rid})
    review_process.update_many({review_process.DraftId: process[review_process.DraftId]}, {review_process.Rebuttal: rebuttal, review_process.Status: 'rebuttal'})


@review_process_blueprint.route('/allocDraft', methods=['POST'])
@json_success
@user_has_login
def alloc_draft():
    # data = {"confId": xxx}
    data = request.get_json()
    confId = data.get('confId')
    conference = (ConferenceService().find_conference_by_ids([confId]))
    if not len(conference):
        raise UNKNOWN_ERROR(reason='无此会议！')
    conference = list(conference.values())[0]
    drafts = DraftService().get_draft_by_conference(conference["_id"])
    if not len(drafts):
        raise UNKNOWN_ERROR(reason='无稿件提交！')
    if conference['status'] != 'submitting':
        raise UNKNOWN_ERROR(reason='当前流程不可开启审稿')
    pcs = conference['pcMembers']
    num_pc = len(pcs)
    if num_pc < 3:
        raise UNKNOWN_ERROR(reason='the number of pc members should be at least 3!')
    review_process = ReviewProcess(current_app.db)
    pcs = pcs*len(drafts)
    for i in range(0, len(drafts)):
        for j in range(i*3, i*3+3):
            process = {
                review_process.ConfId: confId,
                review_process.DraftId: drafts[i]["_id"],
                review_process.PcMemberId: pcs[int(j)],
                review_process.Rebuttal: None,
                review_process.Status: 'init',
                review_process.Contributor: drafts[i]['contributor']
            }
            review_process.create(process)
    return None
