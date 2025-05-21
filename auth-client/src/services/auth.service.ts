import apiWrapper from './apiWrapper'
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
  id: number
  username: string
  email: string
  fullName: string
  avatar?: string
  role: string
}

export interface UserInfoResponse extends UserInfo {
  roleId: number
  permissions: string[]
}

export interface DataToken {
  token: string
  refreshToken?: string
  user: UserInfo
}

export interface AuthResponse {
  token: string
  refreshToken?: string
  user: UserInfo
}

class AuthService {
  async register(data: RegisterRequest): Promise<AuthResponse> {
    try {
      return await apiWrapper.post<AuthResponse>('/auth/register', data)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      return await apiWrapper.post<AuthResponse>('/auth/login', data)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async socialLogin(data: SocialLoginRequest): Promise<AuthResponse> {
    try {
      return await apiWrapper.post<AuthResponse>('/auth/social-login', data)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async oauthCallback(data: OAuthCodeRequest): Promise<AuthResponse> {
    try {
      return await apiWrapper.post<AuthResponse>('/auth/oauth2/callback', data)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async getCurrentUser(token: string): Promise<UserInfo | UserInfoResponse> {
    try {
      return await apiWrapper.get<UserInfo | UserInfoResponse>('/auth/me', {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async refreshToken(refreshToken: string): Promise<AuthResponse> {
    try {
      return await apiWrapper.post<AuthResponse>('/auth/refresh-token', null, {
        headers: {
          Authorization: `Bearer ${refreshToken}`
        }
      })
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async changePassword(token: string, data: ChangePasswordRequest): Promise<void> {
    try {
      await apiWrapper.post<void>('/auth/change-password', data, {
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
      await apiWrapper.post<void>('/auth/forgot-password', data)
    } catch (error) {
      throw handleApiError(error)
    }
  }

  async resetPassword(data: ResetPasswordRequest): Promise<void> {
    try {
      await apiWrapper.post<void>('/auth/reset-password', data)
    } catch (error) {
      throw handleApiError(error)
    }
  }
}

export default new AuthService()
