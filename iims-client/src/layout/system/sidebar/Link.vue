<template>
  <component v-if="to" :is="linkType" :to="to" v-bind="linkProps(to)">
    <slot />
  </component>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { isExternal } from '@/utils/validate'

// 定义 Props 接口
interface Props {
  to: string
  frame?: number
}

// 使用 withDefaults 处理默认值
const props = withDefaults(defineProps<Props>(), {
  frame: 1
})

// 计算属性
const isExternalLink = computed<boolean>(() => {
  return isExternal(props.to)
})

const linkType = computed<string>(() => {
  return isExternalLink.value ? 'a' : 'router-link'
})

// 方法
const linkProps = (to: string) => {
  if (isExternalLink.value || props.frame === 0) {
    return {
      href: `#${to}`, // 对于外部链接使用 href
      target: '_blank',
      rel: 'noopener'
    }
  }
  return {
    to: to
  }
}
</script>