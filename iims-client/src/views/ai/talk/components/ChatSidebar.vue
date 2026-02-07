<template>
  <el-aside
      width="210px"
      class="animate__animated animate__fadeIn"
      style="margin: 0; padding: 6px; background-color: #ffffff; box-shadow: 0 2px 8px rgba(0,0,0,0.05);"
      ref="sidebarRef"
  >
    <el-image style="width: 130px; margin: 8px 34px 12px;" :src="logoImg" fit="fill" />

    <!-- 搜索框 -->
    <el-input
        v-model="searchKeyword"
        placeholder="搜索历史对话..."
        clearable
        :prefix-icon="Search"
        style="width: 100%"
    />

    <div
        class="chat-box"
        ref="chatBoxRef"
        @scroll="handleScroll"
    >
      <div v-if="filteredGroupedChatItems.length === 0" class="chat-empty">
        <el-empty description="暂无数据" :image="emptyImage" :image-size="130" />
      </div>

      <!-- 分组渲染 -->
      <div style="margin: 0 3px;" v-for="(group, groupIndex) in filteredGroupedChatItems" :key="groupIndex">
        <!-- 分组标题带折叠按钮 -->
        <div class="group-header">
          <div class="group-title" @click="toggleGroup(getGroupNameByIndex(groupIndex))">
            {{ group.title }}
            <i
                class="ri-arrow-down-s-line group-toggle-icon"
                :class="{ 'collapsed': !getGroupExpandedState(groupIndex) }"
            ></i>
          </div>
        </div>

        <!-- 对话项 - 只在展开状态下显示 -->
        <transition name="slide-fade">
          <div v-show="getGroupExpandedState(groupIndex)" key="chat-items">
            <div v-for="(item, index) in group.items" :key="item.id || index" class="chat-item"
                 :class="{ active: activeTopicId === item.id, moreActive: clickMoreBtnId === item.id }" @click="$emit('switch-topic', item.id)">
              <div class="chat-content">
                <i v-if="loadTopicIdSet.has(item.id)" class="chat-loading text-base ri-loader-4-line animate-spin"></i>
                <p class="chat-title">{{ item.title }}</p>
              </div>
              <el-popover
                  popper-style="border-radius: 8px; padding: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);"
                  placement="bottom-start"
                  trigger="focus"
                  :offset="-3"
                  @hide="handleHide"
                  :show-arrow="false">
                <template #reference>
                  <el-button @click.stop="clickMoreBtn(item.id)" link class="more-btn">
                    <i class="ri-more-fill topic-more-btn"></i>
                  </el-button>
                </template>
                <el-button style="width: 100%; justify-content: flex-start; margin: 0; padding: 6px 10px;" text plain>
                  <i class="ri-skip-up-line" style="margin-right: 6px; color: #606266;"></i>置顶
                </el-button>
                <el-button style="width: 100%; justify-content: flex-start; margin: 0; padding: 6px 10px;" text plain>
                  <i class="ri-archive-stack-line" style="margin-right: 6px; color: #606266;"></i>归档
                </el-button>
                <el-button @click="$emit('rename-chat', item)"
                           style="width: 100%; justify-content: flex-start; margin: 0; padding: 6px 10px;" text plain>
                  <i class="ri-edit-line" style="margin-right: 6px; color: #606266;"></i>重命名
                </el-button>
                <el-button style="width: 100%; justify-content: flex-start; margin: 0; padding: 6px 10px;" text plain>
                  <i class="ri-share-forward-line" style="margin-right: 6px; color: #606266;"></i>分享对话
                </el-button>
                <el-button @click="$emit('del-chat', item.id)"
                           style="width: 100%; justify-content: flex-start; margin: 0; padding: 6px 10px;" text plain>
                  <i class="ri-delete-bin-line" style="margin-right: 6px; color: #606266;"></i>删除对话
                </el-button>
              </el-popover>
            </div>
          </div>
        </transition>
      </div>

      <!-- 加载更多提示 -->
      <div v-if="loadingMore" class="loading-more">
        <span style="margin-left: 8px;">加载中...</span>
      </div>
    </div>

    <!-- 用户信息区域 -->
    <el-popover
        placement="top-start"
        trigger="click"
        popper-class="user-popover"
        width="185px"
        :show-arrow="false"
        :offset="3"
    >
      <template #reference>
        <div class="user-info">
          <avatar-with-token size="default" :src="avatar" />
          <div class="user-text">
            <span class="username">{{ username }}</span>
            <span class="email">{{ email }}</span>
          </div>
        </div>
      </template>

      <div class="user-menu">
        <div class="menu-item" @click="$emit('open-settings')">
          <i class="ri-settings-3-line menu-icon"></i>
          <span>设置</span>
        </div>
        <div class="menu-item" @click="$emit('view-archived')">
          <i class="ri-archive-line menu-icon"></i>
          <span>已归档内容</span>
        </div>
        <div class="menu-item" @click="$emit('view-favorites')">
          <i class="ri-heart-3-line menu-icon"></i>
          <span>已收藏内容</span>
        </div>
        <el-divider style="margin: 6px 0; width: 160px;" />
        <div class="menu-item logout-item" @click="logout()">
          <i class="ri-logout-box-r-line menu-icon"></i>
          <span>退出登录</span>
        </div>
      </div>
    </el-popover>
  </el-aside>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import logo from '@/assets/icons/QJingTalk-logo.png';
import emptyImg from '@/assets/images/empty.png';
import type { ChatItem } from "@/views/ai/talk/types/talk.ts";
import { Search } from "@element-plus/icons-vue";
import { useStore } from 'vuex';
import AvatarWithToken from "@/components/information/AvatarWithToken.vue";
import {useRouter} from "vue-router";

const router = useRouter()
const store = useStore();
const avatar = computed(() => store.getters.avatar);
const username = computed(() => store.getters.username);
const email = computed(() => store.getters.email);
const props = withDefaults(defineProps<{
  chatItems: ChatItem[];
  activeTopicId?: string | null;
  hasMore?: boolean; // 是否还有更多数据
  loadingMore?: boolean; // 是否正在加载更多
  loadTopicIdSet?: Set<string>;
}>(), {
  activeTopicId: null,
  chatItems: () => [],
  hasMore: true,
  loadingMore: false
});

const emit = defineEmits<{
  (e: 'switch-topic', id: string): void;
  (e: 'rename-chat', item: ChatItem): void;
  (e: 'del-chat', id: string): void;
  (e: 'open-settings'): void;
  (e: 'view-archived'): void;
  (e: 'view-favorites'): void;
  (e: 'load-more'): void;
}>();

const logoImg = ref(logo);
const emptyImage = ref(emptyImg);
const searchKeyword = ref('');
const chatBoxRef = ref<HTMLDivElement>();
const sidebarRef = ref<HTMLDivElement>();
const clickMoreBtnId = ref<string | null>(null);

// 定义分组名称数组
const GROUP_NAMES = ['today', 'yesterday', 'last7Days', 'last30Days', 'older'] as const;

const loadTopicIdSet = computed(() => props.loadTopicIdSet || new Set<string>());

// 折叠状态管理
const expandedGroups = ref({
  today: true,
  yesterday: true,
  last7Days: true,
  last30Days: true,
  older: true
});

const handleHide = () => {
  clickMoreBtnId.value = null
}

const clickMoreBtn = (id: string) => {
  clickMoreBtnId.value = id
}

// 防抖函数
const debounce = (func: Function, wait: number) => {
  let timeout: NodeJS.Timeout;
  return function executedFunction(...args: any[]) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

const logout = async () => {
  await store.dispatch('user/logout')
  await router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
}

// 滚动事件处理器
const handleScroll = debounce(() => {
  if (!chatBoxRef.value || !props.hasMore || props.loadingMore) return;

  const { scrollTop, scrollHeight, clientHeight } = chatBoxRef.value;
  // 判断是否滚动到底部（预留10px的容差）
  if (scrollTop + clientHeight >= scrollHeight - 10) {
    emit('load-more');
  }
}, 200);

// 计算属性保持不变
const groupedChatItems = computed(() => {
  if (!props.chatItems || props.chatItems.length === 0) {
    return [];
  }

  const groups: {
    [key: string]: { title: string; items: ChatItem[] };
  } = {
    today: { title: '今天', items: [] },
    yesterday: { title: '昨天', items: [] },
    last7Days: { title: '过去7天', items: [] },
    last30Days: { title: '过去30天', items: [] },
    older: { title: '更早', items: [] },
  };

  const parseTime = (timeStr: string) => {
    const [datePart, timePart] = timeStr.split(' ');
    const [year, month, day] = datePart.split('-').map(Number);
    const [hour, minute, second] = timePart.split(':').map(Number);
    return new Date(year, month - 1, day, hour, minute, second);
  };

  for (const item of props.chatItems) {
    const now = new Date();
    const createTime = parseTime(item.createTime);
    const diffMs = now.getTime() - createTime.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    if (diffDays === 0) {
      groups.today.items.push(item);
    } else if (diffDays === 1) {
      groups.yesterday.items.push(item);
    } else if (diffDays <= 7) {
      groups.last7Days.items.push(item);
    } else if (diffDays <= 30) {
      groups.last30Days.items.push(item);
    } else {
      groups.older.items.push(item);
    }
  }

  return [
    { key: 'today', ...groups.today },
    { key: 'yesterday', ...groups.yesterday },
    { key: 'last7Days', ...groups.last7Days },
    { key: 'last30Days', ...groups.last30Days },
    { key: 'older', ...groups.older }
  ].filter(group => group.items.length > 0);
});

const filteredGroupedChatItems = computed(() => {
  if (!searchKeyword.value.trim()) {
    return groupedChatItems.value.map(group => ({ ...group }));
  }

  const keyword = searchKeyword.value.toLowerCase().trim();

  return groupedChatItems.value
      .map(group => ({
        ...group,
        items: group.items.filter(item =>
            item.title.toLowerCase().includes(keyword)
        )
      }))
      .filter(group => group.items.length > 0);
});

// 根据索引获取分组名称
const getGroupNameByIndex = (index: number): typeof GROUP_NAMES[number] => {
  return GROUP_NAMES[index];
};

// 获取分组展开状态
const getGroupExpandedState = (index: number): boolean => {
  const groupName = getGroupNameByIndex(index);
  return expandedGroups.value[groupName];
};

// 切换分组折叠状态
const toggleGroup = (groupName: typeof GROUP_NAMES[number]) => {
  expandedGroups.value[groupName] = !expandedGroups.value[groupName];
};

onMounted(() => {
  // 添加滚动事件监听器
  if (chatBoxRef.value) {
    chatBoxRef.value.addEventListener('scroll', handleScroll);
  }
});

onUnmounted(() => {
  // 移除滚动事件监听器
  if (chatBoxRef.value) {
    chatBoxRef.value.removeEventListener('scroll', handleScroll);
  }
});
</script>

<style scoped>
/* 保持原有的样式，添加加载更多的样式 */
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  color: #909399;
  font-size: 12px;
}

.group-header {
  display: flex;
  align-items: center;
  margin: 3px 0;
}

.group-title {
  font-weight: 600;
  color: #409EFF;
  padding: 8px 12px;
  font-size: 13px;
  margin-top: 4px;
  margin-bottom: 4px;
  border-left: 3px solid #409EFF;
  background-color: #f8fbff;
  border-radius: 2px 4px 4px 2px;
  cursor: pointer;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.group-toggle-icon {
  transition: transform 0.2s ease;
  font-size: 14px;
  color: #409EFF;
}

.group-toggle-icon.collapsed {
  transform: rotate(-90deg);
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.1s ease;
  margin: 3px 4px;
  border: none;
}

.chat-item:hover {
  background-color: #f0f9ff;
}

.chat-item.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409EFF;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.1);
}

.chat-item.moreActive {
  background-color: #ecf5ff;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.1);
}

.chat-item.moreActive .more-btn {
  opacity: 1;
}

.chat-item.moreActive .topic-more-btn {
  color: #409EFF;
}

.chat-box {
  height: calc(100vh - 180px); /* 调整高度以适应滚动 */
  overflow-y: auto;
  overflow-x: hidden;
}

.chat-content {
  flex: 1;
  gap: 3px;
  overflow: hidden;
  display: flex;
  align-items: center;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-left: 6px;
}

.chat-title {
  margin: 0;
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s;
}

.chat-item:hover .chat-title {
  color: #409EFF;
}

.chat-item.active .chat-title {
  color: #409EFF;
  font-weight: 500;
}

.chat-item.active .chat-loading {
  color: #409EFF;
  font-weight: 500;
}

.chat-item.moreActive .chat-title {
  color: #409EFF;
  font-weight: 500;
}

.more-btn {
  opacity: 0;
  transition: opacity 0.2s;
}

.chat-item:hover .more-btn {
  opacity: 1;
}

.topic-more-btn {
  color: #909399;
  font-size: 16px;
  transition: color 0.2s;
}

.chat-item:hover .topic-more-btn {
  color: #409EFF;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 10px 12px 4px;
  cursor: pointer;
  border-radius: 6px;
  transition: background-color 0.2s;
  margin: 0 6px;
}

.user-info:hover {
  background-color: #f8fbff;
}

.user-text {
  display: flex;
  flex-direction: column;
  margin-left: 10px;
  flex: 1;
}

.username {
  width: 110px;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.email {
  width: 110px;
  font-size: 12px;
  color: #909399;
  line-height: 1.2;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 用户菜单样式 */
.user-menu {
  min-width: 180px;
}

.menu-item {
  width: calc(100% - 20px);
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s, color 0.2s;
  margin: 2px 0;
  font-size: 14px;
  color: #606266;
}

.menu-item:hover {
  background-color: #f8fbff;
  color: #409EFF;
}

.logout-item:hover {
  background-color: #fef0f0;
  color: #f56c6c;
}

.menu-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #909399;
  transition: color 0.2s;
}

.menu-item:hover .menu-icon {
  color: inherit;
}

.logout-item:hover .menu-icon {
  color: #f56c6c;
}

:deep(.user-popover) {
  border-radius: 8px !important;
  padding: 8px 0 !important;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1) !important;
}
</style>