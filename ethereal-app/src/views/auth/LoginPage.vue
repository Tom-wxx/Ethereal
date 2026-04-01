<template>
  <div class="login-page">
    <!-- 装饰背景 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-content">
      <!-- 品牌区 -->
      <div class="brand-section">
        <h1 class="brand-name">Ethereal</h1>
        <p class="brand-slogan">发现属于你的灵魂共鸣</p>
      </div>

      <!-- 登录表单 -->
      <div class="login-form">
        <!-- 手机号输入 -->
        <div class="input-group">
          <div class="country-code" @click="showCountryPicker = true">
            <span>+86</span>
            <van-icon name="arrow-down" size="12" />
          </div>
          <input
            v-model="phone"
            type="tel"
            maxlength="11"
            placeholder="请输入手机号"
            class="phone-input"
          />
        </div>

        <!-- 验证码输入 -->
        <div class="input-group">
          <input
            v-model="code"
            type="text"
            maxlength="6"
            placeholder="请输入验证码"
            class="code-input"
          />
          <button
            class="send-code-btn"
            :disabled="isCounting"
            @click="handleSendCode"
          >
            {{ isCounting ? `${countdown}s` : '发送验证码' }}
          </button>
        </div>

        <!-- 登录按钮 -->
        <button class="login-btn" :disabled="!canLogin" @click="handleLogin">
          <span>Login / Sign Up</span>
          <van-icon name="arrow" />
        </button>

        <!-- 社交登录 -->
        <div class="social-login">
          <div class="divider">
            <span>其他登录方式</span>
          </div>
          <div class="social-icons">
            <div class="social-icon" @click="handleWxLogin">
              <van-icon name="chat-o" size="24" />
            </div>
            <div class="social-icon">
              <van-icon name="contact" size="24" />
            </div>
          </div>
        </div>
      </div>

      <!-- 用户协议 -->
      <div class="agreement" @click="agreed = !agreed">
        <div class="checkbox" :class="{ checked: agreed }">
          <van-icon v-if="agreed" name="success" size="12" color="#fff" />
        </div>
        <span class="agreement-text">
          我已阅读并同意
          <a>服务协议</a>、<a>隐私政策</a>
          以及<a>儿童隐私保护指引</a>。未注册手机号将自动创建账号。
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { showToast } from 'vant'
import { useCountdown } from '@/composables/useCountdown'
import { useAuthStore } from '@/stores/auth'
import { sendSmsCode } from '@/api/auth'

const phone = ref('')
const code = ref('')
const agreed = ref(true)
const showCountryPicker = ref(false)
const { countdown, isCounting, start: startCountdown } = useCountdown(60)
const authStore = useAuthStore()

const canLogin = computed(() => phone.value.length === 11 && code.value.length >= 4 && agreed.value)

async function handleSendCode() {
  if (phone.value.length !== 11) {
    showToast('请输入正确的手机号')
    return
  }
  try {
    await sendSmsCode(phone.value)
    startCountdown()
    showToast('验证码已发送')
  } catch (e: any) {
    showToast(e.message || '发送失败')
  }
}

async function handleLogin() {
  if (!agreed.value) {
    showToast('请先同意用户协议')
    return
  }
  try {
    await authStore.login(phone.value, code.value)
  } catch (e: any) {
    showToast(e.message || '登录失败')
  }
}

function handleWxLogin() {
  showToast('微信登录功能开发中')
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.login-page {
  min-height: 100vh;
  background: $surface;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;

  .circle {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.3;
  }
  .circle-1 {
    width: 300px;
    height: 300px;
    background: $primary;
    top: -100px;
    right: -80px;
  }
  .circle-2 {
    width: 200px;
    height: 200px;
    background: $secondary;
    top: 30%;
    left: -60px;
  }
  .circle-3 {
    width: 250px;
    height: 250px;
    background: $tertiary;
    bottom: -50px;
    right: -30px;
  }
}

.login-content {
  position: relative;
  z-index: 1;
  padding: 0 32px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-section {
  text-align: center;
  margin-bottom: 48px;
}

.brand-name {
  font-family: 'Plus Jakarta Sans', sans-serif;
  font-size: 42px;
  font-weight: 800;
  background: linear-gradient(135deg, $primary, $secondary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  letter-spacing: -1px;
}

.brand-slogan {
  color: $on-surface-variant;
  font-size: 15px;
  margin-top: 8px;
  letter-spacing: 1px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-group {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: $radius-lg;
  padding: 0 16px;
  height: 52px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s;

  &:focus-within {
    background: rgba(255, 255, 255, 0.9);
    border-color: rgba($primary, 0.2);
  }
}

.country-code {
  display: flex;
  align-items: center;
  gap: 4px;
  padding-right: 12px;
  border-right: 1px solid $outline-variant;
  margin-right: 12px;
  font-weight: 600;
  color: $on-surface;
  cursor: pointer;
}

.phone-input, .code-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 16px;
  color: $on-surface;
  font-family: inherit;

  &::placeholder {
    color: $outline;
  }
}

.send-code-btn {
  border: none;
  background: transparent;
  color: $primary;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  cursor: pointer;
  padding: 4px 0;

  &:disabled {
    color: $outline;
  }
}

.login-btn {
  height: 52px;
  border: none;
  border-radius: $radius-xl;
  background: linear-gradient(135deg, $primary, $primary-container);
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  margin-top: 8px;
  transition: opacity 0.3s;
  font-family: 'Plus Jakarta Sans', sans-serif;
  letter-spacing: 0.5px;

  &:disabled {
    opacity: 0.5;
  }

  &:active {
    opacity: 0.8;
  }
}

.social-login {
  margin-top: 24px;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;

  &::before, &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: $outline-variant;
  }

  span {
    padding: 0 16px;
    color: $outline;
    font-size: 13px;
  }
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.social-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: $on-surface-variant;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.3);

  &:active {
    background: rgba(255, 255, 255, 0.9);
  }
}

.agreement {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 32px;
  padding: 0 4px;
  cursor: pointer;
}

.checkbox {
  min-width: 18px;
  height: 18px;
  border-radius: 4px;
  border: 1.5px solid $outline;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 2px;
  transition: all 0.2s;

  &.checked {
    background: $primary;
    border-color: $primary;
  }
}

.agreement-text {
  font-size: 12px;
  color: $on-surface-variant;
  line-height: 1.6;

  a {
    color: $primary;
    text-decoration: none;
  }
}
</style>
