<template>
  <div class="min-h-screen">
    <template v-if="!isAdminRoute">
      <div class="min-h-screen bg-gray-50 flex flex-col">
        <Navbar />
        <main class="container mx-auto px-4 py-8 flex-grow">
          <router-view />
        </main>
        <footer class="bg-gray-100 py-4">
          <div class="container mx-auto px-4 text-center text-gray-600">
            &copy; {{ new Date().getFullYear() }} AuthenHub. All rights reserved.
          </div>
        </footer>
      </div>
    </template>

    <template v-else>
      <router-view />
    </template>

    <!-- Toast Container -->
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '@/components/layout/Navbar.vue'
import ToastContainer from '@/components/ui/ToastContainer.vue'

const route = useRoute()

// Check if current route is an admin route
const isAdminRoute = computed(() => {
  return route.path.startsWith('/admin')
})
</script>
