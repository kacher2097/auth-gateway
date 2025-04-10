<template>
  <div class="max-w-md mx-auto">
    <h1 class="text-3xl font-bold text-center text-gray-900 mb-8">Forgot Password</h1>
    
    <div class="bg-white shadow rounded-lg overflow-hidden">
      <div class="p-6">
        <div v-if="submitted">
          <div class="text-center">
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-green-100 mb-4">
              <svg class="h-6 w-6 text-green-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 class="text-lg font-medium text-gray-900">Check your email</h2>
            <p class="mt-2 text-gray-600">
              We've sent a password reset link to your email address. Please check your inbox and follow the instructions to reset your password.
            </p>
            <div class="mt-6">
              <router-link to="/login" class="btn btn-primary">
                Return to Login
              </router-link>
            </div>
          </div>
        </div>
        
        <form v-else @submit.prevent="handleSubmit" class="space-y-6">
          <ErrorAlert :message="error" title="Password Reset Failed" />
          
          <p class="text-gray-600 mb-4">
            Enter your email address and we'll send you a link to reset your password.
          </p>
          
          <div>
            <label for="email" class="form-label">Email Address</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              class="form-input"
              :class="{ 'border-red-500': errors.email }"
              required
            />
            <p v-if="errors.email" class="form-error">{{ errors.email }}</p>
          </div>
          
          <div>
            <button
              type="submit"
              class="w-full btn btn-primary"
              :disabled="loading"
            >
              <span v-if="loading">Sending...</span>
              <span v-else>Send Reset Link</span>
            </button>
          </div>
          
          <div class="text-center text-sm">
            <router-link to="/login" class="text-blue-600 hover:text-blue-500">
              Back to Login
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import ErrorAlert from '@/components/ui/ErrorAlert.vue'
import authService, { ForgotPasswordRequest } from '@/services/auth.service'
import { ApiError } from '@/utils/errorHandler'

const form = reactive<ForgotPasswordRequest>({
  email: ''
})

const errors = reactive({
  email: ''
})

const loading = ref(false)
const error = ref('')
const submitted = ref(false)

function validateForm() {
  let isValid = true
  
  // Reset errors
  errors.email = ''
  
  if (!form.email) {
    errors.email = 'Email is required'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Please enter a valid email address'
    isValid = false
  }
  
  return isValid
}

async function handleSubmit() {
  if (!validateForm()) return
  
  loading.value = true
  error.value = ''
  
  try {
    await authService.forgotPassword(form)
    submitted.value = true
  } catch (err) {
    if (err instanceof ApiError) {
      error.value = err.message
      
      // Handle specific error codes
      if (err.is('EMAIL_NOT_FOUND')) {
        errors.email = 'No account found with this email address'
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
      error.value = 'Failed to send password reset email. Please try again.'
    }
  } finally {
    loading.value = false
  }
}
</script>
