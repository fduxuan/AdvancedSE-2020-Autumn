<!--
   Created on 2020/12/9 10:46 上午

   @Author: fduxuan

   Desc: 系统通知，ws连接
 */
 -->
<template>
  <div>
    <a-list item-layout="vertical" :data-source="notifications">
      <a-list-item slot="renderItem" slot-scope="item, index">
        <!-------------------- 标题为 系统通知 ------------------->
        <a-card class="notification-card"
                :headStyle=headStyle
                title="System Notification">
          <span slot="extra" style="font-size: 50%">{{ item.created_at.slice(0,10) }}</span>
          <a-row>
            <!-------------------- 左三分之儿为通知消息详情 ------------------->
            <a-col :span="16">
              <p class="notification-title">System Notification Detail</p>
              <p>{{item.message}}</p>
            </a-col>

            <!-------------------- 右三分之一为操作 ------------------->
            <a-col :span="7" :offset="1">
              <p class="notification-title">Your Operator</p>

              <!-------------------- 标为已读 ------------------->
              <a-row v-if="item.status==='unread'">
                <a-popconfirm
                    title="Are you sure mark this notification to read?" ok-text="Confirm" cancel-text="Cancel"
                    @confirm="markReadOrUnread(item._id, index, 'read')">
                  <a-button class="notification-button" style="background: rgba(51,205,50,0.65)">Mark As Read</a-button>
                </a-popconfirm>
              </a-row>

              <!-------------------- 标为未读 ------------------->
              <a-row v-if="item.status==='read'">
                <a-popconfirm
                    title="Are you sure mark this notification to unread?" ok-text="Confirm" cancel-text="Cancel"
                    @confirm="markReadOrUnread(item._id, index, 'unread')">
                  <a-button class="notification-button" style="background: rgba(255,6,9,0.65)">Mark As Unread</a-button>
                </a-popconfirm>
              </a-row>
            </a-col>

          </a-row>
        </a-card>
      </a-list-item>
    </a-list>
  </div>

</template>

<script>
import NotificationService from "@/model/NotificationService";
export default {
    name: "SystemNotification",
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
        }
    },

    methods:{

      /* 将通知标为已读或者未读 */
      async markReadOrUnread(nid, index, status){
        await NotificationService.changeNotificationStatus(nid, status)
        this.$message.success(`success mark this notification to ${status}!`)
        this.$emit('handleNotiChange', index)
      },

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

