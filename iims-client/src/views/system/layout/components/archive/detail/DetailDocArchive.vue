<template>
  <!-- 文书档案详情 -->
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
              <i class="ri-info-card-line"></i>
            </el-icon>
            主送机关
          </div>
        </template>
        {{ form.mainOrgan || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-info-card-line"></i>
            </el-icon>
            抄送机关
          </div>
        </template>
        {{ form.sendOrgan || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-paper-line"></i>
            </el-icon>
            机关署名
          </div>
        </template>
        {{ form.publishOrigin || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-alarm-warning-line"></i>
            </el-icon>
            紧急程度
          </div>
        </template>
        {{ getOptionLabel(urgentLevels, form.urgentLevel) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-bring-to-front"></i>
            </el-icon>
            发送范围
          </div>
        </template>
        {{ getOptionLabel(sendRanges, form.sendRange) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-page-separator"></i>
            </el-icon>
            档案页数
          </div>
        </template>
        {{ form.pageNum !== undefined ? form.pageNum : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-archive-stack-line"></i>
            </el-icon>
            文件载体
          </div>
        </template>
        {{ getCarrierLabels(form.carrier).join(', ') || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-info-card-line"></i>
            </el-icon>
            附件说明
          </div>
        </template>
        {{ form.enclosureNotation || '-' }}
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
interface DocumentForm {
  mainOrgan: string
  sendOrgan: string
  publishOrigin: string
  pageNum: number
  enclosureNotation: string
  carrier: string[]
  urgentLevel: string
  sendRange: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<DocumentForm>({
  mainOrgan: '',
  sendOrgan: '',
  publishOrigin: '',
  pageNum: 1,
  enclosureNotation: '',
  carrier: [],
  urgentLevel: '',
  sendRange: ''
})

// 文件载体选项
const carriers = ref<OptionItem[]>([
  { value: '1', label: '纸质' },
  { value: '2', label: '磁盘' },
  { value: '3', label: '光盘' },
  { value: '4', label: '磁带' },
  { value: '5', label: '音像' }
])

// 紧急程度选项
const urgentLevels = ref<OptionItem[]>([
  { value: '1', label: '特急件' },
  { value: '2', label: '加急件' },
  { value: '3', label: '急件' },
  { value: '4', label: '平件' }
])

// 发送范围选项
const sendRanges = ref<OptionItem[]>([
  { value: '1', label: '内部文件，仅限本单位使用' },
  { value: '2', label: '外部文件，可分享外部单位使用' },
  { value: '3', label: '共享文件，可分享其他内部单位使用' }
])

// 根据值获取选项标签
const getOptionLabel = (options: OptionItem[], value: string) => {
  const option = options.find(item => item.value === value)
  return option ? option.label : ''
}

// 根据值数组获取标签数组
const getCarrierLabels = (values: string[]) => {
  if (!values || !Array.isArray(values)) return []
  return values.map(value => {
    const option = carriers.value.find(item => item.value === value)
    return option ? option.label : ''
  }).filter(label => label !== '')
}

// 初始化档案元数据
const initArchiveMetadata = () => {
  if (props.metadata) {
    try {
      const metadata = JSON.parse(props.metadata)
      form.value = {
        mainOrgan: metadata.mainOrgan || '',
        sendOrgan: metadata.sendOrgan || '',
        publishOrigin: metadata.publishOrigin || '',
        pageNum: metadata.pageNum !== undefined ? metadata.pageNum : 1,
        enclosureNotation: metadata.enclosureNotation || '',
        carrier: metadata.carrier || [],
        urgentLevel: metadata.urgentLevel || '',
        sendRange: metadata.sendRange || ''
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

