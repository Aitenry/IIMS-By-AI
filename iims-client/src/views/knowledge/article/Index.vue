<template>
  <div class="app-container customer-page-box">
    <div class="cus-btn-con">
      <el-button
        plain
        type="primary"
        @click="showEditDialog()"
      ><i style="margin-right: 3px;" class="ri-menu-add-fill"></i>新增</el-button>
      <el-popover
        placement="bottom-start"
        width="930"
        :visible="visible"
      >

        <el-form
          ref="queryForm"
          :model="pages"
          :inline="true"
          style="height: 32px; text-align: end;"
        >
          <el-form-item
            label="文章标题"
            prop="title"
          >
            <el-input
              v-model="pages.title"
              placeholder="请输入文章标题"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item>
            <el-date-picker
              v-model="pages.date"
              type="datetimerange"
              start-placeholder="起始日期"
              end-placeholder="截止日期"
            />
          </el-form-item>
          <el-form-item class="search-btn wp-75">
            <el-button
              type="primary" plain
              @click="handleQuery"
            >搜索</el-button>
            <el-button
              type="warning" plain
              @click="resetQuery"
            >重置</el-button>
          </el-form-item>
        </el-form>
        <template #reference>
          <el-button @click="visible = !visible" type="primary"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-popover>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="数据加载中..."
      border
      fit
      highlight-current-row
      empty-text="暂无数据"
      style="width: calc(100% - 40px); height: calc(100vh - 210px); margin: auto;"
    >
      <el-table-column
        align="center"
        label="序号"
        width="95"
      >
        <template #default="scope">
          {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="标题"
      >
        <template #default="scope">
          {{ scope.row.title }}
        </template>
      </el-table-column>
      <el-table-column
        label="封面"
        align="center"
      >
        <template #default="scope">
          <el-image
            fit="contain"
            style="width: 100px; border-radius: 10px; margin-top: 6px;"
            :initial-index="scope.$index"
            :src="scope.row.imageUrl"
            :preview-teleported="true"
            :show-progress="true"
            :preview-src-list="previewList"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="分类"
        align="center"
      >
        <template #default="scope">
          <el-tag
            type="info"
            size="small"
          >
            {{ scope.row.categoryName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        class-name="status-col"
        label="标签"
        align="center"
      >
        <template #default="scope">
          <el-tag
            v-for="(item, i) in scope.row.tagsName"
            :key="i"
            type="success"
            size="small"
            style="margin: 5px;"
          >{{ item }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        class-name="status-col"
        label="是否置顶"
        align="center"
      >
        <template #default="scope">
          <el-switch
            v-model="scope.row.isTop"
            :active-value="true"
            :inactive-value="false"
            @change="articleIsTop(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="创建者"
        align="center"
        prop="userInfo"
      >
        <template #default="scope">
          <el-button type='primary' text @click="openUserInfo(scope.row.userInfo.id)">
            {{ scope.row.userInfo?.username || '-' }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="createTime"
        label="创建时间"
        width="200"
      >
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        prop="updateTime"
        label="更新时间"
        width="200"
      >
        <template #default="scope">
          <i class="el-icon-time" />
          <span> {{ scope.row.updateTime }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        width="200"
      >
        <template #default="{ row }">

          <el-tooltip class="box-item" effect="dark" content="预览文章" placement="bottom">
            <el-button size="small" plain type="primary" @click="goArticleDetailPage(row.id)"
                       :icon="View">
            </el-button>
          </el-tooltip>

          <el-tooltip
            class="item"
            effect="dark"
            content="修改文章"
            placement="bottom"
          >
            <el-button
              plain
              type="primary"
              size="small"
              icon="edit"
              @click="showEditDialog(row)"
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
              @click="deleteArticleHandle(row)"
            />
          </el-tooltip>

        </template>
      </el-table-column>
    </el-table>

    <el-row class="page">
      <el-col :span="24">
        <el-pagination
          background
          :current-page="pages.page"
          :page-sizes="[10, 20, 30]"
          :page-size="pages.pageSize"
          style="justify-content: center; margin-top: 20px;"
          layout="sizes, total, prev, pager, next, jumper"
          :total="pages.total"
          :hide-on-single-page="true"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </el-col>
    </el-row>

    <el-dialog
      v-model="addOrUpdateVisible"
      :title="!form.id ? '添加' : '修改'"
      append-to-body
      :close-on-click-modal="false"
      width="1260px"
      @close="closeHandle"
      class="article-dialog"
      align-center
      draggable
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        :inline="true"
        label-width="auto"
        class="article-form"
      >
        <el-tabs
          v-model="activeName"
          style="width: 100%;"
        >
          <el-tab-pane
            label="文章内容"
            name="content"
          >
            <el-form-item
              v-if="editorVisible"
              label="内容"
              prop="content"
            >
              <VditorEdit style="height: 500px" :content="form.content" ref="editorRef" />
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane
            label="文章信息"
            name="info"
          >
            <el-row style="height: 66vh;" :gutter="20">
              <el-col
                :span="11"
              >
                <el-form-item
                  label="标题"
                  prop="title"
                >
                  <el-input
                    v-model="form.title"
                    placeholder="请输入标题"
                  />
                </el-form-item>
                <el-form-item
                  label="摘要"
                  style="position: relative;"
                  prop="summary"
                >
                <el-tooltip v-if="form.id" effect="dark" content="AI生成摘要" placement="right">
                  <el-button
                    plain circle
                    type="primary"
                    size="small"
                    :loading="loadingModel"
                    @click="generateSummary(form.id)"
                    style="position: absolute; right: -12px; top: -12px; z-index: 1;"
                  ><template #icon><i v-if="!loadingModel" class="ri-brush-ai-line"></i></template></el-button>
                </el-tooltip>
                  
                  <el-input
                    v-model="form.summary"
                    placeholder="请输入摘要"
                    class="summary"
                    type="textarea"
                    resize="none"
                    :autosize="{ minRows: 17, maxRows: 17 }"
                    input-style="text-align: justify;"
                  />
                </el-form-item>
              </el-col>
              <el-col
                :span="11"
                style="margin-left: auto; margin-right: 20px;"
              >
                <el-form-item
                  label="封面"
                  prop="cover"
                >
                  <el-upload
                    class="cover-uploader"
                    :action="baseUrl + '/common/upload'"
                    :show-file-list="false"
                    :headers="config"
                    :data="{ itemType: 1 }"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                  >
                    <el-image
                      v-if="form.cover"
                      :src="imageUrl"
                      class="cover"
                      fit="cover"
                    />
                    <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
                <el-form-item
                  label="分类"
                  prop="categoryId"
                >
                  <el-select
                    v-model="form.categoryId"
                    placeholder="请选择分类"
                    clearable
                    filterable
                  >
                    <el-option
                      v-for="cate in cateList"
                      :key="cate.id"
                      :label="cate.value"
                      :value="cate.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item
                  label="标签"
                  prop="tagIds"
                >
                  <el-select
                    v-model="form.tagIds"
                    placeholder="请选择标签"
                    clearable
                    filterable
                    multiple
                  >
                    <el-option
                      v-for="cate in tagsList"
                      :key="cate.id"
                      :label="cate.value"
                      :value="cate.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item
                  label="是否置顶"
                  prop="isTop"
                >
                  <el-switch
                    v-model="form.isTop"
                    :active-value="true"
                    :inactive-value="false"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <el-button
          plain
          @click="addOrUpdateVisible = false"
        >取 消</el-button>
        <el-button
          type="primary"
          @click="submitForm()"
        >确 定</el-button>
      </template>
    </el-dialog>
    <el-dialog
      title="责任人信息"
      v-model="dialogUserInfo"
      align-center
      width="600px"
    >
      <InfoCard :id="createBy" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticlesList, addArticles, updateArticleIsTop, updateArticles, deleteArticles, getArticleById } from '@/api/articles'
import { getCategoryAll, getTagsAll } from '@/api/dictionary'
import { getGenerateSummary } from '@/api/articles'
import InfoCard from '@/layout/user/InfoCard.vue'
import { getStorage } from '@/utils/auth'
import VditorEdit from '@/components/v-md-editor/Index.vue'
import type { FormInstance, FormRules } from 'element-plus'
import {Plus, View} from "@element-plus/icons-vue";
import {useRouter} from "vue-router";

// 类型定义
interface PageParams {
  page: number
  pageSize: number
  total: number
  title: string
  date: string[]
}

interface ArticleForm {
  id: string | null
  title: string
  summary: string
  categoryId: string
  tagIds: string[] | string
  content: string | null
  isTop: boolean
  cover: string
  imageUrl?: string
}

interface ArticleItem {
  id: string
  title: string
  imageUrl: string
  categoryName: string
  tagsName: string[]
  isTop: boolean
  userInfo: {
    id: number
    username: string
  }
  createTime: string
  updateTime: string
}

interface CategoryItem {
  id: number
  value: string
}

// Refs
const activeName = ref('content')
const router = useRouter()
const editorVisible = ref(false)
const addOrUpdateVisible = ref(false)
const loadingModel = ref(false)
const baseUrl = import.meta.env.VITE_APP_API_URL
const imageUrl = ref('')
const token = ref('')
const pages = reactive<PageParams>({
  page: 1,
  pageSize: 10,
  total: 0,
  title: '',
  date: []
})
const form = reactive<ArticleForm>({
  id: null,
  title: '',
  summary: '',
  categoryId: '',
  tagIds: [],
  content: '',
  isTop: false,
  cover: ''
})
const list = ref<ArticleItem[]>([])
const createBy = ref<string | null>(null)
const dialogUserInfo = ref(false)
const listLoading = ref(true)
const visible = ref(false)
const cateList = ref<CategoryItem[]>([])
const tagsList = ref<CategoryItem[]>([])
const previewList = ref<string[]>([])

// 表单引用
const queryFormRef = ref<FormInstance>()
const formRef = ref<FormInstance>()
const editorRef = ref<InstanceType<typeof VditorEdit> | null>(null)

// 表单校验规则
const rules = reactive<FormRules>({
  title: [{ required: true, message: '不能为空', trigger: 'blur' }],
  summary: [{ required: true, message: '不能为空', trigger: 'blur' }],
  categoryId: [{ required: true, message: '不能为空', trigger: 'blur' }],
  tagIds: [{ required: true, message: '不能为空', trigger: 'blur' }],
  cover: [{ required: true, message: '不能为空', trigger: 'blur' }]
})

// 计算属性
const config = computed(() => {
  return { token: token.value }
})

// 方法
const openUserInfo = (userId: string) => {
  if (userId) {
    createBy.value = userId
    dialogUserInfo.value = !dialogUserInfo.value
  }
}

const generateSummary = async (id: string | null) => {
  if (!id) return
  loadingModel.value = true
  try {
    const res = await getGenerateSummary(id)
    if (res.code === 1) {
      form.summary = res.data
    }
  } catch (error) {
    console.error(error)
  } finally {
    loadingModel.value = false
  }
}

const getCateList = async () => {
  try {
    const res = await getCategoryAll()
    cateList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const getTagsList = async () => {
  try {
    const res = await getTagsAll()
    tagsList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const fetchData = async () => {
  listLoading.value = true
  previewList.value = []
  try {
    const res = await getArticlesList(pages)
    list.value = res.data.list
    pages.total = res.data.total
    list.value.forEach(item => {
      previewList.value.push(item.imageUrl)
    })
  } catch (error) {
    console.error(error)
  } finally {
    listLoading.value = false
  }
}

const handleCurrentChange = (val: number) => {
  pages.page = val
  fetchData()
}

const handleSizeChange = (val: number) => {
  pages.pageSize = val
  fetchData()
}

const resetQuery = () => {
  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  pages.page = 1
  pages.pageSize = 10
  pages.total = 0
  pages.title = ''
  pages.date = []
  handleQuery()
}

const handleQuery = () => {
  pages.page = 1
  fetchData()
}

const showEditDialog = async (row?: ArticleItem) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }

  // 重置表单
  form.id = null
  form.title = ''
  form.summary = ''
  form.categoryId = ''
  form.tagIds = []
  form.cover = ''
  form.content = ''
  form.isTop = false
  imageUrl.value = ''

  if (row) {
    try {
      const res = await getArticleById(row.id)
      Object.assign(form, res.data)
      imageUrl.value = res.data.imageUrl || ''
      addOrUpdateVisible.value = true
    } catch (error) {
      console.error(error)
    }
  } else {
    form.content = ''
    addOrUpdateVisible.value = true
  }
  editorVisible.value = true
}

const closeHandle = () => {
  editorVisible.value = false
  form.id = null
  form.content = ''
}

const submitForm = async () => {
  if (!formRef.value || !editorRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      // 获取编辑器内容
      if (editorRef.value) {
        form.content = editorRef.value.getEditValue()
      }

      // 深拷贝表单数据
      const deepForm = JSON.parse(JSON.stringify(form))
      if (Array.isArray(deepForm.tagIds)) {
        deepForm.tagIds = JSON.stringify(deepForm.tagIds)
      }

      try {
        let res
        if (form.id) {
          res = await updateArticles(deepForm)
          if (res.errCode === 0) {
            ElMessage.success('修改成功')
            await fetchData()
            addOrUpdateVisible.value = false
          }
        } else {
          res = await addArticles(deepForm)
          if (res.errCode === 0) {
            ElMessage.success('新增成功')
            await fetchData()
            addOrUpdateVisible.value = false
          }
        }
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const articleIsTop = async (row: ArticleItem) => {
  listLoading.value = true
  try {
    const res = await updateArticleIsTop(row)
    if (res.errCode === 0) {
      await fetchData()
    }
  } catch (error) {
    console.error(error)
  } finally {
    listLoading.value = false
  }
}

const deleteArticleHandle = async (row: ArticleItem) => {
  try {
    await ElMessageBox.confirm(
        `是否确认删除文章为"${row.title}"的数据项?`,
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const res = await deleteArticles([row.id])
    if (res.code === 1) {
      await fetchData()
      ElMessage.success('删除成功')
    }
  } catch (error) {
    console.error(error)
  }
}

const handleAvatarSuccess = (res: any, file: any) => {
  form.cover = res.data.fileId
  imageUrl.value = res.data.url
}

const beforeAvatarUpload = (file: File) => {
  const isLt3M = file.size / 1024 / 1024 < 3
  if (!isLt3M) {
    ElMessage.error('上传头像图片大小不能超过 3MB!')
  }
  return isLt3M
}

// 跳转文章详情页
const goArticleDetailPage = (articleId: string) => {
  const url = router.resolve({
    path: `/article/${articleId}`
  }).href
  window.open(url, '_blank')
}

// 生命周期
onMounted(() => {
  token.value = getStorage('token') || ''
  getCateList()
  getTagsList()
  fetchData()
})
</script>

<style scoped>
.cover {
  width: 360px;
  height: 100%;
}

.el-image-viewer__img {
  height: 50%;
}

.cover-uploader .el-upload {
  width: 360px;
  height: 191px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.cover-uploader .el-upload:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 30px;
  color: #8c939d;
  width: 360px;
  height: 191px;
  line-height: 127px;
  text-align: center;
}

.article-dialog .el-dialog__body {
  padding: 20px !important;
}

.article-form .el-form-item__content {
  display: block !important;
}

.article-form .el-form-item {
  margin: 0 20px 20px;
}

.summary .el-textarea__inner {
  padding: 5px 7px !important;
}

.summary .el-textarea__inner::-webkit-scrollbar {
  width: 0;
}
</style>
