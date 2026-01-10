<template>
  <el-card class="w-full">
    <template #header>
      <span class="flex items-center">
        <i class="ri-vip-crown-fill mr-1"></i>
        统计信息
      </span>
    </template>
    <div class="w-full flex flex-col md:flex-row gap-6">
      <!-- 扇形图 -->
      <div class="w-full md:w-1/3">
        <div ref="sectorChartRef" class="w-full h-72 md:h-80 lg:h-96"></div>
      </div>

      <!-- 折线图 -->
      <div class="w-full md:w-2/3">
        <div ref="lineChartRef" class="w-full h-72 md:h-80 lg:h-96"></div>
      </div>
    </div>
  </el-card>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import {getStatisticsData, getStatisticsDayData} from "@/api/statistics.ts";

const sectorChartRef = ref<HTMLDivElement | null>(null)
const lineChartRef = ref<HTMLDivElement | null>(null)
let sectorChartInstance: echarts.ECharts | null = null
let lineChartInstance: echarts.ECharts | null = null

interface PieDataItem {
  value: number;
  name: string;
}

interface LineDataItem {
  data: number[];
  name: string;
  smooth: boolean;
  type: string;
}

const initSectorChart = async () => {
  if (!sectorChartRef.value) return

  // 销毁之前的实例
  if (sectorChartInstance) {
    sectorChartInstance.dispose()
  }

  sectorChartInstance = echarts.init(sectorChartRef.value)

  const option = {
    legend: {
      top: 'top'
    },
    toolbox: {
      show: true
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [
      {
        name: '统计信息',
        type: 'pie',
        radius: ['40%', '75%'],
        center: ['50%', '50%'],
        avoidLabelOverlap: false,
        top: 35,
        itemStyle: {
          borderRadius: 8
        },
        label: {
          show: true,
          formatter: '{b}\n{d}%\n({c})',
          fontSize: 12,
          lineHeight: 16
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: true
        },
        data: [] as PieDataItem[]
      }
    ]
  }

  const res = await getStatisticsData()

  if (res.code === 1) {
    option.series[0].data = [
      { value: res.data.articleCount || 0, name: '文档' },
      { value: res.data.fileCount || 0, name: '文件' },
      { value: res.data.logCount || 0, name: '日志' },
      { value: res.data.userCount || 0, name: '人员' },
      { value: res.data.dictCount || 0, name: '字典' },
      { value: res.data.wikiCount || 0, name: '知识库' }
    ]
  }

  sectorChartInstance.setOption(option)
}

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
      data: ['文档', '文件', '日志', '人员', '字典', '知识库']
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
    series: [] as LineDataItem[]
  }

  const res = await getStatisticsDayData()
  if (res.code === 1) {
    option.xAxis.data = res.data.statisticsDays
    option.series = [
      { data: res.data.articleCounts, smooth: true, type: 'line', name: '文档' },
      { data: res.data.fileCounts, smooth: true, type: 'line', name: '文件' },
      { data: res.data.logCounts, smooth: true, type: 'line', name: '日志' },
      { data: res.data.userCounts, smooth: true, type: 'line', name: '人员' },
      { data: res.data.dictCounts, smooth: true, type: 'line', name: '字典' },
      { data: res.data.wikiCounts, smooth: true, type: 'line', name: '知识库' }
    ]
  }
  lineChartInstance.setOption(option)
}

const handleResize = () => {
  if (sectorChartInstance) {
    sectorChartInstance.resize()
  }
  if (lineChartInstance) {
    lineChartInstance.resize()
  }
}

onMounted(() => {
  nextTick(async () => {
    await initSectorChart()
    await initLineChart()
    window.addEventListener('resize', handleResize)
  })
})

onUnmounted(() => {
  if (sectorChartInstance) {
    sectorChartInstance.dispose()
  }
  if (lineChartInstance) {
    lineChartInstance.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
</style>