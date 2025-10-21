export const getBigNum = (arr) => {
  return Math.max(...arr)
}

export const getNowFormatDate = () => {
  const date = new Date()
  const seperator1 = '-'
  const year = date.getFullYear()
  let month = date.getMonth() + 1
  let strDate = date.getDate()
  let hour = date.getHours()
  let minute = date.getMinutes()
  let second = date.getSeconds()
  if (month >= 1 && month <= 9) {
    month = '0' + month
  }
  if (hour >= 1 && hour <= 9) {
    hour = '0' + hour
  }
  if (minute >= 1 && minute <= 9) {
    minute = '0' + minute
  }
  if (second >= 1 && second <= 9) {
    second = '0' + second
  }
  if (strDate >= 0 && strDate <= 9) {
    strDate = '0' + strDate
  }

  const currentdate =
    year +
    seperator1 +
    month +
    seperator1 +
    strDate +
    seperator1 +
    hour +
    seperator1 +
    minute +
    seperator1 +
    second
  return currentdate
}

/**
 * @description: 文件下载
 * @param {Blob} blob 参数1：blob对象
 * @param {string} name 参数2：文件名称，包含文件后缀
 * @return {*}
 */
export const download = (blob, name) => {
  const link = document.createElement('a') // 创建一个a标签
  const url = URL.createObjectURL(blob) // 将blob文件对象通过URL.createObjectURL()方法转为为url
  link.href = url // 为a标签设置href属性，并赋值为url
  link.download = name // 定义下载的文件名，文件名要包含后缀哟！如'导出EXCEL.xlsx'
  document.body.appendChild(link) // 把a标签放在body上
  link.click() // 出发a标签点击下载
  document.body.removeChild(link) // 在body中移除这个a标签
  URL.revokeObjectURL(url) // 释放blob对象
}

// 判断开始日期（时间）必须小于结束日期（时间）
export const isCheckStartAndEndTime = (startTime, endTime) => {
  const d1 = new Date(startTime.replace(/\-/g, '/'))
  const d2 = new Date(endTime.replace(/\-/g, '/'))
  if (startTime !== '' && endTime !== '' && d1 >= d2) {
    return false
  }

  return true
}
