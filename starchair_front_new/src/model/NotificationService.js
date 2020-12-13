/*
 * Created on 2020/12/9 10:40 上午
 *
 * @Author: fduxuan
 *
 * Desc: 增加notification系统通知
 */
import {Get, Post} from "./req"

export default class NotificationService  {

    static async getNotifications(status='unread'){
        return await Post(`/api/notification/all`, {status: status})
    }

    static async changeNotificationStatus(nid, status){
        return await Post(`/api/notification/changeNotificationStatus`, {nid: nid, status: status})
    }
}
