<template>
  <div>
    <el-dialog title="添加账号" :visible.sync="visible" width="30%">
      <el-form ref="ruleForm" :model="ruleForm" :rules="rules">
        <el-form-item label="URL" :label-width="formLabelWidth" prop="url">
          <el-input v-model.trim="ruleForm.url" autocomplete="off" style="width: 100%" />
        </el-form-item>
        <el-form-item label="用户名" :label-width="formLabelWidth" prop="username">
          <el-input v-model.trim="ruleForm.username" autocomplete="off" style="width: 100%" />
        </el-form-item>
        <el-form-item label="密码" :label-width="formLabelWidth" prop="password">
          <el-input v-model.trim="ruleForm.password" autocomplete="off" style="width: 100%">
            <el-button slot="append" icon="el-icon-setting" @click="onFormPasswordGenClick" />
          </el-input>
        </el-form-item>
        <el-form-item label="绑定手机" :label-width="formLabelWidth" prop="mobile">
          <el-input v-model.trim="ruleForm.mobile" autocomplete="off" style="width: 100%" />
        </el-form-item>
        <el-form-item label="绑定邮箱" :label-width="formLabelWidth" prop="email">
          <el-input v-model.trim="ruleForm.email" autocomplete="off" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" :label-width="formLabelWidth" prop="remark">
          <el-input v-model.trim="ruleForm.remark" autocomplete="off" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onFormSubmitClick('ruleForm')">立即创建</el-button>
        <el-button @click="onFormResetClick('ruleForm')">重置</el-button>
      </div>
    </el-dialog>
    <gen-password-dialog ref="genConfigRef" @callback="onGenConfigCallback" />
  </div>
</template>

<script>
import pwdInfoService from '@/api/vital/pwdInfo'
import { MessageUtil } from '@/utils/elementuiTool'
import GenPasswordDialog from '@/views/vital/pwdInfo/GenPasswordDialog'

export default {
  components: {
    GenPasswordDialog
  },
  data() {
    return {
      formLabelWidth: '100px',
      visible: false,
      ruleForm: {
        url: '',
        username: '',
        password: '',
        genConfig: '',
        mobile: '',
        email: '',
        remark: ''
      },
      rules: {
        url: [
          { required: true, message: '请输入URL', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '请输入备注', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
  },
  methods: {
    show() {
      this.ruleForm.url = ''
      this.ruleForm.username = ''
      this.ruleForm.password = ''
      this.ruleForm.genConfig = ''
      this.ruleForm.email = ''
      this.ruleForm.mobile = ''
      this.ruleForm.remark = ''
      this.visible = true
    },
    onFormPasswordGenClick() {
      const genConfigRef = this.$refs['genConfigRef']
      genConfigRef.show()
    },
    onGenConfigCallback(genVo, genRule) {
      this.ruleForm.password = genVo.content
      this.ruleForm.genConfig = genRule
    },
    onFormSubmitClick(formName) {
      const _this = this
      _this.$refs[formName].validate((valid) => {
        if (valid) {
          pwdInfoService.createPwdInfo(_this.ruleForm).then(res => {
            MessageUtil.success(_this, '操作成功')
            _this.$emit('onSuccessCallback')
            _this.visible = false
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    onFormResetClick(formName) {
      this.$refs[formName].resetFields()
    }
  }
}
</script>
