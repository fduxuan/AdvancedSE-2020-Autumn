/*
 * Created on 2020/12/4 3:52 下午
 *
 * @Author: fduxuan
 *
 * Desc:
 */

import {Get, Post} from "./req"

export default class UserService  {

    static async allocDraft(cid){
        return await Post(`/api/reviewProcess/allocDraft`, {confId: cid})
    }

    static async getReviewProcessByPcMember(confId,status=undefined){
        let data = status === undefined ? {}: {status: status}
        data['confId'] = confId
        return await Post(`/api/reviewProcess/getReviewProcessByPcMember`, data)
    }

    static async score(data){
        return await Post(`/api/reviewProcess/score`, data)
    }

    static async getReviewProcessByDraftId(did){
        return await Get(`/api/reviewProcess/getReviewProcessByDraftId/${did}`)
    }

    static async rebuttal(pid, data){
        return await Post(`/api/reviewProcess/rebuttal`, {reviewId: pid, rebuttal: data})
    }
}
