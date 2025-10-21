<template>
  <el-dialog align-center v-model="dialogVisible" :title="title" :width="width" :destroy-on-close="destroyOnClose" :draggable="true"
             :close-on-click-modal="true" :close-on-press-escape="true">

    <!-- 添加一级目录按钮 -->
    <div style="text-align: end;" class="mb-5">
      <el-button type="primary" plain @click="addCatalogDialogRef.open">
        <i style="margin-right: 3px;" class="ri-menu-add-fill"></i>添加目录
      </el-button>
    </div>

    <!-- 内容 -->
    <div id="accordion-flush" data-accordion="collapse"
         data-active-classes="bg-white dark:bg-gray-900 text-gray-900 dark:text-white"
         data-inactive-classes="text-gray-500 dark:text-gray-400">
      <div id="accordion-flush-heading-1" v-for="(catalog, index) in catalogs" :key="index">
        <!-- 一级目录 -->
        <button type="button" class="hover:bg-gray-100 py-3 px-3 rounded-lg flex items-center w-full font-medium rtl:text-right
                    text-gray-500 dark:border-gray-700 dark:text-gray-400"
                data-accordion-target="#accordion-flush-body-1" aria-expanded="true"
                aria-controls="accordion-flush-body-1">
          <svg data-accordion-icon class="w-3 h-3 mr-2 rotate-180 shrink-0" aria-hidden="true"
               xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 10 6">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M9 5 5 1 1 5" />
          </svg>
          <!-- 一级目录标题 -->
          <span v-if="!catalog.editing" class="flex items-center grow" v-html="catalog.title"></span>
          <!-- 标题输入框 -->
          <span v-else class="w-full">
                        <el-input v-model="catalog.title" @blur="onEditTitleInputBlur(catalog.id)" placeholder="请输入目录标题"
                                  clearable />
                    </span>
          <!-- 右侧竖着的三个点：操作图标 -->
          <span class="hover:bg-gray-200 rounded py-2 px-2">
            <el-dropdown @command="handleCommand">
                            <span class="el-dropdown-link">
                              <i class="ri-more-line icon w-5 h-5 outline-none"></i>
                            </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="{ id: catalog.id, sort: catalog.sort, action: 'rename' }">
                    <el-icon>
                      <i class="ri-edit-line"></i>
                    </el-icon>
                    重命名
                  </el-dropdown-item>
                  <el-dropdown-item :command="{ id: catalog.id, sort: catalog.sort, action: 'addArticle' }">
                    <el-icon>
                      <i class="ri-add-line"></i>
                    </el-icon>
                    添加文章
                  </el-dropdown-item>
                  <el-dropdown-item :command="{ id: catalog.id, sort: catalog.sort, action: 'moveUp' }"
                                    divided v-if="(index + 1) > 1">
                    <el-icon>
                      <i class="ri-arrow-up-line"></i>
                    </el-icon>
                    上移
                  </el-dropdown-item>
                  <el-dropdown-item :command="{ id: catalog.id, sort: catalog.sort, action: 'moveDown' }"
                                    v-if="(index + 1) < catalogs.length">
                    <el-icon>
                      <i class="ri-arrow-down-line"></i>
                    </el-icon>
                    下移
                  </el-dropdown-item>
                  <el-dropdown-item
                      :command="{ id: catalog.id, sort: catalog.sort, action: 'removeFromCatalog' }"
                      divided>
                    <el-icon>
                      <i class="ri-file-reduce-line"></i>
                    </el-icon>
                    移出目录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </span>
        </button>

        <!-- 二级目录 -->
        <ul v-if="catalog.children && catalog.children.length > 0">
          <VueDraggable ref="el" v-model="catalog.children" @end="onDragEnd">
            <li v-for="(childCatalog, index2) in catalog.children" :key="index2"
                class="flex items-center ps-10 py-2 pe-3 rounded-lg hover:bg-gray-100">
              <!-- 二级标题 -->
              <span class="w-full">
                                <span v-if="!childCatalog.editing" v-html="childCatalog.title"
                                      class="flex items-center"></span>
                                <span v-else>
                                    <el-input v-model="childCatalog.title" :autofocus="true"
                                              @blur="onEditTitleInputBlur(childCatalog.id)" placeholder="请输入目录标题" clearable />
                                </span>
                            </span>
              <span class="grow"></span>
              <!-- 重命名 -->
              <span class="hover:bg-gray-200 rounded py-2 px-2 ml-2 mr-2" @click="editTitle(childCatalog.id)">
                              <i class="ri-pencil-line icon w-4 h-4"></i>
                            </span>
              <!-- 移出目录 -->
              <el-tooltip class="box-item" effect="dark" content="移出目录" placement="right">
                                <span class="hover:bg-gray-200 rounded py-2 px-2"
                                      @click="removeArticleFromCatalog(childCatalog.id)">
                                  <i class="ri-delete-bin-line icon w-4 h-4"></i>
                                </span>
              </el-tooltip>
            </li>
          </VueDraggable>
        </ul>
      </div>
    </div>
  </el-dialog>

  <!-- 添加目录 -->
  <FormDialog ref="addCatalogDialogRef" title="添加目录" @submit="onAddCatalogSubmit">
    <el-form ref="addCatalogFormRef" :rules="rules" :model="addCatalogForm">
      <el-form-item label="标题" prop="title" label-width="80px" size="large">
        <el-input v-model="addCatalogForm.title" placeholder="请输入目录标题" clearable />
      </el-form-item>
    </el-form>
  </FormDialog>

  <!-- 添加文章到目录 -->
  <FormDialog ref="addArticle2CatalogDialogRef" title="添加文章" width="80%" confirmText="添加" @submit="onAddArticleCatalogSubmit">
    <div>
      <!-- 表头分页查询条件， shadow="never" 指定 card 卡片组件没有阴影 -->
      <el-card shadow="never" class="mb-5">
        <!-- flex 布局，内容垂直居中 -->
        <div class="flex items-center">
          <el-text>文章标题</el-text>
          <div class="ml-3 w-52 mr-5"><el-input v-model="searchArticleTitle" placeholder="请输入（模糊查询）" clearable/></div>

          <el-text>创建日期</el-text>
          <div class="ml-3 w-90 mr-5">
            <!-- 日期选择组件（区间选择） -->
            <el-date-picker v-model="pickDate" type="daterange" range-separator="至" start-placeholder="开始时间"
                            end-placeholder="结束时间" size="default" :shortcuts="shortcuts" @change="datepickerChange" />
          </div>

          <el-button type="primary" class="ml-3" :icon="Search" @click="getTableData">查询</el-button>
          <el-button class="ml-3" :icon="RefreshRight" @click="reset">重置</el-button>
        </div>
      </el-card>

      <el-card shadow="never">
        <!-- 分页列表 -->
        <el-table max-height="50vh" :data="tableData" border stripe v-loading="tableLoading" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="45" />
          <el-table-column type="index" align="center" label="序号" width="60" />
          <el-table-column prop="title" label="标题" width="380" />
          <el-table-column prop="cover" label="封面" width="180">
            <template #default="scope">
              <el-image style="width: 100px;" :src="scope.row.imageUrl" />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" />
        </el-table>

        <!-- 分页 -->
        <div class="mt-10 flex justify-center">
          <el-pagination background :current-page="page" :page-sizes="[10, 20, 30]" :page-size="pageSize"
                         layout="sizes, total, prev, pager, next, jumper" :total="total" :hide-on-single-page="true"
                         @current-change="getTableData" @size-change="handleSizeChange" />
        </div>
      </el-card>
    </div>
  </FormDialog>
</template>

<script setup lang="ts">
import {reactive, ref} from 'vue'
import {showMessage, showModel} from '@/composables/utils.ts'
import {VueDraggable} from 'vue-draggable-plus'
import FormDialog from '@/components/wiki/FormDialog.vue'
import {getArticlesList} from '@/api/articles.js'
import {RefreshRight, Search} from '@element-plus/icons-vue'
import {getWikiCatalogs, updateWikiCatalogs} from '@/api/wiki.js'

// 定义目录项类型
interface CatalogItem {
  id: number;
  title: string;
  editing: boolean;
  level: number;
  isEmbedding: boolean;
  sort: number;
  children?: CatalogItem[];
  docId?: number;
  type?: number;
}

// 定义文章类型
interface ArticleItem {
  id: number;
  title: string;
  imageUrl: string;
  createTime: string;
}

// 定义下拉菜单命令类型
interface CommandItem {
  id: number;
  sort: number;
  action: string;
}

// 目录数据
const catalogs = ref<CatalogItem[]>([])

const oldTitle = ref<string>(null)

// 一级目录: 操作按钮下拉菜单
const handleCommand = (command: CommandItem) => {
  if (command.action === 'rename') { // 重命名
    editTitle(command.id)
  } else if (command.action === 'moveUp') { // 上移
    catalogMove(command.id, command.sort, 'up')
  } else if (command.action === 'moveDown') { // 下移
    catalogMove(command.id, command.sort, 'down')
  } else if (command.action === 'removeFromCatalog') { // 移除出目录
    removeCatalog(command.id)
  } else if (command.action === 'addArticle') {
    // 记录当前被编辑的目录 ID
    currCatalogId.value = command.id
    getTableData()
    addArticle2CatalogDialogRef.value.open()
  }

}

// 编辑标题
const editTitle = (catalogId: number) => {
  // 根据目录 ID 查找对应的目录
  let targetCatalog = findCatalogById(catalogs.value, catalogId)
  // 将编辑状态置为 true
  if (targetCatalog) {
    targetCatalog.editing = true
    oldTitle.value = targetCatalog.title
  }
}

// 查找对应的目录
function findCatalogById(catalogs: CatalogItem[], targetId: number): CatalogItem {
  for (const catalog of catalogs) {
    if (catalog.id === targetId) {
      return catalog;  // 找到目标目录，返回它
    }

    if (catalog.children && catalog.children.length > 0) {
      // 递归
      const foundInChildren = findCatalogById(catalog.children, targetId);
      if (foundInChildren) {
        return foundInChildren;  // 在子目录中找到目标目录，返回它
      }
    }
  }

  return null;  // 没有找到目标目录
}

// 标题输入框 blur 事件
const onEditTitleInputBlur = (catalogId: number) => {
  let targetCatalog = findCatalogById(catalogs.value, catalogId)
  if (targetCatalog) {
    // 将目标目录的 editing 字段置为 false
    targetCatalog.editing = false
    // 若标题被更新成了空字符串，则给个默认的标题, 提示用户需要输入标题
    targetCatalog.title = targetCatalog.title !== '' ? targetCatalog.title : '请输入标题'
    if (oldTitle.value !== targetCatalog.title) {
      updateWikiCatalogsData()
    }
  }
}

// 移出目录
const removeCatalog = (catalogId: number) => {
  showModel('是否确定移除该目录？').then(() => {
    deleteCatalog(catalogs.value, catalogId)
    updateWikiCatalogsData()
  })
}

// 移出二级目录中的文章
const removeArticleFromCatalog = (catalogId: number) => {
  showModel('是否确定移除该篇文章？').then(() => {
    deleteCatalog(catalogs.value, catalogId)
    updateWikiCatalogsData()
  })
}

// 删除 catalogs 数组中对应的目录对象
function deleteCatalog(catalogs: CatalogItem[], targetId: number): CatalogItem[] {
  for (let i = 0; i < catalogs.length; i++) {
    const catalog = catalogs[i];

    // 一级目录删除
    if (catalog.id === targetId) {
      catalogs.splice(i, 1);
      return catalogs;
    }

    // 二级目录删除
    if (catalog.children) {
      // 递归
      catalog.children = deleteCatalog(catalog.children, targetId);
    }
  }

  return catalogs;
}

// 菜单上移
function catalogMove(catalogId: number, sort: number, action: string) {
  // 被移动的目录
  let sourceCatalog = findCatalogById(catalogs.value, catalogId)
  // 目标目录
  let targetCatalog = getCatalogBySort(sort, action)

  // 若没有找到替换的目标目录，则 return
  if (targetCatalog === null) return

  // 各自的排序号
  let sourceSort = sourceCatalog!.sort
  // 互换排序号
  sourceCatalog!.sort = targetCatalog.sort
  targetCatalog.sort = sourceSort
  // 重新排序
  sortCatalogs()
  updateWikiCatalogsData()
}

// 根据排序规则，得到其需要互换位置的目录
function getCatalogBySort(sort: number, action: string): CatalogItem {
  if (action == 'up') { // 上移
    // 复制一份临时数组，防止等会使用 reverse() 方法后，影响原数组的顺序
    const tmpCatalogs = [...catalogs.value]
    for (const catalog of tmpCatalogs.reverse()) {
      if (catalog.sort < sort) {
        return catalog;  // 找到目标目录，返回它
      }
    }
  } else if (action == 'down') {  // 下移
    for (const catalog of catalogs.value) {
      if (catalog.sort > sort) {
        return catalog;  // 找到目标目录，返回它
      }
    }

  }

  return null;  // 没有找到目标目录
}

// 重新排序目录
function sortCatalogs() {
  // 使用 sort 方法对 sort 字段升序排序
  catalogs.value = catalogs.value.sort((a, b) => a.sort - b.sort);
}

// 添加目录对话框引用
const addCatalogDialogRef = ref<any>(null)
// 添加目录表单引用
const addCatalogFormRef = ref<any>(null)

// 添加目录表单对象
const addCatalogForm = reactive({
  title: ''
})

// 规则校验
const rules = {
  title: [
    {
      required: true,
      message: '目录标题不能为空',
      trigger: 'blur',
    },
  ]
}

// 临时 ID
const tmpId = ref<number>(-1)
// 添加一级目录提交事件
const onAddCatalogSubmit = () => {
  // 先验证 form 表单字段
  addCatalogFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false
    }

    // 获取最后一个一级目录对象
    let lastCatalog = catalogs.value[catalogs.value.length - 1]

    // 构造新的目录对象
    let newCatalog: CatalogItem = {
      id: tmpId.value, // 新的目录由于没有 ID, 这里给个临时 ID, 负数表示, 标识是一个新添加的目录
      title: addCatalogForm.title,
      editing: false,
      level: 1,
      isEmbedding: false,
      sort: lastCatalog.sort + 1, // 最后一个目录的 sort 值加一
      children: []
    }
    // 添加新目录
    catalogs.value.push(newCatalog)
    // 减一
    tmpId.value -= 1
    // 关闭表单对话框
    addCatalogDialogRef.value.close()
    // 将表单中的 title 置空
    addCatalogForm.title = ''
    updateWikiCatalogsData()
  })
}

// 添加文章到目录对话框引用
const addArticle2CatalogDialogRef = ref<any>(null)

// 模糊搜索的文章标题
const searchArticleTitle = ref<string>('')
// 日期
const pickDate = ref<string>(null)

// 查询条件：开始结束时间
const startDate = reactive<{value: string}>({value: null})
const endDate = reactive<{value: string}>({value: null})

// 监听日期组件改变事件，并将开始结束时间设置到变量中
const datepickerChange = (e: [Date, Date]) => {
  if (e) {
    startDate.value = moment(e[0]).format('YYYY-MM-DD')
    endDate.value = moment(e[1]).format('YYYY-MM-DD')
    if (startDate.value === endDate.value) {
      pickDate.value = ''
      startDate.value = null
      endDate.value = null
      showMessage("选择的时间不能一样哦", 'warning')
    }
  }
}

const shortcuts = [
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    },
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    },
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    },
  },
]

// 重置
const reset = () => {
  pickDate.value = ''
  startDate.value = null
  endDate.value = null
  searchArticleTitle.value = ''
  getTableData()
}

// 表格加载 Loading
const tableLoading = ref<boolean>(false)
// 表格数据
const tableData = ref<ArticleItem[]>([])
// 当前页码，给了一个默认值 1
const page = ref<number>(1)
// 总数据量，给了个默认值 0
const total = ref<number>(0)
// 每页显示的数据量，给了个默认值 10
const pageSize = ref<number>(10)


// 获取分页数据
function getTableData() {
  // 显示表格 loading
  tableLoading.value = true
  // 调用后台分页接口，并传入所需参数
  getArticlesList({ page: page.value, pageSize: pageSize.value, startDate: startDate.value, endDate: endDate.value,
    title: searchArticleTitle.value, type: 1 })
      .then((res: any) => {
        if (res.success == true) {
          tableData.value = res.data.list
          total.value = res.data.total
        }
      })
      .finally(() => tableLoading.value = false) // 隐藏表格 loading
}

// 每页展示数量变更事件
const handleSizeChange = (chooseSize: number) => {
  pageSize.value = chooseSize
  getTableData()
}

// 被选择的文章
const selectionArticles = ref<ArticleItem[]>([])
// 表格选择事件
const handleSelectionChange = (articles: ArticleItem[]) => {
  selectionArticles.value = articles
}

// 当前被编辑的目录 ID
const currCatalogId = ref<number>(null)

// 添加文章到目录下
const onAddArticleCatalogSubmit = () => {
  // 校验是否选中文章
  if (!selectionArticles.value || selectionArticles.value.length === 0) {
    showMessage('请勾选需要添加的文章', 'warning')
    return
  }

  for (const catalog of catalogs.value) {
    // 找到当前被编辑的目录
    if (catalog.id === currCatalogId.value) {
      // 循环添加被选中的文章
      for (const selectionArticle of selectionArticles.value) {
        // 文章标题
        let articleTitle = selectionArticle.title
        // 构建新的二级目录
        let newCatalog: CatalogItem = {
          id: tmpId.value,
          docId: selectionArticle.id,
          type: 0,
          title: articleTitle,
          editing: false,
          isEmbedding: false,
          level: 2,
        }
        // 添加到目录数组中
        catalog.children.push(newCatalog)
        tmpId.value -= 1
      }
    }
  }
  // 关闭对话框
  addArticle2CatalogDialogRef.value.close()
  // 置空被选择的文章
  selectionArticles.value = []
  updateWikiCatalogsData()
}

// 当前知识库 ID
const currWikiId = ref<number>(null)
// 获取当前知识库的目录数据
function getCatalogs() {
  getWikiCatalogs({id: currWikiId.value}).then(res => {
    if (res.success) {
      catalogs.value = res.data
    }
  })
}

// 更新知识库目录数据
function updateWikiCatalogsData() {
  updateWikiCatalogs({id: currWikiId.value, catalogs: catalogs.value}).then(res => {
    // 响参失败，提示错误消息
    if (res.success == false) {
      let message = res.message
      showMessage(message, 'error')
    }

    // 重新渲染目录数据
    getCatalogs()
  })
}

// 拖拽结束事件
const onDragEnd = () => {
  updateWikiCatalogsData()
}

// 对话框是否显示
const dialogVisible = ref<boolean>(false)

// 确认按钮加载 loading
const btnLoading = ref<boolean>(false)
// 显示 loading
const showBtnLoading = () => btnLoading.value = true
// 隐藏 loading
const closeBtnLoading = () => btnLoading.value = false

withDefaults(defineProps<{
  title: string;
  width?: string;
  destroyOnClose?: boolean;
  confirmText?: string;
}>(), {
  width: '40%',
  destroyOnClose: false,
  confirmText: '提交'
})

// 打开
const open = (wikiId: number) => {
  dialogVisible.value = true
  currWikiId.value = wikiId
  getCatalogs()
}
// 关闭
const close = () => dialogVisible.value = false

// 对外暴露方法
defineExpose({
  open,
  close,
  showBtnLoading,
  closeBtnLoading
})

</script>