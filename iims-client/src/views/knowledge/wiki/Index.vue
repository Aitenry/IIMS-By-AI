<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <el-button type="primary" plain @click="addWikiBtnClick()"><i style="margin-right: 3px;"
                                                                    class="ri-menu-add-fill"></i>新增</el-button>
    </div>

    <!-- 分页列表 -->
    <el-table :data="tableData" border fit highlight-current-row v-loading="tableLoading"
              style="width: calc(100% - 40px); height: calc(100vh - 210px); margin: auto" empty-text="暂无数据" table-layout="auto">
      <el-table-column align="center" type="index" label="序号" width="60" />
      <el-table-column align="center" prop="title" label="标题" />
      <el-table-column align="center" prop="imgUrl" label="封面">
        <template #default="scope">
          <el-image style="width: 100px; border-radius: 7px;" :src="scope.row.imgUrl"
                    :initial-index="scope.$index"
                    :preview-teleported="true"
                    :show-progress="true"
                    :preview-src-list="previewList" />
        </template>
      </el-table-column>
      <el-table-column align="center" prop="taskStatus" label="知识库状态">
        <template #default="scope">
          <el-tag v-show="scope.row.taskStatus === 0" type="info">未量化</el-tag>
          <el-tag v-show="scope.row.taskStatus === 1" type="primary">部分量化</el-tag>
          <el-tag v-show="scope.row.taskStatus === 2" type="success">已量化</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="type" label="知识库类型">
        <template #default="scope">
          <el-tag v-show="scope.row.type === 0" style="color: white;" color="rgb(255, 100, 100, 0.9)">
            <i style="margin-right: 2px" class="ri-building-line"></i>企业
          </el-tag>
          <el-tag v-show="scope.row.type === 1" style="color: white;" color="rgba(147, 111, 253, 0.9)">
            <i style="margin-right: 2px" class="ri-open-arm-line"></i>个人
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="isPublish" label="是否发布">
        <template #default="scope">
          <el-switch @change="handleIsPublishChange(scope.row)" v-model="scope.row.isPublish" inline-prompt
                     :active-icon="Check" :inactive-icon="Close" />
        </template>
      </el-table-column>
      <el-table-column align="center" prop="isTop" label="是否置顶">
        <template #default="scope">
          <el-switch @change="handleIsTopChange(scope.row)" v-model="scope.row.isTop" inline-prompt :active-icon="Check"
                     :inactive-icon="Close" />
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createTime" label="创建时间" />
      <el-table-column align="center" prop="updateTime" label="更新时间" />
      <el-table-column align="center" fixed="right" label="操作" width="300">
        <template #default="scope">
          <el-tooltip class="box-item" effect="dark" content="编辑知识库" placement="bottom">
            <el-button size="small" plain type="warning" @click="showEditWikiDialog(scope.row)" :icon="Edit">
            </el-button>
          </el-tooltip>

          <el-tooltip class="box-item" effect="dark" content="编辑知识目录" placement="bottom">
            <el-button size="small" plain color="#626aef" @click="showEditWikiCatalogDialog(scope.row)" :icon="Tickets">
            </el-button>
          </el-tooltip>

          <el-tooltip class="box-item" effect="dark" content="预览知识库" placement="bottom">
            <el-button size="small" plain type="primary" @click="goWikiArticleDetailPage(scope.row.id, scope.row.firstArticleId)"
                       :icon="View">
            </el-button>
          </el-tooltip>

          <el-tooltip class="item" effect="dark" content="知识向量化" placement="bottom">
            <el-button plain type="success" size="small" @click="documentEmbeddingSubmit(scope.row)"><i class="ri-sparkling-line"></i></el-button>
          </el-tooltip>

          <el-tooltip class="box-item" effect="dark" content="删除知识库" placement="bottom">
            <el-button plain type="danger" size="small" @click="deleteWikiSubmit(scope.row)" :icon="Delete" >
            </el-button>
          </el-tooltip>

        </template>
      </el-table-column>
    </el-table>

    <el-row class="page">
      <el-col :span="24">
        <el-pagination background :current-page="page" :page-sizes="[10, 20, 30]" :page-size="size"
                       layout="sizes, total, prev, pager, next, jumper" :total="total" :hide-on-single-page="true"
                       @current-change="getTableData" @size-change="handleSizeChange" />
      </el-col>
    </el-row>

    <!-- 新增知识库 -->
    <FormDialog ref="formDialogRef" width="470px" title="新增知识库" @submit="onSubmit">
      <el-form ref="formRef" :rules="rules" :model="form">
        <el-form-item label="标题" prop="title" label-width="80px" size="large">
          <el-input style="width: 320px;" v-model="form.title" placeholder="请输入知识库标题" maxlength="15" show-word-limit clearable >
            <template #prepend>
              <el-select v-model="form.type" placeholder="选择类型" style="width: 130px">
                <el-option label="企业知识库" :value="0" />
                <el-option label="个人知识库" :value="1" />
              </el-select>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="封面" prop="cover" label-width="80px" size="large">
          <el-upload class="avatar-uploader-wiki" action="#" :on-change="handleCoverChange" :auto-upload="false"
                     :show-file-list="false">
            <img v-if="form.cover && imageUrl" :src="imageUrl" class="avatar" alt="avatar"/>
            <el-icon v-else class="avatar-uploader-wiki-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="摘要" prop="summary" label-width="80px" size="large">
          <!-- :rows="3" 指定 textarea 默认显示 3 行 -->
          <el-input style="width: 320px;" v-model="form.summary" :rows="3" maxlength="30" show-word-limit type="textarea"
                    placeholder="请输入知识库摘要" clearable />
        </el-form-item>
      </el-form>
    </FormDialog>

    <!-- 编辑知识库 -->
    <FormDialog ref="editFormDialogRef" width="550px" title="编辑知识库" @submit="onEditWikiSubmit">
      <el-form ref="editFormRef" :rules="rules" :model="editForm">
        <el-form-item label="标题" prop="title" label-width="100px" size="large">
          <el-input style="width: 350px;" v-model="editForm.title" placeholder="请输入知识库标题" maxlength="15" show-word-limit clearable >
            <template #prepend>
              <el-select v-model="editForm.type" placeholder="选择类型" style="width: 130px">
                <el-option label="企业知识库" :value="0" />
                <el-option label="个人知识库" :value="1" />
              </el-select>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="封面" prop="cover" label-width="100px" size="large">
          <el-upload class="avatar-uploader-wiki" action="#" :on-change="handleUpdateCoverChange" :auto-upload="false"
                     :show-file-list="false">
            <img v-if="editForm.cover && imageUrl" :src="imageUrl" class="avatar-wiki" alt="avatar-wiki"/>
            <el-icon v-else class="avatar-uploader-wiki-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="摘要" prop="summary" label-width="100px" size="large">
          <!-- :rows="3" 指定 textarea 默认显示 3 行 -->
          <el-input style="width: 350px;" v-model="editForm.summary" :rows="3" maxlength="90" show-word-limit type="textarea"
                    placeholder="请输入知识库摘要" clearable />
        </el-form-item>
      </el-form>
    </FormDialog>

    <!-- 目录编辑 -->
    <WikiCatalogEditDialog ref="editCatalogFormDialogRef" title="编辑目录" width="600px"/>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Check, Close, Delete, Edit, Tickets, View, Plus } from '@element-plus/icons-vue'
import { getWikiPageList, addWiki, updateWikiIsTop, updateWikiIsPublish, deleteWiki, updateWiki } from '@/api/wiki'
import FormDialog from '@/components/information/FormDialog.vue'
import WikiCatalogEditDialog from '@/components/wiki/WikiCatalogEditDialog.vue'
import { showMessage, showModel } from '@/composables/utils'
import { uploadFile } from '@/api/common.js'
import { documentEmbedding } from '@/api/wiki.js'
import { useRouter } from 'vue-router'

// 定义 WikiItem 类型
interface WikiItem {
  id: string
  title: string
  imgUrl: string
  taskStatus: number
  type: number
  isPublish: boolean
  isTop: boolean
  createTime: string
  updateTime: string
  firstArticleId: string
  cover: string
  summary: string
}

const router = useRouter()

// 模糊搜索的知识库标题
const searchWikiTitle = ref<string>('')
const previewList = ref<string[]>([])

// 查询条件：开始结束时间
const startDate = reactive<Record<string, any>>({})
const endDate = reactive<Record<string, any>>({})

// 表格加载 Loading
const tableLoading = ref<boolean>(false)
// 表格数据
const tableData = ref<WikiItem[]>([])
// 当前页码，给了一个默认值 1
const page = ref<number>(1)
// 总数据量，给了个默认值 0
const total = ref<number>(0)
// 每页显示的数据量，给了个默认值 10
const size = ref<number>(10)

// 获取分页数据
function getTableData() {
  // 显示表格 loading
  tableLoading.value = true
  // 调用后台分页接口，并传入所需参数
  getWikiPageList({
    page: page.value,
    pageSize: size.value,
    startDate: startDate.value,
    endDate: endDate.value,
    title: searchWikiTitle.value
  })
      .then((res: any) => {
        previewList.value = []
        if (res.success == true) {
          tableData.value = res.data.list
          total.value = res.data.total
          tableData.value.forEach(item => {
            previewList.value.push(item.imgUrl)
          })
        }
      })
      .finally(() => tableLoading.value = false) // 隐藏表格 loading
}
getTableData()

// 每页展示数量变更事件
const handleSizeChange = (chooseSize: number) => {
  size.value = chooseSize
  getTableData()
}

// 对话框是否显示
const formDialogRef = ref<any>(null)

// 新增知识库按钮点击事件
const addWikiBtnClick = () => {
  formDialogRef.value.open()
}

// 表单引用
const formRef = ref<any>(null)
// 表单对象
const form = reactive({
  title: '',
  cover: '',
  summary: '',
  type: 1
})

const imageUrl = ref<string | null>(null)

// 表单校验规则
const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 15, message: '标题要求大于1个字符，小于15个字符', trigger: 'blur' },
  ],
  summary: [
    { required: true, message: '请输入摘要', trigger: 'blur' },
    { min: 1, max: 30, message: '摘要要求大于1个字符，小于30个字符', trigger: 'blur' },
  ],
  cover: [{ required: true, message: '请上传封面', trigger: 'blur' }],
}

// 上传封面图片
const handleCoverChange = async (file: any) => {
  // 表单对象
  let formData = new FormData()
  // 添加 file 字段，并将文件传入
  formData.append('file', file.raw)
  formData.append('itemType', '1')
  const res = await uploadFile(formData)
  form.cover = res.data.fileId
  imageUrl.value = res.data.url
  console.log(res)
}

const onSubmit = () => {
  // 先验证 form 表单字段
  formRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false
    }

    // 显示提交按钮 loading
    formDialogRef.value.showBtnLoading()
    addWiki(form).then((res: any) => {
      if (!res.success) {
        // 获取服务端返回的错误消息
        let message = res.message
        // 提示错误消息
        showMessage(message, 'error')
        return
      }

      showMessage('添加成功')
      // 将表单中数据置空
      form.title = ''
      form.cover = ''
      form.summary = ''
      form.type = 1
      // 隐藏对话框
      formDialogRef.value.close()
      // 重新请求分页接口，渲染数据
      getTableData()
    }).finally(() => formDialogRef.value.closeBtnLoading()) // 隐藏提交按钮 loading

  })
}

// 更新置顶
const handleIsTopChange = (row: WikiItem) => {
  updateWikiIsTop({ id: row.id, isTop: row.isTop }).then((res: any) => {
    // 重新请求分页接口，渲染列表数据
    getTableData()

    if (res.success == false) {
      // 获取服务端返回的错误消息
      let message = res.message
      // 提示错误消息
      showMessage(message, 'error')
      return
    }

    showMessage(row.isTop ? '置顶成功' : "已取消置顶")
  })
}

// 更新发布状态
const handleIsPublishChange = (row: WikiItem) => {
  updateWikiIsPublish({ id: row.id, isPublish: row.isPublish }).then((res: any) => {
    // 重新请求分页接口，渲染列表数据
    getTableData()

    if (res.success == false) {
      // 获取服务端返回的错误消息
      let message = res.message
      // 提示错误消息
      showMessage(message, 'error')
      return
    }

    showMessage(row.isPublish ? '发布成功' : "已取消发布")
  })
}

// 跳转文章详情页
const goWikiArticleDetailPage = (wikiId: string, articleId: string) => {
  const url = router.resolve({
    path: `/wiki/${wikiId}`,
    query: { articleId }
  }).href
  window.open(url, '_blank')
}

// 删除知识库
const deleteWikiSubmit = (row: WikiItem) => {
  showModel('是否确定要删除该知识库？').then(() => {
    deleteWiki({id: row.id}).then((res: any) => {
      if (res.success == false) {
        // 获取服务端返回的错误消息
        let message = res.message
        // 提示错误消息
        showMessage(message, 'error')
        return
      }

      showMessage('删除成功')
      // 重新请求分页接口，渲染数据
      getTableData()
    })
  })
}

const documentEmbeddingSubmit = (row: WikiItem) => {
  showModel('是否确定要对该知识库进行向量化？').then(() => {
    documentEmbedding(row.id).then((res: any) => {
      if (res.success == false) {
        // 获取服务端返回的错误消息
        let message = res.message
        // 提示错误消息
        showMessage(message, 'error')
        return
      }

      showMessage('正在向量化！')
      // 重新请求分页接口，渲染数据
      getTableData()
    })
  })
}

// 更新知识库对话框引用
const editFormDialogRef = ref<any>(null)
// 弹出知识库编辑对话框
const showEditWikiDialog = (row: WikiItem) => {
  editFormDialogRef.value.open()
  editForm.id = row.id
  editForm.title = row.title
  editForm.cover = row.cover
  editForm.summary = row.summary || ''
  editForm.type = row.type
  imageUrl.value = row.imgUrl
}

// 表单引用
const editFormRef = ref<any>(null)
// 表单对象
const editForm = reactive({
  id: null as string | null,
  title: '',
  cover: null as string | null,
  summary: '',
  type: 1
})

// 知识库编辑：上传封面图片
const handleUpdateCoverChange = async (file: any) => {
  // 表单对象
  let formData = new FormData()
  // 添加 file 字段，并将文件传入
  formData.append('file', file.raw)
  formData.append('itemType', '1')
  const res = await uploadFile(formData)
  console.log(res)
}

// 编辑知识库提交事件
const onEditWikiSubmit = () => {
  // 先验证 form 表单字段
  editFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false
    }

    // 显示提交按钮 loading
    editFormDialogRef.value.showBtnLoading()
    updateWiki(editForm).then((res: any) => {
      if (!res.success) {
        // 获取服务端返回的错误消息
        let message = res.message
        // 提示错误消息
        showMessage(message, 'error')
        return
      }

      showMessage('更新成功')
      // 将表单中数据置空
      editForm.id = null
      editForm.title = ''
      editForm.cover = ''
      editForm.summary = ''
      // 隐藏对话框
      editFormDialogRef.value.close()
      // 重新请求分页接口，渲染数据
      getTableData()
    }).finally(() => editFormDialogRef.value.closeBtnLoading()) // 隐藏提交按钮 loading

  })
}

// 编辑目录对话框是否显示
const editCatalogFormDialogRef = ref<any>(null)
// 编辑目录按钮点击事件
const showEditWikiCatalogDialog = (row: WikiItem) => {
  // 显示编辑目录对话框, 并传入知识库 ID
  editCatalogFormDialogRef.value.open(row.id)
}
</script>

<style>
.avatar-uploader-wiki .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader-wiki .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-wiki-icon {
  font-size: 28px;
  color: #8c939d;
  width: 349px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar-wiki {
  width: 349px;
  height: 188px;
  display: block;
}
</style>