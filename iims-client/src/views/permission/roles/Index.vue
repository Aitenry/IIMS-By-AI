<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <el-button type="primary" plain @click="showDialog()"><i style="margin-right: 3px;"
                                                               class="ri-menu-add-fill"></i>新增</el-button>
      <el-popover placement="bottom-start" width="760" trigger="click">
        <el-form ref="queryForm" :inline="true" style="height: 32px; text-align: end;" :model="pages">
          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="pages.roleName" placeholder="请输入角色名称" clearable @keyup.enter.native="handleQuery" />
          </el-form-item>
          <el-form-item label="权限字符" prop="roleEn">
            <el-input v-model="pages.roleEn" placeholder="请输入权限字符" clearable @keyup.enter.native="handleQuery" />
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
    <el-table v-loading="listLoading" :data="list" element-loading-text="数据加载中..." border fit highlight-current-row
              empty-text="暂无数据" style="width: calc(100% - 40px); margin: auto">
      <el-table-column align="center" label="序号" width="65">
        <template #default="scope">
          {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="角色名称" align="center" prop="roleName" />
      <el-table-column label="权限字符" align="center" prop="roleEn" />
      <el-table-column label="描述" align="center" prop="info" />
      <el-table-column class-name="status-col" label="系统角色" align="center">
        <template #default="scope">
          <el-switch v-model="scope.row.systemic" :active-value="1" :inactive-value="0" active-text="内置"
                     inactive-text="外置" inline-prompt @change="setRoleStatus(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createTime" label="创建时间">
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
            <el-button :disabled="row.systemic == 1" plain type="danger" size="small" icon="delete"
                       @click="deleteRoleHandle(row)" />
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

    <!-- 添加或修改角色对话框 -->
    <el-dialog v-model="addOrUpdateVisible" :title="!form.id ? '添加' : '修改'" width="750px" append-to-body
               :close-on-click-modal="false" @close="setCloseProps" align-center draggable>
      <el-form ref="formRef" :model="form" :rules="rules" :inline="true" label-width="auto">
        <el-form-item label="角色名称" prop="roleName">
          <el-input style="width: 230px;" v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleEn">
          <el-input v-model="form.roleEn" style="width: 230px;" placeholder="请输入角色英文名称" clearable />
        </el-form-item>
        <el-form-item label="角色描述" prop="info">
          <el-input v-model="form.info" placeholder="请输入描述" style="width: 230px; height: 220px;" resize="none"
                    :autosize="{ minRows: 9, maxRows: 9 }" type="textarea" clearable />
        </el-form-item>
        <el-form-item v-if="showMenuTreeItem" label="菜单权限集合">
          <el-tree ref="menusRef" style="width: 230px; height: 220px; overflow: auto;" :data="menus" check-strictly
                   show-checkbox node-key="id" highlight-current :props="defaultProps" :default-checked-keys="form.menus" expand-on-click-node />
        </el-form-item>
        <el-form-item label="系统角色" prop="systemic">
          <el-switch v-model="form.systemic" :active-value="1" :inactive-value="0" inline-prompt active-text="内置"
                     inactive-text="外置" />
        </el-form-item>
      </el-form>
      <template class="dialog-footer" #footer>
        <el-button plain @click="addOrUpdateVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm()">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRolePage, updateRole, addRole, deleteRole } from '@/api/role'
import { getMenuTree } from '@/api/menu'
import type { FormInstance, TreeInstance } from 'element-plus'

// 数据模型
const showMenuTreeItem = ref(false)
const defaultProps = {
  children: 'children',
  label: 'title'
}
const list = ref<any[]>([])
const listLoading = ref(true)
const addOrUpdateVisible = ref(false)
const queryForm = ref()
const formRef = ref<FormInstance>()
const menusRef = ref<TreeInstance>()

const pages = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
  roleName: '',
  roleEn: ''
})

const form = reactive({
  id: null,
  roleName: '',
  roleEn: '',
  menus: [],
  info: '',
  systemic: 0
})

const rules = {
  roleName: [{ required: true, message: '不能为空', trigger: 'blur' }],
  roleEn: [{ required: true, message: '不能为空', trigger: 'blur' }],
  menus: [{ required: true, message: '不能为空', trigger: 'blur' }],
  systemic: [{ required: true, message: '不能为空', trigger: 'blur' }]
}

const menus = ref<any[]>([])

// 获取菜单树
const getMenus = async () => {
  try {
    const res = await getMenuTree()
    menus.value = res.data
  } catch (error) {
    console.log(error)
  }
}

// 获取角色列表
const fetchData = async () => {
  listLoading.value = true
  try {
    const res = await getRolePage(pages)
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
  getMenus()
}

// 重置搜索
const resetQuery = () => {
  // 清空搜索条件
  pages.roleName = ''
  pages.roleEn = ''
  pages.page = 1
  fetchData()
  getMenus()
}

// 设置角色状态
const setRoleStatus = async (row: any) => {
  listLoading.value = true
  try {
    const res = await updateRole(row)
    if (res.errCode === 0) await fetchData()
  } catch (error) {
    console.log(error)
  } finally {
    listLoading.value = false
  }
}

// 显示对话框
const showDialog = (row?: any) => {
  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields()
  }

  if (row) {
    showMenuTreeItem.value = true
    const deepForm = JSON.parse(JSON.stringify(row))
    deepForm.menus = deepForm?.menus && JSON.parse(row.menus)
    Object.assign(form, deepForm)
    addOrUpdateVisible.value = true
  } else {
    Object.assign(form, {
      id: null,
      roleName: '',
      roleEn: '',
      menus: null,
      info: '',
      systemic: 0
    })
    showMenuTreeItem.value = true
    addOrUpdateVisible.value = true
  }
}

// 关闭对话框时的处理
const setCloseProps = () => {
  Object.assign(form, {
    id: null,
    roleName: '',
    roleEn: '',
    menus: null,
    info: '',
    systemic: 0
  })
  showMenuTreeItem.value = false
}

// 提交表单
const submitForm = () => {
  if (!formRef.value) return

  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      const formatForm = JSON.parse(JSON.stringify(form))
      const checkedKeys = menusRef.value?.getCheckedKeys() || []
      formatForm.menus = checkedKeys.length > 0 ? JSON.stringify(checkedKeys) : null

      try {
        if (form.id) {
          const res = await updateRole(formatForm)
          if (res.errCode === 0) {
            ElMessage.success({
              message: '修改成功'
            })
            await fetchData()
            addOrUpdateVisible.value = false
          }
        } else {
          const res = await addRole(formatForm)
          if (res.errCode === 0) {
            ElMessage.success({
              message: '新增角色成功'
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

// 删除角色
const deleteRoleHandle = async (row: any) => {
  const id = row.id
  try {
    await ElMessageBox.confirm(
        `是否确认删除角色为“${row.roleName}”的数据项?`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await deleteRole(id)
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
  getMenus()
})
</script>

<style scoped>
/* 保持原有的样式 */
</style>