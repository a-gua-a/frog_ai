import axios from 'axios'

// use direct backend calls during development as requested (localhost:8080)
// for production builds we can use an explicit environment variable.
const baseURL = import.meta.env.DEV
  ? 'http://localhost:8080'
  : (import.meta.env.VITE_API_BASE || '')

const api = axios.create({
  baseURL,
  timeout: 60000,
})

// attach token from localStorage on each request
api.interceptors.request.use((config) => {
  const reqUrl = config.url || ''
  const method = (config.method || 'GET').toString().toUpperCase()
  console.debug(`[api] Request start: ${method} ${reqUrl}`, config.headers)

  // do not attach token for login endpoint
  if (reqUrl && reqUrl.includes('/login')) {
    console.debug('[api] Skipping token attach for login request')
    return config
  }

  let token = localStorage.getItem('token')
  // guard against storing the magic failure string from backend
  if (token) {
    const invalid = token.toUpperCase() === 'FAILED' || token.trim() === ''
    if (invalid) {
      localStorage.removeItem('token')
      token = ''
      console.debug('[api] Removed invalid token from localStorage')
    }
  }

  if (token) {
    config.headers = config.headers || {}
    // send both headers to satisfy whatever backend looks for
    config.headers['authentication'] = token
    config.headers['Authorization'] = `Bearer ${token}`
    console.debug('[api] Attached token to headers (authentication + Authorization)')
    console.debug('[api] Final request headers:', config.headers)
  } else {
    console.debug('[api] No token to attach')
  }

  return config
})

// handle 401 globally
api.interceptors.response.use(
  (res) => res,
  async (err) => {
    if (err?.response?.status === 401) {
      localStorage.removeItem('token')
      try {
        const { useAuthStore } = await import('@/stores/auth')
        const auth = useAuthStore()
        auth.logout()
      } catch (_) {
        // fallback: redirect manually
        window.location.href = '/login'
      }
    }
    return Promise.reject(err)
  }
)

export default api
