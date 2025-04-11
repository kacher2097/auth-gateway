import React, { useState, useEffect } from 'react';
import { Menu, Button, Divider } from 'antd';
import {
  DashboardOutlined,
  UserOutlined,
  AreaChartOutlined,
  SettingOutlined,
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
  MenuUnfoldOutlined,
  MenuFoldOutlined,
} from '@ant-design/icons';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import type { MenuProps } from 'antd';

type MenuItem = Required<MenuProps>['items'][number];

interface SidebarMenuProps {
  collapsed: boolean;
  onCollapse: (collapsed: boolean) => void;
}

const SidebarMenu: React.FC<SidebarMenuProps> = ({ collapsed, onCollapse }) => {
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
        label: 'Dashboard',
        children: [
          {
            key: 'overview',
            label: <Link to="/admin/overview">Overview</Link>,
          },
          {
            key: 'statistics',
            label: <Link to="/admin/statistics">Statistics</Link>,
          },
        ],
      },
      {
        key: 'users',
        icon: <UserOutlined />,
        label: 'Users',
        children: [
          {
            key: 'userList',
            label: <Link to="/admin/users/list">All Users</Link>,
            icon: <TeamOutlined />,
          },
          {
            key: 'userRoles',
            label: <Link to="/admin/users/roles">Roles & Permissions</Link>,
            icon: <KeyOutlined />,
          },
          {
            key: 'userInvites',
            label: <Link to="/admin/users/invites">Invitations</Link>,
            icon: <MailOutlined />,
          },
          {
            key: 'newUser',
            label: <Link to="/admin/users/new">New User</Link>,
            icon: <UserAddOutlined />,
          },
        ],
      },
      {
        key: 'analytics',
        icon: <AreaChartOutlined />,
        label: 'Analytics',
        children: [
          {
            key: 'traffic',
            label: <Link to="/admin/analytics/traffic">Traffic</Link>,
            icon: <BarChartOutlined />,
          },
          {
            key: 'userActivity',
            label: <Link to="/admin/analytics/users">User Activity</Link>,
            icon: <LineChartOutlined />,
          },
          {
            key: 'reports',
            label: <Link to="/admin/analytics/reports">Reports</Link>,
            icon: <FileTextOutlined />,
          },
        ],
      },
      {
        key: 'settings',
        icon: <SettingOutlined />,
        label: 'Settings',
        children: [
          {
            key: 'general',
            label: <Link to="/admin/settings/general">General</Link>,
            icon: <ToolOutlined />,
          },
          {
            key: 'security',
            label: <Link to="/admin/settings/security">Security</Link>,
            icon: <KeyOutlined />,
          },
          {
            key: 'appearance',
            label: <Link to="/admin/settings/appearance">Appearance</Link>,
            icon: <EyeOutlined />,
          },
          {
            key: 'notifications',
            label: <Link to="/admin/settings/notifications">Notifications</Link>,
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

  return (
    <>
      {/* Collapse toggle button */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        padding: '8px 0',
        borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
      }}>
        <Button
          type="text"
          icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          onClick={() => onCollapse(!collapsed)}
          style={{ color: 'rgba(255, 255, 255, 0.65)' }}
        />
      </div>
      
      {/* Main menu */}
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={getSelectedKeys()}
        openKeys={openKeys}
        onOpenChange={onOpenChange}
        items={getMenuItems()}
        style={{ borderRight: 0 }}
      />
      
      {/* Quick actions - only visible when expanded */}
      {!collapsed && (
        <div style={{ padding: '16px' }}>
          <Divider style={{ margin: '8px 0', borderColor: 'rgba(255, 255, 255, 0.1)' }} />
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
              onClick={() => navigate('/admin/users/new')}
              style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}
            >
              New User
            </Button>
            <Button
              type="primary"
              icon={<FileTextOutlined />}
              size="small"
              onClick={() => navigate('/admin/analytics/reports/new')}
              style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}
            >
              New Report
            </Button>
          </div>
        </div>
      )}
    </>
  );
};

export default SidebarMenu;
