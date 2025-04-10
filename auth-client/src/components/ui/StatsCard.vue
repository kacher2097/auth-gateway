<template>
  <div class="bg-white rounded-lg shadow p-6">
    <div class="flex items-center">
      <div 
        class="p-3 rounded-full"
        :class="{
          'bg-blue-100 text-blue-600': color === 'blue',
          'bg-green-100 text-green-600': color === 'green',
          'bg-purple-100 text-purple-600': color === 'purple',
          'bg-orange-100 text-orange-600': color === 'orange'
        }"
      >
        <component 
          :is="iconComponent"
          class="h-6 w-6"
        />
      </div>
      <div class="ml-4">
        <h2 class="text-sm font-medium text-gray-500">{{ title }}</h2>
        <p class="text-2xl font-semibold text-gray-900">{{ formattedValue }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import * as Icons from '@heroicons/vue/outline'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: Number,
    required: true
  },
  icon: {
    type: String,
    required: true
  },
  color: {
    type: String,
    default: 'blue',
    validator: (value: string) => ['blue', 'green', 'purple', 'orange'].includes(value)
  }
})

const iconComponent = computed(() => {
  const iconName = props.icon.split('-')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join('')
  return Icons[`${iconName}Icon`]
})

const formattedValue = computed(() => {
  return new Intl.NumberFormat().format(props.value)
})
</script>