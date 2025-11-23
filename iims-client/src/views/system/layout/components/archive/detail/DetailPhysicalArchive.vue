<template>
  <!-- 实物档案详情 -->
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
              <i class="ri-archive-stack-line"></i>
            </el-icon>
            物品名称
          </div>
        </template>
        {{ form.itemName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-number-1"></i>
            </el-icon>
            物品编号
          </div>
        </template>
        {{ form.itemNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-line"></i>
            </el-icon>
            物品类别
          </div>
        </template>
        {{ getOptionLabel(itemCategories, form.itemCategory) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-money-cny-circle-line"></i>
            </el-icon>
            价值金额
          </div>
        </template>
        {{ form.valueAmount !== undefined ? form.valueAmount : '-' }}
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
        {{ form.obtainTime || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            存放位置
          </div>
        </template>
        {{ form.storageLocation || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            物品描述
          </div>
        </template>
        {{ form.itemDescription || '-' }}
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

