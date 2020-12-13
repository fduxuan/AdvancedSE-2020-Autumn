<!--
   Created on 2020/11/26 2:02 下午

   @Author: fduxuan

   Desc:
 */
 -->
<template>
    <div v-if="this.$store.state.user == null">
      <HomePage></HomePage>
    </div>
    <div style="padding:5%;" v-else>
      <!-- 标题 -->
      <a-row>
        <h1>
          Conferences
          <a-tooltip placement="bottom">
            <template slot="title">
              <span>Apply New Conference</span>
            </template>
            <a-button type="primary" shape="circle" icon="plus" @click="createModalVisible=true" />
          </a-tooltip>
        </h1>
      </a-row>

      <!-- 过滤器 -->
      <a-row type="flex" justify="center" align="middle">
        <a-col :span="12" style="text-align: left">
          <div>
            <a-radio-group default-value="all" button-style="solid">
              <a-radio-button :value="item.value" v-for="item in all_status" :key="item.value" @click="setQuery(item.value)">
                {{item.name}}
              </a-radio-button>
            </a-radio-group>
          </div>

          <div style="margin-top: 10px; position:relative">
            <div>
              <a-radio-group default-value="all_conference" @change="handleConfCateChange" button-style="solid">
                <a-radio-button value="all_conference">
                  所有会议
                </a-radio-button>
                <a-radio-button value="my_conference">
                  我的会议
                </a-radio-button>
              </a-radio-group>
            </div>

            <!-- 我的会议中的子筛选：作为主席/chairman/参与投稿 -->

            <div v-if="subcateVisible" style="margin-top: 15px;position:absolute;left: 86px">
              <div>
                <a-radio-group size="small" button-style="solid">
                  <a-radio-button :value="item.value" v-for="(item, iden_index) in all_identities" :key="item.value" @click="getConference(item.value, true, iden_index)">
                    {{ item.name }}
                  </a-radio-button>
                </a-radio-group>
              </div>
              <span :style="identities_index === 0 ? 'background-color: #494949;' : 'background-color: #fff;'" class="triangle"></span>
            </div>
          </div>

        </a-col>
        <!-- 搜索功能，模糊查询会议名称 -->
        <a-col :span="8" :offset="4">
          <a-row type="flex" justify="center" align="middle">
            <a-col :span="16" :offset="4"><a-input v-model="searchValue" placeholder="search" /></a-col>
            <a-col :span="4" style="font-size: 25px"><a-icon @click="searchConference" type="search" style="color: #B3AD9A"/></a-col>
          </a-row>
        </a-col>
      </a-row>

      <!-- 会议列表 -->
      <a-row style="margin-top: 65px">
        <a-spin tip="Loading..." :spinning="loading">

          <a-list :grid="{ gutter: 48, column: 3 }" :data-source="conferences.slice(pageSize*currentPage-pageSize, Math.min(pageSize*currentPage, conferences.length))" >
            <a-list-item slot="renderItem" slot-scope="item, index">
              <a-card :hoverable="true" class="conference_card" :title="item.shortenForm" :headStyle="headStyle">
                <h4 style="font-size: 110%;color:#495604" class="text-one-line"> {{item.fullName}}</h4>
                <p style="padding-top: 2%" class="text-one-line">Status : <span style="color: #1a564c; font-weight: bold">{{status_map[item.status]}}</span></p>

                <a-row class="tag-one-line">
                  <a-tag color="cyan" v-for="topic in item.topics.slice(0, Math.min(item.topics.length, 4))" :key="topic" style="margin-bottom: 2%; margin-top: 2%">{{topic}}</a-tag>
                </a-row>
                <div slot="actions">
                  <a-button class="text-one-line" type="primary"  round @click="goConferenceDetail(item._id)">More Info</a-button>
                </div>
              </a-card>
              <br><br>
            </a-list-item>
          </a-list>
        </a-spin>
      </a-row>

      <br><br><br>
      <a-pagination v-model="currentPage"
                    :total="conferences.length"
                    :pageSize="pageSize"
                    @change="changePage"
                    show-less-items />

        <create-modal
          :visible="createModalVisible"
          :confirmLoading="createModalConformLoading"
          @handleClickOk="handleClickCreateModalOk"
          @handleClickCancel="createModalVisible=false" />
    </div>

</template>

<script>

import CreateModal from '../components/conference/createModal.vue'
import ConferenceMetaService from "@/model/ConferenceMetaService";
import HomePage from "@/view/HomePage";

export default {
    name: "Conference",
    components:{
      HomePage,
      CreateModal
    },
    data(){
        return{
          loading: false,
          currentPage: 1,
          pageSize: 6,
          conferences: [],
          searchValue: "",
          createModalVisible: false,
          createModalConformLoading: false,
          subcateVisible: false,
          headStyle: {'font-size': '150%', 'font-family': "Microsoft YaHei"},
          all_status: [
            {name: "所有状态", value: 'all'},
            {name: "未开始", value: 'accept'},
            {name: '正在投稿', value: 'submitting'},
            {name: '审稿中', value: 'reviewing'}, // 其中，审稿中包括第一次讨论，第二次讨论，第一次结果发布
            {name: '已结束', value: 'finalPublish'}, // 二轮发布
          ],
          all_identities: [
            {name: "chairman", value: 'chairman'},
            {name: "pcmember", value: 'pcmember'},
            {name: "contributor", value: 'contributor'},
          ],
          identities_index: -1,
          status_map: {
            'init': 'Check Pending',
            'accept': 'Not Start Yet',
            'reject': 'Rejected',
            'submitting': 'Submitting',
            'reviewing': 'Reviewing',
            'firstDiscussion': 'First Discussion',
            'firstPublish': 'Published First Results',
            'finalDiscussion': 'Final Discussion',
            'finalPublish': 'Published Final Results'
          },
          query: {},
          current_id: "all_conference"
        }
    },

    methods:{
      /*所有会议 & 我的会议*/
      async handleConfCateChange(e) {
        let id = e.target.value
        if (e.target.value === 'all_conference') {
          this.identities_index = -1
        }
        this.subcateVisible = e.target.value === 'my_conference';
        this.searchValue = ""
        await this.getConference(id)
      },

      /*跳转至详情页面*/
      goConferenceDetail(id){
        this.$router.push({path: `/conference/${id}` }).catch(err => {})
      },

      /*键入关键字查询会议（会议名称或者简称，支持模糊查询）*/
      async searchConference(){
        if (this.searchValue !== ""){
          let query = {$or:[
              {shortenForm: {$regex: this.searchValue}},
              {fullName: {$regex: this.searchValue}}
            ]
          }
          Object.assign(this.query, query)
        }
        else{
          if('$or' in this.query){
            delete this.query['$or'];
          }
        }
        await this.getConference()
      },

      /*发起会议申请*/
      async handleClickCreateModalOk(value) {
        this.createModalConformLoading = true
        await ConferenceMetaService.create(value)
        await this.getConference()
        this.createModalVisible = false
        this.createModalConformLoading = false
        this.$message.success('create successfully!')
      },

      /*拉取会议内容*/
      async getConference(id=null, flag=false, iden_index=null){
        if (flag) {
          this.identities_index = iden_index
        }
        this.loading=true
        if(id != null){
          this.current_id = id
        }
        let query = this.query
        if(this.current_id === 'chairman'){
          this.conferences = await ConferenceMetaService.attendAsChairman(query)
        }
        if(this.current_id === 'my_conference') {
          this.conferences = await ConferenceMetaService.attendConference(query)
        }
        if(this.current_id === 'pcmember'){
          this.conferences = await ConferenceMetaService.attendAsPc(query)
        }
        if(this.current_id === 'all_conference'){
          this.conferences = await ConferenceMetaService.allConference(query)
        }
        if(this.current_id === 'contributor'){
          this.conferences = await ConferenceMetaService.attendAsContributor(query)
        }
        this.loading=false
      },

      /*上层的filter按钮选项*/
      async setQuery(id){
        let query = {}
        if(id === 'reviewing'){
          query = {status: {$in:['reviewing', 'firstDiscussion', 'firstPublish', 'finalDiscussion']}}
        }
        else if(id === 'all'){
          query = {}
        }
        else{
          query = {status: id}
        }
        this.query = query
        this.searchValue = ""
        await this.getConference()
      },

      changePage(page){
        this.currentPage = page
      },

    },

    mounted(){
      this.getConference()
    },
}
</script>


<style scoped>
.triangle {
  clip-path: polygon(0% 0%, 100% 100%, 0% 100%);
  transform: rotate(135deg);
  height: 10px;
  width: 10px;
  position: absolute;
  top: -4px;
  border: inherit;
  display: block;
  left: 12px;
  border-radius: 0 0 0 3px;
  border: 1px solid#d9d9d9;
}

.conference_card{
  background: #fffdf9;
  border-radius: 10px;
}
</style>
