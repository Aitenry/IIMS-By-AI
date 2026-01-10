import request from '@/utils/request.ts'

// 获取知识库分页数据
export function getWikiPageList(data: any) {
  return request({
    url: '/wiki/list',
    method: 'post',
    data
  }) as any
}

// 获取知识库发布分页数据
export function getWikiPublishPageList(data: any) {
  return request({
    url: '/wiki/publish/list',
    method: 'post',
    data
  }) as any
}

// 新增知识库
export function addWiki(data: any) {
  return request({
    url: '/wiki/add',
    method: 'post',
    data
  }) as any
}

// 更新知识库置顶状态
export function updateWikiIsTop(data: any) {
  return request({
    url: '/wiki/isTop/update',
    method: 'post',
    data
  }) as any
}

// 更新知识库发布状态
export function updateWikiIsPublish(data: any) {
  return request({
    url: '/wiki/isPublish/update',
    method: 'post',
    data
  }) as any
}

// 删除知识库
export function deleteWiki(data: any) {
  return request({
    url: '/wiki/delete',
    method: 'post',
    data
  }) as any
}

// 更新知识库
export function updateWiki(data: any) {
  return request({
    url: '/wiki/update',
    method: 'post',
    data
  }) as any
}

// 获取知识库目录
export function getWikiCatalogs(data: any) {
  return request({
    url: '/wiki/catalog/list',
    method: 'post',
    data
  }) as any
}

// 更新知识库目录
export function updateWikiCatalogs(data: any) {
  return request({
    url: '/wiki/catalog/update',
    method: 'post',
    data
  }) as any
}

export function getWikiArticlePreNext(data: any) {
  return request({
    url: '/wiki/article/preNext',
    method: 'post',
    data
  }) as any
}

/**
 * 根据Id查询文档详情
 * @param {*} data
 * @returns
 */
export function getWikiArticleDetailById(data: any) {
  return request({
    url: `/wiki/article/detail`,
    method: 'post',
    data
  }) as any
}

export function documentEmbedding(wikiId: string) {
  return request({
    url: `/wiki/document/embedding/${wikiId}`,
    method: 'post'
  }) as any
}