<template>
  <el-footer :style="footerStyle">
    <div class="animate__animated animate__pulse animate__fadeInUp">
      <el-row>
        <el-col :span="16" :offset="4">
          <el-tooltip :show-arrow="false" effect="dark" content="获取工具" placement="top">
            <el-button size="small" class="ability-btn" round>
              <i class="ri-magic-fill"></i>
            </el-button>
          </el-tooltip>
          <el-tooltip :show-arrow="false" effect="dark" content="选择知识库" placement="top">
            <el-button size="small" class="ability-btn" round @click="emit('toggle-wiki-drawer')">
              <i class="ri-stack-fill"></i>
            </el-button>
          </el-tooltip>
        </el-col>
      </el-row>
      <el-row style="margin-top: 7px">
        <el-col :span="16" :offset="4">
          <div class="relative w-full p-2 rounded-lg bg-white/60 border border-blue-300 focus-within:ring-1 focus-within:ring-blue-400 focus-within:border-blue-400/40 transition-all duration-300">
            <div v-if="useFileParam.isUseFile" class="mb-2">
              <div class="flex flex-wrap gap-2">
                <div
                    v-for="(file, index) in useFileParam.fileInfos"
                    :key="index"
                    class="relative group"
                    @mouseenter="showDeleteOverlay(index)"
                    @mouseleave="hideDeleteOverlay"
                >
                  <el-tag
                      type="primary"
                      class="cursor-pointer relative animate__animated animate__pulse animate__fadeInDown"
                      :class="{ 'opacity-70': deleteOverlayIndex === index }"
                  >
                    <i class="ri-attachment-2 mr-1"></i>{{ file.filename }}
                  </el-tag>
                  <!-- 删除蒙层 -->
                  <div
                      v-show="deleteOverlayIndex === index"
                      class="absolute inset-0 bg-red-500/80 rounded flex items-center justify-center cursor-pointer transition-all duration-200"
                      @click="removeFile(index)"
                  >
                    <i class="ri-delete-bin-2-line text-white text-sm"></i>
                  </div>
                </div>
              </div>
            </div>
            <div class="flex items-end h-full">
              <textarea
                  ref="questionInputRef"
                  v-model="inputQuestion"
                  @keydown="handleKeyDown"
                  @input="adjustTextareaHeight"
                  placeholder="请输入您的问题，按回车发送，按 Shift+Enter 换行！"
                  rows="1"
                  class="flex-1 m-0 p-0 border-0 focus:ring-0 focus:outline-none resize-none bg-transparent custom-scrollbar w-full"
                  :style="textareaStyle"
                  aria-label="输入消息"
              ></textarea>
            </div>
            <div class="flex items-center justify-between mt-2">
              <div class="flex items-center gap-3">
                <el-button size="large" type="info" @click="emit('toggle-upload-dialog')" circle bg text>
                  <i class="ri-attachment-2 text-base"></i>
                </el-button>
              </div>
              <div>
                <el-button class="send-button" circle size="large" @click="emit('send-out')">
                  <i :class="[isSendOut ? 'ri-stop-large-fill' : 'ri-arrow-up-line']"></i>
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-footer>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, watch, onMounted } from 'vue'
import type { Ref } from 'vue'
import type { UseFileParam } from "@/views/ai/talk/types/talk.ts";


// 定义 props
const props = withDefaults(defineProps<{
  modelValue: string
  messagesLength: number
  msgParamTopicId?: string | number | null
  useFileParam: UseFileParam
  isSendOut: boolean
}>(), {
  msgParamTopicId: null
})

// 定义 emits
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'send-out'): void
  (e: 'toggle-upload-dialog'): void
  (e: 'toggle-wiki-drawer'): void
  (e: 'remove-file', value: number): void
}>()

// 定义响应式数据
const deleteOverlayIndex: Ref<number> = ref(-1)
const textareaHeight: Ref<number> = ref(44) // 初始高度
const questionInputRef = ref<HTMLTextAreaElement | null>(null)

// 计算属性
const inputQuestion = computed<string>({
  get() {
    return props.modelValue
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

// 动态计算 footer 高度
const footerStyle = computed(() => {
  const baseHeight = props.useFileParam.isUseFile ? 202 : 170
  const additionalHeight = Math.max(0, textareaHeight.value - 44) // 44 是初始 textarea 高度
  const totalHeight = baseHeight + additionalHeight + 7 // +7 是 margin-top
  return {
    height: totalHeight + 'px',
    marginTop: '7px'
  }
})

// 动态计算 textarea 样式
const textareaStyle = computed(() => {
  return {
    minHeight: '20px',
    maxHeight: '120px',
    height: textareaHeight.value + 'px'
  }
})

// 方法
const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    event.preventDefault()
    if (event.shiftKey) {
      // Shift+Enter 换行
      const textarea = questionInputRef.value as HTMLTextAreaElement
      if (textarea) {
        const start = textarea.selectionStart
        const end = textarea.selectionEnd
        const value = inputQuestion.value

        inputQuestion.value = value.substring(0, start) + '\n' + value.substring(end)

        // 保持光标位置正确
        nextTick(() => {
          if (textarea) {
            textarea.selectionStart = textarea.selectionEnd = start + 1
            adjustTextareaHeight()
          }
        })
      }
    } else {
      // Enter 发送
      emit('send-out')
      // 发送后重置高度
      nextTick(() => {
        resetTextareaHeight()
      })
    }
  }
}

const showDeleteOverlay = (index: number) => {
  deleteOverlayIndex.value = index
}

const hideDeleteOverlay = () => {
  deleteOverlayIndex.value = -1
}

const removeFile = (index: number) => {
  emit('remove-file', index)
  hideDeleteOverlay()
}

const adjustTextareaHeight = () => {
  const textarea = questionInputRef.value as HTMLTextAreaElement
  if (textarea) {
    // 重置高度以获取正确的 scrollHeight
    textarea.style.height = 'auto'
    const scrollHeight = textarea.scrollHeight

    // 设置最大高度为 120px，最小高度为 44px（初始高度）
    textareaHeight.value = Math.min(Math.max(scrollHeight, 44), 120)
    textarea.style.height = textareaHeight.value + 'px'
  }
}

const resetTextareaHeight = () => {
  // 重置到初始高度
  textareaHeight.value = 44
  const textarea = questionInputRef.value as HTMLTextAreaElement
  if (textarea) {
    textarea.style.height = 'auto'
  }
}

// 生命周期钩子
onMounted(() => {
  nextTick(() => {
    if (questionInputRef.value) {
      adjustTextareaHeight()
    }
  })
})

// 监听器
watch(
    () => props.modelValue,
    () => {
      nextTick(() => {
        adjustTextareaHeight()
      })
    }
)

watch(
    () => props.useFileParam.isUseFile,
    () => {
      // 文件状态改变时重新计算高度
      nextTick(() => {
        adjustTextareaHeight()
      })
    }
)
</script>