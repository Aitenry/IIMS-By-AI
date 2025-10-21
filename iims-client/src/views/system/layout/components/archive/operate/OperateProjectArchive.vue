<template>
  <!-- 项目档案 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        class="margin-top"
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <i class="el-icon-postcard" />
          项目名称
        </template>
        <el-input
            v-model="form.projectName"
            placeholder="请输入项目名称"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="el-icon-postcard" />
          项目编号
        </template>
        <el-input
            v-model="form.projectNumber"
            placeholder="请输入项目编号"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="el-icon-postcard" />
          项目负责人
        </template>
        <el-input
            v-model="form.projectLeader"
            placeholder="请输入项目负责人"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="el-icon-s-finance" />
          经费来源
        </template>
        <el-input
            v-model="form.fundingSource"
            placeholder="请输入经费来源"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          起止时间
        </template>
        <el-date-picker
            v-model="form.startAndEndTime"
            type="daterange"
            :picker-options="pickerOptions"
            range-separator="~"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-money-cny-circle-line"></i>
          可用金额
        </template>
        <el-input-number
            v-model="form.projectAmount"
            :min="0"
            :precision="2"
            placeholder="请输入可用金额"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="el-icon-s-order" />
          立项依据
        </template>
        <el-input
            v-model="form.basis"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入立项依据"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="el-icon-s-order" />
          研究目标
        </template>
        <el-input
            v-model="form.goals"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入研究目标"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="el-icon-s-order" />
          预期成果
        </template>
        <el-input
            v-model="form.achievements"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入预期成果"
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

// 日期选择器配置
const pickerOptions = {
  shortcuts: [{
    text: '最近一周',
    onClick(picker: any) {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      picker.$emit('pick', [start, end])
    }
  }, {
    text: '最近一个月',
    onClick(picker: any) {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      picker.$emit('pick', [start, end])
    }
  }, {
    text: '最近三个月',
    onClick(picker: any) {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      picker.$emit('pick', [start, end])
    }
  }]
}

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