<!--
   Created on 2020/11/25 10:23 上午

   @Author: fduxuan

   Desc:  主页
 */
 -->
<template>
  <div>
    <a-row class="homepage-back">
      <h1 style="font-size: 500%">Welcome to StarChair</h1>
      <h1>A Platform Created for Scientists</h1>
      <br><br>
      <h3>admin: {admin: password}</h3>
      <a-button type="primary"  size="large"
                style="width: 30%; background: #1d1b1b"
                @click="getStart">
                GET  STARTED</a-button>
    </a-row>
    <a-row style="padding: 5%">
      <h1 style="color: #000">The best platform to contribute your paper.</h1>
      <br>
      <h3 style="color: #000">People from all the countries gather here.</h3>
      <br>
      <a-col :span="8"  v-for="item in card_content" :key="item.title" style="padding: 2%;height: 100%">
        <a-card :title="item.title" :bordered="false" height="100%" >
          <p class="text-more-line">{{ item.content }}</p>
        </a-card>
      </a-col>
    </a-row>

    <a-row class="homepage-foot">
      <a-col :span="6" :offset="4" >
        <h1>Services</h1>
        <p>Cras facilisis mi vitae nunc lobortis pharetra. Nulla volutpat tincidunt ornare.</p>
        <a-row>
          <h3><a-icon type="carry-out"/>  Publishing</h3>
          <h3><a-icon type="apple" />  Smart program</h3>
          <h3><a-icon type="team" />  Conference managing</h3>
        </a-row>
      </a-col>

      <a-col :span="6" :offset="4">
        <h1>Contact us</h1>
        <p>Cras facilisis mi vitae nunc lobortis pharetra. Nulla volutpat tincidunt ornare.</p>
        <a-row>
          <h3><a-icon type="phone" />  777-867-5309</h3>
          <h3><a-icon type="environment" />  Shanghai, CN</h3>
          <h3><a-icon type="mail" />  starchair@fudan.edu.cn</h3>
        </a-row>

      </a-col>

    </a-row>

  </div>
</template>

<script>
export default {
    name: "HomePage",
    components:{

    },
    data(){
        return{
          card_content:[
            {title: "About StarChair", content:"StarChair is a conference management system that is flexible, easy to use, and has many features to make it suitable for various conference models. It is currently probably the most commonly used conference management system. EasyChair was designed to help conference organisers to cope with the complexity of the refereeing process..."},
            {title: "Why StarChair", content:"You can use it for free!Management and monitoring of the program committee more easily! Sophisticated and flexible management of the access of PC members and referees to papers and conflicts of interests! Flexibility, EasyChair has been used for evaluating project proposals, teaching students paper writing and peer reviewing..."},
            {title:"News", content:"StarChair launched a call for papers service for conferences.\n" +
                  "Following the success of the StarChair EPiC Series...\n" +
                  "The design of EasyChair homepage changed.\n" +
                  "The design of EasyChair homepage changed.\n" +
                  "The design of EasyChair homepage changed." +
                  "The design of EasyChair homepage changed."}
          ]
        }
    },

    methods:{
      /* 跳转至conference页面或者提示登录*/
      getStart(){
        if(this.$store.state.user!=null){
          this.$router.push({path: '/conference'}).catch(e=>{})
        }
        else{
          this.$message.warning('please login first ！')
        }
      }
    },

    mounted(){

    },
}
</script>

<style scoped>
.homepage-back{
  width: 100%;
  background-image: url("../assets/bg.jpg");
  height: 695px;
  padding: 10%;
}
.homepage-foot{
  padding: 3%;
  height: 300px;
  background-image: url('../assets/bg2.jpg');
  color: #fff;
  font-weight: bold;
}
h1, h3{
  color: #fff;
}
</style>
