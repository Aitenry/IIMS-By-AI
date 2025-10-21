import { computed, onMounted, ref } from 'vue'
import { useStore } from 'vuex'

// 定义设备类型
type DeviceType = 'desktop' | 'mobile'

// 定义状态接口
interface AppState {
  device: DeviceType
}

interface RootState {
  app: AppState
}

// 响应式数据
const store = useStore<RootState>()
const subMenu = ref<any>(null)

// 计算属性
const device = computed<DeviceType>(() => {
  return store.state.app.device
})

// 方法
const fixBugIniOS = () => {
  const $subMenu = subMenu.value
  if ($subMenu) {
    const handleMouseleave = $subMenu.handleMouseleave
    $subMenu.handleMouseleave = (e: Event) => {
      if (device.value === 'mobile') {
        return
      }
      handleMouseleave && handleMouseleave(e)
    }
  }
}

// 生命周期
onMounted(() => {
  fixBugIniOS()
})

// 暴露给模板
defineExpose({
  device,
  subMenu,
  fixBugIniOS
})