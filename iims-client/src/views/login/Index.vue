<template>
  <div class="login-background">
    <div class="box-right"></div>
    <div class="login-container">
      <el-form ref="loginFormRef" :model="loginForm" class="login-form" autocomplete="on" label-position="left">
        <div class="title-container">
          <h3 class="title animate__animated animate__pulse animate__flipInX">启境智能信息平台</h3>
          <h5 class="title-child animate__animated animate__pulse animate__fadeInUp">—— IIMS By AI ——</h5>
        </div>

        <el-form-item prop="email">
          <el-input
            ref="email"
            v-model="loginForm.email"
            size="large"
            :prefix-icon="User"
            placeholder="Username"
            name="email"
            type="text"
            tabindex="1"
            autocomplete="on"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            :key="passwordType"
            ref="password"
            size="large"
            :prefix-icon="Key"
            v-model="loginForm.password"
            :type="passwordType"
            placeholder="Password"
            name="password"
            show-password
            tabindex="2"
            autocomplete="on"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click="handleLogin">{{loading ? '' : '登录'}}</el-button>
      </el-form>
    </div>
    <svg class="waves" viewBox="0 0 150 28" preserveAspectRatio="none">
      <defs>
        <!-- 定义一个波浪路径 -->
        <path id="wave" d="M-160 10c30 0 58-10 88-10s 58 10 88 10 58-10 88-10 58 10 88 10 v10h-352z" />
      </defs>
      <!-- 使用更多的波浪图案以确保无缝衔接 -->
      <use class="wave" xlink:href="#wave" fill="rgb(197.7, 225.9, 255)" x="-352" y="0"></use>
      <use class="wave" xlink:href="#wave" fill="rgb(197.7, 225.9, 255)" x="0" y="0"></use>
      <use class="wave" xlink:href="#wave" fill="rgb(197.7, 225.9, 255)" x="352" y="0"></use>
      <use class="wave-next" xlink:href="#wave" fill="rgb(159.5, 206.5, 255)" x="-352" y="0"></use>
      <use class="wave-next" xlink:href="#wave" fill="rgb(159.5, 206.5, 255)" x="0" y="0"></use>
      <use class="wave-next" xlink:href="#wave" fill="rgb(159.5, 206.5, 255)" x="352" y="0"></use>
      <use class="wave-vnext" xlink:href="#wave" fill="rgb(121.3, 187.1, 255)" x="-352" y="0"></use>
      <use class="wave-vnext" xlink:href="#wave" fill="rgb(121.3, 187.1, 255)" x="0" y="0"></use>
      <use class="wave-vnext" xlink:href="#wave" fill="rgb(121.3, 187.1, 255)" x="352" y="0"></use>
    </svg>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Key } from '@element-plus/icons-vue'

const store = useStore()
const router = useRouter()
const route = useRoute()

const loginForm = reactive({
  email: 'aitenry@126.com',
  password: 'admin'
})

const positiveMessages = [
  '每一天都是一个新的开始，充满了无限可能！',
  '保持微笑，因为生活总是青睐乐观的人！',
  '不论天气如何，心中总有阳光灿烂的一天！',
  '让我们用积极的态度迎接每一个挑战！',
  '前方的路或许未知，但每一步都充满了精彩！',
  '快乐是一种选择，每天都值得我们去选择它！',
  '生活就像一面镜子，你对它笑，它就会对你笑！',
  '即使遇到困难，也要相信自己能够找到解决之道！',
  '与朋友分享快乐时光，让幸福加倍！',
  '不要忘了欣赏身边的美好，它们是生活中最珍贵的宝藏！'
]

const loading = ref(false)
const passwordType = ref('password')
const redirect = computed(() => route.query && route.query.redirect)

function getRandomMessage() {
  const randomIndex = Math.floor(Math.random() * positiveMessages.length)
  return positiveMessages[randomIndex]
}

const loginFormRef = ref(null)

function handleLogin() {
  loginFormRef.value.validate(valid => {
    if (valid) {
      loading.value = true
      store.dispatch('user/login', loginForm).then(() => {
        store.dispatch('permission/generateRoutes').then((accessRoutes) => {
          accessRoutes.forEach(route => {
            router.addRoute(route)
          })
          router.push({ path: redirect.value || '/home' })
          ElMessage({
            message: `欢迎回来，${getRandomMessage()}`,
            type: 'success',
            center: true
          })
        })
      }).finally(() => {
        loading.value = false
      })
    } else {
      loading.value = false
      return false
    }
  })
}

onMounted(() => {
  
})

onBeforeUnmount(() => {
  // 组件卸载前执行的逻辑
})
</script>


<style scoped>
.box-right {
  position: absolute;
  right: 0;
  border-radius: 100% 0% 0% 100% / 0% 0% 100% 100%;
  background-image: linear-gradient(45deg, rgb(121.3, 187.1, 255) 0%, rgb(159.5, 206.5, 255) 50%, rgb(197.7, 225.9, 255) 100%);
  width: 370px;
  height: 270px;
}

.login-background {
  width: 100vw;
  height: 100vh;
}

.login-container {
  z-index: 1;
  width: 400px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  overflow: hidden;
  position: absolute;
  top: calc(50% - 178px);
  left: calc(50% - 200px);
}

.login-background svg {
  width: 100vw;
  height: 150px; /* 调整到适合的高度 */
  position: absolute;
  bottom: 0;
}

.waves {
  width: 100%;
  position: absolute;
  bottom: 0;
  left: 0;
  z-index: 1;
}

.wave {
  animation: moveWave 6s linear infinite;
}

@keyframes moveWave {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-352px); /* 应该等于一个波长 */
  }
}

.wave-next {
  animation: moveWave-next 4.3s linear infinite;
}

@keyframes moveWave-next {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-352px); /* 应该等于一个波长 */
  }
}

.wave-vnext {
  animation: moveWave-vnext 3.3s linear infinite;
}

@keyframes moveWave-vnext {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-352px); /* 应该等于一个波长 */
  }
}

.title-container {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  color: #2c3e50;
  font-weight: bold;
  margin: 0;
  font-size: 24px;
  letter-spacing: 2px;
}

.title-child {
  color: #66b9f1;
  font-size: 16px;
  margin: 10px 0 0;
}

.login-form {
  width: 100%;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.el-input__inner {
  border-radius: 4px;
  border-color: #dcdfe6;
  color: #2c3e50;
}

/* 主按钮样式 */
.el-button--primary {
  background: linear-gradient(90deg, #66b9f1 0%, #45aaf2 100%); /* 渐变背景 */
  border-color: #45aaf2;
  width: 100%;
  font-size: 16px;
  font-weight: bold;
  color: white; /* 确保文字颜色与背景形成对比 */
  box-shadow: 0 4px 15px rgba(70, 170, 242, 0.4); /* 添加一个微妙的阴影 */
  transition: all 0.3s ease, box-shadow 0.2s ease-in-out, transform 0.2s ease-in-out;
}

/* 悬停时的样式 */
.el-button--primary:hover {
  background: linear-gradient(90deg, #2980b9 0%, #3498db 100%);
  border-color: #3498db;
  box-shadow: 0 6px 20px rgba(41, 128, 185, 0.6);
  transform: translateY(-2px); /* 轻微上移效果 */
}

/* 激活（按下）状态样式 */
.el-button--primary:active {
  background: linear-gradient(90deg, #3498db 0%, #1e8cd5 100%);
  border-color: #1e8cd5;
  box-shadow: 0 1px 3px rgba(41, 128, 185, 0.6);
  transform: translateY(2px); /* 下压效果 */
}

/* 加载中的样式 */
.el-button--primary.is-loading {
  background: linear-gradient(90deg, #3498db 0%, #1e8cd5 100%);
  border-color: #1e8cd5;
}

.el-button--primary.is-loading {
  background-color: #1e8cd5;
  border-color: #1e8cd5;
}

.animate__animated {
  animation-duration: 1s;
}
</style>