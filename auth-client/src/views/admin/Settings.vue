<template>
  <AdminLayout title="Admin Settings">
    <div class="space-y-6">
      <!-- System Settings -->
      <div class="bg-white shadow rounded-lg overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-700">System Settings</h3>
        </div>
        <div class="p-6">
          <form @submit.prevent="saveSystemSettings" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label for="siteName" class="block text-sm font-medium text-gray-700">Site Name</label>
                <input
                  id="siteName"
                  v-model="systemSettings.siteName"
                  type="text"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
              </div>

              <div>
                <label for="siteUrl" class="block text-sm font-medium text-gray-700">Site URL</label>
                <input
                  id="siteUrl"
                  v-model="systemSettings.siteUrl"
                  type="url"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
              </div>

              <div>
                <label for="adminEmail" class="block text-sm font-medium text-gray-700">Admin Email</label>
                <input
                  id="adminEmail"
                  v-model="systemSettings.adminEmail"
                  type="email"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
              </div>

              <div>
                <label for="defaultUserRole" class="block text-sm font-medium text-gray-700">Default User Role</label>
                <select
                  id="defaultUserRole"
                  v-model="systemSettings.defaultUserRole"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="USER">User</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>
            </div>

            <div class="flex justify-end">
              <button
                type="submit"
                :disabled="systemSettingsLoading"
                class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                <span v-if="systemSettingsLoading">Saving...</span>
                <span v-else>Save System Settings</span>
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Authentication Settings -->
      <div class="bg-white shadow rounded-lg overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-700">Authentication Settings</h3>
        </div>
        <div class="p-6">
          <form @submit.prevent="saveAuthSettings" class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label for="tokenExpiration" class="block text-sm font-medium text-gray-700">Token Expiration (days)</label>
                <input
                  id="tokenExpiration"
                  v-model="authSettings.tokenExpiration"
                  type="number"
                  min="1"
                  max="30"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
              </div>

              <div>
                <label for="passwordResetExpiration" class="block text-sm font-medium text-gray-700">Password Reset Expiration (hours)</label>
                <input
                  id="passwordResetExpiration"
                  v-model="authSettings.passwordResetExpiration"
                  type="number"
                  min="1"
                  max="72"
                  class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                />
              </div>

              <div class="col-span-2">
                <div class="flex items-start">
                  <div class="flex items-center h-5">
                    <input
                      id="allowSocialLogin"
                      v-model="authSettings.allowSocialLogin"
                      type="checkbox"
                      class="focus:ring-blue-500 h-4 w-4 text-blue-600 border-gray-300 rounded"
                    />
                  </div>
                  <div class="ml-3 text-sm">
                    <label for="allowSocialLogin" class="font-medium text-gray-700">Allow Social Login</label>
                    <p class="text-gray-500">Enable login with Google and Facebook</p>
                  </div>
                </div>
              </div>

              <div class="col-span-2">
                <div class="flex items-start">
                  <div class="flex items-center h-5">
                    <input
                      id="requireEmailVerification"
                      v-model="authSettings.requireEmailVerification"
                      type="checkbox"
                      class="focus:ring-blue-500 h-4 w-4 text-blue-600 border-gray-300 rounded"
                    />
                  </div>
                  <div class="ml-3 text-sm">
                    <label for="requireEmailVerification" class="font-medium text-gray-700">Require Email Verification</label>
                    <p class="text-gray-500">Users must verify their email address before they can log in</p>
                  </div>
                </div>
              </div>
            </div>

            <div class="flex justify-end">
              <button
                type="submit"
                :disabled="authSettingsLoading"
                class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                <span v-if="authSettingsLoading">Saving...</span>
                <span v-else>Save Auth Settings</span>
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- Maintenance Mode -->
      <div class="bg-white shadow rounded-lg overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-700">Maintenance Mode</h3>
        </div>
        <div class="p-6">
          <div class="flex items-start mb-6">
            <div class="flex items-center h-5">
              <input
                id="maintenanceMode"
                v-model="maintenanceMode"
                type="checkbox"
                class="focus:ring-blue-500 h-4 w-4 text-blue-600 border-gray-300 rounded"
              />
            </div>
            <div class="ml-3 text-sm">
              <label for="maintenanceMode" class="font-medium text-gray-700">Enable Maintenance Mode</label>
              <p class="text-gray-500">When enabled, only administrators can access the site</p>
            </div>
          </div>

          <div v-if="maintenanceMode">
            <label for="maintenanceMessage" class="block text-sm font-medium text-gray-700">Maintenance Message</label>
            <textarea
              id="maintenanceMessage"
              v-model="maintenanceMessage"
              rows="3"
              class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
              placeholder="Enter a message to display to users during maintenance"
            ></textarea>
          </div>

          <div class="mt-6 flex justify-end">
            <button
              @click="saveMaintenanceSettings"
              :disabled="maintenanceLoading"
              class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <span v-if="maintenanceLoading">Saving...</span>
              <span v-else>Save Maintenance Settings</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { useToast } from '@/components/ui/ToastContainer.vue'

const authStore = useAuthStore()
const toast = useToast()

// System Settings
const systemSettings = ref({
  siteName: 'AuthenHub',
  siteUrl: 'http://localhost:5173',
  adminEmail: '',
  defaultUserRole: 'USER'
})
const systemSettingsLoading = ref(false)

// Auth Settings
const authSettings = ref({
  tokenExpiration: 7,
  passwordResetExpiration: 24,
  allowSocialLogin: true,
  requireEmailVerification: false
})
const authSettingsLoading = ref(false)

// Maintenance Settings
const maintenanceMode = ref(false)
const maintenanceMessage = ref('')
const maintenanceLoading = ref(false)

// Load settings
function loadSettings() {
  // In a real app, you would fetch these from your API
  // For now, we'll just use the current user's email as admin email
  if (authStore.user?.email) {
    systemSettings.value.adminEmail = authStore.user.email
  }

  // Simulate loading from localStorage for demo purposes
  const savedSystemSettings = localStorage.getItem('admin-system-settings')
  if (savedSystemSettings) {
    try {
      systemSettings.value = JSON.parse(savedSystemSettings)
    } catch (e) {
      console.error('Error parsing system settings:', e)
    }
  }

  const savedAuthSettings = localStorage.getItem('admin-auth-settings')
  if (savedAuthSettings) {
    try {
      authSettings.value = JSON.parse(savedAuthSettings)
    } catch (e) {
      console.error('Error parsing auth settings:', e)
    }
  }

  maintenanceMode.value = localStorage.getItem('maintenance-mode') === 'true'
  maintenanceMessage.value = localStorage.getItem('maintenance-message') || ''
}

// Save settings
async function saveSystemSettings() {
  systemSettingsLoading.value = true

  try {
    // In a real app, you would send these to your API
    // For demo purposes, we'll just save to localStorage
    localStorage.setItem('admin-system-settings', JSON.stringify(systemSettings.value))

    toast.success('System settings saved successfully')
  } catch (err: any) {
    toast.error('Failed to save system settings')
    console.error('Error saving system settings:', err)
  } finally {
    systemSettingsLoading.value = false
  }
}

async function saveAuthSettings() {
  authSettingsLoading.value = true

  try {
    // In a real app, you would send these to your API
    localStorage.setItem('admin-auth-settings', JSON.stringify(authSettings.value))

    toast.success('Authentication settings saved successfully')
  } catch (err: any) {
    toast.error('Failed to save authentication settings')
    console.error('Error saving auth settings:', err)
  } finally {
    authSettingsLoading.value = false
  }
}

async function saveMaintenanceSettings() {
  maintenanceLoading.value = true

  try {
    // In a real app, you would send these to your API
    localStorage.setItem('maintenance-mode', maintenanceMode.value.toString())
    localStorage.setItem('maintenance-message', maintenanceMessage.value)

    toast.success('Maintenance settings saved successfully')
  } catch (err: any) {
    toast.error('Failed to save maintenance settings')
    console.error('Error saving maintenance settings:', err)
  } finally {
    maintenanceLoading.value = false
  }
}

// Lifecycle
onMounted(() => {
  loadSettings()
})
</script>
