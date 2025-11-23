<template>
  <!-- 人事档案详情 -->
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
              <i class="ri-user-line"></i>
            </el-icon>
            姓名
          </div>
        </template>
        {{ form.name || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-id-card-line"></i>
            </el-icon>
            身份证号
          </div>
        </template>
        {{ form.idCard || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            出生日期
          </div>
        </template>
        {{ form.birthDate || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-genderless-line"></i>
            </el-icon>
            性别
          </div>
        </template>
        {{ getOptionLabel(genders, form.gender) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-building-line"></i>
            </el-icon>
            所在部门
          </div>
        </template>
        {{ form.department || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            职务职称
          </div>
        </template>
        {{ form.position || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            入职时间
          </div>
        </template>
        {{ form.hireDate || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-text-line"></i>
            </el-icon>
            个人简介
          </div>
        </template>
        {{ form.personalProfile || '-' }}
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
interface PersonnelForm {
  name: string
  idCard: string
  birthDate: string
  gender: string
  department: string
  position: string
  hireDate: string
  personalProfile: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<PersonnelForm>({
  name: '',
  idCard: '',
  birthDate: '',
  gender: '',
  department: '',
  position: '',
  hireDate: '',
  personalProfile: ''
})

// 性别选项
const genders = ref<OptionItem[]>([
  { value: '1', label: '男' },
  { value: '2', label: '女' }
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
        name: metadata.name || '',
        idCard: metadata.idCard || '',
        birthDate: metadata.birthDate || '',
        gender: metadata.gender || '',
        department: metadata.department || '',
        position: metadata.position || '',
        hireDate: metadata.hireDate || '',
        personalProfile: metadata.personalProfile || ''
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

