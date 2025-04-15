import React, { lazy, Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Spin } from 'antd';
import { AuthProvider } from '../context/AuthContext';
import PrivateRoute from './PrivateRoute';
import AdminRoute from './AdminRoute';
import AdminLayout from '../components/layout/AdminLayout';

// Error Boundary Component
class ErrorBoundary extends React.Component<
  { children: React.ReactNode },
  { hasError: boolean }
> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(): { hasError: boolean } {
    return { hasError: true };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    // You can log the error to an error reporting service here
    console.error('Error caught by ErrorBoundary:', error, errorInfo);
  }

  render(): React.ReactNode {
    if (this.state.hasError) {
      return (
        <div className="error-boundary" style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          height: '100vh',
          padding: '20px',
          textAlign: 'center',
          backgroundColor: '#f8f9fa'
        }}>
          <h1 style={{ color: '#343a40', marginBottom: '20px' }}>Oops! Something went wrong</h1>
          <p style={{ color: '#6c757d', marginBottom: '30px', maxWidth: '500px' }}>
            We're sorry, but we couldn't load this page. This could be due to a temporary issue or a network problem.
          </p>
          <button
            onClick={() => window.location.reload()}
            style={{
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              padding: '10px 20px',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '16px'
            }}
          >
            Reload page
          </button>
          <p style={{ marginTop: '20px', fontSize: '14px', color: '#6c757d' }}>
            If the problem persists, please contact support.
          </p>
        </div>
      );
    }

    return this.props.children;
  }
}

// Lazy load components
const Login = lazy(() => import('../pages/auth/Login'));
const Register = lazy(() => import('../pages/auth/Register'));
const ForgotPassword = lazy(() => import('../pages/auth/ForgotPassword'));
const ResetPassword = lazy(() => import('../pages/auth/ResetPassword'));
const OAuthCallback = lazy(() => import('../pages/auth/OAuthCallback'));
const Home = lazy(() => import('../pages/Home'));
const Profile = lazy(() => import('../pages/Profile'));
const NotFound = lazy(() => import('../pages/NotFound'));

// Admin pages
const Overview = lazy(() => import('../pages/admin/dashboard/Overview'));
const Statistics = lazy(() => import('../pages/admin/dashboard/Statistics'));
const UserList = lazy(() => import('../pages/admin/users/UserList'));
const UserRoles = lazy(() => import('../pages/admin/users/UserRoles'));
const UserInvites = lazy(() => import('../pages/admin/users/UserInvites'));
const NewUser = lazy(() => import('../pages/admin/users/NewUser'));
const Traffic = lazy(() => import('../pages/admin/analytics/Traffic'));
const UserActivity = lazy(() => import('../pages/admin/analytics/UserActivity'));
const Reports = lazy(() => import('../pages/admin/analytics/Reports'));
const NewReport = lazy(() => import('../pages/admin/analytics/NewReport'));

// Proxy management pages
const ProxyList = lazy(() => import('../pages/admin/proxies/ProxyList'));
const NewProxy = lazy(() => import('../pages/admin/proxies/NewProxy'));
const ProxyImport = lazy(() => import('../pages/admin/proxies/ProxyImport'));
const General = lazy(() => import('../pages/admin/settings/General'));
const Security = lazy(() => import('../pages/admin/settings/Security'));
const Appearance = lazy(() => import('../pages/admin/settings/Appearance'));
const Notifications = lazy(() => import('../pages/admin/settings/Notifications'));

// Loading component
const LoadingFallback: React.FC = () => (
  <div style={{
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100vh'
  }}>
    <Spin size="large" />
  </div>
);

const AppRoutes: React.FC = () => {
  return (
    <AuthProvider>
      <ErrorBoundary>
        <Suspense fallback={<LoadingFallback />}>
          <Routes>
            {/* Public routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password/:token" element={<ResetPassword />} />
            <Route path="/oauth/callback/:provider" element={<OAuthCallback />} />

            {/* Protected routes */}
            <Route path="/" element={<PrivateRoute element={<Home />} />} />
            <Route path="/profile" element={<PrivateRoute element={<Profile />} />} />

            {/* Admin routes */}
            <Route path="/admin" element={<AdminRoute element={<AdminLayout />} />}>
              <Route index element={<Navigate to="/admin/overview" replace />} />

              {/* Dashboard */}
              <Route path="overview" element={<Overview />} />
              <Route path="statistics" element={<Statistics />} />

              {/* Users */}
              <Route path="users" element={<Navigate to="/admin/users/list" replace />} />
              <Route path="users/list" element={<UserList />} />
              <Route path="users/roles" element={<UserRoles />} />
              <Route path="users/invites" element={<UserInvites />} />
              <Route path="users/new" element={<NewUser />} />

              {/* Analytics */}
              <Route path="analytics" element={<Navigate to="/admin/analytics/traffic" replace />} />
              <Route path="analytics/traffic" element={<Traffic />} />
              <Route path="analytics/users" element={<UserActivity />} />
              <Route path="analytics/reports" element={<Reports />} />
              <Route path="analytics/reports/new" element={<NewReport />} />

              {/* Proxies */}
              <Route path="proxies" element={<Navigate to="/admin/proxies/list" replace />} />
              <Route path="proxies/list" element={<ProxyList />} />
              <Route path="proxies/new" element={<NewProxy />} />
              <Route path="proxies/import" element={<ProxyImport />} />

              {/* Settings */}
              <Route path="settings" element={<Navigate to="/admin/settings/general" replace />} />
              <Route path="settings/general" element={<General />} />
              <Route path="settings/security" element={<Security />} />
              <Route path="settings/appearance" element={<Appearance />} />
              <Route path="settings/notifications" element={<Notifications />} />
            </Route>

            {/* 404 Not Found */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </ErrorBoundary>
    </AuthProvider>
  );
};

export default AppRoutes;
