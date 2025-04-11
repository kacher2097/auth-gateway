import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Button, Switch, message, Breadcrumb, Typography, Divider } from 'antd';
import { SaveOutlined } from '@ant-design/icons';
import api from '../../../services/api.service';

const { Title } = Typography;

interface SystemSettings {
  siteName: string;
  siteUrl: string;
  adminEmail: string;
  defaultUserRole: 'USER' | 'ADMIN';
}

const General: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  
  // Load settings
  useEffect(() => {
    loadSettings();
  }, []);
  
  const loadSettings = async () => {
    try {
      setLoading(true);
      
      // In a real app, you would fetch these from your API
      // For demo purposes, we'll use localStorage or default values
      const savedSettings = localStorage.getItem('admin-system-settings');
      
      if (savedSettings) {
        const settings = JSON.parse(savedSettings);
        form.setFieldsValue(settings);
      } else {
        // Default values
        form.setFieldsValue({
          siteName: 'AuthenHub',
          siteUrl: 'http://localhost:5173',
          adminEmail: '',
          defaultUserRole: 'USER'
        });
      }
    } catch (error) {
      console.error('Error loading settings:', error);
      message.error('Failed to load settings');
    } finally {
      setLoading(false);
    }
  };
  
  const handleSave = async (values: SystemSettings) => {
    try {
      setLoading(true);
      
      // In a real app, you would send these to your API
      // For demo purposes, we'll save to localStorage
      localStorage.setItem('admin-system-settings', JSON.stringify(values));
      
      message.success('System settings saved successfully');
    } catch (error) {
      console.error('Error saving settings:', error);
      message.error('Failed to save settings');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div>
      <Breadcrumb
        items={[
          { title: 'Home' },
          { title: 'Admin' },
          { title: 'Settings' },
          { title: 'General' }
        ]}
        style={{ marginBottom: 16 }}
      />
      
      <Title level={4}>General Settings</Title>
      
      <Card style={{ marginTop: 16 }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSave}
          initialValues={{
            siteName: 'AuthenHub',
            siteUrl: 'http://localhost:5173',
            adminEmail: '',
            defaultUserRole: 'USER'
          }}
        >
          <Title level={5}>Site Information</Title>
          <Divider />
          
          <Form.Item
            name="siteName"
            label="Site Name"
            rules={[{ required: true, message: 'Please enter site name' }]}
          >
            <Input placeholder="Enter site name" />
          </Form.Item>
          
          <Form.Item
            name="siteUrl"
            label="Site URL"
            rules={[
              { required: true, message: 'Please enter site URL' },
              { type: 'url', message: 'Please enter a valid URL' }
            ]}
          >
            <Input placeholder="Enter site URL" />
          </Form.Item>
          
          <Form.Item
            name="adminEmail"
            label="Admin Email"
            rules={[
              { required: true, message: 'Please enter admin email' },
              { type: 'email', message: 'Please enter a valid email' }
            ]}
          >
            <Input placeholder="Enter admin email" />
          </Form.Item>
          
          <Title level={5} style={{ marginTop: 24 }}>User Settings</Title>
          <Divider />
          
          <Form.Item
            name="defaultUserRole"
            label="Default User Role"
            rules={[{ required: true, message: 'Please select default user role' }]}
          >
            <Input.Group>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Switch
                  checked={form.getFieldValue('defaultUserRole') === 'ADMIN'}
                  onChange={(checked) => form.setFieldsValue({ defaultUserRole: checked ? 'ADMIN' : 'USER' })}
                  style={{ marginRight: 8 }}
                />
                <span>{form.getFieldValue('defaultUserRole') === 'ADMIN' ? 'Admin' : 'User'}</span>
              </div>
            </Input.Group>
          </Form.Item>
          
          <Form.Item style={{ marginTop: 24 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              icon={<SaveOutlined />}
            >
              Save Settings
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default General;
