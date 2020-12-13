<template>
  <div>
    <div v-if="this.conference.status==='accept'">
      <a-result status="403" title="Not Start" sub-title="This conference has not started submission yet">
        <template #extra>
          <a-button type="primary" @click="backHome">
            Back Home
          </a-button>
        </template>
      </a-result>
    </div>
    <div v-else-if="this.conference.status!=='submitting'">
      <a-result status="500" title="Submission Closed" sub-title="This conference has closed the submission">
        <template #extra>
          <a-button type="primary" @click="backHome">
            Back Home
          </a-button>
        </template>
      </a-result>
    </div>
    <a-form v-else
        id="components-form-demo-validate-other"
        v-bind="formItemLayout"
        @submit="handleSubmit"
        :form="form"
    >
      <span class="intab-title">Submit Draft</span>
      <a-form-item label="Title">
        <a-input v-decorator="['title',{ rules: [{ required: true, message: 'Please input the title!' }] },]"
                 placeholder="Title">
        </a-input>
      </a-form-item>
      <a-form-item label="Authors">
        <template v-for="(tag, index) in authors">
          <a-tooltip v-if="tag.length > 20" :key="tag" :title="tag">
            <a-tag :key="tag" :closable="true" @close="() => handleClose(tag)">
              {{ `${tag.slice(0, 20)}...` }}
            </a-tag>
          </a-tooltip>
          <a-tag v-else :key="tag" :closable="true" @close="() => handleClose(tag)">
            {{ tag }}
          </a-tag>
        </template>
        <a-input
            v-if="inputVisible"
            ref="input"
            type="text"
            size="small"
            :style="{ width: '78px' }"
            :value="inputAuthor"
            @change="handleInputChange"
            @blur="handleInputConfirm"
            @keyup.enter="handleInputConfirm"
        />
        <a-tag v-else style="background: #fff; borderStyle: dashed;" @click="showInput">
          <a-icon type="plus" /> New Tag
        </a-tag>
      </a-form-item>
      <a-form-item label="Topics">
        <a-checkbox-group v-decorator="['topics']">
          <template v-for="topic in conference_defalut.topics">
            <a-row  :key="topic" type="flex">
              <a-checkbox :value="topic">
                {{topic}}
              </a-checkbox>
            </a-row>
          </template>
        </a-checkbox-group>
      </a-form-item>
      <a-form-item label="Summary">
        <a-textarea
            v-decorator="['summary',{ rules: [{ required: true, message: 'Please input the summary!' }] }]"
            placeholder="Enter the summary of your draft"
            :auto-size="{ minRows: 3, maxRows: 20 }"
        />
      </a-form-item>
      <a-form-item label="Dragger">
        <div class="dropbox">
          <a-upload-dragger
              name="file"
              :customRequest="handleUploadFile"
              :file-list="fileList"

          >
            <p class="ant-upload-drag-icon">
              <a-icon type="inbox" />
            </p>
            <p class="ant-upload-text">
              Click or drag file to this area to upload
            </p>
            <p class="ant-upload-hint">
              Support for a single or bulk upload.
            </p>
          </a-upload-dragger>
        </div>
      </a-form-item>
      <a-form-item :wrapper-col="{ span: 12, offset: 6 }">
        <a-button type="primary" html-type="submit">
          Submit
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
import DraftMetaService from "@/model/DraftMetaService";
export default {
  name: "SubmitDraft",
  props: ['conference', 'visible', 'confirmLoading'],
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
        pcmembers: ['yiwen', 'lyf', 'ww', 'ljy'],
        topics: ['Cloud computing',
          'Human-computer interaction',
          'Component-based service-oriented systems',
          'Specification languages',
          'Configuration management'],
        status: "submitting"
      },
      form: this.$form.createForm(this),
      authors: ['zry', 'fduxuan', 'sir'],
      inputVisible: false,
      inputAuthor: "",
      formItemLayout: {
        labelCol: { span: 6 },
        wrapperCol: { span: 14 },
      },
      spinning: false,
      fileList: [],
      uploadFile: undefined,
    }
  },
  methods: {

    backHome(){
      this.$router.push({'path': '/conference'}).catch(e=>{})
    },

    handleSubmit(event) {
      event.preventDefault();
      this.form.validateFields(async (err, values) => {
        if (!err) {
          if (this.uploadFile === undefined){
            this.$message.error('no file attach!')
          }
          else{
            console.log(values)
            values['confId'] = this.conference._id
            let draft_id = await DraftMetaService.create(values)
            await this.upload(draft_id)
            this.$message.success('成功交稿！')
            this.form.resetFields()
          }
        }
      });

    },

    /*究极魔改只接受一个file*/
    async handleUploadFile(info){
      // 仅保留一个
      this.fileList = [info.file]
      this.uploadFile = info
    },

    async upload(draft_id){
      if(this.uploadFile!==undefined) {
        const data = new FormData()
        data.append('file', this.uploadFile.file)
        data.append('file_name', this.uploadFile.file.name)
        data.append('Content-Type', 'multipart/form-data')
        data.append('draftId', draft_id)
        let res = await DraftMetaService.upload(data)
        console.log(res)
      }
    },

    // 关于author tag的操作
    handleClose(removedTag) {
      const authors = this.authors.filter(author => author !== removedTag);
      console.log(authors);
      this.authors = authors;
    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick(function () {
        this.$refs.input.focus();
      });
    },

    handleInputChange(e) {
      this.inputAuthor = e.target.value;
    },

    handleInputConfirm() {
      const inputAuthor = this.inputAuthor;
      let authors = this.authors;
      if (inputAuthor && authors.indexOf(inputAuthor) === -1) {
        authors = [...authors, inputAuthor];
      }
      console.log(authors);
      Object.assign(this, {
        authors,
        inputVisible: false,
        inputAuthor: '',
      });
    },



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
