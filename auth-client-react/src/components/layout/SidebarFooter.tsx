import React from 'react';
import { Avatar, Tooltip } from 'antd';
import { UserOutlined, HomeOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';

interface SidebarFooterProps {
  collapsed: boolean;
}

const SidebarFooter: React.FC<SidebarFooterProps> = ({ collapsed }) => {
  const { user } = useAuth();

  return (
    <div style={{ 
      position: 'absolute', 
      bottom: 0, 
      width: '100%',
      padding: collapsed ? '16px 0' : '16px',
      borderTop: '1px solid rgba(255, 255, 255, 0.1)',
      backgroundColor: 'rgba(0, 0, 0, 0.2)',
    }}>
      <Link to="/" style={{ display: 'flex', alignItems: 'center' }}>
        <Avatar 
          size={collapsed ? 'default' : 'large'}
          src={user?.avatar}
          icon={!user?.avatar && <UserOutlined />}
          style={{ 
            backgroundColor: !user?.avatar ? '#1890ff' : undefined,
            margin: collapsed ? '0 auto' : undefined,
          }}
        />
        
        {!collapsed && (
          <div style={{ marginLeft: '12px', overflow: 'hidden' }}>
            <div style={{ color: 'white', fontWeight: 500, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
              {user?.fullName}
            </div>
            <div style={{ 
              color: 'rgba(255, 255, 255, 0.65)', 
              fontSize: '12px',
              display: 'flex',
              alignItems: 'center',
            }}>
              <HomeOutlined style={{ marginRight: '4px' }} />
              Back to site
            </div>
          </div>
        )}
      </Link>
      
      {/* Tooltip for collapsed state */}
      {collapsed && (
        <Tooltip 
          title="Back to site" 
          placement="right"
          overlayStyle={{ zIndex: 1050 }}
        >
          <div style={{ width: '100%', height: '100%' }}></div>
        </Tooltip>
      )}
    </div>
  );
};

export default SidebarFooter;
