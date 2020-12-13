import {Get, Post} from './req'

export default class DiscussService {

  static async getInfoByDraftId(draftId) {
    return await Get(`/api/discuss/getOrCreateByDraft/${draftId}`)
  }

  static async createPost(data) {
    return await Post('/api/discuss/createPost', data)
  }

  static async createReply(data) {
    return await Post('/api/discuss/createReply', data)
  }
  
}