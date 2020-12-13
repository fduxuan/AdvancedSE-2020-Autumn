/*
 * Created on 2020/11/24 3:14 下午
 *
 * @Author: fduxuan
 *
 * Desc: 封装axios
 */

import axios from "axios"
import { message } from 'ant-design-vue'

export async function Get(url, config=undefined) {

    if(config === undefined) {config = {}}
    if(config.params === undefined) {config.params = {}}
    config.headers = {'Content-Type': 'application/json;charset=UTF-8'}
    let resp = await axios.get(url, config);
    let data = resp.data;
    if(resp.status !== 200) {
        throw resp.statusText
    }
    if(data.code !== 0) {
        message.error(data.error)
        throw data.error
    }
        return data.data
}

export async function Post(url, data, config=undefined) {
    if(config === undefined) {config = {}}
    if (config.headers === undefined) {
        config['headers'] = {'Content-Type': 'application/json;charset=UTF-8'}
    }
    config.withCredentials=true
    let resp = await axios.post(url, data, config)

    let respData = resp.data
    if(resp.status !== 200) {
        throw resp.statusText
    }
    if(respData.code !== 0) {
        message.error(respData.error)
        throw respData.error
    }
    return respData.data
}

