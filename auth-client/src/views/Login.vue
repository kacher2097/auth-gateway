<template>
  <div class="max-w-md mx-auto">
    <h1 class="text-3xl font-bold text-center text-gray-900 mb-8">Login to Your Account</h1>

    <div class="bg-white shadow rounded-lg overflow-hidden">
      <div class="p-6">
        <form @submit.prevent="handleSubmit" class="space-y-6">
          <ErrorAlert :message="error" title="Đăng nhập thất bại" />

          <div>
            <label for="username" class="form-label">Username</label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              class="form-input"
              :class="{ 'border-red-500': errors.username }"
              required
            />
            <p v-if="errors.username" class="form-error">{{ errors.username }}</p>
          </div>

          <div>
            <div class="flex justify-between items-center">
              <label for="password" class="form-label">Password</label>
              <router-link to="/forgot-password" class="text-sm text-blue-600 hover:text-blue-500">
                Forgot password?
              </router-link>
            </div>
            <input
              id="password"
              v-model="form.password"
              type="password"
              class="form-input"
              :class="{ 'border-red-500': errors.password }"
              required
            />
            <p v-if="errors.password" class="form-error">{{ errors.password }}</p>
          </div>

          <div>
            <button
              type="submit"
              class="w-full btn btn-primary"
              :disabled="loading"
            >
              <span v-if="loading">Logging in...</span>
              <span v-else>Login</span>
            </button>
          </div>
        </form>

        <div class="mt-6">
          <div class="relative">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-gray-300"></div>
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-2 bg-white text-gray-500">Or continue with</span>
            </div>
          </div>

          <div class="mt-6 grid grid-cols-2 gap-3">
            <button
              type="button"
              class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              @click="handleSocialLogin('GOOGLE')"
              :disabled="loading"
            >
              <svg class="h-5 w-5 text-red-500" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12.545,10.239v3.821h5.445c-0.712,2.315-2.647,3.972-5.445,3.972c-3.332,0-6.033-2.701-6.033-6.032s2.701-6.032,6.033-6.032c1.498,0,2.866,0.549,3.921,1.453l2.814-2.814C17.503,2.988,15.139,2,12.545,2C7.021,2,2.543,6.477,2.543,12s4.478,10,10.002,10c8.396,0,10.249-7.85,9.426-11.748L12.545,10.239z"/>
              </svg>
              <span class="ml-2">Google</span>
            </button>

            <button
              type="button"
              class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50"
              @click="handleSocialLogin('FACEBOOK')"
              :disabled="loading"
            >
              <svg class="h-5 w-5 text-blue-600" fill="currentColor" viewBox="0 0 24 24">
                <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
              </svg>
              <span class="ml-2">Facebook</span>
            </button>
          </div>
        </div>

        <div class="mt-6 text-center text-sm">
          <p class="text-gray-600">
            Don't have an account?
            <router-link to="/register" class="text-blue-600 hover:text-blue-500">
              Register now
            </router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRoute, useRouter } from 'vue-router'
import oauthService from '@/services/oauth.service'
import ErrorAlert from '@/components/ui/ErrorAlert.vue'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

const loading = ref(false)
// Get error state from auth store
const error = computed(() => authStore.error)
const apiFieldErrors = computed(() => authStore.fieldErrors)

// Get redirect path from query params if available
const redirectPath = computed(() => route.query.redirect?.toString() || '/')

// Check for OAuth error
onMounted(() => {
  if (route.query.error === 'oauth_failed') {
    error.value = 'Social login failed. Please try again or use email/password.'
  }
})

function validateForm() {
  let isValid = true

  // Reset errors
  errors.username = ''
  errors.password = ''

  if (!form.username) {
    errors.username = 'Username is required'
    isValid = false
  }

  if (!form.password) {
    errors.password = 'Password is required'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'Password must be at least 6 characters'
    isValid = false
  }

  return isValid
}

async function handleSubmit() {
  if (!validateForm()) return

  loading.value = true

  try {
    await authStore.login({
      username: form.username,
      password: form.password
    })
    router.push(redirectPath.value)
  } catch (err: any) {
    // Error is already handled in the auth store
    // Check for field-specific errors from API
    if (apiFieldErrors.value) {
      Object.entries(apiFieldErrors.value).forEach(([field, message]) => {
        if (field in errors) {
          errors[field as keyof typeof errors] = message
        }
      })
    }
  } finally {
    loading.value = false
  }
}

function handleSocialLogin(provider: 'GOOGLE' | 'FACEBOOK') {
  // Disable the form while we're redirecting
  loading.value = true
  error.value = ''

  // Initiate the OAuth flow by redirecting to the provider
  if (provider === 'GOOGLE') {
    oauthService.loginWithGoogle()
  } else if (provider === 'FACEBOOK') {
    oauthService.loginWithFacebook()
  }
}
</script>
