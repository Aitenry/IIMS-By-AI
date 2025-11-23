import request from '@/utils/request.ts'

export function getLogPage(data: any) {
    return request({
        url: '/log/page',
        method: 'post',
        data
    }) as any
}

export function deleteLog(id: string) {
    return request({
        url: `/log/del/${id}`,
        method: 'get'
    }) as any
}