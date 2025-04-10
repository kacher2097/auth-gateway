<template>
  <div class="max-w-md mx-auto">
    <h1 class="text-3xl font-bold text-center text-gray-900 mb-8">Reset Password</h1>
    
    <div class="bg-white shadow rounded-lg overflow-hidden">
      <div class="p-6">
        <div v-if="submitted">
          <div class="text-center">
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-green-100 mb-4">
              <svg class="h-6 w-6 text-green-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 class="text-lg font-medium text-gray-900">Password Reset Successful</h2>
            <p class="mt-2 text-gray-600">
              Your password has been reset successfully. You can now log in with your new password.
            </p>
            <div class="mt-6">
              <router-link to="/login" class="btn btn-primary">
                Go to Login
              </router-link>
            </div>
          </div>
        </div>
        
        <form v-else @submit.prevent="handleSubmit" class="space-y-6">
          <ErrorAlert :message="error" title="Password Reset Failed" />
          
          <div>
            <label for="newPassword" class="form-label">New Password</label>
            <input
              id="newPassword"
              v-model="form.newPassword"
              type="password"
              class="form-input"
              :class="{ 'border-red-500': errors.newPassword }"
              required
            />
            <p v-if="errors.newPassword" class="form-error">{{ errors.newPassword }}</p>
          </div>
          
          <div>
            <label for="confirmPassword" class="form-label">Confirm Password</label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              class="form-input"
              :class="{ 'border-red-500': errors.confirmPassword }"
              required
            />
            <p v-if="errors.confirmPassword" class="form-error">{{ errors.confirmPassword }}</p>
          </div>
          
          <div>
            <button
              type="submit"
              class="w-full btn btn-primary"
              :disabled="loading"
            >
              <span v-if="loading">Resetting...</span>
              <span v-else>Reset Password</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ErrorAlert from '@/components/ui/ErrorAlert.vue'
import authService, { ResetPasswordRequest } from '@/services/auth.service'
import { ApiError } from '@/utils/errorHandler'

const route = useRoute()
const router = useRouter()

const form = reactive<ResetPasswordRequest>({
  token: '',
  newPassword: '',
  confirmPassword: ''
})

const errors = reactive({
  token: '',
  newPassword: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref('')
const submitted = ref(false)

onMounted(() => {
  // Get token from URL query parameter
  const token = route.query.token as string
  
  if (!token) {
    error.value = 'Invalid or missing reset token. Please request a new password reset link.'
    return
  }
  
  form.token = token
})

function validateForm() {
  let isValid = true
  
  // Reset errors
  errors.token = ''
  errors.newPassword = ''
  errors.confirmPassword = ''
  
  if (!form.token) {
    errors.token = 'Reset token is missing'
    error.value = 'Invalid or missing reset token. Please request a new password reset link.'
    isValid = false
  }
  
  if (!form.newPassword) {
    errors.newPassword = 'New password is required'
    isValid = false
  } else if (form.newPassword.length < 6) {
    errors.newPassword = 'New password must be at least 6 characters'
    isValid = false
  }
  
  if (!form.confirmPassword) {
    errors.confirmPassword = 'Please confirm your new password'
    isValid = false
  } else if (form.newPassword !== form.confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
    isValid = false
  }
  
  return isValid
}

async function handleSubmit() {
  if (!validateForm()) return
  
  loading.value = true
  error.value = ''
  
  try {
    await authService.resetPassword(form)
    submitted.value = true
  } catch (err) {
    if (err instanceof ApiError) {
      error.value = err.message
      
      // Handle specific error codes
      if (err.is('INVALID_TOKEN')) {
        error.value = 'The password reset link is invalid or has expired. Please request a new one.'
      } else if (err.is('PASSWORD_MISMATCH')) {
        errors.confirmPassword = 'Passwords do not match'
      }
      
      // Map field errors if any
      if (err.fieldErrors) {
        Object.entries(err.fieldErrors).forEach(([field, message]) => {
          if (field in errors) {
            errors[field as keyof typeof errors] = message
          }
        })
      }
    } else {
      error.value = 'Failed to reset password. Please try again.'
    }
  } finally {
    loading.value = false
  }
}
</script>
