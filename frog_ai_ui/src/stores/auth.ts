import { defineStore } from 'pinia'
import router from '@/router'

interface AuthState {
  token: string
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: (localStorage.getItem('token') as string) || '',
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
  actions: {
    setToken(token: string) {
      // `this` is correctly typed as AuthState & Actions
      this.token = token
      console.debug('[auth] setToken saved to store:', token)
      try {
        localStorage.setItem('token', token)
        console.debug('[auth] token persisted to localStorage')
      } catch (e) {
        console.error('[auth] failed to persist token to localStorage', e)
      }
    },
    logout() {
      this.token = ''
      localStorage.removeItem('token')
      console.debug('[auth] token removed from localStorage and store')
      router.push({ name: 'login' })
    },
  },
})
