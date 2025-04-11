export interface UserInfo {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: 'USER' | 'ADMIN' | 'CONTENT_EDITOR' | 'ANALYST';
  active: boolean;
  avatar?: string;
  socialProvider?: string;
  lastLogin?: string;
  createdAt: string;
  updatedAt: string;
}

export interface LoginRequest {
  username: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  fullName: string;
}

export interface SocialLoginRequest {
  provider: 'GOOGLE' | 'FACEBOOK'; // Backend sử dụng chữ in hoa cho provider
  token: string; // Được chuyển thành accessToken trong service
}

export interface OAuthCodeRequest {
  provider: string; // 'google' hoặc 'facebook'
  code: string;
  redirectUri: string;
}

export interface AuthResponse {
  token: string;
  user: UserInfo;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
  errors?: Record<string, string>;
}
