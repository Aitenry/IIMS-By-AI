import { login, loginKey, logout } from '@/api/common'
import {
  getStorage, setStorage, removeStorage
} from '@/utils/auth'
import { resetRouter } from '@/router'
import { getItem, setItem, removeItem } from '@/utils/storage'
import { JSEncrypt } from 'encryptlong'

// 定义状态类型
interface UserState {
  token: string | null
  name: string | null
  username: string | null
  role: string | null
  userId: string | null
  avatar: string
  email: string
  permissions: string[]
  menus: any[]
}

// 定义用户信息类型
interface UserInfo {
  email: string
  password: string
}

const getDefaultState = (): UserState => {
  return {
    token: getStorage('token') || null,
    name: getStorage('name') || null,
    username: getStorage('username') || null,
    role: getStorage('role') || null,
    userId: getStorage('userId') || null,
    avatar: getItem('avatar') || null,
    email: getItem('email') || null,
    permissions: getItem('permissions') || null,
    menus: getItem('menus') || null
  }
}

const state: UserState = getDefaultState()

const mutations = {
  RESET_STATE: (state: UserState): void => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state: UserState, token: string): void => {
    state.token = token
  },
  SET_NAME: (state: UserState, name: string): void => {
    state.name = name
  },
  SET_AVATAR: (state: UserState, avatar: string): void => {
    state.avatar = avatar
  },
  SET_EMAIL: (state: UserState, email: string): void => {
    state.email = email
  },
  SET_MENUS: (state: UserState, menus: any[]): void => {
    state.menus = menus
  },
  SET_PERMISSIONS: (state: UserState, permissions: string[]): void => {
    state.permissions = permissions
  },
  SET_USERNAME: (state: UserState, username: string): void => {
    state.username = username
  },
  SET_ROLE: (state: UserState, role: string): void => {
    state.role = role
  },
  SET_USER_ID: (state: UserState, userId: string): void => {
    state.userId = userId
  }
}

const actions = {
  login({ commit }: { commit: Function }, userInfo: UserInfo): Promise<any[]> {
    const { email, password } = userInfo
    return new Promise((resolve, reject) => {
      loginKey().then((response: any) => {
        const { data } = response
        const encryptor = new JSEncrypt()
        encryptor.setPublicKey(data.publicKey)

        login({
          email: email.trim(),
          password: encryptor.encryptLong(password),
          uuid: data.uuid
        }).then((response: any) => {
          const { data } = response

          commit('SET_TOKEN', data.token)
          commit('SET_PERMISSIONS', data.permissions)
          commit('SET_MENUS', data.menus)
          commit('SET_NAME', data.name)
          commit('SET_ROLE', data.role)
          commit('SET_USERNAME', data.username)
          commit('SET_AVATAR', data.imageUrl)
          commit('SET_USER_ID', data.id)
          commit('SET_EMAIL', data.email)

          setStorage('token', data.token)
          setStorage('name', data.name)
          setStorage('role', data.role)
          setStorage('username', data.username)
          setStorage('userId', data.id)

          setItem('permissions', data.permissions)
          setItem('avatar', data.imageUrl)
          setItem('email', data.email)
          setItem('menus', data.menus)

          resolve(data.menus)
        }).catch((error: any) => {
          console.log(error)
          reject(error)
        })
      }).catch((error: any) => {
        console.log(error)
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state }: { commit: Function; state: UserState }): Promise<void> {
    return new Promise((resolve, reject) => {
      logout().then(() => {
        removeStorage('token')
        removeStorage('name')
        removeStorage('role')
        removeStorage('userId')
        removeStorage('username')
        removeItem('avatar')
        removeItem('permissions')
        removeItem('menus')
        removeItem('email')

        resetRouter()
        commit('RESET_STATE')
        resolve()
      }).catch((error: any) => {
        console.log(error)
        reject(error)
      })
    })
  },

  // remove token
  resetStorage({ commit }: { commit: Function }): Promise<void> {
    return new Promise(resolve => {
      removeStorage('token')
      removeStorage('name')
      removeStorage('role')
      removeStorage('username')
      removeStorage('userId')

      removeItem('avatar')
      removeItem('permissions')
      removeItem('menus')

      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}