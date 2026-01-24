<template>
  <el-aside width="210px" class="animate__animated animate__fadeIn"
            style="margin: 0; padding: 6px; background-color: #ffffff; box-shadow: 0 2px 8px rgba(0,0,0,0.05);">
    <el-image style="width: 130px; margin: 8px 34px 12px;" :src="logoImg" fit="fill" />

    <!-- 搜索框 -->
    <el-input
        v-model="searchKeyword"
        placeholder="搜索历史对话..."
        clearable
        :prefix-icon="Search"
        style="width: 100%"
    />

    <div class="chat-box">
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
                 :class="{ active: activeTopicId === item.id }" @click="$emit('switch-topic', item.id)">
              <div class="chat-content">
                <p class="chat-title">{{ item.title }}</p>
              </div>
              <el-popover popper-style="border-radius: 8px; padding: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);"
                          placement="bottom-start" trigger="click"
                          :offset="-3" :show-arrow="false">
                <template #reference>
                  <el-button @click.stop link class="more-btn"><i class="ri-more-fill topic-more-btn"></i></el-button>
                </template>
                <el-button style="width: 100%; justify-content: flex-start; margin: 0; padding: 6px 10px;" text plain>
                  <i class="ri-skip-up-line" style="margin-right: 6px; color: #606266;"></i>置顶
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
                  <i class="ri-delete-bin-line" style="margin-right: 6px; color: #F56C6C;"></i>删除对话
                </el-button>
              </el-popover>
            </div>
          </div>
        </transition>
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
          <span>收藏内容</span>
        </div>
        <el-divider style="margin: 6px 0; width: 160px;" />
        <div class="menu-item logout-item" @click="$emit('logout')">
          <i class="ri-logout-box-r-line menu-icon"></i>
          <span>退出登录</span>
        </div>
      </div>
    </el-popover>
  </el-aside>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import logo from '@/assets/icons/QJingTalk-logo.png';
import emptyImg from '@/assets/images/empty.png';
import type { ChatItem } from "@/views/ai/talk/types/talk.ts";
import { Search } from "@element-plus/icons-vue";
import { useStore } from 'vuex';
import AvatarWithToken from "@/components/information/AvatarWithToken.vue";

const store = useStore();
const avatar = computed(() => store.getters.avatar);
const username = computed(() => store.getters.username);
const email = computed(() => store.getters.email);
const props = withDefaults(defineProps<{
  chatItems: ChatItem[];
  activeTopicId?: string | null;
}>(), {
  activeTopicId: null,
  chatItems: () => []
});

defineEmits<{
  (e: 'switch-topic', id: string): void;
  (e: 'rename-chat', item: ChatItem): void;
  (e: 'del-chat', id: string): void;
  (e: 'open-settings'): void;
  (e: 'view-archived'): void;
  (e: 'view-favorites'): void;
  (e: 'logout'): void;
}>();

const logoImg = ref(logo);
const emptyImage = ref(emptyImg);
const searchKeyword = ref('');

// 定义分组名称数组
const GROUP_NAMES = ['today', 'yesterday', 'last7Days', 'last30Days', 'older'] as const;

// 折叠状态管理 - 使用数组替代对象
const expandedGroups = ref({
  today: true,
  yesterday: true,
  last7Days: true,
  last30Days: true,
  older: true
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

// 计算分组后的聊天记录
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

  // 辅助函数：将字符串时间转为 Date 对象
  const parseTime = (timeStr: string) => {
    // 确保使用本地时区解析
    const [datePart, timePart] = timeStr.split(' ');
    const [year, month, day] = datePart.split('-').map(Number);
    const [hour, minute, second] = timePart.split(':').map(Number);

    // 使用本地时区构造日期
    return new Date(year, month - 1, day, hour, minute, second);
  };

  // 遍历所有聊天项，按时间分组
  for (const item of props.chatItems) {
    const now = new Date(); // 当前时间
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

  // 返回非空分组（按顺序）
  return [
    { key: 'today', ...groups.today },
    { key: 'yesterday', ...groups.yesterday },
    { key: 'last7Days', ...groups.last7Days },
    { key: 'last30Days', ...groups.last30Days },
    { key: 'older', ...groups.older }
  ].filter(group => group.items.length > 0);
});

// 过滤搜索结果
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
      .filter(group => group.items.length > 0); // 只返回包含匹配项的分组
});
</script>

<style scoped>
.group-header {
  display: flex;
  align-items: center;
  margin: 3px 0;
}

.group-title {
  font-weight: 600;
  color: #409EFF; /* 主题蓝色 */
  padding: 8px 12px;
  font-size: 13px;
  margin-top: 4px;
  margin-bottom: 4px;
  border-left: 3px solid #409EFF;
  background-color: #f8fbff; /* 淡蓝色背景 */
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
  background-color: #f0f9ff; /* 淡蓝色悬停背景 */
}

.chat-item.active {
  background-color: #ecf5ff; /* 激活状态淡蓝色背景 */
  border-left: 3px solid #409EFF; /* 左侧蓝色激活条 */
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.1);
}

.chat-content {
  flex: 1;
  overflow: hidden;
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

/* 用户信息样式 */
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

/* 自定义Popover样式 */
:deep(.user-popover) {
  border-radius: 8px !important;
  padding: 8px 0 !important;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1) !important;
}
</style>