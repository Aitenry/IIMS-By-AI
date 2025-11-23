<template>
  <!-- 建设档案详情 -->
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
              <i class="ri-building-line"></i>
            </el-icon>
            项目名称
          </div>
        </template>
        {{ form.projectName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-map-pin-line"></i>
            </el-icon>
            项目地点
          </div>
        </template>
        {{ form.projectLocation || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-line"></i>
            </el-icon>
            建设规模
          </div>
        </template>
        {{ form.constructionScale || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-money-cny-circle-line"></i>
            </el-icon>
            投资金额
          </div>
        </template>
        {{ form.investmentAmount !== undefined ? form.investmentAmount : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            开工时间
          </div>
        </template>
        {{ form.constructionStartTime || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            竣工时间
          </div>
        </template>
        {{ form.constructionEndTime || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            建设内容
          </div>
        </template>
        {{ form.constructionContent || '-' }}
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

