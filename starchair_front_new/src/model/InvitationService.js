/*
 * Created on 2020/12/2 9:25 上午
 *
 * @Author: fduxuan
 *
 * Desc:
 */
import {Get, Post} from "./req"

export default class InvitationService  {
    static async create(cid, invitee){
        return await Post(`/api/invitation/createInvitation`,{confId: cid, invitee:invitee})
    }

    static async received(status='init'){
        // 查看所有自己收到的邀请
        return await Post(`/api/invitation/received`, {status:status})
    }

    static async sent(status='init'){
        return await Post(`/api/invitation/sent`, {status:status})
    }

    static async approve(iid){
        return await Post(`/api/invitation/approve/${iid}`)
    }

    static async reject(iid){
        return await Post(`/api/invitation/reject/${iid}`)
    }


}
