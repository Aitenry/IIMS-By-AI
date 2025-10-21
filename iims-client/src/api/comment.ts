import request from '@/utils/request.ts'

export function publishComment(data: any) {
  return request({
    url: '/comment/publish',
    method: 'post',
    data
  }) as any
}

export function findComments(data: any) {
  return request({
    url: '/comment/list',
    method: 'post',
    data
  }) as any
}