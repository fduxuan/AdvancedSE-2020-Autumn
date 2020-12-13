<template>
  <div>
    <p class="intab-title"> Check Pcmembers </p>
    <p class="intab-subtitle"> 当前会议共有 {{data.length}} 位评审人</p>
    <a-list :grid="{ gutter: 16, column: 4 }" :data-source="data">
      <a-list-item slot="renderItem" slot-scope="item, index">
        <a-card :title="item.username">
          <p class="desc text-one-line"><b>email:</b> {{item.email}}</p>
          <p class="desc text-one-line"><b>company:</b> {{item.company}}</p>
          <p class="desc text-one-line"><b>area:</b> {{item.area}}</p>
        </a-card>
      </a-list-item>
    </a-list>
  </div>
</template>
<script>
const data = [
  {
    username: 'fduxuan',
    email: "2132@dfsd.com",
    company: "fudan",
    area: "china",
  },
  {
    username: 'sir',
    email: "sdfsdfad@qq.com",
    company: "fudan123",
    area: "Mars",
  },
  {
    username: 'zyz',
    email: "123@123.com",
    company: "fudan",
    area: "china",
  },
  {
    username: 'zry',
    email: "2132@dfsd.com",
    company: "fudan",
    area: "china",
  },
];
import UserService from "@/model/UserService";
export default {
  props:['pcMembers'],
  data() {
    return {
      data,
    };
  },

  methods:{

  },

  mounted(){
    this.data = this.pcMembers
  },

  watch:{
    pcMembers(newValue,oldValue){
      this.data = newValue
    }
  }
};
</script>
<style>
.desc {
  text-align: left;
  letter-spacing: 1px;
}
</style>
