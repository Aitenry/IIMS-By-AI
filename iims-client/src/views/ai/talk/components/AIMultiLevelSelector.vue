<template>
  <div class="ai-multi-level-selector">
    <!-- 主按钮 -->
    <div ref="triggerWrapperRef" class="trigger-wrapper">
      <el-button
          ref="triggerRef"
          :style="buttonStyle"
          :disabled="disabled"
          @click="toggleDropdown"
      >
        <span class="selector-label">
          <i v-if="selectedIcon" :class="selectedIcon" style="margin-right: 3px; font-size: 16px;"></i>
          <span style="font-size: 12px;"> {{ selectedDisplayText || placeholder }} </span>
        </span>
        <i class="ri-arrow-down-s-line" style="font-size: 14px; margin-left: 6px;"></i>
      </el-button>
    </div>

    <!-- 下拉面板 -->
    <div
        v-if="showDropdown"
        ref="dropdownRef"
        class="dropdown-panel"
        :style="dropdownPositionStyle"
        @click.stop
    >
      <!-- 左侧：API类型分类 -->
      <div class="left-panel">
        <div class="category-list">
          <div
              v-for="(category, _index) in categories"
              :key="category.key"
              class="category-item"
              :class="{ active: activeCategory === category.key }"
              @click="selectCategory(category.key)"
          >
            <span style="display: flex; align-items: center;">
              <i :class="category.icon" style="margin-right: 3px; font-size: 16px;"></i>
              <span style="font-size: 12px;"> {{ category.name }} </span>
            </span>
            <i
                v-if="activeCategory === category.key && category.hasMore"
                class="ri-arrow-right-s-line"
                style="font-size: 14px; color: #64728b;"
            ></i>
          </div>
        </div>
      </div>

      <!-- 右侧：模型列表 -->
      <div class="right-panel">
        <div class="search-box">
          <el-input
              v-model="searchQuery"
              placeholder="搜索模型..."
              clearable
              size="small"
              :prefix-icon="Search"
              @input="onSearchInput"
          />
        </div>

        <div class="model-list">
          <div
              v-for="model in filteredModels"
              :key="model.id"
              class="model-item"
              :class="{ selected: isSelected(model) }"
              @click="selectModel(model)"
          >
            <div class="model-header">
              <div class="model-header-left">
                <i v-if="model.icon" :class="model.icon" style="margin-right: 8px; font-size: 16px;"></i>
                <span class="model-name">{{ model.name }}</span>
              </div>
              <div
                  v-if="model.modelType"
                  class="type-tag"
              >
                <span>{{ getModelTypeName(model.modelType) }}</span>
              </div>
            </div>
            <div class="model-desc">{{ model.description || '暂无描述' }}</div>
            <i
                v-if="isSelected(model)"
                class="ri-check-line"
                style="color: #40a0ff; font-size: 12px; position: absolute; top: 3px; right: 3px;"
            ></i>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { Search } from "@element-plus/icons-vue";

interface ModelItem {
  id: string
  name: string
  description: string | null
  apiType: string
  modelType: string
  icon: string
}

interface Category {
  key: string
  name: string
  hasMore?: boolean
  icon?: string
}

interface AIMultiLevelSelectorProps {
  models?: ModelItem[]
  placeholder?: string
  disabled?: boolean
  buttonStyle?: Record<string, string>
  defaultSelection?: string // 默认选中项的ID
}

const props = withDefaults(defineProps<AIMultiLevelSelectorProps>(), {
  models: () => [],
  placeholder: '智能体/大模型',
  disabled: false,
  defaultSelection: ''
})

interface AIMultiLevelSelectorEmits {
  (e: 'selection-changed', selection: ModelItem | null): void
}

const emit = defineEmits<AIMultiLevelSelectorEmits>()

// refs
const triggerRef = ref<any>(null)
const triggerWrapperRef = ref<HTMLElement | null>(null)
const dropdownRef = ref<HTMLElement | null>(null)
const dropdownPositionStyle = ref<Record<string, string>>({})

// 状态
const showDropdown = ref(false)
const searchQuery = ref('')
const activeCategory = ref<string>('')
const selectedModelId = ref<string>('')

// API类型映射
const apiTypeMap: Record<string, { name: string; icon: string }> = {
  AGENT: { name: 'Agent', icon: 'ri-leaf-line' },
  OPENAI: { name: 'OpenAI', icon: 'ri-openai-line' },
  OLLAMA: { name: 'Ollama', icon: 'ri-layout-masonry-line' },
  DEEPSEEK: { name: 'DeepSeek', icon: 'ri-deepseek-line' }
}

// 模型类型映射
const modelTypeMap: Record<string, string> = {
  LANGUAGE: '语言模型',
  VISION: '视觉模型',
  MULTIMODAL: '多模态模型'
}

// 分类数据
const categories = computed<Category[]>(() => {
  const availableTypes = Array.from(new Set(props.models.map(model => model.apiType)))

  return availableTypes.map(type => ({
    key: type,
    name: apiTypeMap[type]?.name || type,
    icon: apiTypeMap[type]?.icon || 'ri-layout-masonry-line'
  }))
})

// 当前分类下的模型
const currentCategoryModels = computed<ModelItem[]>(() => {
  return props.models
      .filter(model => model.apiType === activeCategory.value)
      .map(model => ({
        ...model,
        icon: apiTypeMap[model.apiType]?.icon || 'ri-layout-masonry-line'
      }))
})

// 过滤后的模型（搜索 + 分类）
const filteredModels = computed<ModelItem[]>(() => {
  let modelsToFilter = currentCategoryModels.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    return modelsToFilter.filter(model =>
        model.name.toLowerCase().includes(query) ||
        (model.description && model.description.toLowerCase().includes(query)) ||
        model.apiType.toLowerCase().includes(query)
    )
  }

  return modelsToFilter
})

// 计算选中的显示文本
const selectedDisplayText = computed(() => {
  if (selectedModelId.value) {
    const model = props.models.find(m => m.id === selectedModelId.value)
    return model ? model.name : ''
  }
  return ''
})

// 计算选中模型的图标
const selectedIcon = computed(() => {
  if (selectedModelId.value) {
    const model = props.models.find(m => m.id === selectedModelId.value)
    return model ? apiTypeMap[model.apiType]?.icon : ''
  }
  return ''
})

// 检查模型是否被选中
const isSelected = (model: ModelItem) => {
  return selectedModelId.value === model.id
}

// 获取模型类型名称
const getModelTypeName = (modelType: string) => {
  return modelTypeMap[modelType] || modelType
}

// 设置默认选中项
const setDefaultSelection = () => {
  if (props.defaultSelection && props.defaultSelection !== '' && props.models.some(m => m.id === props.defaultSelection)) {
    // 如果提供了明确的默认选中项，且该模型存在
    selectedModelId.value = props.defaultSelection
    const selectedModel = props.models.find(m => m.id === props.defaultSelection)
    if (selectedModel && !activeCategory.value) {
      activeCategory.value = selectedModel.apiType
    }
  } else if (props.defaultSelection === '' && props.models.length > 0) {
    // 如果没有提供默认选中项（空字符串），则选中第一个模型
    const firstModel = props.models[0]
    selectedModelId.value = firstModel.id
    if (!activeCategory.value) {
      activeCategory.value = firstModel.apiType
    }
  } else if (!selectedModelId.value && !activeCategory.value && categories.value.length > 0) {
    // 如果都没有设置，则默认激活第一个分类
    activeCategory.value = categories.value[0].key
  }

  // 如果有选中的模型，触发事件
  if (selectedModelId.value) {
    const selectedModel = props.models.find(m => m.id === selectedModelId.value)
    if (selectedModel) {
      emit('selection-changed', selectedModel)
    }
  }
}

// 选择分类
const selectCategory = (categoryKey: string) => {
  activeCategory.value = categoryKey
  searchQuery.value = '' // 切换分类时清空搜索
}

// 选择模型
const selectModel = (model: ModelItem) => {
  selectedModelId.value = model.id
  showDropdown.value = false
  emit('selection-changed', model)
}

// 获取真实的 DOM 元素
const getTriggerElement = (): HTMLElement | null => {
  if (triggerRef.value?.$el) {
    return triggerRef.value.$el as HTMLElement
  } else if (triggerWrapperRef.value) {
    return triggerWrapperRef.value
  }
  return null
}

// 更新下拉面板位置
const updateDropdownPosition = () => {
  const triggerEl = getTriggerElement()
  if (!triggerEl || !dropdownRef.value) return

  const triggerRect = triggerEl.getBoundingClientRect()
  const dropdownRect = dropdownRef.value.getBoundingClientRect()

  let top = triggerRect.bottom + window.scrollY
  let left = triggerRect.left + window.scrollX

  // 防止下拉超出视口底部
  if (top + dropdownRect.height > window.innerHeight + window.scrollY) {
    top = triggerRect.top - dropdownRect.height + window.scrollY
  }

  // 防止下拉超出视口右侧
  if (left + dropdownRect.width > window.innerWidth + window.scrollX) {
    left = Math.max(0, window.innerWidth - dropdownRect.width + window.scrollX)
  }

  dropdownPositionStyle.value = {
    position: 'fixed',
    top: `${top}px`,
    left: `${left}px`,
    zIndex: '9999'
  }
}

// 切换下拉菜单
const toggleDropdown = () => {
  if (props.disabled) return

  showDropdown.value = !showDropdown.value

  if (showDropdown.value) {
    nextTick(() => {
      updateDropdownPosition()
    })
  }
}

// 搜索输入处理
const onSearchInput = () => {
  // 搜索时保持当前分类
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  const triggerEl = getTriggerElement()

  if (
      showDropdown.value &&
      triggerEl &&
      !triggerEl.contains(event.target as Node) &&
      dropdownRef.value &&
      !dropdownRef.value.contains(event.target as Node)
  ) {
    showDropdown.value = false
  }
}

// 监听模型数据变化，重新设置默认选中项
watch(() => props.models, () => {
  setDefaultSelection()
}, { immediate: true })

// 监听默认选中项变化
watch(() => props.defaultSelection, (newVal) => {
  if (newVal && newVal !== '' && props.models.some(m => m.id === newVal)) {
    selectedModelId.value = newVal
    const selectedModel = props.models.find(m => m.id === newVal)
    if (selectedModel) {
      activeCategory.value = selectedModel.apiType
      emit('selection-changed', selectedModel)
    }
  } else if (newVal === '' && props.models.length > 0) {
    // 当 defaultSelection 为空字符串时，选中第一个模型
    const firstModel = props.models[0]
    selectedModelId.value = firstModel.id
    activeCategory.value = firstModel.apiType
    emit('selection-changed', firstModel)
  }
})

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  setDefaultSelection() // 初始化时设置默认选中项
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.ai-multi-level-selector {
  position: relative;
  display: inline-block;
}

.trigger-wrapper {
  display: inline-block;
}

.dropdown-panel {
  position: fixed;
  z-index: 9999;
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1), 0 4px 6px rgba(0, 0, 0, 0.05);
  width: 530px;
  height: 291px;
  display: flex;
  border: 1px solid #e5e7eb;
  margin-top: 12px;
}

/* 左侧面板 */
.left-panel {
  width: 130px;
  border-right: 1px solid #e5e7eb;
  background: #f9fafb;
  display: flex;
  flex-direction: column;
}

.category-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #6b7280;
  font-size: 14px;
  border-left: 3px solid transparent;
  position: relative;
  overflow: hidden;
}

.category-item:hover {
  background: #f3f4f6;
  color: #4b5563;
}

.category-item.active {
  background: #f0f9ff;
  color: #2563eb;
  font-weight: 500;
  border-left: 3px solid #93c5fd;
}

.category-item .ri-arrow-right-s-line {
  opacity: 0.5;
  transition: all 0.2s ease;
  color: inherit;
}

.category-item.active .ri-arrow-right-s-line {
  opacity: 0.8;
  color: #2563eb !important;
}

.category-item.active i:not(.ri-arrow-right-s-line) {
  color: #2563eb !important;
  transition: color 0.3s ease;
}

/* 右侧面板 */
.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
}

.search-box {
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
}

.model-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.model-list::-webkit-scrollbar {
  width: 6px;
}

.model-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.model-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.model-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.model-item {
  padding: 14px 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  margin: 2px 8px;
}

.model-item:hover {
  background: #f9fafb;
}

.model-item.selected {
  background: #f0f9ff;
  border: 1px solid #dbeafe;
}

.model-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
}

.model-header-left {
  display: flex;
  align-items: center;
}

.model-name {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.model-desc {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.4;
  max-height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-box-orient: vertical;
}

.type-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(79, 146, 246, 0.1);
  color: #4f93f6;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.selector-label {
  flex: 1;
  text-align: left;
  font-size: 14px;
  font-weight: 500;
  color: #4f93f6;
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>