<template>
  <!-- 声像档案 -->
  <el-scrollbar class="archive-dialog-scrollbar" style="height: calc(50vh - 200px);">
    <el-descriptions
        class="margin-top"
        :column="3"
        border
        style="height: calc(50vh - 200px);"
    >
      <el-descriptions-item>
        <template #label>
          <i class="ri-video-line"></i>
          录音类型
        </template>
        <el-select
            v-model="form.recordingType"
            placeholder="选择录音类型"
            style="width: 230px;"
        >
          <el-option
              v-for="item in recordingTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-music-line"></i>
          音乐类型
        </template>
        <el-select
            v-model="form.musicType"
            placeholder="选择音乐类型"
            style="width: 230px;"
        >
          <el-option
              v-for="item in musicTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-image-line"></i>
          图片类型
        </template>
        <el-select
            v-model="form.imageType"
            placeholder="选择图片类型"
            style="width: 230px;"
        >
          <el-option
              v-for="item in imageTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-video-play-line"></i>
          视频类型
        </template>
        <el-select
            v-model="form.videoType"
            placeholder="选择视频类型"
            style="width: 230px;"
        >
          <el-option
              v-for="item in videoTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          录制时间
        </template>
        <el-date-picker
            v-model="form.recordingTime"
            type="datetime"
            placeholder="选择录制时间"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-time-line"></i>
          录制时长
        </template>
        <el-input-number
            v-model="form.recordingDuration"
            style="width: 230px;"
            :min="0"
            :precision="2"
            placeholder="请输入录制时长(分钟)"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-file-line"></i>
          文件格式
        </template>
        <el-select
            v-model="form.fileFormat"
            placeholder="选择文件格式"
            style="width: 230px;"
        >
          <el-option
              v-for="item in fileFormats"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-info-card-line"></i>
          备注说明
        </template>
        <el-input
            v-model="form.remarks"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入备注说明"
        />
      </el-descriptions-item>
    </el-descriptions>
  </el-scrollbar>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 定义 props
const props = defineProps<{
  metadata?: string
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