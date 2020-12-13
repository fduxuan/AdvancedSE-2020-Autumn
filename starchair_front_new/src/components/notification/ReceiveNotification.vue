<!--
   Created on 2020/12/2 3:32 下午

   @Author: fduxuan

   Desc:
 */
 -->
<template>
    <div>
      <a-list item-layout="vertical" :data-source="notifications">
        <a-list-item slot="renderItem" slot-scope="item, index">
          <!-------------------- 标题为 来自xxx的邀请，关于xxx会议 ------------------->
          <a-card class="notification-card"
                  :headStyle=headStyle
                  v-if="item.user_info"
                  :title="'Invitation From '+item.user_info.fullname+' Of Conference '+item.conference_info.shortenForm">
            <span slot="extra" style="font-size: 50%">{{ item.created_at.slice(0,10) }}</span>
            <a-row>
              <!-------------------- 左三分之一为邀请者（主席）信息 ------------------->
              <a-col :span="8">
                <p class="notification-title">Inviter Information</p>
                <p>Name: {{item.user_info.fullname}}</p>
                <p>Email: {{item.user_info.email}}</p>
                <p>Company: {{item.user_info.company}}</p>
              </a-col>
              <!-------------------- 中三分之一为会议信息 ------------------->
              <a-col :span="8">
                <p class="notification-title">Conference Information</p>
                <p>Name: {{item.conference_info.fullName}}</p>
                <p>Location: {{item.conference_info.location}}</p>
              </a-col>
              <!-------------------- 右三分之一为操作 ------------------->
              <a-col :span="7" :offset="1">
                <p class="notification-title">Your Operator</p>
                <br>

                <a-row v-if="item.status==='init'">
                  <a-col :span="10">
                    <a-popconfirm
                        title="Are you sure approve this invitation?" ok-text="Confirm" cancel-text="Cancel"
                        @confirm="approve_invitation(item._id, index)">
                      <!-------------------- 接受 ------------------->
                      <a-button class="notification-button" style="background: rgba(51,205,50,0.65)">Accept</a-button>
                    </a-popconfirm>
                  </a-col>
                  <a-col :span="10">
                    <a-popconfirm
                        title="Are you sure reject this invitation?" ok-text="Confirm" cancel-text="Cancel"
                        @confirm="reject_invitation(item._id, index)">
                      <!-------------------- 拒绝 ------------------->
                      <a-button class="notification-button" style="background: rgba(255,6,9,0.65)">Reject</a-button>
                    </a-popconfirm>
                  </a-col>
                </a-row>

                <!-------------------- 已经操作的invitation ------------------->
                <a-row v-else>
                  <a-button :disabled="true" class="notification-button" style="background: rgba(96,148,96,0.65)">{{status_map[item.status]}}</a-button>
                </a-row>
              </a-col>

            </a-row>
          </a-card>
        </a-list-item>
      </a-list>
    </div>

</template>

<script>
import InvitationService from "@/model/InvitationService";
export default {
    name: "ReceiveNotification",
    components:{

    },
    props: {
      notifications: {
        type: Array,
        default() {return []}
      }
    },
    data(){
        return{
          headStyle: {"font-size": "150%", color: "#060930"},
          status_map: {
            accept: 'Accepted',
            reject: 'Rejected'
          }
        }
    },

    methods:{
      /* 接受邀请 */
      async approve_invitation(iid, index){
        await InvitationService.approve(iid)
        this.$message.success('success accept this invitation!')
        this.$emit('handleNotiChange', index)
      },

      /* 拒绝邀请 */
      async reject_invitation(iid, index){
        await InvitationService.reject(iid)
        this.$message.success('success reject this invitation!')
        this.$emit('handleNotiChange', index)
      }

    }
  }
</script>

<style scoped>

.notification-card{
  border-radius: 10px;
  margin:20px;
  text-align: left;

}

.notification-title{
  font-weight: bold;
  font-size: 110%;
  color:#16697a;
}
.notification-button{
  font-weight: bold;
  font-size: 120%;
  color: #faf6e9;

}
</style>
