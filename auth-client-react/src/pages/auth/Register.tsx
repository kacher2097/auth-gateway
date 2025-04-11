import React, { useEffect } from 'react';
import { Form, Input, Button, Typography, Divider, Alert } from 'antd';
import { UserOutlined, MailOutlined, LockOutlined, UserAddOutlined, GoogleOutlined, FacebookOutlined } from '@ant-design/icons';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { RegisterRequest } from '../../types/auth.types';
import oauthService from '../../services/oauth.service';

const { Title, Text } = Typography;

const Register: React.FC = () => {
  const { register, error, fieldErrors, clearErrors, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  // Check for OAuth error
  useEffect(() => {
    if (location.search.includes('error=oauth_failed')) {
      // Set error message for OAuth failure
    }
  }, [location]);

  const onFinish = async (values: RegisterRequest) => {
    try {
      await register(values);
      navigate('/');
    } catch (err) {
      // Error is handled by the auth context
    }
  };

  // Social login handlers
  const handleGoogleLogin = () => {
    // Sử dụng oauth.service để bắt đầu luồng đăng nhập Google
    oauthService.loginWithGoogle();
  };

  const handleFacebookLogin = () => {
    // Sử dụng oauth.service để bắt đầu luồng đăng nhập Facebook
    oauthService.loginWithFacebook();
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 rounded-lg bg-white p-8 shadow-md">
        <div className="text-center">
          <Title level={2}>Đăng ký tài khoản</Title>
          <Text type="secondary">Tạo tài khoản mới để sử dụng hệ thống</Text>
        </div>

        {error && (
          <Alert
            message="Đăng ký thất bại"
            description={error}
            type="error"
            showIcon
            closable
            onClose={clearErrors}
          />
        )}

        <Form
          name="register"
          layout="vertical"
          onFinish={onFinish}
          onFocus={clearErrors}
          requiredMark={false}
        >
          <Form.Item
            name="fullName"
            label="Họ và tên"
            validateStatus={fieldErrors?.fullName ? 'error' : ''}
            help={fieldErrors?.fullName}
            rules={[{ required: true, message: 'Vui lòng nhập họ và tên' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Họ và tên" size="large" />
          </Form.Item>

          <Form.Item
            name="username"
            label="Tên đăng nhập"
            validateStatus={fieldErrors?.username ? 'error' : ''}
            help={fieldErrors?.username}
            rules={[
              { required: true, message: 'Vui lòng nhập tên đăng nhập' },
              { min: 4, message: 'Tên đăng nhập phải có ít nhất 4 ký tự' },
              { pattern: /^[a-zA-Z0-9_]+$/, message: 'Tên đăng nhập chỉ chứa chữ cái, số và dấu gạch dưới' },
            ]}
          >
            <Input prefix={<UserAddOutlined />} placeholder="Tên đăng nhập" size="large" />
          </Form.Item>

          <Form.Item
            name="email"
            label="Email"
            validateStatus={fieldErrors?.email ? 'error' : ''}
            help={fieldErrors?.email}
            rules={[
              { required: true, message: 'Vui lòng nhập email' },
              { type: 'email', message: 'Vui lòng nhập email hợp lệ' },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="Email" size="large" />
          </Form.Item>

          <Form.Item
            name="password"
            label="Mật khẩu"
            validateStatus={fieldErrors?.password ? 'error' : ''}
            help={fieldErrors?.password}
            rules={[
              { required: true, message: 'Vui lòng nhập mật khẩu' },
              { min: 8, message: 'Mật khẩu phải có ít nhất 8 ký tự' },
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Mật khẩu" size="large" />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            label="Xác nhận mật khẩu"
            dependencies={['password']}
            rules={[
              { required: true, message: 'Vui lòng xác nhận mật khẩu' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Hai mật khẩu không khớp'));
                },
              }),
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Xác nhận mật khẩu" size="large" />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              size="large"
              block
              loading={loading}
              className="mt-4"
            >
              Đăng ký
            </Button>
          </Form.Item>
        </Form>

        <Divider plain>Hoặc đăng ký với</Divider>

        <div className="flex justify-center space-x-4">
          <Button
            icon={<GoogleOutlined />}
            size="large"
            onClick={handleGoogleLogin}
          >
            Google
          </Button>
          <Button
            icon={<FacebookOutlined />}
            size="large"
            onClick={handleFacebookLogin}
          >
            Facebook
          </Button>
        </div>

        <div className="mt-6 text-center text-sm">
          <Text type="secondary">
            Đã có tài khoản?{' '}
            <Link to="/login" className="text-blue-600 hover:text-blue-500">
              Đăng nhập ngay
            </Link>
          </Text>
        </div>
      </div>
    </div>
  );
};

export default Register;