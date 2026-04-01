<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :span="6" v-for="stat in statCards" :key="stat.label">
        <div class="stat-card" :style="{ borderLeftColor: stat.color }">
          <div class="stat-icon" :style="{ background: stat.color + '18', color: stat.color }">
            <el-icon :size="22"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div
            v-if="stat.trend !== null"
            class="stat-trend"
            :class="stat.trend > 0 ? 'up' : stat.trend < 0 ? 'down' : ''"
          >
            <el-icon v-if="stat.trend > 0"><Top /></el-icon>
            <el-icon v-else-if="stat.trend < 0"><Bottom /></el-icon>
            {{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表 + 最近注册 -->
    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="16">
        <div class="chart-card">
          <div class="chart-header">
            <h3>用户增长趋势</h3>
            <span class="chart-sub">最近 14 天</span>
          </div>
          <div ref="growthChartRef" style="height: 320px;"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-header">
            <h3>最近注册用户</h3>
          </div>
          <el-skeleton :rows="5" animated v-if="recentUsersLoading" />
          <div v-else class="recent-users">
            <div
              v-for="user in recentUsers"
              :key="user.id"
              class="recent-user-item"
            >
              <el-avatar :size="38" :src="user.avatar">
                {{ user.nickname?.charAt(0) }}
              </el-avatar>
              <div class="user-info">
                <span class="name">{{ user.nickname }}</span>
                <span class="meta">{{ user.city || '未知城市' }} · {{ formatTime(user.createdAt) }}</span>
              </div>
            </div>
            <el-empty v-if="recentUsers.length === 0" description="暂无新注册用户" :image-size="80" />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { Top, Bottom, User, Star, ChatDotSquare, Document } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getOverviewStats, getUserGrowthTrend, getRecentUsers } from '@/api/stats'

const growthChartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

interface OverviewData {
  todayNewUsers: number
  todayNewUsersTrend: number
  totalUsers: number
  todayActiveUsers: number
  todayActiveUsersTrend: number
  todayMatches: number
  todayMatchesTrend: number
  pendingAudit: number
}

const overview = ref<OverviewData>({
  todayNewUsers: 0,
  todayNewUsersTrend: 0,
  totalUsers: 0,
  todayActiveUsers: 0,
  todayActiveUsersTrend: 0,
  todayMatches: 0,
  todayMatchesTrend: 0,
  pendingAudit: 0,
})

const recentUsers = ref<any[]>([])
const recentUsersLoading = ref(true)

const statCards = computed(() => [
  {
    label: '今日新增用户',
    value: formatNum(overview.value.todayNewUsers),
    color: '#FF4D6D',
    trend: overview.value.todayNewUsersTrend,
    icon: User,
  },
  {
    label: '今日活跃用户',
    value: formatNum(overview.value.todayActiveUsers),
    color: '#FF9F1C',
    trend: overview.value.todayActiveUsersTrend,
    icon: Star,
  },
  {
    label: '今日新匹配',
    value: formatNum(overview.value.todayMatches),
    color: '#FF8FA3',
    trend: overview.value.todayMatchesTrend,
    icon: ChatDotSquare,
  },
  {
    label: '待审核内容',
    value: formatNum(overview.value.pendingAudit),
    color: '#757778',
    trend: null,
    icon: Document,
  },
])

function formatNum(n: number): string {
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  return n.toLocaleString()
}

function formatTime(dt: string): string {
  if (!dt) return ''
  const d = new Date(dt)
  const now = Date.now()
  const diff = now - d.getTime()
  if (diff < 60_000) return '刚刚'
  if (diff < 3600_000) return Math.floor(diff / 60_000) + '分钟前'
  if (diff < 86400_000) return Math.floor(diff / 3600_000) + '小时前'
  if (diff < 172800_000) return '昨天'
  return `${d.getMonth() + 1}/${d.getDate()}`
}

async function loadOverview() {
  try {
    const data = (await getOverviewStats()) as unknown as OverviewData
    overview.value = data
  } catch { /* card stays 0 */ }
}

async function loadRecentUsers() {
  recentUsersLoading.value = true
  try {
    const data = (await getRecentUsers(5)) as unknown as any[]
    recentUsers.value = data
  } catch { /* empty */ }
  recentUsersLoading.value = false
}

async function loadGrowthChart() {
  try {
    const data = (await getUserGrowthTrend()) as unknown as { date: string; count: number }[]
    if (!growthChartRef.value) return
    chartInstance = echarts.init(growthChartRef.value)
    const dates = data.map(d => {
      const parts = d.date.split('-')
      return `${parseInt(parts[1])}/${parseInt(parts[2])}`
    })
    const counts = data.map(d => d.count)

    chartInstance.setOption({
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255,255,255,0.96)',
        borderColor: '#eee',
        textStyle: { color: '#333' },
      },
      grid: { left: 45, right: 20, top: 20, bottom: 30 },
      xAxis: {
        type: 'category',
        data: dates,
        axisLine: { lineStyle: { color: '#e0e0e0' } },
        axisLabel: { color: '#999' },
      },
      yAxis: {
        type: 'value',
        minInterval: 1,
        axisLine: { show: false },
        splitLine: { lineStyle: { color: '#f5f5f5' } },
        axisLabel: { color: '#999' },
      },
      series: [
        {
          name: '新增用户',
          type: 'line',
          smooth: true,
          data: counts,
          itemStyle: { color: '#FF4D6D' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(255,77,109,0.3)' },
              { offset: 1, color: 'rgba(255,77,109,0.02)' },
            ]),
          },
        },
      ],
    })
  } catch { /* chart stays empty */ }
}

function handleResize() {
  chartInstance?.resize()
}

onMounted(() => {
  loadOverview()
  loadRecentUsers()
  loadGrowthChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style lang="scss" scoped>
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border-left: 4px solid;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: flex-start;
  gap: 14px;
  min-height: 100px;

  .stat-icon {
    width: 44px;
    height: 44px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .stat-body {
    flex: 1;
    min-width: 0;
  }

  .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #333;
    line-height: 1.2;
  }

  .stat-label {
    font-size: 13px;
    color: #999;
    margin-top: 4px;
  }

  .stat-trend {
    font-size: 13px;
    font-weight: 600;
    margin-top: 4px;
    display: flex;
    align-items: center;
    gap: 2px;
    white-space: nowrap;

    &.up { color: #2e7d32; }
    &.down { color: #d32f2f; }
  }
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  height: 400px;
  display: flex;
  flex-direction: column;

  .chart-header {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 16px;

    h3 {
      margin: 0;
      font-size: 16px;
      color: #333;
    }

    .chart-sub {
      font-size: 12px;
      color: #bbb;
    }
  }
}

.recent-users {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
  overflow-y: auto;
}

.recent-user-item {
  display: flex;
  align-items: center;
  gap: 10px;

  .user-info {
    display: flex;
    flex-direction: column;
    min-width: 0;

    .name {
      font-size: 14px;
      font-weight: 500;
      color: #333;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .meta {
      font-size: 12px;
      color: #999;
      margin-top: 2px;
    }
  }
}
</style>
