import { useAuthStore } from '@/stores/auth'

/**
 * Check if the current user has a specific permission
 * @param permission The permission to check
 * @returns True if the user has the permission, false otherwise
 */
export function usePermission(permission: string): boolean {
  const authStore = useAuthStore()
  return authStore.hasPermission(permission)
}

/**
 * Check if the current user has any of the specified permissions
 * @param permissions Array of permissions to check
 * @returns True if the user has any of the permissions, false otherwise
 */
export function useAnyPermission(permissions: string[]): boolean {
  const authStore = useAuthStore()
  return authStore.hasAnyPermission(permissions)
}

/**
 * Check if the current user has all of the specified permissions
 * @param permissions Array of permissions to check
 * @returns True if the user has all of the permissions, false otherwise
 */
export function useAllPermissions(permissions: string[]): boolean {
  const authStore = useAuthStore()
  return authStore.hasAllPermissions(permissions)
}

/**
 * Get all permissions of the current user
 * @returns Array of permission strings
 */
export function useUserPermissions(): string[] {
  const authStore = useAuthStore()
  return authStore.permissions
}

/**
 * Check if the current user is an admin
 * @returns True if the user is an admin, false otherwise
 */
export function useIsAdmin(): boolean {
  const authStore = useAuthStore()
  return authStore.isAdmin
}

/**
 * Check if the current user is authenticated
 * @returns True if the user is authenticated, false otherwise
 */
export function useIsAuthenticated(): boolean {
  const authStore = useAuthStore()
  return authStore.isAuthenticated
}

export default {
  usePermission,
  useAnyPermission,
  useAllPermissions,
  useUserPermissions,
  useIsAdmin,
  useIsAuthenticated
}
