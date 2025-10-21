<template>
  <el-aside width="210px" class="animate__animated animate__pulse animate__fadeIn"
            style="margin: 0; padding: 6px; background-color: #ffffff">
    <el-image style="width: 130px; margin: 2px 34px" :src="logoImg" fit="fill" />
    <div class="chat-box">
      <div v-if="chatItems.length === 0" class="chat-empty">
        <el-empty description="暂无数据" :image="emptyImage" :image-size="130" />
      </div>
      <div v-for="(item, index) in chatItems" :key="index" class="chat-item"
           :class="{ active: activeTopicId === item.id }" @click="$emit('switch-topic', item.id)">
        <i class="ri-message-3-line" style="font-size: 29px; margin-left: 3px"></i>
        <div class="chat-content">
          <p>{{ item.title }}</p>
        </div>
        <el-popover popper-style="border-radius: 10px; padding: 7px;" placement="bottom-start" trigger="click"
                    :offset="-3" :show-arrow="false">
          <template #reference>
            <el-button link><i class="ri-more-fill topic-more-btn"></i></el-button>
          </template>
          <el-button style="width: 100%; justify-content: flex-start" text plain>
            <i class="ri-skip-up-line" style="margin-right: 6px"></i>置顶
          </el-button>
          <el-button @click="$emit('rename-chat', item)" style="width: 100%; justify-content: flex-start; margin: 0" text plain>
            <i class="ri-edit-line" style="margin-right: 6px"></i>重命名
          </el-button>
          <el-button style="width: 100%; justify-content: flex-start; margin: 0" text plain>
            <i class="ri-share-forward-line" style="margin-right: 6px"></i>分享对话
          </el-button>
          <el-button @click="$emit('del-chat', item.id)" style="width: 100%; justify-content: flex-start; margin: 0" text plain>
            <i class="ri-delete-bin-line" style="margin-right: 6px"></i>删除对话
          </el-button>
        </el-popover>
      </div>
    </div>
    <el-divider />
    <el-button class="btn-manage-chat">管理对话记录</el-button>
  </el-aside>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import logo from '@/assets/icons/QJingTalk-logo.png';
import emptyImg from '@/assets/images/empty.png';
import type {ChatItem} from "@/views/ai/talk/types/talk.ts";


withDefaults(defineProps<{
  chatItems: ChatItem[];
  activeTopicId?: string | null;
}>(), {
  activeTopicId: null
});

defineEmits<{
  (e: 'switch-topic', id: string ): void;
  (e: 'rename-chat', item: ChatItem): void;
  (e: 'del-chat', id: string): void;
}>();
const logoImg = ref(logo);
const emptyImage = ref(emptyImg);
</script>