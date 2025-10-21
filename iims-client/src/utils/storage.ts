/*
 * @Author: Musa Tabitay
 * @Date: 2022-01-01 22:49:52
 * @LastEditTime: 2022-01-01 23:02:40
 */

/**
 * 存储数据
 */
export const setItem = (key, value) => {
  // 将复杂数据类型的数据转换为JSON格式字符串进行存储
  if (typeof value === 'object') {
    value = JSON.stringify(value)
  }
  window.localStorage.setItem(key, value)
}

/**
 * 获取数据
 */
export const getItem = key => {
  const data = window.localStorage.getItem(key)
  try {
    return JSON.parse(data)
  } catch (err) {
    return data
  }
}

/**
 * 删除数据
 */
export const removeItem = key => {
  window.localStorage.removeItem(key)
}
