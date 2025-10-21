<template>
  <!-- 科技档案 -->
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
          项目名称
        </template>
        <el-input
            v-model="form.projectName"
            style="width: 230px;"
            placeholder="请输入项目名称"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-number-1"></i>
          项目编号
        </template>
        <el-input
            v-model="form.projectNumber"
            style="width: 230px;"
            placeholder="请输入项目编号"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-building-line"></i>
          研发部门
        </template>
        <el-input
            v-model="form.researchDepartment"
            style="width: 230px;"
            placeholder="请输入研发部门"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-line"></i>
          技术领域
        </template>
        <el-select
            v-model="form.technicalField"
            placeholder="选择技术领域"
            style="width: 230px;"
        >
          <el-option
              v-for="item in technicalFields"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          研发时间
        </template>
        <el-date-picker
            v-model="form.researchTime"
            type="date"
            placeholder="选择研发时间"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          研发成果
        </template>
        <el-input
            v-model="form.researchAchievements"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入研发成果"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          技术特点
        </template>
        <el-input
            v-model="form.technicalFeatures"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入技术特点"
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