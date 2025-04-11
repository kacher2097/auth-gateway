import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Form, 
  Input, 
  Button, 
  Select, 
  Switch, 
  Typography, 
  Divider, 
  message,
  Alert
} from 'antd';
import { 
  UserOutlined, 
  MailOutlined, 
  LockOutlined, 
  SaveOutlined,
  UserAddOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import adminService from '../../../services/admin.service';

const { Title, Text } = Typography;
const { Option } = Select;

interface RoleData {
  id: string;
  name: string;
  description: string;
}

const NewUser: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState<boolean>(false);
  const [roles, setRoles] = useState<RoleData[]>([]);
  const [rolesLoading, setRolesLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  // Fetch roles on component mount
  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      setRolesLoading(true);
      const data = await adminService.getRoles();
      setRoles(data);
    } catch (error) {
      console.error('Failed to fetch roles:', error);
      message.error('Failed to load roles');
    } finally {
      setRolesLoading(false);
    }
  };

  const onFinish = async (values: any) => {
    try {
      setLoading(true);
      setError(null);
      
      await adminService.createUser(values);
      message.success('User created successfully');
      
      // Navigate to user list
      navigate('/admin/users/list');
    } catch (err: any) {
      console.error('Failed to create user:', err);
      setError(err.message || 'Failed to create user');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <Title level={2}>Create New User</Title>
        <Button 
          type="primary" 
          onClick={() => navigate('/admin/users/list')}
        >
          Back to Users
        </Button>
      </div>

      <Card>
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

        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          initialValues={{
            active: true,
            role: 'USER',
          }}
        >
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <Title level={4}>User Information</Title>
              <Text type="secondary" className="block mb-4">
                Enter the basic information for the new user.
              </Text>

              <Form.Item
                name="fullName"
                label="Full Name"
                rules={[{ required: true, message: 'Please enter full name' }]}
              >
                <Input prefix={<UserOutlined />} placeholder="Full Name" />
              </Form.Item>

              <Form.Item
                name="username"
                label="Username"
                rules={[
                  { required: true, message: 'Please enter username' },
                  { min: 4, message: 'Username must be at least 4 characters' },
                  { pattern: /^[a-zA-Z0-9_]+$/, message: 'Username can only contain letters, numbers and underscores' },
                ]}
              >
                <Input prefix={<UserOutlined />} placeholder="Username" />
              </Form.Item>

              <Form.Item
                name="email"
                label="Email"
                rules={[
                  { required: true, message: 'Please enter email' },
                  { type: 'email', message: 'Please enter a valid email' },
                ]}
              >
                <Input prefix={<MailOutlined />} placeholder="Email" />
              </Form.Item>
            </div>

            <div>
              <Title level={4}>Account Settings</Title>
              <Text type="secondary" className="block mb-4">
                Configure the user's account settings and permissions.
              </Text>

              <Form.Item
                name="password"
                label="Password"
                rules={[
                  { required: true, message: 'Please enter password' },
                  { min: 8, message: 'Password must be at least 8 characters' },
                ]}
              >
                <Input.Password prefix={<LockOutlined />} placeholder="Password" />
              </Form.Item>

              <Form.Item
                name="role"
                label="Role"
                rules={[{ required: true, message: 'Please select a role' }]}
              >
                <Select loading={rolesLoading}>
                  {roles.map(role => (
                    <Option key={role.id} value={role.name}>
                      {role.name} - {role.description}
                    </Option>
                  ))}
                </Select>
              </Form.Item>

              <Form.Item
                name="active"
                label="Account Status"
                valuePropName="checked"
              >
                <Switch 
                  checkedChildren="Active" 
                  unCheckedChildren="Inactive" 
                />
              </Form.Item>
            </div>
          </div>

          <Divider />

          <div className="flex justify-end">
            <Button 
              type="default" 
              onClick={() => navigate('/admin/users/list')}
              className="mr-2"
            >
              Cancel
            </Button>
            <Button 
              type="primary" 
              htmlType="submit" 
              icon={<SaveOutlined />}
              loading={loading}
            >
              Create User
            </Button>
          </div>
        </Form>
      </Card>
    </div>
  );
};

export default NewUser;
