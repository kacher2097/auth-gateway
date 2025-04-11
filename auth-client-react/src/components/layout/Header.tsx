import React from 'react';
import { Layout, Typography, Button } from 'antd';
import { MenuUnfoldOutlined, MenuFoldOutlined } from '@ant-design/icons';

const { Header: AntHeader } = Layout;
const { Title } = Typography;

interface HeaderProps {
  title?: string;
  isMobile: boolean;
  mobileOpen: boolean;
  handleMobileOpen: () => void;
  handleMobileClose: () => void;
  theme: any; // Using any for theme token to match the original implementation
}

const Header: React.FC<HeaderProps> = ({
  title,
  isMobile,
  mobileOpen,
  handleMobileOpen,
  handleMobileClose,
  theme,
}) => {
  return (
    <AntHeader style={{
      padding: '0 16px',
      background: theme.colorBgContainer,
      boxShadow: '0 1px 4px rgba(0, 21, 41, 0.08)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
    }}>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {isMobile && (
          <Button
            type="text"
            icon={mobileOpen ? <MenuFoldOutlined /> : <MenuUnfoldOutlined />}
            onClick={mobileOpen ? handleMobileClose : handleMobileOpen}
            style={{ marginRight: 16 }}
          />
        )}
        <Title level={4} style={{ margin: 0 }}>
          {title}
        </Title>
      </div>

      {/* Action buttons can be placed here */}
      <div>
        {/* Placeholder for header actions */}
      </div>
    </AntHeader>
  );
};

export default Header;
