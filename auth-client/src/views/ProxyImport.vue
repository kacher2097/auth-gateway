<template>
  <AdminLayout title="Import Proxies">
    <div class="proxy-import">
    <div class="header">
      <h1 class="text-2xl font-bold">Import Proxies</h1>
      <div class="flex space-x-2">
        <button @click="downloadTemplate" class="btn btn-outline">
          <i class="fas fa-download mr-2"></i> Download Template
        </button>
        <router-link to="/admin/proxies" class="btn btn-outline">
          <i class="fas fa-arrow-left mr-2"></i> Back to List
        </router-link>
      </div>
    </div>

    <div class="steps-container mb-6">
      <div
        v-for="(step, index) in steps"
        :key="step.title"
        class="step"
        :class="{
          'active': currentStep === index,
          'completed': currentStep > index
        }"
      >
        <div class="step-number">{{ index + 1 }}</div>
        <div class="step-content">
          <div class="step-title">{{ step.title }}</div>
          <div class="step-description">{{ step.description }}</div>
        </div>
      </div>
    </div>

    <!-- Step 1: Upload File -->
    <div v-if="currentStep === 0" class="card">
      <div class="alert info mb-6">
        <i class="fas fa-info-circle mr-2"></i>
        <div>
          <div class="font-medium">File Format Requirements</div>
          <div>
            <p>Your file should have the following columns in this order:</p>
            <ol class="list-decimal pl-6 mt-2">
              <li><strong>IP Address</strong> (required) - e.g., 192.168.1.1</li>
              <li><strong>Port</strong> (required) - e.g., 8080</li>
              <li><strong>Protocol</strong> (required) - One of: HTTP, HTTPS, SOCKS4, SOCKS5</li>
              <li><strong>Country</strong> (optional) - e.g., United States</li>
              <li><strong>City</strong> (optional) - e.g., New York</li>
              <li><strong>Notes</strong> (optional) - Any additional information</li>
            </ol>
            <p class="mt-2">The first row should be a header row with column names.</p>
          </div>
        </div>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">Select File Type:</label>
        <div class="flex space-x-2">
          <label class="radio-btn" :class="{ 'active': fileType === 'CSV' }">
            <input
              type="radio"
              v-model="fileType"
              value="CSV"
              class="hidden"
            />
            <i class="fas fa-file-csv mr-2"></i> CSV
          </label>
          <label class="radio-btn" :class="{ 'active': fileType === 'EXCEL' }">
            <input
              type="radio"
              v-model="fileType"
              value="EXCEL"
              class="hidden"
            />
            <i class="fas fa-file-excel mr-2"></i> Excel
          </label>
        </div>
      </div>

      <div
        class="upload-area"
        @dragover.prevent="dragover = true"
        @dragleave.prevent="dragover = false"
        @drop.prevent="onFileDrop"
        :class="{ 'dragover': dragover }"
      >
        <input
          type="file"
          ref="fileInput"
          @change="onFileChange"
          :accept="fileType === 'CSV' ? '.csv' : '.xlsx,.xls'"
          class="hidden"
        />
        <div class="upload-icon">
          <i class="fas fa-upload"></i>
        </div>
        <div class="upload-text">
          <p class="font-medium">Click or drag file to this area to upload</p>
          <p class="text-sm text-gray-500">
            Support for a single {{ fileType === 'CSV' ? '.csv' : '.xlsx/.xls' }} file.
          </p>
        </div>
        <button
          type="button"
          @click="$refs.fileInput.click()"
          class="btn btn-outline mt-4"
        >
          Select File
        </button>
      </div>

      <div v-if="selectedFile" class="selected-file mt-4">
        <div class="flex items-center justify-between p-3 border rounded-md">
          <div class="flex items-center">
            <i class="fas" :class="fileType === 'CSV' ? 'fa-file-csv' : 'fa-file-excel'"></i>
            <span class="ml-2">{{ selectedFile.name }}</span>
            <span class="ml-2 text-sm text-gray-500">
              ({{ formatFileSize(selectedFile.size) }})
            </span>
          </div>
          <button @click="removeFile" class="text-red-600 hover:text-red-800">
            <i class="fas fa-times"></i>
          </button>
        </div>
      </div>

      <div class="divider"></div>

      <button
        @click="startImport"
        class="btn btn-primary"
        :disabled="!selectedFile || uploading"
      >
        <i class="fas fa-upload mr-2"></i>
        {{ uploading ? 'Importing...' : 'Start Import' }}
      </button>
    </div>

    <!-- Step 2: Processing -->
    <div v-if="currentStep === 1" class="card">
      <div class="text-center py-8">
        <div class="loader mx-auto"></div>
        <h2 class="text-xl font-bold mt-4">Processing Your File</h2>
        <p class="text-gray-600 mt-2">
          Please wait while we process and validate your proxies...
        </p>
      </div>
    </div>

    <!-- Step 3: Results -->
    <div v-if="currentStep === 2 && importResult" class="results">
      <div class="card mb-6">
        <div class="result-header" :class="getResultHeaderClass()">
          <div class="result-icon">
            <i class="fas" :class="getResultIconClass()"></i>
          </div>
          <div>
            <h2 class="text-xl font-bold">Import Completed</h2>
            <p class="text-gray-600">
              Processed {{ importResult.totalProcessed }} proxies with
              {{ importResult.successCount }} successful and
              {{ importResult.failCount }} failed.
            </p>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
          <div class="stat-card">
            <div class="stat-title">Total Processed</div>
            <div class="stat-value">{{ importResult.totalProcessed }}</div>
            <div class="stat-icon"><i class="fas fa-info-circle"></i></div>
          </div>
          <div class="stat-card">
            <div class="stat-title">Successfully Imported</div>
            <div class="stat-value text-green-600">{{ importResult.successCount }}</div>
            <div class="stat-icon"><i class="fas fa-check-circle text-green-600"></i></div>
          </div>
          <div class="stat-card">
            <div class="stat-title">Failed</div>
            <div class="stat-value text-red-600">{{ importResult.failCount }}</div>
            <div class="stat-icon"><i class="fas fa-times-circle text-red-600"></i></div>
          </div>
        </div>

        <div class="mt-6">
          <div class="progress-bar-container">
            <div
              class="progress-bar"
              :style="{ width: `${getSuccessRate()}%` }"
              :class="getProgressBarClass()"
            ></div>
          </div>
          <div class="text-center mt-2">
            {{ getSuccessRate() }}% Success Rate
          </div>
        </div>

        <div class="flex justify-center space-x-4 mt-6">
          <router-link to="/admin/proxies" class="btn btn-primary">
            Go to Proxy List
          </router-link>
          <button
            @click="resetImport"
            class="btn btn-outline"
          >
            Import Another File
          </button>
        </div>
      </div>

      <!-- Error Table -->
      <div v-if="importResult.errors.length > 0" class="card">
        <h3 class="text-lg font-bold mb-4">Import Errors</h3>
        <div class="overflow-x-auto">
          <table class="table-auto w-full">
            <thead>
              <tr>
                <th class="px-4 py-2 text-left">Row</th>
                <th class="px-4 py-2 text-left">Error Message</th>
                <th class="px-4 py-2 text-left">Raw Data</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="error in importResult.errors" :key="error.rowNumber" class="border-b">
                <td class="px-4 py-2">{{ error.rowNumber }}</td>
                <td class="px-4 py-2">{{ error.errorMessage }}</td>
                <td class="px-4 py-2 truncate max-w-xs">{{ error.rawData }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Tips -->
    <div v-if="currentStep === 0" class="card mt-6">
      <h2 class="text-xl font-bold mb-4">
        <i class="fas fa-lightbulb mr-2"></i> Tips for Successful Import
      </h2>
      <ul class="list-disc pl-6">
        <li>Make sure your file follows the required format</li>
        <li>IP addresses should be in the standard format (e.g., 192.168.1.1)</li>
        <li>Ports should be valid numbers between 1-65535</li>
        <li>Protocols must be one of: HTTP, HTTPS, SOCKS4, SOCKS5</li>
        <li>Each proxy will be automatically checked for validity during import</li>
        <li>Large files may take some time to process</li>
      </ul>
    </div>
    </div>
  </AdminLayout>
</template>

<script lang="ts">
import { defineComponent, ref, reactive } from 'vue'
import proxyService from '@/services/proxy.service'
import type { ImportResult } from '@/types/proxy'
import AdminLayout from '@/components/layout/AdminLayout.vue'

export default defineComponent({
  name: 'ProxyImport',
  components: {
    AdminLayout
  },
  setup() {
    const fileInput = ref<HTMLInputElement | null>(null)
    const fileType = ref<'CSV' | 'EXCEL'>('CSV')
    const selectedFile = ref<File | null>(null)
    const dragover = ref(false)
    const uploading = ref(false)
    const currentStep = ref(0)
    const importResult = ref<ImportResult | null>(null)

    const steps = [
      {
        title: 'Upload File',
        description: 'Select and upload file'
      },
      {
        title: 'Processing',
        description: 'Validating proxies'
      },
      {
        title: 'Complete',
        description: 'View results'
      }
    ]

    const onFileChange = (event: Event) => {
      const input = event.target as HTMLInputElement
      if (input.files && input.files.length > 0) {
        const file = input.files[0]

        // Check file extension
        const isCSV = fileType.value === 'CSV' && file.name.endsWith('.csv')
        const isExcel = fileType.value === 'EXCEL' && (file.name.endsWith('.xlsx') || file.name.endsWith('.xls'))

        if (!(isCSV || isExcel)) {
          alert(`Please upload a ${fileType.value === 'CSV' ? '.csv' : '.xlsx/.xls'} file.`)
          return
        }

        selectedFile.value = file
      }
    }

    const onFileDrop = (event: DragEvent) => {
      dragover.value = false

      if (event.dataTransfer?.files.length) {
        const file = event.dataTransfer.files[0]

        // Check file extension
        const isCSV = fileType.value === 'CSV' && file.name.endsWith('.csv')
        const isExcel = fileType.value === 'EXCEL' && (file.name.endsWith('.xlsx') || file.name.endsWith('.xls'))

        if (!(isCSV || isExcel)) {
          alert(`Please upload a ${fileType.value === 'CSV' ? '.csv' : '.xlsx/.xls'} file.`)
          return
        }

        selectedFile.value = file
      }
    }

    const removeFile = () => {
      selectedFile.value = null
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const formatFileSize = (bytes: number): string => {
      if (bytes === 0) return '0 Bytes'

      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))

      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }

    const startImport = async () => {
      if (!selectedFile.value) return

      uploading.value = true
      currentStep.value = 1

      try {
        const result = await proxyService.importProxies(selectedFile.value, fileType.value)
        importResult.value = result
        currentStep.value = 2
      } catch (error) {
        console.error('Error importing proxies:', error)
        alert('An error occurred while importing proxies')
        currentStep.value = 0
      } finally {
        uploading.value = false
      }
    }

    const resetImport = () => {
      currentStep.value = 0
      selectedFile.value = null
      importResult.value = null
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const getSuccessRate = (): number => {
      if (!importResult.value || importResult.value.totalProcessed === 0) return 0

      return Math.round((importResult.value.successCount / importResult.value.totalProcessed) * 100)
    }

    const getResultHeaderClass = (): string => {
      if (!importResult.value) return ''

      return importResult.value.successCount > 0 ? 'success' : 'warning'
    }

    const getResultIconClass = (): string => {
      if (!importResult.value) return ''

      return importResult.value.successCount > 0 ? 'fa-check-circle' : 'fa-exclamation-triangle'
    }

    const getProgressBarClass = (): string => {
      const rate = getSuccessRate()

      if (rate === 100) return 'success'
      if (rate === 0) return 'danger'
      return 'warning'
    }

    const downloadTemplate = () => {
      const csvContent = "IP Address,Port,Protocol,Country,City,Notes\n192.168.1.1,8080,HTTP,United States,New York,Example proxy"
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.setAttribute('download', 'proxy_template.csv')
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }

    return {
      fileInput,
      fileType,
      selectedFile,
      dragover,
      uploading,
      currentStep,
      importResult,
      steps,
      onFileChange,
      onFileDrop,
      removeFile,
      formatFileSize,
      startImport,
      resetImport,
      getSuccessRate,
      getResultHeaderClass,
      getResultIconClass,
      getProgressBarClass,
      downloadTemplate
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

.steps-container {
  @apply flex;
}

.step {
  @apply flex-1 flex items-start relative;
}

.step:not(:last-child)::after {
  content: '';
  @apply absolute top-4 left-10 w-full h-0.5 bg-gray-200;
  z-index: 0;
}

.step.active .step-number, .step.completed .step-number {
  @apply bg-blue-600 text-white;
}

.step.completed:not(:last-child)::after {
  @apply bg-blue-600;
}

.step-number {
  @apply w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center font-bold z-10;
}

.step-content {
  @apply ml-3;
}

.step-title {
  @apply font-medium;
}

.step-description {
  @apply text-sm text-gray-500;
}

.alert {
  @apply p-4 rounded-md flex items-start;
}

.alert.info {
  @apply bg-blue-50 text-blue-800;
}

.radio-btn {
  @apply px-4 py-2 border rounded-md cursor-pointer;
}

.radio-btn.active {
  @apply bg-blue-50 border-blue-500;
}

.upload-area {
  @apply border-2 border-dashed border-gray-300 rounded-lg p-8 text-center cursor-pointer transition-colors duration-200;
}

.upload-area.dragover {
  @apply border-blue-500 bg-blue-50;
}

.upload-icon {
  @apply text-4xl text-gray-400 mb-4;
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

.divider {
  @apply my-6 border-t border-gray-200;
}

.loader {
  @apply w-12 h-12 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin;
}

.result-header {
  @apply flex items-center p-4 rounded-md;
}

.result-header.success {
  @apply bg-green-50;
}

.result-header.warning {
  @apply bg-yellow-50;
}

.result-icon {
  @apply text-3xl mr-4;
}

.result-header.success .result-icon {
  @apply text-green-600;
}

.result-header.warning .result-icon {
  @apply text-yellow-600;
}

.stat-card {
  @apply bg-gray-50 p-4 rounded-lg relative;
}

.stat-title {
  @apply text-gray-500 text-sm;
}

.stat-value {
  @apply text-2xl font-bold;
}

.stat-icon {
  @apply absolute top-4 right-4 text-xl opacity-50;
}

.progress-bar-container {
  @apply w-full h-2 bg-gray-200 rounded-full overflow-hidden;
}

.progress-bar {
  @apply h-full transition-all duration-500;
}

.progress-bar.success {
  @apply bg-green-600;
}

.progress-bar.warning {
  @apply bg-yellow-600;
}

.progress-bar.danger {
  @apply bg-red-600;
}
</style>
