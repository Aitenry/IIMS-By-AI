<template>
  <!-- 文书档案 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        class="margin-top"
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <i class="ri-info-card-line"></i>
          主送机关
        </template>
        <el-input
            v-model="form.mainOrgan"
            style="width: 230px;"
            placeholder="请输入主送机关"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-info-card-line"></i>
          抄送机关
        </template>
        <el-input
            v-model="form.sendOrgan"
            style="width: 230px;"
            placeholder="请输入抄送机关"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-paper-line"></i>
          机关署名
        </template>
        <el-input
            v-model="form.publishOrigin"
            style="width: 230px;"
            placeholder="请输入机关署名"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-alarm-warning-line"></i>
          紧急程度
        </template>
        <el-select
            v-model="form.urgentLevel"
            placeholder="选择紧急程度"
            style="width: 230px;"
        >
          <el-option
              v-for="item in urgentLevels"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-bring-to-front"></i>
          发送范围
        </template>
        <el-select
            v-model="form.sendRange"
            style="width: 230px;"
            placeholder="选择范围规定"
        >
          <el-option
              v-for="item in sendRanges"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-page-separator"></i>
          档案页数
        </template>
        <el-input-number
            v-model="form.pageNum"
            style="width: 230px;"
            :min="1"
            placeholder="请输入档案页数"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-archive-stack-line"></i>
          文件载体
        </template>
        <el-select
            v-model="form.carrier"
            multiple
            placeholder="选择载体"
            style="width: 230px;"
        >
          <el-option
              v-for="item in carriers"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-info-card-line"></i>
          附件说明
        </template>
        <el-input
            v-model="form.enclosureNotation"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入附件说明"
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