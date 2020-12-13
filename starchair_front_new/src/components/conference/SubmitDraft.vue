<template>
  <a-modal
    :title="this.modalTitle"
    okText="SUBMIT"
    cancelText="CANCEL"
    :visible="visible"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel">
    <div>
        <!-- <div v-if="this.conference.status==='accept'">
        <a-result status="403" title="Not Start" sub-title="This conference has not started submission yet">
            <template #extra>
            <a-button type="primary" @click="backHome">
                Back Home
            </a-button>
            </template>
        </a-result>
        </div> -->
        <div v-if="this.conference.status!=='submitting'">
        <a-result status="500" title="Submission Closed" sub-title="This conference has closed the submission">
            <template #extra>
            <a-button type="primary" @click="backHome">
                Back Home
            </a-button>
            </template>
        </a-result>
        </div>

    <a-form-model v-else
      ref="createSubmissionForm"
      :rules="formRules"
      :model="form"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 12 }" >
      <a-form-model-item label="Title:" prop="title">
        <a-input
          v-model="form.title"
          placeholder="Please input your Title"
        />
      </a-form-model-item>

      <a-form-model-item label="Authors:" prop="authors">
        <div>
          <template v-for="(tag, index) in form.authors">
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
            :value="inputAuthor"
            @change="handleInputChange"
            @blur="handleInputConfirm"
            @keyup.enter="handleInputConfirm"
          />
          <a-tag v-else style="background: #fff; borderStyle: dashed;" @click="showInput">
            <a-icon type="plus" /> New Author
          </a-tag>
        </div>
      </a-form-model-item>


      <a-form-model-item label="Topics:" prop="topics">
        <a-checkbox-group v-model="form.topics">
          <template v-for="topic in conference.topics">
            <a-row  :key="topic" type="flex">
              <a-checkbox :value="topic">
                {{topic}}
              </a-checkbox>
            </a-row>
          </template>
        </a-checkbox-group>
      </a-form-model-item>

      <a-form-model-item label="Summary:" prop="summary">
        <a-textarea
            v-model="form.summary"
            placeholder="Enter the summary of your draft"
            :auto-size="{ minRows: 3, maxRows: 20 }"
        />
      </a-form-model-item>

      <a-form-model-item label="Original File:" v-if="this.modalType==='modify'">
        <a-button icon="eye" @click="handleClickPreview()">Preview</a-button>
      </a-form-model-item>

      <a-form-model-item label="Submit File" prop="fileList">
          <div class="dropbox">
          <a-upload-dragger
              name="file"
              :customRequest="handleUploadFile"
              :file-list="fileList"
              :remove="remove"

          >
            <p class="ant-upload-drag-icon">
              <a-icon type="inbox" />
            </p>
            <p class="ant-upload-text">
              Click or drag file to this area to upload
            </p>
            <p class="ant-upload-hint">
              You can only submit one PDF file
            </p>
          </a-upload-dragger>
        </div>
      </a-form-model-item>
    </a-form-model>
    </div>
  </a-modal>
</template>

<script>
const formRules = {
  title: [{ required: true, message: 'Please input the title!', trigger: 'blur' }],
  summary: [{ required: true, message: 'Please input the summary!', trigger: 'blur'}],
  authors: [{ type: 'array', required: true, message: 'Please input the authors!', trigger: 'blur' }],
  topics: [{ type: 'array', required: true, message: 'Please input the topics!', trigger: 'change' }],
}


export default {
  name: "SubmitDraft",
  props: {
    visible: {
      type: Boolean,
      default() {return false}
    },
    confirmLoading: {
      type: Boolean,
      default() {return false}
    },
    conference: {
    },
    modalType: {
    },
    modalTitle: {},
    modalForm: {},
    currentDraft: {},
  },
  data() {
    return {
      formRules,
      form: this.modalForm,
      inputVisible: false,
      inputAuthor: '',
      fileList: [],
      uploadFile: undefined,
    }
  },
  methods: {
    handleOk() {
      this.$refs.createSubmissionForm.validate(validate => {
        if (validate) {
          if (this.modalType == 'create' && this.uploadFile === undefined){
            this.$message.error('no file attached!')
          }
          else{
              let formData = this.form
              formData['confId'] = this.conference._id
              formData['uploadFile'] = this.uploadFile
              console.log(formData.uploadFile)
              console.log('submission form', formData)
              this.$emit('handleClickOk', formData)
              this.fileList = []
              this.uploadFile = undefined
          }
        }
      });
    },
    handleCancel() {
      this.$emit('handleClickCancel')
      this.fileList = []
      this.uploadFile = undefined
    },

    // 上传/预览文件操作
    /*究极魔改只接受一个file*/
    async handleUploadFile(info){
      // 仅保留一个
      if (info.file.type !== 'application/pdf'){
        this.$message.error('You can only upload PDF file!');
        return
      }
      this.fileList = [info.file]
      this.uploadFile = info
      console.log(this.uploadFile)

    },

    remove(){
      this.fileList = []
      this.uploadFile = undefined
    },

    async handleClickPreview() {
      console.log(this.currentDraft);
      const fid = this.currentDraft.file_id
      const pdfURL = this.$router.resolve({path: `/pdfpreview/${fid}`})
      window.open(pdfURL.href, '_blank')
    },


    // 关于author tag的操作
    showInput() {
      this.inputVisible = true;
      this.$nextTick(function() {
        this.$refs.input.focus();
      });
    },

    handleInputChange(e) {
      this.inputAuthor = e.target.value
    },
    handleInputConfirm() {
      const inputAuthor = this.inputAuthor
      let authors = this.form.authors
      if (inputAuthor && authors.indexOf(inputAuthor) === -1) {
        authors = [...authors, inputAuthor]
      }
      this.inputVisible = false
      this.inputAuthor = ''
      this.form = {
        ...this.form,
        authors
      }
    },
    handleClose(index) {
      let authors = this.form.authors
      authors.splice(index, 1)
      this.form = { ...this.form, authors }
    }
  },
  mounted() {
  },
  watch:{
    modalForm(newValue,oldValue){
      this.form = newValue
    },

  }
}
</script>

<style scoped>

</style>
