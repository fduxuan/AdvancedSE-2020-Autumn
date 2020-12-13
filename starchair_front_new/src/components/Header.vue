<!--
   Created on 2020/11/26 10:25 上午

   @Author: fduxuan

   Desc: 所有页面的header
 */
 -->
<template>
    <div>
      <a-row type="flex" class="header" align="middle">
        <a-col :offset="1" :span="9" style="font-size: 35px; text-align: left; font-weight: bold">
          <a-row>
            <span class="homepage-icon" @click="goHomepage">
              StarChair
            </span>
          </a-row>
        </a-col>
        <a-col :span="10" :offset="4">
          <a-row type="flex" justify="end" align="middle" >

            <a-col class="header-button" v-if="this.$store.state.user!=null&&this.$store.state.user.username!=='admin'" @click="goConference">
              <a-icon type="bulb"/> Conference
            </a-col>

            <a-col :span="2" class="header-button" @click="goNotification" v-if="this.$store.state.user!=null&&this.$store.state.user.username!=='admin'">
              <a-badge dot style="font-size: 18px">
                <a-icon  type="bell"/>
              </a-badge>
            </a-col>

            <a-col :span="3" class="header-button"  v-if="this.$store.state.user!=null">
              <a-dropdown @click="e => e.preventDefault()">
                <span>
                  <a-avatar>{{ $store.state.user.username.substr(0, 1).toUpperCase() || 'USER' }}</a-avatar>
                  &nbsp;<a-icon type="down" />
                </span>
                <a-menu slot="overlay">
                  <a-menu-item v-if="this.$store.state.user.username!=='admin'" @click="goUserMenu">
                    <a-icon type="user" /> <span style="font-weight: bold">{{ $store.state.user.username|| 'USER' }}</span>
                  </a-menu-item>
                  <!-- <a-menu-item @click="() => { $router.push({path: '/discussion' }).catch(err => {}) }">
                    Discussion
                  </a-menu-item> -->
                  <a-menu-item @click="logout">
                    <a-icon type="logout" /> Logout
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </a-col>

            <a-col class="header-button" v-if="this.$store.state.user==null" @click="openSign">
              Sign In / Sign Up
            </a-col>

          </a-row>
        </a-col>
      </a-row>
      <Authentication
        id = "login_form"
        ref = "authenticationForm"
        :visible="visible"
        @cancel="handleCancel"
        @submit="handleSubmit"
      ></Authentication>


    </div>

</template>

<script>
import Authentication from "@/components/Authentication";
import UserService from "@/model/UserService";
export default {
    name: "Header",
    components:{
      Authentication,
    },
    data(){
        return{
          if_login: false,
          visible: false,
        }
    },

    methods:{
      goHomepage () {
        if (this.$route.path !== '/') {
          this.$router.push({path: "/" }).catch(err => {})
        }
      },
      goConference(){
        this.$router.push({path: "/conference" }).catch(err => {})
      },

      goNotification(){
        this.$router.push({path: "/notification" }).catch(err => {})
      },

      goUserMenu(){
        this.$message.warning('个人信息页')

      },

      openSign(){
        this.visible = true;
      },

      handleCancel() {
        this.visible = false;
      },

      openNotification(msg) {
        this.$notification.open({
          message: 'Notification',
          description: msg,
          icon: <a-icon type="smile" style="color: #108ee9" />
        })
      },

      handleSubmit() {
        console.log(this.$refs.authenticationForm.form_tab);
        const tab = this.$refs.authenticationForm.form_tab;
        let form;
        if (tab == "SIGN_IN"){
          form = this.$refs.authenticationForm.login_form;
        }else{
          form = this.$refs.authenticationForm.register_form;
        }

        form.validateFields(async (err, values) => {
          if (err) {
            return;
          }
          // console.log('Received values of form: ', values);
          // add login and register
          if(tab === 'SIGN_IN'){
            const res = await UserService.login(values)
            this.$message.success('success login!')
            form.resetFields()
            this.$store.commit('set_user', res)
            this.visible = false

            // open websocket
            if (this.$store.state.ws === null) {
              const ws = new WebSocket(`ws://${location.host}/ws/${res._id}`)
              this.$store.commit('set_ws', ws)

              ws.onopen = () => {
                console.log('WebSocket连接成功    状态码：' + ws.readyState)
              }

              ws.onclose = () => {
                console.log('WebSocket连接关闭')
              }

              ws.onmessage = (e) => {
                console.log("接收到WebSocket服务器消息: " + e.data)
                this.openNotification(e.data)
              }
            }
          }

          else {
            const res = await UserService.register(values)
            this.$message.success('success register!')
            form.resetFields();
            this.visible = false;
          }

        });
      },

      async logout(){
        await UserService.logout()
        this.$message.success('success logout')
        this.$store.commit('set_user', null)
        this.if_login=false
        this.$router.push({path: "/" }).catch(err => {})

        if (this.$store.state.ws !== null) {
          this.$store.state.ws.close()
          this.$store.commit('set_ws', null)
        }
      }

    },

    mounted(){

    },
}
</script>

<style scoped>
  .header{
    height: 70px;
    /*background: #494949;*/
    background: #1d1b1b;
    align-items: center;
    color: #fffdf6;
  }

  .header-button:hover{
    background: #333333;
  }
  .header-button{
    /* 圆角 focus变手*/
    border-radius: 30px;
    font-size: 20px;
    padding:5px 10px;
    cursor:pointer;
  }
  .ant-dropdown-menu {
    margin-top: 10px;
  }

  .homepage-icon:hover {
    cursor: pointer;
  }

</style>
