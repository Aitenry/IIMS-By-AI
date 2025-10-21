import sse from '@/utils/request-chat.ts'
import request from '@/utils/request.ts'

const VITE_APP_API_URL = import.meta.env.VITE_APP_API_URL

/**
 * 接收AI回答的函数
 * @param {string} uuid - 唯一标识符
 * @param data
 * @param {Function} callback - 回调函数，当接收到消息时调用
 */
export function receiveAnswer(uuid: string, data: any, callback: any) {
  // 建立SSE连接，并传入回调函数
  const cancelConnection = sse({
    url: `${VITE_APP_API_URL}/ai/chat/receive/answer/${uuid}`,
    method: 'POST',
    data
  }, {}, callback)

  return () => {
    if (cancelConnection) {
      cancelConnection()
    }
  }
}

export function stopAnswer(uuid: string) {
  return request({
    url: `/ai/chat/stop/answer/${uuid}`,
    method: 'get'
  }) as any
}

/**
 * 对话记录分页查询
 * @param {*} data
 * @returns
 */
export function getChatTopicList(data: any) {
  return request({
    url: '/ai/topic/page',
    method: 'post',
    data
  }) as any
}

/**
 * 对话内容记录分页查询
 * @param {*} data
 * @returns
 */
export function getChatDialogueList(data: any) {
  return request({
    url: '/ai/dialogue/page',
    method: 'post',
    data
  }) as any
}

/**
 * 收藏开关
 * @param {*} data
 * @returns
 */
export function starSwitch(data: any) {
  return request({
    url: `/ai/dialogue/star/${data.status}?id=${data.id}`,
    method: 'post'
  }) as any
}

/**
 * 切换状态
 * @param {*} data
 * @returns
 */
export function exchangeFeedback(data: any) {
  return request({
    url: `/ai/dialogue/feedback/${data.status}?id=${data.id}`,
    method: 'post'
  }) as any
}

/**
 * 删除对话记录
 * @returns
 */
export function delDialogue(lastId: string) {
  return request({
    url: `/ai/dialogue/del/${lastId}`,
    method: 'get'
  }) as any
}

/**
 * 删除对话
 * @param {*} ids
 * @returns
 */
export function delTopic(ids: any) {
  return request({
    url: `/ai/topic/del`,
    method: 'post',
    data: ids
  }) as any
}

/**
 * 重命名对话
 * @param {*} data
 * @returns
 */
export function renameTopic(data: any) {
  return request({
    url: `/ai/topic/rename`,
    method: 'post',
    data
  }) as any
}


/**
 * 清除对话历史记录
 * @param {*} id
 * @returns
 */
export function clearDialogueFromTopic(id: string) {
  return request({
    url: `/ai/topic/clear/${id}`,
    method: 'get'
  }) as any
}