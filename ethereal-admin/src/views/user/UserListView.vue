<template>
  <div class="user-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="搜索手机号/昵称" clearable style="width: 240px;" />
      <el-select v-model="searchForm.status" placeholder="用户状态" clearable style="width: 140px;">
        <el-option label="正常" :value="1" />
        <el-option label="封禁" :value="2" />
      </el-select>
      <el-select v-model="searchForm.verified" placeholder="认证状态" clearable style="width: 140px;">
        <el-option label="已认证" :value="1" />
        <el-option label="未认证" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 用户表格 -->
    <el-table :data="tableData" stripe style="width: 100%;" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="头像" width="70">
        <template #default="{ row }">
          <el-avatar :size="36">{{ row.nickname?.charAt(0) || '?' }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="性别" width="70">
        <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}</template>
      </el-table-column>
      <el-table-column prop="city" label="城市" width="100" />
      <el-table-column label="认证" width="80">
        <template #default="{ row }">
          <el-tag :type="row.verified ? 'success' : 'info'" size="small">{{ row.verified ? '已认证' : '未认证' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '封禁' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="160" />
      <el-table-column label="操作" fixed="right" width="180">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="viewDetail(row)">详情</el-button>
          <el-button text :type="row.status === 1 ? 'danger' : 'success'" size="small" @click="toggleBan(row)">
            {{ row.status === 1 ? '封禁' : '解封' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const searchForm = reactive({ keyword: '', status: undefined as number | undefined, verified: undefined as number | undefined })

const tableData = ref([
  { id: 1, nickname: 'Mei Chen', phone: '13800138001', gender: 2, city: 'Shanghai', verified: 1, status: 1, createdAt: '2026-03-28 10:00' },
  { id: 2, nickname: '林娜', phone: '13800138002', gender: 2, city: 'Shanghai', verified: 1, status: 1, createdAt: '2026-03-28 11:00' },
  { id: 3, nickname: 'Chen Wei', phone: '13800138003', gender: 1, city: 'Shanghai', verified: 0, status: 1, createdAt: '2026-03-29 09:00' },
  { id: 4, nickname: '张真', phone: '13800138004', gender: 1, city: 'Shanghai', verified: 1, status: 2, createdAt: '2026-03-29 14:00' },
  { id: 5, nickname: 'Chloe Wang', phone: '13800138005', gender: 2, city: 'Shanghai', verified: 0, status: 1, createdAt: '2026-03-30 08:00' },
])
total.value = tableData.value.length

function loadData() { /* API call */ }

function handleSearch() {
  page.value = 1
  loadData()
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.status = undefined
  searchForm.verified = undefined
  handleSearch()
}

function viewDetail(row: any) {
  router.push(`/users/${row.id}`)
}

async function toggleBan(row: any) {
  const action = row.status === 1 ? '封禁' : '解封'
  await ElMessageBox.confirm(`确定要${action}用户 ${row.nickname} 吗？`, '提示', { type: 'warning' })
  row.status = row.status === 1 ? 2 : 1
  ElMessage.success(`${action}成功`)
}
</script>

<style lang="scss" scoped>
.user-list {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
