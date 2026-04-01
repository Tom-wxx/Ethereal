<template>
  <div class="moment-audit">
    <el-table :data="moments" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="nickname" label="发布者" width="120" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.auditStatus === 0 ? 'warning' : row.auditStatus === 1 ? 'success' : 'danger'" size="small">
            {{ ['待审核', '已通过', '已拒绝'][row.auditStatus] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="发布时间" width="160" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button text type="success" size="small" @click="row.auditStatus = 1">通过</el-button>
          <el-button text type="danger" size="small" @click="row.auditStatus = 2">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const moments = ref([
  { id: 1, nickname: 'Chen Wei', content: 'Found this hidden rooftop cafe...', auditStatus: 0, createdAt: '2026-03-31 10:00' },
  { id: 2, nickname: 'Lina', content: 'Early morning run by the river...', auditStatus: 0, createdAt: '2026-03-31 11:00' },
  { id: 3, nickname: 'Marcus', content: 'New exhibition at West Bund Museum...', auditStatus: 1, createdAt: '2026-03-30 09:00' },
])
</script>

<style scoped>
.moment-audit { background: #fff; border-radius: 12px; padding: 20px; }
</style>
