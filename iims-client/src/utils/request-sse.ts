import {fetchEventSource, type FetchEventSourceInit} from '@microsoft/fetch-event-source'
import { ElMessage } from 'element-plus'
import { getStorage } from '@/utils/auth.js'

// 定义输入对象的类型
interface SseInputObject {
  url: string;
  method?: string;
  data?: any;
}

// 创建一个fetchEventSource实例的函数
const sse = (
    obj: SseInputObject,
    options: Partial<FetchEventSourceInit> = {},
    onMessageCallback: (ev: any) => void
) => {
  // 请求前的拦截处理
  const onOpen = async () => {
    // 可以在这里做一些初始化操作
  }

  // 分离 SSE 特定的选项和其他选项
  const sseSpecificOptions: FetchEventSourceInit = {
    method: obj.method || 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive',
      'token': getStorage('token') || ''
    },
    body: JSON.stringify(obj.data),
    onopen: async () => {
      await onOpen();
    },
    onmessage: (ev: any) => {
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
    onerror: (err: any) => {
      console.error('SSE error:', err)
      if (err.name === 'AbortError') {
        // 用户主动关闭了连接
      } else {
        ElMessage.error('SSE connection error.')
      }
      throw err; // 需要抛出错误让 fetchEventSource 知道是否继续重试
    },
    onclose: () => {
      console.log('SSE connection closed.')
    },
    openWhenHidden: true
  }

  // 合并 SSE 选项和传入的自定义选项
  const finalOptions: FetchEventSourceInit = { ...sseSpecificOptions, ...options }

  // 创建 AbortController 来控制连接
  const abortController = new AbortController()

  // 开始建立SSE连接
  fetchEventSource(obj.url, {
    ...finalOptions,
    signal: abortController.signal
  }).catch((err) => {
    console.error('FetchEventSource failed:', err);
  })

  // 返回一个函数来取消SSE连接
  return () => {
    abortController.abort()
  }
}

export default sse
