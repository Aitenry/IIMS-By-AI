import Cookies from 'js-cookie'

// 定义状态接口
interface SidebarState {
  opened: boolean
  withoutAnimation: boolean
}

interface AppState {
  sidebar: SidebarState
  device: string
}

const state: AppState = {
  sidebar: {
    opened: (() => {
      const sidebarStatus = Cookies.get('sidebarStatus')
      return sidebarStatus ? !!+sidebarStatus : true
    })(),
    withoutAnimation: false
  },
  device: 'desktop'
}

const mutations = {
  TOGGLE_SIDEBAR: (state: AppState): void => {
    state.sidebar.opened = !state.sidebar.opened
    state.sidebar.withoutAnimation = false
    if (state.sidebar.opened) {
      Cookies.set('sidebarStatus', '1')
    } else {
      Cookies.set('sidebarStatus', '0')
    }
  },
  CLOSE_SIDEBAR: (state: AppState, withoutAnimation: boolean): void => {
    Cookies.set('sidebarStatus', '0')
    state.sidebar.opened = false
    state.sidebar.withoutAnimation = withoutAnimation
  },
  TOGGLE_DEVICE: (state: AppState, device: string): void => {
    state.device = device
  }
}

const actions = {
  toggleSideBar({ commit }: { commit: Function }): void {
    commit('TOGGLE_SIDEBAR')
  },
  closeSideBar({ commit }: { commit: Function }, { withoutAnimation }: { withoutAnimation: boolean }): void {
    commit('CLOSE_SIDEBAR', withoutAnimation)
  },
  toggleDevice({ commit }: { commit: Function }, device: string): void {
    commit('TOGGLE_DEVICE', device)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}