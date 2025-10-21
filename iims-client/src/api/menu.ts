import request from '@/utils/request.ts'

/**
 * 菜单分页查询
 * @param {*} data
 * @returns
 */
export function getMenuPage(data: any) {
  return request({
    url: '/menu/page',
    method: 'post',
    data
  }) as any
}

/**
 * 查询所有菜单
 * @returns
 */
export function getMenuList() {
  return request({
    url: '/menu/list',
    method: 'get'
  }) as any
}

/**
 * 查询所有菜单
 * @returns
 */
export function getMenuListByUserId() {
  return request({
    url: '/menu/getMenuListByUserId',
    method: 'get'
  }) as any
}

/**
 * 查询菜单树形结构
 * @returns
 */
export function getMenuTree() {
  return request({
    url: '/menu/menuTree',
    method: 'get'
  }) as any
}

/**
 * 更新菜单
 * @param {*} data
 * @returns
 */
export function updateMenu(data: any) {
  return request({
    url: '/menu',
    method: 'put',
    data
  }) as any
}

/**
 * 新增菜单
 * @param {*} data
 * @returns
 */
export function addMenu(data: any) {
  return request({
    url: '/menu',
    method: 'post',
    data
  }) as any
}

/**
 * 删除菜单
 * @param {*} id
 * @returns
 */
export function deleteMenu(id: string) {
  return request({
    url: `/menu/${id}`,
    method: 'delete'
  }) as any
}
