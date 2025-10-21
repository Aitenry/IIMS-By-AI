import { fetchEventSource } from '@microsoft/fetch-event-source'
import { ElMessage } from 'element-plus'
import { getStorage } from '@/utils/auth.js'

// 创建一个fetchEventSource实例的函数
const sse = (obj, options = {}, onMessageCallback) => {
  // 请求前的拦截处理
  const onOpen = () => {
    // 可以在这里做一些初始化操作
  }

  // 设置默认选项
  const defaultOptions = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive',
      'token': getStorage('token')
    },
    body: JSON.stringify(obj.data),
    onopen: onOpen,
    onmessage: (ev) => {
      try {
        ev.data = JSON.parse(ev.data)
        // 调用回调函数处理数据
        if (typeof onMessageCallback === 'function') {
          onMessageCallback(ev)
        }
      } catch (error) {
        console.log(error) // 如果数据不是JSON格式，则直接打印数据
      }
    },
    onerror: (err) => {
      console.error('SSE error:', err)
      if (err.name === 'AbortError') {
        // 用户主动关闭了连接
      } else {
        ElMessage.error('SSE connection error.')
      }
    },
    onclose: () => {
      console.log('SSE connection closed.')
    },
    openWhenHidden: true
  }

  // 合并默认选项和传入的选项
  const finalOptions = { ...defaultOptions, ...options }

  // 开始建立SSE连接
  const abortController = new AbortController()
  const controllerSignal = abortController.signal

  fetchEventSource(obj.url, { ...finalOptions, signal: controllerSignal })

  // 返回一个函数来取消SSE连接
  return () => {
    if (abortController) {
      abortController.abort()
    }
  }
}

export default sse
