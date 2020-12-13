<template>
  <div>
    <p class="intab-title"> Invite Pcmembers </p>
    <a-form
        id="components-form-demo-validate-other"
        v-bind="formItemLayout"
        :form="form"
    >
      <a-form-item label="Find user">
        <a-input-search placeholder="input fullname" style="width: 400px"
                        v-decorator="['username']"
                        @search="handleSearch"
        />
      </a-form-item>
    </a-form>
    <!-- 分页设为四，用了trick的办法来遍历  -->
    <a-table :columns="columns"
             :data-source="users.slice(current*pageSize-pageSize, Math.min(current*pageSize, users.length))"
             rowKey="_id" :pagination="false">
      <span slot="action" slot-scope="text, record">

        <a-popconfirm
            title="Are you sure send this invitation?"
            ok-text="Yes"
            cancel-text="No"
            @confirm="handleInvite(record._id)">
          <a-button type="primary">
            Invite
          </a-button>
        </a-popconfirm>
      </span>
    </a-table>
    <br>
    <a-pagination style="text-align: right"
                  v-model="current"
                  :total="this.users.length"
                  :pageSize="pageSize"
                  show-less-items
                  @change="onChange"/>
  </div>
</template>

<script>
const columns = [
  {
    title: 'Username',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: 'Fullname',
    dataIndex: 'fullname',
    key: 'fullname',
  },

  {
    title: 'Email',
    dataIndex: 'email',
    key: 'email',
  },
  {
    title: 'Company',
    dataIndex: 'company',
    key: 'company',
  },
  {
    title: 'Area',
    dataIndex: 'area',
    key: 'area',
  },
  {
    title: 'Action',
    key: 'action',
    scopedSlots: { customRender: 'action' },
  },
];

import UserService from "@/model/UserService";
import InvitationService from "@/model/InvitationService";
export default {
  name: "MakeInvitation",
  props: ['cid', 'status'],
  data() {
    return {
      columns,
      users:[],
      current: 1,
      pageSize: 4,
      form: this.$form.createForm(this),
      formItemLayout: {
        labelCol: { span: 6 },
        wrapperCol: { span: 18 },
      },
    }
  },
  methods: {

    /*使用姓名搜索，支持模糊查询*/
    handleSearch(event) {
      this.form.validateFields(async(err, values) => {
        if (!err) {
          let username = values.username
          this.users = await UserService.findUserByName(username)
          this.current = 1
        }
      });
    },

    /*发送邀请*/
    async handleInvite(uid){
      if(this.status !== 'accept'){
        this.$message.error("You can not send invitation after staring submitting process!")
        return
      }
      let res = await InvitationService.create(this.cid, uid)
      this.$message.success('成功发送邀请！')
    },

    onChange(current) {
      this.current = current;
    },
  },

  async mounted(){
    this.users = await UserService.findUserByName()
  }
}
</script>

<style scoped>

</style>
