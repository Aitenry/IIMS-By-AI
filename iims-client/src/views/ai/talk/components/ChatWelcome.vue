<!-- src/views/chat/components/ChatWelcome.vue -->
<template>
  <el-card class="box-card animate__animated animate__pulse animate__fadeIn">
    <template #header>
      <div class="clearfix">
        <span style="color: #409eff">QJingTalk <span style="font-size: 12px">V1.3.10</span></span>
        <span class="card-title" type="text">以凡人之心，比肩浩瀚星辰！</span>
      </div>
    </template>
    <div>
      <el-row :gutter="10">
        <el-col class="animate__animated animate__pulse animate__fadeInUp" :span="8">
          <div class="grid-content bg-purple-b1">
            <div style="
                display: grid;
                align-items: center;
                width: 100%;
                margin-bottom: 10px;
              ">
              <span style="font-weight: bold; margin-bottom: 3px">话题热榜</span>
              <span style="font-size: 12px; color: #8f91a8">深度搜索您感兴趣的话题</span>
            </div>
            <div>
              <div v-for="(topic, index) in hotTopics" :key="index" class="hot-box">
                <span style="
                    font-weight: bold;
                    font-size: 14px;
                    margin-right: 6px;
                    font-style: italic;
                  " :class="topicColorClass(topic)">{{ topic.rank }}</span>
                <el-tooltip :show-arrow="false" :content="topic.title" popper-class="tips-tooltip" :visible-arrow="false"
                            placement="bottom-start">
                  <span class="hot-title">{{ topic.title }}</span>
                </el-tooltip>
              </div>
            </div>
          </div>
        </el-col>
        <el-col class="animate__animated animate__pulse animate__fadeInDown" :span="8">
          <div class="grid-content bg-purple-b2">
            <div style="
                display: grid;
                align-items: center;
                width: 100%;
                margin-bottom: 10px;
              ">
              <span style="font-weight: bold; margin-bottom: 3px">效率工具</span>
              <span style="font-size: 12px; color: #8f91a8">专注于办公与学习的工具箱</span>
            </div>
            <div>
              <div v-for="(tool, index) in tools" :key="index" class="tool-box">
                <el-avatar shape="square" style="width: 32px; height: 32px;" :src="tool.avatarSrc" />
                <div style="
                    display: grid;
                    align-items: center;
                    width: calc(100% - 46px);
                    margin-left: 10px;
                  ">
                  <span style="
                      font-weight: bold;
                      margin-bottom: 6px;
                      font-size: 13px;
                    ">{{ tool.name }}</span>
                  <span class="tool-content">{{ tool.content }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col class="animate__animated animate__pulse animate__fadeInUp" :span="8">
          <div class="grid-content bg-purple-b3">
            <div style="
                display: grid;
                align-items: center;
                width: 100%;
                margin-bottom: 10px;
              ">
              <span style="font-weight: bold; margin-bottom: 3px">常用智能体</span>
              <span style="font-size: 12px; color: #8f91a8">罗列出常用智能体，方便调用</span>
            </div>
            <div>
              <div v-for="(agent, index) in agents" :key="index" class="agent-box">
                <el-avatar shape="square" style="width: 32px; height: 32px;" :src="agent.avatarSrc" />
                <div style="
                    display: grid;
                    align-items: center;
                    width: calc(100% - 46px);
                    margin-left: 10px;
                  ">
                  <span style="
                      font-weight: bold;
                      margin-bottom: 6px;
                      font-size: 13px;
                    ">{{ agent.name }}</span>
                  <span class="agent-content">{{ agent.content }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-card>
</template>

<script setup lang="ts">

import type {AgentTopic, ToolTopic, HotTopic} from "@/views/ai/talk/types/talk.ts";

defineProps<{
  hotTopics: HotTopic[];
  tools: ToolTopic[];
  agents: AgentTopic[];
}>();

const topicColorClass = (topic: HotTopic): string => {
  switch (topic.rank) {
    case 1:
      return 'red';
    case 2:
      return 'light-red';
    case 3:
      return 'medium-red';
    default:
      return 'gray';
  }
};
</script>