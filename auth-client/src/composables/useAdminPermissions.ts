import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import adminPermissions from '@/config/adminPermissions'

/**
 * Composable for checking admin dashboard permissions
 * 
 * This composable provides functions to check if the current user has
 * the required permissions for different sections of the admin dashboard.
 */
export function useAdminPermissions() {
  const authStore = useAuthStore()
  
  /**
   * Check if the user has permission to access a specific section
   * 
   * @param section The section to check (e.g., 'dashboard', 'users')
   * @param action The action to check (e.g., 'view', 'create')
   * @returns True if the user has permission, false otherwise
   */
  const hasPermission = (section: string, action: string): boolean => {
    // Get the required permissions for the section and action
    const requiredPermissions = adminPermissions[section]?.[action]
    
    // If no permissions are defined, allow access
    if (!requiredPermissions || requiredPermissions.length === 0) {
      return true
    }
    
    // Check if the user has any of the required permissions
    return authStore.hasAnyPermission(requiredPermissions)
  }
  
  /**
   * Check if the user has permission to view the dashboard
   */
  const canViewDashboard = computed(() => {
    return hasPermission('dashboard', 'view')
  })
  
  /**
   * Check if the user has permission to view the user management section
   */
  const canViewUsers = computed(() => {
    return hasPermission('users', 'view')
  })
  
  /**
   * Check if the user has permission to view the analytics section
   */
  const canViewAnalytics = computed(() => {
    return hasPermission('analytics', 'view')
  })
  
  /**
   * Check if the user has permission to view the settings section
   */
  const canViewSettings = computed(() => {
    return hasPermission('settings', 'view')
  })
  
  /**
   * Check if the user has permission to view the proxies section
   */
  const canViewProxies = computed(() => {
    return hasPermission('proxies', 'view')
  })
  
  /**
   * Get the sections that the user has permission to view
   */
  const availableSections = computed(() => {
    return {
      dashboard: canViewDashboard.value,
      users: canViewUsers.value,
      analytics: canViewAnalytics.value,
      settings: canViewSettings.value,
      proxies: canViewProxies.value
    }
  })
  
  return {
    hasPermission,
    canViewDashboard,
    canViewUsers,
    canViewAnalytics,
    canViewSettings,
    canViewProxies,
    availableSections
  }
}
