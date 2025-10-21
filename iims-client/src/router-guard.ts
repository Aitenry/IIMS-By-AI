import router from './router/index.js'
import store from './store/index.js'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getStorage } from '@/utils/auth.js' // get token from cookie
import getPageTitle from '@/utils/get-page-title.js'
import { generateWatermark, removeWatermark } from '@/utils/common.js'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login']

router.beforeEach(async(to, from, next) => {
  NProgress.start()
  document.title = getPageTitle(to.meta.title)
  const hasToken = getStorage('token')
  if (hasToken) {
    if (to.path === '/login' || to.path === '/') {
      next({ path: '/home' })
      NProgress.done()
    } else {
      const hasGetUserInfo = store.getters.name
      if (hasGetUserInfo) {
        generateWatermark(getStorage('name'))
        next()
      } else {
        try {
          const accessRoutes = await store.dispatch('permission/generateRoutes')
          accessRoutes.forEach(route => {
            router.addRoute(route)
          })
          next({ ...to, replace: true })
        } catch (error) {
          await store.dispatch('user/resetStorage')
          ElMessage.error(error || 'Has Error')
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
      removeWatermark()
    } else {
      if (to.path === '/') {
        next('/login')
      } else {
        next(`/login?redirect=${to.path}`)
      }
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
