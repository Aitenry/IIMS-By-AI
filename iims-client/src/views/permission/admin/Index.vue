<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <authority permission="permission:admin:add">
        <el-button type="primary" plain @click="showEditDialog()"><i style="margin-right: 3px;"
                                                                     class="ri-menu-add-fill"></i>新增</el-button>
      </authority>
      <authority permission="permission:admin:export">
        <el-button type="success" plain @click="excelUserData()"><i style="margin-right: 3px;"
                                                                    class="ri-export-line"></i>导出</el-button>
      </authority>
      <authority permission="permission:admin:query">
        <el-popover placement="bottom-start" width="760" trigger="click">
          <el-form ref="queryForm" class="cus-search-box" :model="pages" :inline="true"
                   style="height: 32px; text-align: end;">
            <el-form-item label="真实姓名" prop="name">
              <el-input v-model="pages.name" placeholder="请输入真实姓名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input v-model="pages.username" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item class="search-btn wp-50">
              <el-button type="primary" @click="handleQuery">搜索</el-button>
              <el-button type="warning" plain @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <template #reference>
            <el-button type="primary"><el-icon>
              <Search />
            </el-icon></el-button>
          </template>
        </el-popover>
      </authority>
    </div>
    <el-table v-loading="listLoading" :data="list" element-loading-text="数据加载中..." border fit highlight-current-row
              empty-text="暂无数据" style="width: calc(100% - 40px); margin: auto;">
      <el-table-column align="center" label="序号" width="50">
        <template #default="scope">
          {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="头像" align="center" width="65">
        <template #default="scope">
          <el-image style="width: 40px;height: 40px;border-radius: 10px;margin-top: 7px;" :src="scope.row.imageUrl" />
        </template>
      </el-table-column>
      <el-table-column label="用户名" align="center" prop="username" />
      <el-table-column label="真实姓名" align="center" prop="name" />
      <el-table-column label="邮箱" align="center" prop="email" />
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column class-name="status-col" label="角色" align="center" width="130">
        <template #default="scope">
          <el-tag v-for="(item, i) in scope.row.roles" :key="i" type="info" class="mb10">{{ item }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="启用状态" align="center" width="100">
        <template #default="scope">
          <el-switch v-model="scope.row.isDisable" :active-value="false" :inactive-value="true"
                     @change="setStatus(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createTime" label="创建时间" width="120">
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-tooltip class="item" effect="dark" content="编辑" placement="bottom">
            <el-button plain type="primary" size="small" icon="edit" @click="showEditDialog(scope.row)" />
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="重置密码" placement="bottom">
            <el-button plain type="warning" size="small" icon="key" @click="resetPasswordHandle(scope.row)" />
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="删除" placement="bottom">
            <el-button plain type="danger" size="small" icon="delete" @click="deleteAdminHandle(scope.row)" />
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

    <!-- 添加修改弹出框 -->
    <el-dialog v-model="addOrUpdateVisible" :title="!form.id ? '添加' : '修改'" width="670px" append-to-body
               :close-on-click-modal="false" align-center draggable>
      <el-form ref="formRef" class="info-form" :inline="true" :model="form" :rules="rules">
        <el-descriptions direction="vertical" border style="margin-top: 20px;">
          <el-descriptions-item :rowspan="2" label="头像" align="center">
            <template #label>
              <i class="ri-image-add-line"></i>
              头像
            </template>
            <el-form-item prop="avatar">
              <el-upload class="avatar-uploader" :action="baseUrl + '/common/upload'" :headers="{ token }"
                         :data="{ itemType: 1 }" :show-file-list="false" :on-success="handleAvatarSuccess"
                         :before-upload="beforeAvatarUpload">
                <img v-if="form.avatar" :src="imageUrl" class="avatar" alt="avatar">
                <el-icon v-else class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
              </el-upload>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i class="ri-user-heart-line"></i>
              真实姓名
            </template>
            <el-form-item prop="name">
              <el-input v-model="form.name" placeholder="请输入真实姓名" :disabled="!!form.id" style="width: 190px;" />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i class="ri-id-card-line"></i>
              身份证号码
            </template>
            <el-form-item prop="idNumber">
              <el-input v-model="form.idNumber" placeholder="请输入身份证号码" clearable style="width: 190px;" />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i class="ri-user-3-line"></i>
              用户名
            </template>
            <el-form-item prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" clearable style="width: 190px;" />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i :class="`ri-${form.sex === '1' ? 'men' : 'women'}-line`"></i>
              性别
            </template>
            <el-form-item prop="sex">
              <el-radio-group v-model="form.sex">
                <el-radio value="1">男</el-radio>
                <el-radio value="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i class="ri-mail-line"></i>
              邮箱
            </template>
            <el-form-item prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" style="width: 190px;" clearable />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i class="ri-smartphone-line"></i>
              手机号
            </template>
            <el-form-item prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" style="width: 190px;" clearable />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i class="ri-shield-keyhole-line"></i>
              角色
            </template>
            <el-form-item prop="role">
              <el-select v-model="form.role" placeholder="请选择角色" clearable filterable multiple style="width: 190px;"
                         collapse-tags collapse-tags-tooltip>
                <el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.id" />
              </el-select>
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i class="ri-building-4-line"></i>
              部门
            </template>
            <el-form-item prop="organization">
              <el-tree-select v-model="form.organization" :data="organizations" :render-after-expand="false"
                              style="width: 190px;" />
            </el-form-item>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <i class="ri-file-paper-line"></i>
              简介
            </template>
            <el-form-item style="width: 100%;" prop="introduction">
              <el-input v-model="form.introduction" maxlength="30" placeholder="请输入您的介绍！" show-word-limit
                        type="textarea" />
            </el-form-item>
          </el-descriptions-item>
        </el-descriptions>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button plain @click="addOrUpdateVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm()">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminList, startOrStop, updateAdmin, resetPassword, addAdmin, getAdmin, excelUser, deleteAdmin } from '@/api/admin'
import { getRoleList } from '@/api/role'
import { getNowFormatDate, download } from '@/utils/tool'
import { getStorage } from '@/utils/auth'
import type { UploadProps } from 'element-plus'
import {Plus} from "@element-plus/icons-vue";
import Authority from "@/layout/system/Authority.vue";

// 数据模型
const baseUrl = import.meta.env.VITE_APP_API_URL
const list = ref<any[]>([])
const listLoading = ref(true)
const token = ref('')
const addOrUpdateVisible = ref(false)
const imageUrl = ref('')
const queryForm = ref()
const formRef = ref()

// 部门树数据
const organizations = ref([
  {
    value: '1',
    label: 'Level one 1',
    children: [
      {
        value: '1-1',
        label: 'Level two 1-1',
        children: [
          {
            value: '1-1-1',
            label: 'Level three 1-1-1',
          },
        ],
      },
    ],
  },
  {
    value: '2',
    label: 'Level one 2',
    children: [
      {
        value: '2-1',
        label: 'Level two 2-1',
        children: [
          {
            value: '2-1-1',
            label: 'Level three 2-1-1',
          },
        ],
      },
      {
        value: '2-2',
        label: 'Level two 2-2',
        children: [
          {
            value: '2-2-1',
            label: 'Level three 2-2-1',
          },
        ],
      },
    ],
  },
  {
    value: '3',
    label: 'Level one 3',
    children: [
      {
        value: '3-1',
        label: 'Level two 3-1',
        children: [
          {
            value: '3-1-1',
            label: 'Level three 3-1-1',
          },
        ],
      },
      {
        value: '3-2',
        label: 'Level two 3-2',
        children: [
          {
            value: '3-2-1',
            label: 'Level three 3-2-1',
          },
        ],
      },
    ],
  },
])

const pages = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
  status: null as any,
  name: '',
  username: ''
})

const form = reactive({
  id: null,
  name: '',
  username: '',
  email: '',
  password: '',
  phone: '',
  sex: '1',
  idNumber: '',
  avatar: '',
  organization: [] as string[],
  introduction: '',
  role: [] as string[]
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: '不能为空', trigger: 'blur' }],
  username: [{ required: true, message: '不能为空', trigger: 'blur' }],
  email: [{ required: true, message: '不能为空', trigger: 'blur' }],
  phone: [{ required: true, message: '不能为空', trigger: 'blur' }],
  sex: [{ required: true, message: '不能为空', trigger: 'blur' }],
  idNumber: [{ required: true, message: '不能为空', trigger: 'blur' }],
  avatar: [{ required: true, message: '不能为空', trigger: 'blur' }],
  role: [{ required: true, message: '不能为空', trigger: 'blur' }]
}

const roleList = ref<any[]>([])

// 获取管理员列表
const fetchData = async () => {
  listLoading.value = true
  try {
    const res = await getAdminList(pages)
    list.value = res.data.list
    pages.total = res.data.total
  } catch (error) {
    console.log(error)
  } finally {
    listLoading.value = false
  }
}

// 获取角色列表
const roleData = async () => {
  try {
    const res = await getRoleList()
    roleList.value = res.data
  } catch (error) {
    console.log(error)
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

// 重置搜索
const resetQuery = () => {
  pages.name = ''
  pages.username = ''
  pages.page = 1
  fetchData()
}

// 搜索
const handleQuery = () => {
  pages.page = 1
  fetchData()
  roleData()
}

// 显示编辑对话框
const showEditDialog = async (row?: any) => {
  if (row) {
    try {
      const res = await getAdmin(row.id)
      if (res.code === 1) {
        const role = row?.role && JSON.parse(row.role)
        Object.assign(form, { ...res.data, role })
        imageUrl.value = row.imageUrl
        addOrUpdateVisible.value = true
      }
    } catch (error) {
      console.log(error)
    }
  } else {
    await nextTick(() => {
      Object.assign(form, {
        id: null,
        name: '',
        username: '',
        email: '',
        password: '',
        phone: '',
        sex: '1',
        idNumber: '',
        avatar: '',
        organization: [],
        introduction: '',
        role: []
      })
      addOrUpdateVisible.value = true
    })
  }
}

// 重置密码
const resetPasswordHandle = async (row: any) => {
  try {
    await ElMessageBox.confirm(`是否确认重置“${row.username}”用户密码?`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await resetPassword(row.id)
    if (res.code === 1) {
      await fetchData()
      ElMessage.success({
        message: '重置成功'
      })
    }
  } catch (error) {
    console.log(error)
  }
}

// 提交表单
const submitForm = () => {
  if (!formRef.value) return

  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const formatForm = JSON.parse(JSON.stringify(form))
        formatForm.role = formatForm?.role && JSON.stringify(formatForm.role)

        if (form.id) {
          const res = await updateAdmin(formatForm)
          if (res.errCode === 0) {
            ElMessage.success({
              message: '修改成功'
            })
            await fetchData()
            addOrUpdateVisible.value = false
          }
        } else {
          const res = await addAdmin(formatForm)
          if (res.errCode === 0) {
            ElMessage.success({
              message: '新增用户成功'
            })
            await fetchData()
            addOrUpdateVisible.value = false
          }
        }
      } catch (error) {
        console.log(error)
      }
    }
  })
}

// 设置用户状态
const setStatus = async (row: any) => {
  listLoading.value = true
  try {
    const res = await startOrStop(row)
    if (res.errCode === 0) {
      ElMessage.success({
        message: `${!row.isDisable ? '已启用' : '已禁用'}“${row.username}”用户！`,
        type: !row.isDisable ? 'success' : 'warning'
      })
      await fetchData()
    }
  } catch (error) {
    console.log(error)
  } finally {
    listLoading.value = false
  }
}

// 头像上传成功回调
const handleAvatarSuccess: UploadProps['onSuccess'] = (response, _file) => {
  form.avatar = response.data.fileId
  imageUrl.value = response.data.url
}

// 头像上传前校验
const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => {
  const isLt3M = file.size / 1024 / 1024 < 3
  if (!isLt3M) {
    ElMessage.error({
      message: '上传头像图片大小不能超过 3MB!'
    })
  }
  return isLt3M
}

// 导出数据
const excelUserData = async () => {
  try {
    await ElMessageBox.confirm('是否确认导出所有用户数据项?', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const result = await excelUser()
    download(result, '用户表' + getNowFormatDate() + '.xlsx')
  } catch (error) {
    console.log(error)
  }
}

// 删除管理员
const deleteAdminHandle = async (row: any) => {
  const id = row.id
  try {
    await ElMessageBox.confirm(`是否确认删除管理员为“${row.username}”的数据项?`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteAdmin(id)
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
  roleData()
  token.value = getStorage('token') || ''
})
</script>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 188px;
  height: 188px;
  display: block;
}
</style>