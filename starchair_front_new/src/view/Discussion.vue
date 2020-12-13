<template>
  <div v-if="this.$store.state.user == null">
    <HomePage/>
  </div>
  <div style="padding: 5% 15%" v-else>
    <a-row>
      <h1>Discussion</h1>
    </a-row>
    <br>
    <a-row>
      <div style="background: #fffdf6;border-radius: 10px;padding:20px 50px;text-align:left">

        <a-comment>
          <a-avatar slot="avatar" :size="48" style="margin-top:4px">{{ ($store.state.user && $store.state.user.username.substr(0, 1).toUpperCase()) || 'U' }}</a-avatar>
          <div slot="content">
            <a-form-item>
              <a-textarea :rows="4" :value="commentInput" @change="handleCommentInputeChange" placeholder="Say Something" />
            </a-form-item>
            <a-form-item>
              <a-button html-type="submit" :loading="submitting" type="primary" @click="handleSubmit">
                评论
              </a-button>
            </a-form-item>
          </div>
        </a-comment>


        <a-timeline style="margin: 20px 30px" pending="discussion...">
          <a-timeline-item
            v-for="(post, p_index) in posts"
            :key="post._id">
            <a-comment :author="post.username">
              <a-avatar slot="avatar" :size="40">{{ post.username.substr(0, 1).toUpperCase() || 'U' }}</a-avatar>
              <a-tooltip slot="datetime" :title="post.created_time">
                <span>{{ moment(post.created_time, date_format).fromNow() }}</span>
              </a-tooltip>
              <p slot="content">
                {{ post.content }}
              </p>
              <template slot="actions">
                <span key="comment-basic-reply-to" @click="handleClickReply(p_index, post.username, false)">Reply</span>
              </template>
            </a-comment>

            <div v-if="post.comments.length > 0 || (replyInputVisible && replyInputIndex === p_index)" class="post-comment">
              <a-comment
                v-for="(comment, c_index) in post.comments"
                :key="comment._id"
                :author="comment.username + (comment.replyTo ? `  回复  @${comment.replyTo}` : '' )">
                <a-avatar slot="avatar" :size="28">{{ comment.username.substr(0, 1).toUpperCase() || 'U' }}</a-avatar>
                <a-tooltip slot="datetime" :title="comment.created_time">
                  <span>{{ moment(comment.created_time, date_format).fromNow() }}</span>
                </a-tooltip>
                <p slot="content">
                  {{ comment.content }}
                </p>
                <template slot="actions">
                  <span key="comment-basic-reply-to" @click="handleClickReply(p_index, comment.username, true)">Reply</span>
                </template>
              </a-comment>

              <a-comment :id="`replyInput${p_index}`" v-if="replyInputVisible && replyInputIndex === p_index">
                <a-avatar slot="avatar" :size="28" style="margin-top:4px">{{ $store.state.user.username.substr(0, 1).toUpperCase() || 'U' }}</a-avatar>
                <div slot="content">
                  <a-form-item>
                    <a-textarea :rows="4" :value="replyInput" @change="handleReplyInputeChange" :placeholder="replyInputInit" />
                  </a-form-item>
                  <a-form-item>
                    <a-button html-type="submit" :loading="submitting" type="primary" @click="handleSubmitReply(p_index, post._id)">
                      评论
                    </a-button>
                  </a-form-item>
                </div>
              </a-comment>
            </div>
          </a-timeline-item>
        </a-timeline>

      </div>
    </a-row>
  </div>
</template>

<script>

import moment from 'moment'
import HomePage from "@/view/HomePage";
const date_format = 'YYYY-MM-DD HH:mm'

import DiscussService from '@/model/DiscussService'

export default {
  name: 'Discussion',
  components: {HomePage},
  data() {
    return {
      moment,
      date_format,
      did: '',
      draftId: this.$route.params.draftId,
      commentInput: '',
      submitting: false,
      posts: [],
      replyInputVisible: false,
      replyInputIndex: -1,
      replyInputInit: '',
      replyInput: '',
      replyTo: ''
    }
  },
  methods: {
    handleCommentInputeChange(e) {
      this.commentInput = e.target.value
    },

    handleReplyInputeChange(e) {
      this.replyInput = e.target.value
    },

    async handleSubmit() {
      if (this.commentInput.length > 0) {
        // request create discussion
        let newPost = {
          content: this.commentInput,
          username: this.$store.state.user.username,
          created_time: moment().toISOString(),
          comments: []
        }

        // add discussion data
        const post_id = await DiscussService.createPost({
          did: this.did,
          post: newPost
        })
        newPost._id = post_id
        this.commentInput = ''
        this.posts = [newPost, ...this.posts]
        this.$message.success('留言成功！')
      }
    },

    async handleSubmitReply(p_index, post_id) {
      if (this.replyInput.length > 0) {
        // request create reply
        let newReply = {
          content: this.replyInput,
          username: this.$store.state.user.username,
          created_time: moment().toISOString()
        }
        if (this.replyTo.length) newReply.replyTo = this.replyTo

        // add discussion data
        const comment_id = await DiscussService.createReply({
          did: this.did,
          post_id,
          comment: newReply
        })
        newReply._id = comment_id
        const comments = this.posts[p_index].comments
        this.replyInput = ''
        this.replyInputVisible = false
        this.posts[p_index].comments = [...comments, newReply]
      }
    },

    isElementInViewport (el) {
      const rect = el.getBoundingClientRect()
      return (
          rect.top >= 0 &&
          rect.left >= 0 &&
          rect.bottom <= (window.innerHeight + el.offsetHeight || document.documentElement.clientHeight + el.offsetHeight) &&
          rect.right <= (window.innerWidth || document.documentElement.clientWidth)
      )
    },

    handleClickReply(p_index, username, replyToExist) {
      this.replyTo = replyToExist ? username : ''
      this.replyInputIndex = p_index
      this.replyInputInit = `回复 @${username}`
      this.replyInputVisible = true
      this.replyInput = ''
      setTimeout(() => {
        const replyEle = document.getElementById(`replyInput${p_index}`)
        if (!this.isElementInViewport(replyEle)) {
          replyEle.scrollIntoView()
        }
        replyEle.getElementsByTagName('textarea')[0].focus()
      }, 100)
    },

    async init() {
      const res = await DiscussService.getInfoByDraftId(this.draftId)
      this.did = res._id
      this.posts = res.posts
    }
  },
  mounted() {
    this.init()
  }
}
</script>

<style scoped>
.post-comment {
  border:1px solid #e8e8e8;
  border-radius: 10px;
  padding: 0 20px;
  margin: 15px 40px;
  background-color:#fff;
}

.ant-timeline >>> .ant-timeline-item-content {
  top: -30px;
  margin: 0 0 0 40px;
}
.ant-timeline >>> .ant-timeline-item-pending .ant-timeline-item-head {
  top: -20px;
}
</style>
