import api from './api'
import { handleApiError } from '@/utils/errorHandler'

export interface RegisterRequest {
  username: string
  email: string
  password: string
  fullName: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface SocialLoginRequest {
  accessToken: string
  provider: 'GOOGLE' | 'FACEBOOK'
}

export interface OAuthCodeRequest {
  code: string
  provider: string
  redirectUri: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

export interface ForgotPasswordRequest {
  email: string
}

export interface ResetPasswordRequest {
  token: string
  newPassword: string
  confirmPassword: string
}

export interface UserInfo {
  id: string
  username: string
  email: string
  fullName: string
  avatar?: string
  role: 'ADMIN' | 'USER'
}

export interface AuthResponse {
  token: string
  user: UserInfo
}

class AuthService {
  async register(data: RegisterRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<AuthResponse>('/auth/register', data)
      return response.data
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<AuthResponse>('/auth/login', data)
      return response.data
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async socialLogin(data: SocialLoginRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<AuthResponse>('/auth/social-login', data)
      return response.data
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async oauthCallback(data: OAuthCodeRequest): Promise<AuthResponse> {
    try {
      const response = await api.post<AuthResponse>('/auth/oauth2/callback', data)
      return response.data
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async getCurrentUser(token: string): Promise<UserInfo> {
    try {
      const response = await api.get<UserInfo>('/auth/me', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      return response.data
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async changePassword(token: string, data: ChangePasswordRequest): Promise<void> {
    try {
      await api.post('/auth/change-password', data, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async forgotPassword(data: ForgotPasswordRequest): Promise<void> {
    try {
      await api.post('/auth/forgot-password', data)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async resetPassword(data: ResetPasswordRequest): Promise<void> {
    try {
      await api.post('/auth/reset-password', data)
    } catch (error) {
      throw handleApiError(error)
    }
  }
}

export default new AuthService()
