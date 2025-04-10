import { useAuthStore } from '@/stores/auth'
import api from './api'
import { ApiError } from '@/utils/errorHandler'

/**
 * Service to handle role-based access control
 */
export class AuthGuardService {
  /**
   * Check if the current user has admin access
   */
  static async hasAdminAccess(): Promise<boolean> {
    const authStore = useAuthStore()
    
    // If not authenticated, definitely not an admin
    if (!authStore.isAuthenticated) {
      return false
    }
    
    // If we already know the user is an admin from the store
    if (authStore.isAdmin) {
      return true
    }
    
    // Double-check with the server (in case role has changed)
    try {
      const response = await api.get('/auth/check-role', {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      })
      
      return response.data?.data?.isAdmin || false
    } catch (error) {
      if (error instanceof ApiError) {
        console.error('Error checking admin access:', error.message)
      } else {
        console.error('Unknown error checking admin access:', error)
      }
      return false
    }
  }
  
  /**
   * Check if the current user has a specific role
   */
  static async hasRole(role: string): Promise<boolean> {
    const authStore = useAuthStore()
    
    // If not authenticated, definitely doesn't have the role
    if (!authStore.isAuthenticated) {
      return false
    }
    
    // If we already know the user has this role from the store
    if (authStore.hasRole(role)) {
      return true
    }
    
    // Double-check with the server (in case role has changed)
    try {
      const response = await api.get('/auth/check-role', {
        headers: {
          Authorization: `Bearer ${authStore.token}`
        }
      })
      
      return response.data?.data?.role === role
    } catch (error) {
      if (error instanceof ApiError) {
        console.error(`Error checking ${role} access:`, error.message)
      } else {
        console.error(`Unknown error checking ${role} access:`, error)
      }
      return false
    }
  }
}

export default AuthGuardService
