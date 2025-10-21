<template>
  <!-- 合同档案 -->
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
          合同编号
        </template>
        <el-input
            v-model="form.contractNumber"
            style="width: 230px;"
            placeholder="请输入合同编号"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-user-line"></i>
          合同甲方
        </template>
        <el-input
            v-model="form.partyA"
            style="width: 230px;"
            placeholder="请输入合同甲方"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-user-line"></i>
          合同乙方
        </template>
        <el-input
            v-model="form.partyB"
            style="width: 230px;"
            placeholder="请输入合同乙方"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-line"></i>
          合同类型
        </template>
        <el-select
            v-model="form.contractType"
            placeholder="选择合同类型"
            style="width: 230px;"
        >
          <el-option
              v-for="item in contractTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          签订日期
        </template>
        <el-date-picker
            v-model="form.signingDate"
            type="date"
            placeholder="选择签订日期"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-money-cny-circle-line"></i>
          合同金额
        </template>
        <el-input-number
            v-model="form.contractAmount"
            style="width: 230px;"
            :min="0"
            :precision="2"
            placeholder="请输入合同金额"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          合同标的
        </template>
        <el-input
            v-model="form.contractSubject"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入合同标的"
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