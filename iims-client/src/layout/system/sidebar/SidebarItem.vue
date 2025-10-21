<template>
  <div v-if="!item.hidden">
    <app-link v-if="showSingleChild" :to="singleChildPath" :frame="singleChildFrame">
      <el-menu-item :index="singleChildPath" :class="{'submenu-title-noDropdown': !isNest}">
        <svg-icon :style="iconStyle" :icon-class="singleChildIcon" />
        <template #title>{{ singleChildTitle }}</template>
      </el-menu-item>
    </app-link>

    <el-sub-menu v-else ref="subMenu" :index="resolvePath(item.path)" popper-append-to-body>
      <template #title>
        <svg-icon :style="iconStyle" :icon-class="item.meta?.icon || ''" />
        <span :style="isHide">{{ item.meta?.title }}</span>
      </template>
      <sidebar-item
          v-for="child in item.children"
          :key="child.name || child.path"
          is-nest
          :item="child"
          :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, nextTick } from 'vue'
import { useStore } from 'vuex'
import { isExternal } from '@/utils/validate.js'
import AppLink from './Link.vue'
import SvgIcon from '@/components/v-svg-icon/Index.vue'

// 类型定义
interface RouteMeta {
  icon?: string
  title?: string
  isFrame?: number
  [key: string]: any
}

interface RouteItem {
  path: string
  name?: string
  hidden?: boolean
  alwaysShow?: boolean
  meta?: RouteMeta
  children?: RouteItem[]
  noShowingChildren?: boolean
  [key: string]: any
}

interface SidebarState {
  opened: boolean
  withoutAnimation: boolean
}

interface RootState {
  app: {
    sidebar: SidebarState
  }
}

// Props 定义
interface Props {
  item: RouteItem
  isNest?: boolean
  basePath?: string
}

const props = withDefaults(defineProps<Props>(), {
  isNest: false,
  basePath: ''
})

// Store
const store = useStore<RootState>()
const sidebar = computed(() => store.state.app.sidebar)

// Refs
const subMenu = ref(null)

// 计算属性
const isHide = computed(() => {
  if (props.isNest) return { display: '' }

  return {
    display: sidebar.value.opened ? '' : 'none'
  }
})

const iconStyle = computed(() => {
  if (props.isNest) return {
    fontSize: '14px'
  }

  return {
    fontSize: sidebar.value.opened ? '14px' : '16px',
    margin: sidebar.value.opened ? '' : '0 auto'
  }
})

const hasOneShowingChild = computed(() => {
  const children = props.item.children || []
  const showingChildren = children.filter((child) => !child.hidden)
  if (showingChildren.length <= 1) {
    return showingChildren[0] || {
      ...props.item,
      path: '',
      noShowingChildren: true,
      meta: props.item.meta || {}
    }
  }
  return false
})

const showSingleChild = computed(() => {
  if (typeof hasOneShowingChild.value === 'boolean') {
    return false
  }

  const child = hasOneShowingChild.value as RouteItem
  return child &&
      (!child.children || child.noShowingChildren) &&
      !props.item.alwaysShow
})

// 提取单个子项的属性，避免在模板中直接访问可能不存在的属性
const singleChildItem = computed(() => {
  if (typeof hasOneShowingChild.value === 'boolean' || !showSingleChild.value) {
    return null
  }
  return hasOneShowingChild.value as RouteItem
})

const singleChildFrame = computed(() => {
  const child = singleChildItem.value
  return child?.meta?.isFrame
})

const singleChildIcon = computed(() => {
  const child = singleChildItem.value
  return child?.meta?.icon || props.item.meta?.icon || ''
})

const singleChildTitle = computed(() => {
  const child = singleChildItem.value
  return child?.meta?.title
})

const singleChildPath = computed<string>(() => {
  if (typeof hasOneShowingChild.value !== 'boolean' && hasOneShowingChild.value) {
    const child = hasOneShowingChild.value as RouteItem
    return resolvePath(child.path || '')
  }
  return ''
})

// 方法
const resolvePath = (routePath: string): string => {
  if (isExternal(routePath)) return routePath
  if (props.basePath && isExternal(props.basePath)) return props.basePath
  return [props.basePath, routePath].filter(Boolean).join('/')
}

// 生命周期
onMounted(() => {
  nextTick(() => {
    const subMenuElements = document.querySelectorAll('.el-sub-menu')
    subMenuElements.forEach((subMenu) => {
      const titleSpan = subMenu.querySelector('span[style*="display: none"]')
      if (titleSpan) {
        const arrowIcon = subMenu.querySelector('.el-sub-menu__icon-arrow')
        if (arrowIcon && arrowIcon instanceof HTMLElement) {
          arrowIcon.style.display = 'none'
        }
      }
    })
  })
})
</script>

<style>
.el-menu--popup {
  min-width: 100% !important;
  padding: 0 !important;
}
</style>