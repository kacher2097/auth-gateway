import api from './api'
import { useAuthStore } from '@/stores/auth'

export interface DashboardStats {
  totalUsers: number
  activeUsers: number
  newUsers: number
  socialLogins: number
  userGrowth: {
    labels: string[]
    data: number[]
  }
  loginMethods: number[]
  recentActivity: Array<{
    id: string
    user: {
      fullName: string
      email: string
      avatar?: string
    }
    action: string
    timestamp: string
  }>
}

export default {
  async getDashboardStats(): Promise<DashboardStats> {
    const authStore = useAuthStore()
    const response = await api.get('/admin/dashboard', {
      headers: { Authorization: `Bearer ${authStore.token}` }
    })
    return response.data
  },

  async getUsers(params: { page?: number; limit?: number; search?: string } = {}) {
    const authStore = useAuthStore()
    const response = await api.get('/admin/users', {
      params,
      headers: { Authorization: `Bearer ${authStore.token}` }
    })
    return response.data
  },

  async updateUser(userId: string, data: {
    role?: string
    active?: boolean
    fullName?: string
    email?: string
  }) {
    const authStore = useAuthStore()
    const response = await api.put(`/admin/users/${userId}`, data, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    })
    return response.data
  },

  async deleteUser(userId: string) {
    const authStore = useAuthStore()
    await api.delete(`/admin/users/${userId}`, {
      headers: { Authorization: `Bearer ${authStore.token}` }
    })
  }
}