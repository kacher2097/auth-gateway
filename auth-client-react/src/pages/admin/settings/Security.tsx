import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Form, 
  Input, 
  Button, 
  Switch, 
  Typography, 
  Divider, 
  message,
  Alert,
  InputNumber,
  Select,
  Space
} from 'antd';
import { 
  SaveOutlined,
  LockOutlined,
  SecurityScanOutlined,
  SafetyOutlined
} from '@ant-design/icons';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;
const { Option } = Select;

interface SecuritySettings {
  passwordPolicy: {
    minLength: number;
    requireUppercase: boolean;
    requireLowercase: boolean;
    requireNumbers: boolean;
    requireSpecialChars: boolean;
    passwordExpiryDays: number;
    preventReuseCount: number;
  };
  loginSecurity: {
    maxLoginAttempts: number;
    lockoutDurationMinutes: number;
    enableCaptcha: boolean;
    captchaThreshold: number;
    enableTwoFactor: boolean;
    twoFactorMethod: string;
  };
  sessionSecurity: {
    sessionTimeoutMinutes: number;
    enableRememberMe: boolean;
    rememberMeDays: number;
    enableSingleSession: boolean;
  };
}

const Security: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(true);
  const [saveLoading, setSaveLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      setLoading(true);
      const data = await adminService.getSettings('security');
      form.setFieldsValue(data);
    } catch (error) {
      console.error('Failed to fetch security settings:', error);
      setError('Failed to load security settings');
    } finally {
      setLoading(false);
    }
  };

  const onFinish = async (values: SecuritySettings) => {
    try {
      setSaveLoading(true);
      setError(null);
      setSuccess(false);
      
      await adminService.updateSettings('security', values);
      
      setSuccess(true);
      message.success('Security settings updated successfully');
    } catch (err: any) {
      console.error('Failed to update security settings:', err);
      setError(err.message || 'Failed to update security settings');
    } finally {
      setSaveLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <Title level={2}>Security Settings</Title>
      </div>

      <Card loading={loading}>
        {error && (
          <Alert
            message="Error"
            description={error}
            type="error"
            showIcon
            closable
            className="mb-6"
            onClose={() => setError(null)}
          />
        )}

        {success && (
          <Alert
            message="Success"
            description="Security settings have been updated successfully."
            type="success"
            showIcon
            closable
            className="mb-6"
            onClose={() => setSuccess(false)}
          />
        )}

        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          disabled={loading}
        >
          <Title level={4}>
            <LockOutlined /> Password Policy
          </Title>
          <Text type="secondary" className="block mb-4">
            Configure password requirements and expiration settings.
          </Text>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Form.Item
              name={['passwordPolicy', 'minLength']}
              label="Minimum Password Length"
              rules={[{ required: true, message: 'Please enter minimum length' }]}
            >
              <InputNumber min={6} max={32} style={{ width: '100%' }} />
            </Form.Item>

            <Form.Item
              name={['passwordPolicy', 'passwordExpiryDays']}
              label="Password Expiry (Days)"
              rules={[{ required: true, message: 'Please enter expiry days' }]}
              tooltip="Set to 0 for no expiration"
            >
              <InputNumber min={0} max={365} style={{ width: '100%' }} />
            </Form.Item>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Space direction="vertical" className="w-full">
              <Form.Item
                name={['passwordPolicy', 'requireUppercase']}
                valuePropName="checked"
                label="Require Uppercase Letters"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                name={['passwordPolicy', 'requireLowercase']}
                valuePropName="checked"
                label="Require Lowercase Letters"
              >
                <Switch />
              </Form.Item>
            </Space>

            <Space direction="vertical" className="w-full">
              <Form.Item
                name={['passwordPolicy', 'requireNumbers']}
                valuePropName="checked"
                label="Require Numbers"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                name={['passwordPolicy', 'requireSpecialChars']}
                valuePropName="checked"
                label="Require Special Characters"
              >
                <Switch />
              </Form.Item>
            </Space>
          </div>

          <Form.Item
            name={['passwordPolicy', 'preventReuseCount']}
            label="Prevent Password Reuse"
            tooltip="Number of previous passwords that cannot be reused"
          >
            <InputNumber min={0} max={24} style={{ width: '100%' }} />
          </Form.Item>

          <Divider />

          <Title level={4}>
            <SecurityScanOutlined /> Login Security
          </Title>
          <Text type="secondary" className="block mb-4">
            Configure login attempt limits and additional security measures.
          </Text>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Form.Item
              name={['loginSecurity', 'maxLoginAttempts']}
              label="Maximum Login Attempts"
              rules={[{ required: true, message: 'Please enter maximum attempts' }]}
            >
              <InputNumber min={1} max={10} style={{ width: '100%' }} />
            </Form.Item>

            <Form.Item
              name={['loginSecurity', 'lockoutDurationMinutes']}
              label="Account Lockout Duration (Minutes)"
              rules={[{ required: true, message: 'Please enter lockout duration' }]}
            >
              <InputNumber min={5} max={1440} style={{ width: '100%' }} />
            </Form.Item>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Space direction="vertical" className="w-full">
              <Form.Item
                name={['loginSecurity', 'enableCaptcha']}
                valuePropName="checked"
                label="Enable CAPTCHA"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                name={['loginSecurity', 'captchaThreshold']}
                label="CAPTCHA Threshold (Failed Attempts)"
                dependencies={['loginSecurity', 'enableCaptcha']}
              >
                <InputNumber 
                  min={1} 
                  max={5} 
                  style={{ width: '100%' }} 
                  disabled={!form.getFieldValue(['loginSecurity', 'enableCaptcha'])}
                />
              </Form.Item>
            </Space>

            <Space direction="vertical" className="w-full">
              <Form.Item
                name={['loginSecurity', 'enableTwoFactor']}
                valuePropName="checked"
                label="Enable Two-Factor Authentication"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                name={['loginSecurity', 'twoFactorMethod']}
                label="Two-Factor Method"
                dependencies={['loginSecurity', 'enableTwoFactor']}
              >
                <Select 
                  disabled={!form.getFieldValue(['loginSecurity', 'enableTwoFactor'])}
                >
                  <Option value="email">Email</Option>
                  <Option value="sms">SMS</Option>
                  <Option value="app">Authenticator App</Option>
                </Select>
              </Form.Item>
            </Space>
          </div>

          <Divider />

          <Title level={4}>
            <SafetyOutlined /> Session Security
          </Title>
          <Text type="secondary" className="block mb-4">
            Configure session timeout and remember me settings.
          </Text>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Form.Item
              name={['sessionSecurity', 'sessionTimeoutMinutes']}
              label="Session Timeout (Minutes)"
              rules={[{ required: true, message: 'Please enter session timeout' }]}
            >
              <InputNumber min={5} max={1440} style={{ width: '100%' }} />
            </Form.Item>

            <Space direction="vertical" className="w-full">
              <Form.Item
                name={['sessionSecurity', 'enableRememberMe']}
                valuePropName="checked"
                label="Enable Remember Me"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                name={['sessionSecurity', 'rememberMeDays']}
                label="Remember Me Duration (Days)"
                dependencies={['sessionSecurity', 'enableRememberMe']}
              >
                <InputNumber 
                  min={1} 
                  max={30} 
                  style={{ width: '100%' }} 
                  disabled={!form.getFieldValue(['sessionSecurity', 'enableRememberMe'])}
                />
              </Form.Item>
            </Space>
          </div>

          <Form.Item
            name={['sessionSecurity', 'enableSingleSession']}
            valuePropName="checked"
            label="Enforce Single Session Per User"
            tooltip="If enabled, users can only be logged in from one device at a time"
          >
            <Switch />
          </Form.Item>

          <Divider />

          <div className="flex justify-end">
            <Button 
              type="primary" 
              htmlType="submit" 
              icon={<SaveOutlined />}
              loading={saveLoading}
            >
              Save Settings
            </Button>
          </div>
        </Form>
      </Card>
    </div>
  );
};

export default Security;
