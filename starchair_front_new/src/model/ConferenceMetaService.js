/*
 * Created on 2020/11/30 6:01 下午
 *
 * @Author: fduxuan
 *
 * Desc: 会议的接口
 */


import {Get, Post} from "./req"

export default class ConferenceMetaService  {

    static async create(data){
        return await Post(`/api/conference/createConference`, data)
    }

    static async getUncheckedConference(){
        return await Get(`/api/conference/getUncheckedConference`)
    }

    static async attendAsChairman(data={}){
        return await Post(`/api/conference/attendAsChairman`, data)
    }

    static async attendConference(data={}){
        return await Post(`/api/conference/attendConference`, data)
    }

    static async attendAsPc(data={}){
        return await Post(`/api/conference/attendAsPc`, data)
    }

    static async allConference(data={}){
        // 所有可见的conference
        return await Post(`/api/conference/visible`, data)
    }

    static async attendAsContributor(data={}){
        // 参与投稿的所有
        return await Post(`/api/conference/attendAsContributor`, data)
    }


    static async getConferenceById(cid){
        return await Get(`/api/conference/getConferenceById/${cid}`, )
    }

    static async approveConference(cid){
        return await Post(`/api/conference/approveConference/${cid}`)
    }

    static async rejectConference(cid){
        return await Post(`/api/conference/rejectConference/${cid}`)
    }

    static async changeStatus(cid, status){
        return await Post(`/api/conference/changeStatus/${cid}`, {status: status})
    }
}
