<template>
  <AdminLayout title="Analytics Dashboard">
    <template #actions>
      <div class="flex items-center space-x-4">
        <div class="relative">
          <select
            v-model="timeRange"
            class="appearance-none w-40 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            @change="fetchAnalytics"
          >
            <option value="7">Last 7 days</option>
            <option value="30">Last 30 days</option>
            <option value="90">Last 90 days</option>
            <option value="365">Last year</option>
          </select>
          <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
            <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 9l4-4 4 4m0 6l-4 4-4-4" />
            </svg>
          </div>
        </div>
        <button
          @click="fetchAnalytics"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          <svg class="h-4 w-4 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          Refresh
        </button>
      </div>
    </template>

    <div v-if="loading" class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
    </div>

    <div v-else-if="error" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md mb-6">
      {{ error }}
    </div>

    <div v-else class="space-y-6">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-blue-100 text-blue-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
            </div>
            <div class="ml-4">
              <h2 class="text-lg font-semibold text-gray-700">Total Visits</h2>
              <p class="text-3xl font-bold text-gray-900">{{ analytics.totalVisits || 0 }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-green-100 text-green-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
            <div class="ml-4">
              <h2 class="text-lg font-semibold text-gray-700">Total Users</h2>
              <p class="text-3xl font-bold text-gray-900">{{ dashboardData.totalUsers || 0 }}</p>
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
              <p class="text-3xl font-bold text-gray-900">{{ dashboardData.adminUsers || 0 }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white shadow rounded-lg p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-yellow-100 text-yellow-600">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
              </svg>
            </div>
            <div class="ml-4">
              <h2 class="text-lg font-semibold text-gray-700">Login Rate</h2>
              <p class="text-3xl font-bold text-gray-900">{{ loginRate }}%</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Charts -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Daily Visits Chart -->
        <div class="bg-white shadow rounded-lg p-6">
          <h3 class="text-lg font-semibold text-gray-700 mb-4">Daily Visits</h3>
          <div class="h-64">
            <!-- Placeholder for chart - in a real app, you'd use a charting library like Chart.js -->
            <div class="h-full flex items-center justify-center bg-gray-50 rounded-lg">
              <p class="text-gray-500">Daily visits chart would be displayed here</p>
            </div>
          </div>
        </div>

        <!-- Device Type Chart -->
        <div class="bg-white shadow rounded-lg p-6">
          <h3 class="text-lg font-semibold text-gray-700 mb-4">Device Types</h3>
          <div class="h-64">
            <!-- Placeholder for chart -->
            <div class="h-full flex items-center justify-center bg-gray-50 rounded-lg">
              <p class="text-gray-500">Device type distribution chart would be displayed here</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Top Users and Endpoints -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Top Users -->
        <div class="bg-white shadow rounded-lg overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-semibold text-gray-700">Top Users</h3>
          </div>
          <div class="p-6">
            <div v-if="!analytics.topUsers || analytics.topUsers.length === 0" class="text-center py-4">
              <p class="text-gray-500">No user activity data available</p>
            </div>
            <ul v-else class="divide-y divide-gray-200">
              <li v-for="(user, index) in analytics.topUsers" :key="index" class="py-3 flex justify-between">
                <div class="flex items-center">
                  <span class="text-gray-900 font-medium">{{ user.username || 'Unknown' }}</span>
                </div>
                <div class="text-gray-500">
                  {{ user.count }} visits
                </div>
              </li>
            </ul>
          </div>
        </div>

        <!-- Top Endpoints -->
        <div class="bg-white shadow rounded-lg overflow-hidden">
          <div class="px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-semibold text-gray-700">Top Endpoints</h3>
          </div>
          <div class="p-6">
            <div v-if="!analytics.topEndpoints || analytics.topEndpoints.length === 0" class="text-center py-4">
              <p class="text-gray-500">No endpoint data available</p>
            </div>
            <ul v-else class="divide-y divide-gray-200">
              <li v-for="(endpoint, index) in analytics.topEndpoints" :key="index" class="py-3 flex justify-between">
                <div class="flex items-center">
                  <span class="text-gray-900 font-medium">{{ endpoint._id }}</span>
                </div>
                <div class="text-gray-500">
                  {{ endpoint.count }} hits
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Browser Stats -->
      <div class="bg-white shadow rounded-lg overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-700">Browser Statistics</h3>
        </div>
        <div class="p-6">
          <div v-if="!analytics.browserStats || analytics.browserStats.length === 0" class="text-center py-4">
            <p class="text-gray-500">No browser statistics available</p>
          </div>
          <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div v-for="(browser, index) in analytics.browserStats" :key="index" class="bg-gray-50 p-4 rounded-lg">
              <div class="text-lg font-semibold text-gray-900">{{ browser._id || 'Unknown' }}</div>
              <div class="text-gray-500">{{ browser.count }} visits</div>
              <div class="mt-2 h-2 bg-gray-200 rounded-full overflow-hidden">
                <div class="h-full bg-blue-500" :style="{ width: `${(browser.count / analytics.totalVisits) * 100}%` }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import api from '@/services/api'
import { useToast } from '@/components/ui/ToastContainer.vue'

const authStore = useAuthStore()
const toast = useToast()

// State
const timeRange = ref('30') // Default to 30 days
const loading = ref(true)
const error = ref('')
const analytics = ref<any>({})
const dashboardData = ref<any>({})

// Computed
const loginRate = computed(() => {
  if (!analytics.value.totalVisits || !dashboardData.value.totalUsers) return 0
  return Math.round((analytics.value.totalVisits / dashboardData.value.totalUsers) * 100)
})

// Fetch analytics data
async function fetchAnalytics() {
  try {
    loading.value = true
    error.value = ''

    // Calculate date range
    const endDate = new Date()
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - parseInt(timeRange.value))

    // Format dates for API
    const startDateStr = startDate.toISOString()
    const endDateStr = endDate.toISOString()

    // Fetch analytics data
    const response = await api.post('/admin/analytics/access-stats', {
      startDate: startDateStr,
      endDate: endDateStr
    }, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    analytics.value = response.data.data || {}

    // Fetch dashboard data for user counts
    await fetchDashboardData()

    toast.success('Analytics data refreshed')
  } catch (err: any) {
    console.error('Error fetching analytics:', err)
    error.value = err.message || 'Failed to load analytics data'
    toast.error('Failed to load analytics data')
  } finally {
    loading.value = false
  }
}

// Fetch dashboard data for user counts
async function fetchDashboardData() {
  try {
    const response = await api.post('/admin/dashboard', {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    dashboardData.value = response.data.data || {}
  } catch (err: any) {
    console.error('Error fetching dashboard data:', err)
  }
}

// Lifecycle
onMounted(() => {
  fetchAnalytics()
})
</script>
