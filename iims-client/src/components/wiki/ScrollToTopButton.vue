<template>
    <!-- fixed 固定位置，并通过 bottom-xx right-xx 设置在右下角 -->
    <div v-show="showScrollToTopBtn" @click="scrollToTop"
        class="z-50 border cursor-pointer fixed bottom-2 right-2 md:bottom-5 md:right-11 inline p-3 bg-white hover:bg-gray-100 rounded
        dark:bg-gray-800 dark:hover:bg-gray-900 dark:border-gray-700">
        <svg class="w-4 h-4 text-gray-500 dark:text-white" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 10 14">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M5 13V1m0 0L1 5m4-4 4 4"></path>
        </svg>
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
