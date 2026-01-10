<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
    </div>
    <el-table v-loading="listLoading" :data="list" element-loading-text="数据加载中..." border fit highlight-current-row
              empty-text="暂无数据" max-height="calc(100vh - 200px)" style="width: calc(100% - 40px); margin: auto">
      <el-table-column type="expand">
        <template #default="props">
          <div class="pl-12 pr-2">
            <el-table :data="props.row.users" border>
              <el-table-column label="头像" align="center" width="65">
                <template #default="scope">
                  <image-with-token style="width: 40px;height: 40px;border-radius: 10px;margin-top: 7px;" :src="scope.row.imageUrl" />
                </template>
              </el-table-column>
              <el-table-column label="用户名"  align="center" width="200" prop="username" />
              <el-table-column label="部门" align="center" width="120" prop="department" />
              <el-table-column label="手机号" align="center" width="120" prop="phone" />
              <el-table-column label="简介" align="center" prop="introduction" />
              <el-table-column label="邮箱" align="center" width="200" prop="email" />
              <el-table-column label="操作" width="150" align="center">
                <template #default="{ row }">
                  <el-tooltip class="item" effect="dark" content="删除" placement="bottom">
                    <el-button :disabled="row.systemic == 1" plain type="danger" size="small" icon="delete"
                               @click="deleteLogHandle(row)" />
                  </el-tooltip>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="序号" width="65">
        <template #default="scope">
          {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="模型名称" width="200" align="center" prop="name" />
      <el-table-column label="接口协议" width="120" align="center" prop="type">
        <template #default="scope">
          <el-tag :type="formatType(scope.row.type).type">{{ formatType(scope.row.type).title }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Token" width="120" align="center" prop="token" />
      <el-table-column label="描述" align="center" prop="description" />
      <el-table-column label="是否在线" width="65" align="center" prop="isOnline">
        <template #default="scope">
          <i :style="{ color: scope.row.isOnline ? '#67c23a' : '#f56c6c' }" class="ri-circle-fill"></i>
        </template>
      </el-table-column>
      <el-table-column label="使用人数" width="65" align="center">
        <template #default="scope">
          {{ scope.row.users?.length || 0 }}
        </template>
      </el-table-column>
      <el-table-column align="center" prop="detectionTime" width="200" label="检测时间">
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.detectionTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template #default="{ row }">
          <el-tooltip class="item" effect="dark" content="编辑" placement="bottom">
            <el-button plain type="primary" size="small" icon="edit" @click="showEditDialog(row)" />
          </el-tooltip>
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
import {getModelPage, deleteModel} from '@/api/settings/model.ts'
import ImageWithToken from "@/components/information/ImageWithToken.vue";

const list = ref<any[]>([])
const listLoading = ref(true)

const pages = reactive({
  page: 1,
  pageSize: 100,
  total: 0,
  level: null as string | null,
  loggerName: ''
})

interface TypeConfig {
  type: string
  title: string
}

interface TypeMapper {
  [key: string]: TypeConfig
}

const formatType = (type: string) => {
  const mapper: TypeMapper = {
    'OPENAI': {
      type: 'primary',
      title: 'OpenAI'
    },
    'OLLAMA': {
      type: 'warning',
      title: 'Ollama'
    }
  }
  return mapper[type]
}

// 获取列表
const fetchData = async () => {
  listLoading.value = true
  try {
    const res = await getModelPage(pages)
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

const showEditDialog = (row: any) => {

}

// 删除角色
const deleteLogHandle = async (row: any) => {
  const id = row.id
  try {
    await ElMessageBox.confirm(
        `是否确认删除模型为“${row.lo}”的数据项?`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await deleteModel(id)
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