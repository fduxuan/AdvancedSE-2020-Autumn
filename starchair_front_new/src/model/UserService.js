/*
 * Created on 2020/11/24 3:17 下午
 *
 * @Author: fduxuan
 *
 * Desc: UserService 的 api
 */


import {Get, Post} from "./req"
import axios from 'axios'

export default class UserService  {
  base_url = 'http://starchair-user-service:5001'

  static async login(data){
    return await Post(`/api/user/login`, data)
  }

  static async register(data){
    return await Post(`/api/user/register`, data)
  }

  static async logout(){
    return await Post(`/api/user/logout`)
  }

  static async getInfo(){
    return await Get(`/api/user/info`)
  }

  static async getInfoInInit() {
    const config = {
      headers: {'Content-Type': 'application/json;charset=UTF-8'},
      params: {}
    }
    const resp = await axios.get(`/api/user/info`, config)
    const data = resp.data
    if (resp.status !== 200) {
      throw resp.statusText
    }
    if (data.code !== 0) {
      throw data.error
    }
    return data.data
  }

  static async getUserById(uid){
    return await Get(`/api/user/getUserById/${uid}`)
  }

  static async findUserByName(name=""){
    return await Post(`/api/user/findUserByName`, {fullname: name})
  }

}
