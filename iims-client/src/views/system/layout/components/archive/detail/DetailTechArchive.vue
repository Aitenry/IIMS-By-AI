<template>
  <!-- 科技档案详情 -->
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
            项目名称
          </div>
        </template>
        {{ form.projectName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-number-1"></i>
            </el-icon>
            项目编号
          </div>
        </template>
        {{ form.projectNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-building-line"></i>
            </el-icon>
            研发部门
          </div>
        </template>
        {{ form.researchDepartment || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-line"></i>
            </el-icon>
            技术领域
          </div>
        </template>
        {{ getOptionLabel(technicalFields, form.technicalField) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            研发时间
          </div>
        </template>
        {{ form.researchTime || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            研发成果
          </div>
        </template>
        {{ form.researchAchievements || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            技术特点
          </div>
        </template>
        {{ form.technicalFeatures || '-' }}
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
interface TechnologyForm {
  projectName: string
  projectNumber: string
  researchDepartment: string
  technicalField: string
  researchTime: string
  researchAchievements: string
  technicalFeatures: string
  remarks: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<TechnologyForm>({
  projectName: '',
  projectNumber: '',
  researchDepartment: '',
  technicalField: '',
  researchTime: '',
  researchAchievements: '',
  technicalFeatures: '',
  remarks: ''
})

// 技术领域选项
const technicalFields = ref<OptionItem[]>([
  { value: '1', label: '信息技术' },
  { value: '2', label: '生物技术' },
  { value: '3', label: '新材料' },
  { value: '4', label: '新能源' },
  { value: '5', label: '航空航天' },
  { value: '6', label: '海洋工程' },
  { value: '7', label: '智能制造' },
  { value: '8', label: '其他科技' }
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
        projectName: metadata.projectName || '',
        projectNumber: metadata.projectNumber || '',
        researchDepartment: metadata.researchDepartment || '',
        technicalField: metadata.technicalField || '',
        researchTime: metadata.researchTime || '',
        researchAchievements: metadata.researchAchievements || '',
        technicalFeatures: metadata.technicalFeatures || '',
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

