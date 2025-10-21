<template>
  <div>
    <el-divider content-position="left">基础属性</el-divider>
    <el-descriptions
        class="margin-top"
        :column="3"
        border
    >
      <el-descriptions-item>
        <template #label>
          <i class="ri-user-heart-line"></i>
          责任人
        </template>
        <el-select
            v-model="form.archivalResponsible"
            remote
            filterable
            placeholder="请搜索责任人"
            :remote-method="remoteMethod"
            :loading="loadingResponsible"
            style="width: 230px"
        >
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-pages-line"></i>
          档号
        </template>
        <el-input
            v-model="form.archivalCode"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-draft-line"></i>
          题名
        </template>
        <el-input
            v-model="form.archivalTitle"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-time-line"></i>
          档案年度
        </template>
        <el-date-picker
            v-model="form.archivalYear"
            type="year"
            value-format="YYYY"
            placeholder="选择年度"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-time-line"></i>
          档案日期
        </template>
        <el-date-picker
            v-model="form.archivalDate"
            type="date"
            placeholder="选择日期"
            :picker-options="pickerOptions"
            style="width: 230px;"
        />
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-calendar-schedule-line"></i>
          保管期限
        </template>
        <el-select
            v-model="form.archivalDeadline"
            placeholder="选择期限"
            style="width: 230px;"
        >
          <el-option
              v-for="item in deadlines"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-information-line"></i>
          档案密级
        </template>
        <el-select
            v-model="form.archivalLevel"
            placeholder="选择密级"
            style="width: 230px;"
        >
          <el-option
              v-for="item in levels"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <i class="ri-info-card-line"></i>
          档案摘要
        </template>
        <el-input
            v-model="form.archivalAbstract"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 3}"
            placeholder="请输入档案摘要"
        />
      </el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">档案属性</el-divider>
    <component
        :is="componentMap[name]"
        v-if="loading"
        ref="metadata"
        :metadata="metadataProperty"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getArchiveMetadata } from '@/api/dms/collect.js'
import { getBaseAdminPage } from "@/api/admin.js"

// 动态组件注册
import OperateDocArchive from './operate/OperateDocArchive.vue'
import OperateTechArchive from './operate/OperateTechArchive.vue'
import OperatePrsArchive from './operate/OperatePrsArchive.vue'
import OperateAcctArchive from './operate/OperateAcctArchive.vue'
import OperateProjectArchive from './operate/OperateProjectArchive.vue'
import OperateProfArchive from './operate/OperateProfArchive.vue'
import OperateAudioVisArchive from './operate/OperateAudioVisArchive.vue'
import OperateContractArchive from './operate/OperateContractArchive.vue'
import OperateJudicialArchive from './operate/OperateJudicialArchive.vue'
import OperateBuildArchive from './operate/OperateBuildArchive.vue'
import OperateFinancialArchive from './operate/OperateFinancialArchive.vue'
import OperatePhysicalArchive from './operate/OperatePhysicalArchive.vue'

type ComponentMap = {
  [key: string]: any;
}

const componentMap: ComponentMap = {
  'OperateDocArchive': OperateDocArchive,
  'OperateTechArchive': OperateTechArchive,
  'OperatePrsArchive': OperatePrsArchive,
  'OperateAcctArchive': OperateAcctArchive,
  'OperateProjectArchive': OperateProjectArchive,
  'OperateProfArchive': OperateProfArchive,
  'OperateAudioVisArchive': OperateAudioVisArchive,
  'OperateContractArchive': OperateContractArchive,
  'OperateJudicialArchive': OperateJudicialArchive,
  'OperateBuildArchive': OperateBuildArchive,
  'OperateFinancialArchive': OperateFinancialArchive,
  'OperatePhysicalArchive': OperatePhysicalArchive
}

// Props
const props = defineProps<{
  id: string | null
  name: string
}>()

// Refs
const metadata = ref()

// 数据模型
const loading = ref(false)
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

const options = ref<{ value: string; label: string }[]>([])
const loadingResponsible = ref(false)
const pickerOptions = {
  disabledDate(time: Date) {
    return time.getTime() > Date.now()
  },
  shortcuts: [{
    text: '今天',
    onClick(picker: any) {
      picker.$emit('pick', new Date())
    }
  }, {
    text: '昨天',
    onClick(picker: any) {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24)
      picker.$emit('pick', date)
    }
  }, {
    text: '一周前',
    onClick(picker: any) {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
      picker.$emit('pick', date)
    }
  }]
}

const deadlines = ref([
  { value: '999', label: '永久存储' },
  { value: '10', label: '定期10年' },
  { value: '30', label: '定期30年' }
])

const levels = ref([
  { value: '1', label: '绝密' },
  { value: '2', label: '机密' },
  { value: '3', label: '秘密' }
])

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
    }
  } else {
    loading.value = true
  }
}

// 远程搜索方法
const remoteMethod = (username: string) => {
  if (username) {
    loadingResponsible.value = true
    setTimeout(async () => {
      loadingResponsible.value = false
      const res = await getBaseAdminPage({ username: username })
      const data = res.data
      const info: { value: string; label: string }[] = []
      data.forEach((item: any) => {
        info.push({
          value: item.id,
          label: item.username
        })
      })
      options.value = info.filter((item) => {
        return item.label.toLowerCase().includes(username.toLowerCase())
      })
    }, 300)
  } else {
    options.value = []
  }
}

// 获取操作表单数据
const getOperateFormData = () => {
  const data = { ...form }
  if (props.id) data.id = props.id
  if (form.archivalResponsible) {
    const selectedOption = options.value.find(item => item.value === form.archivalResponsible?.value)
    if (selectedOption) {
      data.archivalResponsible = selectedOption
    }
  }
  data.metadataProperty = JSON.stringify(metadata.value?.form || {})
  return data
}

// 组件挂载后初始化
onMounted(() => {
  initArchiveMetadata()
})

// 暴露给父组件的方法
defineExpose({
  getOperateFormData
})
</script>