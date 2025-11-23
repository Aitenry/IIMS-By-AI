<template>
  <!-- 声像档案详情 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-video-line"></i>
            </el-icon>
            录音类型
          </div>
        </template>
        {{ getOptionLabel(recordingTypes, form.recordingType) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-music-line"></i>
            </el-icon>
            音乐类型
          </div>
        </template>
        {{ getOptionLabel(musicTypes, form.musicType) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-image-line"></i>
            </el-icon>
            图片类型
          </div>
        </template>
        {{ getOptionLabel(imageTypes, form.imageType) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-video-play-line"></i>
            </el-icon>
            视频类型
          </div>
        </template>
        {{ getOptionLabel(videoTypes, form.videoType) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            录制时间
          </div>
        </template>
        {{ form.recordingTime || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-time-line"></i>
            </el-icon>
            录制时长
          </div>
        </template>
        {{ form.recordingDuration !== undefined ? form.recordingDuration + ' 分钟' : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-file-line"></i>
            </el-icon>
            文件格式
          </div>
        </template>
        {{ getOptionLabel(fileFormats, form.fileFormat) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-info-card-line"></i>
            </el-icon>
            备注说明
          </div>
        </template>
        {{ form.remarks || '-' }}
      </el-descriptions-item>
    </el-descriptions>
  </el-scrollbar>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 定义 props
const props = defineProps<{
  metadata?: string
  isReadOnly?: boolean
}>()

// 定义表单数据类型
interface AudioVisualForm {
  recordingType: string
  musicType: string
  imageType: string
  videoType: string
  recordingTime: string
  recordingDuration: number
  fileFormat: string
  remarks: string
}

// 定义选项类型
interface OptionItem {
  value: string
  label: string
}

// 表单数据
const form = ref<AudioVisualForm>({
  recordingType: '',
  musicType: '',
  imageType: '',
  videoType: '',
  recordingTime: '',
  recordingDuration: 0,
  fileFormat: '',
  remarks: ''
})

// 录音类型选项
const recordingTypes = ref<OptionItem[]>([
  { value: '1', label: '会议录音' },
  { value: '2', label: '访谈录音' },
  { value: '3', label: '讲座录音' },
  { value: '4', label: '采访录音' }
])

// 音乐类型选项
const musicTypes = ref<OptionItem[]>([
  { value: '1', label: '古典音乐' },
  { value: '2', label: '流行音乐' },
  { value: '3', label: '民族音乐' },
  { value: '4', label: '爵士音乐' }
])

// 图片类型选项
const imageTypes = ref<OptionItem[]>([
  { value: '1', label: '照片' },
  { value: '2', label: '图表' },
  { value: '3', label: '示意图' },
  { value: '4', label: '扫描件' }
])

// 视频类型选项
const videoTypes = ref<OptionItem[]>([
  { value: '1', label: '会议视频' },
  { value: '2', label: '教学视频' },
  { value: '3', label: '宣传片' },
  { value: '4', label: '纪录片' }
])

// 文件格式选项
const fileFormats = ref<OptionItem[]>([
  { value: 'mp3', label: 'MP3' },
  { value: 'wav', label: 'WAV' },
  { value: 'flac', label: 'FLAC' },
  { value: 'mp4', label: 'MP4' },
  { value: 'avi', label: 'AVI' },
  { value: 'mov', label: 'MOV' },
  { value: 'jpg', label: 'JPG' },
  { value: 'png', label: 'PNG' }
])

// 根据值获取选项标签
const getOptionLabel = (options: OptionItem[], value: string) => {
  const option = options.find(item => item.value === value)
  return option ? option.label : ''
}

// 初始化档案元数据
const initArchiveMetadata = () => {
  if (props.metadata) {
    try {
      const metadata = JSON.parse(props.metadata)
      form.value = {
        recordingType: metadata.recordingType || '',
        musicType: metadata.musicType || '',
        imageType: metadata.imageType || '',
        videoType: metadata.videoType || '',
        recordingTime: metadata.recordingTime || '',
        recordingDuration: metadata.recordingDuration !== undefined ? metadata.recordingDuration : 0,
        fileFormat: metadata.fileFormat || '',
        remarks: metadata.remarks || ''
      }
    } catch (error) {
      console.error('解析元数据失败:', error)
    }
  }
}

// 组件挂载后初始化
onMounted(() => {
  initArchiveMetadata()
})

// 暴露给父组件的表单数据
defineExpose({
  form: form
})
</script>

