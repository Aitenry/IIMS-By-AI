<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
            v-if="item.redirect === 'noRedirect' || index === levelList.length - 1 || item.redirect !== undefined"
            class="no-redirect"
            :class="{'current-bread': index === levelList.length - 1}"
        >
          {{ item.meta.title }}
        </span>
        <a
            v-else
            :class="{'current-bread': index === levelList.length - 1}"
            @click.prevent="handleLink(item)"
        >
          {{ item.meta.title }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 定义路由项类型
interface RouteMeta {
  title?: string
  breadcrumb?: boolean
  [key: string]: any
}

interface RouteRecord {
  path: string
  redirect?: string
  meta: RouteMeta
  [key: string]: any
}

// 响应式数据
const levelList = ref<RouteRecord[] | null>(null)
const route = useRoute()
const router = useRouter()

// 获取面包屑
const getBreadcrumb = () => {
  let matched = route.matched.filter((item: any) =>
      item.meta && item.meta.title && item.path
  ) as RouteRecord[]

  if (matched.length === 0 || matched[0].path !== '/home') {
    matched = [{ path: '/home', meta: { title: '信息门户' } } as RouteRecord].concat(matched)
  }

  levelList.value = matched.filter(item =>
      item.meta && item.meta.title && item.meta.breadcrumb !== false
  )
}

// 处理链接点击
const handleLink = (item: RouteRecord) => {
  const { redirect, path } = item
  if (redirect) {
    router.push(redirect)
    return
  }
  router.push(path)
}

// 监听路由变化
watch(
    () => route.path,
    (newPath) => {
      if (newPath.startsWith('/redirect/')) {
        return
      }
      getBreadcrumb()
    }
)

// 组件挂载时获取面包屑
onMounted(() => {
  getBreadcrumb()
})
</script>