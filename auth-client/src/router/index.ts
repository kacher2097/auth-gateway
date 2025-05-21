import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Profile from '@/views/Profile.vue'
import ForgotPassword from '@/views/ForgotPassword.vue'
import ResetPassword from '@/views/ResetPassword.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import UserManagement from '@/views/admin/UserManagement.vue'
import Analytics from '@/views/admin/Analytics.vue'
import Settings from '@/views/admin/Settings.vue'
import NotFound from '@/views/NotFound.vue'
import OAuthCallback from '@/views/OAuthCallback.vue'
import ProxyList from '@/views/ProxyList.vue'
import NewProxy from '@/views/NewProxy.vue'
import ProxyImport from '@/views/ProxyImport.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: Home,
    meta: { title: 'Home' }
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { title: 'Login', guest: true }
  },
  {
    path: '/register',
    name: 'register',
    component: Register,
    meta: { title: 'Register', guest: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: Profile,
    meta: { title: 'Profile', requiresAuth: true }
  },
  {
    path: '/oauth/callback/:provider',
    name: 'oauth-callback',
    component: OAuthCallback,
    meta: { title: 'Processing Authentication' }
  },
  {
    path: '/forgot-password',
    name: 'forgot-password',
    component: ForgotPassword,
    meta: { title: 'Forgot Password', guest: true }
  },
  {
    path: '/reset-password',
    name: 'reset-password',
    component: ResetPassword,
    meta: { title: 'Reset Password', guest: true }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/DashboardWrapper.vue'),
    meta: {
      title: 'Admin Dashboard',
      requiresAuth: true
      // No permissions required - all authenticated users can access
    },
    children: [
      {
        path: '',
        name: 'admin-dashboard',
        component: AdminDashboard,
        meta: {
          title: 'Admin Dashboard',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/admin/overview',
    name: 'admin-overview',
    component: AdminDashboard,
    meta: {
      title: 'Dashboard Overview',
      requiresAuth: true,
      permissions: ['admin:dashboard', 'admin:view', 'admin:all']
    }
  },
  {
    path: '/admin/statistics',
    name: 'admin-statistics',
    component: AdminDashboard,
    meta: {
      title: 'Dashboard Statistics',
      requiresAuth: true,
      permissions: ['admin:statistics', 'admin:view', 'admin:all']
    }
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: UserManagement,
    meta: {
      title: 'User Management',
      requiresAuth: true,
      permissions: ['user:manage', 'user:read', 'admin:all']
    }
  },
  {
    path: '/admin/users/list',
    name: 'admin-users-list',
    component: UserManagement,
    meta: { title: 'All Users', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users/roles',
    name: 'admin-users-roles',
    component: UserManagement,
    meta: { title: 'Roles & Permissions', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users/invites',
    name: 'admin-users-invites',
    component: UserManagement,
    meta: { title: 'User Invitations', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users/new',
    name: 'admin-users-new',
    component: UserManagement,
    meta: { title: 'New User', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/analytics',
    name: 'admin-analytics',
    component: Analytics,
    meta: { title: 'Analytics', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/analytics/traffic',
    name: 'admin-analytics-traffic',
    component: Analytics,
    meta: { title: 'Traffic Analytics', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/analytics/users',
    name: 'admin-analytics-users',
    component: Analytics,
    meta: { title: 'User Activity', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/analytics/reports',
    name: 'admin-analytics-reports',
    component: Analytics,
    meta: { title: 'Analytics Reports', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/analytics/reports/new',
    name: 'admin-analytics-reports-new',
    component: Analytics,
    meta: { title: 'New Report', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/settings',
    name: 'admin-settings',
    component: Settings,
    meta: { title: 'Admin Settings', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/settings/general',
    name: 'admin-settings-general',
    component: Settings,
    meta: { title: 'General Settings', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/settings/security',
    name: 'admin-settings-security',
    component: Settings,
    meta: { title: 'Security Settings', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/settings/appearance',
    name: 'admin-settings-appearance',
    component: Settings,
    meta: { title: 'Appearance Settings', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/settings/notifications',
    name: 'admin-settings-notifications',
    component: Settings,
    meta: { title: 'Notification Settings', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/proxies',
    name: 'admin-proxies',
    component: ProxyList,
    meta: { title: 'Proxy Management', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/proxies/new',
    name: 'admin-proxies-new',
    component: NewProxy,
    meta: { title: 'Add New Proxy', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/proxies/import',
    name: 'admin-proxies-import',
    component: ProxyImport,
    meta: { title: 'Import Proxies', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFound,
    meta: { title: 'Page Not Found' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// Navigation guards
router.beforeEach(async (to, from, next) => {
  // Set page title
  document.title = `${to.meta.title} | AuthenHub` || 'AuthenHub'

  const authStore = useAuthStore()

  // Initialize auth store if not already done
  if (!authStore.user && authStore.token) {
    await authStore.initialize()
  }

  // Handle protected routes
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }

  // Handle permission-based routes
  if (to.meta.permissions) {
    // Skip permission check for the main admin dashboard
    if (to.name === 'admin-dashboard') {
      // Allow access to the main admin dashboard for all authenticated users
    } else {
      const requiredPermissions = to.meta.permissions as string[]

      // Check if user has any of the required permissions
      if (!authStore.hasAnyPermission(requiredPermissions)) {
        // If user doesn't have permission, redirect to home
        next({ name: 'home' })
        return
      }
    }
  }

  // Handle admin-only routes (legacy support)
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next({ name: 'home' })
    return
  }

  // Handle guest-only routes (like login/register)
  if (to.meta.guest && authStore.isAuthenticated) {
    next({ name: 'home' })
    return
  }

  // If all checks pass, proceed to the route
  next()
})

export default router
