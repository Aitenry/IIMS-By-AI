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
              <div class="markdown-body" v-html="loadHtml(message?.userContent?.question || '')" />
            </div>
            <avatar-with-token shape="square" size="small" style="min-width: 40px; min-height: 40px" :src="avatar" />
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
              <div v-for="(content, index) in message.aiContent" :key="index">
                <div v-for="(result, index) in content.contentResult" :key="index">
                  <div v-if="result.type === 'reasoning'" style="margin-bottom: 6px !important;">
                    <strong @click="$emit('toggle-think', result)" class="chat-think-container collapsible" tabindex="0">
                      <i :class="'ri-' + (result.isComplete ? 'verified-badge' : 'donut-chart') + '-fill chat-think-icon'"></i>
                      <span class="chat-think-text">{{ result.isComplete ? '思考过程' : '思考中...' }}</span>
                      <i :class="['ri-arrow-down-s-line', 'collapse-icon', { rotated: result.isExpanded }]"></i>
                    </strong>
                    <div class="chat-think-box collapse-content markdown-body" :class="{ collapsed: !result.isExpanded }" v-html="result.view" />
                  </div>
                  <div v-else-if="result.type === 'text'" class="markdown-body" v-html="result.view" />
                </div>
                <el-collapse v-if="content.tools && content.tools.length > 0" class="tool-collapse">
                  <el-collapse-item v-for="(tool, index) in content.tools" :key="index" :name="index">
                    <template #title>
                      <div class="tool-title-bar">
                        <div class="tool-icon-box" :class="{ 'completed': tool.responseData }">
                          <i :class="[
                              'ri',
                              tool.responseData ? 'ri-check-double-line' : 'ri-loader-4-line animate-spin'
                            ]"></i>
                        </div>
                        <span class="tool-name">{{ tool.name || 'Unknown Tool' }}</span>
                        <el-tag size="small" :type="tool.responseData ? 'success' : 'info'" class="tool-status">
                          {{ tool.responseData ? '已完成' : '执行中' }}
                        </el-tag>
                      </div>
                    </template>

                    <!-- 调用参数 -->
                    <div class="tool-section" v-if="tool.arguments">
                      <div class="section-header">
                        <span class="section-label">调用参数</span>
                      </div>
                      <pre class="tool-json-content">{{ formatJsonContent(tool.arguments) }}</pre>
                    </div>

                    <!-- 返回结果 -->
                    <div class="tool-section mt-3" v-if="tool.responseData">
                      <div class="section-header">
                        <span class="section-label">返回结果</span>
                      </div>
                      <pre class="tool-json-content result">{{ formatJsonContent(tool.responseData) }}</pre>
                    </div>

                    <!-- 待执行参数 -->
                    <div class="tool-section mt-3" v-if="!tool.responseData && tool.arguments">
                      <div class="section-header">
                        <span class="section-label">待执行参数</span>
                      </div>
                      <pre class="tool-json-content pending">{{ formatJsonContent(tool.arguments) }}</pre>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>

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
                <hr class="my-2 border-t border-gray-300 dark:border-gray-600" />
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
                <el-button class="chat-btn" @click="$emit('copy-content', message)"><i
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
import {ref, computed, onMounted, nextTick, watch} from 'vue';
import { Loading } from '@element-plus/icons-vue';
import { useStore } from 'vuex';
import logoAvatar from '@/assets/icons/QJingTalk-logo-avatar.png';
import { getStorage } from '@/utils/auth'
import type {Message, MetadataItem, StatusData} from "@/views/ai/talk/types/talk.ts";
import AvatarWithToken from "@/components/information/AvatarWithToken.vue";
import type {ParseResult} from "@/utils/parse-reasoning.ts";

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
  (e: 'copy-content', message: Message): void;
  (e: 'thumb-status', feedbackStatus: number, status: number, index: number): void;
  (e: 'star-ai-chat', message: Message, index: number): void;
  (e: 'show-wiki-doc-metadata', meta: MetadataItem): void;
  (e: 'toggle-think', parseResult: ParseResult): void;
}>();
const store = useStore();
const mainContainerRef = ref<any | null>(null);

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

const formatJsonContent = (content: string | null) => {
  if (!content) return '';

  try {
    // 尝试解析 JSON 字符串并格式化
    const parsed = JSON.parse(content);
    return JSON.stringify(parsed, null, 2);
  } catch (e) {
    return content;
  }
};

// 设置图片安全加载监听器
const setupImageSecurity = () => {
  // 使用setTimeout确保DOM已经更新
  setTimeout(() => {
    const container = mainContainerRef.value?.$el || mainContainerRef.value;
    if (container instanceof HTMLElement) {
      // 只选择 markdown-body 类下的图片
      const markdownContainers = container.querySelectorAll('.markdown-body');

      markdownContainers.forEach(markdownContainer => {
        const images = markdownContainer.querySelectorAll('img');
        images.forEach(img => {
          const originalSrc = img.getAttribute('data-original-src') || img.getAttribute('src');
          if (originalSrc) {
            // 存储原始src用于后续比较
            img.setAttribute('data-original-src', originalSrc);
            loadImageWithToken(originalSrc, img);
          }

          // 监听 src 属性变化
          const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
              if (mutation.type === 'attributes' && mutation.attributeName === 'src') {
                const newSrc = img.getAttribute('src');
                const originalSrc = img.getAttribute('data-original-src');
                // 只有当src发生变化且不是我们设置的blob URL时才重新加载
                if (newSrc && originalSrc && !newSrc.startsWith('blob:')) {
                  img.setAttribute('data-original-src', newSrc);
                  loadImageWithToken(newSrc, img);
                }
              }
            });
          });

          observer.observe(img, {
            attributes: true,
            attributeFilter: ['src']
          });
        });
      });
    }
  }, 100);
};

// 加载带token的图片
const loadImageWithToken = async (src: string, imgElement: HTMLImageElement) => {
  try {
    const token = getStorage('token'); // 获取认证token
    if (!token) {
      // 如果没有token，则直接使用原始src
      imgElement.src = src;
      return;
    }

    // 创建带认证头的fetch请求
    const response = await fetch(src, {
      headers: {
        'token': token
      }
    })

    const blob = await response.blob();
    const objectUrl = URL.createObjectURL(blob);

    // 清理之前的object URL
    if (imgElement.src.startsWith('blob:')) {
      URL.revokeObjectURL(imgElement.src);
    }

    imgElement.src = objectUrl;
  } catch (error) {
    console.error('Failed to load image with token:', error);
    // 失败时回退到原始src
    imgElement.src = src;
  }
};

defineExpose({
  mainContainerRef
});

onMounted(async () => {
  // 初始设置
  await nextTick();
  setupImageSecurity();

  // 如果消息列表发生变化，重新设置
  watch(
      () => props.messages,
      () => {
        nextTick(() => {
          setupImageSecurity();
        });
      },
      { deep: true }
  );
});

</script>

<style scoped>
.tool-collapse {
  margin-bottom: 10px;
  border-radius: 6px;
  overflow: hidden;
}

.tool-title-bar {
  display: flex;
  align-items: center;
  padding: 1px 12px;
  font-size: 13px;
  color: #374151;
}

.tool-icon-box {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  margin-right: 8px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.tool-icon-box.completed {
  background-color: #d1fae5;
  color: #059669;
}

.tool-icon-box:not(.completed) {
  background-color: #dbeafe;
  color: #2563eb;
}

.tool-name {
  flex: 1;
  font-weight: 500;
}

.tool-status {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
}

.tool-section {
  padding: 0 12px;
  background-color: #ffffff;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
}

.section-label {
  text-transform: uppercase;
  letter-spacing: 1px;
}

.tool-json-content {
  margin: 0;
  padding: 8px 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-family: 'Fira Code', monospace;
  font-size: 12px;
  line-height: 1.5;
  color: #4a5568;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 150px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #cbd5e1 #f8fafc;
}

.tool-json-content.result {
  background: #f0fdf4;
  border-color: #bbf7d0;
  color: #2d3748;
}

.tool-json-content.pending {
  background: #eff6ff;
  border-color: #bfdbfe;
  color: #2d3748;
}

.el-collapse-icon-position-right .el-collapse-item__header {
  padding-right: 0;
}

/* 滚动条样式 */
.tool-json-content::-webkit-scrollbar {
  height: 4px;
}

.tool-json-content::-webkit-scrollbar-track {
  background: #f8fafc;
  border-radius: 4px;
}

.tool-json-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.tool-json-content::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>