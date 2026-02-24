import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  server: {
    // if your backend runs on a different port/origin during development,
    // uncomment and adapt the proxy rules below
     proxy: {
       '/chat': {
         target: 'http://localhost:8080',
         changeOrigin: true,
       },
       '/audio': {
         target: 'http://localhost:8080',
         changeOrigin: true,
       },
     },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
