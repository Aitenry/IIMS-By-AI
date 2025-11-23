<template>
  <!-- 会计档案详情 -->
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
            凭证编号
          </div>
        </template>
        {{ form.voucherNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            制单日期
          </div>
        </template>
        {{ form.receiptDate || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-money-cny-circle-line"></i>
            </el-icon>
            记账金额
          </div>
        </template>
        {{ form.creditAmount !== undefined ? form.creditAmount : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-user-line"></i>
            </el-icon>
            制单人
          </div>
        </template>
        {{ form.maker || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-user-line"></i>
            </el-icon>
            审核人
          </div>
        </template>
        {{ form.checker || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-user-line"></i>
            </el-icon>
            记账人
          </div>
        </template>
        {{ form.bookkeeper || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-list-view"></i>
            </el-icon>
            会计科目
          </div>
        </template>
        {{ getAccountingSubjectLabel(form.accountingSubject) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-info-card-line"></i>
            </el-icon>
            摘要说明
          </div>
        </template>
        {{ form.summaryExplanation || '-' }}
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
interface ArchiveForm {
  voucherNumber: string
  maker: string
  checker: string
  bookkeeper: string
  receiptDate: string
  creditAmount: number
  summaryExplanation: string
  accountingSubject: string
}

// 定义会计科目类型
interface AccountingSubject {
  value: string
  label: string
}

// 表单数据
const form = ref<ArchiveForm>({
  voucherNumber: '',
  maker: '',
  checker: '',
  bookkeeper: '',
  receiptDate: '',
  creditAmount: 1,
  summaryExplanation: '',
  accountingSubject: ''
})

// 会计科目选项
const accountingSubjects = ref<AccountingSubject[]>([
  { value: '1', label: '资产类' },
  { value: '2', label: '负债类' },
  { value: '3', label: '共同类' },
  { value: '4', label: '权益类' },
  { value: '5', label: '成本类' },
  { value: '6', label: '损益类' }
])

// 根据值获取会计科目标签
const getAccountingSubjectLabel = (value: string) => {
  const subject = accountingSubjects.value.find(item => item.value === value)
  return subject ? subject.label : ''
}

// 初始化档案元数据
const initArchiveMetadata = () => {
  if (props.metadata) {
    try {
      const metadata = JSON.parse(props.metadata)
      form.value = {
        voucherNumber: metadata.voucherNumber || '',
        maker: metadata.maker || '',
        checker: metadata.checker || '',
        bookkeeper: metadata.bookkeeper || '',
        creditAmount: metadata.creditAmount !== undefined ? metadata.creditAmount : 1,
        receiptDate: metadata.receiptDate || '',
        accountingSubject: metadata.accountingSubject || '',
        summaryExplanation: metadata.summaryExplanation || ''
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

