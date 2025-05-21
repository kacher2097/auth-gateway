import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService, { UserInfo, UserInfoResponse, LoginRequest, RegisterRequest, SocialLoginRequest, OAuthCodeRequest } from '@/services/auth.service'
import router from '@/router'
import { ApiError } from '@/utils/errorHandler'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref<string | null>(localStorage.getItem('token'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const user = ref<UserInfo | UserInfoResponse | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const fieldErrors = ref<Record<string, string> | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => {
    // Check if user has admin role or has all admin permissions
    if (user.value?.role === 'ADMIN') return true

    // Check if user has UserInfoResponse with permissions
    const userWithPermissions = user.value as UserInfoResponse
    if (userWithPermissions?.permissions) {
      // Check for admin permission or role:admin permission
      return userWithPermissions.permissions.some(p =>
        p === 'admin' || p === 'role:admin' || p.includes('admin:all')
      )
    }

    return false
  })

  // Get user permissions
  const permissions = computed(() => {
    const userWithPermissions = user.value as UserInfoResponse
    return userWithPermissions?.permissions || []
  })

  // Check if user has specific role
  function hasRole(role: string): boolean {
    return user.value?.role === role
  }

  // Check if user has specific permission
  function hasPermission(permission: string): boolean {
    // Admin role always has all permissions
    if (user.value?.role === 'ADMIN') return true

    // Check if user has UserInfoResponse with permissions
    const userWithPermissions = user.value as UserInfoResponse
    if (userWithPermissions?.permissions) {
      return userWithPermissions.permissions.includes(permission)
    }

    return false
  }

  // Check if user has any of the specified permissions
  function hasAnyPermission(requiredPermissions: string[]): boolean {
    // Admin role always has all permissions
    if (user.value?.role === 'ADMIN') return true

    // If no permissions required, return true
    if (!requiredPermissions || requiredPermissions.length === 0) return true

    // Check if user has UserInfoResponse with permissions
    const userWithPermissions = user.value as UserInfoResponse
    if (userWithPermissions?.permissions) {
      return requiredPermissions.some(permission =>
        userWithPermissions.permissions.includes(permission)
      )
    }

    return false
  }

  // Check if user has all of the specified permissions
  function hasAllPermissions(requiredPermissions: string[]): boolean {
    // Admin role always has all permissions
    if (user.value?.role === 'ADMIN') return true

    // If no permissions required, return true
    if (!requiredPermissions || requiredPermissions.length === 0) return true

    // Check if user has UserInfoResponse with permissions
    const userWithPermissions = user.value as UserInfoResponse
    if (userWithPermissions?.permissions) {
      return requiredPermissions.every(permission =>
        userWithPermissions.permissions.includes(permission)
      )
    }

    return false
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
      if (response.refreshToken) {
        refreshToken.value = response.refreshToken
        localStorage.setItem('refreshToken', response.refreshToken)
      }
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
      if (response.refreshToken) {
        refreshToken.value = response.refreshToken
        localStorage.setItem('refreshToken', response.refreshToken)
      }
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
      if (response.refreshToken) {
        refreshToken.value = response.refreshToken
        localStorage.setItem('refreshToken', response.refreshToken)
      }
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
      if (response.refreshToken) {
        refreshToken.value = response.refreshToken
        localStorage.setItem('refreshToken', response.refreshToken)
      }
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
    refreshToken.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    router.push('/login')

    // Show logout toast
    if (window.toast) {
      window.toast.info('You have been logged out successfully')
    }
  }

  // Add a method to refresh the token when it expires
  async function refreshAccessToken() {
    if (!refreshToken.value) {
      // No refresh token available, force logout
      logout()
      return false
    }

    try {
      loading.value = true
      const response = await authService.refreshToken(refreshToken.value)
      token.value = response.token
      if (response.refreshToken) {
        refreshToken.value = response.refreshToken
        localStorage.setItem('refreshToken', response.refreshToken)
      }
      localStorage.setItem('token', response.token)
      return true
    } catch (err) {
      console.error('Failed to refresh token:', err)
      logout()
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    token,
    refreshToken,
    user,
    loading,
    error,
    fieldErrors,
    isAuthenticated,
    isAdmin,
    permissions,
    hasRole,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    initialize,
    register,
    login,
    socialLogin,
    handleOAuthCallback,
    refreshAccessToken,
    logout
  }
})
