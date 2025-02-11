<template>
  <el-dialog
    title="密码生成器"
    :visible.sync="genPasswordDialogFormVisible"
    width="30%"
    append-to-body
    @close-on-click-modal="false"
  >
    <el-form ref="genPasswordDialogFormRef" :model="genPasswordDialogFormData" :rules="genPasswordDialogFormDataRules">
      <el-form-item label="密码长度范围" :label-width="genPasswordDialogFormLabelWidth" prop="lengthRange">
        <el-slider
          v-model="genPasswordDialogFormData.lengthRange"
          range
          show-stops
          :min="8"
          :max="50"
          :step="1"
          @change="onLengthChange"
        />
      </el-form-item>
      <el-form-item label="包含字符" :label-width="genPasswordDialogFormLabelWidth" prop="includeType">
        <el-checkbox-group v-model="genPasswordDialogFormData.includeType" @change="onIncludeTypeChange">
          <el-checkbox
            v-for="includeTypeItem in includeTypeOptions"
            :key="includeTypeItem.value"
            :label="includeTypeItem.value"
          >{{ includeTypeItem.label+': '+includeTypeItem.rel }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="不包含的字符" :label-width="genPasswordDialogFormLabelWidth" prop="notIncludeSymbols">
        <el-input
          v-model.trim="genPasswordDialogFormData.notIncludeSymbols"
          autocomplete="off"
          clearable
          @input="onNotIncludeSymbolsChange"
        />
      </el-form-item>
      <el-form-item label="生成的密码长度" :label-width="genPasswordDialogFormLabelWidth" prop="defineLength">
        <el-slider
          v-model="genPasswordDialogFormData.defineLength"
          show-stops
          :min="genPasswordDialogFormData.lengthRange[0]"
          :max="genPasswordDialogFormData.lengthRange[1]"
          :step="1"
          @change="onDefineLengthChange"
        />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button
        v-prevent-re-click
        class="dialog-button"
        type="primary"
        @click="onSubmitBtnClick('genPasswordCriteriaRef')"
      >生成
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import pwdInfoService from '@/api/vital/pwdInfo'

const defaultForm = {
  lengthRange: [8, 50],
  includeType: ['symbols', 'numbers', 'lower_letters', 'upper_letters'],
  notIncludeSymbols: '()~{[}}!,',
  defineLength: 8
}

export default {
  data() {
    return {
      includeTypeOptions: [
        { label: '标点符号', value: 'symbols', rel: '~!@#$%^&*()-=_+[],.?;:' },
        { label: '数字', value: 'numbers', rel: '0-9' },
        { label: '小写字母', value: 'lower_letters', rel: 'a-z' },
        { label: '大写字母', value: 'upper_letters', rel: 'A-Z' }
      ],
      // 表单组件(弹出层)
      genPasswordDialogFormVisible: false,
      genPasswordDialogFormLabelWidth: '120px',
      genPasswordDialogFormData: { ...defaultForm },
      genPasswordDialogFormDataRules: {
        lengthRange: [
          { required: true, message: '请选择密码的长度', trigger: 'change' }
        ],
        includeType: [
          { required: true, message: '请选择包含的字符', trigger: 'change' }
        ],
        defineLength: [
          { required: true, message: '请选择生成的密码的长度', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    onLengthChange(val) {
      this.genPasswordDialogFormData.lengthRange = val
    },
    onIncludeTypeChange(val) {
      this.genPasswordDialogFormData.includeType = val
    },
    onNotIncludeSymbolsChange(val) {
      this.genPasswordDialogFormData.notIncludeSymbols = val
    },
    onDefineLengthChange(val) {
      this.genPasswordDialogFormData.defineLength = val
    },
    onSubmitBtnClick() {
      const _this = this
      _this.$refs['genPasswordDialogFormRef'].validate((valid) => {
        if (valid) {
          const genRule = _this.genPasswordDialogFormData
          pwdInfoService.generatePassword(genRule).then(genVo => {
            _this.genPasswordDialogFormVisible = false
            // NotificationUtil.success(_this, '已生成')
            _this.$emit('callback', genVo, genRule)
          }).catch(err => {
            console.error(err)
          })
        } else {
          console.error('校验不通过')
          return false
        }
      })
    },
    show() {
      this.genPasswordDialogFormVisible = true
    },
    hide() {
      this.genPasswordDialogFormVisible = false
    }
  }
}
</script>

<style scoped>

</style>
