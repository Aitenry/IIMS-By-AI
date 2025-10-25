<template>
  <div v-show="showScrollToTopBtn" @click="scrollToTop"
       class="z-50 fixed bottom-0 left-1/2 transform -translate-x-1/2 cursor-pointer px-6 bg-white hover:bg-gray-100 rounded-t-full shadow-lg
        dark:bg-gray-800 dark:hover:bg-gray-700 border border-gray-200 dark:border-gray-600 transition-all duration-200 hover:scale-105">
    <i class="ri-arrow-up-wide-line text-gray-600 dark:text-gray-300"></i>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

// 是否展示返回顶部按钮
const showScrollToTopBtn = ref(false)

// 添加滚动监听
onMounted(() => window.addEventListener('scroll', handleScroll))

// 移除滚动监听
onBeforeUnmount(() => window.removeEventListener('scroll', handleScroll))

const handleScroll = () => {
  // 如果页面滚动超过300px，显示回到顶部按钮，否则隐藏
  showScrollToTopBtn.value = window.scrollY > 300
}

// 滚动到顶部
const scrollToTop = () => {
  window.scrollTo({
    top: 0, // 距离顶部位置
    behavior: 'smooth' // 平滑滚动效果
  });
}
</script>