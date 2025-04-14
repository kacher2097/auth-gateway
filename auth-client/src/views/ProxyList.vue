<template>
  <AdminLayout title="Proxy Management">
    <div class="proxy-list">
    <div class="header">
      <h1 class="text-2xl font-bold">Proxy Management</h1>
      <div class="flex space-x-2">
        <router-link to="/admin/proxies/import" class="btn btn-outline">
          <i class="fas fa-upload mr-2"></i> Import Proxies
        </router-link>
        <router-link to="/admin/proxies/new" class="btn btn-primary">
          <i class="fas fa-plus mr-2"></i> Add New Proxy
        </router-link>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="stat-card">
        <div class="stat-title">Total Proxies</div>
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-icon"><i class="fas fa-globe"></i></div>
      </div>
      <div class="stat-card">
        <div class="stat-title">Active Proxies</div>
        <div class="stat-value text-green-600">{{ stats.active }}</div>
        <div class="stat-desc" v-if="stats.total > 0">{{ Math.round(stats.active / stats.total * 100) }}%</div>
        <div class="stat-icon"><i class="fas fa-check-circle text-green-600"></i></div>
      </div>
      <div class="stat-card">
        <div class="stat-title">Avg Response Time</div>
        <div class="stat-value" :class="stats.avgResponseTime < 500 ? 'text-green-600' : 'text-red-600'">
          {{ stats.avgResponseTime }}ms
        </div>
        <div class="stat-icon"><i class="fas fa-tachometer-alt"></i></div>
      </div>
      <div class="stat-card">
        <div class="stat-title">Avg Uptime</div>
        <div class="stat-value" :class="stats.avgUptime > 90 ? 'text-green-600' : 'text-red-600'">
          {{ stats.avgUptime }}%
        </div>
        <div class="stat-icon"><i class="fas fa-chart-line"></i></div>
      </div>
    </div>

    <!-- Filter Tabs -->
    <div class="tabs mb-4">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-btn"
        :class="{ 'active': activeTab === tab.key }"
        @click="handleTabChange(tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Search and Refresh -->
    <div class="flex justify-between mb-4">
      <div class="search-box">
        <i class="fas fa-search"></i>
        <input
          type="text"
          v-model="searchText"
          placeholder="Search proxies..."
          class="search-input"
        />
      </div>
      <button @click="fetchProxies" class="btn btn-outline" :disabled="loading">
        <i class="fas fa-sync-alt" :class="{ 'fa-spin': loading }"></i> Refresh
      </button>
    </div>

    <!-- Proxies Table -->
    <div class="overflow-x-auto">
      <table class="table-auto w-full">
        <thead>
          <tr>
            <th class="px-4 py-2 text-left">Status</th>
            <th class="px-4 py-2 text-left">IP Address</th>
            <th class="px-4 py-2 text-left">Protocol</th>
            <th class="px-4 py-2 text-left">Performance</th>
            <th class="px-4 py-2 text-left">Last Checked</th>
            <th class="px-4 py-2 text-left">Actions</th>
          </tr>
        </thead>
        <tbody v-if="!loading">
          <tr v-for="proxy in filteredProxies" :key="proxy.id" class="border-b hover:bg-gray-50">
            <td class="px-4 py-2">
              <span
                class="status-badge"
                :class="proxy.isActive ? 'active' : 'inactive'"
              >
                {{ proxy.isActive ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-4 py-2">
              <div>
                <div class="font-medium">{{ proxy.ipAddress }}:{{ proxy.port }}</div>
                <div class="text-sm text-gray-500" v-if="proxy.country">
                  {{ proxy.country }}{{ proxy.city ? `, ${proxy.city}` : '' }}
                </div>
              </div>
            </td>
            <td class="px-4 py-2">
              <span
                class="protocol-badge"
                :class="{
                  'http': proxy.protocol === 'HTTP',
                  'https': proxy.protocol === 'HTTPS',
                  'socks4': proxy.protocol === 'SOCKS4',
                  'socks5': proxy.protocol === 'SOCKS5'
                }"
              >
                {{ proxy.protocol }}
              </span>
            </td>
            <td class="px-4 py-2">
              <div>
                <div>Response: <span class="font-medium">{{ proxy.responseTimeMs }}ms</span></div>
                <div>Uptime: <span class="font-medium">{{ proxy.uptime.toFixed(1) }}%</span></div>
              </div>
            </td>
            <td class="px-4 py-2">
              {{ formatDate(proxy.lastChecked) }}
            </td>
            <td class="px-4 py-2">
              <div class="flex space-x-2">
                <button
                  @click="checkProxy(proxy.id)"
                  class="btn btn-sm btn-outline"
                  :disabled="checkingId === proxy.id"
                >
                  <i
                    class="fas fa-sync-alt mr-1"
                    :class="{ 'fa-spin': checkingId === proxy.id }"
                  ></i>
                  Check
                </button>
                <button @click="editProxy(proxy)" class="btn btn-sm btn-outline" title="Edit Proxy">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click="confirmDelete(proxy.id)" class="btn btn-sm btn-danger" title="Delete Proxy">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="filteredProxies.length === 0">
            <td colspan="6" class="px-4 py-8 text-center text-gray-500">
              No proxies found. Try a different filter or add a new proxy.
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr>
            <td colspan="6" class="px-4 py-8 text-center">
              <div class="flex justify-center">
                <div class="loader"></div>
              </div>
              <div class="mt-2 text-gray-500">Loading proxies...</div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Edit Modal -->
    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="text-xl font-bold">Edit Proxy</h2>
          <button @click="showEditModal = false" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveEdit">
            <div class="form-group">
              <label for="ipAddress">IP Address</label>
              <input
                type="text"
                id="ipAddress"
                v-model="editForm.ipAddress"
                required
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label for="port">Port</label>
              <input
                type="number"
                id="port"
                v-model.number="editForm.port"
                required
                min="1"
                max="65535"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label for="protocol">Protocol</label>
              <select
                id="protocol"
                v-model="editForm.protocol"
                required
                class="form-select"
              >
                <option v-for="protocol in protocols" :key="protocol" :value="protocol">
                  {{ protocol }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label for="country">Country (Optional)</label>
              <input
                type="text"
                id="country"
                v-model="editForm.country"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label for="city">City (Optional)</label>
              <input
                type="text"
                id="city"
                v-model="editForm.city"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label for="notes">Notes (Optional)</label>
              <textarea
                id="notes"
                v-model="editForm.notes"
                rows="3"
                class="form-textarea"
              ></textarea>
            </div>
            <div class="modal-footer">
              <button type="button" @click="showEditModal = false" class="btn btn-outline">
                Cancel
              </button>
              <button type="submit" class="btn btn-primary" :disabled="saving">
                <i class="fas fa-save mr-2"></i> Save Changes
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="text-xl font-bold">Confirm Delete</h2>
          <button @click="showDeleteModal = false" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <p>Are you sure you want to delete this proxy? This action cannot be undone.</p>
        </div>
        <div class="modal-footer">
          <button @click="showDeleteModal = false" class="btn btn-outline">
            Cancel
          </button>
          <button @click="deleteProxy" class="btn btn-danger" :disabled="deleting">
            <i class="fas fa-trash mr-2"></i> Delete
          </button>
        </div>
      </div>
    </div>
    </div>
  </AdminLayout>
</template>

<script lang="ts">
import { defineComponent, ref, reactive, computed, onMounted } from 'vue'
import proxyService from '@/services/proxy.service'
import type { Proxy, ProxyRequest, ProxyProtocol } from '@/types/proxy'
import { PROXY_PROTOCOLS } from '@/types/proxy'
import AdminLayout from '@/components/layout/AdminLayout.vue'

export default defineComponent({
  name: 'ProxyList',
  components: {
    AdminLayout
  },
  setup() {
    // State
    const proxies = ref<Proxy[]>([])
    const loading = ref(false)
    const searchText = ref('')
    const activeTab = ref('all')
    const checkingId = ref<string | null>(null)
    const showEditModal = ref(false)
    const showDeleteModal = ref(false)
    const currentProxyId = ref<string | null>(null)
    const saving = ref(false)
    const deleting = ref(false)

    // Edit form
    const editForm = reactive<ProxyRequest>({
      ipAddress: '',
      port: 0,
      protocol: 'HTTP',
      country: '',
      city: '',
      notes: ''
    })

    // Stats
    const stats = reactive({
      total: 0,
      active: 0,
      inactive: 0,
      http: 0,
      https: 0,
      socks4: 0,
      socks5: 0,
      avgResponseTime: 0,
      avgUptime: 0
    })

    // Tabs
    const tabs = [
      { key: 'all', label: 'All Proxies' },
      { key: 'active', label: 'Active' },
      { key: 'http', label: 'HTTP' },
      { key: 'https', label: 'HTTPS' },
      { key: 'socks4', label: 'SOCKS4' },
      { key: 'socks5', label: 'SOCKS5' },
      { key: 'fast', label: 'Fast (<500ms)' },
      { key: 'reliable', label: 'Reliable (>95%)' }
    ]

    // Computed
    const filteredProxies = computed(() => {
      return proxies.value.filter(proxy =>
        proxy.ipAddress.toLowerCase().includes(searchText.value.toLowerCase()) ||
        proxy.protocol.toLowerCase().includes(searchText.value.toLowerCase()) ||
        (proxy.country && proxy.country.toLowerCase().includes(searchText.value.toLowerCase())) ||
        (proxy.city && proxy.city.toLowerCase().includes(searchText.value.toLowerCase())) ||
        (proxy.notes && proxy.notes.toLowerCase().includes(searchText.value.toLowerCase()))
      )
    })

    // Methods
    const fetchProxies = async () => {
      loading.value = true
      try {
        let data: Proxy[] = []

        switch (activeTab.value) {
          case 'active':
            data = await proxyService.getActiveProxies()
            break
          case 'http':
            data = await proxyService.getProxiesByProtocol('HTTP')
            break
          case 'https':
            data = await proxyService.getProxiesByProtocol('HTTPS')
            break
          case 'socks4':
            data = await proxyService.getProxiesByProtocol('SOCKS4')
            break
          case 'socks5':
            data = await proxyService.getProxiesByProtocol('SOCKS5')
            break
          case 'fast':
            data = await proxyService.getFastProxies(500)
            break
          case 'reliable':
            data = await proxyService.getReliableProxies(95)
            break
          default:
            data = await proxyService.getAllProxies()
        }

        proxies.value = data
        calculateStats()
      } catch (error) {
        console.error('Error fetching proxies:', error)
        alert('Failed to fetch proxies')
      } finally {
        loading.value = false
      }
    }

    const calculateStats = () => {
      const active = proxies.value.filter(p => p.isActive).length
      const http = proxies.value.filter(p => p.protocol === 'HTTP').length
      const https = proxies.value.filter(p => p.protocol === 'HTTPS').length
      const socks4 = proxies.value.filter(p => p.protocol === 'SOCKS4').length
      const socks5 = proxies.value.filter(p => p.protocol === 'SOCKS5').length

      const totalResponseTime = proxies.value.reduce((sum, p) => sum + p.responseTimeMs, 0)
      const avgResponseTime = proxies.value.length > 0 ? Math.round(totalResponseTime / proxies.value.length) : 0

      const totalUptime = proxies.value.reduce((sum, p) => sum + p.uptime, 0)
      const avgUptime = proxies.value.length > 0 ? Math.round(totalUptime / proxies.value.length * 10) / 10 : 0

      stats.total = proxies.value.length
      stats.active = active
      stats.inactive = proxies.value.length - active
      stats.http = http
      stats.https = https
      stats.socks4 = socks4
      stats.socks5 = socks5
      stats.avgResponseTime = avgResponseTime
      stats.avgUptime = avgUptime
    }

    const handleTabChange = (tab: string) => {
      activeTab.value = tab
      searchText.value = ''
      fetchProxies()
    }

    const checkProxy = async (id: string) => {
      checkingId.value = id
      try {
        const result = await proxyService.checkProxy(id)

        // Update the proxy in the list
        proxies.value = proxies.value.map(p => {
          if (p.id === id) {
            return {
              ...p,
              isActive: result.isWorking,
              responseTimeMs: result.responseTimeMs,
              lastChecked: result.checkedAt,
              successCount: result.isWorking ? p.successCount + 1 : p.successCount,
              failCount: result.isWorking ? p.failCount : p.failCount + 1,
              uptime: ((p.successCount + (result.isWorking ? 1 : 0)) /
                      (p.successCount + p.failCount + 1)) * 100
            }
          }
          return p
        })

        calculateStats()

        alert(`Proxy is ${result.isWorking ? 'working' : 'not working'}. Response time: ${result.responseTimeMs}ms`)
      } catch (error) {
        console.error('Error checking proxy:', error)
        alert('Failed to check proxy')
      } finally {
        checkingId.value = null
      }
    }

    const editProxy = (proxy: Proxy) => {
      currentProxyId.value = proxy.id
      editForm.ipAddress = proxy.ipAddress
      editForm.port = proxy.port
      editForm.protocol = proxy.protocol as ProxyProtocol
      editForm.country = proxy.country || ''
      editForm.city = proxy.city || ''
      editForm.notes = proxy.notes || ''
      showEditModal.value = true
    }

    const saveEdit = async () => {
      if (!currentProxyId.value) return

      saving.value = true
      try {
        const updatedProxy = await proxyService.updateProxy(currentProxyId.value, editForm)

        proxies.value = proxies.value.map(p =>
          p.id === updatedProxy.id ? updatedProxy : p
        )

        showEditModal.value = false
        alert('Proxy updated successfully')
      } catch (error) {
        console.error('Error updating proxy:', error)
        alert('Failed to update proxy')
      } finally {
        saving.value = false
      }
    }

    const confirmDelete = (id: string) => {
      currentProxyId.value = id
      showDeleteModal.value = true
    }

    const deleteProxy = async () => {
      if (!currentProxyId.value) return

      deleting.value = true
      try {
        await proxyService.deleteProxy(currentProxyId.value)

        proxies.value = proxies.value.filter(p => p.id !== currentProxyId.value)
        calculateStats()

        showDeleteModal.value = false
        alert('Proxy deleted successfully')
      } catch (error) {
        console.error('Error deleting proxy:', error)
        alert('Failed to delete proxy')
      } finally {
        deleting.value = false
      }
    }

    const formatDate = (dateString: string | undefined) => {
      if (!dateString) return 'Never'

      const date = new Date(dateString)
      const now = new Date()
      const diffMs = now.getTime() - date.getTime()
      const diffSec = Math.floor(diffMs / 1000)
      const diffMin = Math.floor(diffSec / 60)
      const diffHour = Math.floor(diffMin / 60)
      const diffDay = Math.floor(diffHour / 24)

      if (diffSec < 60) return `${diffSec} seconds ago`
      if (diffMin < 60) return `${diffMin} minutes ago`
      if (diffHour < 24) return `${diffHour} hours ago`
      if (diffDay < 30) return `${diffDay} days ago`

      return date.toLocaleDateString()
    }

    // Lifecycle hooks
    onMounted(() => {
      fetchProxies()
    })

    return {
      proxies,
      loading,
      searchText,
      activeTab,
      tabs,
      filteredProxies,
      stats,
      checkingId,
      showEditModal,
      showDeleteModal,
      currentProxyId,
      editForm,
      saving,
      deleting,
      protocols: PROXY_PROTOCOLS,
      fetchProxies,
      handleTabChange,
      checkProxy,
      editProxy,
      saveEdit,
      confirmDelete,
      deleteProxy,
      formatDate
    }
  }
})
</script>

<style scoped>
.header {
  @apply flex justify-between items-center mb-6;
}

.btn {
  @apply px-4 py-2 rounded-md font-medium transition-colors duration-200;
}

.btn-primary {
  @apply bg-blue-600 text-white hover:bg-blue-700;
}

.btn-outline {
  @apply border border-gray-300 hover:bg-gray-100;
}

.btn-danger {
  @apply bg-red-600 text-white hover:bg-red-700;
}

.btn-sm {
  @apply px-2 py-1 text-sm min-w-[32px] min-h-[32px] inline-flex items-center justify-center;
}

.stat-card {
  @apply bg-white p-4 rounded-lg shadow-md;
}

.stat-title {
  @apply text-gray-500 text-sm;
}

.stat-value {
  @apply text-2xl font-bold;
}

.stat-desc {
  @apply text-sm text-gray-500;
}

.stat-icon {
  @apply absolute top-4 right-4 text-xl opacity-50;
}

.tabs {
  @apply flex border-b;
}

.tab-btn {
  @apply px-4 py-2 border-b-2 border-transparent hover:border-blue-500;
}

.tab-btn.active {
  @apply border-blue-500 font-medium;
}

.search-box {
  @apply relative;
}

.search-input {
  @apply pl-10 pr-4 py-2 border rounded-md w-64;
}

.search-box i {
  @apply absolute left-3 top-3 text-gray-400;
}

.status-badge {
  @apply px-2 py-1 rounded-full text-xs font-medium;
}

.status-badge.active {
  @apply bg-green-100 text-green-800;
}

.status-badge.inactive {
  @apply bg-red-100 text-red-800;
}

.protocol-badge {
  @apply px-2 py-1 rounded-full text-xs font-medium;
}

.protocol-badge.http {
  @apply bg-green-100 text-green-800;
}

.protocol-badge.https {
  @apply bg-blue-100 text-blue-800;
}

.protocol-badge.socks4 {
  @apply bg-purple-100 text-purple-800;
}

.protocol-badge.socks5 {
  @apply bg-pink-100 text-pink-800;
}

.loader {
  @apply w-8 h-8 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin;
}

.modal-overlay {
  @apply fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50;
}

.modal-container {
  @apply bg-white rounded-lg shadow-xl w-full max-w-md mx-4;
}

.modal-header {
  @apply flex justify-between items-center p-4 border-b;
}

.modal-close {
  @apply text-gray-500 hover:text-gray-700;
}

.modal-body {
  @apply p-4;
}

.modal-footer {
  @apply flex justify-end space-x-2 mt-4;
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
</style>
