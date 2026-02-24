<script setup lang="ts">
import { RouterLink, RouterView, useRoute } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
// import image as URL via Vite ?url to get resolved path
import bg from './img/Background.PNG?url'

const auth = useAuthStore()
const route = useRoute()
// highlight active menu item based on route name
const activeIndex = computed(() => route.name || 'home')

function logout() {
  auth.logout()
}
</script>

<template>
  <div class="common-layout" :style="{ backgroundImage: `url(${bg})` }">
    <el-container>
      <el-header>
        <el-menu mode="horizontal" :default-active="activeIndex">
          <el-menu-item index="home">
            <RouterLink to="/">简介</RouterLink>
          </el-menu-item>
          <el-menu-item index="chat">
            <RouterLink to="/chat">聊天</RouterLink>
          </el-menu-item>
          <el-menu-item index="about">
            <RouterLink to="/about">continue</RouterLink>
          </el-menu-item>
          <el-menu-item index="login" v-if="!auth.isAuthenticated">
            <RouterLink to="/login">登录</RouterLink>
          </el-menu-item>
          <el-menu-item index="logout" v-else @click="logout">
            登出
          </el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <RouterView />
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.common-layout {
  min-height: 100vh;
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
}

/* Make Element Plus containers transparent. Use deep selector so child components are affected. */
:deep(.el-container),
:deep(.el-header),
:deep(.el-main),
:deep(.el-menu),
:deep(.el-menu-item),
:deep(.el-input),
:deep(.el-button) {
  background: transparent !important;
}

/* Ensure router views and inner panels are transparent by default */
:deep(.view-wrapper),
:deep(.chat-container),
:deep(.message-list) {
  background: transparent !important;
}

</style>
