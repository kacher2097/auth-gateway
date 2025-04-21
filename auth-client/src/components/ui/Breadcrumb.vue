<template>
  <nav class="flex" aria-label="Breadcrumb">
    <ol class="inline-flex items-center space-x-1 md:space-x-3">
      <li class="inline-flex items-center">
        <router-link to="/" class="inline-flex items-center text-sm font-medium text-blue-600 hover:text-blue-800 transition-colors">
          <svg class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
            <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"></path>
          </svg>
          Home
        </router-link>
      </li>

      <li v-for="(item, index) in items" :key="index">
        <div class="flex items-center">
          <svg class="w-5 h-5 text-gray-400" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd"></path>
          </svg>
          <router-link
            v-if="!item.active && item.to"
            :to="item.to"
            class="ml-1 text-sm font-medium text-blue-600 hover:text-blue-800 transition-colors md:ml-2"
          >
            {{ item.label || item.text }}
          </router-link>
          <span
            v-else
            class="ml-1 text-sm font-medium text-gray-700 md:ml-2"
          >
            {{ item.label || item.text }}
          </span>
        </div>
      </li>
    </ol>
  </nav>
</template>

<script setup lang="ts">
defineProps({
  items: {
    type: Array as () => Array<{
      text?: string;
      label?: string;
      to?: string;
      active?: boolean;
    }>,
    required: true
  }
})
</script>
