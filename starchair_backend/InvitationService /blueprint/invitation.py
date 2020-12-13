# -*- coding: utf-8 -*-
"""
Created on 2020/10/28 11:06 下午

@Author: fduxuan

Desc:

"""
from flask import Blueprint, request, current_app, session
from domain import Invitation
from middleware.helper import user_has_login, json_success, map_key_with_detail
from middleware.errors import INCOMPLETE_PROFILE, NO_PRIVILEGE
from domain.services import UserService, ConferenceService, NotificationService


invitation_blueprint: Blueprint = Blueprint('invitation', __name__)


@invitation_blueprint.route('/createInvitation', methods=['POST'])
@json_success
@user_has_login
def create_invitation():
    """
    data 包含 {confId: xxx, invitee: xxx}
    """
    data = request.get_json()
    invitation = Invitation(current_app.db)
    invitee = data.get(invitation.Invitee, None)
    confId = data.get(invitation.ConfId, None)
    if confId is None or invitee is None:
        raise INCOMPLETE_PROFILE(reason='发送信息不完整')
    # 不存在会议 raise error

    # 不存在user 也应该检查

    inviter = session.get('userid')
    invitation_info = {
        invitation.Status: 'init',
        invitation.ConfId: confId,
        invitation.Invitee: invitee,
        invitation.Inviter: inviter,
    }
    iid = invitation.create(invitation_info)
    # 用户接到系统通知
    NotificationService().send(receiver_list=[invitee],
                               message="You have received an invitation about becoming pcmember!")
    return iid


@invitation_blueprint.route('/received', methods=['POST'])
@json_success
@user_has_login
def received():
    # 查看自己收到的所有邀请, 默认为init
    # data: {status: xxx}
    invitation = Invitation(current_app.db)
    user_id = session.get('userid')
    status = request.get_json().get('status', 'init')
    if status not in ['init', 'accept', 'reject']:
        status = 'init'
    query = {invitation.Invitee: user_id, invitation.Status: status}

    invitations = invitation.find(query)
    # 只需要获得inviters 的信息
    inviters = list(set([x[invitation.Inviter] for x in invitations]))
    inviters_info = UserService().find_user_by_ids(inviters)
    invitations = map_key_with_detail(key=invitation.Inviter, origin_data=invitations, detail=inviters_info, target_key='user_info')

    # conference 信息
    conf_ids = list(set([x[invitation.ConfId] for x in invitations]))
    conf_info = ConferenceService().find_conference_by_ids(conf_ids)
    invitations = map_key_with_detail(key=invitation.ConfId, origin_data=invitations, detail=conf_info, target_key='conference_info')

    return invitations


@invitation_blueprint.route('/sent', methods=['POST'])
@json_success
@user_has_login
def sent():
    # 查看自己发出的所有邀请
    invitation = Invitation(current_app.db)
    user_id = session.get('userid')
    status = request.get_json().get('status', 'init')
    if status not in ['init', 'accept', 'reject']:
        status = 'init'
    query = {invitation.Inviter: user_id, invitation.Status: status}
    invitations = invitation.find(query)

    # 只需要获得invitees 的信息
    invitees = list(set([x[invitation.Invitee] for x in invitations]))
    invitees_info = UserService().find_user_by_ids(invitees)
    invitations = map_key_with_detail(key=invitation.Invitee, origin_data=invitations, detail=invitees_info,
                                      target_key='user_info')

    # conference 信息
    conf_ids = list(set([x[invitation.ConfId] for x in invitations]))
    conf_info = ConferenceService().find_conference_by_ids(conf_ids)
    invitations = map_key_with_detail(key=invitation.ConfId, origin_data=invitations, detail=conf_info,
                                      target_key='conference_info')

    return invitations


@invitation_blueprint.route('/approve/<iid>', methods=['POST'])
@json_success
@user_has_login
def approve_invitation(iid):
    # iid=>invitation id
    user_id = session.get('userid')
    username = session.get('username')
    invitation = Invitation(current_app.db)
    invitation_info = invitation.find_one({"_id": iid}, to_raise=True)
    if invitation_info[invitation.Invitee] != user_id:
        raise NO_PRIVILEGE
    invitation.update_one({"_id": iid}, {'status': 'accept'})
    # 调用conference service 里更新会议里的pcmember
    ConferenceService().add_pc_members(cid=invitation_info[invitation.ConfId], members=[user_id])
    NotificationService().send(receiver_list=[invitation_info['inviter']],
                               message=f"{username} has agreed to become pcmember")
    return None


@invitation_blueprint.route('/reject/<iid>', methods=['POST'])
@json_success
@user_has_login
def reject_invitation(iid):
    # iid=>invitation id
    user_id = session.get('userid')
    username = session.get('username')
    invitation = Invitation(current_app.db)
    invitation_info = invitation.find_one({"_id": iid}, to_raise=True)
    if invitation_info[invitation.Invitee] != user_id:
        raise NO_PRIVILEGE
    invitation.update_one({"_id": iid}, {'status': 'reject'})
    NotificationService().send(receiver_list=[invitation_info['inviter']],
                               message=f"{username} has refused to become pcmember")
    return None
