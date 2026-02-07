import sse from '@/utils/request-sse.ts'
import request from "@/utils/request.ts";

const VITE_APP_API_URL = import.meta.env.VITE_APP_API_URL

/**
 * 接收消息的函数
 * @param {Function} callback - 回调函数，当接收到消息时调用
 */
export function subscriberConnect(callback: any) {
    // 建立SSE连接，并传入回调函数
    const cancelConnection = sse({
        url: `${VITE_APP_API_URL}/subscriber/connect`,
        method: 'GET'
    }, {}, callback)

    return () => {
        if (cancelConnection) {
            cancelConnection()
        }
    }
}

export function subscriberCompleted(userId: string) {
    return request({
        url: `/subscriber/completed/${userId}`,
        method: 'get'
    }) as any
}