import React from 'react';
import { Alert, Button, Result, Space } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';

interface ErrorDisplayProps {
  error: string | null;
  onClear?: () => void;
  onRetry?: () => void;
  type?: 'alert' | 'result';
  showRetry?: boolean;
}

/**
 * A reusable component for displaying errors in the application
 */
const ErrorDisplay: React.FC<ErrorDisplayProps> = ({
  error,
  onClear,
  onRetry,
  type = 'alert',
  showRetry = true
}) => {
  if (!error) return null;

  if (type === 'result') {
    return (
      <Result
        status="error"
        title="Error"
        subTitle={error}
        extra={
          <Space>
            {showRetry && onRetry && (
              <Button type="primary" icon={<ReloadOutlined />} onClick={onRetry}>
                Try Again
              </Button>
            )}
            {onClear && (
              <Button onClick={onClear}>
                Dismiss
              </Button>
            )}
          </Space>
        }
      />
    );
  }

  return (
    <Alert
      message="Error"
      description={
        <div>
          <div>{error}</div>
          {showRetry && onRetry && (
            <Button 
              type="link" 
              icon={<ReloadOutlined />} 
              onClick={onRetry}
              style={{ padding: '4px 0', marginTop: 8 }}
            >
              Try Again
            </Button>
          )}
        </div>
      }
      type="error"
      showIcon
      closable={!!onClear}
      onClose={onClear}
      style={{ marginBottom: 16 }}
    />
  );
};

export default ErrorDisplay;
