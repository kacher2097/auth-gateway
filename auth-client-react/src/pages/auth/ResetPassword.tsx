import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Typography, Alert, Result } from 'antd';
import { LockOutlined } from '@ant-design/icons';
import { useParams, useNavigate, Link } from 'react-router-dom';
import authService from '../../services/auth.service';

const { Title, Text } = Typography;

interface ResetPasswordForm {
  newPassword: string;
  confirmPassword: string;
}

const ResetPassword: React.FC = () => {
  const { token } = useParams<{ token: string }>();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [tokenValid, setTokenValid] = useState<boolean | null>(null);
  const navigate = useNavigate();

  // Validate token on mount
  useEffect(() => {
    if (token) {
      validateToken();
    } else {
      setTokenValid(false);
    }
  }, [token]);

  const validateToken = async () => {
    try {
      // In a real implementation, you would verify the token with the backend
      // For now, we'll just assume it's valid if it exists and has a reasonable length
      setTokenValid(token ? token.length > 20 : false);
    } catch (err) {
      setTokenValid(false);
      setError('Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.');
    }
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};
    let isValid = true;

    // Validation logic can be added here if needed

    setFieldErrors(errors);
    return isValid;
  };

  const onFinish = async (values: ResetPasswordForm) => {
    if (!token || !validateForm()) return;

    try {
      setLoading(true);
      setError(null);

      await authService.resetPassword({
        token,
        newPassword: values.newPassword,
        confirmPassword: values.confirmPassword
      });

      setSuccess(true);
      setTimeout(() => {
        navigate('/login');
      }, 3000);
    } catch (err: any) {
      setError(err.message || 'Không thể đặt lại mật khẩu. Vui lòng thử lại sau.');

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

  if (success) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
        <div className="w-full max-w-md space-y-8 rounded-lg bg-white p-8 shadow-md">
          <Result
            status="success"
            title="Đặt lại mật khẩu thành công!"
            subTitle="Mật khẩu của bạn đã được đặt lại thành công. Bạn sẽ được chuyển đến trang đăng nhập sau vài giây."
            extra={[
              <Button type="primary" key="login" onClick={() => navigate('/login')}>
                Đăng nhập ngay
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
          <Title level={2}>Đặt lại mật khẩu</Title>
          <Text type="secondary">Nhập mật khẩu mới của bạn</Text>
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

        {tokenValid === false && (
          <Alert
            message="Liên kết không hợp lệ"
            description="Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu liên kết mới."
            type="error"
            showIcon
            action={
              <Button size="small" type="primary" onClick={() => navigate('/forgot-password')}>
                Yêu cầu liên kết mới
              </Button>
            }
          />
        )}

        {tokenValid && (
          <Form
            name="reset-password"
            layout="vertical"
            onFinish={onFinish}
            onFocus={clearErrors}
            requiredMark={false}
          >
            <Form.Item
              name="newPassword"
              label="Mật khẩu mới"
              validateStatus={fieldErrors.newPassword ? 'error' : ''}
              help={fieldErrors.newPassword}
              rules={[
                { required: true, message: 'Vui lòng nhập mật khẩu mới' },
                { min: 8, message: 'Mật khẩu phải có ít nhất 8 ký tự' },
              ]}
            >
              <Input.Password
                prefix={<LockOutlined />}
                placeholder="Mật khẩu mới"
                size="large"
              />
            </Form.Item>

            <Form.Item
              name="confirmPassword"
              label="Xác nhận mật khẩu"
              validateStatus={fieldErrors.confirmPassword ? 'error' : ''}
              help={fieldErrors.confirmPassword}
              dependencies={['newPassword']}
              rules={[
                { required: true, message: 'Vui lòng xác nhận mật khẩu' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('newPassword') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('Hai mật khẩu không khớp'));
                  },
                }),
              ]}
            >
              <Input.Password
                prefix={<LockOutlined />}
                placeholder="Xác nhận mật khẩu"
                size="large"
              />
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
                Đặt lại mật khẩu
              </Button>
            </Form.Item>

            <div className="text-center text-sm">
              <Link to="/login" className="text-blue-600 hover:text-blue-500">
                Quay lại đăng nhập
              </Link>
            </div>
          </Form>
        )}
      </div>
    </div>
  );
};

export default ResetPassword;