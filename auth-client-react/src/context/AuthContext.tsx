import React, { createContext, useState, useEffect, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/auth.service';
import { UserInfo, LoginRequest, RegisterRequest, SocialLoginRequest, OAuthCodeRequest } from '../types/auth.types';
import { ApiError } from '../utils/errorHandler';
import { message } from 'antd';
import { ResetPasswordRequest } from '../services/auth.service';

interface AuthContextType {
  user: UserInfo | null;
  token: string | null;
  loading: boolean;
  error: string | null;
  fieldErrors: Record<string, string> | null;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  socialLogin: (socialData: SocialLoginRequest) => Promise<void>;
  oauthCallback: (oauthData: OAuthCodeRequest) => Promise<void>;
  logout: () => void;
  forgotPassword: (email: string) => Promise<void>;
  resetPassword: (data: ResetPasswordRequest) => Promise<void>;
  clearErrors: () => void;
  isAdmin: boolean;
  isAuthenticated: boolean;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  token: null,
  loading: false,
  error: null,
  fieldErrors: null,
  login: async () => {},
  register: async () => {},
  socialLogin: async () => {},
  oauthCallback: async () => {},
  logout: () => {},
  forgotPassword: async () => {},
  resetPassword: async () => {},
  clearErrors: () => {},
  isAdmin: false,
  isAuthenticated: false,
});

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string> | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!localStorage.getItem('token'));

  const navigate = useNavigate();

  const isAdmin = user?.role === 'ADMIN';

  // Load user on mount if token exists
  useEffect(() => {
    const loadUser = async () => {
      if (token) {
        try {
          setLoading(true);
          const userData = await authService.getCurrentUser();
          setUser(userData);
          setIsAuthenticated(true);
        } catch (err) {
          console.error('Failed to load user:', err);
          logout();
        } finally {
          setLoading(false);
        }
      }
    };

    loadUser();
  }, [token]);

  const clearErrors = () => {
    setError(null);
    setFieldErrors(null);
  };

  const login = async (credentials: LoginRequest) => {
    try {
      setLoading(true);
      clearErrors();

      const response = await authService.login(credentials);
      setToken(response.token);
      setUser(response.user);
      setIsAuthenticated(true);
      localStorage.setItem('token', response.token);

      message.success(`Xin chào, ${response.user.fullName}!`);
    } catch (err: any) {
      if (err instanceof ApiError) {
        setError(err.message);
        setFieldErrors(err.fieldErrors);
      } else {
        setError('Đăng nhập thất bại. Vui lòng kiểm tra thông tin đăng nhập.');
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData: RegisterRequest) => {
    try {
      setLoading(true);
      clearErrors();

      const response = await authService.register(userData);
      setToken(response.token);
      setUser(response.user);
      setIsAuthenticated(true);
      localStorage.setItem('token', response.token);

      message.success('Đăng ký thành công!');
    } catch (err: any) {
      if (err instanceof ApiError) {
        setError(err.message);
        setFieldErrors(err.fieldErrors);

        // Handle specific error codes
        if (err.is('USERNAME_EXISTS')) {
          setError('Tên đăng nhập đã tồn tại. Vui lòng chọn tên đăng nhập khác.');
        } else if (err.is('EMAIL_EXISTS')) {
          setError('Email đã được sử dụng. Vui lòng sử dụng email khác.');
        }
      } else {
        setError('Đăng ký thất bại. Vui lòng thử lại sau.');
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const socialLogin = async (socialData: SocialLoginRequest) => {
    try {
      setLoading(true);
      clearErrors();

      const response = await authService.socialLogin(socialData);
      setToken(response.token);
      setUser(response.user);
      setIsAuthenticated(true);
      localStorage.setItem('token', response.token);

      message.success(`Xin chào, ${response.user.fullName}!`);
    } catch (err: any) {
      if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError('Đăng nhập bằng mạng xã hội thất bại. Vui lòng thử lại sau.');
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const oauthCallback = async (oauthData: OAuthCodeRequest) => {
    try {
      setLoading(true);
      clearErrors();

      const response = await authService.oauthCallback(oauthData);
      setToken(response.token);
      setUser(response.user);
      setIsAuthenticated(true);
      localStorage.setItem('token', response.token);

      message.success(`Xin chào, ${response.user.fullName}!`);
    } catch (err: any) {
      if (err instanceof ApiError) {
        setError(err.message);
      } else {
        setError('Xác thực OAuth thất bại. Vui lòng thử lại sau.');
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
    localStorage.removeItem('token');

    message.info('Bạn đã đăng xuất thành công');
    navigate('/login');
  };

  const forgotPassword = async (email: string) => {
    try {
      setLoading(true);
      clearErrors();

      await authService.forgotPassword(email);
      // Success message is handled by the component
    } catch (err: any) {
      if (err instanceof ApiError) {
        setError(err.message);
        setFieldErrors(err.fieldErrors);
      } else {
        setError('Không thể xử lý yêu cầu đặt lại mật khẩu. Vui lòng thử lại sau.');
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const resetPassword = async (data: ResetPasswordRequest) => {
    try {
      setLoading(true);
      clearErrors();

      await authService.resetPassword(data);
      // Success message is handled by the component
    } catch (err: any) {
      if (err instanceof ApiError) {
        setError(err.message);
        setFieldErrors(err.fieldErrors);
      } else {
        setError('Không thể đặt lại mật khẩu. Vui lòng thử lại sau.');
      }
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const value = {
    user,
    token,
    loading,
    error,
    fieldErrors,
    login,
    register,
    socialLogin,
    oauthCallback,
    logout,
    forgotPassword,
    resetPassword,
    clearErrors,
    isAdmin,
    isAuthenticated,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
