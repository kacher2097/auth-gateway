<template>
  <div>
    <Breadcrumb :items="breadcrumbItems" />
    
    <div class="bg-white shadow rounded-lg p-6 mt-4">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">User Invitations</h2>
      
      <!-- Invite Form -->
      <div class="mb-8 bg-gray-50 rounded-lg border border-gray-200 p-6">
        <h3 class="text-lg font-medium text-gray-700 mb-4">Invite New User</h3>
        
        <Form
          :model="inviteForm"
          layout="vertical"
          @finish="handleInviteSubmit"
        >
          <Row :gutter="16">
            <Col :span="12">
              <Form.Item
                label="Email"
                name="email"
                :rules="[
                  { required: true, message: 'Please enter email' },
                  { type: 'email', message: 'Please enter a valid email' }
                ]"
              >
                <Input v-model:value="inviteForm.email" placeholder="Enter email address" />
              </Form.Item>
            </Col>
            
            <Col :span="12">
              <Form.Item
                label="Role"
                name="role"
                :rules="[{ required: true, message: 'Please select a role' }]"
              >
                <Select v-model:value="inviteForm.role" placeholder="Select role">
                  <Select.Option value="USER">User</Select.Option>
                  <Select.Option value="ADMIN">Admin</Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          
          <Form.Item
            label="Message (Optional)"
            name="message"
          >
            <Input.TextArea v-model:value="inviteForm.message" rows="3" placeholder="Add a personal message to the invitation email" />
          </Form.Item>
          
          <Form.Item>
            <Button type="primary" html-type="submit" :loading="submitting">
              <template #icon><MailOutlined /></template>
              Send Invitation
            </Button>
          </Form.Item>
        </Form>
      </div>
      
      <!-- Invitations Table -->
      <div>
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-medium text-gray-700">Pending Invitations</h3>
          <Button type="primary" @click="fetchInvitations">
            <template #icon><ReloadOutlined /></template>
            Refresh
          </Button>
        </div>
        
        <Table
          :columns="columns"
          :data-source="invitations"
          :loading="loading"
          :pagination="{ pageSize: 10 }"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
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
                  :disabled="record.status !== 'PENDING'"
                  @click="resendInvitation(record)"
                >
                  <template #icon><MailOutlined /></template>
                  Resend
                </Button>
                <Button 
                  type="primary" 
                  danger 
                  size="small" 
                  :disabled="record.status !== 'PENDING'"
                  @click="confirmCancelInvitation(record)"
                >
                  <template #icon><DeleteOutlined /></template>
                  Cancel
                </Button>
              </Space>
            </template>
          </template>
        </Table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { useToast } from '@/components/ui/ToastContainer.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import { Button, Table, Space, Tag, Modal, Form, Input, Select, Row, Col } from 'ant-design-vue'
import { MailOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons-vue'

// Initialize auth store and toast
const authStore = useAuthStore()
const toast = useToast()

// Breadcrumb items
const breadcrumbItems = [
  { text: 'Dashboard', to: '/admin' },
  { text: 'Users', to: '/admin/users' },
  { text: 'Invitations', to: '/admin/users/invites', active: true }
]

// State
const loading = ref(false)
const submitting = ref(false)
const invitations = ref<any[]>([])

// Invite form
const inviteForm = ref({
  email: '',
  role: 'USER',
  message: ''
})

// Table columns
const columns = [
  {
    title: 'Email',
    dataIndex: 'email',
    key: 'email',
    sorter: (a: any, b: any) => a.email.localeCompare(b.email)
  },
  {
    title: 'Role',
    dataIndex: 'role',
    key: 'role',
    filters: [
      { text: 'User', value: 'USER' },
      { text: 'Admin', value: 'ADMIN' }
    ],
    onFilter: (value: string, record: any) => record.role === value
  },
  {
    title: 'Status',
    dataIndex: 'status',
    key: 'status',
    filters: [
      { text: 'Pending', value: 'PENDING' },
      { text: 'Accepted', value: 'ACCEPTED' },
      { text: 'Expired', value: 'EXPIRED' },
      { text: 'Cancelled', value: 'CANCELLED' }
    ],
    onFilter: (value: string, record: any) => record.status === value
  },
  {
    title: 'Invited By',
    dataIndex: 'invitedBy',
    key: 'invitedBy',
    render: (text: string, record: any) => record.invitedBy?.fullName || 'System'
  },
  {
    title: 'Invited At',
    dataIndex: 'createdAt',
    key: 'createdAt',
    render: (text: string) => new Date(text).toLocaleString(),
    sorter: (a: any, b: any) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
  },
  {
    title: 'Expires At',
    dataIndex: 'expiresAt',
    key: 'expiresAt',
    render: (text: string) => text ? new Date(text).toLocaleString() : 'Never',
    sorter: (a: any, b: any) => {
      if (!a.expiresAt) return 1
      if (!b.expiresAt) return -1
      return new Date(a.expiresAt).getTime() - new Date(b.expiresAt).getTime()
    }
  },
  {
    title: 'Actions',
    key: 'actions'
  }
]

// Fetch invitations
const fetchInvitations = async () => {
  try {
    loading.value = true
    
    const response = await api.get('/admin/invitations', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    // Process response
    if (response.data && response.data.data) {
      invitations.value = response.data.data
    } else {
      invitations.value = response.data || []
    }
  } catch (error) {
    console.error('Error fetching invitations:', error)
    toast.error('Failed to load invitations')
  } finally {
    loading.value = false
  }
}

// Get status color
const getStatusColor = (status: string) => {
  switch (status) {
    case 'PENDING':
      return 'blue'
    case 'ACCEPTED':
      return 'green'
    case 'EXPIRED':
      return 'orange'
    case 'CANCELLED':
      return 'red'
    default:
      return 'default'
  }
}

// Send invitation
const handleInviteSubmit = async () => {
  try {
    submitting.value = true
    
    const response = await api.post('/admin/invitations', inviteForm.value, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    // Add to list
    const newInvitation = response.data.data || response.data
    invitations.value.unshift(newInvitation)
    
    // Reset form
    inviteForm.value = {
      email: '',
      role: 'USER',
      message: ''
    }
    
    toast.success('Invitation sent successfully')
  } catch (error) {
    console.error('Error sending invitation:', error)
    toast.error('Failed to send invitation')
  } finally {
    submitting.value = false
  }
}

// Resend invitation
const resendInvitation = async (invitation: any) => {
  try {
    loading.value = true
    
    await api.post(`/admin/invitations/${invitation.id}/resend`, {}, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    toast.success('Invitation resent successfully')
    
    // Refresh list
    await fetchInvitations()
  } catch (error) {
    console.error('Error resending invitation:', error)
    toast.error('Failed to resend invitation')
  } finally {
    loading.value = false
  }
}

// Cancel invitation
const confirmCancelInvitation = (invitation: any) => {
  Modal.confirm({
    title: 'Are you sure you want to cancel this invitation?',
    content: 'This action cannot be undone.',
    okText: 'Yes, Cancel Invitation',
    okType: 'danger',
    cancelText: 'No',
    onOk: () => cancelInvitation(invitation.id)
  })
}

const cancelInvitation = async (invitationId: string) => {
  try {
    loading.value = true
    
    await api.delete(`/admin/invitations/${invitationId}`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    // Update status in list
    const index = invitations.value.findIndex(inv => inv.id === invitationId)
    if (index !== -1) {
      invitations.value[index].status = 'CANCELLED'
    }
    
    toast.success('Invitation cancelled successfully')
  } catch (error) {
    console.error('Error cancelling invitation:', error)
    toast.error('Failed to cancel invitation')
  } finally {
    loading.value = false
  }
}

// Initialize on mount
onMounted(() => {
  fetchInvitations()
})
</script>
