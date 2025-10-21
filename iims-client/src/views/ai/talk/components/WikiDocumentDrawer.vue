<template>
  <el-drawer v-model="visible" direction="rtl" size="570" class="wiki-drawer">
    <template #header>
      <div style="width: 100%">
        <div class="link-wiki-article" @click="
          goWikiArticleDetailPage(wikiMeta.wikiId, wikiMeta.docId)
          ">
          <i class="ri-share-circle-line"></i>
        </div>
        <div style="display: grid; justify-content: center">
          <h1 style="text-align: center">{{ wikiMeta.name }}</h1>
          <p style="margin-top: 0; text-align: center">参考文献</p>
        </div>
      </div>
    </template>
    <el-scrollbar style="margin: 0 10px; padding: 0 10px" height="calc(100vh - 170px)">
      <el-card v-for="(item, index) in renderedMarkdown" :key="index" style="margin: 10px">
        <div class="show-doc-md markdown-body">
          <div v-html="item.renderedText"></div>
        </div>
      </el-card>
    </el-scrollbar>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type {RenderedMarkdownItem, WikiMeta} from "@/views/ai/talk/types/talk.ts";

const props = defineProps<{
  modelValue: boolean
  wikiMeta: WikiMeta
  renderedMarkdown: RenderedMarkdownItem[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'go-wiki-article-detail-page', wikiId: string, articleId: string): void
}>()

const visible = computed<boolean>({
  get() {
    return props.modelValue
  },
  set(value: boolean) {
    emit('update:modelValue', value)
  }
})

const goWikiArticleDetailPage = (wikiId: string, articleId: string) => {
  emit('go-wiki-article-detail-page', wikiId, articleId)
}
</script>

<style scoped>
/* Add scoped styles if needed, or move global ones from original */
</style>