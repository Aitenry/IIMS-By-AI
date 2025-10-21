<template>
  <div :class="{'has-logo': showLogo}">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar max-height="calc(100vh - 100px)" wrap-class="scrollbar-wrapper">
      <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :background-color="variables.menuBg"
          :text-color="variables.menuText"
          unique-opened
          :active-text-color="variables.menuActiveText"
          :collapse-transition="true"
          class="iims-menu"
          mode="vertical"
      >
        <sidebar-item v-for="(item, i) in routes" :key="i" :item="item" :base-path="item.path" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
import Logo from './Logo.vue'
import SidebarItem from './SidebarItem.vue'

// 类型定义
interface SidebarState {
  opened: boolean
  withoutAnimation: boolean
}

interface SettingsState {
  sidebarLogo: boolean
}

interface RootState {
  app: {
    sidebar: SidebarState
  }
  settings: SettingsState
}

interface RouteMeta {
  activeMenu?: string
  [key: string]: any
}

interface RouteItem {
  path: string
  meta?: RouteMeta
  children?: RouteItem[]
  redirect?: string
  name?: string
  component?: any
  [key: string]: any
}

interface Variables {
  menuBg: string
  menuText: string
  menuActiveText: string
}

// 获取 store 和 route 实例
const store = useStore<RootState>()
const route = useRoute()

// 响应式数据
const variables = reactive<Variables>({
  menuBg: '#283443',
  menuText: '#ffffff',
  menuActiveText: '#409EFF'
})

// 计算属性
const sidebar = computed(() => store.state.app.sidebar)

const routes = computed<RouteItem[]>(() => {
  return (store.getters.menusRoutes as RouteItem[]) || []
})

const activeMenu = computed<string>(() => {
  const meta = route.meta as RouteMeta | undefined
  const path = route.path
  if (meta?.activeMenu) {
    return meta.activeMenu as string
  }
  return path
})

const showLogo = computed<boolean>(() => {
  return store.state.settings.sidebarLogo
})

const isCollapse = computed<boolean>(() => {
  return !sidebar.value.opened
})

// 方法
const loadVariables = (): void => {
  const root = document.documentElement
  variables.menuBg = getComputedStyle(root).getPropertyValue('--menu-bg') || '#283443'
  variables.menuText = getComputedStyle(root).getPropertyValue('--menu-text') || '#ffffff'
  variables.menuActiveText = getComputedStyle(root).getPropertyValue('--menu-active-text') || '#409EFF'
}

// 生命周期钩子
onMounted(() => {
  loadVariables()
})
</script>

<style lang="scss">
.el-scrollbar {
  height: auto !important;
}
a {
  text-decoration: none;
}
.iims-menu:not(.el-menu--collapse) {
  width: 200px;
}
.el-menu {
  border: none !important;
}
.el-menu-item,
.el-submenu__title,
.el-sub-menu__title,
.submenu-title-noDropdown {
  color: #ffffff !important;
  background-color: #283443 !important;
  &:hover {
    background-color: #001528 !important;
  }
}
</style>