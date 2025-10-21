import request from '@/utils/request.ts'

/**
 * 角色分页查询
 * @param {*} data
 * @returns
 */
export function getRolePage(data: any) {
  return request({
    url: '/role/page',
    method: 'post',
    data
  }) as any
}

/**
 * 查询所有角色
 * @returns
 */
export function getRoleList() {
  return request({
    url: '/role/list',
    method: 'get'
  }) as any
}

/**
 * 更新角色
 * @param {*} data
 * @returns
 */
export function updateRole(data: any) {
  return request({
    url: '/role',
    method: 'put',
    data
  }) as any
}

/**
 * 新增角色
 * @param {*} data
 * @returns
 */
export function addRole(data: any) {
  return request({
    url: '/role',
    method: 'post',
    data
  }) as any
}

/**
 * 删除角色
 * @param {*} id
 * @returns
 */
export function deleteRole(id: string) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  }) as any
}

