<template>
  <div :class="classObj" class="app-wrapper" :style="{'--current-color': theme}">
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside" />
    <Sidebar v-if="!isFrame" class="sidebar-container" />
    <div v-if="!isFrame" class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <Navbar />
      </div>
      <AppMain />
    </div>
    <AppMain v-if="isFrame" style="min-height: 100vh;" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
import { Navbar, Sidebar, AppMain } from './system'
import {useResizeHandler} from './mixin/ResizeHandler.ts'

useResizeHandler()
// 类型定义
interface SidebarState {
  opened: boolean
  withoutAnimation: boolean
}

interface AppState {
  sidebar: SidebarState
  device: string
}

interface SettingsState {
  theme: string
  sideTheme: string
  showSettings: boolean
  tagsView: boolean
  fixedHeader: boolean
}

interface RootState {
  app: AppState
  settings: SettingsState
}

const store = useStore<RootState>()
const route = useRoute()

// 计算属性
const theme = computed(() => store.state.settings.theme)
const sidebar = computed(() => store.state.app.sidebar)
const device = computed(() => store.state.app.device)
const fixedHeader = computed(() => store.state.settings.fixedHeader)

const classObj = computed(() => ({
  hideSidebar: !sidebar.value.opened,
  openSidebar: sidebar.value.opened,
  withoutAnimation: sidebar.value.withoutAnimation,
  mobile: device.value === 'mobile'
}))

const isFrame = computed(() => route.meta.isFrame === 0)

// 方法
const handleClickOutside = () => {
  store.dispatch('app/closeSideBar', { withoutAnimation: false })
}
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: 100vh;
  background: #283443;
}

.main-container {
  width: 100vw;
}

.app-wrapper {
  position: relative;
  display: flex;
  height: 100%;
  width: 100%;
  &.mobile.openSidebar{
    position: fixed;
    top: 0;
  }
}
.drawer-bg {
  background: #000;
  opacity: 0.3;
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  width: 100%;
  padding: 0 !important;
  transition: width 0.28s;
}

.hideSidebar .fixed-header {
  width: 100%
}

.mobile .fixed-header {
  width: 100%;
}
</style>