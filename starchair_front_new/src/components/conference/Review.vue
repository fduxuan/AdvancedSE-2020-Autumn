<template>
  <div>
    <!--------------------生命周期控制，开启审核前不可见----------------------->
    <div v-if="['accept', 'submitting'].includes(status)">
      <a-result status="403" title="Not Start" sub-title="This conference has not started reviewing yet">
        <template #extra>
          <a-button type="primary" @click="backHome">Back Home</a-button>
        </template>
      </a-result>
    </div>
    <div v-else>
      <p class="intab-title"> Review Drafts </p>
      <a-row style="margin: 3%; text-align: left" >
        <a-col>
          <!--------------------筛选filter方便pcmember检索----------------------->
          <a-radio-group default-value="all" button-style="solid" @change="search"  v-model="filter">
            <a-radio-button value="all">All</a-radio-button>
            <a-radio-button value="init">Not Scored</a-radio-button>
            <a-radio-button value="firstResult">Already Scored</a-radio-button>
            <a-radio-button value="rebuttal">Rebuttal</a-radio-button>
          </a-radio-group>
        </a-col>
      </a-row>

      <a-list :grid="{ gutter: 16, column: 2}" :data-source="data">
        <a-list-item slot="renderItem" slot-scope="item, index">
          <a-card :title="item.draft_info.title" style="text-align:left;letter-spacing:1px">
            <!--------------------状态控制，后续添加筛选filter方便pcmember检索----------------------->
            <a-tag slot="extra"  color="orange" v-if="item.status==='init'">Not Scored</a-tag>
            <a-tag slot="extra"  color="green" v-if="item.status==='firstResult'">Already Scored</a-tag>
            <a-tag slot="extra"  color="red" v-if="item.status==='rebuttal'">Rebuttal</a-tag>
            <a-tag slot="extra"  color="cyan" v-if="item.status==='finalResult'">Rescored</a-tag>
            <div>
              <b>Contributor:</b>&nbsp;
              <span style="font-size: 16px">{{item.contributor_info.fullname}}</span>
            </div>
            <!--------------------文稿的tags----------------------->
            <div style="margin-top: 10px">
              <b>Topics:</b>&nbsp;
              <a-tag
                v-for="(topic, index) in item.draft_info.topics"
                :key="topic"
                :color="colors[index%colors.length]"
                style="margin-left: 5px;margin-top:10px">
                {{ topic }}
              </a-tag>
            </div>
            <!--------------------文稿的摘要----------------------->
            <div class="mt-15">
              <b>Summary:</b>&nbsp;
              <span>{{item.draft_info.summary}}</span>
            </div>

            <div class="mt-15">
              <b>Files:</b>
              <a-button size="small" icon="eye" style="margin-left:20px" @click="handleClickPreview(item.draft_info.file_id)">Preview</a-button>
            </div>
            <a-row class="mt-15" type="flex" justify="space-around">
              <a-button type="primary" @click="showModal(item.draftId, index, item.status)">
                Score
              </a-button>
              <a-button @click="goDiscussion(item.draftId)" v-if="['firstDiscussion', 'finalDiscussion'].includes(status)" type="primary">Discussion</a-button>
            </a-row>
          </a-card>
        </a-list-item>
      </a-list>
      <!--------------------打分表单----------------------->
      <template>
        <div>
          <a-modal v-model="visible" title="Score" @ok="handleOk">
            <div v-if="['firstPublish', 'finalPublish'].includes(this.status)">
              <a-result status="403" title="403" sub-title="Sorry, It's not the time for scoring">
              </a-result>
            </div>
            <div v-else>
              <a-form
                  id="components-form-demo-validate-other"
                  v-bind="formItemLayout"
                  :form="form"
              >
                <a-form-item label="score">
                  <a-radio-group v-decorator="['score',{ rules: [{ required: true, message: 'Please choose at least one item!' }]}]">
                    <a-radio :value="-2">
                      -2(reject)
                    </a-radio>
                    <a-radio :value="-1">
                      -1(weak reject)
                    </a-radio>
                    <a-radio :value="1">
                      weak accept
                    </a-radio>
                    <a-radio :value="2">
                      accept
                    </a-radio>
                  </a-radio-group>
                </a-form-item>
                <a-form-item label="Comment">
                  <a-input v-decorator="['comment',{ rules: [{ required: true, message: 'Please input the comment!' }] },]"
                           placeholder="your comments">
                  </a-input>
                </a-form-item>
                <a-form-item label="confidence">
                  <a-radio-group v-decorator="['confidence',{ rules: [{ required: true, message: 'Please choose at least one item!' }]}]">
                    <a-radio :value="-2">
                      very low
                    </a-radio>
                    <a-radio :value="-1">
                      low
                    </a-radio>
                    <a-radio :value="1">
                      high
                    </a-radio>
                    <a-radio :value="2">
                      very high
                    </a-radio>
                  </a-radio-group>
                </a-form-item>
              </a-form>
            </div>

          </a-modal>
        </div>
      </template>
    </div>
  </div>
</template>

<script>

import ReviewProcessService from "@/model/ReviewProcessService";
export default {
  props:['status', 'cid'],
  data() {
    return {
      data: [],
      filter: 'all',
      current_draft: undefined,
      current_draft_index: undefined,
      colors: ['pink', 'red', 'blue', 'green', 'orange', 'purple'],
      visible: false,
      form: this.$form.createForm(this),
      formItemLayout: {
        labelCol: { span: 6 },
        wrapperCol: { span: 14 },
      },
      status_map: {'init': 'Not Scored', 'firstResult': 'Already Scored'}
    };
  },
  methods: {
    /* 回到主界面 */
    backHome(){
      this.$router.push({path: '/conference'}).catch(e=>{})
    },

    /* 打开打分的表单，同时针对是否已经打过分进行判断，打过则自动赋值 */
    async showModal(draftId, index, status) {
      this.visible = true;
      if(status!=='init'){
        let review = await ReviewProcessService.getReviewProcessByDraftId(draftId)
        this.form.setFieldsValue({
          score: review.score,
          comment: review.comment,
          confidence: review.confidence
        });
      }
      this.current_draft = draftId
      this.current_draft_index = index
    },

    /* 打分，自动更新字段 */
    async handleOk(e) {
      this.form.validateFields(async (err, values) => {
        if (!err) {
          console.log(values)
          values['draftId'] = this.current_draft

          await ReviewProcessService.score(values)

          this.$message.success('success score！')
          this.form.resetFields();
          this.visible = false;

          // 如果要转变状态的，从列表中移除
          if (['init', 'rebuttal'].includes(this.filter)){
            this.data.splice(this.current_draft_index, 1)
            return
          }
          let current_status = this.data[this.current_draft_index]['status']
          this.data[this.current_draft_index]['status'] = ['firstResult', 'init'].includes(current_status) ? 'firstResult': 'finalResult'

        }
      });
    },

    /* 获得对应的filter数据 */
    async search(e){
      let status = e.target.value
      console.log('ttt',this.filter)
      if(status === 'all'){
        this.data = await ReviewProcessService.getReviewProcessByPcMember(this.cid)

      }
      else if(status === 'rebuttal'){
        let data1 = await ReviewProcessService.getReviewProcessByPcMember(this.cid, 'rebuttal')
        let data2 = await ReviewProcessService.getReviewProcessByPcMember(this.cid, 'finalResult')
        this.data = data1.concat(data2)
      }
      else{
        this.data = await ReviewProcessService.getReviewProcessByPcMember(this.cid, status)
      }
    },

    /* 前往讨论区 */
    goDiscussion(draftId){
      const url = this.$router.resolve({path: `/discussion/${draftId}` })
      window.open(url.href, '_blank')
    },

    handleClickPreview(fid) {
      const pdfURL = this.$router.resolve({path: `/pdfpreview/${fid}`})
      window.open(pdfURL.href, '_blank')
    }
  },

  async mounted() {
    this.data = await ReviewProcessService.getReviewProcessByPcMember(this.cid)
  }
};
</script>
<style>
.mt-15 {
  margin-top: 20px;
}
.desc {
  text-align: left;
}
.desc > a {
  margin-left: 30px;
  font-size: 16px;
  color: blue;
}
.ant-card-head-wrapper > .ant-card-head-title {
  font-size: 14px;
  overflow: visible;
  white-space: normal;
  text-overflow: clip;
}
</style>
