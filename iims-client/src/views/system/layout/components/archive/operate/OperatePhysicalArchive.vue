<template>
  <!-- 实物档案 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        class="margin-top"
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <i class="ri-archive-stack-line"></i>
          物品名称
        </template>
        <el-input
            v-model="form.itemName"
            style="width: 230px;"
            placeholder="请输入物品名称"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-number-1"></i>
          物品编号
        </template>
        <el-input
            v-model="form.itemNumber"
            style="width: 230px;"
            placeholder="请输入物品编号"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-line"></i>
          物品类别
        </template>
        <el-select
            v-model="form.itemCategory"
            placeholder="选择物品类别"
            style="width: 230px;"
        >
          <el-option
              v-for="item in itemCategories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-money-cny-circle-line"></i>
          价值金额
        </template>
        <el-input-number
            v-model="form.valueAmount"
            style="width: 230px;"
            :min="0"
            :precision="2"
            placeholder="请输入价值金额"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          获得时间
        </template>
        <el-date-picker
            v-model="form.obtainTime"
            type="date"
            placeholder="选择获得时间"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          存放位置
        </template>
        <el-input
            v-model="form.storageLocation"
            style="width: 230px;"
            placeholder="请输入存放位置"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          物品描述
        </template>
        <el-input
            v-model="form.itemDescription"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入物品描述"
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
interface PhysicalForm {
  itemName: string
  itemNumber: string
  itemCategory: string
  valueAmount: number
  obtainTime: string
  storageLocation: string
  itemDescription: string
  remarks: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<PhysicalForm>({
  itemName: '',
  itemNumber: '',
  itemCategory: '',
  valueAmount: 0,
  obtainTime: '',
  storageLocation: '',
  itemDescription: '',
  remarks: ''
})

// 物品类别选项
const itemCategories = ref<OptionItem[]>([
  { value: '1', label: '文物古籍' },
  { value: '2', label: '珍贵标本' },
  { value: '3', label: '重要文件' },
  { value: '4', label: '纪念品' },
  { value: '5', label: '奖章证书' },
  { value: '6', label: '其他实物' }
])

// 初始化档案元数据
const initArchiveMetadata = () => {
  if (props.metadata) {
    try {
      const metadata = JSON.parse(props.metadata)
      form.value = {
        itemName: metadata.itemName || '',
        itemNumber: metadata.itemNumber || '',
        itemCategory: metadata.itemCategory || '',
        valueAmount: metadata.valueAmount !== undefined ? metadata.valueAmount : 0,
        obtainTime: metadata.obtainTime || '',
        storageLocation: metadata.storageLocation || '',
        itemDescription: metadata.itemDescription || '',
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