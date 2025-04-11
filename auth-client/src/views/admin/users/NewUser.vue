<template>
  <div>
    <Breadcrumb :items="breadcrumbItems" />
    
    <div class="bg-white shadow rounded-lg p-6 mt-4">
      <h2 class="text-xl font-semibold text-gray-800 mb-4">Create New User</h2>
      
      <Form
        :model="userForm"
        layout="vertical"
        @finish="handleSubmit"
      >
        <Row :gutter="16">
          <Col :span="12">
            <Form.Item
              label="Username"
              name="username"
              :rules="[
                { required: true, message: 'Please enter username' },
                { min: 3, message: 'Username must be at least 3 characters' }
              ]"
            >
              <Input v-model:value="userForm.username" placeholder="Enter username" />
            </Form.Item>
          </Col>
          
          <Col :span="12">
            <Form.Item
              label="Email"
              name="email"
              :rules="[
                { required: true, message: 'Please enter email' },
                { type: 'email', message: 'Please enter a valid email' }
              ]"
            >
              <Input v-model:value="userForm.email" placeholder="Enter email address" />
            </Form.Item>
          </Col>
        </Row>
        
        <Row :gutter="16">
          <Col :span="12">
            <Form.Item
              label="Full Name"
              name="fullName"
              :rules="[{ required: true, message: 'Please enter full name' }]"
            >
              <Input v-model:value="userForm.fullName" placeholder="Enter full name" />
            </Form.Item>
          </Col>
          
          <Col :span="12">
            <Form.Item
              label="Role"
              name="role"
              :rules="[{ required: true, message: 'Please select a role' }]"
            >
              <Select v-model:value="userForm.role" placeholder="Select role">
                <Select.Option value="USER">User</Select.Option>
                <Select.Option value="ADMIN">Admin</Select.Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>
        
        <Row :gutter="16">
          <Col :span="12">
            <Form.Item
              label="Password"
              name="password"
              :rules="[
                { required: true, message: 'Please enter password' },
                { min: 8, message: 'Password must be at least 8 characters' }
              ]"
            >
              <Input.Password v-model:value="userForm.password" placeholder="Enter password" />
            </Form.Item>
          </Col>
          
          <Col :span="12">
            <Form.Item
              label="Confirm Password"
              name="confirmPassword"
              :rules="[
                { required: true, message: 'Please confirm password' },
                { validator: validateConfirmPassword }
              ]"
            >
              <Input.Password v-model:value="userForm.confirmPassword" placeholder="Confirm password" />
            </Form.Item>
          </Col>
        </Row>
        
        <Form.Item
          label="Avatar URL (Optional)"
          name="avatar"
        >
          <Input v-model:value="userForm.avatar" placeholder="Enter avatar URL" />
        </Form.Item>
        
        <Form.Item
          name="active"
          valuePropName="checked"
        >
          <Checkbox v-model:checked="userForm.active">Account is active</Checkbox>
        </Form.Item>
        
        <Form.Item
          name="emailVerified"
          valuePropName="checked"
        >
          <Checkbox v-model:checked="userForm.emailVerified">Email is verified</Checkbox>
        </Form.Item>
        
        <Form.Item>
          <Button type="primary" html-type="submit" :loading="submitting">
            <template #icon><UserAddOutlined /></template>
            Create User
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/services/api'
import { useToast } from '@/components/ui/ToastContainer.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import { Button, Form, Input, Select, Checkbox, Row, Col } from 'ant-design-vue'
import { UserAddOutlined } from '@ant-design/icons-vue'

// Initialize auth store, router, and toast
const authStore = useAuthStore()
const router = useRouter()
const toast = useToast()

// Breadcrumb items
const breadcrumbItems = [
  { text: 'Dashboard', to: '/admin' },
  { text: 'Users', to: '/admin/users' },
  { text: 'New User', to: '/admin/users/new', active: true }
]

// State
const submitting = ref(false)

// User form
const userForm = ref({
  username: '',
  email: '',
  fullName: '',
  password: '',
  confirmPassword: '',
  role: 'USER',
  avatar: '',
  active: true,
  emailVerified: true
})

// Validate confirm password
const validateConfirmPassword = (_rule: any, value: string) => {
  if (value !== userForm.value.password) {
    return Promise.reject('The two passwords do not match')
  }
  return Promise.resolve()
}

// Submit form
const handleSubmit = async () => {
  try {
    submitting.value = true
    
    // Create user object (omit confirmPassword)
    const userData = {
      username: userForm.value.username,
      email: userForm.value.email,
      fullName: userForm.value.fullName,
      password: userForm.value.password,
      role: userForm.value.role,
      avatar: userForm.value.avatar || null,
      active: userForm.value.active,
      emailVerified: userForm.value.emailVerified
    }
    
    const response = await api.post('/admin/users', userData, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })
    
    toast.success('User created successfully')
    
    // Navigate to user list
    router.push('/admin/users/list')
  } catch (error: any) {
    console.error('Error creating user:', error)
    
    if (error.response?.data?.message) {
      toast.error(error.response.data.message)
    } else {
      toast.error('Failed to create user')
    }
  } finally {
    submitting.value = false
  }
}

// Reset form
const resetForm = () => {
  userForm.value = {
    username: '',
    email: '',
    fullName: '',
    password: '',
    confirmPassword: '',
    role: 'USER',
    avatar: '',
    active: true,
    emailVerified: true
  }
}
</script>
