import React, { useState } from 'react';
import { Layout, Menu, Button, Dropdown, Avatar, Typography, Badge } from 'antd';
import { 
  UserOutlined, 
  SettingOutlined, 
  LogoutOutlined, 
  DashboardOutlined,
  MenuOutlined,
  BellOutlined,
  HomeOutlined
} from '@ant-design/icons';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import type { MenuProps } from 'antd';

const { Header: AntHeader } = Layout;
const { Text } = Typography;

interface HeaderProps {
  collapsed?: boolean;
  toggleSidebar?: () => void;
  showSidebarToggle?: boolean;
}

const Header: React.FC<HeaderProps> = ({ 
  collapsed, 
  toggleSidebar, 
  showSidebarToggle = false 
}) => {
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileMenuVisible, setMobileMenuVisible] = useState(false);

  // User dropdown menu items
  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: <Link to="/profile">Hồ sơ cá nhân</Link>,
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: <Link to="/settings">Cài đặt</Link>,
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Đăng xuất',
      onClick: () => logout(),
    },
  ];

  // If user is admin, add admin dashboard link
  if (isAdmin) {
    userMenuItems.splice(2, 0, {
      key: 'admin',
      icon: <DashboardOutlined />,
      label: <Link to="/admin/overview">Quản trị hệ thống</Link>,
    });
  }

  // Mobile menu items
  const mobileMenuItems: MenuProps['items'] = [
    {
      key: 'home',
      label: <Link to="/">Trang chủ</Link>,
      icon: <HomeOutlined />,
    },
    {
      key: 'profile',
      label: <Link to="/profile">Hồ sơ cá nhân</Link>,
      icon: <UserOutlined />,
    },
  ];

  if (isAdmin) {
    mobileMenuItems.push({
      key: 'admin',
      label: <Link to="/admin/overview">Quản trị hệ thống</Link>,
      icon: <DashboardOutlined />,
    });
  }

  mobileMenuItems.push({
    key: 'logout',
    label: 'Đăng xuất',
    icon: <LogoutOutlined />,
    onClick: () => logout(),
  });

  return (
    <AntHeader style={{ 
      background: '#fff', 
      padding: '0 24px',
      boxShadow: '0 1px 4px rgba(0, 21, 41, 0.08)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      position: 'sticky',
      top: 0,
      zIndex: 1000,
      height: '64px',
    }}>
      {/* Left section with logo and sidebar toggle */}
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {showSidebarToggle && toggleSidebar && (
          <Button
            type="text"
            icon={collapsed ? <MenuOutlined /> : <MenuOutlined />}
            onClick={toggleSidebar}
            style={{ marginRight: '16px' }}
          />
        )}
        
        <Link to="/" style={{ display: 'flex', alignItems: 'center' }}>
          <img 
            src="/logo.png" 
            alt="Logo" 
            style={{ height: '32px', width: '32px', marginRight: '8px' }} 
          />
          <Typography.Title level={4} style={{ margin: 0 }}>
            AuthenHub
          </Typography.Title>
        </Link>
      </div>

      {/* Desktop Navigation */}
      <div className="hidden md:flex items-center">
        <Menu 
          mode="horizontal" 
          selectedKeys={[location.pathname]} 
          style={{ border: 'none', lineHeight: '62px' }}
        >
          <Menu.Item key="/">
            <Link to="/">Trang chủ</Link>
          </Menu.Item>
          {isAdmin && (
            <Menu.Item key="/admin">
              <Link to="/admin/overview">Quản trị</Link>
            </Menu.Item>
          )}
        </Menu>
      </div>

      {/* User section */}
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {/* Notifications */}
        <Badge count={3} size="small">
          <Button 
            type="text" 
            icon={<BellOutlined />} 
            style={{ marginRight: '16px' }}
            onClick={() => navigate('/notifications')}
          />
        </Badge>

        {/* User dropdown */}
        <Dropdown menu={{ items: userMenuItems }} trigger={['click']} placement="bottomRight">
          <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
            <Avatar 
              size="small" 
              src={user?.avatar} 
              icon={!user?.avatar && <UserOutlined />}
              style={{ backgroundColor: !user?.avatar ? '#1890ff' : undefined }}
            />
            <Text style={{ margin: '0 8px' }} ellipsis>
              {user?.fullName || 'User'}
            </Text>
          </div>
        </Dropdown>

        {/* Mobile menu button */}
        <Button
          type="text"
          icon={<MenuOutlined />}
          onClick={() => setMobileMenuVisible(!mobileMenuVisible)}
          className="md:hidden ml-2"
        />
      </div>

      {/* Mobile dropdown menu */}
      {mobileMenuVisible && (
        <div 
          className="md:hidden" 
          style={{ 
            position: 'absolute', 
            top: '64px', 
            left: 0, 
            right: 0, 
            background: '#fff',
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
            zIndex: 1000,
          }}
        >
          <Menu items={mobileMenuItems} mode="vertical" />
        </div>
      )}
    </AntHeader>
  );
};

export default Header;
