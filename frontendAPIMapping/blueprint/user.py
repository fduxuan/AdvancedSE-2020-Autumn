# -*- coding: utf-8 -*-

from flask import Blueprint, request, jsonify


user_blueprint = Blueprint('user', __name__)


@user_blueprint.route('/register', methods=['POST'])
def Register():
    """
    前端调用：post("http://114.116.136.180/register", {
            username: this.registerform.username,
            fullname: this.registerform.fullname,
            password: this.registerform.password,
            checkPassword: this.registerform.checkPassword,
            e_mail: this.registerform.e_mail,
            companie: this.registerform.companie,
            area: this.registerform.area
          })
    返回新建用户信息，需有id字段
    """
    data = request.json()
    username = data.get('username')
    email = data.get('e_mail')
    # 调用/getUser 判断username是否已存在
    username_exists = False
    # 调用/getUser 判断email是否已存在
    email_exists = False
    if username_exists:
        raise Exception  # UsernameHasBeenRegisteredException
    elif email_exists:
        raise Exception  # MailBoxHasBeenRegisteredException
    else:
        # 调用/createUser
        newUser = {}
        return newUser


@user_blueprint.route('/login', methods=['GET'])
def Login():
    """
    前端调用：post("http://114.116.136.180/login", {
            username: this.loginform.username,
            password: this.loginform.password
          })
    返回{"token": token, "userDetails": user}
    """
    data = request.json()
    username = data.get('username')
    password = data.get('password')
    # 调用/getUser 判断username是否已存在，并获得用户信息
    user = {}
    username_exists = True
    password_match = True
    if not username_exists:
        raise Exception  # UsernameNotFoundException
    elif not password_match:
        raise Exception  # BadCredentialsException
    else:
        # 生成jwt token
        token = ""
        return {"token": token, "userDetails": user}


@user_blueprint.route('/searchUserInfo', methods=['GET'])
def SearchUserInfo():
    # @user_is_chair
    """
    前端调用：get("http://114.116.136.180/searchUserInfo", {
        params: {
          fullName: input
        }
      })
    返回用户list
    """
    fullName = request.args.get("fullName")
    # 调用/getUser,返回user list，示例query
    query = {"fullName": fullName}
    userList = []
    return jsonify(userList)


@user_blueprint.route('/sendInvitation', methods=['POST'])
def SendInvitation():
    # @user_is_chair
    """
    前端调用：post("http://114.116.136.180/sendInvitation", {
          meetingID: this.$route.query.confID,
          inviter: JSON.parse(localStorage.getItem("userDetails")).username,
          invitee: self.inviteeList
        })
    返回{"message":"success"} 目前后端只有success状态
    """
    data = request.json()
    conferenceId = data.get("meetingID")
    inviter_username = data.get("inviter")
    invitees = data.get("invitee")
    # 调用/getUser,返回inviter_username对应的inviter信息
    inviter = {}
    for invitee in invitees:
        # 调用/getInvitation, 返回inviter_id, invitee_id, conferenceId 联合对应的invitation
        invitation = [{}, {}]
        if len(invitation) == 0 and inviter.id != invitee.id:  # 相同邀请只存一次，且不能邀请自己
            print("调用/createInvitation")
    message = "success"
    return {"message": message}


@user_blueprint.route('/author', methods=['GET'])
def Author():
    # @user_is_author
    """
    前端调用：get("http://114.116.136.180/author", {
          params: {
            //用户参数
            username: JSON.parse(localStorage.getItem("userDetails")).username,
            meetingId: this.meetingId
          }
        })
    返回{"articleDetails": articleList, "topicDetails": }
    返回的是用户在当前会议的投稿，而不是所有投稿
    具体需要查看前端返回值
    """
    username = request.args.get('username')
    meetingId = request.args.get('meetingId')
    # 调用/getPaper 根据username和meetingId过滤
    papers = []
    # 调用/getMeeting 获取会议信息
    meeting = {}
    # 接下来为每篇paper补充score信息
    if meeting.submitStatus in ['inSubmit', 'withTopic', 'withBurden', 'firstDiscussion']:
        # 还没发布评审结果，此时author不允许看到评分信息，但是数据库表拆分后，paper里本来就不包含评分
        for paper in papers:
            # 加上空的score字段，前端要用
            paper.score = None
    elif meeting.submitStatus == 'rebuttal':
        for paper in papers:
            # 此时，不允许看到新的评分信息，只能看到第一次发布的分数，所以需要额外存储第一次发布的分数
            # 调用/getRecord 获取第一次发布的分数信息
            oldScore = {}
            paper.score = oldScore
    # 由于现在topics直接保存在会议中，直接从meeting中取
    topicDetails = meeting.topic
    return {"articleDetails": papers, "topicDetails": topicDetails}



