<template>
  <div class="stats-view">
    <el-row :gutter="16">
      <el-col :span="12">
        <div class="chart-card">
          <h3>用户增长</h3>
          <div ref="growthRef" style="height: 300px;"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <h3>匹配率趋势</h3>
          <div ref="matchRef" style="height: 300px;"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const growthRef = ref<HTMLElement>()
const matchRef = ref<HTMLElement>()

onMounted(() => {
  const days = Array.from({ length: 7 }, (_, i) => { const d = new Date(); d.setDate(d.getDate() - 6 + i); return `${d.getMonth()+1}/${d.getDate()}` })

  if (growthRef.value) {
    echarts.init(growthRef.value).setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: days },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: [85, 92, 78, 105, 120, 110, 128], itemStyle: { color: '#FF4D6D', borderRadius: [6, 6, 0, 0] } }]
    })
  }

  if (matchRef.value) {
    echarts.init(matchRef.value).setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: days },
      yAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
      series: [{ type: 'line', smooth: true, data: [32, 35, 38, 36, 42, 45, 48], itemStyle: { color: '#FF9F1C' }, areaStyle: { color: 'rgba(255,159,28,0.1)' } }]
    })
  }
})
</script>

<style scoped>
.chart-card { background: #fff; border-radius: 12px; padding: 20px; }
.chart-card h3 { margin: 0 0 16px; font-size: 16px; }
</style>
