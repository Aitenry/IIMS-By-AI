<template>
  <div style="padding: 0 15px;" @click="toggleClick">
    <i :class="[isActive ? 'ri-indent-decrease' : 'ri-indent-increase', 'contents w-7 h-7']"></i>
  </div>
</template>

<script setup lang="ts">
import { defineEmits, defineProps } from 'vue'

// 定义 props
interface Props {
  isActive?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isActive: false
})

// 定义 emits
const emit = defineEmits<{
  (e: 'toggleClick'): void
}>()

// 方法
const toggleClick = (): void => {
  emit('toggleClick')
}
</script>