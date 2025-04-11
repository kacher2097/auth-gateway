import React from 'react';
import { ConfigProvider } from 'antd';
import AppRoutes from './routes';

// Configure Ant Design theme
const theme = {
  token: {
    colorPrimary: '#1890ff',
    borderRadius: 4,
  },
};

const App: React.FC = () => {
  return (
    <ConfigProvider theme={theme}>
      <AppRoutes />
    </ConfigProvider>
  );
};

export default App;
