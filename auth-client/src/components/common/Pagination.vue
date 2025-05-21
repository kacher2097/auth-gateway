<template>
  <div class="flex items-center justify-between px-4 py-3 sm:px-6">
    <div class="flex flex-1 justify-between sm:hidden">
      <button
        @click="onPrevious"
        :disabled="currentPage === 0"
        :class="[
          'relative inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700',
          currentPage === 0 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-50'
        ]"
      >
        Previous
      </button>
      <button
        @click="onNext"
        :disabled="currentPage >= totalPages - 1"
        :class="[
          'relative ml-3 inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700',
          currentPage >= totalPages - 1 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-50'
        ]"
      >
        Next
      </button>
    </div>
    <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
      <div>
        <p class="text-sm text-gray-700">
          Showing
          <span class="font-medium">{{ startItem }}</span>
          to
          <span class="font-medium">{{ endItem }}</span>
          of
          <span class="font-medium">{{ totalItems }}</span>
          results
        </p>
      </div>
      <div>
        <nav class="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
          <button
            @click="onPrevious"
            :disabled="currentPage === 0"
            :class="[
              'relative inline-flex items-center rounded-l-md px-2 py-2 text-gray-400',
              currentPage === 0 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-50'
            ]"
          >
            <span class="sr-only">Previous</span>
            <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
              <path fill-rule="evenodd" d="M12.79 5.23a.75.75 0 01-.02 1.06L8.832 10l3.938 3.71a.75.75 0 11-1.04 1.08l-4.5-4.25a.75.75 0 010-1.08l4.5-4.25a.75.75 0 011.06.02z" clip-rule="evenodd" />
            </svg>
          </button>
          
          <!-- Page numbers -->
          <template v-for="page in displayedPages" :key="page">
            <button
              v-if="page !== '...'"
              @click="onPageChange(Number(page))"
              :class="[
                'relative inline-flex items-center px-4 py-2 text-sm font-semibold',
                page === currentPage
                  ? 'z-10 bg-blue-600 text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600'
                  : 'text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 focus:outline-offset-0'
              ]"
            >
              {{ Number(page) + 1 }}
            </button>
            <span
              v-else
              class="relative inline-flex items-center px-4 py-2 text-sm font-semibold text-gray-700 ring-1 ring-inset ring-gray-300"
            >
              ...
            </span>
          </template>
          
          <button
            @click="onNext"
            :disabled="currentPage >= totalPages - 1"
            :class="[
              'relative inline-flex items-center rounded-r-md px-2 py-2 text-gray-400',
              currentPage >= totalPages - 1 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-50'
            ]"
          >
            <span class="sr-only">Next</span>
            <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
              <path fill-rule="evenodd" d="M7.21 14.77a.75.75 0 01.02-1.06L11.168 10 7.23 6.29a.75.75 0 111.04-1.08l4.5 4.25a.75.75 0 010 1.08l-4.5 4.25a.75.75 0 01-1.06-.02z" clip-rule="evenodd" />
            </svg>
          </button>
        </nav>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  currentPage: number
  pageSize: number
  totalItems: number
  totalPages: number
}>()

const emit = defineEmits<{
  (e: 'page-change', page: number): void
}>()

// Calculate start and end items for display
const startItem = computed(() => props.currentPage * props.pageSize + 1)
const endItem = computed(() => Math.min((props.currentPage + 1) * props.pageSize, props.totalItems))

// Generate array of page numbers to display
const displayedPages = computed(() => {
  const totalPages = props.totalPages
  const currentPage = props.currentPage
  
  if (totalPages <= 7) {
    // If we have 7 or fewer pages, show all pages
    return Array.from({ length: totalPages }, (_, i) => i)
  }
  
  // Always show first and last page
  const pages: (number | string)[] = []
  
  // Always include first page
  pages.push(0)
  
  if (currentPage > 3) {
    // Add ellipsis if current page is far from the start
    pages.push('...')
  }
  
  // Add pages around current page
  const start = Math.max(1, currentPage - 1)
  const end = Math.min(totalPages - 2, currentPage + 1)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  if (currentPage < totalPages - 4) {
    // Add ellipsis if current page is far from the end
    pages.push('...')
  }
  
  // Always include last page
  pages.push(totalPages - 1)
  
  return pages
})

// Event handlers
const onPrevious = () => {
  if (props.currentPage > 0) {
    emit('page-change', props.currentPage - 1)
  }
}

const onNext = () => {
  if (props.currentPage < props.totalPages - 1) {
    emit('page-change', props.currentPage + 1)
  }
}

const onPageChange = (page: number) => {
  emit('page-change', page)
}
</script>
