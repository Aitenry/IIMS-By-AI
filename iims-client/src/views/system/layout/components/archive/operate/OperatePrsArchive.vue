<template>
  <!-- 人事档案 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        class="margin-top"
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <i class="ri-user-line"></i>
          姓名
        </template>
        <el-input
            v-model="form.name"
            style="width: 230px;"
            placeholder="请输入姓名"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-id-card-line"></i>
          身份证号
        </template>
        <el-input
            v-model="form.idCard"
            style="width: 230px;"
            placeholder="请输入身份证号"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          出生日期
        </template>
        <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="选择出生日期"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-genderless-line"></i>
          性别
        </template>
        <el-select
            v-model="form.gender"
            placeholder="选择性别"
            style="width: 230px;"
        >
          <el-option
              v-for="item in genders"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-building-line"></i>
          所在部门
        </template>
        <el-input
            v-model="form.department"
            style="width: 230px;"
            placeholder="请输入所在部门"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          职务职称
        </template>
        <el-input
            v-model="form.position"
            style="width: 230px;"
            placeholder="请输入职务职称"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          入职时间
        </template>
        <el-date-picker
            v-model="form.hireDate"
            type="date"
            placeholder="选择入职时间"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-text-line"></i>
          个人简介
        </template>
        <el-input
            v-model="form.personalProfile"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入个人简介"
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