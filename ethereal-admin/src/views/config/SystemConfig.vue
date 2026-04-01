<template>
  <div class="system-config">
    <el-form label-width="160px" style="max-width: 600px;">
      <el-form-item label="每日推荐数量">
        <el-input-number v-model="config.dailyRecommendLimit" :min="1" :max="100" />
      </el-form-item>
      <el-form-item label="验证码有效期(秒)">
        <el-input-number v-model="config.smsCodeExpiry" :min="60" :max="600" :step="30" />
      </el-form-item>
      <el-form-item label="最大上传图片数">
        <el-input-number v-model="config.maxPhotoCount" :min="1" :max="20" />
      </el-form-item>
      <el-form-item label="附近动态范围(km)">
        <el-input-number v-model="config.nearbyRange" :min="1" :max="100" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSave">保存配置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const config = reactive({
  dailyRecommendLimit: 30,
  smsCodeExpiry: 300,
  maxPhotoCount: 9,
  nearbyRange: 10,
})

function handleSave() {
  ElMessage.success('配置保存成功')
}
</script>

<style scoped>
.system-config { background: #fff; border-radius: 12px; padding: 30px; }
</style>
