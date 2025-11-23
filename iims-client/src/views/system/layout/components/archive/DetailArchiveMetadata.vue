<template>
  <div>
    <el-divider content-position="left">基础属性</el-divider>
    <el-descriptions
        :column="3"
        border
    >
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-user-heart-line"></i>
            </el-icon>
            责任人
          </div>
        </template>
        {{ form.archivalResponsible ? form.archivalResponsible.label : '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-pages-line"></i>
            </el-icon>
            档号
          </div>
        </template>
        {{ form.archivalCode || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-draft-line"></i>
            </el-icon>
            题名
          </div>
        </template>
        {{ form.archivalTitle || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-time-line"></i>
            </el-icon>
            档案年度
          </div>
        </template>
        {{ form.archivalYear || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-time-line"></i>
            </el-icon>
            档案日期
          </div>
        </template>
        {{ form.archivalDate || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-calendar-schedule-line"></i>
            </el-icon>
            保管期限
          </div>
        </template>
        {{ getDeadlineLabel(form.archivalDeadline) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-information-line"></i>
            </el-icon>
            档案密级
          </div>
        </template>
        {{ getLevelLabel(form.archivalLevel) || '-' }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            <el-icon>
              <i class="ri-info-card-line"></i>
            </el-icon>
            档案摘要
          </div>
        </template>
        <el-input
            v-if="isEditing"
            v-model="form.archivalAbstract"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入档案摘要"
        />
        <span v-else>{{ form.archivalAbstract || '-' }}</span>
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">档案属性</el-divider>
    <component
        :is="componentMap[name]"
        v-if="loading"
        ref="metadata"
        :metadata="metadataProperty"
        :is-read-only="!isEditing"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getArchiveMetadata } from '@/api/dms/collect.js'
import { ElMessage } from 'element-plus'

// 动态组件注册
import DetailDocArchive from './detail/DetailDocArchive.vue'
import DetailTechArchive from './detail/DetailTechArchive.vue'
import DetailPrsArchive from './detail/DetailPrsArchive.vue'
import DetailAcctArchive from './detail/DetailAcctArchive.vue'
import DetailProjectArchive from './detail/DetailProjectArchive.vue'
import DetailProfArchive from './detail/DetailProfArchive.vue'
import DetailAudioVisArchive from './detail/DetailAudioVisArchive.vue'
import DetailContractArchive from './detail/DetailContractArchive.vue'
import DetailJudicialArchive from './detail/DetailJudicialArchive.vue'
import DetailBuildArchive from './detail/DetailBuildArchive.vue'
import DetailFinancialArchive from './detail/DetailFinancialArchive.vue'
import DetailPhysicalArchive from './detail/DetailPhysicalArchive.vue'

type ComponentMap = {
  [key: string]: any;
}

const componentMap: ComponentMap = {
  'DetailDocArchive': DetailDocArchive,
  'DetailTechArchive': DetailTechArchive,
  'DetailPrsArchive': DetailPrsArchive,
  'DetailAcctArchive': DetailAcctArchive,
  'DetailProjectArchive': DetailProjectArchive,
  'DetailProfArchive': DetailProfArchive,
  'DetailAudioVisArchive': DetailAudioVisArchive,
  'DetailContractArchive': DetailContractArchive,
  'DetailJudicialArchive': DetailJudicialArchive,
  'DetailBuildArchive': DetailBuildArchive,
  'DetailFinancialArchive': DetailFinancialArchive,
  'DetailPhysicalArchive': DetailPhysicalArchive
}

// Props
const props = defineProps<{
  id: string | null
  name: string
}>()

// Emits
const emit = defineEmits(['edit'])

// Refs
const metadata = ref()

// 数据模型
const loading = ref(false)
const isEditing = ref(false)
const metadataProperty = ref('')
const form = reactive({
  id: null as string | null,
  archivalResponsible: {} as { value: string; label: string } | null,
  archivalCode: '',
  archivalTitle: '',
  archivalYear: '',
  archivalDate: '',
  archivalDeadline: '',
  archivalLevel: '',
  archivalAbstract: '',
  metadataProperty: ''
})

// 保管期限选项
const deadlines = [
  { value: '999', label: '永久存储' },
  { value: '10', label: '定期10年' },
  { value: '30', label: '定期30年' }
]

// 档案密级选项
const levels = [
  { value: '1', label: '绝密' },
  { value: '2', label: '机密' },
  { value: '3', label: '秘密' }
]

// 获取保管期限标签
const getDeadlineLabel = (value: string) => {
  const item = deadlines.find(item => item.value === value)
  return item ? item.label : ''
}

// 获取档案密级标签
const getLevelLabel = (value: string) => {
  const item = levels.find(item => item.value === value)
  return item ? item.label : ''
}

// 初始化档案元数据
const initArchiveMetadata = async () => {
  if (props.id) {
    try {
      const res = await getArchiveMetadata(props.id)
      const data = res.data
      metadataProperty.value = data.metadataProperty
      if (data.archivalResponsible) {
        const info = JSON.parse(data.archivalResponsible)
        data.archivalResponsible = {
          value: info.id,
          label: info.username
        }
      }
      form.archivalResponsible = data.archivalResponsible
      form.archivalCode = data.archivalCode
      form.archivalTitle = data.archivalTitle
      form.archivalYear = data.archivalYear
      form.archivalDate = data.archivalDate
      form.archivalDeadline = data.archivalDeadline
      form.archivalLevel = data.archivalLevel
      form.archivalAbstract = data.archivalAbstract
      loading.value = true
    } catch (error) {
      console.log(error)
      ElMessage.error('获取档案信息失败')
    }
  } else {
    loading.value = true
  }
}

// 组件挂载后初始化
onMounted(() => {
  initArchiveMetadata()
})
</script>