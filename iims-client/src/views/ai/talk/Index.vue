<template>
  <div v-loading="isLoadDialogueMore">
    <el-container style="height: 100vh; width: 100vw;">
      <chat-sidebar
          v-show="isOpenChatList"
          :chat-items="chatItems"
          :active-topic-id="dialoguePages.topicId"
          :has-more="hasMoreData"
          :loading-more="isLoadingMore"
          :load-topic-id-set="loadTopicIdSet"
          @switch-topic="switchTopic"
          @rename-chat="renameChat"
          @del-chat="delChat"
          @init-chat-topic="initChatTopic"
          @load-more="loadMoreChatTopics"
      />
      <el-container style="background-color: aliceblue;">
        <el-header>
          <chat-header
              :is-open-chat-list="isOpenChatList"
              :wiki-status-decl="wikiStatusDecl"
              :is-flashing="isFlashing"
              :load-wiki-titles="loadWikiTitles"
              :on-model-selection-changed="handleModelSelectionChanged"
              @open-chat-list="openChatList"
              @open-new-chat="openNewChat"
              @handle-wiki-click="handleWikiClick"
              @close-file-click="closeFileClick"
              @toggle-wiki-drawer="wikiDrawer = true"
          />
          <el-divider style="width: calc(100% - 30px); margin: 0 15px" />
        </el-header>
        <el-main v-if="msgParam.topicId === null" style="
            display: flex;
            align-items: center;
            margin: 0 auto;
            overflow: hidden;
          ">
          <chat-welcome
              :hot-topics="hotTopics"
              :tools="tools"
              :agents="agents"
          />
        </el-main>
        <chat-messages
            v-else
            ref="messagesComponent"
            :messages="messages"
            :is-send-out="isSendOut"
            :status-data="statusData"
            :is-load-dialogue-more="isLoadDialogueMore"
            :dialogue-pages="dialoguePages"
            :mdi="mdi"
            @load-more="loadMoreChatDialogue"
            @del-message="delMessage"
            @copy-content="copyContent"
            @star-ai-chat="starAiChat"
            @thumb-status="changeThumbStatus"
            @show-wiki-doc-metadata="showWikiDocMetadata"
            @toggle-think="toggleThink"
        />
        <chat-input
            v-model="question"
            :messages-length="messages.length"
            :msg-param-topic-id="msgParam.topicId"
            :is-send-out="isSendOut"
            :use-file-param="useFileParam"
            @remove-file="handleRemoveFile"
            @send-out="sendOut"
            @toggle-upload-dialog="uploadFileDialogVisible = true"
            @toggle-wiki-drawer="wikiDrawer = true"
        />
        <wiki-selector-drawer
            v-model="wikiDrawer"
            v-model:wiki-ids="wikiIds"
            :wikis="wikis"
            :search-wikis="searchWikis"
            :search-wiki-name="searchWikiName"
            @update:search-wiki-name="searchWikiName = $event"
            @filtered-wikis="filteredWikis"
            @get-wiki-table-data="getWikiTableData"
            @handle-checkbox-click="handleCheckboxClick"
        />
        <wiki-document-drawer
            v-model="wikiDocsDrawer"
            :wiki-meta="wikiMeta"
            :rendered-markdown="renderedMarkdown"
            @go-wiki-article-detail-page="goWikiArticleDetailPage"
            @go-wiki-article-detail-page-index="goWikiArticleDetailPageIndex"
        />
      </el-container>
    </el-container>
    <file-upload-dialog
        v-model="uploadFileDialogVisible"
        :file-list="fileList"
        :base-url="baseUrl"
        :config="config"
        @handle-upload-file-success="handleUploadFileSuccess"
        @handle-upload-file-remove="handleUploadFileRemove"
    />
  </div>
</template>

<script setup lang="ts">
import {computed, nextTick, onBeforeUnmount, onMounted, ref, watch} from 'vue'
import {useStore} from 'vuex'
import {useRouter} from 'vue-router'
import markdownItMermaid from "@/utils/mermaid-plugin.ts"
import {
  delDialogue,
  delTopic,
  exchangeFeedback,
  getChatDialogueList,
  getChatTopicList,
  receiveAnswer,
  renameTopic,
  starSwitch,
  stopAnswer
} from '@/api/ai/chat.js'
import MarkdownIt from 'markdown-it'
import mathjax from 'markdown-it-mathjax3'
import hljs from 'highlight.js'
import 'highlight.js/styles/intellij-light.min.css'
import 'github-markdown-css/github-markdown-light.css'
import '@/styles/chat.css'
import {getStorage} from '@/utils/auth.js'
import {getWikiPublishPageList} from '@/api/wiki.js'

import ChatSidebar from './components/ChatSidebar.vue'
import ChatHeader from './components/ChatHeader.vue'
import ChatWelcome from './components/ChatWelcome.vue'
import ChatMessages from './components/ChatMessages.vue'
import ChatInput from './components/ChatInput.vue'
import WikiSelectorDrawer from './components/WikiSelectorDrawer.vue'
import WikiDocumentDrawer from './components/WikiDocumentDrawer.vue'
import FileUploadDialog from './components/FileUploadDialog.vue'
import {ElMessageBox, ElNotification, type UploadFile} from "element-plus";
import type {
  AgentTopic,
  AiContent,
  DialoguePages,
  HotTopic,
  Message,
  MsgParam,
  ToolTopic,
  TopicPages,
  UseFileParam,
  WikiItem,
  WikiMeta,
  WikiPages
} from "@/views/ai/talk/types/talk.ts";
import {parseReasoning, type ParseResult} from "@/utils/parse-reasoning.ts";


const store = useStore()
const router = useRouter()

// Reactive data
const question = ref('')
const token = ref('')
const wikiDrawer = ref(false)
const isSendOut = ref(false)
const isFlashing = ref(false)

const wikiIds = ref<string[] | null>(null)
const loadWikiTitles = ref<string[]>([])
const cancelSseConnection = ref<any>(null)
const searchWikiName = ref('')
const fileList = ref<UploadFile[]>([])
const uploadFileDialogVisible = ref(false)
const isOpenChatList = ref(true)
const milliSecond = ref<string | null>(null)
const wikiDocsDrawer = ref(false)
const isDialogueMore = ref(true)
const isLoadDialogueMore = ref(false)
const isInitDialogue = ref(true)
const isLoadingMore = ref(false);
const hasMoreData = ref(true);
const messagesMap = ref<Map<string, Message[]>>(new Map());
const loadTopicIdSet = ref<Set<string>>(new Set())

const useFileParam = ref<UseFileParam>({
  isUseFile: false,
  fileInfos: []
})

const wikiMeta = ref<WikiMeta>({
  wikiId: '',
  docId: '',
  index: '',
  name: ''
})
const wikiDocs = ref<any[]>([])

const msgParam = ref<MsgParam>({
  topicId: null,
  lastId: null,
  fileId: null,
  modelId: null,
  apiType: 'AGENT',
  question: '',
  wikiIds: null
})

const dialoguePages = ref<DialoguePages>({
  topicId: null,
  page: 1,
  pageSize: 10,
  total: 0,
})

const statusData = ref<any>(null)

const topicPages = ref<TopicPages>({
  title: null,
  page: 1,
  pageSize: 20,
  total: 0,
})

const wikiPages = ref<WikiPages>({
  title: null,
  page: 1,
  pageSize: 20,
  total: 0,
})

const wikis = ref<WikiItem[]>([])
const searchWikis = ref<WikiItem[]>([])
const messages = ref<Message[]>([])
const chatItems = ref<any[]>([])

const hotTopics: HotTopic[] = [
  { rank: 1, title: '中秋佳节习近平总书记这样寄语。' },
  { rank: 2, title: 'SpaceX龙飞船完成商业太空行走任务，安全返回地球。' },
  {
    rank: 3,
    title: '英特尔表明诚意，部分酷睿 i9-13900K 用户可获得全额 RMA 退款或免费升级 14900K。',
  },
  { rank: 4, title: '一名大学生的困惑与探索："我不知道该相信什么"。' },
  {
    rank: 5,
    title: '德国宣布9月16日起所有陆地边境恢复护照检查，德国的这一决定是基于何种战略或政治考虑？',
  },
  {
    rank: 6,
    title: '宇航员连牺牲都不怕，为什么从月球上看地球会感到无比恐惧？',
  },
  {
    rank: 7,
    title: '台风贝碧嘉登陆上海浦东，最大风力14级，成为1949年以来登陆上海的最强台风，目前情况如何?',
  },
  { rank: 8, title: '婚姻与爱情的本质是什么？' },
  {
    rank: 9,
    title: '央行、国家金融监管总局出台住房信贷新政策：首套住房商贷最低首付比例不低于20%，二套房首付比例不低于30%。',
  },
]

const tools: ToolTopic[] = [
  {
    name: '系统资料调度',
    content: '专注于系统中管理和控制数据流动与分析',
    avatarSrc: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png  ',
  },
  {
    name: '专业技能测试',
    content: '用于测试员工的专业技能水平，并生成的测试结果',
    avatarSrc: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png  ',
  },
  {
    name: '全能扫描提取',
    content: '专注于从不同格式的文件提取文本的能力',
    avatarSrc: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png  ',
  },
  {
    name: '智能联网搜索',
    content: '专注于AI互联网搜索引擎，提供高质量的搜索结果',
    avatarSrc: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png  ',
  },
]

const agents: AgentTopic[] = [
  {
    name: '报告生成助手',
    content: '根据需求自动生成各类报告',
    avatarSrc: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png  ',
  },
  {
    name: '客户分析助手',
    content: '辅助销售人员追踪潜在客户，提供销售预测',
    avatarSrc: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png  ',
  },
  {
    name: '营销规划助手',
    content: '帮助企业制定营销策略，分析市场趋势',
    avatarSrc: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png  ',
  },
  {
    name: '智能客服助手',
    content: '帮助客服人员更快地查找解决方案，提供个性化推荐',
    avatarSrc: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png  ',
  },
]

const baseUrl = computed(() => import.meta.env.VITE_APP_API_URL)
const wikiStatusDecl = computed(() => isFlashing.value ? '开启' : '关闭')
const renderedMarkdown = computed(() => {
  return wikiDocs.value.map((item) => ({
    ...item,
    renderedText: mdi.value.render(item.text),
  }))
})

const config = computed(() => {
  return { token: token.value }
})

const userId = computed(() => store.getters.userId)

const mdi = ref<MarkdownIt>(new MarkdownIt())

const messagesComponent = ref<any>(null);

watch(messages, (newMessages, oldMessages) => {
  if (newMessages.length !== oldMessages?.length) {
    scrollToBottom()
    return
  }
  if (isSendOut.value) {
    scrollToBottom()
  }
}, { deep: true })

onMounted(() => {
  initMarkdownIt()
  initChatTopic()
  getWikiTableData()
  token.value = getStorage('token') || ''
  window.addEventListener('beforeunload', handleBeforeUnload)
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
})

// Methods
const toggleThink = (parseResult: ParseResult) => {
  parseResult.isExpanded = !parseResult.isExpanded
}

const scrollToBottom = () => {
  nextTick(() => {
    const containerRef = messagesComponent.value?.mainContainerRef?.$el;
    if (containerRef) {
      containerRef.scrollTop = containerRef.scrollHeight;
    }
  })
}

const handleRemoveFile = (index: number) => {
  useFileParam.value.fileInfos.splice(index, 1)
  if (useFileParam.value.fileInfos.length === 0) {
    useFileParam.value.isUseFile = false
  }
}

const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  if (isSendOut.value) {
    event.preventDefault()
    return true
  }
}

const goWikiArticleDetailPage = (wikiId: string, articleId: string) => {
  const url = router.resolve({
    path: `/wiki/${wikiId}`,
    query: { articleId },
  }).href
  window.open(url, '_blank')
}

const goWikiArticleDetailPageIndex = (wikiId: string, index: string, articleId: string) => {
  const url = router.resolve({
    path: `/wiki/${wikiId}/${index}`,
    query: { articleId },
  }).href
  window.open(url, '_blank')
}

const showWikiDocMetadata = (mata: any) => {
  wikiDocsDrawer.value = true
  wikiMeta.value = mata.metadata
  wikiDocs.value = mata.score
}

const filteredWikis = () => {
  const keyword = searchWikiName.value.trim().toLowerCase()
  if (!keyword) {
    searchWikis.value = wikis.value
    return
  }
  searchWikis.value = wikis.value?.filter((item) => {
    return (
        item.title.toLowerCase().includes(keyword) ||
        item.summary.toLowerCase().includes(keyword)
    )
  }) || []
}

const openChatList = () => {
  isOpenChatList.value = !isOpenChatList.value
}

const getWikiTableData = () => {
  getWikiPublishPageList({
    page: wikiPages.value.page,
    pageSize: wikiPages.value.pageSize,
  }).then((res: any) => {
    if (res.success === true) {
      wikis.value = res.data.list
      searchWikis.value = res.data.list
      if (wikis.value?.length > 0) {
        const item = wikis.value.find((item) => item.taskStatus === 2)
        if (item) {
          wikiIds.value = [item.id]
          loadWikiTitles.value = [item.title]
        }
      }
    }
  })
}

const handleUploadFileSuccess = (res: any, file: UploadFile) => {
  uploadFileDialogVisible.value = false
  msgParam.value.fileId = res.data.fileId
  ElNotification({
    message: '上传文件成功！',
    type: 'success',
    customClass: 'talkNotification',
    offset: 47,
  })
  fileList.value = []
  useFileParam.value = {
    isUseFile: true,
    fileInfos: [{
      id: res.data.fileId,
      filename: file.name,
      fileSize: file.size,
      url: res.data.url
    }]
  }
}

const handleUploadFileRemove = (file: any) => {
  fileList.value = fileList.value.filter((f) => f.uid !== file.uid)
}

const closeFileClick = () => {
  useFileParam.value = {
    isUseFile: false,
    fileInfos: []
  }
}

const handleCheckboxClick = (_wikiIds: string[]) => {
  wikiIds.value = _wikiIds
  msgParam.value.wikiIds = isFlashing.value ? wikiIds.value : null
  loadWikiTitles.value = wikis.value.filter(wiki => _wikiIds.includes(wiki.id)).map(wiki => wiki.title) || []
}

const loadMoreChatDialogue = async () => {
  await nextTick()
  if (isDialogueMore.value && !isLoadDialogueMore.value) {
    isInitDialogue.value = false
    if (dialoguePages.value.total <= messages.value.length) {
      isDialogueMore.value = false
      return
    }
    dialoguePages.value.page++
    isLoadDialogueMore.value = true
    const res = await getChatDialogueList(dialoguePages.value)
    if (res.code === 1) {
      const newMessages = res.data.list.map((item: Message) => {
        if (item.sender === 'assistant') {
          item.aiContent?.forEach(value => {
            if (value.thinking) {
              value.contentResult = [{
                type: "reasoning",
                content: value.thinking,
                isComplete: true,
                isExpanded: false
              }, {
                type: "text",
                content: value.content,
                isComplete: true,
                isExpanded: false
              }]
            } else {
              value.contentResult = parseReasoning(value.content || "", [])
            }
            value.contentResult.forEach(result => {
              result.view = mdi.value.render(result.content)
            })
          })
        }
        return item
      })
      messages.value = newMessages.concat(messages.value)
      dialoguePages.value.total = res.data.total
      isLoadDialogueMore.value = false
    }
  }
}

const handleWikiClick = () => {
  if (wikiIds.value) {
    isFlashing.value = !isFlashing.value
    msgParam.value.wikiIds = isFlashing.value ? wikiIds.value : null
  } else {
    wikiDrawer.value = true
  }
}

const openNewChat = () => {
  msgParam.value = {
    topicId: null,
    lastId: null,
    fileId: null,
    modelId: msgParam.value.modelId,
    apiType: msgParam.value.apiType,
    question: '',
    wikiIds: msgParam.value.wikiIds
  }
  dialoguePages.value = {
    topicId: null,
    page: 1,
    pageSize: 10,
    total: 0,
  }
  messages.value = []
  isInitDialogue.value = true
}

const switchTopic = (id: string) => {
  if (dialoguePages.value.topicId !== id) {
    dialoguePages.value.topicId = id
    msgParam.value.topicId = id
    dialoguePages.value.page = 1
    isDialogueMore.value = true
    isLoadDialogueMore.value = false
    if (loadTopicIdSet.value.has(id) && messagesMap.value.get(id)) {
      messages.value = [...(messagesMap.value.get(id) || [])];
      if (messages.value.length > 0) {
        msgParam.value.lastId = messages.value[messages.value.length - 1].id
      }
      isInitDialogue.value = true
      return
    }
    initChatDialogue()
  }
}

const getChinaTimestamp = () => {
  return new Date().getTime()
}

const limitLengthWithEllipsis = (question: string) => {
  const maxLength = 36
  if (question.length > maxLength) {
    return question.substring(0, maxLength) + "..."
  } else {
    return question
  }
}

const handleModelSelectionChanged = (model: any) => {
  msgParam.value.modelId = model.modelId
  msgParam.value.apiType = model.apiType
}

const sendOut = async () => {
  if (isSendOut.value) {
    await stopAnswerNow()
    return
  }
  if (isEmptyStr(question.value)) {
    ElNotification({
      message: '请检查输入的内容是否有效！',
      type: 'warning',
      customClass: 'talkNotification',
      offset: 47,
    })
    return
  }
  statusData.value = null
  try {
    msgParam.value.question = question.value
    messages.value.push(
        { id: null, lastId: null, userContent: { question: question.value }, sender: 'user', fileInfos: useFileParam.value.fileInfos, feedbackStatus: 0, isStar: false },
        { id: null, lastId: null, isLoadingAnswer: true, sender: 'assistant', feedbackStatus: 0, isStar: false }
    )
    question.value = ''
    msgParam.value.fileId = null
    closeFileClick()
    isSendOut.value = true
    milliSecond.value = `${userId.value}${getChinaTimestamp()}`
    cancelSseConnection.value = receiveAnswer(milliSecond.value, msgParam.value, (item: any) => {
      const _data = item.data
      const loadTopicId = _data.topicId
      const _messages = (dialoguePages.value.topicId === loadTopicId || dialoguePages.value.topicId === null ? messages.value : messagesMap.value.get(loadTopicId)) || []
      const message = _messages[_messages.length - 1]
      const lastMessage = _messages[_messages.length - 2]
      if (['output', 'end'].includes(item.event) && message.isLoadingAnswer) {
        message.isLoadingAnswer = false
      }

      if (item.event === 'start') {
        if (dialoguePages.value.topicId === null) {
          dialoguePages.value.topicId = loadTopicId
          msgParam.value.topicId = loadTopicId
          chatItems.value.splice(0, 0, {
            id: _data.topicId, title: limitLengthWithEllipsis(msgParam.value.question), createTime: _data.createTime
          })
        }
        messagesMap.value.set(loadTopicId, [...messages.value])
        loadTopicIdSet.value.add(loadTopicId)
        lastMessage.id = _data.lastId
      } else if (item.event === 'output') {
        const exchangeData = message.aiContent ?? []
        message.aiContent = _data.aiContent.map((newValue: AiContent, index: number) => {
          const existingValue = exchangeData[index];

          let contentResult;

          if (newValue.thinking) {
            // 构造新的 contentResult，但继承旧状态
            const existingReasoning = existingValue?.contentResult?.find((r: any) => r.type === "reasoning");
            const existingText = existingValue?.contentResult?.find((r: any) => r.type === "text");

            contentResult = [
              {
                type: "reasoning",
                content: newValue.thinking,
                isComplete: newValue.content && newValue.content?.trim() !== '',
                isExpanded: existingReasoning?.isExpanded ?? false,
                view: ''
              },
              {
                type: "text",
                content: newValue.content,
                isComplete: true,
                isExpanded: existingText?.isExpanded ?? false,
                view: ''
              }
            ];
          } else {
            const parsed = parseReasoning(newValue.content || "", []);
            contentResult = parsed.map(newRes => {
              const existingRes = existingValue?.contentResult?.find((r: any) => r.type === newRes.type);
              return {...existingRes, ...newRes};
            });
          }

          contentResult.forEach(result => {
            result.view = mdi.value.render(result.content);
          });

          return {
            ...existingValue,   // 保留旧状态
            ...newValue,        // 覆盖新数据（thinking, content 等）
            contentResult,      // 使用合并后的 contentResult
          };
        });
      } else if (item.event === 'end') {
        if (_data.docMetadata) {
          message.docMetadata = _data.docMetadata
        }
        message.id = _data.lastId
        msgParam.value.lastId = _data.lastId
        message.lastId = lastMessage.id
        isSendOut.value = false
        milliSecond.value = null
        if (dialoguePages.value.topicId === loadTopicId && loadTopicId && messagesMap.value.get(loadTopicId)) {
          messages.value = [...(messagesMap.value.get(loadTopicId) || [])];
        }
        messagesMap.value.delete(loadTopicId)
        loadTopicIdSet.value.delete(loadTopicId)
      } else if (item.event === 'status') {
        statusData.value = _data
      } else {
        console.log('null listening type appears!')
      }
    })
  } catch (error) {
    console.log(error)
  }
}

const stopAnswerNow = async () => {
  // @ts-ignore
  const confirmResult = await ElMessageBox.confirm(
      `此操作将停止生成内容, 是否继续?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).catch(() => {})

  if (confirmResult === 'confirm' && milliSecond.value) {
    const res = await stopAnswer(milliSecond.value)
    if (res.code === 1) {
      isSendOut.value = false
      milliSecond.value = null
      ElNotification({
        message: '已停止生成内容！',
        type: 'success',
        customClass: 'talkNotification',
        offset: 47,
      })
    } else {
      ElNotification({
        message: '停止生成内容失败！',
        type: 'warning',
        customClass: 'talkNotification',
        offset: 47,
      })
    }
  }
}

const initChatTopic = async () => {
  try {
    const res = await getChatTopicList(topicPages.value)
    if (res.code === 1) {
      chatItems.value = res.data.list
      topicPages.value.total = res.data.total
    }
  } catch (error) {
    console.log(error)
  }
}

// 加载更多数据
const loadMoreChatTopics = async () => {
  if (isLoadingMore.value || !hasMoreData.value) return;

  isLoadingMore.value = true;

  try {
    const nextPage = topicPages.value.page + 1;
    const res = await getChatTopicList({
      ...topicPages.value,
      page: nextPage
    });

    if (res.code === 1) {
      // 将新数据追加到现有数据后面
      chatItems.value = [...chatItems.value, ...res.data.list];
      topicPages.value.page = nextPage;

      // 判断是否还有更多数据
      hasMoreData.value = chatItems.value.length < res.data.total;
    }
  } catch (error) {
    console.log(error);
  } finally {
    isLoadingMore.value = false;
  }
};

const initChatDialogue = async () => {
  try {
    const res = await getChatDialogueList(dialoguePages.value)
    if (res.code === 1) {
      res.data.list.forEach((item: Message) => {
        if (item.sender === 'assistant') {
          item.aiContent?.forEach(value => {
            if (value.thinking) {
              value.contentResult = [{
                type: "reasoning",
                content: value.thinking,
                isComplete: true,
                isExpanded: false
              }, {
                type: "text",
                content: value.content,
                isComplete: true,
                isExpanded: false
              }]
            } else {
              value.contentResult = parseReasoning(value.content || "", [])
            }
            value.contentResult.forEach(result => {
              result.view = mdi.value.render(result.content)
            })
          })
        }
      })
      messages.value = res.data.list
      dialoguePages.value.total = res.data.total
      if (messages.value.length > 0) {
        msgParam.value.lastId = messages.value[messages.value.length - 1].id
      }
      isInitDialogue.value = true
    }
  } catch (error) {
    console.log(error)
  }
}

const delChat = async (id: string) => {
  // @ts-ignore
  const confirmResult = await ElMessageBox.confirm(
      '是否要删除当前选中的对话？',
      '删除对话',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).catch(() => {})

  if (confirmResult === 'confirm') {
    const res = await delTopic([id])
    if (res.code === 1) {
      ElNotification({
        type: 'success',
        message: '已成功删除该对话！',
        customClass: 'talkNotification',
        offset: 47,
      })
      openNewChat()
      await initChatTopic()
    } else {
      ElNotification({
        message: '该对话删除失败！',
        type: 'warning',
        customClass: 'talkNotification',
        offset: 47,
      })
    }
  }
}

const renameChat = async (item: any) => {
  try {
    const { value } = await ElMessageBox.prompt(
        '请输入修改后的标题',
        '重命名',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
        }
    )

    const res = await renameTopic({ id: item.id, title: value })
    if (res.code === 1) {
      ElNotification({
        type: 'success',
        message: '已成功修改该对话标题！',
        customClass: 'talkNotification',
        offset: 47,
      })
      item.title = value
    }
  } catch (error) {
    // 用户取消操作
  }
}

const copyContent = async (message: Message) => {
  try {
    const aiContent = message.aiContent
    if (aiContent) {
      const content = aiContent[aiContent.length - 1].content
      await navigator.clipboard.writeText(
          content.replace(/<think>[\s\S]*?<\/think>/g, '')
      )
      ElNotification({
        message: '文本已复制到剪切板！',
        type: 'success',
        customClass: 'talkNotification',
        offset: 47,
      })
    } else {
      ElNotification({
        message: '无可用内容复制！',
        customClass: 'talkNotification',
        type: 'warning',
        offset: 47,
      })
    }
  } catch (err) {
    ElNotification({
      message: '复制失败，请检查是否打开复制权限！',
      customClass: 'talkNotification',
      type: 'warning',
      offset: 47,
    })
  }
}

const starAiChat = async (message: Message, index: number) => {
  try {
    const isStar = !message.isStar
    const res = await starSwitch({ id: message.id!, status: isStar })
    if (res.code === 1) {
      messages.value[index].isStar = isStar
      // @ts-ignore
      ElNotification({
        message: isStar ? '收藏成功！' : '已取消收藏！',
        type: isStar ? 'success' : 'warning',
        customClass: 'talkNotification',
        offset: 47,
      })
    } else {
      ElNotification({
        message: '请检查是否有权限！',
        customClass: 'talkNotification',
        type: 'warning',
        offset: 47,
      })
    }
  } catch (err) {
    console.log(err)
  }
}

const changeThumbStatus = async (feedbackStatus: number, status: number, index: number) => {
  let _status
  if (feedbackStatus === 0 || feedbackStatus !== status) {
    _status = status
  } else {
    _status = 0
  }
  const message = messages.value[index]
  if (message && message.id) {
    const res = await exchangeFeedback({ id: message.id, status: _status })
    if (res.code === 1) {
      messages.value[index].feedbackStatus = _status
      let messageInfo = ""
      if (_status === 0 && feedbackStatus === 1) {
        messageInfo = "已取消点赞！"
      } else if (_status === 0 && feedbackStatus === -1) {
        messageInfo = "已取消点踩！"
      } else if (_status === -1) {
        messageInfo = "已点踩！"
      } else if (_status === 1) {
        messageInfo = "已点赞！"
      }
      ElNotification({
        message: messageInfo,
        type: 'success',
        customClass: 'talkNotification',
        offset: 47,
      })
    } else {
      ElNotification({
        message: '请检查是否有权限！',
        customClass: 'talkNotification',
        type: 'warning',
        offset: 47,
      })
    }
  }
}

const delMessage = async (lastId: string | null, isStar: boolean, index: number) => {
  const confirmResult = await ElMessageBox.confirm(
      `此操作将永久删除该对话记录${isStar ? '，但保存收藏记录' : ''}, 是否继续?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).catch(() => {})

  if (confirmResult === 'confirm' && lastId) {
    try {
      const res = await delDialogue(lastId)
      if (res.code === 1) {
        messages.value.splice(index, 1)
        messages.value.splice(index - 1, 1)
        messages.value.forEach(item => {
          item.aiContent?.forEach(value => {
            if (value.thinking) {
              value.contentResult = [{
                type: "reasoning",
                content: value.thinking,
                isComplete: true,
                isExpanded: false
              }, {
                type: "text",
                content: value.content,
                isComplete: true,
                isExpanded: false
              }]
            } else {
              value.contentResult = parseReasoning(value.content || "", [])
            }
            value.contentResult.forEach(result => {
              result.view = mdi.value.render(result.content)
            })
          })

        })
        // @ts-ignore
        ElNotification({
          message: '删除对话成功！',
          customClass: 'talkNotification',
          type: 'success',
          offset: 47,
        })
      } else {
        // @ts-ignore
        ElNotification({
          message: '请检查是否有权限！',
          customClass: 'talkNotification',
          type: 'warning',
          offset: 47,
        })
      }
    } catch (err) {
      console.log(err)
    }
  }
}

const isEmptyStr = (str: string) => {
  return str.trim() === ''
}

const initMarkdownIt = () => {
  // 初始化 MarkdownIt 实例
  mdi.value = new MarkdownIt({
    linkify: true,
    highlight(code: string, language: string) {
      const validLang = !!(language && hljs.getLanguage(language))
      if (validLang) {
        const lang = language ?? ''
        return hljs.highlight(code, {
          language: lang,
          ignoreIllegals: true,
        }).value
      }
      return hljs.highlightAuto(code).value
    },
  })
  mdi.value.use(mathjax)
  mdi.value.use(markdownItMermaid)
  // 自定义规则：解析 $xx$ 行内数学公式
  mdi.value.inline.ruler.after('escape', 'math_inline', (state: any, silent: boolean) => {
    const match = state.src.slice(state.pos).match(/^\$(.+?)\$/)
    if (!match) return false
    if (!silent) {
      const token = state.push('math_inline', '', 0)
      token.content = match[1]
      state.pos += match[0].length
    }
    return true
  })
}
</script>