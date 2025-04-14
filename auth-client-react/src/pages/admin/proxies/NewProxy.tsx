import React, { useState } from 'react';
import { 
  Card, Form, Input, Button, Select, InputNumber, 
  Typography, Breadcrumb, notification, Space, Divider, Alert
} from 'antd';
import { 
  SaveOutlined, ArrowLeftOutlined, GlobalOutlined, 
  ApiOutlined, InfoCircleOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import proxyService from '../../../services/proxy.service';
import { ProxyRequest, PROXY_PROTOCOLS } from '../../../types/proxy.types';

const { Title, Text, Paragraph } = Typography;

const NewProxy: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (values: ProxyRequest) => {
    try {
      setLoading(true);
      await proxyService.createProxy(values);
      notification.success({
        message: 'Success',
        description: 'Proxy added successfully',
      });
      navigate('/admin/proxies');
    } catch (error) {
      notification.error({
        message: 'Error',
        description: 'Failed to add proxy',
      });
      console.error('Error adding proxy:', error);
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
          { title: <a onClick={() => navigate('/admin/proxies')}>Proxies</a> },
          { title: 'Add New Proxy' }
        ]}
        style={{ marginBottom: 16 }}
      />
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={4}>Add New Proxy</Title>
        <Button 
          icon={<ArrowLeftOutlined />} 
          onClick={() => navigate('/admin/proxies')}
        >
          Back to List
        </Button>
      </div>
      
      <Card>
        <Alert
          message="Proxy Verification"
          description="The system will automatically check if the proxy is working before adding it to the database."
          type="info"
          showIcon
          style={{ marginBottom: 24 }}
        />
        
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            protocol: 'HTTP'
          }}
        >
          <Form.Item
            name="ipAddress"
            label="IP Address"
            rules={[
              { required: true, message: 'Please enter IP address' },
              { 
                pattern: /^(\d{1,3}\.){3}\d{1,3}$/, 
                message: 'Please enter a valid IP address' 
              }
            ]}
          >
            <Input 
              prefix={<GlobalOutlined />} 
              placeholder="e.g. 192.168.1.1" 
            />
          </Form.Item>
          
          <Form.Item
            name="port"
            label="Port"
            rules={[
              { required: true, message: 'Please enter port' }
            ]}
          >
            <InputNumber 
              min={1} 
              max={65535} 
              style={{ width: '100%' }} 
              placeholder="e.g. 8080"
            />
          </Form.Item>
          
          <Form.Item
            name="protocol"
            label="Protocol"
            rules={[{ required: true, message: 'Please select protocol' }]}
          >
            <Select>
              {PROXY_PROTOCOLS.map(protocol => (
                <Select.Option key={protocol} value={protocol}>{protocol}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          
          <Form.Item
            name="country"
            label="Country (Optional)"
          >
            <Input placeholder="e.g. United States" />
          </Form.Item>
          
          <Form.Item
            name="city"
            label="City (Optional)"
          >
            <Input placeholder="e.g. New York" />
          </Form.Item>
          
          <Form.Item
            name="notes"
            label="Notes (Optional)"
          >
            <Input.TextArea 
              rows={3} 
              placeholder="Add any additional information about this proxy"
            />
          </Form.Item>
          
          <Divider />
          
          <Form.Item>
            <Space>
              <Button 
                type="primary" 
                htmlType="submit" 
                icon={<SaveOutlined />}
                loading={loading}
              >
                Add Proxy
              </Button>
              <Button 
                onClick={() => form.resetFields()}
              >
                Reset
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
      
      <Card style={{ marginTop: 24 }}>
        <Title level={5}>
          <InfoCircleOutlined /> About Free Proxies
        </Title>
        <Paragraph>
          Free proxies are public proxy servers that can be used to route your internet traffic through a different IP address.
          They can be useful for testing, web scraping, or accessing geo-restricted content.
        </Paragraph>
        <Paragraph>
          <Text strong>Note:</Text> Free proxies may have limitations such as:
        </Paragraph>
        <ul>
          <li>Slower connection speeds</li>
          <li>Intermittent availability</li>
          <li>Limited bandwidth</li>
          <li>Potential security risks</li>
        </ul>
        <Paragraph>
          Always verify that a proxy is working before using it for important tasks.
        </Paragraph>
      </Card>
    </div>
  );
};

export default NewProxy;
