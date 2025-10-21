import { onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'

const WIDTH = 992 // refer to Bootstrap's responsive design

export function useResizeHandler() {
  const store = useStore<any>()
  const route = useRoute()

  // 使用 computed 获取 store 中的状态 (如果需要的话)
  // const device = computed(() => store.state.app.device)
  // const sidebar = computed(() => store.state.app.sidebar)

  const isMobile = (): boolean => {
    const rect = document.body.getBoundingClientRect()
    return rect.width - 1 < WIDTH
  }

  const resizeHandler = async () => {
    if (!document.hidden) {
      const currentIsMobile = isMobile()
      await store.dispatch('app/toggleDevice', currentIsMobile ? 'mobile' : 'desktop')

      if (currentIsMobile) {
        await store.dispatch('app/closeSideBar', { withoutAnimation: true })
      }
    }
  }

  // 监听路由变化
  watch(
      () => route.path, // 监听 route.path 或其他你需要的部分
      () => {
        // 注意：这里获取 device 和 sidebar 状态需要在回调内部，
        // 因为 watch 回调不是响应式作用域的一部分，除非使用 getter 函数
        const device = store.state.app.device;
        const sidebar = store.state.app.sidebar;
        if (device === 'mobile' && sidebar.opened) {
          store.dispatch('app/closeSideBar', {withoutAnimation: false}).then()
         }
      },
      { immediate: true } // 如果需要立即执行一次，可以取消注释
  )

  // 组件挂载时
  onMounted(async () => {
    window.addEventListener('resize', resizeHandler)

    // 检查初始状态
    const initialIsMobile = isMobile()
    if (initialIsMobile) {
      await store.dispatch('app/toggleDevice', 'mobile')
      await store.dispatch('app/closeSideBar', { withoutAnimation: true })
    }
  })

  // 组件卸载前
  onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeHandler)
  })

  // 可以返回 handler 以便在特定时机手动触发（如果需要）
  return { resizeHandler, isMobile }
}