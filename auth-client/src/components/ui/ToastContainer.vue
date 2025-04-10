<template>
  <div aria-live="assertive" class="fixed inset-0 flex items-end px-4 py-6 pointer-events-none sm:p-6 sm:items-start z-50">
    <div class="w-full flex flex-col items-center space-y-4 sm:items-end">
      <Toast
        v-for="toast in toasts"
        :key="toast.id"
        :id="toast.id"
        :title="toast.title"
        :message="toast.message"
        :type="toast.type"
        :duration="toast.duration"
        :auto-close="toast.autoClose"
        @close="removeToast"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue'

export interface ToastOptions {
  id?: string
  title?: string
  message: string
  type?: 'default' | 'success' | 'error' | 'warning' | 'info'
  duration?: number
  autoClose?: boolean
}

export interface ToastItem extends ToastOptions {
  id: string
}

// Create toast methods for composition API usage
export const useToast = () => {
  return {
    show: (options: ToastOptions) => window.toast?.show(options) || '',
    success: (message: string, title?: string, options?: Partial<ToastOptions>) =>
      window.toast?.success(message, title, options) || '',
    error: (message: string, title?: string, options?: Partial<ToastOptions>) =>
      window.toast?.error(message, title, options) || '',
    warning: (message: string, title?: string, options?: Partial<ToastOptions>) =>
      window.toast?.warning(message, title, options) || '',
    info: (message: string, title?: string, options?: Partial<ToastOptions>) =>
      window.toast?.info(message, title, options) || '',
    clear: () => window.toast?.clear()
  }
}

// Define toast on window
declare global {
  interface Window {
    toast: {
      show: (options: ToastOptions) => string
      success: (message: string, title?: string, options?: Partial<ToastOptions>) => string
      error: (message: string, title?: string, options?: Partial<ToastOptions>) => string
      warning: (message: string, title?: string, options?: Partial<ToastOptions>) => string
      info: (message: string, title?: string, options?: Partial<ToastOptions>) => string
      clear: () => void
    }
  }
}
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Toast from './Toast.vue'

const toasts = ref<ToastItem[]>([])

function addToast(options: ToastOptions): string {
  const id = options.id || `toast-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`

  toasts.value.push({
    id,
    title: options.title || '',
    message: options.message,
    type: options.type || 'default',
    duration: options.duration !== undefined ? options.duration : 5000,
    autoClose: options.autoClose !== undefined ? options.autoClose : true
  })

  return id
}

function removeToast(id: string) {
  const index = toasts.value.findIndex(toast => toast.id === id)
  if (index !== -1) {
    toasts.value.splice(index, 1)
  }
}

// Create global toast methods
const toastMethods = {
  show: (options: ToastOptions) => addToast(options),
  success: (message: string, title?: string, options?: Partial<ToastOptions>) =>
    addToast({ message, title, type: 'success', ...options }),
  error: (message: string, title?: string, options?: Partial<ToastOptions>) =>
    addToast({ message, title, type: 'error', ...options }),
  warning: (message: string, title?: string, options?: Partial<ToastOptions>) =>
    addToast({ message, title, type: 'warning', ...options }),
  info: (message: string, title?: string, options?: Partial<ToastOptions>) =>
    addToast({ message, title, type: 'info', ...options }),
  clear: () => { toasts.value = [] }
}

// Add toast methods to window for global access
onMounted(() => {
  window.toast = toastMethods
})
</script>
