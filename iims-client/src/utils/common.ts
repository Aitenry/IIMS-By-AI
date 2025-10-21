/**
 * 通用ts方法封装处理
 */

const baseURL = import.meta.env.VITE_APP_BASE_API

let waterMarkObserver: MutationObserver | null = null // 保存MutationObserver实例

// 日期格式化
export function parseTime(time: any, pattern?: string): string | null {
  if (arguments.length === 0 || !time) {
    return null
  }

  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
  let date: Date

  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
      time = parseInt(time, 10)
    } else if (typeof time === 'string') {
      time = time.replace(new RegExp(/-/gm), '/')
    }
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }

  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }

  // 修正 replace 回调函数的类型
  return format.replace(/{([ymdhisa])+}/g, (result: string, key: string): string => {
    let value = formatObj[key as keyof typeof formatObj]
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    }
    if (result.length > 0 && value < 10) {
      return `0${value}`
    }
    return String(value || 0)
  })
}

// 表单重置
export function resetForm(refName: string, vueInstance: any): void {
  if (vueInstance.$refs[refName]) {
    vueInstance.$refs[refName].resetFields()
  }
}

export function addDateRange(params: any, dateRange: any[] | null | undefined, propName?: string): any {
  const search = params
  search.params = {}

  // 修正条件判断
  if (dateRange && dateRange.length > 0) {
    if (propName === undefined) {
      search.params['beginTime'] = dateRange[0]
      search.params['endTime'] = dateRange[1]
    } else {
      search.params['begin' + propName] = dateRange[0]
      search.params['end' + propName] = dateRange[1]
    }
  }
  return search
}

// 回显数据字典
export function selectDictLabel(data: any, value: string | number): string {
  const actions: string[] = []
  Object.keys(data).some((key) => {
    if (data[key].dictValue === ('' + value)) {
      actions.push(data[key].dictLabel)
      return true
    }
  })
  return actions.join('')
}

// 回显数据字典（字符串数组）
export function selectDictLabels(data: any, value: string, separator?: string): string {
  const actions: string[] = []
  const currentSeparator = separator === undefined ? ',' : separator
  const temp = value.split(currentSeparator)
  Object.keys(temp).some((val) => {
    Object.keys(data).some((key) => {
      if (data[key].dictValue === ('' + temp[Number(val)])) {
        actions.push(data[key].dictLabel + currentSeparator)
      }
    })
  })
  return actions.join('').substring(0, actions.join('').length - 1)
}

// 通用下载方法
export function download(fileName: string): void {
  window.location.href = baseURL + '/common/download?fileName=' + encodeURI(fileName) + '&delete=' + true
}

// 字符串格式化(%s )
export function sprintf(str: string, ...args: any[]): string {
  let i = 1
  let flag = true
  const formatStr = str.replace(/%s/g, function() {
    const arg = args[i++]
    if (typeof arg === 'undefined') {
      flag = false
      return ''
    }
    return arg
  })
  return flag ? formatStr : ''
}

// 转换字符串，undefined,null等转化为''
export function parseStrEmpty(str: any): string {
  if (!str || str === 'undefined' || str === 'null') {
    return ''
  }
  return str
}

/**
 * 构造树型结构数据
 * @param data 数据源
 * @param id id字段 默认 'id'
 * @param parentId 父节点字段 默认 'parentId'
 * @param children 孩子节点字段 默认 'children'
 * @param rootId 根Id 默认 0
 */
export function handleTree(
    data: any[],
    id = 'id',
    parentId = 'parentId',
    children = 'children',
    rootId?: string
): any[] {
  const actualRootId = rootId || Math.min.apply(Math, data.map(item => { return item[parentId] })) || '0'
  // 对源数据深度克隆
  const cloneData = JSON.parse(JSON.stringify(data))
  // 循环所有项
  const treeData = cloneData.filter((father: any) => {
    const branchArr = cloneData.filter((child: any) => {
      // 返回每一项的子级数组
      return father[id] === child[parentId]
    })
    if (branchArr.length > 0) {
      father.children = branchArr
    }
    // 返回第一层
    return father[parentId] === actualRootId
  })
  return treeData !== '' ? treeData : data
}

/**
 * 封装将路由home字符串形式转为路由的函数
 * @param componentName 组件名称
 * @returns 动态导入函数
 */
export function transformFn(componentName: string): () => Promise<any> {
  return () => import(`@/views/${componentName}.vue`)
}

// 水印配置接口
interface WatermarkConfig {
  container?: HTMLElement
  width?: string
  height?: string
  textAlign?: CanvasTextAlign
  textBaseline?: CanvasTextBaseline
  font?: string
  fillStyle?: string
  content?: string
  rotate?: string
  zIndex?: number
}

// 调用生成水印
export function generateWatermark(content: string): void {
  __canvasWM({ content })
}

// 移除水印并停止MutationObserver
export function removeWatermark(): void {
  const watermarkDiv = document.querySelector('.__wm')
  if (watermarkDiv) {
    watermarkDiv.remove()
  }
  if (waterMarkObserver) {
    waterMarkObserver.disconnect()
    waterMarkObserver = null
  }
}

function __canvasWM(config: WatermarkConfig = {}): void {
  const {
    container = document.body,
    width = '150px',
    height = '150px',
    textAlign = 'center',
    textBaseline = 'middle',
    font = '13px Microsoft Yahei',
    fillStyle = 'rgba(184, 184, 184, 0.3)',
    content = 'QiJing',
    rotate = '-30',
    zIndex = 9999
  } = config

  const args = config
  const canvas = document.createElement('canvas')
  canvas.setAttribute('width', width)
  canvas.setAttribute('height', height)
  const ctx = canvas.getContext('2d')
  if (ctx) {
    ctx.textAlign = textAlign
    ctx.textBaseline = textBaseline
    ctx.font = font
    ctx.fillStyle = fillStyle
    ctx.rotate(Math.PI / 180 * parseInt(rotate))
    ctx.fillText(content, parseFloat(width) / 2, parseFloat(height) / 2)
  }
  const base64Url = canvas.toDataURL()
  const watermarkDiv = document.querySelector('.__wm') || document.createElement('div')
  const styleStr = `
    position:absolute;
    top:0;
    left:0;
    width:100%;
    height:100%;
    z-index:${zIndex};
    pointer-events:none;
    background-repeat:repeat;
    background-image:url('${base64Url}')
  `
  watermarkDiv.setAttribute('style', styleStr)
  watermarkDiv.classList.add('__wm')
  if (!document.querySelector('.__wm')) {
    container.style.position = 'relative'
    container.insertBefore(watermarkDiv, container.firstChild)
  }

  // 如果没有创建过观察者，则创建一个新的观察者，并且开始观察DOM的变化
  if (!waterMarkObserver) {
    const MutationObserver = window.MutationObserver
    if (MutationObserver) {
      waterMarkObserver = new MutationObserver(mutations => {
        mutations.forEach(() => {
          // 如果水印元素被移除或样式改变，则尝试恢复它
          if (!document.querySelector('.__wm') || document.querySelector('.__wm')?.getAttribute('style') !== styleStr) {
            __canvasWM(JSON.parse(JSON.stringify(args))) // 重新绘制水印
          }
        })
      })

      waterMarkObserver.observe(container, {
        attributes: true,
        subtree: true,
        childList: true
      })
    }
  }
}

/**
 * 格式化时间显示
 * @param time 时间戳或时间字符串
 * @param option 时间格式化选项
 * @returns 格式化后的时间字符串
 */
export function formatTime(time: string | number, option?: string): string {
  let timestamp: number

  if (('' + time).length === 10) {
    timestamp = parseInt(time as string) * 1000
  } else {
    timestamp = +time
  }

  const d = new Date(timestamp)
  const now = Date.now()

  const diff = (now - d.getTime()) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }

  if (option) {
    return parseTime(timestamp, option) || ''
  } else {
    return (
        (d.getMonth() + 1) +
        '月' +
        d.getDate() +
        '日' +
        d.getHours() +
        '时' +
        d.getMinutes() +
        '分'
    )
  }
}

/**
 * 将URL参数转换为对象
 * @param url 完整的URL字符串
 * @returns 参数对象
 */
export function paramObj(url: string): Record<string, string> {
  const search = decodeURIComponent(url.split('?')[1] || '').replace(/\+/g, ' ')
  if (!search) {
    return {}
  }

  const obj: Record<string, string> = {}
  const searchArr = search.split('&')

  searchArr.forEach(v => {
    const index = v.indexOf('=')
    if (index !== -1) {
      const name = v.substring(0, index)
      obj[name] = v.substring(index + 1)
    }
  })

  return obj
}