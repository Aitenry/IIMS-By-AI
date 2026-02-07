<template>
  <el-card class="w-full">
    <template #header>
      <span class="flex items-center">
        <i class="ri-vip-crown-fill mr-1"></i>
        统计信息
      </span>
    </template>

    <!-- 统计卡片区域 -->
    <div class="w-full grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-blue-50 dark:bg-blue-900/20 p-4 rounded-lg border border-blue-100 dark:border-blue-800">
        <div class="flex items-center">
          <i class="ri-file-text-line text-2xl text-blue-500 mr-3"></i>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">文档</p>
            <p class="text-xl font-bold text-gray-800 dark:text-white">{{ statisticsData.articleCount || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="bg-green-50 dark:bg-green-900/20 p-4 rounded-lg border border-green-100 dark:border-green-800">
        <div class="flex items-center">
          <i class="ri-folder-2-line text-2xl text-green-500 mr-3"></i>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">文件</p>
            <p class="text-xl font-bold text-gray-800 dark:text-white">{{ statisticsData.fileCount || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="bg-orange-50 dark:bg-orange-900/20 p-4 rounded-lg border border-orange-100 dark:border-orange-800">
        <div class="flex items-center">
          <i class="ri-user-line text-2xl text-orange-500 mr-3"></i>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">人员</p>
            <p class="text-xl font-bold text-gray-800 dark:text-white">{{ statisticsData.userCount || 0 }}</p>
          </div>
        </div>
      </div>

      <div class="bg-purple-50 dark:bg-purple-900/20 p-4 rounded-lg border border-purple-100 dark:border-purple-800">
        <div class="flex items-center">
          <i class="ri-book-open-line text-2xl text-purple-500 mr-3"></i>
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400">知识库</p>
            <p class="text-xl font-bold text-gray-800 dark:text-white">{{ statisticsData.wikiCount || 0 }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 折线图 - 日志和文件 -->
    <div class="w-full">
      <div ref="lineChartRef" class="w-full h-72 md:h-80 lg:h-96"></div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { getStatisticsData, getStatisticsDayData } from "@/api/statistics.ts";

const lineChartRef = ref<HTMLDivElement | null>(null)
let lineChartInstance: echarts.ECharts | null = null

interface StatisticsData {
  articleCount?: number;
  fileCount?: number;
  logCount?: number;
  userCount?: number;
  dictCount?: number;
  wikiCount?: number;
}

const statisticsData = reactive<StatisticsData>({})

const initLineChart = async () => {
  if (!lineChartRef.value) return

  // 销毁之前的实例
  if (lineChartInstance) {
    lineChartInstance.dispose()
  }

  lineChartInstance = echarts.init(lineChartRef.value)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['日志', '文件']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [],
        smooth: true,
        type: 'line',
        name: '日志',
        itemStyle: {
          color: '#ef4444'
        },
        areaStyle: {
          opacity: 0.1
        }
      },
      {
        data: [],
        smooth: true,
        type: 'line',
        name: '文件',
        itemStyle: {
          color: '#10b981'
        },
        areaStyle: {
          opacity: 0.1
        }
      }
    ]
  }

  const res = await getStatisticsDayData()
  if (res.code === 1) {
    option.xAxis.data = res.data.statisticsDays
    option.series[0].data = res.data.logCounts
    option.series[1].data = res.data.fileCounts
  }

  lineChartInstance.setOption(option)
}

// 获取统计数据
const fetchStatisticsData = async () => {
  const res = await getStatisticsData()
  if (res.code === 1) {
    Object.assign(statisticsData, res.data)
  }
}

const handleResize = () => {
  if (lineChartInstance) {
    lineChartInstance.resize()
  }
}

onMounted(async () => {
  await fetchStatisticsData()
  await initLineChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (lineChartInstance) {
    lineChartInstance.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
</style>