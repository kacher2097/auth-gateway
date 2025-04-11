import React, { useState } from 'react';
import { Form, Input, Button, Typography, Alert, Result } from 'antd';
import { MailOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../../services/auth.service';

const { Title, Text } = Typography;

interface ForgotPasswordForm {
  email: string;
}

const ForgotPassword: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const navigate = useNavigate();

  const validateForm = () => {
    const errors: Record<string, string> = {};
    let isValid = true;

    // Validation logic can be added here if needed

    setFieldErrors(errors);
    return isValid;
  };

  const onFinish = async (values: ForgotPasswordForm) => {
    if (!validateForm()) return;

    setLoading(true);
    setError(null);

    try {
      await authService.forgotPassword(values.email);
      setSubmitted(true);
    } catch (err: any) {
      setError(err.message || 'Không thể gửi email khôi phục mật khẩu. Vui lòng thử lại sau.');

      // Handle specific error codes
      if (err.is && err.is('EMAIL_NOT_FOUND')) {
        setFieldErrors({ email: 'Không tìm thấy tài khoản với email này' });
      }

      // Map field errors if any
      if (err.fieldErrors) {
        setFieldErrors(err.fieldErrors);
      }
    } finally {
      setLoading(false);
    }
  };

  const clearErrors = () => {
    setError(null);
    setFieldErrors({});
  };

  if (submitted) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
        <div className="w-full max-w-md space-y-8 rounded-lg bg-white p-8 shadow-md">
          <Result
            status="success"
            title="Yêu cầu đã được gửi!"
            subTitle="Chúng tôi đã gửi hướng dẫn khôi phục mật khẩu đến email của bạn. Vui lòng kiểm tra hộp thư đến."
            extra={[
              <Button type="primary" key="login" onClick={() => navigate('/login')}>
                Quay lại đăng nhập
              </Button>,
            ]}
          />
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 rounded-lg bg-white p-8 shadow-md">
        <div className="text-center">
          <Title level={2}>Quên mật khẩu</Title>
          <Text type="secondary">Nhập email của bạn để nhận hướng dẫn khôi phục mật khẩu</Text>
        </div>

        {error && (
          <Alert
            message="Lỗi"
            description={error}
            type="error"
            showIcon
            closable
            onClose={clearErrors}
          />
        )}

        <Form
          name="forgot-password"
          layout="vertical"
          onFinish={onFinish}
          onFocus={clearErrors}
        >
          <Form.Item
            name="email"
            label="Email"
            validateStatus={fieldErrors.email ? 'error' : ''}
            help={fieldErrors.email}
            rules={[
              { required: true, message: 'Vui lòng nhập email của bạn' },
              { type: 'email', message: 'Vui lòng nhập email hợp lệ' },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="Email" size="large" />
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
              Gửi yêu cầu
            </Button>
          </Form.Item>

          <div className="text-center text-sm">
            <Link to="/login" className="text-blue-600 hover:text-blue-500">
              Quay lại đăng nhập
            </Link>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default ForgotPassword;