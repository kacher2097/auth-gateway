import React from 'react';
import { Layout } from 'antd';

const { Footer: AntFooter } = Layout;

interface FooterProps {
  // Add any props needed for the footer
}

const Footer: React.FC<FooterProps> = () => {
  return (
    <AntFooter style={{ 
      textAlign: 'center',
      padding: '12px 50px',
      backgroundColor: 'transparent',
    }}>
      {/* Footer content - currently empty but available for future use */}
    </AntFooter>
  );
};

export default Footer;
