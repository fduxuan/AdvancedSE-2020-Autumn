# -*- coding: utf-8 -*-

from flask import Blueprint, request, jsonify


meeting_blueprint = Blueprint('meeting', __name__)


@meeting_blueprint.route('/applyConference', methods=['POST'])
def ApplyConference():
    """
    前端调用：post("http://114.116.136.180/applyConference",{
        "fullname": "",
        "shortenForm": "",
        "time": "",
        "location": "",
        "ddl": "",
        "publishingTime": "",
        "applicant": "",
        "topic": []
    })
    :return: 新申请的会议，需要有id字段用于前端判断
    """
    data = request.json
    # 调用/createMeeting 新建会议 返回新建的会议record
    result = {}
    return result


@meeting_blueprint.route('/getUncheckedConference', methods=['GET'])
def GetUncheckedConference():
    # @user_is_admin
    """
    Admin view the unchecked conferences.
    前端调用：get("http://114.116.136.180/getUncheckedConference")
    :return: [meeting1, topicList1, meeting2, topicList2, ...]
    建议改成纯json，前端处理
    """
    # 调用/getMeeting 以下query是按照java后端写的，示例
    query = {"isVarified": "false"}
    result = ["meeting1", "topicList1", "meeting2", "topicList2", "..."]
    return jsonify(result)


@meeting_blueprint.route('/getAllPassedMeetings', methods=['GET'])
def GetAllPassedMeetings():
    """
    User view the passed conferences.
    前端调用：get("http://114.116.136.180/getAllPassedMeetings")
    :return: [meeting1, topicList1, meeting2, topicList2, ...]
    建议改成纯json，前端处理
    """
    # 调用/getMeeting 以下query是按照java后端写的，示例
    query = {"isVarified": "pass"}
    result = ["meeting1", "topicList1", "meeting2", "topicList2", "..."]
    return jsonify(result)


@meeting_blueprint.route('/changeApplicationStatus', methods=['POST'])
def ChangeApplicationStatus():
    # @user_is_admin
    """
    前端调用：post("http://114.116.136.180/changeApplicationStatus", {
      applicationId: id,
      applyStatus: status
    })
    :return: 新的待审核会议list,但前端没用到
    """
    data = request.json()
    conferenceId = data.get('applicationId')
    newStatus = data.get('applyStatus')
    # 调用/updateMeeting 修改会议状态
    if newStatus == "pass":
        # 目前的API只有根据userId和meetingId判断authority状态这一个选项，根据我们的数据表拆分，这里不需要动user表
        # 调用/updateMeeting, 设置该会议的chair和pc（申请人的topic关联项，是该会议的topic）
        print()
    # 调用/getUncheckedConference
    uncheckedConferences = []
    return jsonify(uncheckedConferences)  # 返回的结果，前端没有用到


@meeting_blueprint.route('/changeSubmitStatus', methods=['GET'])
def ChangeSubmitStatus():
    # @user_is_chair
    """
    前端调用：get("http://114.116.136.180/changeSubmitStatus", {
            params: {
              meetingID: this.$route.query.confID,
              submitStatus: status
            }
          })
    :return: {"message":"success"}
    """
    conferenceId = request.args.get('meetingID')
    newStatus = request.args.get('submitStatus')
    # 调用/updateMeeting 修改会议状态
    return {"message": "success"}


@meeting_blueprint.route('/getPcList', methods=['GET'])
def GetPcList():
    # @user_is_chair

    """
    前端调用：get("http://114.116.136.180/getPcList", {
        params: {
          meetingID: this.$route.query.confID // 123
        }
      })
    :return: {"userDetails": userList, "topicDetails":[] , "submitStatus"}PC member列表和对饮的topics列表
    """
    conferenceId = request.args.get("meetingID")
    # 调用/getMeeting, 获取会议信息，拿到其中的每一个pc member id信息，以及topic信息
    # 调用/getUser，根据user_id拿到完整的user信息
    userList = []
    topicList = [[], []]
    submitStatus = ""
    return {"userDetails": userList, "topicDetails": topicList, "submitStatus": submitStatus}


@meeting_blueprint.route('/startReview', methods=['GET'])
def StartReview():
    # @user_is_chair
    """
    前端调用：get("http://114.116.136.180/startReview", {
            params: {
              meetingId: this.$route.query.confID,
              strategy: reviewWay
            }
          })
    :return:{"message":"No article found!"/"success"}
    """
    conferenceId = request.args.get('meetingId')
    strategy = request.args.get('strategy')

    # 分配算法，太复杂不想看了
    message = ""
    return {"message": message}


@meeting_blueprint.route('/confirmFinish', methods=['GET'])
def ConfirmFinish():
    # @user_is_chair
    """
    前端调用：get("http://114.116.136.180/confirmFinish", {
        params: {
          meetingId: this.$route.query.confID
        }
      })
    :return:{"message":"success"}
    """
    conferenceId = request.args.get('meetingId')
    newStatus = "Reviewing"  # meeting生命周期也要和前端对上
    # 调用/updateMeeting 修改会议状态
    message = "success"
    return {"message": message}


@meeting_blueprint.route('/finishReview', methods=['GET'])
def FinishReview():
    # @user_is_chair
    """
    前端调用：get("http://114.116.136.180/finishReview", {
        params: {
          meetingId: this.$route.query.confID
        }
      })
    :return: "success"
    """
    conferenceId = request.args.get('meetingId')
    # 首先判断评审是否结束，对每一篇论文，查看其review状态
    # 调用/getPaper 根据meetingId，查找对应的论文列表
    papers = []
    all_finished = True
    for paper in papers:
        if paper.status == '未完成review状态':  # 示例状态
            all_finished = False
            break
    if not all_finished:
        raise Exception  # review Not finished exception

    # 调用/updateMeeting 修改会议状态为"fistDiscussion"
    # 为每篇paper建讨论贴的第一条
    for paper in papers:
        # 调用/createDiscussion 建第一条讨论贴，内容为"Feel free to share your precious opinions on this paper."
        print("create first discussion logic")

    return "success"


@meeting_blueprint.route('/firstPublishScores', methods=['GET'])
def FirstPublishScores():
    # @user_is_chair
    """
    前端调用：get("http://114.116.136.180/firstPublishScores", {
        params: {
          meetingId: this.$route.query.confID
        }
      })
    :return: "success"
    """
    conferenceId = request.args.get('meetingId')

    # 调用/getPaper 根据meetingId，查找对应的论文列表
    papers = []
    for paper in papers:
        # 调用/getRecord, 根据conferenceId和paperId 获取打分
        records = []
        for record in records:
            if not record.confirmed:
                raise Exception  # ScoreNotConfirmedException
    # 判断稿件是否被录用，并修改稿件状态
    # 调用/updateMeeting, 设置会议状态为"rebuttal"

    return "success"


@meeting_blueprint.route('/finalPublishScores', methods=['GET'])
def FinalPublishScores():
    # @user_is_chair
    """
    前端调用：get("http://114.116.136.180/finalPublishScores", {
        params: {
          meetingId: this.$route.query.confID
        }
      })
    :return: "success"
    """
    conferenceId = request.args.get('meetingId')

    # 调用/getPaper 根据meetingId，查找对应的论文列表
    papers = []
    for paper in papers:
        # 调用/getRecord, 根据conferenceId和paperId 获取打分
        records = []
        for record in records:
            if not record.confirmed:
                raise Exception  # ScoreNotConfirmedException
    # 判断稿件是否被录用，并修改稿件状态
    # 调用/updateMeeting, 设置会议状态为"final"

    return "success"


@meeting_blueprint.route('/viewAllArticles', methods=['GET'])
def ViewAllArticles():
    # @user_is_chair
    """
    前端调用：get("http://114.116.136.180/viewAllArticles", {
        params: {
          meetingId: this.$route.query.confID
        }
      })
    :return: 返回的articleList, 包含很多东西，需要仔细对照前端
    """
    conferenceId = request.args.get('meetingId')
    # 调用/getMeeting 获取会议信息
    # 调用/getPaper 根据meetingId 查找稿件
    submitStatus = ""
    papers = []
    if submitStatus == "withTopic" or submitStatus == "withAverage":
        for paper in papers:
            # 调用/getRecord 根据meetingId和paperId获取打分记录
            records = []
            # 判断打分已确认的记录个数
            confirmed_records = []
            if len(confirmed_records) >= 3:
                print("调用/updatePaper 修改稿件状态为reviewed")
            # 继续组装必要信息，包括每篇稿件对应的评分records，审稿人，等等。。。
    return jsonify(papers)





