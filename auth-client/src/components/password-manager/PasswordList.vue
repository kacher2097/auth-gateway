<template>
  <div class="bg-white shadow overflow-hidden sm:rounded-md">
    <!-- Search form -->
    <div class="px-4 py-5 sm:p-6">
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div>
          <label for="keyword" class="block text-sm font-medium text-gray-700">Keyword</label>
          <input
            type="text"
            id="keyword"
            v-model="searchParams.keyword"
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
            placeholder="Search by site or username"
          />
        </div>
        <div>
          <label for="provider" class="block text-sm font-medium text-gray-700">Provider</label>
          <input
            type="text"
            id="provider"
            v-model="searchParams.provider"
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
            placeholder="e.g. Google, Facebook"
          />
        </div>
        <div>
          <label for="username" class="block text-sm font-medium text-gray-700">Username</label>
          <input
            type="text"
            id="username"
            v-model="searchParams.username"
            class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
            placeholder="e.g. user@example.com"
          />
        </div>
      </div>
      <div class="mt-4 flex justify-end">
        <button
          type="button"
          @click="searchPasswords"
          class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          Search
        </button>
      </div>
    </div>

    <!-- Password list -->
    <div v-if="loading" class="px-4 py-5 sm:p-6 text-center">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
      <p class="mt-2 text-sm text-gray-500">Loading passwords...</p>
    </div>
    
    <div v-else-if="passwords.length === 0" class="px-4 py-5 sm:p-6 text-center">
      <p class="text-sm text-gray-500">No passwords found. Try a different search or add a new password.</p>
    </div>
    
    <ul v-else role="list" class="divide-y divide-gray-200">
      <li v-for="password in passwords" :key="password.id" class="px-4 py-4 sm:px-6">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <img 
              v-if="password.iconUrl" 
              :src="password.iconUrl" 
              alt="Site icon" 
              class="h-10 w-10 rounded-full mr-4"
              @error="handleImageError"
            />
            <div v-else class="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center mr-4">
              <svg class="h-6 w-6 text-gray-500" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z" />
              </svg>
            </div>
            <div>
              <div class="text-sm font-medium text-blue-600">{{ password.siteUrl }}</div>
              <div class="text-sm text-gray-500">{{ password.username }}</div>
            </div>
          </div>
          <div class="flex space-x-2">
            <button
              @click="viewPassword(password.id)"
              class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-blue-700 bg-blue-100 hover:bg-blue-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              View
            </button>
            <button
              @click="editPassword(password.id)"
              class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-green-700 bg-green-100 hover:bg-green-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
            >
              Edit
            </button>
            <button
              @click="confirmDelete(password.id)"
              class="inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
            >
              Delete
            </button>
          </div>
        </div>
      </li>
    </ul>
    
    <!-- Pagination -->
    <Pagination
      v-if="totalItems > 0"
      :current-page="currentPage"
      :page-size="pageSize"
      :total-items="totalItems"
      :total-pages="totalPages"
      @page-change="handlePageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import passwordManagerService, { Password, PasswordSearchRequest } from '@/services/password-manager.service'
import Pagination from '@/components/common/Pagination.vue'

// State
const passwords = ref<Password[]>([])
const loading = ref(false)
const currentPage = ref(0)
const pageSize = ref(10)
const totalItems = ref(0)
const totalPages = ref(0)

const searchParams = reactive<PasswordSearchRequest>({
  keyword: '',
  provider: '',
  username: '',
  page: 0,
  size: 10,
  sortBy: 'createdAt',
  sortDirection: 'DESC'
})

// Methods
const searchPasswords = async () => {
  try {
    loading.value = true
    searchParams.page = currentPage.value
    searchParams.size = pageSize.value
    
    const response = await passwordManagerService.searchPasswords(searchParams)
    
    passwords.value = response.content
    currentPage.value = response.page
    pageSize.value = response.size
    totalItems.value = response.totalElements
    totalPages.value = response.totalPages
  } catch (error) {
    console.error('Error searching passwords:', error)
    // Handle error (show notification, etc.)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  searchPasswords()
}

const viewPassword = async (id: number) => {
  // Implement view password logic
  console.log('View password:', id)
}

const editPassword = (id: number) => {
  // Implement edit password logic
  console.log('Edit password:', id)
}

const confirmDelete = (id: number) => {
  // Implement delete confirmation logic
  console.log('Delete password:', id)
}

const handleImageError = (event: Event) => {
  // Replace broken image with default icon
  const target = event.target as HTMLImageElement
  target.src = '/images/default-icon.png'
}

// Lifecycle hooks
onMounted(() => {
  searchPasswords()
})
</script>
