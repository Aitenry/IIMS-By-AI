<template>
  <el-select
      v-model="selectedIcon"
      filterable
      remote
      reserve-keyword
      placeholder="请输入关键字来搜索图标"
      :remote-method="searchIcons"
      :loading="loading"
      style="width: 100%"
  >
    <el-option-group
        v-for="group in groupedIconOptions"
        :key="group.label"
        :label="group.label"
    >
      <el-option
          v-for="item in group.options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
      >
        <div class="flex items-center">
          <i :class="item.value" class="mr-2"></i>
          <span>{{ item.label }}</span>
        </div>
      </el-option>
    </el-option-group>
  </el-select>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import icons from '@/assets/json/tags.json'

interface IconItem {
  value: string
  label: string
  keywords: string[]
}

interface GroupedOption {
  label: string
  options: IconItem[]
}

const selectedIcon = ref<string>('')
const iconOptions = ref<IconItem[]>([])
const loading = ref(false)

// 初始化时加载所有图标
const initializeAllIcons = () => {
  const allIcons: IconItem[] = []

  for (const [category, items] of Object.entries(icons)) {
    for (const [iconName, keywordsStr] of Object.entries(items as any)) {
      const keywords = (keywordsStr as string).split(',').map(k => k.trim())
      allIcons.push({
        value: iconName,
        label: `${iconName}`,
        keywords
      })
    }
  }

  return allIcons
}

// 构建反向索引用于快速搜索
const buildReverseIndex = (iconsData: any) => {
  const index: Record<string, Array<{ category: string; iconName: string; keywords: string }>> = {}

  for (const [category, items] of Object.entries(iconsData)) {
    for (const [iconName, keywordsStr] of Object.entries(items as any)) {
      const keywords = (keywordsStr as string).split(',').map(k => k.trim().toLowerCase())

      keywords.forEach(keyword => {
        if (!index[keyword]) index[keyword] = []
        index[keyword].push({
          category,
          iconName,
          keywords: keywordsStr as string
        })
      })
    }
  }

  return index
}

const reverseIndex = buildReverseIndex(icons)

const searchIcons = (query: string) => {
  if (query) {
    loading.value = true
    const searchTerm = query.toLowerCase().trim()
    const results: IconItem[] = []

    // 精确匹配
    if (reverseIndex[searchTerm]) {
      reverseIndex[searchTerm].forEach(item => {
        results.push({
          value: item.iconName,
          label: `${item.iconName}`,
          keywords: item.keywords.split(',')
        })
      })
    }

    // 模糊匹配关键词
    for (const [keyword, items] of Object.entries(reverseIndex)) {
      if (keyword.includes(searchTerm) && !results.some(r =>
          r.value === items[0].iconName
      )) {
        items.forEach(item => {
          // 避免重复添加
          if (!results.some(r => r.value === item.iconName)) {
            results.push({
              value: item.iconName,
              label: `${item.iconName}`,
              keywords: item.keywords.split(',')
            })
          }
        })
      }
    }

    // 按类别分组结果
    const groupedResults: Record<string, IconItem[]> = {}
    results.forEach(item => {
      // 这里需要从原始数据中获取类别信息
      const originalCategory = getCategoryForIcon(item.value)
      const categoryLabel = originalCategory || '其他'

      if (!groupedResults[categoryLabel]) {
        groupedResults[categoryLabel] = []
      }
      groupedResults[categoryLabel].push(item)
    })

    iconOptions.value = results
    loading.value = false
  } else {
    iconOptions.value = initializeAllIcons()
  }
}

// 根据图标名称获取所属类别
const getCategoryForIcon = (iconName: string): string | null => {
  for (const [category, items] of Object.entries(icons)) {
    if (Object.keys(items as any).includes(iconName)) {
      return category as string
    }
  }
  return null
}

// 计算分组后的选项
const groupedIconOptions = computed<GroupedOption[]>(() => {
  const groupedResults: Record<string, IconItem[]> = {}

  iconOptions.value.forEach(item => {
    const originalCategory = getCategoryForIcon(item.value)
    const categoryLabel = originalCategory || '其他'

    if (!groupedResults[categoryLabel]) {
      groupedResults[categoryLabel] = []
    }
    groupedResults[categoryLabel].push({
      ...item,
      label: `${item.value}`
    })
  })

  return Object.entries(groupedResults).map(([label, options]) => ({
    label,
    options
  }))
})

</script>

<style scoped>
.flex {
  display: flex;
}
.items-center {
  align-items: center;
}
.mr-2 {
  margin-right: 0.5rem;
}
</style>