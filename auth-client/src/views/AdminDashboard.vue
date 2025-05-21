<template>
  <AdminLayout title="Dashboard">

    <div v-if="loading" class="flex justify-center">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
    </div>

    <div v-else-if="error" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md mb-6">
      {{ error }}
    </div>

    <!-- Welcome message for all authenticated users -->
    <div v-if="!loading && !error" class="bg-white shadow rounded-lg p-6 mb-8">
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

    <div v-else>
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-blue-100 text-blue-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
            <div class="ml-4">
              <h2 class="text-lg font-semibold text-gray-700">Total Users</h2>
              <p class="text-3xl font-bold text-gray-900">{{ dashboardData.totalUsers }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-purple-100 text-purple-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
            </div>
            <div class="ml-4">
              <h2 class="text-lg font-semibold text-gray-700">Admin Users</h2>
              <p class="text-3xl font-bold text-gray-900">{{ dashboardData.adminUsers }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-green-100 text-green-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656.126-1.283.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656-.126-1.283-.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <div class="ml-4">
              <h2 class="text-lg font-semibold text-gray-700">Regular Users</h2>
              <p class="text-3xl font-bold text-gray-900">{{ dashboardData.regularUsers }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- User List -->
      <div class="bg-white shadow rounded-lg overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-xl font-semibold text-gray-800">User Management</h2>
        </div>

        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Email
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Role
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Login Type
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="user in users" :key="user.id">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10">
                      <div v-if="user.avatar" class="h-10 w-10 rounded-full overflow-hidden">
                        <img :src="user.avatar" alt="User avatar" class="h-full w-full object-cover" />
                      </div>
                      <div v-else class="h-10 w-10 rounded-full bg-blue-500 flex items-center justify-center text-white">
                        {{ user.fullName.charAt(0) }}
                      </div>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">
                        {{ user.fullName }}
                      </div>
                      <div class="text-sm text-gray-500">
                        @{{ user.username }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">{{ user.email }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                        :class="user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'">
                    {{ user.role }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                        :class="user.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'">
                    {{ user.active ? 'Active' : 'Inactive' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ user.socialProvider ? user.socialProvider : 'Email/Password' }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import api from '@/services/api'
import { ApiError } from '@/utils/errorHandler'
import { useAdminPermissions } from '@/composables/useAdminPermissions'

const router = useRouter()
const authStore = useAuthStore()
const { hasPermission, availableSections } = useAdminPermissions()

const loading = ref(true)
const error = ref('')
const users = ref<any[]>([])
const dashboardData = ref({
  totalUsers: 0,
  adminUsers: 0,
  regularUsers: 0
})

// Check if the user has permission to view specific dashboard sections
const canViewUserStats = computed(() => hasPermission('users', 'view'))
const canViewAnalytics = computed(() => hasPermission('analytics', 'view'))
const canViewSettings = computed(() => hasPermission('settings', 'view'))

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  // No permission check - all authenticated users can access the dashboard

  await fetchDashboardData()
  await fetchUsers()
})

async function fetchDashboardData() {
  try {
    loading.value = true
    error.value = ''

    const response = await api.post('/admin/dashboard', {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    dashboardData.value = response.data.data
  } catch (err) {
    if (err instanceof ApiError) {
      error.value = err.message

      // If unauthorized, redirect to home
      if (err.status === 403 || err.status === 401) {
        router.push('/')
      }
    } else {
      error.value = 'Failed to load dashboard data'
    }
  } finally {
    loading.value = false
  }
}

async function fetchUsers() {
  try {
    const response = await api.post('/admin/users', {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    users.value = response.data
  } catch (err) {
    if (err instanceof ApiError) {
      error.value = err.message
    } else {
      error.value = 'Failed to load users'
    }
  }
}
</script>


