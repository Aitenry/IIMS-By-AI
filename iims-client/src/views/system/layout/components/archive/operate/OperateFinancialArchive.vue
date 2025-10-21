<template>
  <!-- 金融档案 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        class="margin-top"
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <i class="ri-bank-card-line"></i>
          账户名称
        </template>
        <el-input
            v-model="form.accountName"
            style="width: 230px;"
            placeholder="请输入账户名称"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-number-1"></i>
          账户号码
        </template>
        <el-input
            v-model="form.accountNumber"
            style="width: 230px;"
            placeholder="请输入账户号码"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-building-line"></i>
          开户银行
        </template>
        <el-input
            v-model="form.openingBank"
            style="width: 230px;"
            placeholder="请输入开户银行"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-money-cny-circle-line"></i>
          余额
        </template>
        <el-input-number
            v-model="form.balance"
            style="width: 230px;"
            :min="0"
            :precision="2"
            placeholder="请输入余额"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          记录日期
        </template>
        <el-date-picker
            v-model="form.recordDate"
            type="date"
            placeholder="选择记录日期"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          交易类型
        </template>
        <el-select
            v-model="form.transactionType"
            placeholder="选择交易类型"
            style="width: 230px;"
        >
          <el-option
              v-for="item in transactionTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          业务摘要
        </template>
        <el-input
            v-model="form.businessSummary"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入业务摘要"
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