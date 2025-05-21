<template>
  <nav class="bg-white shadow">
    <div class="container mx-auto px-4">
      <div class="flex justify-between h-16">
        <div class="flex">
          <div class="flex-shrink-0 flex items-center">
            <router-link to="/" class="text-xl font-bold text-blue-600">
              AuthenHub
            </router-link>
          </div>
          <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
            <router-link
              to="/"
              class="inline-flex items-center px-1 pt-1 border-b-2"
              :class="[$route.name === 'home' ? 'border-blue-500 text-gray-900' : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700']"
            >
              Home
            </router-link>
            <router-link
              v-if="authStore.isAuthenticated"
              to="/profile"
              class="inline-flex items-center px-1 pt-1 border-b-2"
              :class="[$route.name === 'profile' ? 'border-blue-500 text-gray-900' : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700']"
            >
              Profile
            </router-link>
            <router-link
              v-if="authStore.isAuthenticated"
              to="/admin"
              class="inline-flex items-center px-1 pt-1 border-b-2"
              :class="[$route.name === 'admin-dashboard' ? 'border-blue-500 text-gray-900' : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700']"
            >
              Admin Dashboard
            </router-link>
          </div>
        </div>
        <div class="hidden sm:ml-6 sm:flex sm:items-center">
          <!-- Show login/register buttons for guests -->
          <template v-if="!authStore.isAuthenticated">
            <router-link
              to="/login"
              class="btn btn-secondary mr-2"
            >
              Login
            </router-link>
            <router-link
              to="/register"
              class="btn btn-primary"
            >
              Register
            </router-link>
          </template>

          <!-- Show user dropdown for authenticated users -->
          <div v-else class="ml-3 relative">
            <div>
              <button
                @click="toggleDropdown"
                class="flex text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                id="user-menu-button"
                aria-expanded="false"
                aria-haspopup="true"
              >
                <span class="sr-only">Open user menu</span>
                <div v-if="authStore.user?.avatar" class="h-8 w-8 rounded-full overflow-hidden">
                  <img
                    :src="authStore.user.avatar"
                    alt="User avatar"
                    class="h-full w-full object-cover"
                  />
                </div>
                <div v-else class="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center text-white">
                  {{ authStore.user?.fullName.charAt(0) }}
                </div>
              </button>
            </div>

            <!-- Dropdown menu -->
            <div
              v-if="isDropdownOpen"
              class="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none"
              role="menu"
              aria-orientation="vertical"
              aria-labelledby="user-menu-button"
              tabindex="-1"
            >
              <div class="px-4 py-2 text-sm text-gray-700 border-b">
                <div class="font-medium">{{ authStore.user?.fullName }}</div>
                <div class="text-gray-500">{{ authStore.user?.email }}</div>
              </div>
              <router-link
                to="/profile"
                class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                role="menuitem"
                tabindex="-1"
                @click="isDropdownOpen = false"
              >
                Your Profile
              </router-link>
              <router-link
                v-if="authStore.isAdmin"
                to="/admin"
                class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                role="menuitem"
                tabindex="-1"
                @click="isDropdownOpen = false"
              >
                Admin Dashboard
              </router-link>
              <button
                @click="logout"
                class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                role="menuitem"
                tabindex="-1"
              >
                Sign out
              </button>
            </div>
          </div>
        </div>

        <!-- Mobile menu button -->
        <div class="flex items-center sm:hidden">
          <button
            @click="isMobileMenuOpen = !isMobileMenuOpen"
            class="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-blue-500"
            aria-expanded="false"
          >
            <span class="sr-only">Open main menu</span>
            <!-- Icon when menu is closed -->
            <svg
              v-if="!isMobileMenuOpen"
              class="block h-6 w-6"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              aria-hidden="true"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
            <!-- Icon when menu is open -->
            <svg
              v-else
              class="block h-6 w-6"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              aria-hidden="true"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile menu -->
    <div v-if="isMobileMenuOpen" class="sm:hidden">
      <div class="pt-2 pb-3 space-y-1">
        <router-link
          to="/"
          class="block pl-3 pr-4 py-2 border-l-4"
          :class="[$route.name === 'home' ? 'border-blue-500 text-blue-700 bg-blue-50' : 'border-transparent text-gray-600 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-800']"
          @click="isMobileMenuOpen = false"
        >
          Home
        </router-link>
        <router-link
          v-if="authStore.isAuthenticated"
          to="/profile"
          class="block pl-3 pr-4 py-2 border-l-4"
          :class="[$route.name === 'profile' ? 'border-blue-500 text-blue-700 bg-blue-50' : 'border-transparent text-gray-600 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-800']"
          @click="isMobileMenuOpen = false"
        >
          Profile
        </router-link>
        <router-link
          v-if="authStore.isAuthenticated && authStore.isAdmin"
          to="/admin"
          class="block pl-3 pr-4 py-2 border-l-4"
          :class="[$route.name === 'admin-dashboard' ? 'border-blue-500 text-blue-700 bg-blue-50' : 'border-transparent text-gray-600 hover:bg-gray-50 hover:border-gray-300 hover:text-gray-800']"
          @click="isMobileMenuOpen = false"
        >
          Admin Dashboard
        </router-link>
      </div>

      <!-- Mobile authentication buttons/user info -->
      <div class="pt-4 pb-3 border-t border-gray-200">
        <template v-if="!authStore.isAuthenticated">
          <div class="flex items-center px-4 space-x-2">
            <router-link
              to="/login"
              class="btn btn-secondary flex-1"
              @click="isMobileMenuOpen = false"
            >
              Login
            </router-link>
            <router-link
              to="/register"
              class="btn btn-primary flex-1"
              @click="isMobileMenuOpen = false"
            >
              Register
            </router-link>
          </div>
        </template>
        <template v-else>
          <div class="flex items-center px-4">
            <div class="flex-shrink-0">
              <div v-if="authStore.user?.avatar" class="h-10 w-10 rounded-full overflow-hidden">
                <img
                  :src="authStore.user.avatar"
                  alt="User avatar"
                  class="h-full w-full object-cover"
                />
              </div>
              <div v-else class="h-10 w-10 rounded-full bg-blue-500 flex items-center justify-center text-white">
                {{ authStore.user?.fullName.charAt(0) }}
              </div>
            </div>
            <div class="ml-3">
              <div class="text-base font-medium text-gray-800">{{ authStore.user?.fullName }}</div>
              <div class="text-sm font-medium text-gray-500">{{ authStore.user?.email }}</div>
            </div>
          </div>
          <div class="mt-3 space-y-1">
            <router-link
              to="/profile"
              class="block px-4 py-2 text-base font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-100"
              @click="isMobileMenuOpen = false"
            >
              Your Profile
            </router-link>
            <button
              @click="logout"
              class="block w-full text-left px-4 py-2 text-base font-medium text-gray-500 hover:text-gray-800 hover:bg-gray-100"
            >
              Sign out
            </button>
          </div>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const isDropdownOpen = ref(false)
const isMobileMenuOpen = ref(false)

function toggleDropdown() {
  isDropdownOpen.value = !isDropdownOpen.value
}

function logout() {
  authStore.logout()
  isDropdownOpen.value = false
  isMobileMenuOpen.value = false
}

// Close dropdown when clicking outside
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement
  if (!target.closest('#user-menu-button') && isDropdownOpen.value) {
    isDropdownOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
