<template>
  <el-drawer v-model="visible" direction="rtl" size="470" class="wiki-drawer">
    <template #header>
      <div style="display: grid; justify-content: center; margin-left: 15px">
        <p style="margin-top: 0; text-align: center">选择所需的知识库</p>
        <el-input
            v-model="localSearchWikiName"
            @input="onSearchInput"
            clearable
            style="width: 290px; margin-top: 10px"
            placeholder="请输入需要检索的知识库名称！"
            prefix-icon="Search"
        >
          <template #append>
            <el-button @click="getWikiTableData">
              <i class="ri-restart-fill"></i>
            </el-button>
          </template>
        </el-input>
      </div>
    </template>
    <div v-if="!wikis || wikis.length === 0" class="chat-empty">
      <el-empty description="暂无数据" :image="emptyImg" :image-size="160" />
    </div>
    <el-scrollbar v-else style="margin: 0 10px" height="calc(100vh - 100px)">
      <el-card
          v-for="(item, index) in searchWikis"
          :key="index"
          body-class="wiki-card"
          :shadow="wikiIds?.includes(item.id) ? 'always' : 'hover'"
          style="margin: 10px"
      >
        <el-badge
            :value="item.type ? '个人' : '企业'"
            :badge-class="'wiki-item-badge-' + (item.type ? 'person' : 'enterprise')"
            class="wiki-item"
            :offset="[-42, -13]"
        >
          <template #content="{ value }">
            <div class="custom-content">
              <i
                  style="margin-right: 2px"
                  :class="'ri-' + (item.type ? 'open-arm' : 'building') + '-line'"
              ></i>
              <span>{{ value }}</span>
            </div>
          </template>
          <el-checkbox
              :model-value="selectedWikiIds?.includes(item.id)"
              @update:model-value="handleCheckboxChange(item)"
              :disabled="item.taskStatus === 0"
              class="wiki-radio"
              border
          >
            <template #default>
              <div style="display: flex; align-items: center">
                <el-image
                    :src="item.imgUrl"
                    fit="cover"
                    style="
                    min-width: 100px;
                    height: 70px;
                    border-radius: 7px;
                    margin-right: 10px;
                  "
                />
                <div style="width: 212px">
                  <p
                      style="
                      font-size: 16px;
                      font-weight: bold;
                      margin-bottom: 5px;
                    "
                  >
                    {{ item.title }}
                  </p>
                  <el-tooltip
                      effect="dark"
                      :content="item.summary"
                      :visible-arrow="false"
                      placement="bottom"
                      popper-class="wiki-tooltip"
                  >
                    <p
                        style="
                        word-wrap: break-word;
                        white-space: nowrap;
                        overflow: hidden;
                        text-overflow: ellipsis;
                      "
                    >
                      {{ item.summary }}
                    </p>
                  </el-tooltip>
                  <div
                      style="
                      margin-top: 6px;
                      display: flex;
                      align-items: center;
                      justify-content: space-between;
                      width: 212px;
                    "
                  >
                    <el-tag style="text-align: left">{{ item.createTime }}</el-tag>
                    <el-tag v-show="item.taskStatus === 0" type="info">未量化</el-tag>
                    <el-tag v-show="item.taskStatus === 1" type="primary">部分量化</el-tag>
                    <el-tag v-show="item.taskStatus === 2" type="success">已量化</el-tag>
                  </div>
                </div>
                <span v-if="item.isTop" class="wiki-weight"><i class="ri-medal-2-line"></i></span>
              </div>
            </template>
          </el-checkbox>
        </el-badge>
      </el-card>
    </el-scrollbar>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import empty from '@/assets/images/empty.png'
import type {WikiItem} from "@/views/ai/talk/types/talk.ts";
import {ElMessage} from "element-plus";

const emptyImg = ref(empty)
const props = withDefaults(defineProps<{
  modelValue: boolean
  wikis: WikiItem[]
  searchWikis: WikiItem[]
  wikiIds: string[] | null
  searchWikiName?: string
}>(), {
  wikiId: null,
  searchWikiName: ''
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'update:wikiId', value: string[] | null): void
  (e: 'update:searchWikiName', value: string): void
  (e: 'filtered-wikis'): void
  (e: 'get-wiki-table-data'): void
  (e: 'handle-checkbox-click', item: string[]): void
}>()

const localSearchWikiName = ref<string>(props.searchWikiName)
const selectedWikiIds = ref<string[] | null>(props.wikiIds ? [...props.wikiIds] : [])

const visible = computed<boolean>({
  get() {
    return props.modelValue
  },
  set(value: boolean) {
    emit('update:modelValue', value)
  }
})

watch(
    () => props.searchWikiName,
    (newVal: string) => {
      localSearchWikiName.value = newVal
    }
)

watch(
    () => props.wikiIds,
    (newVal: string[] | null) => {
      selectedWikiIds.value = newVal ? [...newVal] : []
    }
)

watch(
    selectedWikiIds,
    (newVal) => {
      emit('update:wikiId', newVal)
    }
)

const onSearchInput = (value: string) => {
  emit('update:searchWikiName', value)
  emit('filtered-wikis')
}

const getWikiTableData = () => {
  emit('get-wiki-table-data')
}

// 处理复选框状态变化
const handleCheckboxChange = (item: WikiItem) => {
  if (item.taskStatus === 0) {
    ElMessage({
      message: '该知识库未进行向量化，不能使用！',
      type: 'warning'
    })
    return
  }
  // 检查是否至少还有一个其他选项被选中，防止全部取消
  if (selectedWikiIds.value && selectedWikiIds.value.length <= 1 && selectedWikiIds.value.includes(item.id)) {
    ElMessage({
      message: '至少需要选择一个知识库！',
      type: 'warning'
    })
    return // 阻止取消选中
  }

  // 检查是否已存在，不存在则添加
  if (!selectedWikiIds.value?.includes(item.id)) {
    selectedWikiIds.value = selectedWikiIds.value ? [...selectedWikiIds.value, item.id] : [item.id]
  } else {
    selectedWikiIds.value = selectedWikiIds.value.filter(id => id !== item.id)
  }
  // 触发原始的点击事件
  emit('handle-checkbox-click', selectedWikiIds.value || [])
}
</script>

<style scoped>
.wiki-radio {
  width: 100%;
  height: 100%;
  padding: 10px;
  display: flex;
  align-items: center;
}
</style>