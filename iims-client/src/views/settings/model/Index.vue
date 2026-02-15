<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <el-button type="primary" @click="showDialog()" plain><i style="margin-right: 3px;" class="ri-menu-add-fill"></i>新增</el-button>
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
              <el-table-column label="邮箱" align="center" width="200" prop="email" />
              <el-table-column label="简介" align="center" prop="introduction" />
            </el-table>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="序号" width="65">
        <template #default="scope">
          {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="显示名称" width="200" align="center" prop="rename" />
      <el-table-column label="模型名称" width="200" align="center" prop="name" />
      <el-table-column label="接口协议" width="120" align="center" prop="type">
        <template #default="scope">
          <el-tag :type="formatType(scope.row.type).type">{{ formatType(scope.row.type).title }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模型类型" width="120" align="center" prop="modelType">
        <template #default="scope">
          <el-tag :type="formatModelType(scope.row.modelType).type">{{ formatModelType(scope.row.modelType).title }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="上下文大小" width="120" align="center" prop="token" />
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
                       @click="deleteModelHandle(row)" />
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

    <!-- 编辑对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        :close-on-click-modal="false"
        @closed="handleDialogClosed"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
          style="padding-right: 20px;"
      >

        <el-form-item label="显示名称" prop="rename">
          <el-input
              v-model="formData.rename"
              placeholder="请输入显示名称"
              maxlength="100"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="模型类型" prop="modelType">
          <el-select
              v-model="formData.modelType"
              placeholder="请选择模型类型"
              style="width: 100%"
          >
            <el-option
                v-for="item in modelTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="接口协议" prop="type">
          <el-select
              v-model="formData.type"
              placeholder="请选择接口协议"
              style="width: 100%"
              @change="handleTypeChange"
          >
            <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="模型名称" prop="name">
          <el-input
              v-model="formData.name"
              placeholder="请输入模型名称"
              maxlength="100"
              show-word-limit
          />
        </el-form-item>

        <el-form-item v-if="formData.type !== 'OLLAMA'" label="URL" prop="url">
          <el-input
              v-model="formData.url"
              placeholder="请输入API URL"
          />
        </el-form-item>

        <el-form-item v-if="formData.type !== 'OLLAMA'" label="API Key" prop="key">
          <el-input
              v-model="formData.key"
              type="password"
              placeholder="请输入API Key"
              show-password
          />
        </el-form-item>

        <el-form-item label="上下文大小" prop="token">
          <el-input
              v-model="formData.token"
              placeholder="请输入Token"
          />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              placeholder="请输入描述信息"
              :rows="3"
              maxlength="500"
              show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getModelPage, deleteModel, createModel, updateModel } from '@/api/settings/model.ts'
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

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const submitLoading = ref(false)

// 编辑模式标识
const isEditMode = ref(false)

// 模型类型选项
const modelTypeOptions = [
  { value: 'EMBEDDING', label: '嵌入模型' },
  { value: 'LANGUAGE', label: '语言模型' },
  { value: 'VISION', label: '视觉模型' },
  { value: 'MULTIMODAL', label: '多模态模型' }
]

// 表单数据
interface FormData {
  id?: number | null;
  name: string;
  rename: string;
  type: string;
  modelType: string;
  url?: string;
  key?: string;
  token: string;
  description: string;
}

const formData = reactive<FormData>({
  id: null,
  name: '',
  rename: '',
  type: '',
  modelType: 'LANGUAGE',
  url: '',
  key: '',
  token: '',
  description: ''
})

// 表单验证规则 - 动态验证，编辑模式下不需要验证API Key
const formRules = computed(() => ({
  name: [
    { required: true, message: '请输入模型名称', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  rename: [
    { required: true, message: '请输入显示名称', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择接口协议', trigger: 'change' }
  ],
  modelType: [
    { required: true, message: '请选择模型类型', trigger: 'change' }
  ],
  url: [
    ...(formData.type !== 'OLLAMA' && !isEditMode.value ? [{ required: true, message: '请输入API URL', trigger: 'blur' }] : [])
  ],
  key: [
    ...(formData.type !== 'OLLAMA' && !isEditMode.value ? [{ required: true, message: '请输入API Key', trigger: 'blur' }] : [])
  ],
  token: [
    { required: true, message: '请输入Token', trigger: 'blur' }
  ]
}))

// 类型选项
const typeOptions = [
  { value: 'OPENAI', label: 'OpenAI' },
  { value: 'OLLAMA', label: 'Ollama' },
  { value: 'DEEPSEEK', label: 'Deepseek' }
]

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
      type: 'info',
      title: 'OpenAI'
    },
    'OLLAMA': {
      type: 'warning',
      title: 'Ollama'
    },
    'DEEPSEEK': {
      type: 'primary',
      title: 'Deepseek'
    }
  }
  return mapper[type] || { type: 'info', title: type }
}

// 格式化模型类型
const formatModelType = (modelType: string) => {
  const mapper: TypeMapper = {
    'EMBEDDING': {
      type: 'success',
      title: '嵌入模型'
    },
    'LANGUAGE': {
      type: 'primary',
      title: '语言模型'
    },
    'VISION': {
      type: 'warning',
      title: '视觉模型'
    },
    'MULTIMODAL': {
      type: 'danger',
      title: '多模态模型'
    }
  }
  return mapper[modelType] || { type: 'info', title: modelType }
}

// 显示新增对话框
const showDialog = () => {
  isEditMode.value = false
  resetFormData()
  dialogTitle.value = '新增模型'
  dialogVisible.value = true
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
    ElMessage.error('获取数据失败')
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

// 显示编辑对话框
const showEditDialog = (row: any) => {
  if (row.id) {
    // 编辑模式
    isEditMode.value = true
    dialogTitle.value = '编辑模型'
    Object.assign(formData, {
      id: row.id,
      name: row.name,
      rename: row.rename || '',
      type: row.type,
      modelType: row.modelType || 'LANGUAGE',
      url: row.url || '',
      key: row.key || '', // 保留现有的key值，不强制要求重新输入
      token: row.token,
      description: row.description
    })
  } else {
    // 新增模式
    isEditMode.value = false
    resetFormData()
    dialogTitle.value = '新增模型'
  }
  dialogVisible.value = true
}

// 重置表单数据
const resetFormData = () => {
  Object.assign(formData, {
    id: null,
    name: '',
    rename: '',
    type: 'OLLAMA',
    modelType: 'LANGUAGE',
    url: '',
    key: '',
    token: '',
    description: ''
  })
}

// 处理对话框关闭
const handleDialogClosed = () => {
  isEditMode.value = false
  resetFormData()
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 处理类型变化
const handleTypeChange = (value: string) => {
  // 当类型改变时，重新验证表单
  if (formRef.value) {
    formRef.value.validateField(['url', 'key'])
  }
  console.log('Selected type:', value)
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitLoading.value = true

    // 根据类型决定是否包含url和key字段
    const submitData: any = {
      id: formData.id,
      name: formData.name,
      rename: formData.rename,
      type: formData.type,
      modelType: formData.modelType,
      token: formData.token,
      description: formData.description
    }

    if (formData.type !== 'OLLAMA') {
      submitData.url = formData.url
      // 在编辑模式下，只有当用户填写了新的key时才更新，否则不发送key字段
      if (!isEditMode.value || formData.key?.trim()) {
        submitData.key = formData.key
      }
    }

    let res
    if (formData.id) {
      // 更新现有模型
      res = await updateModel(submitData)
    } else {
      // 创建新模型
      res = await createModel(submitData)
    }

    if (res.code === 1) {
      ElMessage.success(formData.id ? '更新成功' : '创建成功')
      dialogVisible.value = false
      await fetchData() // 重新获取数据
    } else {
      ElMessage.error(res.message || (formData.id ? '更新失败' : '创建失败'))
    }
  } catch (error) {
    console.error('Submit error:', error)
    if (error instanceof Error) {
      ElMessage.error(`操作失败: ${error.message}`)
    }
  } finally {
    submitLoading.value = false
  }
}

// 删除模型
const deleteModelHandle = async (row: any) => {
  const id = row.id
  try {
    await ElMessageBox.confirm(
        `是否确认删除模型为"${row.name}"的数据项?`,
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
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    console.log(error)
    if (error !== 'cancel') { // 用户取消操作不提示错误
      ElMessage.error('删除操作失败')
    }
  }
}

// 组件挂载后执行
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dialog-footer button:first-child {
  margin-right: 10px;
}
</style>