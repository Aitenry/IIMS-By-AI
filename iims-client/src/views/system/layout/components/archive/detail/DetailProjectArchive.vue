<template>
  <!-- 项目档案详情 -->
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
              <i class="el-icon-postcard" />
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
              <i class="el-icon-postcard" />
            </el-icon>
            项目编号
          </div>
        </template>
        {{ form.projectNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="el-icon-postcard" />
            </el-icon>
            项目负责人
          </div>
        </template>
        {{ form.projectLeader || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="el-icon-s-finance" />
            </el-icon>
            经费来源
          </div>
        </template>
        {{ form.fundingSource || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            起止时间
          </div>
        </template>
        {{ form.startAndEndTime && form.startAndEndTime.length === 2 ?
          (form.startAndEndTime[0] || '-') + ' ~ ' + (form.startAndEndTime[1] || '-') : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-money-cny-circle-line"></i>
            </el-icon>
            可用金额
          </div>
        </template>
        {{ form.projectAmount !== undefined ? form.projectAmount : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="el-icon-s-order" />
            </el-icon>
            立项依据
          </div>
        </template>
        {{ form.basis || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="el-icon-s-order" />
            </el-icon>
            研究目标
          </div>
        </template>
        {{ form.goals || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="el-icon-s-order" />
            </el-icon>
            预期成果
          </div>
        </template>
        {{ form.achievements || '-' }}
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
interface ProjectForm {
  projectName: string
  projectNumber: string
  projectLeader: string
  fundingSource: string
  startAndEndTime: (string | null)[]
  projectAmount: number
  basis: string
  goals: string
  achievements: string
}

// 表单数据
const form = ref<ProjectForm>({
  projectName: '',
  projectNumber: '',
  projectLeader: '',
  fundingSource: '',
  startAndEndTime: [],
  projectAmount: 1,
  basis: '',
  goals: '',
  achievements: ''
})

// 初始化档案元数据
const initArchiveMetadata = () => {
  if (props.metadata) {
    try {
      const metadata = JSON.parse(props.metadata)
      form.value = {
        projectName: metadata.projectName || '',
        projectNumber: metadata.projectNumber || '',
        projectLeader: metadata.projectLeader || '',
        fundingSource: metadata.fundingSource || '',
        startAndEndTime: metadata.startAndEndTime || [],
        projectAmount: metadata.projectAmount !== undefined ? metadata.projectAmount : 1,
        basis: metadata.basis || '',
        goals: metadata.goals || '',
        achievements: metadata.achievements || ''
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

