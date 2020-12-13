<template>
  <div id="app">
    <Header></Header>

    <div class="my-content" id="rou">
      <Admin v-if="$store.state.user!=null&&$store.state.user.username==='admin'" />
      <router-view v-else></router-view>
    </div>
    <Footer></Footer>

  </div>
</template>

<script>
import Header from './components/Header.vue'
import Footer from "./components/Footer";
import Admin from './view/Admin.vue';
import UserService from "./model/UserService";

export default {
  name: 'App',
  components: {
    Header,
    Footer,
    Admin
  },
  methods: {
    openNotification(msg) {
      this.$notification.open({
        message: 'Notification',
        description: msg,
        icon: <a-icon type="smile" style="color: #108ee9" />
      })
    }
  },
  mounted(){
    // 全局session判断登录函数
    UserService.getInfoInInit().then(d=>{
      this.$store.commit('set_user', d)

      if (this.$store.state.ws === null) {
        const ws = new WebSocket(`ws://106.14.244.24:5007/ws/${d._id}`)
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

    }).catch(e=>{
      this.$store.commit('set_user', null)
    })

  },
  beforeDestroy() {
    if (this.$store.state.ws !== null) {
      this.$store.state.ws.close()
      this.$store.commit('set_ws', null)
    }
  }

}
</script>

<style>
#app {
  /* font-family: Avenir, Helvetica, Arial, sans-serif; */
  font-family:'Noto Sans TC', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  height: 100%;
}

.my-content{
  background: #ece8d9;
  min-height: calc(100% - 70px);
}
</style>
