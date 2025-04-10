<template>
  <div class="max-w-md mx-auto text-center py-12">
    <div class="bg-white shadow rounded-lg p-8">
      <div v-if="loading" class="flex flex-col items-center">
        <div class="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-500 mb-4"></div>
        <h2 class="text-xl font-semibold text-gray-900">Processing Authentication</h2>
        <p class="text-gray-600 mt-2">Please wait while we complete your authentication...</p>
      </div>
      
      <div v-else-if="error" class="text-center">
        <div class="text-red-500 mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <h2 class="text-xl font-semibold text-gray-900">Authentication Failed</h2>
        <p class="text-gray-600 mt-2">{{ error }}</p>
        <div class="mt-6">
          <router-link to="/login" class="btn btn-primary">
            Try Again
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import oauthService from '@/services/oauth.service'

const route = useRoute()
const authStore = useAuthStore()

const loading = ref(true)
const error = ref<string | null>(null)

onMounted(async () => {
  try {
    // Extract parameters from URL
    const { code, state, provider } = oauthService.extractAuthParams()
    
    // Validate parameters
    if (!code) {
      error.value = 'Authorization code is missing'
      loading.value = false
      return
    }
    
    if (!state || !oauthService.verifyState(state)) {
      error.value = 'Invalid state parameter. This could be a CSRF attack.'
      loading.value = false
      return
    }
    
    if (!provider) {
      error.value = 'Provider information is missing'
      loading.value = false
      return
    }
    
    // Determine the redirect URI based on the provider
    const redirectUri = `${window.location.origin}/oauth/callback/${provider}`
    
    // Process the OAuth callback
    await authStore.handleOAuthCallback({
      code,
      provider,
      redirectUri
    })
    
  } catch (err: any) {
    console.error('OAuth callback error:', err)
    error.value = err.message || 'Authentication failed. Please try again.'
  } finally {
    loading.value = false
  }
})
</script>
