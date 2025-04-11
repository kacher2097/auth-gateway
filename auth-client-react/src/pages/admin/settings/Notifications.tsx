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
  Select,
  Tabs,
  Space
} from 'antd';
import { 
  SaveOutlined,
  MailOutlined,
  BellOutlined,
  MessageOutlined,
  MobileOutlined
} from '@ant-design/icons';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;
const { TabPane } = Tabs;

interface NotificationSettings {
  email: {
    enabled: boolean;
    smtpHost: string;
    smtpPort: number;
    smtpUsername: string;
    smtpPassword: string;
    smtpSecure: boolean;
    fromEmail: string;
    fromName: string;
  };
  sms: {
    enabled: boolean;
    provider: string;
    apiKey: string;
    fromNumber: string;
  };
  push: {
    enabled: boolean;
    firebaseServerKey: string;
    vapidPublicKey: string;
    vapidPrivateKey: string;
  };
  events: {
    userRegistration: {
      email: boolean;
      sms: boolean;
      push: boolean;
    };
    passwordReset: {
      email: boolean;
      sms: boolean;
      push: boolean;
    };
    loginAttempt: {
      email: boolean;
      sms: boolean;
      push: boolean;
    };
    accountLocked: {
      email: boolean;
      sms: boolean;
      push: boolean;
    };
  };
}

const Notifications: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(true);
  const [saveLoading, setSaveLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<string>('email');

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      setLoading(true);
      const data = await adminService.getSettings('notifications');
      form.setFieldsValue(data);
    } catch (error) {
      console.error('Failed to fetch notification settings:', error);
      setError('Failed to load notification settings');
    } finally {
      setLoading(false);
    }
  };

  const onFinish = async (values: NotificationSettings) => {
    try {
      setSaveLoading(true);
      setError(null);
      setSuccess(false);
      
      await adminService.updateSettings('notifications', values);
      
      setSuccess(true);
      message.success('Notification settings updated successfully');
    } catch (err: any) {
      console.error('Failed to update notification settings:', err);
      setError(err.message || 'Failed to update notification settings');
    } finally {
      setSaveLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <Title level={2}>Notification Settings</Title>
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
            description="Notification settings have been updated successfully."
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
          <Tabs 
            activeKey={activeTab} 
            onChange={setActiveTab}
            type="card"
          >
            <TabPane 
              tab={
                <span>
                  <MailOutlined /> Email
                </span>
              } 
              key="email"
            >
              <Form.Item
                name={['email', 'enabled']}
                valuePropName="checked"
                label="Enable Email Notifications"
              >
                <Switch />
              </Form.Item>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Form.Item
                  name={['email', 'smtpHost']}
                  label="SMTP Host"
                  rules={[{ required: true, message: 'Please enter SMTP host' }]}
                  dependencies={['email', 'enabled']}
                >
                  <Input 
                    placeholder="smtp.example.com" 
                    disabled={!form.getFieldValue(['email', 'enabled'])}
                  />
                </Form.Item>

                <Form.Item
                  name={['email', 'smtpPort']}
                  label="SMTP Port"
                  rules={[{ required: true, message: 'Please enter SMTP port' }]}
                  dependencies={['email', 'enabled']}
                >
                  <Input 
                    placeholder="587" 
                    type="number" 
                    disabled={!form.getFieldValue(['email', 'enabled'])}
                  />
                </Form.Item>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Form.Item
                  name={['email', 'smtpUsername']}
                  label="SMTP Username"
                  rules={[{ required: true, message: 'Please enter SMTP username' }]}
                  dependencies={['email', 'enabled']}
                >
                  <Input 
                    placeholder="username" 
                    disabled={!form.getFieldValue(['email', 'enabled'])}
                  />
                </Form.Item>

                <Form.Item
                  name={['email', 'smtpPassword']}
                  label="SMTP Password"
                  rules={[{ required: true, message: 'Please enter SMTP password' }]}
                  dependencies={['email', 'enabled']}
                >
                  <Input.Password 
                    placeholder="password" 
                    disabled={!form.getFieldValue(['email', 'enabled'])}
                  />
                </Form.Item>
              </div>

              <Form.Item
                name={['email', 'smtpSecure']}
                valuePropName="checked"
                label="Use Secure Connection (TLS/SSL)"
                dependencies={['email', 'enabled']}
              >
                <Switch disabled={!form.getFieldValue(['email', 'enabled'])} />
              </Form.Item>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Form.Item
                  name={['email', 'fromEmail']}
                  label="From Email"
                  rules={[
                    { required: true, message: 'Please enter from email' },
                    { type: 'email', message: 'Please enter a valid email' }
                  ]}
                  dependencies={['email', 'enabled']}
                >
                  <Input 
                    placeholder="noreply@example.com" 
                    disabled={!form.getFieldValue(['email', 'enabled'])}
                  />
                </Form.Item>

                <Form.Item
                  name={['email', 'fromName']}
                  label="From Name"
                  rules={[{ required: true, message: 'Please enter from name' }]}
                  dependencies={['email', 'enabled']}
                >
                  <Input 
                    placeholder="AuthenHub" 
                    disabled={!form.getFieldValue(['email', 'enabled'])}
                  />
                </Form.Item>
              </div>
            </TabPane>

            <TabPane 
              tab={
                <span>
                  <MobileOutlined /> SMS
                </span>
              } 
              key="sms"
            >
              <Form.Item
                name={['sms', 'enabled']}
                valuePropName="checked"
                label="Enable SMS Notifications"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                name={['sms', 'provider']}
                label="SMS Provider"
                rules={[{ required: true, message: 'Please select SMS provider' }]}
                dependencies={['sms', 'enabled']}
              >
                <Select disabled={!form.getFieldValue(['sms', 'enabled'])}>
                  <Option value="twilio">Twilio</Option>
                  <Option value="aws_sns">AWS SNS</Option>
                  <Option value="nexmo">Nexmo (Vonage)</Option>
                </Select>
              </Form.Item>

              <Form.Item
                name={['sms', 'apiKey']}
                label="API Key / Access Key"
                rules={[{ required: true, message: 'Please enter API key' }]}
                dependencies={['sms', 'enabled']}
              >
                <Input.Password 
                  placeholder="Enter API key" 
                  disabled={!form.getFieldValue(['sms', 'enabled'])}
                />
              </Form.Item>

              <Form.Item
                name={['sms', 'fromNumber']}
                label="From Phone Number"
                rules={[{ required: true, message: 'Please enter from phone number' }]}
                dependencies={['sms', 'enabled']}
              >
                <Input 
                  placeholder="+1234567890" 
                  disabled={!form.getFieldValue(['sms', 'enabled'])}
                />
              </Form.Item>
            </TabPane>

            <TabPane 
              tab={
                <span>
                  <BellOutlined /> Push
                </span>
              } 
              key="push"
            >
              <Form.Item
                name={['push', 'enabled']}
                valuePropName="checked"
                label="Enable Push Notifications"
              >
                <Switch />
              </Form.Item>

              <Form.Item
                name={['push', 'firebaseServerKey']}
                label="Firebase Server Key"
                rules={[{ required: true, message: 'Please enter Firebase server key' }]}
                dependencies={['push', 'enabled']}
              >
                <Input.Password 
                  placeholder="Enter Firebase server key" 
                  disabled={!form.getFieldValue(['push', 'enabled'])}
                />
              </Form.Item>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Form.Item
                  name={['push', 'vapidPublicKey']}
                  label="VAPID Public Key"
                  rules={[{ required: true, message: 'Please enter VAPID public key' }]}
                  dependencies={['push', 'enabled']}
                >
                  <Input 
                    placeholder="Enter VAPID public key" 
                    disabled={!form.getFieldValue(['push', 'enabled'])}
                  />
                </Form.Item>

                <Form.Item
                  name={['push', 'vapidPrivateKey']}
                  label="VAPID Private Key"
                  rules={[{ required: true, message: 'Please enter VAPID private key' }]}
                  dependencies={['push', 'enabled']}
                >
                  <Input.Password 
                    placeholder="Enter VAPID private key" 
                    disabled={!form.getFieldValue(['push', 'enabled'])}
                  />
                </Form.Item>
              </div>
            </TabPane>

            <TabPane 
              tab={
                <span>
                  <MessageOutlined /> Events
                </span>
              } 
              key="events"
            >
              <Title level={5}>User Registration</Title>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                <Form.Item
                  name={['events', 'userRegistration', 'email']}
                  valuePropName="checked"
                  label="Email"
                >
                  <Switch disabled={!form.getFieldValue(['email', 'enabled'])} />
                </Form.Item>

                <Form.Item
                  name={['events', 'userRegistration', 'sms']}
                  valuePropName="checked"
                  label="SMS"
                >
                  <Switch disabled={!form.getFieldValue(['sms', 'enabled'])} />
                </Form.Item>

                <Form.Item
                  name={['events', 'userRegistration', 'push']}
                  valuePropName="checked"
                  label="Push"
                >
                  <Switch disabled={!form.getFieldValue(['push', 'enabled'])} />
                </Form.Item>
              </div>

              <Title level={5}>Password Reset</Title>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                <Form.Item
                  name={['events', 'passwordReset', 'email']}
                  valuePropName="checked"
                  label="Email"
                >
                  <Switch disabled={!form.getFieldValue(['email', 'enabled'])} />
                </Form.Item>

                <Form.Item
                  name={['events', 'passwordReset', 'sms']}
                  valuePropName="checked"
                  label="SMS"
                >
                  <Switch disabled={!form.getFieldValue(['sms', 'enabled'])} />
                </Form.Item>

                <Form.Item
                  name={['events', 'passwordReset', 'push']}
                  valuePropName="checked"
                  label="Push"
                >
                  <Switch disabled={!form.getFieldValue(['push', 'enabled'])} />
                </Form.Item>
              </div>

              <Title level={5}>Failed Login Attempt</Title>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
                <Form.Item
                  name={['events', 'loginAttempt', 'email']}
                  valuePropName="checked"
                  label="Email"
                >
                  <Switch disabled={!form.getFieldValue(['email', 'enabled'])} />
                </Form.Item>

                <Form.Item
                  name={['events', 'loginAttempt', 'sms']}
                  valuePropName="checked"
                  label="SMS"
                >
                  <Switch disabled={!form.getFieldValue(['sms', 'enabled'])} />
                </Form.Item>

                <Form.Item
                  name={['events', 'loginAttempt', 'push']}
                  valuePropName="checked"
                  label="Push"
                >
                  <Switch disabled={!form.getFieldValue(['push', 'enabled'])} />
                </Form.Item>
              </div>

              <Title level={5}>Account Locked</Title>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <Form.Item
                  name={['events', 'accountLocked', 'email']}
                  valuePropName="checked"
                  label="Email"
                >
                  <Switch disabled={!form.getFieldValue(['email', 'enabled'])} />
                </Form.Item>

                <Form.Item
                  name={['events', 'accountLocked', 'sms']}
                  valuePropName="checked"
                  label="SMS"
                >
                  <Switch disabled={!form.getFieldValue(['sms', 'enabled'])} />
                </Form.Item>

                <Form.Item
                  name={['events', 'accountLocked', 'push']}
                  valuePropName="checked"
                  label="Push"
                >
                  <Switch disabled={!form.getFieldValue(['push', 'enabled'])} />
                </Form.Item>
              </div>
            </TabPane>
          </Tabs>

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

export default Notifications;
