<template>
  <a-modal
    title="APPLY NEW CONFERENCE"
    okText="APPLY"
    cancelText="CANCEL"
    :visible="visible"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-form-model
      ref="createConfForm"
      :rules="formRules"
      :model="form"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 12 }" >
      <a-form-model-item label="Full name:" prop="fullName">
        <a-input
          v-model="form.fullName"
          placeholder="Please input your full name"
        />
      </a-form-model-item>

      <a-form-model-item label="Shorten form:" prop="shortenForm">
        <a-input
          v-model="form.shortenForm"
          placeholder="Please input your shorten form"
        />
      </a-form-model-item>

      <a-form-model-item label="Location:" prop="location">
        <a-input
          v-model="form.location"
          placeholder="Please input your location"
        />
      </a-form-model-item>

      <a-form-model-item label="Start time:" prop="startTime">
        <a-date-picker
          v-model="form.startTime"
          placeholder="Select start time"
          :disabledDate="startTimeDisable"
        />
      </a-form-model-item>

      <a-form-model-item label="Stop submitting time:" prop="stopSubmittingTime">
        <a-date-picker
          v-model="form.stopSubmittingTime"
          placeholder="Select stop submitting time"
          :disabledDate="stopSubmittingTimeDisable"
        />
      </a-form-model-item>

      <a-form-model-item label="Publishing Time:" prop="publishingTime">
        <a-date-picker
          v-model="form.publishingTime"
          placeholder="Select publishing time"
          :disabledDate="publishingTimeDisable"
        />
      </a-form-model-item>

      <a-form-model-item label="Topics:" prop="topics">
        <div>
          <template v-for="(tag, index) in form.topics">
            <a-tooltip v-if="tag.length > 20" :key="tag" :title="tag">
              <a-tag :key="tag" :closable="index !== -1" @close="() => handleClose(index)">
                {{ `${tag.slice(0, 20)}...` }}
              </a-tag>
            </a-tooltip>
            <a-tag v-else :key="tag" :closable="index !== -1" @close="() => handleClose(index)">
              {{ tag }}
            </a-tag>
          </template>
          <a-input
            v-if="inputVisible"
            ref="input"
            type="text"
            size="small"
            :style="{ width: '78px' }"
            :value="inputValue"
            @change="handleInputChange"
            @blur="handleInputConfirm"
            @keyup.enter="handleInputConfirm"
          />
          <a-tag v-else style="background: #fff; borderStyle: dashed;" @click="showInput">
            <a-icon type="plus" /> New Topic
          </a-tag>
        </div>
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
const formRules = {
  fullName: [{ required: true, message: 'Please input your full name', trigger: 'blur' }],
  shortenForm: [{ required: true, message: 'Please input your shorten form', trigger: 'blur' }],
  location: [{ required: true, message: 'Please input your location', trigger: 'blur' }],
  startTime: [{ required: true, message: 'Please select your start time', trigger: 'change' }],
  publishingTime: [{ required: true, message: 'Please select your publishing time', trigger: 'change' }],
  stopSubmittingTime: [{ required: true, message: 'Please select your stop submitting time', trigger: 'change' }],
  topics: [{ type: 'array', required: true, message: 'Please input your topics', trigger: 'change' }]
}

import moment from "moment"
export default {
  name: "CreateModel",
  props: {
    visible: {
      type: Boolean,
      default() {return false}
    },
    confirmLoading: {
      type: Boolean,
      default() {return false}
    }
  },
  data() {
    return {
      formRules,
      form: {
        topics: []
      },
		  inputVisible: false,
		  inputValue: '',
    }
  },
  methods: {
    handleOk() {
      this.$refs.createConfForm.validate(validate => {
        if (validate) {
          let formData = this.form
          const timeField = ['startTime', 'publishingTime', 'stopSubmittingTime']
          timeField.map(f => {
            formData[f] = formData[f].toISOString()
          })
          this.$emit('handleClickOk', formData)
          this.form = { topics: [] }
        }
      });
    },
    handleCancel() {
      this.$emit('handleClickCancel')
      this.form = { topics: [] }
    },
    showInput() {
      this.inputVisible = true;
      this.$nextTick(function() {
        this.$refs.input.focus();
      });
    },
    handleInputChange(e) {
      this.inputValue = e.target.value
    },
    handleInputConfirm() {
      const inputValue = this.inputValue
      let topics = this.form.topics
      if (inputValue && topics.indexOf(inputValue) === -1) {
        topics = [...topics, inputValue]
      }
      this.inputVisible = false
      this.inputValue = ''
      this.form = {
        ...this.form,
        topics
      }
    },
    handleClose(index) {
      let topics = this.form.topics
      topics.splice(index, 1)
      this.form = { ...this.form, topics }
    },


    // 时间验证
    startTimeDisable(current){
      let disabled = false
      if (current < moment().endOf('day').subtract(1, 'day')){  // 必须是今天之后的日期
        disabled = true
      }
      if (this.form.stopSubmittingTime && current > moment(this.form.stopSubmittingTime.endOf('day'))){  // 必须早于stopSubmittingTime
        disabled = true
      }
      if (this.form.publishingTime && current > moment(this.form.publishingTime.endOf('day'))){  // 必须早于publishingTime
        disabled = true
      }
      return disabled
    },


    stopSubmittingTimeDisable(current){
      let disabled = false
      if (current < moment().endOf('day').subtract(1, 'day')){  // 必须是今天之后的日期
        disabled = true
      }
      if (this.form.startTime && current < moment(this.form.startTime.endOf('day')).subtract(1, 'day')){ // 必须晚于开始时间
        disabled = true
      }
      if (this.form.publishingTime && current > moment(this.form.publishingTime.endOf('day'))){  // 必须早于publishingTime
        disabled = true
      }
      return disabled
    },
    publishingTimeDisable(current){
      let disabled = false
      if (current < moment().endOf('day').subtract(1, 'day')){  // 必须是今天之后的日期
        disabled = true
      }
      if (this.form.startTime && current < moment(this.form.startTime.endOf('day')).subtract(1, 'day')){ // 必须晚于开始时间
        disabled = true
      }
      if (this.form.stopSubmittingTime && current < moment(this.form.stopSubmittingTime.endOf('day')).subtract(1, 'day')){  // 必须晚于stopSubmitting时间
        disabled = true
      }
      return disabled

      
    }
  }
}
</script>

<style scoped>

</style>
