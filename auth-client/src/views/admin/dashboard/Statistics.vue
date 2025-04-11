<template>
  <div>
    <Breadcrumb :items="breadcrumbItems" />
    
    <div class="bg-white shadow rounded-lg p-6 mt-4">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">Dashboard Statistics</h2>
      
      <!-- Time Range Selector -->
      <div class="mb-6 flex justify-end">
        <Select
          v-model="timeRange"
          class="w-48"
          @change="fetchStatistics"
        >
          <option value="7">Last 7 days</option>
          <option value="30">Last 30 days</option>
          <option value="90">Last 90 days</option>
          <option value="365">Last year</option>
        </Select>
      </div>
      
      <!-- User Growth Chart -->
      <div class="mb-8">
        <h3 class="text-lg font-medium text-gray-700 mb-3">User Growth</h3>
        <div class="bg-gray-50 rounded-lg border border-gray-200 p-4 h-64">
          <canvas ref="userGrowthChart"></canvas>
        </div>
      </div>
      
      <!-- Authentication Methods -->
      <div class="mb-8">
        <h3 class="text-lg font-medium text-gray-700 mb-3">Authentication Methods</h3>
        <div class="bg-gray-50 rounded-lg border border-gray-200 p-4 h-64">
          <canvas ref="authMethodsChart"></canvas>
        </div>
      </div>
      
      <!-- User Activity by Hour -->
      <div>
        <h3 class="text-lg font-medium text-gray-700 mb-3">User Activity by Hour</h3>
        <div class="bg-gray-50 rounded-lg border border-gray-200 p-4 h-64">
          <canvas ref="activityChart"></canvas>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import Chart from 'chart.js/auto'
import { useToast } from '@/components/ui/ToastContainer.vue'

// Initialize auth store and toast
const authStore = useAuthStore()
const toast = useToast()

// Breadcrumb items
const breadcrumbItems = [
  { text: 'Dashboard', to: '/admin' },
  { text: 'Statistics', to: '/admin/statistics', active: true }
]

// Chart references
const userGrowthChart = ref<HTMLCanvasElement | null>(null)
const authMethodsChart = ref<HTMLCanvasElement | null>(null)
const activityChart = ref<HTMLCanvasElement | null>(null)

// Chart instances
let userGrowthChartInstance: Chart | null = null
let authMethodsChartInstance: Chart | null = null
let activityChartInstance: Chart | null = null

// Time range for statistics
const timeRange = ref('30')

// Statistics data
const statistics = ref({
  userGrowth: [] as { date: string; count: number }[],
  authMethods: [] as { method: string; count: number }[],
  activityByHour: [] as { hour: number; count: number }[]
})

// Fetch statistics data
const fetchStatistics = async () => {
  try {
    const days = parseInt(timeRange.value)
    const endDate = new Date()
    const startDate = new Date()
    startDate.setDate(startDate.getDate() - days)
    
    const startDateStr = startDate.toISOString()
    const endDateStr = endDate.toISOString()
    
    const response = await api.post('/admin/analytics/statistics', {
      startDate: startDateStr,
      endDate: endDateStr
    }, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    // Check if the response has a data property (ApiResponse format)
    if (response.data && response.data.data) {
      statistics.value = response.data.data
    } else {
      // Fallback if the API returns the data directly
      statistics.value = response.data || {
        userGrowth: [],
        authMethods: [],
        activityByHour: []
      }
    }
    
    // Update charts
    updateCharts()
  } catch (error) {
    console.error('Error fetching statistics:', error)
    toast.error('Failed to load statistics data')
    
    // Initialize with empty data
    statistics.value = {
      userGrowth: [],
      authMethods: [],
      activityByHour: []
    }
    
    // Update charts with empty data
    updateCharts()
  }
}

// Update all charts
const updateCharts = () => {
  updateUserGrowthChart()
  updateAuthMethodsChart()
  updateActivityChart()
}

// Update user growth chart
const updateUserGrowthChart = () => {
  if (!userGrowthChart.value) return
  
  const ctx = userGrowthChart.value.getContext('2d')
  if (!ctx) return
  
  // Destroy previous chart instance if it exists
  if (userGrowthChartInstance) {
    userGrowthChartInstance.destroy()
  }
  
  // Prepare data
  const labels = statistics.value.userGrowth.map(item => item.date)
  const data = statistics.value.userGrowth.map(item => item.count)
  
  // Create new chart
  userGrowthChartInstance = new Chart(ctx, {
    type: 'line',
    data: {
      labels,
      datasets: [{
        label: 'New Users',
        data,
        borderColor: '#1890ff',
        backgroundColor: 'rgba(24, 144, 255, 0.1)',
        tension: 0.3,
        fill: true
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: 'User Growth Over Time'
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            precision: 0
          }
        }
      }
    }
  })
}

// Update authentication methods chart
const updateAuthMethodsChart = () => {
  if (!authMethodsChart.value) return
  
  const ctx = authMethodsChart.value.getContext('2d')
  if (!ctx) return
  
  // Destroy previous chart instance if it exists
  if (authMethodsChartInstance) {
    authMethodsChartInstance.destroy()
  }
  
  // Prepare data
  const labels = statistics.value.authMethods.map(item => item.method)
  const data = statistics.value.authMethods.map(item => item.count)
  
  // Create new chart
  authMethodsChartInstance = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels,
      datasets: [{
        data,
        backgroundColor: [
          '#1890ff',
          '#52c41a',
          '#fa8c16',
          '#722ed1'
        ],
        hoverOffset: 4
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: 'Authentication Methods'
        },
        legend: {
          position: 'right'
        }
      }
    }
  })
}

// Update activity by hour chart
const updateActivityChart = () => {
  if (!activityChart.value) return
  
  const ctx = activityChart.value.getContext('2d')
  if (!ctx) return
  
  // Destroy previous chart instance if it exists
  if (activityChartInstance) {
    activityChartInstance.destroy()
  }
  
  // Prepare data
  const hours = Array.from({ length: 24 }, (_, i) => i)
  const activityData = Array(24).fill(0)
  
  // Fill in the data we have
  statistics.value.activityByHour.forEach(item => {
    activityData[item.hour] = item.count
  })
  
  // Create new chart
  activityChartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: hours.map(hour => `${hour}:00`),
      datasets: [{
        label: 'Activity Count',
        data: activityData,
        backgroundColor: 'rgba(24, 144, 255, 0.6)',
        borderColor: '#1890ff',
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: 'User Activity by Hour'
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            precision: 0
          }
        }
      }
    }
  })
}

// Watch for time range changes
watch(timeRange, () => {
  fetchStatistics()
})

// Initialize on mount
onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.select {
  @apply block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500;
}
</style>
