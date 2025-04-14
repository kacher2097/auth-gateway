<template>
  <AdminLayout title="Add New Proxy">
    <div class="new-proxy">
    <div class="header">
      <h1 class="text-2xl font-bold">Add New Proxy</h1>
      <router-link to="/admin/proxies" class="btn btn-outline">
        <i class="fas fa-arrow-left mr-2"></i> Back to List
      </router-link>
    </div>

    <div class="card">
      <div class="alert info mb-6">
        <i class="fas fa-info-circle mr-2"></i>
        <div>
          <div class="font-medium">Proxy Verification</div>
          <div>The system will automatically check if the proxy is working before adding it to the database.</div>
        </div>
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="ipAddress">IP Address</label>
          <div class="input-with-icon">
            <i class="fas fa-globe"></i>
            <input
              type="text"
              id="ipAddress"
              v-model="form.ipAddress"
              placeholder="e.g. 192.168.1.1"
              required
              pattern="^(\d{1,3}\.){3}\d{1,3}$"
              title="Please enter a valid IP address"
              class="form-input"
            />
          </div>
          <div v-if="errors.ipAddress" class="error-message">{{ errors.ipAddress }}</div>
        </div>

        <div class="form-group">
          <label for="port">Port</label>
          <input
            type="number"
            id="port"
            v-model.number="form.port"
            placeholder="e.g. 8080"
            required
            min="1"
            max="65535"
            class="form-input"
          />
          <div v-if="errors.port" class="error-message">{{ errors.port }}</div>
        </div>

        <div class="form-group">
          <label for="protocol">Protocol</label>
          <select
            id="protocol"
            v-model="form.protocol"
            required
            class="form-select"
          >
            <option v-for="protocol in protocols" :key="protocol" :value="protocol">
              {{ protocol }}
            </option>
          </select>
          <div v-if="errors.protocol" class="error-message">{{ errors.protocol }}</div>
        </div>

        <div class="form-group">
          <label for="country">Country (Optional)</label>
          <input
            type="text"
            id="country"
            v-model="form.country"
            placeholder="e.g. United States"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label for="city">City (Optional)</label>
          <input
            type="text"
            id="city"
            v-model="form.city"
            placeholder="e.g. New York"
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label for="notes">Notes (Optional)</label>
          <textarea
            id="notes"
            v-model="form.notes"
            placeholder="Add any additional information about this proxy"
            rows="3"
            class="form-textarea"
          ></textarea>
        </div>

        <div class="divider"></div>

        <div class="flex space-x-2">
          <button type="submit" class="btn btn-primary" :disabled="submitting">
            <i class="fas fa-save mr-2"></i> Add Proxy
          </button>
          <button type="button" @click="resetForm" class="btn btn-outline">
            Reset
          </button>
        </div>
      </form>
    </div>

    <div class="card mt-6">
      <h2 class="text-xl font-bold mb-4">
        <i class="fas fa-info-circle mr-2"></i> About Free Proxies
      </h2>
      <p class="mb-4">
        Free proxies are public proxy servers that can be used to route your internet traffic through a different IP address.
        They can be useful for testing, web scraping, or accessing geo-restricted content.
      </p>
      <p class="mb-2">
        <strong>Note:</strong> Free proxies may have limitations such as:
      </p>
      <ul class="list-disc pl-6 mb-4">
        <li>Slower connection speeds</li>
        <li>Intermittent availability</li>
        <li>Limited bandwidth</li>
        <li>Potential security risks</li>
      </ul>
      <p>
        Always verify that a proxy is working before using it for important tasks.
      </p>
    </div>
    </div>
  </AdminLayout>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import proxyService from '@/services/proxy.service'
import type { ProxyRequest, ProxyProtocol } from '@/types/proxy'
import { PROXY_PROTOCOLS } from '@/types/proxy'
import AdminLayout from '@/components/layout/AdminLayout.vue'

export default defineComponent({
  name: 'NewProxy',
  components: {
    AdminLayout
  },
  setup() {
    const router = useRouter()
    const submitting = ref(false)

    const form = reactive<ProxyRequest>({
      ipAddress: '',
      port: 0,
      protocol: 'HTTP',
      country: '',
      city: '',
      notes: ''
    })

    const errors = reactive({
      ipAddress: '',
      port: '',
      protocol: ''
    })

    const validateForm = (): boolean => {
      let isValid = true

      // Reset errors
      errors.ipAddress = ''
      errors.port = ''
      errors.protocol = ''

      // Validate IP address
      const ipRegex = /^(\d{1,3}\.){3}\d{1,3}$/
      if (!ipRegex.test(form.ipAddress)) {
        errors.ipAddress = 'Please enter a valid IP address'
        isValid = false
      }

      // Validate port
      if (!form.port || form.port < 1 || form.port > 65535) {
        errors.port = 'Port must be between 1 and 65535'
        isValid = false
      }

      // Validate protocol
      if (!PROXY_PROTOCOLS.includes(form.protocol as ProxyProtocol)) {
        errors.protocol = 'Please select a valid protocol'
        isValid = false
      }

      return isValid
    }

    const handleSubmit = async () => {
      if (!validateForm()) return

      submitting.value = true
      try {
        await proxyService.createProxy(form)
        alert('Proxy added successfully')
        router.push('/admin/proxies')
      } catch (error) {
        console.error('Error adding proxy:', error)
        alert('Failed to add proxy')
      } finally {
        submitting.value = false
      }
    }

    const resetForm = () => {
      form.ipAddress = ''
      form.port = 0
      form.protocol = 'HTTP'
      form.country = ''
      form.city = ''
      form.notes = ''

      errors.ipAddress = ''
      errors.port = ''
      errors.protocol = ''
    }

    return {
      form,
      errors,
      submitting,
      protocols: PROXY_PROTOCOLS,
      handleSubmit,
      resetForm
    }
  }
})
</script>

<style scoped>
.header {
  @apply flex justify-between items-center mb-6;
}

.card {
  @apply bg-white p-6 rounded-lg shadow-md;
}

.alert {
  @apply p-4 rounded-md flex items-start;
}

.alert.info {
  @apply bg-blue-50 text-blue-800;
}

.btn {
  @apply px-4 py-2 rounded-md font-medium transition-colors duration-200;
}

.btn-primary {
  @apply bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed;
}

.btn-outline {
  @apply border border-gray-300 hover:bg-gray-100;
}

.form-group {
  @apply mb-4;
}

.form-group label {
  @apply block text-sm font-medium text-gray-700 mb-1;
}

.form-input, .form-select, .form-textarea {
  @apply w-full px-3 py-2 border rounded-md;
}

.input-with-icon {
  @apply relative;
}

.input-with-icon i {
  @apply absolute left-3 top-3 text-gray-400;
}

.input-with-icon input {
  @apply pl-10;
}

.error-message {
  @apply text-red-600 text-sm mt-1;
}

.divider {
  @apply my-6 border-t border-gray-200;
}
</style>
