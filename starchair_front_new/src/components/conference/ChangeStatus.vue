<template>
  <div>
    <p class="intab-title"> Change the Status of Conference </p>
    <a-steps direction="vertical" :current="current">
      <a-step>
        <div slot="title" class="in-step">
          <!------------------会议已被创建------------------------->
          <p>A Created Conference</p>
          <a-popconfirm title="Are you sure change status?" ok-text="Yes"  cancel-text="No" @confirm="changeStatus">
              <a-button v-if="current === 0" type="primary" >Start Submitting</a-button>
          </a-popconfirm>
        </div>
      </a-step>
      <a-step>
        <div slot="title" class="in-step">
          <!------------------投稿阶段------------------------->
          <p v-if="current === 1">Submitting...</p>
          <p v-else-if="current > 1">Submission Closed</p>
          <p v-else>This process has not begun yet.</p>
          <a-popconfirm title="Are you sure change status?" ok-text="Yes"  cancel-text="No" @confirm="changeStatus">
            <a-button v-if="current === 1" type="primary">
              Close Submission & Start Reviewing
            </a-button>
          </a-popconfirm>
        </div>
      </a-step>
      <a-step>
        <!------------------审稿阶段------------------------->
        <div slot="title" class="in-step">
          <p v-if="current === 2">Reviewing...</p>
          <p v-else-if="current > 2">Review Closed</p>
          <p v-else>This process has not begun yet.</p>
          <a-popconfirm title="Are you sure change status?" ok-text="Yes"  cancel-text="No" @confirm="changeStatus">
            <a-button v-if="current === 2" type="primary" >
              Start First Discussion
            </a-button>
          </a-popconfirm>
        </div>
      </a-step>
      <a-step>
        <!------------------第一次讨论------------------------->
        <div slot="title" class="in-step">
          <p v-if="current === 3">First Discussing...</p>
          <p v-else-if="current > 3">First Discussion Closed</p>
          <p v-else>This process has not begun yet.</p>
          <a-popconfirm title="Are you sure change status?" ok-text="Yes"  cancel-text="No" @confirm="changeStatus">
            <a-button v-if="current === 3" type="primary" >
              Close First Discussion & Close Review & Publish First Result
            </a-button>
          </a-popconfirm>
        </div>
      </a-step>
      <a-step>
        <!------------------发布第一轮结果------------------------->
        <div slot="title" class="in-step">
          <p v-if="current >= 4">First Result Published</p>
          <p v-else>This process has not begun yet.</p>
          <a-popconfirm title="Are you sure change status?" ok-text="Yes"  cancel-text="No" @confirm="changeStatus">
            <a-button v-if="current === 4" type="primary">
              Start Final Discussion & Review For Rebuttal
            </a-button>
          </a-popconfirm>
        </div>
      </a-step>
      <a-step>
        <!------------------第二轮讨论------------------------->
        <div slot="title" class="in-step">
          <p v-if="current === 5">Reviewing & Second Discussing....</p>
          <p v-else-if="current > 5">Second Discussion Closed</p>
          <p v-else>This process has not begun yet.</p>
          <a-popconfirm title="Are you sure change status?" ok-text="Yes"  cancel-text="No" @confirm="changeStatus">
            <a-button v-if="current === 5" type="primary">
              Publish Final Result
            </a-button>
          </a-popconfirm>
        </div>
      </a-step>
      <a-step>
        <!------------------最终结果发布------------------------->
        <div slot="title" class="in-step">
          <p v-if="current === 6">Final Reulst Published</p>
          <p v-else>This process has not begun yet.</p>
        </div>
      </a-step>
    </a-steps>
    <!------------------审核分布方式------------------------->
    <a-modal v-model="visible" title="Score" @ok="handleOk">
      <a-form :form="form">
        <a-form-item label="Distribution Method">
          <a-radio-group v-decorator="['distribution',{ rules: [{ required: true, message: 'Please choose at least one item!' }]}]">
            <a-radio :value="0">
              Distribution Mode 1
            </a-radio>
            <a-radio :value="1">
              Distribution Mode 2
            </a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

  </div>


</template>

<script>
import ConferenceMetaService from "@/model/ConferenceMetaService";
import ReviewProcessService from "@/model/ReviewProcessService";
export default {
  name: "SubmitDraft",
  props:['status', 'cid'],
  data() {
    return {
      current: 0,
      icons:['check-circle','right-circle','minus-circle'],
      visible: false,
      form: this.$form.createForm(this),
      status_map: {
        0: 'accept',
        1: 'submitting',
        2: 'reviewing',
        3: 'firstDiscussion',
        4: 'firstPublish',
        5: 'finalDiscussion',
        6: 'finalPublish'
      },
    }
  },
  methods: {
    async changeStatus() {
      // if current is submitting, ask for distribution method"
      if(this.current === 1){
        this.showModal();
      }
      else {
        await this.change()
      }
    },

    async change(){
      await ConferenceMetaService.changeStatus(this.cid, this.status_map[this.current + 1])
      this.current = this.current + 1;
      this.$message.success('success change Status')
    },

    showModal() {
      this.visible = true;
    },

    handleOk(e) {
      this.form.validateFields(async (err, values) => {
        if (!err) {
          console.log(values)
          this.form.resetFields();
          await ReviewProcessService.allocDraft(this.cid)
          this.$message.warning('分配稿件成功！')
          await this.change()

          this.visible = false;
        }
      });
    },
  },
  mounted() {
    for(let x in this.status_map){
      if (this.status_map[x] === this.status){
        this.current = parseInt(x)
      }
    }
  },
  watch:{
    status(newValue,oldValue){
      for(let x in this.status_map){
        console.log(x)
        if (this.status_map[x] === newValue){
          this.current = parseInt(x)
        }
      }
    }
  }
}
</script>

<style scoped>
.intab-container .ant-steps {
  width: auto;
  margin: 50px 100px;
}
.in-step {
  padding: 0 20px 20px 20px;
}
</style>
