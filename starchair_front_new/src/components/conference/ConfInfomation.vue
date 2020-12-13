<template>
  <div>
    <a-spin :spinning="spinning">
      <a-descriptions title="Conference details" bordered>
        <a-descriptions-item label="Fullname" :span="2">
          {{conference_defalut.fullName}}
        </a-descriptions-item>
        <a-descriptions-item label="Abbr.">
          {{ conference_defalut.shortenForm }}
        </a-descriptions-item>
        <a-descriptions-item label="Location" :span="2">
          {{ conference_defalut.location }}
        </a-descriptions-item>
        <a-descriptions-item label="Status">
          <a-badge status="processing" :text="conference_defalut.status" />
        </a-descriptions-item>
        <a-descriptions-item label="Start Time">
          {{ conference_defalut.startTime && conference_defalut.startTime.slice(0, 10) }}
        </a-descriptions-item>
        <a-descriptions-item label="Stop Submitting Time">
          {{ conference_defalut.stopSubmittingTime && conference_defalut.stopSubmittingTime.slice(0, 10) }}
        </a-descriptions-item>
        <a-descriptions-item label="Publishing Time">
          {{ conference_defalut.publishingTime && conference_defalut.publishingTime.slice(0, 10) }}
        </a-descriptions-item>
        <a-descriptions-item label="Chariman">
          {{ conference_defalut.chairman && conference_defalut.chairman.fullname }}
        </a-descriptions-item>
        <a-descriptions-item label="Pcmembers" :span="2">
          <a-tag style="margin-top: 7px" v-for="member in conference_defalut.pcMembers" :key="member._id">
            {{member.fullname}}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="Topics" :span="3">
          <template v-for="(topic, index) in conference_defalut.topics">
            <a-tag :key="topic" :color="colors[index%colors.length]" style="margin: 5px 3px">
              {{topic}}
            </a-tag>
          </template>
        </a-descriptions-item>

      </a-descriptions>
    </a-spin>
  </div>
</template>

<script>
export default {
  name: "ConfInfomation",
  props:['conference'],
  data() {
    return {
      conference_defalut: {
        fullName: "ASE 2020: International Conference on Automated Software Engineering",
        shortenForm: "ASE",
        location: "Fudan University, Shanghai, China",
        startTime: "2020-11-28 17:00:00",
        stopSubmittingTime: "2020-11-29 17:00:00",
        publishingTime: "2020-11-30 17:00:00",
        chairman: "fduxuan",
        pcMembers: ['yiwen', 'lyf', 'ww', 'ljy'],
        topics: ['Cloud computing',
          'Human-computer interaction',
          'Component-based service-oriented systems',
          'Specification languages',
          'Configuration management'],
        status: "submitting",
      },

      spinning: false,
      colors: ['pink', 'red', 'blue', 'green', 'orange', 'purple'],
    }
  },

  methods:{

  },

  mounted(){
      this.conference_defalut = this.conference
  },

  watch:{
    conference(newValue,oldValue){
      this.spinning=true
      this.conference_defalut = newValue
      this.spinning=false
    },

  }

}
</script>

<style scoped>

</style>
