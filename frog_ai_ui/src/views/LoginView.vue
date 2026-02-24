<template>
  <div class="login-container">
    <el-form :model="form" ref="formRef" class="login-form">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" autocomplete="username" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          autocomplete="current-password"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLogin">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()

if (auth.isAuthenticated) {
  router.replace({ name: 'chat' })
}

const formRef = ref()
const form = ref({ username: '', password: '' })

async function handleLogin() {
  if (!form.value.username || !form.value.password) {
    return
  }
  try {
    // backend uses GET parameters
    const resp = await api.get(
      '/login',
      {
        params: {
          username: form.value.username,
          password: form.value.password,
        },
      }
    )
    const token = resp.data as string
    console.debug('[login] received token from server:', token)
    const cleanToken = token ? token.toString().trim() : ''
    const isFailure = !cleanToken || cleanToken.toUpperCase() === 'FAILED'
    const looksLikeJWT = cleanToken.split('.').length === 3

    if (!isFailure && looksLikeJWT) {
      auth.setToken(cleanToken)
      router.push({ name: 'chat' })
    } else {
      console.debug('[login] token invalid or login failed, not storing token')
      ElMessage.error('用户名或密码错误')
    }
  } catch (err) {
    console.error('login failed', err)
    ElMessage.error('登录请求失败')
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 2rem auto;
}
.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
</style>
