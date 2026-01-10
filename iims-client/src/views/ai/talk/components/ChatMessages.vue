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
              <div v-if="message.tools && message.tools.length > 0" class="metadata-container">
                <hr class="my-2 border-t border-gray-300 dark:border-gray-600" />
                <div class="metadata-label">使用工具:</div>
                <div class="flex flex-wrap gap-3 chat-metadata-box">
                  <div v-for="(tool, index) in message.tools"
                       :key="index"
                       @click="showToolDetail(tool)"
                       class="group relative inline-flex items-center px-2.5 py-1 rounded-xl bg-gradient-to-r from-blue-50/80 to-indigo-50/80 dark:from-gray-800/60 dark:to-gray-800/40 border border-blue-100/60 dark:border-gray-700/60 backdrop-blur-sm transition-all duration-300 hover:shadow-md cursor-pointer">
                    <div class="flex items-center space-x-2">
                          <span :class="[
                             'inline-flex items-center px-2.5 py-0.5 rounded-full transition-all duration-300 ease-in-out',
                             tool.responseData ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/30 dark:text-emerald-200' :
                             'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-200'
                          ]">
                             <i :class="[tool.responseData ? 'ri-check-double-line' : 'ri-loader-4-line animate-spin', 'text-xs']"></i>
                          </span>
                      <span class="font-sans text-sm font-medium text-gray-700 dark:text-gray-300">
                            {{ tool.name || 'Unknown Tool' }}
                          </span>
                    </div>
                  </div>
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
    <!-- 工具详情弹窗 -->
    <el-dialog
        v-model="showToolDetailDialog"
        :title="`工具详情 - ${selectedToolDetail?.name || '未知工具'}`"
        width="39%"
        :before-close="() => { showToolDetailDialog = false; }"
        destroy-on-close
    >
      <div v-if="selectedToolDetail" class="tool-detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="ID">
            <code>{{ selectedToolDetail.id || 'N/A' }}</code>
          </el-descriptions-item>
          <el-descriptions-item label="类型">
            <code>{{ selectedToolDetail.type || 'N/A' }}</code>
          </el-descriptions-item>
          <el-descriptions-item label="名称">
            <code>{{ selectedToolDetail.name || 'N/A' }}</code>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag
                :type="selectedToolDetail.responseData ? 'success' : 'warning'"
                size="small"
            >
              {{ selectedToolDetail.responseData ? '已执行' : '等待执行' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 参数 -->
        <div class="detail-section" v-if="selectedToolDetail.arguments">
          <h4>调用参数</h4>
          <pre class="json-display">{{ formatJsonContent(selectedToolDetail.arguments) }}</pre>
        </div>

        <!-- 返回结果 -->
        <div class="detail-section" v-if="selectedToolDetail.responseData">
          <h4>返回结果</h4>
          <pre class="json-display">{{ formatJsonContent(selectedToolDetail.responseData) }}</pre>
        </div>

        <!-- 如果没有返回结果但有参数 -->
        <div class="detail-section" v-if="!selectedToolDetail.responseData && selectedToolDetail.arguments">
          <h4>待执行参数</h4>
          <pre class="json-display">{{ formatJsonContent(selectedToolDetail.arguments) }}</pre>
        </div>
      </div>
    </el-dialog>
  </el-main>
</template>

<script setup lang="ts">
import {ref, computed, onMounted, nextTick, watch} from 'vue';
import { Loading } from '@element-plus/icons-vue';
import { useStore } from 'vuex';
import logoAvatar from '@/assets/icons/QJingTalk-logo-avatar.png';
import { getStorage } from '@/utils/auth'
import type {Message, MetadataItem, StatusData, Tool} from "@/views/ai/talk/types/talk.ts";

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
const showToolDetailDialog = ref(false);
const selectedToolDetail = ref<Tool | null>(null);
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

const showToolDetail = (tool: Tool) => {
  selectedToolDetail.value = tool;
  showToolDetailDialog.value = true;
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
      const images = container.querySelectorAll('img');
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
.detail-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 5px;
}

.json-display {
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px;
  overflow-x: auto;
  font-family: 'Courier New', Consolas, Monaco, monospace;
  font-size: 12px;
  line-height: 1.4;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 400px;
  overflow-y: auto;
}

.tool-detail-content {
  max-height: 70vh;
  overflow-y: auto;
}
</style>