<!--
   Created on 2020/11/26 2:08 下午

   @Author: fduxuan

   Desc:
 */
 -->
<template>
    <div v-if="this.$store.state.user == null">
      <HomePage/>
    </div>
    <div style="padding:5%" v-else>
      <a-row>
        <h1>Notification</h1>
      </a-row>
      <br>
      <a-row type="flex" align="top">
        <a-col span="6" >
          <a-menu mode="inline"
                  :open-keys="openKeys"
                  @openChange="onOpenChange"
                  @click="handleMenuClick"
                  theme="dark"
                  style="width: 100%; text-align: left;border-radius: 10px; "
                  v-model="current_menu"
          >
            <!---------------------- 系统消息 -------------------->
            <a-sub-menu key="sub3">
              <span slot="title"><a-icon type="setting" /><span  style="font-size: 120%">System Notifications</span></span>
              <a-menu-item :key=10 class="menu-font">
                Unread
              </a-menu-item>
              <a-menu-item :key=11 class="menu-font">
                Read
              </a-menu-item>
            </a-sub-menu>

            <!---------------------- 收到的消息 -------------------->
            <a-sub-menu key="sub1"  >
              <span slot="title"><a-icon type="setting" /><span style="font-size: 120%">Received Invitations</span></span>
              <a-menu-item :key="item.key" class="menu-font" v-for="item in subMenu">
                {{ item.value }}
              </a-menu-item>
            </a-sub-menu>

            <!---------------------- 发出的消息 -------------------->
            <a-sub-menu key="sub2" >
              <span slot="title"><a-icon type="setting" /><span  style="font-size: 120%">Sent Invitations</span></span>
              <a-menu-item :key="item.key+3" class="menu-font" v-for="item in subMenu">
                {{ item.value }}
              </a-menu-item>
            </a-sub-menu>


          </a-menu>

        </a-col>

        <a-col :span="16" :offset="1">
          <div class="notification-list">
            <a-spin :spinning="spinning">
              <!---------------------- 使用三个component来控制三种类型的通知 -------------------->
              <ReceiveNotification keep-alive :notifications="notifications.slice(pageSize*current-pageSize, Math.min(pageSize*current, notifications.length))"
                                   v-if="current_mode===1" @handleNotiChange="handleNotiChange"></ReceiveNotification>
              <SentNotification :notifications="notifications.slice(pageSize*current-pageSize, Math.min(pageSize*current, notifications.length))"
                                v-else-if="current_mode===2"></SentNotification>
              <SystemNotification :notifications="notifications.slice(pageSize*current-pageSize, Math.min(pageSize*current, notifications.length))"
                                   v-if="current_mode===3" @handleNotiChange="handleNotiChange"></SystemNotification>
              <br>

              <!---------------------- 分页 -------------------->
              <a-pagination v-model="current"
                            :total="notifications.length"
                            :pageSize="pageSize"
                            @change="changePage"
                            show-less-items />
            </a-spin>
          </div>



        </a-col>
      </a-row>
    </div>

</template>

<script>
import InvitationService from "@/model/InvitationService";
import ReceiveNotification from "@/components/notification/ReceiveNotification";
import SentNotification from "@/components/notification/SentNotification";
import SystemNotification from "@/components/notification/SystemNotification";
import NotificationService from "@/model/NotificationService";
import HomePage from "@/view/HomePage";
export default {
    name: "Notification",
    components:{
      HomePage,
      ReceiveNotification, SentNotification, SystemNotification
    },
    data(){
        return{
          subMenu:[
            {key: 1, value: 'Not operated'},
            {key: 2, value: 'Accepted'},
            {key: 3, value: 'Rejected'},
          ],
          openKeys: ['sub3'],
          notifications: [],
          current: 1,
          pageSize: 3,
          current_menu: [10],
          spinning: false,
          current_status: "unread",
          current_mode: 3
        }
    },

    methods:{

      /* menu最上层开关 */
      onOpenChange(openKeys) {
        this.openKeys = openKeys
      },

      /* 分页 */
      changePage(current){
        this.current = current
      },

      /* 根据menu选择拉取不同数据 */
      async handleMenuClick(doc){
        this.current = 1
        this.current_status = [1, 4].includes(doc.key) ? 'init': this.current_status         // 是否为未操作
        this.current_status = [2, 5].includes(doc.key) ? 'accept': this.current_status       // 是否为已接受
        this.current_status = [3, 6].includes(doc.key) ? 'reject': this.current_status       // 是否为已拒绝
        this.current_status = doc.key === 10 ? 'unread': this.current_status                 // 是否为未读
        this.current_status = doc.key === 11 ? 'read': this.current_status                   // 是否为已读

        this.current_mode = [1, 2, 3].includes(doc.key) ? 1: this.current_mode    // 是否是拉取接受消息
        this.current_mode = [4, 5, 6].includes(doc.key) ? 2: this.current_mode    // 是否是拉取发送消息
        this.current_mode = [10, 11].includes(doc.key) ? 3: this.current_mode     // 是否是拉取系统消息

        await this.getNotifications()
      },

      /* 拉取信息的函数 */
      async getNotifications(){
        this.spinning = true
        if(this.current_mode === 1){ // 拉取收到的
          this.notifications = await InvitationService.received(this.current_status)
        }
        else if (this.current_mode === 2){ // 拉取发送的
          this.notifications = await InvitationService.sent(this.current_status)
        }
        else{ // 拉取系统消息
          this.notifications = await NotificationService.getNotifications(this.current_status)
        }
        this.spinning = false
      },

      handleNotiChange(index) {
        this.notifications.splice(index, 1)
      }

    },

    mounted(){
      this.getNotifications()
    },
}
</script>

<style scoped>
  .notification-list{
    background: #fffdf6;
    border-radius: 10px;
    padding:10px;
  }
  .menu-font {
    font-size:110%
  }

</style>
