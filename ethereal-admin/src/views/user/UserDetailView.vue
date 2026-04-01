<template>
  <div class="user-detail">
    <el-page-header @back="$router.back()" content="用户详情" />
    <el-descriptions :column="2" border style="margin-top: 20px;">
      <el-descriptions-item label="用户ID">{{ route.params.id }}</el-descriptions-item>
      <el-descriptions-item label="昵称">加载中...</el-descriptions-item>
      <el-descriptions-item label="手机号">-</el-descriptions-item>
      <el-descriptions-item label="城市">-</el-descriptions-item>
      <el-descriptions-item label="认证状态">
        <el-tag type="info">未认证</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="账号状态">
        <el-tag type="success">正常</el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<style scoped>
.user-detail { background: #fff; border-radius: 12px; padding: 20px; }
</style>
