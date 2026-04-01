<template>
  <div class="admin-login">
    <div class="login-card">
      <div class="login-header">
        <h1 class="brand">Ethereal</h1>
        <p>管理后台</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const adminStore = useAdminStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  try {
    await formRef.value?.validate()
  } catch { return }

  loading.value = true
  try {
    await adminStore.login({ username: form.username, password: form.password })
    await nextTick()
    ElMessage.success('登录成功')
    await router.replace({ path: '/dashboard' })
  } catch (e: any) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.admin-login {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
}

.login-card {
  width: 400px;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  .brand {
    margin: 0;
    font-size: 32px;
    font-weight: 800;
    background: linear-gradient(135deg, #FF4D6D, #FF9F1C);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    font-family: 'Plus Jakarta Sans', sans-serif;
  }

  p {
    color: #999;
    margin-top: 4px;
  }
}

.login-btn {
  width: 100%;
  background: linear-gradient(135deg, #FF4D6D, #ff7386);
  border: none;
  font-weight: 600;

  &:hover {
    background: linear-gradient(135deg, #e63e5c, #ff6070);
  }
}
</style>
