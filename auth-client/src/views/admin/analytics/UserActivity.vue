<template>
  <div>
    <Breadcrumb :items="breadcrumbItems" />

    <div class="bg-white shadow rounded-lg p-6 mt-4">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">User Activity</h2>

      <!-- Filters -->
      <div class="mb-6 flex flex-wrap gap-4 items-center">
        <div>
          <DatePicker.RangePicker
            v-model:value="dateRange"
            :disabled-date="disabledDate"
            @change="handleDateRangeChange"
          />
        </div>

        <div>
          <Select
            v-model:value="selectedUser"
            placeholder="Filter by user"
            style="width: 200px"
            allowClear
            @change="fetchUserActivity"
          >
            <Select.Option value="">All Users</Select.Option>
            <Select.Option v-for="user in users" :key="user.id" :value="user.id">
              {{ user.fullName }} ({{ user.username }})
            </Select.Option>
          </Select>
        </div>

        <div>
          <Select
            v-model:value="selectedAction"
            placeholder="Filter by action"
            style="width: 200px"
            allowClear
            @change="fetchUserActivity"
          >
            <Select.Option value="">All Actions</Select.Option>
            <Select.Option value="LOGIN">Login</Select.Option>
            <Select.Option value="LOGOUT">Logout</Select.Option>
            <Select.Option value="REGISTER">Register</Select.Option>
            <Select.Option value="PASSWORD_RESET">Password Reset</Select.Option>
            <Select.Option value="PROFILE_UPDATE">Profile Update</Select.Option>
          </Select>
        </div>

        <Button type="primary" @click="fetchUserActivity">
          <template #icon><SearchOutlined /></template>
          Apply Filters
        </Button>

        <Button @click="resetFilters">
          <template #icon><ClearOutlined /></template>
          Reset
        </Button>
      </div>

      <!-- Activity Table -->
      <Table
        :columns="columns"
        :data-source="activities"
        :loading="loading"
        :pagination="{ pageSize: 10, showSizeChanger: true }"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            <div class="flex items-center">
              <Avatar
                :src="record.user?.avatar"
                :style="{ backgroundColor: !record.user?.avatar ? '#1890ff' : 'transparent' }"
              >
                {{ record.user?.fullName.charAt(0) }}
              </Avatar>
              <span class="ml-2">{{ record.user?.fullName }}</span>
            </div>
          </template>

          <template v-if="column.key === 'action'">
            <Tag :color="getActionColor(record.action)">
              {{ record.action }}
            </Tag>
          </template>

          <template v-if="column.key === 'details'">
            <Button type="link" @click="showDetails(record)">
              View Details
            </Button>
          </template>
        </template>
      </Table>
    </div>

    <!-- Activity Details Modal -->
    <Modal
      :title="'Activity Details'"
      :open="detailsModalVisible"
      @cancel="detailsModalVisible = false"
      footer={null}
    >
      <Descriptions bordered column={1}>
        <Descriptions.Item label="User">
          {{ selectedActivity?.user?.fullName }} ({{ selectedActivity?.user?.username }})
        </Descriptions.Item>
        <Descriptions.Item label="Action">
          <Tag :color="getActionColor(selectedActivity?.action)">
            {{ selectedActivity?.action }}
          </Tag>
        </Descriptions.Item>
        <Descriptions.Item label="Timestamp">
          {{ formatDate(selectedActivity?.timestamp) }}
        </Descriptions.Item>
        <Descriptions.Item label="IP Address">
          {{ selectedActivity?.ipAddress }}
        </Descriptions.Item>
        <Descriptions.Item label="User Agent">
          {{ selectedActivity?.userAgent }}
        </Descriptions.Item>
        <Descriptions.Item label="Device">
          {{ selectedActivity?.device }}
        </Descriptions.Item>
        <Descriptions.Item label="Browser">
          {{ selectedActivity?.browser }}
        </Descriptions.Item>
        <Descriptions.Item label="OS">
          {{ selectedActivity?.os }}
        </Descriptions.Item>
        <Descriptions.Item label="Details">
          <pre class="whitespace-pre-wrap">{{ JSON.stringify(selectedActivity?.details, null, 2) }}</pre>
        </Descriptions.Item>
      </Descriptions>
    </Modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { useToast } from '@/components/ui/ToastContainer.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import { Button, Table, Tag, Modal, DatePicker, Select, Avatar, Descriptions } from 'ant-design-vue'
import { SearchOutlined, ClearOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

// Initialize auth store and toast
const authStore = useAuthStore()
const toast = useToast()

// Breadcrumb items
const breadcrumbItems = [
  { text: 'Dashboard', to: '/admin' },
  { text: 'Analytics', to: '/admin/analytics' },
  { text: 'User Activity', to: '/admin/analytics/users', active: true }
]

// State
const loading = ref(false)
const activities = ref<any[]>([])
const users = ref<any[]>([])
const dateRange = ref<any>([dayjs().subtract(30, 'days'), dayjs()])
const selectedUser = ref('')
const selectedAction = ref('')
const detailsModalVisible = ref(false)
const selectedActivity = ref<any>(null)

// Table columns
const columns = [
  {
    title: 'User',
    key: 'user',
    sorter: (a: any, b: any) => a.user?.fullName.localeCompare(b.user?.fullName)
  },
  {
    title: 'Action',
    key: 'action',
    filters: [
      { text: 'Login', value: 'LOGIN' },
      { text: 'Logout', value: 'LOGOUT' },
      { text: 'Register', value: 'REGISTER' },
      { text: 'Password Reset', value: 'PASSWORD_RESET' },
      { text: 'Profile Update', value: 'PROFILE_UPDATE' }
    ],
    onFilter: (value: string, record: any) => record.action === value
  },
  {
    title: 'Timestamp',
    dataIndex: 'timestamp',
    key: 'timestamp',
    render: (text: string) => formatDate(text),
    sorter: (a: any, b: any) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
  },
  {
    title: 'IP Address',
    dataIndex: 'ipAddress',
    key: 'ipAddress'
  },
  {
    title: 'Device',
    dataIndex: 'device',
    key: 'device',
    filters: [
      { text: 'Desktop', value: 'desktop' },
      { text: 'Mobile', value: 'mobile' },
      { text: 'Tablet', value: 'tablet' }
    ],
    onFilter: (value: string, record: any) => record.device === value
  },
  {
    title: 'Browser',
    dataIndex: 'browser',
    key: 'browser'
  },
  {
    title: 'Details',
    key: 'details'
  }
]

// Format date
const formatDate = (date: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// Get action color
const getActionColor = (action: string) => {
  switch (action) {
    case 'LOGIN':
      return 'green'
    case 'LOGOUT':
      return 'blue'
    case 'REGISTER':
      return 'purple'
    case 'PASSWORD_RESET':
      return 'orange'
    case 'PROFILE_UPDATE':
      return 'cyan'
    default:
      return 'default'
  }
}

// Disabled date (future dates)
const disabledDate = (current: dayjs.Dayjs) => {
  return current && current > dayjs().endOf('day')
}

// Handle date range change
const handleDateRangeChange = (dates: any) => {
  if (dates && dates.length === 2) {
    dateRange.value = dates
  }
}

// Fetch users
const fetchUsers = async () => {
  try {
    const response = await api.post('/admin/users', {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Process response
    if (response.data && response.data.data) {
      users.value = response.data.data
    } else {
      users.value = response.data || []
    }
  } catch (error) {
    console.error('Error fetching users:', error)
    toast.error('Failed to load users')
  }
}

// Fetch user activity
const fetchUserActivity = async () => {
  try {
    loading.value = true

    const params: any = {
      startDate: dateRange.value[0].toISOString(),
      endDate: dateRange.value[1].toISOString()
    }

    if (selectedUser.value) {
      params.userId = selectedUser.value
    }

    if (selectedAction.value) {
      params.action = selectedAction.value
    }

    const response = await api.post('/admin/analytics/user-activity', params, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    // Process response
    if (response.data && response.data.data) {
      activities.value = response.data.data
    } else {
      activities.value = response.data || []
    }
  } catch (error) {
    console.error('Error fetching user activity:', error)
    toast.error('Failed to load user activity')
  } finally {
    loading.value = false
  }
}

// Reset filters
const resetFilters = () => {
  dateRange.value = [dayjs().subtract(30, 'days'), dayjs()]
  selectedUser.value = ''
  selectedAction.value = ''
  fetchUserActivity()
}

// Show activity details
const showDetails = (activity: any) => {
  selectedActivity.value = activity
  detailsModalVisible.value = true
}

// Initialize on mount
onMounted(() => {
  fetchUsers()
  fetchUserActivity()
})
</script>
