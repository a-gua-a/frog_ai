/// <reference types="vite/client" />

import { ComponentCustomProperties } from 'vue'
import { AxiosInstance } from 'axios'

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $api: AxiosInstance
  }
}
