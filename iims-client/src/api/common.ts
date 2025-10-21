import request from '@/utils/request.ts'

export function login(data: any) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  }) as any
}

export function loginKey() {
  return request({
    url: '/admin/login/key',
    method: 'get'
  }) as any
}

export function getInfo(id: string) {
  return request({
    url: `/admin/${id}`,
    method: 'get'
  }) as any
}

export function logout() {
  return request({
    url: '/admin/logout',
    method: 'get'
  }) as any
}

export function uploadFile(formData: any) {
  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }) as any
}
