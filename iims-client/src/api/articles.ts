import request from '@/utils/request.ts'

export function getArticlesList(data: any) {
  return request({
    url: '/article/page',
    method: 'post',
    data
  }) as any
}

/**
 * 更新文章
 * @param {*} data
 * @returns
 */
export function updateArticles(data: any) {
  return request({
    url: '/article/update',
    method: 'put',
    data
  }) as any
}

export function updateArticleIsTop(data: any) {
  return request({
    url: `/article/isTop/update/${data.isTop}?id=${data.id}`,
    method: 'post'
  }) as any
}

/**
 * 新增文章
 * @param {*} data
 * @returns
 */
export function addArticles(data: any) {
  return request({
    url: '/article/publish',
    method: 'post',
    data
  }) as any
}

/**
 * 删除文章
 * @param {*} data
 * @returns
 */
export function deleteArticles(data: any) {
  return request({
    url: `/article/delete`,
    method: 'post',
    data
  }) as any
}

/**
 * 根据Id查询文章详情
 * @param {*} id
 * @returns
 */
export function getArticleById(id: string) {
  return request({
    url: `/article/detail/${id}`,
    method: 'get'
  }) as any
}

/**
 * 根据Id查询文章详情
 * @param {*} data
 * @returns
 */
export function getArticleDetailById(data: any) {
  return request({
    url: `/article/detail/info`,
    method: 'post',
    data
  }) as any
}


export function getGenerateSummary(id: string) {
  return request({
    url: `/article/generate/summary/${id}`,
    method: 'post'
  }) as any
}