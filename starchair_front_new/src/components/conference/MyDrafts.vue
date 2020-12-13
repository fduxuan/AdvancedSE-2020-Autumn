<template>
  <div>
    <div v-if="status==='accept'">
      <a-result status="403" title="Not Start" sub-title="This conference has not started submission yet">
        <template #extra>
          <a-button type="primary" @click="backHome">
            Back Home
          </a-button>
        </template>
      </a-result>
    </div>
    <div v-else>
      <a-row>
        <h1>
          My Drafts
          <a-tooltip placement="bottom">
            <template slot="title">
              <span>Make new submission</span>
            </template>
            <a-button type="primary" shape="circle" icon="plus" @click="showCreateModal()" />
          </a-tooltip>
        </h1>
      </a-row>
      <!-- <p class="intab-title"> My Drafts </p> -->
      <p class="intab-subtitle"> 当前共提交了 {{drafts.length}} 篇论文</p>
      <a-list :grid="{ gutter: 16, column: 2}" :data-source="drafts">
        <a-list-item slot="renderItem" slot-scope="item, index">
          <a-card style="text-align:left" >
            <div style="text-align:center" slot="title">
              {{ item.title }}
            </div>
            <div>
              <b>Contributor:</b>&nbsp;
              <span style="font-size: 16px">{{$store.state.user.fullname}}</span>
            </div>
            <div style="margin-top: 10px">
              <b>Topics:</b>&nbsp;
              <a-tag
                v-for="(topic, index) in item.topics"
                :key="topic"
                :color="colors[index%colors.length]"
                style="margin-left: 5px;margin-top:10px">
                {{ topic }}
              </a-tag>
            </div>
            <div class="mt-15">
              <b>Summary:</b>&nbsp;
              <span>{{item.summary}}</span>
            </div>
            <div class="mt-15">
              <b>Files:</b>
              <a-button size="small" icon="eye" style="margin-left:20px" @click="handleClickPreview(item.file_id)">Preview</a-button>
            </div>
            <a-row class="mt-15" type="flex" justify="space-around">
              <a-button type="primary" v-if="status==='submitting'" @click="showModifyModal(item)">
                Modify
              </a-button>
              <a-button type="primary" v-if="['firstPublish', 'finalDiscussion', 'finalPublish'].includes(status)" @click="showScoreModal(item)">
                View Score
              </a-button>
              <a-button type="error" v-if="status==='firstPublish'" @click="showRebuttalModal(item)">
                Rebuttal
              </a-button>
            </a-row>
          </a-card>
        </a-list-item>
      </a-list>

      <submit-draft
          :visible="createModalVisible"
          :confirmLoading="createModalConformLoading"
          :conference="conference"
          :modalType="modalType"
          :modalTitle="modalTitle"
          :currentDraft="currentDraft"
          :modalForm="modalForm"
          @handleClickOk="handleClickModalOk"
          @handleClickCancel="createModalVisible=false" />
    </div>
  </div>
</template>

<script>

import DraftMetaService from "@/model/DraftMetaService";
import SubmitDraft from './SubmitDraft.vue';
import ReviewProcessService from "@/model/ReviewProcessService";
export default {
  name: "MyDrafts",
  components: {
    SubmitDraft
  },
  props: ['cid', 'status', 'conference'],
  data() {
    return {
      drafts:[],
      colors: ['pink', 'red', 'blue', 'green', 'orange', 'purple'],
      currentDraft: {},
      modalType: 'create',
      modalTitle: 'MAKE NEW SUBMISSION',
      modalForm: {},
      visible: false,
      createModalVisible: false,
      createModalConformLoading: false,
      form: this.$form.createForm(this),
      formItemLayout: {
        labelCol: { span: 6 },
        wrapperCol: { span: 14 },
      }
    };
  },
  methods: {
    backHome(){
      this.$router.push({'path': '/conference'}).catch(e=>{})
    },
    showCreateModal(){
      this.modalType = 'create'
      this.modalTitle = 'MAKE NEW SUBMISSION'
      this.modalForm = {
        authors: [],
      }
      this.createModalVisible=true

    },
    showModifyModal(draft) {
      this.modalType = 'modify'
      this.modalTitle = 'MODIFY YOUR SUBMISSION'
      this.currentDraft = draft
      console.log(this.currentDraft)
      this.modalForm = {
        authors: draft.authors,
        title: draft.title,
        topics: draft.topics,
        summary: draft.summary
      }
      this.createModalVisible = true
    },
    async getDrafts(){
      this.drafts = await DraftMetaService.visible(this.cid)
      console.log(this.drafts)
    },

    /*分别处理create和modify*/
    async handleClickModalOk(value){
      if (this.modalType === 'create'){
        await this.handleCreateDraft(value)
      }else{
        await this.handleModifyDraft(value)
      }

    },
    /*提交论文*/
    async handleCreateDraft(value) {
      console.log('开始提交', value)
      let uploadFile = value.uploadFile
      delete value.uploadFile;
      let draft_id = await DraftMetaService.create(value)
      await this.uploadFile(draft_id, uploadFile)
      this.createModalConformLoading = true
      setTimeout(() => {
        this.createModalVisible = false
        this.createModalConformLoading = false
        this.$message.success('create successfully!')
        this.getDrafts()
      }, 1000)
    },
    /*修改论文*/
    async handleModifyDraft(value){
      console.log('开始修改', value)
      let uploadFile = value.uploadFile
      delete value.uploadFile
      console.log(value)
      value['_id'] = this.currentDraft._id
      await DraftMetaService.update(value)
      if (uploadFile !== undefined){  // 修改了pdf文件
        await this.uploadFile(this.currentDraft._id, uploadFile)
      }
      this.createModalConformLoading = true
      setTimeout(() => {
        this.createModalVisible = false
        this.createModalConformLoading = false
        this.$message.success('modify successfully!')
        this.getDrafts()
      }, 1000)
    },

    async uploadFile(draft_id, uploadFile){
      if(uploadFile!==undefined) {
        const data = new FormData()
        data.append('file', uploadFile.file)
        data.append('file_name', uploadFile.file.name)
        data.append('Content-Type', 'multipart/form-data')
        data.append('draftId', draft_id)
        let res = await DraftMetaService.upload(data)
        console.log(res)
      }
    },

    handleClickPreview(fid) {
      const pdfURL = this.$router.resolve({path: `/pdfpreview/${fid}`})
      window.open(pdfURL.href, '_blank')
    },

    async showRebuttalModal(item) {
      let process = await ReviewProcessService.getReviewProcessByDraftId(item._id)
      let rebuttalCont = ''
      if (process.status === 'rebuttal'){
        rebuttalCont = process.rebuttal
      }

      const textareaChange = (e) => {
        rebuttalCont = e.target.value
      }
      let that = this
      this.$confirm({
        title: 'Rebuttal',
        okType: 'danger',
        okText: 'Submit',
        content: (
          <a-textarea default-value={rebuttalCont} placeholder="Rebuttal Content" rows={10} onChange={textareaChange} />
        ),
        width: 800,
        async onOk() {
          console.log("wwww", rebuttalCont)

          await ReviewProcessService.rebuttal(process._id, rebuttalCont)
          that.$message.success('success submit rebuttal!')
        }
      })
    },

    showScoreModal(item) {
      const scores = [
        {
          score: -1,
          comment: 'kdlakldklsakdlksal',
          confidence: 0
        },
        {
          score: -2,
          comment: 'kdlakldklsakdlk909301sal',
          confidence: 1
        },
        {
          score: 1,
          comment: '90319039120ckjkjkl',
          confidence: -1
        }
      ]
      this.$info({
        title: 'Score',
        content: (
          scores.map((item, index) => (
            <div>
              <a-divider>PC{index+1}</a-divider>
              <div>
                <b>Score:</b> {item.score}
              </div>
              <div>
                <b>Confidence:</b> {item.confidence}
              </div>
              <div>
                <b>Comment:</b> {item.comment}
              </div>
            </div>
          ))
        ),
      })
    }
  },

  mounted() {
    this.getDrafts()
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
