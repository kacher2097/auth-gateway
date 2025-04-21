<template>
  <div class="flex h-screen overflow-hidden bg-gray-50">
    <Sidebar ref="sidebar" :is-mobile-open="isMobileMenuOpen" @close-mobile="isMobileMenuOpen = false" />

    <!-- Mobile backdrop when sidebar is open -->
    <div
      v-if="isMobileMenuOpen"
      class="fixed inset-0 bg-gray-600 bg-opacity-75 z-10 lg:hidden"
      @click="isMobileMenuOpen = false"
    ></div>

    <div class="flex-1 overflow-auto transition-all duration-300 w-full" :class="sidebarClass">
      <header class="bg-white shadow-sm sticky top-0 z-10">
        <div class="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
          <div class="flex items-center">
            <button
              @click="toggleMobileMenu"
              class="inline-flex items-center justify-center p-2 bg-blue-600 rounded-md text-white mr-3 lg:hidden hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h7" />
              </svg>
            </button>
            <h1 class="text-2xl font-bold text-gray-900">
              {{ title }}
            </h1>
          </div>
          <div class="flex items-center space-x-4">
            <!-- Breadcrumb -->
            <Breadcrumb v-if="breadcrumbItems && breadcrumbItems.length > 0" :items="breadcrumbItems" class="hidden md:flex" />

            <!-- User dropdown -->
            <div class="relative">
              <button
                @click="toggleUserMenu"
                class="flex items-center text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
              >
                <span class="sr-only">Open user menu</span>
                <div v-if="authStore.user?.avatar" class="h-8 w-8 rounded-full overflow-hidden bg-gray-100">
                  <img :src="authStore.user.avatar" alt="User avatar" class="h-full w-full object-cover" />
                </div>
                <div v-else class="h-8 w-8 rounded-full bg-blue-600 flex items-center justify-center text-white font-medium">
                  {{ userInitials }}
                </div>
                <span class="ml-2 text-gray-700 hidden md:block">{{ authStore.user?.fullName }}</span>
                <svg class="ml-1 h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" />
                </svg>
              </button>

              <!-- Dropdown menu -->
              <div
                v-if="isUserMenuOpen"
                class="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 divide-y divide-gray-100 focus:outline-none z-20"
              >
                <div class="py-1">
                  <router-link to="/profile" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                    Your Profile
                  </router-link>
                  <router-link to="/admin/settings" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                    Settings
                  </router-link>
                </div>
                <div class="py-1">
                  <button @click="logout" class="w-full text-left block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                    Sign out
                  </button>
                </div>
              </div>
            </div>

            <slot name="actions"></slot>
          </div>
        </div>
      </header>

      <!-- Breadcrumb for mobile -->
      <div v-if="breadcrumbItems && breadcrumbItems.length > 0" class="bg-white border-b border-gray-200 md:hidden">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-2">
          <Breadcrumb :items="breadcrumbItems" />
        </div>
      </div>

      <main class="pb-6">
        <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <slot></slot>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import Sidebar from './Sidebar.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  breadcrumbItems: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()
const authStore = useAuthStore()

const sidebar = ref(null)
const isMobileMenuOpen = ref(false)
const isUserMenuOpen = ref(false)

// Computed properties
const sidebarClass = computed(() => {
  if (!sidebar.value) return ''
  return sidebar.value.isCollapsed ? 'lg:ml-20' : 'lg:ml-72 xl:ml-72 2xl:ml-72'
})

const userInitials = computed(() => {
  if (!authStore.user?.fullName) return '?'

  const nameParts = authStore.user.fullName.split(' ')
  if (nameParts.length >= 2) {
    return `${nameParts[0][0]}${nameParts[nameParts.length - 1][0]}`.toUpperCase()
  }
  return nameParts[0][0].toUpperCase()
})

// Methods
const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
  if (isMobileMenuOpen.value) {
    isUserMenuOpen.value = false
  }
}

const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}

// Close user menu when clicking outside
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (isUserMenuOpen.value && !target.closest('button')) {
    isUserMenuOpen.value = false
  }
}

// Add event listener for window resize to close mobile menu on larger screens
const handleResize = () => {
  if (window.innerWidth >= 1024 && isMobileMenuOpen.value) {
    isMobileMenuOpen.value = false
  }
}

// Watch for sidebar collapse state changes
onMounted(() => {
  // Force a re-render to get the correct sidebar state
  setTimeout(() => {
    // This is a hack to force Vue to re-evaluate the computed property
    sidebar.value = { ...sidebar.value }
  }, 0)

  window.addEventListener('resize', handleResize)
  document.addEventListener('click', handleClickOutside)
})

// Clean up event listeners
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  document.removeEventListener('click', handleClickOutside)
})
</script>
