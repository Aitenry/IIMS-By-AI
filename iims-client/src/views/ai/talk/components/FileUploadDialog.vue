<template>
  <el-dialog v-model="visible" title="上传对话文件" width="500" align-center>
    <el-upload ref="uploadRef" :file-list="fileList" drag class="upload-file" :action="baseUrl + '/common/upload'" :headers="config" :data="{ itemType: 1 }"
               :auto-upload="false" :on-success="handleUploadSuccess" list-type="picture-card" :limit="1">
      <el-icon><Plus /></el-icon>
      <template #file="{ file }">
        <div style="width: 100%; display: flex; justify-content: center; align-items: center;">
          <div style="margin: 0 6px; display: grid; justify-content: center; text-align: center;">
            <i :class="getFileIconClass(file?.raw?.name || '')" style="font-size: 32px;"></i>
            <span>{{ formatFileSize(file?.raw.size) }}</span>
            <span style="display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; font-size: 14px;">
              {{ file?.raw.name }}
            </span>
          </div>
          <span class="el-upload-list__item-actions">
            <span class="el-upload-list__item-delete"
                  @click="handleRemove(file)"
            >
              <el-icon><Delete /></el-icon>
            </span>
          </span>
        </div>
      </template>
    </el-upload>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload"> 上传 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import type { UploadFile, UploadInstance } from 'element-plus'

// 定义组件选项
defineOptions({
  name: 'FileUploadDialog'
})

// 定义 props 和 emits
const props = withDefaults(defineProps<{
  modelValue: boolean
  fileList: Array<any>
  baseUrl: string
  config: Record<string, any>
}>(), {
  modelValue: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'handle-upload-file-success', res: any, file: UploadFile): void
  (e: 'handle-upload-file-remove', file: any): void
}>()

// 定义 ref
const uploadRef = ref<UploadInstance>()

// 计算属性
const visible = computed({
  get() {
    return props.modelValue
  },
  set(value: boolean) {
    emit('update:modelValue', value)
  }
})

// 方法
const submitUpload = () => {
  uploadRef.value?.submit()
}

const handleUploadSuccess = (res: any, file: UploadFile) => {
  emit('handle-upload-file-success', res, file)
}

const handleRemove = (file: any) => {
  emit('handle-upload-file-remove', file)
}

// 根据文件名获取文件图标
const getFileIconClass = (filename: string) => {
  const ext = filename.split('.').pop()?.toLowerCase() || ''
  const icons: Record<string, string> = {
    pdf: 'ri-file-pdf-2-line',
    png: 'ri-file-image-line',
    jpg: 'ri-file-image-line',
    pptx: 'ri-file-ppt-2-line',
    ppt: 'ri-file-ppt-2-line',
    jpeg: 'ri-file-image-line',
    doc: 'ri-file-word-2-line',
    docx: 'ri-file-word-2-line',
    xls: 'ri-file-excel-2-line',
    xlsx: 'ri-file-excel-2-line',
    md: 'ri-markdown-line',
    default: 'ri-file-unknow-line',
  }
  return icons[ext] || icons.default
}

// 格式化文件大小
const formatFileSize = (sizeInBytes: number) => {
  if (sizeInBytes < 1024) return `${sizeInBytes} B`
  if (sizeInBytes < 1024 * 1024) return `${(sizeInBytes / 1024).toFixed(1)} KB`
  return `${(sizeInBytes / (1024 * 1024)).toFixed(1)} MB`
}
</script>

<style scoped>
/* Add scoped styles if needed, or move global ones from original */
</style>