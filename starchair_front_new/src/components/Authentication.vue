<template>
  <div class="tab-container">
    <a-modal
      :visible="visible"
      title='SIGN IN/SIGN UP'
      okText='Confirm'
      @cancel="() => { $emit('cancel') }"
      @ok="() => { $emit('submit') }"
    >
    <a-tabs default-active-key="SIGN_IN" @change="FormTabChanged" size="small">
      <a-tab-pane tab="SIGN IN" key="SIGN_IN">
        <a-form layout='vertical' :form="login_form">
          <a-form-item>
            <a-input
              placeholder="Username"
              v-decorator="[
                'username',
                {
                  rules: [{ required: true, message: 'The username cannot be empty'}],
                }
              ]"
            />
          </a-form-item>
          <a-form-item>
            <a-input
              type='password'
              placeholder="Password"
              v-decorator="[
                'password',
                {
                  rules: [{ required: true, message: 'The password cannot be empty'}],
                }
              ]"
            />
          </a-form-item>
        </a-form>
      </a-tab-pane>
      <a-tab-pane tab="SIGN UP" key="SIGN_UP">
        <a-form layout="vertical" :form="register_form">
          <a-form-item>
            <a-input
              placeholder="Username"
              v-decorator="[
                'username',
                {
                  rules: [{validator: handleUsername}],
                }
              ]"
            />
          </a-form-item>
          <a-form-item>
            <a-input
            placeholder="Fullname"
            v-decorator="[
              'fullname',
              {
                rules: [{ required: true, message: 'The fullname cannot be empty'}],
              }
            ]"
            />
          </a-form-item>
          <a-form-item>
            <a-input
              type='password'
              placeholder="Password"
              v-decorator="[
                'password',
                {
                  rules: [{validator: handlePassword}],
                }
              ]"
            />
          </a-form-item>
          <a-form-item>
            <a-input
              type='password'
              placeholder="Confirm Password"
              v-decorator="[
                'confirm_password',
                {
                  rules: [{validator: handleConfirmPassword}],
                }
              ]"
            />
          </a-form-item>
          <a-form-item>
            <a-input
              type='email'
              placeholder="Mailbox"
              v-decorator="[
                'email',
                {
                  rules: [{validator: handleMailbox}],
                }
              ]"
            />
          </a-form-item>
          <a-form-item>
            <a-input
              placeholder="Company"
              v-decorator="[
                'company',
                {
                  rules: [{ required: true, message: 'The subordinate companies cannot be empty'}],
                }
              ]"
            />
          </a-form-item>
          <a-form-item>
            <a-input
              placeholder="Area"
              v-decorator="[
                'area',
                {
                  rules: [{ required: true, message: 'The area cannot be empty'}],
                }
              ]"
            />
          </a-form-item>
        </a-form>
      </a-tab-pane>
    </a-tabs>



    </a-modal>
  </div>
</template>

<script>
export default {
    name: "Authentication",
    props: ['visible'],
    beforeCreate() {
        this.login_form = this.$form.createForm(this, {name: 'login_form'});
        this.register_form  = this.$form.createForm(this, {name: 'register_form'})
    },
    components:{

    },
    data(){
        return{
          form_tab: 'SIGN_IN'
        }
    },

    methods:{
      FormTabChanged(key){
        console.log("Form changed to:", key);
        this.form_tab  = key;
      },
      handleUsername(rule, value, callback){
        if (!value.length){
          callback("The username cannot be empty");
        }
        else if (!/^[a-zA-Z][a-zA-Z0-9]{2,31}$/.test(value)){
          callback("The username can only begin with a letter and contain 3-32 characters of letters or numbers")
        }
        callback();
      },
      handlePassword(rule, value, callback){
        this.password = value;
        if (!value.length){
          callback("The password cannot be empty");
        }
        else if (value.length < 6){
          callback("The password must contain more than 6 characters");
        }
        callback();
      },
      handleConfirmPassword(rule, value, callback){
        if (this.password && this.password !== value){
          callback("The two input passwords are inconsistent!")
        }
        callback();
      },
      handleMailbox(rule, value, callback){
        if (!value.length){
          callback("The e-mail address cannot be empty")
        }
        else if (!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value)){
          callback("Please enter the correct email address");
        }
        callback();
      }
    },

    mounted(){

    },
}
</script>

<style scoped>
  .tab-container > .ant-modal > .ant-modal-content > .ant-modal-header {
    background: #494949;
    align-items: center;
    color: #fffdf6;
  }
</style>
