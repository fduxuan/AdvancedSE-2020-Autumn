# -*- coding: utf-8 -*-

from .helper import Model


class Notification(Model):
    coll_name = "notification"

    Receiver = 'receiver'
    Message = 'message'
    Status = 'status'  # Read/Unread

