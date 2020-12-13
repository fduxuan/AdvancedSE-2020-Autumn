<template>
  <div v-if="this.$store.state.user == null">
    <HomePage/>
  </div>
  <div style="padding:5%;" v-else>
    <div class="card-container">
      <a-tabs type="card" tabPosition="left" defaultActiveKey="info" @change="handleChangeTab">
        <!------------------- 所有人可见 ------------------>
        <a-tab-pane key="info"  tab="详细信息">
          <div class="intab-container">
            <ConfInfomation :conference="conference" />
          </div>
        </a-tab-pane>
        <!-- ----------------- chairman和pcmember不可见 ----------------
        <a-tab-pane key="submit" v-if="!pcAuth && acceptAuth"  tab="提交稿件">
          <div class="intab-container">
            <SubmitDraft :conference="conference"></SubmitDraft>
          </div>
        </a-tab-pane> -->
        <!------------------- chairman可见 ------------------>
        <a-tab-pane key="invitation" v-if="chairmanAuth && acceptAuth"  tab="邀请审稿人">
          <div class="intab-container">
            <MakeInvitation :cid="cid" :status="conference.status"/>
          </div>
        </a-tab-pane>
        <!------------------- chairman和pcmember可见 ------------------>
        <a-tab-pane key="pcmembers" v-if="pcAuth && acceptAuth" tab="查看审稿人">
          <div class="intab-container">
            <CheckPcmembers :pcMembers="conference.pcMembers"/>
          </div>
        </a-tab-pane>
        <!------------------- chairman和pcmember可见 ------------------>
        <a-tab-pane key="review" v-if="pcAuth && acceptAuth"  tab="审稿">
          <div class="intab-container">
            <Review :status="conference.status" :cid="conference._id"/>
          </div>
        </a-tab-pane>
        <!------------------- chairman可见 ------------------>
        <a-tab-pane key="changeStatus" v-if="chairmanAuth && acceptAuth" tab="改变会议状态">
          <div class="intab-container">
            <ChangeStatus :status="conference.status" :cid="cid"/>
          </div>
        </a-tab-pane>
        <!------------------- chairman和pcmember不可见 ------------------>
        <a-tab-pane key="myDrafts" v-if="!pcAuth && acceptAuth"  tab="我的稿件">
          <div class="intab-container">
            <MyDrafts :cid="cid" :status="conference.status" :conference="conference"/>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>

</template>
<script>
import MakeInvitation from "../components/conference/MakeInvitation";
import ConfInfomation from "../components/conference/ConfInfomation";
import SubmitDraft from "../components/conference/SubmitDraft";
import CheckPcmembers from "@/components/conference/CheckPcmembers";
import Review from "@/components/conference/Review";
import ChangeStatus from "@/components/conference/ChangeStatus";
import ConferenceMetaService from "@/model/ConferenceMetaService";
import MyDrafts from "@/components/conference/MyDrafts";
import UserService from "@/model/UserService";
import HomePage from "@/view/HomePage";

export default {
  components: {HomePage, ChangeStatus, CheckPcmembers, ConfInfomation, MakeInvitation, SubmitDraft, Review, MyDrafts},
  data() {
    return {
      conference: {},
      cid: this.$route.params.cid,
      operation: ["info", "submit", 'invitation', "pcmembers", 'review','changeStatus', 'myDrafts'],
      submitTab: {
        formItemLayout: {
          labelCol: { span: 6 },
          wrapperCol: { span: 14 },
        },
        authors:['fduxuan','sir','zry'],
      },
      chairmanAuth: false,
      pcAuth: false,
      acceptAuth: true
    };
  },
  methods: {
    async handleChangeTab() {
      await this.getConferenceDetail()
    },

    /*拉取会议详情*/
    async getConferenceDetail(){
      this.conference = await ConferenceMetaService.getConferenceById(this.cid)
      console.log(this.conference)
      this.setAuth()
    },

    /*
     * 权限控制显示tab函数，在进入detail时触发
     * 通过chairmanAuth, pcAuth两个参数来控制显示页面
     */
    setAuth(){
      let userId = this.$store.state.user._id
      // 正在等待审核或者正在被拒绝
      this.acceptAuth = ['init', 'reject'].indexOf(this.conference.status)=== -1

      // 是否为chairman
      this.chairmanAuth = this.conference.chairman._id === userId
      // 是否为pcmember(chairman本身为pcmember）
      this.pcAuth = this.checkIfPc(userId, this.conference.pcMembers)
    },

    // 当前pcMemebers是一个元素为对象的数组
    checkIfPc(userId, pcMembers){
      for(let i = 0; i < pcMembers.length; i++){
        if(pcMembers[i]["_id"] === userId){
          return true
        }
      }
      return false
    }

  },
  async mounted()  {
    await this.getConferenceDetail()
  }
};
</script>
<style>
.intab-container {
  min-height: 500px;
}
.card-container {
  background: #ece8d9;
}
/* tab内容 */
.card-container > .ant-tabs-card > .ant-tabs-content {
  margin-top: -16px;
  padding-left: 0px;
}

.card-container > .ant-tabs-card > .ant-tabs-content > .ant-tabs-tabpane {
  background: #fff;
  padding: 24px;
}

/* tab表头整体 */
.card-container > .ant-tabs-card > .ant-tabs-bar {
  background: transparent;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab {
  border-color: transparent;
  background: #faf6e9;
  margin-right: 0;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab-active {
  border-color: transparent;
  background: #fff;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab {
  text-align: center;
}

.intab-title {
  font-size: 24px;
  font-weight: bold;
  margin: 30px;
  display: block;
}
.intab-subtitle {
  font-size: 16px;
  margin: 30px;
  display: block;
}
</style>
