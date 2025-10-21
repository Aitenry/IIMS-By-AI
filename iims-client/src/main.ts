import { createApp } from 'vue'
import App from './App.vue'
import SvgIcon from '@/components/v-svg-icon/Index.vue'
import router from './router/index'
import store from './store/index'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets
import 'remixicon/fonts/remixicon.css'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/styles/animate.css'
import '@/styles/index.css'

import _ from 'lodash' // 导入lodash插件

import '@/icons/index' // icon
import '@/router-guard' // router-guard control
import { getStorage } from '@/utils/auth'
import {
    parseTime,
    resetForm,
    addDateRange,
    selectDictLabel,
    selectDictLabels,
    download,
    handleTree
} from '@/utils/common'

import Viewer from 'v-viewer'

/* v-md-editor 编辑器 start  */
import VueMarkdownEditor from '@kangc/v-md-editor'
import '@kangc/v-md-editor/lib/style/base-editor.css'
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress'
import '@kangc/v-md-editor/lib/theme/style/vuepress.css'
import Prism from 'prismjs'
VueMarkdownEditor.use(vuepressTheme, {
    Prism
})

import vditorEdit from '@/components/v-md-editor/Index.vue'
import githubTheme from '@kangc/v-md-editor/lib/theme/github'
import '@kangc/v-md-editor/lib/theme/style/github.css'
import hljs from 'highlight.js'
VueMarkdownEditor.use(githubTheme, {
    Hljs: hljs
})

import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 动态添加路由
async function initApp(): Promise<void> {
    const app = createApp(App)

    // 注册全局方法
    app.config.globalProperties.$parseTime = parseTime
    app.config.globalProperties.$resetForm = resetForm
    app.config.globalProperties.$addDateRange = addDateRange
    app.config.globalProperties.$selectDictLabel = selectDictLabel
    app.config.globalProperties.$selectDictLabels = selectDictLabels
    app.config.globalProperties.$download = download
    app.config.globalProperties.$handleTree = handleTree
    app.config.globalProperties.$lodash = _

    // 注册 Element Plus Icons
    for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
        app.component(key, component)
    }

    // 使用 Element Plus
    app.use(ElementPlus, { locale: zhCn })

    // 使用 VueMarkdownEditor
    app.use(VueMarkdownEditor)
    app.use(Viewer)

    // 注册全局组件
    app.component('SvgIcon', SvgIcon)
    app.component('VditorEdit', vditorEdit)

    const token = getStorage('token')

    if (token) {
        // 动态生成并添加访问权限路由
        const accessRoutes: any[] = await store.dispatch('permission/generateRoutes')
        accessRoutes.forEach(route => {
            router.addRoute(route)
        })
    }

    // 挂载应用
    app.use(router).use(store).mount('#app')
}

initApp().then()