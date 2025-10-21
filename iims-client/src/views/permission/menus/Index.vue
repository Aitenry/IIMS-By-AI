<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <el-button
          type="primary"
          plain
          @click="handleAdd"
      >
        <i style="margin-right: 3px;" class="ri-menu-add-fill"></i>新增
      </el-button>
    </div>

    <el-table
        v-loading="loading"
        max-height="calc(100vh - 200px)"
        :data="menuList"
        stripe
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        empty-text="暂无数据"
        style="width: calc(100% - 40px); margin: auto"
    >
      <el-table-column
          prop="title"
          label="菜单名称"
          :show-overflow-tooltip="true"
          min-width="160"
      />
      <el-table-column
          prop="icon"
          label="图标"
          align="center"
          min-width="100"
      >
        <template #default="scope">
          <svg-icon :icon-class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column
          prop="orderNum"
          label="排序"
          min-width="100"
          align="center"
      />
      <el-table-column
          prop="perms"
          label="权限标识"
          :show-overflow-tooltip="true"
          min-width="200"
      />
      <el-table-column
          prop="component"
          label="组件路径"
          :show-overflow-tooltip="true"
          min-width="200"
      />
      <el-table-column
          prop="path"
          label="路由地址"
          :show-overflow-tooltip="true"
          min-width="200"
      />
      <el-table-column
          prop="menuType"
          label="菜单类型"
          min-width="100"
          align="center"
      >
        <template #default="scope">
          <el-tag
              :type="menuTypeTag(scope.row.menuType)"
              class="mb10"
          >
            {{ formatMenuType(scope.row.menuType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
          prop="status"
          label="菜单状态"
          min-width="100"
          align="center"
      >
        <template #default="scope">
          <el-tag :type="statusTag(scope.row.status)">
            {{ formatStatus(scope.row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
          fixed="right"
          label="操作"
          align="center"
          class-name="small-padding fixed-width"
          min-width="150"
      >
        <template #default="scope">
          <el-tooltip
              class="item"
              effect="dark"
              content="添加"
              placement="bottom"
          >
            <el-button
                plain
                type="success"
                size="small"
                icon="plus"
                @click="handleAdd(scope.row)"
            />
          </el-tooltip>
          <el-tooltip
              class="item"
              effect="dark"
              content="编辑"
              placement="bottom"
          >
            <el-button
                plain
                type="warning"
                size="small"
                icon="edit"
                @click="handleUpdate(scope.row)"
            />
          </el-tooltip>
          <el-tooltip
              class="item"
              effect="dark"
              content="删除"
              placement="bottom"
          >
            <el-button
                plain
                type="danger"
                size="small"
                icon="delete"
                @click="handleDelete(scope.row)"
            />
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog
        :title="title"
        v-model="open"
        width="670px"
        append-to-body
        :close-on-click-modal="false"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
      >
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                  v-model="form.parentId"
                  :data="menuOptions"
                  :render-after-expand="false"
                  :check-strictly="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="M">目录</el-radio>
                <el-radio value="C">视图</el-radio>
                <el-radio value="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="24">
            <el-form-item label="菜单图标">
              <el-input
                  v-model="form.icon"
                  placeholder="点击选择图标"
                  readonly
              >
                <template #prefix></template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="菜单名称" prop="title">
              <el-input
                  v-model="form.title"
                  placeholder="请输入菜单名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="11" :offset="2">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number
                  v-model="form.orderNum"
                  controls-position="right"
                  :min="0"
              />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="11">
            <el-form-item label="开启新页">
              <el-radio-group v-model="form.isFrame">
                <el-radio :value="0">是</el-radio>
                <el-radio :value="1">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="11" :offset="2" v-if="form.menuType !== 'F'">
            <el-form-item label="路由地址" prop="path">
              <el-input
                  v-model="form.path"
                  placeholder="请输入路由地址"
              />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType === 'C'" :span="11">
            <el-form-item label="组件路径" prop="component">
              <el-input
                  v-model="form.component"
                  placeholder="请输入组件路径"
              />
            </el-form-item>
          </el-col>
          <el-col :span="11" :offset="form.menuType === 'F' ? 0 : 2" v-if="form.menuType !== 'M'">
            <el-form-item label="权限标识">
              <el-input
                  v-model="form.perms"
                  placeholder="请权限标识"
                  maxlength="50"
              />
            </el-form-item>
          </el-col>
          <el-col :span="11" v-if="form.menuType !== 'F'">
            <el-form-item label="显示状态" prop="visible">
              <el-radio-group v-model="form.visible">
                <el-radio :value="0">显示</el-radio>
                <el-radio :value="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button plain @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addMenu, deleteMenu, getMenuList, updateMenu } from '@/api/menu'
import type { FormInstance } from 'element-plus'
import {handleTree} from "@/utils/common.ts";
interface MenuData {
  id: string | null
  parentId: string
  title: string
  icon?: string
  menuType: 'M' | 'C' | 'F'
  orderNum: number
  isFrame: 0 | 1
  visible: 0 | 1
  perms?: string
  component?: string
  path?: string
  status: 0 | 1
  children?: MenuData[]
  hasChildren?: boolean
}

interface MenuForm extends MenuData {
  [key: string]: any
}
// 数据模型
const loading = ref(true)
const menuList = ref<MenuData[]>([])
const menuOptions = ref<any[]>([])
const open = ref(false)
const title = ref('')
const formRef = ref<FormInstance>()

const form = reactive<MenuForm>({
  id: null,
  parentId: '0',
  title: '',
  visible: 0,
  orderNum: 0,
  status: 0,
  menuType: "M",
  isFrame: 0
})

const rules = {
  title: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
  orderNum: [{ required: true, message: '菜单顺序不能为空', trigger: 'blur' }],
  path: [{ required: true, message: '路由地址不能为空', trigger: 'blur' }]
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getMenuList()
    menuList.value = handleTree(res.data, 'id')
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

const normalizer = (node: any) => {
  if (!node.children || node.children === 'null') {
    delete node.children
  }
  if (node.menuType === 'F') {
    return {}
  }
  return {
    value: node.id,
    label: node.title ?? node.label,
    children: node.children ?? []
  }
}

const normalizeTree = (tree: any): any => {
  const normalizedNode = normalizer(tree)

  if (Object.keys(normalizedNode).length === 0) return {}

  if (Array.isArray(normalizedNode.children)) {
    normalizedNode.children = normalizedNode.children
        .map((child: any) => normalizeTree(child))
        .filter((child: any) => Object.keys(child).length > 0)
  }

  return normalizedNode
}

const getTreeSelect = async () => {
  try {
    menuOptions.value = []
    const res = await getMenuList()
    const menu: any = { id: '0', label: '主类目', children: [] }
    menu.children = handleTree(res.data, 'id')
    menuOptions.value.push(normalizeTree(menu))
  } catch (err) {
    console.error(err)
  }
}

const reset = () => {
  Object.assign(form, {
    id: undefined,
    parentId: '0',
    title: undefined,
    icon: undefined,
    menuType: 'M',
    orderNum: undefined,
    isFrame: 1,
    visible: 0
  })
}

const cancel = () => {
  open.value = false
  reset()
}

const handleAdd = async (row?: MenuData) => {
  await nextTick()
  reset()
  await getTreeSelect()
  if (row && row.id) {
    form.parentId = row.id
  } else {
    form.parentId = '0'
  }
  title.value = '添加菜单'
  open.value = true
}

const handleUpdate = async (row: MenuData) => {
  reset()
  await getTreeSelect()
  Object.assign(form, { ...row })
  open.value = true
  title.value = '修改菜单'
}

const submitForm = () => {
  if (!formRef.value) return
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.id) {
        updateMenu(form).then(() => {
          ElMessage.success({ message: '修改成功', duration: 1500 })
          open.value = false
          getList()
        })
      } else {
        addMenu(form).then(() => {
          ElMessage.success({ message: '新增成功', duration: 1500 })
          open.value = false
          getList()
        })
      }
    }
  })
}

const handleDelete = (row: MenuData) => {
  ElMessageBox.confirm(`是否确认删除名称为${row.title}的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (!row.id) return
    return deleteMenu(row.id)
  }).then(() => {
    getList()
  })
}

// 工具方法
const formatStatus = (row: MenuData) => {
  const status: Record<number, string> = { 0: '停用', 1: '正常' }
  return status[row.status]
}

const statusTag = (status: number) => {
  const map: Record<number, string> = { 1: 'success', 0: 'danger' }
  return map[status]
}

const formatMenuType = (type: string) => {
  const map: Record<string, string> = { M: '目录', C: '视图', F: '按钮' }
  return map[type]
}

const menuTypeTag = (type: string) => {
  const map: Record<string, string> = { M: 'success', C: 'primary', F: 'info' }
  return map[type]
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.reset-form-item {
  margin-right: 0;
}

:deep(.el-table) {
  thead tr th:first-child .cell {
    padding-left: 25px;
  }

  tbody tr td:first-child .cell {
    padding-left: 25px;
  }
}
</style>