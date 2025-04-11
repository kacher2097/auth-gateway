import React, { useState } from 'react';
import { Card, Avatar, Typography, Tabs, Form, Input, Button, Divider, message } from 'antd';
import { UserOutlined, MailOutlined, LockOutlined, SaveOutlined } from '@ant-design/icons';
import useAuth from '../hooks/useAuth';
import userService from '../services/user.service';
import MainLayout from '../components/layout/MainLayout';

const { Title, Text } = Typography;
const { TabPane } = Tabs;

const Profile: React.FC = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);

  const onProfileUpdate = async (values: any) => {
    try {
      setLoading(true);
      await userService.updateProfile(values);
      message.success('Cập nhật hồ sơ thành công');
    } catch (err) {
      console.error('Failed to update profile:', err);
      message.error('Không thể cập nhật hồ sơ');
    } finally {
      setLoading(false);
    }
  };

  const onPasswordChange = async (values: any) => {
    try {
      setPasswordLoading(true);
      await userService.changePassword(values);
      message.success('Đổi mật khẩu thành công');
    } catch (err) {
      console.error('Failed to change password:', err);
      message.error('Không thể đổi mật khẩu');
    } finally {
      setPasswordLoading(false);
    }
  };

  return (
    <MainLayout>
      <Card className="max-w-4xl mx-auto">
        <div className="flex flex-col md:flex-row items-center md:items-start gap-8 mb-8">
          <div className="flex flex-col items-center">
            <Avatar
              size={120}
              src={user?.avatar}
              icon={!user?.avatar && <UserOutlined />}
              className="mb-4"
            />
            <Title level={4}>{user?.fullName}</Title>
            <Text type="secondary">{user?.email}</Text>
            <Text type="secondary">Role: {user?.role}</Text>
          </div>

          <Divider type="vertical" className="hidden md:block h-auto" />

          <div className="flex-1">
            <Tabs defaultActiveKey="profile">
              <TabPane tab="Profile Information" key="profile">
                <Form
                  layout="vertical"
                  initialValues={{
                    fullName: user?.fullName,
                    username: user?.username,
                    email: user?.email,
                  }}
                  onFinish={onProfileUpdate}
                >
                  <Form.Item
                    name="fullName"
                    label="Full Name"
                    rules={[{ required: true, message: 'Please enter your full name' }]}
                  >
                    <Input prefix={<UserOutlined />} />
                  </Form.Item>

                  <Form.Item
                    name="username"
                    label="Username"
                  >
                    <Input prefix={<UserOutlined />} disabled />
                  </Form.Item>

                  <Form.Item
                    name="email"
                    label="Email"
                    rules={[
                      { required: true, message: 'Please enter your email' },
                      { type: 'email', message: 'Please enter a valid email' }
                    ]}
                  >
                    <Input prefix={<MailOutlined />} />
                  </Form.Item>

                  <Form.Item>
                    <Button
                      type="primary"
                      htmlType="submit"
                      icon={<SaveOutlined />}
                      loading={loading}
                    >
                      Save Changes
                    </Button>
                  </Form.Item>
                </Form>
              </TabPane>

              <TabPane tab="Change Password" key="password">
                <Form
                  layout="vertical"
                  onFinish={onPasswordChange}
                >
                  <Form.Item
                    name="currentPassword"
                    label="Current Password"
                    rules={[{ required: true, message: 'Please enter your current password' }]}
                  >
                    <Input.Password prefix={<LockOutlined />} />
                  </Form.Item>

                  <Form.Item
                    name="newPassword"
                    label="New Password"
                    rules={[
                      { required: true, message: 'Please enter your new password' },
                      { min: 8, message: 'Password must be at least 8 characters' }
                    ]}
                  >
                    <Input.Password prefix={<LockOutlined />} />
                  </Form.Item>

                  <Form.Item
                    name="confirmPassword"
                    label="Confirm New Password"
                    dependencies={['newPassword']}
                    rules={[
                      { required: true, message: 'Please confirm your new password' },
                      ({ getFieldValue }) => ({
                        validator(_, value) {
                          if (!value || getFieldValue('newPassword') === value) {
                            return Promise.resolve();
                          }
                          return Promise.reject(new Error('The two passwords do not match'));
                        },
                      }),
                    ]}
                  >
                    <Input.Password prefix={<LockOutlined />} />
                  </Form.Item>

                  <Form.Item>
                    <Button
                      type="primary"
                      htmlType="submit"
                      icon={<SaveOutlined />}
                      loading={passwordLoading}
                    >
                      Change Password
                    </Button>
                  </Form.Item>
                </Form>
              </TabPane>
            </Tabs>
          </div>
        </div>
      </Card>
    </MainLayout>
  );
};

export default Profile;
