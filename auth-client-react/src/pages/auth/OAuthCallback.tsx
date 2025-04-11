import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Spin, Result, Button } from 'antd';
import useAuth from '../../hooks/useAuth';
import oauthService from '../../services/oauth.service';

const OAuthCallback: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { oauthCallback } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    handleOAuthCallback();
  }, []);

  const handleOAuthCallback = async () => {
    try {
      const { code, state, provider } = oauthService.extractAuthParams();

      // Validate state to prevent CSRF attacks
      if (!code || !state || !provider) {
        throw new Error('Thiếu thông tin xác thực OAuth');
      }

      if (!oauthService.verifyState(state)) {
        throw new Error('Xác thực OAuth không hợp lệ hoặc đã hết hạn');
      }

      // Process OAuth callback
      // Lấy provider gốc (chữ thường) cho redirectUri
      const originalProvider = provider.toLowerCase();

      await oauthCallback({
        code,
        provider, // Provider đã được chuyển thành chữ in hoa trong extractAuthParams
        redirectUri: `${window.location.origin}/oauth/callback/${originalProvider}`
      });

      // Redirect to home on success
      navigate('/');
    } catch (err: any) {
      console.error('OAuth callback error:', err);
      setError(err.message || 'Đăng nhập bằng mạng xã hội thất bại');
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-center">
          <Spin size="large" />
          <div className="mt-4">Đang xử lý đăng nhập...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center p-4">
        <Result
          status="error"
          title="Đăng nhập thất bại"
          subTitle={error}
          extra={[
            <Button type="primary" key="login" onClick={() => navigate('/login')}>
              Quay lại đăng nhập
            </Button>
          ]}
        />
      </div>
    );
  }

  return null;
};

export default OAuthCallback;
