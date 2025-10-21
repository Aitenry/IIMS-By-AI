<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <el-button v-if="isOpenDictValue" @click="isOpenDictValue = false" style="float: left;" type="primary" plain><i class="ri-arrow-left-line"></i></el-button>
      <el-button v-if="!isOpenDictValue" type="primary" @click="showDialog()" plain><i style="margin-right: 3px;" class="ri-menu-add-fill"></i>新增</el-button>
      <el-button v-else type="primary" @click="showDialogValue()" plain><i style="margin-right: 3px;" class="ri-menu-add-fill"></i>新增</el-button>
      <el-popover placement="bottom-start" width="510" trigger="click">
        <el-form ref="queryForm" :inline="true" style="height: 32px; text-align: end;" :model="pages">
          <el-form-item label="字典名称" prop="roleName">
            <el-input style="width: 210px;" v-model="pages.name" placeholder="请输入字典名称" clearable @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item class="search-btn wp-50">
            <el-button type="primary" plain @click="handleQuery">搜索</el-button>
            <el-button type="warning" plain @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <template #reference>
          <el-button type="primary" style="margin-left: 12px;"><el-icon>
            <Search />
          </el-icon></el-button>
        </template>
      </el-popover>
    </div>
    <el-table v-if="!isOpenDictValue" v-loading="listLoading" :data="list" element-loading-text="数据加载中..." border fit highlight-current-row
              empty-text="暂无数据" style="width: calc(100% - 40px); margin: auto">
      <el-table-column align="center" label="序号" width="65">
        <template #default="scope">
          {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="字典名称" align="center" prop="name" >
        <template #default="scope">
          <el-button plain type="primary" @click="openDictValue(scope.row.id)" :text="true">{{ scope.row.name }}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="字典描述" align="center" prop="remark" />
      <el-table-column align="center" prop="isCanChange" label="字典状态">
        <template #default="scope">
          <el-tag :type="scope.row.isCanChange ? 'success' : 'danger'">{{ scope.row.isCanChange ? '外置' : '内置' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="是否禁用" align="center">
        <template #default="scope">
          <el-switch v-model="scope.row.isDisable" :disabled="!scope.row.isCanChange" :active-value="true" :inactive-value="false" inline-prompt
                     @change="setIsDisable(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createTime" label="创建时间">
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="updateTime" label="更新时间">
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="{ row }">
          <el-tooltip class="item" effect="dark" content="编辑" placement="bottom">
            <el-button plain type="primary" size="small" icon="edit" @click="showDialog(row)" />
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="删除" placement="bottom">
            <el-button plain type="danger" :disabled="!row.isCanChange" size="small" icon="delete"
                       @click="deleteDictHandle(row)" />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
    <el-table v-else v-loading="listLoading" :data="listValue" element-loading-text="数据加载中..." border fit highlight-current-row
              empty-text="暂无数据" style="width: calc(100% - 40px); margin: auto">
      <el-table-column align="center" label="序号" width="65">
        <template #default="scope">
          {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="字典值" align="center" prop="value" />
      <el-table-column label="值描述" align="center" prop="remark" />
      <el-table-column align="center" prop="createTime" label="创建时间">
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="updateTime" label="更新时间">
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="{ row }">
          <el-tooltip class="item" effect="dark" content="编辑" placement="bottom">
            <el-button plain type="primary" size="small" icon="edit" @click="showDialogValue(row)" />
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="删除" placement="bottom">
            <el-button plain type="danger" size="small" icon="delete" @click="deleteDictValueHandle(row)" />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <el-row class="page">
      <el-col :span="24">
        <el-pagination background :current-page="pages.page" :page-sizes="[10, 20, 30]" :page-size="pages.pageSize"
                       layout="sizes, total, prev, pager, next, jumper" :total="pages.total" :hide-on-single-page="true"
                       @current-change="handleCurrentChange" @size-change="handleSizeChange" />
      </el-col>
    </el-row>

    <!-- 添加或修改字典对话框 -->
    <el-dialog v-model="addOrUpdateVisible" :title="!form.id ? '添加' : '修改'" width="320px" append-to-body
               :close-on-click-modal="false" @close="setCloseProps(false)" align-center draggable>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
        <el-form-item label="字典名称" prop="name">
          <el-input style="width: 230px;" v-model="form.name" placeholder="请输入字典英文名称" clearable />
        </el-form-item>
        <el-form-item label="字典描述" prop="remark">
          <el-input v-model="form.remark" maxlength="30" show-word-limit type="textarea" style="width: 230px;" placeholder="请输入字典描述" />
        </el-form-item>
        <el-form-item label="是否内置" prop="isCanChange">
          <el-switch v-model="form.isCanChange" :active-value="false" :inactive-value="true" inline-prompt />
        </el-form-item>
      </el-form>

      <template class="dialog-footer" #footer>
        <el-button plain @click="addOrUpdateVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm()">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 添加或修改字典值对话框 -->
    <el-dialog v-model="addOrUpdateValueVisible" :title="!formValue.id ? '添加' : '修改'" width="320px" append-to-body
               :close-on-click-modal="false" @close="setCloseProps(true)" align-center draggable>

      <el-form ref="formValueRef" :model="formValue" :rules="rules" label-width="auto">
        <el-form-item label="字典值" prop="value">
          <el-input style="width: 230px;" v-model="formValue.value" placeholder="请输入字典值" clearable />
        </el-form-item>
        <el-form-item label="值描述" prop="remark">
          <el-input v-model="formValue.remark" maxlength="30" show-word-limit type="textarea" style="width: 230px;" placeholder="请输入字典描述" />
        </el-form-item>
      </el-form>

      <template class="dialog-footer" #footer>
        <el-button plain @click="addOrUpdateValueVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitFormValue()">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addDict, updateDict, getDictList, deleteDict, disableDict,
  getDictValueList, addDictValue, updateDictValue, deleteDictValue
} from '@/api/dictionary'

// 数据模型
const list = ref<any[]>([])
const listValue = ref<any[]>([])
const dictId = ref<string | null>(null)
const listLoading = ref(true)
const addOrUpdateVisible = ref(false)
const addOrUpdateValueVisible = ref(false)
const isOpenDictValue = ref(false)
const formValueRef = ref()
const queryForm = ref()
const formRef = ref()

const pages = reactive({
  page: 1,
  name: null as string | null,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null as string | null,
  name: '',
  remark: '',
  isCanChange: true
})

const formValue = reactive({
  id: null as string | null,
  dictId: null as string | null,
  value: '',
  remark: ''
})

const rules = {}

// 获取字典列表
const fetchData = async () => {
  listLoading.value = true
  try {
    const res = await getDictList(pages)
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

// 搜索
const handleQuery = () => {
  pages.page = 1
  fetchData()
}

// 重置搜索
const resetQuery = () => {
  pages.name = null
  pages.page = 1
  fetchData()
}

// 设置字典禁用状态
const setIsDisable = async (row: any) => {
  listLoading.value = true
  try {
    const res = await disableDict(row.id, row.isDisable)
    if (res.errCode === 0) await fetchData()
  } catch (error) {
    console.log(error)
  } finally {
    listLoading.value = false
  }
}

// 显示字典对话框
const showDialog = (row?: any) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }

  if (row) {
    const deepForm = JSON.parse(JSON.stringify(row))
    Object.assign(form, deepForm)
    addOrUpdateVisible.value = true
  } else {
    Object.assign(form, {
      id: null,
      name: '',
      remark: '',
      isCanChange: true
    })
    addOrUpdateVisible.value = true
  }
}

// 设置关闭属性
const setCloseProps = (isDictValue: boolean) => {
  if (isDictValue) {
    Object.assign(formValue, {
      id: null,
      dictId: null,
      value: '',
      remark: ''
    })
  } else {
    Object.assign(form, {
      id: null,
      name: '',
      remark: '',
      isCanChange: true
    })
  }
}

// 提交字典表单
const submitForm = () => {
  if (!formRef.value) return

  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      const formatForm = JSON.parse(JSON.stringify(form))
      try {
        if (form.id) {
          const res = await updateDict(formatForm)
          if (res.errCode === 0) {
            ElMessage.success({
              message: '修改成功'
            })
            await fetchData()
            addOrUpdateVisible.value = false
          }
        } else {
          const res = await addDict(formatForm)
          if (res.errCode === 0) {
            ElMessage.success({
              message: '新增成功'
            })
          }
          await fetchData()
          addOrUpdateVisible.value = false
        }
      } catch (error) {
        console.log(error)
      }
    }
  })
}

// 提交字典值表单
const submitFormValue = () => {
  if (!formValueRef.value) return

  formValueRef.value.validate(async (valid: boolean) => {
    if (valid) {
      formValue.dictId = dictId.value
      const formatForm = JSON.parse(JSON.stringify(formValue))
      try {
        if (formValue.id) {
          const res = await updateDictValue(formatForm)
          if (res.errCode === 0) {
            ElMessage.success({
              message: '修改成功'
            })
            addOrUpdateValueVisible.value = false
          }
        } else {
          const res = await addDictValue(formatForm)
          if (res.errCode === 0) {
            ElMessage.success({
              message: '新增成功'
            })
          }
          addOrUpdateValueVisible.value = false
        }
      } catch (error) {
        console.log(error)
      } finally {
        if (dictId.value) {
          await openDictValue(dictId.value)
        }
      }
    }
  })
}

// 删除字典
const deleteDictHandle = async (row: any) => {
  const id = row.id
  try {
    await ElMessageBox.confirm(
        `是否确认删除字典为“${row.name}”的数据项，该字典已被使用会影响业务?`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await deleteDict([id])
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

// 显示字典值对话框
const showDialogValue = (row?: any) => {
  if (formValueRef.value) {
    formValueRef.value.resetFields()
  }

  if (row) {
    const deepForm = JSON.parse(JSON.stringify(row))
    Object.assign(formValue, deepForm)
    addOrUpdateValueVisible.value = true
  } else {
    Object.assign(formValue, {
      id: null,
      dictId: null,
      value: '',
      remark: ''
    })
    addOrUpdateValueVisible.value = true
  }
}

// 删除字典值
const deleteDictValueHandle = async (row: any) => {
  const id = row.id
  try {
    await ElMessageBox.confirm(
        `是否确认删除字典值为“${row.name}”的数据项，该字典值已被使用会影响业务?`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await deleteDictValue([id])
    if (res.code === 1) {
      ElMessage.success({
        message: '删除成功'
      })
    }
  } catch (error) {
    console.log(error)
  } finally {
    if (dictId.value) {
      await openDictValue(dictId.value)
    }
  }
}

// 打开字典值页面
const openDictValue = async (dictIdParam: string) => {
  isOpenDictValue.value = true
  dictId.value = dictIdParam
  pages.page = 1
  pages.pageSize = 10
  pages.total = 0

  listLoading.value = true
  try {
    const res = await getDictValueList(dictIdParam, pages)
    if (res.errCode === 0) {
      listValue.value = res.data.list
      pages.total = res.data.total
    }
  } catch (error) {
    console.log(error)
  } finally {
    listLoading.value = false
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