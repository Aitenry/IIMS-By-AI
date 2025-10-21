import request from '@/utils/request.ts'

/**
 * 查询菜单树形结构
 * @returns
 */
export function getMenuTree() {
  return request({
    url: '/dms/collect/menuTree',
    method: 'get'
  }) as any
}

/**
 * 查询当前菜单下的档案
 * @param {*} data
 * @returns
 */
export function getArchiveList(data: any) {
  return request({
    url: '/dms/collect/page',
    method: 'post',
    data
  }) as any
}

/**
 * 获取当前档案元数据
 * @param {*} id
 * @returns
 */
export function getArchiveMetadata(id: string) {
  return request({
    url: `/dms/collect/metadata/${id}`,
    method: 'get'
  }) as any
}

/**
 * 编辑当前档案元数据
 * @param {*} data
 * @returns
 */
export function editArchiveMetadata(data: any) {
  return request({
    url: `/dms/collect/edit/metadata`,
    method: 'post',
    data
  }) as any
}

/**
 * 添加档案元数据
 * @param {*} data
 * @returns
 */
export function addArchiveMetadata(data: any) {
  return request({
    url: `/dms/collect/add/metadata`,
    method: 'post',
    data
  }) as any
}

/**
 * 删除档案元数据
 * @param {*} data
 * @returns
 */
export function delArchiveMetadata(data: any) {
  return request({
    url: `/dms/collect/del/metadata`,
    method: 'post',
    data
  }) as any
}
