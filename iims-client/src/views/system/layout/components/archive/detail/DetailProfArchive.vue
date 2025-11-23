<template>
  <!-- 专业档案详情 -->
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
            专业名称
          </div>
        </template>
        {{ form.professionalName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-number-1"></i>
            </el-icon>
            专业编号
          </div>
        </template>
        {{ form.professionalNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-building-line"></i>
            </el-icon>
            专业领域
          </div>
        </template>
        {{ getOptionLabel(professionalFields, form.professionalField) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-line"></i>
            </el-icon>
            专业等级
          </div>
        </template>
        {{ getOptionLabel(professionalLevels, form.professionalLevel) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            获得时间
          </div>
        </template>
        {{ form.getTime || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            发证机构
          </div>
        </template>
        {{ form.issuingAuthority || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            专业描述
          </div>
        </template>
        {{ form.professionalDescription || '-' }}
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
interface ProfessionalForm {
  professionalName: string
  professionalNumber: string
  professionalField: string
  professionalLevel: string
  getTime: string
  issuingAuthority: string
  professionalDescription: string
  remarks: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<ProfessionalForm>({
  professionalName: '',
  professionalNumber: '',
  professionalField: '',
  professionalLevel: '',
  getTime: '',
  issuingAuthority: '',
  professionalDescription: '',
  remarks: ''
})

// 专业领域选项
const professionalFields = ref<OptionItem[]>([
  { value: '1', label: '工程技术' },
  { value: '2', label: '医学卫生' },
  { value: '3', label: '教育科学' },
  { value: '4', label: '文化艺术' },
  { value: '5', label: '经济管理' },
  { value: '6', label: '法律实务' },
  { value: '7', label: '农业技术' },
  { value: '8', label: '其他专业' }
])

// 专业等级选项
const professionalLevels = ref<OptionItem[]>([
  { value: '1', label: '初级' },
  { value: '2', label: '中级' },
  { value: '3', label: '高级' },
  { value: '4', label: '专家级' }
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
        professionalName: metadata.professionalName || '',
        professionalNumber: metadata.professionalNumber || '',
        professionalField: metadata.professionalField || '',
        professionalLevel: metadata.professionalLevel || '',
        getTime: metadata.getTime || '',
        issuingAuthority: metadata.issuingAuthority || '',
        professionalDescription: metadata.professionalDescription || '',
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

