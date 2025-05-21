import { Directive, DirectiveBinding } from 'vue'
import { useAuthStore } from '@/stores/auth'

/**
 * Custom directive for permission-based element visibility
 * 
 * Usage:
 * v-permission="'user:read'"                 // Single permission
 * v-permission="['user:read', 'user:write']" // Any of these permissions (OR)
 * v-permission.all="['user:read', 'user:write']" // All of these permissions (AND)
 * v-permission.not="'user:delete'"           // Hide if user has this permission
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const authStore = useAuthStore()
    
    // Skip for unauthenticated users
    if (!authStore.isAuthenticated) {
      el.style.display = 'none'
      return
    }
    
    const { value, modifiers } = binding
    
    // Handle negation with .not modifier
    if (modifiers.not) {
      const hasPermission = typeof value === 'string'
        ? authStore.hasPermission(value)
        : authStore.hasAnyPermission(value)
      
      if (hasPermission) {
        el.style.display = 'none'
      }
      return
    }
    
    // Handle .all modifier (requires all permissions)
    if (modifiers.all && Array.isArray(value)) {
      if (!authStore.hasAllPermissions(value)) {
        el.style.display = 'none'
      }
      return
    }
    
    // Default behavior: check for any permission
    const hasPermission = typeof value === 'string'
      ? authStore.hasPermission(value)
      : authStore.hasAnyPermission(value)
    
    if (!hasPermission) {
      el.style.display = 'none'
    }
  }
}

export default permission
