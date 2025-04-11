import React, { useState, useEffect } from 'react';
import { Layout, Menu, Avatar, Divider, Button, Tooltip, Badge } from 'antd';
import {
  DashboardOutlined,
  UserOutlined,
  AreaChartOutlined,
  SettingOutlined,
  HomeOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserAddOutlined,
  FileTextOutlined,
  TeamOutlined,
  KeyOutlined,
  MailOutlined,
  BarChartOutlined,
  LineChartOutlined,
  BellOutlined,
  ToolOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import type { MenuProps } from 'antd';

const { Sider } = Layout;

type MenuItem = Required<MenuProps>['items'][number];

interface SidebarProps {
  collapsed: boolean;
  onCollapse: (collapsed: boolean) => void;
  isMobile: boolean;
  onMobileClose?: () => void;
  theme?: 'dark' | 'light';
}

const Sidebar: React.FC<SidebarProps> = ({
  collapsed,
  onCollapse,
  isMobile,
  onMobileClose,
  theme = 'dark'
}) => {
  const { user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  // Track expanded submenus
  const [openKeys, setOpenKeys] = useState<string[]>([]);

  // Set initial open keys based on current path
  useEffect(() => {
    if (!collapsed) {
      const pathParts = location.pathname.split('/');
      if (pathParts.length > 2) {
        const mainPath = pathParts[2]; // e.g., 'users', 'analytics', 'settings'
        setOpenKeys([mainPath]);
      }
    }
  }, [location.pathname, collapsed]);

  // Handle submenu open/close
  const onOpenChange = (keys: string[]) => {
    setOpenKeys(keys);
  };

  // Reset open keys when sidebar collapses
  useEffect(() => {
    if (collapsed) {
      setOpenKeys([]);
    }
  }, [collapsed]);

  // Create menu items
  const getMenuItems = (): MenuItem[] => {
    const items: MenuItem[] = [
      {
        key: 'dashboard',
        icon: <DashboardOutlined />,
        label: 'Tổng quan',
        children: [
          {
            key: 'overview',
            label: <Link to="/admin/overview">Tổng quan hệ thống</Link>,
          },
          {
            key: 'statistics',
            label: <Link to="/admin/statistics">Thống kê</Link>,
          },
        ],
      },
      {
        key: 'users',
        icon: <UserOutlined />,
        label: 'Người dùng',
        children: [
          {
            key: 'userList',
            label: <Link to="/admin/users/list">Danh sách người dùng</Link>,
            icon: <TeamOutlined />,
          },
          {
            key: 'userRoles',
            label: <Link to="/admin/users/roles">Vai trò & Quyền hạn</Link>,
            icon: <KeyOutlined />,
          },
          {
            key: 'userInvites',
            label: <Link to="/admin/users/invites">Lời mời</Link>,
            icon: <MailOutlined />,
          },
          {
            key: 'newUser',
            label: <Link to="/admin/users/new">Thêm người dùng mới</Link>,
            icon: <UserAddOutlined />,
          },
        ],
      },
      {
        key: 'analytics',
        icon: <AreaChartOutlined />,
        label: 'Phân tích',
        children: [
          {
            key: 'traffic',
            label: <Link to="/admin/analytics/traffic">Lưu lượng truy cập</Link>,
            icon: <BarChartOutlined />,
          },
          {
            key: 'userActivity',
            label: <Link to="/admin/analytics/users">Hoạt động người dùng</Link>,
            icon: <LineChartOutlined />,
          },
          {
            key: 'reports',
            label: <Link to="/admin/analytics/reports">Báo cáo</Link>,
            icon: <FileTextOutlined />,
          },
        ],
      },
      {
        key: 'settings',
        icon: <SettingOutlined />,
        label: 'Cài đặt',
        children: [
          {
            key: 'general',
            label: <Link to="/admin/settings/general">Cài đặt chung</Link>,
            icon: <ToolOutlined />,
          },
          {
            key: 'security',
            label: <Link to="/admin/settings/security">Bảo mật</Link>,
            icon: <KeyOutlined />,
          },
          {
            key: 'appearance',
            label: <Link to="/admin/settings/appearance">Giao diện</Link>,
            icon: <EyeOutlined />,
          },
          {
            key: 'notifications',
            label: <Link to="/admin/settings/notifications">Thông báo</Link>,
            icon: <BellOutlined />,
          },
        ],
      },
    ];

    return items;
  };

  // Get selected keys based on current path
  const getSelectedKeys = (): string[] => {
    const pathParts = location.pathname.split('/');
    if (pathParts.length > 2) {
      const lastPart = pathParts[pathParts.length - 1];

      // Map path to menu key
      const keyMap: Record<string, string> = {
        'overview': 'overview',
        'statistics': 'statistics',
        'list': 'userList',
        'roles': 'userRoles',
        'invites': 'userInvites',
        'new': 'newUser',
        'traffic': 'traffic',
        'users': 'userActivity',
        'reports': 'reports',
        'general': 'general',
        'security': 'security',
        'appearance': 'appearance',
        'notifications': 'notifications',
      };

      const key = keyMap[lastPart];
      return key ? [key] : [];
    }

    return [];
  };

  // Handle click on menu item in mobile view
  const handleMenuItemClick = () => {
    if (isMobile && onMobileClose) {
      onMobileClose();
    }
  };

  return (
    <Sider
      trigger={null}
      collapsible
      collapsed={collapsed}
      breakpoint="lg"
      collapsedWidth={isMobile ? 0 : 80}
      width={250}
      theme={theme}
      className="site-layout-background sidebar-component"
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
        zIndex: 1000,
        transition: 'all 0.2s',
      }}
    >
      {/* Logo and title */}
      <div className="logo" style={{
        height: '64px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: collapsed ? 'center' : 'flex-start',
        padding: collapsed ? '0' : '0 16px',
        borderBottom: `1px solid ${theme === 'dark' ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.06)'}`,
      }}>
        <img
          src="/logo.png"
          alt="Logo"
          style={{ height: '32px', width: '32px' }}
        />
        {!collapsed && (
          <h1 style={{
            color: theme === 'dark' ? 'white' : 'rgba(0, 0, 0, 0.85)',
            margin: '0 0 0 12px',
            fontSize: '18px',
            fontWeight: 500,
          }}>
            AuthenHub
          </h1>
        )}
      </div>

      {/* Collapse toggle button */}
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        padding: '8px 0',
        borderBottom: `1px solid ${theme === 'dark' ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.06)'}`,
      }}>
        <Button
          type="text"
          icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          onClick={() => onCollapse(!collapsed)}
          style={{ color: theme === 'dark' ? 'rgba(255, 255, 255, 0.65)' : 'rgba(0, 0, 0, 0.65)' }}
        />
      </div>

      {/* Main menu */}
      <Menu
        theme={theme}
        mode="inline"
        selectedKeys={getSelectedKeys()}
        openKeys={openKeys}
        onOpenChange={onOpenChange}
        items={getMenuItems()}
        style={{ borderRight: 0 }}
        onClick={handleMenuItemClick}
      />

      {/* Quick actions - only visible when expanded */}
      {!collapsed && (
        <div style={{ padding: '16px' }}>
          <Divider style={{
            margin: '8px 0',
            borderColor: theme === 'dark' ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.06)'
          }} />
          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '8px',
            marginBottom: '16px',
          }}>
            <Button
              type="primary"
              icon={<UserAddOutlined />}
              size="small"
              onClick={() => {
                navigate('/admin/users/new');
                handleMenuItemClick();
              }}
              style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}
            >
              Thêm người dùng
            </Button>
            <Button
              type="primary"
              icon={<FileTextOutlined />}
              size="small"
              onClick={() => {
                navigate('/admin/analytics/reports/new');
                handleMenuItemClick();
              }}
              style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}
            >
              Tạo báo cáo
            </Button>
          </div>
        </div>
      )}

      {/* User profile */}
      <div style={{
        position: 'absolute',
        bottom: 0,
        width: '100%',
        padding: collapsed ? '16px 0' : '16px',
        borderTop: `1px solid ${theme === 'dark' ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.06)'}`,
        backgroundColor: theme === 'dark' ? 'rgba(0, 0, 0, 0.2)' : 'rgba(0, 0, 0, 0.02)',
      }}>
        <Link
          to="/"
          style={{ display: 'flex', alignItems: 'center' }}
          onClick={handleMenuItemClick}
        >
          <Badge dot={true} offset={[-4, 4]}>
            <Avatar
              size={collapsed ? 'default' : 'large'}
              src={user?.avatar}
              icon={!user?.avatar && <UserOutlined />}
              style={{
                backgroundColor: !user?.avatar ? '#1890ff' : undefined,
                margin: collapsed ? '0 auto' : undefined,
              }}
            />
          </Badge>

          {!collapsed && (
            <div style={{ marginLeft: '12px', overflow: 'hidden' }}>
              <div style={{
                color: theme === 'dark' ? 'white' : 'rgba(0, 0, 0, 0.85)',
                fontWeight: 500,
                whiteSpace: 'nowrap',
                overflow: 'hidden',
                textOverflow: 'ellipsis'
              }}>
                {user?.fullName}
              </div>
              <div style={{
                color: theme === 'dark' ? 'rgba(255, 255, 255, 0.65)' : 'rgba(0, 0, 0, 0.45)',
                fontSize: '12px',
                display: 'flex',
                alignItems: 'center',
              }}>
                <HomeOutlined style={{ marginRight: '4px' }} />
                Quay lại trang chủ
              </div>
            </div>
          )}
        </Link>

        {/* Tooltip for collapsed state */}
        {collapsed && (
          <Tooltip
            title="Quay lại trang chủ"
            placement="right"
            overlayStyle={{ zIndex: 1050 }}
          >
            <div style={{ width: '100%', height: '100%' }}></div>
          </Tooltip>
        )}
      </div>
    </Sider>
  );
};

export default Sidebar;
