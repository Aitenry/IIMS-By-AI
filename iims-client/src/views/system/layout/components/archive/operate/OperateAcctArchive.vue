<template>
  <!-- 会计档案 -->
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
          凭证编号
        </template>
        <el-input
            v-model="form.voucherNumber"
            style="width: 230px;"
            placeholder="请输入凭证编号"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          制单日期
        </template>
        <el-date-picker
            v-model="form.receiptDate"
            type="date"
            placeholder="选择日期"
            :picker-options="pickerOptions"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-money-cny-circle-line"></i>
          记账金额
        </template>
        <el-input-number
            v-model="form.creditAmount"
            style="width: 230px;"
            :min="0"
            :precision="2"
            placeholder="请输入记账金额"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-user-line"></i>
          制单人
        </template>
        <el-input
            v-model="form.maker"
            style="width: 230px;"
            placeholder="请输入制单人"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-user-line"></i>
          审核人
        </template>
        <el-input
            v-model="form.checker"
            style="width: 230px;"
            placeholder="请输入审核人"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-user-line"></i>
          记账人
        </template>
        <el-input
            v-model="form.bookkeeper"
            style="width: 230px;"
            placeholder="请输入记账人"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-list-view"></i>
          会计科目
        </template>
        <el-select
            v-model="form.accountingSubject"
            style="width: 230px;"
            placeholder="选择会计科目"
        >
          <el-option
              v-for="item in accountingSubjects"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-info-card-line"></i>
          摘要说明
        </template>
        <el-input
            v-model="form.summaryExplanation"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入摘要说明"
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

// 日期选择器配置
const pickerOptions = {
  disabledDate(time: Date) {
    return time.getTime() > Date.now()
  },
  shortcuts: [{
    text: '今天',
    onClick(picker: any) {
      picker.$emit('pick', new Date())
    }
  }, {
    text: '昨天',
    onClick(picker: any) {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24)
      picker.$emit('pick', date)
    }
  }, {
    text: '一周前',
    onClick(picker: any) {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
      picker.$emit('pick', date)
    }
  }]
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