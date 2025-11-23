import request from '@/utils/request.ts'

export function getModelPage(data: any) {
    return request({
        url: '/model/page',
        method: 'post',
        data
    }) as any
}

export function deleteModel(id: string) {
    return request({
        url: `/model/del/${id}`,
        method: 'get'
    }) as any
}