<template>
  <div>
    <span v-if="title">{{ title }}</span>
  </div>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'

// 定义 Props 接口
interface Props {
  icon?: string
  title?: string
}

// 使用 withDefaults 处理默认值
const props = withDefaults(defineProps<Props>(), {
  icon: '',
  title: ''
})
</script>