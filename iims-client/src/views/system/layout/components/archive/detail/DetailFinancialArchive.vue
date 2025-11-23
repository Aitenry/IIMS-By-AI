<template>
  <!-- 金融档案详情 -->
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
              <i class="ri-bank-card-line"></i>
            </el-icon>
            账户名称
          </div>
        </template>
        {{ form.accountName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-number-1"></i>
            </el-icon>
            账户号码
          </div>
        </template>
        {{ form.accountNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-building-line"></i>
            </el-icon>
            开户银行
          </div>
        </template>
        {{ form.openingBank || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-money-cny-circle-line"></i>
            </el-icon>
            余额
          </div>
        </template>
        {{ form.balance !== undefined ? form.balance : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            记录日期
          </div>
        </template>
        {{ form.recordDate || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            交易类型
          </div>
        </template>
        {{ getOptionLabel(transactionTypes, form.transactionType) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            业务摘要
          </div>
        </template>
        {{ form.businessSummary || '-' }}
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
interface FinancialForm {
  accountName: string
  accountNumber: string
  openingBank: string
  balance: number
  recordDate: string
  transactionType: string
  businessSummary: string
  remarks: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<FinancialForm>({
  accountName: '',
  accountNumber: '',
  openingBank: '',
  balance: 0,
  recordDate: '',
  transactionType: '',
  businessSummary: '',
  remarks: ''
})

// 交易类型选项
const transactionTypes = ref<OptionItem[]>([
  { value: '1', label: '存款' },
  { value: '2', label: '取款' },
  { value: '3', label: '转账' },
  { value: '4', label: '利息收入' },
  { value: '5', label: '手续费' },
  { value: '6', label: '贷款' },
  { value: '7', label: '还款' }
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
        accountName: metadata.accountName || '',
        accountNumber: metadata.accountNumber || '',
        openingBank: metadata.openingBank || '',
        balance: metadata.balance !== undefined ? metadata.balance : 0,
        recordDate: metadata.recordDate || '',
        transactionType: metadata.transactionType || '',
        businessSummary: metadata.businessSummary || '',
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

