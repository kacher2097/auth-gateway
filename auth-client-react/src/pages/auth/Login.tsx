import React, { useEffect } from 'react';
import { Form, Input, Button, Typography, Divider, Alert, Checkbox } from 'antd';
import { UserOutlined, LockOutlined, GoogleOutlined, FacebookOutlined } from '@ant-design/icons';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { LoginRequest } from '../../types/auth.types';
import oauthService from '../../services/oauth.service';

const { Title, Text } = Typography;

const Login: React.FC = () => {
  const { login, error, fieldErrors, clearErrors, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  // Get redirect path from query params or default to home
  const redirectPath = location.state?.from?.pathname || '/';

  // Check for OAuth error
  useEffect(() => {
    if (location.search.includes('error=oauth_failed')) {
      // Set error message for OAuth failure
    }
  }, [location]);

  const onFinish = async (values: LoginRequest) => {
    try {
      await login(values);
      navigate(redirectPath);
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
          <Title level={2}>Đăng nhập</Title>
          <Text type="secondary">Đăng nhập vào tài khoản của bạn</Text>
        </div>

        {error && (
          <Alert
            message="Đăng nhập thất bại"
            description={error}
            type="error"
            showIcon
            closable
            onClose={clearErrors}
          />
        )}

        <Form
          name="login"
          layout="vertical"
          initialValues={{ remember: true }}
          onFinish={onFinish}
          onFocus={clearErrors}
        >
          <Form.Item
            name="username"
            label="Tên đăng nhập"
            validateStatus={fieldErrors?.username ? 'error' : ''}
            help={fieldErrors?.username}
            rules={[{ required: true, message: 'Vui lòng nhập tên đăng nhập!' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Tên đăng nhập" size="large" />
          </Form.Item>

          <Form.Item
            name="password"
            label="Mật khẩu"
            validateStatus={fieldErrors?.password ? 'error' : ''}
            help={fieldErrors?.password}
            rules={[{ required: true, message: 'Vui lòng nhập mật khẩu!' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Mật khẩu" size="large" />
          </Form.Item>

          <div className="flex items-center justify-between">
            <Form.Item name="rememberMe" valuePropName="checked" noStyle>
              <Checkbox>Ghi nhớ đăng nhập</Checkbox>
            </Form.Item>
            <Link to="/forgot-password" className="text-sm text-blue-600 hover:text-blue-500">
              Quên mật khẩu?
            </Link>
          </div>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              size="large"
              block
              loading={loading}
              className="mt-4"
            >
              Đăng nhập
            </Button>
          </Form.Item>
        </Form>

        <Divider plain>Hoặc đăng nhập với</Divider>

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
            Chưa có tài khoản?{' '}
            <Link to="/register" className="text-blue-600 hover:text-blue-500">
              Đăng ký ngay
            </Link>
          </Text>
        </div>
      </div>
    </div>
  );
};

export default Login;