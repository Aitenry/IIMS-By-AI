<template>
  <!-- 司法档案详情 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            案件编号
          </div>
        </template>
        {{ form.caseNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-user-line"></i>
            </el-icon>
            当事人
          </div>
        </template>
        {{ form.parties || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-building-line"></i>
            </el-icon>
            承办机构
          </div>
        </template>
        {{ form.handleOrganization || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-line"></i>
            </el-icon>
            案件类型
          </div>
        </template>
        {{ getOptionLabel(caseTypes, form.caseType) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            案发时间
          </div>
        </template>
        {{ form.caseTime || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            案件地点
          </div>
        </template>
        {{ form.caseLocation || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            案件描述
          </div>
        </template>
        {{ form.caseDescription || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            备注说明
          </div>
        </template>
        {{ form.remarks || '-' }}
      </el-descriptions-item>
    </el-descriptions>
  </el-scrollbar>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 定义 props
const props = defineProps<{
  metadata?: string
  isReadOnly?: boolean
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

// 根据值获取选项标签
const getOptionLabel = (options: OptionItem[], value: string) => {
  const option = options.find(item => item.value === value)
  return option ? option.label : ''
}

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

