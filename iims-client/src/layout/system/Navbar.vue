<template>
  <div class="navbar flex items-center justify-between">
    <div class="flex items-center">
      <hamburger :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

      <breadcrumb class="breadcrumb-container" />
    </div>

    <div class="right-menu">
      <div class="btn-lav">
        <el-button type="primary" text plain style="font-size: 20px; font-weight: bolder;">
          <i class="ri-search-eye-line"></i>
        </el-button>
        <el-button type="primary" text plain style="font-size: 20px; font-weight: bolder;">
          <el-badge is-dot>
            <i class="ri-notification-line"></i>
          </el-badge>
        </el-button>
        <el-button type="primary" text plain style="font-size: 20px; font-weight: bolder;">
          <i class="ri-settings-6-line"></i>
        </el-button>
        <el-button @click="fullScreen" type="primary" text plain style="font-size: 20px; font-weight: bolder;">
          <i :class="switchFullScreen ? 'ri-fullscreen-exit-line' : 'ri-fullscreen-fill'"></i>
        </el-button>
      </div>
      <el-dropdown class="avatar-container">
        <div style="display: flex; align-items: center;">
          <div class="avatar-wrapper">
            <img :src="avatar" class="user-avatar" alt="user-avatar">
          </div>
          <div style="margin-left: 6px;">
            <p class="les-name">{{ username }}</p>
            <el-tag size="small">{{ role }}</el-tag>
          </div>
        </div>
        <template #dropdown>
          <el-dropdown-menu style="margin: 0 5px;">
            <el-dropdown-item class="function-btn">
              <span style="display: block; padding: 6px;" @click="showEditPasswordDialog(userId)"><i class="ri-shield-keyhole-fill"></i>修改密码</span>
            </el-dropdown-item>
            <el-dropdown-item class="function-btn" style="margin-top: 6px;">
              <span style="display: block; padding: 6px;"><i class="ri-edit-circle-fill"></i>修改信息</span>
            </el-dropdown-item>
            <el-dropdown-item class="logout-btn" divided @click.native="logout">
              <span style="display: block; padding: 6px;"><i class="ri-logout-circle-line"></i>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 修改密码弹出框 -->
    <el-dialog v-model="updatePasswordVisible" title="修改密码"
               width="290px"
               append-to-body
               :close-on-click-modal="false"
               align-center
               draggable>
      <el-form ref="pwForm" class="cus-search-box cus-dialog-form-box" :model="pwForm" :rules="rulesPassword">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwForm.oldPassword" placeholder="请输入旧密码" clearable />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwForm.newPassword" placeholder="请输入新密码" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button plain @click="updatePasswordVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitPasswordForm()">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, nextTick, computed} from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import { updatePassword } from '@/api/admin.js'
import screenfull from "screenfull"
import Breadcrumb from '@/components/breadcrumb/Index.vue'
import Hamburger from '@/components/hamburger/Index.vue'

// 定义类型接口
interface PasswordForm {
  id: string | number
  oldPassword: string
  newPassword: string
}

// 响应式数据
const store = useStore()
const router = useRouter()
const updatePasswordVisible = ref(false)
const pwForm = ref<PasswordForm>({
  id: '',
  oldPassword: '',
  newPassword: ''
})
const switchFullScreen = ref(false)

// 表单校验规则
const rulesPassword = {
  oldPassword: [{ required: true, message: '不能为空', trigger: 'blur' }],
  newPassword: [{ required: true, message: '不能为空', trigger: 'blur' }]
}

// 计算属性 - 从 store 获取数据
const userId = computed(() => store.getters.userId)
const role = computed(() => store.getters.role)
const username = computed(() => store.getters.username)
const sidebar = computed(() => store.getters.sidebar)
const avatar = computed(() => store.getters.avatar)

// 重置表单方法
const resetForm = (formName: string) => {
  const form = document.querySelector(`[ref="${formName}"]`) as HTMLFormElement
  if (form && typeof (form as any).resetFields === 'function') {
    (form as any).resetFields()
  }
}

// 方法
const toggleSideBar = () => {
  store.dispatch('app/toggleSideBar')
}

const showEditPasswordDialog = (userId: string | number) => {
  resetForm('pwForm')
  pwForm.value = {
    id: '',
    oldPassword: '',
    newPassword: ''
  }
  nextTick(() => {
    updatePasswordVisible.value = true
    pwForm.value.id = userId
  })
}

const fullScreen = () => {
  if (screenfull.isEnabled) {
    switchFullScreen.value = !switchFullScreen.value
    screenfull.toggle()
  }
}

const submitPasswordForm = async () => {
  const form = document.querySelector('[ref="pwForm"]') as any
  if (form && typeof form.validate === 'function') {
    try {
      const valid = await form.validate()
      if (valid) {
        const res = await updatePassword(pwForm.value)
        if (res.errCode === 0) {
          ElMessage({
            message: '密码修改成功，请重新登录',
            type: 'success',
            duration: 1500,
            center: true
          })
          updatePasswordVisible.value = false
          setTimeout(async () => {
            await store.dispatch('user/logout')
            await router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
          }, 2000)
        }
      }
    } catch (error) {
      console.log(error)
    }
  }
}

const logout = async () => {
  await store.dispatch('user/logout')
  await router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
}

// 生命周期钩子
onMounted(() => {
  screenfull.on('change', () => {
    switchFullScreen.value = screenfull.isFullscreen
  })
})
</script>

<style lang="scss" scoped>
.navbar {
  height: 60px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 5px 0;

  .hamburger-container {
    float: left;
    height: 16px;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .les-name {
    font-size: 13px;
    margin: 0 0 3px 0;
    text-align: center;
  }

  .right-menu {
    display: flex;
    align-items: center;
    float: right;
    height: 100%;
    margin-right: 20px;

    &:focus {
      outline: none;
    }

    .btn-lav {
      display: inline-flex;
      align-items: center;
      margin-right: 12px;
      border-radius: 10px;
      border: 2px solid rgb(121.3, 187.1, 255);
      padding: 2px 5px;
      .el-button, .el-button.is-round {
        padding: 7px;
      }
      .el-button+.el-button {
        margin-left: 5px;
      }
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      cursor: pointer;
      .avatar-wrapper {
        position: relative;

        .user-avatar {
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>

<style>
.logout-btn {
  border-radius: 6px;
  padding: 1px 6px !important;
  background-color: rgb(253, 225.6, 225.6) !important;
  border: 3px solid rgb(253, 225.6, 225.6) !important;
  color: rgb(255, 115, 115) !important;
  font-weight: bolder !important;
}

.logout-btn:hover, .logout-btn:focus {
  background-color: rgb(255, 115, 115) !important;
  color: rgb(253, 225.6, 225.6) !important;
}

.function-btn {
  border-radius: 6px;
  padding: 1px 6px !important;
  border: 3px solid rgb(216.8, 235.6, 255) !important;
  background-color: rgb(216.8, 235.6, 255) !important;
  color: rgb(97, 176, 255) !important;
  font-weight: bolder !important;
}

.function-btn:hover, .function-btn:focus {
  background-color: rgb(97, 176, 255) !important;
  color: rgb(216.8, 235.6, 255) !important;
}

.avatar-container .el-tooltip__trigger:focus-visible {
  outline: none;
  border: none;
}
</style>