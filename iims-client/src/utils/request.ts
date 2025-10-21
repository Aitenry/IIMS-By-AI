import axios, { type AxiosInstance, type AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store/index'
import router from '@/router/index'
import { getStorage } from '@/utils/auth'

// 定义响应数据结构
export interface ApiResponse {
  code: number
  msg: string
  errCode?: number
}

// 创建 axios 实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_API_URL as string,
  withCredentials: true // Cookie跨域
})

// 请求拦截器
service.interceptors.request.use(
    config => {
      if (store.getters.token) {
        config.headers['token'] = getStorage('token')
      }
      return config
    },
    error => {
      console.log(error)
      return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    async (response: AxiosResponse<ApiResponse>): Promise<ApiResponse | any> => {
      // 未设置状态码则默认成功状态
      const code = response.data.code
      const errCode = response.data.errCode
      // 获取错误信息
      if (code === 0) {
        if (errCode === 10007) {
          ElMessage({
            message: '登录状态已过期，请退出重新登录',
            type: 'error'
          })
          // @ts-ignore
          await store.dispatch('user/resetStorage')
          await router.replace({ path: '/login' })
          return Promise.reject(new Error(response.data.msg))
        } else {
          ElMessage({
            message: response.data.msg,
            type: 'error'
          })
          return Promise.reject(new Error(response.data.msg))
        }
      } else {
        return response.data
      }
    },
    (error: AxiosError): Promise<AxiosError> => {
      console.log('err' + error)
      let { message } = error

      if (message === 'Network Error') {
        message = '后端接口连接异常'
      } else if (message && message.includes('timeout')) {
        message = '系统接口请求超时'
      } else if (message && message.includes('Request failed with status code')) {
        message = '系统接口' + message.slice(-3) + '异常'
      } else if (!message) {
        message = '未知错误'
      }

      ElMessage({
        message: message,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(error)
    }
)

export default service