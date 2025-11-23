import request from '@/utils/request.ts'

/**
 * 查询菜单树形结构
 * @returns
 */
export function getMenuTree(companyId: string | null) {
    return request({
        url: `/organization/menuTree?companyId=${companyId ? companyId : ''}`,
        method: 'get'
    }) as any
}

/**
 * 查询菜单树形结构
 * @returns
 */
export function getAllMenuTree() {
    return request({
        url: `/organization/allMenuTree`,
        method: 'get'
    }) as any
}