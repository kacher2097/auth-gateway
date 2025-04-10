<template>
  <div class="bg-white shadow rounded-lg p-6">
    <h2 class="text-xl font-semibold text-gray-900 mb-4">Change Password</h2>
    
    <form @submit.prevent="handleSubmit" class="space-y-4">
      <ErrorAlert :message="error" title="Password Change Failed" />
      <SuccessAlert :message="successMessage" title="Password Changed" />
      
      <div>
        <label for="currentPassword" class="form-label">Current Password</label>
        <input
          id="currentPassword"
          v-model="form.currentPassword"
          type="password"
          class="form-input"
          :class="{ 'border-red-500': errors.currentPassword }"
          required
        />
        <p v-if="errors.currentPassword" class="form-error">{{ errors.currentPassword }}</p>
      </div>
      
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
        <label for="confirmPassword" class="form-label">Confirm New Password</label>
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
      
      <div class="flex justify-end">
        <button
          type="submit"
          class="btn btn-primary"
          :disabled="loading"
        >
          <span v-if="loading">Updating...</span>
          <span v-else>Update Password</span>
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useAuthStore } from '@/stores/auth'
import ErrorAlert from '@/components/ui/ErrorAlert.vue'
import SuccessAlert from '@/components/ui/SuccessAlert.vue'
import authService, { ChangePasswordRequest } from '@/services/auth.service'
import { ApiError } from '@/utils/errorHandler'

const authStore = useAuthStore()

const form = reactive<ChangePasswordRequest>({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const errors = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref('')
const successMessage = ref('')

function validateForm() {
  let isValid = true
  
  // Reset errors
  errors.currentPassword = ''
  errors.newPassword = ''
  errors.confirmPassword = ''
  
  if (!form.currentPassword) {
    errors.currentPassword = 'Current password is required'
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
  successMessage.value = ''
  
  try {
    if (!authStore.token) {
      throw new Error('You must be logged in to change your password')
    }
    
    await authService.changePassword(authStore.token, form)
    
    // Reset form on success
    form.currentPassword = ''
    form.newPassword = ''
    form.confirmPassword = ''
    
    successMessage.value = 'Your password has been updated successfully'
  } catch (err) {
    if (err instanceof ApiError) {
      error.value = err.message
      
      // Handle specific error codes
      if (err.is('INVALID_PASSWORD')) {
        errors.currentPassword = 'Current password is incorrect'
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
      error.value = 'Failed to update password. Please try again.'
    }
  } finally {
    loading.value = false
  }
}
</script>
