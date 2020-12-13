/*
 * Created on 2020/12/3 3:04 下午
 *
 * @Author: fduxuan
 *
 * Desc: 提交文稿相关api
 */

import {Get, Post} from "./req"
import axios from 'axios'

export default class DraftMetaService  {

    static async upload(formData){
        const headers = {
            'Content-Type': 'multipart/form-data',
        }
        return await Post('/api/draft/upload', formData, headers)
    }

    static async create(data){
        return await Post(`/api/draft/submitDraft`, data)
    }

    static async visible(conference_id){
        return await Get(`/api/draft/visible/${conference_id}`)
    }

    static async download(fid) {
        const config = {
            byteResponse: true,
            responseType: 'blob'
        }
        const resp = await axios.get(`/api/draft/download/${fid}`, config)
        if (resp.status !== 200) {
            throw resp.statusText
        }
        return resp.data
    }

    static async update(data){
        return await Post(`/api/draft/modifyDraft`, data)
    }
}
