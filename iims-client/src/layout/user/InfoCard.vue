<template>
  <el-descriptions direction="vertical" border style="margin-top: 20px;">
    <el-descriptions-item :rowspan="2" :width="140" label="头像" align="center">
      <image-with-token
          style="width: 100px; height: 100px"
          :src="info.imageUrl || ''"
          :preview-src-list="[info.imageUrl || '']"
          :initial-index="0"
          fit="cover"
      />
    </el-descriptions-item>
    <el-descriptions-item label="用户名">{{ info.username }}</el-descriptions-item>
    <el-descriptions-item label="归属部门">
      <el-tag size="small">{{ info.department }}</el-tag>
    </el-descriptions-item>
    <el-descriptions-item label="电话号码">{{ info.phone }}</el-descriptions-item>
    <el-descriptions-item label="电子邮箱">{{ info.email }}</el-descriptions-item>
    <el-descriptions-item label="简介">
      {{ info.introduction || '暂无介绍' }}
    </el-descriptions-item>
  </el-descriptions>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { getBaseAdminInfo } from '@/api/admin.js'
import ImageWithToken from "@/components/information/ImageWithToken.vue";

// 类型定义
interface UserInfo {
  imageUrl?: string
  username: string
  department: string
  phone: string
  email: string
  introduction?: string
}

const props = withDefaults(defineProps<{
  id: string | null
}>(), {
  id: null
})

// 响应式数据
const info = ref<UserInfo>({
  username: '',
  department: '',
  phone: '',
  email: '',
  introduction: ''
})

// 方法
const initCardInfo = async () => {
  if (!props.id) return

  try {
    const res = await getBaseAdminInfo(props.id)
    if (res.data) {
      info.value = res.data
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 监听器
watch(
    () => props.id,
    (newVal, oldVal) => {
      if (newVal !== oldVal) {
        initCardInfo()
      }
    }
)

// 生命周期
onMounted(() => {
  initCardInfo()
})

// 暴露方法给父组件（如果需要）
defineExpose({
  initCardInfo
})
</script>

<style scoped>
/* 如果需要自定义样式可以在这里添加 */
</style>