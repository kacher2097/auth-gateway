import api from './api.service';
import { handleApiError } from '../utils/errorHandler';
import { LoginRequest, RegisterRequest, UserInfo, AuthResponse, SocialLoginRequest, OAuthCodeRequest } from '../types/auth.types';

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

class AuthService {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    try {
      // Đảm bảo request phù hợp với AuthRequest trong backend
      const response = await api.post<AuthResponse>('/auth/login', {
        username: credentials.username,
        password: credentials.password
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    try {
      // Đảm bảo request phù hợp với RegisterRequest trong backend
      const response = await api.post<AuthResponse>('/auth/register', {
        username: userData.username,
        email: userData.email,
        password: userData.password,
        fullName: userData.fullName
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async socialLogin(socialData: SocialLoginRequest): Promise<AuthResponse> {
    try {
      // Đảm bảo request phù hợp với SocialLoginRequest trong backend
      const response = await api.post<AuthResponse>('/auth/social-login', {
        provider: socialData.provider,
        accessToken: socialData.token // Chuyển đổi token thành accessToken theo yêu cầu backend
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async oauthCallback(oauthData: OAuthCodeRequest): Promise<AuthResponse> {
    try {
      // Đảm bảo request phù hợp với OAuth2CallbackRequest trong backend
      const response = await api.post<AuthResponse>('/auth/oauth2/callback', {
        code: oauthData.code,
        provider: oauthData.provider,
        redirectUri: oauthData.redirectUri
      });
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async forgotPassword(email: string): Promise<void> {
    try {
      await api.post('/auth/forgot-password', { email });
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async resetPassword(data: ResetPasswordRequest): Promise<void> {
    try {
      await api.post('/auth/reset-password', {
        token: data.token,
        newPassword: data.newPassword,
        confirmPassword: data.confirmPassword
      });
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async changePassword(data: ChangePasswordRequest): Promise<void> {
    try {
      await api.post('/auth/change-password', {
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
        confirmPassword: data.confirmPassword
      });
      // Token được tự động thêm vào header bởi interceptor
    } catch (error) {
      throw handleApiError(error);
    }
  }

  async getCurrentUser(): Promise<UserInfo> {
    try {
      const response = await api.get<UserInfo>('/auth/me');
      return response.data;
    } catch (error) {
      throw handleApiError(error);
    }
  }

  logout(): void {
    localStorage.removeItem('token');
  }
}

export default new AuthService();
