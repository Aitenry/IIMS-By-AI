import { createRouter, createWebHashHistory } from 'vue-router'
import Layout from '@/layout/Index.vue'

// 基本路由表
const constantRoutes = [
    {
        path: '/login',
        component: () => import('@/views/login/Index.vue'),
        hidden: true
    },
    {
        path: '/404',
        component: () => import('@/views/error/404.vue'),
        hidden: true
    },
    {
        path: '/',
        component: Layout,
        children: [],
        hidden: true
    },
    {
        path: '/wiki/:wikiId', // 知识库详情页
        component: () => import('@/layout/wiki/WikiDetail.vue'),
        meta: {
            title: '知识库'
        },
        hidden: true
    }
]

// 静态路由表
const asyncRoutes = [
    {
        path: '/:pathMatch(.*)*',
        redirect: '/404',
        hidden: true
    }
]

const createRouterInstance = () => createRouter({
    history: createWebHashHistory(),
    routes: constantRoutes
})

const router = createRouterInstance()

// 重新设置路由
export function resetRouter() {
    // 获取所有动态添加的路由名称
    const routeNames = router.getRoutes().map(route => route.name)

    // 移除所有动态路由
    routeNames.forEach(name => {
        if (name) {
            router.removeRoute(name)
        }
    })
}

export default router

export { asyncRoutes, constantRoutes }