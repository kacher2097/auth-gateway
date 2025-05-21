import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

// API base URL
const BASE_URL = 'http://localhost:8118'

// Create axios instance
const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true // Include cookies in cross-site requests
})

// Add request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Add response interceptor to handle token expiration
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // If the error is 401 (Unauthorized) and we haven't tried to refresh the token yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      const authStore = useAuthStore()

      // Try to refresh the token
      const refreshSuccess = await authStore.refreshAccessToken()

      if (refreshSuccess) {
        // Update the Authorization header with the new token
        originalRequest.headers.Authorization = `Bearer ${authStore.token}`
        // Retry the original request
        return api(originalRequest)
      } else {
        // If refresh failed, logout and redirect to login page
        authStore.logout()
      }
    }

    return Promise.reject(error)
  }
)

export default api
