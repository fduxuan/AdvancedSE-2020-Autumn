<!--
   Created on 2020/12/2 3:39 下午

   @Author: fduxuan

   Desc:
 */
 -->
<template>
  <div>
    <a-list item-layout="vertical" :data-source="notifications">
      <a-list-item slot="renderItem" slot-scope="item, index">
        <!-------------------- 标题为 发给xxx的邀请，关于xxx会议 ------------------->
        <a-card class="notification-card"
                :headStyle=headStyle
                :title="'Invitation To '+item.user_info.fullname+' Of Conference '+item.conference_info.shortenForm">
          <span slot="extra" style="font-size: 50%">{{ item.created_at.slice(0,10) }}</span>
          <a-row>
            <!-------------------- 左三分之一为被邀请者信息 ------------------->
            <a-col :span="8">
              <p class="notification-title">Invitee Information</p>
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
            <!-------------------- 右三分之一为状态 ------------------->
            <a-col :span="7" :offset="1">
              <p class="notification-title">Current Status</p>
              <br>
              <a-row>
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
        init: 'Not Decided',
        accept: 'Accepted',
        reject: 'Rejected'
      }
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
