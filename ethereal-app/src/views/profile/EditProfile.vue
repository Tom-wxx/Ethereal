<template>
  <div class="edit-profile">
    <EtherealNavBar title="编辑资料" :show-back="true">
      <template #right>
        <span class="save-btn" @click="handleSave">保存</span>
      </template>
    </EtherealNavBar>

    <div class="form-section">
      <van-cell-group inset>
        <van-field v-model="form.nickname" label="昵称" placeholder="请输入昵称" />
        <van-field v-model="form.bio" label="简介" type="textarea" placeholder="介绍一下自己" rows="3" autosize />
        <van-field v-model="form.profession" label="职业" placeholder="你的职业" />
        <van-field v-model="form.city" label="城市" placeholder="所在城市" />
      </van-cell-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import EtherealNavBar from '@/components/common/EtherealNavBar.vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  nickname: userStore.userInfo?.nickname || '',
  bio: userStore.userInfo?.bio || '',
  profession: userStore.userInfo?.profession || '',
  city: userStore.userInfo?.city || '',
})

async function handleSave() {
  try {
    await userStore.updateProfile(form.value)
    showToast('保存成功')
    router.back()
  } catch (e: any) {
    showToast(e.message || '保存失败')
  }
}
</script>

<style lang="scss" scoped>
.edit-profile {
  min-height: 100vh;
  background: #f5f6f7;
}

.form-section {
  padding: 16px 0;
}

.save-btn {
  color: #FF4D6D;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
}
</style>
