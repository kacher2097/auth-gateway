<template>
  <AdminLayout title="User Management">
    <template #actions>
      <div class="relative">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search users..."
          class="w-64 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
          <svg class="h-5 w-5 text-gray-400" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>
      </div>
    </template>

    <div class="bg-white shadow rounded-lg overflow-hidden">
      <div v-if="loading" class="p-8 flex justify-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>

      <div v-else-if="error" class="p-8">
        <div class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md">
          {{ error }}
        </div>
      </div>

      <div v-else>
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
                <th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="user in filteredUsers" :key="user.id">
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
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div class="flex justify-end space-x-2">
                    <button
                      @click="editUser(user)"
                      class="text-blue-600 hover:text-blue-900"
                    >
                      Edit
                    </button>
                    <button
                      v-if="user.active"
                      @click="deactivateUser(user)"
                      class="text-red-600 hover:text-red-900"
                    >
                      Deactivate
                    </button>
                    <button
                      v-else
                      @click="activateUser(user)"
                      class="text-green-600 hover:text-green-900"
                    >
                      Activate
                    </button>
                    <button
                      v-if="user.role === 'USER'"
                      @click="promoteToAdmin(user)"
                      class="text-purple-600 hover:text-purple-900"
                    >
                      Make Admin
                    </button>
                    <button
                      v-else-if="user.id !== authStore.user?.id"
                      @click="demoteToUser(user)"
                      class="text-gray-600 hover:text-gray-900"
                    >
                      Remove Admin
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="filteredUsers.length === 0" class="p-8 text-center">
          <p class="text-gray-500">No users found matching your search criteria.</p>
        </div>

        <div v-if="users.length > 0 && filteredUsers.length === 0" class="p-4 flex justify-center">
          <button
            @click="searchQuery = ''"
            class="text-blue-600 hover:text-blue-900"
          >
            Clear search
          </button>
        </div>
      </div>
    </div>

    <!-- Edit User Modal -->
    <div v-if="showEditModal" class="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg max-w-md w-full p-6">
        <h3 class="text-lg font-medium text-gray-900 mb-4">Edit User</h3>

        <div v-if="editError" class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md mb-4">
          {{ editError }}
        </div>

        <form @submit.prevent="saveUserEdit" class="space-y-4">
          <div>
            <label for="fullName" class="block text-sm font-medium text-gray-700">Full Name</label>
            <input
              id="fullName"
              v-model="editForm.fullName"
              type="text"
              class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div>
            <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
            <input
              id="email"
              v-model="editForm.email"
              type="email"
              class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div>
            <label for="avatar" class="block text-sm font-medium text-gray-700">Avatar URL</label>
            <input
              id="avatar"
              v-model="editForm.avatar"
              type="text"
              class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div class="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              @click="showEditModal = false"
              class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="editLoading"
              class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <span v-if="editLoading">Saving...</span>
              <span v-else>Save</span>
            </button>
          </div>
        </form>
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
const users = ref<any[]>([])
const loading = ref(true)
const error = ref('')
const searchQuery = ref('')

// Edit modal state
const showEditModal = ref(false)
const editForm = ref({
  id: '',
  fullName: '',
  email: '',
  avatar: ''
})
const editLoading = ref(false)
const editError = ref('')

// Computed
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value

  const query = searchQuery.value.toLowerCase()
  return users.value.filter(user =>
    user.fullName.toLowerCase().includes(query) ||
    user.username.toLowerCase().includes(query) ||
    user.email.toLowerCase().includes(query) ||
    user.role.toLowerCase().includes(query)
  )
})

// Fetch users
async function fetchUsers() {
  try {
    loading.value = true
    error.value = ''

    const response = await api.get('/admin/users', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    users.value = response.data
  } catch (err: any) {
    console.error('Error fetching users:', err)
    error.value = err.message || 'Failed to load users'
  } finally {
    loading.value = false
  }
}

// User actions
function editUser(user: any) {
  editForm.value = {
    id: user.id,
    fullName: user.fullName,
    email: user.email,
    avatar: user.avatar || ''
  }
  showEditModal.value = true
}

async function saveUserEdit() {
  try {
    editLoading.value = true
    editError.value = ''

    const response = await api.put(`/admin/users/${editForm.value.id}`, {
      fullName: editForm.value.fullName,
      email: editForm.value.email,
      avatar: editForm.value.avatar || null
    }, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Update user in the list
    const index = users.value.findIndex(u => u.id === editForm.value.id)
    if (index !== -1) {
      users.value[index] = { ...users.value[index], ...response.data.data }
    }

    showEditModal.value = false
    toast.success('User updated successfully')
  } catch (err: any) {
    console.error('Error updating user:', err)
    editError.value = err.message || 'Failed to update user'
  } finally {
    editLoading.value = false
  }
}

async function activateUser(user: any) {
  try {
    const response = await api.put(`/admin/users/${user.id}/activate`, {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Update user in the list
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value[index].active = true
    }

    toast.success(`User ${user.username} has been activated`)
  } catch (err: any) {
    console.error('Error activating user:', err)
    toast.error(err.message || 'Failed to activate user')
  }
}

async function deactivateUser(user: any) {
  // Prevent deactivating yourself
  if (user.id === authStore.user?.id) {
    toast.error('You cannot deactivate your own account')
    return
  }

  try {
    const response = await api.put(`/admin/users/${user.id}/deactivate`, {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Update user in the list
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value[index].active = false
    }

    toast.success(`User ${user.username} has been deactivated`)
  } catch (err: any) {
    console.error('Error deactivating user:', err)
    toast.error(err.message || 'Failed to deactivate user')
  }
}

async function promoteToAdmin(user: any) {
  try {
    const response = await api.put(`/admin/users/${user.id}/promote-to-admin`, {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Update user in the list
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value[index].role = 'ADMIN'
    }

    toast.success(`User ${user.username} has been promoted to admin`)
  } catch (err: any) {
    console.error('Error promoting user:', err)
    toast.error(err.message || 'Failed to promote user')
  }
}

async function demoteToUser(user: any) {
  // Prevent demoting yourself
  if (user.id === authStore.user?.id) {
    toast.error('You cannot demote your own account')
    return
  }

  try {
    const response = await api.put(`/admin/users/${user.id}/demote-to-user`, {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Update user in the list
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value[index].role = 'USER'
    }

    toast.success(`User ${user.username} has been demoted to regular user`)
  } catch (err: any) {
    console.error('Error demoting user:', err)
    toast.error(err.message || 'Failed to demote user')
  }
}

// Lifecycle
onMounted(() => {
  fetchUsers()
})
</script>
