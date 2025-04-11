import React from 'react';
import { Layout, Typography, Space, Divider } from 'antd';
import { GithubOutlined, TwitterOutlined, LinkedinOutlined } from '@ant-design/icons';

const { Footer: AntFooter } = Layout;
const { Text, Link: AntLink } = Typography;

const Footer: React.FC = () => {
  const currentYear = new Date().getFullYear();
  
  return (
    <AntFooter style={{ 
      textAlign: 'center',
      background: '#f0f2f5',
      padding: '24px'
    }}>
      <Divider style={{ margin: '0 0 24px 0' }} />
      
      <div style={{ marginBottom: '16px' }}>
        <Space split={<Divider type="vertical" />}>
          <AntLink href="/about">Giới thiệu</AntLink>
          <AntLink href="/privacy">Chính sách bảo mật</AntLink>
          <AntLink href="/terms">Điều khoản sử dụng</AntLink>
          <AntLink href="/contact">Liên hệ</AntLink>
        </Space>
      </div>
      
      <Space size="large" style={{ marginBottom: '16px' }}>
        <AntLink href="https://github.com" target="_blank" style={{ color: 'rgba(0, 0, 0, 0.65)' }}>
          <GithubOutlined style={{ fontSize: '24px' }} />
        </AntLink>
        <AntLink href="https://twitter.com" target="_blank" style={{ color: 'rgba(0, 0, 0, 0.65)' }}>
          <TwitterOutlined style={{ fontSize: '24px' }} />
        </AntLink>
        <AntLink href="https://linkedin.com" target="_blank" style={{ color: 'rgba(0, 0, 0, 0.65)' }}>
          <LinkedinOutlined style={{ fontSize: '24px' }} />
        </AntLink>
      </Space>
      
      <Text type="secondary">
        AuthenHub ©{currentYear} - Hệ thống xác thực an toàn và bảo mật
      </Text>
    </AntFooter>
  );
};

export default Footer;
