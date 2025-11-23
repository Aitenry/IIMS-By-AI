<template>
  <!-- 合同档案详情 -->
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
            合同编号
          </div>
        </template>
        {{ form.contractNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-user-line"></i>
            </el-icon>
            合同甲方
          </div>
        </template>
        {{ form.partyA || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-user-line"></i>
            </el-icon>
            合同乙方
          </div>
        </template>
        {{ form.partyB || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-line"></i>
            </el-icon>
            合同类型
          </div>
        </template>
        {{ getOptionLabel(contractTypes, form.contractType) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            签订日期
          </div>
        </template>
        {{ form.signingDate || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-money-cny-circle-line"></i>
            </el-icon>
            合同金额
          </div>
        </template>
        {{ form.contractAmount !== undefined ? form.contractAmount : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            合同标的
          </div>
        </template>
        {{ form.contractSubject || '-' }}
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
interface ContractForm {
  contractNumber: string
  partyA: string
  partyB: string
  contractType: string
  signingDate: string
  contractAmount: number
  contractSubject: string
  remarks: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<ContractForm>({
  contractNumber: '',
  partyA: '',
  partyB: '',
  contractType: '',
  signingDate: '',
  contractAmount: 0,
  contractSubject: '',
  remarks: ''
})

// 合同类型选项
const contractTypes = ref<OptionItem[]>([
  { value: '1', label: '采购合同' },
  { value: '2', label: '销售合同' },
  { value: '3', label: '服务合同' },
  { value: '4', label: '租赁合同' },
  { value: '5', label: '建设工程合同' },
  { value: '6', label: '技术合同' },
  { value: '7', label: '劳动合同' }
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
        contractNumber: metadata.contractNumber || '',
        partyA: metadata.partyA || '',
        partyB: metadata.partyB || '',
        contractType: metadata.contractType || '',
        signingDate: metadata.signingDate || '',
        contractAmount: metadata.contractAmount !== undefined ? metadata.contractAmount : 0,
        contractSubject: metadata.contractSubject || '',
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

