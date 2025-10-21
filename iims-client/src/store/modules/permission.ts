import { asyncRoutes, constantRoutes } from '@/router'
import { getMenuListByUserId } from '@/api/menu'
import Layout from '@/layout/Index.vue'
import { AppMain } from '@/layout/system'
import { getItem, setItem } from '@/utils/storage'

// 定义菜单项接口
interface MenuItem {
  id: string
  parentId: string
  path: string
  component: string
  redirect?: string
  title: string
  icon: string
  isFrame: string
  menuType: string
  children?: MenuItem[]
}

// 定义路由配置接口
interface RouteConfig {
  path: string
  component?: any
  redirect?: string
  children?: RouteConfig[]
  name?: string
  meta?: {
    title?: string
    icon?: string
    isFrame?: string
    [key: string]: any
  }
}

// 定义状态接口
interface PermissionState {
  routes: RouteConfig[]
  addRoutes: RouteConfig[]
}

/**
 * 静态路由懒加载
 * @param view  格式必须为 xxx/xxx 开头不要加斜杠
 * @returns
 */
const views = import.meta.glob('@/views/**/*.vue')

export const loadView = (view: string): (() => Promise<any>) | undefined => {
  if (!view) return undefined
  const key = Object.keys(views).find(key => key.includes(`/${view}/Index.vue`))
  if (key) {
    return () => views[key]()
  }
  return undefined
}

/**
 * 把从后端查询的菜单数据拼装成路由格式的数据
 * @param routes
 * @param menus 后端返回的菜单数据
 */
export function generaMenu(routes: RouteConfig[], menus: MenuItem[]): RouteConfig[] {
  let children: RouteConfig[] = []

  /**
   * 方案一:
   * 1.先把列表转为树形结构
   * 2.遍历该树形结构,根据title映射生成另一棵由静态路由表中元素构成的树
   */
  // 先把菜单列表转为树形结构
  menus.forEach(menu => {
    const pid = menu.parentId
    if (pid !== '0') {
      menus.forEach(Menu => {
        if (Menu.id === pid) {
          if (!Menu.children) {
            Menu.children = []
          }
          Menu.children.push(menu)
        }
      })
    }
  })

  // 只保留一级菜单
  menus = menus.filter(menu => menu.parentId === '0')

  // 解析menu树,构造动态菜单
  menus.forEach(menu => {
    children = generateRoutes(children, menu)
  })

  children.forEach(menu => {
    routes.push(menu)
  })
  return routes
}

// 向菜单树中添加节点
function generateRoutes(children: RouteConfig[], item: MenuItem): RouteConfig[] {
  if (item.children) {
    const parentMenu: RouteConfig = {
      path: item.path,
      component: item.component === '' || item.component === undefined ? Layout : loadView(item.component),
      redirect: item.redirect,
      children: [],
      name: item.title,
      meta: {
        title: item.title,
        icon: item.icon,
        isFrame: item.isFrame
      }
    }
    generateMenu(parentMenu, item)
    children.push(parentMenu)
  } else {
    const menu: RouteConfig = {
      path: '',
      component: Layout,
      children: [
        {
          path: item.path,
          name: item.title,
          component: loadView(item.component),
          meta: {
            title: item.title,
            icon: item.icon,
            isFrame: item.isFrame
          }
        }
      ]
    }
    children.push(menu)
  }
  return children
}

function generateMenu(parentMenu: RouteConfig, item: MenuItem): void {
  if (item.children && item.children.length > 0) {
    item.children.forEach(childItem => {
      if (childItem.menuType !== 'F') {
        const menuConfig = getMenuConfig(childItem)
        menuConfig.children = [] // 初始化children，即使当前菜单没有子菜单也保持结构一致
        if (parentMenu.children) {
          parentMenu.children.push(menuConfig)
        }
        generateMenu(menuConfig, childItem) // 递归调用生成子菜单
      }
    })
  }
}

function getMenuConfig(item: MenuItem): RouteConfig {
  const componentMapping: Record<string, any> = {
    'M': AppMain,
    'C': loadView(item.component)
  }

  const baseMeta = {
    title: item.title,
    icon: item.icon
  }

  return {
    path: item.path,
    component: componentMapping[item.menuType] || null, // 处理未定义类型的情况
    redirect: item.menuType === 'M' ? item.redirect : undefined, // 只有类型'M'时才有redirect
    name: item.title,
    meta: baseMeta
  }
}

const state: PermissionState = {
  routes: getItem('menus') || [],
  addRoutes: getItem('menus') || []
}

const mutations = {
  SET_ROUTES: (state: PermissionState, routes: any): void => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
    setItem('menus', constantRoutes.concat(routes ))
  }
}

const actions = {
  generateRoutes({ commit }: { commit: Function }): Promise<RouteConfig[]> {
    return new Promise(resolve => {
      // 从后端获取用户菜单，并加入全局状态
      getMenuListByUserId().then((res: any) => {
        if (res?.code === 1) {
          const formatMenu = res.data.filter((item: any) => item.menuType !== 'F')
          const menuData: MenuItem[] = Object.assign([], formatMenu)
          const tempAsyncRoutes: RouteConfig[] = Object.assign([], asyncRoutes)
          const accessedRoutes: RouteConfig[] = generaMenu(tempAsyncRoutes, menuData)
          commit('SET_ROUTES', accessedRoutes)
          resolve(accessedRoutes)
        } else {
          resolve([])
        }
      }).catch((error: any) => {
        console.log(error)
        resolve([])
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}