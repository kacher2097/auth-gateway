<template>
  <div class="max-w-4xl mx-auto">
    <h1 class="text-3xl font-bold text-gray-900 mb-8">Your Profile</h1>

    <div class="bg-white shadow rounded-lg overflow-hidden">
      <div class="md:flex">
        <div class="md:w-1/3 bg-gray-50 p-8 flex flex-col items-center">
          <div v-if="authStore.user?.avatar" class="h-32 w-32 rounded-full overflow-hidden mb-4">
            <img
              :src="authStore.user.avatar"
              alt="User avatar"
              class="h-full w-full object-cover"
            />
          </div>
          <div v-else class="h-32 w-32 rounded-full bg-blue-500 flex items-center justify-center text-white text-4xl mb-4">
            {{ authStore.user?.fullName.charAt(0) }}
          </div>

          <h2 class="text-xl font-semibold text-gray-900">{{ authStore.user?.fullName }}</h2>
          <p class="text-gray-500 mb-4">{{ authStore.user?.email }}</p>

          <div class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium"
               :class="authStore.user?.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'">
            {{ authStore.user?.role }}
          </div>
        </div>

        <div class="md:w-2/3 p-8">
          <h3 class="text-xl font-semibold text-gray-900 mb-4">Account Information</h3>

          <div class="space-y-4">
            <div>
              <p class="text-sm font-medium text-gray-500">Username</p>
              <p class="mt-1 text-gray-900">{{ authStore.user?.username }}</p>
            </div>

            <div>
              <p class="text-sm font-medium text-gray-500">Full Name</p>
              <p class="mt-1 text-gray-900">{{ authStore.user?.fullName }}</p>
            </div>

            <div>
              <p class="text-sm font-medium text-gray-500">Email</p>
              <p class="mt-1 text-gray-900">{{ authStore.user?.email }}</p>
            </div>

            <div>
              <p class="text-sm font-medium text-gray-500">Account ID</p>
              <p class="mt-1 text-gray-900">{{ authStore.user?.id }}</p>
            </div>
          </div>

          <div class="mt-8">
            <h3 class="text-xl font-semibold text-gray-900 mb-4">Security</h3>

            <ChangePasswordForm v-if="!authStore.user?.socialProvider" />

            <div v-else class="bg-blue-50 border border-blue-200 text-blue-600 px-4 py-3 rounded-md">
              <p>You're signed in with {{ authStore.user.socialProvider }}. Password management is not available for social login accounts.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="mt-8 bg-white shadow rounded-lg overflow-hidden">
      <div class="p-8">
        <h3 class="text-xl font-semibold text-gray-900 mb-4">Session Information</h3>

        <div class="space-y-4">
          <div>
            <p class="text-sm font-medium text-gray-500">Authentication Method</p>
            <p class="mt-1 text-gray-900">
              {{ authStore.user?.socialProvider ? `Social Login (${authStore.user.socialProvider})` : 'Username & Password' }}
            </p>
          </div>

          <div>
            <p class="text-sm font-medium text-gray-500">Token Expiration</p>
            <p class="mt-1 text-gray-900">
              {{ tokenExpirationDate ? tokenExpirationDate.toLocaleString() : 'Unknown' }}
            </p>
          </div>
        </div>

        <div class="mt-6">
          <button
            class="btn btn-primary bg-red-600 hover:bg-red-700"
            @click="handleLogout"
          >
            Sign Out
          </button>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import ChangePasswordForm from '@/components/profile/ChangePasswordForm.vue'

const authStore = useAuthStore()
const router = useRouter()



// Mock token expiration date (in a real app, you would decode this from the JWT)
const tokenExpirationDate = computed(() => {
  if (!authStore.token) return null
  // Assuming token expires in 7 days from now (just for demonstration)
  const date = new Date()
  date.setDate(date.getDate() + 7)
  return date
})

function handleLogout() {
  authStore.logout()
  router.push('/')
}


</script>
