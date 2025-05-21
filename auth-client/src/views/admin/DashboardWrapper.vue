<template>
  <div>
    <!-- Welcome message for all authenticated users -->
    <div class="bg-white shadow rounded-lg p-6 mb-8">
      <div class="flex items-center mb-4">
        <div class="p-3 rounded-full bg-blue-100 text-blue-600 mr-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <h2 class="text-xl font-semibold text-gray-800">Welcome to the Admin Dashboard</h2>
      </div>
      <p class="text-gray-600">
        This dashboard provides access to various administrative functions. The sections you can view depend on your permissions.
        If you need access to additional features, please contact your system administrator.
      </p>
    </div>
    
    <!-- Render the original dashboard component -->
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  // Only check if user is authenticated, no permission check
  if (!authStore.isAuthenticated) {
    router.push('/login')
  }
})
</script>
