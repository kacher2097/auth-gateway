import React from 'react';

interface SidebarHeaderProps {
  collapsed: boolean;
}

const SidebarHeader: React.FC<SidebarHeaderProps> = ({ collapsed }) => {
  return (
    <div className="logo" style={{ 
      height: '64px', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: collapsed ? 'center' : 'flex-start',
      padding: collapsed ? '0' : '0 16px',
      borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
    }}>
      <img 
        src="/logo.png" 
        alt="Logo" 
        style={{ height: '32px', width: '32px' }} 
      />
      {!collapsed && (
        <h1 style={{ 
          color: 'white', 
          margin: '0 0 0 12px', 
          fontSize: '18px',
          fontWeight: 500,
        }}>
          AuthenHub
        </h1>
      )}
    </div>
  );
};

export default SidebarHeader;
