<template>
  <div>
    <Breadcrumb :items="breadcrumbItems" />
    
    <div class="bg-white shadow rounded-lg p-6 mt-4">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">Roles & Permissions</h2>
      
      <!-- Roles Table -->
      <div class="mb-8">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-medium text-gray-700">System Roles</h3>
          <Button type="primary" @click="showAddRoleModal">Add New Role</Button>
        </div>
        
        <Table
          :columns="roleColumns"
          :data-source="roles"
          :loading="loading"
          :pagination="{ pageSize: 5 }"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actions'">
              <Space>
                <Button type="primary" size="small" @click="editRole(record)">
                  <template #icon><EditOutlined /></template>
                  Edit
                </Button>
                <Button 
                  type="primary" 
                  danger 
                  size="small" 
                  :disabled="record.name === 'ADMIN' || record.name === 'USER'"
                  @click="confirmDeleteRole(record)"
                >
                  <template #icon><DeleteOutlined /></template>
                  Delete
                </Button>
              </Space>
            </template>
          </template>
        </Table>
      </div>
      
      <!-- Permissions Table -->
      <div>
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-medium text-gray-700">Permissions</h3>
          <Button type="primary" @click="showAddPermissionModal">Add New Permission</Button>
        </div>
        
        <Table
          :columns="permissionColumns"
          :data-source="permissions"
          :loading="loading"
          :pagination="{ pageSize: 5 }"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'actions'">
              <Space>
                <Button type="primary" size="small" @click="editPermission(record)">
                  <template #icon><EditOutlined /></template>
                  Edit
                </Button>
                <Button 
                  type="primary" 
                  danger 
                  size="small" 
                  @click="confirmDeletePermission(record)"
                >
                  <template #icon><DeleteOutlined /></template>
                  Delete
                </Button>
              </Space>
            </template>
          </template>
        </Table>
      </div>
    </div>
    
    <!-- Add/Edit Role Modal -->
    <Modal
      :title="editingRole ? 'Edit Role' : 'Add New Role'"
      :open="roleModalVisible"
      :confirm-loading="submitting"
      @ok="handleRoleSubmit"
      @cancel="roleModalVisible = false"
    >
      <Form
        :model="roleForm"
        layout="vertical"
      >
        <Form.Item
          label="Role Name"
          name="name"
          :rules="[{ required: true, message: 'Please enter role name' }]"
        >
          <Input v-model:value="roleForm.name" :disabled="editingRole && (roleForm.name === 'ADMIN' || roleForm.name === 'USER')" />
        </Form.Item>
        
        <Form.Item
          label="Description"
          name="description"
        >
          <Input.TextArea v-model:value="roleForm.description" rows="3" />
        </Form.Item>
        
        <Form.Item
          label="Permissions"
          name="permissions"
        >
          <Select
            v-model:value="roleForm.permissions"
            mode="multiple"
            placeholder="Select permissions"
            style="width: 100%"
          >
            <Select.Option v-for="permission in permissions" :key="permission.id" :value="permission.id">
              {{ permission.name }}
            </Select.Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
    
    <!-- Add/Edit Permission Modal -->
    <Modal
      :title="editingPermission ? 'Edit Permission' : 'Add New Permission'"
      :open="permissionModalVisible"
      :confirm-loading="submitting"
      @ok="handlePermissionSubmit"
      @cancel="permissionModalVisible = false"
    >
      <Form
        :model="permissionForm"
        layout="vertical"
      >
        <Form.Item
          label="Permission Name"
          name="name"
          :rules="[{ required: true, message: 'Please enter permission name' }]"
        >
          <Input v-model:value="permissionForm.name" />
        </Form.Item>
        
        <Form.Item
          label="Description"
          name="description"
        >
          <Input.TextArea v-model:value="permissionForm.description" rows="3" />
        </Form.Item>
      </Form>
    </Modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { useToast } from '@/components/ui/ToastContainer.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import { Button, Table, Space, Modal, Form, Input, Select } from 'ant-design-vue'
import { EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'

// Initialize auth store and toast
const authStore = useAuthStore()
const toast = useToast()

// Breadcrumb items
const breadcrumbItems = [
  { text: 'Dashboard', to: '/admin' },
  { text: 'Users', to: '/admin/users' },
  { text: 'Roles & Permissions', to: '/admin/users/roles', active: true }
]

// State
const loading = ref(false)
const submitting = ref(false)
const roles = ref<any[]>([])
const permissions = ref<any[]>([])

// Role modal
const roleModalVisible = ref(false)
const editingRole = ref(false)
const roleForm = ref({
  id: '',
  name: '',
  description: '',
  permissions: [] as string[]
})

// Permission modal
const permissionModalVisible = ref(false)
const editingPermission = ref(false)
const permissionForm = ref({
  id: '',
  name: '',
  description: ''
})

// Table columns
const roleColumns = [
  {
    title: 'Role Name',
    dataIndex: 'name',
    key: 'name',
    sorter: (a: any, b: any) => a.name.localeCompare(b.name)
  },
  {
    title: 'Description',
    dataIndex: 'description',
    key: 'description'
  },
  {
    title: 'Permissions',
    key: 'permissions',
    render: (text: string, record: any) => (
      <span>
        {record.permissions?.map((permission: any) => (
          <Tag color="blue" key={permission.id}>
            {permission.name}
          </Tag>
        ))}
      </span>
    )
  },
  {
    title: 'Actions',
    key: 'actions'
  }
]

const permissionColumns = [
  {
    title: 'Permission Name',
    dataIndex: 'name',
    key: 'name',
    sorter: (a: any, b: any) => a.name.localeCompare(b.name)
  },
  {
    title: 'Description',
    dataIndex: 'description',
    key: 'description'
  },
  {
    title: 'Actions',
    key: 'actions'
  }
]

// Fetch roles and permissions
const fetchRolesAndPermissions = async () => {
  try {
    loading.value = true
    
    // Fetch roles
    const rolesResponse = await api.get('/admin/roles', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    // Fetch permissions
    const permissionsResponse = await api.get('/admin/permissions', {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    // Process responses
    if (rolesResponse.data && rolesResponse.data.data) {
      roles.value = rolesResponse.data.data
    } else {
      roles.value = rolesResponse.data || []
    }
    
    if (permissionsResponse.data && permissionsResponse.data.data) {
      permissions.value = permissionsResponse.data.data
    } else {
      permissions.value = permissionsResponse.data || []
    }
  } catch (error) {
    console.error('Error fetching roles and permissions:', error)
    toast.error('Failed to load roles and permissions')
  } finally {
    loading.value = false
  }
}

// Role functions
const showAddRoleModal = () => {
  editingRole.value = false
  roleForm.value = {
    id: '',
    name: '',
    description: '',
    permissions: []
  }
  roleModalVisible.value = true
}

const editRole = (role: any) => {
  editingRole.value = true
  roleForm.value = {
    id: role.id,
    name: role.name,
    description: role.description || '',
    permissions: role.permissions?.map((p: any) => p.id) || []
  }
  roleModalVisible.value = true
}

const confirmDeleteRole = (role: any) => {
  Modal.confirm({
    title: 'Are you sure you want to delete this role?',
    content: 'This action cannot be undone.',
    okText: 'Yes, Delete',
    okType: 'danger',
    cancelText: 'Cancel',
    onOk: () => deleteRole(role.id)
  })
}

const deleteRole = async (roleId: string) => {
  try {
    loading.value = true
    
    await api.delete(`/admin/roles/${roleId}`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    // Remove from list
    roles.value = roles.value.filter(role => role.id !== roleId)
    
    toast.success('Role deleted successfully')
  } catch (error) {
    console.error('Error deleting role:', error)
    toast.error('Failed to delete role')
  } finally {
    loading.value = false
  }
}

const handleRoleSubmit = async () => {
  try {
    submitting.value = true
    
    if (editingRole.value) {
      // Update existing role
      const response = await api.put(`/admin/roles/${roleForm.value.id}`, roleForm.value, {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      })
      
      // Update in list
      const updatedRole = response.data.data || response.data
      const index = roles.value.findIndex(role => role.id === updatedRole.id)
      if (index !== -1) {
        roles.value[index] = updatedRole
      }
      
      toast.success('Role updated successfully')
    } else {
      // Create new role
      const response = await api.post('/admin/roles', roleForm.value, {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      })
      
      // Add to list
      const newRole = response.data.data || response.data
      roles.value.push(newRole)
      
      toast.success('Role created successfully')
    }
    
    roleModalVisible.value = false
  } catch (error) {
    console.error('Error saving role:', error)
    toast.error('Failed to save role')
  } finally {
    submitting.value = false
  }
}

// Permission functions
const showAddPermissionModal = () => {
  editingPermission.value = false
  permissionForm.value = {
    id: '',
    name: '',
    description: ''
  }
  permissionModalVisible.value = true
}

const editPermission = (permission: any) => {
  editingPermission.value = true
  permissionForm.value = {
    id: permission.id,
    name: permission.name,
    description: permission.description || ''
  }
  permissionModalVisible.value = true
}

const confirmDeletePermission = (permission: any) => {
  Modal.confirm({
    title: 'Are you sure you want to delete this permission?',
    content: 'This action cannot be undone.',
    okText: 'Yes, Delete',
    okType: 'danger',
    cancelText: 'Cancel',
    onOk: () => deletePermission(permission.id)
  })
}

const deletePermission = async (permissionId: string) => {
  try {
    loading.value = true
    
    await api.delete(`/admin/permissions/${permissionId}`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    // Remove from list
    permissions.value = permissions.value.filter(permission => permission.id !== permissionId)
    
    toast.success('Permission deleted successfully')
  } catch (error) {
    console.error('Error deleting permission:', error)
    toast.error('Failed to delete permission')
  } finally {
    loading.value = false
  }
}

const handlePermissionSubmit = async () => {
  try {
    submitting.value = true
    
    if (editingPermission.value) {
      // Update existing permission
      const response = await api.put(`/admin/permissions/${permissionForm.value.id}`, permissionForm.value, {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      })
      
      // Update in list
      const updatedPermission = response.data.data || response.data
      const index = permissions.value.findIndex(permission => permission.id === updatedPermission.id)
      if (index !== -1) {
        permissions.value[index] = updatedPermission
      }
      
      toast.success('Permission updated successfully')
    } else {
      // Create new permission
      const response = await api.post('/admin/permissions', permissionForm.value, {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      })
      
      // Add to list
      const newPermission = response.data.data || response.data
      permissions.value.push(newPermission)
      
      toast.success('Permission created successfully')
    }
    
    permissionModalVisible.value = false
  } catch (error) {
    console.error('Error saving permission:', error)
    toast.error('Failed to save permission')
  } finally {
    submitting.value = false
  }
}

// Initialize on mount
onMounted(() => {
  fetchRolesAndPermissions()
})
</script>
