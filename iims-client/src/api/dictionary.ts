import request from '@/utils/request.ts'

export function getCategoryAll() {
  return request({
    url: '/dictionary/category/list',
    method: 'get'
  }) as any
}

export function getTagsAll() {
  return request({
    url: '/dictionary/tags/list',
    method: 'get'
  }) as any
}

export function getDictList(data: any) {
  return request({
    url: '/dictionary/list',
    method: 'post',
    data
  }) as any
}

export function deleteDict(data: any) {
  return request({
    url: `/dictionary/delete`,
    method: 'post',
    data
  }) as any
}

export function disableDict(id: string, isDisable: boolean) {
  return request({
    url: `/dictionary/disable/${id}/${isDisable}`,
    method: 'get'
  }) as any
}

export function addDict(data: any) {
  return request({
    url: '/dictionary/add',
    method: 'post',
    data
  }) as any
}

export function updateDict(data: any) {
  return request({
    url: '/dictionary/update',
    method: 'post',
    data
  }) as any
}

export function getDictValueList(dictId: string, data: any) {
  return request({
    url: `/dictionary/value/list/${dictId}`,
    method: 'post',
    data
  }) as any
}

export function addDictValue(data: any) {
  return request({
    url: '/dictionary/value/add',
    method: 'post',
    data
  }) as any
}

export function updateDictValue(data: any) {
  return request({
    url: '/dictionary/value/update',
    method: 'post',
    data
  }) as any
}

export function deleteDictValue(data: any) {
  return request({
    url: '/dictionary/value/delete',
    method: 'post',
    data
  }) as any
}
