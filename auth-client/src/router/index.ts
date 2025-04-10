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
    name: 'admin-dashboard',
    component: AdminDashboard,
    meta: { title: 'Admin Dashboard', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/overview',
    name: 'admin-overview',
    component: AdminDashboard,
    meta: { title: 'Dashboard Overview', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/statistics',
    name: 'admin-statistics',
    component: AdminDashboard,
    meta: { title: 'Dashboard Statistics', requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: UserManagement,
    meta: { title: 'User Management', requiresAuth: true, requiresAdmin: true }
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
  }
  // Handle admin-only routes
  else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next({ name: 'home' })
  }
  // Handle guest-only routes (like login/register)
  else if (to.meta.guest && authStore.isAuthenticated) {
    next({ name: 'home' })
  }
  else {
    next()
  }
})

export default router
