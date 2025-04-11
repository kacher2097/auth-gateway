import React from 'react';
import { Layout } from 'antd';
import Topbar from './Topbar';

const { Content, Footer } = Layout;

interface MainLayoutProps {
  children: React.ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Topbar />
      <Content style={{ padding: '0 24px', marginTop: '16px' }}>
        {children}
      </Content>
      <Footer style={{ textAlign: 'center', padding: '16px' }}>
        AuthenHub ©{new Date().getFullYear()} - Secure Authentication System
      </Footer>
    </Layout>
  );
};

export default MainLayout;
