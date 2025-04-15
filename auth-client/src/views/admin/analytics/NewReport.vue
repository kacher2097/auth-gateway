<template>
  <div>
    <Breadcrumb :items="breadcrumbItems" />

    <div class="bg-white shadow rounded-lg p-6 mt-4">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">Create New Report</h2>

      <Form
        :model="reportForm"
        layout="vertical"
        @finish="handleSubmit"
      >
        <Row :gutter="16">
          <Col :span="12">
            <Form.Item
              label="Report Name"
              name="name"
              :rules="[{ required: true, message: 'Please enter report name' }]"
            >
              <Input v-model:value="reportForm.name" placeholder="Enter report name" />
            </Form.Item>
          </Col>

          <Col :span="12">
            <Form.Item
              label="Report Type"
              name="type"
              :rules="[{ required: true, message: 'Please select report type' }]"
            >
              <Select
                v-model:value="reportForm.type"
                placeholder="Select report type"
                @change="handleReportTypeChange"
              >
                <Select.Option value="USER_ACTIVITY">User Activity</Select.Option>
                <Select.Option value="LOGIN_STATS">Login Statistics</Select.Option>
                <Select.Option value="SYSTEM_USAGE">System Usage</Select.Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>

        <Row :gutter="16">
          <Col :span="24">
            <Form.Item
              label="Description"
              name="description"
            >
              <Input.TextArea v-model:value="reportForm.description" rows="3" placeholder="Enter report description" />
            </Form.Item>
          </Col>
        </Row>

        <!-- Dynamic parameters based on report type -->
        <div v-if="reportForm.type === 'USER_ACTIVITY'">
          <Divider>User Activity Parameters</Divider>

          <Row :gutter="16">
            <Col :span="12">
              <Form.Item
                label="Date Range"
                name="dateRange"
                :rules="[{ required: true, message: 'Please select date range' }]"
              >
                <DatePicker.RangePicker
                  v-model:value="reportForm.parameters.dateRange"
                  style="width: 100%"
                  :disabled-date="disabledDate"
                />
              </Form.Item>
            </Col>

            <Col :span="12">
              <Form.Item
                label="User"
                name="userId"
              >
                <Select
                  v-model:value="reportForm.parameters.userId"
                  placeholder="Select user (optional)"
                  allowClear
                  style="width: 100%"
                >
                  <Select.Option value="">All Users</Select.Option>
                  <Select.Option v-for="user in users" :key="user.id" :value="user.id">
                    {{ user.fullName }} ({{ user.username }})
                  </Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row :gutter="16">
            <Col :span="12">
              <Form.Item
                label="Activity Types"
                name="activityTypes"
              >
                <Select
                  v-model:value="reportForm.parameters.activityTypes"
                  mode="multiple"
                  placeholder="Select activity types (optional)"
                  style="width: 100%"
                  allowClear
                >
                  <Select.Option value="LOGIN">Login</Select.Option>
                  <Select.Option value="LOGOUT">Logout</Select.Option>
                  <Select.Option value="REGISTER">Register</Select.Option>
                  <Select.Option value="PASSWORD_RESET">Password Reset</Select.Option>
                  <Select.Option value="PROFILE_UPDATE">Profile Update</Select.Option>
                </Select>
              </Form.Item>
            </Col>

            <Col :span="12">
              <Form.Item
                label="Include Details"
                name="includeDetails"
                valuePropName="checked"
              >
                <Checkbox v-model:checked="reportForm.parameters.includeDetails">
                  Include detailed activity information
                </Checkbox>
              </Form.Item>
            </Col>
          </Row>
        </div>

        <div v-else-if="reportForm.type === 'LOGIN_STATS'">
          <Divider>Login Statistics Parameters</Divider>

          <Row :gutter="16">
            <Col :span="12">
              <Form.Item
                label="Date Range"
                name="dateRange"
                :rules="[{ required: true, message: 'Please select date range' }]"
              >
                <DatePicker.RangePicker
                  v-model:value="reportForm.parameters.dateRange"
                  style="width: 100%"
                  :disabled-date="disabledDate"
                />
              </Form.Item>
            </Col>

            <Col :span="12">
              <Form.Item
                label="Group By"
                name="groupBy"
              >
                <Select
                  v-model:value="reportForm.parameters.groupBy"
                  placeholder="Select grouping"
                  style="width: 100%"
                >
                  <Select.Option value="day">Day</Select.Option>
                  <Select.Option value="week">Week</Select.Option>
                  <Select.Option value="month">Month</Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row :gutter="16">
            <Col :span="12">
              <Form.Item
                label="Include Failed Logins"
                name="includeFailedLogins"
                valuePropName="checked"
              >
                <Checkbox v-model:checked="reportForm.parameters.includeFailedLogins">
                  Include failed login attempts
                </Checkbox>
              </Form.Item>
            </Col>

            <Col :span="12">
              <Form.Item
                label="Include Device Info"
                name="includeDeviceInfo"
                valuePropName="checked"
              >
                <Checkbox v-model:checked="reportForm.parameters.includeDeviceInfo">
                  Include device and browser information
                </Checkbox>
              </Form.Item>
            </Col>
          </Row>
        </div>

        <div v-else-if="reportForm.type === 'SYSTEM_USAGE'">
          <Divider>System Usage Parameters</Divider>

          <Row :gutter="16">
            <Col :span="12">
              <Form.Item
                label="Date Range"
                name="dateRange"
                :rules="[{ required: true, message: 'Please select date range' }]"
              >
                <DatePicker.RangePicker
                  v-model:value="reportForm.parameters.dateRange"
                  style="width: 100%"
                  :disabled-date="disabledDate"
                />
              </Form.Item>
            </Col>

            <Col :span="12">
              <Form.Item
                label="Metrics"
                name="metrics"
                :rules="[{ required: true, message: 'Please select at least one metric' }]"
              >
                <Select
                  v-model:value="reportForm.parameters.metrics"
                  mode="multiple"
                  placeholder="Select metrics"
                  style="width: 100%"
                >
                  <Select.Option value="activeUsers">Active Users</Select.Option>
                  <Select.Option value="newUsers">New Users</Select.Option>
                  <Select.Option value="apiCalls">API Calls</Select.Option>
                  <Select.Option value="errors">Errors</Select.Option>
                  <Select.Option value="avgResponseTime">Average Response Time</Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row :gutter="16">
            <Col :span="12">
              <Form.Item
                label="Group By"
                name="groupBy"
              >
                <Select
                  v-model:value="reportForm.parameters.groupBy"
                  placeholder="Select grouping"
                  style="width: 100%"
                >
                  <Select.Option value="hour">Hour</Select.Option>
                  <Select.Option value="day">Day</Select.Option>
                  <Select.Option value="week">Week</Select.Option>
                  <Select.Option value="month">Month</Select.Option>
                </Select>
              </Form.Item>
            </Col>

            <Col :span="12">
              <Form.Item
                label="Format"
                name="format"
              >
                <Select
                  v-model:value="reportForm.parameters.format"
                  placeholder="Select output format"
                  style="width: 100%"
                >
                  <Select.Option value="json">JSON</Select.Option>
                  <Select.Option value="csv">CSV</Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
        </div>

        <Form.Item style="margin-top: 24px">
          <Button type="primary" html-type="submit" :loading="submitting">
            <template #icon><FileAddOutlined /></template>
            Generate Report
          </Button>
          <Button style="margin-left: 8px" @click="resetForm">
            Reset
          </Button>
        </Form.Item>
      </Form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { useToast } from '@/components/ui/ToastContainer.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import { Button, Form, Input, Select, DatePicker, Checkbox, Divider, Row, Col } from 'ant-design-vue'
import { FileAddOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

// Initialize auth store, router, and toast
const authStore = useAuthStore()
const router = useRouter()
const toast = useToast()

// Breadcrumb items
const breadcrumbItems = [
  { text: 'Dashboard', to: '/admin' },
  { text: 'Analytics', to: '/admin/analytics' },
  { text: 'Reports', to: '/admin/analytics/reports' },
  { text: 'New Report', to: '/admin/analytics/reports/new', active: true }
]

// State
const submitting = ref(false)
const users = ref<any[]>([])

// Report form
const reportForm = ref({
  name: '',
  type: '',
  description: '',
  parameters: {
    // User Activity parameters
    dateRange: [dayjs().subtract(30, 'days'), dayjs()],
    userId: '',
    activityTypes: [],
    includeDetails: true,

    // Login Stats parameters
    groupBy: 'day',
    includeFailedLogins: true,
    includeDeviceInfo: true,

    // System Usage parameters
    metrics: ['activeUsers', 'newUsers'],
    format: 'json'
  }
})

// Disabled date (future dates)
const disabledDate = (current: dayjs.Dayjs) => {
  return current && current > dayjs().endOf('day')
}

// Handle report type change
const handleReportTypeChange = (value: string) => {
  // Reset parameters
  reportForm.value.parameters = {
    ...reportForm.value.parameters,
    dateRange: [dayjs().subtract(30, 'days'), dayjs()]
  }

  // Set default parameters based on report type
  if (value === 'USER_ACTIVITY') {
    reportForm.value.parameters = {
      ...reportForm.value.parameters,
      userId: '',
      activityTypes: [],
      includeDetails: true
    }
  } else if (value === 'LOGIN_STATS') {
    reportForm.value.parameters = {
      ...reportForm.value.parameters,
      groupBy: 'day',
      includeFailedLogins: true,
      includeDeviceInfo: true
    }
  } else if (value === 'SYSTEM_USAGE') {
    reportForm.value.parameters = {
      ...reportForm.value.parameters,
      metrics: ['activeUsers', 'newUsers'],
      groupBy: 'day',
      format: 'json'
    }
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

// Submit form
const handleSubmit = async () => {
  try {
    submitting.value = true

    // Prepare parameters
    const parameters: any = {}

    // Common parameters
    if (reportForm.value.parameters.dateRange && reportForm.value.parameters.dateRange.length === 2) {
      parameters.startDate = reportForm.value.parameters.dateRange[0].toISOString()
      parameters.endDate = reportForm.value.parameters.dateRange[1].toISOString()
    }

    // Type-specific parameters
    if (reportForm.value.type === 'USER_ACTIVITY') {
      if (reportForm.value.parameters.userId) {
        parameters.userId = reportForm.value.parameters.userId
      }

      if (reportForm.value.parameters.activityTypes && reportForm.value.parameters.activityTypes.length > 0) {
        parameters.activityTypes = reportForm.value.parameters.activityTypes
      }

      parameters.includeDetails = reportForm.value.parameters.includeDetails
    } else if (reportForm.value.type === 'LOGIN_STATS') {
      parameters.groupBy = reportForm.value.parameters.groupBy
      parameters.includeFailedLogins = reportForm.value.parameters.includeFailedLogins
      parameters.includeDeviceInfo = reportForm.value.parameters.includeDeviceInfo
    } else if (reportForm.value.type === 'SYSTEM_USAGE') {
      parameters.metrics = reportForm.value.parameters.metrics
      parameters.groupBy = reportForm.value.parameters.groupBy
      parameters.format = reportForm.value.parameters.format
    }

    // Create report request
    const reportRequest = {
      name: reportForm.value.name,
      type: reportForm.value.type,
      description: reportForm.value.description || null,
      parameters
    }

    const response = await api.post('/admin/reports', reportRequest, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    toast.success('Report created successfully')

    // Navigate to reports list
    router.push('/admin/analytics/reports')
  } catch (error: any) {
    console.error('Error creating report:', error)

    if (error.response?.data?.message) {
      toast.error(error.response.data.message)
    } else {
      toast.error('Failed to create report')
    }
  } finally {
    submitting.value = false
  }
}

// Reset form
const resetForm = () => {
  reportForm.value = {
    name: '',
    type: '',
    description: '',
    parameters: {
      dateRange: [dayjs().subtract(30, 'days'), dayjs()],
      userId: '',
      activityTypes: [],
      includeDetails: true,
      groupBy: 'day',
      includeFailedLogins: true,
      includeDeviceInfo: true,
      metrics: ['activeUsers', 'newUsers'],
      format: 'json'
    }
  }
}

// Initialize on mount
onMounted(() => {
  fetchUsers()
})
</script>
