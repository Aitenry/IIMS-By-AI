<template>
  <div>
    <el-container class="customer-page-box">
      <el-aside
          width="220px"
          style="margin-left: 10px;"
      >
        <el-input
            v-model="filterText"
            placeholder="搜索档案关键字"
            prefix-icon="search"
            clearable
            style="margin-bottom: 13px;"
        />
        <el-scrollbar height="calc(100vh - 143px)">
          <el-tree
              default-expand-all
              style="overflow: auto; height: 100%;"
              :data="menus"
              :props="defaultProps"
              :filter-node-method="filterNode"
              @node-click="handleNodeClick"
          >
            <template #default="{ data }">
              <span>
                <i :class="data.icon" />
                {{ `${data.label}` }}
              </span>
            </template>
          </el-tree>
        </el-scrollbar>
      </el-aside>
      <el-main style="padding: 0;" width="80%">
        <div
            v-if="!pages.id"
            style="margin-top: calc(50vh - 149px); text-align: center; display: grid;"
        >
          <el-icon style="font-size: 7em; color: #c5c3c3; margin: 0 auto;"><OfficeBuilding /></el-icon>
          <div style="display: inline-flex; align-items: center; margin: 0 auto;">
            <el-icon style="font-size: 37px; color: #c5c3c3;"><Promotion /></el-icon>
            <div style="display: grid; color: #c5c3c3;">
              <span>点击左侧目录上的档案，</span>
              <span>可以查阅相应的档案信息！</span>
            </div>
          </div>
        </div>
        <div v-else :key="pages.id">
          <el-header style="height: 32px; margin-bottom: 13px;">
            <el-row style="justify-content: end;">
              <el-button plain type="primary" @click="showAddArchive"><i style="margin-right: 3px;" class="ri-menu-add-fill"></i>新增</el-button>
              <el-button plain type="success"><i style="margin-right: 3px;" class="ri-inbox-archive-fill"></i>归档</el-button>
              <el-button plain type="danger" @click="delSelectedArchive"><i style="margin-right: 3px;" class="ri-delete-bin-5-fill"></i>删除</el-button>
              <el-button plain color="#626aef"><i style="margin-right: 3px;" class="ri-chat-ai-fill"></i>AI助手</el-button>
              <el-dropdown
                  split-button
                  type="primary"
                  style="margin-left: 12px;"
                  @click="handleMoreClick"
              >
                更多
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>清除档号</el-dropdown-item>
                    <el-dropdown-item>更新档号</el-dropdown-item>
                    <el-dropdown-item>四性检测</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-popover
                  placement="bottom-start"
                  width="870"
                  :visible="popoverVisible"
              >
                <el-form
                    ref="queryForm"
                    class="cus-search-box"
                    :model="pages"
                    :inline="true"
                    style="height: 82px; text-align: start;"
                >
                  <el-form-item
                      label="档号"
                      prop="archivalCode"
                  >
                    <el-input
                        v-model="pages.archivalCode"
                        placeholder="请输入档号"
                        clearable
                        @keyup.enter.native="handleQuery"
                    />
                  </el-form-item>
                  <el-form-item
                      label="题名"
                      prop="archivalTitle"
                  >
                    <el-input
                        v-model="pages.archivalTitle"
                        placeholder="请输入题名"
                        clearable
                        @keyup.enter.native="handleQuery"
                    />
                  </el-form-item>
                  <el-form-item
                      label="责任人"
                      prop="archivalResponsible"
                  >
                    <el-input
                        v-model="pages.archivalResponsible"
                        placeholder="请输入责任人"
                        clearable
                        @keyup.enter.native="handleQuery"
                    />
                  </el-form-item>
                  <el-form-item
                      label="年度"
                      prop="archivalYear"
                  >
                    <el-date-picker
                        v-model="pages.archivalYear"
                        type="year"
                        value-format="YYYY"
                        placeholder="选择年度"
                        clearable
                        @keyup.enter.native="handleQuery"
                        style="width: 181px;"
                    />
                  </el-form-item>
                  <el-form-item
                      label="档案日期"
                      prop="archivalDate"
                  >
                    <el-date-picker
                        v-model="pages.archivalTime"
                        style="width: 300px;"
                        type="daterange"
                        unlink-panels
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                    />
                  </el-form-item>
                  <el-form-item class="search-btn wp-75">
                    <el-button
                        class="cus-search-btn"
                        type="primary"
                        @click="handleQuery"
                    >搜索</el-button>
                    <el-button
                        class="cus-reset-btn"
                        @click="resetQuery"
                    >重置</el-button>
                  </el-form-item>
                </el-form>
                <template #reference>
                  <el-button
                      type="primary"
                      style="margin-left: 12px;"
                      @click="popoverVisible = !popoverVisible"
                  ><el-icon><Search /></el-icon></el-button>
                </template>
              </el-popover>
            </el-row>
          </el-header>

          <el-table
              ref="table"
              v-loading="listLoading"
              :data="list"
              element-loading-text="数据加载中..."
              border
              fit
              highlight-current-row
              empty-text="暂无数据"
              style="width: calc(100% - 40px); margin: auto; height: calc(100vh - 210px);"
              max-height="calc(100vh - 210px)"
              @row-click="handleRowClick"
              @selection-change="handleSelectedChange"
          >
            <el-table-column
                type="selection"
                width="40"
                align="center"
            />
            <el-table-column
                align="center"
                label="序号"
                width="50"
            >
              <template #default="scope">
                {{ (pages.page - 1) * pages.pageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column
                label="档号"
                width="170"
                align="center"
                prop="archivalCode"
                show-overflow-tooltip
            />
            <el-table-column
                label="题名"
                width="170"
                align="center"
                prop="archivalTitle"
                show-overflow-tooltip
            />
            <el-table-column
                label="责任人"
                align="center"
                prop="archivalResponsible"
                show-overflow-tooltip
            >
              <template #default="scope">
                <el-button type='primary' text @click="openResponsibleInfo(scope.row.archivalResponsible.id)">
                  {{ scope.row.archivalResponsible?.username || '-' }}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column
                label="年度"
                width="60"
                align="center"
                prop="archivalYear"
                show-overflow-tooltip
            />
            <el-table-column
                label="密级"
                width="60"
                align="center"
                prop="archivalLevel"
                show-overflow-tooltip
            />
            <el-table-column
                label="保管期限"
                align="center"
                prop="archivalDeadline"
                show-overflow-tooltip
            />
            <el-table-column
                label="档案日期"
                align="center"
                prop="archivalDate"
                show-overflow-tooltip
            >
              <template #default="scope">
                <span> {{ parseTime(scope.row.archivalDate, "{y}-{m}-{d}") }}</span>
              </template>
            </el-table-column>
            <el-table-column
                label="操作"
                width="190"
                prop="handle"
                align="center"
            >
              <template #default="{ row }">
                <el-tooltip
                    class="item"
                    effect="dark"
                    content="档案详情"
                    placement="bottom"
                >
                  <el-button
                      plain
                      type="primary"
                      size="small"
                      icon="view"
                      @click="showArchiveDetail(row.id)"
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
                      @click="showEditArchive(row.id)"
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
                      @click="deleteArchiveHandle(row.id)"
                  />
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
              style="justify-content: center; margin-top: 20px;"
              :page-sizes="[15, 30, 60]"
              :page-size="pages.pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pages.total"
              :background="true"
              :hide-on-single-page="true"
              @current-change="handleCurrentChange"
              @size-change="handleSizeChange"
          />
        </div>
      </el-main>
    </el-container>
    <el-dialog
        :title="titleOperateType + archiveTypeLabel"
        v-model="dialogOperateArchive"
        width="1170px"
        height="calc(100vh - 305px)"
    >
      <OperateArchiveMetadata
          v-if="dialogOperateArchive"
          :id="archiveId"
          ref="operate"
          :name="operateComponent"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogOperateArchive = false">取 消</el-button>
          <el-button
              type="primary"
              @click="doSubmitToOperateArchive"
          >确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
        title="责任人信息"
        v-model="dialogOperateUserInfo"
        align-center
        width="600px"
    >
      <InfoCard :id="userId" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMenuTree, getArchiveList, editArchiveMetadata, addArchiveMetadata, delArchiveMetadata } from '@/api/dms/collect'
import type { TableInstance } from 'element-plus'
import {OfficeBuilding, Promotion} from "@element-plus/icons-vue";
import OperateArchiveMetadata from "@/views/system/layout/components/archive/OperateArchiveMetadata.vue";
import InfoCard from "@/layout/user/InfoCard.vue";
import {parseTime} from "@/utils/common.ts";

interface BaseTreeNode {
  id: string
  parentId: string
  icon?: string
  label: string
  labelCode?: string
  typeLabel?: string
  operateComponent?: string
  detailComponent?: string
  children?: BaseTreeNode[]
}

// 组件引用
const menus = ref<BaseTreeNode[]>([])
const table = ref<TableInstance>()
const operate = ref()

// 数据模型
const ids = ref<string[]>([])
const list = ref<any[]>([])
const archiveId = ref<string | null>(null)
const filterText = ref('')
const userId = ref('')
const listLoading = ref(true)
const titleOperateType = ref('')
const dialogOperateArchive = ref(false)
const dialogOperateUserInfo = ref(false)
const popoverVisible = ref(false)
const archiveTypeLabel = ref('')
const operateComponent = ref('')
const queryForm = ref()

const defaultProps = {
  children: 'children',
  label: 'label'
}

const pages = reactive({
  id: null,
  archivalCode: null,
  archivalTitle: null,
  archivalResponsible: null,
  archivalTime: [] as (string)[],
  archivalYear: null,
  page: 1,
  pageSize: 15,
  total: 0
})

// 监听搜索文本变化
watch(filterText, (val) => {
  if (menus.value) {
    (menus.value as any).filter(val)
  }
})

// 获取菜单树
const getMenus = async () => {
  try {
    const res = await getMenuTree()
    menus.value = res.data
  } catch (error) {
    console.log(error)
  }
}

// 重置查询
const resetQuery = () => {
  pages.archivalCode = null
  pages.archivalTitle = null
  pages.archivalResponsible = null
  pages.archivalYear = null
  pages.archivalTime = []
  pages.page = 1
  handleQuery()
}

// 搜索按钮操作
const handleQuery = () => {
  pages.page = 1
  fetchData()
}

// 点击行选中或取消复选框
const handleRowClick = (row: any, column: any, _event: any) => {
  if (column.property !== 'handle' && !dialogOperateUserInfo.value) {
    table.value?.toggleRowSelection(row)
  }
}

// 处理选中变化
const handleSelectedChange = (selection: any[]) => {
  ids.value = selection.map(item => item.id)
}

// 删除选中的档案
const delSelectedArchive = async () => {
  if (ids.value.length === 0) {
    ElMessage.warning({
      message: '至少勾选一条档案！'
    })
    return
  }

  try {
    const res = await delArchiveMetadata(ids.value)
    if (res.code === 1) {
      await fetchData()
      ElMessage.success({
        message: '档案删除成功'
      })
    }
  } catch (error) {
    console.log(error)
  }
}

// 更多按钮点击
const handleMoreClick = async () => {
  // 空实现
}

// 提交操作档案
const doSubmitToOperateArchive = async () => {
  dialogOperateArchive.value = false

  if (archiveId.value) {
    try {
      const data = operate.value.getOperateFormData()
      data.archivalResponsible = JSON.stringify({
        id: data.archivalResponsible.value,
        username: data.archivalResponsible.label
      })
      const res = await editArchiveMetadata(data)
      if (res.code === 1) {
        await fetchData()
        ElMessage.success({
          message: `成功${titleOperateType.value}${archiveTypeLabel.value}`
        })
      }
    } catch (error) {
      console.log(error)
    }
  } else {
    const data = operate.value.getOperateFormData()
    data.metadataOwnership = pages.id
    const res = await addArchiveMetadata(data)
    if (res.code === 1) {
      await fetchData()
      ElMessage.success({
        message: `成功${titleOperateType.value}${archiveTypeLabel.value}`
      })
    }
  }
}

// 显示档案详情
const showArchiveDetail = async (id: string) => {
  console.log(id)
}

// 显示新增档案
const showAddArchive = async () => {
  dialogOperateArchive.value = true
  titleOperateType.value = '新增'
  archiveId.value = null
}

// 显示编辑档案
const showEditArchive = async (id: string) => {
  dialogOperateArchive.value = true
  titleOperateType.value = '编辑'
  archiveId.value = id
}

// 删除档案
const deleteArchiveHandle = async (id: string) => {
  try {
    const res = await delArchiveMetadata([id])
    if (res.code === 1) {
      await fetchData()
      ElMessage.success({
        message: '档案删除成功'
      })
    }
  } catch (error) {
    console.log(error)
  }
}

// 节点点击
const handleNodeClick = async (data: any, node: any) => {
  if (node.isLeaf) {
    if (pages.id !== data.id) {
      pages.id = data.id
      operateComponent.value = data.operateComponent
      archiveTypeLabel.value = data.typeLabel
      pages.page = 1
      pages.pageSize = 15
      pages.total = 0
      await fetchData()
    }
  }
}

// 获取数据
const fetchData = async () => {
  listLoading.value = true
  try {
    const res = await getArchiveList(pages)
    list.value = res.data.list
    pages.total = res.data.total
  } catch (error) {
    console.log(error)
  } finally {
    listLoading.value = false
  }
}

// 当前页改变
const handleCurrentChange = (val: number) => {
  pages.page = val
  fetchData()
}

// 页面大小改变
const handleSizeChange = (val: number) => {
  pages.pageSize = val
  fetchData()
}

// 打开责任人信息
const openResponsibleInfo = (userIdParam: string) => {
  if (userIdParam) {
    userId.value = userIdParam
    dialogOperateUserInfo.value = !dialogOperateUserInfo.value
  }
}

// 过滤节点
const filterNode = (value: string, data: any) => {
  if (!value) return true
  return data.label.indexOf(value) !== -1
}

// 组件挂载后执行
onMounted(() => {
  getMenus()
})
</script>

<style>
</style>