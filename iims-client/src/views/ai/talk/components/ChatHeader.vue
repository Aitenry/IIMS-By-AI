<template>
  <div style="
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin: 10px;
    ">
    <div style="display: flex; align-items: center; gap: 8px;">
      <!-- 原有的聊天列表按钮 -->
      <el-button style="
          border: 2px solid #8ac5ff;
          color: #40a0ffa8;
          background: #ecf5ff;
          border-radius: 12px;
        " @click="emit('open-chat-list')" circle>
        <i :class="'ri-indent-' + (isOpenChatList ? 'decrease' : 'increase')"></i>
      </el-button>
      <AIMultiLevelSelector
          :button-style="{
            border: '2px solid #8ac5ff',
            color: '#40a0ffa8',
            background: '#ecf5ff',
            borderRadius: '12px'
          }"
          :models="endpointList"
          @selection-changed="handleModelSelectionChanged"
      />
    </div>
    <div class="animate__animated animate__pulse animate__fadeInRight">
      <el-tooltip :show-arrow="false" effect="dark" content="开启新对话" placement="bottom-start">
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
      <el-tooltip :show-arrow="false" effect="dark" :content="'知识库：' + wikiStatusDecl" placement="bottom-start">
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
import {ref, watch, nextTick, onMounted} from 'vue';
import AIMultiLevelSelector from "@/views/ai/talk/components/AIMultiLevelSelector.vue";
import {getEndpointList} from "@/api/ai/chat.ts";

const props = withDefaults(defineProps<ChatHeaderProps>(), {})
const endpointList = ref([])

const wikiContainerStyle = ref({})

const emit = defineEmits<{
  'open-chat-list': []
  'open-new-chat': []
  'handle-wiki-click': []
  'close-file-click': []
  'toggle-wiki-drawer': []
}>()

// 容器引用
const wikiContainerRef = ref<HTMLElement | null>(null)

const handleModelSelectionChanged = (model: any) => {
  props.onModelSelectionChanged(model)
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

watch(() => [props.loadWikiTitles, props.isFlashing], updatePosition, { immediate: true });

onMounted(() => {
  initEndpointList()
})

const initEndpointList = async () => {
  const res = await getEndpointList()
  if (res.code === 1) {
    endpointList.value = res.data
  }
}
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
</style>