# -*- coding: utf-8 -*-

from flask import Blueprint, request, current_app, session
from domain import Notification
from middleware import json_success
from middleware.helper import user_has_login
from middleware.errors import NO_PRIVILEGE
from util.rabbit_producer import rmq_producer


notification_blueprint: Blueprint = Blueprint('notification', __name__)


@notification_blueprint.route('/check', methods=['GET'])
def check():
    return 'success'


# 此方法非用户调用，供其他service使用
@notification_blueprint.route('/send', methods=['POST'])
@json_success
def send():
    notification = Notification(current_app.db)
    data = request.get_json()
    uid_list = data.get('uid_list')
    message = data.get('message')

    doc_id_list = []
    for uid in uid_list:
        rmq_producer.send_message(uid, message)
        # 将消息存储到数据库
        doc_id_list.append(notification.create({'receiver': uid, 'message': message, 'status': 'unread'}))

    return doc_id_list


@notification_blueprint.route('/changeNotificationStatus', methods=['POST'])
@json_success
@user_has_login
def change_notification_status():
    data = request.get_json()
    nid = data.get('nid')
    status = data.get('status')
    notification = Notification(current_app.db)
    noti_info = notification.find_one({"_id": nid})
    if noti_info[notification.Receiver] != session.get('userid'):
        raise NO_PRIVILEGE
    return notification.update_one({"_id": nid}, {notification.Status: status})


@notification_blueprint.route('/getNotificationById/<nid>', methods=['GET'])
@json_success
@user_has_login
def get_notification_by_id(nid):
    notification = Notification(current_app.db)
    return notification.find_one({"_id": nid})


@notification_blueprint.route('/all', methods=['POST'])
@json_success
@user_has_login
def get_notifications():
    # 拿到自己收到的所有的通知
    # data = {status: unread/read}
    data = request.get_json()
    status = data.get('status', 'unread')
    uid = session.get('userid')
    notification = Notification(current_app.db)
    return notification.find({notification.Receiver: uid, notification.Status: status})

