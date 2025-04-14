import React, { useState, useEffect } from 'react';
import { Layout, theme, Grid } from 'antd';
import Sidebar from './Sidebar';
import Header from './Header';
import Footer from './Footer';
import { Outlet } from 'react-router-dom';

const { Content } = Layout;
const { useBreakpoint } = Grid;

interface AdminLayoutProps {
  title?: string;
  children: React.ReactNode;
  showBreadcrumb?: boolean;
  breadcrumbItems?: { title: React.ReactNode; href?: string }[];
  sidebarTheme?: 'dark' | 'light';
}

const AdminLayout: React.FC<AdminLayoutProps> = ({
  title,
  children,
  showBreadcrumb = true,
  breadcrumbItems,
  sidebarTheme = 'dark'
}) => {
  const [collapsed, setCollapsed] = useState(false);
  const [isMobile, setIsMobile] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  const screens = useBreakpoint();
  const { token } = theme.useToken();

  // Check if mobile on mount and on screen resize
  useEffect(() => {
    setIsMobile(!screens.lg);

    // Load collapsed state from localStorage (desktop only)
    if (screens.lg) {
      const savedState = localStorage.getItem('sidebar-collapsed');
      if (savedState !== null) {
        setCollapsed(savedState === 'true');
      }
    }
  }, [screens]);

  // Save collapsed state to localStorage
  const handleCollapse = (collapsed: boolean) => {
    setCollapsed(collapsed);
    localStorage.setItem('sidebar-collapsed', collapsed.toString());
  };

  // Handle mobile sidebar open/close
  const handleMobileOpen = () => {
    setMobileOpen(true);
  };

  const handleMobileClose = () => {
    setMobileOpen(false);
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar
        collapsed={isMobile ? !mobileOpen : collapsed}
        onCollapse={handleCollapse}
        isMobile={isMobile}
        onMobileClose={handleMobileClose}
      />

      <Layout style={{
        marginLeft: isMobile ? 0 : (collapsed ? 80 : 250),
        transition: 'margin-left 0.2s',
      }}>
        <Header
          title={title}
          isMobile={isMobile}
          mobileOpen={mobileOpen}
          handleMobileOpen={handleMobileOpen}
          handleMobileClose={handleMobileClose}
          theme={token}
        />

        <Content style={{
          margin: '24px 16px',
          padding: 24,
          background: token.colorBgContainer,
          borderRadius: token.borderRadiusLG,
          minHeight: 280,
        }}>
          <Outlet />
        </Content>

        <Footer />
      </Layout>
    </Layout>
  );
};

export default AdminLayout;
