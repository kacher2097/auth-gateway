import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService, { UserInfo, LoginRequest, RegisterRequest, SocialLoginRequest, OAuthCodeRequest } from '@/services/auth.service'
import router from '@/router'
import { ApiError } from '@/utils/errorHandler'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<UserInfo | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const fieldErrors = ref<Record<string, string> | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // Check if user has specific role
  function hasRole(role: string): boolean {
    return user.value?.role === role
  }

  // Actions
  async function initialize() {
    if (token.value) {
      try {
        loading.value = true
        user.value = await authService.getCurrentUser(token.value)
      } catch (err) {
        console.error('Failed to get user info:', err)
        logout()
      } finally {
        loading.value = false
      }
    }
  }

  async function register(credentials: RegisterRequest) {
    try {
      loading.value = true
      error.value = null
      fieldErrors.value = null
      const response = await authService.register(credentials)
      token.value = response.token
      user.value = response.user
      localStorage.setItem('token', response.token)
      router.push('/')

      // Show success toast
      if (window.toast) {
        window.toast.success('Registration successful! Welcome to AuthenHub.')
      }
    } catch (err: any) {
      if (err instanceof ApiError) {
        error.value = err.message
        fieldErrors.value = err.fieldErrors || null

        // Handle specific error codes
        if (err.is('USERNAME_EXISTS')) {
          error.value = 'Tên đăng nhập đã được sử dụng. Vui lòng chọn tên đăng nhập khác.'
        } else if (err.is('EMAIL_EXISTS')) {
          error.value = 'Email đã được sử dụng. Vui lòng sử dụng email khác.'
        }

        // Show error toast
        if (window.toast) {
          window.toast.error(error.value)
        }
      } else {
        error.value = 'Đăng ký thất bại. Vui lòng thử lại sau.'
        if (window.toast) {
          window.toast.error(error.value)
        }
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  async function login(credentials: LoginRequest) {
    try {
      loading.value = true
      error.value = null
      fieldErrors.value = null
      const response = await authService.login(credentials)
      token.value = response.token
      user.value = response.user
      localStorage.setItem('token', response.token)
      router.push('/')

      // Show success toast
      if (window.toast) {
        window.toast.success(`Welcome back, ${response.user.fullName}!`)
      }
    } catch (err: any) {
      if (err instanceof ApiError) {
        error.value = err.message
        fieldErrors.value = err.fieldErrors || null

        // Show error toast
        if (window.toast) {
          window.toast.error(error.value)
        }
      } else {
        error.value = 'Đăng nhập thất bại. Vui lòng kiểm tra thông tin đăng nhập.'
        if (window.toast) {
          window.toast.error(error.value)
        }
      }
      throw err
    } finally {
      loading.value = false
    }
  }

  async function socialLogin(data: SocialLoginRequest) {
    try {
      loading.value = true
      error.value = null
      const response = await authService.socialLogin(data)
      token.value = response.token
      user.value = response.user
      localStorage.setItem('token', response.token)
      router.push('/')
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Social login failed'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function handleOAuthCallback(data: OAuthCodeRequest) {
    try {
      loading.value = true
      error.value = null
      const response = await authService.oauthCallback(data)
      token.value = response.token
      user.value = response.user
      localStorage.setItem('token', response.token)
      router.push('/')
    } catch (err: any) {
      error.value = err.response?.data?.message || 'OAuth authentication failed'
      router.push('/login?error=oauth_failed')
      throw err
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    router.push('/login')

    // Show logout toast
    if (window.toast) {
      window.toast.info('You have been logged out successfully')
    }
  }

  return {
    token,
    user,
    loading,
    error,
    fieldErrors,
    isAuthenticated,
    isAdmin,
    hasRole,
    initialize,
    register,
    login,
    socialLogin,
    handleOAuthCallback,
    logout
  }
})
