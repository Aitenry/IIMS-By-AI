<template>
  <!-- 专业档案 -->
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
          专业名称
        </template>
        <el-input
            v-model="form.professionalName"
            style="width: 230px;"
            placeholder="请输入专业名称"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-number-1"></i>
          专业编号
        </template>
        <el-input
            v-model="form.professionalNumber"
            style="width: 230px;"
            placeholder="请输入专业编号"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-building-line"></i>
          专业领域
        </template>
        <el-select
            v-model="form.professionalField"
            placeholder="选择专业领域"
            style="width: 230px;"
        >
          <el-option
              v-for="item in professionalFields"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-line"></i>
          专业等级
        </template>
        <el-select
            v-model="form.professionalLevel"
            placeholder="选择专业等级"
            style="width: 230px;"
        >
          <el-option
              v-for="item in professionalLevels"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          获得时间
        </template>
        <el-date-picker
            v-model="form.getTime"
            type="date"
            placeholder="选择获得时间"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          发证机构
        </template>
        <el-input
            v-model="form.issuingAuthority"
            style="width: 230px;"
            placeholder="请输入发证机构"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          专业描述
        </template>
        <el-input
            v-model="form.professionalDescription"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入专业描述"
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
interface ProfessionalForm {
  professionalName: string
  professionalNumber: string
  professionalField: string
  professionalLevel: string
  getTime: string
  issuingAuthority: string
  professionalDescription: string
  remarks: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<ProfessionalForm>({
  professionalName: '',
  professionalNumber: '',
  professionalField: '',
  professionalLevel: '',
  getTime: '',
  issuingAuthority: '',
  professionalDescription: '',
  remarks: ''
})

// 专业领域选项
const professionalFields = ref<OptionItem[]>([
  { value: '1', label: '工程技术' },
  { value: '2', label: '医学卫生' },
  { value: '3', label: '教育科学' },
  { value: '4', label: '文化艺术' },
  { value: '5', label: '经济管理' },
  { value: '6', label: '法律实务' },
  { value: '7', label: '农业技术' },
  { value: '8', label: '其他专业' }
])

// 专业等级选项
const professionalLevels = ref<OptionItem[]>([
  { value: '1', label: '初级' },
  { value: '2', label: '中级' },
  { value: '3', label: '高级' },
  { value: '4', label: '专家级' }
])

// 初始化档案元数据
const initArchiveMetadata = () => {
  if (props.metadata) {
    try {
      const metadata = JSON.parse(props.metadata)
      form.value = {
        professionalName: metadata.professionalName || '',
        professionalNumber: metadata.professionalNumber || '',
        professionalField: metadata.professionalField || '',
        professionalLevel: metadata.professionalLevel || '',
        getTime: metadata.getTime || '',
        issuingAuthority: metadata.issuingAuthority || '',
        professionalDescription: metadata.professionalDescription || '',
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