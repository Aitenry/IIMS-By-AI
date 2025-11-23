<template>
  <el-main ref="mainContainerRef" @scroll="handleScroll" class="messages">
    <div class="infinite-list">
      <el-row v-for="(message, index) in reactiveMessages" :key="index" class="chat-row"
              :style="{ 'margin-top': index > 0 ? '16px' : '0' }">
        <el-col v-if="message.sender === 'user'" :span="16" :offset="4">
          <div style="
              display: flex;
              align-items: flex-start;
              justify-content: end;
            ">
            <div class="info info-user">
              <div class="markdown-body" v-html="loadHtml(message.content)" />
            </div>
            <el-avatar shape="square" size="small" style="min-width: 40px; min-height: 40px" :src="avatar" />
          </div>
          <!-- 文件卡片 -->
          <div v-if="message?.fileInfos?.length" class="file-cards-container">
            <div v-for="(file, index) in message.fileInfos" :key="index" class="file-card">
              <div class="file-icon">
                <i :class="getFileIconClass(file.filename)"></i>
              </div>
              <div class="file-info">
                <div class="file-name">{{ file.filename }}</div>
                <div class="file-details">
                  <span>{{ formatFileSize(file.fileSize) }}</span>
                  <span>{{ getFileType(file.filename) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col v-else :span="16" :offset="4">
          <div style="
              display: flex;
              align-items: flex-start;
              justify-content: start;
            ">
            <el-avatar shape="square" size="small" style="min-width: 40px; min-height: 40px" :src="logoAvatarImage" />
            <div class="info info-ai">
              <div v-if="message.isShowThink" style="margin-bottom: 6px !important;">
                <strong @click="$emit('toggle-think', message)" class="chat-think-container collapsible" tabindex="0">
                  <i :class="'ri-' + (message.isComplete ? 'verified-badge' : 'donut-chart') + '-fill chat-think-icon'"></i>
                  <span class="chat-think-text">{{ message.isComplete ? '思考过程' : '思考中...' }}</span>
                  <i :class="['ri-arrow-down-s-line', 'collapse-icon', { rotated: message.isExpanded }]"></i>
                </strong>
                <div class="chat-think-box collapse-content markdown-body" :class="{ collapsed: !message.isExpanded }" v-html="message.think" />
              </div>
              <div class="markdown-body" v-html="message.view" />
              <div v-if="message.isLoadingAnswer" style="display: flex; align-items: center">
                <el-icon class="is-loading">
                  <Loading />
                </el-icon>
                <div v-if="statusData" style="
                    display: flex;
                    align-items: center;
                    margin-left: 3px;
                    font-size: 10px;
                  ">
                  <span>{{ statusData.task }}</span>
                  <span style="margin-left: 6px; color: rgb(51.2, 126.4, 204)">{{ statusData.progress }}/{{
                      statusData.total
                    }}</span>
                </div>
              </div>
              <div v-if="message.docMetadata" class="metadata-container">
                <div class="metadata-label">参考文献:</div>
                <div v-if="message.docMetadata.length > 0" class="chat-metadata-box">
                  <div v-for="(meta, index) in message.docMetadata" :key="index"
                       @click="$emit('show-wiki-doc-metadata', meta)" class="metadata-item">
                    {{ meta.metadata.name }}
                  </div>
                </div>
                <div v-else class="no-metadata">暂无参考文献</div>
              </div>
              <div class="chat-btn-box">
                <el-button class="chat-btn" @click="
                  $emit('del-message', message.lastId, message.isStar, index)
                  "><i class="ri-delete-bin-line"></i></el-button>
                <el-button class="chat-btn" @click="$emit('copy-content', message.content)"><i
                    class="ri-file-copy-2-line"></i></el-button>
                <el-button class="chat-btn" @click="$emit('thumb-status', message.feedbackStatus, 1, index)">
                  <i :class="'ri-thumb-up-' + (message.feedbackStatus === 1 ? 'fill' : 'line')
                    "></i>
                </el-button>
                <el-button class="chat-btn" @click="$emit('thumb-status', message.feedbackStatus, -1, index)">
                  <i :class="'ri-thumb-down-' + (message.feedbackStatus === -1 ? 'fill' : 'line')
                    "></i>
                </el-button>
                <el-button class="chat-btn"><i class="ri-menu-4-line"></i></el-button>
                <el-button class="chat-btn" @click="$emit('star-ai-chat', message, index)">
                  <i :class="'ri-star-' + (message.isStar ? 'fill' : 'line')
                    "></i>
                </el-button>
                <el-button class="chat-btn"><i class="ri-refresh-line"></i></el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-backtop target=".messages" style="right: calc(13% - 20px); bottom: 130px" :visibility-height="300" />
    </div>
  </el-main>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { Loading } from '@element-plus/icons-vue';
import { useStore } from 'vuex';
import logoAvatar from '@/assets/icons/QJingTalk-logo-avatar.png';
import type {Message, MetadataItem, StatusData} from "@/views/ai/talk/types/talk.ts";

const props = withDefaults(defineProps<{
  messages: Message[];
  isSendOut: boolean;
  statusData?: StatusData | null;
  isLoadDialogueMore: boolean;
  mdi: any;
}>(), {
  statusData: null
});

const emit = defineEmits<{
  (e: 'load-more'): void;
  (e: 'del-message', lastId: string | null, isStar: boolean, index: number): void;
  (e: 'copy-content', content: string): void;
  (e: 'thumb-status', feedbackStatus: number, status: number, index: number): void;
  (e: 'star-ai-chat', message: Message, index: number): void;
  (e: 'show-wiki-doc-metadata', meta: MetadataItem): void;
  (e: 'toggle-think', message: Message): void;
}>();

const store = useStore();
const mainContainerRef = ref<HTMLElement | null>(null);

// Computed properties
const avatar = computed(() => store.getters.avatar);
const reactiveMessages = computed(() => [...props.messages]);
const logoAvatarImage = ref(logoAvatar)

const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement;
  if (target.scrollTop < 3) {
    emit('load-more');
  }
};

const loadHtml = (content: string) => {
  return props.mdi.render(content);
};

// 根据文件名获取文件图标
const getFileIconClass = (filename: string) => {
  const ext = filename.split('.').pop()?.toLowerCase() || '';
  const icons: Record<string, string> = {
    pdf: 'ri-file-pdf-2-line',
    png: 'ri-file-image-line',
    jpg: 'ri-file-image-line',
    pptx: 'ri-file-ppt-2-line',
    ppt: 'ri-file-ppt-2-line',
    jpeg: 'ri-file-image-line',
    doc: 'ri-file-word-2-line',
    docx: 'ri-file-word-2-line',
    xls: 'ri-file-excel-2-line',
    xlsx: 'ri-file-excel-2-line',
    md: 'ri-markdown-line',
    default: 'ri-file-unknow-line',
  };
  return icons[ext] || icons.default;
};

// 格式化文件大小
const formatFileSize = (sizeInBytes: number) => {
  if (sizeInBytes < 1024) return `${sizeInBytes} B`;
  if (sizeInBytes < 1024 * 1024) return `${(sizeInBytes / 1024).toFixed(1)} KB`;
  return `${(sizeInBytes / (1024 * 1024)).toFixed(1)} MB`;
};

// 获取文件类型
const getFileType = (filename: string) => {
  return filename.split('.').pop()?.toUpperCase() || '';
};

defineExpose({
  mainContainerRef
});
</script>

<style scoped>
/* Add scoped styles if needed, or move global ones from original */
</style>