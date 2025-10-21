<template>
  <div style="height: 100%;width: 100%;">
    <div ref="editorRef"></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, watch, nextTick } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'

const props = withDefaults(defineProps<{
  content: string | null
}>(), {
  content: '请输入内容！'
})

// 定义 refs
const editorRef = ref<HTMLElement | null>(null)

// 定义变量
let instance: Vditor | null = null
let initialContent = ''

// 初始化编辑器
function init() {
  if (!editorRef.value) return

  instance = new Vditor(editorRef.value, {
    height: '100%',
    mode: 'ir',
    preview: {
      actions: []
    },
    toolbar: [
      'emoji', 'headings', 'bold', 'italic', 'strike', 'line', '|',
      'quote', 'list', 'ordered-list', 'check', 'outdent', 'indent',
      'code', 'inline-code', 'table', '|', 'upload', 'link',
      'both', 'preview', 'outline', 'br', 'insert-after', 'insert-before', 'undo', 'redo', 'fullscreen',
      {
        name: 'more',
        tip: '更多操作',
        tipPosition: 'nw',
        icon: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16" fill="currentColor"><path d="M5 10C3.9 10 3 10.9 3 12C3 13.1 3.9 14 5 14C6.1 14 7 13.1 7 12C7 10.9 6.1 10 5 10ZM19 10C17.9 10 17 10.9 17 12C17 13.1 17.9 14 19 14C20.1 14 21 13.1 21 12C21 10.9 20.1 10 19 10ZM12 10C10.9 10 10 10.9 10 12C10 13.1 10.9 14 12 14C13.1 14 14 13.1 14 12C14 10.9 13.1 10 12 10Z"></path></svg>',
        toolbar: ['export', 'edit-mode', 'record', 'code-theme', 'content-theme']
      }
    ],
    toolbarConfig: {
      pin: true
    },
    counter: {
      enable: true
    },
    outline: {
      enable: true
    },
    cache: {
      enable: false
    },
    after: () => {
      initialContent = props.content || ''
      if (instance) {
        instance.setValue(props.content || '')
      }
    },
    upload: {
      accept: 'image/jpg, image/jpeg, image/png',
      url: '/dfs/upload',
      multiple: false,
      fieldName: 'file',
      max: 2 * 1024 * 1024,
      extraData: { token: '123456789' },
      linkToImgUrl: '/dfs/upload',
      filename(name) {
        return name
      },
      validate(msg) {
        console.log(msg + '格式')
      },
      linkToImgFormat(files: string) {
        try {
          const resData = JSON.parse(files)
          const code = resData.code
          const msg = resData.msg
          const data = resData.data
          if (code === '0') {
            const succ: Record<string, string> = {}
            succ[data.fileName] = data.url
            return JSON.stringify({
              msg,
              code,
              data: {
                errFiles: [],
                succMap: succ
              }
            })
          } else {
            console.log(msg + '上传失败了')
          }
        } catch (error) {
          console.error('解析上传响应失败:', error)
        }
        return files
      },
      format(files: File[], responseText: string) {
        try {
          const resData = JSON.parse(responseText)
          const code = resData.code
          const msg = resData.msg
          const data = resData.data
          if (code === '0') {
            const succ: Record<string, string> = {}
            succ[data.fileName] = data.url
            return JSON.stringify({
              msg,
              code,
              data: {
                errFiles: [],
                succMap: succ
              }
            })
          } else {
            console.log(msg + '上传失败了')
          }
        } catch (error) {
          console.error('解析上传响应失败:', error)
        }
        return responseText
      },
      error(msg: string) {
        console.log(msg + '上传失败了')
      }
    }
  })
}

// 监听 content 变化
watch(
    () => props.content,
    (content) => {
      if (instance) {
        instance.setValue(content || '')
      }
    },
    {
      immediate: true
    }
)

// 初始化
onMounted(() => {
  nextTick(() => {
    init()
  })
})

// 销毁
onBeforeUnmount(() => {
  if (instance) {
    try {
      instance.destroy()
    } catch (error) {
      console.error('销毁编辑器时出错:', error)
    }
    instance = null
  }
})

// 获取内容
function getEditValue(): string | null {
  if (!instance) return props.content || ''
  if (initialContent === instance.getValue()) return null
  return instance.getValue()
}

// 暴露方法给父组件
defineExpose({
  getEditValue
})
</script>

<style scoped>
</style>