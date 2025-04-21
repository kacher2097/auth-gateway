<template>
  <div class="max-w-7xl mx-auto">
    <!-- Welcome Section -->
    <div class="bg-white shadow-md rounded-lg p-6 mb-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">
            Welcome back, {{ authStore.user?.fullName }}!
          </h1>
          <p class="text-gray-600 mt-1">
            Here's an overview of your account and available services.
          </p>
        </div>
        <div class="hidden md:block">
          <img src="@/assets/images/dashboard-welcome.svg" alt="Welcome" class="h-24" />
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="mb-8">
      <h2 class="text-lg font-semibold text-gray-700 mb-4">Quick Actions</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div 
          v-for="(action, index) in quickActions" 
          :key="index"
          class="bg-white p-4 rounded-lg shadow-sm border border-gray-100 hover:shadow-md transition-shadow cursor-pointer"
          @click="navigateTo(action.to)"
        >
          <div class="flex items-center">
            <div class="p-2 rounded-full" :class="action.bgColor">
              <component :is="action.icon" class="h-6 w-6" :class="action.iconColor" />
            </div>
            <div class="ml-3">
              <h3 class="font-medium text-gray-800">{{ action.title }}</h3>
              <p class="text-sm text-gray-500">{{ action.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Stats Overview -->
    <div class="mb-8">
      <h2 class="text-lg font-semibold text-gray-700 mb-4">Account Overview</h2>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div 
          v-for="(stat, index) in stats" 
          :key="index"
          class="bg-white p-4 rounded-lg shadow-sm border border-gray-100"
        >
          <div class="flex flex-col">
            <span class="text-sm text-gray-500">{{ stat.title }}</span>
            <span class="text-2xl font-bold text-gray-800 mt-1">{{ stat.value }}</span>
            <span class="text-xs text-gray-500 mt-1">{{ stat.subtitle }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="mb-8">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-lg font-semibold text-gray-700">Recent Activity</h2>
        <button 
          class="text-sm text-blue-600 hover:text-blue-800 transition-colors"
          @click="loadMoreActivity"
        >
          View All
        </button>
      </div>
      <div class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
        <div v-if="loading" class="p-4 text-center">
          <p class="text-gray-500">Loading activity...</p>
        </div>
        <div v-else-if="recentActivity.length === 0" class="p-4 text-center">
          <p class="text-gray-500">No recent activity found</p>
        </div>
        <ul v-else class="divide-y divide-gray-200">
          <li v-for="(activity, index) in recentActivity" :key="index" class="p-4 hover:bg-gray-50">
            <div class="flex items-start">
              <div class="p-2 rounded-full" :class="getActivityIconBg(activity.type)">
                <component :is="getActivityIcon(activity.type)" class="h-5 w-5" :class="getActivityIconColor(activity.type)" />
              </div>
              <div class="ml-3">
                <p class="text-sm font-medium text-gray-800">{{ activity.description }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ formatDate(activity.timestamp) }}</p>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <!-- Available Services -->
    <div>
      <h2 class="text-lg font-semibold text-gray-700 mb-4">Available Services</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div 
          v-for="(service, index) in services" 
          :key="index"
          class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow"
        >
          <div class="p-5">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-medium text-gray-800">{{ service.title }}</h3>
              <div class="p-2 rounded-full" :class="service.bgColor">
                <component :is="service.icon" class="h-5 w-5" :class="service.iconColor" />
              </div>
            </div>
            <p class="text-sm text-gray-600 mb-4">{{ service.description }}</p>
            <router-link 
              :to="service.to" 
              class="inline-flex items-center text-sm font-medium text-blue-600 hover:text-blue-800 transition-colors"
            >
              Explore
              <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
              </svg>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { format, parseISO } from 'date-fns'

// Icons
const UserIcon = {
  template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
  </svg>`
}

const KeyIcon = {
  template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
  </svg>`
}

const ShieldIcon = {
  template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
  </svg>`
}

const ProxyIcon = {
  template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
  </svg>`
}

const AnalyticsIcon = {
  template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
  </svg>`
}

const LoginIcon = {
  template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
  </svg>`
}

const LogoutIcon = {
  template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
  </svg>`
}

const SettingsIcon = {
  template: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
  </svg>`
}

// Router and auth store
const router = useRouter()
const authStore = useAuthStore()

// State
const loading = ref(false)
const recentActivity = ref([
  {
    type: 'login',
    description: 'You logged in successfully',
    timestamp: new Date().toISOString()
  },
  {
    type: 'settings',
    description: 'You updated your profile information',
    timestamp: new Date(Date.now() - 86400000).toISOString() // 1 day ago
  },
  {
    type: 'proxy',
    description: 'You added a new proxy server',
    timestamp: new Date(Date.now() - 172800000).toISOString() // 2 days ago
  }
])

// Quick actions
const quickActions = [
  {
    title: 'Update Profile',
    description: 'Edit your personal information',
    icon: UserIcon,
    bgColor: 'bg-blue-100',
    iconColor: 'text-blue-600',
    to: '/profile'
  },
  {
    title: 'Change Password',
    description: 'Update your security credentials',
    icon: KeyIcon,
    bgColor: 'bg-purple-100',
    iconColor: 'text-purple-600',
    to: '/profile?tab=security'
  },
  {
    title: 'Manage Proxies',
    description: 'View and configure proxy servers',
    icon: ProxyIcon,
    bgColor: 'bg-green-100',
    iconColor: 'text-green-600',
    to: '/proxies'
  }
]

// Stats
const stats = [
  {
    title: 'Last Login',
    value: computed(() => {
      return authStore.user?.lastLogin 
        ? format(parseISO(authStore.user.lastLogin), 'MMM d, yyyy')
        : 'N/A'
    }),
    subtitle: 'Your previous session'
  },
  {
    title: 'Account Status',
    value: computed(() => authStore.user?.active ? 'Active' : 'Inactive'),
    subtitle: 'Current account state'
  },
  {
    title: 'Role',
    value: computed(() => authStore.user?.role || 'User'),
    subtitle: 'Your permission level'
  },
  {
    title: 'Login Method',
    value: computed(() => {
      return authStore.user?.socialProvider 
        ? authStore.user.socialProvider.charAt(0).toUpperCase() + authStore.user.socialProvider.slice(1).toLowerCase()
        : 'Email'
    }),
    subtitle: 'How you authenticate'
  }
]

// Available services
const services = [
  {
    title: 'Proxy Management',
    description: 'Access and manage proxy servers for secure browsing and API access',
    icon: ProxyIcon,
    bgColor: 'bg-green-100',
    iconColor: 'text-green-600',
    to: '/proxies'
  },
  {
    title: 'Security Settings',
    description: 'Configure your account security and authentication options',
    icon: ShieldIcon,
    bgColor: 'bg-red-100',
    iconColor: 'text-red-600',
    to: '/profile?tab=security'
  },
  {
    title: 'Analytics',
    description: 'View insights and statistics about your account usage',
    icon: AnalyticsIcon,
    bgColor: 'bg-blue-100',
    iconColor: 'text-blue-600',
    to: '/analytics'
  }
]

// Methods
function navigateTo(path: string) {
  router.push(path)
}

function loadMoreActivity() {
  // This would typically load more activity from an API
  console.log('Loading more activity...')
}

function formatDate(dateString: string) {
  try {
    const date = parseISO(dateString)
    return format(date, 'MMM d, yyyy h:mm a')
  } catch (error) {
    return 'Invalid date'
  }
}

function getActivityIcon(type: string) {
  switch (type) {
    case 'login':
      return LoginIcon
    case 'logout':
      return LogoutIcon
    case 'settings':
      return SettingsIcon
    case 'proxy':
      return ProxyIcon
    default:
      return UserIcon
  }
}

function getActivityIconBg(type: string) {
  switch (type) {
    case 'login':
      return 'bg-green-100'
    case 'logout':
      return 'bg-red-100'
    case 'settings':
      return 'bg-blue-100'
    case 'proxy':
      return 'bg-purple-100'
    default:
      return 'bg-gray-100'
  }
}

function getActivityIconColor(type: string) {
  switch (type) {
    case 'login':
      return 'text-green-600'
    case 'logout':
      return 'text-red-600'
    case 'settings':
      return 'text-blue-600'
    case 'proxy':
      return 'text-purple-600'
    default:
      return 'text-gray-600'
  }
}

// Lifecycle
onMounted(() => {
  // This would typically fetch data from an API
  console.log('Dashboard mounted')
})
</script>
