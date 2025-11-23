<template>
  <div class="sidebar-logo-container" :class="{'iims-collapse': collapse}">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/home">
        <img v-if="logo" :src="logo" class="sidebar-logo" alt="logo">
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/home">
        <img v-if="logo" :src="logo" class="sidebar-logo" alt="logo">
        <div class="sidebar-title">
          <div style="font-size: 14px; height: 21px; font-style:oblique; line-height: normal;">启境科技</div>
          <div style="font-size: 12px; height: 16px; font-style:oblique; line-height: normal;">{{ title }} </div>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script setup lang="ts">
import url from '@/assets/icons/iims.png'

// 使用 withDefaults 处理 props
withDefaults(defineProps<{
  collapse: boolean
}>(), {
  collapse: false
})

// 响应式数据
const title = 'IIMS By AI'
const logo = url
</script>

<style lang="scss" scoped>
.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    display: ruby;
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 10px;
      border-radius: 1px;
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #fff;
      font-weight: 600;
      line-height: 50px;
      height: 35px;
      width: 66px;
      font-size: 14px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
  }

  &.iims-collapse {
    .sidebar-logo {
      margin-right: 0;
    }
  }
}
</style>