<template>
  <!-- 卡片 -->
  <div
      class="w-full px-5 py-10 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
    <!-- 评论发布表单 -->
    <h2 class="flex justify-center items-center mb-7 text-gray-500">全部评论<span>({{ total }})</span></h2>
    <form>
      <div class="flex gap-3">
        <!-- 头像 -->
        <div>
          <img v-if="avatar && avatar.length > 0"
               :src="avatar" class="w-10 h-10 rounded-lg" alt="avatar">
          <svg v-else class="w-10 h-10 text-gray-400 dark:text-gray-400" aria-hidden="true"
               xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
            <path
                d="M10 0a10 10 0 1 0 10 10A10.011 10.011 0 0 0 10 0Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 13a8.949 8.949 0 0 1-4.951-1.488A3.987 3.987 0 0 1 9 13h2a3.987 3.987 0 0 1 3.951 3.512A8.949 8.949 0 0 1 10 18Z" />
          </svg>
        </div>
        <!-- 评论内容 -->
        <div class="grow">
          <div class="w-full mb-4 border border-gray-200 rounded-lg bg-gray-50 dark:bg-gray-700 dark:border-gray-600">
            <div class="px-4 py-2 bg-white rounded-t-lg dark:bg-gray-800">
              <label for="comment" class="sr-only">Your comment</label>
              <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 6 }" id="comment" v-model="commentForm.content" placeholder="发表一个友善的评论吧..." required></el-input>
            </div>
            <div class="flex items-center justify-between px-3 py-2 border-t dark:border-gray-600">
              <div @click="onPublishCommentClick" class="inline-flex items-center py-2.5 px-4 text-xs font-medium text-center text-white bg-sky-600 rounded-lg focus:ring-4 focus:ring-sky-200 dark:focus:ring-sky-900 hover:bg-sky-700">
                发送
              </div>
              <div class="flex ps-0 space-x-1 rtl:space-x-reverse sm:ps-2">
                <!-- Emoji -->
                <div data-popover-target="popover-emoji" type="button"
                     class="inline-flex justify-center items-center p-2 text-gray-500 rounded cursor-pointer hover:text-gray-900 hover:bg-gray-100 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-600">
                  <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                       fill="none" viewBox="0 0 24 24">
                    <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                          stroke-width="2"
                          d="M15 9h0M9 9h0m12 3a9 9 0 1 1-18 0 9 9 0 0 1 18 0ZM7 13c0 1 .5 2.4 1.5 3.2a5.5 5.5 0 0 0 7 0c1-.8 1.5-2.2 1.5-3.2 0 0-2 1-5 1s-5-1-5-1Z" />
                  </svg>
                </div>

                <!-- Emoji Popover -->
                <div data-popover id="popover-emoji" role="tooltip"
                     class="absolute z-10 invisible inline-block w-64 text-sm text-gray-500 transition-opacity duration-300 bg-white border border-gray-200 rounded-lg shadow-sm opacity-0 dark:text-gray-400 dark:border-gray-600 dark:bg-gray-800">
                  <div class="p-2">
                    <div class="grid grid-cols-6 gap-2">
                      <div v-for="(emoji, index) in emojis" :key="index"
                           class="text-2xl hover:cursor-pointer" @click="addEmoji(emoji)">{{ emoji }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </form>
    <!-- 评论列表 -->
    <div v-if="comments && comments.length > 0" v-for="(comment, index) in comments" :key="index">
      <!-- 边界线 -->
      <div v-if="index > 0" class="border-t ml-12 mt-5  border-gray-100 dark:border-gray-700"></div>
      <!-- 一级评论 -->
      <div class="flex gap-3 mt-5">
        <!-- 左边头像栏 -->
        <div>
          <img v-if="comment.avatar && comment.avatar.length > 0" :src="comment.avatar"
               class="w-10 h-10 rounded-lg" alt="avatar">
          <svg v-else class="w-10 h-10 text-gray-400 rounded-lg dark:text-gray-400"
               aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
               viewBox="0 0 20 20">
            <path
                d="M10 0a10 10 0 1 0 10 10A10.011 10.011 0 0 0 10 0Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 13a8.949 8.949 0 0 1-4.951-1.488A3.987 3.987 0 0 1 9 13h2a3.987 3.987 0 0 1 3.951 3.512A8.949 8.949 0 0 1 10 18Z" />
          </svg>
        </div>
        <!-- 右边评论信息 -->
        <div class="flex flex-col gap-2 grow">
          <!-- 昵称 -->
          <div class="text-xs text-[#FB7299] font-bold">{{ comment.nickname }}</div>
          <!-- 评论内容 -->
          <div class="text-sm dark:text-gray-400">{{ comment.content }}</div>
          <!-- Meta 信息 -->
          <div class="flex items-center text-xs text-gray-400">
            <!-- 发布时间 -->
            <div>{{ comment.createTime }}</div>
            <div class="text-gray-400 cursor-pointer ml-4 hover:text-sky-600"
                 @click="showReplyForm(index, comment.nickname, comment.id, comment.id)">
              回复
            </div>
          </div>
        </div>
      </div>

      <!-- 二级评论 -->
      <div class="ml-12" v-if="comment.childComments && comment.childComments.length > 0">
        <div v-for="(childComment, index2) in comment.childComments" :key="index2">
          <!-- 头像、昵称、评论内容 -->
          <div class="flex items-center gap-3 mt-5">
            <!-- 左边头像栏 -->
            <div>
              <img v-if="childComment.avatar && childComment.avatar.length > 0"
                   :src="childComment.avatar" class="w-6 h-6 rounded-lg" alt="avatar">
              <svg v-else class="w-6 h-6 text-gray-400 rounded-lg dark:text-gray-400"
                   aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                   viewBox="0 0 20 20">
                <path
                    d="M10 0a10 10 0 1 0 10 10A10.011 10.011 0 0 0 10 0Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 13a8.949 8.949 0 0 1-4.951-1.488A3.987 3.987 0 0 1 9 13h2a3.987 3.987 0 0 1 3.951 3.512A8.949 8.949 0 0 1 10 18Z" />
              </svg>
            </div>
            <!-- 昵称 -->
            <div class="text-xs text-[#FB7299] font-bold">
              {{ childComment.nickname }}
              <!-- 【回复 @xxx】 -->
              <span v-if="childComment.replyNickname" class="text-gray-400 font-normal ml-1 mr-1">回复
                                <span class="text-sky-600 font-normal text-sm">@{{ childComment.replyNickname }}</span>
                                <span class="text-gray-400"> :</span>
                            </span>
            </div>
            <!-- 评论内容 -->
            <div class="text-sm dark:text-gray-400">{{ childComment.content }}</div>
          </div>
          <!-- Meta 信息 -->
          <div class="ml-9 mt-1 flex items-center text-xs text-gray-400">
            <!-- 发布时间 -->
            <div>{{ childComment.createTime }}</div>
            <div class="text-gray-400 cursor-pointer ml-4 hover:text-sky-600"
                 @click="showReplyForm(index, childComment.nickname, childComment.id, comment.id)">
              回复
            </div>
          </div>
        </div>
      </div>
      <!-- 二级评论回复表单 -->
      <form class="ml-12 mt-5" v-if="comment.isShowReplyForm">
        <div class="flex gap-3">
          <!-- 头像 -->
          <div>
            <img v-if="avatar && avatar.length > 0"
                 :src="avatar" class="w-10 h-10 rounded-lg" alt="avatar">
            <svg v-else class="w-10 h-10 text-gray-400 dark:text-gray-400" aria-hidden="true"
                 xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 20 20">
              <path
                  d="M10 0a10 10 0 1 0 10 10A10.011 10.011 0 0 0 10 0Zm0 5a3 3 0 1 1 0 6 3 3 0 0 1 0-6Zm0 13a8.949 8.949 0 0 1-4.951-1.488A3.987 3.987 0 0 1 9 13h2a3.987 3.987 0 0 1 3.951 3.512A8.949 8.949 0 0 1 10 18Z" />
            </svg>
          </div>
          <!-- 评论内容 -->
          <div class="grow">
            <div
                class="w-full mb-4 mt-4 border border-gray-200 rounded-lg bg-gray-50 dark:bg-gray-700 dark:border-gray-600">
              <div class="px-4 py-2 bg-white rounded-t-lg dark:bg-gray-800">
                <label for="comment" class="sr-only">Your comment</label>
                <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 6 }" id="comment" v-model="replyContent"
                          class="w-full px-0 text-sm text-gray-900 bg-white border-0 dark:bg-gray-800 focus:ring-0 dark:text-white dark:placeholder-gray-400"
                          :placeholder="replyPlaceholderText" required></el-input>
              </div>
              <div class="flex items-center justify-between px-3 py-2 border-t dark:border-gray-600">
                <div @click="onReplyContentSubmit" class="inline-flex items-center py-2.5 px-4 text-xs font-medium text-center text-white bg-sky-600 rounded-lg focus:ring-4 focus:ring-sky-200 dark:focus:ring-sky-900 hover:bg-sky-700">
                  发送
                </div>
                <div class="flex ps-0 space-x-1 rtl:space-x-reverse sm:ps-2">
                  <!-- Emoji -->
                  <div data-popover-target="popover-emoji2" type="button"
                       class="inline-flex justify-center items-center p-2 text-gray-500 rounded cursor-pointer hover:text-gray-900 hover:bg-gray-100 dark:text-gray-400 dark:hover:text-white dark:hover:bg-gray-600">
                    <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
                         fill="none" viewBox="0 0 24 24">
                      <path stroke="currentColor" stroke-linecap="round"
                            stroke-linejoin="round" stroke-width="2"
                            d="M15 9h0M9 9h0m12 3a9 9 0 1 1-18 0 9 9 0 0 1 18 0ZM7 13c0 1 .5 2.4 1.5 3.2a5.5 5.5 0 0 0 7 0c1-.8 1.5-2.2 1.5-3.2 0 0-2 1-5 1s-5-1-5-1Z" />
                    </svg>
                  </div>

                  <!-- Emoji Popover -->
                  <div data-popover id="popover-emoji2" role="tooltip"
                       class="absolute z-10 invisible inline-block w-64 text-sm text-gray-500 transition-opacity duration-300 bg-white border border-gray-200 rounded-lg shadow-sm opacity-0 dark:text-gray-400 dark:border-gray-600 dark:bg-gray-800">
                    <div class="p-2">
                      <div class="grid grid-cols-6 gap-2">
                        <!-- 评论回复表单中的添加 Emoji 表情 -->
                        <div v-for="(emoji, index) in emojis" :key="index"
                             class="text-2xl hover:cursor-pointer" @click="addReplyEmoji(emoji)">
                          {{ emoji }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>
    </div>
    <!-- 没有评论的提示文字 -->
    <div v-else class="flex items-center mt-10 mb-10 justify-center text-gray-400">还没有任何评论哟~</div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch, computed } from 'vue'
import { initTooltips, initPopovers } from 'flowbite'
import { showMessage } from '@/composables/utils.ts'
import { publishComment, findComments } from '@/api/comment.ts'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex';

const route = useRoute()
const store = useStore()

// 评论类型定义
interface CommentItem {
  id: number;
  avatar: string;
  nickname: string;
  content: string;
  createTime: string;
  replyNickname?: string;
  childComments?: CommentItem[];
  isShowReplyForm?: boolean;
}

interface CommentForm {
  avatar: string;
  content: string;
  mail: string;
  nickname: string;
  routerUrl: string;
  website: string;
  replyCommentId: number | null;
  parentCommentId: number | null;
}

// 回复的评论
const replyContent = ref<string>('')

// 回复 textarea 的 placeholder 提示文字
const replyPlaceholderText = ref<string>('发表一个友善的评论吧...')
const avatar = computed<string>(() => store.getters.avatar);

// 当前回复的评论 ID
const currReplyCommentId = ref<number | null>(null)
// 当前回复的父级 ID
const currParentCommentId = ref<number | null>(null)
// 展示回复表单
const showReplyForm = (index: number, nickname: string, replyCommentId: number, parentCommentId: number) => {
  currReplyCommentId.value = replyCommentId
  currParentCommentId.value = parentCommentId

  // 先将评论数组中一级评论的所有 isShowReplyForm 字段设置为 false
  comments.value.forEach(comment => comment.isShowReplyForm = false)
  // 拿到当前下标的评论
  let comment = comments.value[index]
  // isShowReplyForm 置为 true
  comment.isShowReplyForm = true
  // 动态设置评论回复表单中的 textarea 的 placeholder 提示文字
  replyPlaceholderText.value = '回复 @' + nickname + ':'
  nextTick(() => {
    initPopovers()
    initTooltips()
  })
}

// 评论回复发送事件
const onReplyContentSubmit = () => {
  // 校验
  if (replyContent.value.length === 0) {
    showMessage('请填写回复内容', 'warning')
    return
  }
  // 评论回复内容
  commentForm.content = replyContent.value
  commentForm.replyCommentId = currReplyCommentId.value
  commentForm.parentCommentId = currParentCommentId.value
  // 请求接口
  publishComment(commentForm).then((res: any) => {
    if (!res.success) {
      // 获取服务端返回的错误消息
      let message = res.message
      // 提示错误消息
      showMessage(message, 'error')
      return
    }

    showMessage('回复评论成功')
    // 将评论回复的内容置空
    replyContent.value = ''
    commentForm.content = ''
    // 重新渲染评论列表
    initComments()
  })
}

onMounted(() => {
  initTooltips()
  initPopovers()
})

const emojis = ref<string[]>(['😃', '😁', '😅', '😂', '😍', '😜', '😝', '🤑', '🥵', '🥰', '😙', '😎'
  , '😵', '😭', '😱', '😖', '🥳', '👽', '🙈', '🤡', '😤', '💣', '💯', '💢', '❤️', '👍', '👏', '👋', '👌', '🤏', '🙏'])

// 添加 Emoji 表情
const addEmoji = (emoji: string) => {
  commentForm.content = commentForm.content + emoji
}

// 评论回复表单：添加 Emoji
const addReplyEmoji = (emoji: string) => {
  replyContent.value = replyContent.value + emoji
}

// 总评论数
const total = ref<number>(0)

// 评论数组
const comments = ref<CommentItem[]>([])

// 监听路由
watch(route, () => {
  // 设置评论表单中的路由路径
  commentForm.routerUrl = route.query.articleId ? (route.path + '?articleId=' + route.query.articleId) : route.path
  // 重新渲染评论数据
  initComments()
})

function initComments() {
  // 通过路由 query 中的 articleId 是否为空，来判断是文档详情页，还是 wiki 详情页，从而设置不同的路由地址
  let path = route.query.articleId ? (route.path + '?articleId=' + route.query.articleId) : route.path
  // 获取当前路由下的所有评论
  findComments({ routerUrl: path }).then((res: any) => {
    if (res.success) {
      total.value = res.data.total
      comments.value = res.data.comments
    }
  })
}
initComments()

// 评论表单
const commentForm = reactive<CommentForm>({
  avatar: '',
  content: '',
  mail: '',
  nickname: '',
  routerUrl: route.query.articleId ? (route.path + '?articleId=' + route.query.articleId) : route.path,
  website: '',
  replyCommentId: null,
  parentCommentId: null
})

// 一级评论发布点击事件
const onPublishCommentClick = () => {
  // 清空其他需要清空的字段
  commentForm.replyCommentId = null
  commentForm.parentCommentId = null
  // 校验
  if (commentForm.content.length === 0) {
    showMessage('请填写评论内容', 'warning')
    return
  }

  publishComment(commentForm).then((res: any) => {

    if (!res.success) {
      // 获取服务端返回的错误消息
      let message = res.message
      // 提示错误消息
      showMessage(message, 'error')
      return
    }

    showMessage('评论发布成功')

    // 将表单对象中的 content 评论内容置空
    commentForm.content = ''
    // 重新渲染表单列表
    initComments()
  })
}

</script>