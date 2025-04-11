import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Form, 
  Input, 
  Button, 
  Typography, 
  Divider, 
  message,
  Alert,
  Upload,
  Select,
  Switch,
  ColorPicker,
  Space
} from 'antd';
import { 
  SaveOutlined,
  UploadOutlined,
  BgColorsOutlined,
  FontSizeOutlined,
  PictureOutlined
} from '@ant-design/icons';
import type { UploadFile } from 'antd/es/upload/interface';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;

interface AppearanceSettings {
  branding: {
    siteName: string;
    logoUrl: string;
    faviconUrl: string;
    primaryColor: string;
    secondaryColor: string;
    loginBackgroundUrl: string;
  };
  layout: {
    theme: 'light' | 'dark' | 'auto';
    sidebarCollapsed: boolean;
    compactMode: boolean;
    showBreadcrumbs: boolean;
    defaultRoute: string;
  };
  customization: {
    customCss: string;
    customJs: string;
    footerText: string;
    enableCustomization: boolean;
  };
}

const Appearance: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(true);
  const [saveLoading, setSaveLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  const [logoFile, setLogoFile] = useState<UploadFile | null>(null);
  const [faviconFile, setFaviconFile] = useState<UploadFile | null>(null);
  const [backgroundFile, setBackgroundFile] = useState<UploadFile | null>(null);

  useEffect(() => {
    fetchSettings();
  }, []);

  const fetchSettings = async () => {
    try {
      setLoading(true);
      const data = await adminService.getSettings('appearance');
      form.setFieldsValue(data);
      
      // Set file lists for uploads if URLs exist
      if (data.branding.logoUrl) {
        setLogoFile({
          uid: '-1',
          name: 'logo.png',
          status: 'done',
          url: data.branding.logoUrl,
        });
      }
      
      if (data.branding.faviconUrl) {
        setFaviconFile({
          uid: '-1',
          name: 'favicon.ico',
          status: 'done',
          url: data.branding.faviconUrl,
        });
      }
      
      if (data.branding.loginBackgroundUrl) {
        setBackgroundFile({
          uid: '-1',
          name: 'background.jpg',
          status: 'done',
          url: data.branding.loginBackgroundUrl,
        });
      }
    } catch (error) {
      console.error('Failed to fetch appearance settings:', error);
      setError('Failed to load appearance settings');
    } finally {
      setLoading(false);
    }
  };

  const onFinish = async (values: AppearanceSettings) => {
    try {
      setSaveLoading(true);
      setError(null);
      setSuccess(false);
      
      // In a real application, you would handle file uploads here
      // and update the URLs in the values object
      
      await adminService.updateSettings('appearance', values);
      
      setSuccess(true);
      message.success('Appearance settings updated successfully');
    } catch (err: any) {
      console.error('Failed to update appearance settings:', err);
      setError(err.message || 'Failed to update appearance settings');
    } finally {
      setSaveLoading(false);
    }
  };

  const normFile = (e: any) => {
    if (Array.isArray(e)) {
      return e;
    }
    return e?.fileList;
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <Title level={2}>Appearance Settings</Title>
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
            description="Appearance settings have been updated successfully."
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
            <BgColorsOutlined /> Branding
          </Title>
          <Text type="secondary" className="block mb-4">
            Configure your application's branding elements.
          </Text>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Form.Item
              name={['branding', 'siteName']}
              label="Site Name"
              rules={[{ required: true, message: 'Please enter site name' }]}
            >
              <Input placeholder="AuthenHub" />
            </Form.Item>

            <div className="grid grid-cols-2 gap-4">
              <Form.Item
                name={['branding', 'primaryColor']}
                label="Primary Color"
                rules={[{ required: true, message: 'Please select primary color' }]}
              >
                <Input type="color" style={{ width: '100%', height: '32px' }} />
              </Form.Item>

              <Form.Item
                name={['branding', 'secondaryColor']}
                label="Secondary Color"
                rules={[{ required: true, message: 'Please select secondary color' }]}
              >
                <Input type="color" style={{ width: '100%', height: '32px' }} />
              </Form.Item>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Form.Item
              name={['branding', 'logoUrl']}
              label="Logo"
              valuePropName="fileList"
              getValueFromEvent={normFile}
              tooltip="Recommended size: 200x50px"
            >
              <Upload
                name="logo"
                listType="picture"
                maxCount={1}
                beforeUpload={() => false}
                fileList={logoFile ? [logoFile] : []}
                onChange={({ fileList }) => setLogoFile(fileList[0])}
              >
                <Button icon={<UploadOutlined />}>Upload Logo</Button>
              </Upload>
            </Form.Item>

            <Form.Item
              name={['branding', 'faviconUrl']}
              label="Favicon"
              valuePropName="fileList"
              getValueFromEvent={normFile}
              tooltip="Recommended size: 32x32px"
            >
              <Upload
                name="favicon"
                listType="picture"
                maxCount={1}
                beforeUpload={() => false}
                fileList={faviconFile ? [faviconFile] : []}
                onChange={({ fileList }) => setFaviconFile(fileList[0])}
              >
                <Button icon={<UploadOutlined />}>Upload Favicon</Button>
              </Upload>
            </Form.Item>

            <Form.Item
              name={['branding', 'loginBackgroundUrl']}
              label="Login Background"
              valuePropName="fileList"
              getValueFromEvent={normFile}
              tooltip="Recommended size: 1920x1080px"
            >
              <Upload
                name="background"
                listType="picture"
                maxCount={1}
                beforeUpload={() => false}
                fileList={backgroundFile ? [backgroundFile] : []}
                onChange={({ fileList }) => setBackgroundFile(fileList[0])}
              >
                <Button icon={<UploadOutlined />}>Upload Background</Button>
              </Upload>
            </Form.Item>
          </div>

          <Divider />

          <Title level={4}>
            <FontSizeOutlined /> Layout
          </Title>
          <Text type="secondary" className="block mb-4">
            Configure the application layout and theme.
          </Text>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Form.Item
              name={['layout', 'theme']}
              label="Theme"
              rules={[{ required: true, message: 'Please select theme' }]}
            >
              <Select>
                <Option value="light">Light</Option>
                <Option value="dark">Dark</Option>
                <Option value="auto">Auto (follow system)</Option>
              </Select>
            </Form.Item>

            <Form.Item
              name={['layout', 'defaultRoute']}
              label="Default Route After Login"
              rules={[{ required: true, message: 'Please enter default route' }]}
            >
              <Input placeholder="/dashboard" />
            </Form.Item>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Form.Item
              name={['layout', 'sidebarCollapsed']}
              valuePropName="checked"
              label="Default Sidebar Collapsed"
            >
              <Switch />
            </Form.Item>

            <Form.Item
              name={['layout', 'compactMode']}
              valuePropName="checked"
              label="Compact Mode"
            >
              <Switch />
            </Form.Item>

            <Form.Item
              name={['layout', 'showBreadcrumbs']}
              valuePropName="checked"
              label="Show Breadcrumbs"
            >
              <Switch />
            </Form.Item>
          </div>

          <Divider />

          <Title level={4}>
            <PictureOutlined /> Advanced Customization
          </Title>
          <Text type="secondary" className="block mb-4">
            Add custom CSS and JavaScript to further customize the application.
          </Text>

          <Form.Item
            name={['customization', 'enableCustomization']}
            valuePropName="checked"
            label="Enable Advanced Customization"
          >
            <Switch />
          </Form.Item>

          <Form.Item
            name={['customization', 'footerText']}
            label="Footer Text"
          >
            <Input placeholder="© 2023 AuthenHub. All rights reserved." />
          </Form.Item>

          <Form.Item
            name={['customization', 'customCss']}
            label="Custom CSS"
            tooltip="Add custom CSS styles"
            dependencies={['customization', 'enableCustomization']}
          >
            <TextArea 
              rows={4} 
              placeholder=".custom-class { color: #ff0000; }"
              disabled={!form.getFieldValue(['customization', 'enableCustomization'])}
            />
          </Form.Item>

          <Form.Item
            name={['customization', 'customJs']}
            label="Custom JavaScript"
            tooltip="Add custom JavaScript code"
            dependencies={['customization', 'enableCustomization']}
          >
            <TextArea 
              rows={4} 
              placeholder="console.log('Custom JS loaded');"
              disabled={!form.getFieldValue(['customization', 'enableCustomization'])}
            />
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

export default Appearance;
