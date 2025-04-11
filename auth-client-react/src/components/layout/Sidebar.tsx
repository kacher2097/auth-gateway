import React from 'react';
import { Layout } from 'antd';
import SidebarHeader from './SidebarHeader';
import SidebarMenu from './SidebarMenu';
import SidebarFooter from './SidebarFooter';

const { Sider } = Layout;

interface SidebarProps {
  collapsed: boolean;
  onCollapse: (collapsed: boolean) => void;
  isMobile: boolean;
  onMobileClose: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({
  collapsed,
  onCollapse,
  isMobile
}) => {
  return (
    <Sider
      trigger={null}
      collapsible
      collapsed={collapsed}
      breakpoint="lg"
      collapsedWidth={isMobile ? 0 : 80}
      width={250}
      theme="dark"
      className="site-layout-background"
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
        zIndex: 1000,
      }}
    >
      {/* Logo and title */}
      <SidebarHeader collapsed={collapsed} />

      {/* Menu and quick actions */}
      <SidebarMenu collapsed={collapsed} onCollapse={onCollapse} />

      {/* User profile */}
      <SidebarFooter collapsed={collapsed} />
    </Sider>
  );
};

export default Sidebar;
