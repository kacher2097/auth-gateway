/**
 * Configuration for admin dashboard permissions
 * 
 * This file defines the permissions required for each section of the admin dashboard.
 * It's used to conditionally show/hide UI elements based on the user's permissions.
 */

export const adminPermissions = {
  // Dashboard sections
  dashboard: {
    view: ['admin:dashboard', 'admin:view', 'admin:all'],
    overview: ['admin:dashboard', 'admin:view', 'admin:all'],
    statistics: ['admin:statistics', 'admin:view', 'admin:all']
  },
  
  // User management
  users: {
    view: ['user:read', 'user:manage', 'admin:all'],
    create: ['user:create', 'user:manage', 'admin:all'],
    edit: ['user:edit', 'user:manage', 'admin:all'],
    delete: ['user:delete', 'user:manage', 'admin:all'],
    roles: ['role:read', 'role:manage', 'admin:all']
  },
  
  // Analytics
  analytics: {
    view: ['analytics:view', 'analytics:manage', 'admin:all'],
    traffic: ['analytics:traffic', 'analytics:view', 'admin:all'],
    users: ['analytics:users', 'analytics:view', 'admin:all'],
    reports: ['analytics:reports', 'analytics:view', 'admin:all']
  },
  
  // Settings
  settings: {
    view: ['settings:view', 'settings:manage', 'admin:all'],
    general: ['settings:general', 'settings:view', 'admin:all'],
    security: ['settings:security', 'settings:view', 'admin:all'],
    notifications: ['settings:notifications', 'settings:view', 'admin:all']
  },
  
  // Proxies
  proxies: {
    view: ['proxy:read', 'proxy:manage', 'admin:all'],
    create: ['proxy:create', 'proxy:manage', 'admin:all'],
    edit: ['proxy:edit', 'proxy:manage', 'admin:all'],
    delete: ['proxy:delete', 'proxy:manage', 'admin:all'],
    import: ['proxy:import', 'proxy:manage', 'admin:all']
  }
}

export default adminPermissions
