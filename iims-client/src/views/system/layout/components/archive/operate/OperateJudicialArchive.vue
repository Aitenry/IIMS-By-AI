<template>
  <!-- 司法档案 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        class="margin-top"
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          案件编号
        </template>
        <el-input
            v-model="form.caseNumber"
            style="width: 230px;"
            placeholder="请输入案件编号"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-user-line"></i>
          当事人
        </template>
        <el-input
            v-model="form.parties"
            style="width: 230px;"
            placeholder="请输入当事人"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-building-line"></i>
          承办机构
        </template>
        <el-input
            v-model="form.handleOrganization"
            style="width: 230px;"
            placeholder="请输入承办机构"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-line"></i>
          案件类型
        </template>
        <el-select
            v-model="form.caseType"
            placeholder="选择案件类型"
            style="width: 230px;"
        >
          <el-option
              v-for="item in caseTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          案发时间
        </template>
        <el-date-picker
            v-model="form.caseTime"
            type="datetime"
            placeholder="选择案发时间"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          案件地点
        </template>
        <el-input
            v-model="form.caseLocation"
            style="width: 230px;"
            placeholder="请输入案件地点"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          案件描述
        </template>
        <el-input
            v-model="form.caseDescription"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入案件描述"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          备注说明
        </template>
        <el-input
            v-model="form.remarks"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入备注说明"
        />
      </el-descriptions-item>
    </el-descriptions>
  </el-scrollbar>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 定义 props
const props = defineProps<{
  metadata?: string
}>()

// 定义表单数据类型
interface JudicialForm {
  caseNumber: string
  parties: string
  handleOrganization: string
  caseType: string
  caseTime: string
  caseLocation: string
  caseDescription: string
  remarks: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<JudicialForm>({
  caseNumber: '',
  parties: '',
  handleOrganization: '',
  caseType: '',
  caseTime: '',
  caseLocation: '',
  caseDescription: '',
  remarks: ''
})

// 案件类型选项
const caseTypes = ref<OptionItem[]>([
  { value: '1', label: '民事案件' },
  { value: '2', label: '刑事案件' },
  { value: '3', label: '行政案件' },
  { value: '4', label: '知识产权案件' },
  { value: '5', label: '执行案件' },
  { value: '6', label: '破产案件' }
])

// 初始化档案元数据
const initArchiveMetadata = () => {
  if (props.metadata) {
    try {
      const metadata = JSON.parse(props.metadata)
      form.value = {
        caseNumber: metadata.caseNumber || '',
        parties: metadata.parties || '',
        handleOrganization: metadata.handleOrganization || '',
        caseType: metadata.caseType || '',
        caseTime: metadata.caseTime || '',
        caseLocation: metadata.caseLocation || '',
        caseDescription: metadata.caseDescription || '',
        remarks: metadata.remarks || ''
      }
    } catch (error) {
      console.error('解析元数据失败:', error)
    }
  }
}

// 组件挂载后初始化
onMounted(() => {
  initArchiveMetadata()
})

// 暴露给父组件的表单数据
defineExpose({
  form: form
})
</script>