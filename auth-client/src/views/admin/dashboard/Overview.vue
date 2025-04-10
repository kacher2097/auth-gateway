<template>
  <div>
    <Breadcrumb :items="breadcrumbItems" />
    
    <div class="bg-white shadow rounded-lg p-6 mt-4">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">Dashboard Overview</h2>
      
      <!-- Summary Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-blue-50 border border-blue-100 rounded-lg p-4">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-blue-100 text-blue-600 mr-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-blue-600 font-medium">Total Users</p>
              <p class="text-2xl font-bold text-blue-800">{{ stats.totalUsers }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-green-50 border border-green-100 rounded-lg p-4">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-green-100 text-green-600 mr-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-green-600 font-medium">Active Sessions</p>
              <p class="text-2xl font-bold text-green-800">{{ stats.activeSessions }}</p>
            </div>
          </div>
        </div>
        
        <div class="bg-purple-50 border border-purple-100 rounded-lg p-4">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-purple-100 text-purple-600 mr-4">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div>
              <p class="text-sm text-purple-600 font-medium">System Status</p>
              <p class="text-2xl font-bold text-purple-800">{{ stats.systemStatus }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Recent Activity -->
      <div class="mb-8">
        <h3 class="text-lg font-medium text-gray-700 mb-3">Recent Activity</h3>
        <div class="bg-gray-50 rounded-lg border border-gray-200">
          <ul class="divide-y divide-gray-200">
            <li v-for="(activity, index) in recentActivity" :key="index" class="p-4">
              <div class="flex items-start">
                <div class="flex-shrink-0 mr-3">
                  <div class="h-10 w-10 rounded-full bg-blue-500 flex items-center justify-center text-white">
                    {{ activity.user.charAt(0) }}
                  </div>
                </div>
                <div>
                  <p class="text-sm font-medium text-gray-900">{{ activity.user }}</p>
                  <p class="text-sm text-gray-500">{{ activity.action }}</p>
                  <p class="text-xs text-gray-400 mt-1">{{ activity.time }}</p>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
      
      <!-- Quick Links -->
      <div>
        <h3 class="text-lg font-medium text-gray-700 mb-3">Quick Links</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <router-link 
            v-for="link in quickLinks" 
            :key="link.to" 
            :to="link.to"
            class="bg-white border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors duration-200 flex flex-col items-center text-center"
          >
            <div class="p-2 rounded-full bg-blue-100 text-blue-600 mb-2">
              <component :is="link.icon" class="h-5 w-5" />
            </div>
            <span class="text-sm font-medium text-gray-700">{{ link.label }}</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'

// Breadcrumb items
const breadcrumbItems = [
  { label: 'Dashboard', to: '/admin' },
  { label: 'Overview', to: '/admin/overview' }
]

// Mock data for the dashboard
const stats = ref({
  totalUsers: 1248,
  activeSessions: 87,
  systemStatus: 'Healthy'
})

const recentActivity = ref([
  { user: 'John Doe', action: 'Created a new user account', time: '5 minutes ago' },
  { user: 'Jane Smith', action: 'Updated system settings', time: '1 hour ago' },
  { user: 'Mike Johnson', action: 'Generated monthly report', time: '3 hours ago' },
  { user: 'Sarah Williams', action: 'Reset password for user@example.com', time: '5 hours ago' }
])

// Quick links with dynamic icons
const quickLinks = [
  { 
    label: 'User Management', 
    to: '/admin/users',
    icon: 'UserIcon'
  },
  { 
    label: 'Analytics', 
    to: '/admin/analytics',
    icon: 'ChartIcon'
  },
  { 
    label: 'Settings', 
    to: '/admin/settings',
    icon: 'CogIcon'
  },
  { 
    label: 'Reports', 
    to: '/admin/analytics/reports',
    icon: 'DocumentIcon'
  }
]

// Define icon components
const UserIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
    </svg>
  `
}

const ChartIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
    </svg>
  `
}

const CogIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
    </svg>
  `
}

const DocumentIcon = {
  template: `
    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
    </svg>
  `
}
</script>
