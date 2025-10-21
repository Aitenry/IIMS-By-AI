<template>
  <!-- 建设档案 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        class="margin-top"
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <i class="ri-building-line"></i>
          项目名称
        </template>
        <el-input
            v-model="form.projectName"
            style="width: 230px;"
            placeholder="请输入项目名称"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-map-pin-line"></i>
          项目地点
        </template>
        <el-input
            v-model="form.projectLocation"
            style="width: 230px;"
            placeholder="请输入项目地点"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-line"></i>
          建设规模
        </template>
        <el-input
            v-model="form.constructionScale"
            style="width: 230px;"
            placeholder="请输入建设规模"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-money-cny-circle-line"></i>
          投资金额
        </template>
        <el-input-number
            v-model="form.investmentAmount"
            style="width: 230px;"
            :min="0"
            :precision="2"
            placeholder="请输入投资金额"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          开工时间
        </template>
        <el-date-picker
            v-model="form.constructionStartTime"
            type="date"
            placeholder="选择开工时间"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          竣工时间
        </template>
        <el-date-picker
            v-model="form.constructionEndTime"
            type="date"
            placeholder="选择竣工时间"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          建设内容
        </template>
        <el-input
            v-model="form.constructionContent"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入建设内容"
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
interface ConstructionForm {
  projectName: string
  projectLocation: string
  constructionScale: string
  investmentAmount: number
  constructionStartTime: string
  constructionEndTime: string
  constructionContent: string
  remarks: string
}

// 表单数据
const form = ref<ConstructionForm>({
  projectName: '',
  projectLocation: '',
  constructionScale: '',
  investmentAmount: 0,
  constructionStartTime: '',
  constructionEndTime: '',
  constructionContent: '',
  remarks: ''
})

// 初始化档案元数据
const initArchiveMetadata = () => {
  if (props.metadata) {
    try {
      const metadata = JSON.parse(props.metadata)
      form.value = {
        projectName: metadata.projectName || '',
        projectLocation: metadata.projectLocation || '',
        constructionScale: metadata.constructionScale || '',
        investmentAmount: metadata.investmentAmount !== undefined ? metadata.investmentAmount : 0,
        constructionStartTime: metadata.constructionStartTime || '',
        constructionEndTime: metadata.constructionEndTime || '',
        constructionContent: metadata.constructionContent || '',
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