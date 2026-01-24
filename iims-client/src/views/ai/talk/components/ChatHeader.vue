<template>
  <div style="
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin: 10px;
    ">
    <el-button style="
        border: 2px solid #8ac5ff;
        color: #40a0ffa8;
        background: #ecf5ff;
        border-radius: 12px;
      " @click="emit('open-chat-list')" circle>
      <i :class="'ri-indent-' + (isOpenChatList ? 'decrease' : 'increase')"></i>
    </el-button>
    <div class="animate__animated animate__pulse animate__fadeInRight">
      <el-tooltip effect="dark" content="开启新对话" placement="bottom-start">
        <el-button style="
            border: 2px solid #40a0ffa8;
            color: #40a0ffa8;
            background: #ecf5ff;
            border-radius: 10px;
            padding: 7px 12px;
          " @click="emit('open-new-chat')" round>
          <i style="font-size: 16px; font-weight: bolder" class="ri-discuss-line"></i>
        </el-button>
      </el-tooltip>
      <el-tooltip effect="dark" :content="'知识库：' + wikiStatusDecl" placement="bottom-start">
        <el-button style="
            border: 2px solid #40a0ffa8;
            color: #40a0ffa8;
            background: #ecf5ff;
            border-radius: 10px;
            padding: 7px 12px;
          " :class="{ loader: isFlashing }" round @click="emit('handle-wiki-click')">
          <i style="font-size: 16px; font-weight: bolder" :class="'ri-' +
            (isFlashing ? 'flashlight' : 'archive-stack') +
            '-fill'
          "></i>
        </el-button>
      </el-tooltip>
      <el-tooltip effect="dark" :content="agentEnabled ? '关闭智能体模式' : '开启智能体模式'" placement="bottom-start">
        <el-button
            class="agent-btn"
            :class="{ 'agent-enabled': agentEnabled }"
            style="
            border: 2px solid #40a0ffa8;
            color: #40a0ffa8;
            background: #ecf5ff;
            border-radius: 10px;
            padding: 7px 12px;
          "
            round
            @click="toggleAgent">
          <div class="thinking-indicator">
            <i
                style="font-size: 16px; font-weight: bolder;"
                :class="{
                  'ri-chat-ai-3-line': !agentEnabled,
                  'thinking-animation': agentEnabled && isThinking
                }"
            ></i>
            <div v-if="agentEnabled && isThinking" class="neural-pulse">
              <div class="pulse-dot" style="--delay: 0s"></div>
              <div class="pulse-dot" style="--delay: 0.2s"></div>
              <div class="pulse-dot" style="--delay: 0.4s"></div>
            </div>
          </div>
        </el-button>
      </el-tooltip>
    </div>
  </div>

  <div v-if="isFlashing && loadWikiTitles && loadWikiTitles.length > 0"
       ref="wikiContainerRef"
       class="animate__animated animate__pulse animate__fadeInDown wiki-names-container"
       :style="wikiContainerStyle"
  >
    <div class="wiki-names-list">
      <template v-if="loadWikiTitles.length <= 2">
        <!-- 显示前两个知识库 -->
        <div v-for="(title, index) in loadWikiTitles" :key="index" class="wiki-name-item">
          <i style="margin-right: 3px; font-weight: normal" class="ri-book-marked-line"></i>
          <span class="wiki-title">{{ title }}</span>
        </div>
      </template>
      <template v-else>
        <!-- 显示前两个 + 摘要 -->
        <div v-for="(title, index) in loadWikiTitles.slice(0, 2)" :key="index" class="wiki-name-item">
          <i style="margin-right: 3px; font-weight: normal" class="ri-book-marked-line"></i>
          <span class="wiki-title">{{ title }}</span>
        </div>
        <div class="wiki-name-item wiki-summary">
          <span class="wiki-title">+{{ loadWikiTitles.length - 2 }}</span>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type {ChatHeaderProps} from "@/views/ai/talk/types/talk.ts";
import {ref, watch, nextTick, onUnmounted} from 'vue';

const props = withDefaults(defineProps<ChatHeaderProps>(), {})

// 添加 Agent 相关状态
const agentEnabled = ref(false)
const isThinking = ref(false)
const wikiContainerStyle = ref({})

const emit = defineEmits<{
  'open-chat-list': []
  'open-new-chat': []
  'handle-wiki-click': []
  'close-file-click': []
  'toggle-wiki-drawer': []
  'toggle-agent': [enabled: boolean]
}>()

// 容器引用
const wikiContainerRef = ref<HTMLElement | null>(null)

// 切换 Agent 状态
const toggleAgent = () => {
  agentEnabled.value = !agentEnabled.value
  isThinking.value = agentEnabled.value;
  emit('toggle-agent', agentEnabled.value)
}

// 模拟思考过程
let thinkingInterval: number | null = null

const startThinkingAnimation = () => {
  if (!agentEnabled.value) return
  isThinking.value = true
}

const stopThinkingAnimation = () => {
  if (thinkingInterval) {
    clearInterval(thinkingInterval)
    thinkingInterval = null
  }
  isThinking.value = false
}

// 更新位置
const updatePosition = async () => {
  if (!props.loadWikiTitles?.length) return;

  await nextTick();
  if (wikiContainerRef.value) {
    const width = wikiContainerRef.value.offsetWidth;
    wikiContainerStyle.value = { left: `calc(50% - ${width / 2 - 105}px)` };
  }
};

watch(() => agentEnabled.value, (newVal) => {
  if (newVal) {
    startThinkingAnimation()
  } else {
    stopThinkingAnimation()
  }
}, { immediate: false })

watch(() => [props.loadWikiTitles, props.isFlashing], updatePosition, { immediate: true });

onUnmounted(() => {
  stopThinkingAnimation()
})
</script>

<style scoped>

.wiki-names-container {
  position: absolute;
  top: 11px;
  display: inline-flex;
  font-family: Arial, sans-serif;
  font-size: 16px;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  letter-spacing: 1px;
  left: 50%;
  transform: translateX(-50%);
}

.wiki-names-container:hover {
  background: rgba(187, 220, 253, 0.2);
}

.wiki-names-list {
  display: flex;
  gap: 8px;
  align-items: center;
}

.wiki-name-item {
  display: flex;
  align-items: center;
  padding: 4px 8px;
  background: rgba(64, 160, 255, 0.1);
  border: 1px solid #8ac5ff;
  border-radius: 6px;
  font-size: 12px;
  color: #40a0ff;
  white-space: nowrap; /* 防止内部文本换行 */
}

.wiki-summary {
  background: rgba(255, 165, 0, 0.1);
  border-color: #ffb347;
  color: #ff8c00;
  font-weight: bold;
}

.wiki-title {
  font-weight: 500;
  white-space: nowrap;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 如果只有一个知识库，保持紧凑样式 */
.wiki-names-list:has(.wiki-name-item:first-child:last-child) {
  justify-content: center;
}

/* Agent 按钮整体样式 */
.agent-btn {
  transition: all 0.3s ease;
  position: relative;
  overflow: visible;
  min-width: 45px;
}

.agent-btn::before {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border-radius: 14px;
  background: linear-gradient(45deg, transparent, rgba(64, 160, 255, 0.3), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.agent-btn.agent-enabled::before {
  animation: agent-glow 3s infinite;
  opacity: 1;
}

.agent-btn.agent-enabled {
  background: linear-gradient(135deg, #e6f7ff, #d1eaff);
  border-color: #1890ff !important;
  color: #1890ff !important;
  box-shadow: 0 0 15px rgba(24, 144, 255, 0.3);
}

.thinking-indicator {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.thinking-animation {
  animation: brain-think 1.5s infinite ease-in-out;
}

.neural-pulse {
  position: absolute;
  top: -2px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 2px;
  pointer-events: none;
}

.pulse-dot {
  width: 4px;
  height: 4px;
  background: #1890ff;
  border-radius: 50%;
  animation: neural-pulse 1.5s infinite;
  animation-delay: var(--delay, 0s);
}

@keyframes agent-glow {
  0%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
}

@keyframes brain-think {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(5deg);
  }
  75% {
    transform: rotate(-5deg);
  }
}

@keyframes neural-pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.5);
    opacity: 1;
  }
}
</style>