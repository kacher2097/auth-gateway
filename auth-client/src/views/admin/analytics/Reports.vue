<template>
  <div>
    <Breadcrumb :items="breadcrumbItems" />

    <div class="bg-white shadow rounded-lg p-6 mt-4">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-xl font-semibold text-gray-800">Analytics Reports</h2>
        <Button type="primary" @click="navigateToNewReport">
          <template #icon><FileAddOutlined /></template>
          Create New Report
        </Button>
      </div>

      <!-- Reports Table -->
      <Table
        :columns="columns"
        :data-source="reports"
        :loading="loading"
        :pagination="{ pageSize: 10 }"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <Tag :color="getReportTypeColor(record.type)">
              {{ record.type }}
            </Tag>
          </template>

          <template v-if="column.key === 'status'">
            <Tag :color="getStatusColor(record.status)">
              {{ record.status }}
            </Tag>
          </template>

          <template v-if="column.key === 'actions'">
            <Space>
              <Button
                type="primary"
                size="small"
                @click="viewReport(record)"
                :disabled="record.status === 'PENDING'"
              >
                <template #icon><EyeOutlined /></template>
                View
              </Button>
              <Button
                type="primary"
                size="small"
                @click="downloadReport(record)"
                :disabled="record.status === 'PENDING' || record.status === 'FAILED'"
              >
                <template #icon><DownloadOutlined /></template>
                Download
              </Button>
              <Button
                type="primary"
                danger
                size="small"
                @click="confirmDeleteReport(record)"
              >
                <template #icon><DeleteOutlined /></template>
                Delete
              </Button>
            </Space>
          </template>
        </template>
      </Table>
    </div>

    <!-- Report View Modal -->
    <Modal
      :title="selectedReport?.name"
      :open="reportModalVisible"
      width="800px"
      @cancel="reportModalVisible = false"
      footer={null}
    >
      <Spin :spinning="reportLoading">
        <div v-if="reportData">
          <Tabs>
            <Tabs.TabPane key="preview" tab="Preview">
              <div class="max-h-96 overflow-auto">
                <div v-if="selectedReport?.type === 'USER_ACTIVITY'">
                  <Table
                    :columns="userActivityColumns"
                    :data-source="reportData.data"
                    :pagination="{ pageSize: 5 }"
                    size="small"
                  />
                </div>
                <div v-else-if="selectedReport?.type === 'LOGIN_STATS'">
                  <Table
                    :columns="loginStatsColumns"
                    :data-source="reportData.data"
                    :pagination="{ pageSize: 5 }"
                    size="small"
                  />
                </div>
                <div v-else>
                  <pre class="whitespace-pre-wrap">{{ JSON.stringify(reportData, null, 2) }}</pre>
                </div>
              </div>
            </Tabs.TabPane>
            <Tabs.TabPane key="json" tab="JSON">
              <div class="max-h-96 overflow-auto">
                <pre class="whitespace-pre-wrap">{{ JSON.stringify(reportData, null, 2) }}</pre>
              </div>
            </Tabs.TabPane>
            <Tabs.TabPane key="details" tab="Report Details">
              <Descriptions bordered column={1}>
                <Descriptions.Item label="Name">{{ selectedReport?.name }}</Descriptions.Item>
                <Descriptions.Item label="Type">
                  <Tag :color="getReportTypeColor(selectedReport?.type)">
                    {{ selectedReport?.type }}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="Status">
                  <Tag :color="getStatusColor(selectedReport?.status)">
                    {{ selectedReport?.status }}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="Created By">{{ selectedReport?.createdBy?.fullName }}</Descriptions.Item>
                <Descriptions.Item label="Created At">{{ formatDate(selectedReport?.createdAt) }}</Descriptions.Item>
                <Descriptions.Item label="Parameters">
                  <pre class="whitespace-pre-wrap">{{ JSON.stringify(selectedReport?.parameters, null, 2) }}</pre>
                </Descriptions.Item>
              </Descriptions>
            </Tabs.TabPane>
          </Tabs>
        </div>
        <Empty v-else description="No report data available" />
      </Spin>
    </Modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { useToast } from '@/components/ui/ToastContainer.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import { Button, Table, Tag, Modal, Space, Tabs, Descriptions, Spin, Empty } from 'ant-design-vue'
import { FileAddOutlined, EyeOutlined, DownloadOutlined, DeleteOutlined } from '@ant-design/icons-vue'

// Initialize auth store, router, and toast
const authStore = useAuthStore()
const router = useRouter()
const toast = useToast()

// Breadcrumb items
const breadcrumbItems = [
  { text: 'Dashboard', to: '/admin' },
  { text: 'Analytics', to: '/admin/analytics' },
  { text: 'Reports', to: '/admin/analytics/reports', active: true }
]

// State
const loading = ref(false)
const reportLoading = ref(false)
const reports = ref<any[]>([])
const reportModalVisible = ref(false)
const selectedReport = ref<any>(null)
const reportData = ref<any>(null)

// Table columns
const columns = [
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    sorter: (a: any, b: any) => a.name.localeCompare(b.name)
  },
  {
    title: 'Type',
    key: 'type',
    filters: [
      { text: 'User Activity', value: 'USER_ACTIVITY' },
      { text: 'Login Stats', value: 'LOGIN_STATS' },
      { text: 'System Usage', value: 'SYSTEM_USAGE' }
    ],
    onFilter: (value: string, record: any) => record.type === value
  },
  {
    title: 'Status',
    key: 'status',
    filters: [
      { text: 'Completed', value: 'COMPLETED' },
      { text: 'Pending', value: 'PENDING' },
      { text: 'Failed', value: 'FAILED' }
    ],
    onFilter: (value: string, record: any) => record.status === value
  },
  {
    title: 'Created By',
    dataIndex: 'createdBy',
    key: 'createdBy',
    render: (createdBy: any) => createdBy?.fullName || 'System'
  },
  {
    title: 'Created At',
    dataIndex: 'createdAt',
    key: 'createdAt',
    render: (text: string) => formatDate(text),
    sorter: (a: any, b: any) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
  },
  {
    title: 'Actions',
    key: 'actions'
  }
]

// User activity columns for report preview
const userActivityColumns = [
  {
    title: 'User',
    dataIndex: 'username',
    key: 'username'
  },
  {
    title: 'Action',
    dataIndex: 'action',
    key: 'action'
  },
  {
    title: 'Timestamp',
    dataIndex: 'timestamp',
    key: 'timestamp',
    render: (text: string) => formatDate(text)
  },
  {
    title: 'IP Address',
    dataIndex: 'ipAddress',
    key: 'ipAddress'
  }
]

// Login stats columns for report preview
const loginStatsColumns = [
  {
    title: 'Date',
    dataIndex: 'date',
    key: 'date'
  },
  {
    title: 'Successful Logins',
    dataIndex: 'successfulLogins',
    key: 'successfulLogins'
  },
  {
    title: 'Failed Logins',
    dataIndex: 'failedLogins',
    key: 'failedLogins'
  },
  {
    title: 'Unique Users',
    dataIndex: 'uniqueUsers',
    key: 'uniqueUsers'
  }
]

// Format date
const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// Get report type color
const getReportTypeColor = (type: string) => {
  switch (type) {
    case 'USER_ACTIVITY':
      return 'blue'
    case 'LOGIN_STATS':
      return 'green'
    case 'SYSTEM_USAGE':
      return 'purple'
    default:
      return 'default'
  }
}

// Get status color
const getStatusColor = (status: string) => {
  switch (status) {
    case 'COMPLETED':
      return 'success'
    case 'PENDING':
      return 'processing'
    case 'FAILED':
      return 'error'
    default:
      return 'default'
  }
}

// Navigate to new report page
const navigateToNewReport = () => {
  router.push('/admin/analytics/reports/new')
}

// Fetch reports
const fetchReports = async () => {
  try {
    loading.value = true

    const response = await api.post('/admin/reports', {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Process response
    if (response.data && response.data.data) {
      reports.value = response.data.data
    } else {
      reports.value = response.data || []
    }
  } catch (error) {
    console.error('Error fetching reports:', error)
    toast.error('Failed to load reports')
  } finally {
    loading.value = false
  }
}

// View report
const viewReport = async (report: any) => {
  try {
    selectedReport.value = report
    reportModalVisible.value = true
    reportLoading.value = true
    reportData.value = null

    const response = await api.get(`/admin/reports/${report.id}/data`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Process response
    if (response.data && response.data.data) {
      reportData.value = response.data.data
    } else {
      reportData.value = response.data || null
    }
  } catch (error) {
    console.error('Error fetching report data:', error)
    toast.error('Failed to load report data')
  } finally {
    reportLoading.value = false
  }
}

// Download report
const downloadReport = async (report: any) => {
  try {
    const response = await api.get(`/admin/reports/${report.id}/download`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      },
      responseType: 'blob'
    })

    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${report.name}.json`)
    document.body.appendChild(link)
    link.click()
    link.remove()

    toast.success('Report downloaded successfully')
  } catch (error) {
    console.error('Error downloading report:', error)
    toast.error('Failed to download report')
  }
}

// Delete report
const confirmDeleteReport = (report: any) => {
  Modal.confirm({
    title: 'Are you sure you want to delete this report?',
    content: 'This action cannot be undone.',
    okText: 'Yes, Delete',
    okType: 'danger',
    cancelText: 'Cancel',
    onOk: () => deleteReport(report.id)
  })
}

const deleteReport = async (reportId: string) => {
  try {
    loading.value = true

    await api.delete(`/admin/reports/${reportId}`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Remove from list
    reports.value = reports.value.filter(report => report.id !== reportId)

    toast.success('Report deleted successfully')
  } catch (error) {
    console.error('Error deleting report:', error)
    toast.error('Failed to delete report')
  } finally {
    loading.value = false
  }
}

// Initialize on mount
onMounted(() => {
  fetchReports()
})
</script>
