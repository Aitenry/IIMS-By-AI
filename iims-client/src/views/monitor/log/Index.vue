<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <el-select
          v-model="selectedLevel"
          clearable
          placeholder="日志级别"
          style="width: 100px"
          @change="handleChange"
      >
        <el-option
            v-for="item in levelOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
        />
      </el-select>
    </div>
    <el-table v-loading="listLoading" :data="list" element-loading-text="数据加载中..." border fit highlight-current-row
              empty-text="暂无数据" max-height="calc(100vh - 200px)" style="width: calc(100% - 40px); margin: auto">
      <el-table-column align="center" label="序号" width="65">
        <template #default="scope">
          {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="日志级别" align="center" width="100" prop="level">
        <template #default="scope">
          <el-tag :type="formatLevel(scope.row.level).type">{{ formatLevel(scope.row.level).title }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="线程名" align="center" prop="threadName" />
      <el-table-column label="日志名称" align="center" prop="loggerName" />
      <el-table-column label="日志信息" align="center" prop="message" />
      <el-table-column align="center" prop="createTime" width="200" label="创建时间">
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-tooltip class="item" effect="dark" content="删除" placement="bottom">
            <el-button :disabled="row.systemic == 1" plain type="danger" size="small" icon="delete"
                       @click="deleteLogHandle(row)" />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <el-row class="page">
      <el-col :span="24">
        <el-pagination style="justify-content: center; margin-top: 20px;" background :current-page="pages.page" :page-sizes="[100, 200, 300]" :page-size="pages.pageSize"
                       layout="sizes, total, prev, pager, next, jumper" :total="pages.total" :hide-on-single-page="true"
                       @current-change="handleCurrentChange" @size-change="handleSizeChange" />
      </el-col>
    </el-row>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLogPage, deleteLog } from '@/api/monitor/log'

const list = ref<any[]>([])
const listLoading = ref(true)
const selectedLevel = ref<string | null>(null)
const levelOptions = [
  {
    value: 'INFO',
    label: 'INFO'
  },
  {
    value: 'WARN',
    label: 'WARN'
  },
  {
    value: 'ERROR',
    label: 'ERROR'
  }
]

const pages = reactive({
  page: 1,
  pageSize: 100,
  total: 0,
  level: null as string | null,
  loggerName: ''
})

interface LevelConfig {
  type: string
  title: string
}

interface LevelMapper {
  [key: string]: LevelConfig
}

const handleChange = async (value: string | null) => {
  pages.level = value
  await fetchData()
}

const formatLevel = (level: string) => {
  const mapper: LevelMapper = {
    'INFO': {
      type: 'info',
      title: 'INFO'
    },
    'WARN': {
      type: 'warning',
      title: 'WARN'
    },
    'ERROR': {
      type: 'danger',
      title: 'ERROR'
    }
  }
  return mapper[level]
}

// 获取列表
const fetchData = async () => {
  listLoading.value = true
  try {
    const res = await getLogPage(pages)
    list.value = res.data.list
    pages.total = res.data.total
  } catch (error) {
    console.log(error)
  } finally {
    listLoading.value = false
  }
}

// 分页相关
const handleCurrentChange = (val: number) => {
  pages.page = val
  fetchData()
}

const handleSizeChange = (val: number) => {
  pages.pageSize = val
  fetchData()
}

// 删除角色
const deleteLogHandle = async (row: any) => {
  const id = row.id
  try {
    await ElMessageBox.confirm(
        `是否确认删除日志为“${row.loggerName}”的数据项?`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await deleteLog(id)
    if (res.code === 1) {
      await fetchData()
      ElMessage.success({
        message: '删除成功'
      })
    }
  } catch (error) {
    console.log(error)
  }
}

// 组件挂载后执行
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* 保持原有的样式 */
</style>