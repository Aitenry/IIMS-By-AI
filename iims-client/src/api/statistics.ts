import request from '@/utils/request.ts'

export function getStatisticsData() {
    return request({
        url: '/statistics/data',
        method: 'get'
    }) as any
}

export function getStatisticsDayData() {
    return request({
        url: '/statistics/day/data',
        method: 'get'
    }) as any
}