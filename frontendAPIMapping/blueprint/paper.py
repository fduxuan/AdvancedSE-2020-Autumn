# -*- coding: utf-8 -*-

from flask import Blueprint, request, jsonify, Response


paper_blueprint = Blueprint('paper', __name__)


@paper_blueprint.route('/checkIfChair', methods=['GET'])
def CheckIfChair():
    """
    前端调用：get("http://114.116.136.180/checkIfChair", {
      params: {
        meetingId: meetingID,
        username: JSON.parse(localStorage.getItem("userDetails")).username
      }
    })
    :return: {"message":"warning"/"success"} 表示是否为chair
    """
    conferenceId = request.args.get('meetingId')
    username = request.args.get('username')
    # 调用 /getMeeting, 获取meeting信息,拿到meeting的chair信息
    # 调用 /getUser, 根据username拿到user_id

    message = ""
    return {"message": message}


@paper_blueprint.route('/contribution', methods=['POST'])
def Contribution():
    """
    前端调用：
    config = {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    let fd = new FormData();
    fd.append("templateFile", this.contributionForm.file); 文件，list[file]
    fd.append("articleName", this.contributionForm.articleName);
    fd.append("author", JSON.stringify(this.contributionForm.author)); 所有的作者,list[author]
    author: {
        name: "",
        email: "",
        company: "",
        area: ""
    }
    fd.append("summary", this.contributionForm.summary);
    fd.append("topics", this.contributionForm.topics); 稿件的topics,meeting的topics中的一部分
    fd.append(
      "username",
      JSON.parse(localStorage.getItem("userDetails")).username
    );
    fd.append("meetingId", this.meetingId);
    :return: {"message":"success"/"error"}
    """
    file = request.files['templateFile']
    other_info = request.form.to_dict()
    paperName = other_info.get('articleName')
    author = other_info.get('author')
    summary = other_info.get('summary')
    topics = other_info.get('topics')
    username = other_info.get('username')
    conferenceId = other_info.get('meetingId')


    # 调用/getPaper 根据articleName, summary, username, meetingId联合查询，若查询有结果，抛出ArticleHasBeenContributedException()
    # Meeting里是否需要author字段？可能要用于进入身份验证？ author单独指投稿人contributor
    # 调用/getMeeting 看返回的author字段，是否已经包含该用户，确定user_has_contributed
    # user_has_contributed = 'true/false'
    # if not user_has_contributed:
    #     print("调用/updateMeeting, 添加新的author")


    # 保存稿件，saveFile() 返回filePath
    filePath = "文件路径/error"
    if not filePath == 'error':
        print("调用/createPaper")  # 设置authors，topics，meetingId
        return {"message": "success"}
    else:
        return {"message": "error"}

@paper_blueprint.route('/modifyAuthor', methods=['POST'])
def ModifyAuthor():
    # @user_is_author
    """
    前端调用：post("http://114.116.136.180/modifyAuthor", fd, config)
        let fd = new FormData();
        fd.append("templateFile", this.modifyForm.file);
        fd.append("articleName", this.modifyForm.articleName);
        fd.append("author", JSON.stringify(this.modifyForm.author)); 所有的作者，list[author]
        author: {
            name: "",
            email: "",
            company: "",
            area: ""
        }
        fd.append("summary", this.modifyForm.summary);
        fd.append("topics", this.modifyForm.topic);
        fd.append(
          "username",
          JSON.parse(localStorage.getItem("userDetails")).username
        );
        fd.append("meetingId", this.meetingId);
        fd.append("articleId", this.item.id);
        // console.log(this.meetingId);
        let config = {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        };
    返回{"message":"success"/"error"}
    """
    file = request.files['templateFile']
    other_info = request.form.to_dict()
    paperName = other_info.get('articleName')
    author = other_info.get('author')
    summary = other_info.get('summary')
    topics = other_info.get('topics')
    username = other_info.get('username')
    conferenceId = other_info.get('meetingId')
    paperId = other_info.get('articleId')

    # Meeting里是否需要author字段？可能要用于进入身份验证？
    # 由于author role 只有contributor才有，这一段不必重复
    # 调用/getMeeting 看返回的author字段，是否已经包含该用户，确定user_has_contributed
    # user_has_contributed = 'true/false'
    # if not user_has_contributed:
    #     print("调用/updateMeeting, 添加新的author")

    # 保存稿件，saveFile() 返回filePath
    filePath = "文件路径/error"
    if not filePath == 'error':
        print("调用/updatePaper")  # 设置authors，topics，meetingId
        return {"message": "success"}
    else:
        return {"message": "error"}


@paper_blueprint.route('/authorRebuttal', methods=['POST'])
def AuthorRebuttal():
    # @user_is_author
    """
    前端调用：post("http://114.116.136.180/authorRebuttal", {
          reason:this.rebuttalForm.reason,
          username: JSON.parse(localStorage.getItem("userDetails")).username,
          meetingId:this.meetingId,
          articleId: this.item.id,
          isRebuttaled:false/true
        })
    :return: "success"
    """
    data = request.json()
    reason = data.get('reason')
    username = data.get('username')
    conferenceId = data.get('meetingId')
    paperId = data.get('articleId')
    isRebuttaled = data.get('isRebuttaled')
    # 调用/getPaper 获取稿件信息
    paper = {}
    if not isRebuttaled:
        # paper应该有个状态，记录是否已经rebuttaled/confirmed，前端需要这个状态，因为只能rebuttal一次
        paper.reviewStatus = "Confirmed"
    else:
        paper.reviewStatus = "Rebuttaled"
        paper.rebuttal = "rebuttalReason"
        # 调用/updateRecord, 存储一轮发布的oldScore，用于前端显示
        # 调用/updatePaper, 存储修改后的稿件信息
    return "success"


@paper_blueprint.route('/showPdf', methods=['GET'])
def ShowPdf():
    # @user_is_pcMember
    """
    前端调用：get("http://114.116.136.180/showPdf", {
            responseType: "arraybuffer",
            params: {
              //用户参数
              path: item.filePath
            }
          })
    这里前端要改一下，改成传paperId
    :return: 文件的byteArray
    """
    paperId = request.args.get('paperId')
    # 调用/getPaper, 获取paper信息
    paper = {}
    # 将paper.file 实现readFileToByteArray的功能
    response = Response("bytearray")
    response.headers.set('Content-Type', 'application/pdf')
    return response


@paper_blueprint.route('/getAllotedArticle', methods=['GET'])
def GetAllotedArticle():
    # @user_is_pcMember
    """
    前端调用：get("http://114.116.136.180/getAllotedArticle", {
            params: {
              //用户参数
              username: JSON.parse(localStorage.getItem("userDetails")).username,
              meetingId: this.meetingId
            }
          })
    :return: {"articleList": [], "submitStatus": ""}
    """
    username = request.args.get('username')
    conferenceId = request.args.get('meetingId')
    # 调用/getUser
    user = {}
    # 调用/getMeeting
    meeting = {}
    # 调用/getPaper， 根据pc_id meeting_id 查询对应的paper
    papers = []
    for paper in papers:
        # 调用/getRecord, 返回的评分信息要注意去除其他pc_member的评分
        paper.score = []

    return {"articleList": papers, "submitStatus": meeting.submitStatus}

