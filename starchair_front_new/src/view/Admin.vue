<!--
   Created on 2020/11/28 11:22 上午

   @Author: fduxuan

   Desc:

 -->
<template>
    <div v-if="this.$store.state.user == null">
      <HomePage></HomePage>
    </div>
  <div v-else-if="!this.$store.state.user.admin">
      非管理员不可访问
  </div>
    <div style="padding:5%;" v-else>
      <a-row>
        <h1>
          Audit Conferences
        </h1>
      </a-row>

      <a-row style="margin-top: 65px">
        <a-spin tip="Loading..." :spinning="loading">

          <a-list :grid="{ gutter: 48, column: 3 }" :data-source="conferences.slice(pageSize*currentPage-pageSize, Math.min(pageSize*currentPage, conferences.length))" >
            <a-list-item slot="renderItem" slot-scope="item, index">
              <a-card class="conference_card" :title="item.shortenForm" :headStyle="headStyle">
                <h4 style="font-size: 110%;color:#495604" class="text-one-line"> {{item.fullName}}</h4>

                <a-row class="tag-one-line" style="margin-top: 20px">
                  <a-tag color="cyan" v-for="topic in item.topics" :key="topic" >{{topic}}</a-tag>
                </a-row>
                <div slot="extra">
                  <a-tooltip>
                    <template slot="title">
                      Conference Detail
                    </template>
                    <a-button icon="info" shape="circle" @click="showConferenceDetail(item._id)"></a-button>
                  </a-tooltip>
                </div>

                <div slot="actions">
                  <a-popconfirm
                      title="Are you sure approve this new conference?"
                      ok-text="Yes"
                      cancel-text="No"
                      @confirm="approveConference(item._id)">
                    <a-button style="color: green;"><span>Pass</span></a-button>
                  </a-popconfirm>
                  <a-popconfirm
                      title="Are you sure reject this new conference?"
                      ok-text="Yes"
                      cancel-text="No"
                      @confirm="rejectConference(item._id)">
                    <a-button style="color: red;margin-left:20px"><span>Reject</span></a-button>
                  </a-popconfirm>
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
    </div>

</template>

<script>
import ConferenceMetaService from "@/model/ConferenceMetaService";
import ConfInfomation from "@/components/conference/ConfInfomation";
import HomePage from "@/view/HomePage";
export default {
    name: "Admin",
    components:{
      ConfInfomation, HomePage
    },
    data(){
        return{
          loading: false,
          conferences: [],
          currentPage: 1,
          pageSize: 6,
          subcateVisible: false,
          headStyle: {'font-size': '30px', 'font-family': "Microsoft YaHei"},
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
            {name: "contributor", value: 'c'},
          ],

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
          current_id: "all_conference",
          confDetail: {}
        }
    },

    methods:{

      async showConferenceDetail(id){
        const confDetail = await ConferenceMetaService.getConferenceById(id)
        console.log(confDetail)
        this.confDetail = confDetail
        this.$info({
          content: (
            <ConfInfomation conference={confDetail} />
          ),
          onOk() {},
          width: 1000
        })
      },

      /*拉取会议内容*/
      async getUncheckedConference(){
        this.loading=true
        this.conferences = await ConferenceMetaService.getUncheckedConference()
        this.loading=false
      },

      // approve conference
      async approveConference(id=null){
        await ConferenceMetaService.approveConference(id)
        await this.getUncheckedConference()
        this.$message.success('success pass this new conference!')
      },

      //reject conference
      async rejectConference(id=null){
        await ConferenceMetaService.rejectConference(id)
        await this.getUncheckedConference()
        this.$message.success('success reject this new conference!')
      },

      changePage(e){
        this.currentPage = e
      }
    },

    mounted(){
      this.getUncheckedConference()
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
  background-color: #494949;
  border-radius: 0 0 0 3px;
  border: 1px solid #494949;
}

.conference_card{
  background: #fffdf9;
  border-radius: 10px;
}
</style>
