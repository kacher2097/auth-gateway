<template>
  <div class="flex h-screen overflow-hidden bg-gray-50">
    <Sidebar ref="sidebar" />

    <div class="flex-1 overflow-auto transition-all duration-300" :class="sidebarClass">
      <header class="bg-white shadow-sm sticky top-0 z-10">
        <div class="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
          <h1 class="text-2xl font-bold text-gray-900 flex items-center">
            <span class="inline-flex items-center justify-center p-2 bg-blue-600 rounded-md text-white mr-3">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h7" />
              </svg>
            </span>
            {{ title }}
          </h1>
          <div class="flex items-center space-x-4">
            <slot name="actions"></slot>
          </div>
        </div>
      </header>
      <main class="pb-6">
        <div class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
          <slot></slot>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import Sidebar from './Sidebar.vue'

defineProps({
  title: {
    type: String,
    required: true
  }
})

const sidebar = ref(null)
const sidebarClass = computed(() => {
  if (!sidebar.value) return ''
  return sidebar.value.isCollapsed ? 'lg:ml-20' : 'lg:ml-64'
})

// Watch for sidebar collapse state changes
onMounted(() => {
  // Force a re-render to get the correct sidebar state
  setTimeout(() => {
    // This is a hack to force Vue to re-evaluate the computed property
    sidebar.value = { ...sidebar.value }
  }, 0)
})
</script>
